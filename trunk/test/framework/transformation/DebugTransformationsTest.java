package framework.transformation;

import java.util.HashSet;
import java.util.Set;

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
        //in order to keep track of cyclic imports
        Set<Ontology> importedOntologies = new HashSet<Ontology>();
        WSMO4JManager wmsoManager = new WSMO4JManager();
        axiomatizationNormalizer = new AxiomatizationNormalizer(wmsoManager, importedOntologies);
        debuggingNormalizer = new ConstraintReplacementNormalizer(wmsoManager);

    }
    
    @Override
    protected void tearDown() throws Exception {
    	super.tearDown();
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
}
