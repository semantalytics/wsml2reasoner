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

package org.wsml.reasoner.builtin.wrapper;

import org.wsml.reasoner.UnsupportedFeatureException;

/**
 * An interface for classes that encasulate the knowledge about tool specific
 * policies of allows names for symbols such as constants, function names,
 * predicates.
 * 
 * For each tool that is to be integrated, one should implement a subclass that
 * delivers valid symbol names.
 * 
 * @author Uwe Keller, DERI Innsbruck
 */
public interface SymbolFactory {

        /**
         * Return a valid name that can be used for a variable in the external tool.
         * @param wsmlName - the original name in WSML
         * @return a valid name for variables in the external tool.
         */
       String getValidVariableName(String wsmlName);
       
       /**
        * Return a valid name that can be used for a constant symbol in the external tool.
        * @param wsmlName - the original name in WSML
        * @return a valid name for a function symbol in the external tool.
        */
       String getValidConstantName(String wsmlName);
       
       /**
        * Return a valid name that can be used for a function symbol in the external tool.
        * @param wsmlName - the original name in WSML
        * @param arity - the arity of the predicate
        * @return a valid name for a function symbol in the external tool.
        * @throws UnsupportedFeatureException - if a specific predicate (in particular built-in
        *         predicate is not supported by the tool.
        */
       String getValidPredicateName(String wsmlName, int arity) throws UnsupportedFeatureException;
       
       
//       /**
//        * Return a valid name that can be used for a datavalue in the external tool.
//        * @param wsmlName - the original string representation in WSML
//        * @param dType - the data type of the given WSML datavalue
//        * @return a valid name for a function symbol in the external tool.
//        * @throws UnsupportedFeatureException - if a specific datavalue (in particular built-in
//        *         types is not supported by the tool.
//        */
//       String getValidDataValue(String wsmlName, org.wsml.reasoner.datalog.DataTypeValue.DataType dType) throws UnsupportedFeatureException;
              
}
