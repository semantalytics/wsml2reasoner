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
package org.wsml.reasoner.builtin.iris;

import junit.framework.TestCase;

import org.deri.iris.api.basics.IAtom;
import org.omwg.logicalexpression.Constants;
import org.sti2.wsmo4j.factory.WsmlFactoryContainer;
import org.wsml.reasoner.Literal;
import org.wsml.reasoner.LiteralTestHelper;
import org.wsmo.factory.WsmoFactory;

public class IrisFacadeConvertTest extends TestCase {

	public void testConvertTermFromWsmo4jToIris() {

		final WsmoFactory WF = new WsmlFactoryContainer().getWsmoFactory();

		// [ 1997166 ] Anonymous IDs cause exception when using IRIS.
		// Anonymous IDs cause exception when using IRIS.
		// The use of anonymous IDs causes an IllegalArgumentException().
		// The problem seems to lie in IrisFacade.wsmoTermConverter().

		assertTrue(IrisStratifiedFacade.convertTermFromWsmo4jToIris(WF
				.createAnonymousID()) != null);
	}

	public void testLiteral2Atom() {
		try {
			IrisStratifiedFacade.literal2Atom(null, false);
			fail(); // should throw an exception
		} catch (Exception e) {
		}

	}

	public void testLiteral2AtomHead() {

		Literal wsmlLiteral = new Literal(true, Constants.EQUAL,
				LiteralTestHelper.createVariable("x"), LiteralTestHelper
						.createVariable("y"));

		// Literal expected = new Literal(true, Constants.EQUAL,
		// LiteralTestHelper
		// .createVariable("x"), LiteralTestHelper.createVariable("y"));

		assertEquals("EQUAL(?x, ?y)", IrisStratifiedFacade.literal2Atom(
				wsmlLiteral, true).toString());
	}
	
	public void testBuiltins() {
		String irins = "http://iris.sti-innsbruck.at/urn:";
		String ns = "http://www.wsmo.org/wsml/wsml-syntax#";
		Literal l = null;
		IAtom atom = null;
		
		// String
		l = LiteralTestHelper.createLiteral(true, ns +"String", irins + "a" );
		atom = IrisStratifiedFacade.literal2Atom(l, false);
		System.out.println(atom);
		assertTrue(atom.toString().contains(ns + "String"));
		
		// double
		l = LiteralTestHelper.createLiteral(true, ns +"Double", irins + "a" );
		atom = IrisStratifiedFacade.literal2Atom(l, false);
		System.out.println(atom);
		assertTrue(atom.toString().contains(ns + "Double"));
		
	}

}
