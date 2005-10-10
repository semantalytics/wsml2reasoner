/**
 * WSML Reasoner Implementation.
 *
 * Copyright (c) 2005, FZI, Austria.
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

import org.omwg.logicalexpression.Binary;
import org.omwg.logicalexpression.LogicalExpression;

/**
 * This singleton class represents a set of normalization rules for pulling
 * disjunctions in logical expressions out of conjunctions, realizing a
 * conjunctive normal form. left-implications.
 * 
 * @author Stephan Grimm, FZI Karlsruhe
 */
public class DisjunctionPullRules extends FixedModificationRules
{
    protected static DisjunctionPullRules instance;

    private DisjunctionPullRules()
    {
        super();
        rules.add(new ConjunctionPushRule());
    }

    public static DisjunctionPullRules instantiate()
    {
        if(instance == null)
        {
            instance = new DisjunctionPullRules();
        }
        return instance;
    }

    protected class ConjunctionPushRule implements NormalizationRule
    {
        public LogicalExpression apply(LogicalExpression expression)
        {
            Binary conjunction = (Binary)expression;
            LogicalExpression conjunct;
            Binary disjunction;
            if(hasLeftDisjunction(conjunction))
            {
                conjunct = conjunction.getArgument(1);
                disjunction = (Binary)conjunction.getArgument(0);
            }
            else
            {
                conjunct = conjunction.getArgument(0);
                disjunction = (Binary)conjunction.getArgument(1);
            }
            Set<LogicalExpression> disjuncts = new HashSet<LogicalExpression>();
            collectDirectDisjuncts(disjunction, disjuncts);
            Set<LogicalExpression> newDisjuncts = new HashSet<LogicalExpression>();
            for(LogicalExpression disjunct : disjuncts)
            {
                newDisjuncts.add(leFactory.createBinary(Binary.AND, conjunct, disjunct));
            }
            return buildNary(Binary.OR, newDisjuncts);
        }

        public boolean isApplicable(LogicalExpression expression)
        {
            if(expression instanceof Binary)
            {
                Binary conjunction = (Binary)expression;
                if(conjunction.getOperator() == Binary.AND)
                {
                    return hasLeftDisjunction(conjunction) || hasRightDisjunction(conjunction);
                }
            }
            return false;
        }
        
        protected boolean hasLeftDisjunction(Binary binary)
        {
            LogicalExpression argument = binary.getArgument(0);
            if(argument instanceof Binary)
                return ((Binary)argument).getOperator() == Binary.OR;
            else
                return false;
        }

        protected boolean hasRightDisjunction(Binary binary)
        {
            LogicalExpression argument = binary.getArgument(1);
            if(argument instanceof Binary)
                return ((Binary)argument).getOperator() == Binary.OR;
            else
                return false;
        }

        protected void collectDirectDisjuncts(LogicalExpression expression, Set<LogicalExpression> disjuncts)
        {
            if(expression instanceof Binary)
            {
                Binary disjunction = (Binary)expression;
                if(disjunction.getOperator() == Binary.OR)
                {
                    collectDirectDisjuncts(disjunction.getArgument(0), disjuncts);
                    collectDirectDisjuncts(disjunction.getArgument(1), disjuncts);
                    return;
                }
            }
            disjuncts.add(expression);
        }
        
        public String toString()
        {
            return "A or B and (C or D)\n\t=>\n A or B and C or B and D\n";
        }
    }
}
