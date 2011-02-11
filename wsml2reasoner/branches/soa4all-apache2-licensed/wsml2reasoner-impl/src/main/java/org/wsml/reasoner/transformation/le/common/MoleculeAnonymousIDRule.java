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