/**
 * WSML Reasoner Implementation.
 *
 * Copyright (c) 2005, University of Innsbruck, Austria.
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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.ontology.Axiom;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsml.reasoner.transformation.le.LogicalExpressionTransformer;
import org.wsml.reasoner.transformation.le.TopDownLESplitter;
import org.wsml.reasoner.transformation.le.inverseimplicationtransformation.InverseImplicationTransformationRules;
import org.wsmo.common.Entity;
import org.wsmo.factory.WsmoFactory;

public class InverseImplicationNormalizer implements OntologyNormalizer {
	protected LogicalExpressionTransformer leTransformer;

	protected WsmoFactory wsmoFactory;

	public InverseImplicationNormalizer(WSMO4JManager wsmoManager) {
	    InverseImplicationTransformationRules inverseImplicationTransformerRules = new InverseImplicationTransformationRules(wsmoManager);
		leTransformer = new TopDownLESplitter(inverseImplicationTransformerRules.getRules());
		wsmoFactory = wsmoManager.getWSMOFactory();

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