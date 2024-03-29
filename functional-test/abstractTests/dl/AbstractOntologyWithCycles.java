/*
 * WSML2Reasoner
 * An extensible framework for reasoning with WSML ontologies.
 * 
 * Copyright (C) 2008 Semantic Technology Institute (STI) Innsbruck, 
 * University of Innsbruck, Technikerstrasse 21a, 6020 Innsbruck, Austria.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 */
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
import abstractTests.DL;

public abstract class AbstractOntologyWithCycles extends TestCase implements DL {

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

        DLReasoner reasoner = getDLReasoner();
       
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
