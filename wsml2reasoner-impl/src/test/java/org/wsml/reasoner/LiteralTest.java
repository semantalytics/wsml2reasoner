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
package org.wsml.reasoner;

import junit.framework.TestCase;

import org.omwg.logicalexpression.terms.Term;

public class LiteralTest extends TestCase
{
	public void testInstantiate()
	{
		String predicate = "TEST_PREDICATE";
		Term[] terms = new Term[ 0 ];
	    Literal literal = new Literal(true, predicate, terms);
	    
	    assertEquals( true, literal.isPositive() );
	    assertEquals( predicate, literal.getPredicateUri() );

	    literal = new Literal(false, predicate, terms);
	    
	    assertEquals( false, literal.isPositive() );
	}
	
}
