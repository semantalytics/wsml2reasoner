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
import java.util.Map;
import java.util.Set;

import org.omwg.logicalexpression.Atom;
import org.omwg.logicalexpression.Binary;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.Molecule;
import org.omwg.logicalexpression.Quantified;
import org.omwg.logicalexpression.Unary;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.transformation.PostfixOrderLogicalExpressionVisitor;



/** 
 * A visitor that analyses variable occurrences in logical expressions.
 * After visiting a logical expression le, it can give the set 
 * FreeVars(le) of free variables in le and the set BoundVars(le) of
 * bound variables in le.
 * 
 * @author Uwe Keller, DERI Innsbruck
 */
public class LogicalExpressionVariableVisitor extends
        PostfixOrderLogicalExpressionVisitor {

    private Map<LogicalExpression,Set<Variable>> freeVars;
    private Map<LogicalExpression,Set<Variable>> boundVars;
    
    private TermVariableVisitor tvv;
    
    
    public LogicalExpressionVariableVisitor() {
        super();
        reset();
    }

    /**
     * Resets the internal state of the visitor such that the object
     * can be reused across several calls in the same thread.
     *
     */
    public void reset(){
        freeVars = new HashMap<LogicalExpression, Set<Variable>>();
        boundVars = new HashMap<LogicalExpression, Set<Variable>>();
        tvv = new TermVariableVisitor();
    }
    
    /**
     * Not used!
     * @return null.
     */
    public Object getSerializedObject() {
        return null;
    }
    
    /**
     * Returns the set of all free variables in the given subexpression of the 
     * visited logical expression.
     * @param some subexpression of the visited expression
     * @return FreeVar(le) or null if le is not a subexpression of the visited expression.
     */
    public Set<Variable> getFreeVariables(LogicalExpression le){
        Set<Variable> result = freeVars.get(le);
        return result;
    }
    
    /**
     * Returns the set of all bound variables in the given subexpression of the 
     * visited logical expression.
     * @param some subexpression of the visited expression
     * @return FreeVar(le) or null if le is not a subexpression of the visited expression.
     */
    public Set<Variable> getBoundVariables(LogicalExpression le){
        Set<Variable> result = boundVars.get(le);
        return result;
    }

   
    /* (non-Javadoc)
     * @see org.wsml.reasoner.normalization.PostfixOrderLogicalExpressionVisitor#handleAtom(org.omwg.logicalexpression.Atom)
     */
    @Override
    public void handleAtom(Atom arg0) {
        Set<Variable> fvs = new HashSet<Variable>();
        Set<Variable> bvs = new HashSet<Variable>();
        
        for(int i = 0; i < arg0.getArity(); i++){
            Term nextArg = arg0.getParameter(i);
            internalDoTerm(nextArg,fvs,bvs);
        }
        
        freeVars.put(arg0, fvs);
        boundVars.put(arg0, bvs);
    }

    /* (non-Javadoc)
     * @see org.wsml.reasoner.normalization.PostfixOrderLogicalExpressionVisitor#handleConstraint(org.omwg.logicalexpression.Unary)
     */
    @Override
    public void handleConstraint(Unary arg0) {
        internalDoUnary(arg0);
    }

    /* (non-Javadoc)
     * @see org.wsml.reasoner.normalization.PostfixOrderLogicalExpressionVisitor#handleEquivalent(org.omwg.logicalexpression.Binary)
     */
    @Override
    public void handleEquivalent(Binary arg0) {
        internalDoBinary(arg0);
    }

    /* (non-Javadoc)
     * @see org.wsml.reasoner.normalization.PostfixOrderLogicalExpressionVisitor#handleExists(org.omwg.logicalexpression.Quantified)
     */
    @Override
    public void handleExists(Quantified arg0) {
        internalDoQuantified(arg0);
    }

    /* (non-Javadoc)
     * @see org.wsml.reasoner.normalization.PostfixOrderLogicalExpressionVisitor#handleForall(org.omwg.logicalexpression.Quantified)
     */
    @Override
    public void handleForall(Quantified arg0) {
        internalDoQuantified(arg0);
    }

    /* (non-Javadoc)
     * @see org.wsml.reasoner.normalization.PostfixOrderLogicalExpressionVisitor#handleImpliedBy(org.omwg.logicalexpression.Binary)
     */
    @Override
    public void handleImpliedBy(Binary arg0) {
        internalDoBinary(arg0);
    }

    /* (non-Javadoc)
     * @see org.wsml.reasoner.normalization.PostfixOrderLogicalExpressionVisitor#handleImplies(org.omwg.logicalexpression.Binary)
     */
    @Override
    public void handleImplies(Binary arg0) {
        internalDoBinary(arg0);
    }

    /* (non-Javadoc)
     * @see org.wsml.reasoner.normalization.PostfixOrderLogicalExpressionVisitor#handleImpliesLP(org.omwg.logicalexpression.Binary)
     */
    @Override
    public void handleImpliesLP(Binary arg0) {
        internalDoBinary(arg0);
    }

    /* (non-Javadoc)
     * @see org.wsml.reasoner.normalization.PostfixOrderLogicalExpressionVisitor#handleMolecule(org.omwg.logicalexpression.Molecule)
     */
    @Override
    public void handleMolecule(Molecule arg0) {
        Set<Variable> fvs = new HashSet<Variable>();
        Set<Variable> bvs = new HashSet<Variable>();

        Term t = arg0.getTerm();
        
        internalDoTerm(t, fvs, bvs);
       
        Set superConcepts =   arg0.listSubConceptOf();
        Set memberOfClasses = arg0.listMemberOf();
        Set attributeSpecs =  arg0.listAttributeSpecifications();
        
        // Superconcepts part ...
        
        if (superConcepts != null && superConcepts.size() > 0) {
            for (Object sc : superConcepts){
                Term scTerm = (Term) sc;
                internalDoTerm(scTerm, fvs, bvs);
            }
        }
        
        // MemberOf part ...
        
        if (memberOfClasses != null && memberOfClasses.size() > 0) {
            for (Object moc : memberOfClasses){
                Term mocTerm = (Term) moc;
                internalDoTerm(mocTerm, fvs, bvs);
            }
        }
        
        // AttributeSpecs part ...
        
        if (attributeSpecs != null && attributeSpecs.size() > 0) {
            for (Object attSpec : attributeSpecs){
                internalDoAttributeSpecification((AttrSpecification) attSpec, fvs, bvs);
            }
        }
        
        
        freeVars.put(arg0, fvs);
        boundVars.put(arg0, bvs);
    }

    /* (non-Javadoc)
     * @see org.wsml.reasoner.normalization.PostfixOrderLogicalExpressionVisitor#handleNaf(org.omwg.logicalexpression.Unary)
     */
    @Override
    public void handleNaf(Unary arg0) {
        internalDoUnary(arg0);
    }

    /* (non-Javadoc)
     * @see org.wsml.reasoner.normalization.PostfixOrderLogicalExpressionVisitor#handleNeg(org.omwg.logicalexpression.Unary)
     */
    @Override
    public void handleNeg(Unary arg0) {
        internalDoUnary(arg0);
    }

    /* (non-Javadoc)
     * @see org.wsml.reasoner.normalization.PostfixOrderLogicalExpressionVisitor#handleOr(org.omwg.logicalexpression.Binary)
     */
    @Override
    public void handleOr(Binary arg0) {
        internalDoBinary(arg0);
    }
    
   // Some helper methods
    
    /* (non-Javadoc)
     * @see org.wsml.reasoner.normalization.PostfixOrderLogicalExpressionVisitor#handleAnd(org.omwg.logicalexpression.Binary)
     */
    @Override
    public void handleAnd(Binary arg0) {
        internalDoBinary(arg0);
    }

    private void internalDoBinary(Binary arg0){
        Set<Variable> fvs = new HashSet<Variable>();
        Set<Variable> bvs = new HashSet<Variable>();
        
        fvs.addAll(freeVars.get(arg0.getArgument(0)));
        fvs.addAll(freeVars.get(arg0.getArgument(1)));
        
        bvs.addAll(boundVars.get(arg0.getArgument(0)));
        bvs.addAll(boundVars.get(arg0.getArgument(1)));
        
        
        freeVars.put(arg0, fvs);
        boundVars.put(arg0, bvs);
    }
    
    private void internalDoUnary(Unary arg0){
        freeVars.put(arg0, freeVars.get(arg0.getArgument(0)));
        boundVars.put(arg0, boundVars.get(arg0.getArgument(0)));
    }
    
    private void internalDoQuantified(Quantified arg0){
        Set<Variable> fvs = new HashSet<Variable>();
        Set<Variable> bvs = new HashSet<Variable>();
             
        fvs.addAll(freeVars.get(arg0.getArgument(0)));
        fvs.removeAll(arg0.listVariables());
        
        bvs.addAll(boundVars.get(arg0.getArgument(0)));
        bvs.addAll(arg0.listVariables());
        
        freeVars.put(arg0, fvs);
        boundVars.put(arg0, bvs);
        
    }
    
    private void internalDoTerm(Term t, Set<Variable> fvs, Set<Variable> bvs){
        tvv.reset();
        t.accept(tvv);
        fvs.addAll(tvv.getVariables());
    }
    
    private void internalDoAttributeSpecification(AttrSpecification attSpec, Set<Variable> fvs, Set<Variable> bvs){
        Term theAttribute = attSpec.getName();
        internalDoTerm(theAttribute, fvs, bvs);
        
        Set theValues = attSpec.listArguments();
                
        for (Object attVal : theValues){
            Term valTerm = (Term) attVal;
            internalDoTerm(valTerm, fvs, bvs);
        }
    }
    
    

    
}
