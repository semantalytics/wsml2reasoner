package concreteTests.mins;

import org.wsml.reasoner.api.LPReasoner;
import abstractTests.lp.AbstractBoolean;

public class Boolean extends AbstractBoolean
{
	public LPReasoner getReasoner()
	{
		return MinsHelper.getMINS();
	}
}
