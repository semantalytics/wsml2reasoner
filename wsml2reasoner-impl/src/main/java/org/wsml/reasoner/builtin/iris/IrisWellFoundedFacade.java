package org.wsml.reasoner.builtin.iris;

import java.util.Map;

import org.deri.iris.Configuration;
import org.wsmo.factory.FactoryContainer;

/**
 * <p>
 * The facade for the iris reasoner with well-founded negation and unsafe
 * rule-handling, i.e. for WSML-Rule.
 * </p>
 */
public class IrisWellFoundedFacade extends IrisStratifiedFacade {
	public IrisWellFoundedFacade(final FactoryContainer m,
			final Map<String, Object> config) {
		super(m, config);
	}

	@Override
	protected void configureIris(Configuration configuration) {
		configuration.ruleSafetyProcessor = new org.deri.iris.rules.safety.AugmentingRuleSafetyProcessor();
		configuration.evaluationStrategyFactory = new org.deri.iris.evaluation.wellfounded.WellFoundedEvaluationStrategyFactory();
	}
}
