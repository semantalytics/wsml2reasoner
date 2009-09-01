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
package org.wsml.reasoner.serializer.owl;

import java.util.HashMap;

import junit.framework.TestCase;

import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.ontology.Axiom;
import org.omwg.ontology.Ontology;
import org.semanticweb.owl.io.RendererException;
import org.semanticweb.owl.model.OWLOntology;
import org.sti2.wsmo4j.factory.WsmlFactoryContainer;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.impl.DLBasedWSMLReasoner;
import org.wsml.reasoner.impl.FOLBasedWSMLReasoner;
import org.wsml.reasoner.transformation.le.LETestHelper;
import org.wsmo.factory.FactoryContainer;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.ParserException;

public class OWLSerializerImplTest extends TestCase {

	protected OWLSerializer owlImpl;
	protected HashMap<String, String> prefs;
	protected DLBasedWSMLReasoner dlReasoner;
	protected FOLBasedWSMLReasoner reasoner;
	protected FactoryContainer wsmoManager;
	protected String ns = "http://ex.org#";
	protected WsmoFactory wsmoFactory;
	protected LogicalExpressionFactory leFactory;
	protected Ontology ontology;
	protected OWLOntology owlOntology;
	protected Axiom axiom;

	public OWLSerializerImplTest() {
		super();
	}

	protected void setUp() throws Exception {
		super.setUp();

		owlImpl = new OWLSerializerImpl();
		FactoryContainer wsmoManager = new WsmlFactoryContainer();
		wsmoFactory = wsmoManager.getWsmoFactory();
		leFactory = wsmoManager.getLogicalExpressionFactory();
		dlReasoner = new DLBasedWSMLReasoner(
				WSMLReasonerFactory.BuiltInReasoner.PELLET, wsmoManager);
		ontology = wsmoFactory.createOntology(wsmoFactory.createIRI(ns
				+ "ontology01"));
		ontology.setDefaultNamespace(wsmoFactory.createIRI(ns));
		axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "axiom01"));
		ontology.addAxiom(axiom);
	}

	public void testSerialize() throws ParserException, RendererException {

		LogicalExpression le = LETestHelper
				.buildLE("_\"urn:a\" [_\"urn:a\" hasValue _\"urn:b\"].");
		axiom.addDefinition(le);
		owlOntology = dlReasoner.createOWLOntology(ontology);
		prefs = new HashMap<String, String>();
		prefs.put(OWLSerializer.OWL_SERIALIZER, OWLSerializer.OWL_ABSTRACT);
		StringBuffer b = new StringBuffer();
		owlImpl.serialize(owlOntology, b, prefs);
		int count = 0;

		if (b
				.toString()
				.contains(
						"Namespace(rdf	= <http://www.w3.org/1999/02/22-rdf-syntax-ns#>)")) {
			count++;
		}

		if (b.toString().contains(
				"Namespace(owl	= <http://www.w3.org/2002/07/owl#>)")) {
			count++;
		}

		if (b.toString().contains(
				"Namespace(xsd	= <http://www.w3.org/2001/XMLSchema#>)")) {
			count++;
		}

		if (b.toString().contains(
				"Namespace(rdfs	= <http://www.w3.org/2000/01/rdf-schema#>)")) {
			count++;
		}

		if (b.toString().contains("ObjectProperty(<urn:a>)")) {
			count++;
		}

		if (b.toString().contains("Individual(<urn:a>")) {
			count++;
		}

		if (b.toString().contains("value(<urn:a> <urn:b>))")) {
			count++;
		}

		assertEquals(7, count);

	}

}
