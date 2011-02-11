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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.deri.wsmo4j.io.parser.wsml.WsmlLogicalExpressionParser;
import org.omwg.logicalexpression.LogicalExpressionParser;
import org.omwg.ontology.Axiom;
import org.omwg.ontology.Ontology;
import org.sti2.wsmo4j.factory.WsmlFactoryContainer;
import org.wsml.reasoner.transformation.ConstructReductionNormalizer;
import org.wsml.reasoner.transformation.OntologyNormalizer;
import org.wsmo.common.IRI;
import org.wsmo.factory.FactoryContainer;

public class ConstructReductionNormalizerTest extends BaseNormalizationTest
{
    protected OntologyNormalizer normalizer;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        FactoryContainer factory = new WsmlFactoryContainer();
        normalizer = new ConstructReductionNormalizer(factory);
    }

    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    public void testNestedImplications() throws Exception
    {
        // read test ontology:
        Ontology ontology = parseOntology("constructs.wsml");
        
        Set <Axiom> axioms = new HashSet <Axiom>();
        axioms.addAll(ontology.listAxioms());

        // normalize ontology with the LEConstructReductionNormalizer:
        axioms = normalizer.normalizeAxioms(axioms);
        
        Ontology o = wsmoFactory.createOntology( wsmoFactory.createIRI( "http://www.ConstructReductionNormalizerTestOntology.com" ) );
        for (Axiom a : axioms){
        	o.addAxiom(a);
        }

        // test whether produced expression is correct
        // by means of regular expressions matched against serialized result
        // ontology:
        String normString = serializeOntology(o);
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
        LogicalExpressionParser leParser = new WsmlLogicalExpressionParser(ontology);
        a.addDefinition(leParser.parse(
                "a[r1 hasValue v1, r2 hasValue v2] " +
                "or b[r1 hasValue v2] " +
                "or c[r2 hasValue v2, r1 hasValue v1]."));
        a.addDefinition(leParser.parse(
                "A[r1 ofType v1, r2 impliesType v2]."));
        
        Set <Axiom> axioms = new HashSet <Axiom>();
        axioms.addAll(ontology.listAxioms());
        
        // normalize ontology with the LEConstructReductionNormalizer:
        axioms = normalizer.normalizeAxioms(axioms);

        Ontology o = wsmoFactory.createOntology( wsmoFactory.createIRI( "http://www.ConstructReductionNormalizerTestOntology.com" ) );
        for (Axiom ax : axioms){
        	o.addAxiom(ax);
        }
        
        // test whether produced expression is correct
        // by means of regular expressions matched against serialized result
        // ontology:
        String normString = serializeOntology(o);
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
        Ontology ontology = parseOntology("constructs.wsml");

        Set <Axiom> axioms = new HashSet <Axiom>();
        axioms.addAll(ontology.listAxioms());
        
        // normalize ontology with the LEConstructReductionNormalizer:
        axioms = normalizer.normalizeAxioms(axioms);

        Ontology o = wsmoFactory.createOntology( wsmoFactory.createIRI( "http://www.ConstructReductionNormalizerTestOntology.com" ) );
        for (Axiom ax : axioms){
        	o.addAxiom(ax);
        }
        // test whether produced expression is correct
        
        String normString = serializeOntology(o);


        Pattern pattern = Pattern.compile("c.*\\[.*r1.*hasValue.*v1.*\\]");
        Matcher matcher = pattern.matcher(normString);
        assertTrue(matcher.find());
        
        pattern = Pattern.compile("c.*\\[.*r3.*hasValue.*v3.*\\]");
        matcher = pattern.matcher(normString);
        assertTrue(matcher.find());

        pattern = Pattern.compile("c.*\\[.*r2.*hasValue.*v2.*\\]");
        matcher = pattern.matcher(normString);
        assertTrue(matcher.find());
    }
}
