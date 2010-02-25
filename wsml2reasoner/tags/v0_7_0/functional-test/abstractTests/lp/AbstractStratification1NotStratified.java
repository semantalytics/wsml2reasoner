
package abstractTests.lp;

import helper.LPHelper;
import helper.OntologyHelper;
import junit.framework.TestCase;
import abstractTests.LP;

/**
 * Check if the reasoner can handle a query with a non-stratified ontology.
 */
public abstract class AbstractStratification1NotStratified extends TestCase implements LP {

    public void testExample1() throws Exception {
        String ONTOLOGY_FILE = "files/stratified1_example1.wsml";
    	String query = "?x memberOf ?y";

    	// ** Just try and execute a query **
    	LPHelper.executeQuery( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, getLPReasoner() );
    }

    public void testExample2() throws Exception {
        String ONTOLOGY_FILE = "files/stratified1_example2.wsml";
    	String query = "?x memberOf ?y";

    	// ** Just try and execute a query **
    	LPHelper.executeQuery( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, getLPReasoner() );
    }    	
}
