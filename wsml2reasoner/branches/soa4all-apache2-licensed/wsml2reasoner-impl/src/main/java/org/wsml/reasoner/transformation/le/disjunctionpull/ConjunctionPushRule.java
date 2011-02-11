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

package org.wsml.reasoner.transformation.le.disjunctionpull;

import java.util.HashSet;
import java.util.Set;

import org.omwg.logicalexpression.Binary;
import org.omwg.logicalexpression.Conjunction;
import org.omwg.logicalexpression.Disjunction;
import org.omwg.logicalexpression.LogicalExpression;
import org.wsml.reasoner.transformation.le.LEUtil;
import org.wsml.reasoner.transformation.le.NormalizationRule;
import org.wsmo.factory.FactoryContainer;
import org.wsmo.factory.LogicalExpressionFactory;


public class ConjunctionPushRule implements NormalizationRule {
    
    private LogicalExpressionFactory leFactory;
    
    public ConjunctionPushRule(FactoryContainer factory){
        this.leFactory = factory.getLogicalExpressionFactory();
    }
    
    public LogicalExpression apply(LogicalExpression expression) {
        Conjunction conjunction = (Conjunction) expression;
        Set <LogicalExpression> result = new HashSet<LogicalExpression>();
        if (hasLeftDisjunction(conjunction)) {
            for (LogicalExpression disjunct : collectDirectDisjuncts(conjunction.getLeftOperand())) {
                result.add(leFactory.createConjunction(conjunction.getRightOperand(), disjunct));
            }
        }
        else {
            for (LogicalExpression disjunct : collectDirectDisjuncts(conjunction.getRightOperand())) {
                result.add(leFactory.createConjunction(conjunction.getLeftOperand(), disjunct));
            }
        }
        return LEUtil.buildNaryDisjunction(leFactory, result);
    }

    public boolean isApplicable(LogicalExpression expression) {
        if (expression instanceof Conjunction) {
            return hasLeftDisjunction((Conjunction) expression) || hasRightDisjunction((Conjunction) expression);
        }
        return false;
    }

    protected boolean hasLeftDisjunction(Binary binary) {
        return binary.getLeftOperand() instanceof Disjunction;
    }

    protected boolean hasRightDisjunction(Binary binary) {
        return binary.getRightOperand() instanceof Disjunction;
    }
    
    protected Set<LogicalExpression> collectDirectDisjuncts(LogicalExpression expression) {
        Set <LogicalExpression> disjuncts = new HashSet<LogicalExpression>();
        if (expression instanceof Disjunction) {
            disjuncts.addAll(collectDirectDisjuncts(((Disjunction) expression).getLeftOperand()));
            disjuncts.addAll(collectDirectDisjuncts(((Disjunction) expression).getRightOperand()));
        }
        else{
            disjuncts.add(expression);
        }
        return disjuncts;
    }

    public String toString() {
        return "A or B and (C or D)\n\t=>\n A or B and C or B and D\n";
    }
}