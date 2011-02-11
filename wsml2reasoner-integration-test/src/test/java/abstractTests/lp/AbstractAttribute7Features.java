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

public abstract class AbstractAttribute7Features extends TestCase implements LP {

	private static final String ONTOLOGY_FILE = "attribute7_features.wsml";
	
	public void testInverseOf1() throws Exception {
		Results r = new Results();
		r.addBinding();

		LPHelper.executeQueryAndCheckResults(OntologyHelper.loadOntology(ONTOLOGY_FILE), "gordi[child hasValue baz] memberOf Human", r.get(), getLPReasoner());
	}

	public void testInverseOf2() throws Exception {
		Results r = new Results();
		r.addBinding();

		LPHelper.executeQueryAndCheckResults(OntologyHelper.loadOntology(ONTOLOGY_FILE), "lola[parent hasValue baz] memberOf Human", r.get(), getLPReasoner());
	}

	public void testTransitive() throws Exception {
		Results r = new Results();
		r.addBinding();

		LPHelper.executeQueryAndCheckResults(OntologyHelper.loadOntology(ONTOLOGY_FILE), "baz[ ancestor hasValue arthur ] memberOf Human", r.get(), getLPReasoner());
	}
	
	public void testSymmetric() throws Exception {
		Results r = new Results();
		r.addBinding();

		LPHelper.executeQueryAndCheckResults(OntologyHelper.loadOntology(ONTOLOGY_FILE), "bizzi[marriedTo hasValue baz] memberOf Human", r.get(), getLPReasoner());
	}

	public void testReflexive() throws Exception {
		Results r = new Results();
		r.addBinding();

		LPHelper.executeQueryAndCheckResults(OntologyHelper.loadOntology(ONTOLOGY_FILE), "baz[friend hasValue baz]", r.get(), getLPReasoner());
	}
}
