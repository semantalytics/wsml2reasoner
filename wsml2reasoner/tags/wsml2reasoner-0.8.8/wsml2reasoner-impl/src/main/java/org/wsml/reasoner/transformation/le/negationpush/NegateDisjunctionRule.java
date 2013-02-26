package org.wsml.reasoner.transformation.le.negationpush;

import org.omwg.logicalexpression.Disjunction;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.NegationAsFailure;
import org.wsml.reasoner.transformation.le.NormalizationRule;
import org.wsmo.factory.FactoryContainer;
import org.wsmo.factory.LogicalExpressionFactory;

public class NegateDisjunctionRule implements NormalizationRule {

	private LogicalExpressionFactory leFactory;

	public NegateDisjunctionRule(FactoryContainer factory) {
		this.leFactory = factory.getLogicalExpressionFactory();
	}

	public LogicalExpression apply(LogicalExpression expression) {
		Disjunction disjunction = (Disjunction) (((NegationAsFailure) expression)
				.getOperand());
		return leFactory
				.createConjunction(leFactory
						.createNegationAsFailure(disjunction.getLeftOperand()),
						leFactory.createNegationAsFailure(disjunction
								.getRightOperand()));
	}

	public boolean isApplicable(LogicalExpression expression) {
		if (expression instanceof NegationAsFailure) {
			return ((NegationAsFailure) expression).getOperand() instanceof Disjunction;
		}
		return false;
	}

	public String toString() {
		return "naf(A or B)\n\t=>\n naf A and naf B\n";
	}
}