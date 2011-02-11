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

public abstract class AbstractAttribute1Inheritance extends TestCase implements LP {

	public void testAttributeInheritance() throws Exception {

		String ns = "http://ex1.org#";
		String ontology = "namespace _\"" + ns + "\" \n"
				+ "ontology o1 \n"
				+ "concept A \n"
				+ "  attr ofType C \n "
				+ "concept B subConceptOf A \n ";

		String query = "?x[?attribute ofType ?type]";

		Results r = new Results("x", "attribute", "type");
		r.addBinding(Results.iri(ns + "B"), Results.iri(ns + "attr"), Results.iri(ns + "C"));
		r.addBinding(Results.iri(ns + "A"), Results.iri(ns + "attr"), Results.iri(ns + "C"));

		LPHelper.executeQueryAndCheckResults(OntologyHelper.parseOntology(ontology), query, r.get(), getLPReasoner());
	}
}
