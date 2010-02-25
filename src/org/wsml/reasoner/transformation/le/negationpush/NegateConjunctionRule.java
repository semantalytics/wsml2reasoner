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

package org.wsml.reasoner.transformation.le.negationpush;

import org.omwg.logicalexpression.Conjunction;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.NegationAsFailure;
import org.wsml.reasoner.transformation.le.NormalizationRule;
import org.wsmo.factory.FactoryContainer;
import org.wsmo.factory.LogicalExpressionFactory;


public class NegateConjunctionRule implements NormalizationRule {
    
    private LogicalExpressionFactory leFactory;
    
    public NegateConjunctionRule(FactoryContainer factory){
        this.leFactory = factory.getLogicalExpressionFactory();
    }
    
    public boolean isApplicable(LogicalExpression expression) {
        if (expression instanceof NegationAsFailure) {
            return ((NegationAsFailure) expression).getOperand() instanceof Conjunction;
        }
        return false;
    }
    
    public LogicalExpression apply(LogicalExpression expression) {
        Conjunction conjunction = (Conjunction) ((NegationAsFailure) expression).getOperand();
        return leFactory.createDisjunction(leFactory.createNegationAsFailure(conjunction.getLeftOperand()), leFactory.createNegationAsFailure(conjunction.getRightOperand()));
    }

    public String toString() {
        return "naf(A and B)\n\t=>\n naf A or naf B\n";
    }
}