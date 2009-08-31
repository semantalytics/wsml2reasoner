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

import java.util.Set;

import junit.framework.TestCase;

import org.omwg.logicalexpression.LogicalExpression;
import org.sti2.wsmo4j.factory.FactoryImpl;
import org.wsml.reasoner.transformation.le.lloydtopor.LloydToporRules;
import org.wsmo.factory.Factory;
import org.wsmo.wsml.ParserException;

public class TopDownLESplitterTest extends TestCase {
	
	private TopDownLESplitter splitter;

	public TopDownLESplitterTest() {
		super();
	}

	protected void setUp() throws Exception {
		super.setUp();
		Factory wsmoManager = new FactoryImpl();
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
