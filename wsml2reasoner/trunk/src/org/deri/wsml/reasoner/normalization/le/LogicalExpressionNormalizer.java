package org.deri.wsml.reasoner.normalization.le;

import org.omwg.logexpression.LogicalExpression;

public interface LogicalExpressionNormalizer
{
    public LogicalExpression normalize(LogicalExpression expression);
}
