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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Variable;

import base.BaseReasonerTest;

public class SimpleGraph2 extends BaseReasonerTest {

    private static final String NS = "http://www.example.org/example/#";

    private static final String ONTOLOGY_FILE = "files/simple-graph2.wsml";

    public static void main(String[] args) {
        junit.textui.TestRunner.run(SimpleGraph2.suite());
    }

    public static Test suite() {
        Test test = new junit.extensions.TestSetup(new TestSuite(
                SimpleGraph2.class)) {
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
     * 
     * See comments in simple-graph2.wsml file
     * 
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

    /**
     * IRIS BUG HERE! Only returns 2 vertices instead of 3
     * 
     * @throws Exception
     */
    public void testScElementsOnADirecteCircleWithF() throws Exception {

        // composedRule(?n,?f) impliedBy scElement(?n) and path(?n,?f) and path(?f,?n).
        String query = "composedRule(?n,f)";

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