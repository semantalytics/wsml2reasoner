/**
 * WSML Reasoner Implementation.
 *
 * Copyright (c) 2005, University of Innsbruck, Austria.
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
package org.deri.wsml.reasoner.normalization.le;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.omwg.logexpression.Binary;
import org.omwg.logexpression.CompoundExpression;
import org.omwg.logexpression.LogicalExpression;
import org.omwg.logexpression.Unary;

/**
 * This singleton class represents a set of transformation rules for splitting
 * complex LP-rules, constraints and facts into simple datalog-style rule,
 * according to the Lloyd-Topor transformation.
 * 
 * @author Stephan Grimm, FZI Karlsruhe
 */
public class LloydToporRules extends FixedModificationRules
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
