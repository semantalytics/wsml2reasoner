package concreteTests.kaon2;

import org.wsml.reasoner.api.LPReasoner;
import abstractTests.lp.AbstractBoolean;

public class Boolean extends AbstractBoolean
{
	public LPReasoner getReasoner()
	{
		return Kaon2Helper.getKAON2();
	}
}
