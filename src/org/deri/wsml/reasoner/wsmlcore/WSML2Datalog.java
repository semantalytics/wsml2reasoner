/**
 * WSML Reasoner Implementation.
 *
 * Copyright (c) 2005, University of Innsbruck, Austria.
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

package org.deri.wsml.reasoner.wsmlcore;

import org.deri.wsml.reasoner.normalization.InfixOrderLogicalExpressionVisitor;
import org.deri.wsml.reasoner.wsmlcore.datalog.*;
import org.omwg.logexpression.Atom;
import org.omwg.logexpression.AttrSpecification;
import org.omwg.logexpression.Binary;
import org.omwg.logexpression.Molecule;
import org.omwg.logexpression.Quantified;
import org.omwg.logexpression.Unary;
import org.omwg.logexpression.Visitor;
import org.omwg.logexpression.terms.IRI;
import org.omwg.logexpression.terms.Term;
import org.omwg.logexpression.terms.Variable;
import org.omwg.ontology.*;

import java.util.*;
import java.util.logging.*;

/**
 * Converts a set of rules in WSML to datalog.
 * 
 * More precisely, the class does the following:
 *  - Takes a set of rules in WSML - Each rule is a datalog rule, where all
 * simple WSML literals are allowed (similar to F-Logic datalog subset), i.e. R :=
 * hl impliedBy (bl_1 and bl_2 and ... and bl_k) , k >= 1 or R := (bl_1 and bl_2
 * and ... and bl_k) implies hl , k >= 1 or R := hl where the head literal hl is
 * a positive atomic formulae that does not contain function symbols of arity >
 * 0 and the body literals bl_k (k >= 1)are positive atomic formulae in WSML
 * Core. Complex molecules are allowed to occur in the head as well as in the
 * body of the WSML rule.
 *  - Negation is not allowed (definite datalog programs only) - Constraints are
 * not allowed (since we deal with WSML Core)
 * 
 * We treat object-object oriented features in a general way, by means of some
 * generic schema level datalog rules. This reduces the amount of rules that are
 * generated for a given KB and make the KB more readable. However it disallows
 * the use of some optimization techniques that are applicable and might be
 * useful when only the standard translation is used.
 * 
 * Implementation is not thread-safe and thus may not be shared across different
 * threads.
 * 
 * @author Uwe Keller, DERI Innsbruck
 */
public class WSML2Datalog {

    // Some predicates that are used to represent the oo-based molecules
    // of WSML in datalog.
    private final Predicate PRED_SUB_CONCEPT_OF;

    private final Predicate PRED_SUB_RELATION_OF;

    private final Predicate PRED_OF_TYPE;

    private final Predicate PRED_IMPLIES_TYPE;

    private final Predicate PRED_MEMBER_OF;

    private final Predicate PRED_HAS_VALUE;

    private Logger logger = Logger
            .getLogger("org.deri.wsml.reasoner.wsmlcore.WSML2Datalog");

    /**
     * Generates a WSML2Datalog converter.
     */
    public WSML2Datalog() {
        super();

        PRED_SUB_CONCEPT_OF = new Predicate("wsml-subconcept-of", 2);
        PRED_SUB_RELATION_OF = new Predicate("wsml-subrelation-of", 2);
        PRED_OF_TYPE = new Predicate("wsml-of-type", 3);
        PRED_IMPLIES_TYPE = new Predicate("wsml-implies-type", 3);
        PRED_MEMBER_OF = new Predicate("wsml-member-of", 2);
        PRED_HAS_VALUE = new Predicate("wsml-has-value", 3);
    }

    /**
     * Transforms the given set of logical expressions to datalog. It is
     * required that the logical expressions are rules of a specific syntactic
     * kind (simple rules) which ensures that they can be converted to datalog.
     * 
     * The conversion in particular transformes the object-oriented modelling
     * elements in rules, like slots or instance-of molecules.
     * 
     * If this is not the case an exception will be thrown.
     * 
     * @param rules -
     *            the set of (simple) WSML rules
     * @throws IllegalArgumentException -
     *             if the given set of logical expressions does not satisfy the
     *             syntactic requirements described above.
     * @return a datalog program that represents the given set of WSML rules.
     */
    public Program transform(
            Set<? extends org.omwg.logexpression.LogicalExpression> rules)
            throws IllegalArgumentException {
        Program result = new Program();
        Set<Predicate> usedAuxilliaryPredicates = new HashSet<Predicate>();

        MoleculeReplacementVisitor mrv = new MoleculeReplacementVisitor();

        for (org.omwg.logexpression.LogicalExpression r : rules) {
            r.accept(mrv);
            Program translation = (Program) mrv.getSerializedObject();
            if (translation != null) {
                result.addAll(translation);
                usedAuxilliaryPredicates.addAll(mrv
                        .getUsedAuxilliaryPredicates());
            } else {
                throw new IllegalArgumentException(
                        "WSML rule can not be translated to datalog: "
                                + r.asString());
            }

            // Reset the internal state of the visitor such that it can be
            // reused.
            mrv.reset();
        }

        // Now generate the rules that define the semantics of the used
        // auxilliary predicates.
        Program auxilliaryRules = generateAuxilliaryPredicateDefinitions(usedAuxilliaryPredicates);
        result.addAll(auxilliaryRules); // Add to the end of all rules. Perhaps
                                        // this is problematic for some
        // Some top-down evaluation strategies in the precence of recursive
        // rules!

        return result;
    }

    /**
     * Transforms a single (simple) WSML (datalog) rule to datalog. In general,
     * the result consists of several datalog rules i.e. a datalog program
     * 
     * @param rule -
     *            the rule to be translated
     * @return a datalog program that represents the given simple WSML rule.
     * @throws IllegalArgumentException -
     *             if the given rule can not be translated to datalog.
     */
    public Program transform(org.omwg.logexpression.LogicalExpression rule)
            throws IllegalArgumentException {
        Set<org.omwg.logexpression.LogicalExpression> rules = new HashSet<org.omwg.logexpression.LogicalExpression>();
        rules.add(rule);
        return transform(rules);
    }

    private Program generateAuxilliaryPredicateDefinitions(
            Set<Predicate> usedAuxilliaryPredicates) {
        Program result = new Program();

        org.deri.wsml.reasoner.wsmlcore.datalog.Variable v1 = new org.deri.wsml.reasoner.wsmlcore.datalog.Variable(
                "?v1");
        org.deri.wsml.reasoner.wsmlcore.datalog.Variable v2 = new org.deri.wsml.reasoner.wsmlcore.datalog.Variable(
                "?v2");
        org.deri.wsml.reasoner.wsmlcore.datalog.Variable v3 = new org.deri.wsml.reasoner.wsmlcore.datalog.Variable(
                "?v3");
        org.deri.wsml.reasoner.wsmlcore.datalog.Variable v4 = new org.deri.wsml.reasoner.wsmlcore.datalog.Variable(
                "?v4");
        org.deri.wsml.reasoner.wsmlcore.datalog.Variable v5 = new org.deri.wsml.reasoner.wsmlcore.datalog.Variable(
                "?v5");

        List<Literal> body;
        Literal head;

        for (Predicate p : usedAuxilliaryPredicates) {
            body = new LinkedList<Literal>();
            head = null;

            if (p == PRED_SUB_CONCEPT_OF) {
                // transitivity: sco(?c1,?c3) <- sco(?c1,?c2) and sco(?c2,?c3)
                // extension-subset: mo(?o,?c2) <- mo(?o,?c1) and sco(?c1,?c2)

                head = new Literal(PRED_SUB_CONCEPT_OF,
                        new org.deri.wsml.reasoner.wsmlcore.datalog.Term[] {
                                v1, v3 });
                body.add(new Literal(PRED_SUB_CONCEPT_OF,
                        new org.deri.wsml.reasoner.wsmlcore.datalog.Term[] {
                                v1, v2 }));
                body.add(new Literal(PRED_SUB_CONCEPT_OF,
                        new org.deri.wsml.reasoner.wsmlcore.datalog.Term[] {
                                v2, v3 }));
                result.add(new Rule(head, body));

                body = new LinkedList<Literal>();
                head = new Literal(PRED_MEMBER_OF,
                        new org.deri.wsml.reasoner.wsmlcore.datalog.Term[] {
                                v1, v3 });
                body.add(new Literal(PRED_MEMBER_OF,
                        new org.deri.wsml.reasoner.wsmlcore.datalog.Term[] {
                                v1, v2 }));
                body.add(new Literal(PRED_SUB_CONCEPT_OF,
                        new org.deri.wsml.reasoner.wsmlcore.datalog.Term[] {
                                v2, v3 }));
                result.add(new Rule(head, body));
                continue;
            }

            if (p == PRED_IMPLIES_TYPE) {
                // Inference of attr value types: mo(v,c2) <- itype(c1, att,
                // c2), mo(o,c1), hval(o,att, v)
                // mo(v1,v2) <- itype(v3, v4, v2), mo(v5,v3), hval(v5,v4, v1)
                head = new Literal(PRED_MEMBER_OF,
                        new org.deri.wsml.reasoner.wsmlcore.datalog.Term[] {
                                v1, v2 });
                body.add(new Literal(PRED_IMPLIES_TYPE,
                        new org.deri.wsml.reasoner.wsmlcore.datalog.Term[] {
                                v3, v4, v2 }));
                body.add(new Literal(PRED_MEMBER_OF,
                        new org.deri.wsml.reasoner.wsmlcore.datalog.Term[] {
                                v5, v3 }));
                body.add(new Literal(PRED_HAS_VALUE,
                        new org.deri.wsml.reasoner.wsmlcore.datalog.Term[] {
                                v5, v4, v1 }));
                result.add(new Rule(head, body));
                continue;
            }

            if (p == PRED_OF_TYPE) {
                // Do nothing, since OF_TYPE statements are not allowed to be
                // used to infer
                // some type information of attribute values.
                // By not generating a rule as for PRED_IMPLIES_TYPE, we avoid
                // that the prover can
                // infer something about the type of a attribute value.
                continue;
            }

            if (p == PRED_HAS_VALUE) {
                // Inference of attr value types already covered by
                // PRED_IMPLIES_TYPE and only needed in case that a
                // IMPLIES_TYPE statement occurs somewhere in the KB.
                continue;
            }

            if (p == PRED_MEMBER_OF) {
                // Extension-subset already covered by PRED_SUB_CONCEPT_OF and
                // only needed in case that a
                // subconcept of statement occurs somewhere in the KB.
                continue;
            }

            // SUB_RELATION_OF Missing.
        }
        return result;
    }

    // -- Inner class for visiting rules (logical expressions) ...

    /**
     * Given a WSML rule the visitor generates a new datalog rule that
     * represents the original WSML rule but where all non-predicate molecules
     * are replaced by respective atomic formulaes that use specific predicates
     * 
     * Implements a left-first, depth-first traversal.
     */
    private class MoleculeReplacementVisitor extends
            InfixOrderLogicalExpressionVisitor {

        private List<Literal> datalogBody;

        private Literal datalogHead;

        private boolean inHeadOfRule;

        private boolean inBodyOfRule;

        private int implicationCount;

        private Set<Predicate> usedAuxilliaryPredicates;

        private Program additionalRules;

        private List<Literal> conjunctiveHeadLiterals;

        public MoleculeReplacementVisitor() {
            super();
            reset();
        }

        /**
         * Resets the state of the visitor to the intial state such that the
         * visitor can be reused again.
         * 
         */
        public void reset() {
            datalogBody = new LinkedList<Literal>();
            usedAuxilliaryPredicates = new HashSet<Predicate>();
            datalogHead = null;
            inHeadOfRule = false;
            inBodyOfRule = false;
            implicationCount = 0;

            /**
             * A set of additional rules that can be created when handling
             * complex molecules
             */
            additionalRules = new Program();

            conjunctiveHeadLiterals = new LinkedList<Literal>();
        }

        /**
         * Constructs a datalog representation of the given (visited) WSML rule.
         * The representation is a datalog program.
         * 
         * The construction introduces new predicates for which a defining
         * datalog rules will not be generated here (in order to avoid multiple
         * generation of the same rules). Instead they will be generated only
         * once in WSML2Datalog.tranform().
         * 
         * @return the Datalog program that represents the WSML rule or null if
         *         the visited rule does not conform to the syntax requirements
         *         stated in class WSML2Datalog and thus can not be translated
         *         to datalog.
         * 
         * @see org.deri.wsml.reasoner.normalization.InfixOrderLogicalExpressionVisitor#getSerializedObject()
         */
        @Override
        public Object getSerializedObject() {
            Program datalogRepresentation = null;
            if (datalogBody != null) {
                datalogRepresentation = new Program();
                if (datalogHead != null) {
                    conjunctiveHeadLiterals.add(0, datalogHead);
                    // Note that multiple head literals stem from resolving
                    // short
                    // cuts of a headliteral in a WSML rule which consists of a
                    // complex
                    // molecule!
                }
                try {
                    // Create a rule for each literal in the conjunctive head
                    // literal list
                    // the body for each of the rules is the same, namely the
                    // list of literals
                    // in "datalogBody"
                    for (Literal hl : conjunctiveHeadLiterals) {
                        datalogRepresentation.add(new Rule(hl, datalogBody));
                    }
                } catch (DatalogException d) {
                    datalogRepresentation = null;
                    logger
                            .severe("Given WSML rule can not be converted to Datalog!"
                                    + "\nException: " + d);
                }
            }
            return datalogRepresentation;
        }

        /**
         * Gets the auxilliary predicates that have been used when translating
         * the WSML rule to datalog.
         * 
         * @return Returns the usedAuxilliaryPredicates.
         */
        public Set<Predicate> getUsedAuxilliaryPredicates() {
            return usedAuxilliaryPredicates;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.deri.wsml.reasoner.normalization.DepthFirstLogicalExpressionVisitor#handleAtom(org.omwg.logexpression.Atom)
         */
        @Override
        public void handleAtom(Atom arg0) {
            Predicate p = new Predicate(arg0.getIdentifier().asString(), arg0
                    .getArity());
            org.deri.wsml.reasoner.wsmlcore.datalog.Term[] predArgs = new org.deri.wsml.reasoner.wsmlcore.datalog.Term[arg0
                    .getArity()];

            for (int i = 0; i < arg0.getArity(); i++) {
                Term atomArg = arg0.getParameter(i);
                predArgs[i] = convertWSMLTerm2Datalog(atomArg);
            } // for

            Literal l = new Literal(p, Literal.NegationType.NONNEGATED,
                    predArgs); // currently we ignore negation since we deal
                                // with WSMLCore

            if (inBodyOfRule) {
                datalogBody.add(l);
            } else if (inHeadOfRule) {
                if (datalogHead == null) {
                    datalogHead = l;
                } else {
                    throw new DatalogException(
                            "Multiple atoms in the head of a rule are not allowed in simple WSML rules!");
                }
            } else {
                // We do not have an implication but only a simple fact.
                datalogHead = l;
            }

        }

        /*
         * (non-Javadoc)
         * 
         * @see org.deri.wsml.reasoner.normalization.InfixOrderLogicalExpressionVisitor#handleConstraint(org.omwg.logexpression.Unary)
         */
        @Override
        public void enterConstraint(Unary arg0) {
            throw new DatalogException(
                    "Constraints are not allowed in WSMLCore.");
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.deri.wsml.reasoner.normalization.InfixOrderLogicalExpressionVisitor#handleEquivalent(org.omwg.logexpression.Binary)
         */
        @Override
        public void enterEquivalent(Binary arg0) {
            throw new DatalogException(
                    "Equivalent is not allowed in simple WSML datalog rules.");
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.deri.wsml.reasoner.normalization.InfixOrderLogicalExpressionVisitor#handleExists(org.omwg.logexpression.Quantified)
         */
        @Override
        public void enterExists(Quantified arg0) {
            throw new DatalogException(
                    "Quantifier is not allowed in simple WSML datalog rules.");
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.deri.wsml.reasoner.normalization.InfixOrderLogicalExpressionVisitor#handleForall(org.omwg.logexpression.Quantified)
         */
        @Override
        public void enterForall(Quantified arg0) {
            throw new DatalogException(
                    "Quantifier is not allowed in simple WSML datalog rules.");
        }

        /*
         * ATTENTION: The current implementation works fine for simple and
         * complex molecules that occur positively in the rule. For negative
         * literals that consist of a simple molecule, the integration of
         * negation is straightforward and simple, we only need to replace
         * NON_NEGATED by the respective negation type. However, in case of
         * negative literals that consist of a complex molecule the procedure
         * must be adapted since it does not give the correct result then Since
         * we only deal with WSML Core here, we only care about positive
         * literals, thus in this context the implementation is not a problem
         * but can not simply be
         * 
         * @see org.deri.wsml.reasoner.normalization.DepthFirstLogicalExpressionVisitor#handleMolecule(org.omwg.logexpression.Molecule)
         */
        @Override
        public void handleMolecule(Molecule m) {

            Term t = m.getTerm();

            org.deri.wsml.reasoner.wsmlcore.datalog.Term objTerm = convertWSMLTerm2Datalog(t);

            Set superConcepts = m.listSubConceptOf();
            Set memberOfClasses = m.listMemberOf();
            Set attributeSpecs = m.listAttributeSpecifications();

            // ATTENTION: The current implementation works fine for simple and
            // complex molecules that
            // occur positively in the rule.
            // For negative literals that consist of a simple molecule, the
            // integration of negation
            // is straightforward and simple, we only need to replace
            // NON_NEGATED by the respective
            // negation type.
            // However, in case of negative literals that consist of a complex
            // molecule the procedure must be
            // adapted since it does not give the correct result then
            // Since we only deal with WSML Core here, we only care about
            // positive literals, thus
            // in this context the implementation is not a problem but can not
            // simply be

            // Superconcepts part ...

            if (superConcepts != null && superConcepts.size() > 0) {
                for (Object sc : superConcepts) {
                    Term scTerm = (Term) sc;
                    org.deri.wsml.reasoner.wsmlcore.datalog.Term dTerm = convertWSMLTerm2Datalog(scTerm);
                    org.deri.wsml.reasoner.wsmlcore.datalog.Term[] args = new org.deri.wsml.reasoner.wsmlcore.datalog.Term[] {
                            objTerm, dTerm };
                    Literal l = new Literal(
                            WSML2Datalog.this.PRED_SUB_CONCEPT_OF,
                            Literal.NegationType.NONNEGATED, args);
                    if (inBodyOfRule) {
                        datalogBody.add(l);
                    } else if (inHeadOfRule) {
                        conjunctiveHeadLiterals.add(l); // store new conjunctive
                                                        // head literal list;
                        // later we will generate a separate rule for each of
                        // them.
                    } else {
                        // We do not have an implication but only a simple fact.
                        conjunctiveHeadLiterals.add(l); // store new conjunctive
                                                        // head literal list;
                        // later we will generate a separate rule for each of
                        // them.
                    }
                    usedAuxilliaryPredicates
                            .add(WSML2Datalog.this.PRED_SUB_CONCEPT_OF);
                }
            }

            // MemberOf part ...

            if (memberOfClasses != null && memberOfClasses.size() > 0) {
                for (Object moc : memberOfClasses) {
                    Term mocTerm = (Term) moc;
                    org.deri.wsml.reasoner.wsmlcore.datalog.Term dTerm = convertWSMLTerm2Datalog(mocTerm);
                    org.deri.wsml.reasoner.wsmlcore.datalog.Term[] args = new org.deri.wsml.reasoner.wsmlcore.datalog.Term[] {
                            objTerm, dTerm };
                    Literal l = new Literal(WSML2Datalog.this.PRED_MEMBER_OF,
                            Literal.NegationType.NONNEGATED, args);
                    if (inBodyOfRule) {
                        datalogBody.add(l);
                    } else if (inHeadOfRule) {
                        conjunctiveHeadLiterals.add(l); // store new conjunctive
                                                        // head literal list;
                        // later we will generate a separate rule for each of
                        // them.
                    } else {
                        // We do not have an implication but only a simple fact.
                        conjunctiveHeadLiterals.add(l); // store new conjunctive
                                                        // head literal list;
                        // later we will generate a separate rule for each of
                        // them.
                    }
                    usedAuxilliaryPredicates
                            .add(WSML2Datalog.this.PRED_MEMBER_OF);
                }
            }

            // AttributeSpecs part ...

            if (attributeSpecs != null && attributeSpecs.size() > 0) {
                for (Object attSpec : attributeSpecs) {
                    translateAttrSpecification((AttrSpecification) attSpec,
                            objTerm);
                }
            }

        }

        private void translateAttrSpecification(AttrSpecification attSpec,
                org.deri.wsml.reasoner.wsmlcore.datalog.Term objTerm) {
            Predicate p;
            switch (attSpec.getOperator()) {
            case AttrSpecification.ATTR_VALUE:
                p = WSML2Datalog.this.PRED_HAS_VALUE;
                break;
            case AttrSpecification.ATTR_INFERENCE:
                p = WSML2Datalog.this.PRED_IMPLIES_TYPE;
                break;
            case AttrSpecification.ATTR_CONSTRAINT:
                p = WSML2Datalog.this.PRED_OF_TYPE;
                break;
            default:
                throw new IllegalArgumentException(
                        "Unknown attribute specification type! "
                                + attSpec.toString());
            }

            Term wsmlAttr = attSpec.getName();
            org.deri.wsml.reasoner.wsmlcore.datalog.Term attrTerm = convertWSMLTerm2Datalog(wsmlAttr); // here
                                                                                                        // the
                                                                                                        // method
                                                                                                        // is a
                                                                                                        // bit
                                                                                                        // to
                                                                                                        // general
                                                                                                        // but
                                                                                                        // works.

            for (Object attVal : attSpec.listArguments()) {
                Term attValTerm = (Term) attVal;
                org.deri.wsml.reasoner.wsmlcore.datalog.Term valTerm = convertWSMLTerm2Datalog(attValTerm);
                org.deri.wsml.reasoner.wsmlcore.datalog.Term[] args = new org.deri.wsml.reasoner.wsmlcore.datalog.Term[] {
                        objTerm, attrTerm, valTerm };
                Literal l = new Literal(p, Literal.NegationType.NONNEGATED,
                        args);
                if (inBodyOfRule) {
                    datalogBody.add(l);
                } else if (inHeadOfRule) {
                    conjunctiveHeadLiterals.add(l); // store new conjunctive
                                                    // head literal list;
                    // later we will generate a separate rule for each of them.
                } else {
                    // We do not have an implication but only a simple fact.
                    conjunctiveHeadLiterals.add(l); // store new conjunctive
                                                    // head literal list;
                    // later we will generate a separate rule for each of them.
                }
                usedAuxilliaryPredicates.add(p);
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.deri.wsml.reasoner.normalization.InfixOrderLogicalExpressionVisitor#handleNeg(org.omwg.logexpression.Unary)
         */
        @Override
        public void enterNeg(Unary arg0) {
            throw new DatalogException(
                    "Classical negation is not allowed in simple WSML datalog rules.");
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.deri.wsml.reasoner.normalization.InfixOrderLogicalExpressionVisitor#enterNaf(org.omwg.logexpression.Unary)
         */
        @Override
        public void enterNaf(Unary arg0) {
            throw new DatalogException(
                    "Negation as failure is not allowed in simple WSML datalog rules.");
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.deri.wsml.reasoner.normalization.InfixOrderLogicalExpressionVisitor#handleOr(org.omwg.logexpression.Binary)
         */
        @Override
        public void enterOr(Binary arg0) {
            throw new DatalogException(
                    "Disjunction is not allowed in simple WSML datalog rules.");
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.deri.wsml.reasoner.normalization.InfixOrderLogicalExpressionVisitor#enterImpliedBy(org.omwg.logexpression.Binary)
         */
        @Override
        public void enterImpliedBy(Binary arg0) {
            implicationCount++;
            if (implicationCount > 1) {
                throw new DatalogException(
                        "More than one implication in the given WSML rule detected!");
            }
            inHeadOfRule = true;
            inBodyOfRule = false;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.deri.wsml.reasoner.normalization.InfixOrderLogicalExpressionVisitor#enterImplies(org.omwg.logexpression.Binary)
         */
        @Override
        public void enterImplies(Binary arg0) {
            implicationCount++;
            if (implicationCount > 1) {
                throw new DatalogException(
                        "More than one implication in the given WSML rule detected!");
            }
            inHeadOfRule = false;
            inBodyOfRule = true;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.deri.wsml.reasoner.normalization.InfixOrderLogicalExpressionVisitor#enterImpliesLP(org.omwg.logexpression.Binary)
         */
        @Override
        public void enterImpliesLP(Binary arg0) {
            implicationCount++;
            if (implicationCount > 1) {
                throw new DatalogException(
                        "More than one implication in the given WSML rule detected!");
            }
            inHeadOfRule = true;
            inBodyOfRule = false;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.deri.wsml.reasoner.normalization.InfixOrderLogicalExpressionVisitor#handleImpliedBy(org.omwg.logexpression.Binary)
         */
        @Override
        public void handleImpliedBy(Binary arg0) {
            inHeadOfRule = false;
            inBodyOfRule = true;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.deri.wsml.reasoner.normalization.InfixOrderLogicalExpressionVisitor#handleImplies(org.omwg.logexpression.Binary)
         */
        @Override
        public void handleImplies(Binary arg0) {
            inHeadOfRule = true;
            inBodyOfRule = false;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.deri.wsml.reasoner.normalization.InfixOrderLogicalExpressionVisitor#handleImpliesLP(org.omwg.logexpression.Binary)
         */
        @Override
        public void handleImpliesLP(Binary arg0) {
            inHeadOfRule = false;
            inBodyOfRule = true;
        }

        private org.deri.wsml.reasoner.wsmlcore.datalog.Term convertWSMLTerm2Datalog(
                Term t) {
            org.deri.wsml.reasoner.wsmlcore.datalog.Term result = null;

            if (t instanceof Variable) {
                // Case: Variable
                Variable v = (Variable) t;
                result = new org.deri.wsml.reasoner.wsmlcore.datalog.Variable(v
                        .getName());
            } else if (t instanceof IRI) {
                // Case: Constant
                IRI c = (IRI) t;
                result = new org.deri.wsml.reasoner.wsmlcore.datalog.Constant(c
                        .asString());
            } else if (t instanceof SimpleDataType) {
                // Case: Simple datatype values

                if (t instanceof WsmlInteger) {
                    WsmlInteger iValue = (WsmlInteger) t;
                    result = new DataTypeValue(iValue.toString(),
                            DataTypeValue.DataType.INTEGER);
                } else if (t instanceof WsmlDecimal) {
                    WsmlDecimal dValue = (WsmlDecimal) t;
                    result = new DataTypeValue(dValue.toString(),
                            DataTypeValue.DataType.DECIMAL);
                } else if (t instanceof WsmlString) {
                    WsmlString sValue = (WsmlString) t;
                    result = new DataTypeValue(sValue.toString(),
                            DataTypeValue.DataType.STRING);
                }

            } else if (t instanceof ComplexDataType) {
                throw new DatalogException(
                        "Complex datatype values are not supported at present in simple WSML rules");
            } else if (t instanceof org.wsmo.common.AnonymousID) {
                throw new DatalogException(
                        "Anonymous identifiers are not allowed in simple WSML rules");
            } else {
                throw new DatalogException(
                        "Atomic formulae must have arguments which are either variables or constants only!");
            }

            return result;

        }

    }

}
