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
import junit.framework.TestCase;
import abstractTests.LP;

/**
 * Check if the reasoner can handle a query with a non-stratified ontology.
 */
public abstract class AbstractStratification1NotStratified extends TestCase implements LP {

    public void testExample1() throws Exception {
        String ONTOLOGY_FILE = "stratified1_example1.wsml";
    	String query = "?x memberOf ?y";

    	// ** Just try and execute a query **
    	LPHelper.executeQuery( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, getLPReasoner() );
    }

    public void testExample2() throws Exception {
        String ONTOLOGY_FILE = "stratified1_example2.wsml";
    	String query = "?x memberOf ?y";

    	// ** Just try and execute a query **
    	LPHelper.executeQuery( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, getLPReasoner() );
    }    	
}