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

/**
 * Lots of problems....
 * @author grahen
 *
 */

public class BuiltInTest extends BaseReasonerTest {
    private static final String NS = "http://www.example.org#datatypes/";

    private static final String ONTOLOGY_FILE = "files/datatypes.wsml";
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(BuiltInTest.suite());
    }

    public static Test suite() {
        Test test = new junit.extensions.TestSetup(new TestSuite(
                BuiltInTest.class)) {
            protected void setUp() throws Exception {
                setupScenario(ONTOLOGY_FILE);
            }

            protected void tearDown() throws Exception {
                System.out.println("Finished!");
            }
        };
        return test;
    }
    
    public void testPreserveTypeAfterOperationWithConcepts() throws Exception {
        String query = "?x[value hasValue ?y] memberOf Miles";
        Set<Map<Variable, Term>> expected = new HashSet<Map<Variable, Term>>();
        Map<Variable, Term> binding = new HashMap<Variable, Term>();
        binding.put(leFactory.createVariable("x"), 
                wsmoFactory.createIRI(NS+"miles"));
        binding.put(leFactory.createVariable("y"), 
                dataFactory.createWsmlDecimal("10.0"));
        expected.add(binding);
        performQuery(query, expected);
        System.out.println("Finished query.");
    }

    /*
     * BELONGS TO TODO....
     * This test fails because 
     * there is somewhere a bug that does not distinguish in the
     * translation that test2 of arity 1 is a different predicate then test2
     * of arity 2
     * 
     * Test should perhaps be changed ~ shouldn't the expected result be 0?
     */
    public void testPreserveTypeAfterOperation2() throws Exception {

        String query = "test2(?y)";
        Set<Map<Variable, Term>> expected = new HashSet<Map<Variable, Term>>();
        Map<Variable, Term> binding = new HashMap<Variable, Term>();
        binding.put(leFactory.createVariable("x"), 
                dataFactory.createWsmlInteger("2"));
        expected.add(binding);
        performQuery(query, expected);
        System.out.println("Finished query.");
    }

    
    public void testPreserveType() throws Exception {

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
    /**
     * this does not work probably becuase mins does not evaluate the
     * Buildin for some reason....
     * @throws Exception
     * 
     * Shouldn't the result
     * 
     * 
     */
    public void testPreserveTypeAfterOperationwithPredicates() throws Exception {

        String query = "test2(?x,?y)";
        Set<Map<Variable, Term>> expected = new HashSet<Map<Variable, Term>>();
        Map<Variable, Term> binding = new HashMap<Variable, Term>();
        binding.put(leFactory.createVariable("x"), 
                dataFactory.createWsmlInteger("2"));
        expected.add(binding);
        performQuery(query, expected);
        System.out.println("Finished query.");
    }    
}
