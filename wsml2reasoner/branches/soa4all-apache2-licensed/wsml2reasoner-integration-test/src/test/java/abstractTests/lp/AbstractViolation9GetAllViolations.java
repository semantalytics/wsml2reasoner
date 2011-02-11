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
package abstractTests.lp;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.Set;

import junit.framework.TestCase;

import org.deri.wsmo4j.io.serializer.wsml.WSMLSerializerImpl;
import org.omwg.ontology.Concept;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.SimpleDataType;
import org.omwg.ontology.SimpleDataValue;
import org.omwg.ontology.XmlSchemaDataType;
import org.wsml.reasoner.api.inconsistency.AttributeTypeViolation;
import org.wsml.reasoner.api.inconsistency.ConsistencyViolation;
import org.wsml.reasoner.api.inconsistency.InconsistencyException;
import org.wsml.reasoner.api.inconsistency.MaxCardinalityViolation;
import org.wsml.reasoner.api.inconsistency.MinCardinalityViolation;
import org.wsml.reasoner.api.inconsistency.NamedUserConstraintViolation;
import org.wsml.reasoner.api.inconsistency.UnNamedUserConstraintViolation;
import org.wsml.reasoner.api.inconsistency.UserConstraintViolation;
import org.wsmo.common.IRI;
import org.wsmo.common.TopEntity;
import org.wsmo.common.exception.InvalidModelException;
import org.wsmo.wsml.Parser;
import org.wsmo.wsml.ParserException;
import org.wsmo.wsml.Serializer;

import abstractTests.LP;

import com.ontotext.wsmo4j.parser.wsml.WsmlParser;

public abstract class AbstractViolation9GetAllViolations extends TestCase implements LP {
	private Set<ConsistencyViolation> errors;
	
	private static final String NS = "http://example.com/violation9#";
	private static final String ONTOLOGY_FILE = "violation9_get_all.wsml";

	protected void setUp() throws Exception {
		getViolations();
	}

	private void getViolations() throws InvalidModelException, IOException, ParserException {
		//WsmoFactory wsmoFactory = new WsmlFactoryContainer().getWsmoFactory();
		Parser parser = new WsmlParser();
		InputStream is = this.getClass().getClassLoader().getResourceAsStream(ONTOLOGY_FILE);
		assertNotNull(is);
		try {
			Ontology ontology = (Ontology) parser.parse(new InputStreamReader(is))[0];
			StringWriter sw = new StringWriter();
			Serializer ontologySerializer = new WSMLSerializerImpl();
			ontologySerializer.serialize(new TopEntity[] { ontology }, sw);
			// Reasoner
			(this.getLPReasoner()).registerOntology(ontology);

			fail("Inconsistency exception expected!");
		} catch (InconsistencyException expected) {
			errors = expected.getViolations();
		}
	}

	public void testAttributeTypeViolation() {
		boolean iTypeChecked = false;
		boolean dTypeChecked = false;

		for (ConsistencyViolation violation : errors) {
			if (violation instanceof AttributeTypeViolation) {
				AttributeTypeViolation v = (AttributeTypeViolation) violation;
				String attributeId = v.getAttribute().getIdentifier()
						.toString();
				String instanceId = v.getInstance().getIdentifier().toString();
				if ((NS + "ai").equals(attributeId)) {
					Concept t = (Concept) v.getExpectedType();
					String typeId = t.getIdentifier().toString();
					IRI val = (IRI) v.getViolatingValue();
					String valueId = val.toString();
					assertEquals(NS + "iC", instanceId);
					assertEquals(NS + "D", typeId);
					assertEquals(NS + "iE", valueId);
					iTypeChecked = true;

				} else if ((NS + "ad").equals(attributeId)) {
					SimpleDataType t = (SimpleDataType) v.getExpectedType();
					String typeId = t.getIdentifier().toString();
					SimpleDataValue val = (SimpleDataValue) v
							.getViolatingValue();
					String value = val.getValue().toString();
					assertEquals(NS + "iC", instanceId);
					assertEquals(XmlSchemaDataType.XSD_INTEGER, (typeId));
					assertEquals("blah", value);
					dTypeChecked = true;
				}

			}

		}
		assertTrue(iTypeChecked);
		assertTrue(dTypeChecked);
	}

	public void testMinCardViolation() {
		boolean minCardChecked = false;

		for (ConsistencyViolation violation : errors) {
			if (violation instanceof MinCardinalityViolation) {
				MinCardinalityViolation v = (MinCardinalityViolation) violation;
				String attributeId = v.getAttribute().getIdentifier()
						.toString();
				String instanceId = v.getTerm()// .getIdentifier()
						.toString();
				assertEquals(NS + "iC", instanceId);
				assertEquals(NS + "amin", attributeId);
				minCardChecked = true;
			}
		}
		assertTrue(minCardChecked);
	}

	public void testMaxCardViolation() {
		boolean maxCardChecked = false;

		for (ConsistencyViolation violation : errors) {
			if (violation instanceof MaxCardinalityViolation) {
				MaxCardinalityViolation v = (MaxCardinalityViolation) violation;
				String attributeId = v.getAttribute().getIdentifier()
						.toString();
				String instanceId = v.getTerm()// .getIdentifier()
						.toString();
				assertEquals(NS + "iC", instanceId);
				assertEquals(NS + "amax", attributeId);
				maxCardChecked = true;
			}

		}
		assertTrue(maxCardChecked);
	}

	public void testUserConstraintViolation() {
		boolean namedUserChecked = false;
		boolean unNamedUserChecked = false;

		for (ConsistencyViolation violation : errors) {
			if (violation instanceof UserConstraintViolation) {
				if (violation instanceof NamedUserConstraintViolation) {
					NamedUserConstraintViolation v = (NamedUserConstraintViolation) violation;
					String axiomId = v.getAxiom().getIdentifier().toString();
					assertEquals(NS + "ax1", axiomId);
					namedUserChecked = true;
				}
				if (violation instanceof UnNamedUserConstraintViolation) {
					unNamedUserChecked = true;
				}
			}
		}

		assertTrue(namedUserChecked);
		assertTrue(unNamedUserChecked);
	}
}
