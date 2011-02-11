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

package org.wsml.reasoner.transformation;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.ontology.Axiom;
import org.sti2.wsmo4j.factory.WsmlFactoryContainer;
import org.wsml.reasoner.transformation.le.LETestHelper;
import org.wsmo.factory.FactoryContainer;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.ParserException;

public class LloydToporNormalizerTest extends TestCase {

	protected LloydToporNormalizer normalizer;

	protected String ns = "http://ex.org#";

	protected WsmoFactory wsmoFactory;

	protected LogicalExpressionFactory leFactory;

	public LloydToporNormalizerTest() {
		super();
	}

	protected void setUp() throws Exception {
		super.setUp();
		FactoryContainer wsmoManager = new WsmlFactoryContainer();
		normalizer = new LloydToporNormalizer(wsmoManager);

		wsmoFactory = wsmoManager.getWsmoFactory();
		leFactory = wsmoManager.getLogicalExpressionFactory();

	}

	public void testNormalizeAxiomsTopDownLESplitter() throws ParserException {
		// TopDownLESplitter:
		Axiom axiom1 = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns
				+ "axiom"));
		axiom1
				.addDefinition(LETestHelper
						.buildLE("_\"urn:a\" and _\"urn:b\" and _\"urn:c\" and _\"urn:f\" :- _\"urn:d\""));

		Set<Axiom> axioms = new HashSet<Axiom>();
		axioms.add(axiom1);

		Set<Axiom> out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(), "_#");
			Set<LogicalExpression> les = ax.listDefinitions();
			int all = 0;
			for (LogicalExpression le : les) {
				if (checkContains(le, LETestHelper
						.buildLE("_\"urn:f\" :- _\"urn:d\"."))) {
					all++;
				}
				if (checkContains(le, LETestHelper
						.buildLE("_\"urn:a\" :- _\"urn:d\"."))) {
					all++;
				}
				if (checkContains(le, LETestHelper
						.buildLE("_\"urn:b\" :- _\"urn:d\"."))) {
					all++;
				}
				if (checkContains(le, LETestHelper
						.buildLE("_\"urn:c\" :- _\"urn:d\"."))) {
					all++;
				}
			}
			assertEquals(4, all);

		}
	}

	public void testNormalizeAxiomsSplitDisjunctiveBody()
			throws ParserException {

		// splitDisjunctiveBody
		Axiom axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns
				+ "axiom2"));
		axiom
				.addDefinition(LETestHelper
						.buildLE("_\"urn:a\" :- _\"urn:b\" or (_\"urn:c\" and _\"urn:f\" and _\"urn:d\")"));

		Set<Axiom> axioms = new HashSet<Axiom>();
		axioms.add(axiom);

		Set<Axiom> out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(), "_#");
			Set<LogicalExpression> les = ax.listDefinitions();
			int all = 0;
			for (LogicalExpression le : les) {
				if (checkContains(le, LETestHelper
						.buildLE("_\"urn:a\" :- _\"urn:b\". "))) {
					all++;
				}
				if (checkContains(
						le,
						LETestHelper
								.buildLE("_\"urn:a\" :- _\"urn:c\"  and _\"urn:f\"  and _\"urn:d\". "))) {
					all++;
				}

			}
			assertEquals(2, all);
		}
	}

	public void testNormalizeAxiomsSplitComplexDisjunctiveBody()
			throws ParserException {

		Axiom axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns
				+ "axiom2"));
		axiom
				.addDefinition(LETestHelper
				// added to replicate bug no. 2615905 on sourceforge
						// this is bound to fail right now
						// remove when fixed
						.buildLE(" _\"urn:r\"  :- _\"urn:a\" and ( _\"urn:b\" or _\"urn:c\")  and ( _\"urn:d\" or _\"urn:e\" )"));

		Set<Axiom> axioms = new HashSet<Axiom>();
		axioms.add(axiom);

		Set<Axiom> out = normalizer.normalizeAxioms(axioms);
		// afterwards there should be no disjunctions left in the set of output
		// axioms, i.e. they should be "simple"

		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(), "_#");
			Set<LogicalExpression> les = ax.listDefinitions();
			int disjunctions = 0;

			for (LogicalExpression le : les) {
				if (le.toString().contains("or")) {
					disjunctions++;
				}
			}
			assertEquals(0, disjunctions);
		}
	}

	public void testNormalizeAxiomsSplitConstraint() throws ParserException {

		// SplitConstraint
		Axiom axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns
				+ "axiom3"));
		axiom.addDefinition(LETestHelper
				.buildLE("!- _\"urn:a\" or _\"urn:b\" and _\"urn:c\" "));

		Set<Axiom> axioms = new HashSet<Axiom>();
		axioms.add(axiom);

		Set<Axiom> out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(), "_#");
			Set<LogicalExpression> les = ax.listDefinitions();
			int all = 0;
			for (LogicalExpression le : les) {
				if (checkContains(le, LETestHelper.buildLE("!- _\"urn:a\". "))) {
					all++;
				}
				if (checkContains(le, LETestHelper
						.buildLE("!- _\"urn:a\"\n  and _\"urn:c\". "))) {

					all++;
				}

			}
			assertEquals(1, all);
		}
	}

	public void testNormalizeAxiomsSplitConjunctiveHead()
			throws ParserException {

		// SplitConjunctiveHead
		Axiom axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns
				+ "axiom4"));
		axiom
				.addDefinition(LETestHelper
						.buildLE("_\"urn:a\" and _\"urn:b\" and _\"urn:c\" :- _\"urn:d\""));

		Set<Axiom> axioms = new HashSet<Axiom>();
		axioms.add(axiom);

		Set<Axiom> out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(), "_#");
			Set<LogicalExpression> les = ax.listDefinitions();
			int all = 0;
			for (LogicalExpression le : les) {
				if (checkContains(le, LETestHelper
						.buildLE("_\"urn:a\" :- _\"urn:d\". "))) {
					all++;
				}
				if (checkContains(le, LETestHelper
						.buildLE("_\"urn:b\" :- _\"urn:d\". "))) {
					all++;
				}
				if (checkContains(le, LETestHelper
						.buildLE("_\"urn:c\" :- _\"urn:d\". "))) {
					all++;
				}

			}
			assertEquals(3, all);
		}
	}

	public void testNormalizeAxiomsTransformNestedImplication()
			throws ParserException {

		// TransformNestedImplication
		Axiom axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns
				+ "axiom5"));
		axiom.addDefinition(LETestHelper
				.buildLE("_\"urn:a\" impliedBy _\"urn:b\" :- _\"urn:c\""));

		Set<Axiom> axioms = new HashSet<Axiom>();
		axioms.add(axiom);

		Set<Axiom> out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(), "_#");
			Set<LogicalExpression> les = ax.listDefinitions();
			int all = 0;
			for (LogicalExpression le : les) {
				if (checkContains(le, LETestHelper
						.buildLE("_\"urn:a\" :- _\"urn:b\"  and _\"urn:c\". "))) {
					all++;
				}
			}
			assertEquals(1, all);
		}
	}

	public void testNormalizeAxiomsSplitConjunction() throws ParserException {
		// SplitConjunction
		Axiom axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns
				+ "axiom6"));
		axiom.addDefinition(LETestHelper
				.buildLE("_\"urn:a\" and _\"urn:b\" and _\"urn:c\""));

		Set<Axiom> axioms = new HashSet<Axiom>();
		axioms.add(axiom);

		Set<Axiom> out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(), "_#");
			Set<LogicalExpression> les = ax.listDefinitions();
			int all = 0;
			for (LogicalExpression le : les) {
				if (checkContains(le, LETestHelper.buildLE("_\"urn:a\". "))) {
					all++;
				}
				if (checkContains(le, LETestHelper.buildLE("_\"urn:b\". "))) {
					all++;
				}
				if (checkContains(le, LETestHelper.buildLE("_\"urn:c\". "))) {
					all++;
				}
			}
			assertEquals(3, all);
		}
	}

	public void testNormalizeAxiomsTransformImplication()
			throws ParserException {

		// TransformImplication
		Axiom axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns
				+ "axiom7"));
		axiom.addDefinition(LETestHelper
				.buildLE("_\"urn:a\" impliedBy _\"urn:b\""));

		Set<Axiom> axioms = new HashSet<Axiom>();
		axioms.add(axiom);

		Set<Axiom> out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(), "_#");
			Set<LogicalExpression> les = ax.listDefinitions();
			int all = 0;
			for (LogicalExpression le : les) {
				if (checkContains(le, LETestHelper
						.buildLE("_\"urn:a\" :- _\"urn:b\". "))) {
					all++;
				}

			}
			assertEquals(1, all);
		}

	}

	private boolean checkContains(LogicalExpression expected,
			LogicalExpression actual) {
		if (expected.toString().equals(actual.toString()))
			return true;

		return false;
	}

}
