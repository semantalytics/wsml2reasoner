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
package org.wsml.reasoner.transformation.le.foldecomposition;

import junit.framework.TestCase;

import org.omwg.logicalexpression.LogicalExpression;
import org.wsml.reasoner.impl.WSMO4JManager;
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
        assertTrue(result.toString().trim().equals("_\""+ MoleculeDecompositionRule.impliesType + "\"(_\"urn:a\",_\"urn:b\",_\"urn:c\")."));
        
        in = LETestHelper.buildLE("_\"urn:a\"[_\"urn:b\" ofType _\"urn:c\"]");
        result = rule.apply(in);        
        assertEquals(2, result.toString().split("#").length);
        assertTrue(result.toString().trim().equals("_\""+ MoleculeDecompositionRule.ofType + "\"(_\"urn:a\",_\"urn:b\",_\"urn:c\")."));
      
        in = LETestHelper.buildLE("_\"urn:a\"[_\"urn:b\" hasValue _\"urn:c\"]");
        result = rule.apply(in);
        assertEquals(2, result.toString().split("#").length);
        assertTrue(result.toString().trim().equals("_\""+ MoleculeDecompositionRule.hasValue + "\"(_\"urn:a\",_\"urn:b\",_\"urn:c\")."));

        in = LETestHelper.buildLE("_\"urn:a\"[_\"urn:b\" impliesType _#]");
        result = rule.apply(in); 
        assertEquals(3, result.toString().split("#").length);
        assertTrue(result.toString().trim().equals("_\""+ MoleculeDecompositionRule.impliesType + "\"(_\"urn:a\",_\"urn:b\",_#)."));

        in = LETestHelper.buildLE("_\"urn:a\" subConceptOf _#");
        result = rule.apply(in);  
        assertEquals(3, result.toString().split("#").length);
        assertTrue(result.toString().trim().equals("_\""+ MoleculeDecompositionRule.sub + "\"(_\"urn:a\",_#)."));
        
        in = LETestHelper.buildLE("_\"urn:a\"[_\"urn:b\" ofType _#]");
        result = rule.apply(in);  
        assertEquals(3, result.toString().split("#").length);
        assertTrue(result.toString().trim().equals("_\""+ MoleculeDecompositionRule.ofType + "\"(_\"urn:a\",_\"urn:b\",_#)."));
        

        in = LETestHelper.buildLE("_\"urn:a\"[_\"urn:b\" impliesType _#]");
        result = rule.apply(in);  
        assertEquals(3, result.toString().split("#").length);
        assertTrue(result.toString().trim().equals("_\""+ MoleculeDecompositionRule.impliesType + "\"(_\"urn:a\",_\"urn:b\",_#)."));

        in = LETestHelper.buildLE("_\"urn:a\"[_\"urn:b\" hasValue _#]");
        result = rule.apply(in);  
        assertEquals(3, result.toString().split("#").length);
        assertTrue(result.toString().trim().equals("_\""+ MoleculeDecompositionRule.hasValue + "\"(_\"urn:a\",_\"urn:b\",_#)."));

        in = LETestHelper.buildLE("_\"urn:a\"[_# ofType _\"urn:b\"]");
        result = rule.apply(in);  
        assertEquals(3, result.toString().split("#").length);
        assertTrue(result.toString().trim().equals("_\""+ MoleculeDecompositionRule.ofType + "\"(_\"urn:a\",_#,_\"urn:b\")."));

        in = LETestHelper.buildLE("_\"urn:a\"[_# impliesType _\"urn:b\"]");
        result = rule.apply(in);  
        assertEquals(3, result.toString().split("#").length);
        assertTrue(result.toString().trim().equals("_\""+ MoleculeDecompositionRule.impliesType + "\"(_\"urn:a\",_#,_\"urn:b\")."));

        in = LETestHelper.buildLE("_\"urn:a\"[_# hasValue _\"urn:b\"]");
        result = rule.apply(in);  
        assertEquals(3, result.toString().split("#").length);
        assertTrue(result.toString().trim().equals("_\""+ MoleculeDecompositionRule.hasValue + "\"(_\"urn:a\",_#,_\"urn:b\")."));

        in = LETestHelper.buildLE("_\"urn:a\"[_# ofType _#]");
        result = rule.apply(in);  
        assertEquals(4, result.toString().split("#").length);
        assertTrue(result.toString().trim().equals("_\""+ MoleculeDecompositionRule.ofType + "\"(_\"urn:a\",_#,_#)."));

        in = LETestHelper.buildLE("_\"urn:a\"[_# impliesType _#]");
        result = rule.apply(in);        
        assertEquals(4, result.toString().split("#").length);
        assertTrue(result.toString().trim().equals("_\""+ MoleculeDecompositionRule.impliesType + "\"(_\"urn:a\",_#,_#)."));

        
        in = LETestHelper.buildLE("_\"urn:a\"[_# hasValue _#]");
        result = rule.apply(in);        
        assertEquals(4, result.toString().split("#").length);
        assertTrue(result.toString().trim().equals("_\""+ MoleculeDecompositionRule.hasValue + "\"(_\"urn:a\",_#,_#)."));

    	

    }
}
