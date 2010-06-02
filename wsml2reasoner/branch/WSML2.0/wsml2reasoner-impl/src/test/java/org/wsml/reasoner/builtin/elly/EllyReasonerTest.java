/**
 * WSML Reasoner Implementation.
 *
 * Copyright (c) 2005, University of Innsbruck, Austria.
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
package org.wsml.reasoner.builtin.elly;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;

import junit.framework.TestCase;

import org.omwg.ontology.Concept;
import org.omwg.ontology.Instance;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Type;
import org.sti2.wsmo4j.factory.WsmlFactoryContainer;
import org.wsml.reasoner.ResourceHelper;
import org.wsml.reasoner.api.DLReasoner;
import org.wsml.reasoner.api.inconsistency.InconsistencyException;
import org.wsml.reasoner.impl.DefaultWSMLReasonerFactory;
import org.wsmo.common.IRI;
import org.wsmo.common.TopEntity;
import org.wsmo.factory.FactoryContainer;
import org.wsmo.wsml.Parser;

import com.ontotext.wsmo4j.parser.wsml.WsmlParser;

/**
 * Usage Example for the wsml2Reasoner Framework
 * 
 * @author Holger Lausen, DERI Innsbruck
 */
public class EllyReasonerTest extends TestCase {

	private static String prefix = "";
	private DLReasoner reasoner;
	private FactoryContainer container;
	Ontology ontology;

	protected void setUp() throws Exception {
		container = new WsmlFactoryContainer();
		reasoner = DefaultWSMLReasonerFactory.getFactory().createDL2Reasoner(null);
	}

	public void testSatisfiable() throws InconsistencyException {
		ontology = loadOntology(prefix + "Human_satisfiable.wsml");
		if (ontology == null)
			fail();
		
		reasoner.registerOntology(ontology);
		
		/* **********
		 * Query
		 * **********/

		assertTrue("The ontology must be satisfiable!", reasoner.isSatisfiable());
	}

	public void testUnsatisfiable() throws InconsistencyException {
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

	public void testConcepts() throws InconsistencyException {
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

	public void testInstances() throws InconsistencyException {
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

//	public void testNominal() throws InconsistencyException {
//		fail("Nominals are not supported in WSMO4J yet");
//	}

	public void testOfType() throws InconsistencyException {
		ontology = loadOntology(prefix + "Human_of_type.wsml");
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
		
		assertEquals(3, ranges.size());

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

	public void testImpliesType() throws InconsistencyException {
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

	/**
	 * Utility Method to get the object model of a wsml ontology
	 * 
	 * @param file
	 *            location of source file (It will be attempted to be loaded from current class path)
	 * @return object model of ontology at file location
	 */
	Ontology loadOntology(String file) {
		Parser wsmlParser = new WsmlParser();

		InputStream is = ResourceHelper.loadResourceAsStream(file);
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

}




/*
public void testConcepts() throws InconsistencyException {
ontology = loadOntology("example/locations.wsml");
if (ontology == null)
	fail();

reasoner.registerOntology(ontology);


IRI identifier = container.getWsmoFactory().createIRI("http://www.example.org/example/Human");
Set<Instance> instances = reasoner.getInstances(container.getWsmoFactory().createConcept(identifier));

System.out.println("Instances of Human:");
System.out.println(instances);

identifier = container.getWsmoFactory().createIRI("http://www.example.org/example/Boy");
instances = reasoner.getInstances(container.getWsmoFactory().createConcept(identifier));

System.out.println("Instances of Boy:");
System.out.println(instances);

identifier = container.getWsmoFactory().createIRI("http://www.example.org/example/Human");
Set<Concept> subConcepts = reasoner.getSubConcepts(container.getWsmoFactory().createConcept(identifier));

System.out.println("SubConcepts of Human:");
System.out.println(subConcepts);

identifier = container.getWsmoFactory().createIRI("http://www.example.org/example/hasName");
System.out.println("Concepts of hasName: " + reasoner.getConceptsOf(identifier));
System.out.println("RangesOfConstraintAttribute of hasName: "
		+ reasoner.getRangesOfConstraintAttribute(identifier));
System.out
		.println("RangesOfInferingAttribute of hasName: " + reasoner.getRangesOfInferingAttribute(identifier));
System.out.println("ConstraintAttributeInstances of hasName: "
		+ reasoner.getConstraintAttributeInstances(identifier));
}

*/