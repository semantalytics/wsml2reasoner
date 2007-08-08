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

//     public String PARAM_WSML_VARIANT = "WSML VARIANT";
//    
//     public enum WSMLVariant {
//    	 WSML_CORE, WSML_FLIGHT, WSML_RULE, WSML_DL, WSML_FULL
//     };

    public String PARAM_WSMO_FACTORY = "WSML FACTORY";

    public String PARAM_LE_FACTORY = "LE FACTORY";

    public String PARAM_DATA_FACTORY = "DATA FACTORY";

    public String PARAM_BUILT_IN_REASONER = "BUILT IN REASONER";

    //public String DISABLE_CONSISTENCY_CHECK = "Disable consistency check";
    /**
     * underlying reasoner reasoner uses a specific evaluation method (if supported)
     */
    public String PARAM_EVAL_METHOD = "Evaluation Method";
    
    public String PARAM_ALLOW_IMPORTS = "Allow ontology imports";
    
    public String PARAM_EXTERNAL_REASONER_URI = "ENDPOINT";

    public enum BuiltInReasoner {
        KAON2, MINS, PELLET, XSB, IRIS, TPTP, SPASS_PLUS_T
    };

    /**
     * Checks the variant of the given ontology and creates a WSML Reasoner 
     * backed up by the chosen Datalog or DL reasoning engine, using the 
     * default WSMO4J factories. If the chosen reasoner is not appropriate for the 
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
     *            implementation to use, see @link BuiltInReasoner</li>
     *            <li>PARAM_EVAL_METHOD: Evaluation Method of underlying reasoner;</li>
     *       </ul>
     *       Meaningful defaults are used, if a specific parameter is not
     *       defined. For the WSMO4J factories the factories with "null" as
     *       parameter are used, as the internal datalog reasoner Kaon2 is used and 
     *       as the internal dl reasoner Pellet is used by default.
     * @return the resoner
     */
    public WSMLReasoner createWSMLReasoner(Map<String, Object> params, Ontology ontology);
    
    /**
     * Checks the variant of the given ontology and creates a WSML Reasoner 
     * backed up by the default Datalog or DL reasoning engine, using the 
     * default WSMO4J factories.
     * 
     * @return the reasoner
     */
    public WSMLReasoner createWSMLReasoner(Ontology ontology);
    
    /**
     * Creates a WSML Core reasoner backed up by the chosen Datalog
     * implementation
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
     *            implementation to use, see @link BuiltInReasoner</li>
     *            <li>PARAM_EVAL_METHOD: Evaluation Method of underlying reasoner;</li>
     *       </ul>
     *       Meaningful defaults are used, if a specific parameter is not
     *       defined. For the WSMO4J factories the factories with "null" as
     *       parameter are used, and as the internal reasoner Kaon2 is used by
     *       default.
     * @return the resoner
     * @throws UnsupportedOperationException
     */
    public WSMLCoreReasoner createWSMLCoreReasoner(Map<String, Object> params)
            throws UnsupportedOperationException;

    /**
     * Creates a WSML Core reasoner backed up by the default Datalog engine, and
     * using the default WSMO4J factories.
     * 
     * @return the resoner
     * @throws UnsupportedOperationException
     */
    public WSMLCoreReasoner createWSMLCoreReasoner()
            throws UnsupportedOperationException;

    /**
     * Creates a WSML DL reasoner backed up by the chosen DL
     * implementation
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
     *            implementation to use, see @link BuiltInReasoner</li>
     *            <li>PARAM_EVAL_METHOD: Evaluation Method of underlying reasoner;</li>
     *       </ul>
     *       Meaningful defaults are used, if a specific parameter is not
     *       defined. For the WSMO4J factories the factories with "null" as
     *       parameter are used, and as the internal reasoner Pellet is used by
     *       default.
     * @return the reasoner
     * @throws UnsupportedOperationException
     */
    public WSMLDLReasoner createWSMLDLReasoner(Map<String, Object> params)
            throws UnsupportedOperationException;

    /**
     * Creates a WSML DL reasoner backed up by the default DL engine, and
     * using the default WSMO4J factories.
     * 
     * @return the reasoner
     * @throws UnsupportedOperationException
     */
    public WSMLDLReasoner createWSMLDLReasoner()
            throws UnsupportedOperationException;
    
    /**
     * Creates a WSML Flight reasoner backed up by the chosen Datalog
     * implementation
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
     *            <li>PARAM_EVAL_METHOD: Evaluation Method of underlying reasoner;</li>
     *       </ul>
     *       Meaningful defaults are used, if a specific parameter is not
     *       defined. For the WSMO4J factories the factories with "null" as
     *       parameter are used, and as the internal reasoner Kaon2 is used by
     *       default.
     * @return the resoner
     * @throws UnsupportedOperationException
     */
    public WSMLFlightReasoner createWSMLFlightReasoner(Map<String, Object> params)
            throws UnsupportedOperationException;

    /**
     * Creates a WSML Flight reasoner backed up by the default Datalog engine
     * and the default WSMO4J factories.
     * 
     * @return the resoner
     * @throws UnsupportedOperationException
     */
    public WSMLFlightReasoner createWSMLFlightReasoner()
    	throws UnsupportedOperationException;

    public WSMLFOLReasoner createWSMLFOLReasoner()
    	throws UnsupportedOperationException;
    
    public WSMLFOLReasoner createWSMLFOLReasoner(Map<String, Object> params)
    	throws UnsupportedOperationException;
    
    public WSMLRuleReasoner createWSMLRuleReasoner()
		throws UnsupportedOperationException;

	public WSMLReasoner createWSMLRuleReasoner(Map<String, Object> params)
		throws UnsupportedOperationException;;
}
