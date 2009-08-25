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

package org.wsml.reasoner.transformation.le;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.omwg.logicalexpression.Conjunction;
import org.omwg.logicalexpression.Disjunction;
import org.omwg.logicalexpression.LogicalExpression;
import org.wsml.reasoner.impl.WSMO4JManager;
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

		WSMO4JManager wsmoManager = new WSMO4JManager();
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
				con.getRightOperand().toString(),
				("_\"urn:a\"[_\"urn:c\" hasValue _\"urn:e\"] implies _\"urn:l\" ."));
		
		Disjunction dis = LEUtil.buildNaryDisjunction(leFactory, list);
		assertEquals(
				dis.getLeftOperand(),
				LETestHelper
						.buildLE("_\"urn:a\"[_\"urn:c\" ofType _\"urn:d\"] subConceptOf _\"urn:b\" "));
		assertEquals(
				dis.getRightOperand().toString(),
				("_\"urn:a\"[_\"urn:c\" hasValue _\"urn:e\"] implies _\"urn:l\" ."));
	
	}

}
