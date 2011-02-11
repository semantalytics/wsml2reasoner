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
public abstract class AbstractFunctionSymbols1Example extends TestCase implements LP{

    private static final String NS = "http://example.com/function_symbols1#";

    private static final String ONTOLOGY_FILE = "function_symbols1_example.wsml";
    
    public void testFSHasVoucher() throws Exception {
        String query = "?x memberOf travelVoucher";
    
        Results results = new Results( "x" );
        
        results.addBinding( Results.fn( NS+ "f", Results.iri( NS+"my_trainTicket1" ) ) );
        results.addBinding( Results.fn( NS+ "f", Results.iri( NS+"my_trainTicket2" ) ) );
        results.addBinding( Results.fn( NS+ "f", Results.iri( NS+"my_trainTicket3" ) ) );
        
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology(ONTOLOGY_FILE ), query, results.get(), getLPReasoner() );
    }
    
    public void testFSHasValidVoucher() throws Exception {
        String query = "?x memberOf validVoucher";

        Results results = new Results( "x" );
        
        results.addBinding( Results.fn( NS+ "f", Results.iri( NS+"my_trainTicket1" ), Results.iri( NS+"customer1" ) ) );
        results.addBinding( Results.fn( NS+ "f", Results.iri( NS+"my_trainTicket2" ), Results.iri( NS+"customer2" ) ) );

    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology(ONTOLOGY_FILE ), query, results.get(), getLPReasoner() );
    }
}
