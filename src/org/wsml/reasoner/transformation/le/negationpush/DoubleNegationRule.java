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

import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.NegationAsFailure;
import org.wsml.reasoner.transformation.le.NormalizationRule;


public class DoubleNegationRule implements NormalizationRule {
    
    public DoubleNegationRule(){
    }
    
    public boolean isApplicable(LogicalExpression expression) {
        if (expression instanceof NegationAsFailure) {
            return ((NegationAsFailure) expression).getOperand() instanceof NegationAsFailure;
        }
        return false;
    }
    
    public LogicalExpression apply(LogicalExpression expression) {
        return ((NegationAsFailure) ((NegationAsFailure) expression).getOperand()).getOperand();
    }

    public String toString() {
        return "naf(naf A)\n\t=>\n A\n";
    }
}