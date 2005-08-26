package org.deri.wsml.reasoner.normalization.le;

import org.omwg.logexpression.LogicalExpression;

/**
 * A modification rule for a logical expression specifies the behaviour of how
 * to perform modifications to this expression. For a given logical expression
 * le it can decide whether this behaviour is applicable to le.
 * 
 * @author Stephan Grimm, FZI Karlsruhe
 */
public interface LEModificationRule
{
    /**
     * This method decides whether the behaviour specified by this modification
     * rule is applicable to a given logical expression.
     * 
     * @param expression
     * @return true if the rule is applicable, falsr otherwise
     */
    public boolean isApplicable(LogicalExpression expression);
}
