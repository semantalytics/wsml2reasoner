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
package org.wsml.reasoner.transformation;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.ontology.Axiom;
import org.wsml.reasoner.transformation.le.LogicalExpressionTransformer;
import org.wsml.reasoner.transformation.le.TopDownLESplitter;
import org.wsml.reasoner.transformation.le.lloydtopor.LloydToporRules;
import org.wsmo.common.Entity;
import org.wsmo.factory.FactoryContainer;
import org.wsmo.factory.WsmoFactory;

public class LloydToporNormalizer implements OntologyNormalizer {
    protected LogicalExpressionTransformer leTransformer;

    protected WsmoFactory wsmoFactory;

    public LloydToporNormalizer(FactoryContainer wsmoManager) {
        LloydToporRules lloydToporRules = new LloydToporRules(wsmoManager);
        leTransformer = new TopDownLESplitter(lloydToporRules.getRules());
        wsmoFactory = wsmoManager.getWsmoFactory();
    }

    public Set<Entity> normalizeEntities(Collection<Entity> theEntities) {
        throw new UnsupportedOperationException();
    }

    public Set<Axiom> normalizeAxioms(Collection<Axiom> theAxioms) {
        Set<Axiom> result = new HashSet<Axiom>();
        // gather logical expressions from axioms in ontology:
        Set<LogicalExpression> expressions = new HashSet<LogicalExpression>();
        for (Axiom axiom : theAxioms) {
            expressions.addAll(axiom.listDefinitions());
        }

        // iteratively normalize logical expressions:
        Set<LogicalExpression> resultExp = new HashSet<LogicalExpression>();
        for (LogicalExpression expression : expressions) {
            resultExp.addAll(leTransformer.transform(expression));
        }

        Axiom axiom = wsmoFactory.createAxiom(wsmoFactory.createAnonymousID());
        for (LogicalExpression expression : resultExp) {
            axiom.addDefinition(expression);
        }
        result.add(axiom);
        return result;
    }
}
