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

import java.util.Set;

import org.omwg.logicalexpression.LogicalExpression;

/**
 * A transformation rule specifies the behaviour of how to transform a single
 * logical expression into a set of logical expressions.
 * 
 * @author Stephan Grimm, FZI Karlsruhe
 */
public interface TransformationRule extends LEModificationRule {
    /**
     * This method applies the transformation behaviour specified by this
     * transformation rule to a logical expression.
     * 
     * @param expression
     * @return a set of logical expressions resulting from the transformation
     */
    public Set<LogicalExpression> apply(LogicalExpression expression);
}