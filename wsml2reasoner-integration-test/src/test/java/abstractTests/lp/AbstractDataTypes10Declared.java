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

public abstract class AbstractDataTypes10Declared extends TestCase implements LP {

    private static final String NS = "http://example.com/datatypes10#";

    private static final String ONTOLOGY_FILE = "datatypes10_new_declared.wsml";

    public void testValuesForDeclaredAttributes() throws Exception {
    	String query = "?instance[?attribute hasValue ?value]";
    	
    	Results r = new Results( "instance", "attribute", "value" );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "aDuration" ), Results._duration( +1, 1, 2, 3, 4, 5, 6.0 ) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "aPlainLiteral" ), Results._text( "This should be a RDF Text", "en" ) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "aRDF_XMLLiteral" ), Results._xmlliteral("<tag>xml literal</tag>", "en" ) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "aYearMonthDuration" ), Results._yearmonthduration(+1, 2009,9) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "aDayTimeDuration" ), Results._daytimeduration(+1, 5, 3, 2, 1.0));

//    	LPHelper.outputON();
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, r.get(), getLPReasoner() );
    }
}
