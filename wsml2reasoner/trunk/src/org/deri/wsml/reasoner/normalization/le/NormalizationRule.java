package org.deri.wsml.reasoner.normalization.le;

import org.omwg.logexpression.LogicalExpression;

/**
 * A normalization rule specifies the behaviour of how to normalize a logical expression.
 * @author Stephan Grimm, FZI Karlsruhe 
 */
public interface NormalizationRule extends LEModificationRule
{
    /**
     * This method applies the normalization behaviour specified by this normalization rule to a logical expression.
     * @param expression
     * @return a logical expressions resulting from the normalization
     */
    public LogicalExpression apply(LogicalExpression expression);
}