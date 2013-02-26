package org.wsml.reasoner.transformation.le.disjunctionpull;

import org.wsml.reasoner.transformation.le.NormalizationRule;
import org.wsml.reasoner.transformation.le.Rules;
import org.wsmo.factory.FactoryContainer;

public class DisjunctionPullRules extends Rules<NormalizationRule> {

	public DisjunctionPullRules(FactoryContainer factory) {
		addRule(new ConjunctionPushRule(factory));
	}
}