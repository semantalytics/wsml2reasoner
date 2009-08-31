package org.wsml.reasoner.builtin.mins;

import java.util.Map;

import org.wsmo.factory.Factory;

public class MinsNaiveFacade extends AbstractMinsFacade
{
	public MinsNaiveFacade(Factory wsmoManager, final Map<String, Object> config) {
		super( wsmoManager, config );
	}
    
	protected int getEvaluationMethod()
    {
    	return EvaluationMethod.NAIVE.getMethod();
    }
}
