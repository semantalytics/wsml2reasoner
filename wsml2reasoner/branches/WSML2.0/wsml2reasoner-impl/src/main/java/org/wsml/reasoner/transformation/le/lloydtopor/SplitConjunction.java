package org.wsml.reasoner.transformation.le.lloydtopor;

import java.util.HashSet;
import java.util.Set;

import org.omwg.logicalexpression.Conjunction;
import org.omwg.logicalexpression.LogicalExpression;
import org.wsml.reasoner.transformation.le.TransformationRule;

public class SplitConjunction implements TransformationRule {

	public boolean isApplicable(LogicalExpression expression) {
		return expression instanceof Conjunction;
	}

	public Set<LogicalExpression> apply(LogicalExpression expression) {
		Set<LogicalExpression> resultingExpressions = new HashSet<LogicalExpression>();
		resultingExpressions.add(((Conjunction) expression).getLeftOperand());
		resultingExpressions.add(((Conjunction) expression).getRightOperand());
		return resultingExpressions;
	}

	public String toString() {
		return "A1 and ... and An \n\t=>\n A1\n\t...\n An\n";
	}
}