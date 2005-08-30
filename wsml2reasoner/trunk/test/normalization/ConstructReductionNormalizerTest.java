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
package normalization;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.deri.wsml.reasoner.normalization.ConstructReductionNormalizer;
import org.deri.wsml.reasoner.normalization.OntologyNormalizer;
import org.omwg.ontology.Ontology;
import org.wsmo.common.TopEntity;
import org.wsmo.factory.Factory;
import org.wsmo.wsml.Serializer;

public class ConstructReductionNormalizerTest extends WSMLNormalizationTest
{
    protected OntologyNormalizer normalizer;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        normalizer = new ConstructReductionNormalizer();
    }

    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    public void testNestedImplications() throws Exception
    {
        // read test ontology:
        Ontology ontology = parseOntology("examples/constructs.wsml");

        // normalize ontology with the LEConstructReductionNormalizer:
        Ontology normOnt = normalizer.normalize(ontology);

        // test whether produced expression is correct
        // by means of regular expressions matched against serialized result
        // ontology:
        String normString = serializeOntology(normOnt);
        Pattern pattern = Pattern.compile(".*E.*impliedBy.*C.*or.*D.*impliedBy.*A.*and.*B.*and.*A.*and.*B.*impliedBy.*E.*impliedBy.*C.*or.*D.*", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(normString);
        assertTrue(matcher.find());
    }

    public void testMoleculeDecomposition() throws Exception
    {
        // read test ontology:
        Ontology ontology = parseOntology("examples/constructs.wsml");

        // normalize ontology with the LEConstructReductionNormalizer:
        Ontology normOnt = normalizer.normalize(ontology);

        // test whether produced expression is correct
        // by means of regular expressions matched against serialized result
        // ontology:
        String normString = serializeOntology(normOnt);
        Pattern pattern = Pattern.compile("c.*\\[.*r1.*hasValue.*v1.*\\].*and.*c.*\\[.*r3.*hasValue.*v3.*].*and.*c.*\\[.*r2.*hasValue.*v2.*\\].*", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(normString);
        assertTrue(matcher.find());
    }

    public void testNegationNormalization() throws Exception
    {
        // read test ontology:
        Ontology ontology = parseOntology("examples/constructs.wsml");

        // normalize ontology with the LEConstructReductionNormalizer:
        Ontology normOnt = normalizer.normalize(ontology);

        // test whether produced expression is correct
        // by means of regular expressions matched against serialized result
        // ontology:
        String normString = serializeOntology(normOnt);
System.out.println(normString);

        Pattern pattern = Pattern.compile("c.*\\[.*r1.*hasValue.*v1.*\\].*and.*c.*\\[.*r3.*hasValue.*v3.*].*and.*c.*\\[.*r2.*hasValue.*v2.*\\].*", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(normString);
        assertTrue(matcher.find());
    }
}
