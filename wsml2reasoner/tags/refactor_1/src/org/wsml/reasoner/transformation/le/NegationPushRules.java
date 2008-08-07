/**
 * WSML Reasoner Implementation.
 *
 * Copyright (c) 2005, FZI, Germany.
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

import org.omwg.logicalexpression.Conjunction;
import org.omwg.logicalexpression.Disjunction;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.NegationAsFailure;
import org.wsml.reasoner.impl.WSMO4JManager;

/**
 * This singleton class represents a set of normalization rules for pushing
 * negation-as-failure operators inside a logical expression into its
 * sub-expressions, such that the remaining occurrences of negation are all
 * atomic.
 * 
 * @author Stephan Grimm, FZI Karlsruhe
 */
public class NegationPushRules extends FixedModificationRules {

    private AddOnlyArrayList<NormalizationRule> rules;

    public NegationPushRules(WSMO4JManager wsmoManager) {
        super(wsmoManager);
        rules = new AddOnlyArrayList<NormalizationRule>();
        rules.add(new DoubleNegationRule());
        rules.add(new NegateConjunctionRule());
        rules.add(new NegateDisjunctionRule());
    }

    protected class DoubleNegationRule implements NormalizationRule {
        public LogicalExpression apply(LogicalExpression expression) {
            NegationAsFailure outerNaf = (NegationAsFailure) expression;
            NegationAsFailure innerNaf = (NegationAsFailure) (outerNaf.getOperand());
            return innerNaf.getOperand();
        }

        public boolean isApplicable(LogicalExpression expression) {
            if (expression instanceof NegationAsFailure) {
                NegationAsFailure outerNaf = (NegationAsFailure) expression;
                return outerNaf.getOperand() instanceof NegationAsFailure;
            }
            return false;
        }

        public String toString() {
            return "naf(naf A)\n\t=>\n A\n";
        }
    }

    protected class NegateConjunctionRule implements NormalizationRule {
        public LogicalExpression apply(LogicalExpression expression) {
            NegationAsFailure naf = (NegationAsFailure) expression;
            Conjunction conjunction = (Conjunction) (naf.getOperand());
            LogicalExpression leftArg = leFactory.createNegationAsFailure(conjunction.getLeftOperand());
            LogicalExpression rightArg = leFactory.createNegationAsFailure(conjunction.getRightOperand());
            LogicalExpression disjunction = leFactory.createDisjunction(leftArg, rightArg);
            return disjunction;
        }

        public boolean isApplicable(LogicalExpression expression) {
            if (expression instanceof NegationAsFailure) {
                NegationAsFailure naf = (NegationAsFailure) expression;
                return naf.getOperand() instanceof Conjunction;
            }
            return false;
        }

        public String toString() {
            return "naf(A and B)\n\t=>\n naf A or naf B\n";
        }
    }

    protected class NegateDisjunctionRule implements NormalizationRule {
        public LogicalExpression apply(LogicalExpression expression) {
            NegationAsFailure naf = (NegationAsFailure) expression;
            Disjunction disjunction = (Disjunction) (naf.getOperand());
            LogicalExpression leftArg = leFactory.createNegationAsFailure(disjunction.getLeftOperand());
            LogicalExpression rightArg = leFactory.createNegationAsFailure(disjunction.getRightOperand());
            LogicalExpression conjunction = leFactory.createConjunction(leftArg, rightArg);
            return conjunction;
        }

        public boolean isApplicable(LogicalExpression expression) {
            if (expression instanceof NegationAsFailure) {
                NegationAsFailure naf = (NegationAsFailure) expression;
                return naf.getOperand() instanceof Disjunction;
            }
            return false;
        }

        public String toString() {
            return "naf(A or B)\n\t=>\n naf A and naf B\n";
        }
    }

    public AddOnlyArrayList<NormalizationRule> getRules() {
        return rules;
    }
}
