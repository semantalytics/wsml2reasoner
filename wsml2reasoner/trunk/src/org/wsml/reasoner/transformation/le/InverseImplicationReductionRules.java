/**
 * WSML Reasoner Implementation.
 *
 * Copyright (c) 2005, University of Innsbruck, Austria.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * You should have received a copy of the GNU Lesser General Public License along
 * with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 */
package org.wsml.reasoner.transformation.le;

import org.omwg.logicalexpression.Conjunction;
import org.omwg.logicalexpression.Disjunction;
import org.omwg.logicalexpression.InverseImplication;
import org.omwg.logicalexpression.LogicalExpression;
import org.wsml.reasoner.impl.WSMO4JManager;

/**
 * This singleton class represents a set of normalization rules for replacing
 * left-side conjunctions and right-side disjunctions within left-implications 
 * in logical expressions.
 *
 * <pre>
 *  Created on July 3rd, 2006
 *  Committed by $Author: nathalie $
 *  $Source: /home/richi/temp/w2r/wsml2reasoner/src/org/wsml/reasoner/transformation/le/InverseImplicationReductionRules.java,v $,
 * </pre>
 *
 * @author Nathalie Steinmetz, DERI Innsbruck
 * @version $Revision: 1.1 $ $Date: 2006-07-18 08:21:02 $
 */
public class InverseImplicationReductionRules extends FixedModificationRules {

	public InverseImplicationReductionRules(WSMO4JManager wsmoManager) {
		super(wsmoManager);
        rules.add(new InvImplLeftConjunctionReplacementRule());
        rules.add(new InvImplRightDisjunctionReplacementRule());
	}

	protected class InvImplLeftConjunctionReplacementRule implements NormalizationRule {
        public LogicalExpression apply(LogicalExpression expression) {
            InverseImplication invImpl = (InverseImplication) expression;
            LogicalExpression leftArg = invImpl.getLeftOperand();
            LogicalExpression rightArg = invImpl.getRightOperand();
            LogicalExpression impliedBy1 = leFactory.createInverseImplication(
            		((Conjunction) leftArg).getLeftOperand(), rightArg);
            LogicalExpression impliedBy2 = leFactory.createInverseImplication(
            		((Conjunction) leftArg).getRightOperand(), rightArg);
            LogicalExpression and = leFactory.createConjunction(impliedBy1, impliedBy2);
            return and;
        }

        public boolean isApplicable(LogicalExpression expression) {
        	if (expression instanceof InverseImplication && ((InverseImplication) 
        			expression).getLeftOperand() instanceof Conjunction) {
        		return true;
        	}
            return false;
        }

        public String toString() {
            return "A and B impliedBy C\n\t=>\n (A impliedBy C) and (B impliedBy C)\n";
        }
    }
	
	protected class InvImplRightDisjunctionReplacementRule implements NormalizationRule {
        public LogicalExpression apply(LogicalExpression expression) {
            InverseImplication invImpl = (InverseImplication) expression;
            LogicalExpression leftArg = invImpl.getLeftOperand();
            LogicalExpression rightArg = invImpl.getRightOperand();
            LogicalExpression impliedBy1 = leFactory.createInverseImplication(
            		leftArg, ((Disjunction) rightArg).getLeftOperand());
            LogicalExpression impliedBy2 = leFactory.createInverseImplication(
            		leftArg, ((Disjunction) rightArg).getRightOperand());
            LogicalExpression and = leFactory.createConjunction(impliedBy1, impliedBy2);
            return and;
        }

        public boolean isApplicable(LogicalExpression expression) {
        	if (expression instanceof InverseImplication && ((InverseImplication) 
        			expression).getRightOperand() instanceof Disjunction) {
        		return true;
        	}
            return false;
        }

        public String toString() {
            return "A impliedBy B or C\n\t=>\n (A impliedBy B) and (A impliedBy C)\n";
        }
    }
}
