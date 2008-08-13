package concreteTests.mins;

import org.wsml.reasoner.api.WSMLReasoner;
import abstractTests.core.AbstractCyclicalImports;

public class CyclicalImports extends AbstractCyclicalImports
{
	public WSMLReasoner getReasoner()
	{
		return MinsHelper.getMINS();
	}
}
