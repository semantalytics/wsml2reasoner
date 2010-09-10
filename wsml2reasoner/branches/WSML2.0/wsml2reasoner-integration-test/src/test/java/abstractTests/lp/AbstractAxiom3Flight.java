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

public abstract class AbstractAxiom3Flight extends TestCase implements LP {

    private static final String NS = "http://example.com/axiom3#";

    private static final String ONTOLOGY_FILE = "axiom3_flight.wsml";

    public void testConjunctionInHead() throws Exception {
    	Results r = new Results( "x" );
    	r.addBinding( Results.iri( NS + "brian" ) );
    	
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), "?x memberOf Alive", r.get(), getLPReasoner() );
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), "?x memberOf Intelligent", r.get(), getLPReasoner() );
    }

    public void testImpliesInHead() throws Exception {
    	Results r = new Results( "x" );
    	r.addBinding( Results.iri( NS + "sopwithCamel" ) );
    	
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), "?x memberOf Biplane", r.get(), getLPReasoner() );
    }

    public void testImpliedByInHead() throws Exception {
    	Results r = new Results( "x" );
    	r.addBinding( Results.iri( NS + "stromboli" ) );
    	
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), "?x memberOf Volcano", r.get(), getLPReasoner() );
    }

    public void testEquivalentInHead() throws Exception {
    	Results r = new Results( "x" );
    	r.addBinding( Results.iri( NS + "physics" ) );
    	r.addBinding( Results.iri( NS + "maths" ) );
    	
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), "?x memberOf Faculty", r.get(), getLPReasoner() );

    	r.addBinding( Results.iri( NS + "fire" ) );
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), "?x memberOf Department", r.get(), getLPReasoner() );
    }

    public void testConjunctionInBody() throws Exception {
    	Results r = new Results( "x" );
    	r.addBinding( Results.iri( NS + "mouse" ) );
    	
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), "?x memberOf Cuddly", r.get(), getLPReasoner() );
    }

    public void testDisjunctionInBody() throws Exception {
    	Results r = new Results( "x" );
    	r.addBinding( Results.iri( NS + "plutonium" ) );
    	r.addBinding( Results.iri( NS + "benzene" ) );
    	r.addBinding( Results.iri( NS + "plutonium_cyanide" ) );
    	
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), "?x memberOf Dangerous", r.get(), getLPReasoner() );
    }

    public void testNafInBody() throws Exception {
    	Results r = new Results( "x" );
    	r.addBinding( Results.iri( NS + "one" ) );
    	r.addBinding( Results.iri( NS + "three" ) );
    	
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), "?x[odd hasValue _boolean(\"true\")]", r.get(), getLPReasoner() );
    }
}
