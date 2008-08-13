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
package helper;

import java.util.Map;
import java.util.Set;
import junit.framework.Assert;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.api.LPReasoner;
import org.wsml.reasoner.api.inconsistency.InconsistencyException;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.wsml.ParserException;

/**
 * 
 */
public class LPHelper
{
    public static void executeQueryAndCheckResults( Ontology ontology, String query, Set<Map<Variable, Term>> expectedResults, LPReasoner reasoner ) throws Exception
    {
		Set<Map<Variable, Term>> actualResults = executeQuery(ontology, query, reasoner );
		
		checkResults( actualResults, expectedResults );
    }
    
    public static Set<Map<Variable, Term>> executeQuery( Ontology ontology, String query, LPReasoner reasoner ) throws ParserException, InconsistencyException
    {
        reasoner.registerOntology(ontology);

        LogicalExpression qExpression = leFactory.createLogicalExpression( query, ontology);

//		System.out.println("Executing query string '" + query + "'");
//		System.out.println("Executing query LE: '" + OntologyHelper.toString( ontology, qExpression ) + "'");

		return reasoner.executeQuery(qExpression);
    }
    
    public static void checkResults( Set<Map<Variable, Term>> actualResults, Set<Map<Variable, Term>> expectedResults )
    {
//		System.out.println( "Expected results: " + OntologyHelper.toString( expectedResults ));
//		System.out.println( "Actual results: " + OntologyHelper.toString( actualResults ));

    	Assert.assertEquals(expectedResults.size(), actualResults.size());

		for (Map<Variable, Term> binding : expectedResults)
			Assert.assertTrue( contains( actualResults, binding ) );
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
                containsAll = containsAll
                        && expectedBinding.get(var).equals(vBinding.get(var));
            }
            if (containsAll) {
                contains = true;
                break;
            }
        }
        return contains;
    }

    private static final LogicalExpressionFactory leFactory = new WSMO4JManager().getLogicalExpressionFactory();
}
