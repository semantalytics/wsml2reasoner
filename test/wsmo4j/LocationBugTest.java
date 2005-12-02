package wsmo4j;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.omwg.ontology.Ontology;
import org.wsml.reasoner.impl.WSMO4JManager;
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
        org.wsmo.factory.LogicalExpressionFactory leFactory = WSMO4JManager.getLogicalExpressionFactory();

        WsmoFactory factory = WSMO4JManager.getWSMOFactory();

        // Set up WSML parser

        Parser wsmlparserimpl = org.wsmo.factory.Factory
                .createParser(null);
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
