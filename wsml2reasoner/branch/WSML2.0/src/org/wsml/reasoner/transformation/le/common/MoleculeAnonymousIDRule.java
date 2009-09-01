/*
 * Copyright (C) 2006 Digital Enterprise Research Insitute (DERI) Innsbruck
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.wsml.reasoner.transformation.le.common;

import org.omwg.logicalexpression.AttributeConstraintMolecule;
import org.omwg.logicalexpression.AttributeInferenceMolecule;
import org.omwg.logicalexpression.AttributeMolecule;
import org.omwg.logicalexpression.AttributeValueMolecule;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.MembershipMolecule;
import org.omwg.logicalexpression.Molecule;
import org.omwg.logicalexpression.SubConceptMolecule;
import org.omwg.logicalexpression.terms.Term;
import org.wsml.reasoner.transformation.AnonymousIdTranslator;
import org.wsml.reasoner.transformation.le.NormalizationRule;
import org.wsmo.factory.FactoryContainer;
import org.wsmo.factory.LogicalExpressionFactory;


public class MoleculeAnonymousIDRule implements NormalizationRule {

    private LogicalExpressionFactory leFactory;
    private AnonymousIdTranslator anonymousIDTranslator;
    
    public MoleculeAnonymousIDRule(FactoryContainer factory, AnonymousIdTranslator anonymousIDTranslator){
        this.leFactory = factory.getLogicalExpressionFactory();
        this.anonymousIDTranslator = anonymousIDTranslator;
    }
    
    public LogicalExpression apply(LogicalExpression expression) {
        Molecule molecule = (Molecule) expression;
        Term leftOperand = anonymousIDTranslator.translate(molecule.getLeftParameter());
        Term rightOperand = anonymousIDTranslator.translate(molecule.getRightParameter());
        Term attribute = null;
        if (molecule instanceof AttributeMolecule) {
            attribute = anonymousIDTranslator.translate(((AttributeMolecule) molecule).getAttribute());
        }

        // instantiate the appropriate molecule type:
        if (molecule instanceof MembershipMolecule) {
            return leFactory.createMemberShipMolecule(leftOperand, rightOperand);
        }
        else if (molecule instanceof SubConceptMolecule) {
            return leFactory.createSubConceptMolecule(leftOperand, rightOperand);
        }
        else if (molecule instanceof AttributeConstraintMolecule) {
            return leFactory.createAttributeConstraint(leftOperand, attribute, rightOperand);
        }
        else if (molecule instanceof AttributeInferenceMolecule) {
            return leFactory.createAttributeInference(leftOperand, attribute, rightOperand);
        }
        else if (molecule instanceof AttributeValueMolecule) {
            return leFactory.createAttributeValue(leftOperand, attribute, rightOperand);
        }
        else{
            throw new RuntimeException("in MoleculeAnonymousIDRule::apply() : reached presumably unreachable code!");
        }
    }

    public boolean isApplicable(LogicalExpression expression) {
        return expression instanceof Molecule;
    }
}