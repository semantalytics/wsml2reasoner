/*
 * Copyright (c) 2006, University of Innsbruck, Austria.
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
 */

package org.wsml.reasoner.builtin.tptp.fileconnector;


/**
 * An interface for invoking a prover for checking proof obligations.
 *
 * The interface hides all the details how the prover is invoked and where the prover
 * is actually running.
 *  
 * @author Uwe Keller, DERI Innsbruck
 */

public interface IProverConnector {
    
    /**
     * Connects to a prover and checks whether the given proof obligation
     * holds.
     * 
     * @param po - the proof obligation to be checked.
     * @return true iff the proof obligation has successfully been proven.
     * @throws ProverConnectorException
     */
    public boolean check(ProofObligation po) throws ProverConnectorException;
    
}
