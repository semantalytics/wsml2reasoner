package variant.dl;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.omwg.ontology.Concept;
import org.omwg.ontology.Ontology;
import org.wsml.reasoner.api.DLReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory.BuiltInReasoner;
import org.wsml.reasoner.api.inconsistency.InconsistencyException;
import org.wsml.reasoner.impl.DefaultWSMLReasonerFactory;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsmo.factory.Factory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.Parser;

import base.BaseReasonerTest;

public class CycleTests extends TestCase {

	private WsmoFactory wsmoFactory;
	
	private Ontology ontology = null;
	
	private String ns = null;
	
	private DLReasoner wsmlReasoner = null;
	
	private BuiltInReasoner previous;
	
	private Parser parser = null;
	
    private Map<String, Object> params = null;
	
	protected void setUp() throws Exception {
		super.setUp();
		WSMO4JManager wsmoManager = new WSMO4JManager();
        wsmoFactory = wsmoManager.getWSMOFactory();
        previous = BaseReasonerTest.reasoner;
        wsmlReasoner = null;
		parser = Factory.createParser(null);
		params = new HashMap<String, Object>();
	}
	
	/**
     * @throws InconsistencyException 
	 * @see TestCase#tearDown()
     */
    protected void tearDown() throws InconsistencyException{
        wsmoFactory=null;
        wsmlReasoner=null;
        parser=null;
        ontology=null;
        BaseReasonerTest.resetReasoner(previous);
        System.gc();
    }
	
    public void testRun() throws Exception {
    	
    	/*
    	 * Do cycle tests with Pellet and KAON2
    	 * 
    	 * The ontology contains a cycle in the concept description.
    	 */
    	
    	// read test file and parse it 
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(
                "files/cycleTestFile.wsml");
        assertNotNull(is);
        // assuming first topentity in file is an ontology  
        ontology = (Ontology)parser.parse(new InputStreamReader(is))[0]; 
        ns = ontology.getDefaultNamespace().getIRI().toString();
        // Pellet
        params.put(DefaultWSMLReasonerFactory.PARAM_BUILT_IN_REASONER, BuiltInReasoner.PELLET);
        wsmlReasoner = DefaultWSMLReasonerFactory.getFactory().createDLReasoner(params);
        doCycleTests();
        
        // KAON2
        if (base.BaseReasonerTest.exists("org.wsml.reasoner.builtin.kaon2.Kaon2DLFacade")) { 
	        params.put(DefaultWSMLReasonerFactory.PARAM_BUILT_IN_REASONER, BuiltInReasoner.KAON2);
	        wsmlReasoner = DefaultWSMLReasonerFactory.getFactory().createDLReasoner(params);
	        doCycleTests();
        }
    }
	
	public void doCycleTests() throws Exception {
       
        // register ontology at the wsml reasoner
		wsmlReasoner.registerOntology(ontology);
        
        
        // test getSubConcepts
		Set<Concept> set = new HashSet<Concept>();
		set = wsmlReasoner.getSubConcepts(ontology.findConcept(wsmoFactory.createIRI(ns + "place")));
//		for (Concept concept : set)
//			System.out.println(concept.getIdentifier().toString());
		assertTrue(set.size() == 5);
		   
        // test getDirectSubConcepts
		set = wsmlReasoner.getDirectSubConcepts(ontology.findConcept(wsmoFactory.createIRI(ns + "place")));
//		for (Concept concept : set)
//			System.out.println(concept.getIdentifier().toString());
		assertTrue(set.size() == 3);
		
		// test getSuperConcepts
		set.clear();
		set = wsmlReasoner.getSuperConcepts(ontology.findConcept(wsmoFactory.createIRI(ns + "place")));
//		for (Concept concept : set)
//			System.out.println(concept.getIdentifier().toString());
		assertTrue(set.size() == 3);
		
		// test getDirectSuperConcepts
		set.clear();
		set = wsmlReasoner.getDirectSuperConcepts(ontology.findConcept(wsmoFactory.createIRI(ns + "place")));
//		for (Concept concept : set)
//			System.out.println(concept.getIdentifier().toString());
		assertTrue(set.size() == 1);
        
		// test getDirectConceptsOf
		set.clear();
		set = wsmlReasoner.getDirectConcepts(ontology.findInstance(wsmoFactory.createIRI(ns + "springfield_elementary")));
//		for (Concept concept : set)
//			System.out.println(concept.getIdentifier().toString());
		assertTrue(set.size() == 1);
	}
	
}
