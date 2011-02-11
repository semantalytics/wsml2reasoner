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

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.omwg.logicalexpression.terms.Term;

public class RuleTest extends TestCase
{
	public void testRule()
	{
	    Literal head = new Literal(true, "PREDICATE1", new Term[ 0 ]);
	    Literal body1 = new Literal(true, "PREDICATE2", new Term[ 0 ]);
	    Literal body2 = new Literal(true, "PREDICATE3", new Term[ 0 ]);

	    List<Literal> body = new ArrayList<Literal>();
	    body.add( body1 );
	    body.add( body2 );
	    
	    Rule rule = new Rule(head, body);

	    assertEquals( head, rule.getHead() );
	    assertEquals( body, rule.getBody() );
	    assertFalse( rule.isFact() );
	    assertFalse( rule.isConstraint() );

	    Rule rule2 = new Rule(head, body);
	    assertEquals( rule, rule2 );
	}

	public void testNegatedPredicatesNotAllowedInHead()
	{
	    Literal head = new Literal(false, "PREDICATE1", new Term[ 0 ]);
	    try
	    {
	    	new Rule(head, null);
	    	fail( "Negated predicate should not be allowed for a rule head." );
	    }
	    catch( DatalogException e )
	    {
	    }
	}

	public void testConstraint()
	{
	    Literal body1 = new Literal(true, "PREDICATE2", new Term[ 0 ]);
	    Literal body2 = new Literal(true, "PREDICATE3", new Term[ 0 ]);

	    List<Literal> body = new ArrayList<Literal>();
	    body.add( body1 );
	    body.add( body2 );
	    
	    Rule rule = new Rule(null, body);

	    assertFalse( rule.isFact() );
	    assertTrue( rule.isConstraint() );
	}

	public void testFact()
	{
	    Literal head = new Literal(true, "PREDICATE1", new Term[ 0 ]);
	    
	    Rule rule = new Rule(head, null);

	    assertTrue( rule.isFact() );
	    assertFalse( rule.isConstraint() );
	}
}
