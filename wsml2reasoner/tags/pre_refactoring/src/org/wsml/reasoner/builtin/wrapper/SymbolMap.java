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

import java.util.HashMap;
import java.util.Map;

import org.omwg.logicalexpression.terms.Term;
import org.wsml.reasoner.UnsupportedFeatureException;

/**
 * Allows to convert between symbol names in WSML and the names that are accepted by 
 * specific target systems.
 * 
 * For instance, not all systems will allow to use all symbols that are allowed in
 * IRIs.
 * 
 * This class presents a default implementation of a converter that can be (re)used by
 * wrappers to specific systems.
 * The knowledge about the specifically allows symbols is hidden in a (tool-specific)
 * symbol factory, that need to be implemented and plugged into the converter in 
 * the cosntructor.
 * 
 * @author Uwe Keller, DERI Innsbruck
 */
public class SymbolMap {

    private SymbolFactory sFactory;
    
    private Map<String,String> wsml2tool = new HashMap<String,String>();
    private Map<String,String> tool2wsml = new HashMap<String,String>();
    
    public SymbolMap(SymbolFactory sf){
        sFactory = sf;
    }
    
//    public String convertToTool(Predicate p) throws UnsupportedFeatureException {
//        String result;
//        String wsmlName = p.getSymbolName();
//        String modName = wsmlName + "_" + p.getArity(); // to make the string rep. unique in WSML
//        if (wsml2tool.containsKey(modName)){
//            result = wsml2tool.get(modName);
//        } else {
//            result = sFactory.getValidPredicateName(wsmlName, p.getArity());
//            wsml2tool.put(modName, result);
//            tool2wsml.put(result,wsmlName);
//        }
//        return result;
//    }
    
    public String convertToTool(Term c) throws UnsupportedFeatureException {
        throw new UnsupportedFeatureException("not implemented");
//        String result;
//        String wsmlName = c.getSymbol();
//        if (wsml2tool.containsKey(wsmlName)){
//            result = wsml2tool.get(wsmlName);
//        } else {
//            result = sFactory.getValidConstantName(wsmlName);
//            wsml2tool.put(wsmlName, result);
//            tool2wsml.put(result,wsmlName);
//        }
//        return result;
    }    
}