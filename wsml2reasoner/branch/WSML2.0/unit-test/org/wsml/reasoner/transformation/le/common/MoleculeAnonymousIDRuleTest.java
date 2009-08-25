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
package org.wsml.reasoner.transformation.le.common;

import junit.framework.TestCase;

import org.omwg.logicalexpression.LogicalExpression;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsml.reasoner.transformation.AnonymousIdTranslator;
import org.wsml.reasoner.transformation.AnonymousIdUtils;
import org.wsml.reasoner.transformation.le.LETestHelper;
import org.wsmo.wsml.ParserException;


public class MoleculeAnonymousIDRuleTest extends TestCase {

	    private MoleculeAnonymousIDRule rule;
	    
	    public MoleculeAnonymousIDRuleTest() {
	        super();
	    }
	    
	    protected void setUp() throws Exception {
	        super.setUp();
	        WSMO4JManager wsmoManager = new WSMO4JManager();
	        this.rule = new MoleculeAnonymousIDRule(wsmoManager, new AnonymousIdTranslator(wsmoManager.getWSMOFactory()));
	    }
	    
	    
	    public void testIsApplicable() throws ParserException {
	        assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"(_#)")));
	        assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"(_#, _#)")));
	        assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"(_#, _#, _#)")));
	        assertTrue(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\" subConceptOf _#")));
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
	        LogicalExpression in = LETestHelper.buildLE("_\"urn:a\" subConceptOf _#");
	        LogicalExpression result = rule.apply(in);
	        assertTrue(!result.toString().contains("_#"));
	        assertEquals(2, result.toString().split(AnonymousIdUtils.ANONYMOUS_PREFIX).length);
	        assertTrue(result.toString().startsWith("_\"urn:a\" subConceptOf _\"" + AnonymousIdUtils.ANONYMOUS_PREFIX));
	        
	        in = LETestHelper.buildLE("_# memberOf _\"urn:a\"");
	        result = rule.apply(in);
	        assertTrue(!result.toString().contains("_#"));
	        assertEquals(2, result.toString().split(AnonymousIdUtils.ANONYMOUS_PREFIX).length);
	        assertTrue(result.toString().startsWith("_\"" + AnonymousIdUtils.ANONYMOUS_PREFIX));
	        assertTrue(result.toString().trim().endsWith(" memberOf _\"urn:a\"."));
	        
	        in = LETestHelper.buildLE("_\"urn:a\"[_\"urn:b\" ofType _#]");
	        result = rule.apply(in);
	        assertTrue(!result.toString().contains("_#"));
	        assertEquals(2, result.toString().split(AnonymousIdUtils.ANONYMOUS_PREFIX).length);
	        assertTrue(result.toString().startsWith("_\"urn:a\"[_\"urn:b\" ofType _\"" + AnonymousIdUtils.ANONYMOUS_PREFIX));
	        
	        in = LETestHelper.buildLE("_\"urn:a\"[_\"urn:b\" impliesType _#]");
	        result = rule.apply(in);
	        assertTrue(!result.toString().contains("_#"));
	        assertEquals(2, result.toString().split(AnonymousIdUtils.ANONYMOUS_PREFIX).length);
	        assertTrue(result.toString().startsWith("_\"urn:a\"[_\"urn:b\" impliesType _\"" + AnonymousIdUtils.ANONYMOUS_PREFIX));
	        
	        in = LETestHelper.buildLE("_\"urn:a\"[_\"urn:b\" hasValue _#]");
	        result = rule.apply(in);
	        assertTrue(!result.toString().contains("_#"));
	        assertEquals(2, result.toString().split(AnonymousIdUtils.ANONYMOUS_PREFIX).length);
	        assertTrue(result.toString().startsWith("_\"urn:a\"[_\"urn:b\" hasValue _\"" + AnonymousIdUtils.ANONYMOUS_PREFIX));
	        
	        in = LETestHelper.buildLE("_\"urn:a\"[_# ofType _\"urn:b\"]");
	        result = rule.apply(in);
	        assertTrue(!result.toString().contains("_#"));
	        assertEquals(2, result.toString().split(AnonymousIdUtils.ANONYMOUS_PREFIX).length);
	        assertTrue(result.toString().startsWith("_\"urn:a\"[_\"" + AnonymousIdUtils.ANONYMOUS_PREFIX));
	        assertTrue(result.toString().trim().endsWith(" ofType _\"urn:b\"]."));
	        
	        in = LETestHelper.buildLE("_\"urn:a\"[_# impliesType _\"urn:b\"]");
	        result = rule.apply(in);
	        assertTrue(!result.toString().contains("_#"));
	        assertEquals(2, result.toString().split(AnonymousIdUtils.ANONYMOUS_PREFIX).length);
	        assertTrue(result.toString().startsWith("_\"urn:a\"[_\"" + AnonymousIdUtils.ANONYMOUS_PREFIX));
	        assertTrue(result.toString().trim().endsWith(" impliesType _\"urn:b\"]."));
	        
	        in = LETestHelper.buildLE("_\"urn:a\"[_# hasValue _\"urn:b\"]");
	        result = rule.apply(in);
	        assertTrue(!result.toString().contains("_#"));
	        assertEquals(2, result.toString().split(AnonymousIdUtils.ANONYMOUS_PREFIX).length);
	        assertTrue(result.toString().startsWith("_\"urn:a\"[_\"" + AnonymousIdUtils.ANONYMOUS_PREFIX));
	        assertTrue(result.toString().trim().endsWith(" hasValue _\"urn:b\"]."));
	        
	        in = LETestHelper.buildLE("_\"urn:a\"[_# ofType _#]");
	        result = rule.apply(in);
	        assertTrue(!result.toString().contains("_#"));
	        assertEquals(3, result.toString().split(AnonymousIdUtils.ANONYMOUS_PREFIX).length);
	        assertTrue(result.toString().startsWith("_\"urn:a\"[_\"" + AnonymousIdUtils.ANONYMOUS_PREFIX));
	        
	        in = LETestHelper.buildLE("_\"urn:a\"[_# impliesType _#]");
	        result = rule.apply(in);
	        assertTrue(!result.toString().contains("_#"));
	        assertEquals(3, result.toString().split(AnonymousIdUtils.ANONYMOUS_PREFIX).length);
	        assertTrue(result.toString().startsWith("_\"urn:a\"[_\"" + AnonymousIdUtils.ANONYMOUS_PREFIX));
	        
	        in = LETestHelper.buildLE("_\"urn:a\"[_# hasValue _#]");
	        result = rule.apply(in);
	        assertTrue(!result.toString().contains("_#"));
	        assertEquals(3, result.toString().split(AnonymousIdUtils.ANONYMOUS_PREFIX).length);
	        assertTrue(result.toString().startsWith("_\"urn:a\"[_\"" + AnonymousIdUtils.ANONYMOUS_PREFIX));
	    }
}

