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

import org.wsml.reasoner.datalog.wrapper.UnsupportedFeatureException;

/**
 * Package: package org.wsml.reasoner.datalog.wrapper.mins;

 * Author: Darko Anicic, DERI Innsbruck
 * Date: 15.09.2005  17:25:43
 */
public interface SymbolFactory {

        /**
         * Return a valid name that can be used for a variable in the external tool.
         * @return a valid name for variables in the external tool.
         */
       int getValidVariableName();
       
       /**
        * Return a valid name that can be used for a constant symbol in the external tool.
        * @return a valid name for a function symbol in the external tool.
        */
       int getValidConstantName();
       
       /**
        * Return a valid name that can be used for a function symbol in the external tool.
        * @return a valid name for a function symbol in the external tool.
        * @throws UnsupportedFeatureException - if a specific predicate (in particular built-in
        *         predicate is not supported by the tool.
        */
       int getValidPredicateName() throws UnsupportedFeatureException;
       
       
       /**
        * Return a valid name that can be used for a datavalue in the external tool.
        * @return a valid name for a function symbol in the external tool.
        * @throws UnsupportedFeatureException - if a specific datavalue (in particular built-in
        *         types is not supported by the tool.
        */
       String getValidDataValue(String wsmlName, org.wsml.reasoner.datalog.DataTypeValue.DataType dType) throws UnsupportedFeatureException;
              
}
