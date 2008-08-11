/**
 * WSML Reasoner Implementation.
 *
 * Copyright (c) 2005, FZI, Germany.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * You should have received a copy of the GNU Lesser General Public License along
 * with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
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