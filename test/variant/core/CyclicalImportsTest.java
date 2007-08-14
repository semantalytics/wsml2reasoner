package variant.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.api.WSMLReasonerFactory.BuiltInReasoner;

import base.BaseReasonerTest;

public class CyclicalImportsTest extends BaseReasonerTest {
	
    private static final String ONTOLOGY_FILE = "test/files/CyclicalImports1.wsml";
    
    String ns = "http://here.comes.the.whistleman/CyclicalImports1#";

    BuiltInReasoner previous;

    protected void setUp() throws Exception {
    	super.setUp();
    	parseThis(getReaderForFile("test/files/CyclicalImports2.wsml"));
        setupScenario(ONTOLOGY_FILE); 
        previous =  BaseReasonerTest.reasoner;             
     }

    protected void tearDown() throws Exception {
    	super.tearDown();
    	BaseReasonerTest.reasoner = previous;
    }

    public void cyclicalImports4Datalog() throws Exception {
        String query = "?x memberOf ?z";
        Set<Map<Variable, Term>> expected = new HashSet<Map<Variable, Term>>();
        Map<Variable, Term> binding = new HashMap<Variable, Term>();
        binding.put(leFactory.createVariable("x"),  wsmoFactory.createIRI("http://here.comes.the.whistleman/CyclicalImports2#Cy2i1"));
        binding.put(leFactory.createVariable("z"),  wsmoFactory.createIRI(ns + "Master"));
        expected.add(binding);
        binding.put(leFactory.createVariable("x"),  wsmoFactory.createIRI(ns + "Cy1i1"));
        binding.put(leFactory.createVariable("z"),  wsmoFactory.createIRI("http://here.comes.the.whistleman/CyclicalImports2#Slave"));
        expected.add(binding);
        performQuery(query, expected);
        System.out.println("Finished query.");
    }
    
    public void cyclicalImports() throws Exception {
        String query = "?x memberOf JazzMusician";
        String concept = ns + "JazzMusician"; //needed for DL query
        Set<Map<Variable, Term>> expected = new HashSet<Map<Variable, Term>>();
        Map<Variable, Term> binding = new HashMap<Variable, Term>();
        binding.put(leFactory.createVariable("x"),  wsmoFactory.createIRI("http://here.comes.the.whistleman/CyclicalImports2#KarlDenson"));
        expected.add(binding);
        binding.put(leFactory.createVariable("x"),  wsmoFactory.createIRI("http://here.comes.the.whistleman/CyclicalImports2#JohnMedeski"));
        expected.add(binding);
        binding.put(leFactory.createVariable("x"),  wsmoFactory.createIRI(ns + "RolandKirk"));
        expected.add(binding);
        binding.put(leFactory.createVariable("x"),  wsmoFactory.createIRI(ns + "JohnScofield"));
        expected.add(binding);
        // This check could be problematic when querying over multiple ontologies
        if (BaseReasonerTest.reasoner.equals(WSMLReasonerFactory.BuiltInReasoner.PELLET)){
        	int i = 0; //indicates DL instance retrieval
        	performDLQuery(i, concept, expected);
        }
        else performQuery(query, expected);
        System.out.println("Finished query.");
    }
    
    
    
    public void testAllReasoners() throws Exception{
    	BaseReasonerTest.reasoner = WSMLReasonerFactory.BuiltInReasoner.IRIS;
    	//cyclicalImports4Datalog();
    	cyclicalImports();
    	
    	BaseReasonerTest.reasoner = WSMLReasonerFactory.BuiltInReasoner.MINS;
    	//cyclicalImports4Datalog();
    	cyclicalImports();
    	
    	BaseReasonerTest.reasoner = WSMLReasonerFactory.BuiltInReasoner.KAON2;
    	//cyclicalImports4Datalog();
    	cyclicalImports();
    	
    	BaseReasonerTest.reasoner = WSMLReasonerFactory.BuiltInReasoner.PELLET;
    	cyclicalImports();
    }
}
