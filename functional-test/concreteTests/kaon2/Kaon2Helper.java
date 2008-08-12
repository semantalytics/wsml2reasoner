package concreteTests.kaon2;

import helper.ReasonerHelper;
import org.wsml.reasoner.api.LPReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory;

public class Kaon2Helper
{
	static LPReasoner getKAON2()
	{
		return (LPReasoner) ReasonerHelper.getReasoner(WSMLReasonerFactory.BuiltInReasoner.KAON2, null );
	}
}
