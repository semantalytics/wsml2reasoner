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

import helper.CoreHelper;
import helper.OntologyHelper;
import helper.Results;
import junit.framework.TestCase;
import abstractTests.Core;

public abstract class AbstractAxiom4Core extends TestCase implements Core {

    private static final String NS = "http://example.com/axiom4#";

    private static final String ONTOLOGY_FILE = "axiom4_core.wsml";

    public void testImpliedBy() throws Exception {
    	Results r = new Results( "X" );
    	r.addBinding( Results.iri( NS + "mary" ) );
    	
    	CoreHelper.queryXMemberOfConceptAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), getReasoner(), NS + "Woman", r.get() );
    }

    public void testImplies() throws Exception {
    	Results r = new Results( "X" );
    	r.addBinding( Results.iri( NS + "john" ) );
    	
    	CoreHelper.queryXMemberOfConceptAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), getReasoner(), NS + "Man", r.get() );
    }

    public void testEquivalent() throws Exception {
    	Results r = new Results( "X" );
    	r.addBinding( Results.iri( NS + "elephant" ) );
    	r.addBinding( Results.iri( NS + "planet" ) );
    	
    	CoreHelper.queryXMemberOfConceptAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), getReasoner(), NS + "Large", r.get() );
    	CoreHelper.queryXMemberOfConceptAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), getReasoner(), NS + "Big", r.get() );
    }

    public void testDisjunction() throws Exception {
    	Results r = new Results( "X" );
    	r.addBinding( Results.iri( NS + "knife" ) );
    	r.addBinding( Results.iri( NS + "rifle" ) );
    	
    	CoreHelper.queryXMemberOfConceptAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), getReasoner(), NS + "Weapon", r.get() );
    }

    public void testConjunction() throws Exception {
    	Results r = new Results( "X" );
    	r.addBinding( Results.iri( NS + "swan" ) );
    	
    	CoreHelper.queryXMemberOfConceptAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), getReasoner(), NS + "Bird", r.get() );
    }
}
