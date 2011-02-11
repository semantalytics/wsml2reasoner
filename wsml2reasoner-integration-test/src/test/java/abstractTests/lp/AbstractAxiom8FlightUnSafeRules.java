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
 */
public abstract class AbstractAxiom8FlightUnSafeRules extends TestCase implements LP {

//    private static final String NS = "http://example.com/axiom8#";

    private static final String ONTOLOGY_FILE = "axiom8_flight_unsafe_rules.wsml";

    public void testSafeRules() throws Exception {
  
    	try
    	{
    		// this exception is not thrown for well founded since well founded augments safety!
	    	LPHelper.executeQuery( OntologyHelper.loadOntology( ONTOLOGY_FILE ), "?x memberOf ?y", getLPReasoner() );
	    	fail( "Some kind of exception should be thrown here, because the ontology contains an unsafe rule." );
    	}
    	catch( Exception e)
    	{
    	}
    }
}
