package org.wsml.reasoner.transformation.dl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.ontology.Attribute;
import org.omwg.ontology.Axiom;
import org.omwg.ontology.Concept;
import org.omwg.ontology.Instance;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Type;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsml.reasoner.transformation.AnonymousIdTranslator;
import org.wsml.reasoner.transformation.OntologyNormalizer;
import org.wsml.reasoner.transformation.le.ImplicationReductionRules;
import org.wsml.reasoner.transformation.le.InverseImplicationReductionRules;
import org.wsml.reasoner.transformation.le.LogicalExpressionNormalizer;
import org.wsml.reasoner.transformation.le.MoleculeDecompositionRules;
import org.wsml.reasoner.transformation.le.NormalizationRule;
import org.wsml.reasoner.transformation.le.OnePassReplacementNormalizer;
import org.wsmo.common.Entity;
import org.wsmo.common.IRI;
import org.wsmo.common.Identifier;
import org.wsmo.common.UnnumberedAnonymousID;
import org.wsmo.common.exception.InvalidModelException;
import org.wsmo.common.exception.SynchronisationException;
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

    public WSMLDLLogExprNormalizer(WSMO4JManager wsmoManager) {
        // Normalization Rules
        List<NormalizationRule> preOrderRules = new ArrayList<NormalizationRule>();
        List<NormalizationRule> postOrderRules = new ArrayList<NormalizationRule>();
        // the ImplicationReductionRules replace equivalences and
        // right-implications
        // in logical expressions by left-implications
        preOrderRules.addAll(new ImplicationReductionRules(wsmoManager).getRules());
        // the InverseImplicationReductionRules replace conjunctions on the left
        // side
        // and disjunctions on the right side of an inverse implication
        preOrderRules.addAll(new InverseImplicationReductionRules(wsmoManager).getRules());
        // the MoleculeDecompositionRules replace complex molecules inside a
        // logical
        // expression by conjunctions of simple ones
        postOrderRules.addAll(new MoleculeDecompositionRules(wsmoManager).getRules());

        leNormalizer = new OnePassReplacementNormalizer(preOrderRules, postOrderRules, wsmoManager);
        wsmoFactory = wsmoManager.getWSMOFactory();
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
                catch (SynchronisationException e1) {
                    e1.printStackTrace();
                }
                catch (InvalidModelException e1) {
                    e1.printStackTrace();
                }
            }
            else if (e instanceof Instance) {
                try {
                    result.addAll(normalizeInstance((Instance) e));
                }
                catch (SynchronisationException e1) {
                    e1.printStackTrace();
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

    private Set<Entity> normalizeConcept(Concept concept) throws SynchronisationException, InvalidModelException {
        Set<Entity> result = new HashSet<Entity>();
        if (concept.getIdentifier() instanceof UnnumberedAnonymousID) {
            Identifier id = (Identifier) anonymousIdTranslator.translate(concept.getIdentifier());
            Concept newConcept = wsmoFactory.createConcept(id);
            // the superConcept to which the old concept refers to is transfered
            // to the new
            // concept
            if (concept.listSuperConcepts().size() > 0) {
                Iterator<Concept> it = concept.listSuperConcepts().iterator();
                while (it.hasNext()) {
                    Concept superConcept = it.next();
                    if (superConcept.getIdentifier() instanceof UnnumberedAnonymousID) {
                        Identifier id2 = (Identifier) anonymousIdTranslator.translate(superConcept.getIdentifier());
                        superConcept = wsmoFactory.createConcept(id2);
                    }
                    newConcept.addSuperConcept(superConcept);
                }
            }
            // the attribute to which the old concept refers to is transfered to
            // the new concept
            if (concept.listAttributes().size() > 0) {
                Iterator<Attribute> it = concept.listAttributes().iterator();
                while (it.hasNext()) {
                    Attribute attribute = it.next();
                    Attribute newAttribute = null;
                    newAttribute = newConcept.createAttribute(attribute.getIdentifier());
                    if (attribute.listTypes().size() > 0) {
                        Set<Type> types = attribute.listTypes();
                        Iterator<Type> it4 = types.iterator();
                        while (it4.hasNext()) {
                            Type type = it4.next();
                            newAttribute.addType(type);
                        }
                    }
                }
            }
            result.add(newConcept);
        }
        else {
            // the superConcept to which the old concept refers to is transfered
            // to the new
            // concept
            if (concept.listSuperConcepts().size() > 0) {
                Iterator<Concept> it = concept.listSuperConcepts().iterator();
                while (it.hasNext()) {
                    Concept superConcept = it.next();
                    if (superConcept.getIdentifier() instanceof UnnumberedAnonymousID) {
                        concept.removeSuperConcept(superConcept);
                        Identifier id = (Identifier) anonymousIdTranslator.translate(superConcept.getIdentifier());
                        superConcept = wsmoFactory.createConcept(id);
                        concept.addSuperConcept(superConcept);
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

    private Set<Entity> normalizeInstance(Instance instance) throws SynchronisationException, InvalidModelException {
        Set<Entity> result = new HashSet<Entity>();
        Instance newInstance = null;
        if (instance.getIdentifier() instanceof UnnumberedAnonymousID) {
            Identifier id = (Identifier) anonymousIdTranslator.translate(instance.getIdentifier());
            newInstance = wsmoFactory.createInstance(id);
            result.add(newInstance);
        }
        else {
            // replace concept's unnumbered anonymous identifiers
            if (instance.listConcepts().size() > 0) {
                Iterator<Concept> it2 = instance.listConcepts().iterator();
                while (it2.hasNext()) {
                    Concept concept = it2.next();
                    if (concept.getIdentifier() instanceof UnnumberedAnonymousID) {
                        Identifier id = (Identifier) anonymousIdTranslator.translate(concept.getIdentifier());
                        Concept newConcept = wsmoFactory.createConcept(id);
                        instance.removeConcept(concept);
                        instance.addConcept(newConcept);
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
        Set<LogicalExpression> expressions = new HashSet<LogicalExpression>();
        for (Axiom axiom : axioms) {
            expressions.addAll(axiom.listDefinitions());
        }

        // iteratively normalize logical expressions:
        Set<LogicalExpression> resultExp = new HashSet<LogicalExpression>();
        for (LogicalExpression expression : expressions) {
            anonymousIdTranslator.setScope(expression);
            resultExp.add(leNormalizer.normalize(expression));
        }
        return resultExp;
    }
}