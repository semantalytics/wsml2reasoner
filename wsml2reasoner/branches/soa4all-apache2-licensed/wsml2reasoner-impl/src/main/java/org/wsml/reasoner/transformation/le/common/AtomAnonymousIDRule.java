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

package org.wsml.reasoner.transformation.le.common;

import java.util.ArrayList;
import java.util.List;

import org.omwg.logicalexpression.Atom;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.terms.Term;
import org.wsml.reasoner.transformation.AnonymousIdTranslator;
import org.wsml.reasoner.transformation.le.NormalizationRule;
import org.wsmo.common.Identifier;
import org.wsmo.factory.FactoryContainer;
import org.wsmo.factory.LogicalExpressionFactory;


public class AtomAnonymousIDRule implements NormalizationRule {

    private LogicalExpressionFactory leFactory;
    private AnonymousIdTranslator anonymousIDTranslator;
    
    public AtomAnonymousIDRule(FactoryContainer wsmoManager, AnonymousIdTranslator anonymousIDTranslator){
        this.leFactory = wsmoManager.getLogicalExpressionFactory();
        this.anonymousIDTranslator = anonymousIDTranslator;
    }
    
    public LogicalExpression apply(LogicalExpression expression) {
        Atom atom = (Atom) expression;
        Identifier id = (Identifier) anonymousIDTranslator.translate(atom.getIdentifier());
        List<Term> args = new ArrayList<Term>();
        for (int i = 0; i < atom.getArity(); i++) {
            Term term = atom.getParameter(i);
            if (term instanceof Identifier) {
                term = anonymousIDTranslator.translate(term);
            }
            args.add(term);
        }
        return leFactory.createAtom(id, args);
    }

    public boolean isApplicable(LogicalExpression expression) {
        return expression instanceof Atom;
    }
}