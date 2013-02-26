package org.wsml.reasoner.transformation.le.negationpush;

import org.omwg.logicalexpression.Conjunction;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.NegationAsFailure;
import org.wsml.reasoner.transformation.le.NormalizationRule;
import org.wsmo.factory.FactoryContainer;
import org.wsmo.factory.LogicalExpressionFactory;

public class NegateConjunctionRule implements NormalizationRule {

	private LogicalExpressionFactory leFactory;

	public NegateConjunctionRule(FactoryContainer factory) {
		this.leFactory = factory.getLogicalExpressionFactory();
	}

	public boolean isApplicable(LogicalExpression expression) {
		if (expression instanceof NegationAsFailure) {
			return ((NegationAsFailure) expression).getOperand() instanceof Conjunction;
		}
		return false;
	}

	public LogicalExpression apply(LogicalExpression expression) {
		Conjunction conjunction = (Conjunction) ((NegationAsFailure) expression)
				.getOperand();
		return leFactory
				.createDisjunction(leFactory
						.createNegationAsFailure(conjunction.getLeftOperand()),
						leFactory.createNegationAsFailure(conjunction
								.getRightOperand()));
	}

	public String toString() {
		return "naf(A and B)\n\t=>\n naf A or naf B\n";
	}
}