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
 * Example tests for function symbols (constructed terms).
 */
public abstract class AbstractFunctionSymbols2Example extends TestCase implements LP{

    private static final String NS = "http://example.com/function_symbols2#";

    private static final String ONTOLOGY_FILE = "function_symbols2_example.wsml";
    
    public void testForMe() throws Exception {
        String query = "o2#mediated1(?x,o2#Citizen)[o2#hasName hasValue " + 
			"o2#mediated1(?x,o2#Name)] memberOf o2#Citizen";
    
        Results results = new Results( "x" );
        
        results.addBinding( Results.iri( NS + "me" ) );
        
//    	System.out.println( OntologyHelper.toString( LPHelper.executeQuery( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, getLPReasoner() ) ) );
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology(ONTOLOGY_FILE ), query, results.get(), getLPReasoner() );
    }
    
}
