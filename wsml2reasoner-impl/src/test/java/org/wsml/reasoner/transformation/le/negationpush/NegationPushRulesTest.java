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
package org.wsml.reasoner.transformation.le.negationpush;

import junit.framework.TestCase;

import org.sti2.wsmo4j.factory.WsmlFactoryContainer;
import org.wsmo.wsml.ParserException;


public class NegationPushRulesTest extends TestCase {

    private NegationPushRules rules;
    
    public NegationPushRulesTest() {
        super();
    }
    
    protected void setUp() throws Exception {
        super.setUp();
        this.rules = new NegationPushRules(new WsmlFactoryContainer());
    }
    
    
    public void testGetRules() throws ParserException {
        assertEquals(3, rules.getRules().size());
    }
}
