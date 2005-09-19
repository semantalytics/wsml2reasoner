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

import org.wsml.reasoner.datalog.DataTypeValue.DataType;

/**
 * A default implementation of the SymbolFactory interface. 
 * Applications can change the default behaviour by subclassing and
 * overriding the methods where some specific names are needed.
 * 
 * Prototypical implementation only.
 * 
 * @author Uwe Keller, DERI Innsbruck
 */
public class DefaultSymbolFactory implements SymbolFactory {

    private long varCnt = 0;
    private long constCnt = 0;
    private long predCnt = 0;
    
    private final String VAR_PREFIX = "Var";
    private final String CONST_PREFIX = "c";
    private final String PRED_PREFIX = "p";
    
    public String getValidVariableName(String wsmlName) {
        return (VAR_PREFIX + (++varCnt));
    }

    public String getValidConstantName(String wsmlName) {
        return (CONST_PREFIX + (++constCnt));
    }

    public String getValidPredicateName(String wsmlName, int arity) throws UnsupportedFeatureException {
            return (PRED_PREFIX + (++predCnt));
    }

    public String getValidDataValue(String wsmlName, DataType dType) throws UnsupportedFeatureException {
        String result = null;
        result = wsmlName;
        return result;
    }

}
