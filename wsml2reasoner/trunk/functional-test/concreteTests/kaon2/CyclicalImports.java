package concreteTests.kaon2;

import org.wsml.reasoner.api.LPReasoner;
import abstractTests.core.AbstractCyclicalImports;

public class CyclicalImports extends AbstractCyclicalImports
{
	public LPReasoner getReasoner()
	{
		return Kaon2Helper.getReasoner();
	}
}
