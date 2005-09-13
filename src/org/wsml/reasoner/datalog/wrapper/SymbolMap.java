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

import java.util.HashMap;
import java.util.Map;

import org.wsml.reasoner.datalog.Constant;
import org.wsml.reasoner.datalog.DataTypeValue;
import org.wsml.reasoner.datalog.Predicate;
import org.wsml.reasoner.datalog.Variable;

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
    
    private Map<String,Integer> wsml2toolPredicates = new HashMap<String,Integer>();
    private Map<String,Integer> wsml2toolConstants = new HashMap<String,Integer>();
    private Map<String,Integer> wsml2toolVariables = new HashMap<String,Integer>();
    private Map<String,String>  wsml2toolDataValues = new HashMap<String,String>();
    
    private Map<Integer, String> tool2wsmlPredicates = new HashMap<Integer, String>();
    private Map<Integer, String> tool2wsmlConstants = new HashMap<Integer, String>();
    private Map<Integer, String> tool2wsmlVariables = new HashMap<Integer, String>();
    private Map<String, String>  tool2wsmlDataValues = new HashMap<String, String>();
    
    public SymbolMap(SymbolFactory sf){
        sFactory = sf;
    }
    
    public int convertToTool(Predicate p) throws UnsupportedFeatureException {
    	
    	//System.out.println("convertToTool, Predicate");
    	
    	int result;
        String wsmlName = p.getSymbolName();
        String modName = wsmlName + "_" + p.getArity(); // to make the string rep. unique in WSML
        if (wsml2toolPredicates.containsKey(modName)){
            result = wsml2toolPredicates.get(modName);
        } else {
            result = sFactory.getValidPredicate();
            wsml2toolPredicates.put(modName, result);
            tool2wsmlPredicates.put(result,wsmlName);
        }
        return result;
    }
    
    public int convertToTool(Constant c) throws UnsupportedFeatureException {
    	
    	//System.out.println("convertToTool, Constant");
    	
    	int result;
        String wsmlName = c.getSymbol();
        if (wsml2toolConstants.containsKey(wsmlName)){
            result = wsml2toolConstants.get(wsmlName);
        } else {
            result = sFactory.getValidConstant();
            wsml2toolConstants.put(wsmlName, result);
            tool2wsmlConstants.put(result,wsmlName);
        }
        return result;
    }
    
    public int convertToTool(Variable v) throws UnsupportedFeatureException {
        
    	//System.out.println("convertToTool, Variable");
    	
    	int result;
        String wsmlName = v.getSymbol();
        if (wsml2toolVariables.containsKey(wsmlName)){
            result = wsml2toolVariables.get(wsmlName);
        } else {
        	result = sFactory.getValidVariable();
        	wsml2toolVariables.put(wsmlName, result);
        	tool2wsmlVariables.put(result,wsmlName);
        }
        return result;
    }
    
    public String convertToTool(DataTypeValue dtv) throws UnsupportedFeatureException {
        
    	//System.out.println("convertToTool, DataTypeValue");
    	
    	String result;
        String wsmlName = dtv.getSymbol();
        String modName = wsmlName + "_" + dtv.getType(); // to make the string rep. unique in WSML
        if (wsml2toolDataValues.containsKey(modName)){
            result = wsml2toolDataValues.get(modName);
        } else {
            result = sFactory.getValidDataValue(wsmlName,dtv.getType());
            wsml2toolDataValues.put(modName, result);
            tool2wsmlDataValues.put(result,wsmlName);
        }
        return result;
    }
    
    public String convertToWSML(int term, int type) throws UnsupportedFeatureException {
    	/*type = 0 => Convert term to a predicate!
    	  type = 1 => Convert term to a constant!
    	  type = 2 => Convert term to a variable!
    	  type = 3 => Convert term to a dataValue!*/
    	
    	System.out.println("convertToWSML, type: " + type);
    	
    	String result = null;
    	switch(type){
    		case 0:
    			if (tool2wsmlPredicates.containsKey(term)){
    	            // symbol is known to be mapped originally, so just map back.
    	            result = tool2wsmlPredicates.get(term);
    	        } else {
    	            result = Integer.toString(term); // for unknown symbols just leave them unmodified for the moment.
    	        }
    			break;
    		case 1:
    			if (tool2wsmlConstants.containsKey(term)){
    	            result = tool2wsmlConstants.get(term);
    	        } else {
    	            result = Integer.toString(term); 
    	        }
    			break;
    		case 2:
    			if (tool2wsmlVariables.containsKey(term)){
    	            result = tool2wsmlVariables.get(term);
    	        } else {
    	            result = Integer.toString(term); 
    	        }
    			break;
    		case 3:
    			if (tool2wsmlDataValues.containsKey(term)){
    	            result = tool2wsmlDataValues.get(term);
    	        } else {
    	            result = Integer.toString(term); 
    	        }
    			break;
    	}
        return result;
    }
}
