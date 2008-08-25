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

package org.wsml.reasoner;

import java.util.HashSet;
import java.util.Set;

import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.ontology.Axiom;
import org.omwg.ontology.Ontology;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsml.reasoner.transformation.le.LETestHelper;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.ParserException;

import junit.framework.TestCase;


public class WSML2DatalogTransformerTest extends TestCase {
	
	private WSML2DatalogTransformer transformer;
	protected Ontology ontology;
	protected String ns = "http://ex.org#";
	protected WsmoFactory wsmoFactory;
	protected LogicalExpressionFactory leFactory;
	protected Axiom axiom;
	
	
	public WSML2DatalogTransformerTest() {
		super();
	}

	protected void setUp() throws Exception {
		super.setUp();
		WSMO4JManager wsmoManager = new WSMO4JManager(); 
		transformer = new WSML2DatalogTransformer(wsmoManager);
		
        wsmoFactory = wsmoManager.getWSMOFactory();
        leFactory = wsmoManager.getLogicalExpressionFactory();
        
        ontology = wsmoFactory.createOntology(wsmoFactory.createIRI(ns + "ont" + System.currentTimeMillis()));
        ontology.setDefaultNamespace(wsmoFactory.createIRI(ns));	
        
        axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "axiom" + System.currentTimeMillis()));
        ontology.addAxiom(axiom);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		transformer = null;
		ontology = null;
		axiom = null;
		leFactory = null;
		wsmoFactory = null;
	}
	
	public void testTransform() throws ParserException {
		
		LogicalExpression le = LETestHelper.buildLE("_\"urn:a\"[_\"urn:a\" hasValue _#]");
		Set<Rule> out = transformer.transform(le);
		for(Rule r : out){
			assertTrue(r.toString().contains("wsml-has-value"));
			assertTrue(r.toString().contains("urn:a, urn:a, _#"));
		}
		
		
		le = LETestHelper.buildLE("_\"urn:a\"[_\"urn:a\" impliesType _#]");
		out = transformer.transform(le);
		for(Rule r : out){
			assertTrue(r.toString().contains("wsml-implies-type"));
			assertTrue(r.toString().contains("urn:a, urn:a, _#"));
		}
		
		le = LETestHelper.buildLE("_\"urn:a\"[_\"urn:a\" ofType _#]");
		out = transformer.transform(le);
		for(Rule r : out){
			assertTrue(r.toString().contains("wsml-of-type"));
			assertTrue(r.toString().contains("urn:a, urn:a, _#"));
		}
		
		le = LETestHelper.buildLE("_\"urn:a\" subConceptOf _\"urn:b\"");
		out = transformer.transform(le);
		for(Rule r : out){
			assertTrue(r.toString().contains("wsml-subconcept-of"));
			assertTrue(r.toString().contains("urn:a, urn:b"));
		}
		
		le = LETestHelper.buildLE("_\"urn:a\" implies _\"urn:b\"");
		out = transformer.transform(le);
		for(Rule r : out){
			assertEquals(r.toString(), ("urn:b() :- urn:a()."));
		}
		
		
		Set<LogicalExpression> in = new HashSet<LogicalExpression>();
		in = new HashSet<LogicalExpression>();
		le = LETestHelper.buildLE("_\"urn:a\"[_\"urn:a\" impliesType _#]");
		in.add(le);
		
		out = transformer.transform(in);
		for(Rule r : out){
			assertTrue(r.toString().contains(("wsml-implies-type(urn:a, urn:a, _#).")));
		}
		
		in = new HashSet<LogicalExpression>();
		le = LETestHelper.buildLE("_\"urn:a\" subConceptOf \"urn:b\" ");
		in.add(le);
		out = transformer.transform(in);
		for(Rule r : out){
			assertTrue(r.toString().contains(("wsml-subconcept-of(urn:a, urn:b).")));
		}
		
	}
}
	
	
	
	


