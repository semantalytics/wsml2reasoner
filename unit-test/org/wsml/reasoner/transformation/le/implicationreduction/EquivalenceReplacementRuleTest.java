/*
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
package org.wsml.reasoner.transformation.le.implicationreduction;

import junit.framework.TestCase;

import org.omwg.logicalexpression.LogicalExpression;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsml.reasoner.transformation.le.LETestHelper;
import org.wsmo.wsml.ParserException;


public class EquivalenceReplacementRuleTest extends TestCase {

    private EquivalenceReplacementRule rule;
    
    public EquivalenceReplacementRuleTest() {
        super();
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        this.rule = new EquivalenceReplacementRule(new WSMO4JManager());
    }
    
    
    public void testIsApplicable() throws ParserException {
        assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"")));
        assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\" and _\"urn:b\"")));
        assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\" or  _\"urn:b\"")));
        assertFalse(rule.isApplicable(LETestHelper.buildLE("naf _\"urn:a\"")));
        assertFalse(rule.isApplicable(LETestHelper.buildLE("naf (_\"urn:a\" equivalent _\"urn:b\")")));
        assertTrue(rule.isApplicable(LETestHelper.buildLE("(_\"urn:a\" equivalent  _\"urn:b\")")));
        assertTrue(rule.isApplicable(LETestHelper.buildLE("(naf _\"urn:a\" equivalent naf _\"urn:b\")")));
        assertTrue(rule.isApplicable(LETestHelper.buildLE("( _\"urn:a\" equivalent naf _\"urn:b\")")));
        assertTrue(rule.isApplicable(LETestHelper.buildLE("( (_\"urn:a\" and _\"urn:b\") equivalent ( _\"urn:c\" or _\"urn:d\") )")));
        assertTrue(rule.isApplicable(LETestHelper.buildLE("( _\"urn:a\" equivalent _\"urn:b\" )")));
    }
    
    public void testApply() throws ParserException {
        LogicalExpression in = LETestHelper.buildLE("(_\"urn:a\" equivalent  _\"urn:b\")");
        LogicalExpression out = LETestHelper.buildLE("(_\"urn:a\" impliedBy _\"urn:b\") and  (_\"urn:b\" impliedBy _\"urn:a\")");
        assertEquals(out, rule.apply(in));
        
        in = LETestHelper.buildLE("( ( _\"urn:a\" and _\"urn:c\" ) equivalent ( _\"urn:b\" and _\"urn:d\" ))");
        out = LETestHelper.buildLE("( ( _\"urn:a\" and _\"urn:c\" ) impliedBy ( _\"urn:b\" and _\"urn:d\" ) ) and ( ( _\"urn:b\" and _\"urn:d\") impliedBy ( _\"urn:a\" and _\"urn:c\") )");
        assertEquals(out, rule.apply(in));
        
        in = LETestHelper.buildLE("( _\"urn:a\" equivalent naf _\"urn:b\")");
        out = LETestHelper.buildLE("( naf _\"urn:b\" impliedBy _\"urn:a\") and ( _\"urn:a\" impliedBy naf _\"urn:b\")");
        assertEquals(out, rule.apply(in));
    }
}
