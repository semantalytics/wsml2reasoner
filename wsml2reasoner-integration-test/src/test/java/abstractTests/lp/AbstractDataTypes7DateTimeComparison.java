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

public abstract class AbstractDataTypes7DateTimeComparison extends TestCase implements LP {

    private static final String NS = "http://example.com/datatypes7#";

    private static final String ONTOLOGY_FILE = "datatypes7_date_time_comparison.wsml";

    public void testDateTimeComparisonInAxiom() throws Exception {
    	Results r = new Results( "x" );
    	r.addBinding( Results.iri( NS + "Chris" ) );
    	r.addBinding( Results.iri( NS + "Anna" ) );
    	
        String query = "?x memberOf Child";
    	
        LPHelper.outputON();
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, r.get(), getLPReasoner() );
    }

    public void testDateTimeLess() throws Exception {
    	Results r = new Results( "x", "y" );
    	
        String query = "?x[birthday hasValue ?y] and ?y < _datetime(1957, 02, 20, 13, 56, 30.0)";
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, r.get(), getLPReasoner() );

//    	query = "?x[birthday hasValue ?y] and ?y < _datetime(1957, 02, 20, 13, 56, 30.0)";
//    	r.addBinding( Results.iri( NS + "Peter" ), Results._datetime( 1957, 02, 20, 13, 56,  30.0 , 0, 0, 0 ) );
    	
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, r.get(), getLPReasoner() );
    }

    public void testDateTimeGreater() throws Exception {
    	Results r = new Results( "x", "y" );
    	
        String query = "?x[birthday hasValue ?y] and ?y > _datetime(2001, 09, 15, 13, 56, 30.0)";
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, r.get(), getLPReasoner() );

    	query = "?x[birthday hasValue ?y] and ?y > _datetime(2001, 09, 15, 13, 55, 30.0)";
    	r.addBinding( Results.iri( NS + "Chris" ), Results._datetime( 2001, 9, 15, 13, 56, 30.0 , 0, 0, 0 ) );
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, r.get(), getLPReasoner() );
    }

    public void testDateTimeLessEqual() throws Exception {
    	Results r = new Results( "x", "y" );
    	
    	String query = "?x[birthday hasValue ?y] and ?y =< _datetime(1976, 8, 16, 13, 56, 30.0)";
    	r.addBinding( Results.iri( NS + "Carla" ), Results._datetime( 1976, 8, 16, 13, 56,  30.0 , 0, 0, 0 ) );
    	r.addBinding( Results.iri( NS + "Peter" ), Results._datetime( 1957, 2, 20, 13, 56,  30.0 , 0, 0, 0 ) );
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, r.get(), getLPReasoner() );
    }

    public void testDateTimeGreaterEqual() throws Exception {
    	Results r = new Results( "x", "y" );
    	
    	String query = "?x[birthday hasValue ?y] and ?y >= _datetime(1996, 05, 23, 13, 56, 30.0)";
    	r.addBinding( Results.iri( NS + "Anna" ), Results._datetime( 1996, 05, 23, 13, 56, 30.0 , 0, 0, 0 ) );
    	r.addBinding( Results.iri( NS + "Chris" ), Results._datetime( 2001, 9, 15, 13, 56, 30.0 , 0, 0, 0 ) );
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, r.get(), getLPReasoner() );
    }

    public void testDateTimeEqual() throws Exception {
    	Results r = new Results( "x", "y" );
    	
    	String query = "?x[birthday hasValue ?y] and ?y = _datetime(1996, 05, 23, 13, 56, 30.0)";
    	r.addBinding( Results.iri( NS + "Anna" ), Results._datetime( 1996, 05, 23, 13, 56, 30.0 , 0, 0, 0 ) );
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, r.get(), getLPReasoner() );
    }

    public void testDateTimeNotEqual() throws Exception {
    	Results r = new Results( "x", "y" );
    	
    	String query = "?x[birthday hasValue ?y] and ?y != _datetime(1996, 05, 23, 13, 56, 30.0)";
    	r.addBinding( Results.iri( NS + "Chris" ), Results._datetime( 2001, 9, 15, 13, 56, 30.0 , 0, 0, 0 ) );
    	r.addBinding( Results.iri( NS + "Carla" ), Results._datetime( 1976, 8, 16, 13, 56, 30.0 , 0, 0, 0 ) );
    	r.addBinding( Results.iri( NS + "Peter" ), Results._datetime( 1957, 2, 20, 13, 56, 30.0 , 0, 0, 0 ) );
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, r.get(), getLPReasoner() );
    }
}
