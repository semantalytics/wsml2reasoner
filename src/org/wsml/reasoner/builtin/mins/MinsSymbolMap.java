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
package org.wsml.reasoner.builtin.mins;

import java.util.*;

import org.deri.mins.builtins.*;
import org.deri.mins.terms.*;
import org.omwg.logicalexpression.*;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.*;
import org.wsml.reasoner.builtin.*;
import org.wsml.reasoner.impl.*;

/**
 * Package: package org.wsml.reasoner.datalog.wrapper.mins;

 * Author: Darko Anicic, DERI Innsbruck
 *         Holger Lausen, DERI Innsbruck
 * Date: $Date$
 */

public class MinsSymbolMap {
    
    int predNo=100;
    int constNo=0;
    
    String IS_INTEGER = "isInteger";
    String IS_DECIMAL = "isDecimal";
    String IS_STRING = "isString";
    
	private Map<String,Integer> wsml2toolPredicates = new HashMap<String,Integer>();
    private Map<org.omwg.logicalexpression.terms.Term,Integer> wsml2toolConstants = new HashMap<org.omwg.logicalexpression.terms.Term,Integer>();
    private Map<String,String>  wsml2toolDataValues = new HashMap<String,String>();
    
    private Map<Integer, String> tool2wsmlPredicates = new HashMap<Integer, String>();
    private Map<Integer, org.omwg.logicalexpression.terms.Term> tool2wsmlConstants = new HashMap<Integer, org.omwg.logicalexpression.terms.Term>();
    private Map<String, String>  tool2wsmlDataValues = new HashMap<String, String>();
    
    private Map<Object, Map<Variable, Integer>> wsml2minsVariablesPerRule = new HashMap<Object, Map<Variable, Integer>>();
    private Map<Object, List<Variable>> mins2wsmlVariablesPerRule = new HashMap<Object, List<Variable>>();
    
    protected Map<String, BuiltinFunc> minsBuiltinFunc = new HashMap<String, BuiltinFunc>();
    protected Map<String, Integer> minsBuiltIn2No = new HashMap<String, Integer>();
    
    public MinsSymbolMap(){
        minsBuiltinFunc.put(Constants.LESS_THAN, new Less());
        minsBuiltIn2No.put(Constants.LESS_THAN, 0);
        
        minsBuiltinFunc.put(Constants.LESS_EQUAL, new Lessorequal());
        minsBuiltIn2No.put(Constants.LESS_EQUAL, 1);
        
        minsBuiltinFunc.put(Constants.GREATER_THAN, new Greater());
        minsBuiltIn2No.put(Constants.GREATER_THAN, 2);
        
        minsBuiltinFunc.put(Constants.GREATER_EQUAL, new Greaterorequal());
        minsBuiltIn2No.put(Constants.GREATER_EQUAL, 3);

        minsBuiltinFunc.put(Constants.NUMERIC_ADD, new NumericAdd());
        minsBuiltIn2No.put(Constants.NUMERIC_ADD, 4);

        minsBuiltinFunc.put(Constants.NUMERIC_SUB, new NumericAdd());
        minsBuiltIn2No.put(Constants.NUMERIC_SUB, 4);

        minsBuiltinFunc.put(Constants.NUMERIC_MUL, new NumericMult());
        minsBuiltIn2No.put(Constants.NUMERIC_MUL, 5);

        minsBuiltinFunc.put(Constants.NUMERIC_DIV, new NumericMult());
        minsBuiltIn2No.put(Constants.NUMERIC_DIV, 5);

        minsBuiltinFunc.put(Constants.EQUAL, new Equal());
        minsBuiltIn2No.put(Constants.EQUAL, 6);
        
        minsBuiltinFunc.put(Constants.INEQUAL, new Equal());
        minsBuiltIn2No.put(Constants.INEQUAL, 6);

        minsBuiltinFunc.put(Constants.NUMERIC_EQUAL, new Equal());
        minsBuiltIn2No.put(Constants.NUMERIC_EQUAL, 6);
        
        minsBuiltinFunc.put(Constants.NUMERIC_INEQUAL, new Equal());
        minsBuiltIn2No.put(Constants.NUMERIC_INEQUAL, 6);
        
        minsBuiltinFunc.put(Constants.STRING_EQUAL, new Equal());
        minsBuiltIn2No.put(Constants.STRING_EQUAL, 6);

        minsBuiltinFunc.put(Constants.STRING_INEQUAL, new Equal());
        minsBuiltIn2No.put(Constants.STRING_INEQUAL, 6);
    }
    
    public int convertToTool(org.wsml.reasoner.Literal literal){
    	int result;
        int arity = 0;
        if(minsBuiltinFunc.containsKey(literal.getPredicateUri()))
            return -1;
        
//        System.out.println(literal);
        String modName = literal.getPredicateUri()+"_"+arity;
        if (literal.getTerms()!=null) arity=literal.getTerms().length;
        if (wsml2toolPredicates.containsKey(modName)){
            result = wsml2toolPredicates.get(modName);
        } else {
            result = predNo++;
            wsml2toolPredicates.put(modName, result);
            tool2wsmlPredicates.put(result,modName);
        }
        
        return result;
    }
    
    public int convertToTool(org.omwg.logicalexpression.terms.Term t){
    	int result;
        if (wsml2toolConstants.containsKey(t)){
            result = wsml2toolConstants.get(t);
        } else {
            result = constNo++;
            wsml2toolConstants.put(t, result);
            tool2wsmlConstants.put(result,t);
        }
        
        return result;
    }
    
    public int convertToTool(Variable v, Object datalogRuleOrQuery){
        Map<Variable,Integer> wsml2mins = wsml2minsVariablesPerRule.get(datalogRuleOrQuery);
        List<Variable> mins2wsml = mins2wsmlVariablesPerRule.get(datalogRuleOrQuery);
        if (wsml2mins == null){
            wsml2mins = new HashMap<Variable,Integer>();
            mins2wsml = new LinkedList<Variable>();
            wsml2minsVariablesPerRule.put(datalogRuleOrQuery,wsml2mins);
            mins2wsmlVariablesPerRule.put(datalogRuleOrQuery,mins2wsml);
        }
        int result;
        if (wsml2mins.containsKey(v)){
            result = wsml2mins.get(v);
        } else {
            result = wsml2mins.size();
            wsml2mins.put(v, result);
            mins2wsml.add(v);
        }
        return result;
    }

    
    public Variable convertToWSML(int variableNo, ConjunctiveQuery query){
        return convertToWSMLHelper(variableNo,query);
    }
    
    public Variable convertToWSML(int variableNo, Rule rule){
        return convertToWSMLHelper(variableNo,rule);
    }

    private Variable convertToWSMLHelper(int variableNo, Object datalogRuleOrQuery){
        List<Variable> mins2wsml = mins2wsmlVariablesPerRule.get(datalogRuleOrQuery);
        if (mins2wsml == null || mins2wsml.get(variableNo) == null)
                throw new RuntimeException("Could not map MINS variable to WSML Term :(");
        return mins2wsml.get(variableNo);
    }
    
    public org.omwg.logicalexpression.terms.Term convertToWSML(Term term) throws UnsupportedFeatureException {
        org.omwg.logicalexpression.terms.Term result = null;
    	if(term.isConstTerm()){
            return tool2wsmlConstants.get(((org.deri.mins.terms.ConstTerm)term).symbol);
    	}else if (term.isStringTerm()){
    	    return WSMO4JManager.getDataFactory().createWsmlString(((StringTerm)term).s);
        }else if (term.isNumTerm()){
            org.deri.mins.terms.NumTerm numTerm = (org.deri.mins.terms.NumTerm)term;
            if (numTerm.zahl - Math.floor(numTerm.zahl) == 0)
                return WSMO4JManager.getDataFactory().createWsmlInteger(new java.lang.Double(numTerm.zahl).intValue()+"");
            else 
                return WSMO4JManager.getDataFactory().createWsmlDecimal(term.toString());
        }
        throw new RuntimeException("Unknown Term Symbol:"+term);
    }
}
