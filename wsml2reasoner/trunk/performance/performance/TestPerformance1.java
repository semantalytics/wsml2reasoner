package performance;


import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    	
    	List <Ontology> wsmo4jOntologies = new ArrayList<Ontology>();
    	
//    	wsmo4jOntologies.add(loadOntology("performance/ontologies/simple/simple-0001-ontology.wsml"));
//    	wsmo4jOntologies.add(loadOntology("performance/ontologies/simple/simple-0002-ontology.wsml"));
//    	wsmo4jOntologies.add(loadOntology("performance/ontologies/simple/simple-0010-ontology.wsml"));
//    	wsmo4jOntologies.add(loadOntology("performance/ontologies/simple/simple-0020-ontology.wsml"));
//    	wsmo4jOntologies.add(loadOntology("performance/ontologies/simple/simple-0030-ontology.wsml"));
//    	wsmo4jOntologies.add(loadOntology("performance/ontologies/simple/simple-0040-ontology.wsml"));
//    	wsmo4jOntologies.add(loadOntology("performance/ontologies/simple/simple-0050-ontology.wsml"));
//    	wsmo4jOntologies.add(loadOntology("performance/ontologies/simple/simple-0060-ontology.wsml"));
//    	wsmo4jOntologies.add(loadOntology("performance/ontologies/simple/simple-0070-ontology.wsml"));
//    	wsmo4jOntologies.add(loadOntology("performance/ontologies/simple/simple-0080-ontology.wsml"));
//    	wsmo4jOntologies.add(loadOntology("performance/ontologies/simple/simple-0090-ontology.wsml"));
    	wsmo4jOntologies.add(loadOntology("performance/ontologies/simple/simple-0100-ontology.wsml"));
    	wsmo4jOntologies.add(loadOntology("performance/ontologies/simple/simple-0200-ontology.wsml"));
    	wsmo4jOntologies.add(loadOntology("performance/ontologies/simple/simple-0300-ontology.wsml"));
    	wsmo4jOntologies.add(loadOntology("performance/ontologies/simple/simple-0400-ontology.wsml"));
    	wsmo4jOntologies.add(loadOntology("performance/ontologies/simple/simple-0500-ontology.wsml"));
    	wsmo4jOntologies.add(loadOntology("performance/ontologies/simple/simple-0600-ontology.wsml"));
    	wsmo4jOntologies.add(loadOntology("performance/ontologies/simple/simple-0700-ontology.wsml"));
    	wsmo4jOntologies.add(loadOntology("performance/ontologies/simple/simple-0800-ontology.wsml"));
    	wsmo4jOntologies.add(loadOntology("performance/ontologies/simple/simple-0900-ontology.wsml"));
    	wsmo4jOntologies.add(loadOntology("performance/ontologies/simple/simple-1000-ontology.wsml"));
        boolean printResults = false;
        
        String[] reasonerNames = new String[]{"MINS", "IRIS"};
        Ontology[] ontologies = wsmo4jOntologies.toArray( new Ontology[]{});
        String[] queries = new String[]{"?x memberOf ?y"};
        
        PerformanceResults performanceresults = new PerformanceResults();
        for (Ontology ontology : ontologies){
            for (int i = 0; i < reasonerNames.length; i++){
                for (String query : queries){
                    performanceresults.addReasonerPerformaceResult(reasonerNames[i], ontology, query, executeQuery(query, ontology, reasonerNames[i], printResults));
                }
            }
        }
        performanceresults.writeAll(new File("performance/performance/results/").getAbsolutePath());
    }

    private PerformanceResult executeQuery(String theQuery, Ontology theOntology, String theReasonerName, boolean thePrintResults) throws ParserException, InconsistencyException {
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
        
        PerformanceResult performanceresult = new PerformanceResult(reasoner);
        
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
            int j = 10;
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