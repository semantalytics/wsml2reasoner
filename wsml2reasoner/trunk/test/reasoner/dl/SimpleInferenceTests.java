package reasoner.dl;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;

import org.mindswap.pellet.exceptions.InconsistentOntologyException;
import org.omwg.ontology.Concept;
import org.omwg.ontology.Instance;
import org.omwg.ontology.Ontology;
import org.wsml.reasoner.api.WSMLDLReasoner;
import org.wsml.reasoner.impl.DefaultWSMLReasonerFactory;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsmo.common.IRI;
import org.wsmo.factory.Factory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.Parser;

import junit.framework.TestCase;

public class SimpleInferenceTests extends TestCase {

	private WsmoFactory wsmoFactory;

    private WSMLDLReasoner wsmlReasoner;

    private Parser parser; 
    
    private Ontology ontology;
    
    private String ns;
    
	protected void setUp() throws Exception {
		super.setUp();
		WSMO4JManager wsmoManager = new WSMO4JManager();
        wsmoFactory = wsmoManager.getWSMOFactory();
        wsmlReasoner = DefaultWSMLReasonerFactory.getFactory().createWSMLDLReasoner();
		parser = Factory.createParser(null);
		
		// read test file and parse it 
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(
                "reasoner/dl/wsml2owlExample.wsml");
        assertNotNull(is);
        // assuming first topentity in file is an ontology  
        ontology = (Ontology)parser.parse(new InputStreamReader(is))[0]; 
        ns = ontology.getDefaultNamespace().getIRI().toString();
		
        // register ontology at the wsml reasoner
        wsmlReasoner.registerOntology(ontology);       
	}
	
	public void testSatisfiability() throws Exception {
		assertTrue(wsmlReasoner.isSatisfiable(null));
	}
	
	public void testConceptSatisfiability1() throws Exception {
		assertFalse(wsmlReasoner.isConsistent(
				wsmoFactory.createConcept(wsmoFactory.createIRI(ns + "Machine"))));
	}
	
	public void testConceptSatisfiability2() throws Exception {
		assertTrue(wsmlReasoner.isConsistent(
				wsmoFactory.createConcept(wsmoFactory.createIRI(ns + "Woman"))));
	}
	
	public void testGetAllConcepts() throws Exception {
		Set<Concept> set = wsmlReasoner.getAllConcepts();
		for (Concept concept : set) 
//			System.out.println(concept.getIdentifier().toString());
		assertTrue(set.size() == 17);
	}
	
	public void testGetAllInstances() throws Exception {
		Set<Instance> set = wsmlReasoner.getAllInstances();
		for (Instance instance : set) 
//			System.out.println(instance.getIdentifier().toString());
		assertTrue(set.size() == 9);
	}
	
	public void testGetAllAttributes() throws Exception {
		Set<IRI> set = wsmlReasoner.getAllAttributes();
		for (IRI attributeId : set) 
			System.out.println(attributeId.toString());
		assertTrue(set.size() == 18);
	}
	
	public void testGetSubConceptsOf() throws Exception {
		Set<Concept> set = wsmlReasoner.getSubConcepts(null, 
				ontology.findConcept(wsmoFactory.createIRI(ns + "Human")));
		for (Concept concept : set)
			System.out.println(concept.getIdentifier().toString());
		assertTrue(set.size() == 6);
	}
	
	public void testGetSuperConceptsOf() throws Exception {
		Set<Concept> set = wsmlReasoner.getSuperConcepts(null,
				ontology.findConcept(wsmoFactory.createIRI(ns + "Human")));
		for (Concept concept : set)
//			System.out.println(concept.getIdentifier().toString());
		assertTrue(set.size() == 2);
	}
	
	public void testEquivalentConcepts() throws Exception {
		Set<Concept> set = wsmlReasoner.getEquivalentConcepts(
				ontology.findConcept(wsmoFactory.createIRI(ns + "Human")));
		for (Concept concept : set)
			System.out.println(concept.getIdentifier().toString());
		assertTrue(set.size() == 1);
	}
	
	public void testIsEquivalentConcept() throws Exception {
		assertTrue(wsmlReasoner.isEquivalentConcept(
				ontology.findConcept(wsmoFactory.createIRI(ns + "Human")),
				wsmoFactory.createConcept(wsmoFactory.createIRI(ns + "Person"))));
	}
	
	public void testIsNotEquivalentConcept() throws Exception {
		assertFalse(wsmlReasoner.isEquivalentConcept(
				ontology.findConcept(wsmoFactory.createIRI(ns + "Human")),
				ontology.findConcept(wsmoFactory.createIRI(ns + "Animal"))));
	}
	
	public void testIsSubConceptOf() throws Exception {
		assertTrue(wsmlReasoner.isSubConceptOf(
				null, 
				ontology.findConcept(wsmoFactory.createIRI(ns + "Woman")), 
				ontology.findConcept(wsmoFactory.createIRI(ns + "Human"))));
	}
	
	public void testIsNotSubConceptOf() throws Exception {
		assertFalse(wsmlReasoner.isSubConceptOf(
				null, 
				ontology.findConcept(wsmoFactory.createIRI(ns + "Animal")), 
				ontology.findConcept(wsmoFactory.createIRI(ns + "Human"))));
	}
	
	public void testIsMemberOf() throws Exception {
		assertTrue(wsmlReasoner.isMemberOf(
				null, 
				ontology.findInstance(wsmoFactory.createIRI(ns + "Mary")), 
				ontology.findConcept(wsmoFactory.createIRI(ns + "Woman"))));
	}
	
	public void testIsNotMemberOf() throws Exception {
		assertFalse(wsmlReasoner.isMemberOf(
				null, 
				ontology.findInstance(wsmoFactory.createIRI(ns + "Jim")), 
				ontology.findConcept(wsmoFactory.createIRI(ns + "Woman"))));
	}
	
	public void testAllConceptsOf() throws Exception {
		Set<Concept> concepts = wsmlReasoner.getConcepts(
				null,
				ontology.findInstance(wsmoFactory.createIRI(ns + "Mary")));
		for(Concept concept : concepts)
//			System.out.println(concept.getIdentifier().toString());
		assertTrue(concepts.size() == 5);
	}
	
	public void testAllInstancesOf() throws Exception {
		Set<Instance> instances = wsmlReasoner.getInstances(
				null,
				ontology.findConcept(wsmoFactory.createIRI(ns + "Woman")));
		for(Instance instance : instances)
//			System.out.println(instance.getIdentifier().toString());
		assertTrue(instances.size() == 4);
	}
	
	public void testGetSubRelationOf() throws Exception {
		Set<IRI> set = wsmlReasoner.getSubRelations( 
				wsmoFactory.createIRI(ns + "hasParent"));
		for (IRI attributeId : set)
//			System.out.println(attributeId.toString());
		assertTrue(set.size() == 1);
	}
	
	public void testGetSuperRelationOf() throws Exception {
		Set<IRI> set = wsmlReasoner.getSuperRelations(
				wsmoFactory.createIRI(ns + "hasMother"));
		for (IRI attributeId : set)
//			System.out.println(attributeId.toString());
		assertTrue(set.size() == 1);
	}
	
	public void testEquivalentAttributes() throws Exception {
		Set<IRI> set = wsmlReasoner.getEquivalentAttributes(
				wsmoFactory.createIRI(ns + "hasHolder"));
		for (IRI attributeId : set)
//			System.out.println(attributeId.toString());
		assertTrue(set.size() == 1);
	}
	
	public void testInconsistentOntology() throws Exception {
		wsmlReasoner.deRegisterOntology((IRI) ontology.getIdentifier());
		// read test file and parse it 
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(
                "reasoner/dl/inconsistentWsml2owlExample.wsml");
        assertNotNull(is);
        // assuming first topentity in file is an ontology  
        ontology = (Ontology)parser.parse(new InputStreamReader(is))[0]; 
		try {
			// register ontology at the wsml reasoner
			wsmlReasoner.registerOntology(ontology);
			fail("Should fail because the given ontology is inconsistent");
		} catch (InconsistentOntologyException e) {
			e.getMessage();		
		}
	}
	
}
