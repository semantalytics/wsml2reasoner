package wsmo4j;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.omwg.ontology.Ontology;
import org.wsmo.common.TopEntity;
import org.wsmo.factory.Factory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.Parser;

import test.BaseTest;

public class LocationBugTest extends TestCase {

    private static final String ONTOLOGY_FILE = "examples/locationBug.wsml";

    public static void main(String[] args) {
        junit.textui.TestRunner.run(LocationBugTest.class);
    }
    
    public void testLoadOntology() throws Exception{
        Map<String, String> leProperties = new HashMap<String, String>();
        leProperties.put(Factory.PROVIDER_CLASS,
                "org.deri.wsmo4j.logexpression.LogicalExpressionFactoryImpl");

        org.wsmo.factory.LogicalExpressionFactory leFactory = (org.wsmo.factory.LogicalExpressionFactory) Factory
                .createLogicalExpressionFactory(leProperties);

        WsmoFactory factory = Factory.createWsmoFactory(null);

        // Set up WSML parser

        Map<String, Object> parserProperties = new HashMap<String, Object>();
        parserProperties.put(Parser.PARSER_WSMO_FACTORY, factory);
        parserProperties.put(Parser.PARSER_LE_FACTORY, leFactory);

        parserProperties.put(org.wsmo.factory.Factory.PROVIDER_CLASS,
                "com.ontotext.wsmo4j.parser.WSMLParserImpl");

        Parser wsmlparserimpl = org.wsmo.factory.Factory
                .createParser(parserProperties);
//      Read simple ontology from file
        final Reader ontoReader = BaseTest.getReaderForFile(ONTOLOGY_FILE);
        final TopEntity[] identifiable = wsmlparserimpl.parse(ontoReader);
        if (identifiable.length > 0 && identifiable[0] instanceof Ontology) {
            System.out.println("Succesfully parsed ontology");
        } else {
            fail("Parsed failed");
        }
        
    }
    
}
