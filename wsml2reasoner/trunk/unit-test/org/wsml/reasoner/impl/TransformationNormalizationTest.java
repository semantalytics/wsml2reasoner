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

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.ontology.Axiom;
import org.omwg.ontology.Ontology;
import org.wsml.reasoner.LiteralTestHelper;
import org.wsml.reasoner.Rule;
import org.wsml.reasoner.WSML2DatalogTransformer;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.api.inconsistency.InconsistencyException;
import org.wsml.reasoner.impl.DatalogBasedWSMLReasoner;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsml.reasoner.transformation.AxiomatizationNormalizer;
import org.wsml.reasoner.transformation.ConstraintReplacementNormalizer;
import org.wsml.reasoner.transformation.ConstructReductionNormalizer;
import org.wsml.reasoner.transformation.LloydToporNormalizer;
import org.wsml.reasoner.transformation.OntologyNormalizer;
import org.wsml.reasoner.transformation.le.LETestHelper;
import org.wsml.reasoner.transformation.le.OnePassReplacementNormalizer;
import org.wsmo.common.Entity;
import org.wsmo.common.exception.InvalidModelException;
import org.wsmo.common.exception.SynchronisationException;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.ParserException;

import junit.framework.TestCase;

public class TransformationNormalizationTest extends TestCase {

	protected OnePassReplacementNormalizer opr_normalizer;
	protected ConstraintReplacementNormalizer crepl_normalizer;
	protected WSML2DatalogTransformer transformer;
	protected ConstructReductionNormalizer cred_normalizer;
	protected LloydToporNormalizer lt_normalizer;
	protected AxiomatizationNormalizer ax_normalizer;

	protected DatalogBasedWSMLReasoner reasoner;
	protected WSMO4JManager wsmoManager;
	protected String ns = "http://ex.org#";
	protected WsmoFactory wsmoFactory;
	protected LogicalExpressionFactory leFactory;
	protected Ontology ontology;

	// AxiomatizationNormalizer
	// ConstructionReduction
	// OnePassReplacement
	// Lloyd Topor

	public TransformationNormalizationTest() {
		super();
	}

	protected void setUp() throws Exception {
		super.setUp();
		WSMO4JManager wsmoManager = new WSMO4JManager();
		wsmoFactory = wsmoManager.getWSMOFactory();

		Map<String, Object> params = new HashMap<String, Object>();
		params.put(WSMLReasonerFactory.PARAM_BUILT_IN_REASONER,
				WSMLReasonerFactory.BuiltInReasoner.IRIS_WELL_FOUNDED);

		reasoner = (DatalogBasedWSMLReasoner) DefaultWSMLReasonerFactory
				.getFactory().createFlightReasoner(params);

		transformer = new WSML2DatalogTransformer(wsmoManager);

		wsmoFactory = wsmoManager.getWSMOFactory();
		leFactory = wsmoManager.getLogicalExpressionFactory();

		ontology = wsmoFactory
				.createOntology(wsmoFactory.createIRI(ns + "ont"));
		ontology.setDefaultNamespace(wsmoFactory.createIRI(ns));

		reasoner.registerOntology(ontology);

	}

	public void testConvertEntitiesImpliesInHead() throws ParserException {

		Set<Entity> in = new HashSet<Entity>();
		Axiom axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns
				+ "axiom01"));
		axiom.addDefinition(LETestHelper
				.buildLE("_\"urn:a\" implies _\"urn:b\" :- _\"urn:c\""));
		in.add(axiom);

		// expected output:
		// urn:b() :- urn:a(), urn:c().
		Rule expected = LiteralTestHelper.createRule(LiteralTestHelper
				.createSimplePosLiteral("urn:b"), LiteralTestHelper
				.createSimplePosLiteral("urn:a"), LiteralTestHelper
				.createSimplePosLiteral("urn:c"));

		Set<Rule> out = reasoner.convertEntities(in);
		in.remove(axiom);
		printRules(out);
		outputContains(expected, out);

	}

	public void testConvertEntitiesImpliesInBody() throws ParserException {

		Set<Entity> in = new HashSet<Entity>();
		Axiom axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns
				+ "axiom01"));
		axiom.addDefinition(LETestHelper
				.buildLE("_\"urn:c\" :- _\"urn:a\" implies _\"urn:b\""));
		in.add(axiom);

		// expected output:
		// A :- not C
		// A :- C and B
		Rule expected01 = LiteralTestHelper.createRule(LiteralTestHelper
				.createSimplePosLiteral("urn:a"), LiteralTestHelper
				.createSimpleNegLiteral("urn:c"));

		Rule expected02 = LiteralTestHelper.createRule(LiteralTestHelper
				.createSimplePosLiteral("urn:a"), LiteralTestHelper
				.createSimplePosLiteral("urn:c"), LiteralTestHelper
				.createSimplePosLiteral("urn:b"));

		Set<Rule> expected = new HashSet<Rule>();
		expected.add(expected01);
		expected.add(expected02);

		Set<Rule> out = reasoner.convertEntities(in);
		printRules(out);
		outputContains(expected, out);
	}

	public void testConvertEntitiesEquivalentInHead() throws ParserException {

		Set<Entity> in = new HashSet<Entity>();
		Axiom axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns
				+ "axiom02"));
		axiom.addDefinition(LETestHelper
				.buildLE("_\"urn:a\" equivalent _\"urn:b\" :- _\"urn:c\""));
		in.add(axiom);

		// result should be:
		// A implies B :- C
		// A impliedBy B :- C

		// and this should again be:
		// B :- A and C
		// A :- B and C

		// expected output:

		Rule expected01 = LiteralTestHelper.createRule(LiteralTestHelper
				.createSimplePosLiteral("urn:b"), LiteralTestHelper
				.createSimplePosLiteral("urn:a"), LiteralTestHelper
				.createSimplePosLiteral("urn:c"));

		Rule expected02 = LiteralTestHelper.createRule(LiteralTestHelper
				.createSimplePosLiteral("urn:a"), LiteralTestHelper
				.createSimplePosLiteral("urn:b"), LiteralTestHelper
				.createSimplePosLiteral("urn:c"));

		Set<Rule> expected = new HashSet<Rule>();
		expected.add(expected01);
		expected.add(expected02);

		Set<Rule> out = reasoner.convertEntities(in);
		in.remove(axiom);

		printRules(out);
		outputContains(expected, out);

	}

	public void testConvertEntitiesEquivalentInBody() throws ParserException {

		Set<Entity> in = new HashSet<Entity>();
		Axiom axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns
				+ "axiom03"));
		axiom.addDefinition(LETestHelper
				.buildLE("_\"urn:a\"  :- _\"urn:c\" equivalent _\"urn:b\""));
		in.add(axiom);

		// result should be:
		// A :- C implies B
		// A :- C impliedBy B

		// and then:
		// A :- not C
		// A :- C and B
		// A :- not B
		// A :- C and B

		// expected output:

		Rule expected01 = LiteralTestHelper.createRule(LiteralTestHelper
				.createSimplePosLiteral("urn:a"), LiteralTestHelper
				.createSimpleNegLiteral("urn:c"));

		Rule expected02 = LiteralTestHelper.createRule(LiteralTestHelper
				.createSimplePosLiteral("urn:a"), LiteralTestHelper
				.createSimplePosLiteral("urn:c"), LiteralTestHelper
				.createSimplePosLiteral("urn:b"));

		Rule expected03 = LiteralTestHelper.createRule(LiteralTestHelper
				.createSimplePosLiteral("urn:a"), LiteralTestHelper
				.createSimpleNegLiteral("urn:b"));

		Set<Rule> expected = new HashSet<Rule>();
		expected.add(expected01);
		expected.add(expected02);
		expected.add(expected03);

		Set<Rule> out = reasoner.convertEntities(in);
		in.remove(axiom);

		printRules(out);
		outputContains(expected, out);

	}

	public void testConvertEntitiesImpliedByInHead() throws ParserException {

		Set<Entity> in = new HashSet<Entity>();
		Axiom axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns
				+ "axiom01"));
		axiom.addDefinition(LETestHelper
				.buildLE("_\"urn:a\" impliedBy _\"urn:b\" :- _\"urn:c\""));
		in.add(axiom);

		// expected output:
		// a :- b and c
		Rule expected = LiteralTestHelper.createRule(LiteralTestHelper
				.createSimplePosLiteral("urn:a"), LiteralTestHelper
				.createSimplePosLiteral("urn:b"), LiteralTestHelper
				.createSimplePosLiteral("urn:c"));

		Set<Rule> out = reasoner.convertEntities(in);
		in.remove(axiom);
		printRules(out);
		outputContains(expected, out);

	}

	public void testConvertEntitiesImpliedByBody() throws ParserException {

		Set<Entity> in = new HashSet<Entity>();
		Axiom axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns
				+ "axiom01"));
		axiom.addDefinition(LETestHelper
				.buildLE("_\"urn:a\" :- _\"urn:c\" impliedBy _\"urn:b\""));
		in.add(axiom);

		// expected output:
		// A :- not B
		// A :- B and C
		Rule expected01 = LiteralTestHelper.createRule(LiteralTestHelper
				.createSimplePosLiteral("urn:a"), LiteralTestHelper
				.createSimpleNegLiteral("urn:b"));

		Rule expected02 = LiteralTestHelper.createRule(LiteralTestHelper
				.createSimplePosLiteral("urn:a"), LiteralTestHelper
				.createSimplePosLiteral("urn:b"), LiteralTestHelper
				.createSimplePosLiteral("urn:c"));

		Set<Rule> expected = new HashSet<Rule>();
		expected.add(expected01);
		expected.add(expected02);

		Set<Rule> out = reasoner.convertEntities(in);
		printRules(out);
		outputContains(expected, out);
	}

	public void testTransformImpliesInBody() throws ParserException,
			SynchronisationException, InvalidModelException,
			InconsistencyException {

		Set<Rule> p = new HashSet<Rule>();
		Axiom axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns
				+ "axiom04"));
		axiom.addDefinition(LETestHelper
				.buildLE("_\"urn:c\" :- _\"urn:a\" implies _\"urn:b\""));
		ontology.addAxiom(axiom);

		Set<Entity> entities = new HashSet<Entity>();

		entities.addAll(ontology.listConcepts());
		entities.addAll(ontology.listInstances());
		entities.addAll(ontology.listRelations());
		entities.addAll(ontology.listRelationInstances());
		entities.addAll(ontology.listAxioms());

		// 1.
		// Convert conceptual syntax to logical expressions
		OntologyNormalizer normalizer = new AxiomatizationNormalizer(
				new WSMO4JManager());
		entities = normalizer.normalizeEntities(entities);

		Set<Axiom> axioms = new HashSet<Axiom>();
		for (Entity e : entities) {
			if (e instanceof Axiom) {
				axioms.add((Axiom) e);
			}
		}

		// printAxioms(axioms);

		// 2.
		// Convert constraints to support debugging
		normalizer = new ConstraintReplacementNormalizer(new WSMO4JManager());
		axioms = normalizer.normalizeAxioms(axioms);

		// printAxioms(axioms);

		// Simplify axioms
		// OnePassReplacementNormalizer should change C :- A implies B to C :- B
		// impliedBy A
		normalizer = new ConstructReductionNormalizer(new WSMO4JManager());
		axioms = normalizer.normalizeAxioms(axioms);
		axiomsContainLE(axioms, LETestHelper
				.buildLE("_\"urn:c\" :- (_\"urn:b\" impliedBy _\"urn:a\")"));
		printAxioms(axioms);

		// Apply Lloyd-Topor rules to get Datalog-compatible LEs
		normalizer = new LloydToporNormalizer(new WSMO4JManager());
		axioms = normalizer.normalizeAxioms(axioms);

		printAxioms(axioms);

		org.wsml.reasoner.WSML2DatalogTransformer wsml2datalog = new org.wsml.reasoner.WSML2DatalogTransformer(
				new WSMO4JManager());
		Set<org.omwg.logicalexpression.LogicalExpression> lExprs = new LinkedHashSet<org.omwg.logicalexpression.LogicalExpression>();
		for (Axiom a : axioms) {
			lExprs.addAll(a.listDefinitions());
		}

		p = wsml2datalog.transform(lExprs);
		printRules(p);

	}

	private void axiomsContainLE(Set<Axiom> axioms,
			LogicalExpression... buildLE) {
		int counter = 0;

		for (LogicalExpression e1 : buildLE) {
			for (Axiom a : axioms) {
				for (LogicalExpression e2 : a.listDefinitions()) {
					if (e2.equals(e1)) {
						counter++;
					}
				}
			}
		}

		assertEquals(1, counter);

	}

	private void outputContains(Set<Rule> expected, Set<Rule> output) {
		for (Rule r : expected) {
			assertTrue(output.contains(r));
		}
	}

	private void outputContains(Rule expected, Set<Rule> output) {
		Set<Rule> ex = new HashSet<Rule>();
		ex.add(expected);
		outputContains(ex, output);
	}

	private void printAxioms(Set<Axiom> axioms) {
		System.out.println("Axioms: ");
		for (Axiom a : axioms) {
			System.out.println(a.toString() + " : " + a.getIdentifier() + " :");
			for (LogicalExpression e : a.listDefinitions()) {
				System.out.println(e.toString());
			}
		}
	}

	// private void printRules(Rule ... rules){
	// for(Rule r : rules){
	// System.out.println(r.toString());
	// }
	// }

	private void printRules(Set<Rule> rules) {
		System.out.println(rules.size() + " Rules :");
		for (Rule r : rules) {
			System.out.println(r.toString());
		}
	}

}
