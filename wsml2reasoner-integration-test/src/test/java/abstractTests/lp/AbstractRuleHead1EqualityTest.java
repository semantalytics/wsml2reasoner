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

public abstract class AbstractRuleHead1EqualityTest extends TestCase implements
		LP {

	protected static final String ONTOLOGY_FILE_1 = "equal1_inHeadIRIS.wsml";

	protected LPReasoner reasoner;

	protected void setUp() throws Exception {
		super.setUp();
		reasoner = getLPReasoner();
	}

	public void testEqual01() throws Exception {

		String query = "?x[name hasValue ?n1] and ?y[name hasValue ?n2] and ?n1=?n2.";

		Results r = new Results("x", "y");
		r.addBinding(Results.iri("http://simple1#x1"), Results
				.iri("http://simple1#x1"));
		r.addBinding(Results.iri("http://simple1#x1"), Results
				.iri("http://simple1#x2"));
		r.addBinding(Results.iri("http://simple1#x2"), Results
				.iri("http://simple1#x1"));
		r.addBinding(Results.iri("http://simple1#x2"), Results
				.iri("http://simple1#x2"));

		LPHelper.executeQueryAndCheckResults(OntologyHelper
				.loadOntology(ONTOLOGY_FILE_1), query, r.get(), reasoner);
	}

	public void testEqual02() throws Exception {

		String query = "?x memberOf Y";

		Results r = new Results("x");
		r.addBinding(Results.iri("http://simple1#x1"));
		r.addBinding(Results.iri("http://simple1#x2"));
		
		LPHelper.executeQueryAndCheckResults(OntologyHelper
				.loadOntology(ONTOLOGY_FILE_1), query, r.get(), reasoner);
	}
}
