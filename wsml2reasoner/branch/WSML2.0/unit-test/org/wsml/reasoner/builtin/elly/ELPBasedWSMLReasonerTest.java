package org.wsml.reasoner.builtin.elly;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Concept;
import org.omwg.ontology.DataType;
import org.omwg.ontology.DataValue;
import org.omwg.ontology.Instance;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Type;
import org.sti2.wsmo4j.factory.WsmlFactoryContainer;
import org.wsml.reasoner.api.DLReasoner;
import org.wsml.reasoner.api.inconsistency.InconsistencyException;
import org.wsml.reasoner.impl.DefaultWSMLReasonerFactory;
import org.wsmo.common.IRI;
import org.wsmo.common.Identifier;
import org.wsmo.common.TopEntity;
import org.wsmo.factory.FactoryContainer;
import org.wsmo.wsml.Parser;

import com.ontotext.wsmo4j.parser.wsml.WsmlParser;

public class ELPBasedWSMLReasonerTest extends TestCase {

	private static String defaultNS = "http://www.example.org/example/";
	private static String prefix = "org/wsml/reasoner/builtin/elly/";
	private DLReasoner reasoner;
	private FactoryContainer container;
	Ontology ontology;

	protected void setUp() throws Exception {
		container = new WsmlFactoryContainer();
		reasoner = DefaultWSMLReasonerFactory.getFactory().createDL2Reasoner(container);
	}
	
	public void testSetDisableConsitencyCheck() {
		fail("Not yet implemented");
	}

	public void testSetAllowImports() {
		fail("Not yet implemented");
	}

	public void testRegisterOntology() {
		fail("Not yet implemented");
	}

	public void testRegisterOntologies() {
		fail("Not yet implemented");
	}

	public void testRegisterEntities() {
		fail("Not yet implemented");
	}

	public void testDeRegister() {
		fail("Not yet implemented");
	}

	public void testRegisterEntitiesNoVerification() {
		fail("Not yet implemented");
	}

	public void testRegisterOntologyNoVerification() {
		fail("Not yet implemented");
	}

	public void testGetAllAttributes() throws InconsistencyException {
		List<Ontology> ontologies = loadOntologies(prefix + "Human.wsml");
		if (ontologies.isEmpty())
			fail();
		
		reasoner.registerOntologies(new HashSet<Ontology>(ontologies));

		/* **********
		 * Query
		 * **********/

		Set<IRI> attributes = reasoner.getAllAttributes();
		
		System.out.println("All Attributes:");
		System.out.println(attributes);
		
		assertEquals(22, attributes.size());
		assertTrue(attributes.contains(container.getWsmoFactory().createIRI("http://www.example.org/example/hasName")));
		assertTrue(attributes.contains(container.getWsmoFactory().createIRI("http://www.example.org/example/hasRelative")));
		assertTrue(attributes.contains(container.getWsmoFactory().createIRI("http://www.example.org/example/hasAncestor")));
		assertTrue(attributes.contains(container.getWsmoFactory().createIRI("http://www.example.org/example/hasParent")));
		assertTrue(attributes.contains(container.getWsmoFactory().createIRI("http://www.example.org/example/hasMother")));
		assertTrue(attributes.contains(container.getWsmoFactory().createIRI("http://www.example.org/example/hasMum")));
		assertTrue(attributes.contains(container.getWsmoFactory().createIRI("http://www.example.org/example/hasChild")));
		assertTrue(attributes.contains(container.getWsmoFactory().createIRI("http://www.example.org/example/hasWeightInKG")));
		assertTrue(attributes.contains(container.getWsmoFactory().createIRI("http://www.example.org/example/hasBirthdate")));
		assertTrue(attributes.contains(container.getWsmoFactory().createIRI("http://www.example.org/example/dateOfDeath")));
		assertTrue(attributes.contains(container.getWsmoFactory().createIRI("http://www.example.org/example/hasBirthplace")));
		assertTrue(attributes.contains(container.getWsmoFactory().createIRI("http://www.example.org/example/isMarriedTo")));
		assertTrue(attributes.contains(container.getWsmoFactory().createIRI("http://www.example.org/example/hasCitizenship")));
		assertTrue(attributes.contains(container.getWsmoFactory().createIRI("http://www.example.org/example/isAlive")));
		assertTrue(attributes.contains(container.getWsmoFactory().createIRI("http://www.example.org/example/hasName2")));
		assertTrue(attributes.contains(container.getWsmoFactory().createIRI("http://www.example.org/example/hasRelative2")));
		assertTrue(attributes.contains(container.getWsmoFactory().createIRI("http://www.example.org/example/hasAncestor2")));
		assertTrue(attributes.contains(container.getWsmoFactory().createIRI("http://www.example.org/example/hasParent2")));
		assertTrue(attributes.contains(container.getWsmoFactory().createIRI("http://www.example.org/example/hasMother2")));
		assertTrue(attributes.contains(container.getWsmoFactory().createIRI("http://www.example.org/example/hasChild2")));
		assertTrue(attributes.contains(container.getWsmoFactory().createIRI("http://www.example.org/example/hasWeightInKG2")));
		assertTrue(attributes.contains(container.getWsmoFactory().createIRI("http://www.example.org/example/hasBirthdate2")));
	}

	public void testGetAllConcepts() throws InconsistencyException {
		List<Ontology> ontologies = loadOntologies(prefix + "Human.wsml");
		if (ontologies.isEmpty())
			fail();
		
		reasoner.registerOntologies(new HashSet<Ontology>(ontologies));

		/* **********
		 * Query
		 * **********/

		Set<Concept> concepts = reasoner.getAllConcepts();
		Set<Identifier> conceptIdentifiers = new HashSet<Identifier>();
		for (Concept concept : concepts) {
			conceptIdentifiers.add(concept.getIdentifier());
		}
		
		
		System.out.println("All Concepts:");
		System.out.println(concepts);
		
		assertEquals(20, concepts.size());
		assertTrue(conceptIdentifiers.contains(container.getWsmoFactory().createIRI(defaultNS + "Human")));
		assertTrue(conceptIdentifiers.contains(container.getWsmoFactory().createIRI("http://www.example.org/foaf#name")));
		assertTrue(conceptIdentifiers.contains(container.getWsmoFactory().createIRI(defaultNS + "FemaleHuman")));
		assertTrue(conceptIdentifiers.contains(container.getWsmoFactory().createIRI(defaultNS + "Mother")));
		assertTrue(conceptIdentifiers.contains(container.getWsmoFactory().createIRI("http://www.example.org/loc#location")));
		assertTrue(conceptIdentifiers.contains(container.getWsmoFactory().createIRI("http://www.example.org/oo#country")));
		assertTrue(conceptIdentifiers.contains(container.getWsmoFactory().createIRI(defaultNS + "Man")));
		assertTrue(conceptIdentifiers.contains(container.getWsmoFactory().createIRI(defaultNS + "Woman")));
		assertTrue(conceptIdentifiers.contains(container.getWsmoFactory().createIRI(defaultNS + "Parent")));
		assertTrue(conceptIdentifiers.contains(container.getWsmoFactory().createIRI(defaultNS + "Child")));
		assertTrue(conceptIdentifiers.contains(container.getWsmoFactory().createIRI(defaultNS + "Girl")));
		assertTrue(conceptIdentifiers.contains(container.getWsmoFactory().createIRI(defaultNS + "Boy")));
		assertTrue(conceptIdentifiers.contains(container.getWsmoFactory().createIRI(defaultNS + "Human2")));
		assertTrue(conceptIdentifiers.contains(container.getWsmoFactory().createIRI(defaultNS + "Mother2")));
		assertTrue(conceptIdentifiers.contains(container.getWsmoFactory().createIRI(defaultNS + "FemaleHuman2")));
		assertTrue(conceptIdentifiers.contains(container.getWsmoFactory().createIRI(defaultNS + "Man2")));
		assertTrue(conceptIdentifiers.contains(container.getWsmoFactory().createIRI(defaultNS + "Woman2")));
		assertTrue(conceptIdentifiers.contains(container.getWsmoFactory().createIRI(defaultNS + "Girl2")));
		assertTrue(conceptIdentifiers.contains(container.getWsmoFactory().createIRI(defaultNS + "Father")));
		assertTrue(conceptIdentifiers.contains(container.getWsmoFactory().createIRI(defaultNS + "Daddy")));
	}

	public void testGetAllConstraintAttributes() throws InconsistencyException {
		List<Ontology> ontologies = loadOntologies(prefix + "Human.wsml");
		if (ontologies.isEmpty())
			fail();
		
		reasoner.registerOntologies(new HashSet<Ontology>(ontologies));

		/* **********
		 * Query
		 * **********/

		Set<IRI> constrainingAttributes = reasoner.getAllConstraintAttributes();
		
		
		System.out.println("All ConstraintAttributes:");
		System.out.println(constrainingAttributes);
		
		assertEquals(6, constrainingAttributes.size());
		assertTrue(constrainingAttributes.contains(container.getWsmoFactory().createIRI(defaultNS + "hasWeightInKG"))); //	ofType xsd#float
		assertTrue(constrainingAttributes.contains(container.getWsmoFactory().createIRI(defaultNS + "hasBirthdate"))); //	ofType xsd#date
		assertTrue(constrainingAttributes.contains(container.getWsmoFactory().createIRI(defaultNS + "dateOfDeath"	))); //	ofType xsd#date
		assertTrue(constrainingAttributes.contains(container.getWsmoFactory().createIRI(defaultNS + "isAlive"		))); //	ofType xsd#boolean
		assertTrue(constrainingAttributes.contains(container.getWsmoFactory().createIRI(defaultNS + "hasWeightInKG2"))); //	ofType xsd#float
		assertTrue(constrainingAttributes.contains(container.getWsmoFactory().createIRI(defaultNS + "hasBirthdate2"))); //	ofType xsd#date
	}

	public void testGetAllInferenceAttributes() throws InconsistencyException {
		List<Ontology> ontologies = loadOntologies(prefix + "Human.wsml");
		if (ontologies.isEmpty())
			fail();
		
		reasoner.registerOntologies(new HashSet<Ontology>(ontologies));

		/* **********
		 * Query
		 * **********/

		Set<IRI> inferringAttributes = reasoner.getAllInferenceAttributes();
		
		
		System.out.println("All inferringAttributes:");
		System.out.println(inferringAttributes);
		
		assertEquals(15, inferringAttributes.size());
		assertTrue(inferringAttributes.contains(container.getWsmoFactory().createIRI(defaultNS + "hasName"))); // impliesType foaf#name
		assertTrue(inferringAttributes.contains(container.getWsmoFactory().createIRI(defaultNS + "hasRelative"))); // impliesType Human
		assertTrue(inferringAttributes.contains(container.getWsmoFactory().createIRI(defaultNS + "hasAncestor"))); // impliesType Human
		assertTrue(inferringAttributes.contains(container.getWsmoFactory().createIRI(defaultNS + "hasParent"))); // impliesType Human
		assertTrue(inferringAttributes.contains(container.getWsmoFactory().createIRI(defaultNS + "hasMother"))); // impliesType FemaleHuman
		assertTrue(inferringAttributes.contains(container.getWsmoFactory().createIRI(defaultNS + "hasChild"))); // impliesType Human
		assertTrue(inferringAttributes.contains(container.getWsmoFactory().createIRI(defaultNS + "hasBirthplace"))); // impliesType loc#location
		assertTrue(inferringAttributes.contains(container.getWsmoFactory().createIRI(defaultNS + "isMarriedTo"))); // impliesType Human
		assertTrue(inferringAttributes.contains(container.getWsmoFactory().createIRI(defaultNS + "hasCitizenship"))); // impliesType oo#country
		assertTrue(inferringAttributes.contains(container.getWsmoFactory().createIRI(defaultNS + "hasName2"))); // impliesType foaf#name
		assertTrue(inferringAttributes.contains(container.getWsmoFactory().createIRI(defaultNS + "hasRelative2"))); // impliesType Human
		assertTrue(inferringAttributes.contains(container.getWsmoFactory().createIRI(defaultNS + "hasAncestor2"))); // impliesType Human
		assertTrue(inferringAttributes.contains(container.getWsmoFactory().createIRI(defaultNS + "hasParent2"))); // impliesType Human
		assertTrue(inferringAttributes.contains(container.getWsmoFactory().createIRI(defaultNS + "hasChild2"))); // impliesType Human
		assertTrue(inferringAttributes.contains(container.getWsmoFactory().createIRI(defaultNS + "hasMother2"))); // impliesType Mother2
	}

	public void testGetAllInstances() throws InconsistencyException {
		List<Ontology> ontologies = loadOntologies(prefix + "Human.wsml");
		if (ontologies.isEmpty())
			fail();
		
		reasoner.registerOntologies(new HashSet<Ontology>(ontologies));

		/* **********
		 * Query
		 * **********/

		Set<Instance> instances = reasoner.getAllInstances();
		Set<Identifier> instanceIDs = new HashSet<Identifier>();
		for (Instance instance : instances) {
			instanceIDs.add(instance.getIdentifier());
		}
		
		
		System.out.println("All Instances:");
		System.out.println(instances);
		
		assertEquals(8, instances.size());
		assertTrue(instanceIDs.contains(container.getWsmoFactory().createIRI(defaultNS + "Mary"))); // memberOf {Parent, Woman}
		assertTrue(instanceIDs.contains(container.getWsmoFactory().createIRI(defaultNS + "Paul"))); // memberOf { Parent, Man }
		assertTrue(instanceIDs.contains(container.getWsmoFactory().createIRI(defaultNS + "Susan"))); // memberOf Woman
		assertTrue(instanceIDs.contains(container.getWsmoFactory().createIRI(defaultNS + "George"))); // memberOf Man
		assertTrue(instanceIDs.contains(container.getWsmoFactory().createIRI(defaultNS + "Paul2"))); // memberOf { Parent, Man }
		assertTrue(instanceIDs.contains(container.getWsmoFactory().createIRI(defaultNS + "George2"))); // memberOf Man
		assertTrue(instanceIDs.contains(container.getWsmoFactory().createIRI("http://www.example.org/oo#de"))); 
		assertTrue(instanceIDs.contains(container.getWsmoFactory().createIRI("http://www.example.org/oo#en"))); 
	}

	public void testGetConcepts() throws InconsistencyException {
		List<Ontology> ontologies = loadOntologies(prefix + "Human.wsml");
		if (ontologies.isEmpty())
			fail();
		
		reasoner.registerOntologies(new HashSet<Ontology>(ontologies));

		/* **********
		 * Query
		 * **********/

		
		
		Identifier instanceID; 
		{
			instanceID = container.getWsmoFactory().createIRI(defaultNS + "Mary"); // memberOf {Parent, Woman} 
			Instance instance = container.getWsmoFactory().createInstance(instanceID);
			Set<Concept> conceptsOfInstance = reasoner.getConcepts(instance);
			
			System.out.println("All concepts Of Instance " + instance + ":");
			System.out.println(conceptsOfInstance);
			
			List<Identifier> conceptIDs = new ArrayList<Identifier>();
			for (Concept concept : conceptsOfInstance) {
				conceptIDs.add(concept.getIdentifier());
			}
			
			assertEquals(3, conceptsOfInstance.size());
			assertTrue(conceptIDs.contains(container.getWsmoFactory().createIRI(defaultNS + "Parent")));
			assertTrue(conceptIDs.contains(container.getWsmoFactory().createIRI(defaultNS + "Woman")));
			assertTrue(conceptIDs.contains(container.getWsmoFactory().createIRI(defaultNS + "Human")));
		}
		
		{
			instanceID = container.getWsmoFactory().createIRI(defaultNS + "Paul2"); // memberOf {Parent, Man2} 
			Instance instance = container.getWsmoFactory().createInstance(instanceID);
			Set<Concept> conceptsOfInstance = reasoner.getConcepts(instance);
			
			System.out.println("All concepts Of Instance " + instance + ":");
			System.out.println(conceptsOfInstance);
			
			List<Identifier> conceptIDs = new ArrayList<Identifier>();
			for (Concept concept : conceptsOfInstance) {
				conceptIDs.add(concept.getIdentifier());
			}
			
			assertEquals(4, conceptsOfInstance.size());
			assertTrue(conceptIDs.contains(container.getWsmoFactory().createIRI(defaultNS + "Parent")));
			assertTrue(conceptIDs.contains(container.getWsmoFactory().createIRI(defaultNS + "Human")));
			assertTrue(conceptIDs.contains(container.getWsmoFactory().createIRI(defaultNS + "Man2")));
			assertTrue(conceptIDs.contains(container.getWsmoFactory().createIRI(defaultNS + "Human2")));
		}
	}

	public void testGetConceptsOf() throws InconsistencyException {
		List<Ontology> ontologies = loadOntologies(prefix + "Human.wsml");
		if (ontologies.isEmpty())
			fail();
		
		reasoner.registerOntologies(new HashSet<Ontology>(ontologies));

		/* **********
		 * Query
		 * **********/

		
		
		Identifier attributeID; 
		{
			attributeID = container.getWsmoFactory().createIRI(defaultNS + "hasName"); 
			Set<Concept> conceptsOfAttribute = reasoner.getConceptsOf(attributeID);
			
			System.out.println("All concepts Of Attribute " + attributeID + ":");
			System.out.println(conceptsOfAttribute);
			
			List<Identifier> conceptIDs = new ArrayList<Identifier>();
			for (Concept concept : conceptsOfAttribute) {
				conceptIDs.add(concept.getIdentifier());
			}
			
			assertEquals(9, conceptsOfAttribute.size());
			assertTrue(conceptIDs.contains(container.getWsmoFactory().createIRI(defaultNS + "Human")));
			assertTrue(conceptIDs.contains(container.getWsmoFactory().createIRI(defaultNS + "Man")));
			assertTrue(conceptIDs.contains(container.getWsmoFactory().createIRI(defaultNS + "Woman")));
			assertTrue(conceptIDs.contains(container.getWsmoFactory().createIRI(defaultNS + "Parent")));
			assertTrue(conceptIDs.contains(container.getWsmoFactory().createIRI(defaultNS + "Child")));
			assertTrue(conceptIDs.contains(container.getWsmoFactory().createIRI(defaultNS + "Girl")));
			assertTrue(conceptIDs.contains(container.getWsmoFactory().createIRI(defaultNS + "Boy")));
			assertTrue(conceptIDs.contains(container.getWsmoFactory().createIRI(defaultNS + "Woman2")));
			assertTrue(conceptIDs.contains(container.getWsmoFactory().createIRI(defaultNS + "Girl2")));
		}
		
		{
			attributeID = container.getWsmoFactory().createIRI(defaultNS + "hasChild"); 
			Set<Concept> conceptsOfAttribute = reasoner.getConceptsOf(attributeID);
			
			System.out.println("All concepts Of Attribute " + attributeID + ":");
			System.out.println(conceptsOfAttribute);
			
			List<Identifier> conceptIDs = new ArrayList<Identifier>();
			for (Concept concept : conceptsOfAttribute) {
				conceptIDs.add(concept.getIdentifier());
			}
			
			assertEquals(1, conceptsOfAttribute.size());
			assertTrue(conceptIDs.contains(container.getWsmoFactory().createIRI(defaultNS + "Parent")));
		}
	}

	public void testGetConstraintAttributeInstances() throws InconsistencyException {
		// a map containing all instances who have values for a specified
		// constraint attribute and for each a set containing all its values
		
		List<Ontology> ontologies = loadOntologies(prefix + "Human.wsml");
		if (ontologies.isEmpty())
			fail();
		
		reasoner.registerOntologies(new HashSet<Ontology>(ontologies));

		/* **********
		 * Query
		 * **********/

		// query hasBirthdate
		
		Identifier attributeID; 
		{
			attributeID = container.getWsmoFactory().createIRI(defaultNS + "hasBirthdate"); 
			Map<Instance, Set<DataValue>> instanceMap = reasoner.getConstraintAttributeInstances(attributeID);
			Map<Identifier, Set<DataValue>> instanceMapID = new HashMap<Identifier, Set<DataValue>>();
			
			System.out.println("All Constraint Attribute Instances of Attribute " + attributeID + ":");
			System.out.println(instanceMap);
			
			for (Instance instance : instanceMap.keySet()) {
				Identifier instanceID = instance.getIdentifier();
				instanceMapID.put(instanceID, instanceMap.get(instance));
			}
			
			assertEquals(5, instanceMap.keySet().size());
			assertTrue(instanceMapID.keySet().contains(container.getWsmoFactory().createIRI(defaultNS + "Mary")));
			assertTrue(instanceMapID.keySet().contains(container.getWsmoFactory().createIRI(defaultNS + "Paul")));
			assertTrue(instanceMapID.keySet().contains(container.getWsmoFactory().createIRI(defaultNS + "Susan")));
			assertTrue(instanceMapID.keySet().contains(container.getWsmoFactory().createIRI(defaultNS + "George")));
			assertTrue(instanceMapID.keySet().contains(container.getWsmoFactory().createIRI(defaultNS + "Paul2")));
			
			
			Set<DataValue> terms = instanceMapID.get(container.getWsmoFactory().createIRI(defaultNS + "Mary"));
			assertTrue(terms.contains(container.getXmlDataFactory().createDate(new GregorianCalendar(1949, 8, 12))));
			
			terms = instanceMapID.get(container.getWsmoFactory().createIRI(defaultNS + "Paul2"));
			assertTrue(terms.contains(container.getXmlDataFactory().createDate(new GregorianCalendar(1976, 7, 16))));
			assertTrue(terms.contains(container.getXmlDataFactory().createDate(new GregorianCalendar(1967, 7, 16))));
		}
		
	}

	public void testGetConstraintAttributeValuesInstance() throws InconsistencyException { 
		// Map<IRI, Set<Term>> getConstraintAttributeValues(Instance instance)
		// a map containing all constraint attributes of a specified
	    // instance and for each a set containing all its values
		
		List<Ontology> ontologies = loadOntologies(prefix + "Human.wsml");
		if (ontologies.isEmpty())
			fail();
		
		reasoner.registerOntologies(new HashSet<Ontology>(ontologies));

		/* **********
		 * Query
		 * **********/

		// query hasBirthdate
		
		{
			Identifier instanceID = container.getWsmoFactory().createIRI(defaultNS + "Paul2"); 
			Instance instance = container.getWsmoFactory().createInstance(instanceID);
			Map<IRI, Set<DataValue>> valueMap = reasoner.getConstraintAttributeValues(instance);
			
			System.out.println("All Constraint AttributeValues for Instance " + instanceID + ":");
			System.out.println(valueMap);
			
			assertEquals(1, valueMap.keySet().size());
			assertTrue(valueMap.keySet().contains(container.getWsmoFactory().createIRI(defaultNS + "hasBirthdate")));
			
			Set<DataValue> terms = valueMap.get(container.getWsmoFactory().createIRI(defaultNS + "hasBirthdate"));
			assertTrue(terms.contains(container.getXmlDataFactory().createDate(new GregorianCalendar(1976, 7, 16))));
			assertTrue(terms.contains(container.getXmlDataFactory().createDate(new GregorianCalendar(1967, 7, 16))));
		}

		{
			/*
		      instance Mary memberOf {Parent, Woman}
		            annotations
		                  dc#description hasValue "Mary is parent of the twins Paul and Susan"
		            endAnnotations
		            hasName hasValue "Maria Smith"
		            hasBirthdate hasValue xsd#date(1949,09,12)
		            isAlive hasValue xsd#boolean("true")
		            hasWeightInKG hasValue 60.3
		            dateOfDeath hasValue xsd#date(2049,09,12)
		            hasChild hasValue { Paul, Susan }
			 */
			
			
			Identifier instanceID = container.getWsmoFactory().createIRI(defaultNS + "Mary"); 
			Instance instance = container.getWsmoFactory().createInstance(instanceID);
			Map<IRI, Set<DataValue>> valueMap = reasoner.getConstraintAttributeValues(instance);
			
			System.out.println("All Constraint AttributeValues for Instance " + instanceID + ":");
			System.out.println(valueMap);
			
			assertEquals(4, valueMap.keySet().size());
			assertTrue(valueMap.keySet().contains(container.getWsmoFactory().createIRI(defaultNS + "hasBirthdate")));
			assertTrue(valueMap.keySet().contains(container.getWsmoFactory().createIRI(defaultNS + "isAlive")));
			assertTrue(valueMap.keySet().contains(container.getWsmoFactory().createIRI(defaultNS + "hasWeightInKG")));
			assertTrue(valueMap.keySet().contains(container.getWsmoFactory().createIRI(defaultNS + "dateOfDeath")));
			
			Set<DataValue> terms = valueMap.get(container.getWsmoFactory().createIRI(defaultNS + "hasBirthdate"));
			assertTrue(terms.contains(container.getXmlDataFactory().createDate(new GregorianCalendar(1949, 8, 12))));

			terms = valueMap.get(container.getWsmoFactory().createIRI(defaultNS + "isAlive"));
			assertTrue(terms.contains(container.getXmlDataFactory().createBoolean(true)));

			terms = valueMap.get(container.getWsmoFactory().createIRI(defaultNS + "hasWeightInKG"));
			assertTrue(terms.contains(container.getXmlDataFactory().createDecimal("60.3")));

			terms = valueMap.get(container.getWsmoFactory().createIRI(defaultNS + "dateOfDeath"));
			assertTrue(terms.contains(container.getXmlDataFactory().createDate(new GregorianCalendar(2049, 8, 12))));

		}
	}

	public void testGetConstraintAttributeValuesInstanceIdentifier() throws InconsistencyException {
		// set containing data values of the given instance and constraint attribute
		
		List<Ontology> ontologies = loadOntologies(prefix + "Human.wsml");
		if (ontologies.isEmpty())
			fail();
		
		reasoner.registerOntologies(new HashSet<Ontology>(ontologies));

		/* **********
		 * Query
		 * **********/

		// query hasBirthdate
		
		{
			Identifier instanceID = container.getWsmoFactory().createIRI(defaultNS + "Paul2"); 
			Instance instance = container.getWsmoFactory().createInstance(instanceID);
			Identifier attributeID = container.getWsmoFactory().createIRI(defaultNS + "hasBirthdate");
			
			Set<DataValue> valueSet = reasoner.getConstraintAttributeValues(instance, attributeID);
			
			System.out.println("All Constraint AttributeValues for Instance " + instanceID + " and Attribute " + attributeID + ":");
			System.out.println(valueSet);
			
			assertEquals(2, valueSet.size());
			assertTrue(valueSet.contains(container.getXmlDataFactory().createDate(new GregorianCalendar(1976, 7, 16)).toString()));
			assertTrue(valueSet.contains(container.getXmlDataFactory().createDate(new GregorianCalendar(1967, 7, 16)).toString()));
		}

		{
			/*
		      instance Mary memberOf {Parent, Woman}
		            annotations
		                  dc#description hasValue "Mary is parent of the twins Paul and Susan"
		            endAnnotations
		            hasName hasValue "Maria Smith"
		            hasBirthdate hasValue xsd#date(1949,09,12)
		            isAlive hasValue xsd#boolean("true")
		            hasWeightInKG hasValue 60.3
		            dateOfDeath hasValue xsd#date(2049,09,12)
		            hasChild hasValue { Paul, Susan }
			 */
			
			
			
			Identifier instanceID = container.getWsmoFactory().createIRI(defaultNS + "Mary"); 
			Instance instance = container.getWsmoFactory().createInstance(instanceID);
			Identifier attributeID = container.getWsmoFactory().createIRI(defaultNS + "hasWeightInKG");
			
			Set<DataValue> valueSet = reasoner.getConstraintAttributeValues(instance, attributeID);
			
			System.out.println("All Constraint AttributeValues for Instance " + instanceID + " and Attribute " + attributeID + ":");
			System.out.println(valueSet);
			
			assertEquals(1, valueSet.size());
			assertTrue(valueSet.contains(container.getXmlDataFactory().createDecimal("60.3").toString()));
			

		}
		
	}

	public void testGetDirectConcepts() {
		fail("Not yet implemented");
	}

	public void testGetDirectSubConcepts() throws InconsistencyException {
		fail("Not yet implemented");
	}

	public void testGetDirectSubRelations() {
		fail("Not yet implemented");
	}

	public void testGetDirectSuperConcepts() {
		fail("Not yet implemented");
	}

	public void testGetDirectSuperRelations() {
		fail("Not yet implemented");
	}

	public void testGetEquivalentConcepts() throws InconsistencyException {
		
		List<Ontology> ontologies = loadOntologies(prefix + "Human.wsml");
		if (ontologies.isEmpty())
			fail();
		
		reasoner.registerOntologies(new HashSet<Ontology>(ontologies));

		/* **********
		 * Query
		 * **********/

		{
			Identifier conceptID = container.getWsmoFactory().createIRI(defaultNS + "Father");
			Concept concept = container.getWsmoFactory().createConcept(conceptID);
			Set<Concept> eqConcepts = reasoner.getEquivalentConcepts(concept);
			
			System.out.println("Equal Concepts of " + concept);
			
			assertEquals(2, eqConcepts.size());
			assertTrue(eqConcepts.contains(container.getWsmoFactory().createConcept(container.getWsmoFactory().createIRI(defaultNS + "Father"))));
			assertTrue(eqConcepts.contains(container.getWsmoFactory().createConcept(container.getWsmoFactory().createIRI(defaultNS + "Daddy"))));
		}
		
		fail("Not yet implemented");
	}

	public void testGetEquivalentRelations() throws InconsistencyException {
		
		List<Ontology> ontologies = loadOntologies(prefix + "Human.wsml");
		if (ontologies.isEmpty())
			fail();
		
		reasoner.registerOntologies(new HashSet<Ontology>(ontologies));

		/* **********
		 * Query
		 * **********/

		{
			Identifier attID = container.getWsmoFactory().createIRI(defaultNS + "hasMother");
			Set<IRI> eqConcepts = reasoner.getEquivalentRelations(attID);
			
			System.out.println("Equal Attributes of " + attID);
			
			assertEquals(2, eqConcepts.size());
			assertTrue(eqConcepts.contains((container.getWsmoFactory().createIRI(defaultNS + "hasMum"))));
			assertTrue(eqConcepts.contains((container.getWsmoFactory().createIRI(defaultNS + "hasMother"))));
		}
		
		fail("Not yet implemented");
	}

	public void testGetInferingAttributeInstances() throws InconsistencyException {
		// a map containing all instances who have values for a specified
		// inferring attribute and for each a set containing all its values
		
		List<Ontology> ontologies = loadOntologies(prefix + "Human.wsml");
		if (ontologies.isEmpty())
			fail();
		
		reasoner.registerOntologies(new HashSet<Ontology>(ontologies));

		/* **********
		 * Query
		 * **********/

		// query hasBirthdate
		
		Identifier attributeID; 
		{
			attributeID = container.getWsmoFactory().createIRI(defaultNS + "hasRelative"); // implied by hasChild
			Map<Instance, Set<Term>> instanceMap = reasoner.getInferingAttributeInstances(attributeID);
			Map<Identifier, Set<Term>> instanceMapID = new HashMap<Identifier, Set<Term>>();
			
			System.out.println("All Inferring Attribute Instances of Attribute " + attributeID + ":");
			System.out.println(instanceMap);
			
			for (Instance instance : instanceMap.keySet()) {
				Identifier instanceID = instance.getIdentifier();
				instanceMapID.put(instanceID, instanceMap.get(instance));
			}
			
			assertEquals(2, instanceMap.keySet().size());
			assertTrue(instanceMapID.keySet().contains(container.getWsmoFactory().createIRI(defaultNS + "Mary")));
			assertTrue(instanceMapID.keySet().contains(container.getWsmoFactory().createIRI(defaultNS + "Paul")));
			
			
			Set<Term> terms = instanceMapID.get(container.getWsmoFactory().createIRI(defaultNS + "Mary"));
			assertTrue(terms.contains((container.getWsmoFactory().createIRI(defaultNS + "Susan"))));
			assertTrue(terms.contains((container.getWsmoFactory().createIRI(defaultNS + "Paul"))));
			assertTrue(terms.contains((container.getWsmoFactory().createIRI(defaultNS + "George"))));
			
			terms = instanceMapID.get(container.getWsmoFactory().createIRI(defaultNS + "Paul"));
			assertTrue(terms.contains((container.getWsmoFactory().createIRI(defaultNS + "George"))));
		}
		
	}

	public void testGetInferingAttributeValuesInstance() throws InconsistencyException { 
		// Map<IRI, Set<Term>> getConstraintAttributeValues(Instance instance)
		// a map containing all constraint attributes of a specified
	    // instance and for each a set containing all its values
		
		List<Ontology> ontologies = loadOntologies(prefix + "Human.wsml");
		if (ontologies.isEmpty())
			fail();
		
		reasoner.registerOntologies(new HashSet<Ontology>(ontologies));

		/* **********
		 * Query
		 * **********/

		// query hasBirthdate
		
		{
			Identifier instanceID = container.getWsmoFactory().createIRI(defaultNS + "Paul"); 
			Instance instance = container.getWsmoFactory().createInstance(instanceID);
			Map<IRI, Set<Term>> valueMap = reasoner.getInferingAttributeValues(instance);
			
			System.out.println("All Inferring AttributeValues for Instance " + instanceID + ":");
			System.out.println(valueMap);
			
			/*
		      instance Paul memberOf { Parent, Man }
		            hasName hasValue "Paul Smith"
		            hasBirthdate hasValue xsd#date(1976,08,16)
		            hasChild hasValue George
		            hasCitizenship hasValue oo#de
		            hasAncestor		hasValue Frank
		  			hasMother		hasValue Mary
		            hasBirthplace	hasValue Innsbruck
		            isMarriedTo		hasValue Paula 
			 */
			
			assertEquals(8, valueMap.keySet().size());
			assertTrue(valueMap.keySet().contains(container.getWsmoFactory().createIRI(defaultNS + "hasName")));
			assertTrue(valueMap.keySet().contains(container.getWsmoFactory().createIRI(defaultNS + "hasChild")));
			assertTrue(valueMap.keySet().contains(container.getWsmoFactory().createIRI(defaultNS + "hasCitizenship")));
			assertTrue(valueMap.keySet().contains(container.getWsmoFactory().createIRI(defaultNS + "hasAncestor")));
			assertTrue(valueMap.keySet().contains(container.getWsmoFactory().createIRI(defaultNS + "hasRelative")));
			assertTrue(valueMap.keySet().contains(container.getWsmoFactory().createIRI(defaultNS + "hasMother")));
			assertTrue(valueMap.keySet().contains(container.getWsmoFactory().createIRI(defaultNS + "hasBirthplace")));
			assertTrue(valueMap.keySet().contains(container.getWsmoFactory().createIRI(defaultNS + "isMarriedTo")));
			assertFalse(valueMap.keySet().contains(container.getWsmoFactory().createIRI(defaultNS + "hasBirthdate")));
			
			Set<Term> terms = valueMap.get(container.getWsmoFactory().createIRI(defaultNS + "hasName"));
			assertEquals(1, terms.size());
			assertTrue(terms.contains(container.getXmlDataFactory().createString("Paul Smith")));

			terms = valueMap.get(container.getWsmoFactory().createIRI(defaultNS + "hasRelative"));
			assertEquals(4, terms.size());
			assertTrue(terms.contains(container.getWsmoFactory().createIRI(defaultNS + "Paula")));
			assertTrue(terms.contains(container.getWsmoFactory().createIRI(defaultNS + "Frank")));
			assertTrue(terms.contains(container.getWsmoFactory().createIRI(defaultNS + "Mary")));
			assertTrue(terms.contains(container.getWsmoFactory().createIRI(defaultNS + "George")));
		}

	}

	public void testGetInferingAttributeValuesInstanceIdentifier() throws InconsistencyException {
		// set containing data values of the given instance and constraint attribute
		
		List<Ontology> ontologies = loadOntologies(prefix + "Human.wsml");
		if (ontologies.isEmpty())
			fail();
		
		reasoner.registerOntologies(new HashSet<Ontology>(ontologies));

		/* **********
		 * Query
		 * **********/

		// query hasBirthdate
		
		{
			Identifier instanceID = container.getWsmoFactory().createIRI(defaultNS + "Paul2"); 
			Instance instance = container.getWsmoFactory().createInstance(instanceID);
			Identifier attributeID = container.getWsmoFactory().createIRI(defaultNS + "hasMother2");
			
			Set<Term> valueSet = reasoner.getInferingAttributeValues(instance, attributeID);
			
			System.out.println("All Inferring AttributeValues for Instance " + instanceID + " and Attribute " + attributeID + ":");
			System.out.println(valueSet);
			
			assertEquals(1, valueSet.size());
			assertTrue(valueSet.contains(container.getWsmoFactory().createIRI(defaultNS + "Mary").toString()));
		}

		{
			Identifier instanceID = container.getWsmoFactory().createIRI(defaultNS + "Paul2"); 
			Instance instance = container.getWsmoFactory().createInstance(instanceID);
			Identifier attributeID = container.getWsmoFactory().createIRI(defaultNS + "hasAncestor2");
			
			Set<Term> valueSet = reasoner.getInferingAttributeValues(instance, attributeID);
			
			assertEquals(0, valueSet.size());
			
		}
		
	}

	public void testGetInstances() throws InconsistencyException {
		ontology = loadOntology(prefix + "Human_concept.wsml");
		if (ontology == null)
			fail();
		
		reasoner.registerOntology(ontology);

		/* **********
		 * Query
		 * **********/

		IRI identifier = container.getWsmoFactory().createIRI("http://www.example.org/example/Human");
		Set<Instance> instances = reasoner.getInstances(container.getWsmoFactory().createConcept(identifier));

		System.out.println("Instances of Human:");
		System.out.println(instances);
		
		assertEquals(0, instances.size());

		identifier = container.getWsmoFactory().createIRI("http://www.example.org/example/Boy");
		instances = reasoner.getInstances(container.getWsmoFactory().createConcept(identifier));

		System.out.println("Instances of Boy:");
		System.out.println(instances);

		assertEquals(0, instances.size());

		identifier = container.getWsmoFactory().createIRI("http://www.example.org/example/Human");
		Set<Concept> subConcepts = reasoner.getSubConcepts(container.getWsmoFactory().createConcept(identifier));

		System.out.println("SubConcepts of Human:");
		System.out.println(subConcepts);

		assertEquals(7, subConcepts.size());

		identifier = container.getWsmoFactory().createIRI("http://www.example.org/example/Boy");
		Set<Concept> superConcepts = reasoner.getSuperConcepts(container.getWsmoFactory().createConcept(identifier));

		System.out.println("SuperConcepts of Boy:");
		System.out.println(superConcepts);

		assertEquals(4, superConcepts.size());
	}
	
	public void testGetInstances2() throws InconsistencyException {
		ontology = loadOntology(prefix + "Human_instance.wsml");
		if (ontology == null)
			fail();
		
		reasoner.registerOntology(ontology);

		/* **********
		 * Query
		 * **********/

		IRI identifier = container.getWsmoFactory().createIRI("http://www.example.org/example/Human");
		Set<Instance> instances = reasoner.getInstances(container.getWsmoFactory().createConcept(identifier));

		System.out.println("Instances of Human:");
		System.out.println(instances);
		
		assertEquals(4, instances.size());

		identifier = container.getWsmoFactory().createIRI("http://www.example.org/example/Boy");
		instances = reasoner.getInstances(container.getWsmoFactory().createConcept(identifier));

		System.out.println("Instances of Boy:");
		System.out.println(instances);

		assertEquals(1, instances.size());


		identifier = container.getWsmoFactory().createIRI("http://www.example.org/example/Man");
		instances = reasoner.getInstances(container.getWsmoFactory().createConcept(identifier));

		System.out.println("Instances of Man:");
		System.out.println(instances);

		assertEquals(2, instances.size());

		identifier = container.getWsmoFactory().createIRI("http://www.example.org/example/Parent");
		instances = reasoner.getInstances(container.getWsmoFactory().createConcept(identifier));

		System.out.println("Instances of Parent:");
		System.out.println(instances);

		assertEquals(2, instances.size());


		identifier = container.getWsmoFactory().createIRI("http://www.example.org/example/Child");
		instances = reasoner.getInstances(container.getWsmoFactory().createConcept(identifier));

		System.out.println("Instances of Child:");
		System.out.println(instances);

		assertEquals(1, instances.size());


	}

	public void testGetInverseRelations() {
		fail("Not yet implemented");
	}

	public void testGetRangesOfConstraintAttribute() throws InconsistencyException {
		ontology = loadOntology(prefix + "Human_of_type.wsml");
		if (ontology == null)
			fail();
		
		reasoner.registerOntology(ontology);

		/* **********
		 * Query
		 * **********/

		IRI mother = container.getWsmoFactory().createIRI("http://www.example.org/example/hasMother");
		Set<DataType> ranges = reasoner.getRangesOfConstraintAttribute(mother);

		System.out.println("Constraining Ranges of hasMother:");
		System.out.println(ranges);
		
		assertEquals(4, ranges.size());
		assertTrue(ranges.contains(container.getWsmoFactory().createIRI("http://www.example.org/example/FemaleHuman")));
		assertTrue(ranges.contains(container.getWsmoFactory().createIRI("http://www.example.org/example/Mother")));
		assertTrue(ranges.contains(container.getWsmoFactory().createIRI("http://www.example.org/example/SingleWoman")));
		assertTrue(ranges.contains(container.getWsmoFactory().createIRI("http://www.example.org/example/HappyMother")));
	}

	public void testGetRangesOfInferingAttribute() throws InconsistencyException {
		ontology = loadOntology(prefix + "Human_implies_type.wsml");
		if (ontology == null)
			fail();
		
		reasoner.registerOntology(ontology);

		/* **********
		 * Query
		 * **********/

		IRI mother = container.getWsmoFactory().createIRI("http://www.example.org/example/hasMother");
		Set<Type> ranges = reasoner.getRangesOfInferingAttribute(mother);

		System.out.println("Inferring Ranges of hasMother:");
		System.out.println(ranges);
		
		assertEquals(4, ranges.size());
		assertTrue(ranges.contains(container.getWsmoFactory().createIRI("http://www.example.org/example/Mother")));
		assertTrue(ranges.contains(container.getWsmoFactory().createIRI("http://www.example.org/example/FemaleHuman")));
		assertTrue(ranges.contains(container.getWsmoFactory().createIRI("http://www.example.org/example/Woman")));
		assertTrue(ranges.contains(container.getWsmoFactory().createIRI("http://www.example.org/example/Human")));
	}

	public void testGetSubConcepts() throws InconsistencyException {
		ontology = loadOntology(prefix + "Human_concept.wsml");
		if (ontology == null)
			fail();
		
		reasoner.registerOntology(ontology);

		/* **********
		 * Query
		 * **********/

		IRI identifier = container.getWsmoFactory().createIRI("http://www.example.org/example/Human");
		Set<Instance> instances = reasoner.getInstances(container.getWsmoFactory().createConcept(identifier));

		System.out.println("Instances of Human:");
		System.out.println(instances);
		
		assertEquals(0, instances.size());

		identifier = container.getWsmoFactory().createIRI("http://www.example.org/example/Boy");
		instances = reasoner.getInstances(container.getWsmoFactory().createConcept(identifier));

		System.out.println("Instances of Boy:");
		System.out.println(instances);

		assertEquals(0, instances.size());

		identifier = container.getWsmoFactory().createIRI("http://www.example.org/example/Human");
		Set<Concept> subConcepts = reasoner.getSubConcepts(container.getWsmoFactory().createConcept(identifier));

		System.out.println("SubConcepts of Human:");
		System.out.println(subConcepts);

		assertEquals(7, subConcepts.size());

		identifier = container.getWsmoFactory().createIRI("http://www.example.org/example/Boy");
		Set<Concept> superConcepts = reasoner.getSuperConcepts(container.getWsmoFactory().createConcept(identifier));

		System.out.println("SuperConcepts of Boy:");
		System.out.println(superConcepts);

		assertEquals(4, superConcepts.size());
	}

	public void testGetSubRelations() {
		fail("Not yet implemented");
	}

	public void testGetSuperConcepts() throws InconsistencyException {
		ontology = loadOntology(prefix + "Human_concept.wsml");
		if (ontology == null)
			fail();
		
		reasoner.registerOntology(ontology);

		/* **********
		 * Query
		 * **********/

		IRI identifier = container.getWsmoFactory().createIRI("http://www.example.org/example/Human");
		Set<Instance> instances = reasoner.getInstances(container.getWsmoFactory().createConcept(identifier));

		System.out.println("Instances of Human:");
		System.out.println(instances);
		
		assertEquals(0, instances.size());

		identifier = container.getWsmoFactory().createIRI("http://www.example.org/example/Boy");
		instances = reasoner.getInstances(container.getWsmoFactory().createConcept(identifier));

		System.out.println("Instances of Boy:");
		System.out.println(instances);

		assertEquals(0, instances.size());

		identifier = container.getWsmoFactory().createIRI("http://www.example.org/example/Human");
		Set<Concept> subConcepts = reasoner.getSubConcepts(container.getWsmoFactory().createConcept(identifier));

		System.out.println("SubConcepts of Human:");
		System.out.println(subConcepts);

		assertEquals(7, subConcepts.size());

		identifier = container.getWsmoFactory().createIRI("http://www.example.org/example/Boy");
		Set<Concept> superConcepts = reasoner.getSuperConcepts(container.getWsmoFactory().createConcept(identifier));

		System.out.println("SuperConcepts of Boy:");
		System.out.println(superConcepts);

		assertEquals(4, superConcepts.size());
	}

	public void testGetSuperRelations() throws InconsistencyException {
		ontology = loadOntology(prefix + "Human_of_type.wsml");
		if (ontology == null)
			fail();
		
		reasoner.registerOntology(ontology);

		/* **********
		 * Query
		 * **********/

		IRI mother = container.getWsmoFactory().createIRI("http://www.example.org/example/hasMother");

		Set<IRI> superAtts = reasoner.getSuperRelations(mother);
		IRI parent = container.getWsmoFactory().createIRI("http://www.example.org/example/hasParent");
		IRI ancestor = container.getWsmoFactory().createIRI("http://www.example.org/example/hasAncestor");
		IRI relative = container.getWsmoFactory().createIRI("http://www.example.org/example/hasRelative");

		System.out.println("SuperAtts of hasMother:");
		System.out.println(superAtts);

		assertTrue(superAtts.contains(parent));
		assertTrue(superAtts.contains(ancestor));
		assertTrue(superAtts.contains(relative));
	}

	public void testIsConceptSatisfiable() {
		fail("Not yet implemented");
	}

	public void testIsEquivalentConcept() {
		fail("Not yet implemented");
	}

	public void testIsMemberOf() {
		fail("Not yet implemented");
	}

	public void testIsSatisfiable() throws InconsistencyException {
		ontology = loadOntology(prefix + "Human_satisfiable.wsml");
		if (ontology == null)
			fail();
		
		reasoner.registerOntology(ontology);
		
		/* **********
		 * Query
		 * **********/

		assertTrue("The ontology must be satisfiable!", reasoner.isSatisfiable());
	}
	
	public void testIsUnsatisfiable() throws InconsistencyException {
		ontology = loadOntology(prefix + "Human_unsatisfiable.wsml");
		if (ontology == null)
			fail();
		
		// would throw exception otherwise
		reasoner.registerOntologyNoVerification(ontology);
		
		/* **********
		 * Query
		 * **********/

		assertFalse("The ontology must not be satisfiable!", reasoner.isSatisfiable());
	}

	public void testIsSubConceptOf() {
		fail("Not yet implemented");
	}

	public void testConvertEntities() {
		fail("Not yet implemented");
	}

	
	/**
	 * Utility Method to get the object model of a wsml ontology
	 * 
	 * @param file
	 *            location of source file (It will be attempted to be loaded from current class path)
	 * @return object model of ontology at file location
	 */
	Ontology loadOntology(String file) {
		Parser wsmlParser = new WsmlParser();

		InputStream is = this.getClass().getClassLoader().getResourceAsStream(file);
		try {
			final TopEntity[] identifiable = wsmlParser.parse(new InputStreamReader(is));
			if (identifiable.length > 0 && identifiable[0] instanceof Ontology) {
				return (Ontology) identifiable[0];
			} else {
				System.out.println("First Element of file no ontology ");
				return null;
			}

		} catch (Exception e) {
			System.out.println("Unable to parse ontology: " + e.getMessage());
			return null;
		}

	}
	
	/**
	 * Utility Method to get the object model of a wsml ontology
	 * 
	 * @param file
	 *            location of source file (It will be attempted to be loaded from current class path)
	 * @return object model of ontology at file location
	 */
	List<Ontology> loadOntologies(String file) {
		List<Ontology> ontologies = new ArrayList<Ontology>();
		Parser wsmlParser = new WsmlParser();

		InputStream is = this.getClass().getClassLoader().getResourceAsStream(file);
		try {
			final TopEntity[] identifiable = wsmlParser.parse(new InputStreamReader(is));
			if (identifiable.length > 0) {
				for (TopEntity te : identifiable) {
					if (te instanceof Ontology) {
						ontologies.add( (Ontology) te );
					}
				}
				return ontologies;
			} else {
				System.out.println("First Element of file no ontology ");
				return null;
			}

		} catch (Exception e) {
			System.out.println("Unable to parse ontology: " + e.getMessage());
			return null;
		}

	}

}
