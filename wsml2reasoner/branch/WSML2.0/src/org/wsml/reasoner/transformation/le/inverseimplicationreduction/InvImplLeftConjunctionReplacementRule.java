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

package org.wsml.reasoner.transformation.le.inverseimplicationreduction;

import org.omwg.logicalexpression.Conjunction;
import org.omwg.logicalexpression.InverseImplication;
import org.omwg.logicalexpression.LogicalExpression;
import org.wsml.reasoner.transformation.le.NormalizationRule;
import org.wsmo.factory.Factory;
import org.wsmo.factory.LogicalExpressionFactory;

public class InvImplLeftConjunctionReplacementRule extends ReplacementRule implements NormalizationRule {
    
    private LogicalExpressionFactory leFactory;
    
    public InvImplLeftConjunctionReplacementRule(Factory factory){
        this.leFactory = factory.getLogicalExpressionFactory();
    }
    
    public LogicalExpression apply(LogicalExpression expression) {
        InverseImplication invImpl = (InverseImplication) expression;
        LogicalExpression impliedBy1 = leFactory.createInverseImplication(((Conjunction) invImpl.getLeftOperand()).getLeftOperand(), invImpl.getRightOperand());
        LogicalExpression impliedBy2 = leFactory.createInverseImplication(((Conjunction) invImpl.getLeftOperand()).getRightOperand(), invImpl.getRightOperand());
        return leFactory.createConjunction(impliedBy1, impliedBy2);
    }

    public boolean isApplicable(LogicalExpression expression) {
        if (expression instanceof InverseImplication && ((InverseImplication) expression).getLeftOperand() instanceof Conjunction) {
            return !checkForDependencies((Conjunction) ((InverseImplication) expression).getLeftOperand());
        }
        return false;
    }

    public String toString() {
        return "A and B impliedBy C\n\t=>\n (A impliedBy C) and (B impliedBy C)\n";
    }
}