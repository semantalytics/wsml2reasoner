/*
 * Copyright 2011, Semantic Technology Institute (STI) Innsbruck, 
 * University of Innsbruck, Technikerstrasse 21a, 6020 Innsbruck, Austria.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package abstractTests.dl;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.omwg.ontology.Concept;
import org.omwg.ontology.Ontology;
import org.sti2.wsmo4j.factory.WsmlFactoryContainer;
import org.wsml.reasoner.api.DLReasoner;
import org.wsmo.factory.FactoryContainer;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.Parser;

import abstractTests.DL;

import com.ontotext.wsmo4j.parser.wsml.WsmlParser;

public abstract class AbstractOntologyWithCycles extends TestCase implements DL {

    /**
     * The ontology contains a cycle in the concept description.
     * Test that is it loaded ok.
     */
    public void testForCyclicalDefinitions() throws Exception {
    	// read test file and parse it 
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(
                "cycleTestFile.wsml");
        assertNotNull(is);

        // assuming first topentity in file is an ontology  
		
        Parser parser = new WsmlParser();

    	Ontology ontology = (Ontology)parser.parse(new InputStreamReader(is))[0]; 
        final String ns = ontology.getDefaultNamespace().getIRI().toString();

        DLReasoner reasoner = getDLReasoner();
       
        // register ontology at the wsml reasoner
		reasoner.registerOntology(ontology);
        
		FactoryContainer factory = new WsmlFactoryContainer();
        WsmoFactory wsmoFactory = factory.getWsmoFactory();
        
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
