package org.deri.wsml.reasoner.normalization.le;

import org.omwg.logexpression.LogicalExpression;

public interface LEModificationRule
{
    public boolean isApplicable(LogicalExpression expression);
}
