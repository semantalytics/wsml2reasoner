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
package reasoner.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Variable;

import test.BaseReasonerTest;

public class SimpleGraph extends BaseReasonerTest {

    private static final String NS = "http://www.example.org/example/#";

    private static final String ONTOLOGY_FILE = "reasoner/core/simple-graph.wsml";

    public static void main(String[] args) {
        junit.textui.TestRunner.run(SimpleGraph.suite());
    }

    public static Test suite() {
        Test test = new junit.extensions.TestSetup(new TestSuite(
                SimpleGraph.class)) {
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
     * MINS BUG HERE! if one rewrites the query to
     * path(?n,?y) and  ?y=f
     * @throws Exception
     */
    public void testElementsConnectedWithF() throws Exception {
        String query = "path(?n,f)";
        Set<Map<Variable, Term>> expected = new HashSet<Map<Variable, Term>>();
        Map<Variable, Term> binding = new HashMap<Variable, Term>();
        binding.put(leFactory.createVariable("n"), wsmoFactory.createIRI(NS
                + "a"));
        expected.add(binding);
        binding = new HashMap<Variable, Term>();
        binding.put(leFactory.createVariable("n"), wsmoFactory.createIRI(NS
                + "b"));
        expected.add(binding);
        binding = new HashMap<Variable, Term>();
        binding.put(leFactory.createVariable("n"), wsmoFactory.createIRI(NS
                + "c"));
        expected.add(binding);
        binding = new HashMap<Variable, Term>();
        binding.put(leFactory.createVariable("n"), wsmoFactory.createIRI(NS
                + "f"));
        expected.add(binding);
        binding = new HashMap<Variable, Term>();
        binding.put(leFactory.createVariable("n"), wsmoFactory.createIRI(NS
                + "g"));
        expected.add(binding);
        binding = new HashMap<Variable, Term>();
        binding.put(leFactory.createVariable("n"), wsmoFactory.createIRI(NS
                + "h"));
        expected.add(binding);
        performQuery(query, expected);
        System.out.println("Finished query.");

    }

    public void testConnectedPairs() throws Exception {
        String query = "path(?n1,?n2)";
        Set<Map<Variable, Term>> expected = new HashSet<Map<Variable, Term>>();
        addTwoVariableResult(expected, "a", "a"); 
        addTwoVariableResult(expected, "a", "b");
        addTwoVariableResult(expected, "a", "c"); 
        addTwoVariableResult(expected, "a", "d"); 
        addTwoVariableResult(expected, "a", "e"); 
        addTwoVariableResult(expected, "a", "f"); 
        addTwoVariableResult(expected, "a", "g"); 
        addTwoVariableResult(expected, "a", "h"); 
        addTwoVariableResult(expected, "a", "i"); 
        addTwoVariableResult(expected, "b", "a"); 
        addTwoVariableResult(expected, "b", "b");
        addTwoVariableResult(expected, "b", "c");
        addTwoVariableResult(expected, "b", "d"); 
        addTwoVariableResult(expected, "b", "e"); 
        addTwoVariableResult(expected, "b", "f"); 
        addTwoVariableResult(expected, "b", "g"); 
        addTwoVariableResult(expected, "b", "h"); 
        addTwoVariableResult(expected, "b", "i"); 
        addTwoVariableResult(expected, "c", "a"); 
        addTwoVariableResult(expected, "c", "b");
        addTwoVariableResult(expected, "c", "c");
        addTwoVariableResult(expected, "c", "d"); 
        addTwoVariableResult(expected, "c", "e"); 
        addTwoVariableResult(expected, "c", "f"); 
        addTwoVariableResult(expected, "c", "g"); 
        addTwoVariableResult(expected, "c", "h"); 
        addTwoVariableResult(expected, "c", "i"); 
        addTwoVariableResult(expected, "f", "f"); 
        addTwoVariableResult(expected, "f", "g"); 
        addTwoVariableResult(expected, "f", "h"); 
        addTwoVariableResult(expected, "f", "i");
        addTwoVariableResult(expected, "g", "f"); 
        addTwoVariableResult(expected, "g", "g"); 
        addTwoVariableResult(expected, "g", "h"); 
        addTwoVariableResult(expected, "g", "i");
        addTwoVariableResult(expected, "h", "f"); 
        addTwoVariableResult(expected, "h", "g"); 
        addTwoVariableResult(expected, "h", "h"); 
        addTwoVariableResult(expected, "h", "i"); 
        performQuery(query, expected);
        System.out.println("Finished query.");

    }

    private void addTwoVariableResult(Set<Map<Variable, Term>> expected, String v1value, String v2value) {
        Map<Variable, Term> binding = new HashMap<Variable, Term>();
        binding.put(leFactory.createVariable("n1"), wsmoFactory.createIRI(NS
                + v1value));
        binding.put(leFactory.createVariable("n2"), wsmoFactory.createIRI(NS
                + v2value));
        expected.add(binding);
    }

    public void testScElementsOnADirecteCircleWithF() throws Exception {

        String query = "scElement(?n) and path(?n,f) and path(f,?n)";
        Set<Map<Variable, Term>> expected = new HashSet<Map<Variable, Term>>();
        Map<Variable, Term> binding = new HashMap<Variable, Term>();
        binding.put(leFactory.createVariable("n"), wsmoFactory.createIRI(NS
                + "f"));
        expected.add(binding);
        binding = new HashMap<Variable, Term>();
        binding.put(leFactory.createVariable("n"), wsmoFactory.createIRI(NS
                + "g"));
        expected.add(binding);
        binding = new HashMap<Variable, Term>();
        binding.put(leFactory.createVariable("n"), wsmoFactory.createIRI(NS
                + "h"));
        expected.add(binding);
        performQuery(query, expected);
        System.out.println("Finished query.");
    }

}
