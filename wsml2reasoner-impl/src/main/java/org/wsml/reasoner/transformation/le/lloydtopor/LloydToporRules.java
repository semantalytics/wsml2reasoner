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
package org.wsml.reasoner.transformation.le.lloydtopor;

import org.wsml.reasoner.transformation.le.Rules;
import org.wsml.reasoner.transformation.le.TransformationRule;
import org.wsmo.factory.FactoryContainer;

/**
 * This singleton class represents a set of transformation rules for splitting
 * complex LP-rules, constraints and facts into simple datalog-style rule,
 * according to the Lloyd-Topor transformation.
 * 
 * @author Stephan Grimm, FZI Karlsruhe
 */
public class LloydToporRules extends Rules <TransformationRule>{
    
    public LloydToporRules(FactoryContainer wsmoManager) {
        addRule(new SplitDisjunctiveBody(wsmoManager));
        addRule(new SplitConstraint(wsmoManager));
        addRule(new SplitConjunctiveHead(wsmoManager));
        addRule(new TransformNestedImplication(wsmoManager));
        addRule(new SplitConjunction());
        addRule(new TransformImplication(wsmoManager));
    }
}
