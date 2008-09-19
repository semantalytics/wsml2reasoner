/**
 * 
 */
package abstractTests.lp;

import helper.LPHelper;
import helper.OntologyHelper;
import helper.Results;
import junit.framework.TestCase;
import abstractTests.LP;

/**
 *
 */
public abstract class AbstractRelations1SimpleNamed extends TestCase implements LP {

    private static final String NS = "http://example.com/relations1_simple_named#";

    private static final String ONTOLOGY_FILE = "files/relations1_simple_named.wsml";

    public void testDeclaredRelation() throws Exception {
    	String query = "Declared(?x1,?x2)";

    	Results r = new Results( "x1", "x2" );
    	r.addBinding( r.iri( NS + "i11" ), r.iri( NS + "i21" ) );
    	r.addBinding( r.iri( NS + "i11" ), r.iri( NS + "i23" ) );
    	
//    	System.out.println( OntologyHelper.toString( LPHelper.executeQuery( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, getLPReasoner() ) ) );
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, r.get(), getLPReasoner() );
    }

    public void testNotDeclaredRelation() throws Exception {
    	String query = "NotDeclared(?x1,?x2)";
    	Results r = new Results( "x1", "x2" );
    	r.addBinding( r.iri( NS + "i12" ), r.iri( NS + "i22" ) );
    	
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, r.get(), getLPReasoner() );
    }    	

    public void testDeclaredAfterRelation() throws Exception {
    	String query = "DeclaredAfter(?x1,?x2)";
    	Results r = new Results( "x1", "x2" );
    	r.addBinding( r.iri( NS + "i13" ), r.iri( NS + "i23" ) );
    	
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, r.get(), getLPReasoner() );
    }    	
}
