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

public abstract class AbstractDataTypes10Declared extends TestCase implements LP {

    private static final String NS = "http://example.com/datatypes10#";

    private static final String ONTOLOGY_FILE = "files/datatypes10_new_declared.wsml";

    public void testValuesForDeclaredAttributes() throws Exception {
    	String query = "?instance[?attribute hasValue ?value]";
    	
    	Results r = new Results( "instance", "attribute", "value" );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "aDuration" ), Results._duration( 1, 2, 3, 4, 5, 6.0 ) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "aRDFText" ), Results._string( "This should be a RDF Text" ) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "aRDF_XMLLiteral" ), Results._string("<tag>xml literal</tag>" ) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "aYearMonthDuration" ), Results._string( "2009,9" ) );
    	r.addBinding( Results.iri( NS + "anInstance" ), Results.iri( NS + "aDayTimeDuration" ), Results._string( "1,10,31,15.5" ) );

    	LPHelper.outputON();
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, r.get(), getLPReasoner() );
    }
}
