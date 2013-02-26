package org.wsml.reasoner.builtin.iris;

import java.util.Map;

import org.deri.iris.Configuration;
import org.deri.iris.evaluation.topdown.sldnf.SLDNFEvaluationStrategyFactory;
import org.wsmo.factory.FactoryContainer;

/**
 * <p>
 * The facade for the iris reasoner with well-founded negation and unsafe
 * rule-handling, i.e. for WSML-Rule.
 * </p>
 */
public class IrisSLDNFFacade extends IrisStratifiedFacade {
	public IrisSLDNFFacade(final FactoryContainer factory,
			final Map<String, Object> config) {
		super(factory, config);
	}

	@Override
	protected void configureIris(Configuration configuration) {
		configuration.evaluationStrategyFactory = new SLDNFEvaluationStrategyFactory();
	}
}
