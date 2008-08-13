/**
 * WSML Reasoner Implementation.
 *
 * Copyright (c) 2005, FZI, Germany.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * You should have received a copy of the GNU Lesser General Public License along
 * with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 */
package abstractTests.lp;

import helper.LPHelper;
import helper.OntologyHelper;
import helper.Results;
import junit.framework.TestCase;
import abstractTests.LPTest;

public abstract class AbstractConceptWithAttribute extends TestCase implements LPTest {

    /**
     * This function tests the query: ?x[?att ofType ?y] on an ontology with a Concept having an
     * attribute.
     */
    public void testConceptWithAttribute() throws Exception {
    	String ontology = ontologyheader + "concept c attr ofType integer ";
    	
        Results r = new Results( "x", "att", "y" );
        r.addBinding( Results.iri( NS + "c" ), Results.iri( NS + "attr" ), Results.iri( NS + "integer" ) );

        LPHelper.executeQueryAndCheckResults( OntologyHelper.parseOntology( ontology ), "?x[?att ofType ?y]", r.get(), getLPReasoner() );
    }

    private static final String NS = "urn:fooXX";
    private static final String ontologyheader = "namespace {_\"" + NS + "\"} ontology bar"+" \n";
}
