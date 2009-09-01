package org.wsml.reasoner.transformation.dl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.ontology.Attribute;
import org.omwg.ontology.Axiom;
import org.omwg.ontology.Concept;
import org.omwg.ontology.Instance;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Type;
import org.wsml.reasoner.transformation.AnonymousIdTranslator;
import org.wsml.reasoner.transformation.OntologyNormalizer;
import org.wsml.reasoner.transformation.le.LogicalExpressionNormalizer;
import org.wsml.reasoner.transformation.le.NormalizationRule;
import org.wsml.reasoner.transformation.le.OnePassReplacementNormalizer;
import org.wsml.reasoner.transformation.le.implicationreduction.ImplicationReductionRules;
import org.wsml.reasoner.transformation.le.inverseimplicationreduction.InverseImplicationReductionRules;
import org.wsml.reasoner.transformation.le.moleculedecomposition.MoleculeDecompositionRules;
import org.wsmo.common.Entity;
import org.wsmo.common.IRI;
import org.wsmo.common.Identifier;
import org.wsmo.common.UnnumberedAnonymousID;
import org.wsmo.common.exception.InvalidModelException;
import org.wsmo.factory.FactoryContainer;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;

/**
 * A normalizer for WSML-DL. This normalizer eliminates anonymous id at WSMO
 * elements and logical expressions and does some more normalizing steps on
 * logical expressions: - equivalences and right-implications are replaced by
 * left-implications - complex molecules are replaced by conjunctions of simple
 * ones
 * 
 * <pre>
 *    Created on July 3rd, 2006
 *    Committed by $Author: graham $
 *    $Source: /home/richi/temp/w2r/wsml2reasoner/src/org/wsml/reasoner/transformation/dl/WSMLDLLogExprNormalizer.java,v $,
 * </pre>
 * 
 * @author Nathalie Steinmetz, DERI Innsbruck
 * @version $Revision: 1.4 $ $Date: 2007-04-26 17:39:14 $
 */
public class WSMLDLLogExprNormalizer implements OntologyNormalizer {

    protected LogicalExpressionNormalizer leNormalizer;
    protected WsmoFactory wsmoFactory;
    protected LogicalExpressionFactory leFactory;
    protected AnonymousIdTranslator anonymousIdTranslator;

    public WSMLDLLogExprNormalizer(FactoryContainer wsmoManager) {
        List<NormalizationRule> preOrderRules = new ArrayList<NormalizationRule>();
        preOrderRules.addAll(new ImplicationReductionRules(wsmoManager).getRules());
        preOrderRules.addAll(new InverseImplicationReductionRules(wsmoManager).getRules());
        
        List<NormalizationRule> postOrderRules = new ArrayList<NormalizationRule>();
        postOrderRules.addAll(new MoleculeDecompositionRules(wsmoManager).getRules());

        leNormalizer = new OnePassReplacementNormalizer(preOrderRules, postOrderRules, wsmoManager);
        wsmoFactory = wsmoManager.getWsmoFactory();
        leFactory = wsmoManager.getLogicalExpressionFactory();
        anonymousIdTranslator = new AnonymousIdTranslator(wsmoFactory);
    }

    /**
     * @see OntologyNormalizer#normalize(Ontology)
     */

    public Set<Axiom> normalizeAxioms(Collection<Axiom> theAxioms) {
        Set<Axiom> result = new HashSet<Axiom>();
        Axiom axiom = wsmoFactory.createAxiom((IRI) anonymousIdTranslator.translate(wsmoFactory.createAnonymousID()));
        for (LogicalExpression expression : normalizeLogExpr(theAxioms)) {
            axiom.addDefinition(expression);
        }
        result.add(axiom);
        return result;
    }

    /**
     * Performs the transformation that is described above.
     * 
     * @param o -
     *            the ontology for which we need to resolve the conceptual
     *            syntax part.
     * 
     * @return an ontology represent o semantically but only consists of axioms
     */

    public Set<Entity> normalizeEntities(Collection<Entity> theEntities) {
        Set<Entity> result = new HashSet<Entity>();

        Set<Axiom> axiomsToProcess = new HashSet<Axiom>();
        for (Entity e : theEntities) {
            if (e instanceof Concept) {
                try {
                    result.addAll(normalizeConcept((Concept) e));
                }
                catch (InvalidModelException e1) {
                    e1.printStackTrace();
                }
            }
            else if (e instanceof Instance) {
                try {
                    result.addAll(normalizeInstance((Instance) e));
                }
                catch (InvalidModelException e1) {
                    e1.printStackTrace();
                }
            }
            else if (e instanceof Axiom) {
                axiomsToProcess.add((Axiom) e);
            }
        }

        return new HashSet<Entity>(normalizeAxioms(axiomsToProcess));
    }

    /*
     * If a concept has an unnumbered anonymous identifier, it is replaced by an
     * IRI containing a randomly created number. A new concept with this new
     * identifier is created and added to the ontology, while the old one is
     * removed from it. The new concept receives eventual subconcepts,
     * superconcepts, instances, nonfunctionalproperties or attributes from the
     * old concept. If a concept has a superconcept with an unnumbered anonymous
     * identifier, this superconcept's identifier is also replaced.
     */

    private Set<Entity> normalizeConcept(Concept concept) throws InvalidModelException {
        Set<Entity> result = new HashSet<Entity>();
        if (concept.getIdentifier() instanceof UnnumberedAnonymousID) {
            Identifier id = (Identifier) anonymousIdTranslator.translate(concept.getIdentifier());
            Concept newConcept = wsmoFactory.createConcept(id);
            // the superConcept to which the old concept refers to is transfered to the new concept
            if (concept.listSuperConcepts().size() > 0) {
                for (Concept sc : concept.listSuperConcepts()){
                    if (sc.getIdentifier() instanceof UnnumberedAnonymousID) {
                        Identifier id2 = (Identifier) anonymousIdTranslator.translate(sc.getIdentifier());
                        sc = wsmoFactory.createConcept(id2);
                    }
                    newConcept.addSuperConcept(sc);
                }
            }
            // the attribute to which the old concept refers to is transfered to the new concept
            if (concept.listAttributes().size() > 0) {
                for (Attribute attribute : concept.listAttributes()){
                    Attribute newAttribute = newConcept.createAttribute(attribute.getIdentifier());
                    if (attribute.listTypes().size() > 0) {
                        for (Type type : attribute.listTypes()){
                            newAttribute.addType(type);
                        }
                    }
                }
            }
            result.add(newConcept);
        }
        else {
            // the superConcept to which the old concept refers to is transfered to the new concept
            if (concept.listSuperConcepts().size() > 0) {
                for (Concept sc : concept.listSuperConcepts()){
                    if (sc.getIdentifier() instanceof UnnumberedAnonymousID) {
                        concept.removeSuperConcept(sc);
                        Identifier id = (Identifier) anonymousIdTranslator.translate(sc.getIdentifier());
                        concept.addSuperConcept(wsmoFactory.createConcept(id));
                    }
                }
            }
            result.add(concept);
        }
        return result;
    }

    /*
     * If an instance has an unnumbered anonymous identifier, it is replaced by
     * an IRI containing a randomly created number. A new instance with this new
     * identifier is created and added to the ontology, while the old one is
     * removed from it. If an instance is member of an concept with an unnumberd
     * anonymous identifier, this concept's identifier is also replaced.
     */

    private Set<Entity> normalizeInstance(Instance instance) throws InvalidModelException {
        Set<Entity> result = new HashSet<Entity>();
        if (instance.getIdentifier() instanceof UnnumberedAnonymousID) {
            Identifier id = (Identifier) anonymousIdTranslator.translate(instance.getIdentifier());
            result.add(wsmoFactory.createInstance(id));
        }
        else {
            // replace concept's unnumbered anonymous identifiers
            if (instance.listConcepts().size() > 0) {
                for (Concept concept : instance.listConcepts()){
                    if (concept.getIdentifier() instanceof UnnumberedAnonymousID) {
                        Identifier id = (Identifier) anonymousIdTranslator.translate(concept.getIdentifier());
                        instance.removeConcept(concept);
                        instance.addConcept(wsmoFactory.createConcept(id));
                    }
                }
            }
            result.add(instance);
        }
        return result;
    }

    /*
     * Method to normalize logical expressions. Anonymous identifiers are
     * replaced, and the preorder and postorder rules defined at the constructor
     * are applied.
     */

    private Set<LogicalExpression> normalizeLogExpr(Collection<Axiom> axioms) {
        Set<LogicalExpression> resultExp = new HashSet<LogicalExpression>();
        for (Axiom axiom : axioms) {
            for (LogicalExpression expression : axiom.listDefinitions()) {
                anonymousIdTranslator.setScope(expression);
                resultExp.add(leNormalizer.normalize(expression));
            }
        }
        return resultExp;
    }
}