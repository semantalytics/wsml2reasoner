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

package org.wsml.reasoner.builtin;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.omwg.logicalexpression.*;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsml.reasoner.transformation.InfixOrderLogicalExpressionVisitor;
import org.wsmo.factory.WsmoFactory;

/**
 * <p>
 * Converts a set of WSML constructs to datalog. The following types of WSML
 * constructs are allowed:
 * </p>
 * <p>
 * <ul>
 * <li>A (fact)</li>
 * <li>A :- B1 and B2 and ... and Bn (rule)</li>
 * <li>!- B1 and B2 and ... and Bn (constraint)</li>
 * </ul>
 * Where Ai and Bi are either atoms or simple molecules (molecules which could
 * be logically transformed to atoms), or negation of them
 * </p>
 * <p>
 * Implementation is not thread-safe and thus may not be shared across different
 * threads.
 * </p>
 * 
 * @author Uwe Keller, DERI Innsbruck
 * @author Gabor Nagypal, FZI
 */
public class WSML2DatalogTransformer {

    // Some predicates that are used to represent the oo-based molecules
    // of WSML in datalog.
    public final static String PRED_SUB_CONCEPT_OF = "wsml-subconcept-of";

    public final static String PRED_OF_TYPE = "wsml-of-type";

    public final static String PRED_IMPLIES_TYPE = "wsml-implies-type";

    public final static String PRED_MEMBER_OF = "wsml-member-of";

    public final static String PRED_HAS_VALUE = "wsml-has-value";

    /**
     * Generates a WSML2Datalog converter.
     */
    public WSML2DatalogTransformer() {
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
    public Set<Rule> transform(Set<? extends LogicalExpression> rules)
            throws IllegalArgumentException {
        Set<Rule> result = new HashSet<Rule>();

        DatalogVisitor datalogVisitor = new DatalogVisitor();

        for (LogicalExpression r : rules) {
            r.accept(datalogVisitor);
            Rule translation = (Rule) datalogVisitor.getSerializedObject();
            if (translation != null) {
                result.add(translation);
            } else {
                throw new IllegalArgumentException(
                        "WSML rule can not be translated to datalog: "
                                + r.toString());
            }
            // Reset the internal state of the visitor such that it can be
            // reused.
            datalogVisitor.reset();
        }
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
    public Set<Rule> transform(LogicalExpression rule)
            throws IllegalArgumentException {
        Set<LogicalExpression> rules = new HashSet<LogicalExpression>();
        rules.add(rule);
        return transform(rules);
    }

    public Set<Rule> generateAuxilliaryRules() {
        Set<Rule> result = new HashSet<Rule>();

        WsmoFactory f = WSMO4JManager.getWSMOFactory();

        Variable vConcept = f.createVariable("concept");
        Variable vConcept2 = f.createVariable("concept2");
        Variable vConcept3 = f.createVariable("concept3");
        Variable vAttribute = f.createVariable("attribute");
        Variable vRange = f.createVariable("range");
        Variable vInstance = f.createVariable("instance");
        Variable vInstance2 = f.createVariable("instance2");
        Variable vAttributeValue = f.createVariable("attributevalue");

        List<Literal> body;
        Literal head;

        // transitivity: sco(?c1,?c3) <- sco(?c1,?c2) and sco(?c2,?c3)
        // extension-subset: mo(?o,?c2) <- mo(?o,?c1) and sco(?c1,?c2)
        body = new LinkedList<Literal>();
        head = new Literal(true, PRED_SUB_CONCEPT_OF, vConcept, vConcept3);
        body.add(new Literal(true, PRED_SUB_CONCEPT_OF, vConcept, vConcept2));
        body.add(new Literal(true, PRED_SUB_CONCEPT_OF, vConcept2, vConcept3));
        result.add(new Rule(head, body));

        body = new LinkedList<Literal>();
        head = new Literal(true, PRED_MEMBER_OF, vInstance, vConcept2);
        body.add(new Literal(true, PRED_MEMBER_OF, vInstance, vConcept ));
        body.add(new Literal(true, PRED_SUB_CONCEPT_OF, vConcept, vConcept2 ));
        result.add(new Rule(head, body));

        // Inference of attr value types: mo(v,c2) <- itype(c1, att,
        // c2), mo(o,c1), hval(o,att, v)
        // mo(v1,v2) <- itype(v3, v4, v2), mo(v5,v3), hval(v5,v4, v1)
        body = new LinkedList<Literal>();
        head = new Literal(true, PRED_MEMBER_OF, vInstance2, vConcept2 );
        body.add(new Literal(true, PRED_IMPLIES_TYPE, vConcept, vAttribute, vConcept2));
        body.add(new Literal(true, PRED_MEMBER_OF, vInstance, vConcept));
        body.add(new Literal(true, PRED_HAS_VALUE, vInstance, vAttribute, vInstance2));
        result.add(new Rule(head, body));

        // Semantics of C1[att => C2] (oftype constraint)
        // !- oftype(c1, att, c2), mo(i,c1), hval(i, att, v), NAF mo(v,c2)
        // With variables: oftype(v1, v2, v3), mo(v4,v1), hval(v4, v2, v5), NAF
        // mo(v5,v3)

        body = new LinkedList<Literal>();
        body.add(new Literal(true, PRED_OF_TYPE, vConcept, vAttribute, vRange));
        body.add(new Literal(true, PRED_MEMBER_OF, vInstance, vConcept));
        body.add(new Literal(true, PRED_HAS_VALUE, vInstance, vAttribute, vAttributeValue));
        body.add(new Literal(false, PRED_MEMBER_OF, vAttributeValue, vRange));
        result.add(new Rule(null, body));

        return result;
    }

    // -- Inner class for visiting rules (logical expressions) ...

    /**
     * Given a WSML rule the visitor generates a new datalog rule that
     * represents the original WSML rule but where all non-predicate molecules
     * are replaced by respective atomic formulaes that use specific predicates
     * 
     * Implements a left-first, depth-first, infix traversal.
     */
    private static class DatalogVisitor extends
            InfixOrderLogicalExpressionVisitor {

        private List<Literal> datalogBody;

        private Literal datalogHead;

        private boolean inHeadOfRule;

        private boolean inBodyOfRule;

        private int implicationCount;

        private boolean positive;

        public DatalogVisitor() {
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
            datalogHead = null;
            inHeadOfRule = false;
            inBodyOfRule = false;
            implicationCount = 0;
            positive = true;
        }

        /**
         * Constructs a datalog representation of the given (visited) WSML rule.
         * The representation is a datalog program.
         * 
         * The construction introduces new predicates for which a defining
         * datalog rules will not be generated here (in order to avoid multiple
         * generation of the same rules). Instead they will be generated only
         * once in WSML2Datalog.transform().
         * 
         * @return the Datalog program that represents the WSML rule or null if
         *         the visited rule does not conform to the syntax requirements
         *         stated in class WSML2Datalog and thus can not be translated
         *         to datalog.
         * 
         * @see org.wsml.reasoner.normalization.InfixOrderLogicalExpressionVisitor#getSerializedObject()
         */
        @Override
        public Rule getSerializedObject() {
            Rule result = null;
            if (datalogBody != null) {
                result = new Rule(datalogHead, datalogBody);
            } else if (datalogHead != null) {
                result = new Rule(datalogHead);
            }
            return result;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.wsml.reasoner.normalization.DepthFirstLogicalExpressionVisitor#handleAtom(org.omwg.logicalexpression.Atom)
         */
        @Override
        public void handleAtom(Atom atom) {
            String predUri = atom.getIdentifier().toString();
            // System.out.println("Atom URI:" + predUri);
            // System.out.println("Atom arity:" + atom.getArity());
            // System.out.println("Atom parameters:" + atom.listParamters());
            // conditional expression is needed because WSMO4J throws a
            // nullpointerexception for atom.listParameters()
            Literal l = (atom.getArity() > 0) ? new Literal(positive, predUri,
                    atom.listParameters()) : new Literal(positive, predUri,
                    new ArrayList<Term>());
            storeLiteral(l);
        }

        private void storeLiteral(Literal l) {
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
            positive = true;
        }

        @Override
        public void enterConstraint(Constraint arg0) {
            implicationCount++;
            if (implicationCount > 1) {
                throw new DatalogException(
                        "More than one implication in the given WSML rule detected!");
            }
            inHeadOfRule = false;
            inBodyOfRule = true;
        }

        @Override
        public void enterEquivalence(Equivalence arg0) {
            throw new DatalogException(
                    "Equivalent is not allowed in simple WSML datalog rules.");
        }

        @Override
        public void enterExistentialQuantification(
                ExistentialQuantification arg0) {
            throw new DatalogException(
                    "Quantifier is not allowed in simple WSML datalog rules.");
        }

        @Override
        public void enterUniversalQuantification(UniversalQuantification arg0) {
            throw new DatalogException(
                    "Quantifier is not allowed in simple WSML datalog rules.");
        }

        @Override
        public void handleSubConceptMolecule(SubConceptMolecule arg0) {
            Literal l = new Literal(positive, PRED_SUB_CONCEPT_OF, arg0
                    .getLeftParameter(), arg0.getRightParameter());
            storeLiteral(l);
        }

        @Override
        public void handleMemberShipMolecule(MembershipMolecule arg0) {
            Literal l = new Literal(positive, PRED_MEMBER_OF, arg0
                    .getLeftParameter(), arg0.getRightParameter());
            storeLiteral(l);
        }

        @Override
        public void handleAttributeInferenceMolecule(
                AttributeInferenceMolecule arg0) {
            Literal l = new Literal(positive, PRED_IMPLIES_TYPE, arg0
                    .getLeftParameter(), arg0.getAttribute(), arg0
                    .getRightParameter());
            storeLiteral(l);
        }

        @Override
        public void handleAttributeConstraintMolecule(
                AttributeConstraintMolecule arg0) {
            Literal l = new Literal(positive, PRED_OF_TYPE, arg0
                    .getLeftParameter(), arg0.getAttribute(), arg0
                    .getRightParameter());
            storeLiteral(l);
        }

        @Override
        public void handleAttributeValueMolecule(AttributeValueMolecule arg0) {
            Literal l = new Literal(positive, PRED_HAS_VALUE, arg0
                    .getLeftParameter(), arg0.getAttribute(), arg0
                    .getRightParameter());
            storeLiteral(l);
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.wsml.reasoner.normalization.InfixOrderLogicalExpressionVisitor#handleNeg(org.omwg.logicalexpression.Unary)
         */
        @Override
        public void enterNegation(Negation arg0) {
            throw new DatalogException(
                    "Classical negation is not allowed in simple WSML datalog rules.");
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.wsml.reasoner.normalization.InfixOrderLogicalExpressionVisitor#enterNaf(org.omwg.logicalexpression.Unary)
         */
        @Override
        public void enterNegationAsFailure(NegationAsFailure naf) {
            LogicalExpression arg = naf.getOperand();
            if (!(arg instanceof AtomicExpression))
                throw new DatalogException(
                        "Negation as failure is allowed only on atoms or molecules in simple WSML datalog rules.");
            positive = false;

        }

        /*
         * (non-Javadoc)
         * 
         * @see org.wsml.reasoner.normalization.InfixOrderLogicalExpressionVisitor#handleOr(org.omwg.logicalexpression.Binary)
         */
        @Override
        public void enterDisjunction(Disjunction arg0) {
            throw new DatalogException(
                    "Disjunction is not allowed in simple WSML datalog rules.");
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.wsml.reasoner.normalization.InfixOrderLogicalExpressionVisitor#enterImpliedBy(org.omwg.logicalexpression.Binary)
         */
        @Override
        public void enterInverseImplication(InverseImplication arg0) {
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
         * @see org.wsml.reasoner.normalization.InfixOrderLogicalExpressionVisitor#enterImplies(org.omwg.logicalexpression.Binary)
         */
        @Override
        public void enterImplication(Implication arg0) {
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
         * @see org.wsml.reasoner.normalization.InfixOrderLogicalExpressionVisitor#enterImpliesLP(org.omwg.logicalexpression.Binary)
         */
        @Override
        public void enterLogicProgrammingRule(LogicProgrammingRule arg0) {
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
         * @see org.wsml.reasoner.normalization.InfixOrderLogicalExpressionVisitor#handleImpliedBy(org.omwg.logicalexpression.Binary)
         */
        @Override
        public void handleInverseImplication(InverseImplication arg0) {
            inHeadOfRule = false;
            inBodyOfRule = true;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.wsml.reasoner.normalization.InfixOrderLogicalExpressionVisitor#handleImplies(org.omwg.logicalexpression.Binary)
         */
        @Override
        public void handleImplication(Implication arg0) {
            inHeadOfRule = true;
            inBodyOfRule = false;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.wsml.reasoner.normalization.InfixOrderLogicalExpressionVisitor#handleImpliesLP(org.omwg.logicalexpression.Binary)
         */
        @Override
        public void handleLogicProgrammingRule(LogicProgrammingRule arg0) {
            inHeadOfRule = false;
            inBodyOfRule = true;
        }

    }

}
