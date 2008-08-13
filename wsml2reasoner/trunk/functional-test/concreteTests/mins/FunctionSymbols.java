package concreteTests.mins;

import org.wsml.reasoner.api.LPReasoner;
import abstractTests.lp.AbstractFunctionSymbols;

public class FunctionSymbols extends AbstractFunctionSymbols
{
	public LPReasoner getLPReasoner()
	{
		return MinsHelper.getMINS();
	}
}
