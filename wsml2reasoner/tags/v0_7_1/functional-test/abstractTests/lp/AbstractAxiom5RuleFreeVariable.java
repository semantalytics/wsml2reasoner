/*
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
package abstractTests.lp;

import helper.LPHelper;
import helper.OntologyHelper;
import helper.Results;

import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Variable;

import abstractTests.LP;

public abstract class AbstractAxiom5RuleFreeVariable extends TestCase implements LP {

    private static final String NS = "http://example.com/axiom5#";

    private static final String ONTOLOGY_FILE = "files/axiom5_rule_free_variable.wsml";

    public void testFreeVariableInNafInBody() throws Exception {
    	Results r = new Results( "x" );
    	r.addBinding( Results.iri( NS + "brian" ) );
    	
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), "Bachelor(?x)", r.get(), getLPReasoner() );
    }

    public void testFreeVariableInHead() throws Exception {
    	Set<Map<Variable, Term>> results = LPHelper.executeQuery( OntologyHelper.loadOntology( ONTOLOGY_FILE ), "Everything(?x)", getLPReasoner() );
    	
    	// System.out.println( OntologyHelper.toString( results ) );

    	// Just make sure the rule fired. There could be all sorts of stuff in the Everything() relation.
    	assertTrue( results.size() > 0 );
    }
}
