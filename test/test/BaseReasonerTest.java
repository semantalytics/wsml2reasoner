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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.deri.wsmo4j.io.serializer.wsml.LogExprSerializerWSML;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.api.WSMLReasoner;
import org.wsml.reasoner.impl.DefaultWSMLReasonerFactory;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsmo.common.IRI;
import org.wsmo.common.TopEntity;
import org.wsmo.factory.DataFactory;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.Parser;
import org.wsmo.wsml.Serializer;

public class BaseReasonerTest extends TestCase {

    protected static WSMLReasoner wsmlReasoner = null;

    protected static Ontology o = null;

    protected LogExprSerializerWSML logExprSerializer = null;

    protected static WsmoFactory wsmoFactory = null;

    protected static LogicalExpressionFactory leFactory = null;

    protected static DataFactory dataFactory = null;

    protected static void setupScenario(String ontologyFile) throws Exception {
        // Set up factories for creating WSML elements

        leFactory = WSMO4JManager.getLogicalExpressionFactory();

        wsmoFactory = WSMO4JManager.getWSMOFactory();

        dataFactory = WSMO4JManager.getDataFactory();

        // Set up WSML parser

        Map<String, Object> parserProperties = new HashMap<String, Object>();
        parserProperties.put(Parser.PARSER_WSMO_FACTORY, wsmoFactory);
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
        // Map<String, Object> params = new HashMap<String, Object>();
        // params.put(WSMLReasonerFactory.PARAM_WSML_VARIANT,
        // WSMLReasonerFactory.WSMLVariant.WSML_CORE);
        // params.put(WSMLReasonerFactory.PARAM_BUILT_IN_REASONER,
        // WSMLReasonerFactory.BuiltInReasoner.KAON2);
        wsmlReasoner = DefaultWSMLReasonerFactory.getFactory()
                .getWSMLFlightReasoner();

        // Register ontology
        System.out.println("Registering ontology");
        wsmlReasoner.registerOntology(o);
    }

    protected void performQuery(String query, Set<Map<Variable, Term>> expected)
            throws Exception {
        System.out.println("\n\nStarting reasoner with query '" + query + "'");
        System.out.println("\n\nExpecting " + expected.size() + " results...");
        LogicalExpression qExpression = leFactory.createLogicalExpression(
                query, o);
        System.out.println("WSML Query LE:");
        System.out.println(logExprSerializer.serialize(qExpression));
        System.out.println("--------------\n\n");

        Set<Map<Variable, Term>> result = wsmlReasoner.executeQuery((IRI) o
                .getIdentifier(), qExpression);

        System.out.println("Found < " + result.size()
                + " > results to the query:");
        int i = 0;
        for (Map<Variable, Term> vBinding : result) {
            System.out.println("(" + (++i) + ") -- " + vBinding.toString());
        }
        assertEquals(expected.size(), result.size());
        for (Map<Variable, Term> binding : expected) {
            assertTrue("Result does not contain binding " + binding, include(
                    result, binding));
        }
    }

    /**
     * Checks whether there is a binding in result which contains all of the
     * variable bindings of expected
     * 
     * @param result
     *            the set of bindings to check
     * @param expectedBinding
     *            the reference binding
     * @return true if there is such an element
     */
    private boolean include(Set<Map<Variable, Term>> result,
            Map<Variable, Term> expectedBinding) {
        boolean contains = false;
        for (Map<Variable, Term> vBinding : result) {
            boolean containsAll = true;
            for (Variable var : expectedBinding.keySet()) {
                containsAll = containsAll
                        && expectedBinding.get(var).equals(vBinding.get(var));
            }
            if (containsAll) {
                contains = true;
                break;
            }
        }
        return contains;
    }

    @Override
    protected void setUp() throws Exception {
        logExprSerializer = new LogExprSerializerWSML(o);
    }

}
