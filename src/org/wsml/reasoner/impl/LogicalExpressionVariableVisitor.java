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

package org.wsml.reasoner.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.omwg.logicalexpression.Atom;
import org.omwg.logicalexpression.AttributeConstraintMolecule;
import org.omwg.logicalexpression.AttributeInferenceMolecule;
import org.omwg.logicalexpression.AttributeMolecule;
import org.omwg.logicalexpression.AttributeValueMolecule;
import org.omwg.logicalexpression.CompoundExpression;
import org.omwg.logicalexpression.CompoundMolecule;
import org.omwg.logicalexpression.Conjunction;
import org.omwg.logicalexpression.Constraint;
import org.omwg.logicalexpression.Disjunction;
import org.omwg.logicalexpression.Equivalence;
import org.omwg.logicalexpression.ExistentialQuantification;
import org.omwg.logicalexpression.Implication;
import org.omwg.logicalexpression.InverseImplication;
import org.omwg.logicalexpression.LogicProgrammingRule;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.MembershipMolecule;
import org.omwg.logicalexpression.Molecule;
import org.omwg.logicalexpression.Negation;
import org.omwg.logicalexpression.NegationAsFailure;
import org.omwg.logicalexpression.Quantified;
import org.omwg.logicalexpression.SubConceptMolecule;
import org.omwg.logicalexpression.UniversalQuantification;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.transformation.PostfixOrderLogicalExpressionVisitor;

/**
 * A visitor that analyses variable occurrences in logical expressions. After
 * visiting a logical expression le, it can give the set FreeVars(le) of free
 * variables in le and the set BoundVars(le) of bound variables in le.
 * 
 * @author Uwe Keller, DERI Innsbruck
 * @author Gabor Nagypal, FZI
 */
public class LogicalExpressionVariableVisitor extends
        PostfixOrderLogicalExpressionVisitor {

    private Map<LogicalExpression, Set<Variable>> freeVars;

    private Map<LogicalExpression, Set<Variable>> boundVars;

    private TermVariableVisitor tvv;

    public LogicalExpressionVariableVisitor() {
        super();
        reset();
    }

    /**
     * Resets the internal state of the visitor such that the object can be
     * reused across several calls in the same thread.
     * 
     */
    public void reset() {
        freeVars = new HashMap<LogicalExpression, Set<Variable>>();
        boundVars = new HashMap<LogicalExpression, Set<Variable>>();
        tvv = new TermVariableVisitor();
    }

    /**
     * Not used!
     * 
     * @return null.
     */
    public Object getSerializedObject() {
        return null;
    }

    /**
     * Returns the set of all free variables in the given subexpression of the
     * visited logical expression.
     * 
     * @param some
     *            subexpression of the visited expression
     * @return FreeVar(le) or null if le is not a subexpression of the visited
     *         expression.
     */
    public Set<Variable> getFreeVariables(LogicalExpression le) {
        // System.out.println("freevars=" + freeVars);
        Set<Variable> result = freeVars.get(le);
        return result;
    }

    /**
     * Returns the set of all bound variables in the given subexpression of the
     * visited logical expression.
     * 
     * @param some
     *            subexpression of the visited expression
     * @return FreeVar(le) or null if le is not a subexpression of the visited
     *         expression.
     */
    public Set<Variable> getBoundVariables(LogicalExpression le) {
        Set<Variable> result = boundVars.get(le);
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.wsml.reasoner.normalization.PostfixOrderLogicalExpressionVisitor#handleAtom(org.omwg.logicalexpression.Atom)
     */
    @Override
    public void handleAtom(Atom arg0) {
        Set<Variable> fvs = new HashSet<Variable>();
        Set<Variable> bvs = new HashSet<Variable>();

        for (int i = 0; i < arg0.getArity(); i++) {
            Term nextArg = arg0.getParameter(i);
            internalDoTerm(nextArg, fvs, bvs);
        }

        freeVars.put(arg0, fvs);
        boundVars.put(arg0, bvs);
    }

    @Override
    public void handleConstraint(Constraint arg0) {
        internalDoCompound(arg0);
    }

    @Override
    public void handleEquivalence(Equivalence arg0) {
        internalDoCompound(arg0);
    }

    @Override
    public void handleExistentialQuantification(ExistentialQuantification arg0) {
        internalDoQuantified(arg0);
    }

    @Override
    public void handleUniversalQuantification(UniversalQuantification arg0) {
        internalDoQuantified(arg0);
    }

    @Override
    public void handleImplication(Implication arg0) {
        internalDoCompound(arg0);
    }

    @Override
    public void handleInverseImplication(InverseImplication arg0) {
        internalDoCompound(arg0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.wsml.reasoner.normalization.PostfixOrderLogicalExpressionVisitor#handleImpliesLP(org.omwg.logicalexpression.Binary)
     */
    @Override
    public void handleLogicProgrammingRule(LogicProgrammingRule arg0) {
        internalDoCompound(arg0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.wsml.reasoner.normalization.PostfixOrderLogicalExpressionVisitor#handleMolecule(org.omwg.logicalexpression.Molecule)
     */
    @Override
    public void handleCompoundMolecule(CompoundMolecule arg0) {
        Set<Variable> fvs = new HashSet<Variable>();
        Set<Variable> bvs = new HashSet<Variable>();

        // extract left term
        List operands = arg0.listOperands();
        if (operands.size() > 0) {
            Molecule m = (Molecule) operands.get(0);
            Term t = m.getLeftParameter();
            internalDoTerm(t, fvs, bvs);
        }

        for (Iterator i = operands.iterator(); i.hasNext();) {
            Molecule m = (Molecule) i.next();
            internalDoTerm(m.getRightParameter(), fvs, bvs);
            if (m instanceof AttributeMolecule) {
                AttributeMolecule am = (AttributeMolecule) m;
                internalDoTerm(am.getAttribute(), fvs, bvs);
            }
        }
        freeVars.put(arg0, fvs);
        boundVars.put(arg0, bvs);
    }

    @Override
    public void handleSubConceptMolecule(SubConceptMolecule arg0) {
        internalHandleMolecule(arg0);
    }

    @Override
    public void handleMemberShipMolecule(MembershipMolecule arg0) {
        internalHandleMolecule(arg0);
    }

    @Override
    public void handleAttributeConstraintMolecule(
            AttributeConstraintMolecule arg0) {
        internalHandleMolecule(arg0);
    }

    @Override
    public void handleAttributeInferenceMolecule(AttributeInferenceMolecule arg0) {
        internalHandleMolecule(arg0);
    }

    @Override
    public void handleAttributeValueMolecule(AttributeValueMolecule arg0) {
        internalHandleMolecule(arg0);
    }

    private void internalHandleMolecule(Molecule m) {
        Set<Variable> fvs = new HashSet<Variable>();
        Set<Variable> bvs = new HashSet<Variable>();
        internalDoTerm(m.getLeftParameter(), fvs, bvs);
        internalDoTerm(m.getRightParameter(), fvs, bvs);
        if (m instanceof AttributeMolecule) {
            AttributeMolecule am = (AttributeMolecule) m;
            internalDoTerm(am.getAttribute(), fvs, bvs);
        }
        freeVars.put(m, fvs);
        boundVars.put(m, bvs);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.wsml.reasoner.normalization.PostfixOrderLogicalExpressionVisitor#handleNaf(org.omwg.logicalexpression.Unary)
     */
    @Override
    public void handleNegationAsFailure(NegationAsFailure arg0) {
        internalDoCompound(arg0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.wsml.reasoner.normalization.PostfixOrderLogicalExpressionVisitor#handleNeg(org.omwg.logicalexpression.Unary)
     */
    @Override
    public void handleNegation(Negation arg0) {
        internalDoCompound(arg0);
    }

    @Override
    public void handleDisjunction(Disjunction arg0) {
        internalDoCompound(arg0);
    }

    @Override
    public void handleConjunction(Conjunction arg0) {
        internalDoCompound(arg0);
    }

    private void internalDoCompound(CompoundExpression arg0) {
        Set<Variable> fvs = new HashSet<Variable>();
        Set<Variable> bvs = new HashSet<Variable>();

        for (Iterator i = arg0.listOperands().iterator(); i.hasNext();) {
            LogicalExpression expr = (LogicalExpression) i.next();
            fvs.addAll(freeVars.get(expr));
            bvs.addAll(freeVars.get(expr));
        }
        freeVars.put(arg0, fvs);
        boundVars.put(arg0, bvs);
    }

    private void internalDoQuantified(Quantified arg0) {
        Set<Variable> fvs = new HashSet<Variable>();
        Set<Variable> bvs = new HashSet<Variable>();

        fvs.addAll(freeVars.get(arg0.getOperand()));
        fvs.removeAll(arg0.listVariables());

        bvs.addAll(boundVars.get(arg0.getOperand()));
        bvs.addAll(arg0.listVariables());

        freeVars.put(arg0, fvs);
        boundVars.put(arg0, bvs);

    }

    private void internalDoTerm(Term t, Set<Variable> fvs, Set<Variable> bvs) {
        tvv.reset();
        t.accept(tvv);
        fvs.addAll(tvv.getVariables());
    }

}
