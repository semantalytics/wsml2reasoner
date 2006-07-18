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
package org.wsml.reasoner.builtin.pellet;

import java.util.Map;
import java.util.Set;

import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.ConjunctiveQuery;
import org.wsml.reasoner.DLReasonerFacade;
import org.wsml.reasoner.ExternalToolException;
import org.wsml.reasoner.Rule;
import org.wsml.reasoner.impl.WSMO4JManager;

/**
 * Integrates the PELLET reasoner system into the WSML-DL reasoner framework.
 *
 * <pre>
 *  Created on July 3rd, 2006
 *  Committed by $Author: nathalie $
 *  $Source: /home/richi/temp/w2r/wsml2reasoner/src/org/wsml/reasoner/builtin/pellet/PelletFacade.java,v $,
 * </pre>
 *
 * @author Nathalie Steinmetz, DERI Innsbruck
 * @version $Revision: 1.1 $ $Date: 2006-07-18 08:21:01 $
 */
public class PelletFacade implements DLReasonerFacade {

	/**
     * Creates a facade object that allows to invoke the PELLET system for
     * performing reasoning tasks.
     */
    public PelletFacade(WSMO4JManager wsmoManager) {
        super();
    }
	
	public void register(String ontologyURI, Set<Rule> kb) throws ExternalToolException {
		// TODO Auto-generated method stub
		
	}

	public void deregister(String ontologyURI) throws ExternalToolException {
		// TODO Auto-generated method stub
		
	}

	public Set<Map<Variable, Term>> evaluate(ConjunctiveQuery q, String ontologyURI) throws ExternalToolException {
		// TODO Auto-generated method stub
		return null;
	}

}
/*
 * $Log: not supported by cvs2svn $
 *
 */