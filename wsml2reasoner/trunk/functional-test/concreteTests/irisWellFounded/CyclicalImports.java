package concreteTests.irisWellFounded;

import org.wsml.reasoner.api.LPReasoner;
import abstractTests.core.AbstractCyclicalImports;

public class CyclicalImports extends AbstractCyclicalImports
{
	public LPReasoner getReasoner()
	{
		return IrisHelper.getReasoner();
	}
}
