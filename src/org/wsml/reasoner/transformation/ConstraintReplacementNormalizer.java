package org.wsml.reasoner.transformation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.omwg.logicalexpression.*;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Axiom;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Value;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.api.InternalReasonerException;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsmo.common.IRI;
import org.wsmo.common.Identifier;
import org.wsmo.common.Namespace;
import org.wsmo.common.exception.InvalidModelException;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;

public class ConstraintReplacementNormalizer implements OntologyNormalizer {
    private static final String PREFIX = "http://www.wsmo.org/reasoner/";

    public static final String ATTR_OFTYPE_IRI = PREFIX + "ATTR_OFTYPE";

    public static final int ATTR_OFTYPE_ARITY = 5;

    public static final String MIN_CARD_IRI = PREFIX + "MIN_CARD";

    public static final int MIN_CARD_ARITY = 3;

    public static final String MAX_CARD_IRI = PREFIX + "MAX_CARD";

    public static final int MAX_CARD_ARITY = 3;

    public static final String NAMED_USER_IRI = PREFIX + "NAMED_USER";

    public static final int NAMED_USER_ARITY = 1;

    public static final String UNNAMED_USER_IRI = PREFIX + "UNNAMED_USER";

    public static final int UNNAMED_USER_ARITY = 1;

    public static final String VIOLATION_IRI = PREFIX + "VIOLATION";

    public static final int VIOLATION_ARITY = 0;

    public IRI attributeOfTypePredicateID;

    public IRI minCardinalityPredicateID;

    public IRI maxCardinalityPredicateID;

    public IRI namedUserAxiomPredicateID;

    public IRI unnamedUserAxiomPredicateID;

    public IRI violationPredicateID;

    private WsmoFactory wsmoFactory;

    private LogicalExpressionFactory leFactory;

    public ConstraintReplacementNormalizer(WSMO4JManager wsmoManager) {
        leFactory = wsmoManager.getLogicalExpressionFactory();
        wsmoFactory = wsmoManager.getWSMOFactory();
        attributeOfTypePredicateID = wsmoFactory.createIRI(ATTR_OFTYPE_IRI);
        minCardinalityPredicateID = wsmoFactory.createIRI(MIN_CARD_IRI);
        maxCardinalityPredicateID = wsmoFactory.createIRI(MAX_CARD_IRI);
        namedUserAxiomPredicateID = wsmoFactory.createIRI(NAMED_USER_IRI);
        unnamedUserAxiomPredicateID = wsmoFactory.createIRI(UNNAMED_USER_IRI);
        violationPredicateID = wsmoFactory.createIRI(VIOLATION_IRI);

    }

    @SuppressWarnings("unchecked")
    public Ontology normalize(Ontology ontology) {
        String ontologyID = (ontology.getIdentifier() != null ? ontology
                .getIdentifier().toString()
                + "-contraints-replaced" : "iri:normalized-ontology-"
                + ontology.hashCode());
        Ontology resultOntology = wsmoFactory.createOntology(wsmoFactory
                .createIRI(ontologyID));

        // process namespace definitions:
        for (Namespace namespace : (Collection<Namespace>) ontology
                .listNamespaces()) {
            resultOntology.addNamespace(namespace);
        }
        resultOntology.setDefaultNamespace(ontology.getDefaultNamespace());
        try {

            // process non-functional properties:
            for (Object nfp : ontology.listNFPValues().entrySet()) {

                if (nfp instanceof Identifier) {
                    Map.Entry entry = (Map.Entry) nfp;
                    resultOntology.addNFPValue((IRI) entry.getKey(),
                            (Identifier) entry.getValue());
                } else if (nfp instanceof Value) {
                    Map.Entry entry = (Map.Entry) nfp;
                    resultOntology.addNFPValue((IRI) entry.getKey(),
                            (Value) entry.getValue());
                }

            }

            // process axioms:
            for (Axiom axiom : (Collection<Axiom>) ontology.listAxioms()) {

                LogicalExpression definition = (LogicalExpression) (axiom
                        .listDefinitions().iterator().next());
                if (definition instanceof Constraint) {
                    Identifier axiomID = axiom.getIdentifier();
                    String axiomIDString = axiomID.toString();
                    if (axiomIDString
                            .startsWith(AnonymousIdUtils.MINCARD_PREFIX)) {
                        resultOntology
                                .addAxiom(replaceMinCardConstraint(axiom));
                    } else if (axiomIDString
                            .startsWith(AnonymousIdUtils.MAXCARD_PREFIX)) {
                        resultOntology
                                .addAxiom(replaceMaxCardConstraint(axiom));
                    } else if (axiomIDString
                            .startsWith(AnonymousIdUtils.ANONYMOUS_PREFIX)) {
                        resultOntology
                                .addAxiom(replaceUnnamedUserConstraint(axiom));
                    } else // axiom named by user
                    {
                        resultOntology
                                .addAxiom(replaceNamedUserConstraint(axiom));
                    }
                } else if (definition instanceof AttributeConstraintMolecule) // oftype
                                                                                // statement
                {
                    resultOntology.addAxiom(replaceAttrOfTypeConstraint(axiom));

                } else // no constraint axiom
                {
                    resultOntology.addAxiom(axiom);
                }

            }
            for (Axiom axiom : createMetaViolationAxioms()) {
                resultOntology.addAxiom(axiom);
            }
        } catch (InvalidModelException e) {
            throw new InternalReasonerException(e);
        }

        return resultOntology;
    }

    private List<Axiom> createMetaViolationAxioms() {

        List<Axiom> resultAxioms = new LinkedList<Axiom>();

        Variable v1 = wsmoFactory.createVariable("v1");
        Variable v2 = wsmoFactory.createVariable("v2");
        Variable v3 = wsmoFactory.createVariable("v3");
        Variable v4 = wsmoFactory.createVariable("v4");
        Variable v5 = wsmoFactory.createVariable("v5");

        Atom head = leFactory.createAtom(violationPredicateID,
                Collections.EMPTY_LIST);

        // VIOLATION :-
        // ATTR_OFTYPE(instance,value,concept,attribute,violated_type)
        List<Term> params = new ArrayList<Term>(5);
        params.add(v1);
        params.add(v2);
        params.add(v3);
        params.add(v4);
        params.add(v5);
        Atom body = leFactory.createAtom(attributeOfTypePredicateID, params);
        addNewViolationAxiom(resultAxioms, head, body);

        // VIOLATION :- MIN_CARD(instance, concept, attribute)
        params = new ArrayList<Term>(3);
        params.add(v1);
        params.add(v2);
        params.add(v3);
        body = leFactory.createAtom(minCardinalityPredicateID, params);
        addNewViolationAxiom(resultAxioms, head, body);

        // VIOLATION :- MAX_CARD(instance, concept, attribute)
        params = new ArrayList<Term>(3);
        params.add(v1);
        params.add(v2);
        params.add(v3);
        body = leFactory.createAtom(maxCardinalityPredicateID, params);
        addNewViolationAxiom(resultAxioms, head, body);

        // VIOLATION :- NAMED_USER(axiom_id)
        params = new ArrayList<Term>(1);
        params.add(v1);
        body = leFactory.createAtom(namedUserAxiomPredicateID, params);
        addNewViolationAxiom(resultAxioms, head, body);

        // VIOLATION :- UNNAMED_USER(axiom_id)
        params = new ArrayList<Term>(1);
        params.add(v1);
        body = leFactory.createAtom(unnamedUserAxiomPredicateID, params);
        addNewViolationAxiom(resultAxioms, head, body);

        return resultAxioms;
    }

    private void addNewViolationAxiom(List<Axiom> resultAxioms, Atom head,
            Atom body) {
        LogicProgrammingRule rule = leFactory.createLogicProgrammingRule(head,
                body);
        Axiom axiom = wsmoFactory.createAxiom(wsmoFactory
                .createIRI(AnonymousIdUtils.getNewAnonymousIri()));
        axiom.addDefinition(rule);
        resultAxioms.add(axiom);
    }

    private Axiom replaceAttrOfTypeConstraint(Axiom axiom) {
        // get relevant elements out of the attribute constraint:
        LogicalExpression expression = (LogicalExpression) axiom
                .listDefinitions().iterator().next();
        if (!(expression instanceof AttributeConstraintMolecule)) {
            System.err.println("The constraint \""
                    + axiom.getIdentifier().toString()
                    + "\" is not of type AttributeConstraintMolecule");
            return axiom;
        }
        AttributeConstraintMolecule attrConstraint = (AttributeConstraintMolecule) expression;
        Identifier conceptID = (Identifier) attrConstraint.getLeftParameter();
        Identifier attributeID = (Identifier) attrConstraint.getAttribute();
        Identifier typeID = (Identifier) attrConstraint.getRightParameter();

        // create corresponding rule:
        Variable xVariable = wsmoFactory.createVariable("x");
        Variable yVariable = wsmoFactory.createVariable("y");
        MembershipMolecule xMOfC = leFactory.createMemberShipMolecule(
                xVariable, conceptID);
        AttributeValueMolecule xPy = leFactory.createAttributeValue(xVariable,
                attributeID, yVariable);
        NegationAsFailure nafyMOfD = leFactory
                .createNegationAsFailure(leFactory.createMemberShipMolecule(
                        yVariable, typeID));
        Conjunction body = leFactory.createConjunction(xMOfC, leFactory
                .createConjunction(xPy, nafyMOfD));
        List<Term> params = new ArrayList<Term>(5);
        params.add(xVariable);
        params.add(yVariable);
        params.add(conceptID);
        params.add(attributeID);
        params.add(typeID);
        Atom head = leFactory.createAtom(attributeOfTypePredicateID, params);
        LogicProgrammingRule rule = leFactory.createLogicProgrammingRule(head,
                body);

        // create an axiom from rule:
        Axiom resultAxiom = wsmoFactory.createAxiom(wsmoFactory
                .createIRI(AnonymousIdUtils.getNewAnonymousIri()));
        resultAxiom.addDefinition(rule);

        return resultAxiom;
    }

    private Axiom replaceUnnamedUserConstraint(Axiom axiom) {
        // create corresponding rule:
        Constraint constraint = (Constraint) (axiom.listDefinitions()
                .iterator().next());
        LogicalExpression body = constraint.getOperand();
        List<Term> params = new ArrayList<Term>(1);
        params.add(axiom.getIdentifier());
        Atom head = leFactory.createAtom(unnamedUserAxiomPredicateID, params);
        LogicProgrammingRule rule = leFactory.createLogicProgrammingRule(head,
                body);

        // create an axiom from rule:
        Axiom resultAxiom = wsmoFactory.createAxiom(wsmoFactory
                .createIRI(AnonymousIdUtils.getNewAnonymousIri()));
        resultAxiom.addDefinition(rule);

        return resultAxiom;
    }

    private Axiom replaceMaxCardConstraint(Axiom axiom) {
        // get relevant elements out of the attribute constraint:
        Constraint constraint = (Constraint) axiom.listDefinitions().iterator()
                .next();
        Conjunction body = (Conjunction) constraint.getOperand();
        Collection<LogicalExpression> conjuncts = extractConjuncts(body);
        Term instanceID = null, attributeID = null, conceptID = null;
        for (LogicalExpression conjunct : conjuncts) {
            if (conjunct instanceof MembershipMolecule) {
                MembershipMolecule mOf = (MembershipMolecule) conjunct;
                instanceID = (Term) mOf.getLeftParameter();
                conceptID = (Term) mOf.getRightParameter();
            } else if (conjunct instanceof CompoundMolecule) {
                CompoundMolecule compound = (CompoundMolecule) conjunct;
                List attrValMols = compound.listAttributeValueMolecules();
                AttributeValueMolecule attrValMol = (AttributeValueMolecule) (attrValMols
                        .get(0));
                attributeID = (Term) attrValMol.getAttribute();
            }
        }

        // create corresponding rule:
        List<Term> params = new ArrayList<Term>(3);
        params.add(instanceID);
        params.add(conceptID);
        params.add(attributeID);
        Atom head = leFactory.createAtom(maxCardinalityPredicateID, params);
        LogicProgrammingRule rule = leFactory.createLogicProgrammingRule(head,
                body);

        // create an axiom from rule:
        Axiom resultAxiom = wsmoFactory.createAxiom(wsmoFactory
                .createIRI(AnonymousIdUtils.getNewAnonymousIri()));
        resultAxiom.addDefinition(rule);

        return resultAxiom;
    }

    private Axiom replaceMinCardConstraint(Axiom axiom) {
        // get relevant elements out of the attribute constraint:
        Constraint constraint = (Constraint) axiom.listDefinitions().iterator()
                .next();
        Conjunction body = (Conjunction) constraint.getOperand();
        Collection<LogicalExpression> conjuncts = extractConjuncts(body);
        Term instanceID = null, conceptID = null, attributeID = null;
        for (LogicalExpression conjunct : conjuncts) {
            if (conjunct instanceof MembershipMolecule) {
                MembershipMolecule mOf = (MembershipMolecule) conjunct;
                instanceID = (Term) mOf.getLeftParameter();
                conceptID = (Term) mOf.getRightParameter();
            } else if (conjunct instanceof NegationAsFailure) {
                Atom pNew = (Atom) (((NegationAsFailure) conjunct).getOperand());
                attributeID = (Term) pNew.listParameters().get(1);
            }
        }

        // create corresponding rule:
        List<Term> params = new ArrayList<Term>(3);
        params.add(instanceID);
        params.add(conceptID);
        params.add(attributeID);
        Atom head = leFactory.createAtom(minCardinalityPredicateID, params);
        LogicProgrammingRule rule = leFactory.createLogicProgrammingRule(head,
                body);

        // create an axiom from rule:
        Axiom resultAxiom = wsmoFactory.createAxiom(wsmoFactory
                .createIRI(AnonymousIdUtils.getNewAnonymousIri()));
        resultAxiom.addDefinition(rule);

        return resultAxiom;
    }

    private Collection<LogicalExpression> extractConjuncts(
            Conjunction conjunction) {
        Collection<LogicalExpression> conjuncts = new ArrayList<LogicalExpression>();
        LogicalExpression leftConjunct = conjunction.getLeftOperand();
        LogicalExpression rightConjunct = conjunction.getRightOperand();
        if (leftConjunct instanceof Conjunction) {
            conjuncts.addAll(extractConjuncts((Conjunction) leftConjunct));
        } else {
            conjuncts.add(leftConjunct);
        }
        if (rightConjunct instanceof Conjunction) {
            conjuncts.addAll(extractConjuncts((Conjunction) rightConjunct));
        } else {
            conjuncts.add(rightConjunct);
        }
        return conjuncts;
    }

    private Axiom replaceNamedUserConstraint(Axiom axiom) {
        // create corresponding rule:
        Constraint constraint = (Constraint) (axiom.listDefinitions()
                .iterator().next());
        LogicalExpression body = constraint.getOperand();
        List<Term> params = new ArrayList<Term>(1);
        params.add(axiom.getIdentifier());
        Atom head = leFactory.createAtom(namedUserAxiomPredicateID, params);
        LogicProgrammingRule rule = leFactory.createLogicProgrammingRule(head,
                body);

        // create an axiom from rule:
        Axiom resultAxiom = wsmoFactory.createAxiom(wsmoFactory
                .createIRI(AnonymousIdUtils.getNewAnonymousIri()));
        resultAxiom.addDefinition(rule);

        return resultAxiom;
    }

}