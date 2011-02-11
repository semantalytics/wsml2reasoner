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

public abstract class AbstractDataTypes8DateComparison extends TestCase implements LP {

    private static final String NS = "http://example.com/datatypes8#";

    private static final String ONTOLOGY_FILE = "datatypes8_date_comparison.wsml";

    public void testDateComparisonInAxiom() throws Exception {
    	Results r = new Results( "x" );
    	r.addBinding( Results.iri( NS + "Chris" ) );
    	r.addBinding( Results.iri( NS + "Anna" ) );
    	
        String query = "?x memberOf Child";
    	
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, r.get(), getLPReasoner() );
    }

    public void testDateLess() throws Exception {
    	Results r = new Results( "x", "y" );
    	
        String query = "?x[birthday hasValue ?y] and ?y < _date(1957, 02, 20,  1, 12, 30)";
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, r.get(), getLPReasoner() );

    	query = "?x[birthday hasValue ?y] and ?y < _date(1957, 02, 21,  1, 12, 30)";
    	r.addBinding( Results.iri( NS + "Peter" ), Results._date( 1957, 02, 20, +1, 12, 30 ) );
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, r.get(), getLPReasoner() );
    }

    public void testDateGreater() throws Exception {
    	Results r = new Results( "x", "y" );
    	
        String query = "?x[birthday hasValue ?y] and ?y > _date(2001, 09, 15,  1, 12, 30)";
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, r.get(), getLPReasoner() );

    	query = "?x[birthday hasValue ?y] and ?y > _date(2001, 09, 14,  1, 12, 30)";
    	r.addBinding( Results.iri( NS + "Chris" ), Results._date( 2001, 9, 15, +1, 12, 30 ) );
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, r.get(), getLPReasoner() );
    }

    public void testDateLessEqual() throws Exception {
    	Results r = new Results( "x", "y" );
    	
    	String query = "?x[birthday hasValue ?y] and ?y =< _date(1976, 8, 16,  1, 12, 30)";
    	r.addBinding( Results.iri( NS + "Carla" ), Results._date( 1976, 8, 16, +1, 12, 30 ) );
    	r.addBinding( Results.iri( NS + "Peter" ), Results._date( 1957, 2, 20, +1, 12, 30 ) );
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, r.get(), getLPReasoner() );
    }

    public void testDateGreaterEqual() throws Exception {
    	Results r = new Results( "x", "y" );
    	
    	String query = "?x[birthday hasValue ?y] and ?y >= _date(1996, 05, 23,  1, 12, 30)";
    	r.addBinding( Results.iri( NS + "Anna" ), Results._date( 1996, 05, 23, +1, 12, 30 ) );
    	r.addBinding( Results.iri( NS + "Chris" ), Results._date( 2001, 9, 15, +1, 12, 30 ) );
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, r.get(), getLPReasoner() );
    }

    public void testDateEqual() throws Exception {
    	Results r = new Results( "x", "y" );
    	
    	String query = "?x[birthday hasValue ?y] and ?y = _date(1996, 05, 23,  1, 12, 30)";
    	r.addBinding( Results.iri( NS + "Anna" ), Results._date( 1996, 05, 23, +1, 12, 30 ) );
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, r.get(), getLPReasoner() );
    }

    public void testDateNotEqual() throws Exception {
    	Results r = new Results( "x", "y" );
    	
    	String query = "?x[birthday hasValue ?y] and ?y != _date(1996, 05, 23,  1, 12, 30)";
    	r.addBinding( Results.iri( NS + "Chris" ), Results._date( 2001, 9, 15, +1, 12, 30 ) );
    	r.addBinding( Results.iri( NS + "Carla" ), Results._date( 1976, 8, 16, +1, 12, 30 ) );
    	r.addBinding( Results.iri( NS + "Peter" ), Results._date( 1957, 2, 20, +1, 12, 30 ) );
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, r.get(), getLPReasoner() );
    }
}
