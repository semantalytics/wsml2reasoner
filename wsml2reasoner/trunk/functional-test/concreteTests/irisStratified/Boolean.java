package concreteTests.irisStratified;

import org.wsml.reasoner.api.LPReasoner;
import abstractTests.lp.AbstractBoolean;

public class Boolean extends AbstractBoolean
{
	public LPReasoner getLPReasoner()
	{
		return IrisHelper.getReasoner();
	}
}
