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

package org.wsml.reasoner.transformation;

import junit.framework.TestCase;

import org.omwg.logicalexpression.Atom;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.terms.Term;
import org.sti2.wsmo4j.factory.WsmlFactoryContainer;
import org.wsml.reasoner.transformation.le.LETestHelper;
import org.wsmo.common.Identifier;
import org.wsmo.factory.FactoryContainer;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.ParserException;

public class AnonymousIdTranslatorTest extends TestCase {

	private AnonymousIdTranslator translator;
	
	public AnonymousIdTranslatorTest() {
		
	}

	protected void setUp() throws Exception {
		super.setUp();
		FactoryContainer factory = new WsmlFactoryContainer();
		WsmoFactory wsmoFactory = factory.getWsmoFactory();
	    translator = new AnonymousIdTranslator(wsmoFactory);
		
	}

	public void testTranslateTermNumberedAnonymous() throws ParserException {
		
		LogicalExpression in = LETestHelper.buildLE("_\"urn:a\"(_#)");
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
		assertEquals(1,i);
       
	}
	
	public void testTranslateTermAnonymousID() throws ParserException {
		
		
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
		
	}
	
	public void testTranslateTermNumberedAnonymousID() throws ParserException {
		
		LogicalExpression in = LETestHelper.buildLE("_\"urn:a\"(_#1, _#2, _#3)");
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
       
	}

}
