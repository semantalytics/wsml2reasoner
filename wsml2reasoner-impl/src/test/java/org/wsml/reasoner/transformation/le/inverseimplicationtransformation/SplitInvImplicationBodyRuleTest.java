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
package org.wsml.reasoner.transformation.le.inverseimplicationtransformation;

import java.util.Set;

import junit.framework.TestCase;

import org.omwg.logicalexpression.LogicalExpression;
import org.sti2.wsmo4j.factory.WsmlFactoryContainer;
import org.wsml.reasoner.transformation.le.LETestHelper;
import org.wsmo.wsml.ParserException;

public class SplitInvImplicationBodyRuleTest extends TestCase {

	protected SplitInvImplicationBodyRule rule;

	public SplitInvImplicationBodyRuleTest() {
		super();
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.rule = new SplitInvImplicationBodyRule(new WsmlFactoryContainer());
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
