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
import org.wsmo.common.exception.InvalidModelException;
import org.wsmo.common.exception.SynchronisationException;
import org.wsmo.factory.Factory;
import org.wsmo.wsml.Parser;
import org.wsmo.wsml.ParserException;

public class TestPerformanceWithUseOfFeatures {
	//how often to repeat each query!
    public static int NO_OF_TESTRUNS = 3;
    public static int TIMELIMIT_QUERY = 10000;
    public static int TIMELIMIT_REGISTRATION = 20000;
    public static int WAIT_INTERVAL = 1000;
    
    int evalmethod = 2;
    
    
    String directoryPath = "performance/performance/results/";
    
    String[] reasonerNames = new String[]{"KAON", "MINS", "IRIS"};

    /**
     * @param args
     *            none expected
     */
    public static void main(String[] args)throws Exception {
        TestPerformanceWithUseOfFeatures ex = new TestPerformanceWithUseOfFeatures();
       
//        ex.doTestRun();
    	ex.testSubconceptOntologies();
    	ex.testDeepSubconceptOntologies();
    	ex.testInstanceOntologies();
    	ex.testInstanceANDsubconceptOntologies();
    	ex.testInstanceANDdeepSubconceptOntologies();
    	ex.testOfTypeOntologies();
    	ex.testOfTypeANDsubconceptOntologies();
    	ex.testCardinality01Ontologies();
    	ex.testCardinality010Ontologies();
    	ex.testInverseAttributeOntologies();
    	ex.testTransitiveAttributeOntologies();
    	ex.testSymmetricAttributeOntologies();
    	ex.testReflexiveAttributeOntologies();
    	ex.testLocallyStratifiedNegation();
    	ex.testGloballyStratifiedNegation();
    	ex.testBuiltInAttributeOntologies();
    }

    /**
     * loads an Ontology and performs sample query
     */
    public void doTestRun() throws Exception {
        Ontology o1 = loadOntology("performance/ontologies/001-simpleont.wsml");
        Ontology o2 = loadOntology("performance/ontologies/002-simpleontHirachy.wsml");
        Ontology[] ontologies = new Ontology[]{o1,o2};
        String fileName = "simple/";
        runPerformanceTests(this.reasonerNames, ontologies, fileName);
    }

    Ontology[] loadOntologies(String dir){
    	System.out.println("Loading ontologies "+dir);
    	File d = new File(dir);
    	List<Ontology> onts = new ArrayList<Ontology>(); 
    	File[] files = d.listFiles(wsmlFilter);
//    	System.out.println(dir+"\n"+files);
    	for (File f:files){
    		try {
				Ontology ont = (Ontology)wsmlParser.parse(new FileReader(f))[0];
				onts.add(ont);
			} catch (Exception e) {
				throw new RuntimeException("could not load ontology",e);
			}
    	}
    	Ontology[] ret = new Ontology[onts.size()];
    	onts.toArray(ret);
    	return ret;
    }
    
    FileFilter wsmlFilter = new FileFilter(){
		public boolean accept(File pathname) {
			return pathname.toString().endsWith(".wsml");
		}
    };
    
    static final String BASE="performance/performance/ontologies/";
    /**
     * loads subconcept type ontologies and performs sample queries
     */
    public void testSubconceptOntologies() throws Exception {
        String fileName = "subconcept/";
    	Ontology[] ontologies = loadOntologies(BASE+fileName);
        runPerformanceTests(this.reasonerNames, ontologies, fileName);
    }
    
    /**
     * loads deep subconcept type ontologies and performs sample queries
     */
    public void testDeepSubconceptOntologies() throws Exception {
        String path = "deepSubconcept/";
    	Ontology[] ontologies = loadOntologies(BASE+path);
        runPerformanceTests(this.reasonerNames, ontologies, path);
    }

    /**
     * loads instance type ontologies and performs sample queries
     */
    public void testInstanceOntologies() throws Exception {
        String path = "instance/";
        Ontology[] ontologies = loadOntologies(BASE+path);
        runPerformanceTests(this.reasonerNames, ontologies, path);
    }
    
    /**
     * loads instance and subconcept type ontologies and performs sample queries
     */
    public void testInstanceANDsubconceptOntologies() throws Exception {
        String path = "instanceANDsubconcept/";
        Ontology[] ontologies = loadOntologies(BASE+path);
        runPerformanceTests(this.reasonerNames, ontologies, path);
    }
    
    /**
     * loads instance and deep subconcept type ontologies and performs sample queries
     */
    public void testInstanceANDdeepSubconceptOntologies() throws Exception {
        String path = "instanceANDdeepSubconcept/";
        Ontology[] ontologies = loadOntologies(BASE+path);
        runPerformanceTests(this.reasonerNames, ontologies, path);
    }
    
    /**
     * loads ofType type ontologies and performs sample queries
     */
    public void testOfTypeOntologies() throws Exception {
        String path = "ofType/";
        Ontology[] ontologies = loadOntologies(BASE+path);
        String[] reasonerNames = new String[]{"MINS", "KAON"};
        runPerformanceTests(reasonerNames, ontologies, path);
    }
    
    /**
     * loads ofType and subconcept type ontologies and performs sample queries
     */
    public void testOfTypeANDsubconceptOntologies() throws Exception {
        String path = "ofTypeANDsubconcept/";
        Ontology[] ontologies = loadOntologies(BASE+path);
        runPerformanceTests(this.reasonerNames, ontologies, path);
    }
    
    /**
     * loads cardinality (0 1) type ontologies and performs sample queries
     */
    public void testCardinality01Ontologies() throws Exception {
        String path = "cardinality_0_1/";
        Ontology[] ontologies = loadOntologies(BASE+path);
        String[] reasonerNames = new String[]{"MINS", "KAON"};
        runPerformanceTests(reasonerNames, ontologies, path);
    }
    
    /**
     * loads cardinality (0 10) type ontologies and performs sample queries
     */
    public void testCardinality010Ontologies() throws Exception {
        String path = "cardinality_0_10/";
        String[] reasonerNames = new String[]{"KAON", "MINS"};
        Ontology[] ontologies = loadOntologies(BASE+path);
        runPerformanceTests(reasonerNames, ontologies, path);
    }
    
    /**
     * loads inverse attribute type ontologies and performs sample queries
     */
    public void testInverseAttributeOntologies() throws Exception {
        String path = "inverseAttribute/";
        Ontology[] ontologies = loadOntologies(BASE+path);
        runPerformanceTests(this.reasonerNames, ontologies, path);
    }
    
    /**
     * loads transitive attribute type ontologies and performs sample queries
     */
    public void testTransitiveAttributeOntologies() throws Exception {
        String path = "transitiveAttribute/";
        Ontology[] ontologies = loadOntologies(BASE+path);
        String[] reasonerNames = new String[]{"MINS", "KAON"};
        runPerformanceTests(reasonerNames, ontologies, path);
    }
    
    /**
     * loads symmetric attribute type ontologies and performs sample queries
     */
    public void testSymmetricAttributeOntologies() throws Exception {
        String path = "symmetricAttribute/";
        Ontology[] ontologies = loadOntologies(BASE+path);
        String[] reasonerNames = new String[]{"MINS", "KAON"};
        runPerformanceTests(reasonerNames, ontologies, path);
    }
    
    /**
     * loads reflexive attribute type ontologies and performs sample queries
     */
    public void testReflexiveAttributeOntologies() throws Exception {
        String path = "reflexiveAttribute/";
        Ontology[] ontologies = loadOntologies(BASE+path);
        runPerformanceTests(this.reasonerNames, ontologies, path);
    }
    

    public void testLocallyStratifiedNegation() throws Exception {
        String path = "locallyStratifiedNegation/";
        Ontology[] ontologies = loadOntologies(BASE+path);
        runPerformanceTests(this.reasonerNames, ontologies, path);
    }
    
    public void testGloballyStratifiedNegation() throws Exception {
        String path = "globallyStratifiedNegation/";
        Ontology[] ontologies = loadOntologies(BASE+path);
        runPerformanceTests(this.reasonerNames, ontologies, path);
    }
    
    /**
     * loads built-in attribute type ontologies and performs sample queries
     */
    public void testBuiltInAttributeOntologies() throws Exception {
        String path = "built_in/";
        Ontology[] ontologies = loadOntologies(BASE+path);
        runPerformanceTests(this.reasonerNames, ontologies, path);
    }
    
    private void runPerformanceTests(String[] reasonerNames, Ontology[] ontologies, String path) 
    		throws SynchronisationException, InvalidModelException, ParserException, 
    		InconsistencyException, IOException {
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
                	try{
	                    performanceresults.addReasonerPerformaceResult(
	                            reasonerNames[i], 
	                            ontology, 
	                            query.getKey(), 
	                            executeQuery(query.getValue(), ontology, reasonerNames[i]));
                	}catch (Exception e){
                		System.out.println("error with ontology "+ontology.getIdentifier()+" and reasoner "+reasonerNames[i]);
                		e.printStackTrace();
                	}
                }
                System.gc();
            }
        }
        performanceresults.write(new File(directoryPath + path).getAbsolutePath());
    }
    
    Set<String> timedOutReasoner = new HashSet<String>();
    
    private WSMLReasoner createReasoner(String theReasonerName) throws InconsistencyException{
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
        return reasoner;
    }
    
    private PerformanceResult executeQuery(
    		String theQuery, Ontology theOntology, 
    		String theReasonerName) 
    		throws ParserException, InconsistencyException {
        
    	PerformanceResult performanceresult = new PerformanceResult();
    	if (timedOutReasoner.contains(theReasonerName)){
    		System.out.println(theReasonerName+" has previously already timed out!!");
    		return performanceresult;
    	}
        
        System.out.println("------------------------------------");
        System.out.println("query = '" + theQuery + "'");
        System.out.println("ontology = '" + theOntology.getIdentifier() + "'");
        System.out.println("reasoner = '" + theReasonerName + "'");
        
        WSMLReasoner reasoner = createReasoner(theReasonerName);
        if (reasoner==null) return performanceresult;
    
        LogicalExpression query = new WSMO4JManager().getLogicalExpressionFactory().createLogicalExpression(theQuery, theOntology);
        
        System.out.print("Registering Ontology ");
        RegistrationThread registrationThread = new RegistrationThread(theOntology,reasoner,performanceresult);
        registrationThread.start();
        long counter=0;
        waitUntilAlive(registrationThread);
        while(registrationThread.isAlive()){
        	if (counter > TIMELIMIT_REGISTRATION){
        		System.out.println("stopping registration thread due to timeout");
        		registrationThread.stop();
        		timedOutReasoner.add(theReasonerName);
        		return performanceresult;
        	}
        	waitABit();
        	counter += WAIT_INTERVAL;
        }
        System.out.println(" done.");
       
        for (int i=0; i<NO_OF_TESTRUNS && !timedOutReasoner.contains(theReasonerName);i++){
            QueryThread queryThread = new QueryThread(theOntology,reasoner,query);
            System.out.print("Executing query ");
            queryThread.start();
            waitUntilAlive(queryThread);
            counter=0;
            while(queryThread.isAlive()){
	            if (counter > TIMELIMIT_QUERY){
            		System.out.println("stopping reasoning thread due to timeout");
            		queryThread.stop();
            		timedOutReasoner.add(theReasonerName);
            	}
	            waitABit();
            	counter += WAIT_INTERVAL;
            }
            long t2=queryThread.getDuration();
    	    performanceresult.addExecuteQuery(i, t2);
            System.out.println("(" + t2 + " ms)");
        }
        
        System.out.print("Deregistering Ontology ");
        long t3_start = System.currentTimeMillis();
        reasoner.deRegisterOntology((IRI) theOntology.getIdentifier());
        long t3_end = System.currentTimeMillis();
        long t3 = t3_end - t3_start;
        System.out.println("(" + t3 + "ms)");
        System.out.println("------------------------------------");
        return performanceresult;
    }
    
    static Set<Map<Variable, Term>> result = null;
    private void waitABit(){
    	synchronized (this) {
    		try {
				wait(WAIT_INTERVAL);
			} catch (InterruptedException e) {
				// can not happen
				e.printStackTrace();
			}
		}
    }
    
    private void waitUntilAlive(MyThread t){
//    	System.out.println("waiting "+t);
    	while (!t.isFinished() && !t.isAlive()){
//    		System.out.println("waiting!! "+t);
    		waitABit();
    	}
    }
    
    private WSMLReasoner getReasoner(WSMLReasonerFactory.BuiltInReasoner theReasoner) throws InconsistencyException{
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(WSMLReasonerFactory.PARAM_BUILT_IN_REASONER, theReasoner);
        params.put(WSMLReasonerFactory.PARAM_EVAL_METHOD, new Integer(evalmethod));
        WSMLReasoner reasoner = DefaultWSMLReasonerFactory.getFactory().createWSMLFlightReasoner(params);
        return reasoner;
    }
    
    Parser wsmlParser = Factory.createParser(null);
   
    private Ontology loadOntology(String file) {
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

class MyThread extends Thread{
	boolean isFinished = false;
	public  boolean isFinished(){
		return isFinished;
	}
}

class RegistrationThread extends MyThread {
	Ontology o;
	WSMLReasoner reasoner;
	PerformanceResult performanceresult;

	RegistrationThread(Ontology o, WSMLReasoner reasoner,PerformanceResult performence) {
		this.o = o;
		this.reasoner = reasoner;
		this.performanceresult=performence;
	}

	public void run() {
		try {
//			System.out.println("Registering "+o.getIdentifier());
			long t1_start = System.currentTimeMillis();
			reasoner.registerOntology(o);
			long t1_end = System.currentTimeMillis();
			long t1 = t1_end-t1_start;
			System.out.println("(" + t1 + "ms)");
            performanceresult.setRegisterOntology(t1);
            isFinished = true;
		} catch (InconsistencyException e) {
			throw new RuntimeException(e);
		}
	}

}

class QueryThread extends MyThread {
	Ontology o;
	WSMLReasoner reasoner;
	LogicalExpression query;

	QueryThread(Ontology o, WSMLReasoner reasoner,LogicalExpression query) {
		this.o = o;
		this.reasoner = reasoner;
		this.query=query;
	}
	long t2=-1;
	public void run() {
	    long t2_start = System.currentTimeMillis();
        TestPerformanceWithUseOfFeatures.result = reasoner.executeQuery((IRI) o.getIdentifier(), query);
        long t2_end = System.currentTimeMillis();
        t2 = t2_end - t2_start;
//        System.out.print("  -"  +t2+ "-   ");
        isFinished = true;
	}
	
	public long getDuration(){
		return t2;
	}

}