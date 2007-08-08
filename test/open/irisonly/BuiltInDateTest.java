package open.irisonly;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.api.WSMLReasoner;
import org.wsmo.common.IRI;
import org.wsmo.factory.Factory;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.Parser;

import base.BaseReasonerTest;

public class BuiltInDateTest extends BaseReasonerTest {

	private WsmoFactory wsmoFactory;
	
	private LogicalExpressionFactory leFactory;
	
	private WSMLReasoner wsmlReasoner;

	private Parser parser;
	   
	private Ontology ontology;
	   
	protected void setUp() throws Exception {
		super.setUp();
			
		// get a reasoner
		wsmlReasoner = BaseReasonerTest.getReasoner();
		wsmoFactory = Factory.createWsmoFactory(null);
		leFactory = Factory.createLogicalExpressionFactory(null);
		parser = Factory.createParser(null);
	}
	
	public void testRun() throws Exception {
		// read test file and parse it 
       InputStream is = this.getClass().getClassLoader().getResourceAsStream(
               "files/builtInDateTest.wsml");
       assertNotNull(is);
       // assuming first topentity in file is an ontology  
       ontology = (Ontology)parser.parse(new InputStreamReader(is))[0]; 
       String nsp = ontology.getDefaultNamespace().getIRI().toString();
       
       // register ontology at the wsml reasoner
       wsmlReasoner.registerOntology(ontology);       
       
       // build query
       String query = "?x memberOf Child";
       LogicalExpression qExpression = leFactory.createLogicalExpression(
               query, ontology);
       
       // build set with expected results
       Set<Map<Variable, Term>> expected = new HashSet<Map<Variable, Term>>();
       Map<Variable, Term> binding = new HashMap<Variable, Term>();
       binding.put(leFactory.createVariable("x"), wsmoFactory.createIRI(nsp + "Anna"));
       expected.add(binding);
       binding.put(leFactory.createVariable("x"), wsmoFactory.createIRI(nsp + "Chris"));
       expected.add(binding);
       
       // perform query
       System.out.println("WSML Query LE:");
       System.out.println(qExpression.toString());
       System.out.println("\n\nExpecting " + expected.size() + " results...\n");
       Set<Map<Variable, Term>> result = wsmlReasoner.executeQuery(
    		   (IRI) ontology.getIdentifier(), qExpression);
       System.out.println("Found < " + result.size() + " > results to the query:\n");
      
       // assert that expected result set size equals actual result set size
       assertEquals(expected.size(), result.size());

       Set<String> resultSet = new HashSet<String>(2);
       int i=0;
       for (Map<Variable, Term> resultBinding : result) {
    	   System.out.println("result binding (" + (i++) + "): " + resultBinding.toString());
    	   resultSet.add(resultBinding.entrySet().iterator().next().getValue().toString());
       }
       
       assertTrue(resultSet.contains(nsp + "Anna"));
       assertTrue(resultSet.contains(nsp + "Chris"));
		
       wsmlReasoner.deRegisterOntology((IRI) ontology.getIdentifier());
	}
	
}
