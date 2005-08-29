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
package test;


import java.io.*;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.omwg.logexpression.LogicalExpressionFactory;
import org.wsmo.factory.Factory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.Parser;
import org.wsmo.wsml.Serializer;

/**
 * Base Test for Reasoner Framework
 *
 * <pre>
 * Created on Aug 17, 2005
 * Committed by $Author: hlausen $
 * $Source: /home/richi/temp/w2r/wsml2reasoner/test/test/BaseTest.java,v $,
 * </pre>
 *
 * @author Holger Lausen
 * @version $Revision: 1.4 $ $Date: 2005-08-29 20:57:12 $
 */
public class BaseTest extends TestCase {
	public LogicalExpressionFactory leFactory;
	public WsmoFactory wsmoFactory; 
	//human reabale parser
	public Parser wsmlParser;
	//human readable serialize
	public Serializer wsmlSerializer;
	
	/**
	 * initializes commonly used elemenets leFactory, wsmoFactory, parser and serializer
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp()throws Exception{
		super.setUp();
        // Set up factories for creating WSML elements
        Map<String, String> leProperties = new HashMap<String, String>();
        leProperties.put(Factory.PROVIDER_CLASS,
                "org.deri.wsmo4j.logexpression.LogicalExpressionFactoryImpl");

        leFactory = (LogicalExpressionFactory) Factory
        		.createLogicalExpressionFactory(leProperties);

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(Factory.PROVIDER_CLASS,
                "com.ontotext.wsmo4j.factory.WsmoFactoryImpl");
        properties.put(Parser.PARSER_LE_FACTORY, leFactory);
        wsmoFactory = Factory.createWsmoFactory(properties);

        // Set up WSML parser

        Map<String, Object> parserProperties = new HashMap<String, Object>();
        parserProperties.put(Parser.PARSER_WSMO_FACTORY, wsmoFactory);
        parserProperties.put(Parser.PARSER_LE_FACTORY, leFactory);

        parserProperties.put(org.wsmo.factory.Factory.PROVIDER_CLASS,
                "com.ontotext.wsmo4j.parser.WSMLParserImpl");

        wsmlParser = org.wsmo.factory.Factory
                .createParser(parserProperties);

        // Set up serializer

        Map<String, String> serializerProperties = new HashMap<String, String>();
        serializerProperties.put(org.wsmo.factory.Factory.PROVIDER_CLASS,
                "com.ontotext.wsmo4j.parser.WSMLSerializerImpl");

        wsmlSerializer = org.wsmo.factory.Factory
                .createSerializer(serializerProperties);
	}
    
    /**
     * Utiltiy to get a Reader, tries first fileReader and then to load
     * from clath pass, helps avoiding FileNotFound exception during automated testing
     * @param location of file
     * @return Reader
     */
    public static Reader getReaderForFile(String location){
        Reader ontoReader= null;
        try{
            ontoReader = new FileReader(location);
        }catch (FileNotFoundException e){
            InputStream is = BaseReasonerTest.class.getResourceAsStream(location);
            assertNotNull("Could not Load file from class path: "+location, is);
            ontoReader = new InputStreamReader(is);
        }
        assertNotNull("Could not Load file from file system: "+location, ontoReader);
        return ontoReader;
    }
    
}
