/*
 * Copyright (C) 2006 Digital Enterprise Research Insitute (DERI) Innsbruck
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.wsml.reasoner.transformation.le.negationpush;

import junit.framework.TestCase;

import org.omwg.logicalexpression.LogicalExpression;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsml.reasoner.transformation.le.LETestHelper;
import org.wsmo.wsml.ParserException;


public class TestNegateDisjunctionRule extends TestCase {

    private NegateDisjunctionRule rule;
    
    public TestNegateDisjunctionRule() {
        super();
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        this.rule = new NegateDisjunctionRule(new WSMO4JManager());
    }
    
    protected void tearDown() throws Exception {
        super.tearDown();
        this.rule = null;
    }
    
    public void testIsApplicable() throws ParserException {
        assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"")));
        assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\" and _\"urn:b\"")));
        assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\" or _\"urn:b\"")));
        assertFalse(rule.isApplicable(LETestHelper.buildLE("naf(_\"urn:a\" and _\"urn:b\")")));
        assertFalse(rule.isApplicable(LETestHelper.buildLE("naf(_\"urn:a\" and (_\"urn:b\" or _\"urn:c\"))")));
        assertFalse(rule.isApplicable(LETestHelper.buildLE("naf(_\"urn:a\" and naf _\"urn:b\")")));
        assertTrue(rule.isApplicable(LETestHelper.buildLE("naf(_\"urn:a\" or _\"urn:b\")")));
        assertTrue(rule.isApplicable(LETestHelper.buildLE("naf(_\"urn:a\" or (_\"urn:b\" or _\"urn:c\"))")));
        assertTrue(rule.isApplicable(LETestHelper.buildLE("naf(_\"urn:a\" or naf _\"urn:b\")")));
    }
    
    public void testApply() throws ParserException {
        LogicalExpression in = LETestHelper.buildLE("naf(_\"urn:a\" or _\"urn:b\")");
        LogicalExpression out = LETestHelper.buildLE("naf _\"urn:a\" and naf _\"urn:b\"");
        assertEquals(out, rule.apply(in));
        
        in = LETestHelper.buildLE("naf(_\"urn:a\" or (_\"urn:b\" or _\"urn:c\"))");
        out = LETestHelper.buildLE("naf _\"urn:a\" and naf (_\"urn:b\" or _\"urn:c\")");
        assertEquals(out, rule.apply(in));
        
        in = LETestHelper.buildLE("naf(_\"urn:a\" or naf _\"urn:b\")");
        out = LETestHelper.buildLE("naf _\"urn:a\" and naf (naf _\"urn:b\")");
        assertEquals(out, rule.apply(in));
    }
}