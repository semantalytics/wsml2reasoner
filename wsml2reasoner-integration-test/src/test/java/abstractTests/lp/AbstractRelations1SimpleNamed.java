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

/**
 *
 */
public abstract class AbstractRelations1SimpleNamed extends TestCase implements LP {

    private static final String NS = "http://example.com/relations1#";

    private static final String ONTOLOGY_FILE = "relations1_simple_named.wsml";

    public void testDeclaredRelation() throws Exception {
    	String query = "Declared(?x1,?x2)";

    	Results r = new Results( "x1", "x2" );
    	r.addBinding( Results.iri( NS + "i11" ), Results.iri( NS + "i21" ) );
    	r.addBinding( Results.iri( NS + "i11" ), Results.iri( NS + "i23" ) );
    	
//    	System.out.println( OntologyHelper.toString( LPHelper.executeQuery( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, getLPReasoner() ) ) );
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, r.get(), getLPReasoner() );
    }

    public void testNotDeclaredRelation() throws Exception {
    	String query = "NotDeclared(?x1,?x2)";
    	Results r = new Results( "x1", "x2" );
    	r.addBinding( Results.iri( NS + "i12" ), Results.iri( NS + "i22" ) );
    	r.addBinding( Results.iri( NS + "i12" ), Results.iri( NS + "i21" ) );
    	
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, r.get(), getLPReasoner() );
    }    	

    public void testDeclaredAfterRelation() throws Exception {
    	String query = "DeclaredAfter(?x1,?x2)";
    	Results r = new Results( "x1", "x2" );
    	r.addBinding( Results.iri( NS + "i13" ), Results.iri( NS + "i23" ) );
    	r.addBinding( Results.iri( NS + "i13" ), Results.iri( NS + "i22" ) );
    	
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, r.get(), getLPReasoner() );
    }    	
}
