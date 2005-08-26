package org.deri.wsml.reasoner.normalization.le;

import org.omwg.logexpression.Binary;
import org.omwg.logexpression.CompoundExpression;
import org.omwg.logexpression.LogicalExpression;

/**
 * This singleton class represents a set of normalization rules for replacing
 * equivalences and right-implications in logical expressions by
 * left-implications.
 * 
 * @author Stephan Grimm, FZI Karlsruhe
 */
public class ImplicationReductionRules extends FixedModificationRules
{
    protected static ImplicationReductionRules instance;

    private ImplicationReductionRules()
    {
        super();
        rules.add(new EquivalenceReplacementRule());
        rules.add(new RightImplicationReplacementRule());
    }

    public static ImplicationReductionRules instantiate()
    {
        if(instance == null)
        {
            instance = new ImplicationReductionRules();
        }
        return instance;
    }

    protected class EquivalenceReplacementRule implements NormalizationRule
    {
        public LogicalExpression apply(LogicalExpression expression)
        {
            Binary equivalence = (Binary)expression;
            LogicalExpression leftArg = equivalence.getArgument(0);
            LogicalExpression rightArg = equivalence.getArgument(1);
            LogicalExpression impliedBy1 = leFactory.createBinary(Binary.IMPLIEDBY, rightArg, leftArg);
            LogicalExpression impliedBy2 = leFactory.createBinary(Binary.IMPLIEDBY, leftArg, rightArg);
            LogicalExpression and = leFactory.createBinary(Binary.AND, impliedBy1, impliedBy2);
            return and;
        }

        public boolean isApplicable(LogicalExpression expression)
        {
            if(expression instanceof CompoundExpression)
            {
                return ((CompoundExpression)expression).getOperator() == CompoundExpression.EQUIVALENT;
            }
            return false;
        }

        public String toString()
        {
            return "A equivalent B\n\t=>\n (A impliedBy B) and (B impliedBy A)\n";
        }
    }

    protected class RightImplicationReplacementRule implements NormalizationRule
    {
        public LogicalExpression apply(LogicalExpression expression)
        {
            Binary equivalence = (Binary)expression;
            LogicalExpression leftArg = equivalence.getArgument(0);
            LogicalExpression rightArg = equivalence.getArgument(1);
            LogicalExpression impliedBy = leFactory.createBinary(Binary.IMPLIEDBY, rightArg, leftArg);
            return impliedBy;
        }

        public boolean isApplicable(LogicalExpression expression)
        {
            if(expression instanceof CompoundExpression)
            {
                return ((CompoundExpression)expression).getOperator() == CompoundExpression.IMPLIES;
            }
            return false;
        }

        public String toString()
        {
            return "A implies B\n\t=>\n B impliedBy A\n";
        }
    }
}
