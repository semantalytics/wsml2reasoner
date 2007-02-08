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
package org.wsml.reasoner.builtin.tptp;

import java.util.*;

import org.apache.log4j.Logger;
import org.omwg.logicalexpression.LogicalExpression;
import org.wsml.reasoner.ExternalToolException;
import org.wsml.reasoner.FOLReasonerFacade;
import org.wsml.reasoner.api.WSMLFOLReasoner.EntailmentType;
import org.wsml.reasoner.impl.WSMO4JManager;

/**
 * <p>
 * The wsmo4j interface to and from TPTP
 * </p>
 * <p>
 * $Id: TPTPFacade.java,v 1.1 2007-02-08 15:01:32 hlausen Exp $
 * </p>
 * 
 * @author Holger Lausen
 * @version $Revision: 1.1 $
 */
public class TPTPFacade implements FOLReasonerFacade {

    WSMO4JManager wsmo4jmanager;
    Logger log = Logger.getLogger(TPTPFacade.class);
    
    public TPTPFacade(WSMO4JManager manager){
        this.wsmo4jmanager=manager;
    }
    
    
    /**
     * here sits the MEET!!!!
     */
    public List<EntailmentType> checkEntailment(String ontologyIRI, List<LogicalExpression> conjecture) {
        // TODO Auto-generated method stub
        return null;
    }

    public void deregister(String ontologyURI) throws ExternalToolException {
        convertedOntologies.remove(ontologyURI);
    }


    Map<String,String> convertedOntologies = new HashMap<String, String>();
    
    /**
     * I guess the cleverest would be to make a file based storage
     * for the converted Ontologies, however for now h HasMap should
     * do it..
     */
    public void register(String ontologyURI, Set<LogicalExpression> expressions) throws ExternalToolException {
        TPTPLESerializeVisitor les = new TPTPLESerializeVisitor();
        String kb ="";
        for (LogicalExpression le:expressions ){
            le.accept(les);
            String newExpression = les.getSerializedObject();
            log.debug("CONVERTED EXPRESSION: "+newExpression);
            kb+=newExpression;
        }
        convertedOntologies.put(ontologyURI, kb);
    }
}
