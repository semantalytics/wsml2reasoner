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

public abstract class AbstractMetaModelling1 extends TestCase implements LP {

    private static final String NS = "http://example.com/metamodelling1#";

    private static final String ONTOLOGY_FILE = "meta-modelling1.wsml";

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
