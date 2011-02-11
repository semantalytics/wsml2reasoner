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
package org.wsml.reasoner.transformation.le.foldecomposition;

import junit.framework.TestCase;

import org.omwg.logicalexpression.LogicalExpression;
import org.sti2.wsmo4j.factory.WsmlFactoryContainer;
import org.wsml.reasoner.transformation.le.LETestHelper;
import org.wsmo.factory.FactoryContainer;
import org.wsmo.wsml.ParserException;

public class FOLMoleculeDecompositionRuleTest extends TestCase {
	
	private FOLMoleculeDecompositionRule rule;
	// "X[A1,...,An]\n\t=>\n X[A1] and ... and X[An]\n";
	
	public FOLMoleculeDecompositionRuleTest() {
        super();
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        FactoryContainer factory = new WsmlFactoryContainer();
        this.rule = new FOLMoleculeDecompositionRule(factory);
    }
    
    
    public void testIsApplicable() throws ParserException {
        assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"(_#)")));
        assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"(_#, _#)")));
        assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"(_#, _#, _#)")));
        assertFalse(rule.isApplicable(LETestHelper.buildLE("(_\"urn:a\" and _\"urn:b\") :- _\"urn:c\" ")));
        assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\" or _\"urn:b\"")));
        assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\" and _\"urn:b\" and _\"urn:c\" or _\"urn:d\"")));
        assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\" and _\"urn:b\" implies _\"urn:c\" ")));
        assertFalse(rule.isApplicable(LETestHelper.buildLE("(_\"urn:a\" or _\"urn:b\" ) and _\"urn:c\" or  _\"urn:d\"")));
        assertFalse(rule.isApplicable(LETestHelper.buildLE("(_\"urn:a\" or _\"urn:b\") :- _\"urn:e\" and _\"urn:c\" and  _\"urn:d\"")));
        assertFalse(rule.isApplicable(LETestHelper.buildLE(" _\"urn:a\" and _\"urn:b\" and _\"urn:c\" or _\"urn:f\" :- _\"urn:d\""))); 
        
        assertTrue(rule.isApplicable(LETestHelper.buildLE("_# memberOf _#")));
        assertTrue(rule.isApplicable(LETestHelper.buildLE("_# memberOf _\"urn:a\"")));
        assertTrue(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"[_\"urn:b\" ofType _#]")));
        assertTrue(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"[_\"urn:b\" impliesType _#]")));
        assertTrue(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"[_\"urn:b\" hasValue _#]")));
        assertTrue(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"[_# ofType _\"urn:b\"]")));
        assertTrue(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"[_# impliesType _\"urn:b\"]")));
        assertTrue(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"[_# hasValue _\"urn:b\"]")));
        assertTrue(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"[_# ofType _#]")));
        assertTrue(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"[_# impliesType _#]")));
        assertTrue(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"[_# hasValue _#]"))); 
        
    }
    
    public void testApply() throws ParserException {
    	LogicalExpression in = LETestHelper.buildLE("_\"urn:a\"[_\"urn:b\" impliesType _\"urn:c\"]");
        LogicalExpression result = rule.apply(in);       
        assertEquals(2, result.toString().split("#").length);
        assertTrue(result.toString().trim().equals("_\""+ FOLMoleculeDecompositionRule.impliesType + "\"(_\"urn:a\",_\"urn:b\",_\"urn:c\")."));
        
        in = LETestHelper.buildLE("_\"urn:a\"[_\"urn:b\" ofType _\"urn:c\"]");
        result = rule.apply(in);        
        assertEquals(2, result.toString().split("#").length);
        assertTrue(result.toString().trim().equals("_\""+ FOLMoleculeDecompositionRule.ofType + "\"(_\"urn:a\",_\"urn:b\",_\"urn:c\")."));
      
        in = LETestHelper.buildLE("_\"urn:a\"[_\"urn:b\" hasValue _\"urn:c\"]");
        result = rule.apply(in);
        assertEquals(2, result.toString().split("#").length);
        assertTrue(result.toString().trim().equals("_\""+ FOLMoleculeDecompositionRule.hasValue + "\"(_\"urn:a\",_\"urn:b\",_\"urn:c\")."));

        in = LETestHelper.buildLE("_\"urn:a\"[_\"urn:b\" impliesType _#]");
        result = rule.apply(in); 
        assertEquals(3, result.toString().split("#").length);
        assertTrue(result.toString().trim().equals("_\""+ FOLMoleculeDecompositionRule.impliesType + "\"(_\"urn:a\",_\"urn:b\",_#)."));

        in = LETestHelper.buildLE("_\"urn:a\" subConceptOf _#");
        result = rule.apply(in);  
        assertEquals(3, result.toString().split("#").length);
        assertTrue(result.toString().trim().equals("_\""+ FOLMoleculeDecompositionRule.sub + "\"(_\"urn:a\",_#)."));
        
        in = LETestHelper.buildLE("_\"urn:a\"[_\"urn:b\" ofType _#]");
        result = rule.apply(in);  
        assertEquals(3, result.toString().split("#").length);
        assertTrue(result.toString().trim().equals("_\""+ FOLMoleculeDecompositionRule.ofType + "\"(_\"urn:a\",_\"urn:b\",_#)."));
        

        in = LETestHelper.buildLE("_\"urn:a\"[_\"urn:b\" impliesType _#]");
        result = rule.apply(in);  
        assertEquals(3, result.toString().split("#").length);
        assertTrue(result.toString().trim().equals("_\""+ FOLMoleculeDecompositionRule.impliesType + "\"(_\"urn:a\",_\"urn:b\",_#)."));

        in = LETestHelper.buildLE("_\"urn:a\"[_\"urn:b\" hasValue _#]");
        result = rule.apply(in);  
        assertEquals(3, result.toString().split("#").length);
        assertTrue(result.toString().trim().equals("_\""+ FOLMoleculeDecompositionRule.hasValue + "\"(_\"urn:a\",_\"urn:b\",_#)."));

        in = LETestHelper.buildLE("_\"urn:a\"[_# ofType _\"urn:b\"]");
        result = rule.apply(in);  
        assertEquals(3, result.toString().split("#").length);
        assertTrue(result.toString().trim().equals("_\""+ FOLMoleculeDecompositionRule.ofType + "\"(_\"urn:a\",_#,_\"urn:b\")."));

        in = LETestHelper.buildLE("_\"urn:a\"[_# impliesType _\"urn:b\"]");
        result = rule.apply(in);  
        assertEquals(3, result.toString().split("#").length);
        assertTrue(result.toString().trim().equals("_\""+ FOLMoleculeDecompositionRule.impliesType + "\"(_\"urn:a\",_#,_\"urn:b\")."));

        in = LETestHelper.buildLE("_\"urn:a\"[_# hasValue _\"urn:b\"]");
        result = rule.apply(in);  
        assertEquals(3, result.toString().split("#").length);
        assertTrue(result.toString().trim().equals("_\""+ FOLMoleculeDecompositionRule.hasValue + "\"(_\"urn:a\",_#,_\"urn:b\")."));

        in = LETestHelper.buildLE("_\"urn:a\"[_# ofType _#]");
        result = rule.apply(in);  
        assertEquals(4, result.toString().split("#").length);
        assertTrue(result.toString().trim().equals("_\""+ FOLMoleculeDecompositionRule.ofType + "\"(_\"urn:a\",_#,_#)."));

        in = LETestHelper.buildLE("_\"urn:a\"[_# impliesType _#]");
        result = rule.apply(in);        
        assertEquals(4, result.toString().split("#").length);
        assertTrue(result.toString().trim().equals("_\""+ FOLMoleculeDecompositionRule.impliesType + "\"(_\"urn:a\",_#,_#)."));

        
        in = LETestHelper.buildLE("_\"urn:a\"[_# hasValue _#]");
        result = rule.apply(in);        
        assertEquals(4, result.toString().split("#").length);
        assertTrue(result.toString().trim().equals("_\""+ FOLMoleculeDecompositionRule.hasValue + "\"(_\"urn:a\",_#,_#)."));

    	

    }
	

}
