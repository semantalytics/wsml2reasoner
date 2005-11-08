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

package org.wsml.reasoner.transformation;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.omwg.logicalexpression.*;
import org.omwg.logicalexpression.terms.ConstructedTerm;
import org.omwg.logicalexpression.terms.NumberedAnonymousID;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.ComplexDataValue;
import org.omwg.ontology.SimpleDataValue;
import org.omwg.ontology.Variable;
import org.omwg.ontology.WsmlDataType;
import org.wsml.reasoner.datalog.DataTypeValue;
import org.wsml.reasoner.datalog.DatalogException;
import org.wsml.reasoner.datalog.Literal;
import org.wsml.reasoner.datalog.Predicate;
import org.wsml.reasoner.datalog.Program;
import org.wsml.reasoner.datalog.Rule;
import org.wsmo.common.IRI;
import org.wsmo.common.UnnumberedAnonymousID;

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
    private final static Predicate PRED_SUB_CONCEPT_OF = new Predicate(
            "wsml-subconcept-of", 2);;

    private final static Predicate PRED_OF_TYPE = new Predicate("wsml-of-type",
            3);;

    private final static Predicate PRED_IMPLIES_TYPE = new Predicate(
            "wsml-implies-type", 3);;

    private final static Predicate PRED_MEMBER_OF = new Predicate(
            "wsml-member-of", 2);;

    private final static Predicate PRED_HAS_VALUE = new Predicate(
            "wsml-has-value", 3);;

    // private Logger logger = Logger
    // .getLogger("org.wsml.reasoner.wsmlcore.WSML2Datalog");

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
    public Program transform(
            Set<? extends org.omwg.logicalexpression.LogicalExpression> rules)
            throws IllegalArgumentException {
        Program result = new Program();

        DatalogVisitor datalogVisitor = new DatalogVisitor();
//        int i = 0;
        for (org.omwg.logicalexpression.LogicalExpression r : rules) {
//            i++;
//            System.out.println(i+""+r);
            r.accept(datalogVisitor);
            Program translation = (Program) datalogVisitor
                    .getSerializedObject();
            if (translation != null) {
                result.addAll(translation);
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
    public Program transform(org.omwg.logicalexpression.LogicalExpression rule)
            throws IllegalArgumentException {
        Set<org.omwg.logicalexpression.LogicalExpression> rules = new HashSet<org.omwg.logicalexpression.LogicalExpression>();
        rules.add(rule);
        return transform(rules);
    }

    public Program generateAuxilliaryRules() {
        Program result = new Program();

        org.wsml.reasoner.datalog.Variable v1 = new org.wsml.reasoner.datalog.Variable(
                "?v1");
        org.wsml.reasoner.datalog.Variable v2 = new org.wsml.reasoner.datalog.Variable(
                "?v2");
        org.wsml.reasoner.datalog.Variable v3 = new org.wsml.reasoner.datalog.Variable(
                "?v3");
        org.wsml.reasoner.datalog.Variable v4 = new org.wsml.reasoner.datalog.Variable(
                "?v4");
        org.wsml.reasoner.datalog.Variable v5 = new org.wsml.reasoner.datalog.Variable(
                "?v5");

        List<Literal> body;
        Literal head;

        // transitivity: sco(?c1,?c3) <- sco(?c1,?c2) and sco(?c2,?c3)
        // extension-subset: mo(?o,?c2) <- mo(?o,?c1) and sco(?c1,?c2)
        body = new LinkedList<Literal>();
        head = new Literal(PRED_SUB_CONCEPT_OF,
                new org.wsml.reasoner.datalog.Term[] { v1, v3 });
        body.add(new Literal(PRED_SUB_CONCEPT_OF,
                new org.wsml.reasoner.datalog.Term[] { v1, v2 }));
        body.add(new Literal(PRED_SUB_CONCEPT_OF,
                new org.wsml.reasoner.datalog.Term[] { v2, v3 }));
        result.add(new Rule(head, body));

        body = new LinkedList<Literal>();
        head = new Literal(PRED_MEMBER_OF,
                new org.wsml.reasoner.datalog.Term[] { v1, v3 });
        body.add(new Literal(PRED_MEMBER_OF,
                new org.wsml.reasoner.datalog.Term[] { v1, v2 }));
        body.add(new Literal(PRED_SUB_CONCEPT_OF,
                new org.wsml.reasoner.datalog.Term[] { v2, v3 }));
        result.add(new Rule(head, body));

        // Inference of attr value types: mo(v,c2) <- itype(c1, att,
        // c2), mo(o,c1), hval(o,att, v)
        // mo(v1,v2) <- itype(v3, v4, v2), mo(v5,v3), hval(v5,v4, v1)
        body = new LinkedList<Literal>();
        head = new Literal(PRED_MEMBER_OF,
                new org.wsml.reasoner.datalog.Term[] { v1, v2 });
        body.add(new Literal(PRED_IMPLIES_TYPE,
                new org.wsml.reasoner.datalog.Term[] { v3, v4, v2 }));
        body.add(new Literal(PRED_MEMBER_OF,
                new org.wsml.reasoner.datalog.Term[] { v5, v3 }));
        body.add(new Literal(PRED_HAS_VALUE,
                new org.wsml.reasoner.datalog.Term[] { v5, v4, v1 }));
        result.add(new Rule(head, body));

        // Semantics of X1[X2 => X3] (oftype constraint)
        // !- oftype(c1, att, c2), mo(i,c1), hval(i, att, v), NAF mo(v,c2)
        // With variables: oftype(v1, v2, v3), mo(v4,v1), hval(v4, v2, v5), NAF
        // mo(v5,v3)

        body = new LinkedList<Literal>();
        body.add(new Literal(PRED_OF_TYPE,
                new org.wsml.reasoner.datalog.Term[] { v1, v2, v3 }));
        body.add(new Literal(PRED_MEMBER_OF,
                new org.wsml.reasoner.datalog.Term[] { v4, v1 }));
        body.add(new Literal(PRED_HAS_VALUE,
                new org.wsml.reasoner.datalog.Term[] { v4, v2, v5 }));
        body.add(new Literal(PRED_MEMBER_OF,
                Literal.NegationType.NEGATIONASFAILURE,
                new org.wsml.reasoner.datalog.Term[] { v5, v3 }));
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

        private boolean isNegated;

        private DatalogTermVisitor datalogTermVisitor = new DatalogTermVisitor();

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
         * @see org.wsml.reasoner.normalization.InfixOrderLogicalExpressionVisitor#getSerializedObject()
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
         * @see org.wsml.reasoner.normalization.DepthFirstLogicalExpressionVisitor#handleAtom(org.omwg.logicalexpression.Atom)
         */
        @Override
        public void handleAtom(Atom atom) {
            Predicate p = new Predicate(atom.getIdentifier().toString(), atom
                    .getArity());
            org.wsml.reasoner.datalog.Term[] predArgs = new org.wsml.reasoner.datalog.Term[atom
                    .getArity()];

            for (int i = 0; i < atom.getArity(); i++) {
                Term atomArg = atom.getParameter(i);
                predArgs[i] = convertWSMLTerm2Datalog(atomArg);
            } // for
            Literal.NegationType negationType = isNegated ? Literal.NegationType.NEGATIONASFAILURE
                    : Literal.NegationType.NONNEGATED;

            Literal l = new Literal(p, negationType, predArgs); // currently we
            // ignore
            // negation
            // since we deal
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
            org.wsml.reasoner.datalog.Term subject = convertWSMLTerm2Datalog(arg0
                    .getLeftParameter());
            Literal l;
            Literal.NegationType negationType = isNegated ? Literal.NegationType.NEGATIONASFAILURE
                    : Literal.NegationType.NONNEGATED;
            org.wsml.reasoner.datalog.Term object = convertWSMLTerm2Datalog(arg0
                    .getRightParameter());
            l = new Literal(PRED_SUB_CONCEPT_OF, negationType,
                    new org.wsml.reasoner.datalog.Term[] { subject, object });
            storeLiteral(l);
        }

        @Override
        public void handleMemberShipMolecule(MembershipMolecule arg0) {
            org.wsml.reasoner.datalog.Term subject = convertWSMLTerm2Datalog(arg0
                    .getLeftParameter());
            Literal l;
            Literal.NegationType negationType = isNegated ? Literal.NegationType.NEGATIONASFAILURE
                    : Literal.NegationType.NONNEGATED;
            org.wsml.reasoner.datalog.Term object = convertWSMLTerm2Datalog(arg0
                    .getRightParameter());
            l = new Literal(PRED_MEMBER_OF, negationType,
                    new org.wsml.reasoner.datalog.Term[] { subject, object });
            storeLiteral(l);
        }

        @Override
        public void handleAttributeInferenceMolecule(
                AttributeInferenceMolecule arg0) {
            org.wsml.reasoner.datalog.Term subject = convertWSMLTerm2Datalog(arg0
                    .getLeftParameter());
            Literal l;
            Literal.NegationType negationType = isNegated ? Literal.NegationType.NEGATIONASFAILURE
                    : Literal.NegationType.NONNEGATED;
            org.wsml.reasoner.datalog.Term object = convertWSMLTerm2Datalog(arg0
                    .getRightParameter());
            org.wsml.reasoner.datalog.Term attr = convertWSMLTerm2Datalog(arg0
                    .getAttribute());
            l = new Literal(PRED_IMPLIES_TYPE, negationType,
                    new org.wsml.reasoner.datalog.Term[] { subject, attr,
                            object });
            storeLiteral(l);
        }

        @Override
        public void handleAttributeConstraintMolecule(
                AttributeConstraintMolecule arg0) {
            org.wsml.reasoner.datalog.Term subject = convertWSMLTerm2Datalog(arg0
                    .getLeftParameter());
            Literal l;
            Literal.NegationType negationType = isNegated ? Literal.NegationType.NEGATIONASFAILURE
                    : Literal.NegationType.NONNEGATED;
            org.wsml.reasoner.datalog.Term object = convertWSMLTerm2Datalog(arg0
                    .getRightParameter());
            org.wsml.reasoner.datalog.Term attr = convertWSMLTerm2Datalog(arg0
                    .getAttribute());
            l = new Literal(PRED_OF_TYPE, negationType,
                    new org.wsml.reasoner.datalog.Term[] { subject, attr,
                            object });
            storeLiteral(l);
        }

        @Override
        public void handleAttributeValueMolecule(AttributeValueMolecule arg0) {
            org.wsml.reasoner.datalog.Term subject = convertWSMLTerm2Datalog(arg0
                    .getLeftParameter());
            Literal l;
            Literal.NegationType negationType = isNegated ? Literal.NegationType.NEGATIONASFAILURE
                    : Literal.NegationType.NONNEGATED;
            org.wsml.reasoner.datalog.Term object = convertWSMLTerm2Datalog(arg0
                    .getRightParameter());
            org.wsml.reasoner.datalog.Term attr = convertWSMLTerm2Datalog(arg0
                    .getAttribute());
            l = new Literal(PRED_HAS_VALUE, negationType,
                    new org.wsml.reasoner.datalog.Term[] { subject, attr,
                            object });
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
            isNegated = true;

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

        private org.wsml.reasoner.datalog.Term convertWSMLTerm2Datalog(Term t) {
            t.accept(this.datalogTermVisitor);
            return this.datalogTermVisitor.getDatalogTerm();
        }

    }

    /**
     * Converts a WSML term to its Datalog representation
     * 
     * @author Gabor Nagypal (FZI)
     * 
     */
    private static class DatalogTermVisitor extends TermVisitor {

        private org.wsml.reasoner.datalog.Term datalogTerm = null;

        public org.wsml.reasoner.datalog.Term getDatalogTerm() {
            return datalogTerm;
        }

        @Override
        public void enterConstructedTerm(ConstructedTerm arg0) {
            throw new DatalogException(
                    "Constructed terms are not allowed in simple WSML rules!");
        }

        @Override
        public void visitComplexDataValue(ComplexDataValue arg0) {
            throw new DatalogException(
                    "Complex datatype values are not supported at present in simple WSML rules");
        }

        @Override
        public void visitIRI(IRI arg0) {
            datalogTerm = new org.wsml.reasoner.datalog.Constant(arg0
                    .toString());
        }

        @Override
        public void visitVariable(Variable arg0) {
            datalogTerm = new org.wsml.reasoner.datalog.Variable(arg0.getName());
        }

        @Override
        public void visitSimpleDataValue(SimpleDataValue arg0) {
            String typeIRI = arg0.getType().getIRI().toString();
            if (typeIRI.equals(WsmlDataType.WSML_INTEGER)) {
                datalogTerm = new DataTypeValue(arg0.getValue().toString(),
                        DataTypeValue.DataType.INTEGER);
            } else if (typeIRI.equals(WsmlDataType.WSML_DECIMAL)) {
                datalogTerm = new DataTypeValue(arg0.getValue().toString(),
                        DataTypeValue.DataType.DECIMAL);

            } else if (typeIRI.equals(WsmlDataType.WSML_STRING)) {
                datalogTerm = new DataTypeValue(arg0.getValue().toString(),
                        DataTypeValue.DataType.STRING);
            } else {
                throw new DatalogException("Unsupported simple datatype:"
                        + typeIRI);
            }
        }

        @Override
        public void visitNumberedID(NumberedAnonymousID arg0) {
            throw new DatalogException(
                    "Anonymous identifiers are not allowed in simple WSML rules");
        }

        @Override
        public void visitUnnumberedID(UnnumberedAnonymousID arg0) {
            throw new DatalogException(
                    "Anonymous identifiers are not allowed in simple WSML rules");
        }

    }

}
