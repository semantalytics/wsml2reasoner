package concreteTests.kaon2dl;

import org.wsml.reasoner.api.WSMLReasoner;
import abstractTests.core.AbstractCyclicalImports;

public class CyclicalImports extends AbstractCyclicalImports
{
	public WSMLReasoner getReasoner()
	{
		return Kaon2Helper.getReasoner();
	}
}
