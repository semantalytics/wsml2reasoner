package concreteTests.iris;

import org.wsml.reasoner.api.LPReasoner;
import abstractTests.lp.AbstractFunctionSymbols;

public class FunctionSymbols extends AbstractFunctionSymbols
{
	public LPReasoner getReasoner()
	{
		return IrisHelper.getIRIS();
	}
}
