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

package org.wsml.reasoner.transformation;

import java.util.HashSet;
import java.util.Set;

import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.ontology.Axiom;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsml.reasoner.transformation.le.LETestHelper;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.ParserException;

import junit.framework.TestCase;

public class ConstructReductionNormalizerTest extends TestCase {

	private ConstructReductionNormalizer normalizer;
	protected String ns = "http://ex.org#";
	protected WsmoFactory wsmoFactory;
	protected LogicalExpressionFactory leFactory;

	public ConstructReductionNormalizerTest() {
		super();
	}

	protected void setUp() throws Exception {
		super.setUp();
		WSMO4JManager wsmoManager = new WSMO4JManager();
		normalizer = new ConstructReductionNormalizer(wsmoManager);
		wsmoFactory = wsmoManager.getWSMOFactory();
		leFactory = wsmoManager.getLogicalExpressionFactory();

	}

	public void testNormalizeAxiomsNegationPushRules01() throws ParserException {

		// 
		Axiom axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns
				+ "axiom00"));
		axiom.addDefinition(LETestHelper.buildLE("_\"urn:a\""));

		Set<Axiom> axioms = new HashSet<Axiom>();
		axioms.add(axiom);

		Set<Axiom> out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(), "_#");
			Set<LogicalExpression> les = ax.listDefinitions();
			for (LogicalExpression le : les) {
				assertEquals(le.toString(), ("_\"urn:a\". "));

			}
		}
	}

	public void testNormalizeAxiomsNegationPushRules02() throws ParserException {

		// doubleNegationrule
		Axiom axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns
				+ "axiom1"));
		axiom.addDefinition(LETestHelper.buildLE("naf( naf _\"urn:a\") "));

		Set<Axiom> axioms = new HashSet<Axiom>();
		axioms.add(axiom);

		Set<Axiom> out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(), "_#");
			Set<LogicalExpression> les = ax.listDefinitions();
			for (LogicalExpression le : les) {
				assertEquals(le.toString(), "_\"urn:a\". ");
			}
		}

	}

	public void testNormalizeAxiomsNegationPushRules03() throws ParserException {

		Axiom axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns
				+ "axiom2"));
		axiom.addDefinition(LETestHelper.buildLE("naf naf( naf _\"urn:a\") "));

		Set<Axiom> axioms = new HashSet<Axiom>();
		axioms.add(axiom);

		Set<Axiom> out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(), "_#");
			Set<LogicalExpression> les = ax.listDefinitions();
			for (LogicalExpression le : les) {
				assertEquals(le.toString(), "naf _\"urn:a\". ");
			}
		}
	}

	public void testNormalizeAxiomsNegationPushRules04() throws ParserException {

		Axiom axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns
				+ "axiom3"));
		axiom.addDefinition(LETestHelper
				.buildLE("naf naf naf( naf _\"urn:a\") "));

		Set<Axiom> axioms = new HashSet<Axiom>();
		axioms.add(axiom);

		Set<Axiom> out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(), "_#");
			Set<LogicalExpression> les = ax.listDefinitions();
			for (LogicalExpression le : les) {
				assertEquals(le.toString(), "_\"urn:a\". ");
			}
		}
	}

	public void testNormalizeAxiomsNegationConjunctionRule01()
			throws ParserException {
		// negateConjunctionRule
		Axiom axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns
				+ "axiom4"));
		axiom.addDefinition(LETestHelper
				.buildLE(" naf (_\"urn:a\" and _\"urn:b\")"));

		Set<Axiom> axioms = new HashSet<Axiom>();
		axioms.add(axiom);

		Set<Axiom> out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(), "_#");
			Set<LogicalExpression> les = ax.listDefinitions();
			for (LogicalExpression le : les) {
				assertEquals(le.toString(),
						("naf _\"urn:a\"\n  or\nnaf _\"urn:b\". "));
			}
		}
	}

	public void testNormalizeAxiomsNegateConjunctionRule02()
			throws ParserException {
		// negateConjunctionRule, doubleNegationRule
		Axiom axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns
				+ "axiom5"));
		axiom.addDefinition(LETestHelper
				.buildLE(" naf (_\"urn:a\" and (naf (naf _\"urn:b\")))"));

		Set<Axiom> axioms = new HashSet<Axiom>();
		axioms.add(axiom);

		Set<Axiom> out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(), "_#");
			Set<LogicalExpression> les = ax.listDefinitions();
			for (LogicalExpression le : les) {
				assertEquals(le.toString(),
						("naf _\"urn:a\"\n  or\nnaf _\"urn:b\". "));
			}
		}
	}

	public void testNormalizeAxiomsNegateDisjunctionRule01()
			throws ParserException {
		// NegateDisjunctionRule
		Axiom axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns
				+ "axiom6"));
		axiom.addDefinition(LETestHelper
				.buildLE(" naf (_\"urn:a\" or  _\"urn:b\")"));

		Set<Axiom> axioms = new HashSet<Axiom>();
		axioms.add(axiom);

		Set<Axiom> out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(), "_#");
			Set<LogicalExpression> les = ax.listDefinitions();
			for (LogicalExpression le : les) {
				assertEquals(le.toString(),
						("naf _\"urn:a\"\n  and naf _\"urn:b\". "));

			}
		}
	}

	public void testNormalizeAxiomsNegateDisjunctionRule02()
			throws ParserException {

		// NegateDisjunctionRule, negateConjunctionRule, doubleNegationRule
		Axiom axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns
				+ "axiom7"));
		axiom
				.addDefinition(LETestHelper
						.buildLE(" naf (_\"urn:a\" or (naf (naf (naf(_\"urn:b\" and _\"urn:c\")))))"));

		Set<Axiom> axioms = new HashSet<Axiom>();
		axioms.add(axiom);

		Set<Axiom> out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(), "_#");
			Set<LogicalExpression> les = ax.listDefinitions();
			for (LogicalExpression le : les) {
				assertEquals(
						le.toString(),
						("naf _\"urn:a\"\n  and _\"urn:b\"\n  and _\"urn:c\". "));
			}
		}

	}

	public void testNormalizeAxiomsImplicationReductionRules01()
			throws ParserException {

		// EquivalenceReplacementRule
		Axiom axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns
				+ "axiom8"));
		axiom.addDefinition(LETestHelper
				.buildLE(" _\"urn:a\" equivalent _\"urn:b\" "));

		Set<Axiom> axioms = new HashSet<Axiom>();
		axioms.add(axiom);

		Set<Axiom> out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(), "_#");
			Set<LogicalExpression> les = ax.listDefinitions();
			for (LogicalExpression le : les) {
				assertTrue(le.toString().contains(
						"(_\"urn:a\"\nimpliedBy\n_\"urn:b\")"));
				assertTrue(le.toString().contains(
						"(_\"urn:b\"\nimpliedBy\n_\"urn:a\")"));
			}
		}
	}

	public void testNormalizeAxiomsImplicationReductionRules02()
			throws ParserException {

		// EquivalenceReplacementRule, doubleNegationrule
		Axiom axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns
				+ "axiom9"));
		axiom.addDefinition(LETestHelper
				.buildLE(" (naf (naf(_\"urn:a\"))) equivalent _\"urn:b\" "));

		Set<Axiom> axioms = new HashSet<Axiom>();
		axioms.add(axiom);

		Set<Axiom> out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(), "_#");
			Set<LogicalExpression> les = ax.listDefinitions();
			for (LogicalExpression le : les) {
				assertTrue(le.toString().contains(
						"(_\"urn:a\"\nimpliedBy\n_\"urn:b\")"));
				assertTrue(le.toString().contains(
						"(_\"urn:b\"\nimpliedBy\n_\"urn:a\")"));
			}
		}
	}

	public void testNormalizeAxiomsImplicationReductionRules03()
			throws ParserException {

		// RightImplicationReplacementRule
		Axiom axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns
				+ "axiom10"));
		axiom.addDefinition(LETestHelper
				.buildLE(" _\"urn:a\" implies _\"urn:b\" "));

		Set<Axiom> axioms = new HashSet<Axiom>();
		axioms.add(axiom);

		Set<Axiom> out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(), "_#");
			Set<LogicalExpression> les = ax.listDefinitions();
			for (LogicalExpression le : les) {
				assertEquals(le.toString(),
						("_\"urn:b\"\nimpliedBy\n_\"urn:a\". "));

			}
		}
	}

	public void testNormalizeAxiomsImplicationReductionRules04()
			throws ParserException {

		// RightImplicationReplacementRule, doubleNegationRule
		Axiom axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns
				+ "axiom11"));
		axiom.addDefinition(LETestHelper
				.buildLE(" _\"urn:a\" implies (naf (naf(_\"urn:b\")))"));

		Set<Axiom> axioms = new HashSet<Axiom>();
		axioms.add(axiom);

		Set<Axiom> out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(), "_#");
			Set<LogicalExpression> les = ax.listDefinitions();
			for (LogicalExpression le : les) {
				assertEquals(le.toString(),
						("_\"urn:b\"\nimpliedBy\n_\"urn:a\". "));

			}
		}
	}

	public void testNormalizeAxiomsImplicationReductionRules05()
			throws ParserException {

		// RightImplicationReplacementRule, EquivalenceReplacementRule,
		// doubleNegationRule
		Axiom axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns
				+ "axiom12"));
		axiom
				.addDefinition(LETestHelper
						.buildLE(" (_\"urn:a\" equivalent _\"urn:c\") implies (naf (naf(_\"urn:b\")))"));

		Set<Axiom> axioms = new HashSet<Axiom>();
		axioms.add(axiom);

		Set<Axiom> out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(), "_#");
			Set<LogicalExpression> les = ax.listDefinitions();
			for (LogicalExpression le : les) {
				assertEquals(
						le.toString(),
						("_\"urn:b\"\nimpliedBy\n(_\"urn:c\"\nimpliedBy\n_\"urn:a\")\n  and (_\"urn:a\"\nimpliedBy\n_\"urn:c\"). "));

			}
		}
	}

	public void testNormalizeAxiomsMoleculeDecompositionRules01()
			throws ParserException {

		// moleculeDecompositionRule
		Axiom axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns
				+ "axiom13"));
		axiom
				.addDefinition(LETestHelper
						.buildLE("_\"urn:a\"[_\"urn:c\" ofType _\"urn:d\"] subConceptOf _\"urn:b\" "));
		Set<Axiom> axioms = new HashSet<Axiom>();
		axioms.add(axiom);

		Set<Axiom> out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(), "_#");
			Set<LogicalExpression> les = ax.listDefinitions();
			for (LogicalExpression le : les) {
				assertEquals(
						le,
						LETestHelper
								.buildLE("_\"urn:a\"[_\"urn:c\" ofType _\"urn:d\"] and _\"urn:a\" subConceptOf _\"urn:b\""));
			}
		}
	}

	public void testNormalizeAxiomsMoleculeDecompositionRules02()
			throws ParserException {
		// MoleculeAnonymousIDRule
		Axiom axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns
				+ "axiom14"));
		axiom.addDefinition(LETestHelper.buildLE("_\"urn:a\" subConceptOf _#"));
		Set<Axiom> axioms = new HashSet<Axiom>();
		axioms.add(axiom);

		Set<Axiom> out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(), "_#");
			Set<LogicalExpression> les = ax.listDefinitions();
			for (LogicalExpression le : les) {
				assertTrue(le.toString().startsWith(
						"_\"urn:a\" subConceptOf _\""
								+ AnonymousIdUtils.ANONYMOUS_PREFIX));
			}
		}
	}

	public void testNormalizeAxiomsMoleculeDecompositionRules03()
			throws ParserException {

		// MoleculeAnonymousIDRule, moleculeDecompositionRule
		Axiom axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns
				+ "axiom15"));
		axiom.addDefinition(LETestHelper
				.buildLE("_\"urn:a\" [_\"urn:c\" hasValue _#] memberOf _#  "));
		Set<Axiom> axioms = new HashSet<Axiom>();
		axioms.add(axiom);

		Set<Axiom> out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(), "_#");
			Set<LogicalExpression> les = ax.listDefinitions();
			for (LogicalExpression le : les) {
				assertTrue(le.toString().contains(
						"_\"urn:a\"[_\"urn:c\" hasValue _\""
								+ AnonymousIdUtils.ANONYMOUS_PREFIX));
				assertTrue(le.toString().contains(
						"_\"urn:a\" memberOf _\""
								+ AnonymousIdUtils.ANONYMOUS_PREFIX));
			}
		}
	}

	public void testNormalizeAxiomsMoleculeDecompositionRules04()
			throws ParserException {

		// AtomAnonymousIDRule
		Axiom axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns
				+ "axiom16"));
		axiom.addDefinition(LETestHelper.buildLE("_\"urn:a\"(_#)"));
		Set<Axiom> axioms = new HashSet<Axiom>();
		axioms.add(axiom);

		Set<Axiom> out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(), "_#");
			Set<LogicalExpression> les = ax.listDefinitions();
			for (LogicalExpression le : les) {
				assertTrue(le.toString().startsWith(
						"_\"urn:a\"(_\"" + AnonymousIdUtils.ANONYMOUS_PREFIX));
			}
		}
	}

	public void testNormalizeAxiomsMoleculeDecompositionRules05()
			throws ParserException {
		// AtomAnonymousIDRule, doubleNegationRule
		Axiom axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns
				+ "axiom17"));
		axiom.addDefinition(LETestHelper.buildLE("naf (naf(_\"urn:a\"(_#)))"));
		Set<Axiom> axioms = new HashSet<Axiom>();
		axioms.add(axiom);

		Set<Axiom> out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(), "_#");
			Set<LogicalExpression> les = ax.listDefinitions();
			for (LogicalExpression le : les) {
				assertTrue(le.toString().startsWith(
						"_\"urn:a\"(_\"" + AnonymousIdUtils.ANONYMOUS_PREFIX));
			}
		}
	}

	public void testNormalizeAxiomsMoleculeDecompositionRules06()
			throws ParserException {
		// AtomAnonymousIDRule, doubleNegationRule,
		// RightImplicationReplacementRule
		Axiom axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns
				+ "axiom18"));
		axiom.addDefinition(LETestHelper
				.buildLE(" _\"urn:a\" implies naf (naf(_\"urn:b\"(_#)))"));
		Set<Axiom> axioms = new HashSet<Axiom>();
		axioms.add(axiom);

		Set<Axiom> out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(), "_#");
			Set<LogicalExpression> les = ax.listDefinitions();
			for (LogicalExpression le : les) {
				assertTrue(le.toString().startsWith(
						"_\"urn:b\"(_\"" + AnonymousIdUtils.ANONYMOUS_PREFIX));
				assertTrue(le.toString().contains("impliedBy\n_\"urn:a\""));
			}
		}
	}

	public void testNormalizeAxiomsMoleculeDisjunctionPullRules01()
			throws ParserException {

		// ConjunctionPushRule
		Axiom axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns
				+ "axiom19"));
		axiom.addDefinition(LETestHelper
				.buildLE("(_\"urn:a\" or _\"urn:b\") and _\"urn:c\""));
		Set<Axiom> axioms = new HashSet<Axiom>();
		axioms.add(axiom);

		Set<Axiom> out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(), "_#");
			Set<LogicalExpression> les = ax.listDefinitions();
			for (LogicalExpression le : les) {
				assertEquals(
						le,
						LETestHelper
								.buildLE("_\"urn:c\" and _\"urn:a\" or _\"urn:c\" and _\"urn:b\""));

			}
		}
	}

	public void testNormalizeAxiomsMoleculeDisjunctionPullRules02()
			throws ParserException {

		// ConjunctionPushRule, doubleNegationRule
		Axiom axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns
				+ "axiom20"));
		axiom
				.addDefinition(LETestHelper
						.buildLE("((naf (naf (_\"urn:a\"))) or _\"urn:b\") and _\"urn:c\""));
		Set<Axiom> axioms = new HashSet<Axiom>();
		axioms.add(axiom);

		Set<Axiom> out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(), "_#");
			Set<LogicalExpression> les = ax.listDefinitions();
			for (LogicalExpression le : les) {
				assertEquals(
						le,
						LETestHelper
								.buildLE("_\"urn:c\" and _\"urn:a\" or _\"urn:c\" and _\"urn:b\""));

			}
		}
	}

	public void testNormalizeAxiomsMoleculeDisjunctionPullRules03()
			throws ParserException {

		// ConjunctionPushRule, doubleNegationRule, AtomAnonymousIDRule,
		Axiom axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns
				+ "axiom21"));
		axiom
				.addDefinition(LETestHelper
						.buildLE("((naf (naf (_\"urn:a\"))) or _\"urn:b\") and _\"urn:c\"(_#, _#, _#)"));
		Set<Axiom> axioms = new HashSet<Axiom>();
		axioms.add(axiom);

		Set<Axiom> out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(), "_#");
			Set<LogicalExpression> les = ax.listDefinitions();
			for (LogicalExpression le : les) {
				assertTrue(le.toString().contains(
						"_\"urn:c\"(_\"" + AnonymousIdUtils.ANONYMOUS_PREFIX));
				assertTrue(le.toString().contains(" and _\"urn:b\""));
			}
		}
	}

	public void testNormalizeAxiomsonePassReplacementNormalizer()
			throws ParserException {

		// onePassReplacementNormalizer
		Axiom axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns
				+ "axiom22"));
		axiom.addDefinition(LETestHelper
				.buildLE("_\"urn:a\"[_\"urn:a\" ofType _#]"));
		Set<Axiom> axioms = new HashSet<Axiom>();
		axioms.add(axiom);

		Set<Axiom> out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(), "_#");
			Set<LogicalExpression> les = ax.listDefinitions();
			for (LogicalExpression le : les) {
				assertTrue(le.toString().startsWith(
						"_\"urn:a\"[_\"urn:a\" ofType _\""
								+ AnonymousIdUtils.ANONYMOUS_PREFIX));

			}
		}
	}

	public void testNormalizeAxiomsAnonymousIDTranslator()
			throws ParserException {

		// AnonymousIDTranslator
		Axiom axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns
				+ "axiom23"));
		axiom.addDefinition(LETestHelper.buildLE("_\"urn:a\"(_#, _#, _#)"));
		Set<Axiom> axioms = new HashSet<Axiom>();
		axioms.add(axiom);

		Set<Axiom> out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(), "_#");
			Set<LogicalExpression> les = ax.listDefinitions();
			for (LogicalExpression le : les) {
				assertTrue(!le.toString().contains("_#"));
				assertEquals(4, le.toString().split(
						AnonymousIdUtils.ANONYMOUS_PREFIX).length);
				assertTrue(le.toString().startsWith("_\"urn:a\"(_\"" + AnonymousIdUtils.ANONYMOUS_PREFIX ));

			}
		}
	}
}
