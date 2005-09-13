package example;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.deri.wsmo4j.logexpression.LogicalExpressionFactoryImpl;
import org.omwg.logexpression.LogicalExpression;
import org.omwg.logexpression.LogicalExpressionFactory;
import org.omwg.ontology.Ontology;
import org.wsml.reasoner.api.OntologyRegistrationRequest;
import org.wsml.reasoner.api.WSMLReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.api.queryanswering.QueryAnsweringRequest;
import org.wsml.reasoner.api.queryanswering.QueryAnsweringResult;
import org.wsml.reasoner.api.queryanswering.VariableBinding;
import org.wsml.reasoner.impl.DefaultWSMLReasonerFactory;
import org.wsml.reasoner.impl.OntologyRegistrationRequestImpl;
import org.wsml.reasoner.impl.QueryAnsweringRequestImpl;
import org.wsml.reasoner.transformation.AxiomatizationNormalizer;
import org.wsmo.common.TopEntity;
import org.wsmo.factory.Factory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.Parser;
import org.wsmo.wsml.Serializer;

public class ReasonerExample2 {

    /**
     * @param args none expected
     */
    public static void main(String[] args) {
        ReasonerExample2 ex = new ReasonerExample2();
        ex.doTestRun();
    }

    /**
     * loads an Ontology and performs 2 sample queries
     */
    public void doTestRun() {
        Logger log = Logger.getLogger("org.deri");
        log.setLevel(Level.FINE);
        
        Ontology exampleOntology = loadOntology("example/humanOntology.wsml");
        if (exampleOntology == null)
            return;

        // The details of creating a Query will be hidden in future
        LogicalExpression query = (LogicalExpression) new LogicalExpressionFactoryImpl(null)
                .createLogicalExpression("?x memberOf Human", exampleOntology);

        QueryAnsweringRequest qaRequest = 
                new QueryAnsweringRequestImpl(exampleOntology.getIdentifier().toString(), query);
        
        //get A reasoner
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(WSMLReasonerFactory.PARAM_WSML_VARIANT,
                WSMLReasonerFactory.WSMLVariant.WSML_CORE);
        params.put(WSMLReasonerFactory.PARAM_BUILT_IN_REASONER,
                WSMLReasonerFactory.BuiltInReasoner.MINS);
        WSMLReasoner reasoner = DefaultWSMLReasonerFactory.getFactory().getWSMLReasoner(
                params);
        
        // Register ontology
        System.out.println("Registering ontology");
        Set<Ontology> ontos = new HashSet<Ontology>();
        ontos.add(exampleOntology);
        OntologyRegistrationRequest regReq = new OntologyRegistrationRequestImpl(
                ontos);
        reasoner.execute(regReq);
        
        System.out.println("Ontology registred!");
        
        QueryAnsweringResult result = (QueryAnsweringResult) reasoner
                .execute(qaRequest);
        
        System.out.println("Query executed!");
        
        // print out the results:
        System.out.println("The query '" + query + "' has the following results:");
        for (VariableBinding vBinding : result) {
            for (String var : vBinding.keySet()) {
                System.out.print("  ?" + var + ": " + vBinding.get(var));
            }
            System.out.println();
        }
        System.out.println("***** The End! *****");

        /*// The details of creating a Query will be hidden in future
        query = (LogicalExpression) new LogicalExpressionFactoryImpl(null)
                .createLogicalExpression("?x[hasRelative hasValue ?y]",exampleOntology);
        qaRequest = new QueryAnsweringRequestImpl(
                exampleOntology.getIdentifier().toString(), query);
        result = (QueryAnsweringResult) reasoner.execute(qaRequest);

        // print out the results:
        System.out.println("The query '" + query + "' has the following results:");
        for (VariableBinding vBinding : result) {
            for (String var : vBinding.keySet()) {
                System.out.print("  ?" + var + ": " + vBinding.get(var));
            }
            System.out.println();
        }*/

    }

    /**
     * Utility Method to get the object model of a wsml ontology
     * 
     * @param file location of source file (It will be attemted to be loaded from
     *            current class path)
     * @return object model of ontology at file location
     */
    private Ontology loadOntology(String file) {
        // set up Factories
        Map<String, String> leProperties = new HashMap<String, String>();
        leProperties.put(Factory.PROVIDER_CLASS,
                "org.deri.wsmo4j.logexpression.LogicalExpressionFactoryImpl");

        LogicalExpressionFactory leFactory = (LogicalExpressionFactory) Factory
                .createLogicalExpressionFactory(leProperties);

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(Factory.PROVIDER_CLASS,
                "com.ontotext.wsmo4j.factory.WsmoFactoryImpl");
        properties.put(Parser.PARSER_LE_FACTORY, leFactory);
        WsmoFactory wsmoFactory = Factory.createWsmoFactory(null);

        // Set up WSML parser
        Map<String, Object> parserProperties = new HashMap<String, Object>();
        parserProperties.put(Parser.PARSER_WSMO_FACTORY, wsmoFactory);
        parserProperties.put(Parser.PARSER_LE_FACTORY, leFactory);

        parserProperties.put(org.wsmo.factory.Factory.PROVIDER_CLASS,
                "com.ontotext.wsmo4j.parser.WSMLParserImpl");

        Parser wsmlParser = org.wsmo.factory.Factory
                .createParser(parserProperties);

        InputStream is = this.getClass().getClassLoader().getResourceAsStream(
                file);
        try {
            final TopEntity[] identifiable = wsmlParser
                    .parse(new InputStreamReader(is));
            if (identifiable.length > 0 && identifiable[0] instanceof Ontology) {
                return (Ontology) identifiable[0];
            }
            else {
                System.out.println("First Element of file no ontology ");
                return null;
            }

        } catch (Exception e) {
            System.out.println("Unable to parse ontology: " + e.getMessage());
            return null;
        }

    }

    /**
     * small utility method for debugging
     * 
     * @param ont ontology to be serialized to string
     * @return string representation of ontology
     */
    private String toString(Ontology ont) {
        Map<String, String> serializerProperties = new HashMap<String, String>();
        serializerProperties.put(org.wsmo.factory.Factory.PROVIDER_CLASS,
                "com.ontotext.wsmo4j.parser.WSMLSerializerImpl");

        Serializer wsmlSerializer = org.wsmo.factory.Factory
                .createSerializer(serializerProperties);

        StringBuffer str = new StringBuffer();
        wsmlSerializer.serialize(new TopEntity[] { ont }, str);
        return str.toString();
    }
}
