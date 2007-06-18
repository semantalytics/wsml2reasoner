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

    int evalmethod = 2;
    
    String directoryPath = "performance/performance/results/";
    
    String[] reasonerNames = new String[]{"KAON", "MINS", "IRIS"};

    /**
     * @param args
     *            none expected
     */
    public static void main(String[] args) {
        TestPerformanceWithUseOfFeatures ex = new TestPerformanceWithUseOfFeatures();
        try {
//            ex.doTestRun();
//        	ex.testSubconceptOntologies();
//        	ex.testDeepSubconceptOntologies();
//        	ex.testInstanceOntologies();
//        	ex.testInstanceANDsubconceptOntologies();
//        	ex.testInstanceANDdeepSubconceptOntologies();
//        	ex.testOfTypeOntologies();
//        	ex.testOfTypeANDsubconceptOntologies();
//        	ex.testCardinality01Ontologies();
//        	ex.testCardinality010Ontologies();
//        	ex.testInverseAttributeOntologies();
//        	ex.testTransitiveAttributeOntologies();
//        	ex.testSymmetricAttributeOntologies();
//        	ex.testReflexiveAttributeOntologies();
//        	ex.testNegationOntologies();
//        	ex.testBuiltInAttributeOntologies();
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
        Ontology[] ontologies = new Ontology[]{o1,o2};
        String fileName = "simple/";
        runPerformanceTests(this.reasonerNames, ontologies, fileName);
    }

    /**
     * loads subconcept type ontologies and performs sample queries
     */
    public void testSubconceptOntologies() throws Exception {
    	Ontology o1 = loadOntology("performance/ontologies/subconcept/subconcept-1-ontology.wsml");
    	Ontology o2 = loadOntology("performance/ontologies/subconcept/subconcept-10-ontology.wsml");
    	Ontology o3 = loadOntology("performance/ontologies/subconcept/subconcept-100-ontology.wsml");
    	Ontology o4 = loadOntology("performance/ontologies/subconcept/subconcept-500-ontology.wsml");
    	Ontology o5 = loadOntology("performance/ontologies/subconcept/subconcept-1000-ontology.wsml");
    	Ontology o6 = loadOntology("performance/ontologies/subconcept/subconcept-5000-ontology.wsml");
    	Ontology o7 = loadOntology("performance/ontologies/subconcept/subconcept-10000-ontology.wsml");       
//        Ontology[] ontologies = new Ontology[] {o1,o2,o3,o4,o5,o6,o7};
        Ontology[] ontologies = new Ontology[] {o1,o2,o3,o4,o5};
//    	Ontology[] ontologies = new Ontology[] {o1};
        String fileName = "subconcept/";
        runPerformanceTests(this.reasonerNames, ontologies, fileName);
    }
    
    /**
     * loads deep subconcept type ontologies and performs sample queries
     */
    public void testDeepSubconceptOntologies() throws Exception {
    	Ontology o1 = loadOntology("performance/ontologies/deepSubconcept/deepSubconcept-1-ontology.wsml");
    	Ontology o2 = loadOntology("performance/ontologies/deepSubconcept/deepSubconcept-10-ontology.wsml");
    	Ontology o3 = loadOntology("performance/ontologies/deepSubconcept/deepSubconcept-100-ontology.wsml");
    	Ontology o4 = loadOntology("performance/ontologies/deepSubconcept/deepSubconcept-500-ontology.wsml");
    	Ontology o5 = loadOntology("performance/ontologies/deepSubconcept/deepSubconcept-1000-ontology.wsml");
    	Ontology o6 = loadOntology("performance/ontologies/deepSubconcept/deepSubconcept-5000-ontology.wsml");
    	Ontology o7 = loadOntology("performance/ontologies/deepSubconcept/deepSubconcept-10000-ontology.wsml");       
//    	Ontology[] ontologies = new Ontology[] {o1,o2,o3,o4,o5,o6,o7};
        Ontology[] ontologies = new Ontology[] {o1,o2};
//    	Ontology[] ontologies = new Ontology[] {o1};
        String path = "deepSubconcept/";
        runPerformanceTests(this.reasonerNames, ontologies, path);
    }

    /**
     * loads instance type ontologies and performs sample queries
     */
    public void testInstanceOntologies() throws Exception {
    	Ontology o1 = loadOntology("performance/ontologies/instance/instance-1-ontology.wsml");
    	Ontology o2 = loadOntology("performance/ontologies/instance/instance-10-ontology.wsml");
    	Ontology o3 = loadOntology("performance/ontologies/instance/instance-100-ontology.wsml");
    	Ontology o4 = loadOntology("performance/ontologies/instance/instance-500-ontology.wsml");
    	Ontology o5 = loadOntology("performance/ontologies/instance/instance-1000-ontology.wsml");
    	Ontology o6 = loadOntology("performance/ontologies/instance/instance-5000-ontology.wsml");
    	Ontology o7 = loadOntology("performance/ontologies/instance/instance-10000-ontology.wsml");       
//    	Ontology[] ontologies = new Ontology[] {o1,o2,o3,o4,o5,o6,o7};
        Ontology[] ontologies = new Ontology[] {o1,o2,o3,o4,o5};
//    	Ontology[] ontologies = new Ontology[] {o1};
        String path = "instance/";
        runPerformanceTests(this.reasonerNames, ontologies, path);
    }
    
    /**
     * loads instance and subconcept type ontologies and performs sample queries
     */
    public void testInstanceANDsubconceptOntologies() throws Exception {
    	Ontology o1 = loadOntology("performance/ontologies/instanceANDsubconcept/instanceANDsubconcept-1-ontology.wsml");
    	Ontology o2 = loadOntology("performance/ontologies/instanceANDsubconcept/instanceANDsubconcept-10-ontology.wsml");
    	Ontology o3 = loadOntology("performance/ontologies/instanceANDsubconcept/instanceANDsubconcept-100-ontology.wsml");
    	Ontology o4 = loadOntology("performance/ontologies/instanceANDsubconcept/instanceANDsubconcept-500-ontology.wsml");
    	Ontology o5 = loadOntology("performance/ontologies/instanceANDsubconcept/instanceANDsubconcept-1000-ontology.wsml");
    	Ontology o6 = loadOntology("performance/ontologies/instanceANDsubconcept/instanceANDsubconcept-5000-ontology.wsml");
    	Ontology o7 = loadOntology("performance/ontologies/instanceANDsubconcept/instanceANDsubconcept-10000-ontology.wsml");       
//    	Ontology[] ontologies = new Ontology[] {o1,o2,o3,o4,o5,o6,o7};
        Ontology[] ontologies = new Ontology[] {o1,o2,o3,o4,o5};
//    	Ontology[] ontologies = new Ontology[] {o1};
        String path = "instanceANDsubconcept/";
        runPerformanceTests(this.reasonerNames, ontologies, path);
    }
    
    /**
     * loads instance and deep subconcept type ontologies and performs sample queries
     */
    public void testInstanceANDdeepSubconceptOntologies() throws Exception {
    	Ontology o1 = loadOntology("performance/ontologies/instanceANDdeepSubconcept/instanceANDdeepSubconcept-1-ontology.wsml");
    	Ontology o2 = loadOntology("performance/ontologies/instanceANDdeepSubconcept/instanceANDdeepSubconcept-10-ontology.wsml");
    	Ontology o3 = loadOntology("performance/ontologies/instanceANDdeepSubconcept/instanceANDdeepSubconcept-100-ontology.wsml");
    	Ontology o4 = loadOntology("performance/ontologies/instanceANDdeepSubconcept/instanceANDdeepSubconcept-500-ontology.wsml");
    	Ontology o5 = loadOntology("performance/ontologies/instanceANDdeepSubconcept/instanceANDdeepSubconcept-1000-ontology.wsml");
    	Ontology o6 = loadOntology("performance/ontologies/instanceANDdeepSubconcept/instanceANDdeepSubconcept-5000-ontology.wsml");
    	Ontology o7 = loadOntology("performance/ontologies/instanceANDdeepSubconcept/instanceANDdeepSubconcept-10000-ontology.wsml");       
//    	Ontology[] ontologies = new Ontology[] {o1,o2,o3,o4,o5,o6,o7};
        Ontology[] ontologies = new Ontology[] {o1,o2};
//    	Ontology[] ontologies = new Ontology[] {o1};
        String path = "instanceANDdeepSubconcept/";
        runPerformanceTests(this.reasonerNames, ontologies, path);
    }
    
    /**
     * loads ofType type ontologies and performs sample queries
     */
    public void testOfTypeOntologies() throws Exception {
    	Ontology o1 = loadOntology("performance/ontologies/ofType/ofType-1-ontology.wsml");
    	Ontology o2 = loadOntology("performance/ontologies/ofType/ofType-10-ontology.wsml");
    	Ontology o3 = loadOntology("performance/ontologies/ofType/ofType-100-ontology.wsml");
    	Ontology o4 = loadOntology("performance/ontologies/ofType/ofType-500-ontology.wsml");
    	Ontology o5 = loadOntology("performance/ontologies/ofType/ofType-1000-ontology.wsml");
    	Ontology o6 = loadOntology("performance/ontologies/ofType/ofType-5000-ontology.wsml");
    	Ontology o7 = loadOntology("performance/ontologies/ofType/ofType-10000-ontology.wsml");       
//    	Ontology[] ontologies = new Ontology[] {o1,o2,o3,o4,o5,o6,o7};
        Ontology[] ontologies = new Ontology[] {o1,o2,o3,o4,o5};
//    	Ontology[] ontologies = new Ontology[] {o1};
        String path = "ofType/";
        String[] reasonerNames = new String[]{"MINS", "KAON"};
        runPerformanceTests(reasonerNames, ontologies, path);
    }
    
    /**
     * loads ofType and subconcept type ontologies and performs sample queries
     */
    public void testOfTypeANDsubconceptOntologies() throws Exception {
    	Ontology o1 = loadOntology("performance/ontologies/ofTypeANDsubconcept/ofTypeANDsubconcept-1-ontology.wsml");
    	Ontology o2 = loadOntology("performance/ontologies/ofTypeANDsubconcept/ofTypeANDsubconcept-10-ontology.wsml");
    	Ontology o3 = loadOntology("performance/ontologies/ofTypeANDsubconcept/ofTypeANDsubconcept-100-ontology.wsml");
    	Ontology o4 = loadOntology("performance/ontologies/ofTypeANDsubconcept/ofTypeANDsubconcept-500-ontology.wsml");
    	Ontology o5 = loadOntology("performance/ontologies/ofTypeANDsubconcept/ofTypeANDsubconcept-1000-ontology.wsml");
    	Ontology o6 = loadOntology("performance/ontologies/ofTypeANDsubconcept/ofTypeANDsubconcept-5000-ontology.wsml");
    	Ontology o7 = loadOntology("performance/ontologies/ofTypeANDsubconcept/ofTypeANDsubconcept-10000-ontology.wsml");       
//    	Ontology[] ontologies = new Ontology[] {o1,o2,o3,o4,o5,o6,o7};
        Ontology[] ontologies = new Ontology[] {o1,o2,o3,o4,o5};
//    	Ontology[] ontologies = new Ontology[] {o1};
        String path = "ofTypeANDsubconcept/";
        runPerformanceTests(this.reasonerNames, ontologies, path);
    }
    
    /**
     * loads cardinality (0 1) type ontologies and performs sample queries
     */
    public void testCardinality01Ontologies() throws Exception {
    	Ontology o1 = loadOntology("performance/ontologies/cardinality_0_1/cardinality_0_1-1-ontology.wsml");
    	Ontology o2 = loadOntology("performance/ontologies/cardinality_0_1/cardinality_0_1-10-ontology.wsml");
    	Ontology o3 = loadOntology("performance/ontologies/cardinality_0_1/cardinality_0_1-100-ontology.wsml");
    	Ontology o4 = loadOntology("performance/ontologies/cardinality_0_1/cardinality_0_1-500-ontology.wsml");
    	Ontology o5 = loadOntology("performance/ontologies/cardinality_0_1/cardinality_0_1-1000-ontology.wsml");
    	Ontology o6 = loadOntology("performance/ontologies/cardinality_0_1/cardinality_0_1-5000-ontology.wsml");
    	Ontology o7 = loadOntology("performance/ontologies/cardinality_0_1/cardinality_0_1-10000-ontology.wsml");       
//    	Ontology[] ontologies = new Ontology[] {o1,o2,o3,o4,o5,o6,o7};
        Ontology[] ontologies = new Ontology[] {o1,o2,o3,o4,o5};
//    	Ontology[] ontologies = new Ontology[] {o1};
        String path = "cardinality_0_1/";
        String[] reasonerNames = new String[]{"MINS", "KAON"};
        runPerformanceTests(reasonerNames, ontologies, path);
    }
    
    /**
     * loads cardinality (0 10) type ontologies and performs sample queries
     */
    public void testCardinality010Ontologies() throws Exception {
    	Ontology o1 = loadOntology("performance/ontologies/cardinality_0_10/cardinality_0_10-1-ontology.wsml");
    	Ontology o2 = loadOntology("performance/ontologies/cardinality_0_10/cardinality_0_10-10-ontology.wsml");
    	Ontology o3 = loadOntology("performance/ontologies/cardinality_0_10/cardinality_0_10-100-ontology.wsml");
    	Ontology o4 = loadOntology("performance/ontologies/cardinality_0_10/cardinality_0_10-500-ontology.wsml");
    	Ontology o5 = loadOntology("performance/ontologies/cardinality_0_10/cardinality_0_10-1000-ontology.wsml");
    	Ontology o6 = loadOntology("performance/ontologies/cardinality_0_10/cardinality_0_10-5000-ontology.wsml");
    	Ontology o7 = loadOntology("performance/ontologies/cardinality_0_10/cardinality_0_10-10000-ontology.wsml");       
//    	Ontology[] ontologies = new Ontology[] {o1,o2,o3,o4,o5,o6,o7};
        Ontology[] ontologies = new Ontology[] {o1,o2,o3};
//    	Ontology[] ontologies = new Ontology[] {o1};
        String path = "cardinality_0_10/";
        String[] reasonerNames = new String[]{"KAON", "MINS"};
        runPerformanceTests(reasonerNames, ontologies, path);
    }
    
    /**
     * loads inverse attribute type ontologies and performs sample queries
     */
    public void testInverseAttributeOntologies() throws Exception {
    	Ontology o1 = loadOntology("performance/ontologies/inverseAttribute/inverseAttribute-1-ontology.wsml");
    	Ontology o2 = loadOntology("performance/ontologies/inverseAttribute/inverseAttribute-10-ontology.wsml");
    	Ontology o3 = loadOntology("performance/ontologies/inverseAttribute/inverseAttribute-100-ontology.wsml");
    	Ontology o4 = loadOntology("performance/ontologies/inverseAttribute/inverseAttribute-500-ontology.wsml");
    	Ontology o5 = loadOntology("performance/ontologies/inverseAttribute/inverseAttribute-1000-ontology.wsml");
    	Ontology o6 = loadOntology("performance/ontologies/inverseAttribute/inverseAttribute-5000-ontology.wsml");
    	Ontology o7 = loadOntology("performance/ontologies/inverseAttribute/inverseAttribute-10000-ontology.wsml");       
//    	Ontology[] ontologies = new Ontology[] {o1,o2,o3,o4,o5,o6,o7};
        Ontology[] ontologies = new Ontology[] {o1,o2,o3};
//    	Ontology[] ontologies = new Ontology[] {o1};
        String path = "inverseAttribute/";
        runPerformanceTests(this.reasonerNames, ontologies, path);
    }
    
    /**
     * loads transitive attribute type ontologies and performs sample queries
     */
    public void testTransitiveAttributeOntologies() throws Exception {
    	Ontology o1 = loadOntology("performance/ontologies/transitiveAttribute/transitiveAttribute-1-ontology.wsml");
    	Ontology o2 = loadOntology("performance/ontologies/transitiveAttribute/transitiveAttribute-10-ontology.wsml");
    	Ontology o3 = loadOntology("performance/ontologies/transitiveAttribute/transitiveAttribute-100-ontology.wsml");
    	Ontology o4 = loadOntology("performance/ontologies/transitiveAttribute/transitiveAttribute-500-ontology.wsml");
    	Ontology o5 = loadOntology("performance/ontologies/transitiveAttribute/transitiveAttribute-1000-ontology.wsml");
    	Ontology o6 = loadOntology("performance/ontologies/transitiveAttribute/transitiveAttribute-5000-ontology.wsml");
    	Ontology o7 = loadOntology("performance/ontologies/transitiveAttribute/transitiveAttribute-10000-ontology.wsml");       
//    	Ontology[] ontologies = new Ontology[] {o1,o2,o3,o4,o5,o6,o7};
        Ontology[] ontologies = new Ontology[] {o1,o2,o3};
//    	Ontology[] ontologies = new Ontology[] {o1};
        String path = "transitiveAttribute/";
        String[] reasonerNames = new String[]{"MINS", "KAON"};
        runPerformanceTests(reasonerNames, ontologies, path);
    }
    
    /**
     * loads symmetric attribute type ontologies and performs sample queries
     */
    public void testSymmetricAttributeOntologies() throws Exception {
    	Ontology o1 = loadOntology("performance/ontologies/symmetricAttribute/symmetricAttribute-1-ontology.wsml");
    	Ontology o2 = loadOntology("performance/ontologies/symmetricAttribute/symmetricAttribute-10-ontology.wsml");
    	Ontology o3 = loadOntology("performance/ontologies/symmetricAttribute/symmetricAttribute-100-ontology.wsml");
    	Ontology o4 = loadOntology("performance/ontologies/symmetricAttribute/symmetricAttribute-500-ontology.wsml");
    	Ontology o5 = loadOntology("performance/ontologies/symmetricAttribute/symmetricAttribute-1000-ontology.wsml");
    	Ontology o6 = loadOntology("performance/ontologies/symmetricAttribute/symmetricAttribute-5000-ontology.wsml");
    	Ontology o7 = loadOntology("performance/ontologies/symmetricAttribute/symmetricAttribute-10000-ontology.wsml");       
//    	Ontology[] ontologies = new Ontology[] {o1,o2,o3,o4,o5,o6,o7};
        Ontology[] ontologies = new Ontology[] {o1,o2,o3,o4};
//    	Ontology[] ontologies = new Ontology[] {o1};
        String path = "symmetricAttribute/";
        String[] reasonerNames = new String[]{"MINS", "KAON"};
        runPerformanceTests(reasonerNames, ontologies, path);
    }
    
    /**
     * loads reflexive attribute type ontologies and performs sample queries
     */
    public void testReflexiveAttributeOntologies() throws Exception {
    	Ontology o1 = loadOntology("performance/ontologies/reflexiveAttribute/reflexiveAttribute-1-ontology.wsml");
    	Ontology o2 = loadOntology("performance/ontologies/reflexiveAttribute/reflexiveAttribute-10-ontology.wsml");
    	Ontology o3 = loadOntology("performance/ontologies/reflexiveAttribute/reflexiveAttribute-100-ontology.wsml");
    	Ontology o4 = loadOntology("performance/ontologies/reflexiveAttribute/reflexiveAttribute-500-ontology.wsml");
    	Ontology o5 = loadOntology("performance/ontologies/reflexiveAttribute/reflexiveAttribute-1000-ontology.wsml");
    	Ontology o6 = loadOntology("performance/ontologies/reflexiveAttribute/reflexiveAttribute-5000-ontology.wsml");
    	Ontology o7 = loadOntology("performance/ontologies/reflexiveAttribute/reflexiveAttribute-10000-ontology.wsml");       
//    	Ontology[] ontologies = new Ontology[] {o1,o2,o3,o4,o5,o6,o7};
        Ontology[] ontologies = new Ontology[] {o1,o2,o3,o4,o5};
//    	Ontology[] ontologies = new Ontology[] {o1};
        String path = "reflexiveAttribute/";
        runPerformanceTests(this.reasonerNames, ontologies, path);
    }
    
    /**
     * loads negation logical expression type ontologies and performs sample queries
     */
    public void testNegationOntologies() throws Exception {
    	Ontology o1 = loadOntology("performance/ontologies/negation/negation-1-ontology.wsml");
    	Ontology o2 = loadOntology("performance/ontologies/negation/negation-10-ontology.wsml");
    	Ontology o3 = loadOntology("performance/ontologies/negation/negation-100-ontology.wsml");
    	Ontology o4 = loadOntology("performance/ontologies/negation/negation-500-ontology.wsml");
    	Ontology o5 = loadOntology("performance/ontologies/negation/negation-1000-ontology.wsml");
    	Ontology o6 = loadOntology("performance/ontologies/negation/negation-5000-ontology.wsml");
    	Ontology o7 = loadOntology("performance/ontologies/negation/negation-10000-ontology.wsml");       
//    	Ontology[] ontologies = new Ontology[] {o1,o2,o3,o4,o5,o6,o7};
        Ontology[] ontologies = new Ontology[] {o1,o2,o3,o4,o5};
//    	Ontology[] ontologies = new Ontology[] {o1};
        String path = "negation/";
        String[] reasonerNames = new String[]{"MINS", "KAON"};
        runPerformanceTests(reasonerNames, ontologies, path);
    }
    
    /**
     * loads built-in attribute type ontologies and performs sample queries
     */
    public void testBuiltInAttributeOntologies() throws Exception {
    	Ontology o1 = loadOntology("performance/ontologies/built_in/built_in-1-ontology.wsml");
    	Ontology o2 = loadOntology("performance/ontologies/built_in/built_in-10-ontology.wsml");
    	Ontology o3 = loadOntology("performance/ontologies/built_in/built_in-100-ontology.wsml");
    	Ontology o4 = loadOntology("performance/ontologies/built_in/built_in-500-ontology.wsml");
    	Ontology o5 = loadOntology("performance/ontologies/built_in/built_in-1000-ontology.wsml");
    	Ontology o6 = loadOntology("performance/ontologies/built_in/built_in-5000-ontology.wsml");
    	Ontology o7 = loadOntology("performance/ontologies/built_in/built_in-10000-ontology.wsml");       
//    	Ontology[] ontologies = new Ontology[] {o1,o2,o3,o4,o5,o6,o7};
        Ontology[] ontologies = new Ontology[] {o1,o2,o3,o4,o5};
//    	Ontology[] ontologies = new Ontology[] {o1};
        String path = "built_in/";
        runPerformanceTests(this.reasonerNames, ontologies, path);
    }
    
    private void runPerformanceTests(String[] reasonerNames, Ontology[] ontologies, String path) 
    		throws SynchronisationException, InvalidModelException, ParserException, 
    		InconsistencyException, IOException {
    	boolean printResults = false;
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
                System.gc();
            }
        }
        performanceresults.write(new File(directoryPath + path).getAbsolutePath());
    }
    
    private PerformanceResult executeQuery(String theQuery, Ontology theOntology, 
    		String theReasonerName, boolean thePrintResults) 
    		throws ParserException, InconsistencyException {
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
            LogicalExpression query = new WSMO4JManager().getLogicalExpressionFactory().
            		createLogicalExpression(theQuery, theOntology);
            
            System.out.print("Registering Ontology ");
            long t1_start = System.currentTimeMillis();
            reasoner.registerOntology(theOntology);
            long t1_end = System.currentTimeMillis();
            long t1 = t1_end - t1_start;
            performanceresult.setRegisterOntology(t1);
            System.out.println("(" + t1 + "ms)");
            
            Set<Map<Variable, Term>> result = null;
            // j determines how many iterations of one query are done
            int j = 20;
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
