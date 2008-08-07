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

import org.omwg.logicalexpression.terms.ConstructedTerm;
import org.omwg.logicalexpression.terms.NumberedAnonymousID;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.logicalexpression.terms.Visitor;
import org.omwg.ontology.ComplexDataValue;
import org.omwg.ontology.SimpleDataValue;
import org.omwg.ontology.Variable;
import org.wsmo.common.IRI;
import org.wsmo.common.UnnumberedAnonymousID;

/**
 * A simple stub implementation of the term Visitor interface. Real
 * functionality should be added in subclasses.
 * 
 * @author Uwe Keller, DERI Innsbruck
 * @author Gabor Nagypal, FZI
 */
public class TermVisitor implements Visitor {

    public void visitConstructedTerm(ConstructedTerm arg0) {
        for (int i = 0; i < arg0.getArity(); i++) {
            Term nextArg = arg0.getParameter(i);
            nextArg.accept(this);
        }
    }

    public void visitVariable(Variable arg0) {
    }

    public void visitSimpleDataValue(SimpleDataValue arg0) {
    }

    public void visitComplexDataValue(ComplexDataValue arg0) {
    }

    public void visitUnnumberedID(UnnumberedAnonymousID arg0) {
    }

    public void visitNumberedID(NumberedAnonymousID arg0) {
    }

    public void visitIRI(IRI arg0) {
    }
}
