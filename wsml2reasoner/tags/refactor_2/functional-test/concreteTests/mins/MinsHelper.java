package concreteTests.mins;

import helper.ReasonerHelper;
import org.wsml.reasoner.api.LPReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory;

public class MinsHelper
{
	static LPReasoner getMINS()
	{
		return ReasonerHelper.getLPReasoner(WSMLReasonerFactory.BuiltInReasoner.MINS);
	}
}
