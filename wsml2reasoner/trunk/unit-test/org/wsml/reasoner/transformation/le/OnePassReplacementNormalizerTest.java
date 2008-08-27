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

import org.omwg.logicalexpression.LogicalExpression;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsml.reasoner.transformation.AnonymousIdUtils;
import org.wsml.reasoner.transformation.le.foldecomposition.FOLMoleculeDecompositionRules;
import org.wsml.reasoner.transformation.le.foldecomposition.FOLMoleculeDecompositionRule;
import org.wsmo.wsml.ParserException;

import junit.framework.TestCase;

public class OnePassReplacementNormalizerTest extends TestCase {
	
	private OnePassReplacementNormalizer normalizer;

	public OnePassReplacementNormalizerTest() {
		super();
	}

	protected void setUp() throws Exception {
		super.setUp();
		
	}
	
	public void testNormalize() throws ParserException {
		WSMO4JManager wsmoManager = new WSMO4JManager();
		List<NormalizationRule> postOrderRules = new ArrayList<NormalizationRule>();
        postOrderRules.addAll(new FOLMoleculeDecompositionRules(wsmoManager).getRules());
        normalizer = new OnePassReplacementNormalizer(postOrderRules, wsmoManager);
        
        LogicalExpression in = LETestHelper.buildLE("_\"urn:a\"[_\"urn:a\" impliesType _#]");
        LogicalExpression out = normalizer.normalize(in);
        assertTrue(out.toString().startsWith("_\""+FOLMoleculeDecompositionRule.impliesType + ""));
        assertTrue(out.toString().contains("_\"urn:a\",_\"urn:a\""));
        assertTrue(out.toString().contains(AnonymousIdUtils.ANONYMOUS_PREFIX));
        
       
        in = LETestHelper.buildLE("_\"urn:a\"[_\"urn:a\" hasValue _#]");
        out = normalizer.normalize(in); 
        assertTrue(out.toString().startsWith("_\""+FOLMoleculeDecompositionRule.hasValue + ""));
        assertTrue(out.toString().contains("_\"urn:a\",_\"urn:a\""));
        assertTrue(out.toString().contains(AnonymousIdUtils.ANONYMOUS_PREFIX));
        
        in = LETestHelper.buildLE("_\"urn:a\"[_\"urn:a\" ofType _#]");
        out = normalizer.normalize(in);
        assertTrue(out.toString().startsWith("_\""+FOLMoleculeDecompositionRule.ofType + ""));
        assertTrue(out.toString().contains("_\"urn:a\",_\"urn:a\""));
        assertTrue(out.toString().contains(AnonymousIdUtils.ANONYMOUS_PREFIX));
        
        in = LETestHelper.buildLE("_\"urn:a\" subConceptOf _#");
        out = normalizer.normalize(in);
        assertTrue(out.toString().startsWith("_\""+ FOLMoleculeDecompositionRule.sub + ""));
        assertTrue(out.toString().contains("_\"urn:a\""));
        assertTrue(out.toString().contains(AnonymousIdUtils.ANONYMOUS_PREFIX));
          
        
	}
	
	
	

}
