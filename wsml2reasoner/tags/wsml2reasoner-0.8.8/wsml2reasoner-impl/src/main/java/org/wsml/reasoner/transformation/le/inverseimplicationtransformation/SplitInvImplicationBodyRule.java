package org.wsml.reasoner.transformation.le.inverseimplicationtransformation;

import java.util.HashSet;
import java.util.Set;

import org.omwg.logicalexpression.InverseImplication;
import org.omwg.logicalexpression.LogicProgrammingRule;
import org.omwg.logicalexpression.LogicalExpression;
import org.wsml.reasoner.transformation.le.TransformationRule;
import org.wsml.reasoner.transformation.le.inverseimplicationreduction.ReplacementRule;
import org.wsmo.factory.FactoryContainer;
import org.wsmo.factory.LogicalExpressionFactory;

public class SplitInvImplicationBodyRule extends ReplacementRule implements
		TransformationRule {

	private LogicalExpressionFactory leFactory;

	public SplitInvImplicationBodyRule(FactoryContainer wsmoManager) {
		this.leFactory = wsmoManager.getLogicalExpressionFactory();
	}

	public boolean isApplicable(LogicalExpression expression) {
		if (expression instanceof LogicProgrammingRule) {
			return ((LogicProgrammingRule) expression).getRightOperand() instanceof InverseImplication;
		}
		return false;
	}

	public Set<LogicalExpression> apply(LogicalExpression expression) {
		Set<LogicalExpression> resultingExpressions = new HashSet<LogicalExpression>();
		InverseImplication invImpl = (InverseImplication) ((LogicProgrammingRule) expression)
				.getRightOperand();
		LogicalExpression e1 = leFactory.createLogicProgrammingRule(
				((LogicProgrammingRule) expression).getLeftOperand(),
				leFactory.createNegationAsFailure(invImpl.getRightOperand()));
		LogicalExpression e2 = leFactory.createLogicProgrammingRule(
				((LogicProgrammingRule) expression).getLeftOperand(),
				leFactory.createConjunction(invImpl.getLeftOperand(),
						invImpl.getRightOperand()));
		resultingExpressions.add(e1);
		resultingExpressions.add(e2);
		return resultingExpressions;
	}

	public String toString() {
		return "A :- B1 impliedBy B2\n\t=>\n A :- not B2\n\t \n A :- B1 and B2n\n";
	}
}