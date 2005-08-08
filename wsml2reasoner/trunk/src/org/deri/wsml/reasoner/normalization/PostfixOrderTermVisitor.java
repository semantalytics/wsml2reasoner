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

import org.omwg.logexpression.terms.*;



/**
 * Implements a left-first, depth-first traversal over terms.
 * The point in time when a node has to be handled according to the traversal order
 * is defined by the postfix order.
 * 
 * Subclasses should be visitors of terms that 
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
public class PostfixOrderTermVisitor implements VisitorTerms {

    public void visitConstructedTerm(ConstructedTerm arg0) {
        enterConstructedTerm(arg0);
        for(int i=0; i < arg0.getArity(); i++){
            Term nextArg = arg0.getParameter(i);
            nextArg.accept(this);
        }
        handleConstructedTerm(arg0);
        leaveConstructedTerm(arg0);

    }

    public void visitVariable(Variable arg0) {
        enterVariable(arg0);
        handleVariable(arg0);
        leaveVariable(arg0);
    }

    public void visitString(WSMLString arg0) {
        enterString(arg0);
        handleString(arg0);
        leaveString(arg0);

    }

    public void visitInteger(WSMLInteger arg0) {
        enterInteger(arg0);
        handleInteger(arg0);
        leaveInteger(arg0);

    }

    public void visitDecimal(WSMLDecimal arg0) {
        enterDecimal(arg0);
        handleDecimal(arg0);
        leaveDecimal(arg0);
    }
 
   
    public void visitAnonymousID(AnonymousID arg0) {
        enterAnonymousID(arg0);
        handleAnonymousID(arg0);
        leaveAnonymousID(arg0);        
    }
    
    public void visitNbAnonymousID(NbAnonymousID arg0) {
        enterNbAnonymousID(arg0);
        handleNbAnonymousID(arg0);
        leaveNbAnonymousID(arg0);
    }

    public void visitIRI(IRI arg0) {
        enterIRI(arg0);
        handleIRI(arg0);
        leaveIRI(arg0);
    }
    
    

    public Object getSerializedObject() {
        return null;
    }

    
    // -- Additional methods ...
    
    public void enterConstructedTerm(ConstructedTerm arg0) {}
    
    public void enterVariable(Variable arg0) {}
    
    public void enterString(WSMLString arg0) {}
    
    public void enterInteger(WSMLInteger arg0) {}
    
    public void enterDecimal(WSMLDecimal arg0) {}

    public void enterAnonymousID(AnonymousID arg0) {}

    public void enterNbAnonymousID(NbAnonymousID arg0) {}

    public void enterIRI(IRI arg0) {}
    
    
    
    public void handleConstructedTerm(ConstructedTerm arg0) {}
    
    public void handleVariable(Variable arg0) {}
    
    public void handleString(WSMLString arg0) {}
    
    public void handleInteger(WSMLInteger arg0) {}
    
    public void handleDecimal(WSMLDecimal arg0) {}

    public void handleAnonymousID(AnonymousID arg0) {}

    public void handleNbAnonymousID(NbAnonymousID arg0) {}

    public void handleIRI(IRI arg0) {}
    
    
    public void leaveConstructedTerm(ConstructedTerm arg0) {}
    
    public void leaveVariable(Variable arg0) {}
    
    public void leaveString(WSMLString arg0) {}
    
    public void leaveInteger(WSMLInteger arg0) {}
    
    public void leaveDecimal(WSMLDecimal arg0) {}

    public void leaveAnonymousID(AnonymousID arg0) {}

    public void leaveNbAnonymousID(NbAnonymousID arg0) {}

    public void leaveIRI(IRI arg0) {}

   
}
