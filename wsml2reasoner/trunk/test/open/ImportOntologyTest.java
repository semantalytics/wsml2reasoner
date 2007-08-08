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
package open;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.api.WSMLReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsmo.common.IRI;
import org.wsmo.factory.Factory;
import org.wsmo.locator.Locator;
import org.wsmo.locator.LocatorManager;
import org.wsmo.wsml.Parser;

import base.BaseReasonerTest;

public class ImportOntologyTest extends BaseReasonerTest {

    public static void main(String[] args) throws Exception {
        junit.textui.TestRunner.run(ImportOntologyTest.suite());
    }

    public static Test suite() {
        Test test = new junit.extensions.TestSetup(new TestSuite(ImportOntologyTest.class)) {};
        return test;
    }

    public void testImportOntologyTest() throws Exception {
        String ns = "http://ex1.org#";
        String test = "namespace _\""+ns+"\" \n" +
                "ontology o1 \n" +
                "importsOntology o2 \n" +
                "concept c \n" +
                "instance i1 memberOf c \n " +
                "ontology o2 \n" +
                "instance i2 memberOf c \n ";
        
        
        wsmoManager = new WSMO4JManager();

        leFactory = wsmoManager.getLogicalExpressionFactory();

        wsmoFactory = wsmoManager.getWSMOFactory();

        dataFactory = wsmoManager.getDataFactory();
        
        Parser wsmlparserimpl = org.wsmo.factory.Factory.createParser(null);
        
        Ontology ontology = (Ontology) wsmlparserimpl.parse(new StringBuffer(test))[0];
        
        LogicalExpression query = leFactory.createLogicalExpression(
                "?x memberOf c", ontology);
        WSMLReasoner reasoner = BaseReasonerTest.getReasoner();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(WSMLReasonerFactory.PARAM_ALLOW_IMPORTS, 1);
        Set<Map<Variable, Term>> result = null;
        reasoner.registerOntology(ontology);
        result = reasoner.executeQuery((IRI) ontology.getIdentifier(), query);
        assertEquals(2, result.size());
        reasoner.deRegisterOntology((IRI) ontology.getIdentifier());
        
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
     * @throws Exception
     */
   public void testImportOntologyTestWithLocator() throws Exception {


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
}