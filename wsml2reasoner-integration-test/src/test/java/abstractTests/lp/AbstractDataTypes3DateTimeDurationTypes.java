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

public abstract class AbstractDataTypes3DateTimeDurationTypes extends TestCase implements LP {

    private static final String NS = "http://example.com/datatypes3#";

    private static final String ONTOLOGY_FILE = "datatypes3_date_time_duration.wsml";

    public void testTime() throws Exception {
    	String query = "?x[aTime hasValue ?t]";
    	
    	Results r = new Results( "x", "t" );
    	r.addBinding( Results.iri( NS + "decimalSeconds" ), Results._time( 23,59,58.98765,0,0,0 ) );
    	r.addBinding( Results.iri( NS + "decimalSecondsWithTimeZones" ), Results._time( 23,59,58.98765,+1,13,30 ) );
    	
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, r.get(), getLPReasoner() );
    }

    public void testDate() throws Exception {
    	String query = "?x[aDate hasValue ?t]";

    	Results r = new Results( "x", "t" );
    	r.addBinding( Results.iri( NS + "decimalSeconds" ), Results._date( 1981, 12, 31,0,0,0 ) );
    	r.addBinding( Results.iri( NS + "decimalSecondsWithTimeZones" ), Results._date( 1981, 12, 31,+1,13,30 ) );
    	
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, r.get(), getLPReasoner() );
    }

    public void testDateTime() throws Exception {
    	String query = "?x[aDateTime hasValue ?t]";

    	Results r = new Results( "x", "t" );
    	r.addBinding( Results.iri( NS + "decimalSeconds" ), Results._datetime( 1981, 12, 31, 23, 59, 58.98765, 0, 0, 0 ) );
    	r.addBinding( Results.iri( NS + "decimalSecondsWithTimeZones" ), Results._datetime( 1981, 12, 31, 23, 59, 58.98765, +1, 13, 30 ) );

    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, r.get(), getLPReasoner() );
    }

    public void testDuration() throws Exception {
    	String query = "?x[aDuration hasValue ?t]";

    	Results r = new Results( "x", "t" );
    	r.addBinding( Results.iri( NS + "decimalSeconds" ), Results._duration( +1, 1, 2, 3, 4, 5, 6.98765 ) );
    	r.addBinding( Results.iri( NS + "decimalSecondsWithTimeZones" ), Results._duration( +1, 1, 2, 3, 4, 5, 6.98765 ) );

    	// FIXME seconds can are represented with maximally 3 fractional digits 
    	// => internally not possible otherwise, XSD doesn't restrict the number of fractional digits
    	// LPHelper.outputON();
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, r.get(), getLPReasoner() );
    }
    
    public void testShorterDuration() throws Exception {
    	String query = "?x[aShorterDuration hasValue ?t]";

    	Results r = new Results( "x", "t" );
    	r.addBinding( Results.iri( NS + "decimalSeconds" ), Results._duration( +1, 1, 2, 3, 4, 5, 1.337 ) );
    	r.addBinding( Results.iri( NS + "decimalSecondsWithTimeZones" ), Results._duration( +1, 1, 2, 3, 4, 5, 1.337 ) );
    	
//    	LPHelper.outputON();
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, r.get(), getLPReasoner() );
    }
}
