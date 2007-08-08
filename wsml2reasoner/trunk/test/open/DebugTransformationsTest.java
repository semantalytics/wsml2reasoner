package open;

import org.omwg.ontology.Ontology;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsml.reasoner.transformation.AxiomatizationNormalizer;
import org.wsml.reasoner.transformation.ConstraintReplacementNormalizer;
import org.wsml.reasoner.transformation.OntologyNormalizer;

import framework.normalization.BaseNormalizationTest;

public class DebugTransformationsTest extends BaseNormalizationTest
{
    private OntologyNormalizer axiomatizationNormalizer, debuggingNormalizer;
    
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        WSMO4JManager wmsoManager = new WSMO4JManager();
        axiomatizationNormalizer = new AxiomatizationNormalizer(wmsoManager);
        debuggingNormalizer = new ConstraintReplacementNormalizer(wmsoManager);

    }
    public void testAxiomIDGeneration()
    {
        Ontology ontology = null;
        try
        {
            ontology = parseOntology("files/SkillOntology.wsml");
        } catch(Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Ontology normOnt = axiomatizationNormalizer.normalize(ontology);
        normOnt = debuggingNormalizer.normalize(normOnt);
        System.out.println(serializeOntology(normOnt));
    }

    public static void main(String[] args)
    {
        // TODO Auto-generated method stub

    }

}
