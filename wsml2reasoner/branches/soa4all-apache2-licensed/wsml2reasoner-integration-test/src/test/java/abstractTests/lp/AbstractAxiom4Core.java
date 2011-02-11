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
