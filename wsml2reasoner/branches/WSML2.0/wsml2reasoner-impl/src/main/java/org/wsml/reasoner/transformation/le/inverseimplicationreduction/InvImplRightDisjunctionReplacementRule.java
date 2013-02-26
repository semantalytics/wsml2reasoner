package org.wsml.reasoner.transformation.le.inverseimplicationreduction;

import org.omwg.logicalexpression.Disjunction;
import org.omwg.logicalexpression.InverseImplication;
import org.omwg.logicalexpression.LogicalExpression;
import org.wsml.reasoner.transformation.le.NormalizationRule;
import org.wsmo.factory.FactoryContainer;
import org.wsmo.factory.LogicalExpressionFactory;

public class InvImplRightDisjunctionReplacementRule extends ReplacementRule
		implements NormalizationRule {

	private LogicalExpressionFactory leFactory;

	public InvImplRightDisjunctionReplacementRule(FactoryContainer wsmoManager) {
		this.leFactory = wsmoManager.getLogicalExpressionFactory();
	}

	public LogicalExpression apply(LogicalExpression expression) {
		InverseImplication invImpl = (InverseImplication) expression;
		LogicalExpression impliedBy1 = leFactory.createInverseImplication(
				invImpl.getLeftOperand(),
				((Disjunction) invImpl.getRightOperand()).getLeftOperand());
		LogicalExpression impliedBy2 = leFactory.createInverseImplication(
				invImpl.getLeftOperand(),
				((Disjunction) invImpl.getRightOperand()).getRightOperand());
		return leFactory.createConjunction(impliedBy1, impliedBy2);
	}

	public boolean isApplicable(LogicalExpression expression) {
		if (expression instanceof InverseImplication
				&& ((InverseImplication) expression).getRightOperand() instanceof Disjunction) {
			return !checkForDependencies((Disjunction) ((InverseImplication) expression)
					.getRightOperand());
		}
		return false;
	}

	public String toString() {
		return "A impliedBy B or C\n\t=>\n (A impliedBy B) and (A impliedBy C)\n";
	}
}