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
 * To test the new feature of wsml 2.0 core of the new feature
 * "instance equivalence", which allows the definition that two instances are
 * equivalent.
 */
public abstract class AbstractWSMLCore_2_0Example extends TestCase implements
		LP {

	private static final String ONTOLOGY_FILE = "example_wsml-core-2.0.wsml";

	private static final String QUERY = "ElBarto[name hasValue ?name, ageInYears hasValue ?age]";

	public AbstractWSMLCore_2_0Example() {
		super();
	}

	/**
	 * Who is "El Barto"? In axiom "thisIsElBarto" we make use of the new
	 * feature "instance equivalence", which allows the definition that two
	 * instances are equivalent.
	 * 
	 * @throws Exception
	 */
	public void test_instance_equivalence() throws Exception {
		Results r = new Results("name", "age");
		r.addBinding(Results._string("El Barto"), Results._integer(10));
		r.addBinding(Results._string("Bart Simpson"), Results._integer(10));

		LPHelper.outputON();
		LPHelper.executeQueryAndCheckResults(OntologyHelper
				.loadOntology(ONTOLOGY_FILE), QUERY, r.get(), getLPReasoner());
	}

}
