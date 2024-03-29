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

public abstract class AbstractDataTypes4DateTimeSubtraction extends TestCase implements LP {

    private static final String NS = "http://example.com/datatypes4#";

    private static final String ONTOLOGY_FILE = "files/datatypes4_date_time_subtraction.wsml";

    public void testDateTimeSubtract() throws Exception {
    	String query = "?x[age hasValue ?t]";

    	Results r = new Results( "x", "a" );
    	r.addBinding( Results.iri( NS + "allFields" ), Results.duration( true, 1, 2, 3, 4, 5, 6.7 ) );
    	r.addBinding( Results.iri( NS + "smallPositive" ), Results.duration( true, 0, 0, 0, 0, 0, 0.1 ) );
    	r.addBinding( Results.iri( NS + "smallNegative" ), Results.duration( false, 0, 0, 0, 0, 0, 0.1 ) );

    	r.addBinding( Results.iri( NS + "oneMicro" ), Results.duration( true, 0, 0, 0, 0, 0, 0.000001 ) );
    	r.addBinding( Results.iri( NS + "oneMilli" ), Results.duration( true, 0, 0, 0, 0, 0, 0.001 ) );
    	
    	r.addBinding( Results.iri( NS + "oneSecond" ), Results.duration( true, 0, 0, 0, 0, 0, 1 ) );
    	r.addBinding( Results.iri( NS + "oneMinute" ), Results.duration( true, 0, 0, 0, 0, 1, 0 ) );
    	r.addBinding( Results.iri( NS + "oneHour" ), Results.duration( true, 0, 0, 0, 1, 0, 0 ) );
    	
    	r.addBinding( Results.iri( NS + "oneDay" ), Results.duration( true, 0, 0, 1, 0, 0, 0 ) );
    	r.addBinding( Results.iri( NS + "oneMonth" ), Results.duration( true, 0, 1, 0, 0, 0, 0 ) );
    	r.addBinding( Results.iri( NS + "oneYear" ), Results.duration( true, 1, 0, 0, 0, 0, 0 ) );
    	
    	query = "?x[age hasValue ?a]";
    	
    	System.out.println( "Query: " + query );

//    	System.out.println( OntologyHelper.toString( LPHelper.executeQuery( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, getLPReasoner() ) ) );
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, r.get(), getLPReasoner() );
    }
}
