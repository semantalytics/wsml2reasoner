/**
 * WSML Reasoner Implementation.
 *
 * Copyright (c) 2005, FZI, Germany.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * You should have received a copy of the GNU Lesser General Public License along
 * with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 */
package org.wsml.reasoner.transformation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.ontology.Axiom;
import org.wsml.reasoner.transformation.le.LogicalExpressionNormalizer;
import org.wsml.reasoner.transformation.le.NormalizationRule;
import org.wsml.reasoner.transformation.le.OnePassReplacementNormalizer;
import org.wsml.reasoner.transformation.le.foldecomposition.FOLMoleculeDecompositionRules;
import org.wsmo.common.Entity;
import org.wsmo.factory.FactoryContainer;
import org.wsmo.factory.WsmoFactory;

public class MoleculeNormalizer implements OntologyNormalizer {
    protected LogicalExpressionNormalizer leNormalizer;

    protected WsmoFactory wsmoFactory;

    protected AnonymousIdTranslator anonymousIdTranslator;

    public MoleculeNormalizer(FactoryContainer wsmoManager) {
        List<NormalizationRule> postOrderRules = new ArrayList<NormalizationRule>();
        postOrderRules.addAll(new FOLMoleculeDecompositionRules(wsmoManager).getRules());
        leNormalizer = new OnePassReplacementNormalizer(postOrderRules, wsmoManager);
        wsmoFactory = wsmoManager.getWsmoFactory();
        anonymousIdTranslator = new AnonymousIdTranslator(wsmoFactory);
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
            anonymousIdTranslator.setScope(expression);
            resultExp.add(leNormalizer.normalize(expression));
        }

        Axiom axiom = wsmoFactory.createAxiom(wsmoFactory.createAnonymousID());
        for (LogicalExpression expression : resultExp) {
            axiom.addDefinition(expression);
        }
        result.add(axiom);
        return result;
    }
}
