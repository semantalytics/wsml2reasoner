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
        hasParentAttr.addType(manConcept);
        Attribute livesAtAttr = manConcept.createAttribute(wsmoFactory.createIRI("urn://livesAt"));
        livesAtAttr.addType(locationConcept);
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
