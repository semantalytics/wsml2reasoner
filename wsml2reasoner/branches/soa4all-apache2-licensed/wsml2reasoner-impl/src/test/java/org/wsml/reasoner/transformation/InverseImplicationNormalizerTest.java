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
package org.wsml.reasoner.transformation;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.ontology.Axiom;
import org.sti2.wsmo4j.factory.WsmlFactoryContainer;
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
		FactoryContainer wsmoManager = new WsmlFactoryContainer();
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
