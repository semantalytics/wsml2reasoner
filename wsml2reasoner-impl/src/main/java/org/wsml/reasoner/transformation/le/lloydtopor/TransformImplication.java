package org.wsml.reasoner.transformation.le.lloydtopor;

import java.util.HashSet;
import java.util.Set;

import org.omwg.logicalexpression.InverseImplication;
import org.omwg.logicalexpression.LogicalExpression;
import org.wsml.reasoner.transformation.le.TransformationRule;
import org.wsmo.factory.FactoryContainer;
import org.wsmo.factory.LogicalExpressionFactory;

public class TransformImplication implements TransformationRule {

	private LogicalExpressionFactory leFactory;

	public TransformImplication(FactoryContainer wsmoManager) {
		this.leFactory = wsmoManager.getLogicalExpressionFactory();
	}

	public boolean isApplicable(LogicalExpression expression) {
		return expression instanceof InverseImplication;
	}

	public Set<LogicalExpression> apply(LogicalExpression expression) {
		Set<LogicalExpression> resultingExpressions = new HashSet<LogicalExpression>();
		resultingExpressions.add(leFactory.createLogicProgrammingRule(
				((InverseImplication) expression).getLeftOperand(),
				((InverseImplication) expression).getRightOperand()));
		return resultingExpressions;
	}

	public String toString() {
		return "A1 impliedBy A2\n\t=>\n A1 :- A2\n";
	}
}