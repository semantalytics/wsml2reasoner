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

import helper.LPHelper;
import helper.OntologyHelper;
import helper.Results;
import junit.framework.TestCase;
import abstractTests.LP;

/**
 * To show an example for the new built-ins and data types of wsml 2.0 rule
 */
public abstract class AbstractWSMLRule_2_0Example extends TestCase implements LP {

	private static final String ONTOLOGY_FILE = "example_wsml-rule-2.0.wsml";

	/**
	 * a) Query the slogans of Bart. Slogans are represented by rdf#text data
	 * values, which have a text with a corresponding language.
	 * 
	 * @throws Exception
	 */
	public void test_queryPlainLiteral() throws Exception {

		String query = "Bart[slogan hasValue ?slogan]";

		Results r = new Results("slogan");
		r.addBinding(Results._text("Ay, caramba", "es"));
		r.addBinding(Results._text("Eat my shorts", "en"));

		LPHelper.outputON();
		LPHelper.executeQueryAndCheckResults(OntologyHelper
				.loadOntology(ONTOLOGY_FILE), query, r.get(), getLPReasoner());
	}

	/**
	 * Query all spanish speakers. We make use of the new built-in
	 * "wsml#langFromText", which extracts the language part of a rdf#text data
	 * value.
	 * 
	 * @throws Exception
	 */
	public void test_queryGetLangFromText() throws Exception {

		String query = "?x memberOf SpanishSpeaker.";
		String namespace = "http://www.example.org/rule#";

		Results r = new Results("x");
		r.addBinding(Results.iri(namespace + "Bart"));

		LPHelper.outputON();
		LPHelper.executeQueryAndCheckResults(OntologyHelper
				.loadOntology(ONTOLOGY_FILE), query, r.get(), getLPReasoner());
	}

}
