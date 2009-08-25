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

import java.util.Map;
import java.util.Vector;

import org.deri.wsmo4j.io.parser.wsml.TempVariable;
import org.omwg.logicalexpression.Atom;
import org.omwg.logicalexpression.AttributeConstraintMolecule;
import org.omwg.logicalexpression.AttributeInferenceMolecule;
import org.omwg.logicalexpression.AttributeValueMolecule;
import org.omwg.logicalexpression.CompoundMolecule;
import org.omwg.logicalexpression.Conjunction;
import org.omwg.logicalexpression.Constants;
import org.omwg.logicalexpression.Constraint;
import org.omwg.logicalexpression.Disjunction;
import org.omwg.logicalexpression.Equivalence;
import org.omwg.logicalexpression.ExistentialQuantification;
import org.omwg.logicalexpression.Implication;
import org.omwg.logicalexpression.InverseImplication;
import org.omwg.logicalexpression.LogicProgrammingRule;
import org.omwg.logicalexpression.LogicalExpressionVisitor;
import org.omwg.logicalexpression.MembershipMolecule;
import org.omwg.logicalexpression.Negation;
import org.omwg.logicalexpression.NegationAsFailure;
import org.omwg.logicalexpression.SubConceptMolecule;
import org.omwg.logicalexpression.TruthValue;
import org.omwg.logicalexpression.UniversalQuantification;
import org.omwg.logicalexpression.terms.ConstructedTerm;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.logicalexpression.terms.TermVisitor;
import org.omwg.ontology.ComplexDataValue;
import org.omwg.ontology.SimpleDataValue;
import org.omwg.ontology.Variable;
import org.omwg.ontology.WsmlDataType;
import org.wsmo.common.IRI;
import org.wsmo.common.NumberedAnonymousID;
import org.wsmo.common.TopEntity;
import org.wsmo.common.UnnumberedAnonymousID;

/**
 * 
 * @author Holger Lausen
 * @version $Revision: 1.5 $ $Date: 2007-08-10 09:44:49 $
 * @see org.omwg.logicalexpression.Visitor
 */
public class TPTPTermSerializer implements LogicalExpressionVisitor, TermVisitor {

    private Map atoms2ConstructedTerms;

    private Vector<String> stack;

    // hack a bit too much of memory?
    private static TPTPSymbolMap sym = new TPTPSymbolMap();

    public TPTPSymbolMap getSymbolMap() {
        return sym;
    }

    public void SetSymbolMap(TPTPSymbolMap map) {
        sym = map;
    }

    /**
     * @param nsC
     *            TopEntity
     * @see org.deri.wsmo4j.io.serializer.wsml.VisitorSerializeWSML#VisitorSerializeWSML(TopEntity)
     */
    public TPTPTermSerializer() {
        stack = new Vector<String>();
    }

    public void setAtoms2ConstructedTerms(Map atoms2ConstructedTerms) {
        this.atoms2ConstructedTerms = atoms2ConstructedTerms;
    }

    /**
     * Builds a String representing the ConstructedTerm and adds it to a vector.
     * 
     * @param t
     *            ConstructedTerm to be serialized
     * @see org.omwg.logicalexpression.terms.Visitor#visitConstructedTerm(org.omwg.logicalexpression.terms.ConstructedTerm)
     */
    public void visitConstructedTerm(ConstructedTerm t) {
        String s = "";
        t.getFunctionSymbol().accept(this);
        s = s + stack.remove(stack.size() - 1);
        int nbParams = t.getArity();
        if (nbParams > 0) {
            s = s + "(";
            for (int i = 0; i < nbParams; i++) {
                t.getParameter(i).accept(this);
                s = s + stack.remove(stack.size() - 1);
                if (i + 1 < nbParams) {
                    s = s + ",";
                }
            }
            s = s + ")";
        }
        stack.add(s);
    }

    /**
     * Builds a String representing the Variable and adds it to a vector.
     * 
     * @param t
     *            Variable to be serialized
     * @see org.omwg.logicalexpression.terms.Visitor#visitVariable(org.omwg.logicalexpression.terms.Variable)
     */
    public void visitVariable(Variable t) {
        // TODO convert to what is allowed in TPTP
        if (t instanceof TempVariable) {
            Term term = (Term) atoms2ConstructedTerms.get(t);
            if (term != null)
                term.accept(this);
            else
                stack.add(t.toString() + "<NonResolvableDependencyToBuiltInAtom>");
            return;
        }
        String var = t.getName();
        stack.add(Character.toUpperCase(var.charAt(0)) + var.substring(1));
    }

    public void visitComplexDataValue(ComplexDataValue value) {
        String s = "";
        value.getType().getIdentifier().accept(this);
        s = stack.remove(stack.size() - 1);
        int nbParams = value.getArity();
        s = s + "(";
        for (byte i = 0; i < nbParams; i++) {
            ((Term) value.getArgumentValue(i)).accept(this);
            s = s + stack.remove(stack.size() - 1);
            if (i + 1 < nbParams) {
                s = s + ",";
            }
        }
        stack.add(s + ")");
    }

    public void visitSimpleDataValue(SimpleDataValue value) {
        if (value.getType().getIdentifier().toString().equals(WsmlDataType.WSML_STRING)) {
            // escape \ and "
            String content = (String) value.getValue();
            content = content.replaceAll("\\\\", "\\\\\\\\");
            content = content.replaceAll("\"", "\\\\\"");
            stack.add(Constants.STRING_DEL_START + content + Constants.STRING_DEL_END);
        }
        else { // WSML_DECIMAL || WSML_INTEGER
            stack.add("" + value.getValue());
        }
    }

    public void visitUnnumberedID(UnnumberedAnonymousID t) {
        throw new RuntimeException("should not be here anymore");
    }

    public void visitNumberedID(NumberedAnonymousID t) {
        throw new RuntimeException("should not be here anymore");
    }

    /**
     * Builds a String representing the IRI and adds it to a vector.
     * 
     * @param t
     *            IRI to be serialized
     * @see org.omwg.logicalexpression.terms.Visitor#visitIRI(org.omwg.logicalexpression.terms.IRI)
     */
    public void visitIRI(IRI t) {
        stack.add(sym.getTPTPTerm(t.toString()));
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
        return stack.remove(0).toString();
    }

	@Override
	public void visitAtom(Atom expr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitAttributeConstraintMolecule(
			AttributeConstraintMolecule expr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitAttributeInferenceMolecule(AttributeInferenceMolecule expr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitAttributeValueMolecule(AttributeValueMolecule expr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitCompoundMolecule(CompoundMolecule expr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitConjunction(Conjunction expr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitConstraint(Constraint expr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitDisjunction(Disjunction expr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitEquivalence(Equivalence expr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitExistentialQuantification(ExistentialQuantification expr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitImplication(Implication expr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitInverseImplication(InverseImplication expr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitLogicProgrammingRule(LogicProgrammingRule expr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitMemberShipMolecule(MembershipMolecule expr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitNegation(Negation expr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitNegationAsFailure(NegationAsFailure expr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitSubConceptMolecule(SubConceptMolecule expr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitTruthValue(TruthValue expr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitUniversalQuantification(UniversalQuantification expr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(ConstructedTerm t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Variable t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(SimpleDataValue t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(ComplexDataValue t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(UnnumberedAnonymousID t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(NumberedAnonymousID t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(IRI t) {
		// TODO Auto-generated method stub
		
	}
}