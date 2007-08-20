/**
 * WSML Reasoner Implementation.
 *
 * Copyright (c) 2005, FZI, Germany
 *                     University of Innsbruck, Austria.
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
package base;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.deri.wsmo4j.io.serializer.wsml.LogExprSerializerWSML;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Instance;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.api.WSMLReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.api.inconsistency.InconsistencyException;
import org.wsml.reasoner.impl.DefaultWSMLReasonerFactory;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsmo.common.IRI;
import org.wsmo.common.TopEntity;
import org.wsmo.common.exception.InvalidModelException;
import org.wsmo.factory.DataFactory;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.Parser;
import org.wsmo.wsml.ParserException;
import org.wsmo.wsml.Serializer;

public class BaseReasonerTest extends TestCase {

    //CHANGE HERE TO CHECK DIFFERENT REASONERS!
    public static WSMLReasonerFactory.BuiltInReasoner reasoner =
//    	WSMLReasonerFactory.BuiltInReasoner.KAON2;
//    	WSMLReasonerFactory.BuiltInReasoner.IRIS;
    	WSMLReasonerFactory.BuiltInReasoner.MINS;
    	
    //CHANGE HERE TO CHECK DIFFERENT EVALUATION METHODS-
    //IS ALSO SET FROM BUNDLED VARIANT TEST SUITES
    //e.g. ReasonerCoreTest
    public static int evalMethod = 3;

    //0=allow imports
    //1=do not allow imports
    public static int allowImports = 0;
    
    protected static WSMLReasoner wsmlReasoner = null;

    protected static Ontology o = null;

    protected LogExprSerializerWSML logExprSerializer = null;

    protected static WsmoFactory wsmoFactory = null;

    protected static LogicalExpressionFactory leFactory = null;

    protected static DataFactory dataFactory = null;

    protected static WSMO4JManager wsmoManager = null;
    
    protected static Parser wsmlparserimpl = org.wsmo.factory.Factory.createParser(null);
    
    public static WSMLReasoner getReasoner(){
        // Create reasoner
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(WSMLReasonerFactory.PARAM_BUILT_IN_REASONER,reasoner);  
        params.put(WSMLReasonerFactory.PARAM_EVAL_METHOD,evalMethod);
        params.put(WSMLReasonerFactory.PARAM_ALLOW_IMPORTS,allowImports);
        
        /**
         * This IF should be changed:
         * KAON2 can also handle DL reasoning
         * DL reasoning should be handled by
         */
        if(reasoner.equals(WSMLReasonerFactory.BuiltInReasoner.IRIS) |
        		reasoner.equals(WSMLReasonerFactory.BuiltInReasoner.KAON2)){
        	wsmlReasoner = DefaultWSMLReasonerFactory.getFactory()
            	.createWSMLFlightReasoner(params);
        	
        } 
        else if(reasoner.equals(WSMLReasonerFactory.BuiltInReasoner.MINS)){
        	wsmlReasoner = DefaultWSMLReasonerFactory.getFactory()
        		.createWSMLRuleReasoner(params);	
        }
        else if(reasoner.equals(WSMLReasonerFactory.BuiltInReasoner.PELLET)){
        	wsmlReasoner = DefaultWSMLReasonerFactory.getFactory()
        		.createWSMLDLReasoner(params);
        }
        
        return wsmlReasoner;
    }
    
    public static void resetReasoner(WSMLReasonerFactory.BuiltInReasoner b) throws InconsistencyException{
    	reasoner = b;
    	//setUpFactories();
    	wsmlReasoner = getReasoner();
    	if(o != null){
    		wsmlReasoner.registerOntology(o);
    	}
    }
    
    protected static void setUpFactories(){
//    	 Set up factories for creating WSML elements
    	wsmoManager = new WSMO4JManager();

        leFactory = wsmoManager.getLogicalExpressionFactory();

        wsmoFactory = wsmoManager.getWSMOFactory();

        dataFactory = wsmoManager.getDataFactory();
    	
    }

    protected static void setupScenario(String ontologyFile) throws IOException, ParserException, InvalidModelException, InconsistencyException {
       
    	setUpFactories();

        // Set up serializer

        Serializer ontologySerializer = org.wsmo.factory.Factory
                .createSerializer(null);

        // Read simple ontology from file
        final Reader ontoReader = getReaderForFile(ontologyFile);
        parseThis(ontoReader);

        // Print ontology in WSML

        System.out.println("WSML Ontology:\n");
        StringWriter sw = new StringWriter();
        ontologySerializer.serialize(new TopEntity[] { o }, sw);
        System.out.println(sw.toString());
        System.out.println("--------------\n\n");

        wsmlReasoner = getReasoner();
        
        // Register ontology
        System.out.println("Registering ontology");
        wsmlReasoner.registerOntology(o);
    }

    protected void performQuery(String query, Set<Map<Variable, Term>> expected)
            throws Exception {
        System.out.println("\n\nStarting reasoner with query '" + query + "'");
        System.out.println("\n\nExpecting " + expected.size() + " result(s)...");
        LogicalExpression qExpression = leFactory.createLogicalExpression(
                query, o);
        System.out.println("WSML Query LE:");
        System.out.println(logExprSerializer.serialize(qExpression));
        System.out.println("--------------\n\n");

        Set<Map<Variable, Term>> result = wsmlReasoner.executeQuery((IRI) o
                .getIdentifier(), qExpression);

        System.out.println("Found < " + result.size()
                + " > results to the query:");
        int i = 0;
        for (Map<Variable, Term> vBinding : result) {
            System.out.println("(" + (++i) + ") -- " + vBinding.toString());
        }
        assertEquals("Engine: " + reasoner + " ", expected.size(), result.size());
        for (Map<Variable, Term> binding : expected) {
            assertTrue("Engine: " + reasoner + "- Result does not contain binding " + binding, include(
                    result, binding));
        }
    }
    
    protected void performDLQuery(int i, String concept, Set<Map<Variable, Term>> expected)
    	throws Exception {
    	switch(i){
    	//0 = instance retrieval - per concept
    	case 0:
    		System.out.println("\n\nStarting DL reasoner - " +
    				"retrieving all instances of concept " + concept);
    	    System.out.println("\n\nExpecting " + expected.size() + " result(s)...");
    		Set<Instance> result = wsmlReasoner.getInstances((IRI) o.getIdentifier(), 
    				wsmoFactory.createConcept(
    				wsmoFactory.createIRI(concept)));
    		System.out.println("Found < " + result.size()
    		        + " > results to the query:");
    		for (Instance instance : result){
    			System.out.println("(" + (++i) + ") -- " + instance.getIdentifier().toString() );
//    			assertTrue("Result does not contain instance: " + instance.getIdentifier().toString(), result.contains(instance));
    		}
    		assertEquals("Engine: " + reasoner + " ", expected.size(), result.size());
    		for (Map<Variable, Term> binding : expected) {
    		    assertTrue("Engine: " + reasoner + " - Result does not contain instance: " +
    		    		binding, instanceCheckerDL(result, binding));
    		}
    	}
    }
    

    private boolean instanceCheckerDL(Set<Instance> result, Map<Variable, Term> binding) {
    	 for (Instance instance : result) {
             if (binding.values().contains(instance.getIdentifier())){
            	 return true;
             }
         }
		return false;
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
    private boolean include(Set<Map<Variable, Term>> result,
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

    @Override
    protected void setUp() throws Exception {
        logExprSerializer = new LogExprSerializerWSML(o);
    }

    /**
     * Utiltiy to get a Reader, tries first fileReader and then to load from
     * clath pass, helps avoiding FileNotFound exception during automated
     * testing
     * 
     * @param location
     *            of file
     * @return Reader
     */
    public static Reader getReaderForFile(String location) {
        Reader ontoReader = null;
        try {
            ontoReader = new FileReader(location);
        } catch (FileNotFoundException e) {
            // get current class loader and try to load from there...
            InputStream is = new BaseReasonerTest().getClass().getClassLoader()
                    .getResourceAsStream(location);
            // System.out.println();
            assertNotNull("Could not load file from class path: " + location,
                    is);
            ontoReader = new InputStreamReader(is);
        }
        assertNotNull("Could not load file from file system: " + location,
                ontoReader);
        return ontoReader;
    }
    
    public static void parseThis(Reader ontoReader) throws IOException, ParserException, InvalidModelException{
    	 final TopEntity[] identifiable = wsmlparserimpl.parse(ontoReader);
         if (identifiable.length > 0 && identifiable[0] instanceof Ontology) {
             o = (Ontology) identifiable[0];
         } else {
             return;
         }
         System.out.println("Ontology parsed");
    }

}
