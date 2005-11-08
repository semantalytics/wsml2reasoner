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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

//import org.deri.mins.terms.Term;
import org.wsml.reasoner.datalog.Constant;
import org.wsml.reasoner.datalog.Predicate;
import org.wsml.reasoner.datalog.Variable;
import org.wsml.reasoner.datalog.wrapper.UnsupportedFeatureException;

/**
 * Package: package org.wsml.reasoner.datalog.wrapper.mins;

 * Author: Darko Anicic, DERI Innsbruck
 *         Holger Lausen, DERI Innsbruck
 * Date: 15.09.2005  17:25:43
 */

public class MinsSymbolMap {
//    private MinsSymbolFactory sFactory;
//   
//	private Map<String,Integer> wsml2toolPredicates = new HashMap<String,Integer>();
//    private Map<String,Integer> wsml2toolConstants = new HashMap<String,Integer>();
//    private Map<String,String>  wsml2toolDataValues = new HashMap<String,String>();
//    
//    private Map<Integer, String> tool2wsmlPredicates = new HashMap<Integer, String>();
//    private Map<Integer, String> tool2wsmlConstants = new HashMap<Integer, String>();
//    private Map<String, String>  tool2wsmlDataValues = new HashMap<String, String>();
//    
//    private Map<Object, Map<String, Integer>> wsml2minsVariablesPerRule = new HashMap<Object, Map<String, Integer>>();
//    private Map<Object, List<String>> mins2wsmlVariablesPerRule = new HashMap<Object, List<String>>();
//    
//    public MinsSymbolMap(MinsSymbolFactory sf){
//        sFactory = sf;
//    }
//    
//    public int convertToTool(Predicate p){
//    	int result;
//        String wsmlName = p.getSymbolName();
//        String modName = wsmlName + "_" + p.getArity(); // to make the string rep. unique in WSML
//        if (wsml2toolPredicates.containsKey(modName)){
//            result = wsml2toolPredicates.get(modName);
//        } else {
//            result = sFactory.getValidPredicateName();
//            wsml2toolPredicates.put(modName, result);
//            tool2wsmlPredicates.put(result,wsmlName);
//        }
//        
//        return result;
//    }
//    
//    public int convertToTool(Constant c){
//    	int result;
//        String wsmlName = c.getSymbol();
//        if (wsml2toolConstants.containsKey(wsmlName)){
//            result = wsml2toolConstants.get(wsmlName);
//        } else {
//            result = sFactory.getValidConstantName();
//            wsml2toolConstants.put(wsmlName, result);
//            tool2wsmlConstants.put(result,wsmlName);
//        }
//        
//        return result;
//    }
//    
//    public int convertToTool(Variable v, Object datalogRuleOrQuery){
//        Map<String,Integer> wsml2mins = wsml2minsVariablesPerRule.get(datalogRuleOrQuery);
//        List<String> mins2wsml = mins2wsmlVariablesPerRule.get(datalogRuleOrQuery);
//        if (wsml2mins == null){
//            wsml2mins = new HashMap<String,Integer>();
//            mins2wsml = new LinkedList<String>();
//            wsml2minsVariablesPerRule.put(datalogRuleOrQuery,wsml2mins);
//            mins2wsmlVariablesPerRule.put(datalogRuleOrQuery,mins2wsml);
//        }
//        String wsmlName = v.getSymbol();
//        int result;
//        if (wsml2mins.containsKey(wsmlName)){
//            result = wsml2mins.get(wsmlName);
//        } else {
//        	result = wsml2mins.size();
//        	wsml2mins.put(wsmlName, result);
//        	mins2wsml.add(wsmlName);
//        }
//        return result;
//    }
//    
//    public String convertToWSML(int variableNo, Object datalogRuleOrQuery){
//        List<String> mins2wsml = mins2wsmlVariablesPerRule.get(datalogRuleOrQuery);
//        if (mins2wsml == null || mins2wsml.get(variableNo) == null)
//                throw new RuntimeException("Could not map MINS variable to WSML Term :(");
//        return mins2wsml.get(variableNo);
//    }
//    
//    public String convertToWSML(Term term) throws UnsupportedFeatureException {
//    	String result = null;
//    	String pos = term.toString().substring(1, term.toString().length());
//    	int position = Integer.parseInt(pos);
//    	
//    	if(term.isConstTerm()){
//	    	if (tool2wsmlConstants.containsKey(position)){
//	            // symbol is known to be mapped originally, so just map back.
//	            result = tool2wsmlConstants.get(position);
//	        } else {
//	            result = term.toString(); // for unknown symbols just leave them unmodified for the moment.
//	        }
//	    	
//		    return result;
//    	}else{
//    		if (tool2wsmlPredicates.containsKey(position)){
//	            // symbol is known to be mapped originally, so just map back.
//	            result = tool2wsmlPredicates.get(position);
//	        } else {
//	            result = term.toString(); // for unknown symbols just leave them unmodified for the moment.
//	        }
//    	}
//        return result;
//    }
}
