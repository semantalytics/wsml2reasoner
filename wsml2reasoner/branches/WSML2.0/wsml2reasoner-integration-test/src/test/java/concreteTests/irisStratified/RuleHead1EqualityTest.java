package concreteTests.irisStratified;

import org.wsml.reasoner.api.LPReasoner;

import abstractTests.lp.AbstractRuleHead1EqualityTest;

public class RuleHead1EqualityTest extends AbstractRuleHead1EqualityTest {

	@Override
	public LPReasoner getLPReasoner() {
		return Reasoner.get();
	}
}
