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
import org.sti2.wsmo4j.factory.WsmlFactoryContainer;
import org.wsml.reasoner.api.DLReasoner;
import org.wsml.reasoner.api.LPReasoner;
import org.wsml.reasoner.api.WSMLReasoner;
import org.wsmo.factory.FactoryContainer;
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
		
		LPHelper.executeQueryAndCheckResults( ontology, X_MEMBER_OF_Y, expectedResults, reasoner );
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
	
	public static Set<Map<Variable, Term>> queryXMemberOfY( Ontology ontology, WSMLReasoner reasoner ) throws Exception {
		if( reasoner instanceof DLReasoner )
			return queryXMemberOfY( ontology, (DLReasoner) reasoner );
		else
			return LPHelper.executeQuery( ontology, X_MEMBER_OF_Y, (LPReasoner) reasoner );
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
	
	private static final String X_MEMBER_OF_Y = "?X memberOf ?Y";
	
    private static final WsmoFactory wsmoFactory;
    private static final LogicalExpressionFactory leFactory;
//    private static final DataFactory dataFactory;
    private static final FactoryContainer factory;
    
    static{
//  	 Set up factories for creating WSML elements
	   	factory = new WsmlFactoryContainer();
	
	   	leFactory = factory.getLogicalExpressionFactory();
	   	wsmoFactory = factory.getWsmoFactory();
//	   	dataFactory = wsmoManager.getDataFactory();
    }

}
