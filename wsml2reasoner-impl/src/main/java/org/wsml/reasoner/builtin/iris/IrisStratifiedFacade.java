package org.wsml.reasoner.builtin.iris;

import java.util.Map;

import org.deri.iris.Configuration;
import org.wsmo.factory.FactoryContainer;

/**
 * <p>
 * The facade for the iris reasoner with (locally) stratified negation, i.e. for
 * WSML-Flight.
 * </p>
 */
public class IrisStratifiedFacade extends AbstractIrisFacade {

	public IrisStratifiedFacade(final FactoryContainer factory,
			final Map<String, Object> config) {
		super(factory, config);
	}

	// public IrisFacade() {
	// }

	protected void configureIris(Configuration configuration) {
		// Nothing to do. Accept defaults.
	}
}
