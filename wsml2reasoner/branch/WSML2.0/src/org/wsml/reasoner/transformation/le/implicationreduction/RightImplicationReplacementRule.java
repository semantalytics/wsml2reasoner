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

package org.wsml.reasoner.transformation.le.implicationreduction;

import org.omwg.logicalexpression.Implication;
import org.omwg.logicalexpression.LogicalExpression;
import org.wsml.reasoner.transformation.le.NormalizationRule;
import org.wsmo.factory.Factory;
import org.wsmo.factory.LogicalExpressionFactory;


public class RightImplicationReplacementRule implements NormalizationRule {
    
    private LogicalExpressionFactory leFactory;
    
    public RightImplicationReplacementRule(Factory factory){
        this.leFactory = factory.getLogicalExpressionFactory();
    }
    
    public LogicalExpression apply(LogicalExpression expression) {
        Implication implication = (Implication) expression;
        return leFactory.createInverseImplication(implication.getRightOperand(), implication.getLeftOperand());
    }

    public boolean isApplicable(LogicalExpression expression) {
        return expression instanceof Implication;
    }

    public String toString() {
        return "A implies B\n\t=>\n B impliedBy A\n";
    }
}