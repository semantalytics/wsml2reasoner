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
package org.wsml.reasoner.transformation.le;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.omwg.logicalexpression.LogicalExpression;

/**
 * This class repeatedly applies a set of transformation rules to a logical
 * expression, splitting it into simpler expressions. These resulting
 * expressions are collected in a set of logical expression until no rule is
 * applicable to any expression in this set anymore.
 * 
 * @author Stephan Grimm, FZI Karlsruhe
 */
public class TopDownLESplitter implements LogicalExpressionTransformer {
    
    protected List<TransformationRule> rules;

    public TopDownLESplitter(List<TransformationRule> rules) {
        this.rules = rules;
    }

    /**
     * This method performs the transformation of a given logical expression
     * according to a set of transformation rules.
     */
    public Set<LogicalExpression> transform(LogicalExpression expression) {
        Set<LogicalExpression> outputExpressions = new HashSet<LogicalExpression>();

        // apply an applicable transformation rule:
        boolean noRuleApplicable = true;
        for (TransformationRule transformationRule : rules) {
            if (transformationRule.isApplicable(expression)) {
                noRuleApplicable = false;
                outputExpressions = transformationRule.apply(expression);
                break;
            }
        }
        if (noRuleApplicable) {
            outputExpressions.add(expression);
            return outputExpressions;
        }

        // recursively apply transformation to all resulting expressions:
        Set<LogicalExpression> resultingExpressions = new HashSet<LogicalExpression>();
        for (LogicalExpression outputExpression : outputExpressions){
            resultingExpressions.addAll(transform(outputExpression));
        }
        return resultingExpressions;
    }
}