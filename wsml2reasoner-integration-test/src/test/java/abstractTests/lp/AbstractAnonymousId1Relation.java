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

import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Variable;

import abstractTests.LP;

public abstract class AbstractAnonymousId1Relation extends TestCase implements LP {

    public void testAnonymousIdInRelation() throws Exception {

		Set<Map<Variable, Term>> results =
			LPHelper.executeQuery( OntologyHelper.loadOntology( "anonymous_id1_relation.wsml" ),
						"rel(?c, ?d)", getLPReasoner() );
//    	System.out.println( OntologyHelper.toString( results ) );
    	
    	assertEquals( 2, results.size() );
    }
	
}
