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

public abstract class AbstractMetaModelling1 extends TestCase implements LP {

    private static final String NS = "http://example.com/metamodelling1#";

    private static final String ONTOLOGY_FILE = "files/meta-modelling1.wsml";

    public void testImplicitConceptAndInstance() throws Exception {
    	String query = "?x memberOf SubConceptOfC";
    	
    	Results r = new Results( "x" );
    	r.addBinding( Results.iri( NS + "c1" ) );
    	r.addBinding( Results.iri( NS + "c2" ) );
    	r.addBinding( Results.iri( NS + "c3" ) );
    	r.addBinding( Results.iri( NS + "c4" ) );
    	
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, r.get(), getLPReasoner() );
    }

    public void testExplicitConceptAndInstance() throws Exception {
    	String query = "?x memberOf InstanceAndConcept and ?x subConceptOf InstanceAndConceptSuperClass";
    	
    	Results r = new Results( "x" );
    	r.addBinding( Results.iri( NS + "InstanceAndConcept" ) );
    	
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, r.get(), getLPReasoner() );
    }
}
