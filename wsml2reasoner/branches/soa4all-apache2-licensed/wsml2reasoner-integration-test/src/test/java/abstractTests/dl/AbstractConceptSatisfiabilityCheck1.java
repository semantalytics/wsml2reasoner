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
package abstractTests.dl;

import helper.DLHelper;
import helper.OntologyHelper;
import junit.framework.TestCase;
import abstractTests.DL;

/*
 */
public abstract class AbstractConceptSatisfiabilityCheck1 extends TestCase implements DL {
	
    //private static final String NS = "http://example.com/concept_satisfiability1#";

    private static final String ONTOLOGY_FILE = "concept_satisfiability_check1.wsml";

    public void testIllegalConceptExpression() throws Exception {

    	// These are not legal expressions and the reasoner should throw an exception.
    	checkCantCheck( "Mary[hasChild hasValue Jim]" );
    	checkCantCheck( "exists ?X (?X memberOf Human)" );	// CLASS CAST!?!?!?!
    	checkCantCheck( "Mary[hasChild hasValue Jim]" );
    }
    
    public void testSatisfiable() throws Exception {

    	checkEntailed( "?X memberOf Human" );
    	checkEntailed( "?X memberOf Human and ?X memberOf Flight" );
    }

    public void testNotSatisfiable() throws Exception {

//    	checkNotEntailed( "?X memberOf Person and neg ?X memberOf Human" );
    	checkNotEntailed( "?x[hasParent hasValue ?p] memberOf Child and neg ?p memberOf Human" );
    }

    private void checkNotEntailed( String expression ) throws Exception {
    	assertFalse( DLHelper.isEntailed( OntologyHelper.loadOntology( ONTOLOGY_FILE ), expression, getDLReasoner() ) );
    }

    private void checkEntailed( String expression ) throws Exception {
    	assertTrue( DLHelper.isEntailed( OntologyHelper.loadOntology( ONTOLOGY_FILE ), expression, getDLReasoner() ) );
    }

    private void checkCantCheck( String expression ) throws Exception {
    	try {
    		DLHelper.isEntailed( OntologyHelper.loadOntology( ONTOLOGY_FILE ), expression, getDLReasoner() );
    		fail();
    	}
    	catch( Exception e ) {
    	}
    }
}
