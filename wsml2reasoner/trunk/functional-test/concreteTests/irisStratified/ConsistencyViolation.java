package concreteTests.irisStratified;

import org.wsml.reasoner.api.LPReasoner;
import abstractTests.lp.AbstractConsistencyViolation;

public class ConsistencyViolation extends AbstractConsistencyViolation
{
	public LPReasoner getLPReasoner()
	{
		return IrisHelper.getReasoner();
	}
}
