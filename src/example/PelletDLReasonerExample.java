package example;
/**
 * WSML Reasoner Implementation.
 *
 * Copyright (c) 2005, University of Innsbruck, Austria.
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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Instance;
import org.omwg.ontology.Ontology;
import org.wsml.reasoner.api.WSMLReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.builtin.pellet.PelletFacade;
import org.wsml.reasoner.impl.DefaultWSMLReasonerFactory;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsmo.common.IRI;
import org.wsmo.common.TopEntity;
import org.wsmo.factory.Factory;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.Parser;

/**
 * Usage example for the wsml2reasoner framework with a wsml dl 
 * ontology.
 *
 * <pre>
 *  Created on July 19rd, 2006
 *  Committed by $Author: nathalie $
 *  $Source: /home/richi/temp/w2r/wsml2reasoner/src/example/PelletDLReasonerExample.java,v $,
 * </pre>
 *
 * @author Nathalie Steinmetz, DERI Innsbruck
 * @version $Revision: 1.1 $ $Date: 2007-01-10 16:08:28 $
 */
public class PelletDLReasonerExample {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PelletDLReasonerExample ex = new PelletDLReasonerExample();
        try {
            ex.doTestRun();
            ex.debugOntology();
            System.exit(0);
        } catch (Throwable e) {
            e.printStackTrace();
        }
	}
	
	/**
     * loads an Ontology and performs sample query
     */
    public void doTestRun() throws Exception {
    	
    	WsmoFactory wsmoFactory = new WSMO4JManager().getWSMOFactory();
    	
    	LogicalExpressionFactory leFactory = new WSMO4JManager().getLogicalExpressionFactory();
    	
    	String ns = "http://www.example.org/ontologies/example#";
        
    	Ontology exampleOntology = loadOntology("example/wsml2owlExample.wsml");
        if (exampleOntology == null)
            return;

        // get a reasoner
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(WSMLReasonerFactory.PARAM_BUILT_IN_REASONER,
                WSMLReasonerFactory.BuiltInReasoner.PELLET);
        WSMLReasoner reasoner = DefaultWSMLReasonerFactory.getFactory()
                .createWSMLDLReasoner(params);     
        
        // Register ontology
        reasoner.registerOntology(exampleOntology);

        // print out if the registered ontology is satisfiable
        System.out.println("\n----------------------\n");
        System.out.println("Ontology is consistent: " + reasoner.isSatisfiable(
        		(IRI) exampleOntology.getIdentifier()));	
        
        // print out if a specified concept is satisfiable
        System.out.println("\n----------------------\n");
        String le = "";
        System.out.println("Is concept \"Machine\" satisfiable? " + 
        		reasoner.entails((IRI) exampleOntology.getIdentifier(), 
        				leFactory.createLogicalExpression("Machine", exampleOntology)));
        
        // print out if a specified logical expression is satisfiable
        System.out.println("\n----------------------\n");
        le = "?x memberOf Pet and ?x memberOf DomesticAnimal.";
        System.out.println("Is the following logical expression satisfiable? \n\"" + le +
        		"\" \n" + reasoner.entails((IRI) exampleOntology.getIdentifier(), 
        				leFactory.createLogicalExpression(le, exampleOntology)));
        
        // print out if a specified logical expression is satisfiable
        System.out.println("\n----------------------\n");
        le = "?x memberOf Man and ?x memberOf Woman."; 
        System.out.println("Is the following logical expression satisfiable? \n\"" + le +
        		"\" \n" + reasoner.entails((IRI) exampleOntology.getIdentifier(), 
        				leFactory.createLogicalExpression(le, exampleOntology)));
        
        // get all instances of woman concept
        System.out.println("\n----------------------\n");
        System.out.println("Get all instances of concept Woman:");
		Set<Instance> set = reasoner.getInstances((IRI) exampleOntology.getIdentifier(), 
				wsmoFactory.createConcept(
				wsmoFactory.createIRI(ns + "Woman")));
        for (Instance instance : set) 
        	System.out.println(instance.getIdentifier().toString());
        
        // get one specific instance's age
        System.out.println("\n----------------------\n");
        System.out.println("Get age of instance Anna: ");
        String age = reasoner.getConstraintAttributeValue((IRI) exampleOntology.getIdentifier(), 
        		wsmoFactory.createInstance(wsmoFactory.createIRI(ns + "Anna")),
        		wsmoFactory.createIRI(ns + "ageOfHuman"));
        System.out.println(age);
        
        // get all info about one specific instance
        System.out.println("\n----------------------\n");
        System.out.println("All information about instance Mary:");
        Set<Entry<IRI, Set<IRI>>> entrySet = reasoner.getInferingAttributeValues(
        		(IRI) exampleOntology.getIdentifier(), 
				wsmoFactory.createInstance(wsmoFactory.createIRI(ns + "Mary"))).entrySet();
		for (Entry<IRI, Set<IRI>> entry : entrySet) {
			System.out.println(entry.getKey().getLocalName().toString());
			Set<IRI> IRIset = entry.getValue();
			for (IRI value : IRIset) 
				System.out.println("   value: " + value.getLocalName().toString());
		}
		Set<Entry<IRI, Set<Term>>> entrySetTerm = reasoner.getConstraintAttributeValues(
				(IRI) exampleOntology.getIdentifier(), 
				wsmoFactory.createInstance(wsmoFactory.createIRI(ns + "Mary"))).entrySet();
		for (Entry<IRI, Set<Term>> entry : entrySetTerm) {
			System.out.println(entry.getKey().getLocalName().toString());
			Set<Term> termSet = entry.getValue();
			for (Term value : termSet) 
				System.out.println("   value: " + value.toString());
		}

//        // execute simple SPARQL query
//		System.out.println("\n----------------------\n");
//        QueryResults results = reasoner.executeQuery(
//        		"BASE    <http://www.example.org/ontologies/> " +
//        		"SELECT  ?ind WHERE {?ind <example#hasChild> ?ind2}");
//        TableData table = results.toTable();
//        StringWriter writer = new StringWriter();
//    	table.print(writer);
//    	System.out.println(writer.toString());
    }
    
    /**
     *  Loads an ontology and "debugs" the owl ontology that results after the 
     *  transformation from WSML to OWL. Pellet prints the class tree of the 
     *  OWL ontology.
     */
    public void debugOntology() throws Exception {
        
    	Ontology exampleOntology = loadOntology("example/wsml2owlExample.wsml");
        if (exampleOntology == null)
            return;

        // get a reasoner
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(WSMLReasonerFactory.PARAM_BUILT_IN_REASONER,
                WSMLReasonerFactory.BuiltInReasoner.PELLET);
        WSMLReasoner reasoner = DefaultWSMLReasonerFactory.getFactory()
                .createWSMLDLReasoner(params);     
    
        // debug ontology - Pellet prints a class tree of the outcoming owl ontology
		Logger log = Logger.getLogger(PelletFacade.class);
		log.setLevel(Level.DEBUG);
		reasoner.registerOntology(exampleOntology);
    }
    
    /**
     * Utility Method to get the object model of a wsml ontology
     * 
     * @param file
     *            location of source file (It will be attemted to be loaded from
     *            current class path)
     * @return object model of ontology at file location
     */
    private Ontology loadOntology(String file) {
        Parser wsmlParser = Factory.createParser(null);

        InputStream is = this.getClass().getClassLoader().getResourceAsStream(
                file);
        try {
            final TopEntity[] identifiable = wsmlParser
                    .parse(new InputStreamReader(is));
            if (identifiable.length > 0 && identifiable[0] instanceof Ontology) {
                return (Ontology) identifiable[0];
            } else {
                System.out.println("First Element of file no ontology ");
                return null;
            }

        } catch (Exception e) {
            System.out.println("Unable to parse ontology: " + e.getMessage());
            return null;
        }

    }
	
}
/*
 * $Log: not supported by cvs2svn $
 * Revision 1.7  2006/09/19 13:47:28  nathalie
 * added example for using Pellet Logger printClassTree function
 *
 * Revision 1.6  2006/08/31 12:36:00  nathalie
 * removed methods from WSMLDLReasoner interface to the WSMLReasoner interface. Replaced some methods by entails() and groundQuery() methods.
 *
 * Revision 1.5  2006/08/21 07:51:10  nathalie
 * *** empty log message ***
 *
 * Revision 1.4  2006/08/08 10:14:28  nathalie
 * implemented support for registering multiple ontolgies at wsml-dl reasoner
 *
 * Revision 1.3  2006/07/21 21:07:03  nathalie
 * updated dl reasoner example
 *
 * Revision 1.2  2006/07/21 17:01:43  nathalie
 * updated dl reasoner example
 *
 * Revision 1.1  2006/07/20 17:50:23  nathalie
 * integration of the pellet reasoner
 *
 *
 */