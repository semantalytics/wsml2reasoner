/**
 * WSML Reasoner Implementation.
 *
 * Copyright (c) 2005, FZI, Germany.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * You should have received a copy of the GNU Lesser General Public License along
 * with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 */
package variant.dl;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Set;

import junit.framework.TestCase;

import org.omwg.ontology.Concept;
import org.omwg.ontology.Ontology;
import org.wsml.reasoner.api.WSMLReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory.BuiltInReasoner;
import org.wsml.reasoner.impl.DefaultWSMLReasonerFactory;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsmo.common.IRI;
import org.wsmo.factory.Factory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.Parser;


public class DLOntologyRegistrationTest extends TestCase {

	private WsmoFactory wsmoFactory = null;
	
    private WSMLReasoner wsmlReasoner = null;

    private Parser parser = null; 
    
    private HashMap<String, Object> params = null;
	
	protected void setUp() throws Exception {
		super.setUp();
		WSMO4JManager wsmoManager = new WSMO4JManager();
        wsmoFactory = wsmoManager.getWSMOFactory();
        wsmlReasoner = DefaultWSMLReasonerFactory.getFactory().createWSMLDLReasoner();
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
        System.gc();
    }
	
	public void testRun() throws Exception {
		/*
		 * Register multiple ontologies at Pellet and KAON2 and do simple 
		 * inference tests
    	 */
        // Pellet
        params.put(DefaultWSMLReasonerFactory.PARAM_BUILT_IN_REASONER, BuiltInReasoner.PELLET);
        wsmlReasoner = DefaultWSMLReasonerFactory.getFactory().createWSMLDLReasoner(params);
        doTestMultipleOntologies();
        System.gc();
        // KAON2
        params.put(DefaultWSMLReasonerFactory.PARAM_BUILT_IN_REASONER, BuiltInReasoner.KAON2);
        wsmlReasoner = DefaultWSMLReasonerFactory.getFactory().createWSMLDLReasoner(params);
        doTestMultipleOntologies();
	}

	public void doTestMultipleOntologies() throws Exception {
		
		/*----------------------- register different ontologies --------------------*/
		
		// read test file and parse it 
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(
                "files/wsml2owlExample.wsml");
        assertNotNull(is);
        // assuming first topentity in file is an ontology  
        Ontology ontology = (Ontology)parser.parse(new InputStreamReader(is))[0]; 
		
        // register ontology at the wsml reasoner
        wsmlReasoner.registerOntology(ontology); 
        
        // read test file and parse it 
        is = this.getClass().getClassLoader().getResourceAsStream(
                "files/decomposition.wsml");
        assertNotNull(is);
        // assuming first topentity in file is an ontology  
        Ontology ontology2 = (Ontology)parser.parse(new InputStreamReader(is))[0]; 
		
        // register ontology at the wsml reasoner
        wsmlReasoner.registerOntology(ontology2); 
        
        // read test file and parse it 
        is = this.getClass().getClassLoader().getResourceAsStream(
                "files/cycleTestFile.wsml");
        assertNotNull(is);
        // assuming first topentity in file is an ontology  
        Ontology ontology3 = (Ontology)parser.parse(new InputStreamReader(is))[0]; 
        
        // register ontology at the wsml reasoner
        wsmlReasoner.registerOntology(ontology3);
        
        /*----------------------- reason over different ontologies --------------------*/
        
        // Ontology 1
//        System.out.println(ontology.getIdentifier().toString());
        Set<Concept> set = null; 
        set = wsmlReasoner.getAllConcepts((IRI) ontology.getIdentifier());
        set.remove(wsmoFactory.createConcept(wsmoFactory.createIRI(
				"http://www.w3.org/2002/07/owl#Thing")));
        set.remove(wsmoFactory.createConcept(wsmoFactory.createIRI(
				"http://www.w3.org/2002/07/owl#Nothing")));
        for (Concept c : set) 
        	System.out.println(c.getIdentifier().toString());
        assertTrue (set.size() == 18);
                
        // Ontology 2
//        System.out.println(ontology2.getIdentifier().toString());
        set.clear();
        set = wsmlReasoner.getAllConcepts(
        		(IRI) ontology2.getIdentifier());
        set.remove(wsmoFactory.createConcept(wsmoFactory.createIRI(
				"http://www.w3.org/2002/07/owl#Thing")));
        set.remove(wsmoFactory.createConcept(wsmoFactory.createIRI(
				"http://www.w3.org/2002/07/owl#Nothing")));
        assertTrue (set.size() == 5);
        
        // Ontology 3
//        System.out.println(ontology3.getIdentifier().toString());
        set.clear();
        set = wsmlReasoner.getAllConcepts(
        		(IRI) ontology3.getIdentifier());
        set.remove(wsmoFactory.createConcept(wsmoFactory.createIRI(
				"http://www.w3.org/2002/07/owl#Thing")));
        set.remove(wsmoFactory.createConcept(wsmoFactory.createIRI(
				"http://www.w3.org/2002/07/owl#Nothing")));
        assertTrue (set.size() == 9);
        
        wsmlReasoner.deRegisterOntology((IRI) ontology.getIdentifier());
        try {
        	assertTrue(wsmlReasoner.getAllConcepts(
        			(IRI) ontology.getIdentifier()).size() == 18);
        	fail("Should fail because ontology is not registrated any more");
        } catch (RuntimeException e) {
        	System.out.println(e.getMessage());
        }
	}
	
}
