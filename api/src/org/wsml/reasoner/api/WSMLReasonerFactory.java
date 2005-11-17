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

/**
 * An interface for getting WSML Reasoners for the various variants of WSML.
 * 
 * @author Uwe Keller, DERI Innsbruck
 */
public interface WSMLReasonerFactory {

    public String PARAM_WSML_VARIANT = "WSML VARIANT";

    public enum WSMLVariant {
        WSML_CORE, WSML_FLIGHT, WSML_RULE, WSML_DL, WSML_FULL
    };

    public String PARAM_BUILT_IN_REASONER = "BUILT IN REASONER";

    public enum BuiltInReasoner {
        DLV, MANDARAX, KAON2, MINS, FLORA2
    };

    /**
     * Creates a WSML Core reasoner backed up by the chosen Datalog
     * implementation
     * 
     * @param builtInReasoner
     *            the built-in reasoner to be used by the WSML reasoner
     * @return the resoner
     * @throws UnsupportedOperationException
     */
    public WSMLCoreReasoner getWSMLCoreReasoner(BuiltInReasoner builtInReasoner)
            throws UnsupportedOperationException;

    /**
     * Creates a WSML Core reasoner backed up by the default Datalog engine
     * 
     * @param builtInReasoner
     *            the built-in reasoner to be used by the WSML reasoner
     * @return the resoner
     * @throws UnsupportedOperationException
     */
    public WSMLCoreReasoner getWSMLCoreReasoner()
            throws UnsupportedOperationException;

    /**
     * Creates a WSML Flight reasoner backed up by the chosen Datalog
     * implementation
     * 
     * @param builtInReasoner
     *            the built-in reasoner to be used by the WSML reasoner
     * @return the resoner
     * @throws UnsupportedOperationException
     */
    public WSMLFlightReasoner getWSMLFlightReasoner(
            BuiltInReasoner builtInReasoner)
            throws UnsupportedOperationException;

    /**
     * Creates a WSML Flight reasoner backed up by the default Datalog engine
     * 
     * @param builtInReasoner
     *            the built-in reasoner to be used by the WSML reasoner
     * @return the resoner
     * @throws UnsupportedOperationException
     */
    public WSMLFlightReasoner getWSMLFlightReasoner()
            throws UnsupportedOperationException;

}
