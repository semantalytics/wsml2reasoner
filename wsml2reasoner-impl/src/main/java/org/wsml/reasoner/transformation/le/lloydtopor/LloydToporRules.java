package org.wsml.reasoner.transformation.le.lloydtopor;

import org.wsml.reasoner.transformation.le.Rules;
import org.wsml.reasoner.transformation.le.TransformationRule;
import org.wsmo.factory.FactoryContainer;

/**
 * This singleton class represents a set of transformation rules for splitting
 * complex LP-rules, constraints and facts into simple datalog-style rule,
 * according to the Lloyd-Topor transformation.
 * 
 * @author Stephan Grimm, FZI Karlsruhe
 */
public class LloydToporRules extends Rules<TransformationRule> {

	public LloydToporRules(FactoryContainer wsmoManager) {
		addRule(new SplitDisjunctiveBody(wsmoManager));
		addRule(new SplitConstraint(wsmoManager));
		addRule(new SplitConjunctiveHead(wsmoManager));
		addRule(new TransformNestedImplication(wsmoManager));
		addRule(new SplitConjunction());
		addRule(new TransformImplication(wsmoManager));
	}
}
