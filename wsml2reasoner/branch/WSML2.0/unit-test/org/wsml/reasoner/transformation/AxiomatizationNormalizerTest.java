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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.ontology.Axiom;
import org.omwg.ontology.Concept;
import org.omwg.ontology.Instance;
import org.omwg.ontology.Relation;
import org.omwg.ontology.RelationInstance;
import org.sti2.wsmo4j.factory.FactoryImpl;
import org.wsml.reasoner.transformation.le.LETestHelper;
import org.wsmo.common.Entity;
import org.wsmo.common.exception.InvalidModelException;
import org.wsmo.factory.FactoryContainer;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.ParserException;

public class AxiomatizationNormalizerTest extends TestCase {

	protected AxiomatizationNormalizer normalizer;
	protected String ns = "http://ex.org#";
	protected WsmoFactory wsmoFactory;
	protected LogicalExpressionFactory leFactory;
	
	public AxiomatizationNormalizerTest() {
		super();
	}

	protected void setUp() throws Exception {
		super.setUp();
		FactoryContainer wsmoManager = new FactoryImpl(); 
		normalizer = new AxiomatizationNormalizer(wsmoManager);
		
        wsmoFactory = wsmoManager.getWsmoFactory();
        leFactory = wsmoManager.getLogicalExpressionFactory();

	}


	public void testNormalizeEntitiesAxiom() throws ParserException, IOException, InvalidModelException {
		
		Set<Entity> in = new HashSet<Entity>();
		Axiom axiom = wsmoFactory.createAxiom(wsmoFactory.createAnonymousID());
		axiom.addDefinition(LETestHelper.buildLE("!- _\"urn:a\" [_\"urn:a\" hasValue _\"urn:c\"] subConceptOf _\"urn:b\" "));
		in.add(axiom);
		
    	Set <Entity> entities = normalizer.normalizeEntities(in);

    	
    	for(Entity en : entities) {
    		if( en instanceof Axiom){
    			Set <LogicalExpression> les = ((Axiom)en).listDefinitions();
    			for(LogicalExpression le : les) {
    				assertEquals(le.toString(), "!- _\"urn:a\" [_\"urn:a\" hasValue _\"urn:c\"] subConceptOf _\"urn:b\" .");
    			}
    		}
    		
    	}

	
	}
	
	public void testNormalizeEntitiesConcept() throws ParserException, IOException, InvalidModelException {
		
		Set<Entity> in = new HashSet<Entity>();
		Axiom axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "axiom_TestNormalizeEntitiesConcept"));
		axiom.addDefinition(LETestHelper.buildLE("_\"urn:a\" "));
		in.add(axiom);

        Instance instance1 = wsmoFactory.createInstance(wsmoFactory.createIRI(ns + "instance1"));   
        in.add(instance1);
        
        Relation rel = wsmoFactory.createRelation(wsmoFactory.createIRI(ns + "relation1"));
        in.add(rel);
        
        Concept concept1 = wsmoFactory.createConcept(wsmoFactory.createIRI(ns + "concept1"));
        concept1.addInstance(wsmoFactory.createInstance(wsmoFactory.createIRI(ns + "instance")));
        in.add(concept1);
        
    	Set <Entity> entities = normalizer.normalizeEntities(in);

    	for(Entity en : entities) {
    		if( en instanceof Concept){
    			fail();
    		}
    		if( en instanceof Instance){
    			fail();
    		}
    		if( en instanceof Relation){
    			fail();
    		}
    			
    		if( en instanceof RelationInstance){
    			fail();
    		}
    		if( en instanceof Axiom){
    			Set <LogicalExpression> les = ((Axiom)en).listDefinitions();
    			for(LogicalExpression le : les) {
    				assertEquals(le.toString(), "_\"urn:a\" .");
    			}
    		}
    		
    	}
    	assertFalse(entities.size() == in.size());

	
	}
	
	public void testGetAxioms() throws ParserException{
		
		LogicalExpression in = LETestHelper.buildLE("_# subConceptOf _\"urn:a\"");
		ArrayList<LogicalExpression> list = new ArrayList<LogicalExpression>();
		
		list.add(in);
		Set<Axiom> set = normalizer.getAxioms(list);
		assertEquals(set.size(), 1);
		
		in = LETestHelper.buildLE("_# subConceptOf _\"urn:b\"");
		list.add(in);
		set = normalizer.getAxioms(list);
		assertEquals(set.size(), 2);
		
		int count = 0;
		for(Axiom ax : set){
			Set<LogicalExpression> les = ax.listDefinitions();
			for(LogicalExpression le : les){
				if(equalsLE(le, "_# subConceptOf _\"urn:a\". ")){
					count++;
				}
				if(equalsLE(le, "_# subConceptOf _\"urn:b\". ")){
					count++;
				}
			}
			
		}
		assertEquals(2,count);
		
		
	}
	
	private boolean equalsLE(LogicalExpression le, String lesin) throws ParserException{
		if (le.toString().equals(lesin))
			return true;

		return false;
		
	}
	


}
