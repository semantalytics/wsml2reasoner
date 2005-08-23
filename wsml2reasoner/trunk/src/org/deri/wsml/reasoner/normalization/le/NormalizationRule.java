package org.deri.wsml.reasoner.normalization.le;

import org.omwg.logexpression.LogicalExpression;

public interface NormalizationRule extends LEModificationRule
{
    public LogicalExpression apply(LogicalExpression expression);
}