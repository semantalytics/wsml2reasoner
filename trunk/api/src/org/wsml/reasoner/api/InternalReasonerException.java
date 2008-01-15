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

/**
 * <p>
 * Exception to indicate, that something went wrong in the reasoner while 
 * evaluation.
 * </p>
 */
public class InternalReasonerException extends RuntimeException {

    /**
     * Id used by the java serialization to identify the version of the class.
	 */
	private static final long serialVersionUID = -2424081972392190338L;

	/**
	 * Creates a new exception with a message.
	 * @param msg the exception message
	 */
    public InternalReasonerException(String msg) {
        super(msg);
    }
    
    /**
     * Creates a new exception empty exception.
     */
    public InternalReasonerException() {
        super();
    }
    
    /**
     * Creates a new exception with a cause.
     * @param e the cause for this exception
     */
    public InternalReasonerException(Throwable e) {
        super(e);
    }
    
    /**
     * Creates a new exception with a message and a cause.
     * @param msg the exception message
     * @param cause the cause for this exception
     */
    public InternalReasonerException(final String msg, final Throwable cause) {
    	super(msg, cause);
    }
}
