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

package org.wsml.reasoner;

/**
 * Represents an exception that is caused by an external tool during the
 * execution of a query.
 * 
 * @author Uwe Keller, DERI Innsbruck
 * @author Gabor Nagypal, FZI
 */
public class ExternalToolException extends Exception {

    /**
     * Needed for Java 5
     */
    private static final long serialVersionUID = 5436234289071988044L;

    public ExternalToolException(String message) {
        super(message);
    }

    public ExternalToolException(String message, Throwable t) {
        super(message, t);
    }

    /**
     * Creates an
     */
    public ExternalToolException(ConjunctiveQuery q) {
        super("Failed to translate query: " + q.toString());
    }

    /**
     * @param arg0
     * @param arg1
     */
    public ExternalToolException(ConjunctiveQuery q, Throwable t) {
        super("Failed to translate query: " + q.toString(), t);
    }

}
