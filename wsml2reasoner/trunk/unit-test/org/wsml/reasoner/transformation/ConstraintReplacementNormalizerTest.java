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



public class ConstraintReplacementNormalizerTest extends TestCase {
	
	private ConstraintReplacementNormalizer normalizer;
	protected String ns = "http://ex.org#";
	protected WsmoFactory wsmoFactory;
	protected LogicalExpressionFactory leFactory;
	
	public ConstraintReplacementNormalizerTest() {
		super();
	}


	protected void setUp() throws Exception {
		super.setUp();
		WSMO4JManager wsmoManager = new WSMO4JManager();
		normalizer = new ConstraintReplacementNormalizer(wsmoManager);
		wsmoFactory = wsmoManager.getWSMOFactory();
		leFactory = wsmoManager.getLogicalExpressionFactory();
	}

	public void testInsertViolationsAxiom() {
		Set<Axiom> axioms = new HashSet<Axiom>();
		
		Set<Axiom> out = normalizer.normalizeAxioms(axioms);
		
		// Should always have the violation constraints in an anonymous axiom
		assertEquals(1, out.size() );
		
		Axiom violationsAxiom = out.iterator().next();
		
		// This axiom should be anonymous
		assertEquals( "_#", violationsAxiom.getIdentifier().toString() );
		
		Set<LogicalExpression> les = violationsAxiom.listDefinitions();
		
		for( LogicalExpression le : les ){
			assertTrue(le.toString().contains("/VIOLATION"));
		}
		assertEquals(5, les.size());
	}

	public void testEmptyAxiomsAreIgnored() {
		Axiom axiom1 = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "axiom_empty" ));
		Set<Axiom> axioms = new HashSet<Axiom>();
		axioms.add(axiom1);
		Set<Axiom> out = normalizer.normalizeAxioms(axioms);
		for(Axiom ax : out) {
			assertFalse(ax.getIdentifier().toString().equals(axiom1.getIdentifier().toString()));
		}
		assertEquals(1,out.size());
	}

	public void testConstraintsAreReplaced() throws ParserException {
		Axiom axiom1 = wsmoFactory.createAxiom(wsmoFactory.createIRI(AnonymousIdUtils.MINCARD_PREFIX + "axiom_mincard" ));
		Set<Axiom> axioms = new HashSet<Axiom>();
		axiom1.addDefinition(LETestHelper.buildLE("!- _\"urn:a\" memberOf _#"));
		axioms.add(axiom1);
		fail();
		
//		Set<Axiom> out = normalizer.normalizeAxioms(axioms);
//		
//		for(Axiom ax : out) {
////			System.out.println(ax.getIdentifier().toString());
//			for(LogicalExpression le : ax.listDefinitions()){
//				System.out.println(le.toString());
//			}
//		}
		
	}

//	public void testNormalizeAxiomsNormalAxiom() {
//		String axiomUri = ns + "axiom_normaliser_test";
//		Axiom axiom1 = wsmoFactory.createAxiom(wsmoFactory.createIRI(axiomUri ));
//
//		Set<Axiom> axioms = new HashSet<Axiom>();
//		axioms.add(axiom1);
//
//		Set<Axiom> out = normalizer.normalizeAxioms(axioms);
//		
//		assertTrue( contains( out, axiomUri ) );
//		
//		// Is this the correct behavior ?
//		assertTrue( contains( out, "_#" ) );
//		assertEquals(2, out.size() );
//	}
	
//	private boolean contains( Set<Axiom> axioms, String identifier )
//	{
//		boolean found = false;
//		for (Axiom ax : axioms) {
//			System.out.println(ax.getIdentifier());
//			if( ax.getIdentifier().toString().equals( identifier  ))
//				found = true;
//		}
//		
//		return found;
//	}
	
//	public void testNormalizeAxioms() {
//		Axiom axiom1 = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "axiom_normaliser_test" ));
//		Axiom axiom2 = wsmoFactory.createAxiom(wsmoFactory.createIRI(AnonymousIdUtils.MINCARD_PREFIX));	
//		Axiom axiom3 = wsmoFactory.createAxiom(wsmoFactory.createIRI(AnonymousIdUtils.MAXCARD_PREFIX));	
//		Axiom axiom4 = wsmoFactory.createAxiom(wsmoFactory.createAnonymousID());
//		
//		Set<Axiom> axioms = new HashSet<Axiom>();
//		axioms.add(axiom1);
//		axioms.add(axiom2);
//		axioms.add(axiom3);
//		axioms.add(axiom4);
//
//		Set<Axiom> out = normalizer.normalizeAxioms(axioms);
//		int axi = 0;
//		for (Axiom ax : out) {
//			axi++;
//		
//		}
//		assertEquals(axi,4);
//		assertFalse(axioms.equals(out));
//	}

	
	
	
	


}
