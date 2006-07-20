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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.omwg.ontology.Instance;
import org.omwg.ontology.Ontology;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.util.OWLManager;
import org.wsml.reasoner.api.WSMLDLReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.impl.DefaultWSMLReasonerFactory;
import org.wsmo.common.IRI;
import org.wsmo.common.TopEntity;
import org.wsmo.factory.Factory;
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
 * @version $Revision: 1.1 $ $Date: 2006-07-20 17:50:23 $
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
        reasoner.printClassTree();
    	
        OWLClass clazz = null;
		try {
			clazz = OWLManager.getOWLConnection().getDataFactory().getOWLClass(new URI(
					"http://www.example.org/ontologies/example#Woman"));
		} catch (OWLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		Set set = reasoner.getAllInstances(clazz);
//
//        Iterator it = reasoner.getAllInstances(clazz).iterator();
//        while (it.hasNext()) {
//        	System.out.println(it.next().toString());
//        }
        
//    	// get all instances of Woman class
//        OWLClass Woman = OWLManager.getOWLConnection().getDataFactory().
//        		getOWLClass(new URI("http://www.example.org/ontologies/example#Woman"));
//    	Set individuals = reasoner.getInstances(null,Woman);
//    	for(Iterator i = individuals.iterator(); i.hasNext(); ) {
//    	    OWLIndividual ind = (OWLIndividual) i.next();
//    	    
//    	    // get the info about this specific individual
//    	    String name = (String) reasoner.getPropertyValue(ind, foafName).getValue();
//    	    OWLClass type = reasoner.typeOf(ind);
//    	    OWLIndividual homepage = reasoner.getPropertyValue(ind, workHomepage);

//        // execute SPARQL query
//        QueryResults results = reasoner.executeQuery(
//        		"BASE    <http://www.example.org/ontologies/> " +
//        		"SELECT  $individual WHERE {?individual <example#hasChild> ?ind2}");
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
 *
 */