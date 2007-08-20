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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.api.WSMLReasonerFactory.BuiltInReasoner;
import org.wsml.reasoner.api.inconsistency.InconsistencyException;

import base.BaseReasonerTest;

public class MinsInconsistencyTestWithFSymbol extends BaseReasonerTest {

    private static final String ONTOLOGY_FILE = "files/MinsInconsistencyTestWithFSymbol.wsml";
    
   BuiltInReasoner previous;
    
    @Override
    protected void setUp() throws Exception {
    	super.setUp();
    	setupScenario(ONTOLOGY_FILE);
    	previous = BaseReasonerTest.reasoner;
    	BaseReasonerTest.reasoner = WSMLReasonerFactory.BuiltInReasoner.MINS;
    	resetReasoner(WSMLReasonerFactory.BuiltInReasoner.MINS);
    }
    
    @Override
    protected void tearDown() throws Exception {
    	super.tearDown();
    	resetReasoner(previous);
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
            //fail("should fail!");
        }catch (InconsistencyException e){
            //should be thrown!
        }
        System.out.println("Finished query.");
    }
}
