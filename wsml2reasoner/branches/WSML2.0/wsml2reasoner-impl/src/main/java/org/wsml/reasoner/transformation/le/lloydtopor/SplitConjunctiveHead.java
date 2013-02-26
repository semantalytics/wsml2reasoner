package org.wsml.reasoner.transformation.le.lloydtopor;

import java.util.HashSet;
import java.util.Set;

import org.omwg.logicalexpression.Conjunction;
import org.omwg.logicalexpression.LogicProgrammingRule;
import org.omwg.logicalexpression.LogicalExpression;
import org.wsml.reasoner.transformation.le.TransformationRule;
import org.wsmo.factory.FactoryContainer;
import org.wsmo.factory.LogicalExpressionFactory;

public class SplitConjunctiveHead implements TransformationRule {

	private LogicalExpressionFactory leFactory;

	public SplitConjunctiveHead(FactoryContainer wsmoManager) {
		this.leFactory = wsmoManager.getLogicalExpressionFactory();
	}

	public boolean isApplicable(LogicalExpression expression) {
		if (expression instanceof LogicProgrammingRule) {
			return ((LogicProgrammingRule) expression).getLeftOperand() instanceof Conjunction;
		}
		return false;
	}

	public Set<LogicalExpression> apply(LogicalExpression expression) {
		Set<LogicalExpression> resultingExpressions = new HashSet<LogicalExpression>();
		Conjunction conjunction = (Conjunction) ((LogicProgrammingRule) expression)
				.getLeftOperand();
		resultingExpressions.add(leFactory.createLogicProgrammingRule(
				conjunction.getLeftOperand(),
				((LogicProgrammingRule) expression).getRightOperand()));
		resultingExpressions.add(leFactory.createLogicProgrammingRule(
				conjunction.getRightOperand(),
				((LogicProgrammingRule) expression).getRightOperand()));
		return resultingExpressions;
	}

	public String toString() {
		return "A1 and ... and An :- B\n\t=>\n A1 :- B\n\t...\n An :- B\n";
	}
}