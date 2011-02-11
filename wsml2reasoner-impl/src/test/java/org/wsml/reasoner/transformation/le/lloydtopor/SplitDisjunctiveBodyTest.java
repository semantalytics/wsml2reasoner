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


public class SplitDisjunctiveBodyTest extends TestCase {

    private SplitDisjunctiveBody rule;
    // "A :- B1 or ... Bn\n\t=>\n A :- B1\n\t...\n A :- Bn\n"
    
    public SplitDisjunctiveBodyTest() {
        super();
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        this.rule = new SplitDisjunctiveBody(new WsmlFactoryContainer());
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
          assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\" :- _\"urn:c\" impliedBy _\"urn:b\"")));
          
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
