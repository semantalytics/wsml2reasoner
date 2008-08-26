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

	protected void tearDown() throws Exception {
		super.tearDown();
		normalizer = null;
		leFactory = null;
		wsmoFactory = null;

	}

	public void testNormalizeAxiomsNegationPushRules()
			throws ParserException {

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

		// doubleNegationrule
		axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "axiom1"));
		axiom.addDefinition(LETestHelper.buildLE("naf( naf _\"urn:a\") "));

		axioms = new HashSet<Axiom>();
		axioms.add(axiom);

		out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(), "_#");
			Set<LogicalExpression> les = ax.listDefinitions();
			for (LogicalExpression le : les) {
				assertEquals(le.toString(), "_\"urn:a\". ");
			}
		}

		axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "axiom2"));
		axiom.addDefinition(LETestHelper.buildLE("naf naf( naf _\"urn:a\") "));

		axioms = new HashSet<Axiom>();
		axioms.add(axiom);

		out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(), "_#");
			Set<LogicalExpression> les = ax.listDefinitions();
			for (LogicalExpression le : les) {
				assertEquals(le.toString(), "naf _\"urn:a\". ");
			}
		}

		axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "axiom3"));
		axiom.addDefinition(LETestHelper
				.buildLE("naf naf naf( naf _\"urn:a\") "));

		axioms = new HashSet<Axiom>();
		axioms.add(axiom);

		out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(), "_#");
			Set<LogicalExpression> les = ax.listDefinitions();
			for (LogicalExpression le : les) {
				assertEquals(le.toString(), "_\"urn:a\". ");
			}
		}

		// negateConjunctionRule
		axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "axiom4"));
		axiom.addDefinition(LETestHelper
				.buildLE(" naf (_\"urn:a\" and _\"urn:b\")"));

		axioms = new HashSet<Axiom>();
		axioms.add(axiom);

		out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(), "_#");
			Set<LogicalExpression> les = ax.listDefinitions();
			for (LogicalExpression le : les) {
				assertEquals(le.toString(),
						("naf _\"urn:a\"\n  or\nnaf _\"urn:b\". "));
			}
		}

		// negateConjunctionRule, doubleNegationRule
		axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "axiom5"));
		axiom.addDefinition(LETestHelper
				.buildLE(" naf (_\"urn:a\" and (naf (naf _\"urn:b\")))"));

		axioms = new HashSet<Axiom>();
		axioms.add(axiom);

		out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(), "_#");
			Set<LogicalExpression> les = ax.listDefinitions();
			for (LogicalExpression le : les) {
				assertEquals(le.toString(),
						("naf _\"urn:a\"\n  or\nnaf _\"urn:b\". "));
			}
		}

		// NegateDisjunctionRule
		axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "axiom6"));
		axiom.addDefinition(LETestHelper
				.buildLE(" naf (_\"urn:a\" or  _\"urn:b\")"));

		axioms = new HashSet<Axiom>();
		axioms.add(axiom);

		out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(), "_#");
			Set<LogicalExpression> les = ax.listDefinitions();
			for (LogicalExpression le : les) {
				assertEquals(le.toString(),
						("naf _\"urn:a\"\n  and naf _\"urn:b\". "));

			}
		}

		// NegateDisjunctionRule, negateConjunctionRule, doubleNegationRule
		axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "axiom7"));
		axiom
				.addDefinition(LETestHelper
						.buildLE(" naf (_\"urn:a\" or (naf (naf (naf(_\"urn:b\" and _\"urn:c\")))))"));

		axioms = new HashSet<Axiom>();
		axioms.add(axiom);

		out = normalizer.normalizeAxioms(axioms);
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

	public void testNormalizeAxiomsImplicationReductionRules()
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

		// EquivalenceReplacementRule, doubleNegationrule
		axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "axiom9"));
		axiom.addDefinition(LETestHelper
				.buildLE(" (naf (naf(_\"urn:a\"))) equivalent _\"urn:b\" "));

		axioms = new HashSet<Axiom>();
		axioms.add(axiom);

		out = normalizer.normalizeAxioms(axioms);
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

		// RightImplicationReplacementRule
		axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "axiom10"));
		axiom.addDefinition(LETestHelper
				.buildLE(" _\"urn:a\" implies _\"urn:b\" "));

		axioms = new HashSet<Axiom>();
		axioms.add(axiom);

		out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(), "_#");
			Set<LogicalExpression> les = ax.listDefinitions();
			for (LogicalExpression le : les) {
				assertEquals(le.toString(),
						("_\"urn:b\"\nimpliedBy\n_\"urn:a\". "));

			}
		}

		// RightImplicationReplacementRule, doubleNegationRule
		axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "axiom11"));
		axiom.addDefinition(LETestHelper
				.buildLE(" _\"urn:a\" implies (naf (naf(_\"urn:b\")))"));

		axioms = new HashSet<Axiom>();
		axioms.add(axiom);

		out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(), "_#");
			Set<LogicalExpression> les = ax.listDefinitions();
			for (LogicalExpression le : les) {
				assertEquals(le.toString(),
						("_\"urn:b\"\nimpliedBy\n_\"urn:a\". "));

			}
		}

		// RightImplicationReplacementRule, EquivalenceReplacementRule,
		// doubleNegationRule
		axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "axiom12"));
		axiom
				.addDefinition(LETestHelper
						.buildLE(" (_\"urn:a\" equivalent _\"urn:c\") implies (naf (naf(_\"urn:b\")))"));

		axioms = new HashSet<Axiom>();
		axioms.add(axiom);

		out = normalizer.normalizeAxioms(axioms);
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
	
	public void testNormalizeAxiomsMoleculeDecompositionRules()
	throws ParserException {

		// moleculeDecompositionRule
		Axiom axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "axiom13"));
		axiom
				.addDefinition(LETestHelper
						.buildLE("_\"urn:a\"[_\"urn:c\" ofType _\"urn:d\"] subConceptOf _\"urn:b\" "));
		Set <Axiom> axioms = new HashSet<Axiom>();
		axioms.add(axiom);

		Set <Axiom> out = normalizer.normalizeAxioms(axioms);
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

		// MoleculeAnonymousIDRule
		axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "axiom14"));
		axiom
				.addDefinition(LETestHelper
						.buildLE("_\"urn:a\" subConceptOf _#"));
		axioms = new HashSet<Axiom>();
		axioms.add(axiom);

		out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(), "_#");
			Set<LogicalExpression> les = ax.listDefinitions();
			for (LogicalExpression le : les) {
				assertTrue(le.toString().startsWith(
						"_\"urn:a\" subConceptOf _\""
								+ AnonymousIdUtils.ANONYMOUS_PREFIX));
			}
		}

		// MoleculeAnonymousIDRule, moleculeDecompositionRule
		axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "axiom15"));
		axiom.addDefinition(LETestHelper
				.buildLE("_\"urn:a\" [_\"urn:c\" hasValue _#] memberOf _#  "));
		axioms = new HashSet<Axiom>();
		axioms.add(axiom);

		out = normalizer.normalizeAxioms(axioms);
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

		// AtomAnonymousIDRule
		axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "axiom16"));
		axiom.addDefinition(LETestHelper.buildLE("_\"urn:a\"(_#)"));
		axioms = new HashSet<Axiom>();
		axioms.add(axiom);

		out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(), "_#");
			Set<LogicalExpression> les = ax.listDefinitions();
			for (LogicalExpression le : les) {
				assertTrue(le.toString().startsWith(
						"_\"urn:a\"(_\"" + AnonymousIdUtils.ANONYMOUS_PREFIX));
			}
		}

		// AtomAnonymousIDRule, doubleNegationRule
		axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "axiom17"));
		axiom.addDefinition(LETestHelper.buildLE("naf (naf(_\"urn:a\"(_#)))"));
		axioms = new HashSet<Axiom>();
		axioms.add(axiom);

		out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(), "_#");
			Set<LogicalExpression> les = ax.listDefinitions();
			for (LogicalExpression le : les) {
				assertTrue(le.toString().startsWith(
						"_\"urn:a\"(_\"" + AnonymousIdUtils.ANONYMOUS_PREFIX));
			}
		}

		// AtomAnonymousIDRule, doubleNegationRule,
		// RightImplicationReplacementRule
		axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "axiom18"));
		axiom.addDefinition(LETestHelper
				.buildLE(" _\"urn:a\" implies naf (naf(_\"urn:b\"(_#)))"));
		axioms = new HashSet<Axiom>();
		axioms.add(axiom);

		out = normalizer.normalizeAxioms(axioms);
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

	public void testNormalizeAxiomsMoleculeDisjunctionPullRules()
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

		// ConjunctionPushRule, doubleNegationRule
		axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "axiom20"));
		axiom
				.addDefinition(LETestHelper
						.buildLE("((naf (naf (_\"urn:a\"))) or _\"urn:b\") and _\"urn:c\""));
		axioms = new HashSet<Axiom>();
		axioms.add(axiom);

		out = normalizer.normalizeAxioms(axioms);
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

		// ConjunctionPushRule, doubleNegationRule, AtomAnonymousIDRule,
		axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "axiom21"));
		axiom
				.addDefinition(LETestHelper
						.buildLE("((naf (naf (_\"urn:a\"))) or _\"urn:b\") and _\"urn:c\"(_#, _#, _#)"));
		axioms = new HashSet<Axiom>();
		axioms.add(axiom);

		out = normalizer.normalizeAxioms(axioms);
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

	public void testNormalizeAxiomsMoleculeRules() throws ParserException {

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
		
		
		// not used / applied in ConstructReductionNormalizer ???? 
		
//		// AnonymousIDTranslator
//		axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "axiom23"));
//		axiom.addDefinition(LETestHelper.buildLE("_\"urn:a\"(_#1, _#2, _#3)"));
//		axioms = new HashSet<Axiom>();
//		axioms.add(axiom);
//
//		out = normalizer.normalizeAxioms(axioms);
//		for (Axiom ax : out) {
//			assertEquals(ax.getIdentifier().toString(), "_#");
//			Set<LogicalExpression> les = ax.listDefinitions();
//			for (LogicalExpression le : les) {
//				System.out.println(le.toString());
//				assertTrue(!le.toString().contains("_#"));
//                assertEquals(2, le.toString().split(AnonymousIdUtils.ANONYMOUS_PREFIX).length);
//                assertTrue(le.toString().startsWith(AnonymousIdUtils.ANONYMOUS_PREFIX));
//				
//			}
//		}
		
		

	}

}
