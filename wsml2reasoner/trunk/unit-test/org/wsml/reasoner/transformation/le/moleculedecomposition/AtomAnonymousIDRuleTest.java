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
import org.wsml.reasoner.transformation.AnonymousIdTranslator;
import org.wsml.reasoner.transformation.AnonymousIdUtils;
import org.wsml.reasoner.transformation.le.LETestHelper;

import org.wsmo.wsml.ParserException;

public class AtomAnonymousIDRuleTest extends TestCase {

	private AtomAnonymousIDRule rule;

	public AtomAnonymousIDRuleTest() {
		super();
	}

	protected void setUp() throws Exception {
		super.setUp();
		WSMO4JManager wsmoManager = new WSMO4JManager();
		this.rule = new AtomAnonymousIDRule(wsmoManager,
				new AnonymousIdTranslator(wsmoManager.getWSMOFactory()));
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		this.rule = null;
	}

	public void testIsApplicable() throws ParserException {
		assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\" subConceptOf _#")));
		assertFalse(rule.isApplicable(LETestHelper.buildLE("_# memberOf _\"urn:a\"")));
		assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"[_\"urn:b\" ofType _#]")));
		assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"[_\"urn:b\" impliesType _#]")));
		assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"[_\"urn:b\" hasValue _#]")));
		assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"[_# ofType _#]")));
		assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"[_# impliesType _#]")));
		assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"[_# hasValue _#]")));
		assertTrue(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"(_#)")));
		assertTrue(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"(_#, _#)")));
		assertTrue(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"(_#, _#, _#)")));
	}

	public void testApply() throws ParserException {
		LogicalExpression in = LETestHelper.buildLE("_\"urn:a\"(_#)");
		LogicalExpression result = rule.apply(in);
		assertTrue(!result.toString().contains("_#"));
		assertEquals(2, result.toString().split(
				AnonymousIdUtils.ANONYMOUS_PREFIX).length);
		assertTrue(result.toString().startsWith(
				"_\"urn:a\"(_\"" + AnonymousIdUtils.ANONYMOUS_PREFIX));

		in = LETestHelper.buildLE("_\"urn:a\"(_#, _#, _#)");
		result = rule.apply(in);
		assertTrue(!result.toString().contains("_#"));
		assertEquals(4, result.toString().split(
				AnonymousIdUtils.ANONYMOUS_PREFIX).length);
		assertTrue(result.toString().startsWith(
				"_\"urn:a\"(_\"" + AnonymousIdUtils.ANONYMOUS_PREFIX));
	}
}
