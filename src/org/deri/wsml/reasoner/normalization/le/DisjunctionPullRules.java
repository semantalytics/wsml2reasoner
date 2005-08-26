package org.deri.wsml.reasoner.normalization.le;

import org.omwg.logexpression.Binary;
import org.omwg.logexpression.CompoundExpression;
import org.omwg.logexpression.LogicalExpression;
import org.omwg.logexpression.Unary;

public class DisjunctionPullRules extends FixedNormalizationRules
{
    protected static DisjunctionPullRules instance; 
    
    private DisjunctionPullRules()
    {       
        super();
        rules.add(new DoubleNegationRule());
        rules.add(new NegateConjunctionRule());
        rules.add(new NegateDisjunctionRule());
    }
    
    public static DisjunctionPullRules instantiate()
    {
        if(instance == null)
        {
            instance = new DisjunctionPullRules();
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
    }

    protected class NegateConjunctionRule implements NormalizationRule
    {
        public LogicalExpression apply(LogicalExpression expression)
        {
            Unary naf = (Unary)expression;
            Binary conjunction = (Binary)(naf.getArgument(0));
            LogicalExpression leftArg = leFactory.createUnary(Unary.NAF,conjunction.getArgument(0));
            LogicalExpression rightArg = leFactory.createUnary(Unary.NAF,conjunction.getArgument(1));
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
    }
    
    protected class NegateDisjunctionRule implements NormalizationRule
    {
        public LogicalExpression apply(LogicalExpression expression)
        {
            Unary naf = (Unary)expression;
            Binary disjunction = (Binary)(naf.getArgument(0));
            LogicalExpression leftArg = leFactory.createUnary(Unary.NAF,disjunction.getArgument(0));
            LogicalExpression rightArg = leFactory.createUnary(Unary.NAF,disjunction.getArgument(1));
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
    }
}
