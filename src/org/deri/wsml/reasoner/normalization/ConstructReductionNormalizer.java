package org.deri.wsml.reasoner.normalization;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.deri.wsml.reasoner.normalization.le.NegationPushRules;
import org.deri.wsml.reasoner.normalization.le.ImplicationReductionRules;
import org.deri.wsml.reasoner.normalization.le.LogicalExpressionNormalizer;
import org.deri.wsml.reasoner.normalization.le.MoleculeDecompositionRules;
import org.deri.wsml.reasoner.normalization.le.DisjunctionPullRules;
import org.deri.wsml.reasoner.normalization.le.NormalizationRule;
import org.deri.wsml.reasoner.normalization.le.OnePassReplacementNormalizer;
import org.omwg.logexpression.LogicalExpression;
import org.omwg.ontology.Axiom;
import org.omwg.ontology.Ontology;
import org.wsmo.common.Namespace;
import org.wsmo.common.exception.InvalidModelException;
import org.wsmo.factory.Factory;
import org.wsmo.factory.WsmoFactory;

public class ConstructReductionNormalizer implements OntologyNormalizer
{
    protected LogicalExpressionNormalizer leNormalizer;
    protected WsmoFactory wsmoFactory;

    public ConstructReductionNormalizer()
    {
        List<NormalizationRule> preOrderRules = new ArrayList<NormalizationRule>();
        List<NormalizationRule> postOrderRules = new ArrayList<NormalizationRule>();
        preOrderRules.addAll((List<NormalizationRule>)ImplicationReductionRules.instantiate());
        preOrderRules.addAll((List<NormalizationRule>)DisjunctionPullRules.instantiate());
        preOrderRules.addAll((List<NormalizationRule>)NegationPushRules.instantiate());
        postOrderRules.addAll((List<NormalizationRule>)MoleculeDecompositionRules.instantiate());
        leNormalizer = new OnePassReplacementNormalizer(preOrderRules, postOrderRules); 
        wsmoFactory = Factory.createWsmoFactory(null);
    }

    public Ontology normalize(Ontology ontology)
    {
        // gather logical expressions from axioms in ontology:
        Set<LogicalExpression> expressions = new HashSet<LogicalExpression>();
        Set<Axiom> axioms = (Set<Axiom>)ontology.listAxioms();
        for(Axiom axiom : axioms)
        {
            expressions.addAll((Collection<LogicalExpression>)axiom.listDefinitions());
        }

        // iteratively normalize logical expressions:
        Set<LogicalExpression> resultExp = new HashSet<LogicalExpression>();
        for(LogicalExpression expression : expressions)
        {
            resultExp.add(leNormalizer.normalize(expression));
        }

        // create new ontology containing the resulting logical expressions:
        String resultIRI = (ontology.getIdentifier() != null ? ontology.getIdentifier().asString() + "-as-axioms" : "iri:normalized-ontology-" + ontology.hashCode());
        Ontology resultOnt = wsmoFactory.createOntology(wsmoFactory.createIRI(resultIRI));
        for(Object n : ontology.listNamespaces())
        {
            resultOnt.addNamespace((Namespace)n);
        }
        resultOnt.setDefaultNamespace(ontology.getDefaultNamespace());
        int axiomCount = 1;
        for(LogicalExpression expression : resultExp)
        {
            Axiom axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI("axiom-" + Integer.toString(axiomCount++)));
            axiom.addDefinition(expression);
            try
            {
                resultOnt.addAxiom(axiom);
            } catch(InvalidModelException e)
            {
                e.printStackTrace();
            }
        }
        
        return resultOnt;
    }
}
