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

public abstract class AbstractDataTypes1Declared extends TestCase implements LP {

    private static final String NS = "http://example.com/datatypes1#";

    private static final String ONTOLOGY_FILE = "files/datatypes1_declared.wsml";

    public void testValuesForDeclaredAttributes() throws Exception {
    	String query = "?instance[?attribute hasValue ?value]";
    	
    	Results r = new Results( "instance", "attribute", "value" );
    	r.addBinding( r.iri( NS + "anInstance" ), r.iri( NS + "aString" ), r.string( "string-value" ) );
    	r.addBinding( r.iri( NS + "anInstance" ), r.iri( NS + "aDecimal" ), r.decimal( 123456.78901 ) );
    	r.addBinding( r.iri( NS + "anInstance" ), r.iri( NS + "aInteger" ), r._integer( 12345 ) );
    	r.addBinding( r.iri( NS + "anInstance" ), r.iri( NS + "aFloat" ), r._float( 123.456f ) );
    	r.addBinding( r.iri( NS + "anInstance" ), r.iri( NS + "aDouble" ), r._double( 12345.6789 ) );
    	r.addBinding( r.iri( NS + "anInstance" ), r.iri( NS + "aBoolean" ), r.bool( true ) );
    	r.addBinding( r.iri( NS + "anInstance" ), r.iri( NS + "aDuration" ), r.duration( true, 1, 2, 3, 4, 5, 6 ) );
    	r.addBinding( r.iri( NS + "anInstance" ), r.iri( NS + "aDateTime" ), r.datetime( 1981, 12, 31, 23, 59, 58, 0, 0 ) );
    	r.addBinding( r.iri( NS + "anInstance" ), r.iri( NS + "aTime" ), r.time( 23,59,58,0,0 ) );
    	r.addBinding( r.iri( NS + "anInstance" ), r.iri( NS + "aDate" ), r.date( 1981, 12, 31,0,0 ) );
    	r.addBinding( r.iri( NS + "anInstance" ), r.iri( NS + "aGYearMonth" ), r.yearMonth( 2008, 12 ) );
    	r.addBinding( r.iri( NS + "anInstance" ), r.iri( NS + "aGYear" ), r.year( 1999 ) );
    	r.addBinding( r.iri( NS + "anInstance" ), r.iri( NS + "aGMonthDay" ), r.monthDay( 12, 31 ) );
    	r.addBinding( r.iri( NS + "anInstance" ), r.iri( NS + "aGDay" ), r.day( 28 ) );
    	r.addBinding( r.iri( NS + "anInstance" ), r.iri( NS + "aGMonth" ), r.month( 2 ) );
    	r.addBinding( r.iri( NS + "anInstance" ), r.iri( NS + "aHexBinary" ), r.hexBinary( "0FB7ABCD" ) );
    	r.addBinding( r.iri( NS + "anInstance" ), r.iri( NS + "aBase64Binary" ), r.base64Binary( "QmFycnkgQmlzaG9w" ) );
    	
//    	System.out.println( OntologyHelper.toString( LPHelper.executeQuery( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, getLPReasoner() ) ) );
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, r.get(), getLPReasoner() );
    }
}