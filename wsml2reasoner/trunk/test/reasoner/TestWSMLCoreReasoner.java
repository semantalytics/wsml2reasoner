package reasoner;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.deri.wsml.reasoner.api.Request;
import org.deri.wsml.reasoner.api.WSMLReasoner;
import org.deri.wsml.reasoner.api.WSMLReasonerFactory;
import org.deri.wsml.reasoner.api.queryanswering.QueryAnsweringRequest;
import org.deri.wsml.reasoner.api.queryanswering.QueryAnsweringResult;
import org.deri.wsml.reasoner.api.queryanswering.VariableBinding;
import org.deri.wsml.reasoner.impl.DefaultWSMLReasonerFactory;
import org.deri.wsml.reasoner.impl.QueryAnsweringRequestImpl;
import org.deri.wsml.reasoner.impl.VariableBindingImpl;
import org.deri.wsmo4j.io.parser.wsml.LogExprParserImpl;
import org.deri.wsmo4j.io.serializer.wsml.LogExprSerializerWSML;
import org.omwg.logexpression.LogicalExpression;
import org.omwg.ontology.Ontology;
import org.wsmo.common.TopEntity;
import org.wsmo.common.exception.InvalidModelException;
import org.wsmo.factory.Factory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.Parser;
import org.wsmo.wsml.ParserException;
import org.wsmo.wsml.Serializer;

import test.BaseTest;

public class TestWSMLCoreReasoner extends BaseTest {

    private static final String NS = "http://www.example.org/example/#";

    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestWSMLCoreReasoner.class);
    }

    private WSMLReasoner wsmlCoreReasoner;

    private void performQuery(Request r, Set<VariableBinding> expected) {
        QueryAnsweringResult result = (QueryAnsweringResult) wsmlCoreReasoner
                .execute(r);

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

    /**
     * Loads a simple ontology from a file, constructs a simple conjunctive
     * query over the ontolgy, evaluates the query and prints the query answer
     * to console.
     * 
     * @param args
     */
    public void testReasoner() {

        String ONTOLOGY_FILE = "examples/simple-graph.wsml";

        Ontology o = null;

        try {

            final Reader ontoReader = new FileReader(ONTOLOGY_FILE);

            final TopEntity[] identifiable = wsmlParser.parse(ontoReader);
            if (identifiable.length > 0 && identifiable[0] instanceof Ontology) {
                o = (Ontology) identifiable[0];
            } else {
                return;
            }

        } catch (Exception e) {
            System.out.println("Unable to parse ontology: " + e.getMessage());
            return;
        }

        // Print ontology in WSML

        try {
            System.out.println("WSML Ontology:\n");
            StringWriter sw = new StringWriter();
            wsmlSerializer.serialize(new TopEntity[] { o }, sw);
            System.out.println(sw.toString());
            System.out.println("--------------\n\n");
        } catch (IOException e6) {
            // TODO Auto-generated catch block
            e6.printStackTrace();
        }

        org.omwg.logexpression.io.Serializer logExprSerializer = new LogExprSerializerWSML(
                o);

        // Build simple conjunctive query in WSML

        LogicalExpression qExpression1 = null;
        LogicalExpression qExpression2 = null;
        LogicalExpression qExpression3 = null;

        org.omwg.logexpression.io.Parser leParser = LogExprParserImpl
                .getInstance(o); // construct queries over the same ontology

        try {
            String query1 = "scElement(?n) and path(?n,f) and path(f,?n)";
            qExpression1 = leParser.parse(query1);

            String query2 = "path(?n,f)";
            qExpression2 = leParser.parse(query2);

            String query3 = "path(?n1,?n2)";
            qExpression3 = leParser.parse(query3);

        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            return;
        } catch (ParserException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            return;
        } catch (InvalidModelException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            return;
        }

        // Print query

        System.out.println("WSML Query (1):");
        System.out.println(logExprSerializer.serialize(qExpression1));
        System.out.println("\nWSML Query (2):");
        System.out.println(logExprSerializer.serialize(qExpression2));
        System.out.println("\nWSML Query (3):");
        System.out.println(logExprSerializer.serialize(qExpression3));
        System.out.println("--------------\n\n");

        // Now get a reasoner, create a query ansering request and print the
        // result.
        try {

            Set<Ontology> ontos = new HashSet<Ontology>();
            ontos.add(o);
            QueryAnsweringRequest qaRequest1 = new QueryAnsweringRequestImpl(
                    ontos, qExpression1);
            QueryAnsweringRequest qaRequest2 = new QueryAnsweringRequestImpl(
                    ontos, qExpression2);
            QueryAnsweringRequest qaRequest3 = new QueryAnsweringRequestImpl(
                    ontos, qExpression3);

            wsmlCoreReasoner = DefaultWSMLReasonerFactory.getFactory()
                    .getWSMLReasoner(WSMLReasonerFactory.WSMLVariant.WSML_CORE);

            System.out.println("Starting reasoner with query (1) ...");
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
            performQuery(qaRequest1, expected);
            System.out.println("Finished query.");

            System.out.println("Starting reasoner with query (2) ...");
            expected.clear();
            binding = new VariableBindingImpl();
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
            performQuery(qaRequest2, expected);
            System.out.println("Finished query.");

            System.out.println("Starting reasoner with query (3) ...");
            expected.clear();
            binding = new VariableBindingImpl();
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
            performQuery(qaRequest3, expected);
            System.out.println("Finished query.");

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Finished!");

    }

}
