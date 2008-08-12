/*
 * Copyright (C) 2006 Digital Enterprise Research Insitute (DERI) Innsbruck
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.wsml.reasoner.transformation.le.disjunctionpull;

import junit.framework.TestCase;

import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsmo.wsml.ParserException;


public class TestDisjunctionPullRules extends TestCase {

    private DisjunctionPullRules rules;
    
    public TestDisjunctionPullRules() {
        super();
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        this.rules = new DisjunctionPullRules(new WSMO4JManager());
    }
    
    protected void tearDown() throws Exception {
        super.tearDown();
        this.rules = null;
    }
    
    public void testGetRules() throws ParserException {
        assertEquals(1, rules.getRules().size());
    }
}
