package concreteTests.pellet;

import helper.ReasonerHelper;
import org.wsml.reasoner.api.DLReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory;

public class PelletHelper
{
	static DLReasoner getPellet()
	{
		return ReasonerHelper.getDLReasoner(WSMLReasonerFactory.BuiltInReasoner.PELLET );
	}
}
