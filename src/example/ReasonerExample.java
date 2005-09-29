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
import java.util.*;

import org.deri.wsmo4j.logexpression.LogicalExpressionFactoryImpl;
import org.omwg.logexpression.LogicalExpression;
import org.omwg.logexpression.LogicalExpressionFactory;
import org.omwg.logexpression.terms.Variable;
import org.omwg.ontology.Ontology;
import org.wsml.reasoner.api.*;
import org.wsml.reasoner.api.queryanswering.*;
import org.wsml.reasoner.impl.*;
import org.wsmo.common.IRI;
import org.wsmo.common.TopEntity;
import org.wsmo.factory.Factory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.Parser;
import org.wsmo.wsml.Serializer;

/**
 * Usage Example for the wsml2Reasoner Framework
 * 
 * @author Holger Lausen, DERI Innsbruck
 */
public class ReasonerExample {

    /**
     * @param args none expected
     */
    public static void main(String[] args) {
        ReasonerExample ex = new ReasonerExample();
        ex.doTestRun();
    }

    /**
     * loads an Ontology and performs sample query
     */
    public void doTestRun() {
        Ontology exampleOntology = loadOntology("example/humanOntology.wsml");
        if (exampleOntology == null)
            return;

        // The details of creating a Query will be hidden in future
        LogicalExpression query = (LogicalExpression) new LogicalExpressionFactoryImpl(null)
                //.createLogicalExpression("?x memberOf Human", exampleOntology);
        		//.createLogicalExpression("?x memberOf Man", exampleOntology);
        		.createLogicalExpression("?x memberOf ?y", exampleOntology);
        		//.createLogicalExpression("?person [hasRelative hasValue ?relative] memberOf Human", exampleOntology);
        
        QueryAnsweringRequest qaRequest = 
                new QueryAnsweringRequestImpl(exampleOntology.getIdentifier().toString(), query);
        
        //get A reasoner
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(WSMLReasonerFactory.PARAM_BUILT_IN_REASONER,
                WSMLReasonerFactory.BuiltInReasoner.MINS);
        params.put(WSMLReasonerFactory.PARAM_WSML_VARIANT,
                WSMLReasonerFactory.WSMLVariant.WSML_CORE);
        WSMLReasoner reasoner = DefaultWSMLReasonerFactory.getFactory().getWSMLReasoner(
                params);
        
        // Register ontology
        reasoner.registerOntology(exampleOntology);
        
        // Execute query request
        Set<Map<Variable,Object>> result = reasoner.executeQuery(
                (IRI)exampleOntology.getIdentifier(), query);
        
        // print out the results:
        System.out.println("The query '" + query + "' has the following results:");
        for (Map<Variable,Object> vBinding : result) {
            for (Variable var : vBinding.keySet()) {
                System.out.print(var + ": " + vBinding.get(var));
            }
            System.out.println();
        }
    }

    /**
     * Utility Method to get the object model of a wsml ontology
     * 
     * @param file location of source file (It will be attemted to be loaded from
     *            current class path)
     * @return object model of ontology at file location
     */
    private Ontology loadOntology(String file) {
        // set up Factories
        Map<String, String> leProperties = new HashMap<String, String>();
        leProperties.put(Factory.PROVIDER_CLASS,
                "org.deri.wsmo4j.logexpression.LogicalExpressionFactoryImpl");

        LogicalExpressionFactory leFactory = (LogicalExpressionFactory) Factory
                .createLogicalExpressionFactory(leProperties);

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(Factory.PROVIDER_CLASS,
                "com.ontotext.wsmo4j.factory.WsmoFactoryImpl");
        properties.put(Parser.PARSER_LE_FACTORY, leFactory);
        WsmoFactory wsmoFactory = Factory.createWsmoFactory(null);

        // Set up WSML parser
        Map<String, Object> parserProperties = new HashMap<String, Object>();
        parserProperties.put(Parser.PARSER_WSMO_FACTORY, wsmoFactory);
        parserProperties.put(Parser.PARSER_LE_FACTORY, leFactory);

        parserProperties.put(org.wsmo.factory.Factory.PROVIDER_CLASS,
                "com.ontotext.wsmo4j.parser.WSMLParserImpl");

        Parser wsmlParser = org.wsmo.factory.Factory
                .createParser(parserProperties);

        InputStream is = this.getClass().getClassLoader().getResourceAsStream(
                file);
        try {
            final TopEntity[] identifiable = wsmlParser
                    .parse(new InputStreamReader(is));
            if (identifiable.length > 0 && identifiable[0] instanceof Ontology) {
                return (Ontology) identifiable[0];
            }
            else {
                System.out.println("First Element of file no ontology ");
                return null;
            }

        } catch (Exception e) {
            System.out.println("Unable to parse ontology: " + e.getMessage());
            return null;
        }

    }

    /**
     * small utility method for debugging
     * 
     * @param ont ontology to be serialized to string
     * @return string representation of ontology
     */
    private String toString(Ontology ont) {
        Map<String, String> serializerProperties = new HashMap<String, String>();
        serializerProperties.put(org.wsmo.factory.Factory.PROVIDER_CLASS,
                "com.ontotext.wsmo4j.parser.WSMLSerializerImpl");

        Serializer wsmlSerializer = org.wsmo.factory.Factory
                .createSerializer(serializerProperties);

        StringBuffer str = new StringBuffer();
        wsmlSerializer.serialize(new TopEntity[] { ont }, str);
        return str.toString();
    }
}
