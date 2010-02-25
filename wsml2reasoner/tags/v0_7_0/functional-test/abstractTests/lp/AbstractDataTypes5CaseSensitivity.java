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

public abstract class AbstractDataTypes5CaseSensitivity extends TestCase implements LP {

    private static final String NS = "http://example.com/datatypes5#";

    private static final String ONTOLOGY_FILE = "files/datatypes5_case_sensitivity.wsml";

    public void testStringLower() throws Exception {
    	String query = "_\"" + NS + "i1\"[?attribute hasValue _string( \"string\" )]";
    	
    	Results r = new Results( "attribute" );
    	r.addBinding( Results.iri( NS + "lowerCaseString" ) );
    	
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, r.get(), getLPReasoner() );
    }

    public void testStringUpper() throws Exception {
    	String query = "_\"" + NS + "i1\"[?attribute hasValue _string( \"STRING\" )]";
    	
    	Results r = new Results( "attribute" );
    	r.addBinding( Results.iri( NS + "upperCaseString" ) );
    	
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, r.get(), getLPReasoner() );
    }

    public void testHexBinaryLower() throws Exception {
    	String query = "_\"" + NS + "i1\"[?attribute hasValue _hexbinary( \"abcd\" )]";
    	
    	Results r = new Results( "attribute" );
    	r.addBinding( Results.iri( NS + "lowerCaseHex" ) );
    	r.addBinding( Results.iri( NS + "upperCaseHex" ) );
    	
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, r.get(), getLPReasoner() );
    }

    public void testHexBinaryUpper() throws Exception {
    	String query = "_\"" + NS + "i1\"[?attribute hasValue _hexbinary( \"ABCD\" )]";
    	
    	Results r = new Results( "attribute" );
    	r.addBinding( Results.iri( NS + "lowerCaseHex" ) );
    	r.addBinding( Results.iri( NS + "upperCaseHex" ) );
    	
//    	System.out.println( "Query: " + query );
//    	System.out.println( OntologyHelper.toString( LPHelper.executeQuery( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, getLPReasoner() ) ) );
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, r.get(), getLPReasoner() );
    }
}
