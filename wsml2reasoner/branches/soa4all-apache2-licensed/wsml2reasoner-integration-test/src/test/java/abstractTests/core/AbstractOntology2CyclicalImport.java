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
package abstractTests.core;

import helper.CoreHelper;
import helper.OntologyHelper;
import helper.Results;
import junit.framework.TestCase;

import org.omwg.ontology.Ontology;

import abstractTests.Core;

public abstract class AbstractOntology2CyclicalImport extends TestCase implements Core {
	
    private static final String ONTOLOGY_FILE1 = "ontology2_cyclical_import1.wsml";
    private static final String ONTOLOGY_FILE2 = "ontology2_cyclical_import2.wsml";
    
    private static final String ns1 = "http://example.com/ontology2a#";
    private static final String ns2 = "http://example.com/ontology2b#";

    // A reference for the imported ontology.
    private Ontology importedOntology;
    
    protected void setUp() throws Exception {
    	importedOntology = OntologyHelper.loadOntology( ONTOLOGY_FILE2 );
    }

    // imports do not work any more as they did since there isn't one big static registry any more
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
