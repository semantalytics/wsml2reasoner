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
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Attribute;
import org.omwg.ontology.Axiom;
import org.omwg.ontology.Concept;
import org.omwg.ontology.Instance;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Variable;
import org.omwg.ontology.WsmlDataType;
import org.wsml.reasoner.ConjunctiveQuery;
import org.wsml.reasoner.Rule;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.api.inconsistency.ConsistencyViolation;
import org.wsml.reasoner.transformation.le.LETestHelper;
import org.wsmo.common.Entity;
import org.wsmo.factory.DataFactory;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.ParserException;
import com.ontotext.wsmo4j.ontology.InstanceImpl;

public class DatalogBasedWSMLReasonerTest extends TestCase {
	
	protected DatalogBasedWSMLReasoner reasoner;
	protected WSMO4JManager wsmoManager;
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
		WSMO4JManager wsmoManager = new WSMO4JManager(); 
		wsmoFactory = wsmoManager.getWSMOFactory();
		dataFactory = wsmoManager.getDataFactory();
        leFactory = wsmoManager.getLogicalExpressionFactory();
        
        ontology = wsmoFactory.createOntology(wsmoFactory.createIRI(ns + "ont"));
        ontology.setDefaultNamespace(wsmoFactory.createIRI(ns));
        
        Concept badConcept = wsmoFactory.createConcept( wsmoFactory.createIRI( ns + "c" ) );
        badConcept.setOntology(ontology);
        ontology.addConcept(badConcept);
        
        Attribute badAttribute = badConcept.createAttribute(wsmoFactory.createIRI(ns + "a"));
		badAttribute.addType(dataFactory.createWsmlDataType(WsmlDataType.WSML_STRING));


		Axiom axiom1 = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "axiom01"));
		axiom1.addDefinition(LETestHelper.buildLE("_\"urn:a\" and _\"urn:b\""));
		axiom1.addDefinition(LETestHelper.buildLE("_\"urn:a\" subConceptOf _\"urn:c\""));
		ontology.addAxiom(axiom1);
		
		Axiom axiom2 = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "axiom02"));
		axiom2.addDefinition(LETestHelper.buildLE("_\"urn:e\" and _\"urn:f\""));
		axiom2.addDefinition(LETestHelper.buildLE("_\"urn:e\" memberOf _\"urn:d\""));
		ontology.addAxiom(axiom2);
		
		Axiom axiom3 = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "axiom03"));
		axiom1.addDefinition(LETestHelper.buildLE("_\"urn:g\" memberOf _\"urn:m\" :- _\"urn:f\""));
		ontology.addAxiom(axiom3);
		
		Instance i = new InstanceImpl(wsmoFactory.createIRI(ns + "aa"));
		i.addConcept(badConcept);
		i.addAttributeValue( wsmoFactory.createIRI( ns + "a" ), dataFactory.createWsmlInteger( new BigInteger("3")) );
		
		ontology.addInstance(i);
		
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(WSMLReasonerFactory.PARAM_BUILT_IN_REASONER, WSMLReasonerFactory.BuiltInReasoner.IRIS_WELL_FOUNDED);
        reasoner = (DatalogBasedWSMLReasoner) DefaultWSMLReasonerFactory.getFactory().createFlightReasoner(params);
        
        reasoner.registerOntology(ontology);
	}
	
	public void testQueryContainment() throws ParserException {
		assertFalse(reasoner.checkQueryContainment(LETestHelper.buildLE("_\"urn:a\""), LETestHelper.buildLE("_\"urn:b\"")));
		
		assertTrue(reasoner.checkQueryContainment(LETestHelper.buildLE("_\"urn:a\""), LETestHelper.buildLE("_\"urn:a\"")));
		assertTrue(reasoner.checkQueryContainment(LETestHelper.buildLE("?x subConceptOf _\"urn:c\""), LETestHelper.buildLE("?x subConceptOf _\"urn:c\"")));
		assertTrue(reasoner.checkQueryContainment(LETestHelper.buildLE("_\"urn:a\" subConceptOf _\"urn:c\""), LETestHelper.buildLE("_\"urn:a\" subConceptOf _\"urn:c\"")));
		assertTrue(reasoner.checkQueryContainment(LETestHelper.buildLE("_\"urn:e\" subConceptOf _\"urn:d\""), LETestHelper.buildLE("_\"urn:e\" subConceptOf _\"urn:d\"")));
		assertTrue(reasoner.checkQueryContainment(LETestHelper.buildLE("_\"urn:e\" subConceptOf _\"urn:d\""), LETestHelper.buildLE("?x subConceptOf _\"urn:d\" and ?y subConceptOf _\"urn:d\"")));

		
	}
	
	public void testGetQueryContainment() throws ParserException {
		
		assertTrue(reasoner.checkQueryContainment(LETestHelper.buildLE("?x subConceptOf _\"urn:d\""), LETestHelper.buildLE("?x subConceptOf _\"urn:d\" and ?y subConceptOf _\"urn:d\"")));
		
		Set<Map<Variable, Term>> set = reasoner.getQueryContainment(LETestHelper.buildLE("?x subConceptOf _\"urn:d\" and ?x subConceptOf _\"urn:d\""), LETestHelper.buildLE("?x subConceptOf _\"urn:d\""));
		
		for(Map <Variable, Term> map: set ){
			  for (Variable var : map.keySet()) {
				  System.out.println(var +" ; " + map.get(var));
				  
			  }
		}
		assertEquals(0,set.size());
		
	}

	public void testCheckConsistency() throws Exception {
		
		Set<ConsistencyViolation> viols = reasoner.checkConsistency();
		
		for(ConsistencyViolation vio : viols) {
			assertEquals(vio, null);	
		}
		// should be:
//		Concept[attribute ofType OtherConcept].
//		f[attribute hasValue 10] memberOf Concept.
		
		 Concept Concept01 = wsmoFactory.createConcept( wsmoFactory.createIRI(ns + "aConcept" ) );
	     Concept01.setOntology(ontology);
	     ontology.addConcept(Concept01);
	     Instance i1 = wsmoFactory.createInstance(wsmoFactory.createIRI(ns + "Instance1"), Concept01);
	     
	     
	     Concept OtherConcept01 = wsmoFactory.createConcept( wsmoFactory.createIRI(ns + "OtherConcept" ) );
	     OtherConcept01.setOntology(ontology);
	     ontology.addConcept(OtherConcept01);
	     
	     Attribute at = Concept01.createAttribute( wsmoFactory.createIRI(ns + "aAttribute"));
	     at.addType(OtherConcept01);
	     
	     Instance i2 = wsmoFactory.createInstance(wsmoFactory.createIRI(ns + "Instance2"), OtherConcept01);
	     i1.addAttributeValue(OtherConcept01.getIdentifier(), i2);

	     
	     Axiom axiomX1 = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "axiomX1"));
//		 axiomX1.addDefinition(LETestHelper.buildLE(Concept01.getIdentifier() + " ["+ Concept01.getIdentifier()+" ofType " + OtherConcept01.getIdentifier() +"]. "));
//	     axiomX1.addDefinition(LETestHelper.buildLE("_\"urn:a\"[_\"urn:a\" hasValue 10] memberOf " + OtherConcept01.getIdentifier() +" ."));
//	     axiomX1.addDefinition(LETestHelper.buildLE("_\"urn:a\"[_\"urn:a\" ofType _\"urn:b\"]" ));
//	     axiomX1.addDefinition(LETestHelper.buildLE("_\"urn:a\" memberOf OtherConcept"));
//	     axiomX1.addDefinition(LETestHelper.buildLE("_\"urn:a\"[_\"urn:a\" hasValue _\"urn:d\"]" ));
	     axiomX1.addDefinition(LETestHelper.buildLE("_\"urn:a\" memberOf OtherConcept"));
	     axiomX1.addDefinition(LETestHelper.buildLE("_\"urn:a\"[_\"urn:a\" hasValue _\"urn:d\"]" ));
		 ontology.addAxiom(axiomX1);

		/////////////////////////////////////////////////////////////////		 
//			Ontology ontology2 = wsmoFactory.createOntology(wsmoFactory.createIRI(ns
//					+ "AboutHumans"));
//			ontology2.setDefaultNamespace(wsmoFactory.createIRI(ns));
//
//			Concept humanConcept = wsmoFactory.createConcept(wsmoFactory.createIRI(ns+ "urn://Human"));
//			Attribute hasRelativeAttr = humanConcept.createAttribute(wsmoFactory.createIRI(ns+ "urn://hasRelative"));
//			// hasRelativeAttr.setSymmetric(true);
//			hasRelativeAttr.addType(humanConcept);
//			Concept locationConcept = wsmoFactory.createConcept(wsmoFactory.createIRI(ns
//					+ "urn://Location"));
//
//			Attribute hasParentAttr = humanConcept.createAttribute(wsmoFactory.createIRI(ns
//					+ "urn://hasParent"));
//			hasParentAttr.addType(humanConcept);
//
//			Attribute livesAtAttr = humanConcept.createAttribute(wsmoFactory.createIRI(ns
//					+ "urn://livesAt"));
//			livesAtAttr.addType(locationConcept);
//
//			Instance person1 = wsmoFactory.createInstance(wsmoFactory
//					.createIRI("urn://Person1"), humanConcept);
//			Instance person2 = wsmoFactory.createInstance(wsmoFactory
//					.createIRI("urn://Person2"), humanConcept);
//			Instance person3 = wsmoFactory.createInstance(wsmoFactory
//					.createIRI("urn://Person3"), humanConcept);
//
//			person1.addAttributeValue(hasParentAttr.getIdentifier(), person2);
//			person1.addAttributeValue(hasParentAttr.getIdentifier(), wsmoFactory
//					.createInstance(wsmoFactory.createAnonymousID()));
//
//			Axiom person1LivesAx = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "urn://person1LivesSomewhere"));
//			person1LivesAx.addDefinition(leFactory.createConjunction(
//					leFactory.createAttributeValue(person1.getIdentifier(),
//							livesAtAttr.getIdentifier(), leFactory
//									.createAnonymousID((byte) 1)), leFactory
//							.createMemberShipMolecule(leFactory
//									.createAnonymousID((byte) 1), locationConcept
//									.getIdentifier())));
//	
//			Axiom person2LivesAx = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns
//					+ "urn://person2LivesSomewhere"));
//			person2LivesAx.addDefinition(leFactory.createConjunction(leFactory
//					.createConjunction(leFactory.createConjunction(leFactory
//							.createConjunction(leFactory.createAttributeValue(
//									person2.getIdentifier(), livesAtAttr
//											.getIdentifier(), leFactory
//											.createAnonymousID((byte) 1)),
//									leFactory.createMemberShipMolecule(leFactory
//											.createAnonymousID((byte) 1),
//											locationConcept.getIdentifier())),
//							leFactory.createAttributeValue(person3.getIdentifier(),
//									livesAtAttr.getIdentifier(), leFactory
//											.createAnonymousID((byte) 2))),
//							leFactory.createMemberShipMolecule(leFactory
//									.createAnonymousID((byte) 2), locationConcept
//									.getIdentifier())), leFactory
//					.createAttributeValue(person3.getIdentifier(), livesAtAttr
//							.getIdentifier(), wsmoFactory.createAnonymousID())));
//
//			ontology2.addConcept(humanConcept);
//			ontology2.addConcept(locationConcept);
//			ontology2.addInstance(person1);
//			ontology2.addInstance(person2);
//			ontology2.addAxiom(person1LivesAx);
//			ontology2.addAxiom(person2LivesAx);
//
//
//		reasoner.deRegister();
        reasoner.registerOntology(ontology);

		viols = reasoner.checkConsistency();
		
		for(ConsistencyViolation vio : viols) {
			assertEquals(vio, null);	
		}

		
	}
	
	public void testEntails() throws ParserException {
		
		assertTrue(reasoner.entails(LETestHelper.buildLE("_\"urn:a\"")));
		assertTrue(reasoner.entails(LETestHelper.buildLE("_\"urn:b\"")));
		assertTrue(reasoner.entails(LETestHelper.buildLE("_\"urn:a\" subConceptOf _\"urn:c\"")));
		assertTrue(reasoner.entails(LETestHelper.buildLE("?x subConceptOf _\"urn:c\"")));
		
		assertFalse(reasoner.entails(LETestHelper.buildLE("?x subConceptOf _\"urn:h\"")));
		assertFalse(reasoner.entails(LETestHelper.buildLE("_\"urn:z\"")));
		assertFalse(reasoner.entails(LETestHelper.buildLE("_\"urn:c\"")));
		
	}
	
	public void testEntailsSetTrue() throws ParserException {
		Set <LogicalExpression> set = new HashSet<LogicalExpression>();
		set.add(LETestHelper.buildLE("_\"urn:a\""));
		set.add(LETestHelper.buildLE("_\"urn:a\" subConceptOf _\"urn:c\""));
		
		assertTrue(reasoner.entails(set));
		
 	}
	
	public void testEntailsSetFalse() throws ParserException {
		Set <LogicalExpression> set = new HashSet<LogicalExpression>();
		set.add(LETestHelper.buildLE("_\"urn:z\""));
		set.add(LETestHelper.buildLE("_\"urn:a\" subConceptOf _\"urn:c\""));
		
		assertFalse(reasoner.entails(set));
		
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
	
	public void testConvertEntitiesStdRules() {
		
		Set<Entity> in = new HashSet<Entity>();
		
		Set<Rule> out = reasoner.convertEntities(in);
		// test if out contains standard rules 
		assertTrue(containStdRules(out));
	}
	
	
	public void testConvertEntities() throws ParserException {
		
		Set<Entity> in = new HashSet<Entity>();
		
		Axiom axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns+ "axiom00"));
		axiom.addDefinition(LETestHelper.buildLE("?x subConceptOf ?concept99"));

		in.add(axiom);

		
		Set<Rule> out = reasoner.convertEntities(in);
		assertTrue(containStdRules(out));
		
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
		
		
		// TODO more of that Queries ??? 
		
		
	}

	
	private boolean containStdRules(Set<Rule> theRules){
		int rules = 0;
		
		for(Rule r : theRules) {
			// test if out contains standard rules
			if(r.getHead().toString().equals("http://www.wsmo.org/reasoner/VIOLATION()") && r.getBody().toString().equals("[http://www.wsmo.org/reasoner/MIN_CARD(?v1, ?v2, ?v3)]")) {
				rules++;
			}
			
			else if(r.getHead().toString().equals("wsml-member-of(?instance, ?concept2)") && r.getBody().toString().equals("[wsml-member-of(?instance, ?concept), wsml-subconcept-of(?concept, ?concept2)]")) {
				rules++;
			}
			
			else if(r.getHead().toString().equals("http://www.wsmo.org/reasoner/VIOLATION()") && r.getBody().toString().equals("[http://www.wsmo.org/reasoner/NAMED_USER(?v1)]")) {
				rules++;
			}
			
			else if(r.getHead().toString().equals("http://temp/knownConcept(?concept)") && r.getBody().toString().equals("[wsml-of-type(?concept, ?attribute, ?concept2)]")) {
				rules++;
			}
			
			else if(r.getHead().toString().equals("http://temp/knownConcept(?concept)") && r.getBody().toString().equals("[wsml-member-of(?instance, ?concept)]")) {
				rules++;
			}
			
			else if(r.getHead().toString().equals("http://temp/direct/subConceptOf(?concept, ?concept3)") && r.getBody().toString().equals("[wsml-subconcept-of(?concept, ?concept3), !http://temp/indirect/subConceptOf(?concept, ?concept3)]")) {
				rules++;
			}
			
			else if(r.getHead().toString().equals("http://temp/indirect/memberOf(?instance, ?concept)") && r.getBody().toString().equals("[wsml-member-of(?instance, ?concept), wsml-member-of(?instance, ?concept2), wsml-subconcept-of(?concept2, ?concept), http://www.wsmo.org/wsml/wsml-syntax#inequal(?concept2, ?concept)]")) {
				rules++;
			}
			
			else if(r.getHead().toString().equals("http://www.wsmo.org/reasoner/VIOLATION()") && r.getBody().toString().equals("[http://www.wsmo.org/reasoner/MAX_CARD(?v1, ?v2, ?v3)]")) {
				rules++;
			}
			
			else if(r.getHead().toString().equals("http://temp/knownConcept(?concept2)") && r.getBody().toString().equals("[wsml-subconcept-of(?concept2, ?concept3)]")) {
				rules++;
			}
			
			else if(r.getHead().toString().equals("wsml-subconcept-of(?concept, ?concept)") && r.getBody().toString().equals("[http://temp/knownConcept(?concept)]")) {
				rules++;
			}
			
			else if(r.getHead().toString().equals("http://www.wsmo.org/reasoner/VIOLATION()") && r.getBody().toString().equals("[http://www.wsmo.org/reasoner/ATTR_OFTYPE(?v1, ?v2, ?v3, ?v4, ?v5)]")) {
				rules++;
			}
			
			else if(r.getHead().toString().equals("http://www.wsmo.org/reasoner/VIOLATION()") && r.getBody().toString().equals("[http://www.wsmo.org/reasoner/UNNAMED_USER(?v1)]")) {
				rules++;
			}
			
			else if(r.getHead().toString().equals("http://temp/knownConcept(?concept)") && r.getBody().toString().equals("[wsml-implies-type(?concept, ?attribute, ?concept2)]")) {
				rules++;
			}
			
			else if(r.getHead().toString().equals("http://temp/knownConcept(?concept3)") && r.getBody().toString().equals("[wsml-subconcept-of(?concept2, ?concept3)]")) {
				rules++;
			}
			
			else if(r.getHead().toString().equals("wsml-member-of(?instance2, ?concept2)") && r.getBody().toString().equals("[wsml-implies-type(?concept, ?attribute, ?concept2), wsml-member-of(?instance, ?concept), wsml-has-value(?instance, ?attribute, ?instance2)]")) {
				rules++;
			}
			
			else if(r.getHead().toString().equals("wsml-subconcept-of(?concept, ?concept3)") && r.getBody().toString().equals("[wsml-subconcept-of(?concept, ?concept2), wsml-subconcept-of(?concept2, ?concept3)]")) {
				rules++;
			}
			
			else if(r.getHead().toString().equals("http://temp/direct/memberOf(?instance, ?concept)") && r.getBody().toString().equals("[wsml-member-of(?instance, ?concept), !http://temp/indirect/memberOf(?instance, ?concept)]")) {
				rules++;
			}
			
			else if(r.getHead().toString().equals("http://temp/indirect/subConceptOf(?concept, ?concept2)") && r.getBody().toString().equals("[wsml-subconcept-of(?concept, ?concept3), wsml-subconcept-of(?concept3, ?concept2), http://www.wsmo.org/wsml/wsml-syntax#inequal(?concept, ?concept3), http://www.wsmo.org/wsml/wsml-syntax#inequal(?concept3, ?concept2)]")) {
				rules++;
			}
		
		}
		
		if(rules == 18) {
			return true;
		}
		
		return false;
	}
	
}
	

	
	
	
	
	
	

		
	
	
	
	

