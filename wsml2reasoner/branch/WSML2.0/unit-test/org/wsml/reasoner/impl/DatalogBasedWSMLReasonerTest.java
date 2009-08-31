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

package org.wsml.reasoner.impl;


import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Attribute;
import org.omwg.ontology.Axiom;
import org.omwg.ontology.Concept;
import org.omwg.ontology.Instance;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Variable;
import org.omwg.ontology.WsmlDataType;
import org.sti2.wsmo4j.factory.FactoryImpl;
import org.wsml.reasoner.ConjunctiveQuery;
import org.wsml.reasoner.Rule;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.api.inconsistency.ConsistencyViolation;
import org.wsml.reasoner.transformation.le.LETestHelper;
import org.wsmo.common.Entity;
import org.wsmo.factory.DataFactory;
import org.wsmo.factory.Factory;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.ParserException;

import com.ontotext.wsmo4j.ontology.InstanceImpl;

public class DatalogBasedWSMLReasonerTest extends TestCase {
	
	protected DatalogBasedWSMLReasoner reasoner;
	protected String ns = "http://ex.org#";
	protected WsmoFactory wsmoFactory;
	protected LogicalExpressionFactory leFactory;
	protected Ontology ontology;
	protected DataFactory dataFactory;
	
	public DatalogBasedWSMLReasonerTest() {
		super();
	}

	protected void setUp() throws Exception {
		super.setUp();
		Factory factory = new FactoryImpl(); 
		wsmoFactory = factory.getWsmoFactory();
		dataFactory = factory.getWsmlDataFactory();
        leFactory = factory.getLogicalExpressionFactory();
        
        ontology = wsmoFactory.createOntology(wsmoFactory.createIRI(ns + "ont"));
        ontology.setDefaultNamespace(wsmoFactory.createIRI(ns));
        
        Concept badConcept = wsmoFactory.createConcept( wsmoFactory.createIRI( ns + "c" ) );
        badConcept.setOntology(ontology);
        ontology.addConcept(badConcept);
        
        Attribute badAttribute = badConcept.createAttribute(wsmoFactory.createIRI(ns + "a"));
		badAttribute.addType(dataFactory.createDataType(WsmlDataType.WSML_STRING));


		Axiom axiom1 = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "axiom01"));
		axiom1.addDefinition(LETestHelper.buildLE("_\"urn:a\" and _\"urn:b\""));
		axiom1.addDefinition(LETestHelper.buildLE("_\"urn:a\" subConceptOf _\"urn:c\""));
		ontology.addAxiom(axiom1);
		
		Axiom axiom2 = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "axiom02"));
		axiom2.addDefinition(LETestHelper.buildLE("_\"urn:e\" and _\"urn:f\""));
		axiom2.addDefinition(LETestHelper.buildLE("_\"urn:e\" memberOf _\"urn:d\""));
		ontology.addAxiom(axiom2);
		
		Axiom axiom3 = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "axiom03"));
		axiom3.addDefinition(LETestHelper.buildLE("_\"urn:g\" memberOf _\"urn:m\" :- _\"urn:f\""));
		ontology.addAxiom(axiom3);
		
		Instance i = new InstanceImpl(wsmoFactory.createIRI(ns + "aa"));
		i.addConcept(badConcept);
		i.addAttributeValue( wsmoFactory.createIRI( ns + "a" ), dataFactory.createInteger( new BigInteger("3")) );
		
		ontology.addInstance(i);
		
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(WSMLReasonerFactory.PARAM_BUILT_IN_REASONER, WSMLReasonerFactory.BuiltInReasoner.IRIS_WELL_FOUNDED);
        reasoner = (DatalogBasedWSMLReasoner) DefaultWSMLReasonerFactory.getFactory().createFlightReasoner(params);
        
        reasoner.registerOntology(ontology);
	}
	
	public void testGetQueryContainment() throws ParserException {
		
		assertTrue(reasoner.checkQueryContainment(LETestHelper.buildLE("?x subConceptOf _\"urn:d\""), LETestHelper.buildLE("?x subConceptOf _\"urn:d\" and ?y subConceptOf _\"urn:d\"")));
		
		Set<Map<Variable, Term>> set = reasoner.getQueryContainment(LETestHelper.buildLE("?x subConceptOf _\"urn:d\" and ?x subConceptOf _\"urn:d\""), LETestHelper.buildLE("?x subConceptOf _\"urn:d\""));
		
		String str = "";
		for(Map <Variable, Term> map: set ){
			  for (Variable var : map.keySet()) {
				 str = str + " " + map.get(var).toString();  
			  }
		}
		assertEquals(2, set.size());
		assertTrue(str.contains("FROZEN_VARIABLE_x"));
		assertTrue(str.contains("urn:d"));
		
	}
	
	public void testQueryContainment() throws ParserException {
		assertFalse(reasoner.checkQueryContainment(LETestHelper.buildLE("_\"urn:a\""), LETestHelper.buildLE("_\"urn:b\"")));
		assertTrue(reasoner.checkQueryContainment(LETestHelper.buildLE("_\"urn:a\""), LETestHelper.buildLE("_\"urn:a\"")));
		assertTrue(reasoner.checkQueryContainment(LETestHelper.buildLE("?x subConceptOf _\"urn:c\""), LETestHelper.buildLE("?x subConceptOf _\"urn:c\"")));
		assertTrue(reasoner.checkQueryContainment(LETestHelper.buildLE("_\"urn:a\" subConceptOf _\"urn:c\""), LETestHelper.buildLE("_\"urn:a\" subConceptOf _\"urn:c\"")));
		assertTrue(reasoner.checkQueryContainment(LETestHelper.buildLE("_\"urn:e\" subConceptOf _\"urn:d\""), LETestHelper.buildLE("_\"urn:e\" subConceptOf _\"urn:d\"")));
		assertTrue(reasoner.checkQueryContainment(LETestHelper.buildLE("_\"urn:e\" subConceptOf _\"urn:d\""), LETestHelper.buildLE("?x subConceptOf _\"urn:d\" and ?y subConceptOf _\"urn:d\"")));

	}
	
	public void testCheckConsistency() throws Exception {
		// just testing if an empty reasoner is consistent
		
		Ontology ontology2 = wsmoFactory.createOntology(wsmoFactory.createIRI(ns + "ont"));
	    ontology.setDefaultNamespace(wsmoFactory.createIRI(ns));
	    reasoner.deRegister();
	    reasoner.registerOntology(ontology2);
		
		Set<ConsistencyViolation> viols = reasoner.checkConsistency();
		
		for(ConsistencyViolation vio : viols) {
			assertEquals(vio, null);	
		}
		
		reasoner.deRegister();
		reasoner.registerOntology(ontology);

	}
	
	public void testAsk() throws ParserException {
		assertTrue(reasoner.ask(LETestHelper.buildLE("_\"urn:a\"")));
		assertTrue(reasoner.ask(LETestHelper.buildLE("_\"urn:b\"")));
		assertTrue(reasoner.ask(LETestHelper.buildLE("_\"urn:a\" subConceptOf _\"urn:c\"")));
		assertTrue(reasoner.ask(LETestHelper.buildLE("?x subConceptOf _\"urn:c\"")));
		
		assertFalse(reasoner.ask(LETestHelper.buildLE("?x subConceptOf _\"urn:h\"")));
		assertFalse(reasoner.ask(LETestHelper.buildLE("_\"urn:z\"")));
		assertFalse(reasoner.ask(LETestHelper.buildLE("_\"urn:c\"")));
		
	}
	
	public void testExecuteQuery () throws ParserException {
		Set <Map <Variable, Term>> set = reasoner.executeQuery(LETestHelper.buildLE("?x subConceptOf _\"urn:c\""));
		int count = 0;
		for(Map <Variable, Term> map: set ){
			  for (Variable var : map.keySet()) {
				if( (map.get(var)).toString().equals("urn:c")) {
					count++;
				}
				if( (map.get(var)).toString().equals("urn:a")) {
					count++;
				}
			}
		}
		assertEquals(2,count);
	}
		
	public void testConvertEntities() throws ParserException {
		
		Set<Entity> in = new HashSet<Entity>();
		
		Axiom axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns+ "axiom00"));
		axiom.addDefinition(LETestHelper.buildLE("?x subConceptOf ?concept99"));
		in.add(axiom);
		Set<Rule> out = reasoner.convertEntities(in);
		
		assertEquals(21, out.size());

		boolean b = false;
		for(Rule r : out) {
			if(r.getHead().toString().equals("wsml-subconcept-of(?x, ?concept99)")){
				assertEquals(r.getBody().toString(), "[]");
				b = true;
			}
		}
		assertEquals(true, b);
	}
	
	
	public void testConvertQuery() throws ParserException {
		
		Set <ConjunctiveQuery> out =  reasoner.convertQuery(LETestHelper.buildLE("_\"urn:a\"")); 
		for(ConjunctiveQuery cq : out) {
			assertEquals( (" ?- urn:a()."), cq.toString()); 
		}
		
		out = reasoner.convertQuery(LETestHelper.buildLE("_\"urn:a\" and _\"urn:b\""));
		for(ConjunctiveQuery cq : out) {
			assertEquals( (" ?- urn:a(), urn:b()."), cq.toString());
		}

		out = reasoner.convertQuery(LETestHelper.buildLE("?concept0 subConceptOf ?concept1"));
		for(ConjunctiveQuery cq : out) {
			assertEquals( (" ?- wsml-subconcept-of(?concept0, ?concept1)."), cq.toString());
		}
		
		out = reasoner.convertQuery(LETestHelper.buildLE("?concept0 subConceptOf ?x"));
		for(ConjunctiveQuery cq : out) {
			assertEquals( (" ?- wsml-subconcept-of(?concept0, ?x)."), cq.toString());
		}
		
		out = reasoner.convertQuery(LETestHelper.buildLE("?x subConceptOf ?x"));
		for(ConjunctiveQuery cq : out) {
			assertEquals( (" ?- wsml-subconcept-of(?x, ?x)."), cq.toString());
		}
		
		out = reasoner.convertQuery(LETestHelper.buildLE("?x memberOf ?x"));
		for(ConjunctiveQuery cq : out) {
			assertEquals( (" ?- wsml-member-of(?x, ?x)."), cq.toString());
		}	
		
		out = reasoner.convertQuery(LETestHelper.buildLE("_\"urn:a\" or _\"urn:b\""));
		for(ConjunctiveQuery cq : out) {
			if(!(cq.toString().equals(" ?- urn:a().") || cq.toString().equals(" ?- urn:b().")))
				assertEquals("should be: \" ?- urn:a().\" or \" ?- urn:b().\"", cq.toString());
		}	
		
		out = reasoner.convertQuery(LETestHelper.buildLE("?x[?x hasValue ?x]"));
		for(ConjunctiveQuery cq : out) {
			assertEquals( (" ?- wsml-has-value(?x, ?x, ?x)."), cq.toString());
		}
		
		out = reasoner.convertQuery(LETestHelper.buildLE("?x[?x ofType ?x]"));
		for(ConjunctiveQuery cq : out) {
			assertEquals( (" ?- wsml-of-type(?x, ?x, ?x)."), cq.toString());
		}
		
		out = reasoner.convertQuery(LETestHelper.buildLE("?x[?x impliesType ?x]"));
		for(ConjunctiveQuery cq : out) {
			assertEquals( (" ?- wsml-implies-type(?x, ?x, ?x)."), cq.toString());
		}
		
	}

}
	

	
	
	
	
	
	

		
	
	
	
	

