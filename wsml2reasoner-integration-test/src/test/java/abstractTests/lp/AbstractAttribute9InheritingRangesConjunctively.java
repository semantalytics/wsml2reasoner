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
import junit.framework.TestCase;

import org.wsml.reasoner.api.inconsistency.InconsistencyException;

import abstractTests.LP;

public abstract class AbstractAttribute9InheritingRangesConjunctively extends TestCase implements LP {

    private static final String ONTOLOGY_FILE = "attribute9_inheriting_ranges_conjunctively.wsml";
    
    public void testInconsistency() throws Exception {
        String query = "?x memberOf ?y";
        try{
            LPHelper.executeQuery( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, getLPReasoner() );
            fail("Should have thrown InconsistencyException");
        }
        catch (InconsistencyException e){
        }
    }
}
