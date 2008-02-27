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
package variant.flight;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Variable;
import org.omwg.ontology.WsmlDataType;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.api.WSMLReasonerFactory.BuiltInReasoner;
import org.wsmo.common.IRI;

import base.BaseReasonerTest;

import com.ontotext.wsmo4j.ontology.SimpleDataValueImpl;

/**
 * @author grahen
 *
 */

public class PreserveTypeTests extends BaseReasonerTest {

    private static final String ONTOLOGY_FILE = "files/datatypes.wsml";
    
   BuiltInReasoner previous;
    
    @Override
    protected void setUp() throws Exception {
    	super.setUp();
    	setupScenario(ONTOLOGY_FILE);
    	previous = BaseReasonerTest.reasoner;
    }
    
    @Override
    protected void tearDown() throws Exception {
    	super.tearDown();
    	resetReasoner(previous);
    }
    
    public void preserveType() throws Exception {

        String query = "tuple1(?x,?y)";
        Set<Map<Variable, Term>> expected = new HashSet<Map<Variable, Term>>();
        Map<Variable, Term> binding = new HashMap<Variable, Term>();
        binding.put(leFactory.createVariable("x"), 
                dataFactory.createWsmlInteger("1"));
        binding.put(leFactory.createVariable("y"), 
                dataFactory.createWsmlDecimal("1.0"));
        expected.add(binding);
        performQuery(query, expected);
        System.out.println("Finished query.");
    }
    
    
    //This test ensures that decimal equations result in decimals
    public void preserveTypeAfterOperationWithConcepts() throws Exception {
        String query = "?x[value hasValue ?y] memberOf Miles";
        LogicalExpression qExpression = leFactory.createLogicalExpression(
                query, o);
        logExprSerializer.serialize(qExpression);
        
        Set<Map<Variable, Term>> result = wsmlReasoner.executeQuery(qExpression);
        
        for (Map<Variable, Term> binding : result) {
        	SimpleDataValueImpl shouldBeDecimal = (SimpleDataValueImpl) 
        		binding.get(leFactory.createVariable("y"));	
        	assertTrue(shouldBeDecimal.getType().toString().equals(WsmlDataType.WSML_DECIMAL));
        }
        System.out.println("Finished query.");
    }
    
    public void preserveTypeAfterOperationwithPredicates() throws Exception {

        String query = "test2(?x, mi)";
        Set<Map<Variable, Term>> expected = new HashSet<Map<Variable, Term>>();
        Map<Variable, Term> binding = new HashMap<Variable, Term>();
        binding.put(leFactory.createVariable("x"), 
                dataFactory.createWsmlDecimal("7.5"));
        expected.add(binding);
        performQuery(query, expected);
        System.out.println("Finished query.");
    }    
    
    public void testFlightReasoners() throws Exception{
    	resetReasoner(WSMLReasonerFactory.BuiltInReasoner.IRIS);
    	preserveType();
    	preserveTypeAfterOperationWithConcepts();
    	preserveTypeAfterOperationwithPredicates();
    	
    	resetReasoner(WSMLReasonerFactory.BuiltInReasoner.MINS);
    	preserveType();
    	preserveTypeAfterOperationWithConcepts();
    	//MINS does not pass
    	//preserveTypeAfterOperationwithPredicates();

    	if (exists("org.wsml.reasoner.builtin.kaon2.Kaon2Facade")) { 
    		resetReasoner(WSMLReasonerFactory.BuiltInReasoner.KAON2);
    		preserveType();
    		preserveTypeAfterOperationWithConcepts();
    		preserveTypeAfterOperationwithPredicates();
    	}
    }
    
}
