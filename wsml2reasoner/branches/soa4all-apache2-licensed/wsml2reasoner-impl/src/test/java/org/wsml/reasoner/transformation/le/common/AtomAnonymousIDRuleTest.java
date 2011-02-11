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


public class AtomAnonymousIDRuleTest extends TestCase {

    private AtomAnonymousIDRule rule;
    
    public AtomAnonymousIDRuleTest() {
        super();
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        FactoryContainer factory = new WsmlFactoryContainer();
        this.rule = new AtomAnonymousIDRule(factory, new AnonymousIdTranslator(factory.getWsmoFactory()));
    }
   
    
    public void testIsApplicable() throws ParserException {
        assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\" subConceptOf _#")));
        assertFalse(rule.isApplicable(LETestHelper.buildLE("_# memberOf _\"urn:a\"")));
        assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"[_\"urn:b\" ofType _#]")));
        assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"[_\"urn:b\" impliesType _#]")));
        assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"[_\"urn:b\" hasValue _#]")));
        assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"[_# ofType _#]")));
        assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"[_# impliesType _#]")));
        assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"[_# hasValue _#]")));
        assertTrue(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"(_#)")));
        assertTrue(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"(_#, _#)")));
        assertTrue(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"(_#, _#, _#)")));
    }
    
    public void testApply() throws ParserException {
        LogicalExpression in = LETestHelper.buildLE("_\"urn:a\"(_#)");
        LogicalExpression result = rule.apply(in);
        assertTrue(!result.toString().contains("_#"));
        assertEquals(2, result.toString().split(AnonymousIdUtils.ANONYMOUS_PREFIX).length);
        assertTrue(result.toString().startsWith("_\"urn:a\"(_\"" + AnonymousIdUtils.ANONYMOUS_PREFIX));
        
        in = LETestHelper.buildLE("_\"urn:a\"(_#, _#, _#)");
        result = rule.apply(in);
        assertTrue(!result.toString().contains("_#"));
        assertEquals(4, result.toString().split(AnonymousIdUtils.ANONYMOUS_PREFIX).length);
        assertTrue(result.toString().startsWith("_\"urn:a\"(_\"" + AnonymousIdUtils.ANONYMOUS_PREFIX));
    }
}
