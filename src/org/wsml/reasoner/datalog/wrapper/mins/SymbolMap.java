package org.wsml.reasoner.datalog.wrapper.mins;

import java.util.HashMap;
import java.util.Map;

import org.wsml.reasoner.datalog.*;
import org.wsml.reasoner.datalog.wrapper.mins.MinsSymbolFactory;
import org.wsml.reasoner.datalog.wrapper.UnsupportedFeatureException;

import com.ontoprise.inference.prolog.terms.Term;

/**
 * Package: package org.wsml.reasoner.datalog.wrapper.mins;

 * Author: Darko Anicic, DERI Innsbruck
 * Date: 15.09.2005  17:25:43
 */

public class SymbolMap {
    private MinsSymbolFactory sFactory;
   
	private Map<String,Integer> wsml2toolPredicates = new HashMap<String,Integer>();
    private Map<String,Integer> wsml2toolConstants = new HashMap<String,Integer>();
    private Map<String,Integer> wsml2toolVariables = new HashMap<String,Integer>();
    private Map<String,String>  wsml2toolDataValues = new HashMap<String,String>();
    
    private Map<Integer, String> tool2wsmlPredicates = new HashMap<Integer, String>();
    private Map<Integer, String> tool2wsmlConstants = new HashMap<Integer, String>();
    private Map<Integer, String> tool2wsmlVariables = new HashMap<Integer, String>();
    private Map<String, String>  tool2wsmlDataValues = new HashMap<String, String>();
    
    private ConjunctiveQuery currentQuery=null;
    private Map<ConjunctiveQuery, Map<String, Integer>> wsml2toolVariablesPerQuery = new HashMap<ConjunctiveQuery, Map<String, Integer>>();
    private Map<ConjunctiveQuery, Map<Integer, String>> tool2wsmlVariablesPerQuery = new HashMap<ConjunctiveQuery, Map<Integer, String>>();
    
    public SymbolMap(MinsSymbolFactory sf){
        sFactory = sf;
    }
    
    /**
     * reseting the variable count (they are local to each rule, so should be called before each rule) 
     */
    public void resetVariables(){
        sFactory.resetVarCount();
        wsml2toolVariables = new HashMap<String,Integer>();
        tool2wsmlVariables = new HashMap<Integer, String>();
        currentQuery=null;
    }
    
    /**
     * FIXME not threadsafe
     * @param q
     */
    public void setQueryForVariableMapping(ConjunctiveQuery q){
        sFactory.resetVarCount();
        currentQuery=q;
        if (tool2wsmlVariablesPerQuery.get(q)== null){
            wsml2toolVariables = new HashMap<String,Integer>();
            tool2wsmlVariables = new HashMap<Integer, String>();
            wsml2toolVariablesPerQuery.put(q,wsml2toolVariables);
            tool2wsmlVariablesPerQuery.put(q,tool2wsmlVariables);
        }else{
            wsml2toolVariables = wsml2toolVariablesPerQuery.get(q);
            tool2wsmlVariables = tool2wsmlVariablesPerQuery.get(q);
        }
            
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
    
    public String convertToWSML(int variableNo){
        String ret = tool2wsmlVariables.get(variableNo);
        if (tool2wsmlVariables!= null){
            return ret;
        } 
        throw new RuntimeException("Could not map MINS variable to WSML Term :(");
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
