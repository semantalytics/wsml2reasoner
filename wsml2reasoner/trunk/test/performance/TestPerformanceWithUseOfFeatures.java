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

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

import org.deri.wsmo4j.io.serializer.wsml.VisitorSerializeWSMLTerms;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.*;
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

public class TestPerformanceWithUseOfFeatures {

    int evalmethod = 2;

    /**
     * @param args
     *            none expected
     */
    public static void main(String[] args) {
        TestPerformanceWithUseOfFeatures ex = new TestPerformanceWithUseOfFeatures();
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
        Ontology o1 = loadOntology("performance/ontologies/001-simpleont.wsml");
        Ontology o2 = loadOntology("performance/ontologies/002-simpleontHirachy.wsml");
        boolean printResults = false;
        
        String[] reasonerNames = new String[]{
                "IRIS",
                "MINS",
                "KAON"
                };
        Ontology[] ontologies = new Ontology[]{o1,o2};
        SortedMap<String,String> queries = new TreeMap<String, String>();
        
        PerformanceResults performanceresults = new PerformanceResults();
        for (Ontology ontology : ontologies){
            if (ontology.listRelationInstances().isEmpty()){
                System.err.println("WARNING: "+ontology.getIdentifier()+" has no queries defined");
            }
            for (RelationInstance r: (Set<RelationInstance>)ontology.listRelationInstances()){
                //assume on axiom per query
                IRI id=(IRI)r.getRelation().getIdentifier();
                if (id.getLocalName().toString().startsWith("query")){
                    queries.put(id.getLocalName().toString(), 
                    r.getParameterValue((byte)0).toString());
                }
            }
          

            for (int i = 0; i < reasonerNames.length; i++){
                
                for (Entry<String, String> query : queries.entrySet()){
                    performanceresults.addReasonerPerformaceResult(
                            reasonerNames[i], 
                            ontology, 
                            query.getKey(), 
                            executeQuery(query.getValue(), ontology, reasonerNames[i], printResults));
                }
            }
        }
        performanceresults.write(new File("test/performance/results/").getAbsolutePath());
    }

    private PerformanceResult executeQuery(String theQuery, Ontology theOntology, String theReasonerName, boolean thePrintResults) throws ParserException, InconsistencyException {
        PerformanceResult performanceresult = new PerformanceResult();
        
        System.out.println("------------------------------------");
        System.out.println("query = '" + theQuery + "'");
        System.out.println("ontology = '" + theOntology.getIdentifier() + "'");
        System.out.println("reasoner = '" + theReasonerName + "'");
        
        System.out.print("Creating reasoner ");
        long t0_start = System.currentTimeMillis();
        WSMLReasoner reasoner = null;
        if (theReasonerName.equals("MINS")){
            reasoner = getReasoner(WSMLReasonerFactory.BuiltInReasoner.MINS);
        }
        else if (theReasonerName.equals("KAON")){
            reasoner = getReasoner(WSMLReasonerFactory.BuiltInReasoner.KAON2);
        }
        else if (theReasonerName.equals("IRIS")){
            reasoner = getReasoner(WSMLReasonerFactory.BuiltInReasoner.IRIS);
        }
        long t0_end = System.currentTimeMillis();
        long t0 = t0_end - t0_start;
        System.out.println("(" + t0 + "ms)");
        
        if (reasoner != null){
            LogicalExpression query = new WSMO4JManager().getLogicalExpressionFactory().createLogicalExpression(theQuery, theOntology);
            
            System.out.print("Registering Ontology ");
            long t1_start = System.currentTimeMillis();
            reasoner.registerOntology(theOntology);
            long t1_end = System.currentTimeMillis();
            long t1 = t1_end - t1_start;
            performanceresult.setRegisterOntology(t1);
            System.out.println("(" + t1 + "ms)");
            
            Set<Map<Variable, Term>> result = null;
            int j = 1;
            for (int i = 0; i < j; i++){
                System.out.print("Executing query " + i + " of " + j + " ");
                long t2_start = System.currentTimeMillis();
                result = reasoner.executeQuery((IRI) theOntology.getIdentifier(), query);
                long t2_end = System.currentTimeMillis();
                long t2 = t2_end - t2_start;
                performanceresult.addExecuteQuery(i, t2);
                System.out.println("(" + t2 + "ms)");
            }
    
            System.out.print("Deregistering Ontology ");
            long t3_start = System.currentTimeMillis();
            reasoner.deRegisterOntology((IRI) theOntology.getIdentifier());
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
