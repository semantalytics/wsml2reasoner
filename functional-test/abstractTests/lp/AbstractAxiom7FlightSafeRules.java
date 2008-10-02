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
import junit.framework.TestCase;
import abstractTests.LP;

/**
 */
public abstract class AbstractAxiom7FlightSafeRules extends TestCase implements LP {

    private static final String NS = "http://example.com/axiom7#";

    private static final String ONTOLOGY_FILE = "files/axiom7_flight_safe_rules.wsml";

    public void testSimple() throws Exception {
    	
    	Results r = new Results( "x", "y" );
    	r.addBinding( Results.iri( NS + "a1" ), Results.iri( NS + "b1" ) );
    	
        LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), "simple(?x,?y)", r.get(), getLPReasoner() );
    }

    public void testInequality() throws Exception {
    	
    	Results r = new Results( "x", "y" );
    	r.addBinding( Results.iri( NS + "a1" ), Results.iri( NS + "b1" ) );
    	
        LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), "in_equality(?x,?y)", r.get(), getLPReasoner() );
    }

    public void testEquality() throws Exception {
    	
    	Results r = new Results( "x", "y" );
    	r.addBinding( Results.iri( NS + "a1" ), Results.iri( NS + "a1" ) );
    	
        LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), "equality(?x,?y)", r.get(), getLPReasoner() );
    }
}
