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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.ontology.Axiom;
import org.omwg.ontology.Ontology;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.change.ChangeVisitor;
import org.semanticweb.owl.util.OWLConnection;
import org.semanticweb.owl.util.OWLManager;
import org.sti2.wsmo4j.factory.FactoryImpl;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.impl.DLBasedWSMLReasoner;
import org.wsml.reasoner.transformation.le.LETestHelper;
import org.wsmo.common.exception.InvalidModelException;
import org.wsmo.factory.Factory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.ParserException;

public class WSMLDL2OWLTransformerTest extends TestCase {

	protected WSMLDL2OWLTransformer transformer;
	protected Factory wsmoManager;
	protected String ns = "http://ex.org#";
	protected DLBasedWSMLReasoner dlReasoner;
	protected WsmoFactory wsmoFactory;
	protected Ontology ontology;
	protected OWLOntology owlOntology;
	protected Axiom axiom;
	protected OWLConnection owlConnection;
	protected OWLDataFactory owlDataFactory;
	protected ChangeVisitor changeVisitor;

	public WSMLDL2OWLTransformerTest() {
		super();
	}

	protected void setUp() throws Exception {
		super.setUp();

		Factory wsmoManager = new FactoryImpl();
		wsmoFactory = wsmoManager.getWsmoFactory();
		
		dlReasoner = new DLBasedWSMLReasoner(WSMLReasonerFactory.BuiltInReasoner.PELLET, wsmoManager);

		ontology = wsmoFactory.createOntology(wsmoFactory.createIRI(ns
				+ "ontology01"));
		ontology.setDefaultNamespace(wsmoFactory.createIRI(ns));
		axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns + "axiom01"));
		
	}

	public void testTransformation() throws OWLException, ParserException, InvalidModelException {

		LogicalExpression le = LETestHelper.buildLE("_\"urn:a\" [_\"urn:a\" hasValue _\"urn:b\"].");
		axiom.addDefinition(le);
		ontology.addAxiom(axiom);
		owlOntology = dlReasoner.createOWLOntology(ontology);
		
		Set<Axiom> theAxioms = ontology.listAxioms();
		
		// Set up the implementation class
		Map<Object, Object> parameters = new HashMap<Object, Object>();
		parameters.put(OWLManager.OWL_CONNECTION,
				"org.semanticweb.owl.impl.model.OWLConnectionImpl");
		owlConnection = OWLManager.getOWLConnection(parameters);

		changeVisitor = owlConnection.getChangeVisitor(owlOntology);
		// Get the OWL Data Factory
		owlDataFactory = owlConnection.getDataFactory();
		// Set up the transformer
		transformer = new WSMLDL2OWLTransformer(owlOntology, owlDataFactory, changeVisitor);
		owlOntology = transformer.transform(theAxioms);
		
		Set<?> set = owlOntology.getIndividuals();
		assertEquals(2, set.size());
	}

}
