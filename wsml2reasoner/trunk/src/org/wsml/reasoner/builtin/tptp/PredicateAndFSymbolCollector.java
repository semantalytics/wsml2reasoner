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

import java.util.HashSet;
import java.util.Set;

import org.omwg.logicalexpression.Atom;
import org.omwg.logicalexpression.terms.ConstructedTerm;
import org.omwg.logicalexpression.terms.NumberedAnonymousID;
import org.omwg.ontology.*;
import org.wsml.reasoner.transformation.InfixOrderLogicalExpressionVisitor;
import org.wsmo.common.IRI;
import org.wsmo.common.UnnumberedAnonymousID;

public class PredicateAndFSymbolCollector extends InfixOrderLogicalExpressionVisitor {
    
    private Set<Atom> atoms = new HashSet<Atom>();
    private FSymCollector col = new FSymCollector(); 
    
    public void clear(){
    	col.fsymbols.clear();
    	atoms.clear();
    }
    
    public Set<ConstructedTerm> getFSyms(){
    	return col.fsymbols;
    }
    
    public Set<Atom> getAtoms(){
    	return atoms;
    }

	@Override
    public void visitAtom(Atom expr) {
        atoms.add(expr);
    }

	@Override
	public Object getSerializedObject() {
		return null;
	}
}

class FSymCollector implements org.omwg.logicalexpression.terms.Visitor{
    Set<ConstructedTerm> fsymbols = new HashSet<ConstructedTerm>();
    
	public void visitIRI(IRI arg0) {}
	public void visitComplexDataValue(ComplexDataValue arg0) {}
	public void visitNumberedID(NumberedAnonymousID arg0) {}
	public void visitSimpleDataValue(SimpleDataValue arg0) {}
	public void visitUnnumberedID(UnnumberedAnonymousID arg0) {}
	public void visitVariable(Variable arg0) {}

	public void visitConstructedTerm(ConstructedTerm arg0) {
		fsymbols.add(arg0);
	}
}