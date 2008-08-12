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


public class TestSplitDisjunctiveBody extends TestCase {

    private SplitDisjunctiveBody rule;
    // "A :- B1 or ... Bn\n\t=>\n A :- B1\n\t...\n A :- Bn\n"
    
    public TestSplitDisjunctiveBody() {
        super();
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        this.rule = new SplitDisjunctiveBody(new WSMO4JManager());
    }
    
    protected void tearDown() throws Exception {
        super.tearDown();
        this.rule = null;
    }
    
    public void testIsApplicable() throws ParserException {
          assertFalse(rule.isApplicable(LETestHelper.buildLE(" _\"urn:a\"")));
          assertFalse(rule.isApplicable(LETestHelper.buildLE(" _\"urn:a\" "))); 
          assertFalse(rule.isApplicable(LETestHelper.buildLE("(_\"urn:a\") and ( _\"urn:b\")")));
          assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\" and _\"urn:b\" and _\"urn:c\" or _\"urn:d\"")));
          assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\" and _\"urn:b\" implies _\"urn:c\" ")));
          assertFalse(rule.isApplicable(LETestHelper.buildLE("(_\"urn:a\" or _\"urn:b\" ) and _\"urn:c\" or  _\"urn:d\"")));
          assertFalse(rule.isApplicable(LETestHelper.buildLE("(_\"urn:a\" or _\"urn:b\") :- _\"urn:e\" and _\"urn:c\" and  _\"urn:d\"")));
          assertFalse(rule.isApplicable(LETestHelper.buildLE(" _\"urn:a\" and _\"urn:b\" and _\"urn:c\" or _\"urn:f\" :- _\"urn:d\""))); 
          assertFalse(rule.isApplicable(LETestHelper.buildLE(" _\"urn:a\" :- _\"urn:b\"")));
          assertFalse(rule.isApplicable(LETestHelper.buildLE(" _\"urn:a\" :- (_\"urn:b\" and _\"urn:d\")")));
          
          assertTrue(rule.isApplicable(LETestHelper.buildLE(" _\"urn:a\" :- _\"urn:b\" or _\"urn:c\"")));
          assertTrue(rule.isApplicable(LETestHelper.buildLE(" _\"urn:a\" :- _\"urn:b\" or (naf _\"urn:c\")")));
          assertTrue(rule.isApplicable(LETestHelper.buildLE(" _\"urn:a\" :- (_\"urn:b\" and _\"urn:c\") or _\"urn:d\"")));
          assertTrue(rule.isApplicable(LETestHelper.buildLE(" (_\"urn:a\" and _\"urn:b\") :- _\"urn:c\" or _\"urn:d\"")));
          
   
    }
    
    public void testApply() throws ParserException {
    	
    	LogicalExpression in = LETestHelper.buildLE(" _\"urn:a\" :- _\"urn:b\" or _\"urn:c\"");
    	Set <LogicalExpression> result = rule.apply(in);
        
        assertTrue(!result.toString().contains("_#"));
        assertEquals(2, result.size());
        assertTrue(result.contains(LETestHelper.buildLE(" _\"urn:a\" :- _\"urn:b\" ")));
        assertTrue(result.contains(LETestHelper.buildLE(" _\"urn:a\" :- _\"urn:c\" ")));
        
        in = LETestHelper.buildLE(" _\"urn:a\" :- (_\"urn:b\" and _\"urn:c\") or _\"urn:d\"");
    	result = rule.apply(in);

        assertTrue(!result.toString().contains("_#"));
        assertEquals(2, result.size());
        assertTrue(result.contains(LETestHelper.buildLE("_\"urn:a\" :- (_\"urn:b\" and _\"urn:c\")")));
        assertTrue(result.contains(LETestHelper.buildLE(" _\"urn:a\" :- _\"urn:d\"")));
       
        
        in = LETestHelper.buildLE(" (_\"urn:a\" and _\"urn:b\") :- _\"urn:c\" or _\"urn:d\"");
    	result = rule.apply(in);
      
        assertTrue(!result.toString().contains("_#"));
        assertEquals(2, result.size());
        assertTrue(result.contains(LETestHelper.buildLE("(_\"urn:a\" and _\"urn:b\") :- _\"urn:c\" ")));
        assertTrue(result.contains(LETestHelper.buildLE("(_\"urn:a\" and _\"urn:b\") :- _\"urn:d\" ")));
      

    }
}
