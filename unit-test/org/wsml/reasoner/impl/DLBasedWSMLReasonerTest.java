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

package org.wsml.reasoner.impl;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.ontology.Attribute;
import org.omwg.ontology.Axiom;
import org.omwg.ontology.Concept;
import org.omwg.ontology.Instance;
import org.omwg.ontology.Ontology;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLOntology;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.api.inconsistency.InconsistencyException;
import org.wsmo.common.Entity;
import org.wsmo.common.IRI;
import org.wsmo.common.exception.InvalidModelException;
import org.wsmo.common.exception.SynchronisationException;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.ParserException;

public class DLBasedWSMLReasonerTest extends TestCase {

	protected DLBasedWSMLReasoner reasoner;
	protected WSMO4JManager wsmoManager;
	protected String ns = "http://ex.org#";
	protected WsmoFactory wsmoFactory;
	protected LogicalExpressionFactory leFactory;
	protected Ontology ontology;
	protected Concept humanConcept;
	protected Attribute hasRelativeAttr;
	protected Concept locationConcept;
	protected Attribute hasParentAttr;
	protected Attribute livesAtAttr;
	protected Instance person1;
	protected Instance person2;

	public DLBasedWSMLReasonerTest() {
		super();
	}

	protected void setUp() throws Exception {
		super.setUp();
		WSMO4JManager wsmoManager = new WSMO4JManager();
		wsmoFactory = wsmoManager.getWSMOFactory();
		leFactory = wsmoManager.getLogicalExpressionFactory();

		// build up standard ontology
		ontology = wsmoFactory.createOntology(wsmoFactory.createIRI(ns
				+ "AboutHumans"));
		ontology.setDefaultNamespace(wsmoFactory.createIRI(ns));

		humanConcept = wsmoFactory.createConcept(wsmoFactory.createIRI(ns
				+ "urn://Human"));
		hasRelativeAttr = humanConcept.createAttribute(wsmoFactory.createIRI(ns
				+ "urn://hasRelative"));
		// hasRelativeAttr.setSymmetric(true);
		hasRelativeAttr.addType(humanConcept);
		locationConcept = wsmoFactory.createConcept(wsmoFactory.createIRI(ns
				+ "urn://Location"));

		hasParentAttr = humanConcept.createAttribute(wsmoFactory.createIRI(ns
				+ "urn://hasParent"));
		hasParentAttr.addType(humanConcept);

		livesAtAttr = humanConcept.createAttribute(wsmoFactory.createIRI(ns
				+ "urn://livesAt"));
		livesAtAttr.addType(locationConcept);

		person1 = wsmoFactory.createInstance(wsmoFactory
				.createIRI("urn://Person1"), humanConcept);
		person2 = wsmoFactory.createInstance(wsmoFactory
				.createIRI("urn://Person2"), humanConcept);
		// Instance person3 = wsmoFactory.createInstance(wsmoFactory
		// .createIRI("urn://Person3"), humanConcept);

		person1.addAttributeValue(hasParentAttr.getIdentifier(), person2);
		person1.addAttributeValue(hasParentAttr.getIdentifier(), wsmoFactory
				.createInstance(wsmoFactory.createAnonymousID()));

		Axiom person1LivesAx = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns
				+ "urn://person1LivesSomewhere"));

		ontology.addConcept(humanConcept);
		ontology.addConcept(locationConcept);
		ontology.addInstance(person1);
		ontology.addInstance(person2);
		ontology.addAxiom(person1LivesAx);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put(WSMLReasonerFactory.PARAM_BUILT_IN_REASONER,
				WSMLReasonerFactory.BuiltInReasoner.PELLET);

		reasoner = (DLBasedWSMLReasoner) DefaultWSMLReasonerFactory
				.getFactory().createDLReasoner(params);

		// reasoner.registerOntology(ontology);

	}

	public void testGetAllAttributes() throws ParserException,
			SynchronisationException, InvalidModelException,
			InconsistencyException {

		reasoner.registerOntology(ontology);
		Set<IRI> set = reasoner.getAllAttributes();
		int count = 0;
		for (IRI iri : set) {
			if (iri.toString().equals(ns + "urn://hasParent")) {
				count++;
			}
			if (iri.toString().equals(ns + "urn://livesAt")) {
				count++;
			}
			if (iri.toString().equals(ns + "urn://hasRelative")) {
				count++;
			}
		}
		assertEquals(3, count);

	}

	public void testTransformOntology() throws ParserException,
			SynchronisationException, InvalidModelException, OWLException,
			URISyntaxException {

		Axiom person1LivesAx = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns
				+ "urn://person1LivesSomewhere"));
		person1LivesAx.addDefinition(leFactory.createConjunction(
				leFactory.createAttributeValue(person1.getIdentifier(),
						livesAtAttr.getIdentifier(), leFactory
								.createAnonymousID((byte) 1)), leFactory
						.createMemberShipMolecule(leFactory
								.createAnonymousID((byte) 1), locationConcept
								.getIdentifier())));

		person1LivesAx.setOntology(ontology);
		ontology.addAxiom(person1LivesAx);

		OWLOntology owlOntology = reasoner.transformOntology(ontology
				.listAxioms());
		Set<?> set = owlOntology.getIndividuals();
		assertEquals(2, set.size());
	}

	public void testConvertEntities() {
		Set<Entity> entities = new HashSet<Entity>();
		entities.addAll(ontology.listConcepts());
		entities.addAll(ontology.listInstances());
		entities.addAll(ontology.listRelations());
		entities.addAll(ontology.listRelationInstances());
		entities.addAll(ontology.listAxioms());

		Set<Axiom> axes = reasoner.convertEntities(entities);
		for (Axiom ax : axes) {
			Set<LogicalExpression> expr = ax.listDefinitions();
			assertEquals(true, containsExpr(expr));
		}

	}

	public void testGetAllConceptsWithNoRelatives()
			throws InconsistencyException, InvalidModelException {

		Ontology ontology1 = wsmoFactory.createOntology(wsmoFactory
				.createIRI(ns + "SomeOntology"));

		// Bug [ 2003873 ] Concept with no relatives not returned from
		// getAllConcepts()
		// Concept with no relatives not returned from getAllConcepts()
		// A concept that does not have any relationship with any other concept
		// via
		// attributes, super-concept, sub-concept etc, is not returned in a call
		// to
		// WSMLResoner.getAllConcepts()

		ontology1.setDefaultNamespace(wsmoFactory.createIRI(ns));
		Concept concept01 = wsmoFactory.createConcept(wsmoFactory.createIRI(ns
				+ "urn://Concept01"));
		ontology1.addConcept(concept01);
		reasoner.registerOntology(ontology1);

		Set<Concept> set = reasoner.getAllConcepts();
		assertEquals(1, set.size());
	}

	public void testGetAllConcepts() throws InconsistencyException,
			InvalidModelException {

		Ontology ontology1 = wsmoFactory.createOntology(wsmoFactory
				.createIRI(ns + "SomeOntology"));

		ontology1.setDefaultNamespace(wsmoFactory.createIRI(ns));
		Concept concept01 = wsmoFactory.createConcept(wsmoFactory.createIRI(ns
				+ "urn://Concept01"));

		Attribute attr01 = concept01.createAttribute(wsmoFactory.createIRI(ns
				+ "urn://anAttribute"));
		attr01.addType(concept01);
		ontology1.addConcept(concept01);
		reasoner.registerOntology(ontology1);
		Set<Concept> set = reasoner.getAllConcepts();
		assertEquals(1, set.size());
		reasoner.deRegister();
	}

	private boolean containsExpr(Set<LogicalExpression> expr) {
		int count = 0;
		for (LogicalExpression le : expr) {

			if (le
					.toString()
					.equals(
							"_\"http://ex.org#urn://Human\"[_\"http://ex.org#urn://hasRelative\" impliesType _\"http://ex.org#urn://Human\"]. ")) {
				count++;
			} else if (le
					.toString()
					.equals(
							"_\"http://ex.org#urn://Human\"[_\"http://ex.org#urn://hasParent\" impliesType _\"http://ex.org#urn://Human\"]. ")) {
				count++;
			} else if (le
					.toString()
					.equals(
							"_\"urn://Person1\" memberOf _\"http://ex.org#urn://Human\". ")) {
				count++;
			} else if (le
					.toString()
					.equals(
							"_\"urn://Person2\" memberOf _\"http://ex.org#urn://Human\". ")) {
				count++;
			} else if (le
					.toString()
					.startsWith(
							"_\"urn://Person1\"[_\"http://ex.org#urn://hasParent\" hasValue _\"http://www.wsmo.org/reasoner/anonymous")) {
				count++;
			} else if (le
					.toString()
					.equals(
							"_\"http://ex.org#urn://Human\"[_\"http://ex.org#urn://livesAt\" impliesType _\"http://ex.org#urn://Location\"]. ")) {
				count++;
			}
		}
		if (count == 6) {
			return true;
		}

		return false;
	}

}
