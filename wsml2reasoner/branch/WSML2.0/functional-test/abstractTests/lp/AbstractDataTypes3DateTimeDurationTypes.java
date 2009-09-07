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

public abstract class AbstractDataTypes3DateTimeDurationTypes extends TestCase implements LP {

    private static final String NS = "http://example.com/datatypes3#";

    private static final String ONTOLOGY_FILE = "files/datatypes3_date_time_duration.wsml";

    public void testTime() throws Exception {
    	String query = "?x[aTime hasValue ?t]";
    	
    	Results r = new Results( "x", "t" );
    	r.addBinding( Results.iri( NS + "integerSeconds" ), Results._time( 23,59,58,0,0 ) );
    	r.addBinding( Results.iri( NS + "decimalSeconds" ), Results._time( 23,59,58.98765,0,0 ) );
    	r.addBinding( Results.iri( NS + "integerSecondsWithTimeZones" ), Results._time( 23,59,58,13,30 ) );
    	r.addBinding( Results.iri( NS + "decimalSecondsWithTimeZones" ), Results._time( 23,59,58.98765,13,30 ) );
    	
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, r.get(), getLPReasoner() );
    }

    public void testDate() throws Exception {
    	String query = "?x[aDate hasValue ?t]";

    	Results r = new Results( "x", "t" );
    	r.addBinding( Results.iri( NS + "integerSeconds" ), Results._date( 1981, 12, 31, 0, 0 ) );
    	r.addBinding( Results.iri( NS + "decimalSeconds" ), Results._date( 1981, 12, 31,0,0 ) );
    	r.addBinding( Results.iri( NS + "integerSecondsWithTimeZones" ), Results._date( 1981, 12, 31,13,30 ) );
    	r.addBinding( Results.iri( NS + "decimalSecondsWithTimeZones" ), Results._date( 1981, 12, 31,13,30 ) );
    	
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, r.get(), getLPReasoner() );
    }

    public void testDateTime() throws Exception {
    	String query = "?x[aDateTime hasValue ?t]";

    	Results r = new Results( "x", "t" );
    	r.addBinding( Results.iri( NS + "integerSeconds" ), Results._datetime( 1981, 12, 31, 23, 59, 58, 0, 0 ) );
    	r.addBinding( Results.iri( NS + "decimalSeconds" ), Results._datetime( 1981, 12, 31, 23, 59, 58.98765, 0, 0 ) );
    	r.addBinding( Results.iri( NS + "integerSecondsWithTimeZones" ), Results._datetime( 1981, 12, 31, 23, 59, 58, 13, 30 ) );
    	r.addBinding( Results.iri( NS + "decimalSecondsWithTimeZones" ), Results._datetime( 1981, 12, 31, 23, 59, 58.98765, 13, 30 ) );

    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, r.get(), getLPReasoner() );
    }

    public void testDuration() throws Exception {
    	String query = "?x[aDuration hasValue ?t]";

    	Results r = new Results( "x", "t" );
    	r.addBinding( Results.iri( NS + "integerSeconds" ), Results._duration( 1, 2, 3, 4, 5, 6 ) );
    	r.addBinding( Results.iri( NS + "decimalSeconds" ), Results._duration( 1, 2, 3, 4, 5, 6.98765 ) );
    	r.addBinding( Results.iri( NS + "integerSecondsWithTimeZones" ), Results._duration(  1, 2, 3, 4, 5, 6 ) );
    	r.addBinding( Results.iri( NS + "decimalSecondsWithTimeZones" ), Results._duration( 1, 2, 3, 4, 5, 6.98765 ) );

//    	System.out.println( OntologyHelper.toString( LPHelper.executeQuery( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, getLPReasoner() ) ) );

    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, r.get(), getLPReasoner() );
    }
}
