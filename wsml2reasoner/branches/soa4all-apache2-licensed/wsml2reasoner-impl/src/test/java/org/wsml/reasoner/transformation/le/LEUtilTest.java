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

import java.util.ArrayList;

import junit.framework.TestCase;

import org.omwg.logicalexpression.Conjunction;
import org.omwg.logicalexpression.Disjunction;
import org.omwg.logicalexpression.LogicalExpression;
import org.sti2.wsmo4j.factory.WsmlFactoryContainer;
import org.wsmo.factory.FactoryContainer;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.wsml.ParserException;

public class LEUtilTest extends TestCase {

	public LEUtilTest() {
		super();
	}

	protected void setUp() throws Exception {
		super.setUp();
	}


	public void testBuildNary() throws ParserException {

		FactoryContainer wsmoManager = new WsmlFactoryContainer();
		LogicalExpressionFactory leFactory = wsmoManager
				.getLogicalExpressionFactory();
		LogicalExpression in1 = LETestHelper
				.buildLE("_\"urn:a\"[_\"urn:c\" ofType _\"urn:d\"] subConceptOf _\"urn:b\" ");
		LogicalExpression in2 = LETestHelper
				.buildLE("_\"urn:a\"[_\"urn:c\" hasValue _\"urn:e\"] implies _\"urn:l\" ");
		ArrayList<LogicalExpression> list = new ArrayList<LogicalExpression>();
		list.add(in1);
		list.add(in2);

		Conjunction con = LEUtil.buildNaryConjunction(leFactory, list);
		assertEquals(
				con.getLeftOperand(),
				LETestHelper
						.buildLE("_\"urn:a\"[_\"urn:c\" ofType _\"urn:d\"] subConceptOf _\"urn:b\" "));
		assertEquals(
				"_\"urn:a\"[_\"urn:c\" hasValue _\"urn:e\"]\n implies \n_\"urn:l\".",
				con.getRightOperand().toString().trim());
		
		Disjunction dis = LEUtil.buildNaryDisjunction(leFactory, list);
		assertEquals(
				dis.getLeftOperand(),
				LETestHelper
						.buildLE("_\"urn:a\"[_\"urn:c\" ofType _\"urn:d\"] subConceptOf _\"urn:b\" "));
		assertEquals(
				("_\"urn:a\"[_\"urn:c\" hasValue _\"urn:e\"]\n implies \n_\"urn:l\". "),
				dis.getRightOperand().toString());
	
	}

}
