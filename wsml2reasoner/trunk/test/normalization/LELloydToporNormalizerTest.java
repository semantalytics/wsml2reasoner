package normalization;

import org.deri.wsml.reasoner.normalization.LELLoydToporNormalizer;
import org.deri.wsml.reasoner.normalization.OntologyNormalizer;
import org.omwg.ontology.Ontology;

public class LELloydToporNormalizerTest extends WSMLNormalizationTest
{
    protected OntologyNormalizer normalizer;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        normalizer = new LELLoydToporNormalizer();
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

        // test whether produced ontology contains exactly 4 axioms:
        assertTrue(normOnt.listAxioms().size() == 4);
    }
}
