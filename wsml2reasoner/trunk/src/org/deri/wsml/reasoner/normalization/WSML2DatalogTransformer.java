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

package org.deri.wsml.reasoner.normalization;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.deri.wsml.reasoner.wsmlcore.datalog.*;
import org.omwg.logexpression.*;
import org.omwg.logexpression.LogicalExpression;
import org.omwg.logexpression.terms.IRI;
import org.omwg.logexpression.terms.Term;
import org.omwg.logexpression.terms.Variable;
import org.omwg.ontology.*;

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
    private final Predicate PRED_SUB_CONCEPT_OF;

    private final Predicate PRED_OF_TYPE;

    private final Predicate PRED_IMPLIES_TYPE;

    private final Predicate PRED_MEMBER_OF;

    private final Predicate PRED_HAS_VALUE;

    private Logger logger = Logger
            .getLogger("org.deri.wsml.reasoner.wsmlcore.WSML2Datalog");

    /**
     * Generates a WSML2Datalog converter.
     */
    public WSML2DatalogTransformer() {
        PRED_SUB_CONCEPT_OF = new Predicate("wsml-subconcept-of", 2);
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

        DatalogVisitor datalogVisitor = new DatalogVisitor();

        for (org.omwg.logexpression.LogicalExpression r : rules) {
            r.accept(datalogVisitor);
            Program translation = (Program) datalogVisitor.getSerializedObject();
            if (translation != null) {
                result.addAll(translation);
            } else {
                throw new IllegalArgumentException(
                        "WSML rule can not be translated to datalog: "
                                + r.asString());
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
    public Program transform(org.omwg.logexpression.LogicalExpression rule)
            throws IllegalArgumentException {
        Set<org.omwg.logexpression.LogicalExpression> rules = new HashSet<org.omwg.logexpression.LogicalExpression>();
        rules.add(rule);
        return transform(rules);
    }

    public Program generateAuxilliaryRules() {
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

        // transitivity: sco(?c1,?c3) <- sco(?c1,?c2) and sco(?c2,?c3)
        // extension-subset: mo(?o,?c2) <- mo(?o,?c1) and sco(?c1,?c2)
        body = new LinkedList<Literal>();
        head = new Literal(PRED_SUB_CONCEPT_OF,
                new org.deri.wsml.reasoner.wsmlcore.datalog.Term[] { v1, v3 });
        body.add(new Literal(PRED_SUB_CONCEPT_OF,
                new org.deri.wsml.reasoner.wsmlcore.datalog.Term[] { v1, v2 }));
        body.add(new Literal(PRED_SUB_CONCEPT_OF,
                new org.deri.wsml.reasoner.wsmlcore.datalog.Term[] { v2, v3 }));
        result.add(new Rule(head, body));

        body = new LinkedList<Literal>();
        head = new Literal(PRED_MEMBER_OF,
                new org.deri.wsml.reasoner.wsmlcore.datalog.Term[] { v1, v3 });
        body.add(new Literal(PRED_MEMBER_OF,
                new org.deri.wsml.reasoner.wsmlcore.datalog.Term[] { v1, v2 }));
        body.add(new Literal(PRED_SUB_CONCEPT_OF,
                new org.deri.wsml.reasoner.wsmlcore.datalog.Term[] { v2, v3 }));
        result.add(new Rule(head, body));

        // Inference of attr value types: mo(v,c2) <- itype(c1, att,
        // c2), mo(o,c1), hval(o,att, v)
        // mo(v1,v2) <- itype(v3, v4, v2), mo(v5,v3), hval(v5,v4, v1)
        body = new LinkedList<Literal>();
        head = new Literal(PRED_MEMBER_OF,
                new org.deri.wsml.reasoner.wsmlcore.datalog.Term[] { v1, v2 });
        body
                .add(new Literal(PRED_IMPLIES_TYPE,
                        new org.deri.wsml.reasoner.wsmlcore.datalog.Term[] {
                                v3, v4, v2 }));
        body.add(new Literal(PRED_MEMBER_OF,
                new org.deri.wsml.reasoner.wsmlcore.datalog.Term[] { v5, v3 }));
        body
                .add(new Literal(PRED_HAS_VALUE,
                        new org.deri.wsml.reasoner.wsmlcore.datalog.Term[] {
                                v5, v4, v1 }));
        result.add(new Rule(head, body));

        // Semantics of X1[X2 => X3] (oftype constraint)
        // !- oftype(c1, att, c2), mo(i,c1), hval(i, att, v), NAF mo(v,c2)
        // With variables: oftype(v1, v2, v3), mo(v4,v1), hval(v4, v2, v5), NAF
        // mo(v5,v3)

        body = new LinkedList<Literal>();
        body
                .add(new Literal(PRED_OF_TYPE,
                        new org.deri.wsml.reasoner.wsmlcore.datalog.Term[] {
                                v1, v2, v3 }));
        body.add(new Literal(PRED_MEMBER_OF,
                new org.deri.wsml.reasoner.wsmlcore.datalog.Term[] { v4, v1 }));
        body
                .add(new Literal(PRED_HAS_VALUE,
                        new org.deri.wsml.reasoner.wsmlcore.datalog.Term[] {
                                v4, v2, v5 }));
        body.add(new Literal(PRED_MEMBER_OF,
                new org.deri.wsml.reasoner.wsmlcore.datalog.Term[] { v5, v3 }));
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
    private class DatalogVisitor extends InfixOrderLogicalExpressionVisitor {

        private List<Literal> datalogBody;

        private Literal datalogHead;

        private boolean inHeadOfRule;

        private boolean inBodyOfRule;

        private int implicationCount;

        private boolean isNegated;

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
            isNegated = false;
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
         * @see org.deri.wsml.reasoner.normalization.InfixOrderLogicalExpressionVisitor#getSerializedObject()
         */
        @Override
        public Program getSerializedObject() {
            Program datalogRepresentation = null;
            if (datalogBody.size() > 0) {
                datalogRepresentation = new Program();
                datalogRepresentation.add(new Rule(datalogHead, datalogBody));

            }

            if (datalogBody != null) {
                datalogRepresentation = new Program();
                datalogRepresentation.add(new Rule(datalogHead, datalogBody));
            } else if (datalogHead != null) {
                datalogRepresentation = new Program();
                datalogRepresentation.add(new Rule(datalogHead));
            }
            return datalogRepresentation;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.deri.wsml.reasoner.normalization.DepthFirstLogicalExpressionVisitor#handleAtom(org.omwg.logexpression.Atom)
         */
        @Override
        public void handleAtom(Atom atom) {
            Predicate p = new Predicate(atom.getIdentifier().asString(), atom
                    .getArity());
            org.deri.wsml.reasoner.wsmlcore.datalog.Term[] predArgs = new org.deri.wsml.reasoner.wsmlcore.datalog.Term[atom
                    .getArity()];

            for (int i = 0; i < atom.getArity(); i++) {
                Term atomArg = atom.getParameter(i);
                predArgs[i] = convertWSMLTerm2Datalog(atomArg);
            } // for
            Literal.NegationType negationType = isNegated ? Literal.NegationType.NEGATIONASFAILURE
                    : Literal.NegationType.NONNEGATED;

            Literal l = new Literal(p, negationType,
                    predArgs); // currently we ignore negation since we deal
            // with WSMLCore

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
            isNegated = false;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.deri.wsml.reasoner.normalization.InfixOrderLogicalExpressionVisitor#handleConstraint(org.omwg.logexpression.Unary)
         */
        @Override
        public void enterConstraint(Unary arg0) {
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

        @Override
        public void handleMolecule(Molecule m) {

            org.deri.wsml.reasoner.wsmlcore.datalog.Term subject = convertWSMLTerm2Datalog(m
                    .getTerm());
            Literal l;
            Literal.NegationType negationType = isNegated ? Literal.NegationType.NEGATIONASFAILURE
                    : Literal.NegationType.NONNEGATED;

            // Handle specific cases

            if (MoleculeUtils.isSimpleSubconceptOf(m)) {
                org.deri.wsml.reasoner.wsmlcore.datalog.Term object = convertWSMLTerm2Datalog(MoleculeUtils
                        .getSuperConcept(m));
                l = new Literal(PRED_SUB_CONCEPT_OF, negationType,
                        new org.deri.wsml.reasoner.wsmlcore.datalog.Term[] {
                                subject, object });
            } else if (MoleculeUtils.isSimpleMemberOf(m)) {
                org.deri.wsml.reasoner.wsmlcore.datalog.Term object = convertWSMLTerm2Datalog(MoleculeUtils
                        .getParentConcept(m));
                l = new Literal(PRED_MEMBER_OF, negationType,
                        new org.deri.wsml.reasoner.wsmlcore.datalog.Term[] {
                                subject, object });
            } else if (MoleculeUtils.isSimpleImpliesType(m)) {
                org.deri.wsml.reasoner.wsmlcore.datalog.Term object = convertWSMLTerm2Datalog(MoleculeUtils
                        .getImpliedType(m));
                org.deri.wsml.reasoner.wsmlcore.datalog.Term attr = convertWSMLTerm2Datalog(MoleculeUtils
                        .getAttrName(m));
                l = new Literal(PRED_IMPLIES_TYPE, negationType,
                        new org.deri.wsml.reasoner.wsmlcore.datalog.Term[] {
                                subject, attr, object });
            } else if (MoleculeUtils.isSimpleOfType(m)) {
                org.deri.wsml.reasoner.wsmlcore.datalog.Term object = convertWSMLTerm2Datalog(MoleculeUtils
                        .getTypeConstraint(m));
                org.deri.wsml.reasoner.wsmlcore.datalog.Term attr = convertWSMLTerm2Datalog(MoleculeUtils
                        .getAttrName(m));
                l = new Literal(PRED_OF_TYPE, negationType,
                        new org.deri.wsml.reasoner.wsmlcore.datalog.Term[] {
                                subject, attr, object });
            } else if (MoleculeUtils.isSimpleAttrValue(m)) {
                org.deri.wsml.reasoner.wsmlcore.datalog.Term object = convertWSMLTerm2Datalog(MoleculeUtils
                        .getAttrValue(m));
                org.deri.wsml.reasoner.wsmlcore.datalog.Term attr = convertWSMLTerm2Datalog(MoleculeUtils
                        .getAttrName(m));
                l = new Literal(PRED_HAS_VALUE, negationType,
                        new org.deri.wsml.reasoner.wsmlcore.datalog.Term[] {
                                subject, attr, object });
            } else
                throw new DatalogException(
                        "Complex molecules are not allowed in simple WSML rules.");

            storeLiteral(l);
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
        public void enterNaf(Unary naf) {
            LogicalExpression arg = naf.getArgument(0);
            if (!(arg instanceof AtomicExpression))
                throw new DatalogException(
                        "Negation as failure is allowed only on atoms or molecules in simple WSML datalog rules.");
            isNegated = true;

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
