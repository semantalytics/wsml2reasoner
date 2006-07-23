/**
 * WSML Reasoner Implementation.
 *
 * Copyright (c) 2005, FZI, Germany.
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
package reasoner.dl;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.Map.Entry;

import org.mindswap.pellet.exceptions.InconsistentOntologyException;
import org.omwg.ontology.Concept;
import org.omwg.ontology.Instance;
import org.omwg.ontology.Ontology;
import org.wsml.reasoner.api.InternalReasonerException;
import org.wsml.reasoner.api.WSMLDLReasoner;
import org.wsml.reasoner.impl.DefaultWSMLReasonerFactory;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsmo.common.IRI;
import org.wsmo.common.Identifier;
import org.wsmo.factory.DataFactory;
import org.wsmo.factory.Factory;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.Parser;

import junit.framework.TestCase;


public class SimpleInferenceTests extends TestCase {

	private WsmoFactory wsmoFactory;
	
	private LogicalExpressionFactory leFactory;
	
	private DataFactory dataFactory;

    private WSMLDLReasoner wsmlReasoner;

    private Parser parser; 
    
    private Ontology ontology;
    
    private String ns;
    
	protected void setUp() throws Exception {
		super.setUp();
		WSMO4JManager wsmoManager = new WSMO4JManager();
        wsmoFactory = wsmoManager.getWSMOFactory();
        leFactory = wsmoManager.getLogicalExpressionFactory();
        dataFactory = wsmoManager.getDataFactory();
        wsmlReasoner = DefaultWSMLReasonerFactory.getFactory().createWSMLDLReasoner();
		parser = Factory.createParser(null);
		
		// read test file and parse it 
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(
                "reasoner/dl/wsml2owlExample.wsml");
        assertNotNull(is);
        // assuming first topentity in file is an ontology  
        ontology = (Ontology)parser.parse(new InputStreamReader(is))[0]; 
        ns = ontology.getDefaultNamespace().getIRI().toString();
		
        // register ontology at the wsml reasoner
        wsmlReasoner.registerOntology(ontology);       
	}
	
	public void testAll() throws Exception {
		// test ontology satisfiability
		assertTrue(wsmlReasoner.isSatisfiable(null));
		
		// test conceptIsNotConsistent
		assertFalse(wsmlReasoner.isConsistent(
				wsmoFactory.createConcept(wsmoFactory.createIRI(ns + "Machine"))));
		
		// test conceptIsConsistent
		assertTrue(wsmlReasoner.isConsistent(
				wsmoFactory.createConcept(wsmoFactory.createIRI(ns + "Woman"))));
		
		// test logicalExpressionIsNotConsistent with a MembershipMolecule
		assertFalse(wsmlReasoner.isConsistent(leFactory.createLogicalExpression(
				"?x memberOf _\"http://www.example.org/ontologies/example#Machine\".")));
		
		// test logicalExpressionIsConsistent with a MembershipMolecule
		assertTrue(wsmlReasoner.isConsistent(leFactory.createLogicalExpression(
				"?x memberOf _\"http://www.example.org/ontologies/example#Woman\".")));
		
		// test logicalExpressionIsConsistent with a Conjunction
		assertTrue(wsmlReasoner.isConsistent(leFactory.createLogicalExpression(
				"?x memberOf _\"http://www.example.org/ontologies/example#Pet\" " +
				"and ?x memberOf _\"http://www.example.org/ontologies/example#DomesticAnimal\".")));
		
		// test logicalExpressionIsNotConsistent with a Conjunction
		assertFalse(wsmlReasoner.isConsistent(leFactory.createLogicalExpression(
				"?x memberOf _\"http://www.example.org/ontologies/example#Man\" " +
				"and ?x memberOf _\"http://www.example.org/ontologies/example#Woman\".")));
		
		// test logicalExpression not supported for consistency check
		try {
			wsmlReasoner.isConsistent(leFactory.createLogicalExpression(
					"?x[_\"http://www.example.org/ontologies/example#hasAge\" hasValue 33]."));
			fail("Should fail because this logical expression is not supported for the " +
					"consistency check");
		} catch (InternalReasonerException e) {
			e.getMessage();
		};
		
		// test getAllConcepts
		Set<Concept> set = wsmlReasoner.getAllConcepts();
		for (Concept concept : set) 
//			System.out.println(concept.getIdentifier().toString());
		assertTrue(set.size() == 17);
		
		// test getAllInstances
		Set<Instance> set2 = wsmlReasoner.getAllInstances();
		for (Instance instance : set2) 
//			System.out.println(instance.getIdentifier().toString());
		assertTrue(set2.size() == 10);
		
		// test getAllAttributes
		Set<IRI> set3 = wsmlReasoner.getAllAttributes();
		for (IRI attributeId : set3) 
//			System.out.println(attributeId.toString());
		assertTrue(set3.size() == 19);
		
		// test getAllConstraintAttributes
		set3.clear();
		set3 = wsmlReasoner.getAllConstraintAttributes();
		for (IRI attributeId : set3) 
//			System.out.println(attributeId.toString());
		assertTrue(set3.size() == 4);
		
		// test getAllInferenceAttributes
		set3.clear();
		set3 = wsmlReasoner.getAllInferenceAttributes();
		for (IRI attributeId : set3) 
//			System.out.println(attributeId.toString());
		assertTrue(set3.size() == 15);
		
		// test getSubConcepts
		set.clear();
		set = wsmlReasoner.getSubConcepts(null, 
				ontology.findConcept(wsmoFactory.createIRI(ns + "Human")));
		for (Concept concept : set)
//			System.out.println(concept.getIdentifier().toString());
		assertTrue(set.size() == 6);
		
		// test getSuperConcepts
		set.clear();
		set = wsmlReasoner.getSuperConcepts(null,
				ontology.findConcept(wsmoFactory.createIRI(ns + "Human")));
		for (Concept concept : set)
//			System.out.println(concept.getIdentifier().toString());
		assertTrue(set.size() == 2);
		
		// test getEquivalentConcepts
		set.clear();
		set = wsmlReasoner.getEquivalentConcepts(
				ontology.findConcept(wsmoFactory.createIRI(ns + "Human")));
		for (Concept concept : set)
//			System.out.println(concept.getIdentifier().toString());
		assertTrue(set.size() == 1);
		
		// test isEquivalentConcept
		assertTrue(wsmlReasoner.isEquivalentConcept(
				ontology.findConcept(wsmoFactory.createIRI(ns + "Human")),
				wsmoFactory.createConcept(wsmoFactory.createIRI(ns + "Person"))));
		
		// test isNotEquivalentConcept
		assertFalse(wsmlReasoner.isEquivalentConcept(
				ontology.findConcept(wsmoFactory.createIRI(ns + "Human")),
				ontology.findConcept(wsmoFactory.createIRI(ns + "Animal"))));
		
		// test isSubConceptOf
		assertTrue(wsmlReasoner.isSubConceptOf(
				null, 
				ontology.findConcept(wsmoFactory.createIRI(ns + "Woman")), 
				ontology.findConcept(wsmoFactory.createIRI(ns + "Human"))));
		
		// test isNotSubConceptOf
		assertFalse(wsmlReasoner.isSubConceptOf(
				null, 
				ontology.findConcept(wsmoFactory.createIRI(ns + "Animal")), 
				ontology.findConcept(wsmoFactory.createIRI(ns + "Human"))));
		
		// test isMemberOf
		assertTrue(wsmlReasoner.isMemberOf(
				null, 
				ontology.findInstance(wsmoFactory.createIRI(ns + "Mary")), 
				ontology.findConcept(wsmoFactory.createIRI(ns + "Woman"))));
		
		// test isNotMemberOf
		assertFalse(wsmlReasoner.isMemberOf(
				null, 
				ontology.findInstance(wsmoFactory.createIRI(ns + "Mary")), 
				ontology.findConcept(wsmoFactory.createIRI(ns + "Child"))));
		
		// test allConceptsOf
		set.clear();
		set = wsmlReasoner.getConcepts(
				null,
				ontology.findInstance(wsmoFactory.createIRI(ns + "Mary")));
		for(Concept concept : set)
//			System.out.println(concept.getIdentifier().toString());
		assertTrue(set.size() == 5);
		
		// test allInstancesOf
		set2.clear();
		set2 = wsmlReasoner.getInstances(
				null,
				ontology.findConcept(wsmoFactory.createIRI(ns + "Woman")));
		for(Instance instance : set2)
//			System.out.println(instance.getIdentifier().toString());
		assertTrue(set2.size() == 4);
		
		// test getSubRelationOf
		set3.clear();
		set3 = wsmlReasoner.getSubRelations( 
				wsmoFactory.createIRI(ns + "hasParent"));
		for (IRI attributeId : set3)
//			System.out.println(attributeId.toString());
		assertTrue(set3.size() == 1);
		
		// test getSuperRelationOf
		set3.clear();
		set3 = wsmlReasoner.getSuperRelations(
				wsmoFactory.createIRI(ns + "hasMother"));
		for (IRI attributeId : set3)
//			System.out.println(attributeId.toString());
		assertTrue(set3.size() == 1);
		
		// test EquivalentRelations
		set3.clear();
		set3 = wsmlReasoner.getEquivalentRelations(
				wsmoFactory.createIRI(ns + "hasHolder"));
		for (IRI attributeId : set3)
//			System.out.println(attributeId.toString());
		assertTrue(set3.size() == 1);
		
		// test InverseRelations
		set3.clear();
		set3 = wsmlReasoner.getInverseRelations(
				wsmoFactory.createIRI(ns + "hasChild"));
		for (IRI attributeId : set3)
//			System.out.println(attributeId.toString());
		assertTrue(set3.size() == 1);
	
		// test getConceptsOfAttribute
		set.clear();
		set = wsmlReasoner.getConceptsOf(
				wsmoFactory.createIRI(ns + "isFatherOf"));
		for (Concept concept : set)
//			System.out.println(concept.getIdentifier().toString());
		assertTrue(set.size() == 1);
		
		// test getRangesOfInferingAttribute
		set3.clear();
		set3 = wsmlReasoner.getRangesOfInferingAttribute(
				wsmoFactory.createIRI(ns + "isFatherOf"));
		for (Identifier rangeId : set3)
//			System.out.println(rangeId.toString());
		assertTrue(set3.size() == 1);
		
		// test getRangesOfConstraintAttribute
		set3.clear();
		set3 = wsmlReasoner.getRangesOfConstraintAttribute(
				wsmoFactory.createIRI(ns + "hasWeight"));
		for (Identifier rangeId : set3)
//			System.out.println(rangeId.toString());
		assertTrue(set3.size() == 2);
		
		// test getValuesOfInferingAttribute of a specified instance
		Set<Entry<IRI, Set<IRI>>> entrySet = wsmlReasoner.getInferingAttributeValues(
				wsmoFactory.createInstance(wsmoFactory.createIRI(ns + "Mary"))).entrySet();
		for (Entry<IRI, Set<IRI>> entry : entrySet) {
//			System.out.println(entry.getKey().toString());
			Set<IRI> IRIset = entry.getValue();
//			for (IRI value : IRIset) 
//				System.out.println("value: " + value.toString());
		}
		assertTrue(entrySet.size() == 2);
		
		// test getValuesOfConstraintAttribute of a specified instance
		entrySet.clear();
		entrySet = wsmlReasoner.getConstraintAttributeValues(
				wsmoFactory.createInstance(wsmoFactory.createIRI(ns + "Mary"))).entrySet();
		for (Entry<IRI, Set<IRI>> entry : entrySet) {
//			System.out.println(entry.getKey().toString());
			Set<IRI> IRIset = entry.getValue();
//			for (IRI value : IRIset) 
//				System.out.println("value: " + value.toString());
		}
		assertTrue(entrySet.size() == 4);
		
		// test get instances and values of a specified infering attribute
		Set<Entry<Instance, Set<IRI>>> entrySet2 = wsmlReasoner.
				getInferingAttributeInstances(
				wsmoFactory.createIRI(ns + "hasChild")).entrySet();
		for (Entry<Instance, Set<IRI>> entry : entrySet2) {
//			System.out.println(entry.getKey().getIdentifier().toString());
			Set<IRI> IRIset = entry.getValue();
//			for (IRI value : IRIset) 
//				System.out.println("value: " + value.toString());
		}
		assertTrue(entrySet2.size() == 3);
		
		// test get instances and values of a specified constraint attribute
		entrySet2.clear();
		entrySet2 = wsmlReasoner.getConstraintAttributeInstances(
				wsmoFactory.createIRI(ns + "ageOfHuman")).entrySet();
		for (Entry<Instance, Set<IRI>> entry : entrySet2) {
//			System.out.println(entry.getKey().getIdentifier().toString());
			Set<IRI> IRIset = entry.getValue();
//			for (IRI value : IRIset) 
//				System.out.println("value: " + value.toString());
		}
		assertTrue(entrySet2.size() == 3);
		
		// test isInstanceHavingInferingAttributeValue
		assertTrue(wsmlReasoner.instanceHasInferingAttributeValue(
				wsmoFactory.createInstance(wsmoFactory.createIRI(ns + "Mary")),
				wsmoFactory.createIRI(ns + "hasChild"),
				wsmoFactory.createInstance(wsmoFactory.createIRI(ns + "Jack"))));
		
		// test isInstanceNotHavingInferingAttributeValue
		assertFalse(wsmlReasoner.instanceHasInferingAttributeValue(
				wsmoFactory.createInstance(wsmoFactory.createIRI(ns + "Mary")),
				wsmoFactory.createIRI(ns + "hasChild"),
				wsmoFactory.createInstance(wsmoFactory.createIRI(ns + "Bob"))));
		
		// test isInstanceHavingConstraintAttributeValue
		assertTrue(wsmlReasoner.instanceHasConstraintAttributeValue(
				wsmoFactory.createInstance(wsmoFactory.createIRI(ns + "Mary")),
				wsmoFactory.createIRI(ns + "hasName"),
				dataFactory.createWsmlString("Mary Jones")));
		
		// test isInstanceNotHavingConstraintAttributeValue
		assertFalse(wsmlReasoner.instanceHasConstraintAttributeValue(
				wsmoFactory.createInstance(wsmoFactory.createIRI(ns + "Mary")),
				wsmoFactory.createIRI(ns + "hasWeight"),
				dataFactory.createWsmlInteger("60")));
		
		// test getInferingAttributeValue
		assertTrue(wsmlReasoner.getInferingAttributeValue(
				wsmoFactory.createInstance(wsmoFactory.createIRI(ns + "Mary")),
				wsmoFactory.createIRI(ns + "hasChild")) == 
					wsmoFactory.createInstance(wsmoFactory.createIRI(ns + "Jack")));
		
		// test getInferingAttributeValue false
		assertFalse(wsmlReasoner.getInferingAttributeValue(
				wsmoFactory.createInstance(wsmoFactory.createIRI(ns + "Mary")),
				wsmoFactory.createIRI(ns + "hasChild")) == 
					wsmoFactory.createInstance(wsmoFactory.createIRI(ns + "Bob")));

		// test getConstraintAttributeValue
		assertTrue(wsmlReasoner.getConstraintAttributeValue(
				wsmoFactory.createInstance(wsmoFactory.createIRI(ns + "Mary")),
				wsmoFactory.createIRI(ns + "ageOfHuman")).equals("33"));
		
		// test getConstraintAttributeValue false
		assertFalse(wsmlReasoner.getConstraintAttributeValue(
				wsmoFactory.createInstance(wsmoFactory.createIRI(ns + "Mary")),
				wsmoFactory.createIRI(ns + "ageOfHuman")).equals("40"));
		
		// test getInferingAttributeValues
		set2.clear();
		set2 = wsmlReasoner.getInferingAttributeValues(
				wsmoFactory.createInstance(wsmoFactory.createIRI(ns + "Clare")),
				wsmoFactory.createIRI(ns + "hasChild"));
		for(Instance instance : set2)
//			System.out.println(instance.getIdentifier().toString());
		assertTrue(set2.size() == 2);

		// test getConstraintAttributeValues
		Set<String> set4 = wsmlReasoner.getConstraintAttributeValues(
				wsmoFactory.createInstance(wsmoFactory.createIRI(ns + "Mary")),
				wsmoFactory.createIRI(ns + "hasWeight"));
		for(String dataValue : set4)
//			System.out.println(dataValue);
		assertTrue(set4.size() == 2);
	}
	
	// test inconsistent ontology
	public void testInconsistentOntology() throws Exception {
		wsmlReasoner.deRegisterOntology((IRI) ontology.getIdentifier());
		// read test file and parse it 
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(
                "reasoner/dl/inconsistentWsml2owlExample.wsml");
        assertNotNull(is);
        // assuming first topentity in file is an ontology  
        ontology = (Ontology)parser.parse(new InputStreamReader(is))[0]; 
		try {
			// register ontology at the wsml reasoner
			wsmlReasoner.registerOntology(ontology);
			fail("Should fail because the given ontology is inconsistent");
		} catch (InconsistentOntologyException e) {
			e.getMessage();		
		}
		wsmlReasoner.deRegisterOntology((IRI) ontology.getIdentifier());
	}
	
}
/*
 * $Log: not supported by cvs2svn $
 * Revision 1.2  2006/07/21 16:25:21  nathalie
 * completing the pellet reasoner integration
 *
 *
 *
 */
