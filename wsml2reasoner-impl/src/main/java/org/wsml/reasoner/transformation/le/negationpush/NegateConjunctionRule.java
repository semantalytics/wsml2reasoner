/*
 * Copyright 2011, Semantic Technology Institute (STI) Innsbruck, 
 * University of Innsbruck, Technikerstrasse 21a, 6020 Innsbruck, Austria.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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