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

package org.wsml.reasoner.api;

import java.util.Map;

import org.omwg.ontology.Ontology;
import org.wsmo.factory.FactoryContainer;

/**
 * An interface for getting WSML Reasoners for the various variants of WSML.
 * 
 * @author Uwe Keller, DERI Innsbruck
 */
public interface WSMLReasonerFactory {

	/**
	 * Key for params map, use a {@link FactoryContainer} object as value. 
	 */
	public static final String PARAM_FACTORY_CONTAINER = "FACTORY CONTAINER";

	/**
	 * Use {@link #PARAM_FACTORY_CONTAINER}.
	 */
	@Deprecated
	public static final String PARAM_WSMO_FACTORY = "WSML FACTORY";

	/**
	 * Use {@link #PARAM_FACTORY_CONTAINER}.
	 */
	@Deprecated
    public static final String PARAM_LE_FACTORY = "LE FACTORY";

	/**
	 * Use {@link #PARAM_FACTORY_CONTAINER}.
	 */
	@Deprecated
    public static final String PARAM_DATA_FACTORY = "DATA FACTORY";

	/**
	 * Key for params map, use a {@link BuiltInReasoner} as value. 
	 */
    public static final String PARAM_BUILT_IN_REASONER = "BUILT IN REASONER";

	/**
	 * Key for params map, use a {@link Integer} as value. 
	 */
    public static final String PARAM_ALLOW_IMPORTS = "Allow ontology imports";

    public static final String PARAM_EXTERNAL_REASONER_URI = "ENDPOINT";

    /**
     * Enum to select which built-in reasoner to use.
     */
    public enum BuiltInReasoner {
        IRIS_STRATIFIED,
        IRIS_WELL_FOUNDED,
        IRIS_SLDNF,
        ELLY
    };

    /**
     * Checks the variant of the given ontology and creates a WSML Reasoner
     * backed up by the chosen Datalog or DL reasoning engine, using the default
     * WSMO4J factories. If the chosen reasoner is not appropriate for the
     * variant of the ontology, the corresponding default reasoner is used.
     * 
     * @param params
     *            configuration parameters. The following parameters are
     *            accepted at the moment:
     *            <ul>
     *            <li>PARAM_FACTORY_CONTAINER: The Factory container instance to use</li>
     *            <li>PARAM_BUILT_IN_REASONER: The internal reasoner implementation to use, see
     *                @link BuiltInReasoner</li>
     *            <li>PARAM_EVAL_METHOD: Evaluation Method of underlying reasoner;</li>
     *       </ul>
     *       Meaningful defaults are used, if a specific parameter is not
     *       defined. For the WSMO4J factories the factories with "null" as
     *       parameter are used, as the internal datalog reasoner IRIS is used
     *       and as the internal DL reasoner ELLY is used by default.
     * @return the resoner
     */
    public WSMLReasoner createWSMLReasoner(Map<String, Object> params, Ontology ontology);

    /**
     * Use {@link WSMLReasonerFactory#createDLReasoner(Map)} instead.
     * This method simply dispatches to {@code createDLReasoner(Map<String, Object> params)}.
     * 
     * @param params
     *            configuration parameters. The following parameters are
     *            accepted at the moment:
     *            <ul>
     *            <li>PARAM_FACTORY_CONTAINER: The Factory container instance to use</li>
     *            <li>PARAM_BUILT_IN_REASONER: The internal reasoner implementation to use, see
     *                @link BuiltInReasoner</li>
     *            <li>PARAM_EVAL_METHOD: Evaluation Method of underlying reasoner;</li>
     *       </ul>
     * @return the Core resoner.
     */
    public LPReasoner createCoreReasoner(Map<String, Object> params);

    /**
     * Use {@link WSMLReasonerFactory#createDLReasoner(Map)} instead.
     * This method simply dispatches to {@code createDLReasoner(Map<String, Object> params)}.
     * 
     * @param params
     *            configuration parameters. The following parameters are
     *            accepted at the moment:
     *            <ul>
     *            <li>PARAM_FACTORY_CONTAINER: The Factory container instance to use</li>
     *            <li>PARAM_BUILT_IN_REASONER: The internal reasoner implementation to use, see
     *                @link BuiltInReasoner</li>
     *            <li>PARAM_EVAL_METHOD: Evaluation Method of underlying reasoner;</li>
     *       </ul>
     * @return the DL resoner.
     */
    public DLReasoner createDLReasoner(Map<String, Object> params);

    /**
     * Use {@link WSMLReasonerFactory#createDLReasoner(Map)} instead.
     * This method simply dispatches to {@code createDLReasoner(Map<String, Object> params)}.
     * 
     * @param params
     *            configuration parameters. The following parameters are
     *            accepted at the moment:
     *            <ul>
     *            <li>PARAM_FACTORY_CONTAINER: The Factory container instance to use</li>
     *            <li>PARAM_BUILT_IN_REASONER: The internal reasoner implementation to use, see
     *                @link BuiltInReasoner</li>
     *            <li>PARAM_EVAL_METHOD: Evaluation Method of underlying reasoner;</li>
     *       </ul>
     * @return the DL resoner.
     */
    @Deprecated 
    public DLReasoner createDL2Reasoner(Map<String, Object> params);

    /**
     * Use {@link WSMLReasonerFactory#createDLReasoner(Map)} instead.
     * This method simply dispatches to {@code createDLReasoner(Map<String, Object> params)}.
     * 
     * @param params
     *            configuration parameters. The following parameters are
     *            accepted at the moment:
     *            <ul>
     *            <li>PARAM_FACTORY_CONTAINER: The Factory container instance to use</li>
     *            <li>PARAM_BUILT_IN_REASONER: The internal reasoner implementation to use, see
     *                @link BuiltInReasoner</li>
     *            <li>PARAM_EVAL_METHOD: Evaluation Method of underlying reasoner;</li>
     *       </ul>
     * @return the Flight resoner.
     */
    public LPReasoner createFlightReasoner(Map<String, Object> params);

    /**
     * Use {@link WSMLReasonerFactory#createDLReasoner(Map)} instead.
     * This method simply dispatches to {@code createDLReasoner(Map<String, Object> params)}.
     * 
     * @param params
     *            configuration parameters. The following parameters are
     *            accepted at the moment:
     *            <ul>
     *            <li>PARAM_FACTORY_CONTAINER: The Factory container instance to use</li>
     *            <li>PARAM_BUILT_IN_REASONER: The internal reasoner implementation to use, see
     *                @link BuiltInReasoner</li>
     *            <li>PARAM_EVAL_METHOD: Evaluation Method of underlying reasoner;</li>
     *       </ul>
     * @return the Rule resoner.
     */
    public LPReasoner createRuleReasoner(Map<String, Object> params);

}