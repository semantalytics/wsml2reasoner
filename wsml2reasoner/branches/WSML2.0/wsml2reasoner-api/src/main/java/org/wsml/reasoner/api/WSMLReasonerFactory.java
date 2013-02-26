package org.wsml.reasoner.api;

import java.util.Map;

import org.omwg.ontology.Ontology;
import org.wsmo.factory.FactoryContainer;

/**
 * An interface for getting WSML Reasoners for the various variants of WSML.
 * 
 * TODO danwin when only DL reasoner is right why not deprecate all fields TODO
 * danwin all create methods point to createDLreasoner why not peprecate them
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

	/**
	 * TODO danwin comment
	 */
	public static final String PARAM_EXTERNAL_REASONER_URI = "ENDPOINT";

	/**
	 * Enum to select which built-in reasoner to use.
	 */
	public enum BuiltInReasoner {
		IRIS_STRATIFIED, IRIS_WELL_FOUNDED, IRIS_SLDNF, STREAMING_IRIS_STRATIFIED, ELLY
	};

	/**
	 * * Checks the variant of the given ontology and creates a WSML Reasoner
	 * backed up by the chosen Datalog or DL reasoning engine, using the default
	 * WSMO4J factories. If the chosen reasoner is not appropriate for the
	 * variant of the ontology, the corresponding default reasoner is used.
	 * 
	 * @param params
	 *            configuration parameters. The following parameters are
	 *            accepted at the moment:
	 *            <ul>
	 *            <li>PARAM_FACTORY_CONTAINER: The Factory container instance to
	 *            use</li>
	 *            <li>PARAM_BUILT_IN_REASONER: The internal reasoner
	 *            implementation to use, see
	 * @link BuiltInReasoner</li> <li>PARAM_EVAL_METHOD: Evaluation Method of
	 *       underlying reasoner;</li>
	 *       </ul>
	 *       Meaningful defaults are used, if a specific parameter is not
	 *       defined. For the WSMO4J factories the factories with "null" as
	 *       parameter are used, as the internal datalog reasoner IRIS is used
	 *       and as the internal DL reasoner ELLY is used by default.
	 * @param ontology
	 * @return the reasoner
	 */
	public WSMLReasoner createWSMLReasoner(Map<String, Object> params,
			Ontology ontology);

	/**
	 * Use {@link WSMLReasonerFactory#createDLReasoner(Map)} instead. This
	 * method simply dispatches to
	 * {@code createDLReasoner(Map<String, Object> params)}.
	 * 
	 * @param params
	 *            configuration parameters. The following parameters are
	 *            accepted at the moment:
	 *            <ul>
	 *            <li>PARAM_FACTORY_CONTAINER: The Factory container instance to
	 *            use</li>
	 *            <li>PARAM_BUILT_IN_REASONER: The internal reasoner
	 *            implementation to use, see
	 * @link BuiltInReasoner</li> <li>PARAM_EVAL_METHOD: Evaluation Method of
	 *       underlying reasoner;</li>
	 *       </ul>
	 * @return the Core reasoner.
	 */
	public LPReasoner createCoreReasoner(Map<String, Object> params);

	/**
	 * Creates a DL reasoner.
	 * 
	 * @param params
	 *            configuration parameters. The following parameters are
	 *            accepted at the moment:
	 *            <ul>
	 *            <li>PARAM_FACTORY_CONTAINER: The Factory container instance to
	 *            use</li>
	 *            <li>PARAM_BUILT_IN_REASONER: The internal reasoner
	 *            implementation to use, see
	 * @link BuiltInReasoner</li> <li>PARAM_EVAL_METHOD: Evaluation Method of
	 *       underlying reasoner;</li>
	 *       </ul>
	 * @return the DL reasoner.
	 */
	public DLReasoner createDLReasoner(Map<String, Object> params);

	/**
	 * Use {@link WSMLReasonerFactory#createDLReasoner(Map)} instead. This
	 * method simply dispatches to
	 * {@code createDLReasoner(Map<String, Object> params)}.
	 * 
	 * @param params
	 *            configuration parameters. The following parameters are
	 *            accepted at the moment:
	 *            <ul>
	 *            <li>PARAM_FACTORY_CONTAINER: The Factory container instance to
	 *            use</li>
	 *            <li>PARAM_BUILT_IN_REASONER: The internal reasoner
	 *            implementation to use, see
	 * @link BuiltInReasoner</li> <li>PARAM_EVAL_METHOD: Evaluation Method of
	 *       underlying reasoner;</li>
	 *       </ul>
	 * @return the DL reasoner.
	 */
	@Deprecated
	public DLReasoner createDL2Reasoner(Map<String, Object> params);

	/**
	 * Use {@link WSMLReasonerFactory#createDLReasoner(Map)} instead. This
	 * method simply dispatches to
	 * {@code createDLReasoner(Map<String, Object> params)}.
	 * 
	 * @param params
	 *            configuration parameters. The following parameters are
	 *            accepted at the moment:
	 *            <ul>
	 *            <li>PARAM_FACTORY_CONTAINER: The Factory container instance to
	 *            use</li>
	 *            <li>PARAM_BUILT_IN_REASONER: The internal reasoner
	 *            implementation to use, see
	 * @link BuiltInReasoner</li> <li>PARAM_EVAL_METHOD: Evaluation Method of
	 *       underlying reasoner;</li>
	 *       </ul>
	 * @return the Flight reasoner.
	 */
	public LPReasoner createFlightReasoner(Map<String, Object> params);

	/**
	 * Use {@link WSMLReasonerFactory#createDLReasoner(Map)} instead. This
	 * method simply dispatches to
	 * {@code createDLReasoner(Map<String, Object> params)}.
	 * 
	 * @param params
	 *            configuration parameters. The following parameters are
	 *            accepted at the moment:
	 *            <ul>
	 *            <li>PARAM_FACTORY_CONTAINER: The Factory container instance to
	 *            use</li>
	 *            <li>PARAM_BUILT_IN_REASONER: The internal reasoner
	 *            implementation to use, see
	 * @link BuiltInReasoner</li> <li>PARAM_EVAL_METHOD: Evaluation Method of
	 *       underlying reasoner;</li>
	 *       </ul>
	 * @return the Streaming Flight reasoner.
	 */
	public StreamingLPReasoner createStreamingFlightReasoner(
			Map<String, Object> params);

	/**
	 * Use {@link WSMLReasonerFactory#createDLReasoner(Map)} instead. This
	 * method simply dispatches to
	 * {@code createDLReasoner(Map<String, Object> params)}.
	 * 
	 * @param params
	 *            configuration parameters. The following parameters are
	 *            accepted at the moment:
	 *            <ul>
	 *            <li>PARAM_FACTORY_CONTAINER: The Factory container instance to
	 *            use</li>
	 *            <li>PARAM_BUILT_IN_REASONER: The internal reasoner
	 *            implementation to use, see
	 * @link BuiltInReasoner</li> <li>PARAM_EVAL_METHOD: Evaluation Method of
	 *       underlying reasoner;</li>
	 *       </ul>
	 * @return the Rule reasoner.
	 */
	public LPReasoner createRuleReasoner(Map<String, Object> params);

}
