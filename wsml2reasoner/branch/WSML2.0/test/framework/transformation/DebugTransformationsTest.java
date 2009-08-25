package framework.transformation;

import java.util.HashSet;
import java.util.Set;

import org.omwg.ontology.Axiom;
import org.omwg.ontology.Ontology;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsml.reasoner.transformation.AxiomatizationNormalizer;
import org.wsml.reasoner.transformation.ConstraintReplacementNormalizer;
import org.wsml.reasoner.transformation.OntologyNormalizer;
import org.wsmo.common.Entity;
import org.wsmo.common.exception.InvalidModelException;

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
    
    @Override
    protected void tearDown() throws Exception {
    	super.tearDown();
    }
    
    public void testAxiomIDGeneration() throws InvalidModelException
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
        
        Set <Entity> entities = new HashSet <Entity>();
    	entities.addAll(ontology.listConcepts());
    	entities.addAll(ontology.listInstances());
    	entities.addAll(ontology.listRelations());
    	entities.addAll(ontology.listRelationInstances());
    	entities.addAll(ontology.listAxioms());
        
    	Set <Entity> entitiesAsAxioms = axiomatizationNormalizer.normalizeEntities( entities );
    	
    	Set <Axiom> axioms = new HashSet <Axiom> ();
        for (Entity e : entitiesAsAxioms){
        	if (e instanceof Axiom){
        		axioms.add((Axiom) e);
        	}
        }
    	
        axioms = debuggingNormalizer.normalizeAxioms(axioms);
        
        Ontology o = wsmoFactory.createOntology( wsmoFactory.createIRI( "http://www.AnonymousIDReplacementTestOntology.com" ) );
        for (Axiom a : axioms){
        	o.addAxiom(a);
        }
        
        System.out.println(serializeOntology(o));
    }
}
