package org.deri.wsml.reasoner.normalization.le;

import java.util.Set;
import org.omwg.logexpression.LogicalExpression;

/**
 * A logical expression transformer transforms a single logical expression into a set of resulting logical expressions. The
 * result should be created such that the original expression remains unchanged.
 * 
 * @author Stephan Grimm, FZI Karlsruhe
 */
public interface LogicalExpressionTransformer
{
    /**
     * This method transforms a single logical expression into a set of resulting expressions.
     * @param expression
     * @return a set of logical expression resulting from the transformation
     */
    public Set<LogicalExpression> transform(LogicalExpression expression);
}
