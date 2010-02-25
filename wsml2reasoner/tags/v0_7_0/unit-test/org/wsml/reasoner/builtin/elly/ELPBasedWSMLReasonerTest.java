package org.wsml.reasoner.builtin.elly;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.deri.wsmo4j.io.parser.wsml.WsmlLogicalExpressionParser;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.MembershipMolecule;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Concept;
import org.omwg.ontology.DataType;
import org.omwg.ontology.DataValue;
import org.omwg.ontology.Instance;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Type;
import org.omwg.ontology.XmlSchemaDataType;
import org.sti2.wsmo4j.factory.WsmlFactoryContainer;
import org.wsml.reasoner.api.DLReasoner;
import org.wsml.reasoner.api.exception.InternalReasonerException;
import org.wsml.reasoner.api.inconsistency.InconsistencyException;
import org.wsml.reasoner.impl.DefaultWSMLReasonerFactory;
import org.wsml.reasoner.impl.ELPBasedWSMLReasoner;
import org.wsmo.common.IRI;
import org.wsmo.common.Identifier;
import org.wsmo.common.TopEntity;
import org.wsmo.factory.FactoryContainer;
import org.wsmo.wsml.Parser;
import org.wsmo.wsml.ParserException;

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

	public void testSetDisableConsitencyCheck() throws InconsistencyException {
		ontology = loadOntology(prefix + "Human_unsatisfiable.wsml");
		if (ontology == null)
			fail();

		((ELPBasedWSMLReasoner) reasoner).setDisableConsitencyCheck(true);
		reasoner.registerOntology(ontology);
	}

	public void testRegisterOntology() throws InconsistencyException {
		List<Ontology> ontologies = loadOntologies(prefix + "Human.wsml");
		if (ontologies.isEmpty())
			fail();

		reasoner.registerOntology(ontologies.get(0));
		reasoner.registerOntology(ontologies.get(1));
	}

	public void testRegisterOntologies() throws InconsistencyException {
		List<Ontology> ontologies = loadOntologies(prefix + "Human.wsml");
		if (ontologies.isEmpty())
			fail();

		reasoner.registerOntologies(new HashSet<Ontology>(ontologies));
	}

	public void testDeRegister() throws InconsistencyException {
		List<Ontology> ontologies = loadOntologies(prefix + "Human.wsml");
		if (ontologies.isEmpty())
			fail();

		reasoner.registerOntologies(new HashSet<Ontology>(ontologies));
		assertFalse(reasoner.getAllAttributes().size() == 0);

		try {
			reasoner.deRegister();
			reasoner.getAllAttributes().size();
			fail();
		} catch (InternalReasonerException e) {
			// fine
		}
	}

	public void testRegisterOntologyNoVerification() {
		ontology = loadOntology(prefix + "Human_unsatisfiable.wsml");
		if (ontology == null)
			fail();

		// would throw exception otherwise
		reasoner.registerOntologyNoVerification(ontology);
		reasoner.deRegister();

		try {
			reasoner.registerOntology(ontology);
			fail();
		} catch (InconsistencyException e) {
			// fine
		}
	}

	public void testIsEntailed() throws InconsistencyException, IllegalArgumentException, ParserException {
		List<Ontology> ontologies = loadOntologies(prefix + "Human.wsml");
		if (ontologies.isEmpty())
			fail();

		reasoner.registerOntologies(new HashSet<Ontology>(ontologies));

		/* **********
		 * Query
		 * **********/

		LogicalExpression expression = new WsmlLogicalExpressionParser(ontologies.get(0), container)
				.parse("Mary[hasName hasValue \"Maria Smith\"]");

		assertTrue(reasoner.isEntailed(expression));

		expression = new WsmlLogicalExpressionParser(ontologies.get(0), container)
				.parse("Mary[hasRelative hasValue George]");

		assertTrue(reasoner.isEntailed(expression));

		expression = new WsmlLogicalExpressionParser(ontologies.get(0), container)
				.parse("Mary[hasRelative hasValue George] and Mary[hasName hasValue \"Maria Smith\"]");

		assertTrue(reasoner.isEntailed(expression));

		expression = new WsmlLogicalExpressionParser(ontologies.get(0), container).parse("?x[hasChild hasValue George] and George memberOf Man");

		assertTrue(reasoner.isEntailed(expression));

		expression = new WsmlLogicalExpressionParser(ontologies.get(0), container).parse("George memberOf Child");

		assertTrue(reasoner.isEntailed(expression));

		expression = new WsmlLogicalExpressionParser(ontologies.get(0), container).parse("George memberOf Boy");

		assertTrue(reasoner.isEntailed(expression));

		expression = new WsmlLogicalExpressionParser(ontologies.get(0), container)
				.parse("Mary[hasRelative hasValue George] and George memberOf Boy");

		assertTrue(reasoner.isEntailed(expression));
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

		assertTrue(attributes.contains(container.getWsmoFactory().createIRI("http://www.example.org/example/hasName")));
		assertTrue(attributes.contains(container.getWsmoFactory().createIRI(
				"http://www.example.org/example/hasRelative")));
		assertTrue(attributes.contains(container.getWsmoFactory().createIRI(
				"http://www.example.org/example/hasAncestor")));
		assertTrue(attributes
				.contains(container.getWsmoFactory().createIRI("http://www.example.org/example/hasParent")));
		assertTrue(attributes
				.contains(container.getWsmoFactory().createIRI("http://www.example.org/example/hasMother")));
		assertTrue(attributes.contains(container.getWsmoFactory().createIRI("http://www.example.org/example/hasMum")));
		assertTrue(attributes.contains(container.getWsmoFactory().createIRI("http://www.example.org/example/hasChild")));
		assertTrue(attributes.contains(container.getWsmoFactory().createIRI(
				"http://www.example.org/example/hasWeightInKG")));
		assertTrue(attributes.contains(container.getWsmoFactory().createIRI(
				"http://www.example.org/example/hasBirthdate")));
		assertTrue(attributes.contains(container.getWsmoFactory().createIRI(
				"http://www.example.org/example/dateOfDeath")));
		assertTrue(attributes.contains(container.getWsmoFactory().createIRI(
				"http://www.example.org/example/hasBirthplace")));
		assertTrue(attributes.contains(container.getWsmoFactory().createIRI(
				"http://www.example.org/example/isMarriedTo")));
		assertTrue(attributes.contains(container.getWsmoFactory().createIRI(
				"http://www.example.org/example/hasCitizenship")));
		assertTrue(attributes.contains(container.getWsmoFactory().createIRI("http://www.example.org/example/isAlive")));
		assertTrue(attributes.contains(container.getWsmoFactory().createIRI("http://www.example.org/example/hasName2")));
		assertTrue(attributes.contains(container.getWsmoFactory().createIRI(
				"http://www.example.org/example/hasRelative2")));
		assertTrue(attributes.contains(container.getWsmoFactory().createIRI(
				"http://www.example.org/example/hasAncestor2")));
		assertTrue(attributes.contains(container.getWsmoFactory()
				.createIRI("http://www.example.org/example/hasParent2")));
		assertTrue(attributes.contains(container.getWsmoFactory()
				.createIRI("http://www.example.org/example/hasMother2")));
		assertTrue(attributes
				.contains(container.getWsmoFactory().createIRI("http://www.example.org/example/hasChild2")));
		assertTrue(attributes.contains(container.getWsmoFactory().createIRI(
				"http://www.example.org/example/hasWeightInKG2")));
		assertTrue(attributes.contains(container.getWsmoFactory().createIRI(
				"http://www.example.org/example/hasBirthdate2")));
		assertEquals(22, attributes.size());
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
		assertTrue(conceptIdentifiers
				.contains(container.getWsmoFactory().createIRI("http://www.example.org/foaf#name")));
		assertTrue(conceptIdentifiers.contains(container.getWsmoFactory().createIRI(defaultNS + "FemaleHuman")));
		assertTrue(conceptIdentifiers.contains(container.getWsmoFactory().createIRI(defaultNS + "Mother")));
		assertTrue(conceptIdentifiers.contains(container.getWsmoFactory().createIRI(
				"http://www.example.org/loc#location")));
		assertTrue(conceptIdentifiers.contains(container.getWsmoFactory()
				.createIRI("http://www.example.org/oo#country")));
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
		assertTrue(constrainingAttributes.contains(container.getWsmoFactory().createIRI(defaultNS + "dateOfDeath"))); //	ofType xsd#date
		assertTrue(constrainingAttributes.contains(container.getWsmoFactory().createIRI(defaultNS + "isAlive"))); //	ofType xsd#boolean
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

		assertEquals(11, instances.size());
		assertTrue(instanceIDs.contains(container.getWsmoFactory().createIRI(defaultNS + "Mary"))); // memberOf {Parent, Woman}
		assertTrue(instanceIDs.contains(container.getWsmoFactory().createIRI(defaultNS + "Paul"))); // memberOf { Parent, Man }
		assertTrue(instanceIDs.contains(container.getWsmoFactory().createIRI(defaultNS + "Susan"))); // memberOf Woman
		assertTrue(instanceIDs.contains(container.getWsmoFactory().createIRI(defaultNS + "George"))); // memberOf Man
		assertTrue(instanceIDs.contains(container.getWsmoFactory().createIRI(defaultNS + "Paul2"))); // memberOf { Parent, Man }
		assertTrue(instanceIDs.contains(container.getWsmoFactory().createIRI(defaultNS + "George2"))); // memberOf Man
		assertTrue(instanceIDs.contains(container.getWsmoFactory().createIRI("http://www.example.org/oo#de")));
		assertTrue(instanceIDs.contains(container.getWsmoFactory().createIRI("http://www.example.org/oo#en")));
		assertTrue(instanceIDs.contains(container.getWsmoFactory().createIRI(defaultNS + "Innsbruck")));
		assertTrue(instanceIDs.contains(container.getWsmoFactory().createIRI(defaultNS + "Frank")));
		assertTrue(instanceIDs.contains(container.getWsmoFactory().createIRI(defaultNS + "Paula")));
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

			assertTrue(conceptIDs.contains(container.getWsmoFactory().createIRI(defaultNS + "Parent")));
			assertTrue(conceptIDs.contains(container.getWsmoFactory().createIRI(defaultNS + "Woman")));
			assertTrue(conceptIDs.contains(container.getWsmoFactory().createIRI(defaultNS + "Human")));
			assertTrue(conceptIDs.contains(container.getWsmoFactory().createIRI(defaultNS + "Mother2")));
			assertTrue(conceptIDs.contains(container.getWsmoFactory().createIRI(defaultNS + "FemaleHuman")));
			assertTrue(conceptIDs.contains(container.getWsmoFactory().createIRI(defaultNS + "Mother")));
			assertEquals(6, conceptsOfInstance.size());
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

			assertTrue(conceptIDs.contains(container.getWsmoFactory().createIRI(defaultNS + "Parent")));
			assertTrue(conceptIDs.contains(container.getWsmoFactory().createIRI(defaultNS + "Human")));
			assertTrue(conceptIDs.contains(container.getWsmoFactory().createIRI(defaultNS + "Man2")));
			assertTrue(conceptIDs.contains(container.getWsmoFactory().createIRI(defaultNS + "Human2")));
			assertEquals(4, conceptsOfInstance.size());
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

			assertTrue(conceptIDs.contains(container.getWsmoFactory().createIRI(defaultNS + "Human")));
			assertTrue(conceptIDs.contains(container.getWsmoFactory().createIRI(defaultNS + "Man")));
			assertTrue(conceptIDs.contains(container.getWsmoFactory().createIRI(defaultNS + "Daddy")));
			assertTrue(conceptIDs.contains(container.getWsmoFactory().createIRI(defaultNS + "Father")));
			assertTrue(conceptIDs.contains(container.getWsmoFactory().createIRI(defaultNS + "Woman")));
			assertTrue(conceptIDs.contains(container.getWsmoFactory().createIRI(defaultNS + "Parent")));
			assertTrue(conceptIDs.contains(container.getWsmoFactory().createIRI(defaultNS + "Child")));
			assertTrue(conceptIDs.contains(container.getWsmoFactory().createIRI(defaultNS + "Girl")));
			assertTrue(conceptIDs.contains(container.getWsmoFactory().createIRI(defaultNS + "Boy")));
			assertTrue(conceptIDs.contains(container.getWsmoFactory().createIRI(defaultNS + "Woman2")));
			assertTrue(conceptIDs.contains(container.getWsmoFactory().createIRI(defaultNS + "Girl2")));
			assertEquals(11, conceptsOfAttribute.size());
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

			assertTrue(conceptIDs.contains(container.getWsmoFactory().createIRI(defaultNS + "Parent")));
			assertTrue(conceptIDs.contains(container.getWsmoFactory().createIRI(defaultNS + "Daddy")));
			assertTrue(conceptIDs.contains(container.getWsmoFactory().createIRI(defaultNS + "Father")));
			assertEquals(3, conceptsOfAttribute.size());
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

			assertTrue(instanceMapID.keySet().contains(container.getWsmoFactory().createIRI(defaultNS + "Mary")));
			assertTrue(instanceMapID.keySet().contains(container.getWsmoFactory().createIRI(defaultNS + "Paul")));
			assertTrue(instanceMapID.keySet().contains(container.getWsmoFactory().createIRI(defaultNS + "Susan")));
			assertTrue(instanceMapID.keySet().contains(container.getWsmoFactory().createIRI(defaultNS + "George")));
			assertTrue(instanceMapID.keySet().contains(container.getWsmoFactory().createIRI(defaultNS + "Paul2")));
			assertEquals(5, instanceMap.keySet().size());

			Set<DataValue> terms = instanceMapID.get(container.getWsmoFactory().createIRI(defaultNS + "Mary"));
			assertTrue(terms.contains(container.getXmlDataFactory().createDate(1949, 9, 12, 0, 0)));

			terms = instanceMapID.get(container.getWsmoFactory().createIRI(defaultNS + "Paul2"));
			assertTrue(terms.contains(container.getXmlDataFactory().createDate(1976, 8, 16, 0, 0)));
			assertTrue(terms.contains(container.getXmlDataFactory().createDate(1967, 8, 16, 0, 0)));
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

			/*
			  instance Paul2 memberOf { Parent, Man2 }
			        hasName hasValue "Paul Smith"
			        hasBirthdate hasValue xsd#date(1976,08,16)
			        hasBirthdate hasValue xsd#date(1967,08,16)
			        hasCitizenship hasValue oo#en
			        hasMother2 hasValue Mary
			 */

			assertTrue(valueMap.keySet().contains(container.getWsmoFactory().createIRI(defaultNS + "hasBirthdate")));
			assertEquals(1, valueMap.keySet().size());

			Set<DataValue> terms = valueMap.get(container.getWsmoFactory().createIRI(defaultNS + "hasBirthdate"));
			assertTrue(terms.contains(container.getXmlDataFactory().createDate(1976, 8, 16, 0, 0)));
			assertTrue(terms.contains(container.getXmlDataFactory().createDate(1967, 8, 16, 0, 0)));
			assertEquals(2, terms.size());
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

			assertTrue(valueMap.keySet().contains(container.getWsmoFactory().createIRI(defaultNS + "hasBirthdate")));
			assertTrue(valueMap.keySet().contains(container.getWsmoFactory().createIRI(defaultNS + "isAlive")));
			assertTrue(valueMap.keySet().contains(container.getWsmoFactory().createIRI(defaultNS + "hasWeightInKG")));
			assertTrue(valueMap.keySet().contains(container.getWsmoFactory().createIRI(defaultNS + "dateOfDeath")));
			assertEquals(4, valueMap.keySet().size());

			Set<DataValue> terms = valueMap.get(container.getWsmoFactory().createIRI(defaultNS + "hasBirthdate"));
			assertTrue(terms.contains(container.getXmlDataFactory().createDate(1949, 9, 12, 0, 0)));

			terms = valueMap.get(container.getWsmoFactory().createIRI(defaultNS + "isAlive"));
			assertTrue(terms.contains(container.getXmlDataFactory().createBoolean(true)));

			terms = valueMap.get(container.getWsmoFactory().createIRI(defaultNS + "hasWeightInKG"));
			assertTrue(terms.contains(container.getXmlDataFactory().createFloat("60.3")));

			terms = valueMap.get(container.getWsmoFactory().createIRI(defaultNS + "dateOfDeath"));
			assertTrue(terms.contains(container.getXmlDataFactory().createDate(2049, 9, 12, 0, 0)));

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

			System.out.println("All Constraint AttributeValues for Instance " + instanceID + " and Attribute "
					+ attributeID + ":");
			System.out.println(valueSet);

			assertTrue(valueSet.contains(container.getXmlDataFactory().createDate(1976, 8, 16, 0, 0)));
			assertTrue(valueSet.contains(container.getXmlDataFactory().createDate(1967, 8, 16, 0, 0)));
			assertEquals(2, valueSet.size());
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

			System.out.println("All Constraint AttributeValues for Instance " + instanceID + " and Attribute "
					+ attributeID + ":");
			System.out.println(valueSet);

			assertTrue(valueSet.contains(container.getXmlDataFactory().createFloat("60.3")));
			assertEquals(1, valueSet.size());
		}

	}

	// not supported in elp
	//	public void testGetDirectConcepts() {
	//		fail("Not yet implemented");
	//	}
	//
	//	public void testGetDirectSubConcepts() throws InconsistencyException {
	//		fail("Not yet implemented");
	//	}
	//
	//	public void testGetDirectSubRelations() {
	//		fail("Not yet implemented");
	//	}
	//
	//	public void testGetDirectSuperConcepts() {
	//		fail("Not yet implemented");
	//	}
	//
	//	public void testGetDirectSuperRelations() {
	//		fail("Not yet implemented");
	//	}

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
			System.out.println(eqConcepts);

			assertTrue(eqConcepts.contains(container.getWsmoFactory().createConcept(
					container.getWsmoFactory().createIRI(defaultNS + "Father"))));
			assertTrue(eqConcepts.contains(container.getWsmoFactory().createConcept(
					container.getWsmoFactory().createIRI(defaultNS + "Daddy"))));
			assertEquals(2, eqConcepts.size());
		}
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
			Set<IRI> eqRoles = reasoner.getEquivalentRelations(attID);

			System.out.println("Equal Attributes of " + attID);
			System.out.println(eqRoles);

			assertTrue(eqRoles.contains((container.getWsmoFactory().createIRI(defaultNS + "hasMum"))));
			assertTrue(eqRoles.contains((container.getWsmoFactory().createIRI(defaultNS + "hasMother"))));
			assertEquals(2, eqRoles.size());
		}
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

			assertTrue(instanceMapID.keySet().contains(container.getWsmoFactory().createIRI(defaultNS + "Mary")));
			assertTrue(instanceMapID.keySet().contains(container.getWsmoFactory().createIRI(defaultNS + "Paul")));
			assertEquals(2, instanceMap.keySet().size());

			Set<Term> terms = instanceMapID.get(container.getWsmoFactory().createIRI(defaultNS + "Mary"));
			assertTrue(terms.contains((container.getWsmoFactory().createIRI(defaultNS + "Susan"))));
			assertTrue(terms.contains((container.getWsmoFactory().createIRI(defaultNS + "Paul"))));
			assertTrue(terms.contains((container.getWsmoFactory().createIRI(defaultNS + "George"))));
			assertEquals(3, terms.size());

			terms = instanceMapID.get(container.getWsmoFactory().createIRI(defaultNS + "Paul"));
			assertTrue(terms.contains((container.getWsmoFactory().createIRI(defaultNS + "George"))));
			assertTrue(terms.contains((container.getWsmoFactory().createIRI(defaultNS + "Paula"))));
			assertTrue(terms.contains((container.getWsmoFactory().createIRI(defaultNS + "Frank"))));
			assertTrue(terms.contains((container.getWsmoFactory().createIRI(defaultNS + "Mary"))));
			assertEquals(4, terms.size());
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

			assertTrue(valueMap.keySet().contains(container.getWsmoFactory().createIRI(defaultNS + "hasName")));
			assertTrue(valueMap.keySet().contains(container.getWsmoFactory().createIRI(defaultNS + "hasChild")));
			assertTrue(valueMap.keySet().contains(container.getWsmoFactory().createIRI(defaultNS + "hasCitizenship")));
			assertTrue(valueMap.keySet().contains(container.getWsmoFactory().createIRI(defaultNS + "hasAncestor")));
			assertTrue(valueMap.keySet().contains(container.getWsmoFactory().createIRI(defaultNS + "hasRelative")));
			assertTrue(valueMap.keySet().contains(container.getWsmoFactory().createIRI(defaultNS + "hasParent")));
			assertTrue(valueMap.keySet().contains(container.getWsmoFactory().createIRI(defaultNS + "hasBirthplace")));
			assertTrue(valueMap.keySet().contains(container.getWsmoFactory().createIRI(defaultNS + "isMarriedTo")));
			assertFalse(valueMap.keySet().contains(container.getWsmoFactory().createIRI(defaultNS + "hasBirthdate")));
			assertTrue(valueMap.keySet().contains(container.getWsmoFactory().createIRI(defaultNS + "hasMother")));
			
			// This test fails because the calculation would take too long (45 seconds on 2.4 GHz quadcore (utilizing 1 core ;) ))
			// change in ELPBasedWSMLReasoner.synchronize to fix
			// if enabled, the test passes
			assertTrue(valueMap.keySet().contains(container.getWsmoFactory().createIRI(defaultNS + "hasMum"))); // since it is sub-attribute to hasMother
			assertEquals(10, valueMap.keySet().size());

			Set<Term> terms = valueMap.get(container.getWsmoFactory().createIRI(defaultNS + "hasName"));
			assertTrue(terms.contains(container.getXmlDataFactory().createString("Paul Smith")));
			assertEquals(1, terms.size());

			terms = valueMap.get(container.getWsmoFactory().createIRI(defaultNS + "hasRelative"));
			assertTrue(terms.contains(container.getWsmoFactory().createIRI(defaultNS + "Paula")));
			assertTrue(terms.contains(container.getWsmoFactory().createIRI(defaultNS + "Frank")));
			assertTrue(terms.contains(container.getWsmoFactory().createIRI(defaultNS + "Mary")));
			assertTrue(terms.contains(container.getWsmoFactory().createIRI(defaultNS + "George")));
			assertEquals(4, terms.size());
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

			System.out.println("All Inferring AttributeValues for Instance " + instanceID + " and Attribute "
					+ attributeID + ":");
			System.out.println(valueSet);

			assertTrue(valueSet.contains(container.getWsmoFactory().createIRI(defaultNS + "Mary")));
			assertEquals(1, valueSet.size());
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

	public void testGetInstances2() throws InconsistencyException, IllegalArgumentException, ParserException {
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
		
		LogicalExpression expression = new WsmlLogicalExpressionParser(ontology, container).parse("George memberOf Boy");

		assertTrue(reasoner.isEntailed(expression));


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

	// not supported in elp
	//	public void testGetInverseRelations() {
	//		fail("Not yet implemented");
	//	}

	public void testGetRangesOfConstraintAttribute() throws InconsistencyException {
		ontology = loadOntology(prefix + "Human_of_type.wsml");
		if (ontology == null)
			fail();

		reasoner.registerOntology(ontology);

		/* **********
		 * Query
		 * **********/

		IRI mother = container.getWsmoFactory().createIRI(defaultNS + "hasBirthdate");
		Set<DataType> ranges = reasoner.getRangesOfConstraintAttribute(mother);

		System.out.println("Constraining Ranges of hasMother:");
		System.out.println(ranges);

		assertEquals(1, ranges.size());
		assertTrue(ranges.contains(container.getXmlDataFactory().createDataType(XmlSchemaDataType.XSD_DATE)));
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

		assertTrue(ranges.contains(container.getWsmoFactory().createConcept(
				container.getWsmoFactory().createIRI("http://www.example.org/example/Mother"))));
		assertTrue(ranges.contains(container.getWsmoFactory().createConcept(
				container.getWsmoFactory().createIRI("http://www.example.org/example/FemaleHuman"))));
		assertTrue(ranges.contains(container.getWsmoFactory().createConcept(
				container.getWsmoFactory().createIRI("http://www.example.org/example/Woman"))));
		assertTrue(ranges.contains(container.getWsmoFactory().createConcept(
				container.getWsmoFactory().createIRI("http://www.example.org/example/Human"))));
		assertEquals(4, ranges.size());
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

	public void testGetSubRelations() throws InconsistencyException {
		ontology = loadOntology(prefix + "Human_of_type.wsml");
		if (ontology == null)
			fail();

		reasoner.registerOntology(ontology);

		/* **********
		 * Query
		 * **********/

		{
			IRI mum = container.getWsmoFactory().createIRI("http://www.example.org/example/hasMum");

			Set<IRI> subAtts = reasoner.getSubRelations(mum);
			System.out.println("SubAtts of hasMum:");
			System.out.println(subAtts);

			IRI mother = container.getWsmoFactory().createIRI("http://www.example.org/example/hasMother");
			assertTrue(subAtts.contains(mother));
			assertTrue(subAtts.contains(mum));
			assertEquals(2, subAtts.size());
		}
		{
			IRI ancestor = container.getWsmoFactory().createIRI("http://www.example.org/example/hasAncestor");

			Set<IRI> subAtts = reasoner.getSubRelations(ancestor);
			System.out.println("SubAtts of hasAncestor:");
			System.out.println(subAtts);

			IRI mother = container.getWsmoFactory().createIRI("http://www.example.org/example/hasMother");
			IRI mum = container.getWsmoFactory().createIRI("http://www.example.org/example/hasMum");
			IRI parent = container.getWsmoFactory().createIRI("http://www.example.org/example/hasParent");

			assertTrue(subAtts.contains(parent));
			assertTrue(subAtts.contains(ancestor));
			assertTrue(subAtts.contains(mother));
			assertTrue(subAtts.contains(mum));
			assertEquals(4, subAtts.size());
		}
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

		{
			IRI mother = container.getWsmoFactory().createIRI("http://www.example.org/example/hasMother");
	
			Set<IRI> superAtts = reasoner.getSuperRelations(mother);
			IRI mum = container.getWsmoFactory().createIRI("http://www.example.org/example/hasMum");
			IRI parent = container.getWsmoFactory().createIRI("http://www.example.org/example/hasParent");
			IRI ancestor = container.getWsmoFactory().createIRI("http://www.example.org/example/hasAncestor");
			IRI relative = container.getWsmoFactory().createIRI("http://www.example.org/example/hasRelative");
	
			System.out.println("SuperAtts of hasMother:");
			System.out.println(superAtts);
	
			assertTrue(superAtts.contains(parent));
			assertTrue(superAtts.contains(ancestor));
			assertTrue(superAtts.contains(relative));
			assertTrue(superAtts.contains(mother));
			assertTrue(superAtts.contains(mum));
			assertEquals(5, superAtts.size());
		}
	}

	public void testIsConceptSatisfiable() throws InconsistencyException {
		ontology = loadOntology(prefix + "Human_unsatisfiable_concept.wsml");
		if (ontology == null)
			fail();

		reasoner.registerOntology(ontology);

		/* **********
		 * Query
		 * **********/

		assertTrue(reasoner.isSatisfiable());

		MembershipMolecule boy = container.getLogicalExpressionFactory().createMemberShipMolecule(
				container.getWsmoFactory().createIRI("http://www.example.org/example/aBoy"), // dummy
				container.getWsmoFactory().createIRI("http://www.example.org/example/Boy"));

		assertFalse(reasoner.isConceptSatisfiable(boy));
	}

	public void testIsEquivalentConcept() throws InconsistencyException {

		List<Ontology> ontologies = loadOntologies(prefix + "Human.wsml");
		if (ontologies.isEmpty())
			fail();

		reasoner.registerOntologies(new HashSet<Ontology>(ontologies));

		/* **********
		 * Query
		 * **********/

		{
			assertTrue(reasoner.isEquivalentConcept(container.getWsmoFactory().createConcept(
					container.getWsmoFactory().createIRI(defaultNS + "Father")), container.getWsmoFactory()
					.createConcept(container.getWsmoFactory().createIRI(defaultNS + "Daddy"))));
		}
	}

	public void testIsMemberOf() throws InconsistencyException {

		ontology = loadOntology(prefix + "Human_instance.wsml");
		if (ontology == null)
			fail();

		reasoner.registerOntology(ontology);

		/* **********
		 * Query
		 * **********/

		{
			Identifier conceptID = container.getWsmoFactory().createIRI(defaultNS + "Woman");
			Concept concept = container.getWsmoFactory().createConcept(conceptID);
			Identifier instanceID = container.getWsmoFactory().createIRI(defaultNS + "Mary");
			Instance instance = container.getWsmoFactory().createInstance(instanceID);

			assertTrue(reasoner.isMemberOf(instance, concept));
		}

		{
			Identifier conceptID = container.getWsmoFactory().createIRI(defaultNS + "Mother");
			Concept concept = container.getWsmoFactory().createConcept(conceptID);
			Identifier instanceID = container.getWsmoFactory().createIRI(defaultNS + "Mary");
			Instance instance = container.getWsmoFactory().createInstance(instanceID);

			assertTrue(reasoner.isMemberOf(instance, concept));
		}
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

	public void testIsSubConceptOf() throws InconsistencyException {
		ontology = loadOntology(prefix + "Human_concept.wsml");
		if (ontology == null)
			fail();

		reasoner.registerOntology(ontology);

		/* **********
		 * Query
		 * **********/

		IRI humanID = container.getWsmoFactory().createIRI("http://www.example.org/example/Human");
		Concept human = container.getWsmoFactory().createConcept(humanID);

		IRI boyID = container.getWsmoFactory().createIRI("http://www.example.org/example/Boy");
		Concept boy = container.getWsmoFactory().createConcept(boyID);

		assertTrue(reasoner.isSubConceptOf(boy, human));
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
						ontologies.add((Ontology) te);
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
