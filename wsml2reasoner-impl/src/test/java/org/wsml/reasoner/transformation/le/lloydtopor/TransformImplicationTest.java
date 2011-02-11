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

public class TransformImplicationTest extends TestCase {

	private TransformImplication rule;
	// "A1 impliedBy A2\n\t=>\n A1 :- A2\n"

	public TransformImplicationTest() {
		super();
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.rule = new TransformImplication(new WsmlFactoryContainer());
	}


	public void testIsApplicable() throws ParserException {
		assertFalse(rule.isApplicable(LETestHelper.buildLE("_\"urn:a\"")));
		assertFalse(rule.isApplicable(LETestHelper
				.buildLE("_\"urn:a\" implies _\"urn:b\"")));
		assertFalse(rule.isApplicable(LETestHelper
				.buildLE("_\"urn:a\" or _\"urn:b\"")));
		assertFalse(rule
				.isApplicable(LETestHelper
						.buildLE("_\"urn:a\" and _\"urn:b\" and _\"urn:c\" or _\"urn:d\"")));
		assertFalse(rule
				.isApplicable(LETestHelper
						.buildLE("_\"urn:a\" implies (_\"urn:b\" impliedBy _\"urn:c\") ")));
		assertFalse(rule
				.isApplicable(LETestHelper
						.buildLE("(_\"urn:a\" or _\"urn:b\" ) and (_\"urn:c\" impliedBy  _\"urn:d\")")));
		
		assertFalse(rule
				.isApplicable(LETestHelper
						.buildLE("_\"urn:c\" impliedBy _\"urn:b\" :- _\"urn:a\"")));
		assertFalse(rule
				.isApplicable(LETestHelper
						.buildLE(" _\"urn:a\" :- _\"urn:c\" impliedBy _\"urn:b\" ")));

		assertTrue(rule.isApplicable(LETestHelper
				.buildLE("_\"urn:a\" impliedBy _\"urn:b\"")));
		assertTrue(rule.isApplicable(LETestHelper
				.buildLE("_\"urn:a\" and _\"urn:b\" impliedBy _\"urn:c\" ")));
		assertTrue(rule
				.isApplicable(LETestHelper
						.buildLE("_\"urn:a\" impliedBy _\"urn:b\" impliedBy  _\"urn:d\" or _\"urn:e\" ")));
		

	}

	public void testApply() throws ParserException {
		LogicalExpression in = LETestHelper
				.buildLE("_\"urn:a\" impliedBy _\"urn:b\"");
		Set<LogicalExpression> result = rule.apply(in);

		assertTrue(!result.toString().contains("_#"));
		assertEquals(1, result.size());

		assertTrue(result.contains(LETestHelper
				.buildLE("_\"urn:a\" :- _\"urn:b\" ")));

		  
		 in = LETestHelper.buildLE("_\"urn:a\" and _\"urn:b\" impliedBy _\"urn:c\" " );
		 result = rule.apply(in);
		        
		 assertTrue(!result.toString().contains("_#"));
		 assertEquals(1, result.size());
		        
		 assertTrue(result.contains(LETestHelper.buildLE(" _\"urn:a\" and _\"urn:b\" :- _\"urn:c\" ")));
		 
		 
		 in = LETestHelper.buildLE("_\"urn:a\" impliedBy _\"urn:b\" impliedBy  _\"urn:d\" or _\"urn:e\" ");
		 result = rule.apply(in);
		        
		 assertTrue(!result.toString().contains("_#"));
		 assertEquals(1, result.size());
		 assertTrue(result.contains(LETestHelper.buildLE("_\"urn:a\" impliedBy _\"urn:b\" :- _\"urn:d\" or _\"urn:e\"" )));
		 
	}
}
