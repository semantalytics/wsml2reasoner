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
package org.wsml.reasoner.transformation.le;

import org.omwg.logicalexpression.Equivalence;
import org.omwg.logicalexpression.Implication;
import org.omwg.logicalexpression.LogicalExpression;
import org.wsml.reasoner.impl.WSMO4JManager;

/**
 * This singleton class represents a set of normalization rules for replacing
 * equivalences and right-implications in logical expressions by
 * left-implications.
 * 
 * @author Stephan Grimm, FZI Karlsruhe
 */
public class ImplicationReductionRules extends FixedModificationRules {
    public ImplicationReductionRules(WSMO4JManager wsmoManager) {
        super(wsmoManager);
        rules.add(new EquivalenceReplacementRule());
        rules.add(new RightImplicationReplacementRule());
    }

    protected class EquivalenceReplacementRule implements NormalizationRule {
        public LogicalExpression apply(LogicalExpression expression) {
            Equivalence equivalence = (Equivalence) expression;
            LogicalExpression leftArg = equivalence.getLeftOperand();
            LogicalExpression rightArg = equivalence.getRightOperand();
            LogicalExpression impliedBy1 = leFactory.createInverseImplication(
                    rightArg, leftArg);
            LogicalExpression impliedBy2 = leFactory.createInverseImplication(
                    leftArg, rightArg);
            LogicalExpression and = leFactory.createConjunction(impliedBy1,
                    impliedBy2);
            return and;
        }

        public boolean isApplicable(LogicalExpression expression) {
            return expression instanceof Equivalence;
        }

        public String toString() {
            return "A equivalent B\n\t=>\n (A impliedBy B) and (B impliedBy A)\n";
        }
    }

    protected class RightImplicationReplacementRule implements
            NormalizationRule {
        public LogicalExpression apply(LogicalExpression expression) {
            Implication implication = (Implication) expression;
            LogicalExpression leftArg = implication.getLeftOperand();
            LogicalExpression rightArg = implication.getRightOperand();
            LogicalExpression impliedBy = leFactory.createInverseImplication(
                    rightArg, leftArg);
            return impliedBy;
        }

        public boolean isApplicable(LogicalExpression expression) {
            return expression instanceof Implication;
        }

        public String toString() {
            return "A implies B\n\t=>\n B impliedBy A\n";
        }
    }
}
