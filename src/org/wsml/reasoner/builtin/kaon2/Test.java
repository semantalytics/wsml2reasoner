/*
 wsmo4j - a WSMO API and Reference Implementation

 Copyright (c) 2005, University of Innsbruck, Austria

 This library is free software; you can redistribute it and/or modify it under
 the terms of the GNU Lesser General Public License as published by the Free
 Software Foundation; either version 2.1 of the License, or (at your option)
 any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 details.
 You should have received a copy of the GNU Lesser General Public License along
 with this library; if not, write to the Free Software Foundation, Inc.,
 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.wsml.reasoner.builtin.kaon2;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;

import org.semanticweb.kaon2.api.*;
import org.semanticweb.kaon2.api.logic.*;
import org.semanticweb.kaon2.api.owl.elements.*;
import org.semanticweb.kaon2.api.reasoner.*;
import org.semanticweb.kaon2.api.reasoner.SubsumptionHierarchy.Node;
import org.wsml.reasoner.serializer.owl.WsmlOwlSerializer;
import org.wsmo.common.TopEntity;
import org.wsmo.factory.Factory;
import org.wsmo.wsml.Parser;
import org.wsmo.wsml.Serializer;

/**
 * Interface or class description
 *
 * <pre>
 *    Created on 22.12.2006
 *    Committed by $Author: nathalie $
 *    $Source: /home/richi/temp/w2r/wsml2reasoner/src/org/wsml/reasoner/builtin/kaon2/Test.java,v $,
 * </pre>
 *
 * @author AuthorFirstName AuthorLastName
 * @author ContributorFirstName ContributorLastName
 * @author ContributorFirstName ContributorLastName
 * @version $Revision: 1.2 $ $Date: 2007-01-10 11:50:38 $
 */
public class Test {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        Parser parser = Factory.createParser(null);
        org.omwg.ontology.Ontology wsmlOnt = 
        	(org.omwg.ontology.Ontology) parser.parse(new StringBuffer(
        			"namespace _\"urn:foo/\" ontology o " +
        				"concept c subConceptOf b " +
        				"  hasChild impliesType bc "))[0];

        Serializer serializer = new WsmlOwlSerializer(null);
        StringBuffer buf = new StringBuffer();
        serializer.serialize(new TopEntity[] {wsmlOnt}, buf);

        System.out.println(buf);
        InputStream in = new ByteArrayInputStream(buf.toString().getBytes());

        KAON2Connection connection = KAON2Manager.newConnection();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(KAON2Connection.LOAD_FROM_INPUT_STREAM, in);
        
        Ontology ontology = connection.openOntology(
        		"http://example.com/a", map);
        Reasoner reasoner = ontology.createReasoner();
        
//System.out.println(ontology.getOntologyURI().toString());
        SubsumptionHierarchy hierarchy = reasoner.getSubsumptionHierarchy();
        for (Node n : hierarchy){
            System.out.println(n);
            for (OWLClass c : n.getOWLClasses()){
                System.out.println("-" + c.getURI());
            }
            for (Node n2 : n.getDescendantNodes()){
            	for (OWLClass c : n2.getOWLClasses()) {
            		 System.out.println("--" + c.getURI());
            	}
            }
        }
        
        OWLClass classB = KAON2Manager.factory().owlClass("urn:foo/b");
        Set<Description> subDescriptions = classB.getSubDescriptions(ontology);
        System.out.println("The subclasses of '" + classB.getURI() + "' are:");
        for (Description subDescription : subDescriptions)
            if (subDescription instanceof OWLClass) {
                OWLClass subClass=(OWLClass)subDescription;
                System.out.println("    "+subClass.getURI());
            }
        System.out.println();

        Variable X = KAON2Manager.factory().variable("X");
        Variable Y = KAON2Manager.factory().variable("Y");
        
        ObjectProperty hasChild = KAON2Manager.factory().objectProperty("urn:foo/hasChild");
        System.out.println("hasChild: " + hasChild);
        Query subClassAll = reasoner.createQuery(new Literal[] {
                KAON2Manager.factory().literal(true, hasChild ,new Term[] {X,Y})
            },new Variable[] {X,Y});
        
        subClassAll = reasoner.createQuery(Namespaces.INSTANCE, "SELECT ?x WHERE { ?x rdfs:subClassOf <urn:foo/b> }");
        
//        ObjectProperty sub = KAON2Manager.factory().objectProperty(Namespaces.OWL_NS + "subClassOf");
//        System.out.println("sub--" + sub);
//        
//        // We now create the query object. A query consists of the following things:
//        //
//        // - a list of literals defining the query (they are created as usual)
//        // - a list of distinguished variables, i.e. the variables that will be returned
//        
//        Query subClassAll=reasoner.createQuery(new Literal[] {
//            KAON2Manager.factory().literal(true,sub,new Term[] {X,Y})
//        },new Variable[] {X,Y});
        
        System.out.println("QUERY--" + subClassAll.getQueryFormula().toString());
        
//        subClassAll=reasoner.createQuery(sub);
        
        System.out.println("QUERY--" + subClassAll.getQueryFormula().toString());
        System.out.println("QUERY--" + subClassAll);


        // Creating the query has the effect of compiling it. A single query can be executed then several times.
        // A query is executed by invoking the Query.open() method.
        System.out.println();
        System.out.println("The subclass statements:");
        System.out.println("---------------------------------------------------");

        subClassAll.open();
        // We now iterate over the query results.
        System.out.println(subClassAll.getNumberOfTuples());
        while (!subClassAll.afterLast()) {
        	System.out.println("hi");
            // A query result is a set of tuples. The values in each tuple correspond to the distinguished variables.
            // In the above example, the distinguished variables are [X,Y]; this means that the first object in
            // the tuple is the value for the X variable, and the second one is the value for the Y variable.
            Object[] tupleBuffer=subClassAll.tupleBuffer();
            System.out.println("'"+tupleBuffer[0].toString()+"' subclasst '"+tupleBuffer[1].toString()+"'.");
            subClassAll.next();
        }

        System.out.println("---------------------------------------------------");

        // If a query has been successfully opened, it should also be closed; otherwise, a resource leak occurs.
        subClassAll.close();

        // When a query is not needed any more, it should be disposed of. Forgetting to do so will result in
        // a fairly serious memory leak.
        subClassAll.dispose();

        // Reasoners have to be disposed off after they are not used any more. Forgetting to do so will result in
        // a fairly serious memory leak!
        reasoner.dispose();

        // Don't forget to close the connection!
        connection.close();

    }
}

