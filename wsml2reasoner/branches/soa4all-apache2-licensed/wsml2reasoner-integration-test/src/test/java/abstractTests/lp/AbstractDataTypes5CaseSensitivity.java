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

public abstract class AbstractDataTypes5CaseSensitivity extends TestCase implements LP {

    private static final String NS = "http://example.com/datatypes5#";

    private static final String ONTOLOGY_FILE = "datatypes5_case_sensitivity.wsml";

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
