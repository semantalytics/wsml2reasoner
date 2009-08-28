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

import org.deri.wsmo4j.validator.WsmlValidatorTypedImpl;
import org.omwg.ontology.Ontology;
import org.sti2.wsmo4j.factory.FactoryImpl;
import org.wsml.reasoner.api.DLReasoner;
import org.wsml.reasoner.api.FOLReasoner;
import org.wsml.reasoner.api.LPReasoner;
import org.wsml.reasoner.api.WSMLReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.builtin.tptp.TPTPFacade;
import org.wsmo.common.WSML;
import org.wsmo.factory.DataFactory;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;
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

    private WSMO4JManager extractWsmoManager(Map<String, Object> params) {
        assert params != null;

        WsmoFactory wsmoFactory = params.containsKey(PARAM_WSMO_FACTORY) ? (WsmoFactory) params.get(PARAM_WSMO_FACTORY) : FactoryImpl.createNewInstance().getWsmoFactory();
        DataFactory wsmlDataFactory = params.containsKey(PARAM_DATA_FACTORY) ? (DataFactory) params.get(PARAM_DATA_FACTORY) : FactoryImpl.createNewInstance().getWsmlDataFactory(wsmoFactory);
        DataFactory xmlDataFactory = FactoryImpl.createNewInstance().getXmlDataFactory(wsmoFactory); // TODO gigi: Since the Factory stuff changed quite a bit, is there a parameter for the xml data factory? 
		LogicalExpressionFactory leFactory = params.containsKey(PARAM_LE_FACTORY) ? (LogicalExpressionFactory) params.get(PARAM_LE_FACTORY) : FactoryImpl.createNewInstance().getLogicalExpressionFactory(wsmoFactory, wsmlDataFactory, xmlDataFactory );
        
        return new WSMO4JManager(wsmoFactory, leFactory, wsmlDataFactory, xmlDataFactory);
    }

    private String determineVariant(Ontology ontology) {
        assert ontology != null;

        WsmlValidator validator = new WsmlValidatorTypedImpl();
        String variant = validator.determineVariant(ontology, new ArrayList<ValidationError>(), new ArrayList<ValidationWarning>());
        if (variant == null) {
            throw new RuntimeException("Unable to determine WSML variant from given ontology: " + ontology.getIdentifier());
        }
        return variant;
    }

    private BuiltInReasoner extractReasoner(Map<String, Object> params, BuiltInReasoner defaultReasoner) {
        assert params != null;

        BuiltInReasoner requested = (BuiltInReasoner) params.get(PARAM_BUILT_IN_REASONER);
        if (requested != null) {
            return requested;
        }
        return defaultReasoner;
    }

    private void setAllowImportsFlag(DatalogBasedWSMLReasoner reasoner, Map<String, Object> params) {
        assert params != null;

        Object o = params.get(PARAM_ALLOW_IMPORTS);
        if (o != null && o instanceof Integer) {
            reasoner.setAllowImports((Integer) o);
        }
    }

    private void setAllowImportsFlag(DLBasedWSMLReasoner reasoner, Map<String, Object> params) {
        assert params != null;

        Object o = params.get(PARAM_ALLOW_IMPORTS);
        if (o != null && o instanceof Integer) {
            reasoner.setAllowImports((Integer) o);
        }
    }

    public WSMLReasoner createWSMLReasoner(Map<String, Object> params, Ontology ontology) {
        if (ontology == null){
            throw new IllegalArgumentException("The ontology paramter may not be null");
        }
        if (params == null){
            params = new HashMap<String, Object>();
        }
        
        String wsmlVariant = determineVariant(ontology);
        if (wsmlVariant.equals(WSML.WSML_CORE)){
            return createCoreReasoner(params);
        }
        else if (wsmlVariant.equals(WSML.WSML_DL)){
            return createDLReasoner(params);
        }
        else if (wsmlVariant.equals(WSML.WSML_FLIGHT)){
            return createFlightReasoner(params);
        }
        else if (wsmlVariant.equals(WSML.WSML_RULE)){
            return createRuleReasoner(params);
        }

        throw new RuntimeException("Unsupported WSML variant: " + wsmlVariant);
    }

    public LPReasoner createCoreReasoner(Map<String, Object> params) {
        return createFlightReasoner(params);
    }

    public DLReasoner createDLReasoner(Map<String, Object> params) {
        if (params == null){
            params = new HashMap<String, Object>();
        }
        DLBasedWSMLReasoner reasoner = new DLBasedWSMLReasoner(extractReasoner(params, BuiltInReasoner.PELLET), extractWsmoManager(params));
        setAllowImportsFlag(reasoner, params);
        return reasoner;
    }

    public LPReasoner createFlightReasoner(Map<String, Object> params) {
        if (params == null){
            params = new HashMap<String, Object>();
        }
        DatalogBasedWSMLReasoner reasoner = new DatalogBasedWSMLReasoner(extractReasoner(params, BuiltInReasoner.IRIS_STRATIFIED), extractWsmoManager(params), params);
        setAllowImportsFlag(reasoner, params);
        return reasoner;
    }

    public LPReasoner createRuleReasoner(Map<String, Object> params) {
        if (params == null){
            params = new HashMap<String, Object>();
        }
        DatalogBasedWSMLReasoner reasoner = new DatalogBasedWSMLReasoner(extractReasoner(params, BuiltInReasoner.IRIS_WELL_FOUNDED), extractWsmoManager(params), params);
        setAllowImportsFlag(reasoner, params);
        return reasoner;
    }

    public FOLReasoner createFOLReasoner(Map<String, Object> params) {
        if (params == null){
            params = new HashMap<String, Object>();
        }
        
        // TODO: BARRY - Can you look at this, I have no idea what this actually trying to do
        BuiltInReasoner reasoner = extractReasoner(params, BuiltInReasoner.SPASS);
        String uri = null;
        if (params.containsKey(PARAM_EXTERNAL_REASONER_URI)) {
            uri = (String) params.get(PARAM_EXTERNAL_REASONER_URI);
        }
        if (uri == null) {
            if (reasoner == BuiltInReasoner.SPASS) {
                uri = TPTPFacade.DERI_SPASS_REASONER;
            }
            else if (reasoner == BuiltInReasoner.TPTP) {
                uri = TPTPFacade.DERI_TPTP_REASONER;
            }
            else {
                throw new RuntimeException("need to specify URI");
            }
        }
        return new org.wsml.reasoner.impl.FOLBasedWSMLReasoner(reasoner, extractWsmoManager(params), uri);
    }
}
