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

package org.wsml.reasoner.transformation;

import org.omwg.logicalexpression.Atom;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.terms.Term;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsml.reasoner.transformation.le.LETestHelper;
import org.wsmo.common.Identifier;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.ParserException;



import junit.framework.TestCase;

public class AnonymousIdTranslatorTest extends TestCase {

	private AnonymousIdTranslator translator;
	
	public AnonymousIdTranslatorTest() {
		
	}

	protected void setUp() throws Exception {
		super.setUp();
		WSMO4JManager wsmoManager = new WSMO4JManager();
		WsmoFactory wsmoFactory = wsmoManager.getWSMOFactory();
	    translator = new AnonymousIdTranslator(wsmoFactory);
		
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		translator = null;
	}
	
	public void testTranslateTerm() throws ParserException {
		
		
		LogicalExpression in = LETestHelper.buildLE("_\"urn:a\"(_#, _#, _#)");
		
	
		Atom atom = (Atom) in;
		int i = 0;
		for (i = 0; i < atom.getArity(); i++) {
            Term term = atom.getParameter(i);
            if (term instanceof Identifier) {
                term = translator.translate(term);
                assertTrue(!term.toString().contains("_#"));
                assertEquals(2, term.toString().split(AnonymousIdUtils.ANONYMOUS_PREFIX).length);
                assertTrue(term.toString().startsWith(AnonymousIdUtils.ANONYMOUS_PREFIX));
            }
        }
		assertEquals(i,3);
		
		in = LETestHelper.buildLE("_\"urn:a\"(_#1, _#2, _#3)");
		atom = (Atom) in;
		for (i = 0; i < atom.getArity(); i++) {
            Term term = atom.getParameter(i);
            if (term instanceof Identifier) {
                term = translator.translate(term);
                assertTrue(!term.toString().contains("_#"));
                assertEquals(2, term.toString().split(AnonymousIdUtils.ANONYMOUS_PREFIX).length);
                assertTrue(term.toString().startsWith(AnonymousIdUtils.ANONYMOUS_PREFIX));
            }
        }
		assertEquals(i,3);
       
	}

}
