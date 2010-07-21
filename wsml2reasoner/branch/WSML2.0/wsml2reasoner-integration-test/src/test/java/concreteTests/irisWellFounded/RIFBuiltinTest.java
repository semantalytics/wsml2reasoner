package concreteTests.irisWellFounded;

import org.wsml.reasoner.api.LPReasoner;

import abstractTests.lp.AbstractRIFBuiltinTest;

public class RIFBuiltinTest extends AbstractRIFBuiltinTest{

	@Override
	public LPReasoner getLPReasoner() {
		return Reasoner.get();
	}
}
