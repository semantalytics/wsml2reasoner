/*
 * Copyright (C) 2006 Digital Enterprise Research Insitute (DERI) Innsbruck
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package performance;

import java.io.File;
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
import org.wsml.reasoner.api.WSMLReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.api.inconsistency.InconsistencyException;
import org.wsml.reasoner.impl.DefaultWSMLReasonerFactory;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsmo.common.IRI;
import org.wsmo.common.TopEntity;
import org.wsmo.factory.Factory;
import org.wsmo.wsml.Parser;
import org.wsmo.wsml.ParserException;

public class TestPerformance1 {

    int evalmethod = 2;

    /**
     * @param args
     *            none expected
     */
    public static void main(String[] args) {
        TestPerformance1 ex = new TestPerformance1();
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
        Ontology o0001 = loadOntology("performance/simple-0001-ontology.wsml");
        Ontology o0002 = loadOntology("performance/simple-0002-ontology.wsml");
        Ontology o0010 = loadOntology("performance/simple-0010-ontology.wsml");
        Ontology o0020 = loadOntology("performance/simple-0020-ontology.wsml");
        Ontology o0030 = loadOntology("performance/simple-0030-ontology.wsml");
        Ontology o0040 = loadOntology("performance/simple-0040-ontology.wsml");
        Ontology o0050 = loadOntology("performance/simple-0050-ontology.wsml");
        Ontology o0060 = loadOntology("performance/simple-0060-ontology.wsml");
        Ontology o0070 = loadOntology("performance/simple-0070-ontology.wsml");
        Ontology o0080 = loadOntology("performance/simple-0080-ontology.wsml");
        Ontology o0090 = loadOntology("performance/simple-0090-ontology.wsml");
        Ontology o0100 = loadOntology("performance/simple-0100-ontology.wsml");
        Ontology o1000 = loadOntology("performance/simple-1000-ontology.wsml");
        
        WSMLReasoner mins = getReasoner(WSMLReasonerFactory.BuiltInReasoner.MINS);
        WSMLReasoner kaon = getReasoner(WSMLReasonerFactory.BuiltInReasoner.KAON2);
        WSMLReasoner iris = getReasoner(WSMLReasonerFactory.BuiltInReasoner.IRIS);
        
        boolean printResults = false;
        
        String[] reasonerNames = new String[]{"MINS", "KAON", "IRIS"};
        WSMLReasoner[] reasoners = new WSMLReasoner[]{mins, kaon, iris};
        Ontology[] ontologies = new Ontology[]{o0001};
        String[] queries = new String[]{"?x subConceptOf ?y", "?x memberOf ?y"};
//        String[] queries = new String[]{"?x memberOf ?y"};
        
        PerformanceResults performanceresults = new PerformanceResults();
        for (Ontology ontology : ontologies){
            for (int i = 0; i < reasoners.length; i++){
                for (String query : queries){
                    performanceresults.addReasonerPerformaceResult(reasonerNames[i], ontology, query, executeQuery(reasoners[i], query, ontology, reasonerNames[i], printResults));
                }
            }
        }
        performanceresults.write(new File("test/performance/results/").getAbsolutePath());
    }

    private PerformanceResult executeQuery(WSMLReasoner theReasoner, String theQuery, Ontology theOntology, String theReasonerName, boolean thePrintResults) throws ParserException, InconsistencyException {
        PerformanceResult performanceresult = new PerformanceResult();
        
        System.out.println("------------------------------------");
        System.out.println("query = '" + theQuery + "'");
        System.out.println("ontology = '" + theOntology.getIdentifier() + "'");
        System.out.println("reasoner = '" + theReasonerName + "'");
        
        LogicalExpression query = new WSMO4JManager().getLogicalExpressionFactory().createLogicalExpression(theQuery, theOntology);
        
        System.out.print("Registering Ontology ");
        long t1_start = System.currentTimeMillis();
        theReasoner.registerOntology(theOntology);
        long t1_end = System.currentTimeMillis();
        long t1 = t1_end - t1_start;
        performanceresult.setRegisterOntology(t1);
        System.out.println("(" + t1 + "ms)");
        
        Set<Map<Variable, Term>> result = null;
        int j = 1;
        for (int i = 0; i < j; i++){
            System.out.print("Executing query " + i + " of " + j + " ");
            long t2_start = System.currentTimeMillis();
            result = theReasoner.executeQuery((IRI) theOntology.getIdentifier(), query);
            long t2_end = System.currentTimeMillis();
            long t2 = t2_end - t2_start;
            performanceresult.addExecuteQuery(i, t2);
            System.out.println("(" + t2 + "ms)");
        }

        System.out.print("Deregistering Ontology ");
        long t3_start = System.currentTimeMillis();
        theReasoner.deRegisterOntology((IRI) theOntology.getIdentifier());
        long t3_end = System.currentTimeMillis();
        long t3 = t3_end - t3_start;
        System.out.println("(" + t3 + "ms)");
        
        if (thePrintResults){
            System.out.println("The result:");
            for (Map<Variable, Term> vBinding : result) {
                for (Variable var : vBinding.keySet()) {
                    System.out.print(var + ": " + termToString(vBinding.get(var), theOntology) + "\t ");
                }
                System.out.println();
            }
        }
        System.out.println("------------------------------------");
        return performanceresult;
    }

    private WSMLReasoner getReasoner(WSMLReasonerFactory.BuiltInReasoner theReasoner) throws InconsistencyException{
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(WSMLReasonerFactory.PARAM_BUILT_IN_REASONER, theReasoner);
        params.put(WSMLReasonerFactory.PARAM_EVAL_METHOD, new Integer(evalmethod));
        WSMLReasoner reasoner = DefaultWSMLReasonerFactory.getFactory().createWSMLFlightReasoner(params);
        return reasoner;
    }
    
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
