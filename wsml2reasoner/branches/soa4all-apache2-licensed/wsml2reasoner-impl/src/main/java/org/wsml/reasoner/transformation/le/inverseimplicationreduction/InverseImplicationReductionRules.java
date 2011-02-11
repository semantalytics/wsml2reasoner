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
package org.wsml.reasoner.transformation.le.inverseimplicationreduction;

import org.wsml.reasoner.transformation.le.NormalizationRule;
import org.wsml.reasoner.transformation.le.Rules;
import org.wsmo.factory.FactoryContainer;

/**
 * This singleton class represents a set of normalization rules for replacing
 * left-side conjunctions and right-side disjunctions within left-implications
 * in logical expressions.
 * 
 * <pre>
 *   Created on July 3rd, 2006
 *   Committed by $Author: nathalie $
 *   $Source: /home/richi/temp/w2r/wsml2reasoner/src/org/wsml/reasoner/transformation/le/InverseImplicationReductionRules.java,v $,
 * </pre>
 * 
 * @author Nathalie Steinmetz, DERI Innsbruck
 * @version $Revision: 1.3 $ $Date: 2006-08-08 12:47:54 $
 */
public class InverseImplicationReductionRules extends Rules <NormalizationRule>{

    public InverseImplicationReductionRules(FactoryContainer wsmoManager) {
        addRule(new InvImplLeftConjunctionReplacementRule(wsmoManager));
        addRule(new InvImplRightDisjunctionReplacementRule(wsmoManager));
    }
}