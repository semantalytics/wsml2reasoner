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
package org.wsml.reasoner.transformation.le.negationpush;

import junit.framework.TestCase;

import org.sti2.wsmo4j.factory.WsmlFactoryContainer;
import org.wsmo.wsml.ParserException;


public class NegationPushRulesTest extends TestCase {

    private NegationPushRules rules;
    
    public NegationPushRulesTest() {
        super();
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        this.rules = new NegationPushRules(new WsmlFactoryContainer());
    }
    
    
    public void testGetRules() throws ParserException {
        assertEquals(3, rules.getRules().size());
    }
}