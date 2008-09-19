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

public abstract class AbstractDateTimeSubtraction extends TestCase implements LP {

    private static final String NS = "http://example.com/date_time_sub#";

    private static final String ONTOLOGY_FILE = "files/date_time_subtraction.wsml";

    public void testDateTimeSubtract() throws Exception {
    	String query = "?x[age hasValue ?t]";

    	Results r = new Results( "x", "a" );
    	r.addBinding( r.iri( NS + "i1" ), r.duration( true, 1, 2, 3, 4, 5, 6.7 ) );
    	r.addBinding( r.iri( NS + "i2" ), r.duration( true, 0, 0, 0, 0, 0, 0.1 ) );
    	r.addBinding( r.iri( NS + "i3" ), r.duration( false, 0, 0, 0, 0, 0, 0.1 ) );

    	r.addBinding( r.iri( NS + "oneMilli" ), r.duration( true, 0, 0, 0, 0, 0, 0.001 ) );
    	
    	r.addBinding( r.iri( NS + "oneSecond" ), r.duration( true, 0, 0, 0, 0, 0, 1 ) );
    	r.addBinding( r.iri( NS + "oneMinute" ), r.duration( true, 0, 0, 0, 0, 1, 0 ) );
    	r.addBinding( r.iri( NS + "oneHour" ), r.duration( true, 0, 0, 0, 1, 0, 0 ) );
    	
    	r.addBinding( r.iri( NS + "oneDay" ), r.duration( true, 0, 0, 1, 0, 0, 0 ) );
    	r.addBinding( r.iri( NS + "oneMonth" ), r.duration( true, 0, 1, 0, 0, 0, 0 ) );
    	r.addBinding( r.iri( NS + "oneYear" ), r.duration( true, 1, 0, 0, 0, 0, 0 ) );
    	
    	query = "?x[age hasValue ?a]";
    	
    	System.out.println( "Query: " + query );

//    	System.out.println( OntologyHelper.toString( LPHelper.executeQuery( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, getLPReasoner() ) ) );
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, r.get(), getLPReasoner() );
    }
}
