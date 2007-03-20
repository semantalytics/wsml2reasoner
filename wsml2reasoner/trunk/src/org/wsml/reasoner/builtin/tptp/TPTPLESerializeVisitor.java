/*
 * Copyright (c) 2006, University of Innsbruck, Austria.
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
 */

package org.wsml.reasoner.builtin.tptp;

import java.util.*;

import org.deri.wsmo4j.logicalexpression.ConstantTransformer;
import org.omwg.logicalexpression.*;
import org.omwg.logicalexpression.terms.Term;
import org.wsmo.common.TopEntity;

/**
 * Default left to right depth first walker...
 *   
 * @author Holger Lausen
 * @version $Revision: 1.2 $ $Date: 2007-03-20 20:30:53 $
 * @see org.omwg.logicalexpression.Visitor
 */
public class TPTPLESerializeVisitor implements Visitor {
    
    private Vector<String> stack;
    private TPTPTermSerializer visitor;

    /**
     * @param nsC TopEntity
     * @see org.deri.wsmo4j.io.serializer.wsml.LogExprSerializerWSML#LogExprSerializerWSML(TopEntity)
     */
    public TPTPLESerializeVisitor() {
        visitor = new TPTPTermSerializer();
        stack = new Vector<String>();
    }

    /**
     * Builds a String representing the Atom and adds it to a vector.
     * @param expr Atom to be serialized
     * @see org.deri.wsmo4j.logicalexpression.AbstractVisitor#visitAtom(Atom)
     */
    public void visitAtom(Atom expr) {
        Term predicateSymbol = expr.getIdentifier();
        //put on term stack
        predicateSymbol.accept(visitor);
        
        String s = visitor.getSerializedObject();
        //one might get something like + back so replace this to http://...#numericAdd
        String realIRI = ConstantTransformer.getInstance().findIri(s);
        
        int nbParams = expr.getArity();
        if (nbParams > 0) {
            s = s + "(";
            for (int i = 0; i < nbParams; i++) {
                expr.getParameter(i).accept(visitor);
                s = s + visitor.getSerializedObject();
                if (i + 1 < nbParams) {
                    s = s + ",";
                }
            }
            s = s + ")";
        }
        stack.add(s);
    }

    /**
     * Builds a String representing the Unary expression and adds it to a vector.
     * @param expr Unary Expression to be serialized, with operator NEG
     * @see org.deri.wsmo4j.logicalexpression.AbstractVisitor#visitNegation(Negation)
     */
    public void visitNegation(Negation expr) {
        expr.getOperand().accept(this);
        stack.add("~ (" + (String)stack.remove(stack.size() - 1) + ")");
    }


    /**
     * Builds a String representing the Binary expression and adds it to a vector.
     * @param expr Binary Expression to be serialized, with operator AND
     * @see org.deri.wsmo4j.logicalexpression.AbstractVisitor#visitConjunction(Conjunction)
     */
    public void visitConjunction(Conjunction expr) {
        expr.getLeftOperand().accept(this);
        expr.getRightOperand().accept(this);
        stack.add("(" + stack.remove(stack.size() - 2) + "  & " + stack.remove(stack.size() - 1)+ ")");
    }

 


    public void visitImplication(Implication expr) {
        expr.getLeftOperand().accept(this);
        expr.getRightOperand().accept(this);
        stack.add("(" + stack.remove(stack.size() - 2) + " => " +
                       stack.remove(stack.size() - 1) + ")");
    }

    /**
     * Builds a String representing the Binary expression and adds it to a vector.
     * @param expr Binary Expression to be serialized, with operator OR
     * @see org.deri.wsmo4j.logicalexpression.AbstractVisitor#visitDisjunction(Disjunction)
     */
    public void visitDisjunction(Disjunction expr) {
        expr.getLeftOperand().accept(this);
        expr.getRightOperand().accept(this);
        stack.add("(" + stack.remove(stack.size() - 2) + " | " + stack.remove(stack.size() - 1) +")");
    }

    /**
     * Builds a String representing the Quantified expression and adds it to a vector.
     * @param expr Quantified Expression to be serialized, with operator FORALL
     * @see org.deri.wsmo4j.logicalexpression.AbstractVisitor#visitUniversalQuantification(UniversalQuantification)
     */
    public void visitUniversalQuantification(UniversalQuantification expr) {
        String res = helpQuantified(expr);
        stack.add("! " + res + " : ( " + (String)stack.remove(stack.size() - 1) + " )");
    }

    /**
     * Builds a String representing the Quantified expression and adds it to a vector.
     * @param expr Quantified Expression to be serialized, with operator EXISTS
     * @see org.deri.wsmo4j.logicalexpression.AbstractVisitor#visitExistentialQuantification(ExistentialQuantification)
     */
    public void visitExistentialQuantification(ExistentialQuantification expr) {
        String res = helpQuantified(expr);
        stack.add("? " + res + " : ( " + (String)stack.remove(stack.size() - 1) + " )");
    }

    /**
     * All serialized elements are added to a vector. This method removes the
     * first serialized object from this vector and shifts any subsequent
     * objects to the left (subtracts one from their indices).
     * @return the serialized String object that is the first element in this vector
     */
    public String getSerializedObject() {
        return stack.remove(0);
    }

    

    /**
     * Builds a String representing the Quantified Expression
     * @param expr Quantified Expression to be serialized, with operator EXISTS
     * @return String representing serialized Quantified Expression
     */
    private String helpQuantified(Quantified expr) {
        String res = "";
        Set s = expr.listVariables();
        Iterator i = s.iterator();
        res = res + "[";
        while (i.hasNext()) {
            ((Term)i.next()).accept(visitor);
            res = res + (String)visitor.getSerializedObject();
            if (i.hasNext()) {
                res = res + ",";
            }
        }
        res = res + "]";
        expr.getOperand().accept(this);
        return res;
    }
    
    
    public void visitNegationAsFailure(NegationAsFailure expr) {
        throw new RuntimeException("no constraints should be here!!!!");
    }
    
    public void visitConstraint(Constraint expr) {
        throw new RuntimeException("no constraints should be here!!!!");
    }
    
    public void visitEquivalence(Equivalence expr) {
        expr.getLeftOperand().accept(this);
        expr.getRightOperand().accept(this);
        stack.add("(" + stack.remove(stack.size() - 2) + " <=> " +
                       stack.remove(stack.size() - 1) + ")");

    }

    public void visitInverseImplication(InverseImplication expr) {
        expr.getLeftOperand().accept(this);
        expr.getRightOperand().accept(this);
        stack.add("(" + stack.remove(stack.size() - 2) + " <= " +
                       stack.remove(stack.size() - 1) + ")");

    }

    public void visitLogicProgrammingRule(LogicProgrammingRule expr) {
        throw new RuntimeException("should not be here anymore!");
    }
    
    public void visitCompoundMolecule(CompoundMolecule expr) {
        throw new RuntimeException("should not be here anymore!");
    }
    
    public void visitAttributeContraintMolecule(AttributeConstraintMolecule expr) {
        throw new RuntimeException("should not be here anymore!");
    }

    public void visitAttributeInferenceMolecule(AttributeInferenceMolecule expr) {
        throw new RuntimeException("should not be here anymore!");
    }

    public void visitAttributeValueMolecule(AttributeValueMolecule expr) {
        throw new RuntimeException("should not be here anymore!");
    }

    public void visitMemberShipMolecule(MembershipMolecule expr) {
        throw new RuntimeException("should not be here anymore!");
    }

    public void visitSubConceptMolecule(SubConceptMolecule expr) {
        throw new RuntimeException("should not be here anymore!");

    }


}