package org.wsml.reasoner.transformation.le.lloydtopor;

import java.util.HashSet;
import java.util.Set;

import org.omwg.logicalexpression.Conjunction;
import org.omwg.logicalexpression.InverseImplication;
import org.omwg.logicalexpression.LogicProgrammingRule;
import org.omwg.logicalexpression.LogicalExpression;
import org.wsml.reasoner.transformation.le.TransformationRule;
import org.wsmo.factory.FactoryContainer;
import org.wsmo.factory.LogicalExpressionFactory;

public class TransformNestedImplication implements TransformationRule {

	private LogicalExpressionFactory leFactory;

	public TransformNestedImplication(FactoryContainer wsmoManager) {
		this.leFactory = wsmoManager.getLogicalExpressionFactory();
	}

	public boolean isApplicable(LogicalExpression expression) {
		if (expression instanceof LogicProgrammingRule) {
			return ((LogicProgrammingRule) expression).getLeftOperand() instanceof InverseImplication;
		}
		return false;
	}

	public Set<LogicalExpression> apply(LogicalExpression expression) {
		Set<LogicalExpression> resultingExpressions = new HashSet<LogicalExpression>();
		InverseImplication innerRule = (InverseImplication) ((LogicProgrammingRule) expression)
				.getLeftOperand();
		Conjunction conjunction = leFactory.createConjunction(
				innerRule.getRightOperand(),
				((LogicProgrammingRule) expression).getRightOperand());
		resultingExpressions.add(leFactory.createLogicProgrammingRule(
				innerRule.getLeftOperand(), conjunction));
		return resultingExpressions;
	}

	public String toString() {
		return "A1 impliedBy A2 :- B\n\t=>\n A1 :- A2 and B\n";
	}
}