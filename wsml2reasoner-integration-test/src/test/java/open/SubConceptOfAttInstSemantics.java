/*
 * File: SubConceptOf_Reflexivity.java
 *
 */
package open;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.api.WSMLReasonerFactory.BuiltInReasoner;

import base.BaseReasonerTest;

/*
 * Tests fail since generation of new knownConcept rules
 * 
 */
public class SubConceptOfAttInstSemantics extends BaseReasonerTest {

    private static final String NS = "http://ontologies.deri.org/";
    private static final String ONTOLOGY_FILE = "simpsons.wsml";
    
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
  	resetReasoner(WSMLReasonerFactory.BuiltInReasoner.IRIS_STRATIFIED);
  	subConceptOfIsReflexiv2();
  	subConceptOfIsReflexiv3();
  }  
}
