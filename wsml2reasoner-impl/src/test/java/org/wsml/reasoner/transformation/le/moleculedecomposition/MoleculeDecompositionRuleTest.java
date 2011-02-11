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
package org.wsml.reasoner.transformation.le.moleculedecomposition;

import junit.framework.TestCase;

import org.omwg.logicalexpression.LogicalExpression;
import org.sti2.wsmo4j.factory.WsmlFactoryContainer;
import org.wsml.reasoner.transformation.le.LETestHelper;
import org.wsmo.factory.FactoryContainer;
import org.wsmo.wsml.ParserException;

public class MoleculeDecompositionRuleTest extends TestCase{
	
	  private MoleculeDecompositionRule rule;
	  // "X[A1,...,An]\n\t=>\n X[A1] and ... and X[An]\n"
	 
	    
	   public MoleculeDecompositionRuleTest() {
	        super();
	   }
	    
	   protected void setUp() throws Exception {
	        super.setUp();
	        FactoryContainer wsmoManager = new WsmlFactoryContainer();
	        this.rule = new MoleculeDecompositionRule(wsmoManager);
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
	        assertFalse(rule.isApplicable(LETestHelper.buildLE("_# memberOf _#")));
	        assertFalse(rule.isApplicable(LETestHelper.buildLE("_# memberOf _\"urn:a\"")));
	        assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"[_\"urn:b\" ofType _#]")));
	        assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"[_\"urn:b\" hasValue _#]")));
	        assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"[_\"urn:b\" impliesType _#]")));
	        assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"[_\"urn:b\" hasValue _#]")));
	        assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"[_# ofType _\"urn:b\"]")));
	        assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"[_# impliesType _\"urn:b\"]")));
	        assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"[_# hasValue _\"urn:b\"]")));
	        assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"[_# ofType _#]")));
	        assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"[_# impliesType _#]")));
	        assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"[_# hasValue _#]")));  
	        assertFalse(rule.isApplicable(LETestHelper.buildLE("_# [_\"urn:a\" hasValue _\"urn:c\"]"))); 
	        assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\" [_\"urn:b\" hasValue _\"urn:c\"]"))); 
	        assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"[_\"urn:b\" impliesType _#]")));
	        assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"[_\"urn:b\" hasValue _#]")));
	        assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"[_# ofType _\"urn:b\"]")));
	        assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"[_# impliesType _\"urn:b\"]")));
	        assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"[_# hasValue _\"urn:b\"]")));
	        assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"[_# ofType _#]")));
	        assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"[_# impliesType _#]")));
	        assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"[_# hasValue _#]"))); 
	        assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\" subConceptOf _\"urn:b\" and _\"urn:a\"[_\"urn:c\" ofType _#]"))); 
	        assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\" subConceptOf _\"urn:b\" and _\"urn:a\"[_\"urn:c\" ofType _\"urn:d\"]")));
	        assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"[_\"urn:c\" ofType _\"urn:d\"] and _\"urn:a\" subConceptOf _\"urn:b\"")));
	        
	        
	        assertTrue(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"[_\"urn:c\" ofType _\"urn:d\"] subConceptOf _\"urn:b\" ")));
	        assertTrue(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"[_\"urn:c\" ofType _#] subConceptOf _# ")));
	        assertTrue(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"[_\"urn:c\" ofType _# , _\"urn:d\" impliesType _#] memberOf _# ")));
	        assertTrue(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"[_\"urn:c\" ofType _# , _\"urn:d\" impliesType _#, _# hasValue _#] subConceptOf _# ")));
	        
	        
	    }
	    
	    public void testApply() throws ParserException {
	    	
	    	LogicalExpression in = LETestHelper.buildLE("_\"urn:a\"[_\"urn:c\" ofType _\"urn:d\"] subConceptOf _\"urn:b\" ");
	    	LogicalExpression out = LETestHelper.buildLE("_\"urn:a\"[_\"urn:c\" ofType _\"urn:d\"] and _\"urn:a\" subConceptOf _\"urn:b\".");
	        LogicalExpression result = rule.apply(in);       
	        assertTrue(result.equals(out));
	        
	        in = LETestHelper.buildLE("_\"urn:a\"[_\"urn:c\" ofType _\"urn:d\"] memberOf _\"urn:b\" ");
	        out = LETestHelper.buildLE("_\"urn:a\"[_\"urn:c\" ofType _\"urn:d\"] and _\"urn:a\" memberOf _\"urn:b\".");
	        result = rule.apply(in);          
	        assertTrue(result.equals(out));
	        
	        in = LETestHelper.buildLE("_\"urn:a\"[_\"urn:c\" ofType _# , _\"urn:d\" impliesType _#] memberOf _#");  
	        result = rule.apply(in);       
	        assertTrue(result.toString().contains("_\"urn:a\" memberOf _#"));
	        assertTrue(result.toString().contains("_\"urn:a\"[_\"urn:c\" ofType _#]"));
	        assertTrue(result.toString().contains("_\"urn:a\"[_\"urn:d\" impliesType _#]"));

	    	

	    }

}
