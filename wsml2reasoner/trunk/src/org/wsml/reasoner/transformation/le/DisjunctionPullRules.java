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
package org.wsml.reasoner.transformation.le;

import java.util.HashSet;
import java.util.Set;

import org.omwg.logicalexpression.Binary;
import org.omwg.logicalexpression.Conjunction;
import org.omwg.logicalexpression.Disjunction;
import org.omwg.logicalexpression.LogicalExpression;
import org.wsml.reasoner.impl.WSMO4JManager;

/**
 * This singleton class represents a set of normalization rules for pulling
 * disjunctions in logical expressions out of conjunctions, realizing a
 * conjunctive normal form. left-implications.
 * 
 * @author Stephan Grimm, FZI Karlsruhe
 */
public class DisjunctionPullRules extends FixedModificationRules {

    private AddOnlyArrayList<NormalizationRule> rules;

    public DisjunctionPullRules(WSMO4JManager wsmoManager) {
        super(wsmoManager);
        rules = new AddOnlyArrayList<NormalizationRule>();
        rules.add(new ConjunctionPushRule());
    }

    protected class ConjunctionPushRule implements NormalizationRule {
        public LogicalExpression apply(LogicalExpression expression) {
            Conjunction conjunction = (Conjunction) expression;
            LogicalExpression conjunct;
            Disjunction disjunction;
            if (hasLeftDisjunction(conjunction)) {
                conjunct = conjunction.getRightOperand();
                disjunction = (Disjunction) conjunction.getLeftOperand();
            }
            else {
                conjunct = conjunction.getLeftOperand();
                disjunction = (Disjunction) conjunction.getRightOperand();
            }
            Set<LogicalExpression> disjuncts = new HashSet<LogicalExpression>();
            collectDirectDisjuncts(disjunction, disjuncts);
            Set<LogicalExpression> newDisjuncts = new HashSet<LogicalExpression>();
            for (LogicalExpression disjunct : disjuncts) {
                newDisjuncts.add(leFactory.createConjunction(conjunct, disjunct));
            }
            return buildNaryDisjunction(newDisjuncts);
        }

        public boolean isApplicable(LogicalExpression expression) {
            if (expression instanceof Conjunction) {
                return hasLeftDisjunction((Conjunction) expression) || hasRightDisjunction((Conjunction) expression);
            }
            return false;
        }

        protected boolean hasLeftDisjunction(Binary binary) {
            return binary.getLeftOperand() instanceof Disjunction;
        }

        protected boolean hasRightDisjunction(Binary binary) {
            return binary.getRightOperand() instanceof Disjunction;
        }

        protected void collectDirectDisjuncts(LogicalExpression expression, Set<LogicalExpression> disjuncts) {
            if (expression instanceof Disjunction) {
                collectDirectDisjuncts(((Disjunction) expression).getLeftOperand(), disjuncts);
                collectDirectDisjuncts(((Disjunction) expression).getRightOperand(), disjuncts);
                return;
            }
            disjuncts.add(expression);
        }

        public String toString() {
            return "A or B and (C or D)\n\t=>\n A or B and C or B and D\n";
        }
    }

    public AddOnlyArrayList<NormalizationRule> getRules() {
        return rules;
    }
}