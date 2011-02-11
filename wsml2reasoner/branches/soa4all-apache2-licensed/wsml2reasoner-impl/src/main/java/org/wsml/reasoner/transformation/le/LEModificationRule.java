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

import org.omwg.logicalexpression.LogicalExpression;

/**
 * A modification rule for a logical expression specifies the behaviour of how
 * to perform modifications to this expression. For a given logical expression
 * le it can decide whether this behaviour is applicable to le.
 * 
 * @author Stephan Grimm, FZI Karlsruhe
 */
public interface LEModificationRule {
    /**
     * This method decides whether the behaviour specified by this modification
     * rule is applicable to a given logical expression.
     * 
     * @param expression
     * @return true if the rule is applicable, falsr otherwise
     */
    public boolean isApplicable(LogicalExpression expression);
}
