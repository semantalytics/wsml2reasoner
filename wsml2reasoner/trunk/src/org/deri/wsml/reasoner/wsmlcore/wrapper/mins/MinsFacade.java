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

package org.deri.wsml.reasoner.wsmlcore.wrapper.mins;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.deri.wsml.reasoner.api.queryanswering.QueryAnsweringResult;
import org.deri.wsml.reasoner.api.queryanswering.VariableBinding;
import org.deri.wsml.reasoner.impl.QueryAnsweringResultImpl;
import org.deri.wsml.reasoner.impl.VariableBindingImpl;
import org.deri.wsml.reasoner.wsmlcore.datalog.*;
import org.deri.wsml.reasoner.wsmlcore.wrapper.*;

import com.ontoprise.inference.*;


public class MinsFacade implements DatalogReasonerFacade {

    private Logger logger = Logger.getLogger("org.deri.wsml.reasoner.wsmlcore.wrapper.mins");
       
    /** Here we store a MINS Engine which contains the compiled KB for each registered ontology */
    private Map<String,Evaluator> registeredKbs = new HashMap<String,Evaluator>();
    
    private SymbolMap symbTransfomer = new SymbolMap(new DefaultSymbolFactory());
    private org.deri.wsml.reasoner.wsmlcore.datalog.Query query;
      
    /**
     * Evaluates a Query on a Datalog knowledgebase.
     * The actual evaluation is done by the MINS system.
     * 
     * @throws ExternalToolException 
     */
    public QueryResult evaluate(ConjunctiveQuery q, String ontologyIRI) throws ExternalToolException {
        
        try {
        
            QueryResult result = new QueryResult(q);
            
            // retrieve KB ment for IRI:
            Evaluator minsEngine = registeredKbs.get(ontologyIRI);
            
            // remove already contained queries in the KB
            minsEngine.deleteQueries();
                   
            query = q;
     
            translateQuery(q, minsEngine); 
            
            logger.info("Starting MINS evaluation");
            minsEngine.evaluate();
            
            logger.info("Computing substitutions");
            Vector v = minsEngine.computeSubstitutions();
            //for all queries in KB
            Iterator<Vector> queries = v.iterator();
            
            if (v.size() > 1) {
                // Should not happen, since we only have a KB which contains a single query.
                throw new RuntimeException("We unexpectetly have a MINS engine which contains multiple queries at once!");
            }
            
            //we only have one query
            Iterator<Vector> subst = queries.next().iterator();
            while(subst.hasNext()){
                //for all vars in one substitution
                Iterator<FLSubst> vars = subst.next().iterator();
                VariableBinding varBinding = new VariableBindingImpl();
                while(vars.hasNext()){
                    FLSubst sub = vars.next();
                    String varName = symbTransfomer.convertToWSML(sub.Var);
                    String varValue = symbTransfomer.convertToWSML(sub.getSubstitutionString());
                    varBinding.put(varName, varValue);
                }
                result.getVariableBindings().add(varBinding);
                logger.info("Added new variable binding to result set: " + varBinding);
            }
            
            
            return result;
            
        } catch(Exception e) { 
            System.out.println("\n\n\n\n\n\n");
            e.printStackTrace();
            throw new ExternalToolException("MINS can not handle given query", e, q); 
        } 
        
    }
    
    /**
     * Translate a knowledgebase
     * @param p - the datalog program that constitutes the knowledgebase
     * @throws UnsupportedFeatureException 
     */
    private void translateKnowledgebase(Program p, Evaluator minsEval) throws ExternalToolException, UnsupportedFeatureException {
        logger.info("Translate knowledgebase :" + p);

        if (p == null) {logger.info("KB is not referenced. Assume empty KB."); return;} 
        
        for (org.deri.wsml.reasoner.wsmlcore.datalog.Rule r : p) {
            translateRule(r, minsEval);
        }
    }

    /**
     * Translate the query
     * @param p - the datalog program that constitutes the knowledgebase
     * @return an object that represents the same query in the Mandrax system
     * @throws UnsupportedFeatureException 
     */
    private void translateQuery(ConjunctiveQuery q, Evaluator minsEval) throws ExternalToolException, UnsupportedFeatureException {
        // At present we convert the rule into a string representation that can be
        // used as input for the MINS engine.
        // This is not optimal but works for a quick prototype.
        
        List body = new ArrayList();
        
        String sRepresentation = " <- ";
        
        // Care about body
        List<Literal> qBody = q.getLiterals();

        int i = 1;
        for( Literal bl : qBody){
            sRepresentation += translateLiteral(bl);
            if (i < qBody.size()){
                sRepresentation += " and ";
            }
            i++;
        }
        
        sRepresentation += ".";
        
        // Prepend all free variables with FORALL quantor
        
        List<org.deri.wsml.reasoner.wsmlcore.datalog.Variable> varPrefix = q.getVariables();
        
        String sVarPrefix = "FORALL ";
        
        i = 1;
        for( org.deri.wsml.reasoner.wsmlcore.datalog.Variable v : varPrefix){
            sVarPrefix += symbTransfomer.convertToTool(v);
            if (i < varPrefix.size()){
                sRepresentation += ",";
            } else {
                sRepresentation += " ";
            }
            i++;
        }
        
        sRepresentation = sVarPrefix + sRepresentation;
        
        logger.info("Transformed rule:\n" + q + "\n to \n" + sRepresentation);
        
        // Transfer the rule to the given MINS engine
        
        try {
            minsEval.compileString(sRepresentation);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new ExternalToolException(e, query);
        }
    }
    
    /**
     * Translate a datalog rule
     * @throws UnsupportedFeatureException 
     */
    private void translateRule(org.deri.wsml.reasoner.wsmlcore.datalog.Rule r, Evaluator minsEngine) throws ExternalToolException, UnsupportedFeatureException {
        
        // At present we convert the rule into a string representation that can be
        // used as input for the MINS engine.
        // This is not optimal but works for a quick prototype.
        
        String sRepresentation = null;
        
        if (r.isConstraint()) {
            throw new ExternalToolException("Constraints are not supported explicitly by MINS!", this.query);
        } 
        
        // Translate head
        String head = translateLiteral(r.getHead());
        
        // Care about body
        List<Literal> rBody = r.getBody();

        
        if (rBody.size() == 0){
            // rule is actually a fact
            sRepresentation = head;
        } else {
            sRepresentation = head + " <- ";
        }
              
        int i = 1;
        for( Literal bl : rBody){
            String blString = translateLiteral(bl); 
            sRepresentation += blString;
            if (i < rBody.size()){
                sRepresentation += " and ";
            }
            i++;
        }
        
        sRepresentation += ".";
        
        // Prepend all free variables with FORALL quantor
        
        List<org.deri.wsml.reasoner.wsmlcore.datalog.Variable> varPrefix = new LinkedList<org.deri.wsml.reasoner.wsmlcore.datalog.Variable>();
        varPrefix.addAll(r.getHeadVariables());
        for (org.deri.wsml.reasoner.wsmlcore.datalog.Variable bVar : r.getBodyVariables()){
            if( !varPrefix.contains(bVar)){
                varPrefix.add(bVar);
            }
        }
        
        String sVarPrefix = "FORALL ";
        
        i = 1;
        for( org.deri.wsml.reasoner.wsmlcore.datalog.Variable v : varPrefix){
            sVarPrefix += symbTransfomer.convertToTool(v);
            if (i < varPrefix.size()){
                sRepresentation += ",";
            } else {
                sRepresentation += " ";
            }
            i++;
        }
        
        sRepresentation = sVarPrefix + sRepresentation;
        
        logger.info("Transformed rule:\n" + r + "\n to \n" + sRepresentation);
        
        // Transfer the rule to the given MINS engine
        
        try {
            minsEngine.compileString(sRepresentation);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new ExternalToolException(e, query);
        }
        
        
    }
    
    private String translateLiteral(Literal l) throws ExternalToolException, UnsupportedFeatureException {
        
        String predName = symbTransfomer.convertToTool(l.getSymbol());

        org.deri.wsml.reasoner.wsmlcore.datalog.Term[] args = l.getArguments();
        String symbName="";
        String argString ="";
        for (org.deri.wsml.reasoner.wsmlcore.datalog.Term arg : args){
            if (arg instanceof org.deri.wsml.reasoner.wsmlcore.datalog.Variable) {
                symbName = symbTransfomer.convertToTool((org.deri.wsml.reasoner.wsmlcore.datalog.Variable) arg);
            }
            else if (arg instanceof Constant) {
                symbName = symbTransfomer.convertToTool((Constant) arg);
            }
            else if (arg instanceof DataTypeValue) {
                symbName = symbTransfomer.convertToTool((DataTypeValue) arg);
                //TODO: DataTypes unclear!
            }else {
                throw new RuntimeException("Uwe says we will not arrive here.");
            }
            
            if (argString.length()==0) {
                argString += symbName;
            }
            else {
                argString += ","+symbName;
            }
        }
            
        if (!l.isPositive()){
            //TODO: NEGATION SYNTAX of mins
        }
        return predName +"("+argString+")"; // TODO: negation needs to be inserted here as well
    }

    /* (non-Javadoc)
     * @see org.deri.wsml.reasoner.wsmlcore.wrapper.DatalogReasonerFacade#useSymbolFactory(org.deri.wsml.reasoner.wsmlcore.wrapper.SymbolFactory)
     */
    public void useSymbolFactory(SymbolFactory sf) {
        symbTransfomer = new SymbolMap(sf);
        
    }

    public void register(String ontologyURI, Program kb) throws ExternalToolException {
        
        // We store a MINS engine which contains the a precompiled knowledgebase for each
        // ontology against which queries can be posed.
        
        // Set up an instance of a MINS engine
        Evaluator minsEngine = new Evaluator();
        // EvaluatorConfig conf = new EvaluatorConfig();
        //conf.EVALUATIONMETHOD=40;
        minsEngine.init();
        
  
        // Translate (resp. Transfer) the knowledge base to MINS 
        try {
            translateKnowledgebase(kb, minsEngine);
        } catch (UnsupportedFeatureException e) {
            e.printStackTrace();
            throw new ExternalToolException("Unsupported feature for MINS in knowledgebase.");
        }
        
        registeredKbs.put(ontologyURI, minsEngine);
    }
}
