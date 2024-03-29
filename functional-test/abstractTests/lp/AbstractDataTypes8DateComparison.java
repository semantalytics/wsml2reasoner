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

public abstract class AbstractDataTypes8DateComparison extends TestCase implements LP {

    private static final String NS = "http://example.com/datatypes8#";

    private static final String ONTOLOGY_FILE = "files/datatypes8_date_comparison.wsml";

    public void testDateComparisonInAxiom() throws Exception {
    	Results r = new Results( "x" );
    	r.addBinding( Results.iri( NS + "Chris" ) );
    	r.addBinding( Results.iri( NS + "Anna" ) );
    	
        String query = "?x memberOf Child";
    	
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, r.get(), getLPReasoner() );
    }

    public void testDateLess() throws Exception {
    	Results r = new Results( "x", "y" );
    	
        String query = "?x[birthday hasValue ?y] and ?y < _date(1957, 02, 20, 12, 30)";
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, r.get(), getLPReasoner() );

    	query = "?x[birthday hasValue ?y] and ?y < _date(1957, 02, 21, 12, 30)";
    	r.addBinding( Results.iri( NS + "Peter" ), Results.date( 1957, 02, 20, 12, 30 ) );
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, r.get(), getLPReasoner() );
    }

    public void testDateGreater() throws Exception {
    	Results r = new Results( "x", "y" );
    	
        String query = "?x[birthday hasValue ?y] and ?y > _date(2001, 09, 15, 12, 30)";
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, r.get(), getLPReasoner() );

    	query = "?x[birthday hasValue ?y] and ?y > _date(2001, 09, 14, 12, 30)";
    	r.addBinding( Results.iri( NS + "Chris" ), Results.date( 2001, 9, 15, 12, 30 ) );
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, r.get(), getLPReasoner() );
    }

    public void testDateLessEqual() throws Exception {
    	Results r = new Results( "x", "y" );
    	
    	String query = "?x[birthday hasValue ?y] and ?y =< _date(1976, 8, 16, 12, 30)";
    	r.addBinding( Results.iri( NS + "Carla" ), Results.date( 1976, 8, 16, 12, 30 ) );
    	r.addBinding( Results.iri( NS + "Peter" ), Results.date( 1957, 2, 20, 12, 30 ) );
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, r.get(), getLPReasoner() );
    }

    public void testDateGreaterEqual() throws Exception {
    	Results r = new Results( "x", "y" );
    	
    	String query = "?x[birthday hasValue ?y] and ?y >= _date(1996, 05, 23, 12, 30)";
    	r.addBinding( Results.iri( NS + "Anna" ), Results.date( 1996, 05, 23, 12, 30 ) );
    	r.addBinding( Results.iri( NS + "Chris" ), Results.date( 2001, 9, 15, 12, 30 ) );
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, r.get(), getLPReasoner() );
    }

    public void testDateEqual() throws Exception {
    	Results r = new Results( "x", "y" );
    	
    	String query = "?x[birthday hasValue ?y] and ?y = _date(1996, 05, 23, 12, 30)";
    	r.addBinding( Results.iri( NS + "Anna" ), Results.date( 1996, 05, 23, 12, 30 ) );
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, r.get(), getLPReasoner() );
    }

    public void testDateNotEqual() throws Exception {
    	Results r = new Results( "x", "y" );
    	
    	String query = "?x[birthday hasValue ?y] and ?y != _date(1996, 05, 23, 12, 30)";
    	r.addBinding( Results.iri( NS + "Chris" ), Results.date( 2001, 9, 15, 12, 30 ) );
    	r.addBinding( Results.iri( NS + "Carla" ), Results.date( 1976, 8, 16, 12, 30 ) );
    	r.addBinding( Results.iri( NS + "Peter" ), Results.date( 1957, 2, 20, 12, 30 ) );
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, r.get(), getLPReasoner() );
    }
}
