/**
 * WSML Reasoner Implementation.
 *
 * Copyright (c) 2005, University of Innsbruck, Austria.
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
package org.wsml.reasoner.transformation;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.ontology.Axiom;
import org.sti2.wsmo4j.factory.FactoryImpl;
import org.wsml.reasoner.transformation.le.LETestHelper;
import org.wsmo.factory.FactoryContainer;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.ParserException;

public class InverseImplicationNormalizerTest extends TestCase {

	protected InverseImplicationNormalizer normalizer;
	protected String ns = "http://ex.org#";
	protected WsmoFactory wsmoFactory;
	protected LogicalExpressionFactory leFactory;

	public InverseImplicationNormalizerTest() {
		super();
	}

	protected void setUp() throws Exception {
		super.setUp();
		FactoryContainer wsmoManager = new FactoryImpl();
		normalizer = new InverseImplicationNormalizer(wsmoManager);
		wsmoFactory = wsmoManager.getWsmoFactory();
		leFactory = wsmoManager.getLogicalExpressionFactory();
	}


	public void testNormalizeAxiomsTransformImpliedByinBody()
			throws ParserException {

		// TransformNestedImplication
		Axiom axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns
				+ "axiom5"));
		axiom.addDefinition(LETestHelper
				.buildLE("_\"urn:a\" :- _\"urn:c\" impliedBy _\"urn:b\""));

		Set<Axiom> axioms = new HashSet<Axiom>();
		axioms.add(axiom);

		Set<Axiom> out = normalizer.normalizeAxioms(axioms);
		for (Axiom ax : out) {
			assertEquals(ax.getIdentifier().toString(), "_#");
			assertEquals(2, ax.listDefinitions().size());
			
			assertTrue(containsLE(ax.listDefinitions(), LETestHelper.buildLE("_\"urn:a\" :- naf _\"urn:b\"")));
			assertTrue(containsLE(ax.listDefinitions(), LETestHelper.buildLE("_\"urn:a\" :- _\"urn:c\" and _\"urn:b\"")));
		}
	}
	
	private boolean containsLE(Set<LogicalExpression> les, LogicalExpression expected ){
		for(LogicalExpression le : les){
			if(le.equals(expected))
				return true;
		}
		return false;
	}

	

}
