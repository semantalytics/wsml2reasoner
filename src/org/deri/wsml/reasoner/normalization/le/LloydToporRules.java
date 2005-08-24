package org.deri.wsml.reasoner.normalization.le;

import java.util.HashSet;
import java.util.Set;

import org.omwg.logexpression.Binary;
import org.omwg.logexpression.CompoundExpression;
import org.omwg.logexpression.LogicalExpression;

public class LloydToporRules extends FixedNormalizationRules
{
    protected static LloydToporRules instance;

    private LloydToporRules()
    {
        super();
        rules.add(new SplitDisjunctiveBody());
        rules.add(new SplitConjunctiveHead());
        rules.add(new TransformNestedImplication());
        rules.add(new SplitConjunction());
    }

    public static LloydToporRules instantiate()
    {
        if(instance == null)
        {
            instance = new LloydToporRules();
        }
        return instance;
    }

    public class SplitConjunctiveHead implements TransformationRule
    {
        public boolean isApplicable(LogicalExpression expression)
        {
            try
            {
                CompoundExpression compound = (CompoundExpression)expression;
                if(compound.getOperator() == CompoundExpression.IMPLIEDBY)
                {
                    CompoundExpression conjunction = (CompoundExpression)compound.getArgument(0);
                    if(conjunction.getOperator() == CompoundExpression.AND)
                    {
                        return true;
                    }
                }
            } catch(ClassCastException e)
            {
            }
            return false;
        }

        public Set<LogicalExpression> apply(LogicalExpression expression)
        {
            Set<LogicalExpression> resultingExpressions = new HashSet<LogicalExpression>();
            Binary rule = (Binary)expression;
            Binary conjunction = (Binary)rule.getArgument(0);
            resultingExpressions.add(leFactory.createBinary(CompoundExpression.IMPLIEDBY, conjunction.getArgument(0), rule.getArgument(1)));
            resultingExpressions.add(leFactory.createBinary(CompoundExpression.IMPLIEDBY, conjunction.getArgument(1), rule.getArgument(1)));
            return resultingExpressions;
        }
    }

    public class SplitDisjunctiveBody implements TransformationRule
    {
        public boolean isApplicable(LogicalExpression expression)
        {
            try
            {
                CompoundExpression compound = (CompoundExpression)expression;
                if(compound.getOperator() == CompoundExpression.IMPLIEDBY)
                {
                    CompoundExpression disjunction = (CompoundExpression)compound.getArgument(1);
                    if(disjunction.getOperator() == CompoundExpression.OR)
                    {
                        return true;
                    }
                }
            } catch(ClassCastException e)
            {
            }
            return false;
        }

        public Set<LogicalExpression> apply(LogicalExpression expression)
        {
            Set<LogicalExpression> resultingExpressions = new HashSet<LogicalExpression>();
            Binary rule = (Binary)expression;
            Binary disjunction = (Binary)rule.getArgument(1);
            resultingExpressions.add(leFactory.createBinary(CompoundExpression.IMPLIEDBY, rule.getArgument(0), disjunction.getArgument(0)));
            resultingExpressions.add(leFactory.createBinary(CompoundExpression.IMPLIEDBY, rule.getArgument(0), disjunction.getArgument(1)));
            return resultingExpressions;
        }
    }

    public class TransformNestedImplication implements TransformationRule
    {
        public boolean isApplicable(LogicalExpression expression)
        {
            try
            {
                CompoundExpression compound = (CompoundExpression)expression;
                if(compound.getOperator() == CompoundExpression.IMPLIEDBY)
                {
                    CompoundExpression rule = (CompoundExpression)compound.getArgument(0);
                    if(rule.getOperator() == CompoundExpression.IMPLIEDBY)
                    {
                        return true;
                    }
                }
            } catch(ClassCastException e)
            {
            }
            return false;
        }

        public Set<LogicalExpression> apply(LogicalExpression expression)
        {
            Set<LogicalExpression> resultingExpressions = new HashSet<LogicalExpression>();
            Binary rule = (Binary)expression;
            Binary innerRule = (Binary)rule.getArgument(0);
            Binary conjunction = leFactory.createBinary(CompoundExpression.AND, innerRule.getArgument(1), rule.getArgument(1));
            resultingExpressions.add(leFactory.createBinary(CompoundExpression.IMPLIEDBY, innerRule.getArgument(0), conjunction));
            return resultingExpressions;
        }
    }

    public class SplitConjunction implements TransformationRule
    {
        public boolean isApplicable(LogicalExpression expression)
        {
            try
            {
                CompoundExpression compound = (CompoundExpression)expression;
                if(compound.getOperator() == CompoundExpression.AND)
                {
                    return true;
                }
            } catch(ClassCastException e)
            {
            }
            return false;
        }

        public Set<LogicalExpression> apply(LogicalExpression expression)
        {
            Set<LogicalExpression> resultingExpressions = new HashSet<LogicalExpression>();
            Binary conjunction = (Binary)expression;
            LogicalExpression leftArg = conjunction.getArgument(0);
            LogicalExpression rightArg = conjunction.getArgument(1);
            resultingExpressions.add(leftArg);
            resultingExpressions.add(rightArg);
            return resultingExpressions;
        }
    }
}
