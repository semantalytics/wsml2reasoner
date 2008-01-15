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

import java.util.HashSet;
import java.util.Set;

import org.omwg.logicalexpression.Conjunction;
import org.omwg.logicalexpression.Constraint;
import org.omwg.logicalexpression.Disjunction;
import org.omwg.logicalexpression.InverseImplication;
import org.omwg.logicalexpression.LogicProgrammingRule;
import org.omwg.logicalexpression.LogicalExpression;
import org.wsml.reasoner.impl.WSMO4JManager;

/**
 * This singleton class represents a set of transformation rules for splitting
 * complex LP-rules, constraints and facts into simple datalog-style rule,
 * according to the Lloyd-Topor transformation.
 * 
 * @author Stephan Grimm, FZI Karlsruhe
 */
public class LloydToporRules extends FixedModificationRules
{
    public LloydToporRules(WSMO4JManager wsmoManager)
    {
        super(wsmoManager);
        rules.add(new SplitDisjunctiveBody());
        rules.add(new SplitConstraint());
        rules.add(new SplitConjunctiveHead());
        rules.add(new TransformNestedImplication());
        rules.add(new SplitConjunction());
        rules.add(new TransformImplication());
    }

    public class SplitConjunctiveHead implements TransformationRule
    {
        public boolean isApplicable(LogicalExpression expression)
        {
            if(expression instanceof LogicProgrammingRule)
            {
                LogicProgrammingRule lpRule = (LogicProgrammingRule)expression;
                return lpRule.getLeftOperand() instanceof Conjunction;
            }
            return false;
        }

        public Set<LogicalExpression> apply(LogicalExpression expression)
        {
            Set<LogicalExpression> resultingExpressions = new HashSet<LogicalExpression>();
            LogicProgrammingRule lpRule = (LogicProgrammingRule)expression;
            Conjunction conjunction = (Conjunction)lpRule.getLeftOperand();
            resultingExpressions.add(leFactory.createLogicProgrammingRule(conjunction.getLeftOperand(), lpRule.getRightOperand()));
            resultingExpressions.add(leFactory.createLogicProgrammingRule(conjunction.getRightOperand(), lpRule.getRightOperand()));
            return resultingExpressions;
        }

        public String toString()
        {
            return "A1 and ... and An :- B\n\t=>\n A1 :- B\n\t...\n An :- B\n";
        }
    }

    public class SplitDisjunctiveBody implements TransformationRule
    {
        public boolean isApplicable(LogicalExpression expression)
        {
            if(expression instanceof LogicProgrammingRule)
            {
                LogicProgrammingRule lpRule = (LogicProgrammingRule)expression;
                return lpRule.getRightOperand() instanceof Disjunction;
            }
            return false;
        }

        public Set<LogicalExpression> apply(LogicalExpression expression)
        {
            Set<LogicalExpression> resultingExpressions = new HashSet<LogicalExpression>();
            LogicProgrammingRule lpRule = (LogicProgrammingRule)expression;
            Disjunction disjunction = (Disjunction)lpRule.getRightOperand();
            resultingExpressions.add(leFactory.createLogicProgrammingRule(lpRule.getLeftOperand(), disjunction.getLeftOperand()));
            resultingExpressions.add(leFactory.createLogicProgrammingRule(lpRule.getLeftOperand(), disjunction.getRightOperand()));
            return resultingExpressions;
        }

        public String toString()
        {
            return "A :- B1 or ... Bn\n\t=>\n A :- B1\n\t...\n A :- Bn\n";
        }
    }

    public class TransformNestedImplication implements TransformationRule
    {
        public boolean isApplicable(LogicalExpression expression)
        {
            if(expression instanceof LogicProgrammingRule)
            {
                LogicProgrammingRule lpRule = (LogicProgrammingRule)expression;
                return lpRule.getLeftOperand() instanceof InverseImplication;
            }
            return false;
        }

        public Set<LogicalExpression> apply(LogicalExpression expression)
        {
            Set<LogicalExpression> resultingExpressions = new HashSet<LogicalExpression>();
            LogicProgrammingRule lpRule = (LogicProgrammingRule)expression;
            
            InverseImplication innerRule = (InverseImplication)lpRule.getLeftOperand();
            Conjunction conjunction = leFactory.createConjunction(innerRule.getRightOperand(), lpRule.getRightOperand());
            resultingExpressions.add(leFactory.createLogicProgrammingRule(innerRule.getLeftOperand(), conjunction));
            return resultingExpressions;
        }

        public String toString()
        {
            return "A1 impliedBy A2 :- B\n\t=>\n A1 :- A2 and B\n";
        }
    }

    public class SplitConjunction implements TransformationRule
    {
        public boolean isApplicable(LogicalExpression expression)
        {
            return expression instanceof Conjunction;
        }

        public Set<LogicalExpression> apply(LogicalExpression expression)
        {
            Set<LogicalExpression> resultingExpressions = new HashSet<LogicalExpression>();
            Conjunction conjunction = (Conjunction)expression;
            LogicalExpression leftArg = conjunction.getLeftOperand();
            LogicalExpression rightArg = conjunction.getRightOperand();
            resultingExpressions.add(leftArg);
            resultingExpressions.add(rightArg);
            return resultingExpressions;
        }

        public String toString()
        {
            return "A1 and ... and An \n\t=>\n A1\n\t...\n An\n";
        }
    }

    public class TransformImplication implements TransformationRule
    {
        public boolean isApplicable(LogicalExpression expression)
        {
            return expression instanceof InverseImplication;
        }

        public Set<LogicalExpression> apply(LogicalExpression expression)
        {
            Set<LogicalExpression> resultingExpressions = new HashSet<LogicalExpression>();
            InverseImplication inverseImplication = (InverseImplication)expression;
            LogicProgrammingRule lpRule = leFactory.createLogicProgrammingRule(inverseImplication.getLeftOperand(), inverseImplication.getRightOperand());
            resultingExpressions.add(lpRule);
            return resultingExpressions;
        }

        public String toString()
        {
            return "A1 impliedBy A2\n\t=>\n A1 :- A2\n";
        }
    }

    public class SplitConstraint implements TransformationRule
    {
        public boolean isApplicable(LogicalExpression expression)
        {
            if(expression instanceof Constraint)
            {
                Constraint constraint = (Constraint)expression;
                return constraint.getOperand() instanceof Disjunction;
            }
            return false;
        }

        public Set<LogicalExpression> apply(LogicalExpression expression)
        {
            Set<LogicalExpression> resultingExpressions = new HashSet<LogicalExpression>();
            Constraint constraint = (Constraint)expression;
            Disjunction disjunction = (Disjunction)constraint.getOperand();
            resultingExpressions.add(leFactory.createConstraint(disjunction.getLeftOperand()));
            resultingExpressions.add(leFactory.createConstraint(disjunction.getRightOperand()));
            return resultingExpressions;
        }

        public String toString()
        {
            return "!- B1 or ... or Bn\n\t=>\n !- B1\n\t...\n !- Bn\n";
        }
    }
}
