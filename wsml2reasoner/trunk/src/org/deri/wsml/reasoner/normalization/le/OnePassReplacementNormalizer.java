package org.deri.wsml.reasoner.normalization.le;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.omwg.logexpression.CompoundExpression;
import org.omwg.logexpression.LogicalExpression;
import org.omwg.logexpression.LogicalExpressionFactory;
import org.omwg.logexpression.Unary;
import org.wsmo.factory.Factory;

public class OnePassReplacementNormalizer implements LogicalExpressionNormalizer
{
    protected List<NormalizationRule> preOrderRules;

    protected List<NormalizationRule> postOrderRules;

    protected static LogicalExpressionFactory leFactory;

    public OnePassReplacementNormalizer(List<NormalizationRule> preorderRules, List<NormalizationRule> postorderRules)
    {
        this.preOrderRules = preorderRules;
        this.postOrderRules = postorderRules;
        if(leFactory == null)
        {
            Map createParams = new HashMap();
            createParams.put(Factory.PROVIDER_CLASS, "org.deri.wsmo4j.logexpression.LogicalExpressionFactoryImpl");
            leFactory = (LogicalExpressionFactory)Factory.createLogicalExpressionFactory(createParams);
        }
    }

    public LogicalExpression normalize(LogicalExpression expression)
    {
        // apply pre-order normalization rules:
        expression = applyRules(expression, preOrderRules);

        // recursively normalize arguments of compound expressions:
        List<LogicalExpression> arguments = new ArrayList<LogicalExpression>();
        if(expression instanceof CompoundExpression)
        {
            CompoundExpression compound = (CompoundExpression)expression;
            int argCount = getArgumentCount(compound);
            for(int i = 0; i < argCount; i++)
            {
                arguments.add(normalize(compound.getArgument(i)));
            }
            expression = replaceArguments(compound, arguments);
        }

        // apply post-order normalization rules:
        expression = applyRules(expression, postOrderRules);

        return expression;
    }

    protected LogicalExpression applyRules(LogicalExpression expression, List<NormalizationRule> rules)
    {
        boolean expressionHasChanged = true;
        while(expressionHasChanged)
        {
            expressionHasChanged = false;
            for(NormalizationRule rule : rules)
            {
                if(rule.isApplicable(expression))
                {
                    LogicalExpression oldExpression = expression;
                    expression = rule.apply(expression);
                    if(!expression.equals(oldExpression))
                            expressionHasChanged = true;
                    break;
                }
            }
        }
        return expression;
    }

    protected int getArgumentCount(CompoundExpression compound)
    {
        int count = 0;
        while(compound.getArgument(count++) != null)
            ;
        return count - 1;
    }

    protected CompoundExpression replaceArguments(CompoundExpression compound, List<LogicalExpression> arguments)
    {
        CompoundExpression result = null;
        if(compound instanceof Unary)
        {
            result = leFactory.createUnary(compound.getOperator(), arguments.get(0));
        }
        else
        // instanceof Binary
        {
            result = leFactory.createBinary(compound.getOperator(), arguments.get(0), arguments.get(1));
        }
        return result;
    }
}
