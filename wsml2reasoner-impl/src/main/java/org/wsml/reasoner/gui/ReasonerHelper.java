package org.wsml.reasoner.gui;

import java.util.HashMap;
import java.util.Map;

import org.wsml.reasoner.api.DLReasoner;
import org.wsml.reasoner.api.LPReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.impl.DefaultWSMLReasonerFactory;

/**
 * A helper class to make creating reasoner objects very simple.
 */
public class ReasonerHelper {
	/** The allow imports flag. This is always true. */
	public static final boolean ALLOW_IMPORTS = true;

	/**
	 * Create a DL reasoner
	 * 
	 * @param reasoner
	 *            The enum identifying the reasoner.
	 * @return The new wsml reasoner object.
	 */
	public static DLReasoner getDLReasoner(
			WSMLReasonerFactory.BuiltInReasoner reasoner) {
		Map<String, Object> params = new HashMap<String, Object>();

		params.put(DefaultWSMLReasonerFactory.PARAM_BUILT_IN_REASONER, reasoner);
		params.put(WSMLReasonerFactory.PARAM_ALLOW_IMPORTS, ALLOW_IMPORTS);

		return DefaultWSMLReasonerFactory.getFactory()
				.createDL2Reasoner(params);
	}

	/**
	 * Create a LP reasoner
	 * 
	 * @param reasoner
	 *            The enum identifying the reasoner.
	 * @return The new wsml reasoner object.
	 */
	public static LPReasoner getLPReasoner(
			WSMLReasonerFactory.BuiltInReasoner reasoner) {
		Map<String, Object> params = new HashMap<String, Object>();

		params.put(WSMLReasonerFactory.PARAM_BUILT_IN_REASONER, reasoner);
		params.put(WSMLReasonerFactory.PARAM_ALLOW_IMPORTS, ALLOW_IMPORTS);

		return DefaultWSMLReasonerFactory.getFactory().createRuleReasoner(
				params);
	}
}
