/*
 * Copyright 2011, Semantic Technology Institute (STI) Innsbruck, 
 * University of Innsbruck, Technikerstrasse 21a, 6020 Innsbruck, Austria.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

    private static final String NS = "http://example.com/relations3#";

    private static final String ONTOLOGY_FILE = "relations3_different_arities.wsml";

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
