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
