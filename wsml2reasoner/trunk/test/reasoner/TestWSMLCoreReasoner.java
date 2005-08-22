package reasoner;

import java.io.FileReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.deri.wsml.reasoner.api.OntologyRegistrationRequest;
import org.deri.wsml.reasoner.api.WSMLReasoner;
import org.deri.wsml.reasoner.api.WSMLReasonerFactory;
import org.deri.wsml.reasoner.api.queryanswering.QueryAnsweringRequest;
import org.deri.wsml.reasoner.api.queryanswering.QueryAnsweringResult;
import org.deri.wsml.reasoner.api.queryanswering.VariableBinding;
import org.deri.wsml.reasoner.impl.DefaultWSMLReasonerFactory;
import org.deri.wsml.reasoner.impl.OntologyRegistrationRequestImpl;
import org.deri.wsml.reasoner.impl.QueryAnsweringRequestImpl;
import org.deri.wsml.reasoner.impl.VariableBindingImpl;
import org.deri.wsmo4j.io.parser.wsml.LogExprParserImpl;
import org.deri.wsmo4j.io.serializer.wsml.LogExprSerializerWSML;
import org.omwg.logexpression.LogicalExpression;
import org.omwg.ontology.Ontology;
import org.wsmo.common.TopEntity;
import org.wsmo.factory.Factory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.Parser;
import org.wsmo.wsml.Serializer;

public class TestWSMLCoreReasoner extends TestCase {

    private static final String NS = "http://www.example.org/example/#";

    private static final String ONTOLOGY_FILE = "examples/simple-graph.wsml";

    private static WSMLReasoner wsmlCoreReasoner = null;

    private static Ontology o = null;

    private org.omwg.logexpression.io.Parser leParser = null;

    private org.omwg.logexpression.io.Serializer logExprSerializer = null;

    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestWSMLCoreReasoner.suite());
    }

    public static Test suite() {
        Test test = new junit.extensions.TestSetup(new TestSuite(
                TestWSMLCoreReasoner.class)) {
            protected void setUp() throws Exception {
                // Set up factories for creating WSML elements

                Map<String, String> leProperties = new HashMap<String, String>();
                leProperties
                        .put(Factory.PROVIDER_CLASS,
                                "org.deri.wsmo4j.logexpression.LogicalExpressionFactoryImpl");

                org.omwg.logexpression.LogicalExpressionFactory leFactory = (org.omwg.logexpression.LogicalExpressionFactory) Factory
                        .createLogicalExpressionFactory(leProperties);

                Map<String, Object> properties = new HashMap<String, Object>();
                properties.put(Factory.PROVIDER_CLASS,
                        "com.ontotext.wsmo4j.factory.WsmoFactoryImpl");
                properties.put(Parser.PARSER_LE_FACTORY, leFactory);
                WsmoFactory factory = Factory.createWsmoFactory(properties);

                // Set up WSML parser

                Map<String, Object> parserProperties = new HashMap<String, Object>();
                parserProperties.put(Parser.PARSER_WSMO_FACTORY, factory);
                parserProperties.put(Parser.PARSER_LE_FACTORY, leFactory);

                parserProperties.put(org.wsmo.factory.Factory.PROVIDER_CLASS,
                        "com.ontotext.wsmo4j.parser.WSMLParserImpl");

                Parser wsmlparserimpl = org.wsmo.factory.Factory
                        .createParser(parserProperties);

                // Set up serializer

                Map<String, String> serializerProperties = new HashMap<String, String>();
                serializerProperties.put(
                        org.wsmo.factory.Factory.PROVIDER_CLASS,
                        "com.ontotext.wsmo4j.parser.WSMLSerializerImpl");

                Serializer ontologySerializer = org.wsmo.factory.Factory
                        .createSerializer(serializerProperties);

                // Read simple ontology from file

                final Reader ontoReader = new FileReader(ONTOLOGY_FILE);

                final TopEntity[] identifiable = wsmlparserimpl
                        .parse(ontoReader);
                if (identifiable.length > 0
                        && identifiable[0] instanceof Ontology) {
                    o = (Ontology) identifiable[0];
                } else {
                    return;
                }

                // Print ontology in WSML

                System.out.println("WSML Ontology:\n");
                StringWriter sw = new StringWriter();
                ontologySerializer.serialize(new TopEntity[] { o }, sw);
                System.out.println(sw.toString());
                System.out.println("--------------\n\n");

                // Create reasoner
                Map<String, Object> params = new HashMap<String, Object>();
                params.put(WSMLReasonerFactory.PARAM_WSML_VARIANT,
                        WSMLReasonerFactory.WSMLVariant.WSML_CORE);
                params.put(WSMLReasonerFactory.PARAM_BUILT_IN_REASONER,
                        WSMLReasonerFactory.BuiltInReasoner.KAON2);
                wsmlCoreReasoner = DefaultWSMLReasonerFactory.getFactory()
                        .getWSMLReasoner(params);

                // Register ontology
                System.out.println("Registering ontology");
                Set<Ontology> ontos = new HashSet<Ontology>();
                ontos.add(o);
                OntologyRegistrationRequest regReq = new OntologyRegistrationRequestImpl(
                        ontos);
                wsmlCoreReasoner.execute(regReq);

            }

            protected void tearDown() throws Exception {
                System.out.println("Finished!");
            }
        };
        return test;
    }

    private void performQuery(String query, Set<VariableBinding> expected)
            throws Exception {
        System.out.println("\n\nStarting reasoner with query " + query);
        LogicalExpression qExpression = leParser.parse(query);
        System.out.println("WSML Query:");
        System.out.println(logExprSerializer.serialize(qExpression));
        System.out.println("--------------\n\n");
        String ontologyUri = o.getIdentifier().toString();

        QueryAnsweringRequest qaRequest = new QueryAnsweringRequestImpl(
                ontologyUri, qExpression);
        QueryAnsweringResult result = (QueryAnsweringResult) wsmlCoreReasoner
                .execute(qaRequest);

        System.out.println("Found < " + result.size()
                + " > results to the query:");
        int i = 0;
        for (VariableBinding vBinding : result) {
            System.out.println("(" + (++i) + ") -- " + vBinding.toString());
        }
        assertEquals(result.size(), expected.size());
        for (VariableBinding binding : expected) {
            assertTrue(result.contains(binding));
        }
    }

    public void testElementsConnectedWithF() throws Exception {
        String query = "path(?n,f)";
        Set<VariableBinding> expected = new HashSet<VariableBinding>();
        VariableBinding binding = new VariableBindingImpl();
        binding.put("n", NS + "a");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n", NS + "b");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n", NS + "c");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n", NS + "f");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n", NS + "g");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n", NS + "h");
        expected.add(binding);
        performQuery(query, expected);
        System.out.println("Finished query.");

    }

    public void testConnectedPairs() throws Exception {
        String query = "path(?n1,?n2)";
        Set<VariableBinding> expected = new HashSet<VariableBinding>();
        VariableBinding binding = new VariableBindingImpl();
        binding.put("n1", NS + "a");
        binding.put("n2", NS + "a");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "a");
        binding.put("n2", NS + "b");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "a");
        binding.put("n2", NS + "c");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "a");
        binding.put("n2", NS + "d");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "a");
        binding.put("n2", NS + "e");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "a");
        binding.put("n2", NS + "f");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "a");
        binding.put("n2", NS + "g");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "a");
        binding.put("n2", NS + "h");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "a");
        binding.put("n2", NS + "i");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "b");
        binding.put("n2", NS + "a");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "b");
        binding.put("n2", NS + "b");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "b");
        binding.put("n2", NS + "c");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "b");
        binding.put("n2", NS + "d");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "b");
        binding.put("n2", NS + "e");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "b");
        binding.put("n2", NS + "f");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "b");
        binding.put("n2", NS + "g");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "b");
        binding.put("n2", NS + "h");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "b");
        binding.put("n2", NS + "i");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "c");
        binding.put("n2", NS + "a");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "c");
        binding.put("n2", NS + "b");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "c");
        binding.put("n2", NS + "c");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "c");
        binding.put("n2", NS + "d");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "c");
        binding.put("n2", NS + "e");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "c");
        binding.put("n2", NS + "f");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "c");
        binding.put("n2", NS + "g");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "c");
        binding.put("n2", NS + "h");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "c");
        binding.put("n2", NS + "i");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "f");
        binding.put("n2", NS + "f");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "f");
        binding.put("n2", NS + "g");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "f");
        binding.put("n2", NS + "h");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "f");
        binding.put("n2", NS + "i");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "g");
        binding.put("n2", NS + "f");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "g");
        binding.put("n2", NS + "g");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "g");
        binding.put("n2", NS + "h");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "g");
        binding.put("n2", NS + "i");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "h");
        binding.put("n2", NS + "f");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "h");
        binding.put("n2", NS + "g");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "h");
        binding.put("n2", NS + "h");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n1", NS + "h");
        binding.put("n2", NS + "i");
        expected.add(binding);
        performQuery(query, expected);
        System.out.println("Finished query.");

    }

    public void testScElementsOnADirecteCircleWithF() throws Exception {

        String query = "scElement(?n) and path(?n,f) and path(f,?n)";        
        Set<VariableBinding> expected = new HashSet<VariableBinding>();
        VariableBinding binding = new VariableBindingImpl();
        binding.put("n", NS + "f");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n", NS + "g");
        expected.add(binding);
        binding = new VariableBindingImpl();
        binding.put("n", NS + "h");
        expected.add(binding);
        performQuery(query, expected);
        System.out.println("Finished query.");
    }

    @Override
    protected void setUp() throws Exception {
        leParser = LogExprParserImpl.getInstance(o); // construct queries
        // over the same
        // ontology
        logExprSerializer = new LogExprSerializerWSML(o);
    }

}
