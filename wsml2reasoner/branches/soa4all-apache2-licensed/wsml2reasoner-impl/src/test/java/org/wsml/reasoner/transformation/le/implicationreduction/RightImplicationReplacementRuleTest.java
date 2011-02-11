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
package org.wsml.reasoner.transformation.le.implicationreduction;

import junit.framework.TestCase;

import org.omwg.logicalexpression.LogicalExpression;
import org.sti2.wsmo4j.factory.WsmlFactoryContainer;
import org.wsml.reasoner.transformation.le.LETestHelper;
import org.wsmo.wsml.ParserException;


public class RightImplicationReplacementRuleTest extends TestCase {

    private RightImplicationReplacementRule rule;
    
    public RightImplicationReplacementRuleTest() {
        super();
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        this.rule = new RightImplicationReplacementRule(new WsmlFactoryContainer());
    }
    
    public void testIsApplicable() throws ParserException {
        assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"")));
        assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\" and _\"urn:b\"")));
        assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\" or  _\"urn:b\"")));
        assertFalse(rule.isApplicable(LETestHelper.buildLE("naf _\"urn:a\"")));
        assertFalse(rule.isApplicable(LETestHelper.buildLE("naf (_\"urn:a\" impliedBy _\"urn:b\")")));
        assertFalse(rule.isApplicable(LETestHelper.buildLE("naf (_\"urn:a\" implies _\"urn:a\")")));
        
        assertTrue(rule.isApplicable(LETestHelper.buildLE("(_\"urn:a\" implies  _\"urn:b\")")));
        assertTrue(rule.isApplicable(LETestHelper.buildLE("(naf _\"urn:a\" implies naf _\"urn:b\")")));
        assertTrue(rule.isApplicable(LETestHelper.buildLE("( _\"urn:a\" implies naf _\"urn:b\")")));
        assertTrue(rule.isApplicable(LETestHelper.buildLE("( (_\"urn:a\" and _\"urn:b\") implies ( _\"urn:c\" and _\"urn:d\") )")));
        assertTrue(rule.isApplicable(LETestHelper.buildLE("( (_\"urn:b\") implies (_\"urn:b\") )")));
    }
    
    public void testApply() throws ParserException {
        LogicalExpression in = LETestHelper.buildLE("_\"urn:a\" implies  _\"urn:b\"");
        LogicalExpression out = LETestHelper.buildLE(" _\"urn:b\" impliedBy _\"urn:a\" ");
        assertEquals(out, rule.apply(in));
        
        in = LETestHelper.buildLE("( (_\"urn:a\" and _\"urn:b\") implies ( _\"urn:c\" and _\"urn:d\") )");
        out = LETestHelper.buildLE("( ( _\"urn:c\" and _\"urn:d\" ) impliedBy ( _\"urn:a\" and _\"urn:b\" ))");
        assertEquals(out, rule.apply(in));
        
        in = LETestHelper.buildLE("( (_\"urn:b\") implies (_\"urn:b\") )");
        out = LETestHelper.buildLE("( (_\"urn:b\") impliedBy (_\"urn:b\") )");
        assertEquals(out, rule.apply(in));
    }
   
}
