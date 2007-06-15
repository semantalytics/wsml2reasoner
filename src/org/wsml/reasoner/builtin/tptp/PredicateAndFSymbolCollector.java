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

import org.deri.wsmo4j.io.parser.wsml.TempVariable;
import org.omwg.logicalexpression.Atom;
import org.omwg.logicalexpression.terms.*;
import org.omwg.ontology.*;
import org.wsml.reasoner.transformation.InfixOrderLogicalExpressionVisitor;
import org.wsmo.common.IRI;
import org.wsmo.common.UnnumberedAnonymousID;

public class PredicateAndFSymbolCollector extends InfixOrderLogicalExpressionVisitor {
    
    private Set<Atom> atoms = new HashSet<Atom>();
    
    private FSymCollector termCollectVisitor = new FSymCollector(); 
    
    public void clear(){
    	termCollectVisitor.fsymbols.clear();
    	termCollectVisitor.constants.clear();
    	atoms.clear();
    }
    
    public Set<ConstructedTerm> getFSyms(){
    	return termCollectVisitor.fsymbols;
    }
    
    public Set<Atom> getAtoms(){
    	return atoms;
    }
    
    public Set<Term> getConstants(){
    	return termCollectVisitor.constants;
    }

	@Override
    public void handleAtom(Atom expr) {
		if(expr.getArity()>0 && expr.getParameter(0) instanceof TempVariable){
			//do nothing this is a buildin which is converted to a function
		}else{
			atoms.add(expr);
		}
		for (Term t :expr.listParameters()){
			t.accept(termCollectVisitor);
		}
    }

	@Override
	public Object getSerializedObject() {
		return null;
	}
}

class FSymCollector implements org.omwg.logicalexpression.terms.Visitor{
    Set<ConstructedTerm> fsymbols = new HashSet<ConstructedTerm>();
    Set<Term> constants = new HashSet<Term>();
    
	public void visitIRI(IRI arg0) {
		constants.add(arg0);
	}
	public void visitComplexDataValue(ComplexDataValue arg0) {}
	public void visitNumberedID(NumberedAnonymousID arg0) {}
	public void visitSimpleDataValue(SimpleDataValue arg0) {}
	public void visitUnnumberedID(UnnumberedAnonymousID arg0) {}
	public void visitVariable(Variable arg0) {}

	public void visitConstructedTerm(ConstructedTerm arg0) {
		fsymbols.add(arg0);
	}
}