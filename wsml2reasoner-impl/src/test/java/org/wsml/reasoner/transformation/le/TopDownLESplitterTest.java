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

package org.wsml.reasoner.transformation.le;

import java.util.Set;

import junit.framework.TestCase;

import org.omwg.logicalexpression.LogicalExpression;
import org.sti2.wsmo4j.factory.WsmlFactoryContainer;
import org.wsml.reasoner.transformation.le.lloydtopor.LloydToporRules;
import org.wsmo.factory.FactoryContainer;
import org.wsmo.wsml.ParserException;

public class TopDownLESplitterTest extends TestCase {
	
	private TopDownLESplitter splitter;

	public TopDownLESplitterTest() {
		super();
	}

	protected void setUp() throws Exception {
		super.setUp();
		FactoryContainer wsmoManager = new WsmlFactoryContainer();
		LloydToporRules lloydToporRules = new LloydToporRules(wsmoManager);
		splitter = new TopDownLESplitter(lloydToporRules.getRules());
	}
	
	public void testTransform() throws ParserException {	
		
		LogicalExpression in = LETestHelper.buildLE(" _\"urn:a\" and _\"urn:b\" and _\"urn:c\" and _\"urn:f\" :- _\"urn:d\"");
		Set<LogicalExpression> out = splitter.transform(in);
		assertEquals(out.size(),4);
		assertTrue(out.contains(LETestHelper.buildLE(("_\"urn:f\" :- _\"urn:d\""))));
		
		in = LETestHelper.buildLE(" !- ( _\"urn:a\" and _\"urn:b\" ) or _\"urn:c\" or _\"urn:d\"");
		out = splitter.transform(in);
		System.out.println(out);
		assertEquals(out.size(),3);
		assertTrue(out.contains(LETestHelper.buildLE("!- _\"urn:d\".")));
		assertTrue(out.contains(LETestHelper.buildLE("!- _\"urn:c\".")));
		assertTrue(out.contains(LETestHelper.buildLE("!- _\"urn:a\" and _\"urn:b\"")));
		
		
	}
	
	
	
	
	
	
	
	
	
	
	

}
