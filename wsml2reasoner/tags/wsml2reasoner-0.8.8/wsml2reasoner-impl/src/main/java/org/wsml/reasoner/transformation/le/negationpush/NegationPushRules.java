package org.wsml.reasoner.transformation.le.negationpush;

import org.wsml.reasoner.transformation.le.NormalizationRule;
import org.wsml.reasoner.transformation.le.Rules;
import org.wsmo.factory.FactoryContainer;

/**
 * This singleton class represents a set of normalization rules for pushing
 * negation-as-failure operators inside a logical expression into its
 * sub-expressions, such that the remaining occurrences of negation are all
 * atomic.
 * 
 * @author Stephan Grimm, FZI Karlsruhe
 */
public class NegationPushRules extends Rules<NormalizationRule> {

	public NegationPushRules(FactoryContainer factory) {
		addRule(new DoubleNegationRule());
		addRule(new NegateConjunctionRule(factory));
		addRule(new NegateDisjunctionRule(factory));
	}
}
