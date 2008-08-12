package abstractTests.dl;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import junit.framework.TestCase;
import org.omwg.ontology.Concept;
import org.omwg.ontology.Ontology;
import org.wsml.reasoner.api.DLReasoner;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsmo.factory.Factory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.Parser;
import abstractTests.DLTest;

public abstract class AbstractOntologyWithCycles extends TestCase implements DLTest {

    /**
     * The ontology contains a cycle in the concept description.
     * Test that is it loaded ok.
     */
    public void testForCyclicalDefinitions() throws Exception {
    	// read test file and parse it 
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(
                "files/cycleTestFile.wsml");
        assertNotNull(is);

        // assuming first topentity in file is an ontology  
		
        Parser parser = Factory.createParser(null);

    	Ontology ontology = (Ontology)parser.parse(new InputStreamReader(is))[0]; 
        final String ns = ontology.getDefaultNamespace().getIRI().toString();

        DLReasoner reasoner = getReasoner();
       
        // register ontology at the wsml reasoner
		reasoner.registerOntology(ontology);
        
		WSMO4JManager wsmoManager = new WSMO4JManager();
        WsmoFactory wsmoFactory = wsmoManager.getWSMOFactory();
        
        // test getSubConcepts
		Set<Concept> set = new HashSet<Concept>();
		set = reasoner.getSubConcepts(ontology.findConcept(wsmoFactory.createIRI(ns + "place")));
//		for (Concept concept : set)
//			System.out.println(concept.getIdentifier().toString());
		assertEquals( 5, set.size() );
		   
        // test getDirectSubConcepts
		set = reasoner.getDirectSubConcepts(ontology.findConcept(wsmoFactory.createIRI(ns + "place")));
//		for (Concept concept : set)
//			System.out.println(concept.getIdentifier().toString());
		assertEquals( 3, set.size() );
		
		// test getSuperConcepts
		set.clear();
		set = reasoner.getSuperConcepts(ontology.findConcept(wsmoFactory.createIRI(ns + "place")));
//		for (Concept concept : set)
//			System.out.println(concept.getIdentifier().toString());
		assertEquals( 3, set.size() );
		
		// test getDirectSuperConcepts
		set.clear();
		set = reasoner.getDirectSuperConcepts(ontology.findConcept(wsmoFactory.createIRI(ns + "place")));
//		for (Concept concept : set)
//			System.out.println(concept.getIdentifier().toString());
		assertEquals( 1, set.size() );
        
		// test getDirectConceptsOf
		set.clear();
		set = reasoner.getDirectConcepts(ontology.findInstance(wsmoFactory.createIRI(ns + "springfield_elementary")));
//		for (Concept concept : set)
//			System.out.println(concept.getIdentifier().toString());
		assertEquals( 1, set.size() );
	}
}
