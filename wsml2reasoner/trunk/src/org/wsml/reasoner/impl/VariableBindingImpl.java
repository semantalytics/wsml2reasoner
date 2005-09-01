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

import java.util.HashMap;
import java.util.Map;

import org.wsml.reasoner.api.queryanswering.VariableBinding;

public class VariableBindingImpl extends HashMap<String,String> implements VariableBinding {

    /**
     * Default serial version id for serialization.
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * Reimplementation of equals() that returns true
     * if two maps contain the same (key,value) pairs
     * 
     * Avoids duplicate variable bindings in objects that
     * implement the java.util.Set interface.
     */
    public boolean equals(Object o){
        boolean result = false;
        
        if (this == o)  {return true;}
        if (o != null && (o instanceof Map)){
            Map m = (Map) o;
            result = true;
            
            for (String key : this.keySet()){
                if(!this.get(key).equals(m.get(key))){
                    result = false;
                    break;
                } 
            }
        } 
        return result;
    }
    
    public String toString(){
        String s = "Varbinding[ ";
        int i = 1;
        for (String varName : this.keySet()){
            s += varName + " = " + this.get(varName);
            if (i < this.size()){
                s += ", ";
            }
            i++;
        }
        s += " ]";
        return s;
    }

}
