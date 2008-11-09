package abstractTests.lp;

import helper.LPHelper;
import helper.OntologyHelper;
import helper.Results;
import junit.framework.TestCase;
import abstractTests.LP;

public abstract class AbstractRelations6WsmlDataTypes extends TestCase implements LP {

    private static final String ONTOLOGY_FILE = "files/relations6_wsml_data_types.wsml";

//    public void testPopulated() throws Exception
//	{
//		String queryString = "RelationOfPrimitives(?x,?y)";
//    	Results r = new Results( "x", "y" );
//    	
//    	String NS = "http://example.com/relations6#";
//
//    	r.addBinding( Results.iri( NS + "a1" ), Results.iri( NS + "b1" ) );
//    	r.addBinding( Results.iri( NS + "a2" ), Results.iri( NS + "b2" ) );
//
//    	System.out.println( OntologyHelper.toString( LPHelper.executeQuery( OntologyHelper.loadOntology( ONTOLOGY_FILE ), queryString, getLPReasoner() ) ) );
//
//		LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), queryString, r.get(), getLPReasoner() );
//	}

    public void testPopulated() throws Exception
	{
		String queryString = "RelationOfPrimitives(?x,?y)";
    	Results r = new Results( "x", "y" );
    	
    	String NS = "http://example.com/relations6#";

    	r.addBinding( Results._integer( 1 ), Results.string( "one" ) );
    	r.addBinding( Results._integer( 2 ), Results.string( "two" ) );

    	System.out.println( OntologyHelper.toString( LPHelper.executeQuery( OntologyHelper.loadOntology( ONTOLOGY_FILE ), queryString, getLPReasoner() ) ) );

		LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), queryString, r.get(), getLPReasoner() );
	}
}