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
package engine.mins;

import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.api.WSMLReasonerFactory.BuiltInReasoner;

import base.BaseReasonerTest;

public class MinsFactsSizeTest extends BaseReasonerTest {

    private static final String ONTOLOGY_FILE = "files/cardinality_1_max-00250-ontology.wsml";
    
    BuiltInReasoner previous;
     
     @Override
     protected void setUp() throws Exception {
     	super.setUp();
     	setupScenario(ONTOLOGY_FILE);
     	previous = BaseReasonerTest.reasoner;
     	resetReasoner(WSMLReasonerFactory.BuiltInReasoner.MINS);
     }
     
     @Override
     protected void tearDown() throws Exception {
     	super.tearDown();
     	resetReasoner(previous);
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
    	setupScenario(ONTOLOGY_FILE);
    }
	
}
