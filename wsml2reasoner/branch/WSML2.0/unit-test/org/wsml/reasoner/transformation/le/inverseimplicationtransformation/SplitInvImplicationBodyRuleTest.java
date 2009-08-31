/**
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
package org.wsml.reasoner.transformation.le.inverseimplicationtransformation;

import java.util.Set;

import junit.framework.TestCase;

import org.omwg.logicalexpression.LogicalExpression;
import org.sti2.wsmo4j.factory.FactoryImpl;
import org.wsml.reasoner.transformation.le.LETestHelper;
import org.wsmo.wsml.ParserException;

public class SplitInvImplicationBodyRuleTest extends TestCase {

	protected SplitInvImplicationBodyRule rule;

	public SplitInvImplicationBodyRuleTest() {
		super();
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.rule = new SplitInvImplicationBodyRule(new FactoryImpl());
	}

	public void testIsApplicable() throws ParserException {
		assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"")));
		assertFalse(rule.isApplicable(LETestHelper
				.buildLE("_\"urn:a\" implies _\"urn:b\"")));
		assertFalse(rule.isApplicable(LETestHelper
				.buildLE("_\"urn:a\" impliedBy  _\"urn:b\"")));
		assertFalse(rule.isApplicable(LETestHelper
				.buildLE("_\"urn:a\" implies _\"urn:b\" or _\"urn:c\" ")));
		assertFalse(rule.isApplicable(LETestHelper
				.buildLE("_\"urn:a\" impliedBy _\"urn:b\" and _\"urn:c\" ")));
		assertFalse(rule.isApplicable(LETestHelper
				.buildLE("_\"urn:a\" impliedBy _\"urn:b\" or _\"urn:c\" ")));
		assertFalse(rule
				.isApplicable(LETestHelper
						.buildLE("_\"urn:a\" impliedBy _\"urn:b\" or  (_\"urn:c\"  and _\"urn:d\")")));
		assertFalse(rule
				.isApplicable(LETestHelper
						.buildLE(" (_\"urn:a\" or _\"urn:c\") impliedBy _\"urn:b\" or _\"urn:d\"")));
		assertFalse(rule.isApplicable(LETestHelper
				.buildLE("_\"urn:a\" impliedBy _\"urn:b\" :- _\"urn:c\" ")));
		assertFalse(rule.isApplicable(LETestHelper
				.buildLE("_\"urn:a\" :- _\"urn:b1\" implies _\"urn:b2\"")));
		assertFalse(rule
				.isApplicable(LETestHelper
						.buildLE("_\"urn:c\" :- (_\"urn:b\" impliedBy _\"urn:a\") and ( _\"urn:a\" impliedBy _\"urn:b\")")));

		assertTrue(rule.isApplicable(LETestHelper
				.buildLE("_\"urn:a\" :- _\"urn:b1\" impliedBy _\"urn:b2\"")));
		assertTrue(rule
				.isApplicable(LETestHelper
						.buildLE("_\"urn:a\" impliedBy _\"urn:b\" :- _\"urn:c\" impliedBy _\"urn:d\"")));
		assertTrue(rule
				.isApplicable(LETestHelper
						.buildLE("_\"urn:z\" or _\"urn:y\" :- (_\"urn:a\" or _\"urn:c\") impliedBy _\"urn:b\" or _\"urn:d\"")));

	}

	public void testApply() throws ParserException {
		LogicalExpression in = LETestHelper
				.buildLE(" _\"urn:a\" :- _\"urn:c\" impliedBy _\"urn:b\"");
		Set<LogicalExpression> result = rule.apply(in);

		assertEquals(2, result.size());
		containsLE(result, " _\"urn:a\" :- _\"urn:c\" and _\"urn:b\"");
		containsLE(result, " _\"urn:a\" :- naf _\"urn:b\"");
		// printRes(result);

		in = LETestHelper
				.buildLE(" _\"urn:a\" :- (_\"urn:c\" and  _\"urn:g\") impliedBy (_\"urn:b\" implies _\"urn:e\")");
		result = rule.apply(in);

		assertEquals(2, result.size());
		containsLE(
				result,
				" _\"urn:a\" :- (_\"urn:c\" and  _\"urn:g\") and (_\"urn:b\" implies _\"urn:e\")");
		containsLE(result, " _\"urn:a\" :- naf (_\"urn:b\" implies _\"urn:e\")");
		// printRes(result);

	}

	private void containsLE(Set<LogicalExpression> res, String... le)
			throws ParserException {
		for (String str : le) {
			LogicalExpression logE = LETestHelper.buildLE(str);
			assertTrue(res.contains(logE));
		}
	}

//	private void printRes(Set<LogicalExpression> result) {
//		for (LogicalExpression le : result) {
//			System.out.println(le.toString());
//		}
//	}

}
