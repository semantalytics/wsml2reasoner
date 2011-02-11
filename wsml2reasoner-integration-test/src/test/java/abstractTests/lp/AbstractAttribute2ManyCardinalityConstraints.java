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

public abstract class AbstractAttribute2ManyCardinalityConstraints extends TestCase implements LP {

	private static final String ONTOLOGY_FILE = "attribute2_many_cardinality_constraints.wsml";
	
	public void testAttributeInheritance() throws Exception {

		String ns = "http://example.com/attribute2#";

		Results r = new Results("y");
		r.addBinding(Results.iri(ns + "i2"));

		LPHelper.executeQueryAndCheckResults(OntologyHelper.loadOntology(ONTOLOGY_FILE), "i1[a1 hasValue ?y]", r.get(), getLPReasoner());
		
		r = new Results("x", "y", "z");
		for( int n = 1; n <= 250; ++n )
			r.addBinding(Results.iri(ns + "i1"), Results.iri(ns + "a" + n), Results.iri(ns + "i2"));
		
		LPHelper.executeQueryAndCheckResults(OntologyHelper.loadOntology(ONTOLOGY_FILE), "?x[?y hasValue ?z]", r.get(), getLPReasoner());
	}
}
