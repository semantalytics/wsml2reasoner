/*
 * WSML2Reasoner
 * An extensible framework for reasoning with WSML ontologies.
 * 
 * Copyright (C) 2008 Semantic Technology Institute (STI) Innsbruck, 
 * University of Innsbruck, Technikerstrasse 21a, 6020 Innsbruck, Austria.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 */
package abstractTests.lp;

import helper.LPHelper;
import helper.OntologyHelper;
import helper.Results;
import junit.framework.TestCase;
import abstractTests.LP;

public abstract class AbstractDataTypes7DateTimeComparison extends TestCase implements LP {

    private static final String NS = "http://example.com/datatypes7#";

    private static final String ONTOLOGY_FILE = "files/datatypes7_date_time_comparison.wsml";

    public void testDateTimeComparisonInAxiom() throws Exception {
    	Results r = new Results( "x" );
    	r.addBinding( Results.iri( NS + "Chris" ) );
    	r.addBinding( Results.iri( NS + "Anna" ) );
    	
        String query = "?x memberOf Child";
    	
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, r.get(), getLPReasoner() );
    }

    public void testDateTimeLess() throws Exception {
    	Results r = new Results( "x", "y" );
    	
        String query = "?x[birthday hasValue ?y] and ?y < _dateTime(1957, 02, 20, 13, 56, 00, 12, 30)";
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, r.get(), getLPReasoner() );

    	query = "?x[birthday hasValue ?y] and ?y < _dateTime(1957, 02, 20, 13, 56, 01, 12, 30)";
    	r.addBinding( Results.iri( NS + "Peter" ), Results.datetime( 1957, 02, 20, 13, 56, 00, 12, 30 ) );
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, r.get(), getLPReasoner() );
    }

    public void testDateTimeGreater() throws Exception {
    	Results r = new Results( "x", "y" );
    	
        String query = "?x[birthday hasValue ?y] and ?y > _dateTime(2001, 09, 15, 13, 56, 00, 12, 30)";
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, r.get(), getLPReasoner() );

    	query = "?x[birthday hasValue ?y] and ?y > _dateTime(2001, 09, 15, 13, 55, 00, 12, 30)";
    	r.addBinding( Results.iri( NS + "Chris" ), Results.datetime( 2001, 9, 15, 13, 56, 00, 12, 30 ) );
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, r.get(), getLPReasoner() );
    }

    public void testDateTimeLessEqual() throws Exception {
    	Results r = new Results( "x", "y" );
    	
    	String query = "?x[birthday hasValue ?y] and ?y =< _dateTime(1976, 8, 16, 13, 56, 00, 12, 30)";
    	r.addBinding( Results.iri( NS + "Carla" ), Results.datetime( 1976, 8, 16, 13, 56, 00, 12, 30 ) );
    	r.addBinding( Results.iri( NS + "Peter" ), Results.datetime( 1957, 2, 20, 13, 56, 00, 12, 30 ) );
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, r.get(), getLPReasoner() );
    }

    public void testDateTimeGreaterEqual() throws Exception {
    	Results r = new Results( "x", "y" );
    	
    	String query = "?x[birthday hasValue ?y] and ?y >= _dateTime(1996, 05, 23, 13, 56, 00, 12, 30)";
    	r.addBinding( Results.iri( NS + "Anna" ), Results.datetime( 1996, 05, 23, 13, 56, 00, 12, 30 ) );
    	r.addBinding( Results.iri( NS + "Chris" ), Results.datetime( 2001, 9, 15, 13, 56, 00, 12, 30 ) );
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, r.get(), getLPReasoner() );
    }

    public void testDateTimeEqual() throws Exception {
    	Results r = new Results( "x", "y" );
    	
    	String query = "?x[birthday hasValue ?y] and ?y = _dateTime(1996, 05, 23, 13, 56, 00, 12, 30)";
    	r.addBinding( Results.iri( NS + "Anna" ), Results.datetime( 1996, 05, 23, 13, 56, 00, 12, 30 ) );
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, r.get(), getLPReasoner() );
    }

    public void testDateTimeNotEqual() throws Exception {
    	Results r = new Results( "x", "y" );
    	
    	String query = "?x[birthday hasValue ?y] and ?y != _dateTime(1996, 05, 23, 13, 56, 00, 12, 30)";
    	r.addBinding( Results.iri( NS + "Chris" ), Results.datetime( 2001, 9, 15, 13, 56, 00, 12, 30 ) );
    	r.addBinding( Results.iri( NS + "Carla" ), Results.datetime( 1976, 8, 16, 13, 56, 00, 12, 30 ) );
    	r.addBinding( Results.iri( NS + "Peter" ), Results.datetime( 1957, 2, 20, 13, 56, 00, 12, 30 ) );
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, r.get(), getLPReasoner() );
    }
}
