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
import org.wsmo.factory.WsmoFactory;

public class ConstructReductionNormalizerTest extends TestCase {

	private static final String A = "_\"urn:a\"";
	private static final String B = "_\"urn:b\"";
	private static final String C = "_\"urn:c\"";
	private static final String D = "_\"urn:d\"";

	private static final String ns = "http://ex.org#";
	private static final String PSEUDO_ANONYMOUS = "_\"h:AxA\"";

	private ConstructReductionNormalizer normalizer;
	private WsmoFactory wsmoFactory;

	protected void setUp() throws Exception {
		WSMO4JManager wsmoManager = new WSMO4JManager();
		normalizer = new ConstructReductionNormalizer(wsmoManager);
		wsmoFactory = wsmoManager.getWSMOFactory();
	}

	private String replaceAnonymousIds( String expression )
	{
		String regex = "_\"" + AnonymousIdUtils.ANONYMOUS_PREFIX + "[a-zA-Z0-9]*\"";
		return expression.replaceAll( regex, PSEUDO_ANONYMOUS );
	}

	/**
	 * Two logical expressions might appear the same (toString) and behave the same,
	 * but be structurally different, e.g. "A and (B and C)" compared with "(A and B) and C".
	 * This method attempts to find a behaviourally identical expression in the collection provided.
	 * @param referenceExpressions The collection of reference expressions.
	 * @param testExpression The Test expression
	 * @return true if the test expression (if behaviourally similar is found)
	 */
	private boolean contains( Set<LogicalExpression> referenceExpressions, LogicalExpression testExpression ) throws Exception
	{
		// Is it found as is?
		if( referenceExpressions.contains( testExpression ) )
			return true;
		
		for( LogicalExpression reference : referenceExpressions )
		{
			String refStr = reference.toString();
			String testStr = testExpression.toString();
			
			testStr = replaceAnonymousIds( testStr );
			
			LogicalExpression ref2 = LETestHelper.buildLE( refStr );
			LogicalExpression test2 = LETestHelper.buildLE( testStr );
			
			if( ref2.equals( test2 ) )
				return true;
			
			if( ref2.toString().equals( test2.toString() ))
				return true;
		}
		
		return false;
	}
	
	private void check( int axiomNumber, String originalExpression, String...normalisedExpressions ) throws Exception
	{
		// Make an axiom and give it the starting logical expression
		Axiom axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "axiom" + axiomNumber));
		axiom.addDefinition(LETestHelper.buildLE(originalExpression));
		Set<Axiom> axioms = new HashSet<Axiom>();
		axioms.add(axiom);
		
		// Parse the expected logical expressions
		Set<LogicalExpression> expectedExpressions = new HashSet<LogicalExpression>();
		for( String normalised : normalisedExpressions)
			expectedExpressions.add( LETestHelper.buildLE(normalised) );

		Set<Axiom> out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(), "_#");

			Set<LogicalExpression> les = ax.listDefinitions();
			for (LogicalExpression le : les) {
//				assertTrue(correctOutputExpressions.contains( le ) );
				assertTrue( contains( expectedExpressions, le ) );
			}
		}
	}

	public void testNormalizeAxiomsNegationPushRules01() throws Exception {
		check( 1, A, A );
	}

	public void testNormalizeAxiomsNegationPushRules02() throws Exception {
		// doubleNegationrule
		check( 2, "naf( naf " + A +")", A );
	}

	public void testNormalizeAxiomsNegationPushRules03() throws Exception {
		check( 3, "naf naf( naf " + A + ")", "naf " + A );
	}

	public void testNormalizeAxiomsNegationPushRules04() throws Exception {
		check( 4, "naf naf naf( naf " + A + ")", A );
	}

	public void testNormalizeAxiomsNegationConjunctionRule01() throws Exception {
		// negateConjunctionRule
		check( 5, "naf (" + A + " and " + B + ")", "naf " + A + " or naf " + B );
	}

	public void testNormalizeAxiomsNegateConjunctionRule02() throws Exception {
		// negateConjunctionRule, doubleNegationRule
		check( 6, "naf (" + A + " and (naf (naf " + B + ")))", "naf " + A + " or naf " + B );
	}

	public void testNormalizeAxiomsNegateDisjunctionRule01() throws Exception {
		// NegateDisjunctionRule
		check( 7, "naf (" + A + " or " + B + ")", "naf " + A + " and naf " + B );
	}

	public void testNormalizeAxiomsNegateDisjunctionRule02() throws Exception {
		// NegateDisjunctionRule, negateConjunctionRule, doubleNegationRule
		check( 8,	"naf (" + A + " or (naf (naf (naf(" + B + " and " + C + ")))))",
					"naf " + A + " and " + B + " and " + C );
	}

	public void testNormalizeAxiomsImplicationReductionRules01() throws Exception {

		// EquivalenceReplacementRule
		check( 9, A + " equivalent " + B,
						"(" + A + " impliedBy " + B + ") and (" + B + " impliedBy " + A + ")" );
	}

	public void testNormalizeAxiomsImplicationReductionRules02() throws Exception {
		// EquivalenceReplacementRule, doubleNegationrule
		check( 10, "(naf (naf(" + A + "))) equivalent " + B,
						"(" + A + " impliedBy " + B +") and (" + B + " impliedBy " + A + ")" );
	}

	public void testNormalizeAxiomsImplicationReductionRules03() throws Exception {
		// RightImplicationReplacementRule
		check( 11, A + " implies " + B, B + " impliedBy " + A);
	}

	public void testNormalizeAxiomsImplicationReductionRules04() throws Exception {
		// RightImplicationReplacementRule, doubleNegationRule
		check( 12, A + " implies (naf (naf(" + B + ")))", B + " impliedBy " + A);
	}

	public void testNormalizeAxiomsImplicationReductionRules05() throws Exception {
		// RightImplicationReplacementRule, EquivalenceReplacementRule,
		// doubleNegationRule
		check( 13, A + " equivalent " + C + " implies (naf (naf(" + B + ")))",
						B + " impliedBy (" + C + " impliedBy " + A + ") and (" + A + " impliedBy " + C + ")" );
	}

	public void testNormalizeAxiomsMoleculeDecompositionRules01() throws Exception {
		// moleculeDecompositionRule
		check( 14, A + "[" + C + " ofType " + D + "] subConceptOf " + B,
						A + "[" + C + " ofType " + D + "] and " + A + " subConceptOf " + B );
	}

	public void testNormalizeAxiomsMoleculeDecompositionRules02() throws Exception {
		// MoleculeAnonymousIDRule
		check( 15, A + " subConceptOf _#",
						A + " subConceptOf " + PSEUDO_ANONYMOUS);
	}
	
	public void testNormalizeAxiomsMoleculeDecompositionRules03() throws Exception {
		// MoleculeAnonymousIDRule, moleculeDecompositionRule
		check( 16, A + "[" + C + " hasValue _#] memberOf _# ",
						A + "[" + C + " hasValue " + PSEUDO_ANONYMOUS + "]" +
						" and " + A + " memberOf " + PSEUDO_ANONYMOUS );
	}

	public void testNormalizeAxiomsMoleculeDecompositionRules04() throws Exception {
		// AtomAnonymousIDRule
		check( 17, A + "(_#)", A + "(" + PSEUDO_ANONYMOUS + ")" );
	}

	public void testNormalizeAxiomsMoleculeDecompositionRules05() throws Exception {
		// AtomAnonymousIDRule, doubleNegationRule
		check( 18, "naf (naf(" + A + "(_#)))", A + "(" + PSEUDO_ANONYMOUS + ")");
	}

	public void testNormalizeAxiomsMoleculeDecompositionRules06() throws Exception {
		// AtomAnonymousIDRule, doubleNegationRule,
		// RightImplicationReplacementRule
		check( 19, " _\"urn:a\" implies naf (naf(_\"urn:b\"(_#)))",
						B + "(" + PSEUDO_ANONYMOUS + ") impliedBy " + A );
	}

	public void testNormalizeAxiomsMoleculeDisjunctionPullRules01() throws Exception {
		// ConjunctionPushRule
		check( 20, "(" + A + " or " + B + ") and " + C, C + " and " + A + " or " + C + " and " + B);
	}

	public void testNormalizeAxiomsMoleculeDisjunctionPullRules02() throws Exception {
		// ConjunctionPushRule, doubleNegationRule
		check( 21, "((naf (naf (" + A + "))) or " + B + ") and " + C,
						C + " and " + A + " or " + C + " and " + B);
	}

	public void testNormalizeAxiomsMoleculeDisjunctionPullRules03() throws Exception {
		// ConjunctionPushRule, doubleNegationRule, AtomAnonymousIDRule,
		check( 22, "((naf (naf (" + A + "))) or " + B + ") and " + C + "(_#, _#, _#)",
						C + "(" + PSEUDO_ANONYMOUS + "," + PSEUDO_ANONYMOUS + "," + PSEUDO_ANONYMOUS + ") and " + A + " or " +
						C + "(" + PSEUDO_ANONYMOUS + "," + PSEUDO_ANONYMOUS + "," + PSEUDO_ANONYMOUS + ") and " + B);
	}

	public void testNormalizeAxiomsonePassReplacementNormalizer() throws Exception {
		// onePassReplacementNormalizer
		check( 23, A + "[" + A + " ofType _#]", A + "[" + A + " ofType " + PSEUDO_ANONYMOUS + "]" );
	}

	public void testNormalizeAxiomsAnonymousIDTranslator() throws Exception {
		// AnonymousIDTranslator
		check( 24, "_\"urn:a\"(_#, _#, _#)",
						A + "(" + PSEUDO_ANONYMOUS + "," + PSEUDO_ANONYMOUS + "," + PSEUDO_ANONYMOUS + ")" );
	}
}
