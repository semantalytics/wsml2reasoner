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

package org.wsml.reasoner.transformation;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.ontology.Axiom;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsml.reasoner.transformation.le.LETestHelper;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.ParserException;


public class LloydToporNormalizerTest extends TestCase {

	protected LloydToporNormalizer normalizer;
	protected String ns = "http://ex.org#";
	protected WsmoFactory wsmoFactory;
	protected LogicalExpressionFactory leFactory;

	public LloydToporNormalizerTest() {
		super();
	}

	protected void setUp() throws Exception {
		super.setUp();
		WSMO4JManager wsmoManager = new WSMO4JManager();
		normalizer = new LloydToporNormalizer(wsmoManager);

		wsmoFactory = wsmoManager.getWSMOFactory();
		leFactory = wsmoManager.getLogicalExpressionFactory();

	}

	protected void tearDown() throws Exception {
		super.tearDown();
		normalizer = null;
		leFactory = null;
		wsmoFactory = null;
	}

	public void testNormalizeAxioms() throws ParserException {
		// TopDownLESplitter:
		Axiom axiom1 = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "axiom"));
		axiom1.addDefinition(LETestHelper.buildLE("_\"urn:a\" and _\"urn:b\" and _\"urn:c\" and _\"urn:f\" :- _\"urn:d\""));
		
		Set<Axiom> axioms = new HashSet<Axiom>();
		axioms.add(axiom1);


		Set<Axiom> out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(),"_#");
			Set <LogicalExpression> les = ax.listDefinitions();
			int all = 0;
			for(LogicalExpression le : les) {
				if(checkContains(le ,"_\"urn:f\"\n:-\n_\"urn:d\". ")){
					all++;
				}
				if (checkContains(le ,"_\"urn:a\"\n:-\n_\"urn:d\". ")){
					all++;
				}
				if (checkContains(le ,"_\"urn:b\"\n:-\n_\"urn:d\". ")){
					all++;
				}
				if (checkContains(le ,"_\"urn:c\"\n:-\n_\"urn:d\". ")){
					all++;
				}
				
			}
			assertEquals(4,all);
			
		}
		
		// splitDisjunctiveBody
		axiom1 = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "axiom2"));
		axiom1.addDefinition(LETestHelper.buildLE("_\"urn:a\" :- _\"urn:b\" or (_\"urn:c\" and _\"urn:f\" and _\"urn:d\")"));
		
	    axioms = new HashSet<Axiom>();
		axioms.add(axiom1);


		out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(),"_#");
			Set <LogicalExpression> les = ax.listDefinitions();
			int all = 0;
			for(LogicalExpression le : les) {
				if(checkContains(le ,"_\"urn:a\"\n:-\n_\"urn:b\". ")){
					all++;
				}
				if(checkContains(le ,"_\"urn:a\"\n:-\n_\"urn:c\"\n  and _\"urn:f\"\n  and _\"urn:d\". ")){
					all++;
				}
				
			}
			assertEquals(2,all);
		}
		
		
		// SplitConstraint
		axiom1 = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "axiom3"));
		axiom1.addDefinition(LETestHelper.buildLE("!- _\"urn:a\" or _\"urn:b\" and _\"urn:c\" "));
		
	    axioms = new HashSet<Axiom>();
		axioms.add(axiom1);


		out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(),"_#");
			Set <LogicalExpression> les = ax.listDefinitions();
			int all = 0;
			for(LogicalExpression le : les) {
				if(checkContains(le ,"!- _\"urn:a\". ")){
					all++;
				}
				if(checkContains(le ,"!- _\"urn:a\"\n  and _\"urn:c\". ")){
					
					all++;
				}
				
			}
			assertEquals(1,all);
		}
		
		// SplitConjunctiveHead
		axiom1 = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "axiom4"));
		axiom1.addDefinition(LETestHelper.buildLE("_\"urn:a\" and _\"urn:b\" and _\"urn:c\" :- _\"urn:d\""));
		
	    axioms = new HashSet<Axiom>();
		axioms.add(axiom1);

		out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(),"_#");
			Set <LogicalExpression> les = ax.listDefinitions();
			int all = 0;
			for(LogicalExpression le : les) {
				if(checkContains(le ,"_\"urn:a\"\n:-\n_\"urn:d\". ")){
					all++;
				}
				if(checkContains(le ,"_\"urn:b\"\n:-\n_\"urn:d\". ")){
					all++;
				}
				if(checkContains(le ,"_\"urn:c\"\n:-\n_\"urn:d\". ")){	
					all++;
				}
				
			}
			assertEquals(3,all);
		}
		
		
		// TransformNestedImplication
		axiom1 = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "axiom5"));
		axiom1.addDefinition(LETestHelper.buildLE("_\"urn:a\" impliedBy _\"urn:b\" :- _\"urn:c\""));
		
	    axioms = new HashSet<Axiom>();
		axioms.add(axiom1);


		out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(),"_#");
			Set <LogicalExpression> les = ax.listDefinitions();
			int all = 0;
			for(LogicalExpression le : les) {
				if(checkContains(le ,"_\"urn:a\"\n:-\n_\"urn:b\"\n  and _\"urn:c\". ")){
					all++;
				}
			}
			assertEquals(1,all);
		}
		
		// SplitConjunction
		axiom1 = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "axiom6"));
		axiom1.addDefinition(LETestHelper.buildLE("_\"urn:a\" and _\"urn:b\" and _\"urn:c\""));
		
	    axioms = new HashSet<Axiom>();
		axioms.add(axiom1);


		out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(),"_#");
			Set <LogicalExpression> les = ax.listDefinitions();
			int all = 0;
			for(LogicalExpression le : les) {
				if(checkContains(le ,"_\"urn:a\". ")){
					all++;
				}
				if(checkContains(le ,"_\"urn:b\". ")){
					all++;
				}
				if(checkContains(le ,"_\"urn:c\". ")){
					all++;
				}
			}
			assertEquals(3,all);
		}
		
		
		// TransformImplication
		axiom1 = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "axiom7"));
		axiom1.addDefinition(LETestHelper.buildLE("_\"urn:a\" impliedBy _\"urn:b\""));
		
	    axioms = new HashSet<Axiom>();
		axioms.add(axiom1);


		out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(),"_#");
			Set <LogicalExpression> les = ax.listDefinitions();
			int all = 0;
			for(LogicalExpression le : les) {
				System.out.println(le);
				if(checkContains(le ,"_\"urn:a\"\n:-\n_\"urn:b\". ")){
					all++;
				}
				
			}
			assertEquals(1,all);
		}
		
		

	}
	
	private boolean checkContains(LogicalExpression le, String g) {
		if(le.toString().equals(g))
			return true;
		
		return false;
	}

}
