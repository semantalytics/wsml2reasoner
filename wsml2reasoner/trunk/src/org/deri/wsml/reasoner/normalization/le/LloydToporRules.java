package org.deri.wsml.reasoner.normalization.le;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.omwg.logexpression.Binary;
import org.omwg.logexpression.CompoundExpression;
import org.omwg.logexpression.LogicalExpression;
import org.omwg.logexpression.Unary;

public class LloydToporRules extends FixedNormalizationRules
{
    protected static LloydToporRules instance;

    private LloydToporRules()
    {
        super();
        rules.add(new SplitDisjunctiveBody());
        rules.add(new SplitConstraint());
        rules.add(new SplitConjunctiveHead());
        rules.add(new TransformNestedImplication());
        rules.add(new SplitConjunction());
        rules.add(new TransformImplication());
    }

    public static LloydToporRules instantiate()
    {
        if(instance == null)
        {
            instance = new LloydToporRules();
        }
        return instance;
    }

    public String getDescription()
    {
        String resultString = new String();
        for(Object object : rules)
        {
            StringTokenizer ruleNameTokenizer = new StringTokenizer(object.getClass().getName().toString(), "$");
            ruleNameTokenizer.nextToken();
            resultString += ruleNameTokenizer.nextToken() + "\n";
            resultString += object.toString() + "\n";
        }
        return resultString;
    }

    public class SplitConjunctiveHead implements TransformationRule
    {
        public boolean isApplicable(LogicalExpression expression)
        {
            try
            {
                CompoundExpression compound = (CompoundExpression)expression;
                if(compound.getOperator() == CompoundExpression.LP_IMPL)
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
            resultingExpressions.add(leFactory.createBinary(CompoundExpression.LP_IMPL, conjunction.getArgument(0), rule.getArgument(1)));
            resultingExpressions.add(leFactory.createBinary(CompoundExpression.LP_IMPL, conjunction.getArgument(1), rule.getArgument(1)));
            return resultingExpressions;
        }

        public String toString()
        {
            return "A1 and ... and An :- B\n\t=>\n A1 :- B\n\t...\n An :- B\n";
        }
    }

    public class SplitDisjunctiveBody implements TransformationRule
    {
        public boolean isApplicable(LogicalExpression expression)
        {
            try
            {
                CompoundExpression compound = (CompoundExpression)expression;
                if(compound.getOperator() == CompoundExpression.LP_IMPL)
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
            resultingExpressions.add(leFactory.createBinary(CompoundExpression.LP_IMPL, rule.getArgument(0), disjunction.getArgument(0)));
            resultingExpressions.add(leFactory.createBinary(CompoundExpression.LP_IMPL, rule.getArgument(0), disjunction.getArgument(1)));
            return resultingExpressions;
        }

        public String toString()
        {
            return "A :- B1 or ... Bn\n\t=>\n A :- B1\n\t...\n A :- Bn\n";
        }
    }

    public class TransformNestedImplication implements TransformationRule
    {
        public boolean isApplicable(LogicalExpression expression)
        {
            try
            {
                CompoundExpression compound = (CompoundExpression)expression;
                if(compound.getOperator() == CompoundExpression.LP_IMPL)
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
            resultingExpressions.add(leFactory.createBinary(CompoundExpression.LP_IMPL, innerRule.getArgument(0), conjunction));
            return resultingExpressions;
        }

        public String toString()
        {
            return "A1 impliedBy A2 :- B\n\t=>\n A1 :- A2 and B\n";
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

        public String toString()
        {
            return "A1 and ... and An \n\t=>\n A1\n\t...\n An\n";
        }
    }

    public class TransformImplication implements TransformationRule
    {
        public boolean isApplicable(LogicalExpression expression)
        {
            try
            {
                CompoundExpression compound = (CompoundExpression)expression;
                if(compound.getOperator() == CompoundExpression.IMPLIEDBY)
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
            Binary implication = (Binary)expression;
            Binary rule = leFactory.createBinary(CompoundExpression.LP_IMPL, implication.getArgument(0), implication.getArgument(1));
            resultingExpressions.add(rule);
            return resultingExpressions;
        }

        public String toString()
        {
            return "A1 impliedBy A2\n\t=>\n A1 :- A2\n";
        }
    }

    public class SplitConstraint implements TransformationRule
    {
        public boolean isApplicable(LogicalExpression expression)
        {
            try
            {
                CompoundExpression compound = (CompoundExpression)expression;
                if(compound.getOperator() == CompoundExpression.CONSTRAINT)
                {
                    CompoundExpression disjunction = (CompoundExpression)compound.getArgument(0);
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
            Unary constraint = (Unary)expression;
            Binary disjunction = (Binary)constraint.getArgument(0);
            resultingExpressions.add(leFactory.createUnary(CompoundExpression.CONSTRAINT, disjunction.getArgument(0)));
            resultingExpressions.add(leFactory.createUnary(CompoundExpression.CONSTRAINT, disjunction.getArgument(1)));
            return resultingExpressions;
        }

        public String toString()
        {
            return "!- B1 or ... or Bn\n\t=>\n !- B1\n\t...\n !- Bn\n";
        }
    }
}
