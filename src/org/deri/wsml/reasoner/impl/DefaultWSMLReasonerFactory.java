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

package org.deri.wsml.reasoner.impl;

import java.util.Map;

import org.deri.wsml.reasoner.api.WSMLReasoner;
import org.deri.wsml.reasoner.api.WSMLReasonerFactory;

/**
 * A default implementation of a factory that constructs WSML Reasoners.
 * 
 * The factory will be based on prototypical implementations for WSML Reasoners
 * that are developed at DERI. 
 * 
 * @author Uwe Keller, DERI Innsbruck
 */
public class DefaultWSMLReasonerFactory implements WSMLReasonerFactory {

    private final static DefaultWSMLReasonerFactory aFactory = new DefaultWSMLReasonerFactory();
    
    /**
     * @return an instance of the default implementation of the WSMLReasonerFactory interface.
     */
    public static WSMLReasonerFactory getFactory(){
        return aFactory;
    }
    
    /**
     * Gives an object for reasoning with WSML descriptions.
     * 
     * At present only WSML Core is supported.
     * Furthermore, at present we only provide support for QueryAnswering over WSML Core Ontologies
     * 
     * 
     * @return a WSMLReasoner object for the chosen variant of WSML.
     */
    public WSMLReasoner getWSMLReasoner(Map<String, Object> parameters) throws UnsupportedOperationException {
        WSMLReasoner reasoner;
        WSMLVariant variant = (WSMLVariant) parameters.get(PARAM_WSML_VARIANT);
        BuiltInReasoner reasonerType = (BuiltInReasoner) parameters.get(PARAM_BUILT_IN_REASONER);
        if (WSMLReasonerFactory.WSMLVariant.WSML_CORE.equals(variant)){
            reasoner = new org.deri.wsml.reasoner.impl.WSMLCoreReasonerImpl(reasonerType);
            return reasoner;
        } else {
            throw new UnsupportedOperationException("The requested variant of WSML ("+variant+") currently not supported by this factory!");
        }
    }

}
