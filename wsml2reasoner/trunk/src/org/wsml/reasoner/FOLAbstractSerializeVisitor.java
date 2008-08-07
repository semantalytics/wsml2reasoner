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

package org.wsml.reasoner;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.omwg.logicalexpression.Atom;
import org.omwg.logicalexpression.AttributeConstraintMolecule;
import org.omwg.logicalexpression.AttributeInferenceMolecule;
import org.omwg.logicalexpression.AttributeValueMolecule;
import org.omwg.logicalexpression.CompoundMolecule;
import org.omwg.logicalexpression.Constraint;
import org.omwg.logicalexpression.LogicProgrammingRule;
import org.omwg.logicalexpression.MembershipMolecule;
import org.omwg.logicalexpression.NegationAsFailure;
import org.omwg.logicalexpression.Quantified;
import org.omwg.logicalexpression.SubConceptMolecule;
import org.omwg.logicalexpression.Visitor;
import org.omwg.logicalexpression.terms.Term;
import org.wsml.reasoner.builtin.tptp.TPTPSymbolMap;
import org.wsml.reasoner.builtin.tptp.TPTPTermSerializer;
import org.wsmo.common.TopEntity;

/**
 * Default left to right depth first walker...
 * 
 * @author Holger Lausen
 * @version $Revision: 1.1 $ $Date: 2007-08-10 09:44:49 $
 * @see org.omwg.logicalexpression.Visitor
 */
public abstract class FOLAbstractSerializeVisitor implements Visitor {
    protected Map<Term, Term> atoms2Rewrite = new HashMap<Term, Term>();

    protected Vector<String> stack;

    TPTPTermSerializer visitor;

    /**
     * @param nsC
     *            TopEntity
     * @see org.deri.wsmo4j.io.serializer.wsml.LogExprSerializerWSML#LogExprSerializerWSML(TopEntity)
     */
    public FOLAbstractSerializeVisitor() {
        visitor = new TPTPTermSerializer();
        stack = new Vector<String>();
        visitor.setAtoms2ConstructedTerms(atoms2Rewrite);
    }

    /**
     * Builds a String representing the Atom and adds it to a vector.
     * 
     * @param expr
     *            Atom to be serialized
     * @see org.deri.wsmo4j.logicalexpression.AbstractVisitor#visitAtom(Atom)
     */
    public void visitAtom(Atom expr) {
        Term predicateSymbol = expr.getIdentifier();
        // put on term stack
        predicateSymbol.accept(visitor);

        String s = visitor.getSerializedObject();
        // one might get something like + back so replace this to
        // http://...#numericAdd

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
     * All serialized elements are added to a vector. This method removes the
     * first serialized object from this vector and shifts any subsequent
     * objects to the left (subtracts one from their indices).
     * 
     * @return the serialized String object that is the first element in this
     *         vector
     */
    public String getSerializedObject() {
        return stack.remove(0);
    }

    /**
     * Builds a String representing the Quantified Expression
     * 
     * @param expr
     *            Quantified Expression to be serialized, with operator EXISTS
     * @return String representing serialized Quantified Expression
     */
    protected String helpQuantified(Quantified expr) {
        String res = "";
        Set s = expr.listVariables();
        Iterator i = s.iterator();
        res = res + "[";
        while (i.hasNext()) {
            ((Term) i.next()).accept(visitor);
            res = res + visitor.getSerializedObject();
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

    public TPTPSymbolMap getSymbolMap() {
        return visitor.getSymbolMap();
    }

    public void setSymbolMap(TPTPSymbolMap map) {
        visitor.SetSymbolMap(map);
    }
}