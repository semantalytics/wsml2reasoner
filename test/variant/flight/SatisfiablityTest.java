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
package variant.flight;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.omwg.ontology.Ontology;
import org.wsml.reasoner.api.WSMLReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsmo.common.IRI;
import org.wsmo.factory.Factory;
import org.wsmo.wsml.Parser;

import base.BaseReasonerTest;


public class SatisfiablityTest extends BaseReasonerTest {

   private WSMLReasoner wsmlReasoner;

   private Parser parser;
   
   private Ontology ontology;
   
	protected void setUp() throws Exception {
		super.setUp();
		
		// get A reasoner
		wsmlReasoner = BaseReasonerTest.getReasoner();
		parser = Factory.createParser(null);
	}
	
	public void satisfiablity() throws Exception {
		// read test file and parse it 
       InputStream is = this.getClass().getClassLoader().getResourceAsStream(
               "files/isSatisfiable.wsml");
       assertNotNull(is);
       // assuming first topentity in file is an ontology  
       ontology = (Ontology)parser.parse(new InputStreamReader(is))[0]; 

       // register ontology at the wsml reasoner
       wsmlReasoner.registerOntology(ontology);       
       
		// test ontology satisfiability
		assertTrue(wsmlReasoner.isSatisfiable((IRI) ontology.getIdentifier()));
		
		wsmlReasoner.deRegisterOntology((IRI) ontology.getIdentifier());
	}
	
    
    public void testFlightReasoners() throws Exception{
    	BaseReasonerTest.reasoner = WSMLReasonerFactory.BuiltInReasoner.IRIS;
    	wsmlReasoner = BaseReasonerTest.getReasoner();
    	satisfiablity();
    	
    	BaseReasonerTest.reasoner = WSMLReasonerFactory.BuiltInReasoner.MINS;
    	wsmlReasoner = BaseReasonerTest.getReasoner();
    	satisfiablity();
    	
    	BaseReasonerTest.reasoner = WSMLReasonerFactory.BuiltInReasoner.KAON2;
    	wsmlReasoner = BaseReasonerTest.getReasoner();
    	satisfiablity();
    }
	
}
/*
 * $Log: not supported by cvs2svn $
 * Revision 1.1  2007-08-08 10:57:58  graham
 * Second stage of refactoring unit tests
 *
 * Revision 1.2  2007-06-19 09:56:24  graham
 * intial steps to refactoring test suites
 *
 * Revision 1.1  2007/03/29 21:21:41  nathalie
 * fixed bug caused by maxCardinality of 0
 *
 *
 */
