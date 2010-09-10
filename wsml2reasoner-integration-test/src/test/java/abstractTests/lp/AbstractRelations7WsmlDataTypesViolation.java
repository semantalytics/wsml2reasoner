package abstractTests.lp;

import helper.LPHelper;
import helper.OntologyHelper;
import junit.framework.TestCase;

import org.wsml.reasoner.api.inconsistency.InconsistencyException;

import abstractTests.LP;

public abstract class AbstractRelations7WsmlDataTypesViolation extends TestCase implements LP {

    private static final String ONTOLOGY_FILE = "relations7_wsml_data_types_violation.wsml";

    public void testViolationDetected() throws Exception
	{
		String queryString = "RelationOfPrimitives(?x,?y,?z)";

    	try {
        	LPHelper.executeQuery( OntologyHelper.loadOntology( ONTOLOGY_FILE ), queryString, getLPReasoner() );
        	fail( "Relation member type constraint violation not detected." );
    	}
    	catch( InconsistencyException e ) {
    	}
	}
}