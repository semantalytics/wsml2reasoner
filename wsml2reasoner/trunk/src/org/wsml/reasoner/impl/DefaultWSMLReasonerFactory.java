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

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.deri.wsmo4j.validator.WsmlValidatorImpl;
import org.omwg.ontology.Ontology;
import org.wsml.reasoner.api.WSMLCoreReasoner;
import org.wsml.reasoner.api.WSMLDLReasoner;
import org.wsml.reasoner.api.WSMLFOLReasoner;
import org.wsml.reasoner.api.WSMLFlightReasoner;
import org.wsml.reasoner.api.WSMLReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.api.WSMLRuleReasoner;
import org.wsml.reasoner.builtin.tptp.TPTPFacade;
import org.wsmo.common.WSML;
import org.wsmo.factory.DataFactory;
import org.wsmo.factory.Factory;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;
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
        WsmoFactory wsmoFactory;
        LogicalExpressionFactory leFactory;
        DataFactory dataFactory;
        String dbConf;
        wsmoFactory = params.containsKey(PARAM_WSMO_FACTORY) ? (WsmoFactory) params
                .get(PARAM_WSMO_FACTORY)
                : Factory.createWsmoFactory(null);
        leFactory = params.containsKey(PARAM_LE_FACTORY) ? (LogicalExpressionFactory) params
                .get(PARAM_LE_FACTORY)
                : Factory.createLogicalExpressionFactory(null);
        dataFactory = params.containsKey(PARAM_DATA_FACTORY) ? (DataFactory) params
                .get(PARAM_DATA_FACTORY)
                : Factory.createDataFactory(null);
        dbConf = params.containsKey(PARAM_DB_CONF_FILE) ? (String) params
                .get(PARAM_DB_CONF_FILE)
                : "";
        return new WSMO4JManager(wsmoFactory, leFactory, dataFactory, dbConf);
    }

    public WSMLReasoner createWSMLReasoner(Map<String, Object> params, Ontology ontology) {
    	WsmlValidator validator = new WsmlValidatorImpl();
    	String variant = validator.determineVariant(ontology, new Vector(), new Vector());
    	if (variant == null) {
    		throw new RuntimeException("Given ontology is not valid WSML-FULL!");
    	}
    	if (params == null) {
			if (variant.equals(WSML.WSML_DL))
				return new DLBasedWSMLReasoner(BuiltInReasoner.PELLET, 
						new WSMO4JManager());
			else if (variant.equals(WSML.WSML_CORE) || variant.equals(WSML.WSML_FLIGHT)) {
				return new DatalogBasedWSMLReasoner(BuiltInReasoner.KAON2, 
						new WSMO4JManager());
			}
		}
		else {
			WSMO4JManager wsmoManager = extractWsmoManager(params);
			if (variant.equals(WSML.WSML_DL)) {
				BuiltInReasoner builtin = params.containsKey(PARAM_BUILT_IN_REASONER) 
        				? (BuiltInReasoner) params.get(PARAM_BUILT_IN_REASONER) 
        						: BuiltInReasoner.PELLET;
        		return new DLBasedWSMLReasoner(builtin, wsmoManager);
			}
			else if (variant.equals(WSML.WSML_CORE) || variant.equals(WSML.WSML_FLIGHT)
					|| variant.equals(WSML.WSML_RULE)) {
				BuiltInReasoner builtin = params.containsKey(PARAM_BUILT_IN_REASONER) 
	            		? (BuiltInReasoner) params.get(PARAM_BUILT_IN_REASONER)
	            				: BuiltInReasoner.KAON2;
	            return new DatalogBasedWSMLReasoner(builtin, wsmoManager);
			}	
		}
    	throw new RuntimeException("Reasoning is not yet supported for WSML-FULL!");
    }

    public WSMLReasoner createWSMLReasoner(Ontology ontology) {
    	return createWSMLReasoner(null, ontology);
    }
    
    public WSMLCoreReasoner createWSMLCoreReasoner(Map<String, Object> params)
            throws UnsupportedOperationException {
        if (params == null) {
            return new org.wsml.reasoner.impl.DatalogBasedWSMLReasoner(
                    BuiltInReasoner.KAON2, new WSMO4JManager());
        } else {
            WSMO4JManager wsmoManager = extractWsmoManager(params);
            BuiltInReasoner builtin = params
                    .containsKey(PARAM_BUILT_IN_REASONER) ? (BuiltInReasoner) params
                    .get(PARAM_BUILT_IN_REASONER)
                    : BuiltInReasoner.KAON2;
            return new org.wsml.reasoner.impl.DatalogBasedWSMLReasoner(
                    builtin, wsmoManager);
        }
    }

    public WSMLCoreReasoner createWSMLCoreReasoner()
            throws UnsupportedOperationException {
        return createWSMLCoreReasoner(null);
    }

    public WSMLDLReasoner createWSMLDLReasoner(Map<String, Object> params) {
		if (params == null) {
			return new DLBasedWSMLReasoner(BuiltInReasoner.PELLET, 
					new WSMO4JManager());
		}
		else {
			WSMO4JManager wsmoManager = extractWsmoManager(params);
            BuiltInReasoner builtin = params.containsKey(PARAM_BUILT_IN_REASONER) 
            		? (BuiltInReasoner) params.get(PARAM_BUILT_IN_REASONER) 
            		: BuiltInReasoner.PELLET;
            DLBasedWSMLReasoner dlwsmlr = new DLBasedWSMLReasoner(builtin, 
            		wsmoManager);
            return dlwsmlr;
		}
	}

	public WSMLDLReasoner createWSMLDLReasoner() 
			throws UnsupportedOperationException {
		return createWSMLDLReasoner(null);
	}
    
    public WSMLFlightReasoner createWSMLFlightReasoner(Map<String, Object> params)
            throws UnsupportedOperationException {
        if (params == null) {
            return new org.wsml.reasoner.impl.DatalogBasedWSMLReasoner(
                    BuiltInReasoner.KAON2, new WSMO4JManager());
        } else {
            WSMO4JManager wsmoManager = extractWsmoManager(params);
            BuiltInReasoner builtin = params
                    .containsKey(PARAM_BUILT_IN_REASONER) ? (BuiltInReasoner) params
                    .get(PARAM_BUILT_IN_REASONER)
                    : BuiltInReasoner.KAON2;
            DatalogBasedWSMLReasoner dbwsmlr = new org.wsml.reasoner.impl.DatalogBasedWSMLReasoner(
                    builtin, wsmoManager);
            
            Object o = params.get(PARAM_EVAL_METHOD);
            if (o!=null && o instanceof Integer){
                dbwsmlr.setEvalMethod((Integer)o);
            }
            
            o = params.get(PARAM_ALLOW_IMPORTS);
            if (o!=null && o instanceof Integer){
                dbwsmlr.setAllowImports((Integer)o);
            }

            return dbwsmlr;
        }
    }

    public WSMLFlightReasoner createWSMLFlightReasoner()
            throws UnsupportedOperationException {
        return createWSMLFlightReasoner(null);
    }

    public WSMLFOLReasoner createWSMLFOLReasoner() throws UnsupportedOperationException {
        return createWSMLFOLReasoner(null);
    }
    
    public WSMLFOLReasoner createWSMLFOLReasoner(Map<String, Object> params) throws UnsupportedOperationException {
        if (params == null) {
        	params = new HashMap<String, Object>();
        }

        BuiltInReasoner reasoner = BuiltInReasoner.SPASS;
        if (params.containsKey(PARAM_BUILT_IN_REASONER)){
        	reasoner = (BuiltInReasoner)params.get(PARAM_BUILT_IN_REASONER);
        }
        
        String uri=null;
        if (params.containsKey(PARAM_EXTERNAL_REASONER_URI)){
        	uri = (String) params.get(PARAM_EXTERNAL_REASONER_URI);
        }
        if (uri==null){
        	if (reasoner==BuiltInReasoner.SPASS){
        		uri=TPTPFacade.DERI_SPASS_REASONER;
        	}else if (reasoner==BuiltInReasoner.TPTP){
        		uri=TPTPFacade.DERI_TPTP_REASONER;
        	}else{
        		throw new RuntimeException("need to specify URI");
        	}
        }
        
        return new org.wsml.reasoner.impl.FOLBasedWSMLReasoner(
    			reasoner,
    			new WSMO4JManager(),
    			uri);
    }

	public WSMLRuleReasoner createWSMLRuleReasoner() throws UnsupportedOperationException {
		return createWSMLRuleReasoner(null);
	}

	public WSMLRuleReasoner createWSMLRuleReasoner(Map<String, Object> params) throws UnsupportedOperationException {
        if (params == null) {
            return new org.wsml.reasoner.impl.DatalogBasedWSMLReasoner(
                    BuiltInReasoner.MINS, new WSMO4JManager());
        } else {
            WSMO4JManager wsmoManager = extractWsmoManager(params);
            BuiltInReasoner builtin = params
                    .containsKey(PARAM_BUILT_IN_REASONER) ? (BuiltInReasoner) params
                    .get(PARAM_BUILT_IN_REASONER)
                    : BuiltInReasoner.MINS;
            DatalogBasedWSMLReasoner dbwsmlr = new org.wsml.reasoner.impl.DatalogBasedWSMLReasoner(
                    builtin, wsmoManager);
            
            Object o = params.get(PARAM_EVAL_METHOD);
            if (o!=null && o instanceof Integer){
                dbwsmlr.setEvalMethod((Integer)o);
            }
            
            o = params.get(PARAM_ALLOW_IMPORTS);
            if (o!=null && o instanceof Integer){
                dbwsmlr.setAllowImports((Integer)o);
            }

            return dbwsmlr;
        }
    }
}