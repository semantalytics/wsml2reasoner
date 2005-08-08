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

package org.deri.wsml.reasoner.impl.test;

import java.io.*;
import java.util.*;

import org.deri.wsml.reasoner.api.*;
import org.deri.wsml.reasoner.api.queryanswering.*;
import org.deri.wsml.reasoner.impl.DefaultWSMLReasonerFactory;
import org.deri.wsml.reasoner.impl.QueryAnsweringRequestImpl;
import org.deri.wsmo4j.io.parser.wsml.LogExprParserImpl;
import org.deri.wsmo4j.io.serializer.wsml.LogExprSerializerWSML;
import org.omwg.logexpression.LogicalExpression;
import org.omwg.ontology.Ontology;
import org.wsmo.common.TopEntity;
import org.wsmo.common.exception.InvalidModelException;
import org.wsmo.factory.Factory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.*;


public class TestWSMLCoreReasoner {

    public static WSMLReasoner wsmlCoreReasoner;
   
    
    private static void performQuery(Request r){
        QueryAnsweringResult result = (QueryAnsweringResult) wsmlCoreReasoner.execute(r);
        
        System.out.println("Found < "+result.size()+" > results to the query:");
        int i = 0;
        for(VariableBinding vBinding : result) {
            System.out.println("("+ (++i) +") -- " + vBinding.toString());
        }
    }
    
    /**
     * Loads a simple ontology from a file, constructs a simple conjunctive query
     * over the ontolgy, evaluates the query and prints the query answer to console.
     * @param args
     */
    public static void main(String[] args) {
        
        String ONTOLOGY_FILE = "examples/simple-graph.wsml";
        String PARSER_CLASS = "com.ontotext.wsmo4j.parser.WSMLParserImpl";

        // Set up factories for creating WSML elements 
        
        Map<String, String> leProperties = new HashMap<String, String>();
        leProperties.put(Factory.PROVIDER_CLASS,
         "org.deri.wsmo4j.logexpression.LogicalExpressionFactoryImpl");

        org.omwg.logexpression.LogicalExpressionFactory leFactory = 
            (org.omwg.logexpression.LogicalExpressionFactory) Factory.createLogicalExpressionFactory(leProperties);
        
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(Factory.PROVIDER_CLASS,
                "com.ontotext.wsmo4j.factory.WsmoFactoryImpl");
        properties.put(Parser.PARSER_LE_FACTORY, leFactory);
        WsmoFactory factory = Factory.createWsmoFactory(properties);
        
        
        // Set up WSML parser
        
        Map<String, Object> parserProperties = new HashMap<String, Object>();
        parserProperties.put(Parser.PARSER_WSMO_FACTORY, factory);
        parserProperties.put(Parser.PARSER_LE_FACTORY, leFactory);
        
        parserProperties.put(org.wsmo.factory.Factory.PROVIDER_CLASS,
                "com.ontotext.wsmo4j.parser.WSMLParserImpl");

        Parser wsmlparserimpl = org.wsmo.factory.Factory.createParser(parserProperties);
       
        // Set up serializer 
        
        Map<String, String> serializerProperties = new HashMap<String, String>();
        serializerProperties.put(org.wsmo.factory.Factory.PROVIDER_CLASS,
                "com.ontotext.wsmo4j.parser.WSMLSerializerImpl");

        Serializer ontologySerializer = org.wsmo.factory.Factory.createSerializer(serializerProperties);
        
        // Read simple ontology from file
        
        Ontology o = null;
        
        try {
            
            final Reader ontoReader = new FileReader(ONTOLOGY_FILE);
            
            
            final TopEntity[] identifiable = wsmlparserimpl.parse(ontoReader);
            if (identifiable.length > 0
                    && identifiable[0] instanceof Ontology) {
                o = (Ontology)identifiable[0];
            } else {
                return;
            }
            
        }
        catch (Exception e) {
            System.out.println("Unable to parse ontology: "
                    + e.getMessage());
            return;
        }
        
        // Print ontology in WSML
        
        try {
            System.out.println("WSML Ontology:\n");
            StringWriter sw = new StringWriter();
            ontologySerializer.serialize(new TopEntity[]{o}, sw);
            System.out.println(sw.toString());
            System.out.println("--------------\n\n");
        } catch (IOException e6) {
            // TODO Auto-generated catch block
            e6.printStackTrace();
        }
        
        org.omwg.logexpression.io.Serializer logExprSerializer = new LogExprSerializerWSML(o); 
        
        
        // Build simple conjunctive query in WSML 
        
        LogicalExpression qExpression1 = null;
        LogicalExpression qExpression2 = null;
        LogicalExpression qExpression3 = null;
  
        org.omwg.logexpression.io.Parser leParser = 
            LogExprParserImpl.getInstance(o); // construct queries over the same ontology
        
         
        try {
            String query1 = "scElement(?n) and path(?n,f) and path(f,?n)";
            qExpression1 = leParser.parse(query1);
                        
            String query2 = "path(?n,f)";
            qExpression2 = leParser.parse(query2);
            
            String query3 = "path(?n1,?n2)";
            qExpression3 = leParser.parse(query3);
            
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            return;
        } catch (ParserException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            return;
        } catch (InvalidModelException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            return;
        }

        
        // Print query  
        
        System.out.println("WSML Query (1):");
        System.out.println(logExprSerializer.serialize(qExpression1));
        System.out.println("\nWSML Query (2):");
        System.out.println(logExprSerializer.serialize(qExpression2));
        System.out.println("\nWSML Query (3):");
        System.out.println(logExprSerializer.serialize(qExpression3));
        System.out.println("--------------\n\n");
        
        // Now get a reasoner, create a query ansering request and print the result.
        try {
            
            Set<Ontology> ontos = new HashSet<Ontology>();
            ontos.add(o);
            QueryAnsweringRequest qaRequest1 = new QueryAnsweringRequestImpl(ontos, qExpression1);
            QueryAnsweringRequest qaRequest2 = new QueryAnsweringRequestImpl(ontos, qExpression2);
            QueryAnsweringRequest qaRequest3 = new QueryAnsweringRequestImpl(ontos, qExpression3);
            
            wsmlCoreReasoner = DefaultWSMLReasonerFactory.getFactory().getWSMLReasoner(WSMLReasonerFactory.WSMLVariant.WSML_CORE);
            
            System.out.println("Starting reasoner with query (1) ...");
            performQuery(qaRequest1);
            System.out.println("Finished query.");
            
            System.out.println("Starting reasoner with query (2) ...");
            performQuery(qaRequest2);
            System.out.println("Finished query.");
            
            System.out.println("Starting reasoner with query (3) ...");
            performQuery(qaRequest3);
            System.out.println("Finished query.");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        System.out.println("Finished!");
        
        
    }

}
