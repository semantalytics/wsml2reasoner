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

import org.omwg.logicalexpression.terms.*;
import org.omwg.ontology.ComplexDataValue;
import org.omwg.ontology.SimpleDataValue;
import org.omwg.ontology.Variable;
import org.wsmo.common.IRI;
import org.wsmo.common.UnnumberedAnonymousID;

/**
 * Implements a left-first, depth-first traversal over terms. The point in time
 * when a node has to be handled according to the traversal order is defined by
 * the postfix order.
 * 
 * Subclasses should be visitors of terms that don't need to care about how to
 * traverse nodes but overwrite the new kinds of methods whose default
 * implementation is empty:
 * 
 * (1) 'handleXXX' methods, that are called by the abstract traversal
 * implementation when the respective node is actually to be handeled (according
 * to the traversal order)
 * 
 * (2) 'enterXXX' methods, that are called by the abstract traversal
 * implementation when the respective subtree rooted in the given node is
 * entered (according to the traversal order)
 * 
 * (3) 'leaveXXX' methods, that are called by the abstract traversal
 * implementation when the respective subtree rooted in the given node is left
 * (according to the traversal order)
 * 
 * @author Uwe Keller, DERI Innsbruck
 * @author Gabor Nagypal, FZI
 */
public class PostfixOrderTermVisitor implements Visitor {

    /*
     * (non-Javadoc)
     * 
     * @see org.omwg.logicalexpression.terms.VisitorTerms#getSerializedObject()
     *      should be obsolate (next snapshot) TODO remove!
     */
    public Object getSerializedObject() {
        return null;
    }

    public void visitConstructedTerm(ConstructedTerm arg0) {
        enterConstructedTerm(arg0);
        for (int i = 0; i < arg0.getArity(); i++) {
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

    public void visitIRI(IRI arg0) {
        enterIRI(arg0);
        handleIRI(arg0);
        leaveIRI(arg0);
    }

    public void visitSimpleDataValue(SimpleDataValue arg0) {
        enterSimpleDataValue(arg0);
        handleSimpleDataValue(arg0);
        leaveSimpleDataValue(arg0);

    }

    public void visitComplexDataValue(ComplexDataValue arg0) {
        enterComplexDataValue(arg0);
        handleComplexDataValue(arg0);
        leaveComplexDataValue(arg0);

    }

    public void visitUnnumberedID(UnnumberedAnonymousID arg0) {
        enterUnnumberedID(arg0);
        handleUnnumberedID(arg0);
        leaveUnnumberedID(arg0);

    }

    public void visitNumberedID(NumberedAnonymousID arg0) {
        enterNumberedID(arg0);
        handleNumberedID(arg0);
        leaveNumberedID(arg0);

    }

    // -- Additional methods ...

    public void enterConstructedTerm(ConstructedTerm arg0) {
    }

    public void handleConstructedTerm(ConstructedTerm arg0) {
    }

    public void leaveConstructedTerm(ConstructedTerm arg0) {
    }

    public void enterVariable(Variable arg0) {
    }

    public void handleVariable(Variable arg0) {
    }

    public void leaveVariable(Variable arg0) {
    }

    public void enterIRI(IRI arg0) {
    }

    public void handleIRI(IRI arg0) {
    }

    public void leaveIRI(IRI arg0) {
    }

    public void enterSimpleDataValue(SimpleDataValue arg0) {
    }

    public void handleSimpleDataValue(SimpleDataValue arg0) {
    }

    public void leaveSimpleDataValue(SimpleDataValue arg0) {
    }

    public void enterComplexDataValue(ComplexDataValue arg0) {
    }

    public void handleComplexDataValue(ComplexDataValue arg0) {
    }

    public void leaveComplexDataValue(ComplexDataValue arg0) {
    }

    public void enterUnnumberedID(UnnumberedAnonymousID arg0) {
    }

    public void handleUnnumberedID(UnnumberedAnonymousID arg0) {
    }

    public void leaveUnnumberedID(UnnumberedAnonymousID arg0) {
    }

    public void enterNumberedID(NumberedAnonymousID arg0) {
    }

    public void handleNumberedID(NumberedAnonymousID arg0) {
    }

    public void leaveNumberedID(NumberedAnonymousID arg0) {
    }

}
