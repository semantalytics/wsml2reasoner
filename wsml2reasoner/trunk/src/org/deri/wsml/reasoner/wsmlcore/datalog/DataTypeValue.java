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

package org.deri.wsml.reasoner.wsmlcore.datalog;

public class DataTypeValue extends Constant {
    
    public enum DataType {UNDEF, INTEGER, STRING, DECIMAL};
    
    private DataType type = DataType.UNDEF;

    /**
     * @return Returns the type.
     */
    public DataType getType() {
        return type;
    }

    public DataTypeValue(String value, DataType datatype){
        super(value);
        type = datatype;
    }
    
    public boolean equals(Object o){
        boolean result = false;
        if (o != null && o instanceof DataTypeValue){
            DataTypeValue d = (DataTypeValue) o;
            result = ( this.getSymbol().equals(d.getSymbol()) 
                       && this.getType()== d.getType());
        }
        return result;
        
    }
    
    public String toString(){
        String result = this.getSymbol();
        
        switch (getType()) {
        case INTEGER:
            result += "_(OF_DATATYPE_INTEGER)";
            break;
        case STRING:
            result += "_(OF_DATATYPE_STRING)";
            break;
        case DECIMAL:
            result += "_(OF_DATATYPE_DECIMAL)";
            break;

        default:
            result += "_(OF_UNDEFINED_DATATYPE)";
            break;
        }
        
        return result;
    }
    
    
}
