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
    public static int NO_OF_TESTRUNS = 1;
    public static int TIMELIMIT_QUERY = 20000;
    public static int TIMELIMIT_REGISTRATION = 60000;
    public static int WAIT_INTERVAL = 1000;
    
    int evalmethod = 3;
    
    
    String directoryPath = "performance/performance/results/";
    
    String[] reasonerNames = new String[]{"KAON", "MINS", "IRIS"};

    /**
     * @param args
     *            none expected
     */
    public static void main(String[] args)throws Exception {
    	if (args!=null && args.length>0){
    		NO_OF_TESTRUNS = Integer.parseInt(args[0]);
    	}
    	
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
    	ex.testCardinality1maxOntologies();
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
        runPerformanceTests(reasonerNames, ontologies, fileName);
    }

    Ontology[] loadOntologies(String dir){
    	System.out.println("Loading ontologies "+dir);
    	File d = new File(dir);
    	Map<String,Ontology> onts = new TreeMap<String, Ontology>(); 
    	File[] files = d.listFiles(wsmlFilter);
    	for (File f:files){
    		try {
				Ontology ont = (Ontology)wsmlParser.parse(new FileReader(f))[0];
				onts.put(f.getName(),ont);
			} catch (Exception e) {
				System.out.println(f.getAbsolutePath()+"/"+f.getName());
				throw new RuntimeException("could not load ontology",e);
			}
    	}
    	Ontology[] ret = new Ontology[onts.size()];
    	int i=0;
    	for (Ontology o:onts.values()){
    		ret[i++]=o;
    	}
    	return ret;
    }
    
    FileFilter wsmlFilter = new FileFilter(){
		public boolean accept(File pathname) {
			return pathname.toString().endsWith(".wsml");
		}
    };
    
    static final String BASE="performance/performance/results/";
    /**
     * loads subconcept type ontologies and performs sample queries
     */
    public void testSubconceptOntologies() throws Exception {
        String fileName = "subconcept/";
    	Ontology[] ontologies = loadOntologies(BASE+fileName);
        runPerformanceTests(reasonerNames, ontologies, fileName);
    }
    
    /**
     * loads deep subconcept type ontologies and performs sample queries
     */
    public void testDeepSubconceptOntologies() throws Exception {
        String path = "deepSubconcept/";
    	Ontology[] ontologies = loadOntologies(BASE+path);
        runPerformanceTests(reasonerNames, ontologies, path);
    }

    /**
     * loads instance type ontologies and performs sample queries
     */
    public void testInstanceOntologies() throws Exception {
        String path = "instance/";
        Ontology[] ontologies = loadOntologies(BASE+path);
        runPerformanceTests(reasonerNames, ontologies, path);
    }
    
    /**
     * loads instance and subconcept type ontologies and performs sample queries
     */
    public void testInstanceANDsubconceptOntologies() throws Exception {
        String path = "instanceANDsubconcept/";
        Ontology[] ontologies = loadOntologies(BASE+path);
        runPerformanceTests(reasonerNames, ontologies, path);
    }
    
    /**
     * loads instance and deep subconcept type ontologies and performs sample queries
     */
    public void testInstanceANDdeepSubconceptOntologies() throws Exception {
        String path = "instanceANDdeepSubconcept/";
        Ontology[] ontologies = loadOntologies(BASE+path);
        runPerformanceTests(reasonerNames, ontologies, path);
    }
    
    /**
     * loads ofType type ontologies and performs sample queries
     */
    public void testOfTypeOntologies() throws Exception {
        String path = "ofType/";
        Ontology[] ontologies = loadOntologies(BASE+path);
        runPerformanceTests(reasonerNames, ontologies, path);
    }
    
    /**
     * loads ofType and subconcept type ontologies and performs sample queries
     */
    public void testOfTypeANDsubconceptOntologies() throws Exception {
        String path = "ofTypeANDsubconcept/";
        Ontology[] ontologies = loadOntologies(BASE+path);
        runPerformanceTests(reasonerNames, ontologies, path);
    }
    
    /**
     * loads cardinality (0 1) type ontologies and performs sample queries
     */
    public void testCardinality01Ontologies() throws Exception {
        String path = "cardinality_0_1/";
        Ontology[] ontologies = loadOntologies(BASE+path);
        runPerformanceTests(reasonerNames, ontologies, path);
    }
    
    /**
     * loads cardinality (0 10) type ontologies and performs sample queries
     */
    public void testCardinality010Ontologies() throws Exception {
        String path = "cardinality_0_10/";
        Ontology[] ontologies = loadOntologies(BASE+path);
        runPerformanceTests(reasonerNames, ontologies, path);
    }
    
    /**
     * loads cardinality (1 *) type ontologies and performs sample queries
     */
    public void testCardinality1maxOntologies() throws Exception {
        String path = "cardinality_1_max/";
        Ontology[] ontologies = loadOntologies(BASE+path);
        runPerformanceTests(reasonerNames, ontologies, path);
    }
    
    /**
     * loads inverse attribute type ontologies and performs sample queries
     */
    public void testInverseAttributeOntologies() throws Exception {
        String path = "inverseAttribute/";
        Ontology[] ontologies = loadOntologies(BASE+path);
        runPerformanceTests(reasonerNames, ontologies, path);
    }
    
    /**
     * loads transitive attribute type ontologies and performs sample queries
     */
    public void testTransitiveAttributeOntologies() throws Exception {
        String path = "transitiveAttribute/";
        Ontology[] ontologies = loadOntologies(BASE+path);
        runPerformanceTests(reasonerNames, ontologies, path);
    }
    
    /**
     * loads symmetric attribute type ontologies and performs sample queries
     */
    public void testSymmetricAttributeOntologies() throws Exception {
        String path = "symmetricAttribute/";
        Ontology[] ontologies = loadOntologies(BASE+path);
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
        runPerformanceTests(reasonerNames, ontologies, path);
    }
    
    public void testGloballyStratifiedNegation() throws Exception {
        String path = "globallyStratifiedNegation/";
        Ontology[] ontologies = loadOntologies(BASE+path);
        runPerformanceTests(reasonerNames, ontologies, path);
    }
    
    /**
     * loads built-in attribute type ontologies and performs sample queries
     */
    public void testBuiltInAttributeOntologies() throws Exception {
        String path = "built_in/";
        Ontology[] ontologies = loadOntologies(BASE+path);
        runPerformanceTests(reasonerNames, ontologies, path);
    }
    
    private void runPerformanceTests(String[] reasonerNames, Ontology[] ontologies, String path) 
    		throws SynchronisationException, InvalidModelException, ParserException, 
    		InconsistencyException, IOException {
    	
    	timedOutReasoner.clear();
    	SortedMap<String,String> queries = new TreeMap<String, String>();
        
        PerformanceResults performanceresults = new PerformanceResults();
        MyStringBuffer log = new MyStringBuffer();
        MyStringBuffer resultLog = new MyStringBuffer();
        for (Ontology ontology : ontologies){
            if (ontology.listRelationInstances().isEmpty()){
                log.println("WARNING: "+ontology.getIdentifier()+" has no queries defined\n");
            }
            for (RelationInstance r: (Set<RelationInstance>)ontology.listRelationInstances()){
                //assume on axiom per query
                IRI id=(IRI)r.getRelation().getIdentifier();
                if (id.getLocalName().toString().startsWith("query")){
                    queries.put(
                    		id.getLocalName().toString(), 
                    		r.getParameterValue((byte)0).toString());
                }
            }
          
            for (int i = 0; i < reasonerNames.length; i++){      
                for (Entry<String, String> query : queries.entrySet()){
                	try{
                		PerformanceResult result = executeQuery(query.getValue(), ontology,reasonerNames[i],log, resultLog);
	                    performanceresults.addReasonerPerformaceResult(
	                            reasonerNames[i], 
	                            ontology, 
	                            query.getKey(), 
	                            result);
                	}catch (Exception e){
                		log.println("error with ontology "+ontology.getIdentifier()+" and reasoner "+reasonerNames[i]);
                		log.println(e.getMessage());
                		log.println(e.getCause()+"");
                		e.printStackTrace();
                	}
                }
                System.gc();
            }
        }
        performanceresults.writeAll(new File(directoryPath + path).getAbsolutePath());
        log.write(new File(directoryPath + path + "log.txt"));
        resultLog.write(new File(directoryPath + path + "resultLog.txt"));
    }
      //reasoner, {regist, query}
    Map<String,Set<String>> timedOutReasoner = new HashMap<String, Set<String>>();
    
    private WSMLReasoner createReasoner(String theReasonerName, MyStringBuffer log) throws InconsistencyException{
        log.print("Creating reasoner ");
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
        log.println("(" + t0 + "ms)");
        return reasoner;
    }
    
    private PerformanceResult executeQuery(
    		String theQuery,
    		Ontology theOntology, 
    		String theReasonerName,
    		MyStringBuffer log,
    		MyStringBuffer resultLog) throws ParserException, InconsistencyException {
        
    	if (timedOutReasoner.containsKey(theReasonerName)){
    		Set<String> detail = timedOutReasoner.get(theReasonerName);
    		if (detail.contains(theQuery) || detail.contains("registration")){
    			log.println(theReasonerName+" has previously already timed out!!");
    			return new PerformanceResult(null);
    		}
    	}
        
        log.println("\n------------------------------------");
        log.println("query = '" + theQuery + "'");
        log.println("ontology = '" + theOntology.getIdentifier() + "'");
        log.println("reasoner = '" + theReasonerName + "'");
        resultLog.printlnLogFile("\n------------------------------------");
        resultLog.printlnLogFile("query = '" + theQuery + "'");
        resultLog.printlnLogFile("ontology = '" + theOntology.getIdentifier() + "'");
        resultLog.printlnLogFile("reasoner = '" + theReasonerName + "'");
        
        WSMLReasoner reasoner = createReasoner(theReasonerName,log);
    	PerformanceResult performanceresult = new PerformanceResult(reasoner);
        if (reasoner==null){
        	log.println("ERROR could not create Reasoner");
        	return performanceresult;
        }
    
        LogicalExpression query = new WSMO4JManager().getLogicalExpressionFactory().createLogicalExpression(theQuery, theOntology);
        
        log.print("Registering Ontology (in total) ");
        RegistrationThread registrationThread = new RegistrationThread(theOntology,reasoner,performanceresult,log);
        registrationThread.start();
        long counter=0;
        waitUntilAlive(registrationThread);
        while(registrationThread.isAlive()){
        	if (counter > TIMELIMIT_REGISTRATION){
        		log.print("stopping registration thread due to timeout");
        		registrationThread.stop();
        		addTimeOut(theReasonerName, "registration");
        		return performanceresult;
        	}
        	waitABit();
        	counter += WAIT_INTERVAL;
        }
       
        boolean ok = true;
        for (int i=0; i<NO_OF_TESTRUNS && ok ;i++){
            QueryThread queryThread = new QueryThread(theOntology,reasoner,query, log, resultLog);
            log.print("Executing query ");
            queryThread.start();
            waitUntilAlive(queryThread);
            counter=0;
            while(queryThread.isAlive()){
	            if (counter > TIMELIMIT_QUERY){
	            	ok=false;
            		log.println("stopping reasoning thread due to timeout");
            		queryThread.stop();
            		addTimeOut(theReasonerName, theQuery);
            	}
	            waitABit();
            	counter += WAIT_INTERVAL;
            }
            long t2=queryThread.getDuration();
            if (t2==-1) addTimeOut(theReasonerName, theQuery);
    	    performanceresult.addExecuteQuery(i, t2);
            log.println("(" + t2 + " ms)");
        }
        
        log.print("Deregistering Ontology ");
        long t3_start = System.currentTimeMillis();
        reasoner.deRegister();
        long t3_end = System.currentTimeMillis();
        long t3 = t3_end - t3_start;
        log.println("(" + t3 + "ms)");
        log.println("------------------------------------");
        return performanceresult;
    }
    
    private void addTimeOut(String theReasonerName, String theQuery ){
    	Set<String> detail;
		if (timedOutReasoner.containsKey(theReasonerName)){
			detail=timedOutReasoner.get(theReasonerName);
		}else{
			detail=new HashSet<String>();
			timedOutReasoner.put(theReasonerName,detail);
		}
		detail.add(theQuery);
    }
    
    
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
	MyStringBuffer log;

	RegistrationThread(Ontology o, WSMLReasoner reasoner, PerformanceResult performence, MyStringBuffer log) {
		this.o = o;
		this.reasoner = reasoner;
		this.performanceresult=performence;
		this.log=log;
	}

	public void run() {
		try {
//			System.out.println("Registering "+o.getIdentifier());
			long t1_start = System.currentTimeMillis();
			reasoner.registerOntology(o);
			long t1_end = System.currentTimeMillis();
			long t1 = t1_end-t1_start;
			log.println("(" + t1 + "ms)");
            performanceresult.setRegisterOntology(t1);
            log.println("Ontology normalization time: " + performanceresult.getNormalizeTime() + "ms");
            log.println("Ontology convertion time: " + performanceresult.getConvertTime() + "ms");
            log.println("Ontology consistency check time: " + 
            		performanceresult.getOntologyConsistencyCheckTime() + "ms");
            isFinished = true;
		} catch (Throwable e) {
			log.println("error registering ontology "+((IRI)o.getIdentifier()).getLocalName());
			log.println("  error detail: "+e);
//			throw new RuntimeException(e.toString(),e);
		}
	}

}

class QueryThread extends MyThread {
	Ontology o;
	WSMLReasoner reasoner;
	LogicalExpression query;
	MyStringBuffer log;
	MyStringBuffer resultLog;

	QueryThread(Ontology o, WSMLReasoner reasoner,LogicalExpression query,MyStringBuffer log, MyStringBuffer resultLog) {
		this.o = o;
		this.reasoner = reasoner;
		this.query=query;
		this.log=log;
		this.resultLog=resultLog;
	}
	long t2=-1;
	public void run() {
		try{
		    long t2_start = System.currentTimeMillis();
		    Set<Map<Variable, Term>> result = reasoner.executeQuery(query);
		    log.print("  size: "+result.size());
		    resultLog.printlnLogFile("Size of result set: "+result.size());
		    if (!result.isEmpty()){
		    	log.print(" sample: "+result.iterator().next());
		    	resultLog.printlnLogFile("All results: \n");
		    	for (Map<Variable, Term> map : result) {
		    		resultLog.printlnLogFile(map + "");
		    	}
		    }
	        long t2_end = System.currentTimeMillis();
	        t2 = t2_end - t2_start;
	//        System.out.print("  -"  +t2+ "-   ");
	        isFinished = true;
		}catch (Throwable e){
			log.println("error querying ontology "+((IRI)o.getIdentifier()).getLocalName()+
					" with query "+query.toString(o) +":");
			log.println("  error detail: "+e);
		}
	}
	
	public long getDuration(){
		return t2;
	}

}

class MyStringBuffer{
	
	StringBuffer log = new StringBuffer();
	
	public void println(String s){
		log.append(s+"\n");
		System.out.println(s);
	}
	
	public void printlnLogFile(String s) {
		log.append(s+"\n");
	}
	
	public void print(String s){
		log.append(s);
		System.out.print(s);
	}

	public void printLogFile(String s){
		log.append(s);
	}
	
	public String toString(){
		return log.toString();
	}
	
	public void write(File f) throws IOException{
		FileWriter fw = new FileWriter(f);
		fw.append(log);
		fw.close();
	}
}