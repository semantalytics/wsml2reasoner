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

import org.wsml.reasoner.datalog.DataTypeValue.DataType;

/**
 * Package: package org.wsml.reasoner.datalog.wrapper.mins;

 * Author: Darko Anicic, DERI Innsbruck
 *         Holger Lausen, DERI Innsbruck
 * Date: 15.09.2005  17:24:24
 */
public class MinsSymbolFactory{
	private int varCnt = 0;
    private int constCnt = 0;
    private int predCnt = 0;
    
    public void resetVarCount(){
        varCnt = 0;
    }
    
	public int getValidVariableName() {
		return varCnt++;
	}

	public int getValidConstantName() {
		return ++constCnt;
	}

	public int getValidPredicateName() {
		return ++predCnt;
	}

	public String getValidDataValue(String wsmlName, DataType dType) {
		String result = null;
        result = wsmlName;
        return result;
	}
}
