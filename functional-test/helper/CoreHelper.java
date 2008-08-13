package helper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Concept;
import org.omwg.ontology.Instance;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.api.DLReasoner;
import org.wsml.reasoner.api.LPReasoner;
import org.wsml.reasoner.api.WSMLReasoner;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsmo.factory.DataFactory;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;

public class CoreHelper
{
	public static void queryXMemberOfYAndCheckResults( Ontology ontology, WSMLReasoner reasoner, Set<Map<Variable, Term>> expectedResults ) throws Exception {

		if( reasoner instanceof DLReasoner )
			queryXMemberOfYAndCheckResults( ontology, (DLReasoner) reasoner, expectedResults );
		else
			queryXMemberOfYAndCheckResults( ontology, (LPReasoner) reasoner, expectedResults );
	}

	private static void queryXMemberOfYAndCheckResults( Ontology ontology, LPReasoner reasoner, Set<Map<Variable, Term>> expectedResults ) throws Exception {
		
		LPHelper.executeQueryAndCheckResults( ontology, "?X memberOf ?Y", expectedResults, reasoner );
	}

	private static void queryXMemberOfYAndCheckResults( Ontology ontology, DLReasoner reasoner, Set<Map<Variable, Term>> expectedResults ) throws Exception {
		
		Set<Map<Variable, Term>> actualResults = queryXMemberOfY( ontology, reasoner );
		
		LPHelper.checkResults( actualResults, expectedResults );
	}
	

	public static void queryXMemberOfConceptAndCheckResults( Ontology ontology, WSMLReasoner reasoner, String concept, Set<Map<Variable, Term>> expectedResults ) throws Exception {

		if( reasoner instanceof DLReasoner )
			queryXMemberOfConceptAndCheckResults( ontology, (DLReasoner) reasoner, concept, expectedResults );
		else
			queryXMemberOfConceptAndCheckResults( ontology, (LPReasoner) reasoner, concept, expectedResults );
	}

	private static void queryXMemberOfConceptAndCheckResults( Ontology ontology, LPReasoner reasoner, String concept, Set<Map<Variable, Term>> expectedResults ) throws Exception {
		
		LPHelper.executeQueryAndCheckResults( ontology, "?X memberOf " + "_\"" + concept + "\"", expectedResults, reasoner );
	}

	private static void queryXMemberOfConceptAndCheckResults( Ontology ontology, DLReasoner reasoner, String concept, Set<Map<Variable, Term>> expectedResults ) throws Exception {
	
		Set<Map<Variable, Term>> actualResults = queryXMemberOfConcept( ontology, reasoner, concept );
		
		LPHelper.checkResults( actualResults, expectedResults );
	}
	
	public static Set<Map<Variable, Term>> queryXMemberOfY( Ontology ontology, DLReasoner reasoner ) throws Exception {
		
		if( ontology != null )
			reasoner.registerOntology( ontology );

		Set<Map<Variable, Term>> results = new HashSet<Map<Variable, Term>>();
        
		Set<Concept> concepts = reasoner.getAllConcepts();
		Variable y = leFactory.createVariable( "Y" );
		
		for( Concept concept : concepts )
		{
			Set<Map<Variable, Term>> resultsForConcept = queryXMemberOfConcept( null, reasoner, concept.getIdentifier().toString() );
			
			for( Map<Variable, Term> conceptBinding : resultsForConcept )
			{
				Map<Variable, Term> binding = new HashMap<Variable, Term>();
				binding.putAll( conceptBinding );
				binding.put( y, concept.getIdentifier() );
				results.add( binding );
			}
		}
		
		return results;
	}

	public static Set<Map<Variable, Term>> queryXMemberOfConcept( Ontology ontology, DLReasoner reasoner, String concept ) throws Exception {
//		System.out.println("Retrieving all instances of concept " + concept);
		
		if( ontology != null )
			reasoner.registerOntology( ontology );
		
		Set<Instance> result = reasoner.getInstances(
				wsmoFactory.createConcept(
				wsmoFactory.createIRI(concept)));
		
		Variable x = leFactory.createVariable( "X" );
		
		Set<Map<Variable, Term>> lpResults = new HashSet<Map<Variable, Term>>();
		for( Instance instance : result )
		{
			Map<Variable, Term> binding = new HashMap<Variable, Term>();
			
			binding.put( x, instance.getIdentifier() );
			
			lpResults.add( binding );
		}
		
		return lpResults;
	}
	
//    public static void checkResults( Set<Instance> actualResults, Set<Map<Variable, Term>> expectedResults )
//    {
//		Assert.assertEquals( expectedResults.size(), actualResults.size() );
//		for (Map<Variable, Term> binding : expectedResults) {
//			Assert.assertTrue("Result does not contain instance: " +
//		    		binding, instanceCheckerDL(actualResults, binding));
//		}
//    }
//	
//    private static boolean instanceCheckerDL(Set<Instance> result, Map<Variable, Term> binding) {
//		 for (Instance instance : result) {
//	         if (binding.values().contains(instance.getIdentifier())){
//	        	 return true;
//	         }
//	     }
//		return false;
//	}
//    
//    private static Set<Identifier> convertQueryResults( Set<Map<Variable, Term>> actualQueryResults )
//    {
//    	Set<Identifier> identifiers = new HashSet<Identifier>();
//    	
//    	for( Map<Variable, Term> binding : actualQueryResults )
//    	{
//    		
//    	}
//    	
//    	return identifiers;
//    }

    private static final WsmoFactory wsmoFactory;
    private static final LogicalExpressionFactory leFactory;
    private static final DataFactory dataFactory;
    private static final WSMO4JManager wsmoManager;
    
    static{
//  	 Set up factories for creating WSML elements
	   	wsmoManager = new WSMO4JManager();
	
	   	leFactory = wsmoManager.getLogicalExpressionFactory();
	   	wsmoFactory = wsmoManager.getWSMOFactory();
	   	dataFactory = wsmoManager.getDataFactory();
    }

}
