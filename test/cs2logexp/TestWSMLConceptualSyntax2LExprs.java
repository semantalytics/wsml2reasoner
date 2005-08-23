package cs2logexp;


import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.deri.wsml.reasoner.normalization.AxiomatizationNormalizer;
import org.omwg.ontology.Ontology;
import org.wsmo.common.TopEntity;
import org.wsmo.factory.Factory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.Parser;
import org.wsmo.wsml.Serializer;

public class TestWSMLConceptualSyntax2LExprs{

    /**
     * Loads a simple ontology from a file, constructs a simple conjunctive query
     * over the ontolgy, evaluates the query and prints the query answer to console.
     * @param args
     */
    public static void main(String[] args) {
        
       
        String PARSER_CLASS = "com.ontotext.wsmo4j.parser.WSMLParserImpl";

        // Set up factories for creating WSML elements 
        
        Map<String, String> leProperties = new HashMap<String, String>();
        leProperties.put(Factory.PROVIDER_CLASS,
         "org.deri.wsmo4j.logexpression.LogicalExpressionFactoryImpl");

        org.omwg.logexpression.LogicalExpressionFactory leFactory = 
            (org.omwg.logexpression.LogicalExpressionFactory) Factory.createLogicalExpressionFactory(leProperties);
        
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

        Parser wsmlparserimpl = org.wsmo.factory.Factory.createParser(parserProperties);
       
        // Set up serializer 
        
        Map<String, String> serializerProperties = new HashMap<String, String>();
        serializerProperties.put(org.wsmo.factory.Factory.PROVIDER_CLASS,
                "com.ontotext.wsmo4j.parser.WSMLSerializerImpl");

        Serializer ontologySerializer = org.wsmo.factory.Factory.createSerializer(serializerProperties);
        
        // Read simple ontology from file
        
        Ontology o = null;
        String ONTOLOGY_FILE = "examples/humanOntology.wsml";
        
        try {
            
            final Reader ontoReader = new FileReader(ONTOLOGY_FILE);
            
            
            final TopEntity[] identifiable = wsmlparserimpl.parse(ontoReader);
            if (identifiable.length > 0
                    && identifiable[0] instanceof Ontology) {
                o = (Ontology)identifiable[0];
            } else {
                return;
            }
            
        }
        catch (Exception e) {
            System.out.println("Unable to parse ontology: "
                    + e.getMessage());
            return;
        }
        
        // Print ontology in WSML
        
        try {
            System.out.println("WSML Ontology:\n");
            StringWriter sw = new StringWriter();
            ontologySerializer.serialize(new TopEntity[]{o}, sw);
            System.out.println(sw.toString());
            System.out.println("--------------\n\n");
        } catch (IOException e6) {
            // TODO Auto-generated catch block
            e6.printStackTrace();
        }
        
        System.out.println("Transforming ontology to axioms only ...");
        
        AxiomatizationNormalizer cs2le = new AxiomatizationNormalizer();
        Ontology normalizedOntology = cs2le.normalize(o);
        
        System.out.println("... finished.");
        
        try {
            System.out.println("Normalized WSML Ontology:\n");
            StringWriter sw = new StringWriter();
            ontologySerializer.serialize(new TopEntity[]{o}, sw);
            System.out.println(sw.toString());
            System.out.println("--------------\n\n");
        } catch (IOException e6) {
            // TODO Auto-generated catch block
            e6.printStackTrace();
        }
        
        
        
        
    }
}
