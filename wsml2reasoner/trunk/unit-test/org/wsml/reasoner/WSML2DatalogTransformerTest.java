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
import java.util.LinkedList;
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

		le = LETestHelper.buildLE(" _\"urn:a\" implies _\"urn:b\"");
		out = transformer.transform(le);
		for (Rule r : out) {
			assertEquals(("urn:b() :- urn:a()."), r.toString());
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

	public void testTransform02() throws IllegalArgumentException,
			ParserException {
		Set<Rule> out = checkTransform("\"_urn:a\"[\"_urn:a\" hasValue \"_urn:b\"] and \"_urn:b\" memberOf \"_urn:a\"");
		for (Rule r : out) {
			assertTrue(r.toString().contains(
					("wsml-member-of(_urn:b, _urn:a).")));
		}

		out = checkTransform("_\"urn:a\"[_\"urn:a\" ofType _#]",
				"_\"urn:a\"[_\"urn:a\" hasValue \"_urn:c\" ]");
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
		assertTrue(str
				.contains("wsml-has-value(urn:a, urn:a, urn:b) :- urn:c()."));

	}

	public void testTransform03() throws IllegalArgumentException,
			ParserException {
		Set<Rule> out = checkTransform("_\"urn:a\" subConceptOf _\"urn:b\" :- _\"urn:c\" ");
		String str = "";
		for (Rule r : out) {
			str += r.toString();
		}
		assertTrue(str.contains("wsml-subconcept-of(urn:a, urn:b) :- urn:c()."));

		out = checkTransform("_\"urn:a\"[_\"urn:a\" hasValue _#] :- _\"urn:d\" ");
		str = "";
		for (Rule r : out) {
			str += r.toString();
		}
		assertTrue(str.contains("wsml-has-value(urn:a, urn:a, _#) :- urn:d()."));
	}

	public void testGenerateAuxilliaryRules() {
		Set<Rule> set = transformer.generateAuxilliaryRules();
		assertEquals(15, set.size());
	}

	public void testExtractConstantsUsedAsConcepts() throws ParserException {
		LogicalExpression le = LETestHelper
				.buildLE("_\"urn:a\"[_\"urn:a\" ofType _\"urn:b\"] implies _\"urn:d\"[_\"urn:d\" hasValue \"_urn:c\" ] ");
		Set<Term> set = transformer.extractConstantsUsedAsConcepts(le);
		assertEquals(2, set.size());
		String r = "";
		for (Term t : set) {
			r += " " + t.toString();
		}
		assertTrue(r.contains("urn:b"));
		assertTrue(r.contains("urn:a"));

	}

	public void testTransformSubConceptOfInHead() throws ParserException {
		LogicalExpression le = LETestHelper
				.buildLE("_\"urn:a\" subConceptOf \"urn:b\" :- _\"urn:c\" ");
		Set<Rule> out = transformer.transform(le);
		for (Rule r : out) {
			assertEquals("wsml-subconcept-of(urn:a, urn:b) :- urn:c().", r
					.toString());
		}
	}

	public void testTransformSubConceptOfInBody() throws ParserException {
		LogicalExpression le = LETestHelper
				.buildLE(" _\"urn:c\" :- _\"urn:a\" subConceptOf \"urn:b\"  ");
		Set<Rule> out = transformer.transform(le);
		int count = 0;
		for (Rule r : out) {
			if (r.toString().equals("http://temp/knownConcept(urn:b).")) {
				count++;
			} else if (r.toString().equals("http://temp/knownConcept(urn:a).")) {
				count++;
			} else if (r.toString().equals(
					"urn:c() :- wsml-subconcept-of(urn:a, urn:b).")) {
				count++;
			}

		}
		assertEquals(3, count);
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

		// B :- A and C
		for (Rule r : out) {
			System.out.println(" \n ___ "
					+ "_\"urn:a\" implies _\"urn:b\" :- _\"urn:c\"");
			System.out.println("Head: " + r.getHead().toString());
			System.out.println("Body: " + r.getBody().toString());

			assertEquals("urn:b()", r.getHead().toString());
			assertEquals("[urn:a(), urn:c()]", r.getBody().toString());
		}

	}

	public void testTransformImpliesInBody() throws ParserException {

		// More than one implication in the given WSML rule detected!
		// [ 2002256 ] 'implies' should be allowed in rule bodies.
		LogicalExpression le = LETestHelper
				.buildLE("_\"urn:a\"  :- _\"urn:c\" implies _\"urn:b\"");

		Set<Rule> out = transformer.transform(le);
		// 
		for (Rule r : out) {
			System.out.println(" \n ___ "
					+ "_\"urn:a\"  :- _\"urn:c\" implies _\"urn:b\"");
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

		// A implies B :- C
		// A impliedBy B :- C
		for (Rule r : out) {
			System.out.println(" \n ___ "
					+ "_\"urn:a\" equivalent _\"urn:b\" :- _\"urn:c\"");
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

		// 
		for (Rule r : out) {
			System.out.println(" \n ___ "
					+ "_\"urn:a\"  :- _\"urn:c\" equivalent _\"urn:b\"");
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

		// A :- B and C
		for (Rule r : out) {
			System.out.println(" \n ___ "
					+ "_\"urn:a\" impliedBy _\"urn:b\" :- _\"urn:c\"");
			System.out.println("Head: " + r.getHead().toString());
			System.out.println("Body: " + r.getBody().toString());
			assertEquals("urn:a()", r.getHead().toString());
			assertEquals("[urn:b(), urn:c()]", r.getBody().toString());
		}

	}

	public void testTransformImpliedByInBody() throws ParserException {

		// More than one implication in the given WSML rule detected!
		// [ 2002256 ] 'implies' should be allowed in rule bodies.
		LogicalExpression le = LETestHelper
				.buildLE("_\"urn:a\" :- _\"urn:c\" impliedBy _\"urn:b\"");

		Set<Rule> out = transformer.transform(le);
		// 
		for (Rule r : out) {
			System.out.println(" \n ___ "
					+ "_\"urn:a\" :- _\"urn:c\" impliedBy _\"urn:b\"");
			System.out.println("Head: " + r.getHead().toString());
			System.out.println("Body: " + r.getBody().toString());
		}
	}

	public void testTransformImpliesTypeInHead() throws ParserException {

		LogicalExpression le = LETestHelper
				.buildLE("_\"urn:a\"[_\"urn:a\" impliesType _\"urn:b\" ] :- _\"urn:c\"");
		Set<Rule> out = transformer.transform(le);
		for (Rule r : out) {
			assertEquals("wsml-implies-type(urn:a, urn:a, urn:b)", r.getHead()
					.toString());
			assertEquals("[urn:c()]", r.getBody().toString());
		}
	}

	public void testTransformImpliesTypeInBody() throws ParserException {

		LogicalExpression le = LETestHelper
				.buildLE("_\"urn:c\" :- _\"urn:a\"[_\"urn:a\" impliesType _\"urn:b\" ]");
		Set<Rule> out = transformer.transform(le);
		String str = "";
		for (Rule r : out) {
			str += r.toString();
		}
		assertTrue(str
				.contains("urn:c() :- wsml-implies-type(urn:a, urn:a, urn:b)."));
		assertTrue(str.contains("http://temp/knownConcept(urn:b)."));
		assertTrue(str.contains("http://temp/knownConcept(urn:a)."));
	}

	public void testTransformHasValueInHead() throws ParserException {

		LogicalExpression le = LETestHelper
				.buildLE("_\"urn:c\"[_\"urn:c\" hasValue  _\"urn:b\"] :- _\"urn:a\" ");

		Set<Rule> out = transformer.transform(le);
		// 
		for (Rule r : out) {
			assertEquals("wsml-has-value(urn:c, urn:c, urn:b)", r.getHead()
					.toString());
			assertEquals("[urn:a()]", r.getBody().toString());
		}
	}

	public void testTransformHasValueInBody() throws ParserException {

		LogicalExpression le = LETestHelper
				.buildLE("_\"urn:a\" :- _\"urn:c\"[_\"urn:c\" hasValue  _\"urn:b\"]");

		Set<Rule> out = transformer.transform(le);
		// 
		for (Rule r : out) {
			assertEquals("urn:a()", r.getHead().toString());
			assertEquals("[wsml-has-value(urn:c, urn:c, urn:b)]", r.getBody()
					.toString());
		}
	}

	public void testTransformOfTypeInHead() throws ParserException {

		LogicalExpression le = LETestHelper
				.buildLE("_\"urn:a\"[_\"urn:a\" ofType _\"urn:b\"] :- _\"urn:c\"");

		Set<Rule> out = transformer.transform(le);
		// 
		for (Rule r : out) {
			assertEquals("wsml-of-type(urn:a, urn:a, urn:b)", r.getHead()
					.toString());
			assertEquals("[urn:c()]", r.getBody().toString());
		}
	}

	public void testTransformOfTypeInBody() throws ParserException {

		LogicalExpression le = LETestHelper
				.buildLE(" _\"urn:c\"  :- _\"urn:a\"[_\"urn:a\" ofType _\"urn:b\"]");

		Set<Rule> out = transformer.transform(le);
		// 
		String str = "";
		for (Rule r : out) {
			str += r.toString();
		}
		assertTrue(str.contains("http://temp/knownConcept(urn:b)."));
		assertTrue(str.contains("http://temp/knownConcept(urn:a)."));
		assertTrue(str
				.contains("urn:c() :- wsml-of-type(urn:a, urn:a, urn:b)."));
	}

	public void testTransformMemberOfInHead() throws ParserException {

		LogicalExpression le = LETestHelper
				.buildLE("\"_urn:a\" memberOf \"_urn:b\" :- _\"urn:c\"");

		Set<Rule> out = transformer.transform(le);
		// 
		for (Rule r : out) {
			// checkRule(r, "wsml-member-of(_urn:a, _urn:b)", "[urn:c]");
			assertEquals("wsml-member-of(_urn:a, _urn:b)", r.getHead()
					.toString());
			assertEquals("[urn:c()]", r.getBody().toString());
		}
	}

	public void testTransformMemberOfInBody() throws ParserException {

		LogicalExpression le = LETestHelper
				.buildLE("_\"urn:c\" :- _\"urn:a\" memberOf _\"urn:b\"");
		Set<Rule> out = transformer.transform(le);

		assertTrue(out.contains(createRule(createSimpleLiteral("urn:c"),
				createLiteral(true, WSML2DatalogTransformer.PRED_MEMBER_OF,
						"urn:a", "urn:b"))));
		assertTrue(out.contains(createRule(createLiteral(true,
				WSML2DatalogTransformer.PRED_KNOWN_CONCEPT, "urn:b"))));

	}

	public void testTransformAndInHead() throws ParserException {

		LogicalExpression le = LETestHelper
				.buildLE("_\"urn:a\" and _\"urn:b\" :- _\"urn:c\"");
		try {
			Set<Rule> out = transformer.transform(le);
			System.out.println(out.toString());
			fail();
		} catch (DatalogException ex) {
		}
	}

	public void testTransformAndInBody() throws ParserException {
		LogicalExpression le = LETestHelper
				.buildLE("_\"urn:c\" :- _\"urn:a\" and  _\"urn:b\"");
		Set<Rule> out = transformer.transform(le);
		for (Rule r : out) {
			checkRule(r, createSimpleLiteral("urn:c"),
					createSimpleLiteral("urn:a"), createSimpleLiteral("urn:b"));
		}
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
			checkRule(r, createSimpleLiteral("urn:c"), createLiteral(false,
					"urn:a", new String[0]), createLiteral(false, "urn:b",
					new String[0]));
		}
	}

	private void checkRule(Rule r, Literal head, Literal... body) {
		Rule r2 = createRule(head, body);
		assertEquals(r2, r);
	}

	private Rule createRule(Literal head, Literal... body) {
		LinkedList<Literal> bodylist = new LinkedList<Literal>();
		for (Literal l : body) {
			bodylist.add(l);
		}
		return new Rule(head, bodylist);
	}

	private Literal createSimpleLiteral(String name) {
		return createLiteral(true, name, new String[0]);
	}

	private Literal createLiteral(boolean isPositive, String wsmlString,
			String... iriNames) {

		WsmoFactory wf = org.wsmo.factory.Factory.createWsmoFactory(null);

		Term[] terms;
		terms = new Term[iriNames.length];
		int i = 0;
		for (String str : iriNames) {
			terms[i] = wf.createIRI(str);
			;
			i++;
		}

		Literal h = new Literal(isPositive, wsmlString, terms);
		return h;
	}

	private Set<Rule> checkTransform(String... le)
			throws IllegalArgumentException, ParserException {
		HashSet<LogicalExpression> h = new HashSet<LogicalExpression>();
		for (String str : le) {
			h.add(LETestHelper.buildLE(str));
		}
		return transformer.transform(h);
	}

}
