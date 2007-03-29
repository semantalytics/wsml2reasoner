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
package reasoner.flight;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.omwg.ontology.Ontology;
import org.wsml.reasoner.api.WSMLReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsmo.common.IRI;
import org.wsmo.factory.Factory;
import org.wsmo.wsml.Parser;

import test.BaseReasonerTest;

import junit.framework.TestCase;


public class CarlosBugTest extends TestCase {

   private WSMLReasoner wsmlReasoner;

   private Parser parser;
   
   private Ontology ontology;
   
	protected void setUp() throws Exception {
		super.setUp();
		
		// get A reasoner
       Map<String, Object> params = new HashMap<String, Object>();
       params.put(WSMLReasonerFactory.PARAM_BUILT_IN_REASONER,
               WSMLReasonerFactory.BuiltInReasoner.MINS);
		wsmlReasoner = BaseReasonerTest.getReasoner();
		parser = Factory.createParser(null);
	}
	
	public void testRun() throws Exception {
		// read test file and parse it 
       InputStream is = this.getClass().getClassLoader().getResourceAsStream(
               "reasoner/flight/Carlos.wsml");
       assertNotNull(is);
       // assuming first topentity in file is an ontology  
       ontology = (Ontology)parser.parse(new InputStreamReader(is))[0]; 

       // register ontology at the wsml reasoner
       wsmlReasoner.registerOntology(ontology);       
       
		// test ontology satisfiability
		assertTrue(wsmlReasoner.isSatisfiable((IRI) ontology.getIdentifier()));
		
		wsmlReasoner.deRegisterOntology((IRI) ontology.getIdentifier());
	}
	
}
/*
 * $Log: not supported by cvs2svn $
 *
 */
