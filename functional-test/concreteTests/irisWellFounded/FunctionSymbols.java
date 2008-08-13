package concreteTests.irisWellFounded;

import org.wsml.reasoner.api.LPReasoner;
import abstractTests.lp.AbstractFunctionSymbols;

public class FunctionSymbols extends AbstractFunctionSymbols
{
	public LPReasoner getLPReasoner()
	{
		return IrisHelper.getReasoner();
	}
}
