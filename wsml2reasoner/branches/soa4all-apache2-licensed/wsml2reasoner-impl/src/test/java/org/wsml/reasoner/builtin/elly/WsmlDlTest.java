/*
 * Copyright 2011, Semantic Technology Institute (STI) Innsbruck, 
 * University of Innsbruck, Technikerstrasse 21a, 6020 Innsbruck, Austria.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wsml.reasoner.builtin.elly;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.deri.wsmo4j.io.parser.wsml.WsmlLogicalExpressionParser;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Concept;
import org.omwg.ontology.DataValue;
import org.omwg.ontology.Instance;
import org.omwg.ontology.Ontology;
import org.sti2.wsmo4j.factory.WsmlFactoryContainer;
import org.wsml.reasoner.ResourceHelper;
import org.wsml.reasoner.api.DLReasoner;
import org.wsml.reasoner.impl.DefaultWSMLReasonerFactory;
import org.wsmo.common.IRI;
import org.wsmo.common.TopEntity;
import org.wsmo.factory.FactoryContainer;
import org.wsmo.wsml.Parser;
import org.wsmo.wsml.ParserException;

import com.ontotext.wsmo4j.parser.wsml.WsmlParser;

public class WsmlDlTest extends TestCase {

	private static String defaultNS = "http://www.example.org#";
	private static String prefix = "";
	private DLReasoner reasoner;
	private FactoryContainer container;
	Ontology ontology;

	protected void setUp() throws Exception {
		container = new WsmlFactoryContainer();
		reasoner = DefaultWSMLReasonerFactory.getFactory().createDL2Reasoner(null);
		
		ontology = loadOntology(prefix + "wsml-dl-v2.wsml");
		if (ontology == null)
			fail();
		
		reasoner.registerOntology(ontology);
	}
	
	public void testMargeLovesNarcist() {
		
		IRI identifier = container.getWsmoFactory().createIRI(defaultNS + "NarcistLover");
		Set<Instance> instances = reasoner.getInstances(container.getWsmoFactory().createConcept(identifier));

		System.out.println("Instances of NarcistLover:");
		System.out.println(instances);

		assertTrue(instances.contains(container.getWsmoFactory().createInstance(container.getWsmoFactory().createIRI(defaultNS + "Marge"))));
		assertTrue(instances.contains(container.getWsmoFactory().createInstance(container.getWsmoFactory().createIRI(defaultNS + "Homer"))));
		assertEquals(2, instances.size());
	}
	
	
	public void testLisaDislikesSteakSandwich() {
		
		IRI dislikes = container.getWsmoFactory().createIRI(defaultNS + "dislikes");
		Instance lisa = container.getWsmoFactory().createInstance(container.getWsmoFactory().createIRI(defaultNS + "Lisa"));
		Set<Term> dislikeValues = reasoner.getInferingAttributeValues(lisa, dislikes);

		System.out.println("Lisa dislikes:");
		System.out.println(dislikeValues);

		assertTrue(dislikeValues.contains(container.getWsmoFactory().createIRI(defaultNS + "Steak")));
		assertTrue(dislikeValues.contains(container.getWsmoFactory().createIRI(defaultNS + "SteakSandwich")));
		assertEquals(2, dislikeValues.size());
	}
	
	
	
	public void testPetsLoveThemselves() {
		
		Concept pet = container.getWsmoFactory().createConcept(container.getWsmoFactory().createIRI(defaultNS + "Pet"));
		Concept lovesSelf = container.getWsmoFactory().createConcept(container.getWsmoFactory().createIRI(defaultNS + "LovesSelf"));
		Instance santasLittleHelper = container.getWsmoFactory().createInstance(container.getWsmoFactory().createIRI(defaultNS + "SantasLittleHelper"));
		Instance snowball2 = container.getWsmoFactory().createInstance(container.getWsmoFactory().createIRI(defaultNS + "Snowball2"));
		Instance bart = container.getWsmoFactory().createInstance(container.getWsmoFactory().createIRI(defaultNS + "Bart"));
		Instance homer = container.getWsmoFactory().createInstance(container.getWsmoFactory().createIRI(defaultNS + "Homer"));
		Instance lisa = container.getWsmoFactory().createInstance(container.getWsmoFactory().createIRI(defaultNS + "Lisa"));
		Instance elBarto = container.getWsmoFactory().createInstance(container.getWsmoFactory().createIRI(defaultNS + "ElBarto"));
		Instance marge = container.getWsmoFactory().createInstance(container.getWsmoFactory().createIRI(defaultNS + "Marge"));
		Instance maggie = container.getWsmoFactory().createInstance(container.getWsmoFactory().createIRI(defaultNS + "Maggie"));
		IRI loves = container.getWsmoFactory().createIRI(defaultNS + "loves");

		Set<Instance> instances = reasoner.getInstances(pet);

		System.out.println("Instances of Pet:");
		System.out.println(instances);

		
		assertTrue(instances.contains(santasLittleHelper));
		assertTrue(instances.contains(snowball2));
		assertEquals(2, instances.size());
		
		instances = reasoner.getInstances(lovesSelf);

		System.out.println("Instances of LovesSelf:");
		System.out.println(instances);

		
		assertTrue(instances.contains(homer));
		assertTrue(instances.contains(santasLittleHelper));
		assertTrue(instances.contains(snowball2));
		assertEquals(3, instances.size());
		
		
		Set<Term> bartLoves = reasoner.getInferingAttributeValues(bart, loves);
		assertTrue(bartLoves.contains(santasLittleHelper.getIdentifier()));

		Set<Term> homerLoves = reasoner.getInferingAttributeValues(homer, loves);
		System.out.println("Homer loves: " + homerLoves);
		assertTrue(homerLoves.contains(homer.getIdentifier()));
		assertTrue(homerLoves.contains(marge.getIdentifier()));
		assertTrue(homerLoves.contains(maggie.getIdentifier()));
		assertTrue(homerLoves.contains(lisa.getIdentifier()));
		assertTrue(homerLoves.contains(bart.getIdentifier()));
		assertTrue(homerLoves.contains(elBarto.getIdentifier()));
		assertEquals(6, homerLoves.size());
	
		Set<Term> slhLoves = reasoner.getInferingAttributeValues(santasLittleHelper, loves);
		System.out.println("santasLittleHelper loves: " + slhLoves);
		assertTrue(slhLoves.contains(santasLittleHelper.getIdentifier()));
	}
	
	/*
	a) Who is "El Barto"?
	ElBarto[name hasValue ?name] and ElBarto[ageInYears hasValue ?age]
	*/
	public void testElBarto() {
		IRI ageInYears = container.getWsmoFactory().createIRI(defaultNS + "ageInYears");
		IRI name = container.getWsmoFactory().createIRI(defaultNS + "name");
		Instance bart = container.getWsmoFactory().createInstance(container.getWsmoFactory().createIRI(defaultNS + "ElBarto"));
		Set<DataValue> values = reasoner.getConstraintAttributeValues(bart, name);

		System.out.println("ElBarto's name:");
		System.out.println(values);

		assertTrue(values.contains(container.getXmlDataFactory().createString("Bart Simpson")));
		assertTrue(values.contains(container.getXmlDataFactory().createString("El Barto")));
		assertEquals(2, values.size());

	
		values = reasoner.getConstraintAttributeValues(bart, ageInYears);
		System.out.println("ElBarto's ageInYears:");
		System.out.println(values);

		DataValue value10 = container.getXmlDataFactory().createInteger("10");
		DataValue queryValue = values.iterator().next(); // FIXME problem is that decimals are created in any numeric case 
		assertEquals(value10, queryValue);
		assertTrue("Value " + value10 + " not contained in values: " + values, values.contains(value10));
		assertEquals(1, values.size());
	}
	
	public void testEntailment() throws IllegalArgumentException, ParserException {

		LogicalExpression expression = new WsmlLogicalExpressionParser(ontology, container)
				.parse("Maggie memberOf Child");

		assertTrue(reasoner.isEntailed(expression));

		expression = new WsmlLogicalExpressionParser(ontology, container)
		.parse("Maggie memberOf Man");

		assertFalse(reasoner.isEntailed(expression));

		expression = new WsmlLogicalExpressionParser(ontology, container)
		.parse("Bart memberOf Child");

		assertTrue(reasoner.isEntailed(expression));

		expression = new WsmlLogicalExpressionParser(ontology, container)
		.parse("Bart memberOf Woman");

		assertFalse(reasoner.isEntailed(expression));

		expression = new WsmlLogicalExpressionParser(ontology, container)
				.parse("Marge memberOf NarcistLover");

		assertTrue(reasoner.isEntailed(expression));

		expression = new WsmlLogicalExpressionParser(ontology, container)
			.parse("Bart memberOf NarcistLover");

		assertFalse(reasoner.isEntailed(expression));

		expression = new WsmlLogicalExpressionParser(ontology, container).parse("ElBarto[name hasValue \"Bart Simpson\"]");

		assertTrue(reasoner.isEntailed(expression));

		expression = new WsmlLogicalExpressionParser(ontology, container).parse("Lisa[dislikes hasValue SteakSandwich]");

		assertTrue(reasoner.isEntailed(expression));
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
