/*
 * Copyright 2011, Semantic Technology Institute (STI) Innsbruck, 
 * University of Innsbruck, Technikerstrasse 21a, 6020 Innsbruck, Austria.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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