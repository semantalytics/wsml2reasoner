package org.wsml.reasoner.datalog.wrapper.mins;

import java.util.HashMap;
import java.util.Map;

import org.wsml.reasoner.datalog.Constant;
import org.wsml.reasoner.datalog.Predicate;
import org.wsml.reasoner.datalog.Variable;
import org.wsml.reasoner.datalog.wrapper.mins.DefaultSymbolFactory;
import org.wsml.reasoner.datalog.wrapper.UnsupportedFeatureException;

import com.ontoprise.inference.prolog.terms.Term;

/**
 * Package: package org.wsml.reasoner.datalog.wrapper.mins;

 * Author: Darko Anicic, DERI Innsbruck
 * Date: 15.09.2005  17:25:43
 */

public class SymbolMap {
    private DefaultSymbolFactory sFactory;
   
	private Map<String,Integer> wsml2toolPredicates = new HashMap<String,Integer>();
    private Map<String,Integer> wsml2toolConstants = new HashMap<String,Integer>();
    private Map<String,Integer> wsml2toolVariables = new HashMap<String,Integer>();
    private Map<String,String>  wsml2toolDataValues = new HashMap<String,String>();
    
    private Map<Integer, String> tool2wsmlPredicates = new HashMap<Integer, String>();
    private Map<Integer, String> tool2wsmlConstants = new HashMap<Integer, String>();
    private Map<Integer, String> tool2wsmlVariables = new HashMap<Integer, String>();
    private Map<String, String>  tool2wsmlDataValues = new HashMap<String, String>();
    
    public SymbolMap(DefaultSymbolFactory sf){
        sFactory = sf;
    }
   
    public int convertToTool(Predicate p) throws UnsupportedFeatureException {
    	int result;
        String wsmlName = p.getSymbolName();
        String modName = wsmlName + "_" + p.getArity(); // to make the string rep. unique in WSML
        if (wsml2toolPredicates.containsKey(modName)){
            result = wsml2toolPredicates.get(modName);
        } else {
            result = sFactory.getValidPredicateName();
            wsml2toolPredicates.put(modName, result);
            tool2wsmlPredicates.put(result,wsmlName);
        }
        
        return result;
    }
    
    public int convertToTool(Constant c) throws UnsupportedFeatureException {
    	int result;
        String wsmlName = c.getSymbol();
        if (wsml2toolConstants.containsKey(wsmlName)){
            result = wsml2toolConstants.get(wsmlName);
        } else {
            result = sFactory.getValidConstantName();
            wsml2toolConstants.put(wsmlName, result);
            tool2wsmlConstants.put(result,wsmlName);
        }
        
        return result;
    }
    
    public int convertToTool(Variable v) throws UnsupportedFeatureException {
    	int result;
        String wsmlName = v.getSymbol();
        if (wsml2toolVariables.containsKey(wsmlName)){
            result = wsml2toolVariables.get(wsmlName);
        } else {
        	result = sFactory.getValidVariableName();
        	wsml2toolVariables.put(wsmlName, result);
        	tool2wsmlVariables.put(result,wsmlName);
        }
        
        return result;
    }
    
    public String convertToWSML(Term term) throws UnsupportedFeatureException {
    	String result = null;
    	String pos = term.toString().substring(1, term.toString().length());
    	int position = Integer.parseInt(pos);
    	
    	if(term.isConstTerm()){
	    	if (tool2wsmlConstants.containsKey(position)){
	            // symbol is known to be mapped originally, so just map back.
	            result = tool2wsmlConstants.get(position);
	        } else {
	            result = term.toString(); // for unknown symbols just leave them unmodified for the moment.
	        }
	    	
		    return result;
    	}
    	if(term.isVariable()){
    		if (tool2wsmlVariables.containsKey(position+1)){
	            // symbol is known to be mapped originally, so just map back.
	            result = tool2wsmlVariables.get(position+1);
	        } else {
	            result = term.toString(); // for unknown symbols just leave them unmodified for the moment.
	        }
    	}else{
    		if (tool2wsmlPredicates.containsKey(position)){
	            // symbol is known to be mapped originally, so just map back.
	            result = tool2wsmlPredicates.get(position);
	        } else {
	            result = term.toString(); // for unknown symbols just leave them unmodified for the moment.
	        }
    	}
    	
        return result;
    }
}
