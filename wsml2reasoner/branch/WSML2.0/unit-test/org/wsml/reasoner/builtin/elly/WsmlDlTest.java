package org.wsml.reasoner.builtin.elly;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Concept;
import org.omwg.ontology.DataValue;
import org.omwg.ontology.Instance;
import org.omwg.ontology.Ontology;
import org.sti2.wsmo4j.factory.WsmlFactoryContainer;
import org.wsml.reasoner.api.DLReasoner;
import org.wsml.reasoner.impl.DefaultWSMLReasonerFactory;
import org.wsmo.common.IRI;
import org.wsmo.common.TopEntity;
import org.wsmo.factory.FactoryContainer;
import org.wsmo.wsml.Parser;

import com.ontotext.wsmo4j.parser.wsml.WsmlParser;

public class WsmlDlTest extends TestCase {

	private static String defaultNS = "http://www.example.org#";
	private static String prefix = "org/wsml/reasoner/builtin/elly/";
	private DLReasoner reasoner;
	private FactoryContainer container;
	Ontology ontology;

	protected void setUp() throws Exception {
		container = new WsmlFactoryContainer();
		reasoner = DefaultWSMLReasonerFactory.getFactory().createDL2Reasoner(container);
		
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

		assertTrue(values.contains(container.getXmlDataFactory().createInteger("10")));
		assertEquals(1, values.size());
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
