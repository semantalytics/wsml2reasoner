/*
 * File: SubConceptOf_Reflexivity.java
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

import variant.flight.DogsworldTest;
import base.BaseReasonerTest;

/*
 * testSubConceptOfIsReflexiv1 fails
 * 
 */
public class SubConceptOfSemanticsTest extends BaseReasonerTest {

    private static final String NS = "http://ontologies.deri.org/";
    private static final String ONTOLOGY_FILE = "files/simpsons.wsml";
    
   
    public static void main(String[] args) {
        junit.textui.TestRunner.run(DogsworldTest.suite());
    }

    public static Test suite() {
        Test test = new junit.extensions.TestSetup(new TestSuite(
                SubConceptOfSemanticsTest.class)) {
            protected void setUp() throws Exception {
                setupScenario(ONTOLOGY_FILE);
             }

            protected void tearDown() throws Exception {
                System.out.println("Finished!");
            }
        };
        return test;
    }

  public void testSubConceptOfIsReflexiv1() throws Exception {
      String query = "?x subConceptOf place";
      Set<Map<Variable,Term>> expected = new HashSet<Map<Variable,Term>>();
      Map<Variable, Term> binding = new HashMap<Variable, Term>();
      binding.put(leFactory.createVariable("x"), wsmoFactory.createIRI(NS + "town"));
      expected.add(binding);
      binding = new HashMap<Variable, Term>();
      binding.put(leFactory.createVariable("x"), wsmoFactory.createIRI(NS + "workplace"));
      expected.add(binding);
      binding = new HashMap<Variable, Term>();
      binding.put(leFactory.createVariable("x"), wsmoFactory.createIRI(NS + "school"));
      expected.add(binding);
      binding = new HashMap<Variable, Term>();
      binding.put(leFactory.createVariable("x"), wsmoFactory.createIRI(NS + "church"));
      expected.add(binding);
      binding = new HashMap<Variable, Term>();
      binding.put(leFactory.createVariable("x"), wsmoFactory.createIRI(NS + "place"));
      expected.add(binding);
      performQuery(query, expected);
  }
  
  public void testSubConceptOfIsReflexiv2() throws Exception {
      // attribute IRIs can be considered as subconcepts of themselves
      String query = "?x subConceptOf hasName";
      Set<Map<Variable,Term>> expected = new HashSet<Map<Variable,Term>>();
      Map<Variable, Term> binding = new HashMap<Variable, Term>();
      binding.put(leFactory.createVariable("x"), wsmoFactory.createIRI(NS + "hasName"));
      expected.add(binding);
      performQuery(query, expected);
  }
  
  public void testSubConceptOfIsReflexiv3() throws Exception {
      // instance IRIs can be considered as subconcepts of themselves
      String query = "(?x memberOf gender) and (?x subConceptOf ?x) ";
      Set<Map<Variable,Term>> expected = new HashSet<Map<Variable,Term>>();
      Map<Variable, Term> binding = new HashMap<Variable, Term>();
      binding.put(leFactory.createVariable("x"), wsmoFactory.createIRI(NS + "male"));
      expected.add(binding);
      binding = new HashMap<Variable, Term>();
      binding.put(leFactory.createVariable("x"), wsmoFactory.createIRI(NS + "female"));
      expected.add(binding);
      performQuery(query, expected);
  }
  
}
