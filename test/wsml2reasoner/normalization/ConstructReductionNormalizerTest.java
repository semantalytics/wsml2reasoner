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
package wsml2reasoner.normalization;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.omwg.ontology.Axiom;
import org.omwg.ontology.Ontology;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsml.reasoner.transformation.ConstructReductionNormalizer;
import org.wsml.reasoner.transformation.OntologyNormalizer;
import org.wsmo.common.IRI;

public class ConstructReductionNormalizerTest extends WSMLNormalizationTest
{
    protected OntologyNormalizer normalizer;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        WSMO4JManager wmsoManager = new WSMO4JManager();
        normalizer = new ConstructReductionNormalizer(wmsoManager);
    }

    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    public void testNestedImplications() throws Exception
    {
        // read test ontology:
        Ontology ontology = parseOntology("wsml2reasoner/normalization/constructs.wsml");

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
        // create test ontology:
        IRI iri = wsmoFactory.createIRI("http://test#"+System.currentTimeMillis());
        Ontology ontology = wsmoFactory.createOntology(iri);
        ontology.setDefaultNamespace(iri);
        Axiom a = wsmoFactory.createAxiom(wsmoFactory.createAnonymousID());
        ontology.addAxiom(a);
        a.addDefinition(leFactory.createLogicalExpression(
                "a[r1 hasValue v1, r2 hasValue v2] " +
                "or b[r1 hasValue v2] " +
                "or c[r2 hasValue v2, r1 hasValue v1].", ontology));
        a.addDefinition(leFactory.createLogicalExpression(
                "A[r1 ofType v1, r2 impliesType v2].", ontology));
        
        // normalize ontology with the LEConstructReductionNormalizer:
        Ontology normOnt = normalizer.normalize(ontology);

        // test whether produced expression is correct
        // by means of regular expressions matched against serialized result
        // ontology:
        String normString = serializeOntology(normOnt);
        Pattern pattern = Pattern.compile("c.*\\[.*r1.*hasValue.*v1.*\\].*and.*c.*\\[.*r2.*hasValue.*v2.*].*", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(normString);
        Pattern pattern2 = Pattern.compile("c.*\\[.*r2.*hasValue.*v2.*\\].*and.*c.*\\[.*r1.*hasValue.*v1.*].*", Pattern.DOTALL);
        Matcher matcher2 = pattern2.matcher(normString);
        System.out.println(normString);
        assertTrue("Error in Nordmalization!",matcher.find() || matcher2.find() );
    }

    public void testNegationNormalization() throws Exception
    {
        // read test ontology:
        Ontology ontology = parseOntology("wsml2reasoner/normalization/constructs.wsml");

        // normalize ontology with the LEConstructReductionNormalizer:
        Ontology normOnt = normalizer.normalize(ontology);

        // test whether produced expression is correct
        
        String normString = serializeOntology(normOnt);
System.out.println(normString);

        Pattern pattern = Pattern.compile("c.*\\[.*r1.*hasValue.*v1.*\\].*and.*c.*\\[.*r3.*hasValue.*v3.*].*and.*c.*\\[.*r2.*hasValue.*v2.*\\].*", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(normString);
        assertTrue(matcher.find());
    }
}
