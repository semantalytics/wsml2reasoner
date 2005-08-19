package test;


import java.util.HashMap;
import java.util.Map;

import org.omwg.logexpression.LogicalExpressionFactory;
import org.wsmo.factory.Factory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.Parser;
import org.wsmo.wsml.Serializer;

import junit.framework.TestCase;

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
 * @version $Revision: 1.1 $ $Date: 2005-08-19 13:43:10 $
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
}
