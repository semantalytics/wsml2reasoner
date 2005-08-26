package org.deri.wsml.reasoner.normalization.le;

import org.omwg.logexpression.Binary;
import org.omwg.logexpression.CompoundExpression;
import org.omwg.logexpression.LogicalExpression;
import org.omwg.logexpression.Unary;

/**
 * This singleton class represents a set of normalization rules for pushing
 * negation-as-failure operators inside a logical expression into its
 * sub-expressions, such that the remaining occurrences of negation are all
 * atomic.
 * 
 * @author Stephan Grimm, FZI Karlsruhe
 */
public class NegationPushRules extends FixedModificationRules
{
    protected static NegationPushRules instance;

    private NegationPushRules()
    {
        super();
        rules.add(new DoubleNegationRule());
        rules.add(new NegateConjunctionRule());
        rules.add(new NegateDisjunctionRule());
    }

    public static NegationPushRules instantiate()
    {
        if(instance == null)
        {
            instance = new NegationPushRules();
        }
        return instance;
    }

    protected class DoubleNegationRule implements NormalizationRule
    {
        public LogicalExpression apply(LogicalExpression expression)
        {
            Unary outerNaf = (Unary)expression;
            Unary innerNaf = (Unary)(outerNaf.getArgument(0));
            return innerNaf.getArgument(0);
        }

        public boolean isApplicable(LogicalExpression expression)
        {
            if(expression instanceof CompoundExpression)
            {
                CompoundExpression compound = (CompoundExpression)expression;
                if(compound.getOperator() == CompoundExpression.NAF)
                {
                    if(compound.getArgument(0) instanceof CompoundExpression)
                    {
                        compound = (CompoundExpression)compound.getArgument(0);
                        return compound.getOperator() == CompoundExpression.NAF;
                    }
                }
            }
            return false;
        }

        public String toString()
        {
            return "naf(naf A)\n\t=>\n A\n";
        }
    }

    protected class NegateConjunctionRule implements NormalizationRule
    {
        public LogicalExpression apply(LogicalExpression expression)
        {
            Unary naf = (Unary)expression;
            Binary conjunction = (Binary)(naf.getArgument(0));
            LogicalExpression leftArg = leFactory.createUnary(Unary.NAF, conjunction.getArgument(0));
            LogicalExpression rightArg = leFactory.createUnary(Unary.NAF, conjunction.getArgument(1));
            LogicalExpression disjunction = leFactory.createBinary(Binary.OR, leftArg, rightArg);
            return disjunction;
        }

        public boolean isApplicable(LogicalExpression expression)
        {
            if(expression instanceof CompoundExpression)
            {
                CompoundExpression compound = (CompoundExpression)expression;
                if(compound.getOperator() == CompoundExpression.NAF)
                {
                    if(compound.getArgument(0) instanceof CompoundExpression)
                    {
                        compound = (CompoundExpression)compound.getArgument(0);
                        return compound.getOperator() == CompoundExpression.AND;
                    }
                }
            }
            return false;
        }

        public String toString()
        {
            return "naf(A and B)\n\t=>\n naf A or naf B\n";
        }
    }

    protected class NegateDisjunctionRule implements NormalizationRule
    {
        public LogicalExpression apply(LogicalExpression expression)
        {
            Unary naf = (Unary)expression;
            Binary disjunction = (Binary)(naf.getArgument(0));
            LogicalExpression leftArg = leFactory.createUnary(Unary.NAF, disjunction.getArgument(0));
            LogicalExpression rightArg = leFactory.createUnary(Unary.NAF, disjunction.getArgument(1));
            LogicalExpression conjunction = leFactory.createBinary(Binary.AND, leftArg, rightArg);
            return conjunction;
        }

        public boolean isApplicable(LogicalExpression expression)
        {
            if(expression instanceof CompoundExpression)
            {
                CompoundExpression compound = (CompoundExpression)expression;
                if(compound.getOperator() == CompoundExpression.NAF)
                {
                    if(compound.getArgument(0) instanceof CompoundExpression)
                    {
                        compound = (CompoundExpression)compound.getArgument(0);
                        return compound.getOperator() == CompoundExpression.OR;
                    }
                }
            }
            return false;
        }

        public String toString()
        {
            return "naf(A or B)\n\t=>\n naf A and naf B\n";
        }
    }
}
