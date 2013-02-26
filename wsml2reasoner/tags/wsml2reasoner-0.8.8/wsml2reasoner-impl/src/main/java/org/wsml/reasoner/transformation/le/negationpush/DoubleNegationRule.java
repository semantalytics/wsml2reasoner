package org.wsml.reasoner.transformation.le.negationpush;

import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.NegationAsFailure;
import org.wsml.reasoner.transformation.le.NormalizationRule;

public class DoubleNegationRule implements NormalizationRule {

	public DoubleNegationRule() {
	}

	public boolean isApplicable(LogicalExpression expression) {
		if (expression instanceof NegationAsFailure) {
			return ((NegationAsFailure) expression).getOperand() instanceof NegationAsFailure;
		}
		return false;
	}

	public LogicalExpression apply(LogicalExpression expression) {
		return ((NegationAsFailure) ((NegationAsFailure) expression)
				.getOperand()).getOperand();
	}

	public String toString() {
		return "naf(naf A)\n\t=>\n A\n";
	}
}