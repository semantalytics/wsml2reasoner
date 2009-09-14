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

    private static final String NS = "http://example.com/wsml2reasoner/datatypes01#";

    private static final String ONTOLOGY_FILE = "files/datatypes1_declared.wsml";

    public void testValuesForDeclaredAttributes() throws Exception {
    	String query = "?instance[?attribute hasValue ?value]";
    	
    	Results r = new Results( "instance", "attribute", "value" );
    	
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "aString" ), Results._string( "string-value" ) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "a_String" ), Results._string( "string-value_" ) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "aXSD_String" ), Results._string("string-value-xsd" ) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "aDecimal" ), Results._decimal( 123456.78901 ) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "a_Decimal" ), Results._decimal(  987654.32101 ) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "aXSD_Decimal" ), Results._decimal(123456.78901 ) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "aInteger" ), Results._integer( 12345 ) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "a_Integer" ), Results._integer( 12345 ) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "aXSD_Integer" ), Results._integer( 12345 ) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "aFloat" ), Results._float( 123.456f ) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "a_Float" ), Results._float( 123.456f ) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "aXSD_Float" ), Results._float( 123.456f ) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "aDouble" ), Results._double( 12345.6789 ) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "a_Double" ), Results._double( 12345.6789 ) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "aXSD_Double" ), Results._double( 12345.6789 ) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "aBoolean" ), Results._bool( true ) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "a_Boolean" ), Results._bool( true ) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "aXSD_Boolean" ), Results._bool( true ) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "aDuration" ), Results._duration( 1, 2, 3, 4, 5, 6.0 ) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "a_Duration" ), Results._duration( 1, 2, 3, 4, 5, 6.1 ) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "aXSD_Duration" ), Results._duration( 1, 2, 3, 4, 5, 6.2 ) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "aDateTime" ), Results._datetime( 1981, 12, 31, 23, 59, 58.0, 0, 0));
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "a_DateTime" ), Results._datetime( 1981, 12, 31, 23, 59, 58.1, 0, 0));
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "aXSD_DateTime" ), Results._datetime( 1981, 12, 31, 23, 59, 58.2, 0, 0 ));
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "aTime" ), Results._time( 23,59,58.0,0,0 ) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "a_Time" ), Results._time( 23,59,58.1,0,0 ) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "aXSD_Time" ), Results._time( 23,59,58.2,0,0 ) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "aDate" ), Results._date( 1981, 12, 29,0,0 ) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "a_Date" ), Results._date( 1981, 12, 30,0,0 ) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "aXSD_Date" ), Results._date( 1981, 12, 31,0,0 ) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "aGYearMonth" ), Results._yearMonth( 2008, 10 ) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "a_GYearMonth" ), Results._yearMonth( 2008, 11 ) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "aXSD_GYearMonth" ), Results._yearMonth( 2008, 12 ) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "aGYear" ), Results._year( 1999 ) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "a_GYear" ), Results._year( 2000 ) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "aXSD_GYear" ), Results._year( 2001 ) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "aGMonthDay" ), Results._monthDay( 10, 31 ) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "a_GMonthDay" ), Results._monthDay( 11, 31 ) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "aXSD_GMonthDay" ), Results._monthDay( 12, 31 ) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "aGDay" ), Results._day( 28 ) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "a_GDay" ), Results._day( 29 ) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "aXSD_GDay" ), Results._day( 30 ) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "aGMonth" ), Results._month( 2 ) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "a_GMonth" ), Results._month( 3 ) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "aXSD_GMonth" ), Results._month( 4 ) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "aHexBinary" ), Results._hexBinary( "0FB7ABCD" ) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "a_HexBinary" ), Results._hexBinary( "0FB7ABCD" ) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "aXSD_HexBinary" ), Results._hexBinary( "0FB7ABCD" ) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "aBase64Binary" ), Results._base64Binary( "QmFycnkgQmlzaG9w" ) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "a_Base64Binary" ), Results._base64Binary( "QmFycnkgQmlzaG9w" ) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "aXSD_Base64Binary" ), Results._base64Binary( "QmFycnkgQmlzaG9w" ) );
    	
    	// RDF data types
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "aRDFText" ), Results._text("Family Guy", "en" ) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "a_RDFText" ), Results._text("Padre de Familia", "es") );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "aRDF_XMLLiteral" ), Results._xmlliteral("<tag>XML Things</tag>", ""));
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "a_RDF_XMLLiteral" ), Results._xmlliteral("<tag>XML Literal</tag>", ""));
    	
    	// new XSD Datatypes
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "aYearMonthDuration" ), Results._yearmonthduration(2008, 10) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "a_YearMonthDuration" ),  Results._yearmonthduration(2008, 11) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "aDayTimeDuration" ), Results._daytimeduration(1, 10, 31, 15.5) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "a_DayTimeDuration" ), Results._daytimeduration(1, 10, 31, 15.6) );
    	
    	LPHelper.outputON();
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, r.get(), getLPReasoner() );
    }
}
