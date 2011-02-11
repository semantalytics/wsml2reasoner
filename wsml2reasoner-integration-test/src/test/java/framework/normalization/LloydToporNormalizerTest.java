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
package framework.normalization;

import java.util.HashSet;
import java.util.Set;

import org.omwg.ontology.Axiom;
import org.omwg.ontology.Ontology;
import org.sti2.wsmo4j.factory.WsmlFactoryContainer;
import org.wsml.reasoner.transformation.LloydToporNormalizer;
import org.wsml.reasoner.transformation.OntologyNormalizer;
import org.wsml.reasoner.transformation.le.lloydtopor.LloydToporRules;
import org.wsmo.factory.FactoryContainer;

public class LloydToporNormalizerTest extends BaseNormalizationTest
{
    protected OntologyNormalizer normalizer;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        FactoryContainer wsmoManager = new WsmlFactoryContainer();
        normalizer = new LloydToporNormalizer(wsmoManager);
    }

    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    public void testNestedOperations() throws Exception
    {
        // read test ontology:
        Ontology ontology = parseOntology("lloyd-topor.wsml");

        Set <Axiom> axioms = new HashSet <Axiom>();
        axioms.addAll(ontology.listAxioms());
        
        // normalize ontology with the LELloydToporNormalizer:
        axioms = normalizer.normalizeAxioms(axioms);
        System.out.println("\n\n***\n"+ new LloydToporRules(new WsmlFactoryContainer()).toString());

        // test whether produced ontology contains exactly 4 axioms:
        assertTrue(axioms.iterator().next().listDefinitions().size() == 4);
    }
}
