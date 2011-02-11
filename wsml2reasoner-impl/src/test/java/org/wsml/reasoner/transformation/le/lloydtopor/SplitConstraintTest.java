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
package org.wsml.reasoner.transformation.le.lloydtopor;

import java.util.Set;

import junit.framework.TestCase;

import org.omwg.logicalexpression.LogicalExpression;
import org.sti2.wsmo4j.factory.WsmlFactoryContainer;
import org.wsml.reasoner.transformation.le.LETestHelper;
import org.wsmo.wsml.ParserException;


public class SplitConstraintTest extends TestCase {

    private SplitConstraint rule;
    // "!- B1 or ... or Bn\n\t=>\n !- B1\n\t...\n !- Bn\n"
    
    public SplitConstraintTest() {
        super();
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        this.rule = new SplitConstraint(new WsmlFactoryContainer());
    }
    
    public void testIsApplicable() throws ParserException {
          assertFalse(rule.isApplicable(LETestHelper.buildLE("!- _\"urn:a\"")));
          assertFalse(rule.isApplicable(LETestHelper.buildLE(" _\"urn:a\" "))); 
          assertFalse(rule.isApplicable(LETestHelper.buildLE("!- (_\"urn:a\") and ( _\"urn:b\")")));
          assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\" and _\"urn:b\" and _\"urn:c\" or _\"urn:d\"")));
          assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\" and _\"urn:b\" implies _\"urn:c\" ")));
          assertFalse(rule.isApplicable(LETestHelper.buildLE("(_\"urn:a\" or _\"urn:b\" ) and _\"urn:c\" or  _\"urn:d\"")));
          assertFalse(rule.isApplicable(LETestHelper.buildLE("(_\"urn:a\" or _\"urn:b\") :- _\"urn:e\" and _\"urn:c\" and  _\"urn:d\"")));
          assertFalse(rule.isApplicable(LETestHelper.buildLE(" _\"urn:a\" and _\"urn:b\" and _\"urn:c\" or _\"urn:f\" :- _\"urn:d\"")));
         
          assertTrue(rule.isApplicable(LETestHelper.buildLE(" !- (_\"urn:a\") or ( _\"urn:b\")")));
          assertTrue(rule.isApplicable(LETestHelper.buildLE(" !- _\"urn:a\" or _\"urn:b\" or _\"urn:c\" ")));
          assertTrue(rule.isApplicable(LETestHelper.buildLE(" !- _\"urn:a\" or (_\"urn:b\" and _\"urn:c\")  ")));
          assertTrue(rule.isApplicable(LETestHelper.buildLE(" !- ( _\"urn:a\" and _\"urn:b\" ) or _\"urn:c\" or _\"urn:d\" ")));
          

   
    }
    
    public void testApply() throws ParserException {
    	
    	LogicalExpression in = LETestHelper.buildLE(" !- (_\"urn:a\") or ( _\"urn:b\")");
    	Set <LogicalExpression> result = rule.apply(in);
        
        assertEquals(2, result.size());
        assertTrue(result.contains(LETestHelper.buildLE("!- _\"urn:a\" ")));
        assertTrue(result.contains(LETestHelper.buildLE("!- _\"urn:b\" ")));
        
        in = LETestHelper.buildLE(" !- (_\"urn:a\") or (_\"urn:b\") or (_\"urn:c\") ");
    	result = rule.apply(in);

        assertEquals(2, result.size());
        assertTrue(result.contains(LETestHelper.buildLE("!- _\"urn:c\" ")));
        assertTrue(result.contains(LETestHelper.buildLE("!- _\"urn:a\" or (_\"urn:b\")")));
       
        
        in = LETestHelper.buildLE(" !- ( _\"urn:a\" and _\"urn:b\" ) or _\"urn:c\" or _\"urn:d\" ");
    	result = rule.apply(in);
      
        assertEquals(2, result.size());
        assertTrue(result.contains(LETestHelper.buildLE("!- _\"urn:d\" ")));
        assertTrue(result.contains(LETestHelper.buildLE("!- ( _\"urn:a\" and _\"urn:b\" ) or _\"urn:c\" ")));
      

    }
}
