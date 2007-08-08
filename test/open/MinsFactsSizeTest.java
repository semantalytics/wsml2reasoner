/**
 * WSML Reasoner Implementation.
 *
 * Copyright (c) 2005, FZI, Germany.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * You should have received a copy of the GNU Lesser General Public License along
 * with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 */
package open;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.wsml.reasoner.api.WSMLReasonerFactory;

import base.BaseReasonerTest;

public class MinsFactsSizeTest extends BaseReasonerTest {

    private static final String ONTOLOGY_FILE = "files/cardinality_1_max-00250-ontology.wsml";

    public static void main(String[] args) {
        junit.textui.TestRunner.run(MinsFactsSizeTest.suite());
    }

    public static Test suite() {
        Test test = new junit.extensions.TestSetup(new TestSuite(
        		MinsFactsSizeTest.class)) {
        };
        return test;
    }

    /**
     * This test does pass is meant to show an ArrayIndexOutOfBoundsError in 
     * Mins, at registration time.
     * 
     * The problem can be eliminated temporarily by changed in the increment 
     * value in the Mins class DB from 200 to 3000 or more. This change does 
     * not fix the bug though!
     * 
     * @throws Exception
     */
    public void testMinsFactsSize() throws Exception {
    	
    	BaseReasonerTest.reasoner = WSMLReasonerFactory.BuiltInReasoner.MINS;   	
    	setupScenario(ONTOLOGY_FILE);
    }
	
}
