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
package abstractTests.dl;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.Map.Entry;

import junit.framework.TestCase;

import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Concept;
import org.omwg.ontology.Instance;
import org.omwg.ontology.Ontology;
import org.sti2.wsmo4j.factory.FactoryImpl;
import org.wsml.reasoner.api.DLReasoner;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsmo.common.IRI;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.Parser;

import abstractTests.DL;

/*
 * needs:  files/wsml2owlExample.wsml
 */
public abstract class AbstractSimpleInference extends TestCase implements DL {

	protected WSMO4JManager wsmoManager;
	protected WsmoFactory wsmoFactory;
	protected Ontology ontology;
	protected Parser parser;
	protected String ns;
	protected DLReasoner wsmlReasoner;

	protected void setUp() throws Exception {
		super.setUp();
		WSMO4JManager wsmoManager = new WSMO4JManager();
		wsmoFactory = wsmoManager.getWSMOFactory();
		parser = FactoryImpl.getInstance().createParser(null);

		// wsml2owlExample.wsml
		InputStream is = this.getClass().getClassLoader().getResourceAsStream(
				"files/wsml2owlExample.wsml");
		assertNotNull(is);

		ontology = (Ontology) parser.parse(new InputStreamReader(is), null)[0];
		ns = ontology.getDefaultNamespace().getIRI().toString();

		wsmlReasoner = this.getDLReasoner();
		wsmlReasoner.registerOntology(ontology);
	}

	public void testSatisfiable() {
		assertTrue(wsmlReasoner.isSatisfiable());
	}

	public void testGetAllConcepts() {
		Set<Concept> set = wsmlReasoner.getAllConcepts();
		assertEquals(19, set.size());
		set.remove(wsmoFactory.createConcept(wsmoFactory
				.createIRI("http://www.w3.org/2002/07/owl#Thing")));
		set.remove(wsmoFactory.createConcept(wsmoFactory
				.createIRI("http://www.w3.org/2002/07/owl#Nothing")));
		assertEquals(19, set.size());
	}

	public void testGetAllInstances() {
		Set<Instance> set = wsmlReasoner.getAllInstances();
		assertEquals(10, set.size());
	}

	public void testGetAllAttributes() {
		Set<IRI> set = wsmlReasoner.getAllAttributes();
		assertEquals(20, set.size());
	}

	public void testGetAllConstraintAttributes() {
		Set<IRI> set = wsmlReasoner.getAllConstraintAttributes();
		assertEquals(4, set.size());
	}

	public void testGetAllInferenceAttributes() {
		Set<IRI> set = wsmlReasoner.getAllInferenceAttributes();
		assertEquals(16, set.size());
	}

	public void testSubConcepts() {
		Set<Concept> set = wsmlReasoner.getSubConcepts(wsmoFactory
				.createConcept(wsmoFactory.createIRI(ns + "Human")));
		assertEquals(6, set.size());
	}

	public void testSuperConcepts() {
		Set<Concept> set = wsmlReasoner.getSuperConcepts(wsmoFactory
				.createConcept(wsmoFactory.createIRI(ns + "Human")));
		assertEquals(2, set.size());
	}

	public void testGetEquivalentConcepts() {
		Set<Concept> set = wsmlReasoner.getEquivalentConcepts(wsmoFactory
				.createConcept(wsmoFactory.createIRI(ns + "Human")));
		assertEquals(1, set.size());
	}

	public void testIsEquivalentConcept() {

		assertTrue(wsmlReasoner
				.isEquivalentConcept(wsmoFactory.createConcept(wsmoFactory
						.createIRI(ns + "Human")), wsmoFactory
						.createConcept(wsmoFactory.createIRI(ns + "Person"))));
	}

	public void testIsNotEquivalentConcept() {

		assertFalse(wsmlReasoner
				.isEquivalentConcept(wsmoFactory.createConcept(wsmoFactory
						.createIRI(ns + "Human")), wsmoFactory
						.createConcept(wsmoFactory.createIRI(ns + "Animal"))));
	}

	public void testIsSubConceptOf() {

		assertTrue(wsmlReasoner.isSubConceptOf(wsmoFactory
				.createConcept(wsmoFactory.createIRI(ns + "Woman")),
				wsmoFactory.createConcept(wsmoFactory.createIRI(ns + "Human"))));
	}

	public void testIsNotSubConceptOf() {

		assertFalse(wsmlReasoner.isSubConceptOf(wsmoFactory
				.createConcept(wsmoFactory.createIRI(ns + "Animal")),
				wsmoFactory.createConcept(wsmoFactory.createIRI(ns + "Human"))));
	}

	public void testIsMemberOf() {

		assertTrue(wsmlReasoner.isMemberOf(wsmoFactory
				.createInstance(wsmoFactory.createIRI(ns + "Mary")),
				wsmoFactory.createConcept(wsmoFactory.createIRI(ns + "Woman"))));
	}

	public void testIsNotMemberOf() {

		assertFalse(wsmlReasoner.isMemberOf(wsmoFactory
				.createInstance(wsmoFactory.createIRI(ns + "Mary")),
				wsmoFactory.createConcept(wsmoFactory.createIRI(ns + "Child"))));
	}

	public void testDirectConceptsOf() {

		Set<Concept> set = wsmlReasoner.getDirectConcepts(wsmoFactory
				.createInstance(wsmoFactory.createIRI(ns + "Mary")));

		assertEquals(2, set.size());
	}

	public void testAllConceptsOf() {

		Set<Concept> set = wsmlReasoner.getConcepts(wsmoFactory
				.createInstance(wsmoFactory.createIRI(ns + "Mary")));
		assertEquals(6, set.size());
	}

	public void testAllInstancesOf() {
		Set<Instance> set = wsmlReasoner.getInstances(wsmoFactory
				.createConcept(wsmoFactory.createIRI(ns + "Woman")));
		assertEquals(4, set.size());
	}

	public void testGetSubRelations() {
		Set<IRI> set = wsmlReasoner.getSubRelations(wsmoFactory.createIRI(ns
				+ "hasParent"));
		assertEquals(1, set.size());
	}

	public void testGetSuperRealtionOf() {

		Set<IRI> set = wsmlReasoner.getSuperRelations(wsmoFactory.createIRI(ns
				+ "hasMother"));
		assertEquals(2, set.size());
	}

	public void testEquivalentRelations() {
		Set<IRI> set = wsmlReasoner.getEquivalentRelations(wsmoFactory
				.createIRI(ns + "hasHolder"));
		assertEquals(1, set.size());
	}

	public void testInverseRelations() {
		Set<IRI> set = wsmlReasoner.getInverseRelations(wsmoFactory
				.createIRI(ns + "hasChild"));
		assertEquals(1, set.size());
	}

	public void testGetConceptsOfAttribute() {
		Set<Concept> set = wsmlReasoner.getConceptsOf(wsmoFactory.createIRI(ns
				+ "isFatherOf"));
		assertEquals(1, set.size());
	}

	public void testGetRangesOfInferingAttribute() {
		Set<IRI> set = wsmlReasoner.getRangesOfInferingAttribute(wsmoFactory
				.createIRI(ns + "isFatherOf"));
		assertEquals(1, set.size());
	}

	public void testGetRangesOfConstraintAttribute() {
		Set<IRI> set = wsmlReasoner.getRangesOfConstraintAttribute(wsmoFactory
				.createIRI(ns + "hasWeight"));
		assertEquals(2, set.size());
	}

	public void testGetValuesOfInferingAttributeOfASpecifiedInstance() {
		Set<Entry<IRI, Set<Term>>> entrySet = wsmlReasoner
				.getInferingAttributeValues(
						wsmoFactory.createInstance(wsmoFactory.createIRI(ns
								+ "Mary"))).entrySet();
		assertEquals(2, entrySet.size());
	}

	public void testGetValuesOfConstraintAttributeOfASpecifiedInstance() {
		Set<Entry<IRI, Set<Term>>> entrySetTerm = wsmlReasoner
				.getConstraintAttributeValues(
						wsmoFactory.createInstance(wsmoFactory.createIRI(ns
								+ "Mary"))).entrySet();
		assertEquals(3, entrySetTerm.size());

	}

	public void testGetInstancesAndValuesOfASpecifiedInferingAttribute() {
		Set<Entry<Instance, Set<Term>>> entrySet = wsmlReasoner
				.getInferingAttributeInstances(
						wsmoFactory.createIRI(ns + "hasChild")).entrySet();
		assertEquals(3, entrySet.size());

	}

	public void testGetInstancesAndValuesOfASpecifiedConstraintAttribute() {
		Set<Entry<Instance, Set<Term>>> entrySetTerm = wsmlReasoner
				.getConstraintAttributeInstances(
						wsmoFactory.createIRI(ns + "ageOfHuman")).entrySet();
		assertEquals(3, entrySetTerm.size());
	}

	public void testGetInferingAttributeValue() {
		assertTrue(wsmlReasoner.getInferingAttributeValues(
				wsmoFactory.createInstance(wsmoFactory.createIRI(ns + "Mary")),
				wsmoFactory.createIRI(ns + "hasChild")).contains(
				wsmoFactory.createIRI(ns + "Jack")));
	}

	public void testGetInferingAttributeValueFalse() {
		assertFalse(wsmlReasoner.getInferingAttributeValues(
				wsmoFactory.createInstance(wsmoFactory.createIRI(ns + "Mary")),
				wsmoFactory.createIRI(ns + "hasChild")).contains(
				wsmoFactory.createIRI(ns + "Bob")));
	}

	public void testGetConstraintAttributeValue() {
		assertTrue(wsmlReasoner.getConstraintAttributeValues(
				wsmoFactory.createInstance(wsmoFactory.createIRI(ns + "Mary")),
				wsmoFactory.createIRI(ns + "ageOfHuman")).contains("33"));
	}

	public void testGetConstraintAttributeValueFalse() {
		assertFalse(wsmlReasoner.getConstraintAttributeValues(
				wsmoFactory.createInstance(wsmoFactory.createIRI(ns + "Mary")),
				wsmoFactory.createIRI(ns + "ageOfHuman")).contains("40"));
	}

	public void testGetInferingAttributeValues() {
		Set<IRI> set = wsmlReasoner.getInferingAttributeValues(wsmoFactory
				.createInstance(wsmoFactory.createIRI(ns + "Clare")),
				wsmoFactory.createIRI(ns + "hasChild"));

		assertEquals(2, set.size());
	}

	public void testGetConstraintAttributeValues() {
		Set<String> set = wsmlReasoner.getConstraintAttributeValues(wsmoFactory
				.createInstance(wsmoFactory.createIRI(ns + "Mary")),
				wsmoFactory.createIRI(ns + "hasWeight"));

		assertEquals(1, set.size());

	}

}
