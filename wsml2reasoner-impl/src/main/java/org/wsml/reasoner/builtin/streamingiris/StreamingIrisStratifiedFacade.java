package org.wsml.reasoner.builtin.streamingiris;

import java.util.Map;

import org.wsmo.factory.FactoryContainer;

import at.sti2.streamingiris.Configuration;

/**
 * <p>
 * The facade for the streaming iris reasoner with (locally) stratified
 * negation, i.e. for WSML-Flight.
 * </p>
 */
public class StreamingIrisStratifiedFacade extends AbstractStreamingIrisFacade {

	public StreamingIrisStratifiedFacade(final FactoryContainer factory,
			final Map<String, Object> config) {
		super(factory, config);
	}

	protected void configureStreamingIris(Configuration configuration) {
	}
}
