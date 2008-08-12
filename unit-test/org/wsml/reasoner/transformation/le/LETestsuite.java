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

package org.wsml.reasoner.transformation.le;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.wsml.reasoner.transformation.le.disjunctionpull.DisjunctionPullTestsuite;
import org.wsml.reasoner.transformation.le.foldecomposition.FOLdecompositionTestsuite;
import org.wsml.reasoner.transformation.le.implicationreduction.ImplicationreductionTestsuite;
import org.wsml.reasoner.transformation.le.inverseimplicationreduction.InverseImplicationReductionTestsuite;
import org.wsml.reasoner.transformation.le.lloydtopor.LloydToporRulesTestSuite;
import org.wsml.reasoner.transformation.le.negationpush.NegationPushTestsuite;


public class LETestsuite {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Test suite for LE rules");
        //$JUnit-BEGIN$
        suite.addTest(DisjunctionPullTestsuite.suite());
        suite.addTest(NegationPushTestsuite.suite());
        suite.addTest(ImplicationreductionTestsuite.suite());
        suite.addTest(InverseImplicationReductionTestsuite.suite());
        suite.addTest(FOLdecompositionTestsuite.suite());
        suite.addTest(LloydToporRulesTestSuite.suite());
        //$JUnit-END$
        return suite;
    }
}
