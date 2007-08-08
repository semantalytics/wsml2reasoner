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
package open;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;

import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Concept;
import org.omwg.ontology.Instance;
import org.omwg.ontology.Ontology;
import org.wsml.reasoner.api.WSMLReasoner;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsmo.common.IRI;
import org.wsmo.factory.Factory;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.Parser;

import base.BaseReasonerTest;

/*
 * IRIS does not pass
 * 
 */


public class SimpleInferenceTests extends BaseReasonerTest {

	private WsmoFactory wsmoFactory;
	
	private LogicalExpressionFactory leFactory;

    private WSMLReasoner wsmlReasoner;

    private Parser parser;
    
    private Ontology ontology;
    
    private String ns;
    
	protected void setUp() throws Exception {
		super.setUp();
		WSMO4JManager wsmoManager = new WSMO4JManager();
        wsmoFactory = wsmoManager.getWSMOFactory();
        leFactory = wsmoManager.getLogicalExpressionFactory();
        // get A reasoner
        wsmlReasoner = BaseReasonerTest.getReasoner();
		parser = Factory.createParser(null);
	}
	
	public void testRun() throws Exception {
		// read test file and parse it 
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(
                "files/simpsons.wsml");
        assertNotNull(is);
        // assuming first topentity in file is an ontology  
        ontology = (Ontology)parser.parse(new InputStreamReader(is))[0]; 
        ns = ontology.getDefaultNamespace().getIRI().toString();

        // register ontology at the wsml reasoner
        wsmlReasoner.registerOntology(ontology);       
        
		// test ontology satisfiability
		assertTrue(wsmlReasoner.isSatisfiable((IRI) ontology.getIdentifier()));
		
		// test logicalExpressionIsNotConsistent with a MembershipMolecule
		assertFalse(wsmlReasoner.entails((IRI) ontology.getIdentifier(), 
				leFactory.createLogicalExpression(
						"bart_simpson memberOf actor.", ontology)));
		
		// test logicalExpressionIsConsistent with a MembershipMolecule
		assertTrue(wsmlReasoner.entails((IRI) ontology.getIdentifier(), 
				leFactory.createLogicalExpression(
						"bart_simpson memberOf character.", ontology)));
		
		// test logicalExpressionIsConsistent with a Conjunction
		assertTrue(wsmlReasoner.entails((IRI) ontology.getIdentifier(), 
				leFactory.createLogicalExpression(
						"?x memberOf school and ?x memberOf place.", 
						ontology)));
		assertTrue(wsmlReasoner.entails((IRI) ontology.getIdentifier(), 
				leFactory.createLogicalExpression(
						"marge_simpson memberOf character and " +
						"nancy_cartwright memberOf actor.", ontology)));
		
		// test logicalExpressionIsNotConsistent with a Conjunction
		assertFalse(wsmlReasoner.entails((IRI) ontology.getIdentifier(), 
				leFactory.createLogicalExpression(
				"?x memberOf character and ?x memberOf actor.", ontology)));
    
		// test getAllConcepts
		Set<Concept> set = new HashSet<Concept>();
		set = wsmlReasoner.getAllConcepts((IRI) ontology.getIdentifier());
//		for (Concept concept : set) 
//			System.out.println(concept.getIdentifier().toString());
		assertTrue(set.size() == 17);
		
		// test getAllInstances
		Set<Instance> set2 = new HashSet<Instance>();
		set2 = wsmlReasoner.getAllInstances((IRI) ontology.getIdentifier());
//		for (Instance instance : set2) 
//			System.out.println(instance.getIdentifier().toString());
		assertTrue(set2.size() == 70);
		
		// test getAllAttributes
		// please note that in datalog you only get all attributes that have 
		// either been explicitly defined as inferring or constraining attributes 
		// or that have been assigned a value
		Set<IRI> set3 = new HashSet<IRI>();
		set3 = wsmlReasoner.getAllAttributes((IRI) ontology.getIdentifier());
//		for (IRI attributeId : set3) 
//			System.out.println(attributeId.toString());
		assertTrue(set3.size() == 32);
	
		// test getAllConstraintAttributes
		set3.clear();
		set3 = wsmlReasoner.getAllConstraintAttributes((IRI) ontology.getIdentifier());
//		for (IRI attributeId : set3) 
//			System.out.println(attributeId.toString());
		assertTrue(set3.size() == 28);
		
		// test getAllInferenceAttributes
		set3.clear();
		set3 = wsmlReasoner.getAllInferenceAttributes((IRI) ontology.getIdentifier());
//		for (IRI attributeId : set3) 
//			System.out.println(attributeId.toString());
		assertTrue(set3.size() == 3);
		
		// test getSubConcepts
		set.clear();
		set = wsmlReasoner.getSubConcepts((IRI) ontology.getIdentifier(),  
				ontology.findConcept(wsmoFactory.createIRI(ns + "place")));
//		for (Concept concept : set)
//			System.out.println(concept.getIdentifier().toString());
		assertTrue(set.size() == 5);
		
		// test getDirectSubConcepts
		set.clear();
		set = wsmlReasoner.getDirectSubConcepts((IRI) ontology.getIdentifier(),  
				ontology.findConcept(wsmoFactory.createIRI(ns + "place")));
//		for (Concept concept : set)
//			System.out.println(concept.getIdentifier().toString());
		assertTrue(set.size() == 4);		
		
		// test getSuperConcepts
		set.clear();
		set = wsmlReasoner.getSuperConcepts((IRI) ontology.getIdentifier(), 
				ontology.findConcept(wsmoFactory.createIRI(ns + "university")));
//		for (Concept concept : set)
//			System.out.println(concept.getIdentifier().toString());
		assertTrue(set.size() == 2);

		// test getDirectSuperConcepts
		set.clear();
		set = wsmlReasoner.getDirectSuperConcepts((IRI) ontology.getIdentifier(), 
				ontology.findConcept(wsmoFactory.createIRI(ns + "university")));
//		for (Concept concept : set)
//			System.out.println(concept.getIdentifier().toString());
		assertTrue(set.size() == 1);

		// test isSubConceptOf
		assertTrue(wsmlReasoner.isSubConceptOf(
				(IRI) ontology.getIdentifier(), 
				ontology.findConcept(wsmoFactory.createIRI(ns + "university")), 
				ontology.findConcept(wsmoFactory.createIRI(ns + "place"))));
		assertTrue(wsmlReasoner.executeGroundQuery((IRI) ontology.getIdentifier(),
				leFactory.createLogicalExpression("workplace subConceptOf place", 
						ontology)));
		
		// test isNotSubConceptOf
		assertFalse(wsmlReasoner.isSubConceptOf(
				(IRI) ontology.getIdentifier(), 
				ontology.findConcept(wsmoFactory.createIRI(ns + "workplace")), 
				ontology.findConcept(wsmoFactory.createIRI(ns + "character"))));
		
		// test isMemberOf
		assertTrue(wsmlReasoner.isMemberOf(
				(IRI) ontology.getIdentifier(), 
				ontology.findInstance(wsmoFactory.createIRI(ns + "bart_simpson")), 
				ontology.findConcept(wsmoFactory.createIRI(ns + "character"))));
		assertTrue(wsmlReasoner.executeGroundQuery((IRI) ontology.getIdentifier(),
				leFactory.createLogicalExpression("bart_simpson memberOf character", 
						ontology)));
		
		// test isNotMemberOf
		assertFalse(wsmlReasoner.isMemberOf(
				(IRI) ontology.getIdentifier(), 
				ontology.findInstance(wsmoFactory.createIRI(ns + "bart_simpson")), 
				ontology.findConcept(wsmoFactory.createIRI(ns + "actor"))));
		
		// test direct conceptsOf
		set.clear();
		set = wsmlReasoner.getDirectConcepts(
				(IRI) ontology.getIdentifier(), 
				ontology.findInstance(wsmoFactory.createIRI(ns + "springfield_elementary")));
//		for(Concept concept : set)
//			System.out.println(concept.getIdentifier().toString());
		set.remove(wsmoFactory.createConcept(
				wsmoFactory.createIRI("http://www.wsmo.org/wsml/wsml-syntax#iri")));
		assertTrue(set.size() == 2);
		
		// test allConceptsOf
		set.clear();
		set = wsmlReasoner.getConcepts(
				(IRI) ontology.getIdentifier(), 
				ontology.findInstance(wsmoFactory.createIRI(ns + "springfield_elementary")));
//		for(Concept concept : set)
//			System.out.println(concept.getIdentifier().toString());
		set.remove(wsmoFactory.createConcept(
				wsmoFactory.createIRI("http://www.wsmo.org/wsml/wsml-syntax#iri")));
		assertTrue(set.size() == 3);
		
		// test allInstancesOf
		set2.clear();
		set2 = wsmlReasoner.getInstances(
				(IRI) ontology.getIdentifier(), 
				ontology.findConcept(wsmoFactory.createIRI(ns + "workplace")));
//		for(Instance instance : set2)
//			System.out.println(instance.getIdentifier().toString());
		assertTrue(set2.size() == 6);
		
		// test getConceptsOfAttribute
		set.clear();
		set = wsmlReasoner.getConceptsOf((IRI) ontology.getIdentifier(), 
				wsmoFactory.createIRI(ns + "hasGender"));
//		for (Concept concept : set)
//			System.out.println(concept.getIdentifier().toString());
		set.remove(wsmoFactory.createConcept(
				wsmoFactory.createIRI("http://www.wsmo.org/wsml/wsml-syntax#iri")));
		assertTrue(set.size() == 3);

		// test getRangesOfInferingAttribute
		set3.clear();
		set3 = wsmlReasoner.getRangesOfInferingAttribute((IRI) ontology.getIdentifier(), 
				wsmoFactory.createIRI(ns + "testAttribute"));
//		for (Identifier rangeId : set3)
//			System.out.println(rangeId.toString());
		assertTrue(set3.size() == 1);
		
		// test getRangesOfConstraintAttribute
		set3.clear();
		set3 = wsmlReasoner.getRangesOfConstraintAttribute((IRI) ontology.getIdentifier(), 
				wsmoFactory.createIRI(ns + "hasChild"));
//		for (Identifier rangeId : set3)
//			System.out.println(rangeId.toString());
		assertTrue(set3.size() == 1);
		
		// test getValuesOfInferingAttribute of a specified instance
		Set<Entry<IRI, Set<Term>>> entrySet = wsmlReasoner.getInferingAttributeValues(
				(IRI) ontology.getIdentifier(), 
				wsmoFactory.createInstance(wsmoFactory.createIRI(ns + "homer_simpson"))).entrySet();
		for (Entry<IRI, Set<Term>> entry : entrySet) {
//			System.out.println(entry.getKey().toString());
			Set<Term> IRIset = entry.getValue();
//			for (Term value : IRIset) 
//				System.out.println("  value: " + value.toString());
		}
		assertTrue(entrySet.size() == 2);

		// test getValuesOfConstraintAttribute of a specified instance
        Set <Entry<IRI, Set<Term>>> entrySetTerm = 
				wsmlReasoner.getConstraintAttributeValues((IRI) ontology.getIdentifier(), 
				wsmoFactory.createInstance(wsmoFactory.createIRI(ns + "marge_simpson"))).entrySet();
		for (Entry<IRI, Set<Term>> entry : entrySetTerm) {
//			System.out.println(entry.getKey().toString());
			Set<Term> termSet = entry.getValue();
//			for (Term value : termSet) 
//				System.out.println("  value: " + value.toString());
		}
		assertTrue(entrySetTerm.size() == 12);
		
		// test get instances and values of a specified infering attribute
		Set<Entry<Instance, Set<Term>>> entrySet2 = wsmlReasoner.
				getInferingAttributeInstances((IRI) ontology.getIdentifier(), 
				wsmoFactory.createIRI(ns + "testAttribute")).entrySet();
		for (Entry<Instance, Set<Term>> entry : entrySet2) {
//			System.out.println(entry.getKey().getIdentifier().toString());
			Set<Term> IRIset = entry.getValue();
//			for (Term value : IRIset) 
//				System.out.println("  value: " + value.toString());
		}
		assertTrue(entrySet2.size() == 1);
				
		// test get instances and values of a specified constraint attribute
        Set<Entry<Instance, Set<Term>>> entrySetTerm2 = wsmlReasoner.getConstraintAttributeInstances(
				(IRI) ontology.getIdentifier(), 
				wsmoFactory.createIRI(ns + "hasCatchPhrase")).entrySet();
		for (Entry<Instance, Set<Term>> entry : entrySetTerm2) {
//			System.out.println(entry.getKey().getIdentifier().toString());
			Set<Term> valueSet = entry.getValue();
//			for (Term value : valueSet) 
//				System.out.println("  value: " + value.toString());
		}
		assertTrue(entrySetTerm2.size() == 3);
		
		// test isInstanceHavingInferingAttributeValue
		assertTrue(wsmlReasoner.executeGroundQuery((IRI) ontology.getIdentifier(),
				leFactory.createLogicalExpression("marge_simpson[hasChild " +
						"hasValue bart_simpson].", ontology)));
		
		// test isInstanceNotHavingInferingAttributeValue
		assertFalse(wsmlReasoner.executeGroundQuery((IRI) ontology.getIdentifier(), 
				leFactory.createLogicalExpression("marge_simpson[hasChild " +
						"hasValue bobby_simpson].", ontology)));
		
		// test isInstanceHavingConstraintAttributeValue
		assertTrue(wsmlReasoner.executeGroundQuery((IRI) ontology.getIdentifier(), 
				leFactory.createLogicalExpression("marge_simpson[hasName " +
						"hasValue \"Marge Simpson\"].", ontology)));
		
		// test isInstanceNotHavingConstraintAttributeValue
		assertFalse(wsmlReasoner.executeGroundQuery((IRI) ontology.getIdentifier(), 
				leFactory.createLogicalExpression("marge_simpson[hasCatchPhrase " +
						"hasValue \"blabla\"].", ontology)));
		
		// test getInferingAttributeValues
		Set untypedSet = new HashSet();
		untypedSet = wsmlReasoner.getInferingAttributeValues(
				(IRI) ontology.getIdentifier(), 
				wsmoFactory.createInstance(wsmoFactory.createIRI(ns + "homer_simpson")),
				wsmoFactory.createIRI(ns + "testAttribute"));
//		for(Object object : untypedSet)
//			System.out.println(object.toString());
		assertTrue(untypedSet.size() == 1);
		
		// test getConstraintAttributeValues
		untypedSet.clear();
		untypedSet = wsmlReasoner.getConstraintAttributeValues(
				(IRI) ontology.getIdentifier(), 
				wsmoFactory.createInstance(wsmoFactory.createIRI(ns + "marge_simpson")),
				wsmoFactory.createIRI(ns + "hasChild"));
//		for(Object dataValue : untypedSet)
//			System.out.println(dataValue.toString());
		assertTrue(untypedSet.size() == 3);
		
		wsmlReasoner.deRegisterOntology((IRI) ontology.getIdentifier());
	}
	
}
/*
 * $Log: not supported by cvs2svn $
 * Revision 1.2  2007-06-19 09:56:23  graham
 * intial steps to refactoring test suites
 *
 * Revision 1.1  2007/03/01 11:44:31  nathalie
 * added test for the newly implemented datalog based reasoning methods
 *
 * Revision 1.7  2006/09/01 12:06:48  nathalie
 * *** empty log message ***
 *
 * Revision 1.6  2006/08/31 12:36:00  nathalie
 * removed methods from WSMLDLReasoner interface to the WSMLReasoner interface. Replaced some methods by entails() and groundQuery() methods.
 *
 * Revision 1.5  2006/08/10 08:30:59  nathalie
 * added request for getting direct concept/concepts of an instance
 *
 * Revision 1.4  2006/08/08 10:14:28  nathalie
 * implemented support for registering multiple ontolgies at wsml-dl reasoner
 *
 * Revision 1.3  2006/07/23 15:20:23  nathalie
 * updated tests and testfiles
 *
 * Revision 1.2  2006/07/21 16:25:21  nathalie
 * completing the pellet reasoner integration
 *
 *
 *
 */
