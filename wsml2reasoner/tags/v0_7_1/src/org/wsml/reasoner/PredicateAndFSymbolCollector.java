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

import java.util.HashSet;
import java.util.Set;

import org.deri.wsmo4j.io.parser.wsml.TempVariable;
import org.omwg.logicalexpression.Atom;
import org.omwg.logicalexpression.AttributeConstraintMolecule;
import org.omwg.logicalexpression.TruthValue;
import org.omwg.logicalexpression.terms.ConstructedTerm;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.logicalexpression.terms.TermVisitor;
import org.omwg.ontology.ComplexDataValue;
import org.omwg.ontology.SimpleDataValue;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.transformation.InfixOrderLogicalExpressionVisitor;
import org.wsmo.common.IRI;
import org.wsmo.common.NumberedAnonymousID;
import org.wsmo.common.UnnumberedAnonymousID;

public class PredicateAndFSymbolCollector extends InfixOrderLogicalExpressionVisitor {

    private Set<Atom> atoms = new HashSet<Atom>();

    private FSymCollector termCollectVisitor = new FSymCollector();

    public void clear() {
        termCollectVisitor.fsymbols.clear();
        termCollectVisitor.constants.clear();
        atoms.clear();
    }

    public Set<ConstructedTerm> getFSyms() {
        return termCollectVisitor.fsymbols;
    }

    public Set<Atom> getAtoms() {
        return atoms;
    }

    public Set<Term> getConstants() {
        return termCollectVisitor.constants;
    }

    @Override
    public void handleAtom(Atom expr) {
        if (expr.getArity() > 0 && expr.getParameter(0) instanceof TempVariable) {
            // do nothing this is a buildin which is converted to a function
        }
        else {
            atoms.add(expr);
        }
        for (Term t : expr.listParameters()) {
            t.accept(termCollectVisitor);
        }
    }

    @Override
    public Object getSerializedObject() {
        return null;
    }

	@Override
	public void visitAttributeConstraintMolecule(
			AttributeConstraintMolecule expr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitTruthValue(TruthValue expr) {
		// TODO Auto-generated method stub
		
	}
}

class FSymCollector implements TermVisitor {
    Set<ConstructedTerm> fsymbols = new HashSet<ConstructedTerm>();

    Set<Term> constants = new HashSet<Term>();

	@Override
	public void visit(ConstructedTerm t) {
		fsymbols.add(t);
	}

	@Override
	public void visit(Variable t) {
		// Do nothing
		
	}

	@Override
	public void visit(SimpleDataValue t) {
		// Do nothing
		
	}

	@Override
	public void visit(ComplexDataValue t) {
		// Do nothing
		
	}

	@Override
	public void visit(UnnumberedAnonymousID t) {
		// Do nothing
		
	}

	@Override
	public void visit(NumberedAnonymousID t) {
		// Do nothing
		
	}

	@Override
	public void visit(IRI t) {
		 constants.add(t);
	}
}