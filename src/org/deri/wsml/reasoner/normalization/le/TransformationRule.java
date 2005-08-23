package org.deri.wsml.reasoner.normalization.le;

import java.util.Set;
import org.omwg.logexpression.LogicalExpression;

public interface TransformationRule extends LEModificationRule
{
    public Set<LogicalExpression> apply(LogicalExpression expression);
}