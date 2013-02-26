package org.wsml.reasoner.transformation;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.ontology.Axiom;
import org.wsml.reasoner.transformation.le.LogicalExpressionTransformer;
import org.wsml.reasoner.transformation.le.TopDownLESplitter;
import org.wsml.reasoner.transformation.le.inverseimplicationtransformation.InverseImplicationTransformationRules;
import org.wsmo.common.Entity;
import org.wsmo.factory.FactoryContainer;
import org.wsmo.factory.WsmoFactory;

public class InverseImplicationNormalizer implements OntologyNormalizer {
	protected LogicalExpressionTransformer leTransformer;

	protected WsmoFactory wsmoFactory;

	public InverseImplicationNormalizer(FactoryContainer wsmoManager) {
		InverseImplicationTransformationRules inverseImplicationTransformerRules = new InverseImplicationTransformationRules(
				wsmoManager);
		leTransformer = new TopDownLESplitter(
				inverseImplicationTransformerRules.getRules());
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