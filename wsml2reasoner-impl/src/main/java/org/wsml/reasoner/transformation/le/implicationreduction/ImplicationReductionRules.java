package org.wsml.reasoner.transformation.le.implicationreduction;

import org.wsml.reasoner.transformation.le.NormalizationRule;
import org.wsml.reasoner.transformation.le.Rules;
import org.wsmo.factory.FactoryContainer;

/**
 * This singleton class represents a set of normalization rules for replacing
 * equivalences and right-implications in logical expressions by
 * left-implications.
 * 
 * @author Stephan Grimm, FZI Karlsruhe
 */
public class ImplicationReductionRules extends Rules<NormalizationRule> {

	public ImplicationReductionRules(FactoryContainer factory) {
		addRule(new EquivalenceReplacementRule(factory));
		addRule(new RightImplicationReplacementRule(factory));
	}
}