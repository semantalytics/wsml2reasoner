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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import junit.framework.TestCase;

import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Concept;
import org.omwg.ontology.Instance;
import org.omwg.ontology.Ontology;
import org.wsml.reasoner.api.InternalReasonerException;
import org.wsml.reasoner.api.WSMLReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory.BuiltInReasoner;
import org.wsml.reasoner.api.inconsistency.InconsistencyException;
import org.wsml.reasoner.impl.DefaultWSMLReasonerFactory;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsmo.common.IRI;
import org.wsmo.common.Identifier;
import org.wsmo.factory.Factory;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.Parser;

/**
 * 
 * @author Nathalie Steinmetz, DERI Innsbruck
 *
 */
public class Kaon2SimpleInferenceTests extends TestCase {

	private WsmoFactory wsmoFactory;
	
	private LogicalExpressionFactory leFactory;

    private WSMLReasoner wsmlReasoner;

    private Parser parser;
    
    private Ontology ontology;
    
    private Map<String, Object> params;
    
    private String ns;
    
	protected void setUp() throws Exception {
		super.setUp();
		WSMO4JManager wsmoManager = new WSMO4JManager();
        wsmoFactory = wsmoManager.getWSMOFactory();
        leFactory = wsmoManager.getLogicalExpressionFactory();
        wsmlReasoner = null;
        params = new HashMap<String, Object>();
        params.put(DefaultWSMLReasonerFactory.PARAM_BUILT_IN_REASONER, BuiltInReasoner.KAON2);
		parser = Factory.createParser(null);
	}
	
	/**
     * @see TestCase#tearDown()
     */
    protected void tearDown(){
        leFactory=null;
        wsmoFactory=null;
        wsmlReasoner=null;
        parser=null;
        ontology=null;
        System.gc();
    }
	
	public void testAll() throws Exception {
		// read test file and parse it 
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(
                "reasoner/dl/kaon2Wsml2owlExample.wsml");
        assertNotNull(is);
        // assuming first topentity in file is an ontology  
        ontology = (Ontology)parser.parse(new InputStreamReader(is))[0]; 
        wsmlReasoner = DefaultWSMLReasonerFactory.getFactory().createWSMLDLReasoner(params);
        ns = ontology.getDefaultNamespace().getIRI().toString();
		
        // register ontology at the wsml reasoner
        wsmlReasoner.registerOntology(ontology);       
        
		// test ontology satisfiability
		assertTrue(wsmlReasoner.isSatisfiable((IRI) ontology.getIdentifier()));
		
		// test logicalExpressionIsNotConsistent with a concept
		assertFalse(wsmlReasoner.entails((IRI) ontology.getIdentifier(),
				leFactory.createLogicalExpression("Machine.", ontology)));
		
		// test logicalExpressionIsConsistent with a concept
		assertTrue(wsmlReasoner.entails((IRI) ontology.getIdentifier(),
				leFactory.createLogicalExpression("Woman.", ontology)));
		
		// test logicalExpressionIsNotConsistent with a MembershipMolecule
		assertFalse(wsmlReasoner.entails((IRI) ontology.getIdentifier(), 
				leFactory.createLogicalExpression("?x memberOf Machine.", 
						ontology)));
		
		// test logicalExpressionIsConsistent with a MembershipMolecule
		assertTrue(wsmlReasoner.entails((IRI) ontology.getIdentifier(), 
				leFactory.createLogicalExpression("?x memberOf Woman.", 
						ontology)));
		
		// test logicalExpressionIsConsistent with a Conjunction
		assertTrue(wsmlReasoner.entails((IRI) ontology.getIdentifier(), 
				leFactory.createLogicalExpression("?x memberOf Pet " +
						"and ?x memberOf DomesticAnimal.", ontology)));
		assertTrue(wsmlReasoner.entails((IRI) ontology.getIdentifier(), 
				leFactory.createLogicalExpression("Mary memberOf " +
						"Woman and Jack memberOf Child.", ontology)));
		assertTrue(wsmlReasoner.executeGroundQuery((IRI) ontology.getIdentifier(), 
				leFactory.createLogicalExpression("Mary memberOf " +
						"Woman and Jack memberOf Child.", ontology)));
		
		// test logicalExpressionIsNotConsistent with a Conjunction
		assertFalse(wsmlReasoner.entails((IRI) ontology.getIdentifier(), 
				leFactory.createLogicalExpression("?x memberOf Man " +
				"and ?x memberOf Woman.", ontology)));
		
		// test logicalExpression not supported for consistency check
		try {
			wsmlReasoner.entails((IRI) ontology.getIdentifier(), 
					leFactory.createLogicalExpression(
							"?x[hasAge hasValue 33].", ontology));
			fail("Should fail because this logical expression is not " +
					"supported for the consistency check");
		} catch (InternalReasonerException e) {
			e.getMessage();
		};
		
		// test getAllConcepts
		Set<Concept> set = wsmlReasoner.getAllConcepts((IRI) ontology.getIdentifier());
		for (Concept concept : set) 
//			System.out.println(concept.getIdentifier().toString());
		assertTrue(set.size() == 18);
		
		// test getAllInstances
		Set<Instance> set2 = wsmlReasoner.getAllInstances((IRI) ontology.getIdentifier());
		for (Instance instance : set2) 
//			System.out.println(instance.getIdentifier().toString());
		assertTrue(set2.size() == 10);
		
		// test getAllAttributes
		Set<IRI> set3 = wsmlReasoner.getAllAttributes((IRI) ontology.getIdentifier());
//		for (IRI attributeId : set3) 
//			System.out.println(attributeId.toString());
		assertTrue(set3.size() == 20);
		
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
		assertTrue(set3.size() == 16);
		
		// test getSubConcepts
		set.clear();
		set = wsmlReasoner.getSubConcepts((IRI) ontology.getIdentifier(),  
				ontology.findConcept(wsmoFactory.createIRI(ns + "Animal")));
		for (Concept concept : set)
//			System.out.println(concept.getIdentifier().toString());
		assertTrue(set.size() == 10);
		
//		 test getDirectSubConcepts
		set.clear();
		set = wsmlReasoner.getDirectSubConcepts((IRI) ontology.getIdentifier(),  
				ontology.findConcept(wsmoFactory.createIRI(ns + "Animal")));
		for (Concept concept : set)
//			System.out.println(concept.getIdentifier().toString());
		assertTrue(set.size() == 4);
		
		// test getSuperConcepts
		set.clear();
		set = wsmlReasoner.getSuperConcepts((IRI) ontology.getIdentifier(), 
				ontology.findConcept(wsmoFactory.createIRI(ns + "Child")));
		for (Concept concept : set)
//			System.out.println(concept.getIdentifier().toString());
		assertTrue(set.size() == 3);
		
		// test getDirectSuperConcepts
		set.clear();
		set = wsmlReasoner.getDirectSuperConcepts((IRI) ontology.getIdentifier(), 
				ontology.findConcept(wsmoFactory.createIRI(ns + "Child")));
		for (Concept concept : set)
//			System.out.println(concept.getIdentifier().toString());
		assertTrue(set.size() == 1);
		
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
		assertTrue(wsmlReasoner.executeGroundQuery((IRI) ontology.getIdentifier(),
				leFactory.createLogicalExpression("Woman subConceptOf Human.", 
						ontology)));
		
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
		assertTrue(wsmlReasoner.executeGroundQuery((IRI) ontology.getIdentifier(),
				leFactory.createLogicalExpression("Mary memberOf Woman.", 
						ontology)));
		
		// test isNotMemberOf
		assertFalse(wsmlReasoner.isMemberOf(
				(IRI) ontology.getIdentifier(), 
				ontology.findInstance(wsmoFactory.createIRI(ns + "Mary")), 
				ontology.findConcept(wsmoFactory.createIRI(ns + "Child"))));
		
		// test direct conceptsOf
		set.clear();
		set = wsmlReasoner.getDirectConcepts(
				(IRI) ontology.getIdentifier(), 
				ontology.findInstance(wsmoFactory.createIRI(ns + "Mary")));
		for(Concept concept : set)
//			System.out.println(concept.getIdentifier().toString());
		assertTrue(set.size() == 2);
		
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
				wsmoFactory.createIRI(ns + "hasRelative"));
		for (IRI attributeId : set3)
//			System.out.println(attributeId.toString());
		assertTrue(set3.size() == 2);
		
		// test getDirectSubRelationOf
		set3.clear();
		set3 = wsmlReasoner.getDirectSubRelations((IRI) ontology.getIdentifier(), 
				wsmoFactory.createIRI(ns + "hasRelative"));
		for (IRI attributeId : set3)
//			System.out.println(attributeId.toString());
		assertTrue(set3.size() == 1);
		
		// test getSuperRelationOf
		set3.clear();
		set3 = wsmlReasoner.getSuperRelations((IRI) ontology.getIdentifier(), 
				wsmoFactory.createIRI(ns + "hasMother"));
		for (IRI attributeId : set3)
//			System.out.println(attributeId.toString());
		assertTrue(set3.size() == 2);
		
		// test getDirectSuperRelationOf
		set3.clear();
		set3 = wsmlReasoner.getDirectSuperRelations((IRI) ontology.getIdentifier(), 
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
//				System.out.println("  value: " + value.toString());
		}
		assertTrue(entrySet.size() == 2);
		
		// test getValuesOfConstraintAttribute of a specified instance
        Set <Entry<IRI, Set<Term>>> entrySetTerm = 
				wsmlReasoner.getConstraintAttributeValues((IRI) ontology.getIdentifier(), 
				wsmoFactory.createInstance(wsmoFactory.createIRI(ns + "Mary"))).entrySet();
		for (Entry<IRI, Set<Term>> entry : entrySetTerm) {
//			System.out.println(entry.getKey().toString());
			Set<Term> termSet = entry.getValue();
//			for (Term value : termSet) 
//				System.out.println("  value: " + value.toString());
		}
		assertTrue(entrySetTerm.size() == 2);
		
		// test get instances and values of a specified infering attribute
		Set<Entry<Instance, Set<IRI>>> entrySet2 = wsmlReasoner.
				getInferingAttributeInstances((IRI) ontology.getIdentifier(), 
				wsmoFactory.createIRI(ns + "hasChild")).entrySet();
		for (Entry<Instance, Set<IRI>> entry : entrySet2) {
//			System.out.println(entry.getKey().getIdentifier().toString());
			Set<IRI> IRIset = entry.getValue();
//			for (IRI value : IRIset) 
//				System.out.println("  value: " + value.toString());
		}
		assertTrue(entrySet2.size() == 3);
		
		// test get instances and values of a specified constraint attribute
        Set<Entry<Instance, Set<Term>>> entrySetTerm2 = wsmlReasoner.getConstraintAttributeInstances(
				(IRI) ontology.getIdentifier(), 
				wsmoFactory.createIRI(ns + "ageOfHuman")).entrySet();
		for (Entry<Instance, Set<Term>> entry : entrySetTerm2) {
//			System.out.println(entry.getKey().getIdentifier().toString());
			Set<Term> valueSet = entry.getValue();
//			for (Term value : valueSet) 
//				System.out.println("value: " + value.toString());
		}
		assertTrue(entrySetTerm2.size() == 3);
		
		// test isInstanceHavingInferingAttributeValue
		assertTrue(wsmlReasoner.executeGroundQuery((IRI) ontology.getIdentifier(),
				leFactory.createLogicalExpression("Mary [hasChild hasValue Jack].", 
						ontology)));
		
		// test isInstanceNotHavingInferingAttributeValue
		assertFalse(wsmlReasoner.executeGroundQuery((IRI) ontology.getIdentifier(), 
				leFactory.createLogicalExpression("Mary [hasChild hasValue Bob].", 
						ontology)));
		
		// test isInstanceHavingConstraintAttributeValue
		assertTrue(wsmlReasoner.executeGroundQuery((IRI) ontology.getIdentifier(), 
				leFactory.createLogicalExpression("Mary [hasName hasValue " +
						"\"Mary Jones\"].", ontology)));
		
		// test isInstanceNotHavingConstraintAttributeValue
		assertFalse(wsmlReasoner.executeGroundQuery((IRI) ontology.getIdentifier(), 
				leFactory.createLogicalExpression("Mary [hasWeight hasValue 60].", 
						ontology)));
		
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
				wsmoFactory.createInstance(wsmoFactory.createIRI(ns + "Anna")),
				wsmoFactory.createIRI(ns + "hasWeight"));
		for(String dataValue : set4)
//			System.out.println(dataValue);
		assertTrue(set4.size() == 1);
		
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
        wsmlReasoner = DefaultWSMLReasonerFactory.getFactory().createWSMLDLReasoner(params);
		try {
			// register ontology at the wsml reasoner
			wsmlReasoner.registerOntology(ontology);
			fail("Should fail because the given ontology is inconsistent");
		} catch (InconsistencyException e) {
			e.getMessage();		
		}	
	}
	
}
/*
 * $Log: not supported by cvs2svn $
 *
 */
