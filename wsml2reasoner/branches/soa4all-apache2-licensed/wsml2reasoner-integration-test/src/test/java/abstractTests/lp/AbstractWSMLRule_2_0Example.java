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
