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
package framework.transformation;

import java.util.HashSet;
import java.util.Set;

import org.omwg.ontology.Axiom;
import org.omwg.ontology.Ontology;
import org.sti2.wsmo4j.factory.WsmlFactoryContainer;
import org.wsml.reasoner.transformation.AxiomatizationNormalizer;
import org.wsml.reasoner.transformation.ConstraintReplacementNormalizer;
import org.wsml.reasoner.transformation.OntologyNormalizer;
import org.wsmo.common.Entity;
import org.wsmo.common.exception.InvalidModelException;
import org.wsmo.factory.FactoryContainer;

import framework.normalization.BaseNormalizationTest;

public class DebugTransformationsTest extends BaseNormalizationTest
{
    private OntologyNormalizer axiomatizationNormalizer, debuggingNormalizer;
    
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        FactoryContainer factory = new WsmlFactoryContainer();
        axiomatizationNormalizer = new AxiomatizationNormalizer(factory);
        debuggingNormalizer = new ConstraintReplacementNormalizer(factory);

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
            ontology = parseOntology("SkillOntology.wsml");
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
