package reasoner.dl;
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


import java.io.InputStream;
import java.io.InputStreamReader;

import org.omwg.ontology.Ontology;
import org.wsml.reasoner.api.WSMLDLReasoner;
import org.wsml.reasoner.impl.DefaultWSMLReasonerFactory;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsmo.common.IRI;
import org.wsmo.factory.DataFactory;
import org.wsmo.factory.Factory;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.Parser;

import junit.framework.TestCase;

public class OntologyRegistrationTest extends TestCase {

	private WsmoFactory wsmoFactory;
	
	private LogicalExpressionFactory leFactory;
	
	private DataFactory dataFactory;

    private WSMLDLReasoner wsmlReasoner;

    private Parser parser; 
	
	protected void setUp() throws Exception {
		super.setUp();
		WSMO4JManager wsmoManager = new WSMO4JManager();
        wsmoFactory = wsmoManager.getWSMOFactory();
        leFactory = wsmoManager.getLogicalExpressionFactory();
        dataFactory = wsmoManager.getDataFactory();
        wsmlReasoner = DefaultWSMLReasonerFactory.getFactory().createWSMLDLReasoner();
		parser = Factory.createParser(null);
	}

	public void testMultipleOntologies() throws Exception {
		WSMLDLReasoner wsmlReasoner = 
			DefaultWSMLReasonerFactory.getFactory().createWSMLDLReasoner();
		
		/*----------------------- register different ontologies --------------------*/
		
		// read test file and parse it 
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(
                "reasoner/dl/wsml2owlExample.wsml");
        assertNotNull(is);
        // assuming first topentity in file is an ontology  
        Ontology ontology = (Ontology)parser.parse(new InputStreamReader(is))[0]; 
		
        // register ontology at the wsml reasoner
        wsmlReasoner.registerOntology(ontology); 
        
        // read test file and parse it 
        is = this.getClass().getClassLoader().getResourceAsStream(
                "wsml2reasoner/normalization/decomposition.wsml");
        assertNotNull(is);
        // assuming first topentity in file is an ontology  
        Ontology ontology2 = (Ontology)parser.parse(new InputStreamReader(is))[0]; 
		
        // register ontology at the wsml reasoner
        wsmlReasoner.registerOntology(ontology2); 
        
        // read test file and parse it 
        is = this.getClass().getClassLoader().getResourceAsStream(
                "reasoner/dl/wsml2owlShortExample.wsml");
        assertNotNull(is);
        // assuming first topentity in file is an ontology  
        Ontology ontology3 = (Ontology)parser.parse(new InputStreamReader(is))[0]; 
        
        // register ontology at the wsml reasoner
        wsmlReasoner.registerOntology(ontology3);
        
        /*----------------------- reason over different ontologies --------------------*/
        
        System.out.println(wsmlReasoner.getInfo((IRI) ontology.getIdentifier()));
        wsmlReasoner.printClassTree((IRI) ontology.getIdentifier());
        System.out.println("----------------------------------------------------");
        System.out.println(wsmlReasoner.getInfo((IRI) ontology2.getIdentifier()));
        wsmlReasoner.printClassTree((IRI) ontology2.getIdentifier());
        System.out.println("----------------------------------------------------");
        System.out.println(wsmlReasoner.getInfo((IRI) ontology3.getIdentifier()));
        wsmlReasoner.printClassTree((IRI) ontology3.getIdentifier());
        
        wsmlReasoner.deRegisterOntology((IRI) ontology.getIdentifier());
        try {
        	System.out.println(wsmlReasoner.getInfo((IRI) ontology.getIdentifier()));
        	fail("Should fail because ontology is not registrated any more");
        } catch (RuntimeException e) {
        	System.out.println(e.getMessage());
        }
	}
	
}
