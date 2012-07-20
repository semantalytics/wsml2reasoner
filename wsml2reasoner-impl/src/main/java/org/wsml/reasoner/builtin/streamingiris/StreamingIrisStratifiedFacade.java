/*
 * WSML2Reasoner
 * An extensible framework for reasoning with WSML ontologies.
 * 
 * Copyright (C) 2008 Semantic Technology Institute (STI) Innsbruck, 
 * University of Innsbruck, Technikerstrasse 21a, 6020 Innsbruck, Austria.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 */
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

	// public IrisFacade() {
	// }

	protected void configureIris(Configuration configuration) {
		// Nothing to do. Accept defaults.
	}
}
