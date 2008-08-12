/*
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

package org.wsml.reasoner.transformation.le.foldecomposition;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;


import org.omwg.logicalexpression.LogicalExpression;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsml.reasoner.transformation.AnonymousIdUtils;
import org.wsml.reasoner.transformation.le.LETestHelper;
import org.wsmo.wsml.ParserException;


public class TestFOLMoleculeDecompositionRules extends TestCase {

    private MoleculeDecompositionRule rule;
    // "X[A1,...,An]\n\t=>\n X[A1] and ... and X[An]\n"
    
    public TestFOLMoleculeDecompositionRules() {
        super();
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        WSMO4JManager wsmoManager = new WSMO4JManager();
        this.rule = new MoleculeDecompositionRule(wsmoManager);
    }
    
    protected void tearDown() throws Exception {
        super.tearDown();
        this.rule = null;
    }
    
    public void testIsApplicable() throws ParserException {
        assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"(_#)")));
        assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"(_#, _#)")));
        assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"(_#, _#, _#)")));
        
        Set<LogicalExpression> set = new HashSet<LogicalExpression>();
		set.add(LETestHelper.buildLE("_\"urn:a\""));
		set.add(LETestHelper.buildLE("_\"urn:b\""));
		set.add(LETestHelper.buildLE("_\"urn:c\""));
        
//        assertTrue(rule.isApplicable(set));
//        assertTrue(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\" ")));
//        assertTrue(rule.isApplicable(LETestHelper.buildLE("_# memberOf _\"urn:a\"")));
//        assertTrue(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"[_\"urn:b\" ofType _#]")));
//        assertTrue(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"[_\"urn:b\" impliesType _#]")));
//        assertTrue(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"[_\"urn:b\" hasValue _#]")));
//        assertTrue(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"[_# ofType _\"urn:b\"]")));
//        assertTrue(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"[_# impliesType _\"urn:b\"]")));
//        assertTrue(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"[_# hasValue _\"urn:b\"]")));
//        assertTrue(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"[_# ofType _#]")));
//        assertTrue(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"[_# impliesType _#]")));
//        assertTrue(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"[_# hasValue _#]")));
        
        
    }
    
    public void testApply() throws ParserException {
    	
    	Set<LogicalExpression> in = new HashSet<LogicalExpression>();
    	in.add(LETestHelper.buildLE("_\"urn:a\""));
		in.add(LETestHelper.buildLE("_\"urn:b\""));
		in.add(LETestHelper.buildLE("_\"urn:c\""));
		
//        LogicalExpression result = rule.apply(in);
//        assertTrue(!result.toString().contains("_#"));
//        assertEquals(2, result.toString().split(AnonymousIdUtils.ANONYMOUS_PREFIX).length);
//        assertTrue(result.toString().startsWith("_\"urn:a\" subConceptOf _\"" + AnonymousIdUtils.ANONYMOUS_PREFIX));
    	

    }
}
