package concreteTests.kaon2dl;

import helper.ReasonerHelper;
import org.wsml.reasoner.api.DLReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory;

public class Kaon2Helper
{
	static DLReasoner getKaon2DL()
	{
		return ReasonerHelper.getDLReasoner(WSMLReasonerFactory.BuiltInReasoner.KAON2DL );
	}
}
