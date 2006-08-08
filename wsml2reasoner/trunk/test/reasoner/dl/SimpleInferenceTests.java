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
	}
	
	public void testAll() throws Exception {
		// read test file and parse it 
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(
                "reasoner/dl/wsml2owlExample.wsml");
        assertNotNull(is);
        // assuming first topentity in file is an ontology  
        ontology = (Ontology)parser.parse(new InputStreamReader(is))[0]; 
        ns = ontology.getDefaultNamespace().getIRI().toString();
		
        // register ontology at the wsml reasoner
        wsmlReasoner.registerOntology(ontology);       
        
		// test ontology satisfiability
		assertTrue(wsmlReasoner.isSatisfiable((IRI) ontology.getIdentifier()));
		
		// test conceptIsNotConsistent
		assertFalse(wsmlReasoner.isConsistent((IRI) ontology.getIdentifier(), 
				wsmoFactory.createConcept(wsmoFactory.createIRI(ns + "Machine"))));
		
		// test conceptIsConsistent
		assertTrue(wsmlReasoner.isConsistent((IRI) ontology.getIdentifier(), 
				wsmoFactory.createConcept(wsmoFactory.createIRI(ns + "Woman"))));
		
		// test logicalExpressionIsNotConsistent with a MembershipMolecule
		assertFalse(wsmlReasoner.isConsistent((IRI) ontology.getIdentifier(), 
				leFactory.createLogicalExpression(
				"?x memberOf _\"http://www.example.org/ontologies/example#Machine\".")));
		
		// test logicalExpressionIsConsistent with a MembershipMolecule
		assertTrue(wsmlReasoner.isConsistent((IRI) ontology.getIdentifier(), 
				leFactory.createLogicalExpression(
				"?x memberOf _\"http://www.example.org/ontologies/example#Woman\".")));
		
		// test logicalExpressionIsConsistent with a Conjunction
		assertTrue(wsmlReasoner.isConsistent((IRI) ontology.getIdentifier(), 
				leFactory.createLogicalExpression(
				"?x memberOf _\"http://www.example.org/ontologies/example#Pet\" " +
				"and ?x memberOf _\"http://www.example.org/ontologies/example#DomesticAnimal\".")));
		
		// test logicalExpressionIsNotConsistent with a Conjunction
		assertFalse(wsmlReasoner.isConsistent((IRI) ontology.getIdentifier(), 
				leFactory.createLogicalExpression(
				"?x memberOf _\"http://www.example.org/ontologies/example#Man\" " +
				"and ?x memberOf _\"http://www.example.org/ontologies/example#Woman\".")));
		
		// test logicalExpression not supported for consistency check
		try {
			wsmlReasoner.isConsistent((IRI) ontology.getIdentifier(), 
					leFactory.createLogicalExpression(
					"?x[_\"http://www.example.org/ontologies/example#hasAge\" hasValue 33]."));
			fail("Should fail because this logical expression is not supported for the " +
					"consistency check");
		} catch (InternalReasonerException e) {
			e.getMessage();
		};
		
		// test getAllConcepts
		Set<Concept> set = wsmlReasoner.getAllConcepts((IRI) ontology.getIdentifier());
		for (Concept concept : set) 
//			System.out.println(concept.getIdentifier().toString());
		assertTrue(set.size() == 17);
		
		// test getAllInstances
		Set<Instance> set2 = wsmlReasoner.getAllInstances((IRI) ontology.getIdentifier());
		for (Instance instance : set2) 
//			System.out.println(instance.getIdentifier().toString());
		assertTrue(set2.size() == 10);
		
		// test getAllAttributes
		Set<IRI> set3 = wsmlReasoner.getAllAttributes((IRI) ontology.getIdentifier());
		for (IRI attributeId : set3) 
//			System.out.println(attributeId.toString());
		assertTrue(set3.size() == 19);
		
		// test getAllConstraintAttributes
		set3.clear();
		set3 = wsmlReasoner.getAllConstraintAttributes((IRI) ontology.getIdentifier());
		for (IRI attributeId : set3) 
//			System.out.println(attributeId.toString());
		assertTrue(set3.size() == 4);
		
		// test getAllInferenceAttributes
		set3.clear();
		set3 = wsmlReasoner.getAllInferenceAttributes((IRI) ontology.getIdentifier());
		for (IRI attributeId : set3) 
//			System.out.println(attributeId.toString());
		assertTrue(set3.size() == 15);
		
		// test getSubConcepts
		set.clear();
		set = wsmlReasoner.getSubConcepts((IRI) ontology.getIdentifier(),  
				ontology.findConcept(wsmoFactory.createIRI(ns + "Human")));
		for (Concept concept : set)
//			System.out.println(concept.getIdentifier().toString());
		assertTrue(set.size() == 6);
		
		// test getSuperConcepts
		set.clear();
		set = wsmlReasoner.getSuperConcepts((IRI) ontology.getIdentifier(), 
				ontology.findConcept(wsmoFactory.createIRI(ns + "Human")));
		for (Concept concept : set)
//			System.out.println(concept.getIdentifier().toString());
		assertTrue(set.size() == 2);
		
		// test getEquivalentConcepts
		set.clear();
		set = wsmlReasoner.getEquivalentConcepts((IRI) ontology.getIdentifier(), 
				ontology.findConcept(wsmoFactory.createIRI(ns + "Human")));
		for (Concept concept : set)
//			System.out.println(concept.getIdentifier().toString());
		assertTrue(set.size() == 1);
		
		// test isEquivalentConcept
		assertTrue(wsmlReasoner.isEquivalentConcept((IRI) ontology.getIdentifier(), 
				ontology.findConcept(wsmoFactory.createIRI(ns + "Human")),
				wsmoFactory.createConcept(wsmoFactory.createIRI(ns + "Person"))));
		
		// test isNotEquivalentConcept
		assertFalse(wsmlReasoner.isEquivalentConcept((IRI) ontology.getIdentifier(), 
				ontology.findConcept(wsmoFactory.createIRI(ns + "Human")),
				ontology.findConcept(wsmoFactory.createIRI(ns + "Animal"))));
		
		// test isSubConceptOf
		assertTrue(wsmlReasoner.isSubConceptOf(
				(IRI) ontology.getIdentifier(), 
				ontology.findConcept(wsmoFactory.createIRI(ns + "Woman")), 
				ontology.findConcept(wsmoFactory.createIRI(ns + "Human"))));
		
		// test isNotSubConceptOf
		assertFalse(wsmlReasoner.isSubConceptOf(
				(IRI) ontology.getIdentifier(), 
				ontology.findConcept(wsmoFactory.createIRI(ns + "Animal")), 
				ontology.findConcept(wsmoFactory.createIRI(ns + "Human"))));
		
		// test isMemberOf
		assertTrue(wsmlReasoner.isMemberOf(
				(IRI) ontology.getIdentifier(), 
				ontology.findInstance(wsmoFactory.createIRI(ns + "Mary")), 
				ontology.findConcept(wsmoFactory.createIRI(ns + "Woman"))));
		
		// test isNotMemberOf
		assertFalse(wsmlReasoner.isMemberOf(
				(IRI) ontology.getIdentifier(), 
				ontology.findInstance(wsmoFactory.createIRI(ns + "Mary")), 
				ontology.findConcept(wsmoFactory.createIRI(ns + "Child"))));
		
		// test allConceptsOf
		set.clear();
		set = wsmlReasoner.getConcepts(
				(IRI) ontology.getIdentifier(), 
				ontology.findInstance(wsmoFactory.createIRI(ns + "Mary")));
		for(Concept concept : set)
//			System.out.println(concept.getIdentifier().toString());
		assertTrue(set.size() == 5);
		
		// test allInstancesOf
		set2.clear();
		set2 = wsmlReasoner.getInstances(
				(IRI) ontology.getIdentifier(), 
				ontology.findConcept(wsmoFactory.createIRI(ns + "Woman")));
		for(Instance instance : set2)
//			System.out.println(instance.getIdentifier().toString());
		assertTrue(set2.size() == 4);
		
		// test getSubRelationOf
		set3.clear();
		set3 = wsmlReasoner.getSubRelations((IRI) ontology.getIdentifier(), 
				wsmoFactory.createIRI(ns + "hasParent"));
		for (IRI attributeId : set3)
//			System.out.println(attributeId.toString());
		assertTrue(set3.size() == 1);
		
		// test getSuperRelationOf
		set3.clear();
		set3 = wsmlReasoner.getSuperRelations((IRI) ontology.getIdentifier(), 
				wsmoFactory.createIRI(ns + "hasMother"));
		for (IRI attributeId : set3)
//			System.out.println(attributeId.toString());
		assertTrue(set3.size() == 1);
		
		// test EquivalentRelations
		set3.clear();
		set3 = wsmlReasoner.getEquivalentRelations((IRI) ontology.getIdentifier(), 
				wsmoFactory.createIRI(ns + "hasHolder"));
		for (IRI attributeId : set3)
//			System.out.println(attributeId.toString());
		assertTrue(set3.size() == 1);
		
		// test InverseRelations
		set3.clear();
		set3 = wsmlReasoner.getInverseRelations((IRI) ontology.getIdentifier(), 
				wsmoFactory.createIRI(ns + "hasChild"));
		for (IRI attributeId : set3)
//			System.out.println(attributeId.toString());
		assertTrue(set3.size() == 1);
	
		// test getConceptsOfAttribute
		set.clear();
		set = wsmlReasoner.getConceptsOf((IRI) ontology.getIdentifier(), 
				wsmoFactory.createIRI(ns + "isFatherOf"));
		for (Concept concept : set)
//			System.out.println(concept.getIdentifier().toString());
		assertTrue(set.size() == 1);
		
		// test getRangesOfInferingAttribute
		set3.clear();
		set3 = wsmlReasoner.getRangesOfInferingAttribute((IRI) ontology.getIdentifier(), 
				wsmoFactory.createIRI(ns + "isFatherOf"));
		for (Identifier rangeId : set3)
//			System.out.println(rangeId.toString());
		assertTrue(set3.size() == 1);
		
		// test getRangesOfConstraintAttribute
		set3.clear();
		set3 = wsmlReasoner.getRangesOfConstraintAttribute((IRI) ontology.getIdentifier(), 
				wsmoFactory.createIRI(ns + "hasWeight"));
		for (Identifier rangeId : set3)
//			System.out.println(rangeId.toString());
		assertTrue(set3.size() == 2);
		
		// test getValuesOfInferingAttribute of a specified instance
		Set<Entry<IRI, Set<IRI>>> entrySet = wsmlReasoner.getInferingAttributeValues(
				(IRI) ontology.getIdentifier(), 
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
		entrySet = wsmlReasoner.getConstraintAttributeValues((IRI) ontology.getIdentifier(), 
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
				getInferingAttributeInstances((IRI) ontology.getIdentifier(), 
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
				(IRI) ontology.getIdentifier(), 
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
				(IRI) ontology.getIdentifier(), 
				wsmoFactory.createInstance(wsmoFactory.createIRI(ns + "Mary")),
				wsmoFactory.createIRI(ns + "hasChild"),
				wsmoFactory.createInstance(wsmoFactory.createIRI(ns + "Jack"))));
		
		// test isInstanceNotHavingInferingAttributeValue
		assertFalse(wsmlReasoner.instanceHasInferingAttributeValue(
				(IRI) ontology.getIdentifier(), 
				wsmoFactory.createInstance(wsmoFactory.createIRI(ns + "Mary")),
				wsmoFactory.createIRI(ns + "hasChild"),
				wsmoFactory.createInstance(wsmoFactory.createIRI(ns + "Bob"))));
		
		// test isInstanceHavingConstraintAttributeValue
		assertTrue(wsmlReasoner.instanceHasConstraintAttributeValue(
				(IRI) ontology.getIdentifier(), 
				wsmoFactory.createInstance(wsmoFactory.createIRI(ns + "Mary")),
				wsmoFactory.createIRI(ns + "hasName"),
				dataFactory.createWsmlString("Mary Jones")));
		
		// test isInstanceNotHavingConstraintAttributeValue
		assertFalse(wsmlReasoner.instanceHasConstraintAttributeValue(
				(IRI) ontology.getIdentifier(), 
				wsmoFactory.createInstance(wsmoFactory.createIRI(ns + "Mary")),
				wsmoFactory.createIRI(ns + "hasWeight"),
				dataFactory.createWsmlInteger("60")));
		
		// test getInferingAttributeValue
		assertTrue(wsmlReasoner.getInferingAttributeValue(
				(IRI) ontology.getIdentifier(), 
				wsmoFactory.createInstance(wsmoFactory.createIRI(ns + "Mary")),
				wsmoFactory.createIRI(ns + "hasChild")) == 
					wsmoFactory.createInstance(wsmoFactory.createIRI(ns + "Jack")));
		
		// test getInferingAttributeValue false
		assertFalse(wsmlReasoner.getInferingAttributeValue(
				(IRI) ontology.getIdentifier(), 
				wsmoFactory.createInstance(wsmoFactory.createIRI(ns + "Mary")),
				wsmoFactory.createIRI(ns + "hasChild")) == 
					wsmoFactory.createInstance(wsmoFactory.createIRI(ns + "Bob")));

		// test getConstraintAttributeValue
		assertTrue(wsmlReasoner.getConstraintAttributeValue(
				(IRI) ontology.getIdentifier(), 
				wsmoFactory.createInstance(wsmoFactory.createIRI(ns + "Mary")),
				wsmoFactory.createIRI(ns + "ageOfHuman")).equals("33"));
		
		// test getConstraintAttributeValue false
		assertFalse(wsmlReasoner.getConstraintAttributeValue(
				(IRI) ontology.getIdentifier(), 
				wsmoFactory.createInstance(wsmoFactory.createIRI(ns + "Mary")),
				wsmoFactory.createIRI(ns + "ageOfHuman")).equals("40"));
		
		// test getInferingAttributeValues
		set2.clear();
		set2 = wsmlReasoner.getInferingAttributeValues(
				(IRI) ontology.getIdentifier(), 
				wsmoFactory.createInstance(wsmoFactory.createIRI(ns + "Clare")),
				wsmoFactory.createIRI(ns + "hasChild"));
		for(Instance instance : set2)
//			System.out.println(instance.getIdentifier().toString());
		assertTrue(set2.size() == 2);

		// test getConstraintAttributeValues
		Set<String> set4 = wsmlReasoner.getConstraintAttributeValues(
				(IRI) ontology.getIdentifier(), 
				wsmoFactory.createInstance(wsmoFactory.createIRI(ns + "Mary")),
				wsmoFactory.createIRI(ns + "hasWeight"));
		for(String dataValue : set4)
//			System.out.println(dataValue);
		assertTrue(set4.size() == 2);
		
		wsmlReasoner.deRegisterOntology((IRI) ontology.getIdentifier());
	}
	
	// test inconsistent ontology
	public void testInconsistentOntology() throws Exception {
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
		} catch (RuntimeException e) {
			e.getMessage();		
		}
	}
	
}
/*
 * $Log: not supported by cvs2svn $
 * Revision 1.3  2006/07/23 15:20:23  nathalie
 * updated tests and testfiles
 *
 * Revision 1.2  2006/07/21 16:25:21  nathalie
 * completing the pellet reasoner integration
 *
 *
 *
 */
