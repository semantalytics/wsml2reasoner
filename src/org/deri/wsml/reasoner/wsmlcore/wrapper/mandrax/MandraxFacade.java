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

package org.deri.wsml.reasoner.wsmlcore.wrapper.mandrax;

import java.util.*;
import java.util.logging.*;

import org.deri.wsml.reasoner.wsmlcore.datalog.*;
import org.deri.wsml.reasoner.wsmlcore.wrapper.*;

import org.mandarax.kernel.*;
import org.mandarax.reference.AbstractResolutionInferenceEngine;
import org.mandarax.reference.ResolutionInferenceEngine3;
import org.mandarax.reference.ResolutionInferenceEngine4;
import org.mandarax.reference.ResolutionInferenceEngine2;
import org.mandarax.reference.ResolutionInferenceEngine;
import org.mandarax.reference.DefaultLoopCheckingAlgorithm;
import org.mandarax.util.*;

import org.deri.wsml.reasoner.api.queryanswering.*;
import org.deri.wsml.reasoner.impl.*;

public class MandraxFacade implements DatalogReasonerFacade {

    private Logger logger = Logger.getLogger("org.deri.wsml.reasoner.wsmlcore.wrapper.mandrax");
    
    AbstractResolutionInferenceEngine mandrax = new ResolutionInferenceEngine2(); // multiple results, no NAF!
    ResultSet rs = null;
    
    public final static int MAX_PROOF_STEPS = 3000;
    
    /**
     * to do: read from property file!
     */
    
    /** Sequence of variable names as used in the query from left to right */
    private String[] queryVarNamesSequence;
    
    private SymbolMap symbTransfomer = new SymbolMap(new DefaultSymbolFactory());
    
    private org.deri.wsml.reasoner.wsmlcore.datalog.Query query;
    
    private KnowledgeBase kb;
    private LogicFactory lf = LogicFactory.getDefaultFactory();
    private LogicFactorySupport lfs = new LogicFactorySupport(lf);
    
    protected boolean incompleteMode = false;
    

    /**
     * Creates a facade for invoking the Mandrax Rule System
     * Since Mandrax implements top-down evaluation of a knowledgebase
     * (based on resolution) it can run into loops when facing cyclic rule 
     * bases. When running in incomplete mode, then the prover stops after
     * predefined number of proof steps. Thus, termination is guarantueed
     * for the sake of completeness of the result to the query.
     * Not all answers to a query need to be found, only a subset is delivered
     * in the general case. 
     * 
     * @param mode
     */
    public MandraxFacade(boolean mode) {
        super();
        // TODO Auto-generated constructor stub
        incompleteMode = mode;
    }

    /**
     * Evaluates a Query on a Datalog knowledgebase
     * @throws ExternalToolException 
     */
    public QueryResult evaluate(ConjunctiveQuery q) throws ExternalToolException {
        
        try {
        
        QueryResult result = new QueryResult(q);
        
      
            
            query = q;
            
            // Some initialization
            
            kb = new org.mandarax.reference.KnowledgeBase();
            // kb = new org.mandarax.reference.AdvancedKnowledgeBase();
            
            // First translate the logic program the query refers to.
//            translateKnowledgebase(q.getKnowledgebase());
            
            // Translate the query itself
            org.mandarax.kernel.Query query = translateQuery(q); 
            
            // Invoke Mandrax and get the results (can take some time)
            
            
            //LoopCheckingAlgorithm lca = new DefaultLoopCheckingAlgorithm(20,10,10);
            //mandrax.setLoopCheckingAlgorithm(lca); // use  Loop checking heuristics
            if (incompleteMode) {
                mandrax.setMaxNumberOfProofSteps(MAX_PROOF_STEPS); // makes the system incomplete in general!
            }
            
            logger.info("Starting Mandrax ...");
            rs = mandrax.query(query, kb, InferenceEngine.ALL, InferenceEngine.BUBBLE_EXCEPTIONS); // find all answers to the query, s           
            logger.info(" Finished query call.");
            
            List queryVars = rs.getQueryVariables();
            VariableBinding newVarBinding;
            long k = 0;
            logger.info("Traversing result set ...");
            
            while (rs.next()){
                logger.info("... got next result.");
                k++;
                // Read the next result and create corresponding variable binding
                newVarBinding = new VariableBindingImpl();
                for (Iterator it = queryVars.iterator(); it.hasNext();){
                    VariableTerm v = (VariableTerm) it.next();
                    String varName = symbTransfomer.convertToWSML(v.getName());
                    String varValue = symbTransfomer.convertToWSML(rs.getResult(v).toString());
                    newVarBinding.put(varName, varValue);
                }
                
                result.getVariableBindings().add(newVarBinding);
                logger.fine("Added result variable binding: " + newVarBinding.toString());
            }
            
            
            logger.fine("Found " + k + " results to the query");
            
            return result;
            
        } catch(Exception e) { 
            throw new ExternalToolException("Mandrax can not handle given query", e, q); 
        } finally
        {
            kb = null;
            mandrax = null;
            try {
                if(rs != null){
                    rs.close(); // Close result set!
                }
            } catch (InferenceException e) {
                e.printStackTrace();
            }
        }
        
    }
    
    /**
     * Translate a knowledgebase
     * @param p - the datalog program that constitutes the knowledgebase
     */
    private void translateKnowledgebase(org.deri.wsml.reasoner.wsmlcore.datalog.Program p) throws ExternalToolException {
        logger.info("Translate knowledgebase :" + p);
        
        
        if (p == null) {logger.info("KB is not referenced. Assume empty KB."); return;} 
        
        for (org.deri.wsml.reasoner.wsmlcore.datalog.Rule r : p) {
            translateRule(r);
        }
    }

    /**
     * Translate the query
     * @param p - the datalog program that constitutes the knowledgebase
     * @return an object that represents the same query in the Mandrax system
     */
    private org.mandarax.kernel.Query translateQuery(ConjunctiveQuery q) throws ExternalToolException {
        logger.info("Translate query :" + q);
        
        // Derive and store the sequence of variables that defines the output tuples from the query 
        List<Variable> bodyVars = q.getVariables();
        queryVarNamesSequence = new String[bodyVars.size()]; 
        org.deri.wsml.reasoner.wsmlcore.datalog.Term[] predArgs = new org.deri.wsml.reasoner.wsmlcore.datalog.Term[bodyVars.size()];
        int i = 0;
        for ( Variable v : bodyVars){
            queryVarNamesSequence[i] = v.getSymbol();
            predArgs[i] = v;
            i++;
        }
        
        logger.log(Level.FINE, "Sequence of variables in query is: ", queryVarNamesSequence);
        
        List<Literal> b = q.getLiterals();
        Fact[] body = new Fact[b.size()];
        i = 0;
        for (Literal l : b){
            body[i] = translateLiteral(l); 
            i++;
        }
        
        org.mandarax.kernel.Query newQuery = lf.createQuery(body, "result-query");
        
        logger.fine("Translated query to :" + newQuery.toString());
       
        return newQuery;
    }
    
    /**
     * Translate a datalog rule
     */
    private void translateRule(org.deri.wsml.reasoner.wsmlcore.datalog.Rule r) throws ExternalToolException {
        
        Fact head; 
        List body = new ArrayList();
        
        if (r.isConstraint()) {
            throw new ExternalToolException("Constraints are not supported explicitly by Mandrax!", this.query);
        } 
        
        // Translate head
        head = translateLiteral(r.getHead());
                          
        // Care about body
        List<Literal> rBody = r.getBody();
        for( Literal bl : rBody){
            Fact f = translateLiteral(bl); // convert fact to prerequisite.
            Prerequisite newBodyLiteral = lf.createPrerequisite(f.getPredicate(), f.getTerms(), !bl.isPositive()); // ignore negation for now (positive datatlog)
            body.add(newBodyLiteral);
        }
        
        // Create the rule in Mandrax and add it to the knowledgebase
        org.mandarax.kernel.Rule newRule;
        newRule = lf.createRule(body, head);
        
        kb.add(newRule);
        
        
        logger.info("Transformed rule:\n" + r + "\n to \n" + newRule.toString());
        
        
    }
    
    Fact translateLiteral(Literal l) throws ExternalToolException {
        try {
            
           Class[] predStructure = new Class[l.getArity()];
           String predName = symbTransfomer.convertToTool(l.getSymbol());
           
           for(int i=0; i < l.getArity(); i++){
               predStructure[i] = Object.class; // allow all possible types for the args.
           }
           org.mandarax.kernel.Predicate p = new SimplePredicate(predName, predStructure);
                      
           org.mandarax.kernel.Term[] predArgs = new org.mandarax.kernel.Term[l.getArity()];
            
            int i = 0;
            org.deri.wsml.reasoner.wsmlcore.datalog.Term[] args = l.getArguments();
            
            String symbName;
            org.mandarax.kernel.Term t = null;
            
            for (org.deri.wsml.reasoner.wsmlcore.datalog.Term arg : args){
                if ( arg.getClass().equals(org.deri.wsml.reasoner.wsmlcore.datalog.Variable.class)) {
                    symbName = symbTransfomer.convertToTool((org.deri.wsml.reasoner.wsmlcore.datalog.Variable) arg);
                    t = lf.createVariableTerm(symbName, Object.class);
                }
                if ( arg.getClass().equals(org.deri.wsml.reasoner.wsmlcore.datalog.Constant.class)) {
                    symbName = symbTransfomer.convertToTool((org.deri.wsml.reasoner.wsmlcore.datalog.Constant) arg);
                    t = lf.createConstantTerm(symbName, Object.class);
                }
                if ( arg.getClass().equals(org.deri.wsml.reasoner.wsmlcore.datalog.DataTypeValue.class)) {
                    symbName = symbTransfomer.convertToTool((DataTypeValue) arg);
                    Class cClass;
                    
                    if (((DataTypeValue) arg).getType() == DataTypeValue.DataType.INTEGER){
                        cClass = Integer.class;
                    } else if (((DataTypeValue) arg).getType() == DataTypeValue.DataType.STRING){
                        cClass = String.class;
                    } else if (((DataTypeValue) arg).getType() == DataTypeValue.DataType.DECIMAL){
                        cClass = Float.class;
                    } else {
                        cClass = Object.class;
                    }
                    
                    t = lf.createConstantTerm(symbName, cClass);
                }
                
                predArgs[i] = t;
               
                i++;
            }
              
            // We ignore the negation type of the literal since we only care about positive datalog here.
            return lf.createFact(p, predArgs);
            
            
        } catch(UnsupportedFeatureException ufe){
            throw new ExternalToolException("Can not convert literal to tool: " + l.toString(), ufe, query);
        }
    }

    /* (non-Javadoc)
     * @see org.deri.wsml.reasoner.wsmlcore.wrapper.DatalogReasonerFacade#useSymbolFactory(org.deri.wsml.reasoner.wsmlcore.wrapper.SymbolFactory)
     */
    public void useSymbolFactory(SymbolFactory sf) {
        symbTransfomer = new SymbolMap(sf);
        
    }

    public void register(String ontologyURI, Program kb) throws ExternalToolException {
        // TODO Auto-generated method stub
        
    }

    public QueryResult evaluate(ConjunctiveQuery q, String ontologyURI) throws ExternalToolException {
        // TODO Auto-generated method stub
        return null;
    }
    

}
