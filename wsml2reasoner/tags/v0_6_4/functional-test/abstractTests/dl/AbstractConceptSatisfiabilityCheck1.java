/**
 * WSML2Reasoner
 * An extensible framework for reasoning with WSML ontologies.
 * 
 * Copyright (C) 2008 Semantic Technology Institute (STI) Innsbruck, 
 * University of Innsbruck, Technikerstrasse 21a, 6020 Innsbruck, Austria.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 */
package abstractTests.dl;

import helper.DLHelper;
import helper.OntologyHelper;
import junit.framework.TestCase;
import abstractTests.DL;

/*
 */
public abstract class AbstractConceptSatisfiabilityCheck1 extends TestCase implements DL {
	
    private static final String NS = "http://example.com/concept_satisfiability1#";

    private static final String ONTOLOGY_FILE = "files/concept_satisfiability_check1.wsml";

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
