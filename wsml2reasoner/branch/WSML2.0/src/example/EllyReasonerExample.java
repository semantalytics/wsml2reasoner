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
package example;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.omwg.ontology.Ontology;
import org.sti2.wsmo4j.factory.WsmlFactoryContainer;
import org.wsml.reasoner.api.DLReasoner;
import org.wsml.reasoner.impl.DefaultWSMLReasonerFactory;
import org.wsmo.common.TopEntity;
import org.wsmo.factory.FactoryContainer;
import org.wsmo.wsml.Parser;

import com.ontotext.wsmo4j.parser.wsml.WsmlParser;

/**
 * Usage Example for the wsml2Reasoner Framework
 * 
 * @author Holger Lausen, DERI Innsbruck
 */
public class EllyReasonerExample {

    /**
     * @param args
     *            none expected
     */
    public static void main(String[] args) {
        EllyReasonerExample ex = new EllyReasonerExample();
        try {
            ex.doTestRun();
            System.exit(0);
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * loads an Ontology and performs sample query
     */
    public void doTestRun() throws Exception {
    	FactoryContainer container = new WsmlFactoryContainer();
    	
        Ontology exampleOntology = loadOntology("example/locations.wsml");
        if (exampleOntology == null)
            return;

        // get A reasoner
        DLReasoner reasoner = DefaultWSMLReasonerFactory.getFactory().createDL2Reasoner(container);
        
        // Register ontology
        reasoner.registerOntology(exampleOntology);
        // reasoner.registerOntologyNoVerification(exampleOntology);

        /* **********
         * Query
         * **********/
        
//      String queryString = "?x memberOf ?y";
//      String queryString = "?x = ?y";
//      String queryString = "?x = ?y :- ?x[name hasValue ?n1] and ?y[name hasValue ?n2] and ?n1=?n2.";
//
//      LogicalExpression query = new WsmlLogicalExpressionParser(exampleOntology, container).parse(queryString);

//        // Execute query request
//        Set<Map<Variable, Term>> result = reasoner.executeQuery(query);
//
//        // print out the results:
//        System.out.println("The query '" + query + "' has the following results:");
//        for (Map<Variable, Term> vBinding : result) {
//            for (Variable var : vBinding.keySet()) {
//                System.out.print(var + ": " + termToString(vBinding.get(var), exampleOntology) + "\t ");
//            }
//            System.out.println();
//        }
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
		Parser wsmlParser = new WsmlParser();

        InputStream is = this.getClass().getClassLoader().getResourceAsStream(file);
        try {
            final TopEntity[] identifiable = wsmlParser.parse(new InputStreamReader(is));
            if (identifiable.length > 0 && identifiable[0] instanceof Ontology) {
                return (Ontology) identifiable[0];
            }
            else {
                System.out.println("First Element of file no ontology ");
                return null;
            }

        }
        catch (Exception e) {
            System.out.println("Unable to parse ontology: " + e.getMessage());
            return null;
        }

    }

}