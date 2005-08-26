package normalization;

import org.deri.wsml.reasoner.normalization.LloydToporNormalizer;
import org.deri.wsml.reasoner.normalization.OntologyNormalizer;
import org.deri.wsml.reasoner.normalization.le.LloydToporRules;
import org.omwg.ontology.Ontology;

public class LloydToporNormalizerTest extends WSMLNormalizationTest
{
    protected OntologyNormalizer normalizer;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        normalizer = new LloydToporNormalizer();
    }

    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    public void testNestedOperations() throws Exception
    {
        // read test ontology:
        Ontology ontology = parseOntology("examples/lloyd-topor.wsml");

        // normalize ontology with the LELloydToporNormalizer:
        Ontology normOnt = normalizer.normalize(ontology);
        System.out.println(LloydToporRules.instantiate().toString());

        // test whether produced ontology contains exactly 4 axioms:
        assertTrue(normOnt.listAxioms().size() == 4);
    }
}
