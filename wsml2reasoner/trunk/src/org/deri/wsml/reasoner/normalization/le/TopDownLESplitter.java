package org.deri.wsml.reasoner.normalization.le;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.omwg.logexpression.LogicalExpression;

public class TopDownLESplitter implements LogicalExpressionTransformer
{
    protected List<TransformationRule> rules;

    public TopDownLESplitter(List<TransformationRule> rules)
    {
        this.rules = rules;
    }

    public Set<LogicalExpression> transform(LogicalExpression expression)
    {
        Set<LogicalExpression> outputExpressions = new HashSet<LogicalExpression>();

        // apply an applicable transformation rule:
        boolean noRuleApplicable = true;
        for(TransformationRule transformationRule : rules)
        {
            if(transformationRule.isApplicable(expression))
            {
                noRuleApplicable = false;
                outputExpressions = transformationRule.apply(expression);
                break;
            }
        }
        if(noRuleApplicable)
        {
            outputExpressions.add(expression);
            return outputExpressions;
        }

        // recursively apply transformation to all resulting expressions:
        Set<LogicalExpression> resultingExpressions = new HashSet<LogicalExpression>();
        Iterator outputExpressionIterator = outputExpressions.iterator();
        while(outputExpressionIterator.hasNext())
        {
            LogicalExpression outputExpression = (LogicalExpression)outputExpressionIterator.next();
            Set<LogicalExpression> replacements = transform(outputExpression);
            resultingExpressions.addAll(replacements);
        }

        return resultingExpressions;
    }
}
