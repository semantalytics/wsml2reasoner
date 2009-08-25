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

import junit.framework.TestCase;

import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.ontology.Axiom;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsml.reasoner.transformation.le.LETestHelper;
import org.wsml.reasoner.transformation.le.foldecomposition.FOLMoleculeDecompositionRule;
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
		WSMO4JManager wsmoManager = new WSMO4JManager();
		normalizer = new MoleculeNormalizer(wsmoManager);

		wsmoFactory = wsmoManager.getWSMOFactory();
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
