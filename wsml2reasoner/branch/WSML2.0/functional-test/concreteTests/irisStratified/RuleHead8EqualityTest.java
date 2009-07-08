package concreteTests.irisStratified;

import org.wsml.reasoner.api.LPReasoner;

import abstractTests.lp.AbstractRuleHead8EqualityTest;

public class RuleHead8EqualityTest extends AbstractRuleHead8EqualityTest{

	@Override
	public LPReasoner getLPReasoner() {
		return Reasoner.get();
	}
}
