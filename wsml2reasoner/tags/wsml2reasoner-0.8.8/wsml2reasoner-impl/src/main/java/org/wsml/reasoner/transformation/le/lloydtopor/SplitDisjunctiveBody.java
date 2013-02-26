package org.wsml.reasoner.transformation.le.lloydtopor;

import java.util.HashSet;
import java.util.Set;

import org.omwg.logicalexpression.Disjunction;
import org.omwg.logicalexpression.LogicProgrammingRule;
import org.omwg.logicalexpression.LogicalExpression;
import org.wsml.reasoner.transformation.le.TransformationRule;
import org.wsmo.factory.FactoryContainer;
import org.wsmo.factory.LogicalExpressionFactory;

public class SplitDisjunctiveBody implements TransformationRule {

	private LogicalExpressionFactory leFactory;

	public SplitDisjunctiveBody(FactoryContainer wsmoManager) {
		this.leFactory = wsmoManager.getLogicalExpressionFactory();
	}

	public boolean isApplicable(LogicalExpression expression) {
		if (expression instanceof LogicProgrammingRule) {
			return ((LogicProgrammingRule) expression).getRightOperand() instanceof Disjunction;
		}
		return false;
	}

	public Set<LogicalExpression> apply(LogicalExpression expression) {
		Set<LogicalExpression> resultingExpressions = new HashSet<LogicalExpression>();
		Disjunction disjunction = (Disjunction) ((LogicProgrammingRule) expression)
				.getRightOperand();
		resultingExpressions.add(leFactory.createLogicProgrammingRule(
				((LogicProgrammingRule) expression).getLeftOperand(),
				disjunction.getLeftOperand()));
		resultingExpressions.add(leFactory.createLogicProgrammingRule(
				((LogicProgrammingRule) expression).getLeftOperand(),
				disjunction.getRightOperand()));
		return resultingExpressions;
	}

	public String toString() {
		return "A :- B1 or ... Bn\n\t=>\n A :- B1\n\t...\n A :- Bn\n";
	}
}