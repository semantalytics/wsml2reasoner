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
package org.wsml.reasoner.transformation.le.moleculedecomposition;

import org.wsml.reasoner.transformation.AnonymousIdTranslator;
import org.wsml.reasoner.transformation.le.NormalizationRule;
import org.wsml.reasoner.transformation.le.Rules;
import org.wsml.reasoner.transformation.le.common.AtomAnonymousIDRule;
import org.wsml.reasoner.transformation.le.common.MoleculeAnonymousIDRule;
import org.wsmo.factory.FactoryContainer;

/**
 * This singleton class represents a set of normalization rules for replacing
 * complex molecules inside a logical expression by conjunctions of simple ones.
 * 
 * @author Stephan Grimm, FZI Karlsruhe
 */
public class MoleculeDecompositionRules extends Rules <NormalizationRule>{

    public MoleculeDecompositionRules(FactoryContainer factory) {
        AnonymousIdTranslator anonymousIDTranslator = new AnonymousIdTranslator(factory.getWsmoFactory());
        
        addRule(new MoleculeDecompositionRule(factory));
        addRule(new MoleculeAnonymousIDRule(factory, anonymousIDTranslator));
        addRule(new AtomAnonymousIDRule(factory, anonymousIDTranslator));
    }
}