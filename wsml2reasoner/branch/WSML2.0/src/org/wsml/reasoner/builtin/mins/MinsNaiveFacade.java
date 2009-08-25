package org.wsml.reasoner.builtin.mins;

import java.util.Map;

import org.wsml.reasoner.impl.WSMO4JManager;

public class MinsNaiveFacade extends AbstractMinsFacade
{
	public MinsNaiveFacade(WSMO4JManager wsmoManager, final Map<String, Object> config) {
		super( wsmoManager, config );
	}
    
	protected int getEvaluationMethod()
    {
    	return EvaluationMethod.NAIVE.getMethod();
    }
}
