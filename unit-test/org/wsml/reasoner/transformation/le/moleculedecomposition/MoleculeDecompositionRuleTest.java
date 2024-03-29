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
package org.wsml.reasoner.transformation.le.moleculedecomposition;

import junit.framework.TestCase;


import org.omwg.logicalexpression.LogicalExpression;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsml.reasoner.transformation.le.LETestHelper;
import org.wsml.reasoner.transformation.le.moleculedecomposition.MoleculeDecompositionRule;
import org.wsmo.wsml.ParserException;

public class MoleculeDecompositionRuleTest extends TestCase{
	
	  private MoleculeDecompositionRule rule;
	  // "X[A1,...,An]\n\t=>\n X[A1] and ... and X[An]\n"
	 
	    
	   public MoleculeDecompositionRuleTest() {
	        super();
	   }
	    
	   protected void setUp() throws Exception {
	        super.setUp();
	        WSMO4JManager wsmoManager = new WSMO4JManager();
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
