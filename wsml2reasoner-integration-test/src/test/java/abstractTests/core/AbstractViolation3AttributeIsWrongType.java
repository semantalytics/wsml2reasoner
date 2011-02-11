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
import junit.framework.TestCase;

import org.wsml.reasoner.api.inconsistency.InconsistencyException;

import abstractTests.Core;

public abstract class AbstractViolation3AttributeIsWrongType extends TestCase implements Core {

    private static final String ONTOLOGY_FILE = "violation3_attribute_is_wrong_type.wsml";
    
    public void testInconsistency() throws Exception {
        try{
        	CoreHelper.queryXMemberOfY( OntologyHelper.loadOntology( ONTOLOGY_FILE ), getReasoner() );
            fail("Should have thrown InconsistencyException");
        }
        catch (InconsistencyException e){
        }
    }
}
