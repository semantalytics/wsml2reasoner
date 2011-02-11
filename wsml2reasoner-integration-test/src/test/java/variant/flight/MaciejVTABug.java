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

import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.api.WSMLReasonerFactory.BuiltInReasoner;

import base.BaseReasonerTest;

public class MaciejVTABug extends BaseReasonerTest {

    private static final String NS = "http://www.wsmo.org/TR/d13/d13.7/ontologies/VTAServiceOntology#";

    private static final String ONTOLOGY_FILE = "VTAServiceOntology.wsml";
    
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
    
    public void attributeValueQuery() throws Exception {
        String query = "?x[sourceLocBool hasValue t]";
        Set<Map<Variable, Term>> expected = new HashSet<Map<Variable, Term>>();
        Map<Variable, Term> binding = new HashMap<Variable, Term>();
        binding.put(leFactory.createVariable("x"), wsmoFactory.createIRI(NS
                + "galway_station"));
        expected.add(binding);
        performQuery(query, expected);
        System.out.println("Finished query.");
    }

    public void memberOfQuery() throws Exception {
        String query = "?x memberOf station";
        Set<Map<Variable, Term>> expected = new HashSet<Map<Variable, Term>>();
        Map<Variable, Term> binding = new HashMap<Variable, Term>();
        binding.put(leFactory.createVariable("x"), wsmoFactory.createIRI(NS
                + "galway_station"));
        expected.add(binding);
        binding = new HashMap<Variable, Term>();
        binding.put(leFactory.createVariable("x"), wsmoFactory.createIRI(NS
                + "dublin_station"));
        expected.add(binding);
        performQuery(query, expected);
        System.out.println("Finished query.");
    }
    
    public void testFlightReasoners() throws Exception{
    	resetReasoner(WSMLReasonerFactory.BuiltInReasoner.IRIS_STRATIFIED);
    	attributeValueQuery();
    	memberOfQuery();
    }

}
