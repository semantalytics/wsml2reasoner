/*
 * Copyright (C) 2006 Digital Enterprise Research Insitute (DERI) Innsbruck
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.wsml.reasoner.transformation.le.disjunctionpull;

import java.util.HashSet;
import java.util.Set;

import org.omwg.logicalexpression.Binary;
import org.omwg.logicalexpression.Conjunction;
import org.omwg.logicalexpression.Disjunction;
import org.omwg.logicalexpression.LogicalExpression;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsml.reasoner.transformation.le.LEUtil;
import org.wsml.reasoner.transformation.le.NormalizationRule;
import org.wsmo.factory.LogicalExpressionFactory;


public class ConjunctionPushRule implements NormalizationRule {
    
    private WSMO4JManager wsmoManager;
    private LogicalExpressionFactory leFactory;
    
    public ConjunctionPushRule(WSMO4JManager wsmoManager){
        this.wsmoManager = wsmoManager;
        this.leFactory = wsmoManager.getLogicalExpressionFactory();
    }
    
    public LogicalExpression apply(LogicalExpression expression) {
        Conjunction conjunction = (Conjunction) expression;
        Set <LogicalExpression> result = new HashSet<LogicalExpression>();
        if (hasLeftDisjunction(conjunction)) {
            for (LogicalExpression disjunct : collectDirectDisjuncts(conjunction.getLeftOperand())) {
                result.add(leFactory.createConjunction(conjunction.getRightOperand(), disjunct));
            }
        }
        else {
            for (LogicalExpression disjunct : collectDirectDisjuncts(conjunction.getRightOperand())) {
                result.add(leFactory.createConjunction(conjunction.getLeftOperand(), disjunct));
            }
        }
        return LEUtil.buildNaryDisjunction(wsmoManager, result);
    }

    public boolean isApplicable(LogicalExpression expression) {
        if (expression instanceof Conjunction) {
            return hasLeftDisjunction((Conjunction) expression) || hasRightDisjunction((Conjunction) expression);
        }
        return false;
    }

    protected boolean hasLeftDisjunction(Binary binary) {
        return binary.getLeftOperand() instanceof Disjunction;
    }

    protected boolean hasRightDisjunction(Binary binary) {
        return binary.getRightOperand() instanceof Disjunction;
    }
    
    protected Set<LogicalExpression> collectDirectDisjuncts(LogicalExpression expression) {
        Set <LogicalExpression> disjuncts = new HashSet<LogicalExpression>();
        if (expression instanceof Disjunction) {
            disjuncts.addAll(collectDirectDisjuncts(((Disjunction) expression).getLeftOperand()));
            disjuncts.addAll(collectDirectDisjuncts(((Disjunction) expression).getRightOperand()));
        }
        else{
            disjuncts.add(expression);
        }
        return disjuncts;
    }

    public String toString() {
        return "A or B and (C or D)\n\t=>\n A or B and C or B and D\n";
    }
}