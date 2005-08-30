/**
 * WSML Reasoner Implementation.
 *
 * Copyright (c) 2005, FZI, Germany
 *                     University of Innsbruck, Austria.
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

import java.io.Reader;
import java.io.StringWriter;
import java.util.*;

import junit.framework.TestCase;

import org.deri.wsml.reasoner.api.*;
import org.deri.wsml.reasoner.api.queryanswering.*;
import org.deri.wsml.reasoner.impl.*;
import org.deri.wsmo4j.io.parser.wsml.LogExprParserImpl;
import org.deri.wsmo4j.io.serializer.wsml.LogExprSerializerWSML;
import org.omwg.logexpression.LogicalExpression;
import org.omwg.ontology.Ontology;
import org.wsmo.common.TopEntity;
import org.wsmo.factory.Factory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.Parser;
import org.wsmo.wsml.Serializer;

public class BaseReasonerTest extends TestCase {

    protected static WSMLReasoner wsmlReasoner = null;

    protected static Ontology o = null;

    protected org.omwg.logexpression.io.Parser leParser = null;

    protected org.omwg.logexpression.io.Serializer logExprSerializer = null;

    protected static void setupScenario(String ontologyFile) throws Exception {
        // Set up factories for creating WSML elements

        Map<String, String> leProperties = new HashMap<String, String>();
        leProperties.put(Factory.PROVIDER_CLASS,
                "org.deri.wsmo4j.logexpression.LogicalExpressionFactoryImpl");

        org.omwg.logexpression.LogicalExpressionFactory leFactory = (org.omwg.logexpression.LogicalExpressionFactory) Factory
                .createLogicalExpressionFactory(leProperties);

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(Factory.PROVIDER_CLASS,
                "com.ontotext.wsmo4j.factory.WsmoFactoryImpl");
        properties.put(Parser.PARSER_LE_FACTORY, leFactory);
        WsmoFactory factory = Factory.createWsmoFactory(null);

        // Set up WSML parser

        Map<String, Object> parserProperties = new HashMap<String, Object>();
        parserProperties.put(Parser.PARSER_WSMO_FACTORY, factory);
        parserProperties.put(Parser.PARSER_LE_FACTORY, leFactory);

        parserProperties.put(org.wsmo.factory.Factory.PROVIDER_CLASS,
                "com.ontotext.wsmo4j.parser.WSMLParserImpl");

        Parser wsmlparserimpl = org.wsmo.factory.Factory
                .createParser(parserProperties);

        // Set up serializer

        Map<String, String> serializerProperties = new HashMap<String, String>();
        serializerProperties.put(org.wsmo.factory.Factory.PROVIDER_CLASS,
                "com.ontotext.wsmo4j.parser.WSMLSerializerImpl");

        Serializer ontologySerializer = org.wsmo.factory.Factory
                .createSerializer(serializerProperties);

        // Read simple ontology from file
        final Reader ontoReader = BaseTest.getReaderForFile(ontologyFile);
        final TopEntity[] identifiable = wsmlparserimpl.parse(ontoReader);
        if (identifiable.length > 0 && identifiable[0] instanceof Ontology) {
            o = (Ontology) identifiable[0];
        } else {
            return;
        }
        
        System.out.println("Parsed ontology");

        // Print ontology in WSML

        System.out.println("WSML Ontology:\n");
        StringWriter sw = new StringWriter();
        ontologySerializer.serialize(new TopEntity[] { o }, sw);
        System.out.println(sw.toString());
        System.out.println("--------------\n\n");

        // Create reasoner
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(WSMLReasonerFactory.PARAM_WSML_VARIANT,
                WSMLReasonerFactory.WSMLVariant.WSML_CORE);
        params.put(WSMLReasonerFactory.PARAM_BUILT_IN_REASONER,
                WSMLReasonerFactory.BuiltInReasoner.KAON2);
        wsmlReasoner = DefaultWSMLReasonerFactory.getFactory().getWSMLReasoner(
                params);

        // Register ontology
        System.out.println("Registering ontology");
        Set<Ontology> ontos = new HashSet<Ontology>();
        ontos.add(o);
        OntologyRegistrationRequest regReq = new OntologyRegistrationRequestImpl(
                ontos);
        wsmlReasoner.execute(regReq);

    }

    protected void performQuery(String query, Set<VariableBinding> expected)
            throws Exception {
        System.out.println("\n\nStarting reasoner with query " + query);
        LogicalExpression qExpression = leParser.parse(query);
        System.out.println("WSML Query:");
        System.out.println(logExprSerializer.serialize(qExpression));
        System.out.println("--------------\n\n");
        String ontologyUri = o.getIdentifier().toString();

        QueryAnsweringRequest qaRequest = new QueryAnsweringRequestImpl(
                ontologyUri, qExpression);
        QueryAnsweringResult result = (QueryAnsweringResult) wsmlReasoner
                .execute(qaRequest);

        System.out.println("Found < " + result.size()
                + " > results to the query:");
        int i = 0;
        for (VariableBinding vBinding : result) {
            System.out.println("(" + (++i) + ") -- " + vBinding.toString());
        }
        assertEquals(result.size(), expected.size());
        for (VariableBinding binding : expected) {
            assertTrue(result.contains(binding));
        }
    }

    @Override
    protected void setUp() throws Exception {
        leParser = LogExprParserImpl.getInstance(o); // construct queries
        // over the same
        // ontology
        logExprSerializer = new LogExprSerializerWSML(o);
    }

}
