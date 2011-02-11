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