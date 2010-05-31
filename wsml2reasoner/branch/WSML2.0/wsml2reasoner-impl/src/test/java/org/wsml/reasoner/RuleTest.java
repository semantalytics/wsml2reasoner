/*
 * WSML2Reasoner
 * An extensible framework for reasoning with WSML ontologies.
 * 
 * Copyright (C) 2008 Semantic Technology Institute (STI) Innsbruck, 
 * University of Innsbruck, Technikerstrasse 21a, 6020 Innsbruck, Austria.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
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
