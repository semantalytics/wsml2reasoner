package wsml2reasoner.normalization;

import org.omwg.ontology.Ontology;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsml.reasoner.transformation.AxiomatizationNormalizer;
import org.wsml.reasoner.transformation.ConstructReductionNormalizer;
import org.wsml.reasoner.transformation.OntologyNormalizer;

public class DebugTransformationsTest extends WSMLNormalizationTest
{
    private OntologyNormalizer axiomatizationNormalizer, reductionNormalizer;
    
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        WSMO4JManager wmsoManager = new WSMO4JManager();
        axiomatizationNormalizer = new AxiomatizationNormalizer(wmsoManager);
        reductionNormalizer = new ConstructReductionNormalizer(wmsoManager);

    }
    public void testAxiomIDGeneration()
    {
        Ontology ontology = null;
        try
        {
            ontology = parseOntology("c:/projects/DIP/WP1/reviewPrep/bundles.wsml");
        } catch(Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Ontology normOnt = axiomatizationNormalizer.normalize(ontology);
//        normOnt = reductionNormalizer.normalize(normOnt);
        System.out.println(serializeOntology(normOnt));
    }

    public static void main(String[] args)
    {
        // TODO Auto-generated method stub

    }

}
