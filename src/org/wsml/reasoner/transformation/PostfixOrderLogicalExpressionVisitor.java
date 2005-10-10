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

import org.omwg.logicalexpression.*;

/**
 * Implements a left-first, depth-first traversal over logical expressions.
 * The point in time when a node has to be handled according to the traversal order
 * is defined by the postfix order.
 * 
 * Subclasses should be visitors of logical expressions that 
 * don't need to care about how to traverse nodes but overwrite the new kinds of 
 * methods whose default implementation is empty:
 * 
 * (1) 'handleXXX' methods, that are called by the abstract traversal implementation
 *  when the respective node is actually to be handeled (according to the 
 *  traversal order)
 *  
 * (2) 'enterXXX' methods, that are called by the abstract traversal implementation
 *  when the respective subtree rooted in the given node is entered (according to the 
 *  traversal order)
 * 
 * (3) 'leaveXXX' methods, that are called by the abstract traversal implementation
 *  when the respective subtree rooted in the given node is left (according to the 
 *  traversal order)
 * 
 * @author Uwe Keller, DERI Innsbruck
 */
public abstract class PostfixOrderLogicalExpressionVisitor implements Visitor {

    public void visitAtom(Atom arg0) {
        enterAtom(arg0);
        handleAtom(arg0);
        leaveAtom(arg0);
    }

    public void visitMolecule(Molecule arg0) {
        enterMolecule(arg0);
        handleMolecule(arg0);
        leaveMolecule(arg0);
    }

    public void visitNeg(Unary arg0) {
        enterNeg(arg0);
        arg0.getArgument(0).accept(this);
        handleNeg(arg0);
        leaveNeg(arg0);
    }

    public void visitNaf(Unary arg0) {
        enterNaf(arg0);
        arg0.getArgument(0).accept(this);
        handleNaf(arg0);
        leaveNaf(arg0);
    }

    public void visitConstraint(Unary arg0) {
        enterConstraint(arg0);
        arg0.getArgument(0).accept(this);
        handleConstraint(arg0);
        leaveConstraint(arg0);
    }

    public void visitAnd(Binary arg0) {
        enterAnd(arg0);
        arg0.getArgument(0).accept(this);
        arg0.getArgument(1).accept(this);
        handleAnd(arg0);
        leaveAnd(arg0);
    }

    public void visitOr(Binary arg0) {
        enterOr(arg0);
        arg0.getArgument(0).accept(this);
        arg0.getArgument(1).accept(this);
        handleOr(arg0);
        leaveOr(arg0);
    }

    public void visitImpliedBy(Binary arg0) {
        enterImpliedBy(arg0);
        arg0.getArgument(0).accept(this);
        arg0.getArgument(1).accept(this);
        handleImpliedBy(arg0);
        leaveImpliedBy(arg0);
    }

    public void visitImplies(Binary arg0) {
        enterImplies(arg0);
        arg0.getArgument(0).accept(this);
        arg0.getArgument(1).accept(this);
        handleImplies(arg0);
        leaveImplies(arg0);
    }

    public void visitEquivalent(Binary arg0) {
        enterEquivalent(arg0);
        arg0.getArgument(0).accept(this);
        arg0.getArgument(1).accept(this);
        handleEquivalent(arg0);
        leaveEquivalent(arg0);
    }

    public void visitImpliesLP(Binary arg0) {
        enterImpliesLP(arg0);
        arg0.getArgument(0).accept(this);
        arg0.getArgument(1).accept(this);
        handleImpliesLP(arg0);
        leaveImpliesLP(arg0);
    }

    public void visitForall(Quantified arg0) {
        enterForall(arg0);
        arg0.getArgument(0).accept(this);
        handleForall(arg0);
        leaveForall(arg0);
    }

    public void visitExists(Quantified arg0) {
        enterExists(arg0);
        arg0.getArgument(0).accept(this);
        handleExists(arg0);
        leaveExists(arg0);
    }

    abstract public Object getSerializedObject();
    
    public void handleAtom(Atom arg0){}
    public void handleMolecule(Molecule arg0){}
    public void handleNeg(Unary arg0) {}
    public void handleNaf(Unary arg0) {}
    public void handleConstraint(Unary arg0) {}
    public void handleAnd(Binary arg0) {}
    public void handleOr(Binary arg0) {}
    public void handleImpliedBy(Binary arg0) {}
    public void handleImplies(Binary arg0) {}
    public void handleEquivalent(Binary arg0) {}
    public void handleImpliesLP(Binary arg0) {}
    public void handleForall(Quantified arg0) {}
    public void handleExists(Quantified arg0) {}
    
    public void enterAtom(Atom arg0){}
    public void enterMolecule(Molecule arg0){}
    public void enterNeg(Unary arg0) {}
    public void enterNaf(Unary arg0) {}
    public void enterConstraint(Unary arg0) {}
    public void enterAnd(Binary arg0) {}
    public void enterOr(Binary arg0) {}
    public void enterImpliedBy(Binary arg0) {}
    public void enterImplies(Binary arg0) {}
    public void enterEquivalent(Binary arg0) {}
    public void enterImpliesLP(Binary arg0) {}
    public void enterForall(Quantified arg0) {}
    public void enterExists(Quantified arg0) {}

    public void leaveAtom(Atom arg0){}
    public void leaveMolecule(Molecule arg0){}
    public void leaveNeg(Unary arg0) {}
    public void leaveNaf(Unary arg0) {}
    public void leaveConstraint(Unary arg0) {}
    public void leaveAnd(Binary arg0) {}
    public void leaveOr(Binary arg0) {}
    public void leaveImpliedBy(Binary arg0) {}
    public void leaveImplies(Binary arg0) {}
    public void leaveEquivalent(Binary arg0) {}
    public void leaveImpliesLP(Binary arg0) {}
    public void leaveForall(Quantified arg0) {}
    public void leaveExists(Quantified arg0) {}

    public void visitCompoundMolecule(CompoundMolecule arg0) {
        // TODO Auto-generated method stub
        
    }

    public void visitSubConceptMolecule(SubConceptMolecule arg0) {
        // TODO Auto-generated method stub
        
    }

    public void visitMemberShipMolecule(MembershipMolecule arg0) {
        // TODO Auto-generated method stub
        
    }

    public void visitAttributeValueMolecule(AttributeValueMolecule arg0) {
        // TODO Auto-generated method stub
        
    }

    public void visitAttributeContraintMolecule(AttributeConstraintMolecule arg0) {
        // TODO Auto-generated method stub
        
    }

    public void visitAttributeInferenceMolecule(AttributeInferenceMolecule arg0) {
        // TODO Auto-generated method stub
        
    }

    public void visitNegation(Negation arg0) {
        // TODO Auto-generated method stub
        
    }

    public void visitNegationAsFailure(NegationAsFailure arg0) {
        // TODO Auto-generated method stub
        
    }

    public void visitConstraint(Constraint arg0) {
        // TODO Auto-generated method stub
        
    }

    public void visitConjunction(Conjunction arg0) {
        // TODO Auto-generated method stub
        
    }

    public void visitDisjunction(Disjunction arg0) {
        // TODO Auto-generated method stub
        
    }

    public void visitInverseImplication(InverseImplication arg0) {
        // TODO Auto-generated method stub
        
    }

    public void visitImplication(Implication arg0) {
        // TODO Auto-generated method stub
        
    }

    public void visitEquivalence(Equivalence arg0) {
        // TODO Auto-generated method stub
        
    }

    public void visitLogicProgrammingRule(LogicProgrammingRule arg0) {
        // TODO Auto-generated method stub
        
    }

    public void visitUniversalQuantification(UniversalQuantification arg0) {
        // TODO Auto-generated method stub
        
    }

    public void visitExistentialQuantification(ExistentialQuantification arg0) {
        // TODO Auto-generated method stub
        
    }
}
