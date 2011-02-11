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
