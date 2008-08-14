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
import org.wsml.reasoner.api.LPReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.api.inconsistency.InconsistencyException;
import org.wsml.reasoner.impl.DefaultWSMLReasonerFactory;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsmo.common.TopEntity;
import org.wsmo.factory.Factory;
import org.wsmo.wsml.Parser;
import org.wsmo.wsml.ParserException;

public class TestPerformance1 {

	/** How often to repeat the query to get a good average. */
	public static final int QUERY_REPETITION_COUNT = 4;
	
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
    	/*
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
    	*/

    	// Finds bug in IRIS GlobolStratifier??? Maybe just inefficient with 11,621 rules!
//    	wsmo4jOntologies.add(loadOntology("performance/ontologies/simple/EconomicActivityOntology.wsml"));
    	
    	
//    	wsmo4jOntologies.add(loadOntology("performance/ontologies/simple/EducationOntology.wsml"));
//    	wsmo4jOntologies.add(loadOntology("performance/ontologies/simple/EG1.wsml"));
//    	wsmo4jOntologies.add(loadOntology("performance/ontologies/simple/GeographyOntology.wsml"));
//    	wsmo4jOntologies.add(loadOntology("performance/ontologies/simple/JobOfferOntology.wsml"));
//    	wsmo4jOntologies.add(loadOntology("performance/ontologies/simple/JobSeekerOntology.wsml"));
//    	wsmo4jOntologies.add(loadOntology("performance/ontologies/simple/LabourRegulatoryOntology.wsml"));
//    	wsmo4jOntologies.add(loadOntology("performance/ontologies/simple/LanguageOntology.wsml"));

    	// Null pointer exception
//    	wsmo4jOntologies.add(loadOntology("performance/ontologies/simple/checkboolean.wsml"));

    	// Consistency check violation with MINS
//    	wsmo4jOntologies.add(loadOntology("performance/ontologies/simple/GeographyOntology.wsml"));
//		wsmo4jOntologies.add(loadOntology("performance/ontologies/simple/JobOfferOntology.wsml"));
//		wsmo4jOntologies.add(loadOntology("performance/ontologies/simple/EducationOntology.wsml"));
//    	wsmo4jOntologies.add(loadOntology("performance/ontologies/simple/LabourRegulatoryOntology.wsml"));
//    	wsmo4jOntologies.add(loadOntology("performance/ontologies/simple/CompensationOntology.wsml"));
//    	wsmo4jOntologies.add(loadOntology("performance/ontologies/simple/DrivingLicenseOntology.wsml"));
    	

//    	wsmo4jOntologies.add(loadOntology("performance/ontologies/simple/EG1.wsml"));
//    	wsmo4jOntologies.add(loadOntology("performance/ontologies/simple/JobSeekerOntology.wsml"));
//    	wsmo4jOntologies.add(loadOntology("performance/ontologies/simple/LanguageOntology.wsml"));
//    	wsmo4jOntologies.add(loadOntology("performance/ontologies/simple/CompetenceOntology.wsml"));

    	String[] reasonerNames = new String[ 0 ];
    	
    	switch( 2 )
    	{
    	case 98:	// nobel
//    		reasonerNames = new String[]{"IRIS", "KAON", "MINS"};
    		reasonerNames = new String[]{"MINS", "IRIS", "KAON"};
    		
    		wsmo4jOntologies.add(loadOntology("performance/ontologies/simple/nobel.wsml"));
    		break;

    	case 99:	// For testing Chart
    		reasonerNames = new String[]{"KAON", "IRIS"};
    		
    		wsmo4jOntologies.add(loadOntology("performance/ontologies/simple/LanguageOntology.wsml"));
        	wsmo4jOntologies.add(loadOntology("performance/ontologies/simple/DrivingLicenseOntology.wsml"));
    		break;
    		
    	case 0:	// For IRIS
    		reasonerNames = new String[]{"IRIS"};
    		
        	wsmo4jOntologies.add(loadOntology("performance/ontologies/simple/EconomicActivityOntology.wsml"));

//        	wsmo4jOntologies.add(loadOntology("performance/ontologies/simple/EG1.wsml"));
//    		wsmo4jOntologies.add(loadOntology("performance/ontologies/simple/LanguageOntology.wsml"));
//        	wsmo4jOntologies.add(loadOntology("performance/ontologies/simple/DrivingLicenseOntology.wsml"));
//        	wsmo4jOntologies.add(loadOntology("performance/ontologies/simple/JobSeekerOntology.wsml"));
//        	wsmo4jOntologies.add(loadOntology("performance/ontologies/simple/CompetenceOntology.wsml"));
//        	wsmo4jOntologies.add(loadOntology("performance/ontologies/simple/GeographyOntology.wsml"));
        	break;
        	
    	case 1: // For KAON and IRIS
    		reasonerNames = new String[]{"KAON", "IRIS"};
    		
//        	wsmo4jOntologies.add(loadOntology("performance/ontologies/simple/EG1.wsml"));
    		wsmo4jOntologies.add(loadOntology("performance/ontologies/simple/LanguageOntology.wsml"));
        	wsmo4jOntologies.add(loadOntology("performance/ontologies/simple/DrivingLicenseOntology.wsml"));
        	wsmo4jOntologies.add(loadOntology("performance/ontologies/simple/JobSeekerOntology.wsml"));
        	wsmo4jOntologies.add(loadOntology("performance/ontologies/simple/CompetenceOntology.wsml"));
        	wsmo4jOntologies.add(loadOntology("performance/ontologies/simple/GeographyOntology.wsml"));
        	break;

    	case 2: // For KAON, IRIS and MINS
    		reasonerNames = new String[]{"KAON", "MINS", "IRIS" };
    		
    		// WEIRD!!!!!!!!
//        	wsmo4jOntologies.add(loadOntology("performance/ontologies/simple/EG1.wsml"));
//    		wsmo4jOntologies.add(loadOntology("performance/ontologies/simple/LanguageOntology.wsml")); // MINS STALLS
//        	wsmo4jOntologies.add(loadOntology("performance/ontologies/simple/DrivingLicenseOntology.wsml")); // MINS Consitency violation
        	wsmo4jOntologies.add(loadOntology("performance/ontologies/simple/JobSeekerOntology.wsml"));
        	wsmo4jOntologies.add(loadOntology("performance/ontologies/simple/CompetenceOntology.wsml"));
        	wsmo4jOntologies.add(loadOntology("performance/ontologies/simple/simple-1000-ontology.wsml"));
        	break;
        	
    	case 3: // For MINS
    		reasonerNames = new String[]{ "MINS" };
//        	wsmo4jOntologies.add(loadOntology("performance/ontologies/simple/GeographyOntology.wsml")); // Inconsistency
        	break;

    	case 5: // For IRIS and MINS
    		reasonerNames = new String[]{"MINS", "IRIS" };
    		
    		// WEIRD!!!!!!!!
//    		wsmo4jOntologies.add(loadOntology("performance/ontologies/simple/EG1.wsml"));	// Can only be loaded alone
 			wsmo4jOntologies.add(loadOntology("performance/ontologies/simple/JobSeekerOntology.wsml"));
	       	wsmo4jOntologies.add(loadOntology("performance/ontologies/simple/CompetenceOntology.wsml"));
//    		wsmo4jOntologies.add(loadOntology("performance/ontologies/simple/LanguageOntology.wsml")); // MINS inconsistency
//        	wsmo4jOntologies.add(loadOntology("performance/ontologies/simple/DrivingLicenseOntology.wsml")); // MINS Consitency violation
//        	wsmo4jOntologies.add(loadOntology("performance/ontologies/simple/GeographyOntology.wsml")); // Consistency violation
//        	wsmo4jOntologies.add(loadOntology("performance/ontologies/simple/EducationOntology.wsml")); // Consistency violation
        	break;
    	}

    	boolean printResults = false;
       
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
        LPReasoner reasoner = null;
        if (theReasonerName.equals("MINS")){
            reasoner = getReasoner(WSMLReasonerFactory.BuiltInReasoner.MINS);
        }
        else if (theReasonerName.equals("KAON")){
            reasoner = getReasoner(WSMLReasonerFactory.BuiltInReasoner.KAON2);
        }
        else if (theReasonerName.equals("IRIS")){
            reasoner = getReasoner(WSMLReasonerFactory.BuiltInReasoner.IRIS_STRATIFIED);
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
            int j = QUERY_REPETITION_COUNT;
            for (int i = 0; i < j; i++){
                System.out.print("Executing query " + i + " of " + j + " ");
                long t2_start = System.currentTimeMillis();
                result = reasoner.executeQuery(query);
                long t2_end = System.currentTimeMillis();
                long t2 = t2_end - t2_start;
                performanceresult.addExecuteQuery(i, t2);
                System.out.println("(" + t2 + "ms)");
            }
    
            System.out.print("Deregistering Ontology ");
            long t3_start = System.currentTimeMillis();
            reasoner.deRegister();
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

    private LPReasoner getReasoner(WSMLReasonerFactory.BuiltInReasoner theReasoner) throws InconsistencyException{
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(WSMLReasonerFactory.PARAM_BUILT_IN_REASONER, theReasoner);
        LPReasoner reasoner = DefaultWSMLReasonerFactory.getFactory().createFlightReasoner(params);
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
                System.out.println("First Element of file '" + file + "' is NOT an ontology ");
                return null;
            }

        }
        catch (Exception e) {
            System.out.println("Unable to parse ontology from file ': " + file + "' - " + e.getMessage());
            return null;
        }
    }

    private String termToString(Term t, Ontology o) {
        VisitorSerializeWSMLTerms v = new VisitorSerializeWSMLTerms(o);
        t.accept(v);
        return v.getSerializedObject();
    }
}
