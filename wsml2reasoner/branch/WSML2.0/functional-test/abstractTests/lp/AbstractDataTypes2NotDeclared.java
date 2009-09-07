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

public abstract class AbstractDataTypes2NotDeclared extends TestCase implements LP {

    private static final String NS = "http://example.com/datatypes2#";

    private static final String ONTOLOGY_FILE = "files/datatypes2_not_declared.wsml";

    public void testValuesForNotDeclaredAttributes() throws Exception {
    	String query = "?instance[?attribute hasValue ?value]";
    	
    	Results r = new Results( "instance", "attribute", "value" );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "aString" ), Results._string( "string-value" ) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "aDecimal" ), Results._decimal( 123456.78901 ) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "aInteger" ), Results._integer( 12345 ) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "aFloat" ), Results._float( 123.456f ) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "aDouble" ), Results._double( 12345.6789 ) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "aBoolean" ), Results._bool( true ) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "aDuration" ), Results._duration(1, 2, 3, 4, 5, 6.0 ) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "aDateTime" ), Results._datetime( 1981, 12, 31, 23, 59, 58, 0, 0 ) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "aTime" ), Results._time( 23,59,58,0,0 ) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "aDate" ), Results._date( 1981, 12, 31,0,0 ) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "aGYearMonth" ), Results._yearMonth( 2008, 12 ) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "aGYear" ), Results._year( 1999 ) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "aGMonthDay" ), Results._monthDay( 12, 31 ) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "aGDay" ), Results._day( 28 ) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "aGMonth" ), Results._month( 2 ) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "aHexBinary" ), Results._hexBinary( "0FB7ABCD" ) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "aBase64Binary" ), Results._base64Binary( "QmFycnkgQmlzaG9w" ) );
    	
//    	System.out.println( OntologyHelper.toString( LPHelper.executeQuery( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, getLPReasoner() ) ) );
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, r.get(), getLPReasoner() );
    }
}
