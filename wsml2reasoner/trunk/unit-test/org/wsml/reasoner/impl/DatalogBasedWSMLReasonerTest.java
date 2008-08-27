/**
 * 
 */
package org.wsml.reasoner.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Axiom;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.Rule;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.api.inconsistency.ConsistencyViolation;
import org.wsml.reasoner.impl.DatalogBasedWSMLReasoner;
import org.wsml.reasoner.impl.DefaultWSMLReasonerFactory;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsml.reasoner.transformation.le.LETestHelper;
import org.wsmo.common.Entity;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.ParserException;

import junit.framework.TestCase;

public class DatalogBasedWSMLReasonerTest extends TestCase {
	
	protected DatalogBasedWSMLReasoner reasoner;
	protected WSMO4JManager wsmoManager;
	protected String ns = "http://ex.org#";
	protected WsmoFactory wsmoFactory;
	protected LogicalExpressionFactory leFactory;
	protected Ontology ontology;
	
	public DatalogBasedWSMLReasonerTest() {
		super();
	}

	protected void setUp() throws Exception {
		super.setUp();
		WSMO4JManager wsmoManager = new WSMO4JManager(); 
		wsmoFactory = wsmoManager.getWSMOFactory();
        leFactory = wsmoManager.getLogicalExpressionFactory();
        
        ontology = wsmoFactory.createOntology(wsmoFactory.createIRI(ns + "ont"));
        ontology.setDefaultNamespace(wsmoFactory.createIRI(ns));	
        
        Axiom axiom1 = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "axiom01"));
		axiom1.addDefinition(LETestHelper.buildLE("_\"urn:a\" and _\"urn:b\""));
		axiom1.addDefinition(LETestHelper.buildLE("_\"urn:a\" subConceptOf _\"urn:c\""));
		ontology.addAxiom(axiom1);
		
		Axiom axiom2 = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "axiom02"));
		axiom2.addDefinition(LETestHelper.buildLE("_\"urn:e\" and _\"urn:f\""));
		axiom2.addDefinition(LETestHelper.buildLE("_\"urn:e\" memberOf _\"urn:d\""));
		ontology.addAxiom(axiom2);
		
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(WSMLReasonerFactory.PARAM_BUILT_IN_REASONER, WSMLReasonerFactory.BuiltInReasoner.IRIS_WELL_FOUNDED);
        reasoner = (DatalogBasedWSMLReasoner) DefaultWSMLReasonerFactory.getFactory().createFlightReasoner(params);
        
        reasoner.registerOntology(ontology);
	}
	
	public void testQueryContainment() throws ParserException {
		assertTrue(reasoner.checkQueryContainment(LETestHelper.buildLE("_\"urn:a\""), LETestHelper.buildLE("_\"urn:a\"")));
		assertTrue(reasoner.checkQueryContainment(LETestHelper.buildLE("?x subConceptOf _\"urn:c\""), LETestHelper.buildLE("?x subConceptOf _\"urn:c\"")));
		assertTrue(reasoner.checkQueryContainment(LETestHelper.buildLE("_\"urn:a\" subConceptOf _\"urn:c\""), LETestHelper.buildLE("_\"urn:a\" subConceptOf _\"urn:c\"")));
		assertTrue(reasoner.checkQueryContainment(LETestHelper.buildLE("_\"urn:e\" subConceptOf _\"urn:d\""), LETestHelper.buildLE("_\"urn:e\" subConceptOf _\"urn:d\"")));
		
		assertTrue(reasoner.checkQueryContainment(LETestHelper.buildLE("_\"urn:e\" subConceptOf _\"urn:d\""), LETestHelper.buildLE("?x subConceptOf _\"urn:d\" and ?y subConceptOf _\"urn:d\"")));
		
		assertFalse(reasoner.checkQueryContainment(LETestHelper.buildLE("_\"urn:a\""), LETestHelper.buildLE("_\"urn:b\"")));
		
	}
	
	public void testGetQueryContainment() throws ParserException {
		
		assertTrue(reasoner.checkQueryContainment(LETestHelper.buildLE("?x subConceptOf _\"urn:d\""), LETestHelper.buildLE("?x subConceptOf _\"urn:d\" and ?y subConceptOf _\"urn:d\"")));
		Set<Map<Variable, Term>> set = reasoner.getQueryContainment(LETestHelper.buildLE("_\"urn:e\" subConceptOf _\"urn:d\""), LETestHelper.buildLE("?x subConceptOf _\"urn:d\" and _\"urn:e\" subConceptOf _\"urn:d\""));
		assertEquals(2,set.size());
		for(Map <Variable, Term> map: set ){
			  for (Variable var : map.keySet()) {
				  System.out.println(var +" ; " + map.get(var));
				  
			  }
		}
		
	}
	
	public void testCheckConsistency() {
		
		Set<ConsistencyViolation> viols = reasoner.checkConsistency();
		
		for(ConsistencyViolation vio : viols) {
//			System.out.println(vio.toString());
			fail();
			
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
	
	public void testConvertEntities01() {
		
		Set<Entity> in = new HashSet<Entity>();
		
		Set<Rule> out = reasoner.convertEntities(in);
		int rules = 0;
	
		for(Rule r : out) {
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
		assertEquals(18,rules);

	}
	
//	public void testConvertEntities02() throws ParserException {
//		
//		Set<Entity> in = new HashSet<Entity>();
//		Axiom axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns+ "axiom00"));
//		axiom.addDefinition(LETestHelper.buildLE("?concept99 subConceptOf ?concept99"));
//		in.add(axiom);
//		
//		Set<Rule> out = reasoner.convertEntities(in);
//		
//		for(Rule r : out) {
//			System.out.println(r.toString());
//		}
//		
//		
//	}
	
}
	

	
	
	
	
	
	

		
	
	
	
	

