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
package normalization;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.omwg.logicalexpression.Binary;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Attribute;
import org.omwg.ontology.Axiom;
import org.omwg.ontology.Concept;
import org.omwg.ontology.Instance;
import org.omwg.ontology.Ontology;
import org.wsml.reasoner.transformation.AxiomatizationNormalizer;
import org.wsml.reasoner.transformation.ConstructReductionNormalizer;
import org.wsml.reasoner.transformation.OntologyNormalizer;

public class AnonymousIDReplacementTest extends WSMLNormalizationTest
{
    protected OntologyNormalizer reductionNormalizer, axiomatizationNormalizer;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        axiomatizationNormalizer = new AxiomatizationNormalizer();
        reductionNormalizer = new ConstructReductionNormalizer();
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
        Concept manConcept = wsmoFactory.createConcept(wsmoFactory.createIRI("Man"));
        Concept locationConcept = wsmoFactory.createConcept(wsmoFactory.createIRI("Location"));
        Attribute hasParentAttr = manConcept.createAttribute(wsmoFactory.createIRI("hasParent"));
        hasParentAttr.addType(manConcept);
        Attribute livesAtAttr = manConcept.createAttribute(wsmoFactory.createIRI("livesAt"));
        livesAtAttr.addType(locationConcept);
        Instance aragorn = wsmoFactory.createInstance(wsmoFactory.createIRI("Aragorn"), manConcept);
        Instance arathorn = wsmoFactory.createInstance(wsmoFactory.createIRI("Arathorn"), manConcept);
        Instance elendil = wsmoFactory.createInstance(wsmoFactory.createIRI("Elendil"), manConcept);
        aragorn.addAttributeValue(hasParentAttr, arathorn);
        aragorn.addAttributeValue(hasParentAttr, wsmoFactory.createInstance(wsmoFactory.createAnonymousID()));
        Axiom aragornLivesAx = wsmoFactory.createAxiom(wsmoFactory.createIRI("aragornLivesSomewhere"));
        aragornLivesAx.addDefinition(leFactory.createConjunction(leFactory.createMemberShipMolecule(aragorn.getIdentifier(), leFactory.createAnonymousID((byte)1)) , leFactory.createMemberShipMolecule(leFactory.createAnonymousID((byte)1),locationConcept.getIdentifier())));
        Axiom arathornLivesAx = wsmoFactory.createAxiom(wsmoFactory.createIRI("arathornLivesSomewhere"));
        arathornLivesAx.addDefinition(leFactory.createConjunction(leFactory.createConjunction(leFactory.createConjunction(leFactory.createConjunction(leFactory.createAttributeValue(arathorn.getIdentifier(), livesAtAttr.getIdentifier(), leFactory.createAnonymousID((byte)1)), leFactory.createMemberShipMolecule(leFactory.createAnonymousID((byte)1), locationConcept.getIdentifier())), leFactory.createAttributeValue(elendil.getIdentifier(),livesAtAttr.getIdentifier(), leFactory.createAnonymousID((byte)2))), leFactory.createMemberShipMolecule(leFactory.createAnonymousID((byte)2), locationConcept.getIdentifier())), leFactory.createAttributeValue(elendil.getIdentifier(), livesAtAttr.getIdentifier(), wsmoFactory.createAnonymousID())));
        ontology.addConcept(manConcept);
        ontology.addConcept(locationConcept);
        ontology.addInstance(aragorn);
        ontology.addInstance(arathorn);
        ontology.addAxiom(aragornLivesAx);
        ontology.addAxiom(arathornLivesAx);
System.out.println(serializeOntology(ontology)+"\n\n\n-------------\n\n\n");
        
        // normalize ontology with the LEConstructReductionNormalizer:
        Ontology normOnt = axiomatizationNormalizer.normalize(ontology);
        normOnt = reductionNormalizer.normalize(normOnt);

        // test whether produced expression is correct
        // by means of regular expressions matched against serialized result
        // ontology:
        String normString = serializeOntology(normOnt);
System.out.println(normString);
        Pattern pattern = Pattern.compile("Arathorn.*livesAt.*(anonymous.*).*and.*\\1.*memberOf.*Location", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(normString);
        assertTrue(matcher.find());
    }
    
    public void testLEEquality()
    {
        LogicalExpression leftArg = leFactory.createMemberShipMolecule(wsmoFactory.createVariable("x"), wsmoFactory.createIRI("Left"));
        LogicalExpression rightArg = leFactory.createMemberShipMolecule(wsmoFactory.createVariable("x"), wsmoFactory.createIRI("Right"));
        LogicalExpression correctExp = leFactory.createDisjunction(leftArg, rightArg);
        LogicalExpression wrongExp = leFactory.createDisjunction(rightArg, leftArg);
        assertTrue(correctExp.equals(wrongExp));
    }
}
