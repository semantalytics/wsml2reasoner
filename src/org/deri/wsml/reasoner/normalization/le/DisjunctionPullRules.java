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
package org.deri.wsml.reasoner.normalization.le;

import org.omwg.logexpression.Binary;
import org.omwg.logexpression.CompoundExpression;
import org.omwg.logexpression.LogicalExpression;

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
        rules.add(new LeftDisjunctionPullRule());
        rules.add(new RightDisjunctionPullRule());
    }

    public static DisjunctionPullRules instantiate()
    {
        if(instance == null)
        {
            instance = new DisjunctionPullRules();
        }
        return instance;
    }

    protected class LeftDisjunctionPullRule implements NormalizationRule
    {
        public LogicalExpression apply(LogicalExpression expression)
        {
            Binary conjunction = (Binary)expression;
            Binary disjunction = (Binary)conjunction.getArgument(0);
            LogicalExpression leftConjunction = leFactory.createBinary(Binary.AND, disjunction.getArgument(0), conjunction.getArgument(1));
            LogicalExpression rightConjunction = leFactory.createBinary(Binary.AND, disjunction.getArgument(1), conjunction.getArgument(1));
            return leFactory.createBinary(Binary.OR, leftConjunction, rightConjunction);
        }

        public boolean isApplicable(LogicalExpression expression)
        {
            if(expression instanceof CompoundExpression)
            {
                CompoundExpression compound = (CompoundExpression)expression;
                if(compound.getOperator() == CompoundExpression.AND)
                {
                    if(compound.getArgument(0) instanceof CompoundExpression)
                    {
                        compound = (CompoundExpression)compound.getArgument(0);
                        return compound.getOperator() == CompoundExpression.OR;
                    }
                }
            }
            return false;
        }

        public String toString()
        {
            return "(A or B) and C\n\t=>\n A and C or B and C\n";
        }
    }

    protected class RightDisjunctionPullRule implements NormalizationRule
    {
        public LogicalExpression apply(LogicalExpression expression)
        {
            Binary conjunction = (Binary)expression;
            Binary disjunction = (Binary)conjunction.getArgument(1);
            LogicalExpression leftConjunction = leFactory.createBinary(Binary.AND, disjunction.getArgument(0), conjunction.getArgument(0));
            LogicalExpression rightConjunction = leFactory.createBinary(Binary.AND, disjunction.getArgument(1), conjunction.getArgument(0));
            return leFactory.createBinary(Binary.OR, leftConjunction, rightConjunction);
        }

        public boolean isApplicable(LogicalExpression expression)
        {
            if(expression instanceof CompoundExpression)
            {
                CompoundExpression compound = (CompoundExpression)expression;
                if(compound.getOperator() == CompoundExpression.AND)
                {
                    if(compound.getArgument(1) instanceof CompoundExpression)
                    {
                        compound = (CompoundExpression)compound.getArgument(1);
                        return compound.getOperator() == CompoundExpression.OR;
                    }
                }
            }
            return false;
        }

        public String toString()
        {
            return "C and (A or B)\n\t=>\n C and A or C and B\n";
        }
    }
}
