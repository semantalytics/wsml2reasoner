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

public abstract class AbstractRelations8ConstrainedToMultipleTypes extends TestCase implements LP {

    private static final String ONTOLOGY_FILE = "relations8_constrained_to_multiple_types.wsml";

    public void testPopulated() throws Exception
	{
		String queryString = "rel(?x,?y,?z)";
    	Results r = new Results( "x", "y", "z" );
    	
    	String NS = "http://example.com/relations8#";

    	r.addBinding( Results.iri( NS + "a" ), Results.iri( NS + "bc" ), Results.iri( NS + "bcd" ) );

    	System.out.println( OntologyHelper.toString( LPHelper.executeQuery( OntologyHelper.loadOntology( ONTOLOGY_FILE ), queryString, getLPReasoner() ) ) );

		LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), queryString, r.get(), getLPReasoner() );
	}
}