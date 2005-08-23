package org.deri.wsml.reasoner.normalization.le;

import java.util.Set;
import org.omwg.logexpression.LogicalExpression;

public interface LogicalExpressionTransformer
{
    public Set<LogicalExpression> transform(LogicalExpression expression);
}
