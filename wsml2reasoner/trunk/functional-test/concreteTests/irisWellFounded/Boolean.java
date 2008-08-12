package concreteTests.irisWellFounded;

import org.wsml.reasoner.api.LPReasoner;
import abstractTests.lp.AbstractBoolean;

public class Boolean extends AbstractBoolean
{
	public LPReasoner getReasoner()
	{
		return IrisHelper.getIRIS();
	}
}
