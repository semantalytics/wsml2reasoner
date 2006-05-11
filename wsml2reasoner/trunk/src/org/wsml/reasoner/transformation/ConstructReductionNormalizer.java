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
import org.omwg.ontology.Ontology;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsml.reasoner.transformation.le.DisjunctionPullRules;
import org.wsml.reasoner.transformation.le.ImplicationReductionRules;
import org.wsml.reasoner.transformation.le.LogicalExpressionNormalizer;
import org.wsml.reasoner.transformation.le.MoleculeDecompositionRules;
import org.wsml.reasoner.transformation.le.NegationPushRules;
import org.wsml.reasoner.transformation.le.NormalizationRule;
import org.wsml.reasoner.transformation.le.OnePassReplacementNormalizer;
import org.wsmo.common.exception.InvalidModelException;
import org.wsmo.factory.WsmoFactory;

public class ConstructReductionNormalizer implements OntologyNormalizer {
    protected LogicalExpressionNormalizer leNormalizer;

    protected WsmoFactory wsmoFactory;

    protected AnonymousIdTranslator anonymousIdTranslator;

    public ConstructReductionNormalizer(WSMO4JManager wsmoManager) {
        List<NormalizationRule> preOrderRules = new ArrayList<NormalizationRule>();
        List<NormalizationRule> postOrderRules = new ArrayList<NormalizationRule>();
        preOrderRules
                .addAll((List<NormalizationRule>) new ImplicationReductionRules(
                        wsmoManager));
        // preOrderRules.addAll((List<NormalizationRule>)DisjunctionPullRules.instantiate());
        preOrderRules.addAll((List<NormalizationRule>) new NegationPushRules(
                wsmoManager));
        postOrderRules
                .addAll((List<NormalizationRule>) new MoleculeDecompositionRules(
                        wsmoManager));
        postOrderRules
                .addAll((List<NormalizationRule>) new DisjunctionPullRules(
                        wsmoManager));
        leNormalizer = new OnePassReplacementNormalizer(preOrderRules,
                postOrderRules, wsmoManager);
        wsmoFactory = wsmoManager.getWSMOFactory();
        anonymousIdTranslator = new AnonymousIdTranslator(wsmoFactory);
    }

    public Ontology normalize(Ontology ontology) {
        // gather logical expressions from axioms in ontology:
        Set<LogicalExpression> expressions = new HashSet<LogicalExpression>();
        Set<Axiom> axioms = (Set<Axiom>) ontology.listAxioms();
        for (Axiom axiom : axioms) {
            expressions.addAll((Collection<LogicalExpression>) axiom.listDefinitions());
        }

        // iteratively normalize logical expressions:
        Set<LogicalExpression> resultExp = new HashSet<LogicalExpression>();
        for (LogicalExpression expression : expressions) {
            anonymousIdTranslator.setScope(expression);
            resultExp.add(leNormalizer.normalize(expression));
        }

        // create new ontology containing the resulting logical expressions:
        String resultIRI = ontology.getIdentifier() + "-as-axioms";
        Ontology resultOnt = wsmoFactory.createOntology(wsmoFactory.createIRI(resultIRI));
        
        //delete old axioms (wsmo4j statics :(
        for (Axiom a: (Set<Axiom>)resultOnt.listAxioms()){
            try {
                a.setOntology(null);
            } catch (InvalidModelException e) {
                e.printStackTrace();
            }
        }
        
        Axiom axiom = wsmoFactory.createAxiom(wsmoFactory.createAnonymousID());
        for (LogicalExpression expression : resultExp) {
            axiom.addDefinition(expression);
        }
        try {
            resultOnt.addAxiom(axiom);
        } catch (InvalidModelException e) {
            e.printStackTrace();
        }
        return resultOnt;
    }
}
