package org.wsml.reasoner.builtin.mins;

import java.util.Map;

import org.wsmo.factory.FactoryContainer;

public class MinsNaiveFacade extends AbstractMinsFacade
{
	public MinsNaiveFacade(FactoryContainer wsmoManager, final Map<String, Object> config) {
		super( wsmoManager, config );
	}
    
	protected int getEvaluationMethod()
    {
    	return EvaluationMethod.NAIVE.getMethod();
    }
}
