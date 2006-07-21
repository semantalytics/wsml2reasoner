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

import org.omwg.ontology.Instance;
import org.omwg.ontology.Ontology;
import org.wsml.reasoner.api.WSMLDLReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.impl.DefaultWSMLReasonerFactory;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsmo.common.IRI;
import org.wsmo.common.TopEntity;
import org.wsmo.factory.Factory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.Parser;

/**
 * Usage example for the wsml2reasoner framework with a wsml dl 
 * ontology.
 *
 * <pre>
 *  Created on July 19rd, 2006
 *  Committed by $Author: nathalie $
 *  $Source: /home/richi/temp/w2r/wsml2reasoner/src/example/DLReasonerExample.java,v $,
 * </pre>
 *
 * @author Nathalie Steinmetz, DERI Innsbruck
 * @version $Revision: 1.2 $ $Date: 2006-07-21 17:01:43 $
 */
public class DLReasonerExample {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DLReasonerExample ex = new DLReasonerExample();
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
    	
    	String ns = "http://www.example.org/ontologies/example#";
        
    	Ontology exampleOntology = loadOntology("example/wsml2owlExample.wsml");
        if (exampleOntology == null)
            return;

        // get a reasoner
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(WSMLReasonerFactory.PARAM_BUILT_IN_REASONER,
                WSMLReasonerFactory.BuiltInReasoner.PELLET);
        WSMLDLReasoner reasoner = DefaultWSMLReasonerFactory.getFactory()
                .createWSMLDLReasoner(params);     
        
        // Register ontology
        reasoner.registerOntologyNoVerification(exampleOntology);

        System.out.println("Ontology is consistent: " + reasoner.isSatisfiable(
        		(IRI) exampleOntology.getIdentifier()));
        
        // print class hierarchy with individuals
        System.out.println("\n----------------------\n");
        reasoner.printClassTree();
    	
        // get all instances of woman concept
        System.out.println("\n----------------------\n");
		Set<Instance> set = reasoner.getInstances(null, wsmoFactory.createConcept(
				wsmoFactory.createIRI(ns + "Woman")));
        for (Instance instance : set) 
        	System.out.println(instance.getIdentifier().toString());
        
        // get one specific instance's age
        System.out.println("\n----------------------\n");
        String age = reasoner.getConstraintAttributeValue(
        		wsmoFactory.createInstance(wsmoFactory.createIRI(ns + "Anna")),
        		wsmoFactory.createIRI(ns + "ageOfHuman"));
        System.out.println(age);
        
        // get all info about one specific instance
        System.out.println("\n----------------------\n");
        Set<Entry<IRI, Set<IRI>>> entrySet = reasoner.getInferingAttributeValues(
				wsmoFactory.createInstance(wsmoFactory.createIRI(ns + "Mary"))).entrySet();
		for (Entry<IRI, Set<IRI>> entry : entrySet) {
			System.out.println(entry.getKey().toString());
			Set<IRI> IRIset = entry.getValue();
			for (IRI value : IRIset) 
				System.out.println("   value: " + value.toString());
		}
		entrySet = reasoner.getConstraintAttributeValues(
				wsmoFactory.createInstance(wsmoFactory.createIRI(ns + "Mary"))).entrySet();
		for (Entry<IRI, Set<IRI>> entry : entrySet) {
			System.out.println(entry.getKey().toString());
			Set<IRI> IRIset = entry.getValue();
			for (IRI value : IRIset) 
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
 * Revision 1.1  2006/07/20 17:50:23  nathalie
 * integration of the pellet reasoner
 *
 *
 */