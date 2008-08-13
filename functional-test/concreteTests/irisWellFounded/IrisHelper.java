package concreteTests.irisWellFounded;

import helper.ReasonerHelper;
import org.wsml.reasoner.api.LPReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory;

public class IrisHelper
{
	static LPReasoner getReasoner()
	{
		return ReasonerHelper.getLPReasoner(WSMLReasonerFactory.BuiltInReasoner.IRIS_WELL_FOUNDED );
	}
}
