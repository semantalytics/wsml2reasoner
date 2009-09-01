/**
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
import org.omwg.ontology.Variable;
import org.sti2.wsmo4j.factory.WsmlFactoryContainer;
import org.wsmo.factory.FactoryContainer;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.wsml.ParserException;

public class ConjunctiveQueryTest extends TestCase {

	private ConjunctiveQuery query;

	public ConjunctiveQueryTest() {
		super();
	}

	public void testGetLiterals() {
		ArrayList <Literal> list = new ArrayList<Literal>();
		int i = 0;
		for (i = 0; i < 10; i++) {
			Literal literal = new Literal(true, new String("TEST_PREDICATE_" + i), new Term[0]);
			list.add(literal);
		}
		query = new ConjunctiveQuery(list);
		
		List <Literal> ls = query.getLiterals();
		
		i = 0;
		for(Literal l : ls){
			assertEquals(l,new Literal(true, new String("TEST_PREDICATE_" + i), new Term[0]));
			i++;	
		}
		
		ls.get(8).equals(new Literal(true, new String("TEST_PREDICATE_8"), new Term[0]));
	}
	
	public void testGetVariables() throws ParserException {
		query = null;
		ArrayList <Literal> list = new ArrayList<Literal>();
		
		FactoryContainer factory = new WsmlFactoryContainer();
		LogicalExpressionFactory f = factory.getLogicalExpressionFactory();
		
		Variable test1 = f.createVariable("Variable01");
		Variable test2 = f.createVariable("Variable02");
		Term[] term = {test1, test2};
		
		int i = 0;
		for (i = 0; i < 10; i++) {
			Literal literal = new Literal(true, new String("TEST_PREDICATE"), term);
			list.add(literal);
	
		}
		query = new ConjunctiveQuery(list);
		
		List<Variable> l = query.getVariables();
		assertEquals(l.size(), 2);
		assertEquals(l.get(0), f.createVariable("Variable01"));
		assertEquals(l.get(1), f.createVariable("Variable02"));

		
	}
	
	public void testEquals() {
		query = null;
		ArrayList <Literal> list = new ArrayList<Literal>();
		int i = 0;
		for (i = 0; i < 10; i++) {
			Literal literal = new Literal(true, new String("TEST_PREDICATE_" + i), new Term[0]);
			list.add(literal);
		}
		query = new ConjunctiveQuery(list);
		
		ConjunctiveQuery query2 = new ConjunctiveQuery(list);
		
		assertEquals(true, query.equals(query2));
		
		ArrayList <Literal> list2 = new ArrayList<Literal>();
		for (i = 0; i < 10; i++) {
			Literal literal = new Literal(true, new String("TEST_PREDICATE_" + i), new Term[0]);
			list2.add(literal);
		}
		list2.remove(0);
		query2 = new ConjunctiveQuery(list2);
		
		assertEquals(false, query.equals(query2));
		
	}
	
	public void testEquals02() {
		query = null;
		ArrayList <Literal> list = new ArrayList<Literal>();
	
		Literal literal01 = new Literal(true, new String("TEST_PREDICATE_A"), new Term[0]);
		Literal literal02 = new Literal(true, new String("TEST_PREDICATE_B"), new Term[0]);
		list.add(literal01);
		list.add(literal02);
		
		
		query = new ConjunctiveQuery(list);
		
	}
	
	

}
