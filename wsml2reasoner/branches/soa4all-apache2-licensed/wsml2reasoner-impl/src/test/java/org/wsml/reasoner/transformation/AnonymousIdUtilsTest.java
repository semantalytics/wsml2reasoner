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
package org.wsml.reasoner.transformation;

import junit.framework.TestCase;

public class AnonymousIdUtilsTest extends TestCase {

    public void testIriGeneration() {
        String iri1 = AnonymousIdUtils.getNewAnonymousIri();
        System.out.println("First IRI: '" + iri1+"'");
        String iri2 = AnonymousIdUtils.getNewAnonymousIri();
        System.out.println("Second IRI: '" + iri2+"'");
        assertFalse(iri1.equals(iri2));
    }
    
    public void testIsAnonymousIri() {
        String iri1 = AnonymousIdUtils.getNewAnonymousIri();
        String iri2 = "urn:blah";
        assertTrue(AnonymousIdUtils.isAnonymousIri(iri1));
        assertFalse(AnonymousIdUtils.isAnonymousIri(iri2));
    }

}