/*
 * Copyright (c) 2005 National University of Ireland, Galway
 *                    University of Innsbruck, Austria
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA  
 */

package abstractTests.lp;

import helper.LPHelper;
import helper.OntologyHelper;
import helper.Results;
import junit.framework.TestCase;
import abstractTests.LPTest;

/** 
 * Simple tests for function symbols (constructed terms).
 */
public abstract class AbstractFunctionSymbols extends TestCase implements LPTest{

    private static final String NS = "http://examples.com/ontologies/mytravel#";

    private static final String ONTOLOGY_FILE = "files/Travel.wsml";
    
    public void testFSHasVoucher() throws Exception {
        String query = "?x memberOf travelVoucher";
    
        Results results = new Results( "x" );
        
        results.addBinding( Results.fn( NS+ "f", Results.iri( NS+"my_trainTicket1" ) ) );
        results.addBinding( Results.fn( NS+ "f", Results.iri( NS+"my_trainTicket2" ) ) );
        results.addBinding( Results.fn( NS+ "f", Results.iri( NS+"my_trainTicket3" ) ) );
        
    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology(ONTOLOGY_FILE ), query, results.get(), getLPReasoner() );
    }
    
    public void testFSHasValidVoucher() throws Exception {
        String query = "?x memberOf validVoucher";

        Results results = new Results( "x" );
        
        results.addBinding( Results.fn( NS+ "f", Results.iri( NS+"my_trainTicket1" ), Results.iri( NS+"customer1" ) ) );
        results.addBinding( Results.fn( NS+ "f", Results.iri( NS+"my_trainTicket2" ), Results.iri( NS+"customer2" ) ) );

    	LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology(ONTOLOGY_FILE ), query, results.get(), getLPReasoner() );
    }
}
