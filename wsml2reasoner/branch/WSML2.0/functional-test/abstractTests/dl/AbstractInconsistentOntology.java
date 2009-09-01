/**
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

import junit.framework.TestCase;

import org.omwg.ontology.Ontology;
import org.wsml.reasoner.api.inconsistency.InconsistencyException;
import org.wsmo.factory.FactoryContainer;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.Parser;

import abstractTests.DL;

import com.ontotext.wsmo4j.parser.wsml.WsmlParser;

/*
 * needs:  files/inconsistentWsml2owlExample.wsml
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
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("files/inconsistentWsml2owlExample.wsml");
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
