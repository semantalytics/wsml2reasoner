package concreteTests.mins;

import helper.ReasonerHelper;
import org.wsml.reasoner.api.LPReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory;

public class MinsHelper
{
	static LPReasoner getMINS()
	{
		return (LPReasoner) ReasonerHelper.getReasoner(WSMLReasonerFactory.BuiltInReasoner.MINS, null );
	}
}
