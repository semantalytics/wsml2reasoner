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

public abstract class AbstractRelations6WsmlDataTypes extends TestCase implements LP {

    private static final String ONTOLOGY_FILE = "relations6_wsml_data_types.wsml";

    public void testPopulated() throws Exception
	{
		String queryString = "RelationOfPrimitives(?x,?y,?z)";
    	Results r = new Results( "x", "y", "z" );
    	
//    	String NS = "http://example.com/relations6#";

    	r.addBinding( Results._integer( 1 ), Results._string( "one" ), Results._yearMonth( 2008, 12 ) );
    	r.addBinding( Results._integer( 2 ), Results._string( "two" ), Results._yearMonth( 2009, 12 ) );

		LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), queryString, r.get(), getLPReasoner() );
	}
}