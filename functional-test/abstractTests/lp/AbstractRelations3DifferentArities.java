/**
 * 
 */
package abstractTests.lp;

import helper.LPHelper;
import helper.OntologyHelper;
import helper.Results;
import junit.framework.TestCase;
import org.omwg.logicalexpression.terms.Term;
import abstractTests.LP;

/**
 *
 */
public abstract class AbstractRelations3DifferentArities extends TestCase implements LP {

    private static final String NS = "http://example.com/relations3_different_arities#";

    private static final String ONTOLOGY_FILE = "files/relations3_different_arities.wsml";

    public void testDeclaredRelations() throws Exception {
    	for( int arity = 1; arity <= 6; ++arity )
    		runQuery( "Declared", arity );
    }

    public void testNotDeclaredRelations() throws Exception {
    	for( int arity = 1; arity <= 6; ++arity )
    		runQuery( "NotDeclared", arity );
    }

    public void testDeclaredAfterRelations() throws Exception {
    	for( int arity = 1; arity <= 6; ++arity )
    		runQuery( "DeclaredAfter", arity );
    }
    
    private void runQuery( String relation, int arity ) throws Exception
    {
    	StringBuilder query = new StringBuilder();
    	query.append( relation ).append( "(" );
    	
    	boolean first = true;
    	for( int i = 1; i <= arity; ++i ) {
    		if( first )
    			first = false;
    		else
    			query.append( ", " );
    		query.append( "?x" ).append( i );
    	}
    	query.append( ")" );

    	String[] resultVars = new String[ arity ];
    	for( int i = 1; i <= arity; ++i ) {
    		resultVars[ i-1 ] = "x" + i;
    	}

    	Term[] resultBindings = new Term[ arity ];
    	for( int i = 1; i <= arity; ++i ) {
    		resultBindings[ i-1 ] = Results.iri( NS + "i" + i );
    	}

    	Results r = new Results( resultVars );
    	r.addBinding( resultBindings );
    	
//    	System.out.println( "Query: " + query);
//    	System.out.println( OntologyHelper.toString( LPHelper.executeQuery( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query.toString(), getLPReasoner() ) ) );
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query.toString(), r.get(), getLPReasoner() );
    }
}
