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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.deri.wsmo4j.io.serializer.wsml.VisitorSerializeWSMLTerms;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.api.LPReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.impl.DefaultWSMLReasonerFactory;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsmo.common.TopEntity;
import org.wsmo.factory.Factory;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.wsml.Parser;

/**
 * Usage Example for the wsml2Reasoner Framework
 * 
 * @author Holger Lausen, DERI Innsbruck
 * 
 * Rewrite to accomodate dl reasoners -- Graham
 * 
 */
public class ReasonerExample {

    /**
     * @param args
     *            none expected
     */
    public static void main(String[] args) {
        if (args == null || args.length == 0) {
            System.out.println("Must specify a reasoner");
            System.out.println("Options are:");
            System.out.println("IRIS");
            System.out.println("MINS");
            System.out.println("KAON2");
            System.out.println("PELLET");
            System.out.println("TPTP");
            System.out.println("XSB");
        }
        else {
            ReasonerExample ex = new ReasonerExample();
            try {
                ex.doTestRun(args[0]);
                System.exit(0);
            }
            catch (Throwable e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * loads an Ontology and performs sample query
     */
    public void doTestRun(String reasoningEngine) throws Exception {
        Ontology exampleOntology = loadOntology("example/simpleOntology.wsml");
        if (exampleOntology == null)
            return;
        LogicalExpressionFactory leFactory = new WSMO4JManager().getLogicalExpressionFactory();

        String queryString = "?x memberOf ?y";

        LogicalExpression query = leFactory.createLogicalExpression(queryString, exampleOntology);

        // get A reasoner
        Map<String, Object> params = new HashMap<String, Object>();
        if (reasoningEngine.equals("iris") || reasoningEngine.equals("IRIS")) {
            params.put(WSMLReasonerFactory.PARAM_BUILT_IN_REASONER, WSMLReasonerFactory.BuiltInReasoner.IRIS_STRATIFIED);
        }
        else if (reasoningEngine.equals("mins") || reasoningEngine.equals("MINS")) {
            params.put(WSMLReasonerFactory.PARAM_BUILT_IN_REASONER, WSMLReasonerFactory.BuiltInReasoner.MINS);
        }
        else if (reasoningEngine.equals("kaon2") || reasoningEngine.equals("KAON2")) {
            params.put(WSMLReasonerFactory.PARAM_BUILT_IN_REASONER, WSMLReasonerFactory.BuiltInReasoner.KAON2);
        }
        else if (reasoningEngine.equals("pellet") || reasoningEngine.equals("PELLET")) {
            params.put(WSMLReasonerFactory.PARAM_BUILT_IN_REASONER, WSMLReasonerFactory.BuiltInReasoner.PELLET);
        }
        else if (reasoningEngine.equals("tptp") || reasoningEngine.equals("TPTP")) {
            params.put(WSMLReasonerFactory.PARAM_BUILT_IN_REASONER, WSMLReasonerFactory.BuiltInReasoner.TPTP);
        }
        else if (reasoningEngine.equals("xsb") || reasoningEngine.equals("XSB")) {
            params.put(WSMLReasonerFactory.PARAM_BUILT_IN_REASONER, WSMLReasonerFactory.BuiltInReasoner.XSB);
        }
        else {
            System.out.println("Unrecognized reasoner\nOptions are:");
            System.out.println("MINS");
            System.out.println("KAON2");
            System.out.println("PELLET");
            System.out.println("TPTP");
            System.out.println("XSB");
            return;
        }

        LPReasoner reasoner = DefaultWSMLReasonerFactory.getFactory().createFlightReasoner(params);

        // Register ontology
        reasoner.registerOntology(exampleOntology);
        // reasoner.registerOntologyNoVerification(exampleOntology);

        // Execute query request
        Set<Map<Variable, Term>> result = reasoner.executeQuery(query);

        // print out the results:
        System.out.println("The query '" + query + "' has the following results:");
        for (Map<Variable, Term> vBinding : result) {
            for (Variable var : vBinding.keySet()) {
                System.out.print(var + ": " + termToString(vBinding.get(var), exampleOntology) + "\t ");
            }
            System.out.println();
        }
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
        Parser wsmlParser = Factory.createParser(null);

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

    private String termToString(Term t, Ontology o) {
        VisitorSerializeWSMLTerms v = new VisitorSerializeWSMLTerms(o);
        t.accept(v);
        return v.getSerializedObject();
    }
}
