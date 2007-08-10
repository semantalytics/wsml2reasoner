package variant.dl;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.omwg.ontology.Concept;
import org.omwg.ontology.Ontology;
import org.wsml.reasoner.api.WSMLDLReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.api.WSMLReasonerFactory.BuiltInReasoner;
import org.wsml.reasoner.impl.DefaultWSMLReasonerFactory;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsmo.common.IRI;
import org.wsmo.factory.Factory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.Parser;

import base.BaseReasonerTest;

import junit.framework.TestCase;

public class CycleTests extends TestCase {

	private WsmoFactory wsmoFactory;
	
	private Ontology ontology = null;
	
	private String ns = null;
	
	private WSMLDLReasoner wsmlReasoner = null;
	
	private Parser parser = null;
	
    private Map<String, Object> params = null;
	
	protected void setUp() throws Exception {
		super.setUp();
		WSMO4JManager wsmoManager = new WSMO4JManager();
        wsmoFactory = wsmoManager.getWSMOFactory();
        wsmlReasoner = null;
		parser = Factory.createParser(null);
		params = new HashMap<String, Object>();
	}
	
	/**
     * @see TestCase#tearDown()
     */
    protected void tearDown(){
        wsmoFactory=null;
        wsmlReasoner=null;
        parser=null;
        ontology=null;
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
        wsmlReasoner = DefaultWSMLReasonerFactory.getFactory().createWSMLDLReasoner(params);
        doCycleTests();
        // KAON2
        params.put(DefaultWSMLReasonerFactory.PARAM_BUILT_IN_REASONER, BuiltInReasoner.KAON2);
        wsmlReasoner = DefaultWSMLReasonerFactory.getFactory().createWSMLDLReasoner(params);
        doCycleTests();
    }
	
	public void doCycleTests() throws Exception {
       
        // register ontology at the wsml reasoner
		wsmlReasoner.registerOntology(ontology);
        
        
        // test getSubConcepts
		Set<Concept> set = new HashSet<Concept>();
		set = wsmlReasoner.getSubConcepts((IRI) ontology.getIdentifier(),  
				ontology.findConcept(wsmoFactory.createIRI(ns + "place")));
//		for (Concept concept : set)
//			System.out.println(concept.getIdentifier().toString());
		assertTrue(set.size() == 5);
		   
        // test getDirectSubConcepts
		set = wsmlReasoner.getDirectSubConcepts((IRI) ontology.getIdentifier(),  
				ontology.findConcept(wsmoFactory.createIRI(ns + "place")));
//		for (Concept concept : set)
//			System.out.println(concept.getIdentifier().toString());
		assertTrue(set.size() == 3);
		
		// test getSuperConcepts
		set.clear();
		set = wsmlReasoner.getSuperConcepts((IRI) ontology.getIdentifier(), 
				ontology.findConcept(wsmoFactory.createIRI(ns + "place")));
//		for (Concept concept : set)
//			System.out.println(concept.getIdentifier().toString());
		assertTrue(set.size() == 3);
		
		// test getDirectSuperConcepts
		set.clear();
		set = wsmlReasoner.getDirectSuperConcepts((IRI) ontology.getIdentifier(), 
				ontology.findConcept(wsmoFactory.createIRI(ns + "place")));
//		for (Concept concept : set)
//			System.out.println(concept.getIdentifier().toString());
		assertTrue(set.size() == 1);
        
		// test getDirectConceptsOf
		set.clear();
		set = wsmlReasoner.getDirectConcepts((IRI) ontology.getIdentifier(), 
				ontology.findInstance(wsmoFactory.createIRI(ns + "springfield_elementary")));
//		for (Concept concept : set)
//			System.out.println(concept.getIdentifier().toString());
		assertTrue(set.size() == 1);
	}
	
}
