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
package org.wsml.reasoner.builtin.iris;

import junit.framework.TestCase;

import org.deri.iris.api.basics.IAtom;
import org.omwg.ontology.XmlSchemaDataType;
import org.sti2.wsmo4j.factory.WsmlFactoryContainer;
import org.wsml.reasoner.Literal;
import org.wsml.reasoner.LiteralTestHelper;
import org.wsmo.common.BuiltIn;
import org.wsmo.factory.WsmoFactory;

public class IrisFacadeConvertTest extends TestCase {

	public void testConvertTermFromWsmo4jToIris() {

		final WsmoFactory WF = new WsmlFactoryContainer().getWsmoFactory();

		// [ 1997166 ] Anonymous IDs cause exception when using IRIS.
		// Anonymous IDs cause exception when using IRIS.
		// The use of anonymous IDs causes an IllegalArgumentException().
		// The problem seems to lie in IrisFacade.wsmoTermConverter().

		assertTrue(TermHelper.convertTermFromWsmo4jToIris(WF
				.createAnonymousID()) != null);
	}

	public void testLiteral2Atom() {
		try {
			LiteralHelper.literal2Atom(null, false);
			fail("should throw an exception"); // should throw an exception
		} catch (Exception e) {
		} catch (AssertionError ae) {
		}

	}

	public void testLiteral2AtomHead() {

		Literal wsmlLiteral = new Literal(true, BuiltIn.EQUAL.getFullName(),
				LiteralTestHelper.createVariable("x"), LiteralTestHelper
						.createVariable("y"));

		assertEquals("EQUAL(?x, ?y)", LiteralHelper.literal2Atom(
				wsmlLiteral, true).toString());
	}
	
	public void testDataTypes() {
		String name = "";
		String expected ="";
		String irins = "http://iris.sti-innsbruck.at/urn:";
		int i = 1;
		
		name = "string";
		expected = XmlSchemaDataType.XSD_STRING + "(" + irins + i + ")";
		checkdt(false, name, expected, i++);
		
		name = "decimal";
		expected = XmlSchemaDataType.XSD_DECIMAL + "(" + irins + i + ")";
		checkdt(false, name, expected, i++);
		
		name = "double";
		expected = XmlSchemaDataType.XSD_DOUBLE + "(" + irins + i + ")";
		checkdt(false, name, expected, i++);
		
		name = "float";
		expected = XmlSchemaDataType.XSD_FLOAT + "(" + irins + i + ")";
		checkdt(false, name, expected, i++);
		
		name = "integer";
		expected = XmlSchemaDataType.XSD_INTEGER + "(" + irins + i + ")";
		checkdt(false, name, expected, i++);
		
		name = "boolean";
		expected = XmlSchemaDataType.XSD_BOOLEAN + "(" + irins + i + ")";
		checkdt(false, name, expected, i++);
		
		name = "dateTime";
		expected = XmlSchemaDataType.XSD_DATETIME + "(" + irins + i + ")";
		checkdt(false, name, expected, i++);
		
		name = "time";
		expected = XmlSchemaDataType.XSD_TIME + "(" + irins + i + ")";
		checkdt(false, name, expected, i++);
		
		name = "date";
		expected = XmlSchemaDataType.XSD_DATE + "(" + irins + i + ")";
		checkdt(false, name, expected, i++);
		
		name = "gYearMonth";
		expected = XmlSchemaDataType.XSD_GYEARMONTH + "(" + irins + i + ")";
		checkdt(false, name, expected, i++);
		
		name = "gYear";
		expected = XmlSchemaDataType.XSD_GYEAR + "(" + irins + i + ")";
		checkdt(false, name, expected, i++);
		
		name = "gMonthDay";
		expected = XmlSchemaDataType.XSD_GMONTHDAY + "(" + irins + i + ")";
		checkdt(false, name, expected, i++);
		
		name = "gDay";
		expected = XmlSchemaDataType.XSD_GDAY + "(" + irins + i + ")";
		checkdt(false, name, expected, i++);
		
		name = "gMonth";
		expected = XmlSchemaDataType.XSD_GMONTH + "(" + irins + i + ")";
		checkdt(false, name, expected, i++);
		
		name = "hexBinary";
		expected = XmlSchemaDataType.XSD_HEXBINARY + "(" + irins + i + ")";
		checkdt(false, name, expected, i++);
		
		name = "base64Binary";
		expected = XmlSchemaDataType.XSD_BASE64BINARY + "(" + irins + i + ")";
		checkdt(false, name, expected, i++);
			
	}
	
	private void checkdt(boolean output, String name, String expected, int i) {
		String irins = "http://iris.sti-innsbruck.at/urn:";
		String ns = "http://www.w3.org/2001/XMLSchema#";
		Literal l = null;
		IAtom atom = null;
		
		l = LiteralTestHelper.createLiteral(true, ns + name , irins +  i);
		atom = LiteralHelper.literal2Atom(l, false);
		if(output) {
			System.out.println(atom);
		}
		
		assertTrue(atom.toString().equals(expected));
	}

}
