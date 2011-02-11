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

import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.ontology.Attribute;
import org.omwg.ontology.Axiom;
import org.omwg.ontology.Concept;
import org.omwg.ontology.Instance;
import org.omwg.ontology.Ontology;
import org.sti2.wsmo4j.factory.WsmlFactoryContainer;
import org.wsml.reasoner.transformation.AxiomatizationNormalizer;
import org.wsml.reasoner.transformation.ConstructReductionNormalizer;
import org.wsml.reasoner.transformation.OntologyNormalizer;
import org.wsmo.common.Entity;
import org.wsmo.factory.FactoryContainer;

public class AnonymousIDReplacementTest extends BaseNormalizationTest
{
    protected OntologyNormalizer reductionNormalizer, axiomatizationNormalizer;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        FactoryContainer factory = new WsmlFactoryContainer();
        axiomatizationNormalizer = new AxiomatizationNormalizer(factory);
        reductionNormalizer = new ConstructReductionNormalizer(factory);
    }

    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    public void testNestedImplications() throws Exception
    {
        // create test ontology:
        Ontology ontology = createOntology();
        Concept manConcept = wsmoFactory.createConcept(wsmoFactory.createIRI("urn://Man"));
        Concept locationConcept = wsmoFactory.createConcept(wsmoFactory.createIRI("urn://Location"));
        Attribute hasParentAttr = manConcept.createAttribute(wsmoFactory.createIRI("urn://hasParent"));
        hasParentAttr.addInferringType(manConcept);
        Attribute livesAtAttr = manConcept.createAttribute(wsmoFactory.createIRI("urn://livesAt"));
        livesAtAttr.addInferringType(locationConcept);
        Instance aragorn = wsmoFactory.createInstance(wsmoFactory.createIRI("urn://Aragorn"), manConcept);
        Instance arathorn = wsmoFactory.createInstance(wsmoFactory.createIRI("urn://Arathorn"), manConcept);
        Instance elendil = wsmoFactory.createInstance(wsmoFactory.createIRI("urn://Elendil"), manConcept);
        aragorn.addAttributeValue(hasParentAttr.getIdentifier(), arathorn);
        aragorn.addAttributeValue(hasParentAttr.getIdentifier(), wsmoFactory.createInstance(wsmoFactory.createAnonymousID()));
        Axiom aragornLivesAx = wsmoFactory.createAxiom(wsmoFactory.createIRI("urn://aragornLivesSomewhere"));
        aragornLivesAx.addDefinition(leFactory.createConjunction(leFactory.createAttributeValue(aragorn.getIdentifier(), livesAtAttr.getIdentifier(), leFactory.createAnonymousID((byte)1)) , leFactory.createMemberShipMolecule(leFactory.createAnonymousID((byte)1),locationConcept.getIdentifier())));
        Axiom arathornLivesAx = wsmoFactory.createAxiom(wsmoFactory.createIRI("urn://arathornLivesSomewhere"));
        arathornLivesAx.addDefinition(leFactory.createConjunction(leFactory.createConjunction(leFactory.createConjunction(leFactory.createConjunction(leFactory.createAttributeValue(arathorn.getIdentifier(), livesAtAttr.getIdentifier(), leFactory.createAnonymousID((byte)1)), leFactory.createMemberShipMolecule(leFactory.createAnonymousID((byte)1), locationConcept.getIdentifier())), leFactory.createAttributeValue(elendil.getIdentifier(),livesAtAttr.getIdentifier(), leFactory.createAnonymousID((byte)2))), leFactory.createMemberShipMolecule(leFactory.createAnonymousID((byte)2), locationConcept.getIdentifier())), leFactory.createAttributeValue(elendil.getIdentifier(), livesAtAttr.getIdentifier(), wsmoFactory.createAnonymousID())));
        ontology.addConcept(manConcept);
        ontology.addConcept(locationConcept);
        ontology.addInstance(aragorn);
        ontology.addInstance(arathorn);
        ontology.addAxiom(aragornLivesAx);
        ontology.addAxiom(arathornLivesAx);
        System.out.println(serializeOntology(ontology)+"\n\n\n-------------\n\n\n");
        
        // normalize ontology with the LEConstructReductionNormalizer:
        Set <Entity> entities = new HashSet <Entity>();
    	entities.addAll(ontology.listConcepts());
    	entities.addAll(ontology.listInstances());
    	entities.addAll(ontology.listRelations());
    	entities.addAll(ontology.listRelationInstances());
    	entities.addAll(ontology.listAxioms());
        
        Set <Entity> entitiesAsAxioms = axiomatizationNormalizer.normalizeEntities(entities);
        
        Set <Axiom> axioms = new HashSet <Axiom> ();
        for (Entity e : entitiesAsAxioms){
        	if (e instanceof Axiom){
        		axioms.add((Axiom) e);
        	}
        }
        
        axioms = reductionNormalizer.normalizeAxioms(axioms);
        
        Ontology o = wsmoFactory.createOntology( wsmoFactory.createIRI( "http://www.AnonymousIDReplacementTestOntology.com" ) );
        for (Axiom a : axioms){
        	o.addAxiom(a);
        }

        // test whether produced expression is correct
        // by means of regular expressions matched against serialized result
        // ontology:
        String normString = serializeOntology(o);
        System.out.println(normString);
        Pattern pattern = Pattern.compile("Arathorn.*livesAt.*(anonymous.*).*and.*\\1.*memberOf.*Location", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(normString);
        assertTrue(matcher.find());
    }
    
    public void testLEEquality()
    {
        LogicalExpression leftArg = leFactory.createMemberShipMolecule(leFactory.createVariable("x"), wsmoFactory.createIRI("urn://Left"));
        LogicalExpression rightArg = leFactory.createMemberShipMolecule(leFactory.createVariable("x"), wsmoFactory.createIRI("urn://Right"));
        LogicalExpression correctExp = leFactory.createDisjunction(leftArg, rightArg);
        LogicalExpression wrongExp = leFactory.createDisjunction(rightArg, leftArg);
        assertTrue(correctExp.equals(wrongExp));
    }
    
    
}
