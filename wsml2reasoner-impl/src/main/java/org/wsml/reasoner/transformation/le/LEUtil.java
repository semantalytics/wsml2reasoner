package org.wsml.reasoner.transformation.le;

import java.util.Collection;

import org.omwg.logicalexpression.Conjunction;
import org.omwg.logicalexpression.Disjunction;
import org.omwg.logicalexpression.LogicalExpression;
import org.wsmo.factory.LogicalExpressionFactory;

public class LEUtil {

	protected static final byte CONJUNCTION = 0;
	protected static final byte DISJUNCTION = 1;

	public static Conjunction buildNaryConjunction(
			LogicalExpressionFactory leFactory,
			Collection<? extends LogicalExpression> expressions) {
		return (Conjunction) buildNary(leFactory, CONJUNCTION, expressions);
	}

	public static Disjunction buildNaryDisjunction(
			LogicalExpressionFactory leFactory,
			Collection<? extends LogicalExpression> expressions) {
		return (Disjunction) buildNary(leFactory, DISJUNCTION, expressions);
	}

	public static LogicalExpression buildNary(
			LogicalExpressionFactory leFactory, byte operationCode,
			Collection<? extends LogicalExpression> expressions) {
		LogicalExpression result = null;
		for (LogicalExpression le : expressions) {
			if (result == null) {
				result = le;
			} else if (operationCode == CONJUNCTION) {
				result = leFactory.createConjunction(result, le);
			} else if (operationCode == DISJUNCTION) {
				result = leFactory.createDisjunction(result, le);
			}
		}
		return result;
	}
}
