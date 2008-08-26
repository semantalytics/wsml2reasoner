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
	
	public void testNormalizeAxioms() throws ParserException {
		
		// 
		Axiom axiom1 = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "axiom00"));
		axiom1.addDefinition(LETestHelper.buildLE("_\"urn:a\""));
		
		Set<Axiom> axioms = new HashSet<Axiom>();
		axioms.add(axiom1);

		Set<Axiom> out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(),"_#");
			Set <LogicalExpression> les = ax.listDefinitions();
			for(LogicalExpression le : les) {
				assertEquals(le.toString(), ("_\"urn:a\". "));
				
			}
		}
		
		// doubleNegationrule
		axiom1 = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "axiom1"));
		axiom1.addDefinition(LETestHelper.buildLE("naf( naf _\"urn:a\") "));
		
		axioms = new HashSet<Axiom>();
		axioms.add(axiom1);

		out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(),"_#");
			Set <LogicalExpression> les = ax.listDefinitions();
			for(LogicalExpression le : les) {
				assertEquals(le.toString(), "_\"urn:a\". ");
			}
		}
		
		axiom1 = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "axiom2"));
		axiom1.addDefinition(LETestHelper.buildLE("naf naf( naf _\"urn:a\") "));
		
		axioms = new HashSet<Axiom>();
		axioms.add(axiom1);

		out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(),"_#");
			Set <LogicalExpression> les = ax.listDefinitions();
			for(LogicalExpression le : les) {
				assertEquals(le.toString(), "naf _\"urn:a\". ");
			}
		}
		
		axiom1 = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "axiom3"));
		axiom1.addDefinition(LETestHelper.buildLE("naf naf naf( naf _\"urn:a\") "));
		
		axioms = new HashSet<Axiom>();
		axioms.add(axiom1);

		out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(),"_#");
			Set <LogicalExpression> les = ax.listDefinitions();
			for(LogicalExpression le : les) {
				assertEquals(le.toString(), "_\"urn:a\". ");
			}
		}
		
		
		// negateConjunctionRule
		axiom1 = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "axiom4"));
		axiom1.addDefinition(LETestHelper.buildLE(" naf (_\"urn:a\" and _\"urn:b\")"));
		
		axioms = new HashSet<Axiom>();
		axioms.add(axiom1);

		out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(),"_#");
			Set <LogicalExpression> les = ax.listDefinitions();
			for(LogicalExpression le : les) {
				assertEquals(le.toString(), ("naf _\"urn:a\"\n  or\nnaf _\"urn:b\". "));
			}
		}
		
		// negateConjunctionRule, doubleNegationRule
		axiom1 = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "axiom5"));
		axiom1.addDefinition(LETestHelper.buildLE(" naf (_\"urn:a\" and (naf (naf _\"urn:b\")))"));
		
		axioms = new HashSet<Axiom>();
		axioms.add(axiom1);

		out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(),"_#");
			Set <LogicalExpression> les = ax.listDefinitions();
			for(LogicalExpression le : les) {
				assertEquals(le.toString(), ("naf _\"urn:a\"\n  or\nnaf _\"urn:b\". "));
			}
		}
		
		// NegateDisjunctionRule
		axiom1 = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "axiom6"));
		axiom1.addDefinition(LETestHelper.buildLE(" naf (_\"urn:a\" or  _\"urn:b\")"));
		
		axioms = new HashSet<Axiom>();
		axioms.add(axiom1);

		out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(),"_#");
			Set <LogicalExpression> les = ax.listDefinitions();
			for(LogicalExpression le : les) {
				assertEquals(le.toString(), ("naf _\"urn:a\"\n  and naf _\"urn:b\". "));
				
			}
		}
		
		// NegateDisjunctionRule, negateConjunctionRule, doubleNegationRule
		axiom1 = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "axiom7"));
		axiom1.addDefinition(LETestHelper.buildLE(" naf (_\"urn:a\" or (naf (naf (naf(_\"urn:b\" and _\"urn:c\")))))"));
		
		axioms = new HashSet<Axiom>();
		axioms.add(axiom1);

		out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(),"_#");
			Set <LogicalExpression> les = ax.listDefinitions();
			for(LogicalExpression le : les) {
				assertEquals(le.toString(), ("naf _\"urn:a\"\n  and _\"urn:b\"\n  and _\"urn:c\". "));	
			}
		}
		
		// EquivalenceReplacementRule
		axiom1 = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "axiom8"));
		axiom1.addDefinition(LETestHelper.buildLE(" _\"urn:a\" equivalent _\"urn:b\" "));
		
		axioms = new HashSet<Axiom>();
		axioms.add(axiom1);

		out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(),"_#");
			Set <LogicalExpression> les = ax.listDefinitions();
			for(LogicalExpression le : les) {
				assertTrue(le.toString().contains("(_\"urn:a\"\nimpliedBy\n_\"urn:b\")"));
				assertTrue(le.toString().contains("(_\"urn:b\"\nimpliedBy\n_\"urn:a\")"));
			}
		}
		
		// EquivalenceReplacementRule, doubleNegationrule
		axiom1 = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "axiom9"));
		axiom1.addDefinition(LETestHelper.buildLE(" (naf (naf(_\"urn:a\"))) equivalent _\"urn:b\" "));
		
		axioms = new HashSet<Axiom>();
		axioms.add(axiom1);

		out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(),"_#");
			Set <LogicalExpression> les = ax.listDefinitions();
			for(LogicalExpression le : les) {
				assertTrue(le.toString().contains("(_\"urn:a\"\nimpliedBy\n_\"urn:b\")"));
				assertTrue(le.toString().contains("(_\"urn:b\"\nimpliedBy\n_\"urn:a\")"));
			}
		}
		
		// RightImplicationReplacementRule
		axiom1 = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "axiom10"));
		axiom1.addDefinition(LETestHelper.buildLE(" _\"urn:a\" implies _\"urn:b\" "));
		
		axioms = new HashSet<Axiom>();
		axioms.add(axiom1);

		out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(),"_#");
			Set <LogicalExpression> les = ax.listDefinitions();
			for(LogicalExpression le : les) {
				assertEquals(le.toString(), ("_\"urn:b\"\nimpliedBy\n_\"urn:a\". "));
				
			}
		}
		
		// RightImplicationReplacementRule, doubleNegationRule 
		axiom1 = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "axiom11"));
		axiom1.addDefinition(LETestHelper.buildLE(" _\"urn:a\" implies (naf (naf(_\"urn:b\")))"));
		
		axioms = new HashSet<Axiom>();
		axioms.add(axiom1);

		out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(),"_#");
			Set <LogicalExpression> les = ax.listDefinitions();
			for(LogicalExpression le : les) {
				assertEquals(le.toString(), ("_\"urn:b\"\nimpliedBy\n_\"urn:a\". "));
				
			}
		}
		
		// RightImplicationReplacementRule, EquivalenceReplacementRule, doubleNegationRule 
		axiom1 = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "axiom12"));
		axiom1.addDefinition(LETestHelper.buildLE(" (_\"urn:a\" equivalent _\"urn:c\") implies (naf (naf(_\"urn:b\")))"));
		
		axioms = new HashSet<Axiom>();
		axioms.add(axiom1);

		out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(),"_#");
			Set <LogicalExpression> les = ax.listDefinitions();
			for(LogicalExpression le : les) {
				assertEquals(le.toString(), ("_\"urn:b\"\nimpliedBy\n(_\"urn:c\"\nimpliedBy\n_\"urn:a\")\n  and (_\"urn:a\"\nimpliedBy\n_\"urn:c\"). "));
				
			}
		}
		
		// moleculeDecompositionRule
		axiom1 = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "axiom13"));
		axiom1.addDefinition(LETestHelper.buildLE("_\"urn:a\"[_\"urn:c\" ofType _\"urn:d\"] subConceptOf _\"urn:b\" "));
		axioms = new HashSet<Axiom>();
		axioms.add(axiom1);

		out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(),"_#");
			Set <LogicalExpression> les = ax.listDefinitions();
			for(LogicalExpression le : les) {
				assertEquals(le, LETestHelper.buildLE("_\"urn:a\"[_\"urn:c\" ofType _\"urn:d\"] and _\"urn:a\" subConceptOf _\"urn:b\""));
			}
		}
	
		// MoleculeAnonymousIDRule
		axiom1 = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "axiom14"));
		axiom1.addDefinition(LETestHelper.buildLE("_\"urn:a\" subConceptOf _#"));
		axioms = new HashSet<Axiom>();
		axioms.add(axiom1);

		out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(),"_#");
			Set <LogicalExpression> les = ax.listDefinitions();
			for(LogicalExpression le : les) {
				assertTrue(le.toString().startsWith("_\"urn:a\" subConceptOf _\"" + AnonymousIdUtils.ANONYMOUS_PREFIX));
			}
		}
		
		// MoleculeAnonymousIDRule,  moleculeDecompositionRule
		axiom1 = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "axiom15"));
		axiom1.addDefinition(LETestHelper.buildLE("_\"urn:a\" [_\"urn:c\" hasValue _#] memberOf _#  "));
		axioms = new HashSet<Axiom>();
		axioms.add(axiom1);

		out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(),"_#");
			Set <LogicalExpression> les = ax.listDefinitions();
			for(LogicalExpression le : les) {
				assertTrue(le.toString().contains("_\"urn:a\"[_\"urn:c\" hasValue _\"" +AnonymousIdUtils.ANONYMOUS_PREFIX ));
				assertTrue(le.toString().contains("_\"urn:a\" memberOf _\"" + AnonymousIdUtils.ANONYMOUS_PREFIX));
			}
		}
		
		// AtomAnonymousIDRule
		axiom1 = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "axiom16"));
		axiom1.addDefinition(LETestHelper.buildLE("_\"urn:a\"(_#)"));
		axioms = new HashSet<Axiom>();
		axioms.add(axiom1);

		out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(),"_#");
			Set <LogicalExpression> les = ax.listDefinitions();
			for(LogicalExpression le : les) {
				assertTrue(le.toString().startsWith("_\"urn:a\"(_\"" + AnonymousIdUtils.ANONYMOUS_PREFIX));
			}
		}
		
		// AtomAnonymousIDRule, doubleNegationRule 
		axiom1 = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "axiom17"));
		axiom1.addDefinition(LETestHelper.buildLE("naf (naf(_\"urn:a\"(_#)))"));
		axioms = new HashSet<Axiom>();
		axioms.add(axiom1);

		out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(),"_#");
			Set <LogicalExpression> les = ax.listDefinitions();
			for(LogicalExpression le : les) {
				assertTrue(le.toString().startsWith("_\"urn:a\"(_\"" + AnonymousIdUtils.ANONYMOUS_PREFIX));
			}
		}
		
		// AtomAnonymousIDRule, doubleNegationRule, RightImplicationReplacementRule
		axiom1 = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "axiom18"));
		axiom1.addDefinition(LETestHelper.buildLE(" _\"urn:a\" implies naf (naf(_\"urn:b\"(_#)))"));
		axioms = new HashSet<Axiom>();
		axioms.add(axiom1);

		out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(),"_#");
			Set <LogicalExpression> les = ax.listDefinitions();
			for(LogicalExpression le : les) {
				assertTrue(le.toString().startsWith("_\"urn:b\"(_\"" + AnonymousIdUtils.ANONYMOUS_PREFIX));
				assertTrue(le.toString().contains("impliedBy\n_\"urn:a\"" ));
			}
		}
		
		// ConjunctionPushRule
		axiom1 = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "axiom19"));
		axiom1.addDefinition(LETestHelper.buildLE("(_\"urn:a\" or _\"urn:b\") and _\"urn:c\""));
		axioms = new HashSet<Axiom>();
		axioms.add(axiom1);

		out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(),"_#");
			Set <LogicalExpression> les = ax.listDefinitions();
			for(LogicalExpression le : les) {
				assertEquals(le, LETestHelper.buildLE("_\"urn:c\" and _\"urn:a\" or _\"urn:c\" and _\"urn:b\""));

			}
		}
		
			

	}

}
