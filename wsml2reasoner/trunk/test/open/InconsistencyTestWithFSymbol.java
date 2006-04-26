/**
 * WSML Reasoner Implementation.
 *
 * Copyright (c) 2006, University of Innsbruck Austria
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

import java.util.*;

import junit.framework.*;

import org.omwg.logicalexpression.terms.*;
import org.omwg.ontology.*;
import org.wsml.reasoner.api.inconsistency.*;

import test.*;

public class InconsistencyTestWithFSymbol extends BaseReasonerTest {
    private static final String NS = "urn:functionsymbol#";

    private static final String ONTOLOGY_FILE = "open/InconsistencyTestWithFSymbol.wsml";
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(InconsistencyTestWithFSymbol.suite());
    }

    public static Test suite() {
        Test test = new junit.extensions.TestSetup(new TestSuite(
                InconsistencyTestWithFSymbol.class)) {
            protected void setUp() throws Exception {
                setupScenario(ONTOLOGY_FILE);
            }

            protected void tearDown() throws Exception {
                System.out.println("Finished!");
            }
        };
        return test;
    }
    
    /**
     * fails because of design of API
     * @throws Exception
     */
    public void testInconsistency() throws Exception {
        String query = "?x memberOf ?y";
        Set<Map<Variable, Term>> expected = new HashSet<Map<Variable, Term>>();
        try{
            performQuery(query, expected);
            fail("should fail!");
        }catch (InconsistencyException e){
            //should be thrown!
        }
        System.out.println("Finished query.");
    }
}
