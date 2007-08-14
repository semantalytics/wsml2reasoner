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
package framework.registration;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;

import org.omwg.ontology.Instance;
import org.omwg.ontology.Ontology;
import org.wsml.reasoner.api.WSMLReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.api.WSMLReasonerFactory.BuiltInReasoner;
import org.wsmo.common.IRI;
import org.wsmo.factory.Factory;
import org.wsmo.wsml.Parser;

import base.BaseReasonerTest;

/**
 * This test shows the use of the DefaultWSMLReasonerFactory method 
 * createReasoner(). This method gets an ontology as parameter, checks 
 * the variant of the ontology and chooses the corresponding reasoner, 
 * either datalog or dl based.
 * 
 * @author nathalie.steinmetz@deri.org
 */
public class ReasonerCreationTest extends BaseReasonerTest {

	private WSMLReasoner wsmlReasoner;
	
	private BuiltInReasoner previous;

    private Parser parser; 
	
	protected void setUp() throws Exception {
		super.setUp();
		previous = BaseReasonerTest.reasoner;
		parser = Factory.createParser(null);
	}
	
	 @Override
	protected void tearDown() throws Exception {
		// TODO Auto-generated method stub
		super.tearDown();
		BaseReasonerTest.reasoner = previous;
	}
	
	public void dlReasonerCreation() throws Exception {
		
		/*----------------------- register wsml dl ontology --------------------*/
		
		// read test file and parse it 
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(
                "files/wsml2owlExample.wsml");
        assertNotNull(is);
        // assuming first topentity in file is an ontology  
        Ontology ontology = (Ontology)parser.parse(new InputStreamReader(is))[0]; 
		
        // create wsml reasoner and register ontology
        wsmlReasoner.registerOntology(ontology); 
        
        System.out.println("\n--------------------------------------------------------------------");
        System.out.println("DL Reasoning: Registered - Instances ");
        System.out.println("--------------------------------------------------------------------");
        Set<Instance> set = wsmlReasoner.getAllInstances((IRI) ontology.getIdentifier());
        for (Instance instance : set) {
        	System.out.println(instance.getIdentifier().toString());
        }
        
        wsmlReasoner.deRegisterOntology((IRI) ontology.getIdentifier());
        
	}
	
	public void datalogReasonerCreation() throws Exception {
		
		/*----------------------- register wsml dl ontology --------------------*/
		
		// read test file and parse it 
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(
                "files/simpsons.wsml");
        assertNotNull(is);
        // assuming first topentity in file is an ontology  
        Ontology ontology = (Ontology)parser.parse(new InputStreamReader(is))[0]; 

        // create wsml reasoner and register ontology
        //wsmlReasoner = DefaultWSMLReasonerFactory.getFactory().createWSMLReasoner(ontology);
        wsmlReasoner.registerOntology(ontology);       
        
        System.out.println("\n--------------------------------------------------------------------");
        System.out.println("Datalog Reasoning: Registered - Instances");
        System.out.println("--------------------------------------------------------------------");
        Set<Instance> set = wsmlReasoner.getAllInstances((IRI) ontology.getIdentifier());
        for (Instance instance : set) {
        	System.out.println(instance.getIdentifier().toString());
        }
        
        wsmlReasoner.deRegisterOntology((IRI) ontology.getIdentifier());
	}
	
    public void testReasonerCreation() throws Exception{
    	BaseReasonerTest.reasoner = WSMLReasonerFactory.BuiltInReasoner.IRIS;
    	wsmlReasoner = BaseReasonerTest.getReasoner();
    	datalogReasonerCreation();
    	
    	BaseReasonerTest.reasoner = WSMLReasonerFactory.BuiltInReasoner.MINS;
    	wsmlReasoner = BaseReasonerTest.getReasoner();
    	datalogReasonerCreation();
    	
    	BaseReasonerTest.reasoner = WSMLReasonerFactory.BuiltInReasoner.KAON2;
    	wsmlReasoner = BaseReasonerTest.getReasoner();
    	datalogReasonerCreation();
//    	dlReasonerCreation();
    	
    	BaseReasonerTest.reasoner = WSMLReasonerFactory.BuiltInReasoner.PELLET;
    	wsmlReasoner = BaseReasonerTest.getReasoner();
    	dlReasonerCreation();
    }
	
}
