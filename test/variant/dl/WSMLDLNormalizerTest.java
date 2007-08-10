/**
 * WSML Reasoner Implementation.
 *
 * Copyright (c) 2005, FZI, Austria.
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.deri.wsmo4j.validator.WsmlDLValidator;
import org.omwg.ontology.Ontology;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsml.reasoner.transformation.AxiomatizationNormalizer;
import org.wsml.reasoner.transformation.dl.Relation2AttributeNormalizer;
import org.wsml.reasoner.transformation.dl.WSMLDLLogExprNormalizer;
import org.wsmo.factory.Factory;
import org.wsmo.wsml.Parser;

import framework.normalization.BaseNormalizationTest;

public class WSMLDLNormalizerTest extends BaseDLReasonerTest {

	protected Relation2AttributeNormalizer relTransformer;
	
	protected WSMLDLLogExprNormalizer logExprTransformer;
	
	protected AxiomatizationNormalizer axiomTransformer;
	
	protected Ontology ontology;
	
	protected void setUp() throws Exception {
        super.setUp();
        //in order to keep track of cyclic imports
        Set<Ontology> importedOntologies = new HashSet<Ontology>();
        relTransformer = new Relation2AttributeNormalizer(new WSMO4JManager());
        axiomTransformer = new AxiomatizationNormalizer(new WSMO4JManager(), importedOntologies);
        logExprTransformer = new WSMLDLLogExprNormalizer(new WSMO4JManager());
	}

    protected void tearDown() throws Exception {
        super.tearDown();
        ontology = null;
        System.gc();
    }
    
    
	public void testPreProcessingSteps() throws Exception {
    	// read test file and parse it 
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(
                "files/wsml2owlNormExample.wsml");
        assertNotNull(is);
        Parser wsmlParser = Factory.createParser(null);
        // assuming first topentity in file is an ontology  
        ontology = (Ontology)wsmlParser.parse(new InputStreamReader(is))[0];  
        
System.out.println(serializeOntology(ontology)+"\n\n\n-------------\n\n\n");

		// validate the test ontology
		WsmlDLValidator validator = new WsmlDLValidator(leFactory);
		List errors = new Vector();
		boolean b = validator.isValid(ontology, errors, new Vector());
        for (int i=0; i<errors.size(); i++) 
        	System.out.println(errors.get(i));
        assertTrue(b);
		
        // normalize ontology with the WSMLDLNormalizer:
		Ontology normOnt = createOntology();
		normOnt = relTransformer.normalize(ontology);
System.out.println(serializeOntology(normOnt)+"\n\n\n-------------\n\n\n"); 

		normOnt = axiomTransformer.normalize(normOnt);	
System.out.println(serializeOntology(normOnt)+"\n\n\n-------------\n\n\n"); 

		normOnt = logExprTransformer.normalize(normOnt);	
System.out.println(serializeOntology(normOnt)+"\n\n\n-------------\n\n\n");	       
    }
	
    
	public void testAnonIdTransformationss() throws Exception {
    	// read test file and parse it 
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(
                "files/anonIds.wsml");
        assertNotNull(is);
        Parser wsmlParser = Factory.createParser(null);
        // assuming first topentity in file is an ontology  
        ontology = (Ontology)wsmlParser.parse(new InputStreamReader(is))[0];  
        
System.out.println(serializeOntology(ontology)+"\n\n\n-------------\n\n\n");

		// validate the test ontology
		WsmlDLValidator validator = new WsmlDLValidator(leFactory);
		List errors = new Vector();
		boolean b = validator.isValid(ontology, errors, new Vector());
        for (int i=0; i<errors.size(); i++) 
        	System.out.println(errors.get(i));
        assertTrue(b);
		
        // normalize ontology with the WSMLDLNormalizer:
		Ontology normOnt = createOntology();
		normOnt = relTransformer.normalize(ontology);
		normOnt = logExprTransformer.normalize(normOnt);
		
System.out.println(serializeOntology(normOnt)+"\n\n\n-------------\n\n\n");
    }
    
	public void testRelationTransformations() throws Exception {
    	// read test file and parse it 
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(
                "files/relation2attribute.wsml");
        assertNotNull(is);
        Parser wsmlParser = Factory.createParser(null);
        // assuming first topentity in file is an ontology  
        ontology = (Ontology)wsmlParser.parse(new InputStreamReader(is))[0];  
        
System.out.println(serializeOntology(ontology)+"\n\n\n-------------\n\n\n");

		// validate the test ontology
		WsmlDLValidator validator = new WsmlDLValidator(leFactory);
		List errors = new Vector();
		boolean b = validator.isValid(ontology, errors, new Vector());
        for (int i=0; i<errors.size(); i++) 
        	System.out.println(errors.get(i));
        assertTrue(b);
		
        // normalize ontology with the WSMLDLNormalizer:
		Ontology normOnt = createOntology();
		normOnt = relTransformer.normalize(ontology);
		normOnt = logExprTransformer.normalize(normOnt);
		
System.out.println(serializeOntology(normOnt)+"\n\n\n-------------\n\n\n");
    }
    
	public void testDecompositionTransformations() throws Exception {
    	// read test file and parse it 
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(
                "files/decomposition.wsml");
        assertNotNull(is);
        Parser wsmlParser = Factory.createParser(null);
        // assuming first topentity in file is an ontology  
        ontology = (Ontology)wsmlParser.parse(new InputStreamReader(is))[0];  
        
System.out.println(serializeOntology(ontology)+"\n\n\n-------------\n\n\n");

		// validate the test ontology
		WsmlDLValidator validator = new WsmlDLValidator(leFactory);
		List errors = new Vector();
		boolean b = validator.isValid(ontology, errors, new Vector());
        for (int i=0; i<errors.size(); i++) 
        	System.out.println(errors.get(i));
        assertTrue(b);
		
        // normalize ontology with the WSMLDLNormalizer:
		Ontology normOnt = createOntology();
		normOnt = relTransformer.normalize(ontology);
		normOnt = logExprTransformer.normalize(normOnt);
		
System.out.println(serializeOntology(normOnt)+"\n\n\n-------------\n\n\n");
    }
	
}
