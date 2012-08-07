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

package org.wsml.reasoner.transformation.le;

import java.util.Collection;

import org.omwg.logicalexpression.Conjunction;
import org.omwg.logicalexpression.Disjunction;
import org.omwg.logicalexpression.LogicalExpression;
import org.wsmo.factory.LogicalExpressionFactory;


public class LEUtil {

    protected static final byte CONJUNCTION = 0;
    protected static final byte DISJUNCTION = 1;
    
    public static Conjunction buildNaryConjunction(LogicalExpressionFactory leFactory, Collection< ? extends LogicalExpression> expressions) {
        return (Conjunction) buildNary(leFactory, CONJUNCTION, expressions);
    }

    public static Disjunction buildNaryDisjunction(LogicalExpressionFactory leFactory, Collection< ? extends LogicalExpression> expressions) {
        return (Disjunction) buildNary(leFactory, DISJUNCTION, expressions);
    }

    public static LogicalExpression buildNary(LogicalExpressionFactory leFactory, byte operationCode, Collection< ? extends LogicalExpression> expressions) {
        LogicalExpression result = null;
        for (LogicalExpression le : expressions){
            if (result == null){
                result = le;
            }
            else if (operationCode == CONJUNCTION){
                result = leFactory.createConjunction(result, le);
            }
            else if (operationCode == DISJUNCTION){
                result = leFactory.createDisjunction(result, le);
            }
        }
        return result;
    }
}
