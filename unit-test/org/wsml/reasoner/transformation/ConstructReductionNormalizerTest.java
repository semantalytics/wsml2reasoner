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

import org.omwg.ontology.Axiom;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;

import junit.framework.TestCase;


public class ConstructReductionNormalizerTest extends TestCase {
	
	private ConstructReductionNormalizer normalizer;
	protected String ns = "http://ex.org#";
	protected WsmoFactory wsmoFactory;
	protected LogicalExpressionFactory leFactory;
	
	public ConstructReductionNormalizerTest() {
		super();
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		WSMO4JManager wsmoManager = new WSMO4JManager();
		normalizer = new ConstructReductionNormalizer(wsmoManager);
        wsmoFactory = wsmoManager.getWSMOFactory();
        leFactory = wsmoManager.getLogicalExpressionFactory();
      
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		normalizer = null;
		leFactory = null;
		wsmoFactory = null;

	}
	
	public void testNormalizeAxioms() {
		Axiom axiom1 = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "axiom" + System.currentTimeMillis()));
		Axiom axiom2 = wsmoFactory.createAxiom(wsmoFactory.createAnonymousID());	
		Axiom axiom3 = wsmoFactory.createAxiom(wsmoFactory.createAnonymousID());
		
		Set<Axiom> axioms = new HashSet<Axiom>();
		axioms.add(axiom1);
		axioms.add(axiom2);
		axioms.add(axiom3);

		Set<Axiom> out = normalizer.normalizeAxioms(axioms);
		
		
	}

}
