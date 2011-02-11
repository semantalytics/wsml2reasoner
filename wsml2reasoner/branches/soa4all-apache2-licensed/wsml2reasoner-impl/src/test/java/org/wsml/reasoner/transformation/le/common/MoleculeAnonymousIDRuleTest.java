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
package org.wsml.reasoner.transformation.le.common;

import junit.framework.TestCase;

import org.omwg.logicalexpression.LogicalExpression;
import org.sti2.wsmo4j.factory.WsmlFactoryContainer;
import org.wsml.reasoner.transformation.AnonymousIdTranslator;
import org.wsml.reasoner.transformation.AnonymousIdUtils;
import org.wsml.reasoner.transformation.le.LETestHelper;
import org.wsmo.factory.FactoryContainer;
import org.wsmo.wsml.ParserException;


public class MoleculeAnonymousIDRuleTest extends TestCase {

	    private MoleculeAnonymousIDRule rule;
	    
	    public MoleculeAnonymousIDRuleTest() {
	        super();
	    }
	    
	    protected void setUp() throws Exception {
	        super.setUp();
	        FactoryContainer wsmoManager = new WsmlFactoryContainer();
	        this.rule = new MoleculeAnonymousIDRule(wsmoManager, new AnonymousIdTranslator(wsmoManager.getWsmoFactory()));
	    }
	    
	    
	    public void testIsApplicable() throws ParserException {
	        assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"(_#)")));
	        assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"(_#, _#)")));
	        assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"(_#, _#, _#)")));
	        assertTrue(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\" subConceptOf _#")));
	        assertTrue(rule.isApplicable(LETestHelper.buildLE("_# memberOf _\"urn:a\"")));
	        assertTrue(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"[_\"urn:b\" ofType _#]")));
	        assertTrue(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"[_\"urn:b\" impliesType _#]")));
	        assertTrue(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"[_\"urn:b\" hasValue _#]")));
	        assertTrue(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"[_# ofType _\"urn:b\"]")));
	        assertTrue(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"[_# impliesType _\"urn:b\"]")));
	        assertTrue(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"[_# hasValue _\"urn:b\"]")));
	        assertTrue(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"[_# ofType _#]")));
	        assertTrue(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"[_# impliesType _#]")));
	        assertTrue(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"[_# hasValue _#]")));
	    }
	    
	    public void testApply() throws ParserException {
	        LogicalExpression in = LETestHelper.buildLE("_\"urn:a\" subConceptOf _#");
	        LogicalExpression result = rule.apply(in);
	        assertTrue(!result.toString().contains("_#"));
	        assertEquals(2, result.toString().split(AnonymousIdUtils.ANONYMOUS_PREFIX).length);
	        assertTrue(result.toString().startsWith("_\"urn:a\" subConceptOf _\"" + AnonymousIdUtils.ANONYMOUS_PREFIX));
	        
	        in = LETestHelper.buildLE("_# memberOf _\"urn:a\"");
	        result = rule.apply(in);
	        assertTrue(!result.toString().contains("_#"));
	        assertEquals(2, result.toString().split(AnonymousIdUtils.ANONYMOUS_PREFIX).length);
	        assertTrue(result.toString().startsWith("_\"" + AnonymousIdUtils.ANONYMOUS_PREFIX));
	        assertTrue(result.toString().trim().endsWith(" memberOf _\"urn:a\"."));
	        
	        in = LETestHelper.buildLE("_\"urn:a\"[_\"urn:b\" ofType _#]");
	        result = rule.apply(in);
	        assertTrue(!result.toString().contains("_#"));
	        assertEquals(2, result.toString().split(AnonymousIdUtils.ANONYMOUS_PREFIX).length);
	        assertTrue(result.toString().startsWith("_\"urn:a\"[_\"urn:b\" ofType _\"" + AnonymousIdUtils.ANONYMOUS_PREFIX));
	        
	        in = LETestHelper.buildLE("_\"urn:a\"[_\"urn:b\" impliesType _#]");
	        result = rule.apply(in);
	        assertTrue(!result.toString().contains("_#"));
	        assertEquals(2, result.toString().split(AnonymousIdUtils.ANONYMOUS_PREFIX).length);
	        assertTrue(result.toString().startsWith("_\"urn:a\"[_\"urn:b\" impliesType _\"" + AnonymousIdUtils.ANONYMOUS_PREFIX));
	        
	        in = LETestHelper.buildLE("_\"urn:a\"[_\"urn:b\" hasValue _#]");
	        result = rule.apply(in);
	        assertTrue(!result.toString().contains("_#"));
	        assertEquals(2, result.toString().split(AnonymousIdUtils.ANONYMOUS_PREFIX).length);
	        assertTrue(result.toString().startsWith("_\"urn:a\"[_\"urn:b\" hasValue _\"" + AnonymousIdUtils.ANONYMOUS_PREFIX));
	        
	        in = LETestHelper.buildLE("_\"urn:a\"[_# ofType _\"urn:b\"]");
	        result = rule.apply(in);
	        assertTrue(!result.toString().contains("_#"));
	        assertEquals(2, result.toString().split(AnonymousIdUtils.ANONYMOUS_PREFIX).length);
	        assertTrue(result.toString().startsWith("_\"urn:a\"[_\"" + AnonymousIdUtils.ANONYMOUS_PREFIX));
	        assertTrue(result.toString().trim().endsWith(" ofType _\"urn:b\"]."));
	        
	        in = LETestHelper.buildLE("_\"urn:a\"[_# impliesType _\"urn:b\"]");
	        result = rule.apply(in);
	        assertTrue(!result.toString().contains("_#"));
	        assertEquals(2, result.toString().split(AnonymousIdUtils.ANONYMOUS_PREFIX).length);
	        assertTrue(result.toString().startsWith("_\"urn:a\"[_\"" + AnonymousIdUtils.ANONYMOUS_PREFIX));
	        assertTrue(result.toString().trim().endsWith(" impliesType _\"urn:b\"]."));
	        
	        in = LETestHelper.buildLE("_\"urn:a\"[_# hasValue _\"urn:b\"]");
	        result = rule.apply(in);
	        assertTrue(!result.toString().contains("_#"));
	        assertEquals(2, result.toString().split(AnonymousIdUtils.ANONYMOUS_PREFIX).length);
	        assertTrue(result.toString().startsWith("_\"urn:a\"[_\"" + AnonymousIdUtils.ANONYMOUS_PREFIX));
	        assertTrue(result.toString().trim().endsWith(" hasValue _\"urn:b\"]."));
	        
	        in = LETestHelper.buildLE("_\"urn:a\"[_# ofType _#]");
	        result = rule.apply(in);
	        assertTrue(!result.toString().contains("_#"));
	        assertEquals(3, result.toString().split(AnonymousIdUtils.ANONYMOUS_PREFIX).length);
	        assertTrue(result.toString().startsWith("_\"urn:a\"[_\"" + AnonymousIdUtils.ANONYMOUS_PREFIX));
	        
	        in = LETestHelper.buildLE("_\"urn:a\"[_# impliesType _#]");
	        result = rule.apply(in);
	        assertTrue(!result.toString().contains("_#"));
	        assertEquals(3, result.toString().split(AnonymousIdUtils.ANONYMOUS_PREFIX).length);
	        assertTrue(result.toString().startsWith("_\"urn:a\"[_\"" + AnonymousIdUtils.ANONYMOUS_PREFIX));
	        
	        in = LETestHelper.buildLE("_\"urn:a\"[_# hasValue _#]");
	        result = rule.apply(in);
	        assertTrue(!result.toString().contains("_#"));
	        assertEquals(3, result.toString().split(AnonymousIdUtils.ANONYMOUS_PREFIX).length);
	        assertTrue(result.toString().startsWith("_\"urn:a\"[_\"" + AnonymousIdUtils.ANONYMOUS_PREFIX));
	    }
}

