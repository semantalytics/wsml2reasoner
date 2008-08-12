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


package org.wsml.reasoner.transformation.le.lloydtopor;

import java.util.Set;

import junit.framework.TestCase;

import org.omwg.logicalexpression.LogicalExpression;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsml.reasoner.transformation.le.LETestHelper;
import org.wsmo.wsml.ParserException;


public class TestSplitConjunctiveHead extends TestCase {

    private SplitConjunctiveHead rule;
    // "A1 and ... and An :- B\n\t=>\n A1 :- B\n\t...\n An :- B\n"
    
    public TestSplitConjunctiveHead() {
        super();
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        this.rule = new SplitConjunctiveHead(new WSMO4JManager());
    }
    
    protected void tearDown() throws Exception {
        super.tearDown();
        this.rule = null;
    }
    
    public void testIsApplicable() throws ParserException {
          assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"")));
          assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\" implies _\"urn:b\""))); 
          assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\" or _\"urn:b\"")));
          assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\" and _\"urn:b\" and _\"urn:c\" or _\"urn:d\"")));
          assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\" and _\"urn:b\" implies _\"urn:c\" ")));
          assertFalse(rule.isApplicable(LETestHelper.buildLE("(_\"urn:a\" or _\"urn:b\" ) and _\"urn:c\" or  _\"urn:d\"")));
          assertFalse(rule.isApplicable(LETestHelper.buildLE("(_\"urn:a\" or _\"urn:b\") :- _\"urn:e\" and _\"urn:c\" and  _\"urn:d\"")));
          assertFalse(rule.isApplicable(LETestHelper.buildLE(" _\"urn:a\" and _\"urn:b\" and _\"urn:c\" or _\"urn:f\" :- _\"urn:d\"")));
         
          assertTrue(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\" and _\"urn:c\" :- _\"urn:b\" ")));
          assertTrue(rule.isApplicable(LETestHelper.buildLE(" _\"urn:a\" and _\"urn:b\" and _\"urn:c\" and _\"urn:f\" :- _\"urn:d\"")));
          assertTrue(rule.isApplicable(LETestHelper.buildLE("( _\"urn:a\" and _\"urn:b\" ) and _\"urn:c\" :- _\"urn:d\" or _\"urn:e\"")));
    }
    
    public void testApply() throws ParserException {
    	
    	LogicalExpression in = LETestHelper.buildLE("_\"urn:a\" and _\"urn:c\" :- _\"urn:b\" ");
        Set <LogicalExpression> result = rule.apply(in);
     
        assertEquals(2, result.size());
        assertTrue(result.contains(LETestHelper.buildLE("_\"urn:a\" :- _\"urn:b\"")));
        assertTrue(result.contains(LETestHelper.buildLE("_\"urn:c\" :- _\"urn:b\"")));
        
  
        in = LETestHelper.buildLE("( _\"urn:a\" and _\"urn:b\" ) and _\"urn:c\" :- _\"urn:d\" or _\"urn:e\"");
        result = rule.apply(in);
        assertEquals(2, result.size());
        assertTrue(result.contains(LETestHelper.buildLE("_\"urn:c\" :- _\"urn:d\" or _\"urn:e\"")));
        assertTrue(result.contains(LETestHelper.buildLE("_\"urn:a\" and _\"urn:b\" :- (_\"urn:d\" or _\"urn:e\")")));
    }
}
