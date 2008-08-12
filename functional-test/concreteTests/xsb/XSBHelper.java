package concreteTests.xsb;

import helper.ReasonerHelper;
import org.wsml.reasoner.api.LPReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory;

public class XSBHelper
{
	static LPReasoner getXSB()
	{
		return ReasonerHelper.getLPReasoner(WSMLReasonerFactory.BuiltInReasoner.XSB );
	}
}
