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

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.omwg.ontology.Attribute;
import org.omwg.ontology.Concept;
import org.omwg.ontology.Instance;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.WsmlDataType;
import org.sti2.wsmo4j.factory.WsmlFactoryContainer;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.impl.DatalogBasedWSMLReasoner;
import org.wsml.reasoner.impl.DefaultWSMLReasonerFactory;
import org.wsmo.common.IRI;
import org.wsmo.factory.DataFactory;
import org.wsmo.factory.FactoryContainer;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;

import com.ontotext.wsmo4j.ontology.InstanceImpl;

public class DLUtilitiesTest extends TestCase {

	protected DLUtilities dlUtils;
	protected DatalogBasedWSMLReasoner reasoner;
	protected String ns = "http://ex.org#";
	protected Ontology ontology;
	protected FactoryContainer factory;
	protected WsmoFactory wsmoFactory;
	protected LogicalExpressionFactory leFactory;
	protected DataFactory dataFactory;

	public DLUtilitiesTest() {
	}

	protected void setUp() throws Exception {
		super.setUp();
		factory = new WsmlFactoryContainer();
		wsmoFactory = factory.getWsmoFactory();
		dataFactory = factory.getXmlDataFactory();
		leFactory = factory.getLogicalExpressionFactory();

		ontology = wsmoFactory
				.createOntology(wsmoFactory.createIRI(ns + "ont"));
		ontology.setDefaultNamespace(wsmoFactory.createIRI(ns));

		Concept conceptWithoutAnyAttr = wsmoFactory.createConcept(wsmoFactory
				.createIRI(ns + "conceptWithoutAttribute"));
		conceptWithoutAnyAttr.setOntology(ontology);
		ontology.addConcept(conceptWithoutAnyAttr);

		Concept badConcept = wsmoFactory.createConcept(wsmoFactory.createIRI(ns
				+ "concept01"));
		badConcept.setOntology(ontology);
		ontology.addConcept(badConcept);

		Concept badSubConcept = wsmoFactory.createConcept(wsmoFactory
				.createIRI(ns + "subconcept01"));
		badSubConcept.setOntology(ontology);

		badConcept.addSubConcept(badSubConcept);

		Attribute badAttribute = badConcept.createAttribute(wsmoFactory
				.createIRI(ns + "a"));
		badAttribute.addInferringType(dataFactory.createDataType(WsmlDataType.WSML_STRING));

		Instance i = new InstanceImpl(wsmoFactory.createIRI(ns + "aa"));
		i.addConcept(badConcept);
		i.addAttributeValue(wsmoFactory.createIRI(ns + "a"), dataFactory
				.createInteger(new BigInteger("3")));

		ontology.addInstance(i);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put(WSMLReasonerFactory.PARAM_BUILT_IN_REASONER,
				WSMLReasonerFactory.BuiltInReasoner.IRIS_WELL_FOUNDED);
		reasoner = (DatalogBasedWSMLReasoner) DefaultWSMLReasonerFactory
				.getFactory().createFlightReasoner(params);

		reasoner.registerOntology(ontology);
		dlUtils = new DLUtilities(reasoner, factory);
	}

	public void testGetAllConcepts() {

		Set<Concept> s = dlUtils.getAllConcepts();
		// [ 2003873 ] Concept with no relatives not returned from
		// getAllConcepts()
		// 
		
		assertEquals(3, s.size());
		for (Concept c : s) {
			System.out.println(c.toString());
		}
		
	}

	public void testGetAllInstances() {
		Set<Instance> s = dlUtils.getAllInstances();
		assertEquals(1, s.size());
	}

	public void testgetAllAttributes() {
		Set<IRI> s = dlUtils.getAllAttributes();
		assertEquals(1, s.size());
	}

	public void testgetAttributes() {
		Set<IRI> s1 = dlUtils.getAllConstraintAttributes();
		Set<IRI> s2 = dlUtils.getAllInferenceAttributes();
		assertEquals(0, s1.size());
		assertEquals(1, s2.size());
	}

	public void testGetConcepts() {
		Instance i = new InstanceImpl(wsmoFactory.createIRI(ns + "aa"));
		Set<Concept> s = dlUtils.getConcepts(i);
		assertEquals(1, s.size());
		for (Concept c : s) {
			assertEquals(wsmoFactory.createIRI(ns + "concept01"), c
					.getIdentifier());
		}
	}

	public void testGetConceptsOf() {
		Set<Concept> s = dlUtils.getConceptsOf(wsmoFactory.createIRI(ns + "a"));
		assertEquals(1, s.size());
		for (Concept c : s) {
			assertEquals(wsmoFactory.createIRI(ns + "concept01"), c
					.getIdentifier());
		}

	}

	public void testGetSubConcept() {
		Set<Concept> s = dlUtils.getSubConcepts(wsmoFactory
				.createConcept(wsmoFactory.createIRI(ns + "concept01")));
		assertEquals(1, s.size());
		for (Concept c : s) {
			assertEquals(wsmoFactory.createIRI(ns + "subconcept01"), c
					.getIdentifier());
		}
	}
}
