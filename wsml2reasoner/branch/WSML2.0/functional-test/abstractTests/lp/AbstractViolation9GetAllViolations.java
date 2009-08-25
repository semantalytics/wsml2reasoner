/**
 * WSML2Reasoner
 * An extensible framework for reasoning with WSML ontologies.
 * 
 * Copyright (C) 2008 Semantic Technology Institute (STI) Innsbruck, 
 * University of Innsbruck, Technikerstrasse 21a, 6020 Innsbruck, Austria.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 */
package abstractTests.lp;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.Set;

import junit.framework.TestCase;

import org.omwg.ontology.Concept;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.SimpleDataType;
import org.omwg.ontology.SimpleDataValue;
import org.omwg.ontology.WsmlDataType;
import org.sti2.wsmo4j.factory.FactoryImpl;
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
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.Parser;
import org.wsmo.wsml.ParserException;
import org.wsmo.wsml.Serializer;

import abstractTests.LP;

public abstract class AbstractViolation9GetAllViolations extends TestCase implements LP {
	private Set<ConsistencyViolation> errors;
	
	private static final String NS = "http://example.com/violation9#";
	private static final String ONTOLOGY_FILE = "files/violation9_get_all.wsml";

	protected void setUp() throws Exception {
		getViolations();
	}

	private void getViolations() throws InvalidModelException, IOException, ParserException {
		WsmoFactory wsmoFactory = FactoryImpl.getInstance().createWsmoFactory();
		Parser parser = FactoryImpl.getInstance().createParser(wsmoFactory);
		InputStream is = this.getClass().getClassLoader().getResourceAsStream(ONTOLOGY_FILE);
		assertNotNull(is);
		try {
			Ontology ontology = (Ontology) parser.parse(new InputStreamReader(is), null)[0];
			StringWriter sw = new StringWriter();
			Serializer ontologySerializer = FactoryImpl.getInstance().createSerializer();
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
					assertEquals(WsmlDataType.WSML_INTEGER, (typeId));
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
