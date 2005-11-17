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
import org.wsml.reasoner.api.WSMLFlightReasoner;
import org.wsml.reasoner.api.WSMLReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory;

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
     * @return an instance of the default implementation of the
     *         WSMLReasonerFactory interface.
     */
    public static WSMLReasonerFactory getFactory() {
        return aFactory;
    }

    public WSMLCoreReasoner getWSMLCoreReasoner(BuiltInReasoner builtInReasoner)
            throws UnsupportedOperationException {

        return new org.wsml.reasoner.impl.NewDatalogBasedWSMLReasoner(
                builtInReasoner);
    }

    public WSMLCoreReasoner getWSMLCoreReasoner()
            throws UnsupportedOperationException {
        // Default reasoner is KAON2
        return new org.wsml.reasoner.impl.NewDatalogBasedWSMLReasoner(
                WSMLReasonerFactory.BuiltInReasoner.KAON2);
    }

    public WSMLFlightReasoner getWSMLFlightReasoner(
            BuiltInReasoner builtInReasoner)
            throws UnsupportedOperationException {
        return new org.wsml.reasoner.impl.NewDatalogBasedWSMLReasoner(
                builtInReasoner);
    }

    public WSMLFlightReasoner getWSMLFlightReasoner()
            throws UnsupportedOperationException {
        // Default reasoner is KAON2
        return new org.wsml.reasoner.impl.NewDatalogBasedWSMLReasoner(
                WSMLReasonerFactory.BuiltInReasoner.KAON2);
    }

}
