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
package framework.normalization;

import java.util.HashSet;
import java.util.Set;
import org.omwg.ontology.Axiom;
import org.omwg.ontology.Ontology;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsml.reasoner.transformation.LloydToporNormalizer;
import org.wsml.reasoner.transformation.OntologyNormalizer;
import org.wsml.reasoner.transformation.le.lloydtopor.LloydToporRules;

public class LloydToporNormalizerTest extends BaseNormalizationTest
{
    protected OntologyNormalizer normalizer;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        WSMO4JManager wmsoManager = new WSMO4JManager();
        normalizer = new LloydToporNormalizer(wmsoManager);
    }

    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    public void testNestedOperations() throws Exception
    {
        // read test ontology:
        Ontology ontology = parseOntology("files/lloyd-topor.wsml");

        Set <Axiom> axioms = new HashSet <Axiom>();
        axioms.addAll(ontology.listAxioms());
        
        // normalize ontology with the LELloydToporNormalizer:
        axioms = normalizer.normalizeAxioms(axioms);
        System.out.println("\n\n***\n"+ new LloydToporRules(new WSMO4JManager()).toString());

        // test whether produced ontology contains exactly 4 axioms:
        assertTrue(axioms.iterator().next().listDefinitions().size() == 4);
    }
}
