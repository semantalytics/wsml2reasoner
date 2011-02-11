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

public abstract class AbstractRuleHead10EqualityTest extends TestCase implements
		LP {

	protected static final String ONTOLOGY_FILE = "equal10_inHeadIRIS.wsml";
	protected LPReasoner reasoner;

	protected void setUp() throws Exception {
		super.setUp();
		reasoner = getLPReasoner();
	}

	public void testEqual() throws Exception {

		String query = "?instance[?attribute hasValue ?value]";

		Results r = new Results("instance", "attribute", "value");
		r.addBinding(Results.iri("http://simple10#A"), Results.iri("http://simple10#aString"), Results._string("string-value") );
		r.addBinding(Results.iri("http://simple10#A"), Results.iri("http://simple10#anotherString"), Results._string("another string-value"));
		r.addBinding(Results.iri("http://simple10#B"), Results.iri("http://simple10#anotherString"),  Results._string("another string-value"));
		r.addBinding(Results.iri("http://simple10#B"), Results.iri("http://simple10#aString"), Results._string("string-value") );
		
//		LPHelper.outputON();
		LPHelper.executeQueryAndCheckResults(OntologyHelper
				.loadOntology(ONTOLOGY_FILE), query, r.get(), reasoner);
	}
}
