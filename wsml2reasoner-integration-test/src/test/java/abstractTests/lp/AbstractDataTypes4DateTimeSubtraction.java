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

public abstract class AbstractDataTypes4DateTimeSubtraction extends TestCase implements LP {

	private static final String NS = "http://example.com/datatypes4#";

	private static final String ONTOLOGY_FILE = "datatypes4_date_time_subtraction.wsml";

	public void testDateTimeSubtract() throws Exception {
		String query = "?x[age hasValue ?t]";

		Results r = new Results("t", "x");
		r.addBinding(Results._daytimeduration(+1, 1*365 + 31 + 28 + 3, 4, 5, 6.7), Results.iri(NS + "allFields"));
		r.addBinding(Results._daytimeduration(+1, 0, 0, 0, 0.1), Results.iri(NS + "smallPositive"));
		r.addBinding(Results._daytimeduration(-1, 0, 0, 0, 0.1), Results.iri(NS + "smallNegative"));

		r.addBinding(Results._daytimeduration(+1, 0, 0, 0, 0.000001), Results.iri(NS + "oneMicro"));
		r.addBinding(Results._daytimeduration(+1, 0, 0, 0, 0.001), Results.iri(NS + "oneMilli"));

		r.addBinding(Results._daytimeduration(+1, 0, 0, 0, 1), Results.iri(NS + "oneSecond"));
		r.addBinding(Results._daytimeduration(+1, 0, 0, 1, 0), Results.iri(NS + "oneMinute"));
		r.addBinding(Results._daytimeduration(+1, 0, 1, 0, 0), Results.iri(NS + "oneHour"));

		r.addBinding(Results._daytimeduration(+1, 1, 0, 0, 0), Results.iri(NS + "oneDay"));
		r.addBinding(Results._daytimeduration(+1, 31, 0, 0, 0), Results.iri(NS + "oneMonth"));
		r.addBinding(Results._daytimeduration(+1, 365, 0, 0, 0), Results.iri(NS + "oneYear"));

		//LPHelper.outputON();
		LPHelper.executeQueryAndCheckResults(OntologyHelper.loadOntology(ONTOLOGY_FILE), query, r.get(), getLPReasoner());
	}
}
