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
import junit.framework.TestCase;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Axiom;
import org.omwg.ontology.Ontology;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsml.reasoner.transformation.le.LETestHelper;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.ParserException;

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
				.buildLE("_\"urn:a\"[_\"urn:a\" hasValue _\"urn:b\"]");
		Set<Rule> out = transformer.transform(le);
		assertEquals(1, out.size());
		for (Rule r : out) {
			checkRule(r, LiteralTestHelper.createPosLiteral(
					WSML2DatalogTransformer.PRED_HAS_VALUE, "urn:a", "urn:a",
					"urn:b"));
		}

		le = LETestHelper
				.buildLE("_\"urn:a\"[_\"urn:a\" impliesType _\"urn:b\"]");
		out = transformer.transform(le);
		assertEquals(1, out.size());
		for (Rule r : out) {
			checkRule(r, LiteralTestHelper.createPosLiteral(
					WSML2DatalogTransformer.PRED_IMPLIES_TYPE, "urn:a",
					"urn:a", "urn:b"));
		}

		le = LETestHelper.buildLE("_\"urn:a\"[_\"urn:a\" ofType _\"urn:b\"]");
		out = transformer.transform(le);
		assertEquals(1, out.size());
		for (Rule r : out) {
			checkRule(r, LiteralTestHelper.createPosLiteral(
					WSML2DatalogTransformer.PRED_OF_TYPE, "urn:a", "urn:a",
					"urn:b"));
		}

		le = LETestHelper.buildLE("_\"urn:a\" subConceptOf _\"urn:b\"");
		out = transformer.transform(le);
		assertEquals(1, out.size());
		for (Rule r : out) {
			checkRule(r, LiteralTestHelper.createPosLiteral(
					WSML2DatalogTransformer.PRED_SUB_CONCEPT_OF, "urn:a",
					"urn:b"));
		}

		le = LETestHelper.buildLE(" _\"urn:a\" implies _\"urn:b\"");
		out = transformer.transform(le);
		assertEquals(1, out.size());
		for (Rule r : out) {
			checkRule(r, LiteralTestHelper.createSimplePosLiteral("urn:b"),
					LiteralTestHelper.createSimplePosLiteral("urn:a"));
		}

		out = transform("_\"urn:a\"[_\"urn:a\" impliesType _\"urn:b\"]");
		assertEquals(1, out.size());
		for (Rule r : out) {
			checkRule(r, LiteralTestHelper.createPosLiteral(
					WSML2DatalogTransformer.PRED_IMPLIES_TYPE, "urn:a",
					"urn:a", "urn:b"));
		}

		out = transform("_\"urn:a\" subConceptOf _\"urn:b\"");
		assertEquals(1, out.size());
		for (Rule r : out) {
			checkRule(r, LiteralTestHelper.createPosLiteral(
					WSML2DatalogTransformer.PRED_SUB_CONCEPT_OF, "urn:a",
					"urn:b"));
		}
	}

	public void testTransform02() throws IllegalArgumentException,
			ParserException {
		Set<Rule> out = transform("_\"urn:a\"[_\"urn:a\" hasValue _\"urn:b\"] and _\"urn:b\" memberOf _\"urn:a\"");
		assertEquals(2, out.size());
		checkContainsRule(out, LiteralTestHelper.createPosLiteral(
				WSML2DatalogTransformer.PRED_HAS_VALUE, "urn:a", "urn:a",
				"urn:b"));
		checkContainsRule(out, LiteralTestHelper.createPosLiteral(
				WSML2DatalogTransformer.PRED_MEMBER_OF, "urn:b", "urn:a"));

		out = transform("_\"urn:a\"[_\"urn:a\" ofType _\"urn:b\"]",
				"_\"urn:a\"[_\"urn:a\" hasValue _\"urn:c\" ]");

		assertEquals(2, out.size());
		checkContainsRule(out, LiteralTestHelper
				.createPosLiteral(WSML2DatalogTransformer.PRED_OF_TYPE,
						"urn:a", "urn:a", "urn:b"));
		checkContainsRule(out, LiteralTestHelper.createPosLiteral(
				WSML2DatalogTransformer.PRED_HAS_VALUE, "urn:a", "urn:a",
				"urn:c"));

		out = transform("_\"urn:a\"[_\"urn:a\" hasValue  _\"urn:b\"] :- _\"urn:c\" ");
		assertEquals(1, out.size());
		for (Rule r : out) {
			checkRule(r, LiteralTestHelper.createPosLiteral(
					WSML2DatalogTransformer.PRED_HAS_VALUE, "urn:a", "urn:a",
					"urn:b"), LiteralTestHelper.createSimplePosLiteral("urn:c"));
		}
	}

	public void testTransform03() throws IllegalArgumentException,
			ParserException {
		Set<Rule> out = transform("_\"urn:a\" subConceptOf _\"urn:b\" :- _\"urn:c\" ");
		assertEquals(1, out.size());
		for (Rule r : out) {
			checkRule(r, LiteralTestHelper.createPosLiteral(
					WSML2DatalogTransformer.PRED_SUB_CONCEPT_OF, "urn:a",
					"urn:b"), LiteralTestHelper.createSimplePosLiteral("urn:c"));
		}

		out = transform("_\"urn:a\"[_\"urn:a\" hasValue _\"urn:c\"] :- _\"urn:d\" ");
		assertEquals(1, out.size());
		for (Rule r : out) {
			checkRule(r, LiteralTestHelper.createPosLiteral(
					WSML2DatalogTransformer.PRED_HAS_VALUE, "urn:a", "urn:a",
					"urn:c"), LiteralTestHelper.createSimplePosLiteral("urn:d"));
		}

		out = transform("_\"urn:a\"[_\"urn:a\" hasValue _\"urn:c\"] and _\"urn:b\" subConceptOf _\"urn:a\" :- _\"urn:d\" ");
		assertEquals(2, out.size());
		checkContainsRule(out, LiteralTestHelper.createPosLiteral(
				WSML2DatalogTransformer.PRED_SUB_CONCEPT_OF, "urn:b", "urn:a"),
				LiteralTestHelper.createSimplePosLiteral("urn:d"));
		checkContainsRule(out, LiteralTestHelper.createPosLiteral(
				WSML2DatalogTransformer.PRED_HAS_VALUE, "urn:a", "urn:a",
				"urn:c"), LiteralTestHelper.createSimplePosLiteral("urn:d"));

	}

	public void testGenerateAuxilliaryRules() {
		Set<Rule> set = transformer.generateAuxilliaryRules();
		assertEquals(15, set.size());
	}

	public void testExtractConstantsUsedAsConcepts() throws ParserException {
		LogicalExpression le = LETestHelper
				.buildLE("_\"urn:a\"[_\"urn:a\" ofType _\"urn:b\"] implies _\"urn:d\"[_\"urn:d\" hasValue \"_urn:c\" ]");
		Set<Term> set = transformer.extractConstantsUsedAsConcepts(le);

		assertEquals(2, set.size());
		assertTrue(set.contains(LiteralTestHelper.createSimpleTerm("urn:a")));
		assertTrue(set.contains(LiteralTestHelper.createSimpleTerm("urn:b")));
	}

	public void testTransformSubConceptOfInHead() throws ParserException {
		LogicalExpression le = LETestHelper
				.buildLE("_\"urn:a\" subConceptOf _\"urn:b\" :- _\"urn:c\" ");
		Set<Rule> out = transformer.transform(le);
		for (Rule r : out) {
			checkRule(r, LiteralTestHelper.createPosLiteral(
					WSML2DatalogTransformer.PRED_SUB_CONCEPT_OF, "urn:a",
					"urn:b"), LiteralTestHelper.createSimplePosLiteral("urn:c"));
		}
	}

	public void testTransformSubConceptOfInBody() throws ParserException {
		LogicalExpression le = LETestHelper
				.buildLE(" _\"urn:c\" :- _\"urn:a\" subConceptOf _\"urn:b\"  ");
		Set<Rule> out = transformer.transform(le);
		assertEquals(3, out.size());
		checkContainsRule(out, LiteralTestHelper
				.createSimplePosLiteral("urn:c"), LiteralTestHelper
				.createPosLiteral(WSML2DatalogTransformer.PRED_SUB_CONCEPT_OF,
						"urn:a", "urn:b"));
		checkContainsRule(out, LiteralTestHelper.createPosLiteral(
				WSML2DatalogTransformer.PRED_KNOWN_CONCEPT, "urn:b"));
		checkContainsRule(out, LiteralTestHelper.createPosLiteral(
				WSML2DatalogTransformer.PRED_KNOWN_CONCEPT, "urn:a"));
	}

	public void testTransformBodyHead() throws ParserException {
		LogicalExpression le = LETestHelper.buildLE("_\"urn:a\" :- _\"urn:b\"");
		Set<Rule> out = transformer.transform(le);
		assertEquals(1, out.size());
		for (Rule r : out) {
			checkRule(r, LiteralTestHelper.createSimplePosLiteral("urn:a"),
					LiteralTestHelper.createSimplePosLiteral("urn:b"));
		}
	}

	public void testTransformImpliesInHead() throws ParserException {
		// More than one implication in the given WSML rule detected!
		// [ 2002256 ] 'implies' should be allowed in rule bodies.
		LogicalExpression le = LETestHelper
				.buildLE("_\"urn:a\" implies _\"urn:b\" :- _\"urn:c\"");

		Set<Rule> out = transformer.transform(le);
//		assertEquals(1, out.size());

		// B :- A and C
		printLE(le);
		printRules(out);

		assertEquals(1, out.size());
		this.checkContainsRule(out, LiteralTestHelper
				.createSimplePosLiteral("urn:b"), LiteralTestHelper
				.createSimplePosLiteral("urn:a"), LiteralTestHelper
				.createSimplePosLiteral("urn:c"));

	}

	public void testTransformImpliesInBody() throws ParserException {
		// More than one implication in the given WSML rule detected!
		// [ 2002256 ] 'implies' should be allowed in rule bodies.
		LogicalExpression le = LETestHelper
				.buildLE("_\"urn:a\"  :- _\"urn:c\" implies _\"urn:b\"");

		// result should be:
		// A :- not C
		// A :- C and B
		Set<Rule> out = transformer.transform(le);
		printLE(le);
		printRules(out);

		assertEquals(2, out.size());

		checkContainsRule(out, LiteralTestHelper
				.createSimplePosLiteral("urn:a"), LiteralTestHelper
				.createSimplePosLiteral("urn:c"), LiteralTestHelper
				.createPosLiteral("urn:b"));

		checkContainsRule(out, LiteralTestHelper
				.createSimplePosLiteral("urn:a"), LiteralTestHelper
				.createSimpleNegLiteral("urn:c"));

	}

	public void testTransformEquivalentInHead() throws ParserException {
		// More than one implication in the given WSML rule detected!
		// [ 2002256 ] 'implies' should be allowed in rule bodies.
		LogicalExpression le = LETestHelper
				.buildLE("_\"urn:a\" equivalent _\"urn:b\" :- _\"urn:c\"");

		Set<Rule> out = transformer.transform(le);

		// result should be:
		// A implies B :- C
		// A impliedBy B :- C

		// and this should again be:
		// B :- A and C
		// A :- B and C

		printLE(le);
		printRules(out);

		assertEquals(2, out.size());
		checkContainsRule(out, LiteralTestHelper
				.createSimplePosLiteral("urn:b"), LiteralTestHelper
				.createSimplePosLiteral("urn:a"), LiteralTestHelper
				.createSimplePosLiteral("urn:c"));

		checkContainsRule(out, LiteralTestHelper
				.createSimplePosLiteral("urn:a"), LiteralTestHelper
				.createSimplePosLiteral("urn:b"), LiteralTestHelper
				.createSimplePosLiteral("urn:c"));

	}

	public void testTransformEquivalentInBody() throws ParserException {
		// More than one implication in the given WSML rule detected!
		// [ 2002256 ] 'implies' should be allowed in rule bodies.
		LogicalExpression le = LETestHelper
				.buildLE("_\"urn:a\"  :- _\"urn:c\" equivalent _\"urn:b\"");

		Set<Rule> out = transformer.transform(le);

		// result should be:
		// A :- C implies B
		// A :- C impliedBy B

		// and then:
		// A :- not C
		// A :- C and B
		// A :- not B
		// A :- C and B

		printLE(le);
		printRules(out);
		
		assertEquals(4, out.size());
		checkContainsRule(out, LiteralTestHelper
				.createSimplePosLiteral("urn:a"), LiteralTestHelper
				.createSimpleNegLiteral("urn:c"));
		
		checkContainsRule(out, LiteralTestHelper
				.createSimplePosLiteral("urn:a"), LiteralTestHelper
				.createSimplePosLiteral("urn:c"), LiteralTestHelper
				.createSimplePosLiteral("urn:b"));
		
		checkContainsRule(out, LiteralTestHelper
				.createSimplePosLiteral("urn:a"), LiteralTestHelper
				.createSimpleNegLiteral("urn:b"));
	}


	public void testTransformImpliedByInHead() throws ParserException {
		// More than one implication in the given WSML rule detected!
		// [ 2002256 ] 'implies' should be allowed in rule bodies.
		LogicalExpression le = LETestHelper
				.buildLE("_\"urn:a\" impliedBy _\"urn:b\" :- _\"urn:c\"");

		Set<Rule> out = transformer.transform(le);

		// result should be:
		// A :- B and C
		assertEquals(1, out.size());
		printLE(le);
		printRules(out);

		checkContainsRule(out, LiteralTestHelper
				.createSimplePosLiteral("urn:a"), LiteralTestHelper
				.createSimplePosLiteral("urn:b"), LiteralTestHelper
				.createSimplePosLiteral("urn:c"));
	}

	public void testTransformImpliedByInBody() throws ParserException {
		// More than one implication in the given WSML rule detected!
		// [ 2002256 ] 'implies' should be allowed in rule bodies.
		LogicalExpression le = LETestHelper
				.buildLE("_\"urn:a\" :- _\"urn:c\" impliedBy _\"urn:b\"");

		Set<Rule> out = transformer.transform(le);

		// result should be:
		// A :- not B
		// A :- C and B
		assertEquals(2, out.size());
		printLE(le);
		printRules(out);
		checkContainsRule(out, LiteralTestHelper
				.createSimplePosLiteral("urn:a"), LiteralTestHelper
				.createSimpleNegLiteral("urn:b"));

		checkContainsRule(out, LiteralTestHelper
				.createSimplePosLiteral("urn:a"), LiteralTestHelper
				.createSimplePosLiteral("urn:c"), LiteralTestHelper
				.createSimplePosLiteral("urn:b"));
	}

	public void testTransformImpliesTypeInHead() throws ParserException {
		LogicalExpression le = LETestHelper
				.buildLE("_\"urn:a\"[_\"urn:a\" impliesType _\"urn:b\" ] :- _\"urn:c\"");
		Set<Rule> out = transformer.transform(le);

		assertEquals(1, out.size());
		for (Rule r : out) {
			checkRule(r, LiteralTestHelper.createPosLiteral(
					WSML2DatalogTransformer.PRED_IMPLIES_TYPE, "urn:a",
					"urn:a", "urn:b"), LiteralTestHelper
					.createSimplePosLiteral("urn:c"));
		}
	}

	public void testTransformImpliesTypeInBody() throws ParserException {
		LogicalExpression le = LETestHelper
				.buildLE("_\"urn:c\" :- _\"urn:a\"[_\"urn:a\" impliesType _\"urn:b\" ]");
		Set<Rule> out = transformer.transform(le);
		checkContainsRule(out, LiteralTestHelper
				.createSimplePosLiteral("urn:c"), LiteralTestHelper
				.createPosLiteral(WSML2DatalogTransformer.PRED_IMPLIES_TYPE,
						"urn:a", "urn:a", "urn:b"));
		checkContainsRule(out, LiteralTestHelper.createPosLiteral(
				WSML2DatalogTransformer.PRED_KNOWN_CONCEPT, "urn:b"));
		checkContainsRule(out, LiteralTestHelper.createPosLiteral(
				WSML2DatalogTransformer.PRED_KNOWN_CONCEPT, "urn:a"));

	}

	public void testTransformHasValueInHead() throws ParserException {
		LogicalExpression le = LETestHelper
				.buildLE("_\"urn:c\"[_\"urn:c\" hasValue  _\"urn:b\"] :- _\"urn:a\" ");
		Set<Rule> out = transformer.transform(le);
		assertEquals(1, out.size());
		checkContainsRule(out, LiteralTestHelper.createPosLiteral(
				WSML2DatalogTransformer.PRED_HAS_VALUE, "urn:c", "urn:c",
				"urn:b"), LiteralTestHelper.createSimplePosLiteral("urn:a"));

	}

	public void testTransformHasValueInBody() throws ParserException {
		LogicalExpression le = LETestHelper
				.buildLE("_\"urn:a\" :- _\"urn:c\"[_\"urn:c\" hasValue  _\"urn:b\"]");

		Set<Rule> out = transformer.transform(le);
		assertEquals(1, out.size());
		checkContainsRule(out, LiteralTestHelper
				.createSimplePosLiteral("urn:a"), LiteralTestHelper
				.createPosLiteral(WSML2DatalogTransformer.PRED_HAS_VALUE,
						"urn:c", "urn:c", "urn:b"));
	}

	public void testTransformOfTypeInHead() throws ParserException {
		LogicalExpression le = LETestHelper
				.buildLE("_\"urn:a\"[_\"urn:a\" ofType _\"urn:b\"] :- _\"urn:c\"");
		Set<Rule> out = transformer.transform(le);

		assertEquals(1, out.size());
		checkContainsRule(out, LiteralTestHelper
				.createPosLiteral(WSML2DatalogTransformer.PRED_OF_TYPE,
						"urn:a", "urn:a", "urn:b"), LiteralTestHelper
				.createSimplePosLiteral("urn:c"));
	}

	public void testTransformOfTypeInBody() throws ParserException {
		LogicalExpression le = LETestHelper
				.buildLE(" _\"urn:c\"  :- _\"urn:a\"[_\"urn:a\" ofType _\"urn:b\"]");

		Set<Rule> out = transformer.transform(le);
		assertEquals(3, out.size());
		checkContainsRule(out, LiteralTestHelper
				.createSimplePosLiteral("urn:c"), LiteralTestHelper
				.createPosLiteral(WSML2DatalogTransformer.PRED_OF_TYPE,
						"urn:a", "urn:a", "urn:b"));
		checkContainsRule(out, LiteralTestHelper.createPosLiteral(
				WSML2DatalogTransformer.PRED_KNOWN_CONCEPT, "urn:a"));
		checkContainsRule(out, LiteralTestHelper.createPosLiteral(
				WSML2DatalogTransformer.PRED_KNOWN_CONCEPT, "urn:b"));
	}

	public void testTransformMemberOfInHead() throws ParserException {
		LogicalExpression le = LETestHelper
				.buildLE("_\"urn:a\" memberOf _\"urn:b\" :- _\"urn:c\"");

		Set<Rule> out = transformer.transform(le);
		// 
		for (Rule r : out) {
			checkRule(r, LiteralTestHelper.createLiteral(true,
					WSML2DatalogTransformer.PRED_MEMBER_OF, "urn:a", "urn:b"),
					LiteralTestHelper.createSimplePosLiteral("urn:c"));
		}
	}

	public void testTransformMemberOfInBody() throws ParserException {

		LogicalExpression le = LETestHelper
				.buildLE("_\"urn:c\" :- _\"urn:a\" memberOf _\"urn:b\"");
		Set<Rule> out = transformer.transform(le);

		assertTrue(out.contains(LiteralTestHelper.createRule(LiteralTestHelper
				.createSimplePosLiteral("urn:c"), LiteralTestHelper
				.createLiteral(true, WSML2DatalogTransformer.PRED_MEMBER_OF,
						"urn:a", "urn:b"))));
		assertTrue(out.contains(LiteralTestHelper.createRule(LiteralTestHelper
				.createLiteral(true,
						WSML2DatalogTransformer.PRED_KNOWN_CONCEPT, "urn:b"))));
	}

	public void testImpliedByImplies() throws ParserException {
		LogicalExpression le1 = LETestHelper
				.buildLE("_\"urn:a\" implies _\"urn:b\" :- _\"urn:c\"");
		Set<Rule> out1 = transformer.transform(le1);

		LogicalExpression le2 = LETestHelper
				.buildLE("_\"urn:b\" impliedBy _\"urn:a\" :- _\"urn:c\"");
		Set<Rule> out2 = transformer.transform(le2);

		assertEquals(1, out1.size());
		assertEquals(1, out2.size());
		assertEquals(out1, out2);

	}

	public void testTransformAndInHead() throws ParserException {
		LogicalExpression le = LETestHelper
				.buildLE("_\"urn:a\" and _\"urn:b\" :- _\"urn:c\"");

		// should be:
		// a :- c
		// b :- c
		// see : http://www.wsmo.org/TR/d16/d16.1/v1.0/#wsml-flight
		// Rules of the form A1 and ... and An :- B are split into n different
		// rules:
		// * A1 :- B
		// * ...
		// * An :- B

		Set<Rule> out = transformer.transform(le);
		assertEquals(2, out.size());
		checkContainsRule(out, LiteralTestHelper
				.createSimplePosLiteral("urn:b"), LiteralTestHelper
				.createSimplePosLiteral("urn:c"));

		checkContainsRule(out, LiteralTestHelper
				.createSimplePosLiteral("urn:a"), LiteralTestHelper
				.createSimplePosLiteral("urn:c"));

	}

	public void testTransformAndInBody() throws ParserException {
		LogicalExpression le = LETestHelper
				.buildLE("_\"urn:c\" :- _\"urn:a\" and  _\"urn:b\"");
		Set<Rule> out = transformer.transform(le);
		this.printRules(out);
		assertEquals(1, out.size());
		for (Rule r : out) {
			checkRule(r, LiteralTestHelper.createSimplePosLiteral("urn:c"),
					LiteralTestHelper.createSimplePosLiteral("urn:a"),
					LiteralTestHelper.createSimplePosLiteral("urn:b"));
		}
	}

	public void testTransformNafAndInHead() throws ParserException {

		String le1 = ("_\"urn:c\" and _\"urn:b\" :- _\"urn:a\" ");
		String le2 = (" naf _\"urn:c\" :- _\"urn:a\"");

		try {
			Set<Rule> out = this.transform(le1, le2);
			printRules(out);
			fail();
		} catch (DatalogException ex) {
		}

	}

	public void testTransformNafAndInBody() throws ParserException {
		String le1 = ("_\"urn:a\" :-_\"urn:c\" and _\"urn:b\"");
		String le2 = ("_\"urn:a\" :- naf _\"urn:c\" ");

		Set<Rule> out = this.transform(le1, le2);
		printRules(out);

		assertEquals(2, out.size());
		checkContainsRule(out, LiteralTestHelper
				.createSimplePosLiteral("urn:a"), LiteralTestHelper
				.createSimpleNegLiteral("urn:c"));

		checkContainsRule(out, LiteralTestHelper
				.createSimplePosLiteral("urn:a"), LiteralTestHelper
				.createSimplePosLiteral("urn:c"), LiteralTestHelper
				.createPosLiteral("urn:b"));
	}
	
	public void testTransformDuplicateRules() throws IllegalArgumentException, ParserException {
		String le1 = ("_\"urn:a\" :-_\"urn:c\" and _\"urn:b\"");
		String le2 = ("_\"urn:a\" :- naf _\"urn:c\" ");
		String le3 = ("_\"urn:a\" :- naf _\"urn:b\" ");
		String le4 = ("_\"urn:a\" :-_\"urn:c\" and _\"urn:b\"");

		Set<Rule> out = this.transform(le1, le2, le3, le4);
		printRules(out);

		assertEquals(3, out.size());
		checkContainsRule(out, LiteralTestHelper
				.createSimplePosLiteral("urn:a"), LiteralTestHelper
				.createSimpleNegLiteral("urn:c"));

		checkContainsRule(out, LiteralTestHelper
				.createSimplePosLiteral("urn:a"), LiteralTestHelper
				.createSimpleNegLiteral("urn:b"));
		
		checkContainsRule(out, LiteralTestHelper
				.createSimplePosLiteral("urn:a"), LiteralTestHelper
				.createSimplePosLiteral("urn:c"), LiteralTestHelper
				.createPosLiteral("urn:b"));
	}
	
	public void testMoreImplications() throws IllegalArgumentException, ParserException {
		String le = ("_\"urn:a\" implies _\"urn:c\"  :-_\"urn:c\" impliedBy _\"urn:b\"");

		Set<Rule> out = this.transform(le);
		printRules(out);

		assertEquals(1, out.size());
		fail();
	}

	public void testTransformNafInHead() throws ParserException {
		LogicalExpression le = LETestHelper
				.buildLE("naf _\"urn:c\" :- _\"urn:a\"");
		try {
			Set<Rule> out = transformer.transform(le);
			System.out.println(out.toString());
			fail();
		} catch (DatalogException ex) {
		}
	}

	public void testTransformNafInBody() throws ParserException {
		LogicalExpression le = LETestHelper
				.buildLE("_\"urn:c\" :- naf _\"urn:a\" and naf _\"urn:b\"");
		Set<Rule> out = transformer.transform(le);
		for (Rule r : out) {
			checkRule(r, LiteralTestHelper.createSimplePosLiteral("urn:c"),
					LiteralTestHelper.createSimpleNegLiteral("urn:a"),
					LiteralTestHelper.createSimpleNegLiteral("urn:b"));
		}
	}

	private void checkContainsRule(Set<Rule> out, Literal head, Literal... body) {
		Rule r2 = LiteralTestHelper.createRule(head, body);
		assertTrue(out.contains(r2));
	}

	private void checkRule(Rule r, Literal head, Literal... body) {
		Rule r2 = LiteralTestHelper.createRule(head, body);
		assertEquals(r2, r);
	}

	private Set<Rule> transform(String... le) throws IllegalArgumentException,
			ParserException {
		HashSet<LogicalExpression> h = new HashSet<LogicalExpression>();
		for (String str : le) {
			h.add(LETestHelper.buildLE(str));
		}
		return transformer.transform(h);
	}

	private void printRules(Set<Rule> rules) {
		System.out.println("Result: ");
		for (Rule r : rules) {
			printRules(r);
		}
	}

	private void printRules(Rule... rules) {
		for (Rule r : rules) {
			System.out.println("Rule: " + r.toString() + "  (isConstraint: "
					+ r.isConstraint() + " )" + " (isFact: " + r.isFact()
					+ " )");
		}
	}

	private void printLE(LogicalExpression... les) {
		for (LogicalExpression le : les) {
			System.out.println("\nLogicalExpression: " + le.toString());
		}
	}

}
