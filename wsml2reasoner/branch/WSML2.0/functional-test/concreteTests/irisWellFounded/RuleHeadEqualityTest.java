package concreteTests.irisWellFounded;

import org.wsml.reasoner.api.LPReasoner;

import abstractTests.lp.AbstractRuleHead1EqualityTest;


public class RuleHeadEqualityTest extends AbstractRuleHead1EqualityTest{

	@Override
	public LPReasoner getLPReasoner() {
		return Reasoner.get();
	}

	

}
