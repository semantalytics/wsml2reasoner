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

package org.wsml.reasoner.transformation.le.negationpush;

import junit.framework.Test;
import junit.framework.TestSuite;


public class NegationPushTestsuite {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Test suite for negation push rules");
        //$JUnit-BEGIN$
        suite.addTestSuite(TestNegationPushRules.class);
        suite.addTestSuite(TestDoubleNegationRule.class);
        suite.addTestSuite(TestNegateConjunctionRule.class);
        suite.addTestSuite(TestNegateDisjunctionRule.class);
        //$JUnit-END$
        return suite;
    }
}
