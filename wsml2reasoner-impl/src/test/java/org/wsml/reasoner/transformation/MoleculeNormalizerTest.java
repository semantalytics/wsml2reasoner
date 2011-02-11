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
import org.wsml.reasoner.transformation.le.foldecomposition.FOLMoleculeDecompositionRule;
import org.wsmo.factory.FactoryContainer;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.ParserException;

public class MoleculeNormalizerTest extends TestCase {

	private MoleculeNormalizer normalizer;
	protected String ns = "http://ex.org#";
	protected WsmoFactory wsmoFactory;
	protected LogicalExpressionFactory leFactory;

	public MoleculeNormalizerTest() {
		super();
	}

	protected void setUp() throws Exception {
		super.setUp();
		FactoryContainer wsmoManager = new WsmlFactoryContainer();
		normalizer = new MoleculeNormalizer(wsmoManager);

		wsmoFactory = wsmoManager.getWsmoFactory();
		leFactory = wsmoManager.getLogicalExpressionFactory();

	}

	public void testNormalizeAxiomsMemberOf() throws ParserException {
		Axiom axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns
				+ "axiom1"));
		axiom.addDefinition(LETestHelper
				.buildLE("_\"urn:a\" memberOf _\"urn:b\""));

		Set<Axiom> axioms = new HashSet<Axiom>();
		axioms.add(axiom);

		Set<Axiom> out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(), "_#");
			Set<LogicalExpression> les = ax.listDefinitions();
			for (LogicalExpression le : les) {
				assertEquals(le.toString(), "_\""
						+ FOLMoleculeDecompositionRule.isa + "\""
						+ "(_\"urn:a\",_\"urn:b\"). ");
			}
		}
	}

	public void testNormalizeAxiomsSubConceptOf() throws ParserException {
		Axiom axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns
				+ "axiom2"));
		axiom.addDefinition(LETestHelper
				.buildLE("_\"urn:a\" subConceptOf _\"urn:b\""));

		Set<Axiom> axioms = new HashSet<Axiom>();
		axioms.add(axiom);

		Set<Axiom> out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(), "_#");
			Set<LogicalExpression> les = ax.listDefinitions();
			for (LogicalExpression le : les) {
				assertEquals(le.toString(), "_\""
						+ FOLMoleculeDecompositionRule.sub + "\""
						+ "(_\"urn:a\",_\"urn:b\"). ");
			}
		}
	}

	public void testNormalizeAxiomsImpliesType() throws ParserException {
		Axiom axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns
				+ "axiom3"));
		axiom.addDefinition(LETestHelper
				.buildLE("_\"urn:a\"[_\"urn:a\" impliesType _\"urn:b\"]"));

		Set<Axiom> axioms = new HashSet<Axiom>();
		axioms.add(axiom);

		Set<Axiom> out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(), "_#");
			Set<LogicalExpression> les = ax.listDefinitions();
			for (LogicalExpression le : les) {
				assertEquals(le.toString(), "_\""
						+ FOLMoleculeDecompositionRule.impliesType + "\""
						+ "(_\"urn:a\",_\"urn:a\",_\"urn:b\"). ");
			}
		}
	}

	public void testNormalizeAxiomsOfType() throws ParserException {
		Axiom axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns
				+ "axiom4"));
		axiom.addDefinition(LETestHelper
				.buildLE("_\"urn:a\"[_\"urn:a\" ofType _\"urn:b\"]"));

		Set<Axiom> axioms = new HashSet<Axiom>();
		axioms.add(axiom);

		Set<Axiom> out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(), "_#");
			Set<LogicalExpression> les = ax.listDefinitions();
			for (LogicalExpression le : les) {
				assertEquals(le.toString(), "_\""
						+ FOLMoleculeDecompositionRule.ofType + "\""
						+ "(_\"urn:a\",_\"urn:a\",_\"urn:b\"). ");
			}
		}
	}


	public void testNormalizeAxiomsMemberOfconstraint() throws ParserException {
		Axiom axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns
				+ "axiom6"));
		axiom.addDefinition(LETestHelper
				.buildLE("!- _\"urn:a\" memberOf _\"urn:b\""));

		Set<Axiom> axioms = new HashSet<Axiom>();
		axioms.add(axiom);

		Set<Axiom> out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(), "_#");
			Set<LogicalExpression> les = ax.listDefinitions();
			for (LogicalExpression le : les) {
				assertEquals(le.toString(), "!- _\""
						+ FOLMoleculeDecompositionRule.isa + "\""
						+ "(_\"urn:a\",_\"urn:b\"). ");
			}
		}
	}

	public void testNormalizeAxiomsMemberOfAnonymousID() throws ParserException {
		// anonymousIDTranslator
		Axiom axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns
				+ "axiom7"));
		axiom.addDefinition(LETestHelper
				.buildLE("!- _\"urn:a\" memberOf _\"urn:b\"(_#1, _#2, _#3)"));

		Set<Axiom> axioms = new HashSet<Axiom>();
		axioms.add(axiom);

		Set<Axiom> out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(), "_#");
			Set<LogicalExpression> les = ax.listDefinitions();
			for (LogicalExpression le : les) {
				assertEquals(le.toString(), "!- _\""
						+ FOLMoleculeDecompositionRule.isa + "\""
						+ "(_\"urn:a\",_\"urn:b\"(_#1,_#2,_#3)). ");
			}
		}
	}

}
