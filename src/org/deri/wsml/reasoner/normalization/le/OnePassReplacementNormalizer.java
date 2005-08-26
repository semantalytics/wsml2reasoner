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

/**
 * This class applies a set of normalization rules to the sub-expressions of a
 * logical expression by traversing the tree a single time. It distinguishes
 * between pre-order rules, which are applied in a top-down manner, and
 * post-order rules, which are applied in a bottom-up manner. For any set of
 * rules, all rules are apllied to a sub-expression until no rule application
 * causes any changes. The original expression will not be modified, rather a
 * copy is created.
 * 
 * @author Stephan Grimm, FZI Karlsruhe
 */
public class OnePassReplacementNormalizer implements LogicalExpressionNormalizer
{
    protected List<NormalizationRule> preOrderRules;

    protected List<NormalizationRule> postOrderRules;

    protected static LogicalExpressionFactory leFactory;

    /**
     * This constructor accepts two sets of normalization rules: one for
     * top-down application and one for bottom-up application.
     * 
     * @param preorderRules
     * @param postorderRules
     */
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

    /**
     * This constructor accepts a single set of normalization rules that are
     * used for top-down application.
     * 
     * @param rules
     */
    public OnePassReplacementNormalizer(List<NormalizationRule> rules)
    {
        this(rules, new ArrayList<NormalizationRule>(0));
    }

    /**
     * This method recursively applies normalization rules to the
     * sub-expressions of a given logical expression. Normalization rules from
     * the pre-order set are applied befor, and normalization rules from the
     * post-order set are applied after a sub-expression is expanded. At any
     * stage, copies of a sub-expression are passed back, such that the original
     * expression remains unchanged.
     */
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

    /**
     * This method iteratively applies the normalization rules in a given list
     * to a given logical expression, until the expression is not changed by an
     * application of a rule anymore.
     * 
     * @param expression
     * @param rules
     * @return the resulting expression after iterative application of all
     *         applicable rules
     */
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

    /**
     * This method detrmines the arity of a compound expression.
     * 
     * @param compound
     * @return
     */
    protected int getArgumentCount(CompoundExpression compound)
    {
        int count = 0;
        while(compound.getArgument(count++) != null)
            ;
        return count - 1;
    }

    /**
     * This method creates a copy of a given compound expression in which the
     * arguments are replaced by a given list of logical expressions.
     * 
     * @param compound
     * @param arguments
     * @return a compound expression with replaced arguments
     */
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
