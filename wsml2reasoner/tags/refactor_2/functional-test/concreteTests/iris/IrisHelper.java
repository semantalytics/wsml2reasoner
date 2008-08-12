package concreteTests.iris;

import helper.ReasonerHelper;
import org.wsml.reasoner.api.LPReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory;

public class IrisHelper
{
	static LPReasoner getIRIS()
	{
		return ReasonerHelper.getLPReasoner(WSMLReasonerFactory.BuiltInReasoner.IRIS_STRATIFIED );
	}
}
