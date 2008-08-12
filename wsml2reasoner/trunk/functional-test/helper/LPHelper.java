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

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.Map;
import java.util.Set;
import junit.framework.Assert;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.api.LPReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsmo.common.TopEntity;
import org.wsmo.common.exception.InvalidModelException;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.wsml.Parser;
import org.wsmo.wsml.ParserException;
import org.wsmo.wsml.Serializer;

/**
 * Currently does not work with PELLET (due to detected inconsistency)
 */
public class LPHelper
{
    public static final WSMLReasonerFactory.BuiltInReasoner[] FLIGHT_REASONERS = new WSMLReasonerFactory.BuiltInReasoner[]
    {
    	WSMLReasonerFactory.BuiltInReasoner.IRIS,
    	WSMLReasonerFactory.BuiltInReasoner.MINS,
    	WSMLReasonerFactory.BuiltInReasoner.KAON2
    	
    };
    
    public static Ontology loadOntology( String ontologyString  ) throws IOException, ParserException, InvalidModelException
    {
    	return parseThis(getReaderForFile(ontologyString ));
    }
    
    public static Ontology parseOntology( String ontologyString ) throws ParserException, InvalidModelException
    {
    	return (Ontology) wsmlparserimpl.parse(new StringBuffer(ontologyString))[0];
    }

    public static void executeQuery( Ontology ontology, String query, Set<Map<Variable, Term>> expectedResults, LPReasoner reasoner ) throws Exception
    {
        reasoner.registerOntology(ontology);

        performQuery(ontology, query, expectedResults, reasoner);
    }
    	
    public static String toString( Ontology ontology )
    {
        Serializer ontologySerializer = org.wsmo.factory.Factory.createSerializer(null);

		StringWriter sw = new StringWriter();
		try
        {
	        ontologySerializer.serialize(new TopEntity[] { ontology }, sw);
        }
        catch( IOException e )
        {
	        // Writing to a StringWriter, so can not get this exception.
        }
		return sw.toString();
    }

   	private static boolean isKaon( WSMLReasonerFactory.BuiltInReasoner reasoner )
   	{
   		return reasoner == WSMLReasonerFactory.BuiltInReasoner.KAON2 ||
   			reasoner == WSMLReasonerFactory.BuiltInReasoner.KAON2DL;
   	}
    
    private static boolean exists (String className)
    {
    	try {
    		Class.forName (className);
    		return true;
    	}
    	catch (ClassNotFoundException exception) {
    		return false;
    	}
    }
    
    private static Reader getReaderForFile(String location) {
        Reader ontoReader = null;
        try {
            ontoReader = new FileReader(location);
        } catch (FileNotFoundException e) {
            // get current class loader and try to load from there...
            InputStream is = LPHelper.class.getClassLoader()
                    .getResourceAsStream(location);
            // System.out.println();
            Assert.assertNotNull("Could not load file from class path: " + location,
                    is);
            ontoReader = new InputStreamReader(is);
        }
        Assert.assertNotNull("Could not load file from file system: " + location,
                ontoReader);
        return ontoReader;
    }
    
    private static void performQuery(Ontology ontology, String query, Set<Map<Variable, Term>> expected, LPReasoner reasoner)
    throws Exception {
//		System.out.println("\n\nStarting reasoner with query '" + query + "'");
//		System.out.println("\n\nExpecting " + expected.size() + " result(s)...");
		LogicalExpression qExpression = leFactory.createLogicalExpression(
		        query, ontology);
//		System.out.println("WSML Query LE:");
		
//		LogExprSerializerWSML logExprSerializer = new LogExprSerializerWSML(ontology);
		
//		System.out.println(logExprSerializer.serialize(qExpression));
//		System.out.println("--------------\n\n");
		
		Set<Map<Variable, Term>> result = reasoner.executeQuery(qExpression);
		
//		System.out.println("Found < " + result.size()
//		        + " > results to the query:");
//		int i = 0;
//		for (Map<Variable, Term> vBinding : result) {
//		    System.out.println("(" + (++i) + ") -- " + vBinding.toString());
//		}
		Assert.assertEquals("Engine: " + reasoner + " ", expected.size(), result.size());
		for (Map<Variable, Term> binding : expected) {
			Assert.assertTrue("Engine: " + reasoner + "- Result does not contain binding " + binding, contains(
		            result, binding));
		}
    }

    private static Ontology parseThis(Reader ontoReader) throws IOException, ParserException, InvalidModelException{
   	 final TopEntity[] identifiable = wsmlparserimpl.parse(ontoReader);
        if (identifiable.length > 0 && identifiable[0] instanceof Ontology) {
            return (Ontology) identifiable[0];
        } else {
            throw new RuntimeException( "Could not create ontology from file" );
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
    private static Parser wsmlparserimpl = org.wsmo.factory.Factory.createParser(null);

    public static int evalMethod = -1;
    public static int allowImports = 0;

}
