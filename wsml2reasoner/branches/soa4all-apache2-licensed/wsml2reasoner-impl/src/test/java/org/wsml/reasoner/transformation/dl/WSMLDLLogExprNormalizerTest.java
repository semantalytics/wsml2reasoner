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
package org.wsml.reasoner.transformation.dl;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.omwg.ontology.Axiom;
import org.omwg.ontology.Concept;
import org.omwg.ontology.Instance;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Relation;
import org.omwg.ontology.RelationInstance;
import org.sti2.wsmo4j.factory.WsmlFactoryContainer;
import org.wsmo.common.Entity;
import org.wsmo.common.exception.InvalidModelException;
import org.wsmo.factory.FactoryContainer;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.ParserException;

public class WSMLDLLogExprNormalizerTest extends TestCase {
	
	private WSMLDLLogExprNormalizer normalizer;
	protected String ns = "http://ex.org#";
	protected WsmoFactory wsmoFactory;
	protected LogicalExpressionFactory leFactory;
	
	public WSMLDLLogExprNormalizerTest() {
		super();
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		FactoryContainer wsmoManager = new WsmlFactoryContainer();
		normalizer = new WSMLDLLogExprNormalizer(wsmoManager);
        wsmoFactory = wsmoManager.getWsmoFactory();
        leFactory = wsmoManager.getLogicalExpressionFactory();
      
	}

	
    public void testNormalizeEntities() throws IOException, ParserException, InvalidModelException {
    
    	Ontology ontology = wsmoFactory.createOntology(wsmoFactory.createIRI(ns + "ont"));
	    ontology.setDefaultNamespace(wsmoFactory.createIRI(ns));	
	    
    	Set<Entity> in = new HashSet<Entity>();
    	Axiom axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "axiom01"));
    	axiom.setOntology(ontology);
    	ontology.addAxiom(axiom);

        Instance instance1 = wsmoFactory.createInstance(wsmoFactory.createIRI(ns + "instance01"));
        instance1.setOntology(ontology);
        ontology.addInstance(instance1);
        
        Concept concept1 = wsmoFactory.createConcept(wsmoFactory.createIRI(ns + "concept01"));
        concept1.setOntology(ontology);
        ontology.addConcept(concept1);
       
        
        in.add(axiom);
        in.add(instance1);
        in.add(concept1);
       
    	
    	Set <Entity> entities = normalizer.normalizeEntities(in);
    	
    	int con = 0;
    	int ins = 0;
    	int rel = 0;
    	int rei = 0;
    	int axi = 0;
    	
    	for(Entity en : entities) {
    		if( en instanceof Concept){
    			con++;
    		}
    		if( en instanceof Instance){
    			ins++;
    		}
    		if( en instanceof Relation){
    			rel++;
    		}
    		if( en instanceof RelationInstance){
    			rei++;
    		}
    		if( en instanceof Axiom){
    			axi++;
    		}
    		
    	}
    	assertEquals(con,0);
    	assertEquals(ins,0);
    	assertEquals(rel,0);
    	assertEquals(rei,0);
    	assertEquals(axi,1);
    	
 
		
	}
	

}
