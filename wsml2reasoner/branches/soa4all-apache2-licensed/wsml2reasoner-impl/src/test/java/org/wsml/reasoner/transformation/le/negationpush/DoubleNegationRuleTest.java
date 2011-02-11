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
package org.wsml.reasoner.transformation.le.negationpush;

import junit.framework.TestCase;

import org.omwg.logicalexpression.LogicalExpression;
import org.wsml.reasoner.transformation.le.LETestHelper;
import org.wsmo.wsml.ParserException;


public class DoubleNegationRuleTest extends TestCase {

    private DoubleNegationRule rule;
    
    public DoubleNegationRuleTest() {
        super();
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        this.rule = new DoubleNegationRule();
    }
   
    
    public void testIsApplicable() throws ParserException {
        assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"")));
        assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\" and _\"urn:b\"")));
        assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\" or _\"urn:b\"")));
        assertFalse(rule.isApplicable(LETestHelper.buildLE("naf(_\"urn:a\")")));
        assertFalse(rule.isApplicable(LETestHelper.buildLE("naf _\"urn:a\"")));
        assertTrue(rule.isApplicable(LETestHelper.buildLE("naf(naf _\"urn:a\")")));
        assertTrue(rule.isApplicable(LETestHelper.buildLE("naf(naf(_\"urn:a\"))")));
        assertTrue(rule.isApplicable(LETestHelper.buildLE("naf(naf(naf _\"urn:a\"))")));
        assertTrue(rule.isApplicable(LETestHelper.buildLE("naf(naf(naf(_\"urn:a\")))")));
    }
    
    public void testApply() throws ParserException {
        LogicalExpression in = LETestHelper.buildLE("naf(naf(_\"urn:a\"))");
        LogicalExpression out = LETestHelper.buildLE("_\"urn:a\"");
        assertEquals(out, rule.apply(in));
        
        in = LETestHelper.buildLE("naf(naf(naf(_\"urn:a\")))");
        out = LETestHelper.buildLE("naf _\"urn:a\"");
        assertEquals(out, rule.apply(in));
        
        in = LETestHelper.buildLE("naf(naf(naf(naf(_\"urn:a\"))))");
        out = LETestHelper.buildLE("naf (naf _\"urn:a\")");
        assertEquals(out, rule.apply(in));
    }
}
