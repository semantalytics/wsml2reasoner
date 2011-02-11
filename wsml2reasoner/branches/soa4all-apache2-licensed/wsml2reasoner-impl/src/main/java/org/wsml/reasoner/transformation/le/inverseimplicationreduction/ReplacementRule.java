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

package org.wsml.reasoner.transformation.le.inverseimplicationreduction;

import java.util.HashSet;
import java.util.Set;

import org.omwg.logicalexpression.AttributeValueMolecule;
import org.omwg.logicalexpression.Binary;
import org.omwg.logicalexpression.Conjunction;
import org.omwg.logicalexpression.Disjunction;
import org.omwg.logicalexpression.ExistentialQuantification;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.MembershipMolecule;
import org.omwg.logicalexpression.Negation;
import org.omwg.logicalexpression.UniversalQuantification;
import org.omwg.ontology.Variable;


public abstract class ReplacementRule {

    private Set<Variable> leftVariables = new HashSet<Variable>();

    private Set<Variable> rightVariables = new HashSet<Variable>();

    /*
     * The methods checks for dependencies in the molecules contained in the
     * given conjunction or disjunction. True is returned if there are
     * dependencies, false if there are not.
     */
    protected boolean checkForDependencies(Binary binary) {
        leftVariables.clear();
        rightVariables.clear();

        LogicalExpression left = binary.getLeftOperand();
        LogicalExpression right = binary.getRightOperand();
        if (left instanceof MembershipMolecule) {
            checkMemberShipMolecule((MembershipMolecule) left);
        }
        else if (left instanceof AttributeValueMolecule) {
            checkAttributeValueMolecule((AttributeValueMolecule) left);
        }
        else if (left instanceof Conjunction) {
            checkConjunction((Conjunction) left);
        }
        else if (left instanceof Disjunction) {
            checkDisjunction((Disjunction) left);
        }
        else if (left instanceof Negation) {
            checkNegation((Negation) left);
        }
        else if (left instanceof ExistentialQuantification) {
            checkExistentialQuantification((ExistentialQuantification) left);
        }
        else if (left instanceof UniversalQuantification) {
            checkUniversalQuantification((UniversalQuantification) left);
        }
        if (right instanceof MembershipMolecule) {
            checkMemberShipMolecule((MembershipMolecule) right);
        }
        else if (right instanceof AttributeValueMolecule) {
            checkAttributeValueMolecule((AttributeValueMolecule) right);
        }
        else if (right instanceof Conjunction) {
            checkConjunction((Conjunction) right);
        }
        else if (right instanceof Disjunction) {
            checkDisjunction((Disjunction) right);
        }
        else if (right instanceof Negation) {
            checkNegation((Negation) right);
        }
        else if (right instanceof ExistentialQuantification) {
            checkExistentialQuantification((ExistentialQuantification) right);
        }
        else if (right instanceof UniversalQuantification) {
            checkUniversalQuantification((UniversalQuantification) right);
        }
        for (Variable vLeft : leftVariables) {
            for (Variable vRight : rightVariables) {
                if (vLeft.equals(vRight)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void checkMemberShipMolecule(MembershipMolecule m) {
        leftVariables.add((Variable) m.getLeftParameter());
    }

    private void checkAttributeValueMolecule(AttributeValueMolecule a) {
        leftVariables.add((Variable) a.getLeftParameter());
        rightVariables.add((Variable) a.getRightParameter());
    }

    private void checkConjunction(Conjunction c) {
        LogicalExpression left = c.getLeftOperand();
        LogicalExpression right = c.getRightOperand();
        if (left instanceof MembershipMolecule) {
            checkMemberShipMolecule((MembershipMolecule) left);
        }
        else if (left instanceof AttributeValueMolecule) {
            checkAttributeValueMolecule((AttributeValueMolecule) left);
        }
        else if (left instanceof Conjunction) {
            checkConjunction((Conjunction) left);
        }
        else if (left instanceof Disjunction) {
            checkDisjunction((Disjunction) left);
        }
        else if (left instanceof Negation) {
            checkNegation((Negation) left);
        }
        else if (left instanceof ExistentialQuantification) {
            checkExistentialQuantification((ExistentialQuantification) left);
        }
        else if (left instanceof UniversalQuantification) {
            checkUniversalQuantification((UniversalQuantification) left);
        }
        if (right instanceof MembershipMolecule) {
            checkMemberShipMolecule((MembershipMolecule) right);
        }
        else if (right instanceof AttributeValueMolecule) {
            checkAttributeValueMolecule((AttributeValueMolecule) right);
        }
        else if (right instanceof Conjunction) {
            checkConjunction((Conjunction) right);
        }
        else if (right instanceof Disjunction) {
            checkDisjunction((Disjunction) right);
        }
        else if (right instanceof Negation) {
            checkNegation((Negation) right);
        }
        else if (right instanceof ExistentialQuantification) {
            checkExistentialQuantification((ExistentialQuantification) right);
        }
        else if (right instanceof UniversalQuantification) {
            checkUniversalQuantification((UniversalQuantification) right);
        }
    }

    private void checkDisjunction(Disjunction d) {
        LogicalExpression left = d.getLeftOperand();
        LogicalExpression right = d.getRightOperand();
        if (left instanceof MembershipMolecule) {
            checkMemberShipMolecule((MembershipMolecule) left);
        }
        else if (left instanceof AttributeValueMolecule) {
            checkAttributeValueMolecule((AttributeValueMolecule) left);
        }
        else if (left instanceof Conjunction) {
            checkConjunction((Conjunction) left);
        }
        else if (left instanceof Disjunction) {
            checkDisjunction((Disjunction) left);
        }
        else if (left instanceof Negation) {
            checkNegation((Negation) left);
        }
        else if (left instanceof ExistentialQuantification) {
            checkExistentialQuantification((ExistentialQuantification) left);
        }
        else if (left instanceof UniversalQuantification) {
            checkUniversalQuantification((UniversalQuantification) left);
        }
        if (right instanceof MembershipMolecule) {
            checkMemberShipMolecule((MembershipMolecule) right);
        }
        else if (right instanceof AttributeValueMolecule) {
            checkAttributeValueMolecule((AttributeValueMolecule) right);
        }
        else if (right instanceof Conjunction) {
            checkConjunction((Conjunction) right);
        }
        else if (right instanceof Disjunction) {
            checkDisjunction((Disjunction) right);
        }
        else if (right instanceof Negation) {
            checkNegation((Negation) right);
        }
        else if (right instanceof ExistentialQuantification) {
            checkExistentialQuantification((ExistentialQuantification) right);
        }
        else if (right instanceof UniversalQuantification) {
            checkUniversalQuantification((UniversalQuantification) right);
        }
    }

    private void checkNegation(Negation n) {
        LogicalExpression logExpr = n.getOperand();
        if (logExpr instanceof MembershipMolecule) {
            checkMemberShipMolecule((MembershipMolecule) logExpr);
        }
        else if (logExpr instanceof AttributeValueMolecule) {
            checkAttributeValueMolecule((AttributeValueMolecule) logExpr);
        }
        else if (logExpr instanceof Conjunction) {
            checkConjunction((Conjunction) logExpr);
        }
        else if (logExpr instanceof Disjunction) {
            checkDisjunction((Disjunction) logExpr);
        }
        else if (logExpr instanceof Negation) {
            checkNegation((Negation) logExpr);
        }
        else if (logExpr instanceof ExistentialQuantification) {
            checkExistentialQuantification((ExistentialQuantification) logExpr);
        }
        else if (logExpr instanceof UniversalQuantification) {
            checkUniversalQuantification((UniversalQuantification) logExpr);
        }
    }

    private void checkExistentialQuantification(ExistentialQuantification e) {
        LogicalExpression logExpr = e.getOperand();
        if (logExpr instanceof MembershipMolecule) {
            checkMemberShipMolecule((MembershipMolecule) logExpr);
        }
        else if (logExpr instanceof AttributeValueMolecule) {
            checkAttributeValueMolecule((AttributeValueMolecule) logExpr);
        }
        else if (logExpr instanceof Conjunction) {
            checkConjunction((Conjunction) logExpr);
        }
        else if (logExpr instanceof Disjunction) {
            checkDisjunction((Disjunction) logExpr);
        }
        else if (logExpr instanceof Negation) {
            checkNegation((Negation) logExpr);
        }
        else if (logExpr instanceof ExistentialQuantification) {
            checkExistentialQuantification((ExistentialQuantification) logExpr);
        }
        else if (logExpr instanceof UniversalQuantification) {
            checkUniversalQuantification((UniversalQuantification) logExpr);
        }
    }

    private void checkUniversalQuantification(UniversalQuantification u) {
        LogicalExpression logExpr = u.getOperand();
        if (logExpr instanceof MembershipMolecule) {
            checkMemberShipMolecule((MembershipMolecule) logExpr);
        }
        else if (logExpr instanceof AttributeValueMolecule) {
            checkAttributeValueMolecule((AttributeValueMolecule) logExpr);
        }
        else if (logExpr instanceof Conjunction) {
            checkConjunction((Conjunction) logExpr);
        }
        else if (logExpr instanceof Disjunction) {
            checkDisjunction((Disjunction) logExpr);
        }
        else if (logExpr instanceof Negation) {
            checkNegation((Negation) logExpr);
        }
        else if (logExpr instanceof ExistentialQuantification) {
            checkExistentialQuantification((ExistentialQuantification) logExpr);
        }
        else if (logExpr instanceof UniversalQuantification) {
            checkUniversalQuantification((UniversalQuantification) logExpr);
        }
    }
}
