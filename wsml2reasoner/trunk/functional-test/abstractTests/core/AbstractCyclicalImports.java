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
package abstractTests.core;

import helper.CoreHelper;
import helper.OntologyHelper;
import helper.Results;
import junit.framework.TestCase;
import org.omwg.ontology.Ontology;
import abstractTests.Core;

public abstract class AbstractCyclicalImports extends TestCase implements Core {
	
    private static final String ONTOLOGY_FILE1 = "files/CyclicalImports1.wsml";
    private static final String ONTOLOGY_FILE2 = "files/CyclicalImports2.wsml";
    
    private static final String ns1 = "http://here.comes.the.whistleman/CyclicalImports1#";
    private static final String ns2 = "http://here.comes.the.whistleman/CyclicalImports2#";

    // A reference for the imported ontology.
    private Ontology importedOntology;
    
    protected void setUp() throws Exception {
    	importedOntology = OntologyHelper.loadOntology( ONTOLOGY_FILE2 );
    }

    public void testCyclicalImports1() throws Exception {
        
        assertNotNull( importedOntology );
     
        Results r = new Results( "X", "Y" );
        
        r.addBinding( Results.iri( ns2 + "Cy2i1" ), Results.iri( ns1 + "Master" ) );
        r.addBinding( Results.iri( ns1 + "Cy1i1" ), Results.iri( ns2 + "Slave" ) );
        r.addBinding( Results.iri( ns1 + "RolandKirk" ), Results.iri( ns1 + "JazzMusician" ) );
        r.addBinding( Results.iri( ns1 + "JohnScofield" ), Results.iri( ns1 + "JazzMusician" ) );
        r.addBinding( Results.iri( ns2 + "KarlDenson" ), Results.iri( ns1 + "JazzMusician" ) );
        r.addBinding( Results.iri( ns2 + "JohnMedeski" ), Results.iri( ns1 + "JazzMusician" ) );

        CoreHelper.queryXMemberOfYAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE1 ), getReasoner(), r.get() );
    }
    
    public void testCyclicalImports2() throws Exception {
        Results r = new Results( "X" );
        
        r.addBinding( Results.iri( ns2 + "KarlDenson" ) );
        r.addBinding( Results.iri( ns2 + "JohnMedeski" ) );
        r.addBinding( Results.iri( ns1 + "RolandKirk" ) );
        r.addBinding( Results.iri( ns1 + "JohnScofield" ) );
    	
        assertNotNull( importedOntology );
        
        CoreHelper.queryXMemberOfConceptAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE1 ), getReasoner(), ns1 + "JazzMusician", r.get() );
    }
}
