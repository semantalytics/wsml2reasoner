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

package org.wsml.reasoner.transformation.le.lloydtopor;

import java.util.HashSet;
import java.util.Set;

import org.omwg.logicalexpression.Conjunction;
import org.omwg.logicalexpression.LogicProgrammingRule;
import org.omwg.logicalexpression.LogicalExpression;
import org.wsml.reasoner.transformation.le.TransformationRule;
import org.wsmo.factory.FactoryContainer;
import org.wsmo.factory.LogicalExpressionFactory;


public class SplitConjunctiveHead implements TransformationRule {
    
    private LogicalExpressionFactory leFactory;
    
    public SplitConjunctiveHead(FactoryContainer wsmoManager){
        this.leFactory = wsmoManager.getLogicalExpressionFactory();
    }
    
    public boolean isApplicable(LogicalExpression expression) {
        if (expression instanceof LogicProgrammingRule) {
            return ((LogicProgrammingRule) expression).getLeftOperand() instanceof Conjunction;
        }
        return false;
    }

    public Set<LogicalExpression> apply(LogicalExpression expression) {
        Set<LogicalExpression> resultingExpressions = new HashSet<LogicalExpression>();
        Conjunction conjunction = (Conjunction) ((LogicProgrammingRule) expression).getLeftOperand();
        resultingExpressions.add(leFactory.createLogicProgrammingRule(conjunction.getLeftOperand(), ((LogicProgrammingRule) expression).getRightOperand()));
        resultingExpressions.add(leFactory.createLogicProgrammingRule(conjunction.getRightOperand(), ((LogicProgrammingRule) expression).getRightOperand()));
        return resultingExpressions;
    }

    public String toString() {
        return "A1 and ... and An :- B\n\t=>\n A1 :- B\n\t...\n An :- B\n";
    }
}