/*
 * File: SubConceptOf_Reflexivity.java
 *
 */
package variant.flight;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.api.WSMLReasonerFactory.BuiltInReasoner;

import base.BaseReasonerTest;

/*
 * testSubConceptOfIsReflexiv1 fails
 * 
 */
public class SubConceptOfSemanticsTest extends BaseReasonerTest {

    private static final String NS = "http://ontologies.deri.org/";
    private static final String ONTOLOGY_FILE = "files/simpsons.wsml";
    
   
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
    
  public void subConceptOfIsReflexiv1() throws Exception {
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
      binding = new HashMap<Variable, Term>();
      binding.put(leFactory.createVariable("x"), wsmoFactory.createIRI(NS + "university"));
      expected.add(binding);
      performQuery(query, expected);
  }
  
  public void subConceptOfIsReflexiv2() throws Exception {
      // attribute IRIs can be considered as subconcepts of themselves
      String query = "?x subConceptOf hasName";
      Set<Map<Variable,Term>> expected = new HashSet<Map<Variable,Term>>();
      Map<Variable, Term> binding = new HashMap<Variable, Term>();
      binding.put(leFactory.createVariable("x"), wsmoFactory.createIRI(NS + "hasName"));
      expected.add(binding);
      performQuery(query, expected);
  }
  
  public void subConceptOfIsReflexiv3() throws Exception {
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
  
  public void testFlightReasoners() throws Exception{
  	resetReasoner(WSMLReasonerFactory.BuiltInReasoner.IRIS);
  	subConceptOfIsReflexiv1();
  	subConceptOfIsReflexiv2();
  	subConceptOfIsReflexiv3();
  	
  	resetReasoner(WSMLReasonerFactory.BuiltInReasoner.MINS);
  	subConceptOfIsReflexiv1();
  	subConceptOfIsReflexiv2();
  	subConceptOfIsReflexiv3();
  	
  	resetReasoner(WSMLReasonerFactory.BuiltInReasoner.KAON2);
  	subConceptOfIsReflexiv1();
  	subConceptOfIsReflexiv2();
  	subConceptOfIsReflexiv3();
  }

  
  
  
}
