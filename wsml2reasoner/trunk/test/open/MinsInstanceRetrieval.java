package open;

import org.omwg.logicalexpression.LogicalExpression;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.api.WSMLReasonerFactory.BuiltInReasoner;
import org.wsmo.common.IRI;

import base.BaseReasonerTest;

public class MinsInstanceRetrieval extends BaseReasonerTest {
	
    private static final String NS = "urn:dogsworld#";
    private static final String ONTOLOGY_FILE = "files/dogsworld.wsml";

    BuiltInReasoner previous;
    
    @Override
    protected void setUp() throws Exception {
    	super.setUp();
    	setupScenario(ONTOLOGY_FILE);
    	previous = BaseReasonerTest.reasoner;
    }
    
    @Override
    protected void tearDown() throws Exception {
    	super.tearDown();
    	resetReasoner(previous);
    }
    
	
    public void instanceRetrieval() throws Exception {
        String query = "Anne memberOf CatOwner";
        LogicalExpression qExpression = leFactory.createLogicalExpression(query, o);
        assertTrue(wsmlReasoner.executeGroundQuery((IRI)o.getIdentifier(), qExpression));
    }
    
    public void testFlightReasoners() throws Exception{
//    	resetReasoner(WSMLReasonerFactory.BuiltInReasoner.IRIS);
//    	instanceRetrieval();
    	
    	resetReasoner(WSMLReasonerFactory.BuiltInReasoner.MINS);
    	instanceRetrieval();
    	
//    	resetReasoner(WSMLReasonerFactory.BuiltInReasoner.KAON2);
//    	instanceRetrieval();
    } 
}
