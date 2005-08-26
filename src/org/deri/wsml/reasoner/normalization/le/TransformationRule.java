package org.deri.wsml.reasoner.normalization.le;

import java.util.Set;
import org.omwg.logexpression.LogicalExpression;

/**
 * A transformation rule specifies the behaviour of how to transform a single logical expression into a set of logical expressions.
 * @author Stephan Grimm, FZI Karlsruhe 
 */
public interface TransformationRule extends LEModificationRule
{
    /**
     * This method applies the transformation behaviour specified by this transformation rule to a logical expression.
     * @param expression
     * @return a set of logical expressions resulting from the transformation 
     */
    public Set<LogicalExpression> apply(LogicalExpression expression);
}