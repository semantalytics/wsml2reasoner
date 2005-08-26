package org.deri.wsml.reasoner.normalization.le;

import org.omwg.logexpression.LogicalExpression;

/**
 * A logical expression normalizer normalizes a single logical expression. The
 * result should be created such that the original expression remains unchanged.
 * 
 * @author Stephan Grimm, FZI Karlsruhe
 */
public interface LogicalExpressionNormalizer
{
    /**
     * This method normalizes a single logical expression.
     * @param expression
     * @return The normalized form of expression (as copy)
     */
    public LogicalExpression normalize(LogicalExpression expression);
}
