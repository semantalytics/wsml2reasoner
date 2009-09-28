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

import org.omwg.logicalexpression.Equivalence;
import org.omwg.logicalexpression.LogicalExpression;
import org.wsml.reasoner.transformation.le.NormalizationRule;
import org.wsmo.factory.FactoryContainer;
import org.wsmo.factory.LogicalExpressionFactory;


public class EquivalenceReplacementRule implements NormalizationRule {
    
    private LogicalExpressionFactory leFactory;
    
    public EquivalenceReplacementRule(FactoryContainer factory){
        this.leFactory = factory.getLogicalExpressionFactory();
    }
    
    public LogicalExpression apply(LogicalExpression expression) {
        Equivalence equivalence = (Equivalence) expression;
        LogicalExpression impliedBy1 = leFactory.createInverseImplication(equivalence.getRightOperand(), equivalence.getLeftOperand());
        LogicalExpression impliedBy2 = leFactory.createInverseImplication(equivalence.getLeftOperand(), equivalence.getRightOperand());
        return leFactory.createConjunction(impliedBy1, impliedBy2);
    }

    public boolean isApplicable(LogicalExpression expression) {
        return expression instanceof Equivalence;
    }

    public String toString() {
        return "A equivalent B\n\t=>\n (A impliedBy B) and (B impliedBy A)\n";
    }
}