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

package org.wsml.reasoner.transformation.le;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.omwg.logicalexpression.LogicalExpression;
import org.sti2.wsmo4j.factory.WsmlFactoryContainer;
import org.wsml.reasoner.transformation.AnonymousIdUtils;
import org.wsml.reasoner.transformation.le.disjunctionpull.DisjunctionPullRules;
import org.wsml.reasoner.transformation.le.foldecomposition.FOLMoleculeDecompositionRule;
import org.wsml.reasoner.transformation.le.foldecomposition.FOLMoleculeDecompositionRules;
import org.wsml.reasoner.transformation.le.implicationreduction.ImplicationReductionRules;
import org.wsml.reasoner.transformation.le.moleculedecomposition.MoleculeDecompositionRules;
import org.wsml.reasoner.transformation.le.negationpush.NegationPushRules;
import org.wsmo.factory.FactoryContainer;
import org.wsmo.wsml.ParserException;

public class OnePassReplacementNormalizerTest extends TestCase {

	private OnePassReplacementNormalizer normalizer;

	public OnePassReplacementNormalizerTest() {
		super();
	}

	protected void setUp() throws Exception {
		super.setUp();
		FactoryContainer wsmoManager = new WsmlFactoryContainer();
		List<NormalizationRule> preOrderRules = new ArrayList<NormalizationRule>();
		preOrderRules.addAll(new ImplicationReductionRules(wsmoManager)
				.getRules());
		preOrderRules.addAll(new NegationPushRules(wsmoManager).getRules());

		List<NormalizationRule> postOrderRules = new ArrayList<NormalizationRule>();
		postOrderRules.addAll(new MoleculeDecompositionRules(wsmoManager)
				.getRules());
		postOrderRules.addAll(new DisjunctionPullRules(wsmoManager).getRules());

		normalizer = new OnePassReplacementNormalizer(preOrderRules,
				postOrderRules, wsmoManager);

	}

	public void testNormalize() throws ParserException {
		FactoryContainer wsmoManager = new WsmlFactoryContainer();
		List<NormalizationRule> postOrderRules = new ArrayList<NormalizationRule>();
		postOrderRules.addAll(new FOLMoleculeDecompositionRules(wsmoManager)
				.getRules());
		normalizer = new OnePassReplacementNormalizer(postOrderRules,
				wsmoManager);

		LogicalExpression in = LETestHelper
				.buildLE("_\"urn:a\"[_\"urn:a\" impliesType _#]");
		LogicalExpression out = normalizer.normalize(in);
		assertTrue(out.toString().startsWith(
				"_\"" + FOLMoleculeDecompositionRule.impliesType + ""));
		assertTrue(out.toString().contains("_\"urn:a\",_\"urn:a\""));
		assertTrue(out.toString().contains(AnonymousIdUtils.ANONYMOUS_PREFIX));

		in = LETestHelper.buildLE("_\"urn:a\"[_\"urn:a\" hasValue _#]");
		out = normalizer.normalize(in);
		assertTrue(out.toString().startsWith(
				"_\"" + FOLMoleculeDecompositionRule.hasValue + ""));
		assertTrue(out.toString().contains("_\"urn:a\",_\"urn:a\""));
		assertTrue(out.toString().contains(AnonymousIdUtils.ANONYMOUS_PREFIX));

		in = LETestHelper.buildLE("_\"urn:a\"[_\"urn:a\" ofType _#]");
		out = normalizer.normalize(in);
		assertTrue(out.toString().startsWith(
				"_\"" + FOLMoleculeDecompositionRule.ofType + ""));
		assertTrue(out.toString().contains("_\"urn:a\",_\"urn:a\""));
		assertTrue(out.toString().contains(AnonymousIdUtils.ANONYMOUS_PREFIX));

		in = LETestHelper.buildLE("_\"urn:a\" subConceptOf _#");
		out = normalizer.normalize(in);
		assertTrue(out.toString().startsWith(
				"_\"" + FOLMoleculeDecompositionRule.sub + ""));
		assertTrue(out.toString().contains("_\"urn:a\""));
		assertTrue(out.toString().contains(AnonymousIdUtils.ANONYMOUS_PREFIX));
	}

	public void testNormalizeImpliesInHead() throws ParserException {
		LogicalExpression in = LETestHelper
				.buildLE("_\"urn:a\" implies _\"urn:b\" :- _\"urn:c\"");
		LogicalExpression expectedOut = LETestHelper
				.buildLE("(_\"urn:b\" impliedBy _\"urn:a\") :- _\"urn:c\"");

		LogicalExpression actualOut = normalizer.normalize(in);
		assertEquals(expectedOut, actualOut);
	}

	public void testNormalizeImpliesInBody() throws ParserException {
		LogicalExpression in = LETestHelper
				.buildLE("_\"urn:a\" :- _\"urn:b\" implies _\"urn:c\"");
		LogicalExpression expectedOut = LETestHelper
				.buildLE("_\"urn:a\"  :- (_\"urn:c\" impliedBy _\"urn:b\")");

		LogicalExpression actualOut = normalizer.normalize(in);
		assertEquals(expectedOut, actualOut);
	}

	public void testNormalizeImpliedByInHead() throws ParserException {
		LogicalExpression in = LETestHelper
				.buildLE("_\"urn:a\" impliedBy _\"urn:b\" :- _\"urn:c\"");
		LogicalExpression expectedOut = LETestHelper
				.buildLE("(_\"urn:a\" impliedBy _\"urn:b\") :- _\"urn:c\"");

		LogicalExpression actualOut = normalizer.normalize(in);
		assertEquals(expectedOut, actualOut);
	}

	public void testNormalizeImpliedByInBody() throws ParserException {
		LogicalExpression in = LETestHelper
				.buildLE("_\"urn:a\" :- _\"urn:b\" impliedBy _\"urn:c\"");
		LogicalExpression expectedOut = LETestHelper
				.buildLE("_\"urn:a\"  :- (_\"urn:b\" impliedBy _\"urn:c\")");

		LogicalExpression actualOut = normalizer.normalize(in);
		assertEquals(expectedOut, actualOut);
	}

	public void testNormalizeEquivalentInHead() throws ParserException {
		LogicalExpression in = LETestHelper
				.buildLE("_\"urn:a\" equivalent _\"urn:b\" :- _\"urn:c\"");
		LogicalExpression expectedOut = LETestHelper
				.buildLE("(_\"urn:b\" impliedBy _\"urn:a\") and (_\"urn:a\" impliedBy _\"urn:b\") :- _\"urn:c\"");

		LogicalExpression actualOut = normalizer.normalize(in);
		assertEquals(expectedOut, actualOut);
	}

	public void testNormalizeEquivalentInBody() throws ParserException {
		LogicalExpression in = LETestHelper
				.buildLE("_\"urn:a\" :- _\"urn:b\" equivalent _\"urn:c\"");
		LogicalExpression expectedOut = LETestHelper
				.buildLE("_\"urn:a\"  :- (_\"urn:c\" impliedBy _\"urn:b\") and (_\"urn:b\" impliedBy _\"urn:c\")");

		LogicalExpression actualOut = normalizer.normalize(in);
		assertEquals(expectedOut, actualOut);
	}

	public void testNormalizeImplies2TimesAndInHeadWithoutBrackets() throws ParserException {
		LogicalExpression in = LETestHelper
				.buildLE("_\"urn:a\" implies _\"urn:b\" and _\"urn:c\" implies _\"urn:d\" :- _\"urn:e\"");
		LogicalExpression expectedOut = LETestHelper
				.buildLE("(_\"urn:d\" impliedBy (_\"urn:b\" and _\"urn:c\" impliedBy _\"urn:a\")) :- _\"urn:e\"");
		LogicalExpression actualOut = normalizer.normalize(in);
		
		assertEquals(expectedOut, actualOut);
	}
	
	public void testNormalizeImplies2TimesAndInHeadWithBrackets() throws ParserException {
		LogicalExpression in = LETestHelper
				.buildLE("(_\"urn:a\" implies _\"urn:b\") and (_\"urn:c\" implies _\"urn:d\") :- _\"urn:e\"");
		LogicalExpression expectedOut = LETestHelper
				.buildLE("(_\"urn:b\" impliedBy _\"urn:a\") and (_\"urn:d\" impliedBy _\"urn:c\") :- _\"urn:e\"");
		LogicalExpression actualOut = normalizer.normalize(in);
			
		assertEquals(expectedOut, actualOut);
	}
	
	public void testNormalizeImplies2TimesAndInBodyWithoutBrackets() throws ParserException {
		LogicalExpression in = LETestHelper
				.buildLE("_\"urn:e\" :- _\"urn:a\" implies _\"urn:b\" and _\"urn:c\" implies _\"urn:d\"");
		LogicalExpression expectedOut = LETestHelper
				.buildLE("_\"urn:e\"  :- (_\"urn:d\" impliedBy (_\"urn:b\" and _\"urn:c\" impliedBy _\"urn:a\"))");
		LogicalExpression actualOut = normalizer.normalize(in);
		
		assertEquals(expectedOut, actualOut);
	}
	
	public void testNormalizeImplies2TimesAndInBodyWithBrackets() throws ParserException {
		LogicalExpression in = LETestHelper
				.buildLE("_\"urn:e\" :- (_\"urn:a\" implies _\"urn:b\") and (_\"urn:c\" implies _\"urn:d\")");
		LogicalExpression expectedOut = LETestHelper
				.buildLE("_\"urn:e\" :- (_\"urn:b\" impliedBy _\"urn:a\") and (_\"urn:d\" impliedBy _\"urn:c\")");
		LogicalExpression actualOut = normalizer.normalize(in);
			
		assertEquals(expectedOut, actualOut);
	}
	
	

}
