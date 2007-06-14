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

import org.omwg.logicalexpression.*;

/**
 * Default left to right depth first walker...
 *   
 * @author Holger Lausen
 * @version $Revision: 1.1 $ $Date: 2007-06-14 16:38:59 $
 * @see org.omwg.logicalexpression.Visitor
 */
public class SpassPlusTSerializeVisitor extends FOLAbstractSerializeVisitor {
    

    /**
     * Builds a String representing the Unary expression and adds it to a vector.
     * @param expr Unary Expression to be serialized, with operator NEG
     * @see org.deri.wsmo4j.logicalexpression.AbstractVisitor#visitNegation(Negation)
     */
    public void visitNegation(Negation expr) {
        expr.getOperand().accept(this);
        stack.add("not (" + (String)stack.remove(stack.size() - 1) + ")");
    }


    /**
     * Builds a String representing the Binary expression and adds it to a vector.
     * @param expr Binary Expression to be serialized, with operator AND
     * @see org.deri.wsmo4j.logicalexpression.AbstractVisitor#visitConjunction(Conjunction)
     */
    public void visitConjunction(Conjunction expr) {
        expr.getLeftOperand().accept(this);
        expr.getRightOperand().accept(this);
        stack.add(" and (" + stack.remove(stack.size() - 2) + " , " + stack.remove(stack.size() - 1)+ ")");
    }

    public void visitImplication(Implication expr) {
        expr.getLeftOperand().accept(this);
        expr.getRightOperand().accept(this);
        stack.add(" implies(" + stack.remove(stack.size() - 2) + ", " +
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
        stack.add(" or(" + stack.remove(stack.size() - 2) + " , " + stack.remove(stack.size() - 1) +")");
    }

    /**
     * Builds a String representing the Quantified expression and adds it to a vector.
     * @param expr Quantified Expression to be serialized, with operator FORALL
     * @see org.deri.wsmo4j.logicalexpression.AbstractVisitor#visitUniversalQuantification(UniversalQuantification)
     */
    public void visitUniversalQuantification(UniversalQuantification expr) {
        String res = helpQuantified(expr);
        stack.add(" forall (" + res + " , " + (String)stack.remove(stack.size() - 1) + " )");
    }

    /**
     * Builds a String representing the Quantified expression and adds it to a vector.
     * @param expr Quantified Expression to be serialized, with operator EXISTS
     * @see org.deri.wsmo4j.logicalexpression.AbstractVisitor#visitExistentialQuantification(ExistentialQuantification)
     */
    public void visitExistentialQuantification(ExistentialQuantification expr) {
        String res = helpQuantified(expr);
        stack.add(" exists " + res + " : ( " + (String)stack.remove(stack.size() - 1) + " )");
    }

    public void visitEquivalence(Equivalence expr) {
        expr.getLeftOperand().accept(this);
        expr.getRightOperand().accept(this);
        stack.add(" equiv(" + stack.remove(stack.size() - 2) + " , " +
                       stack.remove(stack.size() - 1) + ")");

    }

    public void visitInverseImplication(InverseImplication expr) {
        expr.getLeftOperand().accept(this);
        expr.getRightOperand().accept(this);
        stack.add(" implies (" + stack.remove(stack.size() - 1) + " , " +
                       stack.remove(stack.size() - 1) + ")");

    }
}