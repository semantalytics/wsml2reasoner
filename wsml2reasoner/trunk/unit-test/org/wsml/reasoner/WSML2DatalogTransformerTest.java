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

import java.util.HashSet;

import java.util.Set;

import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Axiom;
import org.omwg.ontology.Ontology;

import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsml.reasoner.transformation.le.LETestHelper;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.ParserException;

import junit.framework.TestCase;

public class WSML2DatalogTransformerTest extends TestCase {

	private WSML2DatalogTransformer transformer;
	protected Ontology ontology;
	protected String ns = "http://ex.org#";
	protected WsmoFactory wsmoFactory;
	protected LogicalExpressionFactory leFactory;
	protected Axiom axiom;
	protected WSMO4JManager wsmoManager;

	public WSML2DatalogTransformerTest() {
		super();
	}

	protected void setUp() throws Exception {
		super.setUp();
		wsmoManager = new WSMO4JManager();
		transformer = new WSML2DatalogTransformer(wsmoManager);

		wsmoFactory = wsmoManager.getWSMOFactory();
		leFactory = wsmoManager.getLogicalExpressionFactory();

		ontology = wsmoFactory
				.createOntology(wsmoFactory.createIRI(ns + "ont"));
		ontology.setDefaultNamespace(wsmoFactory.createIRI(ns));

		axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "axiom"));
		ontology.addAxiom(axiom);
		
	}

	public void testTransform01() throws ParserException {
		
		LogicalExpression le = LETestHelper
				.buildLE("_\"urn:a\"[_\"urn:a\" hasValue _#]");
		Set<Rule> out = transformer.transform(le);
		for (Rule r : out) {
			assertTrue(r.toString().contains("wsml-has-value"));
			assertTrue(r.toString().contains("urn:a, urn:a, _#"));
		}

		le = LETestHelper.buildLE("_\"urn:a\"[_\"urn:a\" impliesType _#]");
		out = transformer.transform(le);
		for (Rule r : out) {
			assertTrue(r.toString().contains("wsml-implies-type"));
			assertTrue(r.toString().contains("urn:a, urn:a, _#"));
		}

		le = LETestHelper.buildLE("_\"urn:a\"[_\"urn:a\" ofType _#]");
		out = transformer.transform(le);
		for (Rule r : out) {
			assertTrue(r.toString().contains("wsml-of-type"));
			assertTrue(r.toString().contains("urn:a, urn:a, _#"));
		}

		le = LETestHelper.buildLE("_\"urn:a\" subConceptOf _\"urn:b\"");
		out = transformer.transform(le);
		for (Rule r : out) {
			assertTrue(r.toString().contains("wsml-subconcept-of"));
			assertTrue(r.toString().contains("urn:a, urn:b"));
		}

		le = LETestHelper.buildLE("_\"urn:a\" implies _\"urn:b\"");
		out = transformer.transform(le);
		for (Rule r : out) {
			assertEquals(r.toString(), ("urn:b() :- urn:a()."));
		}

		
		out = checkTransform("_\"urn:a\"[_\"urn:a\" impliesType _#]");
		
		for (Rule r : out) {
			assertTrue(r.toString().contains(
					("wsml-implies-type(urn:a, urn:a, _#).")));
		}

		
		out = checkTransform("_\"urn:a\" subConceptOf \"urn:b\"");
		
		for (Rule r : out) {
			assertTrue(r.toString().contains(
					("wsml-subconcept-of(urn:a, urn:b).")));
		}	
	}
	
	public void testTransform02() throws IllegalArgumentException, ParserException {
		Set<Rule> out = checkTransform("\"_urn:a\"[\"_urn:a\" hasValue \"_urn:b\"] and \"_urn:b\" memberOf \"_urn:a\"");
		for (Rule r : out) {
			assertTrue(r.toString().contains(("wsml-member-of(_urn:b, _urn:a).")));
		}
		
		out = checkTransform("_\"urn:a\"[_\"urn:a\" ofType _#]", "_\"urn:a\"[_\"urn:a\" hasValue \"_urn:c\" ]");
		String str = "";
		for (Rule r : out) {
			str += r.toString();
		}
		assertTrue(str.contains("wsml-of-type(urn:a, urn:a, _#)."));
		assertTrue(str.contains("wsml-has-value(urn:a, urn:a, _urn:c)."));
		
		out = checkTransform(" _#[_# ofType _#]");
		str = "";
		for (Rule r : out) {
			str += r.toString();
		}
		assertTrue(str.contains("wsml-of-type(_#, _#, _#)."));

		out = checkTransform("_\"urn:a\"[_\"urn:a\" hasValue  _\"urn:b\"] :- _\"urn:c\" ");
		str = "";
		for (Rule r : out) {
			str += r.toString();
		}
		assertTrue(str.contains("wsml-has-value(urn:a, urn:a, urn:b) :- urn:c()."));
		
	}
	
	public void testGenerateAuxilliaryRules() {
		Set<Rule> set = transformer.generateAuxilliaryRules();
		assertEquals(15, set.size());
	}
	
	
	public void testExtractConstantsUsedAsConcepts() throws ParserException {
		LogicalExpression le = LETestHelper.buildLE("_\"urn:a\"[_\"urn:a\" ofType _\"urn:b\"] implies _\"urn:d\"[_\"urn:d\" hasValue \"_urn:c\" ] ");
		Set<Term> set = transformer.extractConstantsUsedAsConcepts(le);
		assertEquals(2 ,set.size());
		String r = "";
		for(Term t : set) {
			r += " " + t.toString();
		}
		assertTrue(r.contains("urn:b"));
		assertTrue(r.contains("urn:a"));
		
	}

	public void testTransformSubConceptOfInHead() throws ParserException {
		LogicalExpression le = LETestHelper.buildLE("_\"urn:a\" subConceptOf \"urn:b\" :- _\"urn:c\" ");
		Set<Rule> out = transformer.transform(le);
		for (Rule r : out) {
			assertEquals("wsml-subconcept-of(urn:a, urn:b) :- urn:c().", r.toString());
		}
	}
	
	public void testTransformSubConceptOfInBody() throws ParserException {
		LogicalExpression le = LETestHelper .buildLE(" _\"urn:c\" :- _\"urn:a\" subConceptOf \"urn:b\"  ");
		Set<Rule> out = transformer.transform(le);
		int count = 0;
		for (Rule r : out) {
			if (r.toString().equals("http://temp/knownConcept(urn:b).")) {
				count++;
			}
			else if (r.toString().equals("http://temp/knownConcept(urn:a).")) {
				count++;	
			}
			else if (r.toString().equals("urn:c() :- wsml-subconcept-of(urn:a, urn:b).")){
				count++;	
			}
			
		}
		assertEquals(3,count);
	}

	public void testTransformBodyHead() throws ParserException {

		LogicalExpression le = LETestHelper.buildLE("_\"urn:a\" :- _\"urn:b\"");
		Set<Rule> out = transformer.transform(le);
		for (Rule r : out) {
			assertEquals("urn:a()", r.getHead().toString());
			assertEquals("[urn:b()]", r.getBody().toString());
		}

	}

	public void testTransformImpliesInHead() throws ParserException {

		// More than one implication in the given WSML rule detected!
		// [ 2002256 ] 'implies' should be allowed in rule bodies.
		LogicalExpression le = LETestHelper
				.buildLE("_\"urn:a\" implies _\"urn:b\" :- _\"urn:c\"");
		
			Set<Rule> out = transformer.transform(le);

			for (Rule r : out) {
				assertEquals("urn:a()", r.getHead().toString());
				assertEquals("[urn:b()]", r.getBody().toString());
			}

	}

	public void testTransformImpliesInBody() throws ParserException {

		// More than one implication in the given WSML rule detected!
		// [ 2002256 ] 'implies' should be allowed in rule bodies.
		LogicalExpression le = LETestHelper
				.buildLE("_\"urn:a\"  :- _\"urn:c\" implies _\"urn:b\"");
		
			Set<Rule> out = transformer.transform(le);

			for (Rule r : out) {
				System.out.println("Head: " + r.getHead().toString());
				System.out.println("Body: " + r.getBody().toString());
			}

	}

	public void testTransformEquivalentInHead() throws ParserException {

		// More than one implication in the given WSML rule detected!
		// [ 2002256 ] 'implies' should be allowed in rule bodies.
		LogicalExpression le = LETestHelper
				.buildLE("_\"urn:a\" equivalent _\"urn:b\" :- _\"urn:c\"");
		
			Set<Rule> out = transformer.transform(le);

			for (Rule r : out) {
				System.out.println("Head: " + r.getHead().toString());
				System.out.println("Body: " + r.getBody().toString());
			}
		
	}

	public void testTransformEquivalentInBody() throws ParserException {

		// More than one implication in the given WSML rule detected!
		// [ 2002256 ] 'implies' should be allowed in rule bodies.
		LogicalExpression le = LETestHelper
				.buildLE("_\"urn:a\"  :- _\"urn:c\" equivalent _\"urn:b\"");
		
			Set<Rule> out = transformer.transform(le);

			for (Rule r : out) {
				System.out.println("Head: " + r.getHead().toString());
				System.out.println("Body: " + r.getBody().toString());
			}
		

	}

	public void testTransformImpliedByInHead() throws ParserException {

		// More than one implication in the given WSML rule detected!
		// [ 2002256 ] 'implies' should be allowed in rule bodies.
		LogicalExpression le = LETestHelper
				.buildLE("_\"urn:a\" impliedBy _\"urn:b\" :- _\"urn:c\"");
		
			Set<Rule> out = transformer.transform(le);

			for (Rule r : out) {
				System.out.println("Head: " + r.getHead().toString());
				System.out.println("Body: " + r.getBody().toString());
			}

	}

	public void testTransformImpliedByInBody() throws ParserException {

		// More than one implication in the given WSML rule detected!
		// [ 2002256 ] 'implies' should be allowed in rule bodies.
		LogicalExpression le = LETestHelper.buildLE("_\"urn:a\" :- _\"urn:c\" impliedBy _\"urn:b\"");
		
			Set<Rule> out = transformer.transform(le);

			for (Rule r : out) {
				System.out.println("Head: " + r.getHead().toString());
				System.out.println("Body: " + r.getBody().toString());
			}
	}
	
	private Set<Rule> checkTransform(String ... le) throws IllegalArgumentException, ParserException{
		HashSet<LogicalExpression> h = new HashSet<LogicalExpression>();
		for(String str : le) {
			h.add(LETestHelper.buildLE(str));
		}
		return transformer.transform(h);
	}
			

}
