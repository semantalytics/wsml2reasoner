package concreteTests.irisStratified;

import org.wsml.reasoner.api.LPReasoner;

import abstractTests.core.AbstractTruthValueTest;

public class TruthValueTest extends AbstractTruthValueTest {

	@Override
	public LPReasoner getLPReasoner() {
		return Reasoner.get();
	}

}
