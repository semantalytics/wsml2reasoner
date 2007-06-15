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

import java.util.LinkedList;
import java.util.List;

import org.deri.wsmo4j.io.parser.wsml.TempVariable;
import org.deri.wsmo4j.logicalexpression.terms.ConstructedTermImpl;
import org.omwg.logicalexpression.*;
import org.omwg.logicalexpression.terms.Term;
import org.wsmo.common.IRI;

/**
 * Default left to right depth first walker...
 *   
 * @author Holger Lausen
 * @version $Revision: 1.2 $ $Date: 2007-06-15 10:23:38 $
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
    	 //check functionsymbols to rewrite
        if (expr.getRightOperand() instanceof Atom){
            Atom a = (Atom)expr.getRightOperand();
            if (a.getArity()>0 && a.getParameter(0) instanceof TempVariable){
                List <Term> params = new LinkedList <Term>(a.listParameters());
                params.remove(0);
                Term key = a.getParameter(0);
                Term subst = new ConstructedTermImpl((IRI)a.getIdentifier(), params);
                atoms2Rewrite.put(a.getParameter(0),subst);
                expr.getLeftOperand().accept(this);
                return;
            }
        }
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
        stack.add(" forall (" + res + "," + (String)stack.remove(stack.size() - 1) + " )");
    }

    /**
     * Builds a String representing the Quantified expression and adds it to a vector.
     * @param expr Quantified Expression to be serialized, with operator EXISTS
     * @see org.deri.wsmo4j.logicalexpression.AbstractVisitor#visitExistentialQuantification(ExistentialQuantification)
     */
    public void visitExistentialQuantification(ExistentialQuantification expr) {
        String res = helpQuantified(expr);
        stack.add(" exists(" + res + "," + (String)stack.remove(stack.size() - 1) + " )");
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