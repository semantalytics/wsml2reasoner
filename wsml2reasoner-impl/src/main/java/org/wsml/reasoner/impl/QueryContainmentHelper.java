/**
 * WSML Reasoner Implementation.
 *
 * Copyright (c) 2005, FZI, Austria.
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

import org.deri.wsmo4j.logicalexpression.AbstractVisitor;
import org.omwg.logicalexpression.Atom;
import org.omwg.logicalexpression.AttributeConstraintMolecule;
import org.omwg.logicalexpression.AttributeInferenceMolecule;
import org.omwg.logicalexpression.AttributeValueMolecule;
import org.omwg.logicalexpression.BuiltInAtom;
import org.omwg.logicalexpression.CompoundMolecule;
import org.omwg.logicalexpression.Disjunction;
import org.omwg.logicalexpression.MembershipMolecule;
import org.omwg.logicalexpression.Negation;
import org.omwg.logicalexpression.NegationAsFailure;
import org.omwg.logicalexpression.SubConceptMolecule;
import org.omwg.logicalexpression.TruthValue;

/**
 * A helper class for the Query Containment Check.
 * 
 * <pre>
 *   Created on November 7th, 2007
 *   Committed by $Author: nathalie $
 *   $Source: /home/richi/temp/w2r/wsml2reasoner/src/org/wsml/reasoner/impl/QueryContainmentHelper.java,v $,
 * </pre>
 * 
 * @author Nathalie Steinmetz, DERI Innsbruck
 * @version $Revision: 1.1 $ $Date: 2007-11-12 16:59:44 $
 */
public class QueryContainmentHelper extends AbstractVisitor {

    public void visitDisjunction(Disjunction arg0) {
        throw new IllegalArgumentException("Queries contain disjunction");
    }

    public void visitNegation(Negation arg0) {
        throw new IllegalArgumentException("Queries contain negation");
    }

    public void visitNegationAsFailure(NegationAsFailure arg0) {
        throw new IllegalArgumentException("Queries contain negation");

    }

    public void visitAtom(Atom expr) {
        if (expr instanceof BuiltInAtom) {
            throw new IllegalArgumentException("Queries contain built-ins");
        }
    }

    public void visitAttributeConstraintMolecule(AttributeConstraintMolecule expr) {
        // TODO Auto-generated method stub

    }

    public void visitAttributeInferenceMolecule(AttributeInferenceMolecule expr) {
        // TODO Auto-generated method stub

    }

    public void visitAttributeValueMolecule(AttributeValueMolecule expr) {
        // TODO Auto-generated method stub

    }

    public void visitCompoundMolecule(CompoundMolecule expr) {
        // TODO Auto-generated method stub

    }

    public void visitMemberShipMolecule(MembershipMolecule expr) {
        // TODO Auto-generated method stub

    }

    public void visitSubConceptMolecule(SubConceptMolecule expr) {
        // TODO Auto-generated method stub

    }

	@Override
	public void visitTruthValue(TruthValue expr) {
		// TODO Auto-generated method stub
		
	}

}
/*
 * $Log: not supported by cvs2svn $
 * 
 */
