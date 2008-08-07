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

import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Instance;
import org.omwg.ontology.Ontology;
import org.wsml.reasoner.api.WSMLReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory;
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
 *  $Source: /home/richi/temp/w2r/wsml2reasoner/src/example/Kaon2DLReasonerExample.java,v $,
 * </pre>
 *
 * @author Nathalie Steinmetz, DERI Innsbruck
 * @version $Revision: 1.2 $ $Date: 2007-03-01 11:37:02 $
 */
public class Kaon2DLReasonerExample {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Kaon2DLReasonerExample ex = new Kaon2DLReasonerExample();
        try {
            ex.doTestRun();
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
                WSMLReasonerFactory.BuiltInReasoner.KAON2);
        WSMLReasoner reasoner = DefaultWSMLReasonerFactory.getFactory()
                .createWSMLDLReasoner(params);     
        
//      Register ontology
        reasoner.registerOntology(exampleOntology);

        // print out if the registered ontology is satisfiable
        System.out.println("\n----------------------\n");
        System.out.println("Ontology is consistent: " + reasoner.isSatisfiable());	
        
        // print out if a specified concept is satisfiable
        System.out.println("\n----------------------\n");
        String le = "";
        System.out.println("Is concept \"Machine\" satisfiable? " + 
        		reasoner.entails( 
        				leFactory.createLogicalExpression("Machine", exampleOntology)));
        
        // print out if a specified logical expression is satisfiable
        System.out.println("\n----------------------\n");
        le = "?x memberOf Pet and ?x memberOf DomesticAnimal.";
        System.out.println("Is the following logical expression satisfiable? \n\"" + le +
        		"\" \n" + reasoner.entails( 
        				leFactory.createLogicalExpression(le, exampleOntology)));
        
        // print out if a specified logical expression is satisfiable
        System.out.println("\n----------------------\n");
        le = "?x memberOf Man and ?x memberOf Woman."; 
        System.out.println("Is the following logical expression satisfiable? \n\"" + le +
        		"\" \n" + reasoner.entails( 
        				leFactory.createLogicalExpression(le, exampleOntology)));
        
        // get all instances of woman concept
        System.out.println("\n----------------------\n");
        System.out.println("Get all instances of concept Woman:");
		Set<Instance> set = reasoner.getInstances( 
				wsmoFactory.createConcept(
				wsmoFactory.createIRI(ns + "Woman")));
        for (Instance instance : set) 
        	System.out.println(((IRI) instance.getIdentifier()).getLocalName().toString());
        
        // get one specific instance's age
        System.out.println("\n----------------------\n");
        System.out.println("All information about instance Mary:");
        Set<Entry<IRI, Set<Term>>> entrySet = reasoner.getInferingAttributeValues(
        		 
				wsmoFactory.createInstance(wsmoFactory.createIRI(ns + "Mary"))).entrySet();
		for (Entry<IRI, Set<Term>> entry : entrySet) {
			System.out.println(entry.getKey().getLocalName().toString());
			Set<Term> IRIset = entry.getValue();
			for (Term value : IRIset) 
				System.out.println("   value: " + ((IRI) value).getLocalName().toString());
		}
		Set<Entry<IRI, Set<Term>>> entrySetTerm = reasoner.getConstraintAttributeValues(
				 
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
 * Revision 1.1  2007/01/10 16:08:28  nathalie
 * added example for kaon2 dl reasoning
 *
 *
 */