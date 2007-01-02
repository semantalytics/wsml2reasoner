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
import org.semanticweb.kaon2.api.owl.axioms.ClassMember;
import org.semanticweb.kaon2.api.owl.elements.*;
import org.semanticweb.kaon2.api.reasoner.*;
import org.semanticweb.kaon2.api.reasoner.SubsumptionHierarchy.Node;
import org.wsml.reasoner.serializer.owl.WsmlOwlSerializer;
import org.wsmo.common.TopEntity;
import org.wsmo.factory.Factory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.Parser;
import org.wsmo.wsml.Serializer;

/**
 * Interface or class description
 *
 * <pre>
 *    Created on 22.12.2006
 *    Committed by $Author: hlausen $
 *    $Source: /home/richi/temp/w2r/wsml2reasoner/src/org/wsml/reasoner/builtin/kaon2/Test.java,v $,
 * </pre>
 *
 * @author AuthorFirstName AuthorLastName
 * @author ContributorFirstName ContributorLastName
 * @author ContributorFirstName ContributorLastName
 * @version $Revision: 1.1 $ $Date: 2007-01-02 11:30:50 $
 */
public class Test {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        WsmoFactory f = Factory.createWsmoFactory(null);
        Parser p = Factory.createParser(null);
        org.omwg.ontology.Ontology o = (org.omwg.ontology.Ontology) p
                .parse(new StringBuffer(
                        "namespace _\"urn:foo/\" ontology o concept c subConceptOf b "))[0];

        Serializer s = new WsmlOwlSerializer(null);
        StringBuffer buf = new StringBuffer();
        s.serialize(new TopEntity[] { o }, buf);

        System.out.println(buf);
        InputStream in = new ByteArrayInputStream(buf.toString().getBytes());

        KAON2Connection connection = KAON2Manager.newConnection();
        Map<String, Object> m = new HashMap<String, Object>();
        m.put(KAON2Connection.LOAD_FROM_INPUT_STREAM, in);
        Ontology ontology = connection.openOntology(
                "http://example.com/a",m);
        Reasoner reasoner = ontology.createReasoner();

        SubsumptionHierarchy ss = reasoner.getSubsumptionHierarchy();
        for (Node n : ss){
            System.out.println(n);
            for (OWLClass c : n.getOWLClasses()){
                System.out.println("-"+c.getURI());
            }
        }

        Variable X = KAON2Manager.factory().variable("X");
        Variable Y = KAON2Manager.factory().variable("Y");
        OWLClass c = KAON2Manager.factory().owlClass("urn:foo/c");

        // We now create the query object. A query consists of the following things:
        //
        // - a list of literals definint the query (they are created as usual)
        // - a list of distinguished variables, i.e. the variables that will be returned
        ObjectProperty sub = KAON2Manager.factory().objectProperty(Namespaces.OWL_NS+"subClassOf");

        System.out.println("sub--"+sub);
        Query subClassAll=reasoner.createQuery(new Literal[] {
            KAON2Manager.factory().literal(true,sub,new Term[] {X,Y})
        },new Variable[] { X,Y});

        subClassAll=reasoner.createQuery(sub);



        System.out.println("QUERY--"+subClassAll);


        // Creating the query has the effect of compiling it. A single query can be executed then several times.
        // A query is executed by invoking the Query.open() method.
        System.out.println();
        System.out.println("The subclass statements:");
        System.out.println("---------------------------------------------------");

        subClassAll.open();
        // We now iterate over the query results.
        while (!subClassAll.afterLast()) {
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
