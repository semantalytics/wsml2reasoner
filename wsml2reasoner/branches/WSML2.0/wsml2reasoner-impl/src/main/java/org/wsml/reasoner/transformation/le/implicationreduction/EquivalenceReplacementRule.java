package org.wsml.reasoner.transformation.le.implicationreduction;

import org.omwg.logicalexpression.Equivalence;
import org.omwg.logicalexpression.LogicalExpression;
import org.wsml.reasoner.transformation.le.NormalizationRule;
import org.wsmo.factory.FactoryContainer;
import org.wsmo.factory.LogicalExpressionFactory;

public class EquivalenceReplacementRule implements NormalizationRule {

	private LogicalExpressionFactory leFactory;

	public EquivalenceReplacementRule(FactoryContainer factory) {
		this.leFactory = factory.getLogicalExpressionFactory();
	}

	public LogicalExpression apply(LogicalExpression expression) {
		Equivalence equivalence = (Equivalence) expression;
		LogicalExpression impliedBy1 = leFactory.createInverseImplication(
				equivalence.getRightOperand(), equivalence.getLeftOperand());
		LogicalExpression impliedBy2 = leFactory.createInverseImplication(
				equivalence.getLeftOperand(), equivalence.getRightOperand());
		return leFactory.createConjunction(impliedBy1, impliedBy2);
	}

	public boolean isApplicable(LogicalExpression expression) {
		return expression instanceof Equivalence;
	}

	public String toString() {
		return "A equivalent B\n\t=>\n (A impliedBy B) and (B impliedBy A)\n";
	}
}