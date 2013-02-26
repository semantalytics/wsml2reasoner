package org.wsml.reasoner.transformation.le;

import java.util.ArrayList;
import java.util.List;

public abstract class Rules<X extends LEModificationRule> {

	private List<X> rules = new ArrayList<X>();

	public Rules() {
		super();
	}

	public void addRule(X theXRule) {
		rules.add(theXRule);
	}

	public List<X> getRules() {
		return rules;
	}
}
