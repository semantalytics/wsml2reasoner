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

package org.wsml.reasoner.transformation.le.lloydtopor;

import java.util.HashSet;
import java.util.Set;

import org.omwg.logicalexpression.Constraint;
import org.omwg.logicalexpression.Disjunction;
import org.omwg.logicalexpression.LogicalExpression;
import org.wsml.reasoner.transformation.le.TransformationRule;
import org.wsmo.factory.FactoryContainer;
import org.wsmo.factory.LogicalExpressionFactory;


public class SplitConstraint implements TransformationRule {
    
    private LogicalExpressionFactory leFactory;
    
    public SplitConstraint(FactoryContainer wsmoManager){
        this.leFactory = wsmoManager.getLogicalExpressionFactory();
    }
    
    public boolean isApplicable(LogicalExpression expression) {
        if (expression instanceof Constraint) {
            return ((Constraint) expression).getOperand() instanceof Disjunction;
        }
        return false;
    }

    public Set<LogicalExpression> apply(LogicalExpression expression) {
        Set<LogicalExpression> resultingExpressions = new HashSet<LogicalExpression>();
        Disjunction disjunction = (Disjunction) ((Constraint) expression).getOperand();
        resultingExpressions.add(leFactory.createConstraint(disjunction.getLeftOperand()));
        resultingExpressions.add(leFactory.createConstraint(disjunction.getRightOperand()));
        return resultingExpressions;
    }

    public String toString() {
        return "!- B1 or ... or Bn\n\t=>\n !- B1\n\t...\n !- Bn\n";
    }
}