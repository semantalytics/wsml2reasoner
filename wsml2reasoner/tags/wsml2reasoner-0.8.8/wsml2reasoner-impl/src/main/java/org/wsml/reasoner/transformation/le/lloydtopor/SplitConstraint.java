package org.wsml.reasoner.transformation.le.lloydtopor;

import java.util.HashSet;
import java.util.Set;

import org.omwg.logicalexpression.Constraint;
import org.omwg.logicalexpression.Disjunction;
import org.omwg.logicalexpression.LogicalExpression;
import org.wsml.reasoner.transformation.le.TransformationRule;
import org.wsmo.factory.FactoryContainer;
import org.wsmo.factory.LogicalExpressionFactory;

public class SplitConstraint implements TransformationRule {

	private LogicalExpressionFactory leFactory;

	public SplitConstraint(FactoryContainer wsmoManager) {
		this.leFactory = wsmoManager.getLogicalExpressionFactory();
	}

	public boolean isApplicable(LogicalExpression expression) {
		if (expression instanceof Constraint) {
			return ((Constraint) expression).getOperand() instanceof Disjunction;
		}
		return false;
	}

	public Set<LogicalExpression> apply(LogicalExpression expression) {
		Set<LogicalExpression> resultingExpressions = new HashSet<LogicalExpression>();
		Disjunction disjunction = (Disjunction) ((Constraint) expression)
				.getOperand();
		resultingExpressions.add(leFactory.createConstraint(disjunction
				.getLeftOperand()));
		resultingExpressions.add(leFactory.createConstraint(disjunction
				.getRightOperand()));
		return resultingExpressions;
	}

	public String toString() {
		return "!- B1 or ... or Bn\n\t=>\n !- B1\n\t...\n !- Bn\n";
	}
}