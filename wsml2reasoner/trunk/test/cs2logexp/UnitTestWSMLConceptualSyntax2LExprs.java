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
package cs2logexp;


import java.io.*;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.deri.wsml.reasoner.normalization.AxiomatizationNormalizer;
import org.omwg.logexpression.LogicalExpressionFactory;
import org.omwg.ontology.Ontology;
import org.wsmo.common.TopEntity;
import org.wsmo.factory.Factory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.Parser;
import org.wsmo.wsml.Serializer;

public class UnitTestWSMLConceptualSyntax2LExprs extends TestCase{

    String PARSER_CLASS = "com.ontotext.wsmo4j.parser.WSMLParserImpl";
	String LOGEXP_CLASS = "org.deri.wsmo4j.logexpression.LogicalExpressionFactoryImpl";
	String WSMO_FACTORY = "com.ontotext.wsmo4j.factory.WsmoFactoryImpl";
	String PARSER_IMPL = "com.ontotext.wsmo4j.parser.WSMLParserImpl";
	String SERIALIZER_IMPL = "com.ontotext.wsmo4j.parser.WSMLSerializerImpl";
	LogicalExpressionFactory leFactory;
	WsmoFactory factory;
	Parser parser;
	Serializer serializer;
	/**
     * Loads a simple ontology from a file, constructs a simple 
     * conjunctive query over the ontolgy, evaluates the query 
     * and prints the query answer to console.
     * @param args
     */
	
	
	protected void setUp(){
        // Set up factories for creating WSML elements 
        Map<String, String> leProperties = new HashMap<String, String>();
        leProperties.put(Factory.PROVIDER_CLASS, LOGEXP_CLASS);
		leFactory = (LogicalExpressionFactory) Factory.createLogicalExpressionFactory(leProperties);
        
        Map<String, Object> properties = new HashMap<String, Object>();
        //properties.put(Factory.PROVIDER_CLASS,WSMO_FACTORY);               
        //properties.put(Parser.PARSER_LE_FACTORY, leFactory);
		factory = Factory.createWsmoFactory(properties);

		// Set up WSML parser
        Map<String, Object> parserProperties = new HashMap<String, Object>();
        parserProperties.put(Parser.PARSER_WSMO_FACTORY, factory);
        parserProperties.put(Parser.PARSER_LE_FACTORY, leFactory);
        parserProperties.put(Factory.PROVIDER_CLASS,PARSER_IMPL);
        parser = Factory.createParser(parserProperties);
       
        // Set up serializer 
        Map<String, String> serializerProperties = new HashMap<String, String>();
        serializerProperties.put(org.wsmo.factory.Factory.PROVIDER_CLASS,SERIALIZER_IMPL);
		serializer = org.wsmo.factory.Factory.createSerializer(serializerProperties);
	}
	
    /**
     * no real unit test yet but only system.out...
     * @throws Exception
     */
	public void testConceptual2LogExp()throws Exception{        
        String ONTOLOGY_FILE = "examples/humanOntology.wsml";
        
        Ontology o = null;
		// Read simple ontology from file
        
        //try from classpath
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(ONTOLOGY_FILE);
        if (is != null){
            o = (Ontology)parser.parse(new InputStreamReader(is))[0];
        }else{//classpath has filed try from normal path...
            Reader ontoReader = new FileReader(ONTOLOGY_FILE);
            o = (Ontology)parser.parse(ontoReader)[0];
        }
        
        assertNotNull(o);
            
        // Print ontology in WSML
        System.out.println("WSML Ontology:\n");
        StringWriter sw = new StringWriter();
        serializer.serialize(new TopEntity[]{o}, sw);
        System.out.println(sw.toString());
        System.out.println("--------------\n\n");
        
        System.out.println("\n\nTransforming ontology to axioms only ...");
        
        AxiomatizationNormalizer cs2le = new AxiomatizationNormalizer();
        Ontology normalizedOntology = cs2le.normalize(o);
        
        System.out.println("... finished.");
        
        System.out.println("Normalized WSML Ontology:\n");
        StringWriter sw1 = new StringWriter();
        serializer.serialize(new TopEntity[]{normalizedOntology}, sw1);
        System.out.println(sw1.toString());
        System.out.println("--------------\n\n");
    }
	
}
