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

import org.omwg.logicalexpression.Disjunction;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.NegationAsFailure;
import org.wsml.reasoner.transformation.le.NormalizationRule;
import org.wsmo.factory.Factory;
import org.wsmo.factory.LogicalExpressionFactory;


public class NegateDisjunctionRule implements NormalizationRule {
    
    private LogicalExpressionFactory leFactory;
    
    public NegateDisjunctionRule(Factory factory){
        this.leFactory = factory.getLogicalExpressionFactory();
    }
    
    public LogicalExpression apply(LogicalExpression expression) {
        Disjunction disjunction = (Disjunction) (((NegationAsFailure) expression).getOperand());
        return leFactory.createConjunction(leFactory.createNegationAsFailure(disjunction.getLeftOperand()), leFactory.createNegationAsFailure(disjunction.getRightOperand()));
    }

    public boolean isApplicable(LogicalExpression expression) {
        if (expression instanceof NegationAsFailure) {
            return ((NegationAsFailure) expression).getOperand() instanceof Disjunction;
        }
        return false;
    }

    public String toString() {
        return "naf(A or B)\n\t=>\n naf A and naf B\n";
    }
}