package abstractTests.core;

import helper.CoreHelper;
import helper.OntologyHelper;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import junit.framework.TestCase;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;
import abstractTests.CoreTest;

public abstract class AbstractCyclicalImports extends TestCase implements CoreTest {
	
    private static final String ONTOLOGY_FILE1 = "files/CyclicalImports1.wsml";
    private static final String ONTOLOGY_FILE2 = "files/CyclicalImports2.wsml";
    
    private static final String ns1 = "http://here.comes.the.whistleman/CyclicalImports1#";
    private static final String ns2 = "http://here.comes.the.whistleman/CyclicalImports2#";

    protected void setUp() throws Exception {
    	// Don't know why this is done here.
    	// Maybe to ensure that the ontology is reachable/loadable.
    	OntologyHelper.loadOntology( ONTOLOGY_FILE2 );

    	// 	 Set up factories for creating WSML elements
	   	wsmoManager = new WSMO4JManager();
	
	   	leFactory = wsmoManager.getLogicalExpressionFactory();
	   	wsmoFactory = wsmoManager.getWSMOFactory();
    }

    public void testCyclicalImports1() throws Exception {
        Set<Map<Variable, Term>> expected = new HashSet<Map<Variable, Term>>();
        Map<Variable, Term> binding = new HashMap<Variable, Term>();
        binding.put(leFactory.createVariable("X"),  wsmoFactory.createIRI(ns2 + "Cy2i1"));
        binding.put(leFactory.createVariable("Y"),  wsmoFactory.createIRI(ns1 + "Master"));
        expected.add(binding);
        
        binding = new HashMap<Variable, Term>();
        binding.put(leFactory.createVariable("X"),  wsmoFactory.createIRI(ns1 + "Cy1i1"));
        binding.put(leFactory.createVariable("Y"),  wsmoFactory.createIRI(ns2 + "Slave"));
        expected.add(binding);
        
        binding = new HashMap<Variable, Term>();
        binding.put(leFactory.createVariable("X"),  wsmoFactory.createIRI(ns1 + "RolandKirk"));
        binding.put(leFactory.createVariable("Y"),  wsmoFactory.createIRI(ns1 + "JazzMusician"));
        expected.add(binding);
        
        binding = new HashMap<Variable, Term>();
        binding.put(leFactory.createVariable("X"),  wsmoFactory.createIRI(ns1 + "JohnScofield"));
        binding.put(leFactory.createVariable("Y"),  wsmoFactory.createIRI(ns1 + "JazzMusician"));
        expected.add(binding);
        
        binding = new HashMap<Variable, Term>();
        binding.put(leFactory.createVariable("X"),  wsmoFactory.createIRI(ns2 + "KarlDenson"));
        binding.put(leFactory.createVariable("Y"),  wsmoFactory.createIRI(ns1 + "JazzMusician"));
        expected.add(binding);
        
        binding = new HashMap<Variable, Term>();
        binding.put(leFactory.createVariable("X"),  wsmoFactory.createIRI(ns2 + "JohnMedeski"));
        binding.put(leFactory.createVariable("Y"),  wsmoFactory.createIRI(ns1 + "JazzMusician"));
        expected.add(binding);
     
        CoreHelper.queryXMemberOfYAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE1 ), getReasoner(), expected );
    }
    
    public void testCyclicalImports2() throws Exception {
        Set<Map<Variable, Term>> expected = new HashSet<Map<Variable, Term>>();
        Map<Variable, Term> binding = new HashMap<Variable, Term>();
        binding.put(leFactory.createVariable("X"),  wsmoFactory.createIRI("http://here.comes.the.whistleman/CyclicalImports2#KarlDenson"));
        expected.add(binding);
        binding.put(leFactory.createVariable("X"),  wsmoFactory.createIRI("http://here.comes.the.whistleman/CyclicalImports2#JohnMedeski"));
        expected.add(binding);
        binding.put(leFactory.createVariable("X"),  wsmoFactory.createIRI(ns1 + "RolandKirk"));
        expected.add(binding);
        binding.put(leFactory.createVariable("X"),  wsmoFactory.createIRI(ns1 + "JohnScofield"));
        expected.add(binding);

        CoreHelper.queryXMemberOfConceptAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE1 ), getReasoner(), ns1 + "JazzMusician", expected );
    }

    private WsmoFactory wsmoFactory;
    private LogicalExpressionFactory leFactory;
    private WSMO4JManager wsmoManager;
}
