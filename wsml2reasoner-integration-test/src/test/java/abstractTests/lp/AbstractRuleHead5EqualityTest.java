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

import org.wsml.reasoner.api.LPReasoner;

import abstractTests.LP;

public abstract class AbstractRuleHead5EqualityTest extends TestCase implements
		LP {

	protected static final String ONTOLOGY_FILE = "equal5_inHeadIRIS.wsml";

	protected LPReasoner reasoner;

	protected void setUp() throws Exception {
		super.setUp();
		reasoner = getLPReasoner();
	}

	public void testEqual() throws Exception {
		String query = "?x[?y hasValue ?z]";

		Results r = new Results("z", "y", "x");
		r.addBinding(Results.iri("http://simple5#aa"), Results
				.iri("http://simple5#other"), Results.iri("http://simple5#A1"));
		r.addBinding(Results.iri("http://simple5#a"), Results
				.iri("http://simple5#some"), Results.iri("http://simple5#A1"));
		r.addBinding(Results.iri("http://simple5#a"), Results
				.iri("http://simple5#some"), Results.iri("http://simple5#B1"));
		r.addBinding(Results.iri("http://simple5#bb"), Results
				.iri("http://simple5#other"), Results.iri("http://simple5#B1"));
		r.addBinding(Results.iri("http://simple5#bb"), Results
				.iri("http://simple5#other"), Results.iri("http://simple5#A1"));
		r.addBinding(Results.iri("http://simple5#aa"), Results
				.iri("http://simple5#other"), Results.iri("http://simple5#B1"));

		LPHelper.executeQueryAndCheckResults(OntologyHelper
				.loadOntology(ONTOLOGY_FILE), query, r.get(), reasoner);

	}

	public void testEqual2() throws Exception {

		String query = "p(?x)";

		Results r = new Results("x");
		r.addBinding(Results.iri("http://simple5#B1"));
		r.addBinding(Results.iri("http://simple5#A1"));

		LPHelper.executeQueryAndCheckResults(OntologyHelper
				.loadOntology(ONTOLOGY_FILE), query, r.get(), reasoner);

	}

}
