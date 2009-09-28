package concreteTests.irisWellFounded;

import org.wsml.reasoner.api.LPReasoner;

import abstractTests.lp.AbstractRIFBuiltin01Test;

public class RIFBuiltin01Test extends AbstractRIFBuiltin01Test{

	@Override
	public LPReasoner getLPReasoner() {
		return Reasoner.get();
	}
}
