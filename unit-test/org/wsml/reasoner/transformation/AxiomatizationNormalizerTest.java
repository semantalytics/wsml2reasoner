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
package org.wsml.reasoner.transformation;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.ontology.Axiom;
import org.omwg.ontology.Ontology;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsmo.common.Entity;
import org.wsmo.common.exception.InvalidModelException;
import org.wsmo.factory.Factory;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.Parser;
import org.wsmo.wsml.ParserException;

import junit.framework.TestCase;

public class AxiomatizationNormalizerTest extends TestCase {

	protected AxiomatizationNormalizer normalizer;
	protected Ontology ontology;
	protected String ns = "http://ex.org#";
	protected WsmoFactory wsmoFactory;
	protected LogicalExpressionFactory leFactory;
	protected Axiom axiom;
	
	public AxiomatizationNormalizerTest() {
		super();
	}

	protected void setUp() throws Exception {
		super.setUp();
		WSMO4JManager wsmoManager = new WSMO4JManager(); 
		normalizer = new AxiomatizationNormalizer(wsmoManager);
		
        wsmoFactory = wsmoManager.getWSMOFactory();
        leFactory = wsmoManager.getLogicalExpressionFactory();
        
        ontology = wsmoFactory.createOntology(wsmoFactory.createIRI(ns + "ont" + System.currentTimeMillis()));
        ontology.setDefaultNamespace(wsmoFactory.createIRI(ns));	
        
        axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "axiom" + System.currentTimeMillis()));
        ontology.addAxiom(axiom);
       
		
		
	}


	protected void tearDown() throws Exception {
		super.tearDown();
		normalizer = null;
		ontology = null;
		axiom = null;
		leFactory = null;
		wsmoFactory = null;
		
		
	}
	
	 
	public void testNormalizeEntities() throws ParserException, IOException, InvalidModelException {
//		String s = "Man subConceptOf Human.";
//        LogicalExpression le = leFactory.createLogicalExpression(s, ontology);
        
		InputStream is = this.getClass().getClassLoader().getResourceAsStream("files/bad.wsml");
        assertNotNull(is);
        Parser wsmlParser = Factory.createParser(null);
        // assuming first topentity in file is an ontology
        ontology = (Ontology) wsmlParser.parse(new InputStreamReader(is))[0];
        
        Set<Entity> in = new HashSet<Entity>();
        in.addAll(ontology.listConcepts());
    	in.addAll(ontology.listInstances());
    	in.addAll(ontology.listRelations());
    	in.addAll(ontology.listRelationInstances());
    	in.addAll(ontology.listAxioms());
		
    	Set <Entity> entitiesAsAxioms = normalizer.normalizeEntities( in );
    	
    	
        for (Entity e : entitiesAsAxioms){
        	if (e instanceof Axiom){
        		System.out.println(e.toString());
        	}
        }
		
	}
	
//    public void testNormalizeConcept() {
//		
//		assertTrue(true);
//	}
	
	

}
