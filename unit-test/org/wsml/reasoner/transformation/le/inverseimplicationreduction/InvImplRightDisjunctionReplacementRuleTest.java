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
package org.wsml.reasoner.transformation.le.inverseimplicationreduction;

import junit.framework.TestCase;

import org.omwg.logicalexpression.LogicalExpression;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsml.reasoner.transformation.le.LETestHelper;
import org.wsmo.wsml.ParserException;


public class InvImplRightDisjunctionReplacementRuleTest extends TestCase {

    private InvImplRightDisjunctionReplacementRule rule;
    
    public InvImplRightDisjunctionReplacementRuleTest() {
        super();
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        this.rule = new InvImplRightDisjunctionReplacementRule(new WSMO4JManager());
    }
    
    
    public void testIsApplicable() throws ParserException {
        assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"")));
        assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\" implies _\"urn:b\"")));
        assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\" impliedBy  _\"urn:b\"")));
        assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\" implies _\"urn:b\" or _\"urn:c\" ")));
        assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\" impliedBy _\"urn:b\" and _\"urn:c\" ")));
        
        assertTrue(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\" impliedBy _\"urn:b\" or _\"urn:c\" ")));
        assertTrue(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\" impliedBy _\"urn:b\" or  (_\"urn:c\"  and _\"urn:d\")")));
        assertTrue(rule.isApplicable(LETestHelper.buildLE(" (_\"urn:a\" or _\"urn:c\") impliedBy _\"urn:b\" or _\"urn:d\"")));
        
        
    }
    
    public void testApply() throws ParserException {
        LogicalExpression in = LETestHelper.buildLE("_\"urn:a\" impliedBy _\"urn:b\" or _\"urn:c\" ");
        LogicalExpression out = LETestHelper.buildLE(" ( _\"urn:a\" impliedBy _\"urn:b\" ) and ( _\"urn:a\" impliedBy _\"urn:c\" )");
        assertEquals(out, rule.apply(in));
        
        in = LETestHelper.buildLE("_\"urn:a\" impliedBy _\"urn:b\" or  (_\"urn:c\"  and _\"urn:d\")");
        out = LETestHelper.buildLE(" (_\"urn:a\" impliedBy _\"urn:b\") and ( _\"urn:a\" impliedBy  (_\"urn:c\"  and _\"urn:d\") ) ");
        assertEquals(out, rule.apply(in));
        
        in = LETestHelper.buildLE(" (_\"urn:a\" or _\"urn:c\") impliedBy _\"urn:b\" or _\"urn:d\"");
        out = LETestHelper.buildLE("( (_\"urn:a\" or _\"urn:c\") impliedBy _\"urn:b\" ) and  ( (_\"urn:a\" or _\"urn:c\") impliedBy _\"urn:d\") ");
        assertEquals(out, rule.apply(in));
    }
}
