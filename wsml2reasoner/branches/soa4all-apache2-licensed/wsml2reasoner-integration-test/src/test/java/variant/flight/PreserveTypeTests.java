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
package variant.flight;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.deri.wsmo4j.io.parser.wsml.WsmlLogicalExpressionParser;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.LogicalExpressionParser;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Variable;
import org.omwg.ontology.XmlSchemaDataType;
import org.wsml.reasoner.api.LPReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.api.WSMLReasonerFactory.BuiltInReasoner;

import base.BaseReasonerTest;

import com.ontotext.wsmo4j.ontology.SimpleDataValueImpl;

/**
 * @author grahen
 *
 */

public class PreserveTypeTests extends BaseReasonerTest {

    private static final String ONTOLOGY_FILE = "datatypes.wsml";
    
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
                dataFactory.createInteger("1"));
        binding.put(leFactory.createVariable("y"), 
                dataFactory.createDecimal("1.0"));
        expected.add(binding);
        performQuery(query, expected);
        System.out.println("Finished query.");
    }
    
    
    //This test ensures that decimal equations result in decimals
    public void preserveTypeAfterOperationWithConcepts() throws Exception {
        String query = "?x[value hasValue ?y] memberOf Miles";
        LogicalExpressionParser leParser = new WsmlLogicalExpressionParser(o);
        LogicalExpression qExpression = leParser.parse(query);
        logExprSerializer.serialize(qExpression);
        
        Set<Map<Variable, Term>> result = ((LPReasoner) wsmlReasoner).executeQuery(qExpression);
        
        for (Map<Variable, Term> binding : result) {
        	SimpleDataValueImpl shouldBeDecimal = (SimpleDataValueImpl) 
        		binding.get(leFactory.createVariable("y"));	
        	assertTrue(shouldBeDecimal.getType().toString().equals(XmlSchemaDataType.XSD_DECIMAL));
        }
        System.out.println("Finished query.");
    }
    
    public void preserveTypeAfterOperationwithPredicates() throws Exception {

        String query = "test2(?x, mi)";
        Set<Map<Variable, Term>> expected = new HashSet<Map<Variable, Term>>();
        Map<Variable, Term> binding = new HashMap<Variable, Term>();
        binding.put(leFactory.createVariable("x"), 
                dataFactory.createDecimal("7.5"));
        expected.add(binding);
        performQuery(query, expected);
        System.out.println("Finished query.");
    }    
    
    public void testFlightReasoners() throws Exception{
    	resetReasoner(WSMLReasonerFactory.BuiltInReasoner.IRIS_STRATIFIED);
    	preserveType();
    	preserveTypeAfterOperationWithConcepts();
    	preserveTypeAfterOperationwithPredicates();
    }
    
}
