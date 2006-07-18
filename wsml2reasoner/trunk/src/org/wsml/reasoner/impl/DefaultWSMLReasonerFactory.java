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

import java.util.Map;

import org.wsml.reasoner.api.WSMLCoreReasoner;
import org.wsml.reasoner.api.WSMLDLReasoner;
import org.wsml.reasoner.api.WSMLFlightReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsmo.factory.DataFactory;
import org.wsmo.factory.Factory;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;

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

    public WSMLDLReasoner createWSMLDLReasoner(Map<String, Object> params) 
    		throws UnsupportedOperationException {
		// TODO Auto-generated method stub
		return null;
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

}
