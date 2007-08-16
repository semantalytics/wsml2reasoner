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
package config;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.api.WSMLReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.api.WSMLReasonerFactory.BuiltInReasoner;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsmo.common.IRI;
import org.wsmo.factory.Factory;
import org.wsmo.locator.Locator;
import org.wsmo.locator.LocatorManager;
import org.wsmo.wsml.Parser;

import base.BaseReasonerTest;

public class ImportOntologyTest extends BaseReasonerTest {

	BuiltInReasoner previous;

    protected void setUp() throws Exception {
    	super.setUp();
        previous =  BaseReasonerTest.reasoner;             
     }

    protected void tearDown() throws Exception {
    	super.tearDown();
    	resetReasoner(previous);
        System.gc();
    }
    
    public void importOntology() throws Exception {
        String ns = "http://www.importtester.org#";
        String ONTOLOGY_FILE = "test/files/simpleImportTest.wsml";
        String query = "?x memberOf c";
        String concept = ns + "c";
        
        setupScenario(ONTOLOGY_FILE);
        
        Set<Map<Variable, Term>> expected = new HashSet<Map<Variable, Term>>();
        Map<Variable, Term> binding = new HashMap<Variable, Term>();
        binding.put(leFactory.createVariable("x"),  wsmoFactory.createIRI(ns + "i1"));
        expected.add(binding);
        binding.put(leFactory.createVariable("x"),  wsmoFactory.createIRI(ns + "i2"));
        expected.add(binding);
        if (BaseReasonerTest.reasoner.equals(WSMLReasonerFactory.BuiltInReasoner.PELLET)){
        	int i = 0; //indicates DL instance retrieval
        	performDLQuery(i, concept, expected);
        } 
        else performQuery(query, expected);
    }
    
    
    /**
     * *********BEFORE RUNNING TEST*********
     * Change the importOntology path in "importOnotlogies.wsml"
     * (currently found in test.reasoner.core) to point to
     * the ontology you wish to import.
     * 
     * A sample ontology (that satisfies test) to copy to your local disk
     * can be found commented out in importOnotologies.wsml
     * 
     * Should be re-written to work with DL
     * 
     * @throws Exception
     */
   public void importOntologyWithLocator() throws Exception {


	   Parser wsmlParser = org.wsmo.factory.Factory.createParser(null);
	   
        //Read test file and parses it 
	    //Make sure that the path to the imported ontology
	    //in importsOntology is correct
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(
                "files/importOntologies.wsml");
        
        //Assumes first Topentity in file is an ontology  
        Ontology ontology = (Ontology)wsmlParser.parse(new InputStreamReader(is))[0]; 
 
		HashMap prefs = new HashMap();
		prefs.put(Factory.PROVIDER_CLASS, "org.deri.wsmo4j.locator.LocatorImpl");
		
		Locator locator = LocatorManager.createLocator(prefs);
		
		Set <Ontology> ontologies = new HashSet <Ontology>();
		
		for (Iterator it = ontology.listOntologies().iterator(); it.hasNext();){
			Ontology o = (Ontology) it.next();		
			Ontology ont = (Ontology) locator.lookup(o.getIdentifier(), null);
			if (ont != null) {
				ontologies.add(ont);
			}
		}
		
        wsmoManager = new WSMO4JManager();

        leFactory = wsmoManager.getLogicalExpressionFactory();

        wsmoFactory = wsmoManager.getWSMOFactory();

        dataFactory = wsmoManager.getDataFactory();
        
        LogicalExpression query = leFactory.createLogicalExpression(
                "?x memberOf ?v", ontology);
        System.out.println("Query:\n" + query.toString());
        WSMLReasoner reasoner = BaseReasonerTest.getReasoner();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(WSMLReasonerFactory.PARAM_ALLOW_IMPORTS, 0);
        Set<Map<Variable, Term>> result = null;
        reasoner.registerOntology(ontology);
        result = reasoner.executeQuery((IRI) ontology.getIdentifier(), query);
        System.out.println("Result:");
        for (Map<Variable, Term> vt : result){
        	for(Variable v : vt.keySet()){
        			System.out.println(v + " : " + vt.get(v));
        	}
        }
        assertEquals(2, result.size());
        reasoner.registerOntology(ontology);
    }
    
    public void testAllReasoners() throws Exception{
    	resetReasoner(WSMLReasonerFactory.BuiltInReasoner.IRIS);
    	importOntology();
    	importOntologyWithLocator();
    	
    	resetReasoner(WSMLReasonerFactory.BuiltInReasoner.MINS);
    	importOntology();
    	importOntologyWithLocator();
    	
    	resetReasoner(WSMLReasonerFactory.BuiltInReasoner.KAON2);
    	importOntology();
    	importOntologyWithLocator();
    	
    	resetReasoner(WSMLReasonerFactory.BuiltInReasoner.PELLET);
    	importOntology();
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}