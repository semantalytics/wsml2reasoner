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

package org.wsml.reasoner.datalog.wrapper;

/**
 * An exception that should be thrown by a facade implementation for a specific tool
 * in case that some feature present in the query or in the knowledgebase is not supported
 * by the tool.
 * 
 * @author Uwe Keller, DERI Innsbruck
 */
public class UnsupportedFeatureException extends Exception {

    /**
     * Default version number for serialization purposes.
     */
    private static final long serialVersionUID = 1L;

    public UnsupportedFeatureException(String s){
        super(s);
    }
}
