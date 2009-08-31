/**
 * WSML2Reasoner
 * An extensible framework for reasoning with WSML ontologies.
 * 
 * Copyright (C) 2008 Semantic Technology Institute (STI) Innsbruck, 
 * University of Innsbruck, Technikerstrasse 21a, 6020 Innsbruck, Austria.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 */

package org.wsml.reasoner.transformation.le.inverseimplicationtransformation;

import java.util.HashSet;
import java.util.Set;

import org.omwg.logicalexpression.InverseImplication;
import org.omwg.logicalexpression.LogicProgrammingRule;
import org.omwg.logicalexpression.LogicalExpression;
import org.wsml.reasoner.transformation.le.TransformationRule;
import org.wsml.reasoner.transformation.le.inverseimplicationreduction.ReplacementRule;
import org.wsmo.factory.Factory;
import org.wsmo.factory.LogicalExpressionFactory;

public class SplitInvImplicationBodyRule extends ReplacementRule implements
		TransformationRule {

	private LogicalExpressionFactory leFactory;

	public SplitInvImplicationBodyRule(Factory wsmoManager) {
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
				((LogicProgrammingRule) expression).getLeftOperand(), leFactory.createNegationAsFailure(invImpl.getRightOperand()));
		LogicalExpression e2 = leFactory.createLogicProgrammingRule(
				((LogicProgrammingRule) expression).getLeftOperand(), leFactory
						.createConjunction(invImpl.getLeftOperand(), invImpl
								.getRightOperand()));
		resultingExpressions.add(e1);
		resultingExpressions.add(e2);
		return resultingExpressions;
	}

	public String toString() {
		return "A :- B1 impliedBy B2\n\t=>\n A :- not B2\n\t \n A :- B1 and B2n\n";
	}
}