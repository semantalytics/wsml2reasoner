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
    	
        WsmoFactory wsmoFactory;
        LogicalExpressionFactory leFactory;
        DataFactory dataFactory;
        
        wsmoFactory = params.containsKey(PARAM_WSMO_FACTORY) ? (WsmoFactory) params
                .get(PARAM_WSMO_FACTORY)
                : Factory.createWsmoFactory(null);
                
        leFactory = params.containsKey(PARAM_LE_FACTORY) ? (LogicalExpressionFactory) params
                .get(PARAM_LE_FACTORY)
                : Factory.createLogicalExpressionFactory(null);
                
        dataFactory = params.containsKey(PARAM_DATA_FACTORY) ? (DataFactory) params
                .get(PARAM_DATA_FACTORY)
                : Factory.createDataFactory(null);
                
        return new WSMO4JManager(wsmoFactory, leFactory, dataFactory);
    }

    private String determineVariant( Ontology ontology )
    {
    	assert ontology != null;
    	
    	WsmlValidator validator = new WsmlValidatorImpl();
    	String variant = validator.determineVariant(ontology, new ArrayList<ValidationError>(), new ArrayList<ValidationWarning>());
    	if (variant == null)
    		throw new RuntimeException("Unable to determine WSML variant from given ontology: " + ontology.getIdentifier() );
    	return variant;
    }
    
    private BuiltInReasoner extractReasoner( Map<String, Object> params, BuiltInReasoner defaultReasoner )
    {
    	assert params != null;

        BuiltInReasoner requested = (BuiltInReasoner) params.get( PARAM_BUILT_IN_REASONER );
        if( requested != null )
        	return requested;

    	return defaultReasoner;
    }
    
	private void setAllowImportsFlag( DatalogBasedWSMLReasoner reasoner, Map<String, Object> params )
	{
    	assert params != null;

        Object o = params.get(PARAM_ALLOW_IMPORTS);
        if (o!=null && o instanceof Integer){
        	reasoner.setAllowImports((Integer)o);
        }
	}

    public WSMLReasoner createWSMLReasoner(Map<String, Object> params, Ontology ontology) {
    	if( ontology == null )
    		throw new IllegalArgumentException( "The ontology paramter may not be null" );
    	
        if (params == null)
        	params = new HashMap<String, Object>();

        String wsmlVariant = determineVariant(ontology);
    	
    	if( wsmlVariant.equals( WSML.WSML_CORE ) )
    		return createWSMLCoreReasoner( params );
    	
    	if( wsmlVariant.equals( WSML.WSML_DL ) )
    		return createWSMLDLReasoner( params );
    	
    	if( wsmlVariant.equals( WSML.WSML_FLIGHT ) )
    		return createWSMLFlightReasoner( params );
    	
    	if( wsmlVariant.equals( WSML.WSML_RULE ) )
    		return createWSMLRuleReasoner( params );

		throw new RuntimeException( "Unsupported WSML variant: " + wsmlVariant );
    }

    public WSMLReasoner createWSMLReasoner(Ontology ontology) {
    	return createWSMLReasoner(null, ontology);
    }
    
    public WSMLCoreReasoner createWSMLCoreReasoner(Map<String, Object> params) {
        if (params == null)
        	params = new HashMap<String, Object>();
    	params.put( PARAM_WSML_VARIANT, WSML.WSML_CORE );
    	
    	return new DatalogBasedWSMLReasoner( extractReasoner( params, BuiltInReasoner.IRIS ), extractWsmoManager( params ), params );
    }

    public WSMLCoreReasoner createWSMLCoreReasoner() {
        return createWSMLCoreReasoner(null);
    }

    public WSMLDLReasoner createWSMLDLReasoner(Map<String, Object> params) {
        if (params == null)
        	params = new HashMap<String, Object>();
    	params.put( PARAM_WSML_VARIANT, WSML.WSML_DL );
    	
    	return new DLBasedWSMLReasoner( extractReasoner( params, BuiltInReasoner.PELLET ), extractWsmoManager( params ) );
	}

	public WSMLDLReasoner createWSMLDLReasoner() {
		return createWSMLDLReasoner(null);
	}
    
    public WSMLFlightReasoner createWSMLFlightReasoner(Map<String, Object> params) {
        if (params == null)
        	params = new HashMap<String, Object>();
    	params.put( PARAM_WSML_VARIANT, WSML.WSML_FLIGHT );
    	
    	DatalogBasedWSMLReasoner reasoner = new DatalogBasedWSMLReasoner( extractReasoner( params, BuiltInReasoner.IRIS ), extractWsmoManager( params ), params );

    	setAllowImportsFlag( reasoner, params );
    	
    	return reasoner;
    }

    public WSMLFlightReasoner createWSMLFlightReasoner() {
        return createWSMLFlightReasoner(null);
    }


	public WSMLRuleReasoner createWSMLRuleReasoner() throws UnsupportedOperationException {
		return createWSMLRuleReasoner(null);
	}
	
	public WSMLRuleReasoner createWSMLRuleReasoner(Map<String, Object> params) throws UnsupportedOperationException {
        if (params == null)
        	params = new HashMap<String, Object>();
    	params.put( PARAM_WSML_VARIANT, WSML.WSML_RULE );

    	DatalogBasedWSMLReasoner reasoner = new DatalogBasedWSMLReasoner( extractReasoner( params, BuiltInReasoner.IRIS ), extractWsmoManager( params ), params );

    	setAllowImportsFlag( reasoner, params );
    	
    	return reasoner;
    }
	
	public WSMLFOLReasoner createWSMLFOLReasoner() throws UnsupportedOperationException {
        return createWSMLFOLReasoner(null);
    }
    
    public WSMLFOLReasoner createWSMLFOLReasoner(Map<String, Object> params) throws UnsupportedOperationException {
        if (params == null)
        	params = new HashMap<String, Object>();
        
        WSMO4JManager wsmo4jManager = extractWsmoManager( params );

        BuiltInReasoner reasoner = extractReasoner( params, BuiltInReasoner.SPASS );
        
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
        
        return new org.wsml.reasoner.impl.FOLBasedWSMLReasoner( reasoner, wsmo4jManager, uri);
    }
}
