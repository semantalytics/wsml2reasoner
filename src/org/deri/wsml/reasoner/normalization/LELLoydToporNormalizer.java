package org.deri.wsml.reasoner.normalization;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.deri.wsml.reasoner.normalization.le.LloydToporRules;
import org.deri.wsml.reasoner.normalization.le.LogicalExpressionTransformer;
import org.deri.wsml.reasoner.normalization.le.TopDownLESplitter;
import org.deri.wsml.reasoner.normalization.le.TransformationRule;
import org.omwg.logexpression.Binary;
import org.omwg.logexpression.CompoundExpression;
import org.omwg.logexpression.LogicalExpression;
import org.omwg.ontology.Axiom;
import org.omwg.ontology.Ontology;
import org.wsmo.common.Namespace;
import org.wsmo.common.exception.InvalidModelException;
import org.wsmo.common.exception.SynchronisationException;
import org.wsmo.factory.Factory;
import org.wsmo.factory.WsmoFactory;

public class LELLoydToporNormalizer implements OntologyNormalizer
{
    protected LogicalExpressionTransformer leTransformer;

    protected WsmoFactory wsmoFactory;

    public LELLoydToporNormalizer()
    {
        List<TransformationRule> lloydToporRules = (List<TransformationRule>)LloydToporRules.instantiate();
        leTransformer = new TopDownLESplitter(lloydToporRules);
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
            resultExp.addAll(leTransformer.transform(expression));
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
