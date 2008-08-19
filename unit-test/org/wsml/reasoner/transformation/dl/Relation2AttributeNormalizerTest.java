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
package org.wsml.reasoner.transformation.dl;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import org.omwg.ontology.Axiom;
import org.omwg.ontology.Concept;
import org.omwg.ontology.Instance;
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


public class Relation2AttributeNormalizerTest extends TestCase {

	private Relation2AttributeNormalizer normalizer;
	protected Ontology ontology;
	protected String ns = "http://ex.org#";
	protected WsmoFactory wsmoFactory;
	protected LogicalExpressionFactory leFactory;
	protected Axiom axiom;
	
	public Relation2AttributeNormalizerTest() {
		super();
	}

	protected void setUp() throws Exception {
		super.setUp();
		WSMO4JManager wsmoManager = new WSMO4JManager();
		normalizer = new Relation2AttributeNormalizer(wsmoManager);
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

	}

	public void testNormalizeEntities() throws IOException, ParserException, InvalidModelException  {
		InputStream is = this.getClass().getClassLoader().getResourceAsStream("files/family.wsml");
        assertNotNull(is);
        Parser wsmlParser = Factory.createParser(null);
        ontology = (Ontology) wsmlParser.parse(new InputStreamReader(is))[0];
        
        Set<Entity> in = new HashSet<Entity>();
        in.addAll(ontology.listConcepts());
    	in.addAll(ontology.listInstances());
    	in.addAll(ontology.listRelations());
    	in.addAll(ontology.listRelationInstances());
    	in.addAll(ontology.listAxioms());
		
    	Set <Entity> entities = normalizer.normalizeEntities(in);

    	int en = 0;
    	int con = 0;
    	int inst = 0;

        for (Entity e : entities){
        	if (e instanceof Axiom){
        		en++;
        	}
        	if (e instanceof Concept){
        		con++;
        	}
        	if(e instanceof Instance){
        		inst++;
        	}
        }
        assertEquals(en,2);
        assertEquals(con,2);
        assertEquals(inst,4);

	}

}
