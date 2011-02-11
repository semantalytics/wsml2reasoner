/*
 * Copyright 2011, Semantic Technology Institute (STI) Innsbruck, 
 * University of Innsbruck, Technikerstrasse 21a, 6020 Innsbruck, Austria.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
