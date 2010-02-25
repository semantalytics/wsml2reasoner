/*
 * WSML2Reasoner
 * An extensible framework for reasoning with WSML ontologies.
 * 
 * Copyright (C) 2008 Semantic Technology Institute (STI) Innsbruck, 
 * University of Innsbruck, Technikerstrasse 21a, 6020 Innsbruck, Austria.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 */
package helper;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.Assert;

import org.deri.wsmo4j.io.parser.wsml.WsmlLogicalExpressionParser;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.api.LPReasoner;
import org.wsml.reasoner.api.inconsistency.InconsistencyException;
import org.wsmo.wsml.ParserException;

/**
 * Helper utilities for DL based tests.
 */
public class LPHelper
{
	private static boolean output = false;
	
    public static void executeQueryAndCheckResults( Ontology ontology, String query, Set<Map<Variable, Term>> expectedResults, LPReasoner reasoner ) throws Exception
    {
    	Set<Ontology> ontologies = new HashSet<Ontology>();
    	ontologies.add( ontology );

        executeQueryAndCheckResults( ontologies, query, expectedResults, reasoner );
    }
    
    public static void executeQueryAndCheckResults( Set<Ontology> ontologies, String query, Set<Map<Variable, Term>> expectedResults, LPReasoner reasoner ) throws Exception
    {
		Set<Map<Variable, Term>> actualResults = executeQuery(ontologies, query, reasoner );
		
		checkResults( actualResults, expectedResults );
    }
    
    public static Set<Map<Variable, Term>> executeQuery( Ontology ontology, String query, LPReasoner reasoner ) throws ParserException, InconsistencyException
    {
    	Set<Ontology> ontologies = new HashSet<Ontology>();
    	ontologies.add( ontology );
    	
    	return executeQuery( ontologies, query, reasoner );
    }
    
    public static Set<Map<Variable, Term>> executeQuery( Set<Ontology> ontologies, String query, LPReasoner reasoner ) throws ParserException, InconsistencyException
    {
        reasoner.registerOntologies(ontologies);

        LogicalExpression qExpression = new WsmlLogicalExpressionParser(ontologies.iterator().next()).parse( query );

        if(output){
        	System.out.println("Executing query string '" + query + "'");
			System.out.println("Executing query LE: '"
					+ OntologyHelper.toString( ontologies.iterator().next(),  qExpression ) + "'");
        }

		return reasoner.executeQuery(qExpression);
    }
    
    public static void checkResults( Set<Map<Variable, Term>> actualResults, Set<Map<Variable, Term>> expectedResults )
    {
    	if(output){
    		System.out.println( "Expected results: " + OntologyHelper.toString( expectedResults ));
    		System.out.println( "Actual results: " + OntologyHelper.toString( actualResults ));
    	}

    	Assert.assertEquals(expectedResults.size(), actualResults.size());

		for (Map<Variable, Term> binding : expectedResults)
		{
			if( ! contains( actualResults, binding ) )
				System.out.println( binding );
			Assert.assertTrue( contains( actualResults, binding ) );
		}
    }
    	
	/**
     * Checks whether there is a binding in result which contains all of the
     * variable bindings of expected
     * 
     * @param result
     *            the set of bindings to check
     * @param expectedBinding
     *            the reference binding
     * @return true if there is such an element
     */
    private static boolean contains(Set<Map<Variable, Term>> result,
            Map<Variable, Term> expectedBinding) {
        boolean contains = false;
       
        for (Map<Variable, Term> vBinding : result) {
        	boolean containsAll = true;  
            for (Variable var : expectedBinding.keySet()) {
            	Term expected = expectedBinding.get(var);
            	Term actual = vBinding.get(var);
            	if(actual == null ) {
            		System.out.println("\nError: Actual Binding has value null: " + vBinding  +" !");
            		return false;
            	}
            	if(expected == null ) {
            		System.out.println("\nError: Expected Binding has value null: " + expectedBinding  +" !");
            		return false;
            	}
                containsAll = containsAll
                        && expected.equals(actual);
            }
            if (containsAll) {
                contains = true;
                break;
            }
        }
        return contains;
    }
    
   public static void outputON(){
    	output = true;
   }
   
   public static void outputOFF(){
   		output = false;
   }
    
}
