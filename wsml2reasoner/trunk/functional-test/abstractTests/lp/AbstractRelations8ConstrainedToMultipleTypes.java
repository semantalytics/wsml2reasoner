package abstractTests.lp;

import helper.LPHelper;
import helper.OntologyHelper;
import helper.Results;
import junit.framework.TestCase;
import abstractTests.LP;

public abstract class AbstractRelations8ConstrainedToMultipleTypes extends TestCase implements LP {

    private static final String ONTOLOGY_FILE = "files/relations8_constrained_to_multiple_types.wsml";

    public void testPopulated() throws Exception
	{
		String queryString = "RelationOfPrimitives(?x,?y,?z)";
    	Results r = new Results( "x", "y", "z" );
    	
    	String NS = "http://example.com/relations8#";

    	r.addBinding( Results.iri( NS + "a" ), Results.iri( NS + "bc" ), Results.iri( NS + "bcd" ) );

    	System.out.println( OntologyHelper.toString( LPHelper.executeQuery( OntologyHelper.loadOntology( ONTOLOGY_FILE ), queryString, getLPReasoner() ) ) );

		LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), queryString, r.get(), getLPReasoner() );
	}
}