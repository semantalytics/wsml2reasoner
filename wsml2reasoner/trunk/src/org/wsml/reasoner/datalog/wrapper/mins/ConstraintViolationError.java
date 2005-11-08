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
package org.wsml.reasoner.datalog.wrapper.mins;

/**
 * Constraint Violation Error
 *
 * <pre>
 * Created on 08.11.2005
 * Committed by $Author$
 * $Source$,
 * </pre>
 *
 * @author Holger Lausen
 *
 * @version $Revision$ $Date$
 */
public class ConstraintViolationError extends RuntimeException {
    public ConstraintViolationError(String str){
        super(str);
    }

}
