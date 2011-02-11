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
import abstractTests.Core;

public abstract class AbstractOntology1SimpleImport extends TestCase implements Core {

    public void testImportOntologyFromSameWsmlFile() throws Exception {
        String ns = "http://www.example-com/ontology1#";
        String ONTOLOGY_FILE = "ontology1_simple_import.wsml";
        String concept = ns + "c";
        
        Results r = new Results( "X" );
        
        r.addBinding( Results.iri( ns + "i1" ) );
        r.addBinding( Results.iri( ns + "i2" ) );

        CoreHelper.queryXMemberOfConceptAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), getReasoner(), concept, r.get() );
    }
}    
