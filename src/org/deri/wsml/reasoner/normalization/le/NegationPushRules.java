package org.deri.wsml.reasoner.normalization.le;

import org.omwg.logexpression.Binary;
import org.omwg.logexpression.CompoundExpression;
import org.omwg.logexpression.LogicalExpression;

public class NegationPushRules extends FixedNormalizationRules
{
    protected static NegationPushRules instance; 
    
    private NegationPushRules()
    {       
        super();
        rules.add(new LeftDisjunctionPullRule());
        rules.add(new RightDisjunctionPullRule());
    }
    
    public static NegationPushRules instantiate()
    {
        if(instance == null)
        {
            instance = new NegationPushRules();
        }
        return instance;
    }
    
    protected class LeftDisjunctionPullRule implements NormalizationRule
    {
        public LogicalExpression apply(LogicalExpression expression)
        {
            Binary conjunction = (Binary)expression;
            Binary disjunction = (Binary)conjunction.getArgument(0);
            LogicalExpression leftConjunction = leFactory.createBinary(Binary.AND, disjunction.getArgument(0), conjunction.getArgument(1));
            LogicalExpression rightConjunction = leFactory.createBinary(Binary.AND, disjunction.getArgument(1), conjunction.getArgument(1));
            return leFactory.createBinary(Binary.OR, leftConjunction, rightConjunction);
        }

        public boolean isApplicable(LogicalExpression expression)
        {
            if(expression instanceof CompoundExpression)
            {
                CompoundExpression compound = (CompoundExpression)expression;
                if(compound.getOperator() == CompoundExpression.AND)
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
    
    protected class RightDisjunctionPullRule implements NormalizationRule
    {
        public LogicalExpression apply(LogicalExpression expression)
        {
            Binary conjunction = (Binary)expression;
            Binary disjunction = (Binary)conjunction.getArgument(1);
            LogicalExpression leftConjunction = leFactory.createBinary(Binary.AND, disjunction.getArgument(0), conjunction.getArgument(0));
            LogicalExpression rightConjunction = leFactory.createBinary(Binary.AND, disjunction.getArgument(1), conjunction.getArgument(0));
            return leFactory.createBinary(Binary.OR, leftConjunction, rightConjunction);
        }

        public boolean isApplicable(LogicalExpression expression)
        {
            if(expression instanceof CompoundExpression)
            {
                CompoundExpression compound = (CompoundExpression)expression;
                if(compound.getOperator() == CompoundExpression.AND)
                {
                    if(compound.getArgument(1) instanceof CompoundExpression)
                    {
                        compound = (CompoundExpression)compound.getArgument(1);
                        return compound.getOperator() == CompoundExpression.OR;
                    }
                }
            }
            return false;
        }
    }
}
