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
package reasoner;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Variable;

import test.BaseReasonerTest;

public class TestKaon2WSMLFlightReasoner extends BaseReasonerTest {

    private static final String NS = "urn:fzi:lordoftherings#";

    private static final String ONTOLOGY_FILE = "examples/lordOfTheRings.wsml";

    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestKaon2WSMLFlightReasoner.suite());
    }

    public static Test suite() {
        Test test = new junit.extensions.TestSetup(new TestSuite(
                TestKaon2WSMLFlightReasoner.class)) {
            protected void setUp() throws Exception {
                setupScenario(ONTOLOGY_FILE);
             }

            protected void tearDown() throws Exception {
                System.out.println("Finished!");
            }
        };
        return test;
    }
    
    public void testArwenIsFemale() throws Exception {
        String query = "?x memberOf Female";
        Set<Map<Variable, Term>> expected = new HashSet<Map<Variable, Term>>();
        Map<Variable, Term> binding = new HashMap<Variable, Term>();
        binding.put(wsmoFactory.createVariable("x"), wsmoFactory.createIRI(NS + "Arwen"));
        expected.add(binding);
        performQuery(query, expected);
        System.out.println("Finished query.");
        
    }

    public void testAragornLovesElf() throws Exception {
        String query = "?x[loves hasValue ?y] and ?y memberOf Elf";
        Set<Map<Variable, Term>> expected = new HashSet<Map<Variable, Term>>();
        Map<Variable, Term> binding = new HashMap<Variable, Term>();
        binding.put(wsmoFactory.createVariable("x"), wsmoFactory.createIRI(NS + "Aragorn"));
        binding.put(wsmoFactory.createVariable("y"), wsmoFactory.createIRI(NS + "Arwen"));
        expected.add(binding);
        performQuery(query, expected);
        System.out.println("Finished query.");
        
    }
    
    public void testAragornLovesFemale() throws Exception {
        String query = "?x[loves hasValue ?y] and ?y memberOf Female";
        Set<Map<Variable, Term>> expected = new HashSet<Map<Variable, Term>>();
        Map<Variable, Term> binding = new HashMap<Variable, Term>();
        binding.put(wsmoFactory.createVariable("x"), wsmoFactory.createIRI(NS + "Aragorn"));
        binding.put(wsmoFactory.createVariable("y"), wsmoFactory.createIRI(NS + "Arwen"));
        expected.add(binding);
        performQuery(query, expected);
        System.out.println("Finished query.");
        
    }

    public void testAragornIsElendilsHeir() throws Exception {
        String query = "Aragorn[hasAncestor hasValue Elendil]";
        Set<Map<Variable, Term>> expected = new HashSet<Map<Variable, Term>>();
        Map<Variable, Term> binding = new HashMap<Variable, Term>();
        expected.add(binding);
        performQuery(query, expected);
        System.out.println("Finished query.");
        
    }
    
    public void testNoMaleFemaleTogether() throws Exception {
        String query = "?x memberOf Male and ?x memberOf Female";
        Set<Map<Variable, Term>> expected = new HashSet<Map<Variable, Term>>();
        performQuery(query, expected);
        System.out.println("Finished query.");
        
    }
    
    public void testMales() throws Exception {
        String query = "?x memberOf Male";
        Set<Map<Variable, Term>> expected = new HashSet<Map<Variable, Term>>();
        Map<Variable, Term> binding = new HashMap<Variable, Term>();
        binding.put(wsmoFactory.createVariable("x"), wsmoFactory.createIRI(NS + "Aragorn"));
        expected.add(binding);
        binding = new HashMap<Variable, Term>();
        binding.put(wsmoFactory.createVariable("x"), wsmoFactory.createIRI(NS + "Elendil"));
        expected.add(binding);
        binding = new HashMap<Variable, Term>();
        binding.put(wsmoFactory.createVariable("x"), wsmoFactory.createIRI(NS + "Arathorn"));
        expected.add(binding);
        performQuery(query, expected);
        System.out.println("Finished query.");
    }
    
    public void testHigherThan7Feet() throws Exception {
        String query = "?x[heightInFeet hasValue ?v] and ?v > 7.0";
        Set<Map<Variable, Term>> expected = new HashSet<Map<Variable, Term>>();
        Map<Variable, Term> binding = new HashMap<Variable, Term>();
        binding.put(wsmoFactory.createVariable("x"), wsmoFactory.createIRI(NS + "Aragorn"));
        binding.put(wsmoFactory.createVariable("v"), dataFactory.createWsmlDecimal("7.5"));
        expected.add(binding);
        performQuery(query, expected);
        System.out.println("Finished query.");
    }
    
    public void testHigherEqualThan6Feet() throws Exception {
        String query = "?x[heightInFeet hasValue ?v] and ?v >= 6.0";
        Set<Map<Variable, Term>> expected = new HashSet<Map<Variable, Term>>();
        Map<Variable, Term> binding = new HashMap<Variable, Term>();
        binding.put(wsmoFactory.createVariable("x"), wsmoFactory.createIRI(NS + "Aragorn"));
        binding.put(wsmoFactory.createVariable("v"), dataFactory.createWsmlDecimal("7.5"));
        expected.add(binding);
        binding = new HashMap<Variable, Term>();
        binding.put(wsmoFactory.createVariable("x"), wsmoFactory.createIRI(NS + "Arwen"));
        binding.put(wsmoFactory.createVariable("v"), dataFactory.createWsmlDecimal("6.0"));
        expected.add(binding);
        performQuery(query, expected);
        System.out.println("Finished query.");
    }
        
    public void testBornBefore900() throws Exception {
        String query = "?x[wasBorn hasValue ?v] and ?v < 900   "   ;
        Set<Map<Variable, Term>> expected = new HashSet<Map<Variable, Term>>();
        Map<Variable, Term> binding = new HashMap<Variable, Term>();
        binding.put(wsmoFactory.createVariable("x"), wsmoFactory.createIRI(NS + "Arwen"));
        binding.put(wsmoFactory.createVariable("v"), dataFactory.createWsmlInteger("500"));
        expected.add(binding);
        performQuery(query, expected);
        System.out.println("Finished query.");
    }
    
    public void testBornBeforeEqual900() throws Exception {
        String query = "?x[wasBorn hasValue ?v] and ?v =< 900  ";
        Set<Map<Variable, Term>> expected = new HashSet<Map<Variable, Term>>();
        Map<Variable, Term> binding = new HashMap<Variable, Term>();
        binding.put(wsmoFactory.createVariable("x"), wsmoFactory.createIRI(NS + "Arwen"));
        binding.put(wsmoFactory.createVariable("v"), dataFactory.createWsmlInteger("500"));
        expected.add(binding);
        binding = new HashMap<Variable, Term>();
        binding.put(wsmoFactory.createVariable("x"), wsmoFactory.createIRI(NS + "Aragorn"));
        binding.put(wsmoFactory.createVariable("v"), dataFactory.createWsmlInteger("900"));
        expected.add(binding);
        performQuery(query, expected);
        System.out.println("Finished query.");
    }
    
    public void testLowerThan7Feet() throws Exception {
        String query = "?x[heightInFeet hasValue ?v] and ?v < 7.0";
        Set<Map<Variable, Term>> expected = new HashSet<Map<Variable, Term>>();
        Map<Variable, Term> binding = new HashMap<Variable, Term>();
        binding.put(wsmoFactory.createVariable("x"), wsmoFactory.createIRI(NS + "Arwen"));
        binding.put(wsmoFactory.createVariable("v"), dataFactory.createWsmlDecimal("6.0"));
        expected.add(binding);
        performQuery(query, expected);
        System.out.println("Finished query.");
    }
    
    public void testLowerEqualThan7AndHalfFeet() throws Exception {
        String query = "?x[heightInFeet hasValue ?v] and ?v =< 7.5";
        Set<Map<Variable, Term>> expected = new HashSet<Map<Variable, Term>>();
        Map<Variable, Term> binding = new HashMap<Variable, Term>();
        binding.put(wsmoFactory.createVariable("x"), wsmoFactory.createIRI(NS + "Aragorn"));
        binding.put(wsmoFactory.createVariable("v"), dataFactory.createWsmlDecimal("7.5"));
        expected.add(binding);
        binding = new HashMap<Variable, Term>();
        binding.put(wsmoFactory.createVariable("x"), wsmoFactory.createIRI(NS + "Arwen"));
        binding.put(wsmoFactory.createVariable("v"), dataFactory.createWsmlDecimal("6.0"));
        expected.add(binding);
        performQuery(query, expected);
        System.out.println("Finished query.");
    }
    
    public void testNamedAragorn() throws Exception {
        String query = "?x[hasName hasValue ?v] and ?v = \"Aragorn\"";
        Set<Map<Variable, Term>> expected = new HashSet<Map<Variable, Term>>();
        Map<Variable, Term> binding = new HashMap<Variable, Term>();
        binding.put(wsmoFactory.createVariable("x"), wsmoFactory.createIRI(NS + "Aragorn"));
        binding.put(wsmoFactory.createVariable("v"), dataFactory.createWsmlString("Aragorn"));
        expected.add(binding);
        performQuery(query, expected);
        System.out.println("Finished query.");
    }
    
    public void testNamedArwen() throws Exception {
        String query = "?x[hasName hasValue \"Arwen\"]";
        Set<Map<Variable, Term>> expected = new HashSet<Map<Variable, Term>>();
        Map<Variable, Term> binding = new HashMap<Variable, Term>();
        binding.put(wsmoFactory.createVariable("x"), wsmoFactory.createIRI(NS + "Arwen"));
        expected.add(binding);
        performQuery(query, expected);
        System.out.println("Finished query.");
    }
    
    public void testBorn500() throws Exception {
        String query = "?x[wasBorn hasValue 500]";
        Set<Map<Variable, Term>> expected = new HashSet<Map<Variable, Term>>();
        Map<Variable, Term> binding = new HashMap<Variable, Term>();
        binding.put(wsmoFactory.createVariable("x"), wsmoFactory.createIRI(NS + "Arwen"));
        expected.add(binding);
        performQuery(query, expected);
        System.out.println("Finished query.");
    }
    
    public void testHeight6Feet() throws Exception {
        String query = "?x[heightInFeet hasValue 6.0]";
        Set<Map<Variable, Term>> expected = new HashSet<Map<Variable, Term>>();
        Map<Variable, Term> binding = new HashMap<Variable, Term>();
        binding.put(wsmoFactory.createVariable("x"), wsmoFactory.createIRI(NS + "Arwen"));
        expected.add(binding);
        performQuery(query, expected);
        System.out.println("Finished query.");
    }
    
    public void testHappy() throws Exception {
        String query = "?x[isHappy hasValue _boolean(\"true\")]";
        Set<Map<Variable, Term>> expected = new HashSet<Map<Variable, Term>>();
        Map<Variable, Term> binding = new HashMap<Variable, Term>();
        binding.put(wsmoFactory.createVariable("x"), wsmoFactory.createIRI(NS + "Aragorn"));
        expected.add(binding);
        binding = new HashMap<Variable, Term>();
        binding.put(wsmoFactory.createVariable("x"), wsmoFactory.createIRI(NS + "Arwen"));
        expected.add(binding);
        performQuery(query, expected);
        System.out.println("Finished query.");
    }
    
    public void testUnHappy() throws Exception {
        String query = "?x[isHappy hasValue _boolean(\"false\")]";
        Set<Map<Variable, Term>> expected = new HashSet<Map<Variable, Term>>();
        Map<Variable, Term> binding = new HashMap<Variable, Term>();
        binding.put(wsmoFactory.createVariable("x"), wsmoFactory.createIRI(NS + "Gloin"));
        expected.add(binding);
        binding = new HashMap<Variable, Term>();
        binding.put(wsmoFactory.createVariable("x"), wsmoFactory.createIRI(NS + "Gimli"));
        expected.add(binding);
        performQuery(query, expected);
        System.out.println("Finished query.");
    }
    
    public void testBorn490And10() throws Exception {
        String query = "?x[wasBorn hasValue ?v] and wsml#numericEqual(?v,(490 + 10))";
        Set<Map<Variable, Term>> expected = new HashSet<Map<Variable, Term>>();
        Map<Variable, Term> binding = new HashMap<Variable, Term>();
        binding.put(wsmoFactory.createVariable("x"), wsmoFactory.createIRI(NS + "Arwen"));
        binding.put(wsmoFactory.createVariable("v"), dataFactory.createWsmlInteger("500"));
        expected.add(binding);
        performQuery(query, expected);
        System.out.println("Finished query.");
    }
    
    public void testBorn510Minus10() throws Exception {
        String query = "?x[wasBorn hasValue ?v] and wsml#numericEqual(?v,(510 - 10))";
        Set<Map<Variable, Term>> expected = new HashSet<Map<Variable, Term>>();
        Map<Variable, Term> binding = new HashMap<Variable, Term>();
        binding.put(wsmoFactory.createVariable("x"), wsmoFactory.createIRI(NS + "Arwen"));
        expected.add(binding);
        performQuery(query, expected);
        System.out.println("Finished query.");
    }
    
    public void testBorn250Twice() throws Exception {
        String query = "?x[wasBorn hasValue ?v] and wsml#numericEqual(?v,(250 * 2))";
        Set<Map<Variable, Term>> expected = new HashSet<Map<Variable, Term>>();
        Map<Variable, Term> binding = new HashMap<Variable, Term>();
        binding.put(wsmoFactory.createVariable("x"), wsmoFactory.createIRI(NS + "Arwen"));
        expected.add(binding);
        performQuery(query, expected);
        System.out.println("Finished query.");
    }
    
    public void testBorn1000DividedBy2() throws Exception {
        String query = "?x[wasBorn hasValue ?v] and wsml#numericEqual(?v,(1000 / 2))";
        Set<Map<Variable, Term>> expected = new HashSet<Map<Variable, Term>>();
        Map<Variable, Term> binding = new HashMap<Variable, Term>();
        binding.put(wsmoFactory.createVariable("x"), wsmoFactory.createIRI(NS + "Arwen"));
        expected.add(binding);
        performQuery(query, expected);
        System.out.println("Finished query.");
    }
    
    public void testHappyOrSad() throws Exception {
        String query = "?x[isHappy hasValue _boolean(\"true\")] or ?x[isHappy hasValue _boolean(\"false\")]";
        Set<Map<Variable, Term>> expected = new HashSet<Map<Variable, Term>>();
        Map<Variable, Term> binding = new HashMap<Variable, Term>();
        binding.put(wsmoFactory.createVariable("x"), wsmoFactory.createIRI(NS + "Aragorn"));
        expected.add(binding);
        binding = new HashMap<Variable, Term>();
        binding.put(wsmoFactory.createVariable("x"), wsmoFactory.createIRI(NS + "Arwen"));
        expected.add(binding);
        binding = new HashMap<Variable, Term>();
        binding.put(wsmoFactory.createVariable("x"), wsmoFactory.createIRI(NS + "Gloin"));
        expected.add(binding);
        binding = new HashMap<Variable, Term>();
        binding.put(wsmoFactory.createVariable("x"), wsmoFactory.createIRI(NS + "Gimli"));
        expected.add(binding);
        performQuery(query, expected);
        System.out.println("Finished query.");
    }

}
