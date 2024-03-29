package abstractTests.lp;

import helper.LPHelper;
import helper.OntologyHelper;
import helper.Results;
import junit.framework.TestCase;
import abstractTests.LP;

public abstract class AbstractRelations6WsmlDataTypes extends TestCase implements LP {

    private static final String ONTOLOGY_FILE = "files/relations6_wsml_data_types.wsml";

    public void testPopulated() throws Exception
	{
		String queryString = "RelationOfPrimitives(?x,?y,?z)";
    	Results r = new Results( "x", "y", "z" );
    	
    	String NS = "http://example.com/relations6#";

    	r.addBinding( Results._integer( 1 ), Results.string( "one" ), Results.yearMonth( 2008, 12 ) );
    	r.addBinding( Results._integer( 2 ), Results.string( "two" ), Results.yearMonth( 2009, 12 ) );

		LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), queryString, r.get(), getLPReasoner() );
	}
}