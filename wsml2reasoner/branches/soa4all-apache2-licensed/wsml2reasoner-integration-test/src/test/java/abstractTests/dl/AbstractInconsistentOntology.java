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

import junit.framework.TestCase;

import org.omwg.ontology.Ontology;
import org.wsml.reasoner.api.inconsistency.InconsistencyException;
import org.wsmo.factory.FactoryContainer;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.Parser;

import abstractTests.DL;

import com.ontotext.wsmo4j.parser.wsml.WsmlParser;

/*
 * needs:  inconsistentWsml2owlExample.wsml
 */
public abstract class AbstractInconsistentOntology extends TestCase implements DL {
	
	protected FactoryContainer factory;
	protected WsmoFactory wsmoFactory;
	protected Ontology ontology;
	protected Parser parser;
	protected String ns;
	
	protected void setUp() throws Exception {
     	super.setUp();
		wsmoFactory = factory.getWsmoFactory();
        parser = new WsmlParser();
        
        // inconsistentWsml2owlExample.wsml
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("inconsistentWsml2owlExample.wsml");
        assertNotNull(is);
        
        ontology = (Ontology)parser.parse(new InputStreamReader(is))[0]; 
        ns = ontology.getDefaultNamespace().getIRI().toString();
	}
	
	public void testInconsistenWSML2OWL() {
		try {
			// register ontology at the wsml reasoner
			(this.getDLReasoner()).registerOntology(ontology);
			fail("Should fail because the given ontology is inconsistent");
		} catch (InconsistencyException e) {
			e.getMessage();		
		}
		
	}	
}
