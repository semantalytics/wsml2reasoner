/**
 * WSML Reasoner Implementation.
 *
 * Copyright (c) 2005, University of Innsbruck, Austria.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * You should have received a copy of the GNU Lesser General Public License along
 * with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 */

package org.wsml.reasoner.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.deri.wsmo4j.validator.WsmlValidatorImpl;
import org.omwg.ontology.Ontology;
import org.sti2.wsmo4j.factory.WsmlFactoryContainer;
import org.wsml.reasoner.api.DLReasoner;
import org.wsml.reasoner.api.LPReasoner;
import org.wsml.reasoner.api.StreamingLPReasoner;
import org.wsml.reasoner.api.WSMLReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsmo.common.WSML;
import org.wsmo.factory.FactoryContainer;
import org.wsmo.validator.ValidationError;
import org.wsmo.validator.ValidationWarning;
import org.wsmo.validator.WsmlValidator;

/**
 * A default implementation of a factory that constructs WSML Reasoners.
 * 
 * The factory will be based on prototypical implementations for WSML Reasoners
 * that are developed at DERI and at FZI.
 * 
 * @author Uwe Keller, DERI Innsbruck
 * @author Gabor Nagypal, FZI
 */
public class DefaultWSMLReasonerFactory implements WSMLReasonerFactory {

	private final static DefaultWSMLReasonerFactory aFactory = new DefaultWSMLReasonerFactory();

	/**
	 * @return an instance of the default implementation of the
	 *         WSMLReasonerFactory interface.
	 */
	public static WSMLReasonerFactory getFactory() {
		return aFactory;
	}

	private String determineVariant(Ontology ontology) {
		assert ontology != null;

		WsmlValidator validator = new WsmlValidatorImpl();
		String variant = validator.determineVariant(ontology,
				new ArrayList<ValidationError>(),
				new ArrayList<ValidationWarning>());
		if (variant == null) {
			throw new RuntimeException(
					"Unable to determine WSML variant from given ontology: "
							+ ontology.getIdentifier());
		}
		return variant;
	}

	private BuiltInReasoner extractReasoner(Map<String, Object> params,
			BuiltInReasoner defaultReasoner) {
		assert params != null;

		BuiltInReasoner requested = (BuiltInReasoner) params
				.get(PARAM_BUILT_IN_REASONER);
		if (requested != null) {
			return requested;
		}
		return defaultReasoner;
	}

	private FactoryContainer extractContainer(Map<String, Object> params) {
		assert params != null;

		FactoryContainer container = (FactoryContainer) params
				.get(PARAM_FACTORY_CONTAINER);
		if (container != null)
			return container;

		// TODO extraction of factories doesn't work as they cannot be combined
		// to a factory container
		// WsmoFactory wsmoFactory = (WsmoFactory)
		// params.get(PARAM_WSMO_FACTORY);
		// DataFactory dataFactory = (DataFactory)
		// params.get(PARAM_DATA_FACTORY);
		// LogicalExpressionFactory leFactory = (LogicalExpressionFactory)
		// params.get(PARAM_LE_FACTORY);

		return new WsmlFactoryContainer();
	}

	private void setAllowImportsFlag(DatalogBasedWSMLReasoner reasoner,
			Map<String, Object> params) {
		assert params != null;

		Object o = params.get(PARAM_ALLOW_IMPORTS);
		if (o != null && o instanceof Integer) {
			reasoner.setAllowImports((Integer) o);
		}
	}

	private void setAllowImportsFlag(
			DatalogBasedStreamingWSMLReasoner reasoner,
			Map<String, Object> params) {
		assert params != null;

		Object o = params.get(PARAM_ALLOW_IMPORTS);
		if (o != null && o instanceof Integer) {
			reasoner.setAllowImports((Integer) o);
		}
	}

	@Override
	public WSMLReasoner createWSMLReasoner(Map<String, Object> params,
			Ontology ontology) {
		if (ontology == null) {
			throw new IllegalArgumentException(
					"The ontology paramter must not be null");
		}
		if (params == null) {
			params = new HashMap<String, Object>();
		}

		String wsmlVariant = determineVariant(ontology);
		if (wsmlVariant.equals(WSML.WSML_CORE)) {
			return createCoreReasoner(params);
		} else if (wsmlVariant.equals(WSML.WSML_DL)) {
			return createDLReasoner(params);
		} else if (wsmlVariant.equals(WSML.WSML_DL2)) {
			return createDL2Reasoner(params);
		} else if (wsmlVariant.equals(WSML.WSML_FLIGHT)) {
			return createFlightReasoner(params);
		} else if (wsmlVariant.equals(WSML.WSML_RULE)) {
			return createRuleReasoner(params);
		}

		throw new RuntimeException("Unsupported WSML variant: " + wsmlVariant);
	}

	@Override
	public LPReasoner createCoreReasoner(Map<String, Object> params) {
		return createFlightReasoner(params);
	}

	@Override
	public LPReasoner createFlightReasoner(Map<String, Object> params) {
		if (params == null) {
			params = new HashMap<String, Object>();
		}
		FactoryContainer container = extractContainer(params);
		DatalogBasedWSMLReasoner reasoner = new DatalogBasedWSMLReasoner(
				extractReasoner(params, BuiltInReasoner.IRIS_STRATIFIED),
				container, params);
		setAllowImportsFlag(reasoner, params);
		return reasoner;
	}

	@Override
	public StreamingLPReasoner createStreamingFlightReasoner(
			Map<String, Object> params) {
		if (params == null) {
			params = new HashMap<String, Object>();
		}
		FactoryContainer container = extractContainer(params);
		DatalogBasedStreamingWSMLReasoner reasoner = new DatalogBasedStreamingWSMLReasoner(
				extractReasoner(params,
						BuiltInReasoner.STREAMING_IRIS_STRATIFIED), container,
				params);
		setAllowImportsFlag(reasoner, params);
		return reasoner;
	}

	@Override
	public LPReasoner createRuleReasoner(Map<String, Object> params) {
		if (params == null) {
			params = new HashMap<String, Object>();
		}
		FactoryContainer container = extractContainer(params);
		DatalogBasedWSMLReasoner reasoner = new DatalogBasedWSMLReasoner(
				extractReasoner(params, BuiltInReasoner.IRIS_WELL_FOUNDED),
				container, params);
		setAllowImportsFlag(reasoner, params);
		return reasoner;
	}

	@Override
	public DLReasoner createDL2Reasoner(Map<String, Object> params) {
		return createDLReasoner(params);
	}

	@Override
	public DLReasoner createDLReasoner(Map<String, Object> params) {
		if (params == null) {
			params = new HashMap<String, Object>();
		}

		BuiltInReasoner builtInReasoner = extractReasoner(params,
				BuiltInReasoner.ELLY);
		FactoryContainer container = extractContainer(params);
		ELPBasedWSMLReasoner reasoner = new ELPBasedWSMLReasoner(
				builtInReasoner, container);

		return reasoner;
	}

}