package org.wsml.reasoner.transformation.le.implicationreduction;

import org.omwg.logicalexpression.Implication;
import org.omwg.logicalexpression.LogicalExpression;
import org.wsml.reasoner.transformation.le.NormalizationRule;
import org.wsmo.factory.FactoryContainer;
import org.wsmo.factory.LogicalExpressionFactory;

public class RightImplicationReplacementRule implements NormalizationRule {

	private LogicalExpressionFactory leFactory;

	public RightImplicationReplacementRule(FactoryContainer factory) {
		this.leFactory = factory.getLogicalExpressionFactory();
	}

	public LogicalExpression apply(LogicalExpression expression) {
		Implication implication = (Implication) expression;
		return leFactory.createInverseImplication(
				implication.getRightOperand(), implication.getLeftOperand());
	}

	public boolean isApplicable(LogicalExpression expression) {
		return expression instanceof Implication;
	}

	public String toString() {
		return "A implies B\n\t=>\n B impliedBy A\n";
	}
}