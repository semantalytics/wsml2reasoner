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

/**
 * An interface for getting WSML Reasoners for the various variants of WSML.
 * 
 * @author Uwe Keller, DERI Innsbruck
 */
public interface WSMLReasonerFactory {

    public static final String PARAM_WSMO_FACTORY = "WSML FACTORY";

    public static final String PARAM_LE_FACTORY = "LE FACTORY";

    public static final String PARAM_DATA_FACTORY = "DATA FACTORY";

    public static final String PARAM_BUILT_IN_REASONER = "BUILT IN REASONER";

    /**
     * underlying reasoner reasoner uses a specific evaluation method (if
     * supported)
     */
    public static final String PARAM_EVAL_METHOD = "Evaluation Method";

    public static final String PARAM_ALLOW_IMPORTS = "Allow ontology imports";

    public static final String PARAM_EXTERNAL_REASONER_URI = "ENDPOINT";

    /**
     * Enum to select which built-in reasoner to use.
     */
    public enum BuiltInReasoner {
        KAON2,
        KAON2DL,
        MINS,
//        MINS_NAIVE,
        PELLET,
        XSB,
        IRIS_STRATIFIED,
        IRIS_WELL_FOUNDED,
        TPTP,
        SPASS
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
     *            <li>PARAM_WSMO_FACTORY: The WsmoFactory implementation to use</li>
     *            <li>PARAM_LE_FACTORY: The LogicalExpressionFactory
     *            implementation to use</li>
     *            <li>PARAM_DATA_FACTORY: The DataFactory implementation to use</li>
     *            <li>PARAM_BUILT_IN_REASONER: The internal reasoner
     *            implementation to use, see
     * @link BuiltInReasoner</li>
     *       <li>PARAM_EVAL_METHOD: Evaluation Method of underlying reasoner;</li>
     *       </ul>
     *       Meaningful defaults are used, if a specific parameter is not
     *       defined. For the WSMO4J factories the factories with "null" as
     *       parameter are used, as the internal datalog reasoner Kaon2 is used
     *       and as the internal dl reasoner Pellet is used by default.
     * @return the resoner
     */
    public WSMLReasoner createWSMLReasoner(Map<String, Object> params, Ontology ontology);

    public LPReasoner createCoreReasoner(Map<String, Object> params);

    public DLReasoner createDLReasoner(Map<String, Object> params);

    public LPReasoner createFlightReasoner(Map<String, Object> params);

    public LPReasoner createRuleReasoner(Map<String, Object> params);

    public FOLReasoner createFOLReasoner(Map<String, Object> params);
}
