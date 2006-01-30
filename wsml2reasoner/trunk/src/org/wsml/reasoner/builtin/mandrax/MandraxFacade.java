///**
// * WSML Reasoner Implementation.
// *
// * Copyright (c) 2005, University of Innsbruck, Austria.
// *
// * This library is free software; you can redistribute it and/or modify it under
// * the terms of the GNU Lesser General Public License as published by the Free
// * Software Foundation; either version 2.1 of the License, or (at your option)
// * any later version.
// * This library is distributed in the hope that it will be useful, but WITHOUT
// * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
// * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
// * details.
// * You should have received a copy of the GNU Lesser General Public License along
// * with this library; if not, write to the Free Software Foundation, Inc.,
// * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
// * 
// */
//
//package org.wsml.reasoner.builtin.mandrax;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
//import org.mandarax.kernel.*;
//import org.mandarax.reference.AbstractResolutionInferenceEngine;
//import org.mandarax.reference.ResolutionInferenceEngine2;
//import org.mandarax.util.LogicFactorySupport;
//import org.wsml.reasoner.api.queryanswering.VariableBinding;
//import org.wsml.reasoner.builtin.*;
//import org.wsml.reasoner.builtin.wrapper.*;
//import org.wsml.reasoner.datalog.ConjunctiveQuery;
//import org.wsml.reasoner.datalog.DataTypeValue;
//import org.wsml.reasoner.datalog.Literal;
//import org.wsml.reasoner.datalog.Program;
//import org.wsml.reasoner.datalog.QueryResult;
//import org.wsml.reasoner.datalog.Variable;
//import org.wsml.reasoner.datalog.wrapper.DatalogReasonerFacade;
//import org.wsml.reasoner.datalog.wrapper.DefaultSymbolFactory;
//import org.wsml.reasoner.datalog.wrapper.ExternalToolException;
//import org.wsml.reasoner.datalog.wrapper.UnsupportedFeatureException;
//import org.wsml.reasoner.impl.VariableBindingImpl;
//
//public class MandraxFacade implements DatalogReasonerFacade {
//
//    private Logger logger = Logger.getLogger("org.wsml.reasoner.wsmlcore.wrapper.mandrax");
//    
//    private Map<String,KnowledgeBase> registeredKbs = new HashMap<String,KnowledgeBase>();
//    
//    AbstractResolutionInferenceEngine mandrax = new ResolutionInferenceEngine2(); // multiple results, no NAF!
//    ResultSet rs = null;
//    
//    public final static int MAX_PROOF_STEPS = 300;
//    
//    /**
//     * to do: read from property file!
//     */
//    
//    /** Sequence of variable names as used in the query from left to right */
//    private String[] queryVarNamesSequence;
//    
//    private SymbolMap symbTransfomer = new SymbolMap(new DefaultSymbolFactory());
//    
//    private org.wsml.reasoner.datalog.Query query;
//    
//    private KnowledgeBase kb;
//    private LogicFactory lf = LogicFactory.getDefaultFactory();
//    private LogicFactorySupport lfs = new LogicFactorySupport(lf);
//    
//    protected boolean incompleteMode = false;
//    
//
//    /**
//     * Creates a facade for invoking the Mandrax Rule System
//     * Since Mandrax implements top-down evaluation of a knowledgebase
//     * (based on resolution) it can run into loops when facing cyclic rule 
//     * bases. When running in incomplete mode, then the prover stops after
//     * predefined number of proof steps. Thus, termination is guarantueed
//     * for the sake of completeness of the result to the query.
//     * Not all answers to a query need to be found, only a subset is delivered
//     * in the general case. 
//     * 
//     * @param mode
//     */
//    public MandraxFacade(boolean mode) {
//        super();
//        // TODO Auto-generated constructor stub
//        incompleteMode = mode;
//    }
//
//    /**
//     * Evaluates a Query on a Datalog knowledgebase
//     * @throws ExternalToolException 
//     */
//    public QueryResult evaluate(ConjunctiveQuery q, String ontologyIRI) throws ExternalToolException {
//        
//        try {
//        
//            QueryResult result = new QueryResult(q);
//            
//            //retrieve KB ment for IRI:
//            this.kb = registeredKbs.get(ontologyIRI);
//        
//            query = q;
//            
//            // Translate the query itself
//            org.mandarax.kernel.Query query = translateQuery(q); 
//            
//            // Invoke Mandrax and get the results (can take some time)
//            
//            
//            //LoopCheckingAlgorithm lca = new DefaultLoopCheckingAlgorithm(20,10,10);
//            //mandrax.setLoopCheckingAlgorithm(lca); // use  Loop checking heuristics
//            if (incompleteMode) {
//                mandrax.setMaxNumberOfProofSteps(MAX_PROOF_STEPS); // makes the system incomplete in general!
//            }
//            
//            logger.info("Starting Mandrax ...");
//            rs = mandrax.query(query, kb, InferenceEngine.ALL, InferenceEngine.BUBBLE_EXCEPTIONS); // find all answers to the query, s           
//            logger.info(" Finished query call.");
//            
//            List queryVars = rs.getQueryVariables();
//            VariableBinding newVarBinding;
//            long k = 0;
//            logger.info("Traversing result set ...");
//            
//            while (rs.next()){
//                logger.info("... got next result.");
//                k++;
//                // Read the next result and create corresponding variable binding
//                newVarBinding = new VariableBindingImpl();
//                for (Iterator it = queryVars.iterator(); it.hasNext();){
//                    VariableTerm v = (VariableTerm) it.next();
//                    String varName = symbTransfomer.convertToWSML(v.getName());
//                    String varValue = symbTransfomer.convertToWSML(rs.getResult(v).toString());
//                    newVarBinding.put(varName, varValue);
//                }
//                
//                result.getVariableBindings().add(newVarBinding);
//                logger.fine("Added result variable binding: " + newVarBinding.toString());
//            }
//            
//            
//            logger.fine("Found " + k + " results to the query");
//            
//            return result;
//            
//        } catch(Exception e) { 
//            System.out.println("\n\n\n\n\n\n");
//            e.printStackTrace();
//            throw new ExternalToolException("Mandrax can not handle given query", e, q); 
//        } finally
//        {
//            kb = null;
//            //mandrax = null;
//            try {
//                if(rs != null){
//                    rs.close(); // Close result set!
//                }
//            } catch (InferenceException e) {
//                e.printStackTrace();
//            }
//        }
//        
//    }
//    
//    /**
//     * Translate a knowledgebase
//     * @param p - the datalog program that constitutes the knowledgebase
//     */
//    private void translateKnowledgebase(org.wsml.reasoner.datalog.Program p) throws ExternalToolException {
//        logger.info("Translate knowledgebase :" + p);
//
//        if (p == null) {logger.info("KB is not referenced. Assume empty KB."); return;} 
//        
//        for (org.wsml.reasoner.datalog.Rule r : p) {
//            translateRule(r);
//        }
//    }
//
//    /**
//     * Translate the query
//     * @param p - the datalog program that constitutes the knowledgebase
//     * @return an object that represents the same query in the Mandrax system
//     */
//    private org.mandarax.kernel.Query translateQuery(ConjunctiveQuery q) throws ExternalToolException {
//        logger.info("Translate query :" + q);
//        
//        // Derive and store the sequence of variables that defines the output tuples from the query 
//        List<Variable> bodyVars = q.getVariables();
//        queryVarNamesSequence = new String[bodyVars.size()]; 
//        org.wsml.reasoner.datalog.Term[] predArgs = new org.wsml.reasoner.datalog.Term[bodyVars.size()];
//        int i = 0;
//        for ( Variable v : bodyVars){
//            queryVarNamesSequence[i] = v.getSymbol();
//            predArgs[i] = v;
//            i++;
//        }
//        
//        logger.log(Level.FINE, "Sequence of variables in query is: ", queryVarNamesSequence);
//        
//        List<Literal> b = q.getLiterals();
//        Fact[] body = new Fact[b.size()];
//        i = 0;
//        for (Literal l : b){
//            body[i] = translateLiteral(l); 
//            i++;
//        }
//        
//        org.mandarax.kernel.Query newQuery = lf.createQuery(body, "result-query");
//        
//        logger.fine("Translated query to :" + newQuery.toString());
//       
//        return newQuery;
//    }
//    
//    /**
//     * Translate a datalog rule
//     */
//    private void translateRule(org.wsml.reasoner.datalog.Rule r) throws ExternalToolException {
//        
//        Fact head; 
//        List body = new ArrayList();
//        
//        if (r.isConstraint()) {
//            throw new ExternalToolException("Constraints are not supported explicitly by Mandrax!", this.query);
//        } 
//        
//        // Translate head
//        head = translateLiteral(r.getHead());
//        
//        // Care about body
//        List<Literal> rBody = r.getBody();
//
//        //FACT
//        if (rBody.size()==0){
//            kb.add(head);
//            return;
//        }
//                          
//        for( Literal bl : rBody){
//            Fact f = translateLiteral(bl); // convert fact to prerequisite.
//            Prerequisite newBodyLiteral = lf.createPrerequisite(f.getPredicate(), f.getTerms(), !bl.isPositive()); // ignore negation for now (positive datatlog)
//            body.add(newBodyLiteral);
//        }
//        
//        
//        
//        // Create the rule in Mandrax and add it to the knowledgebase
//        org.mandarax.kernel.Rule newRule;
//        newRule = lf.createRule(body, head);
//        
//        kb.add(newRule);
//        
//        
//        logger.info("Transformed rule:\n" + r + "\n to \n" + newRule.toString());
//        
//        
//    }
//    
//    Fact translateLiteral(Literal l) throws ExternalToolException {
//        try {
//            
//           Class[] predStructure = new Class[l.getArity()];
//           String predName = symbTransfomer.convertToTool(l.getSymbol());
//           
//           for(int i=0; i < l.getArity(); i++){
//               predStructure[i] = Object.class; // allow all possible types for the args.
//           }
//           org.mandarax.kernel.Predicate p = new SimplePredicate(predName, predStructure);
//                      
//           org.mandarax.kernel.Term[] predArgs = new org.mandarax.kernel.Term[l.getArity()];
//            
//            int i = 0;
//            org.wsml.reasoner.datalog.Term[] args = l.getArguments();
//            
//            String symbName;
//            org.mandarax.kernel.Term t = null;
//            
//            for (org.wsml.reasoner.datalog.Term arg : args){
//                if ( arg.getClass().equals(org.wsml.reasoner.datalog.Variable.class)) {
//                    symbName = symbTransfomer.convertToTool((org.wsml.reasoner.datalog.Variable) arg);
//                    t = lf.createVariableTerm(symbName, Object.class);
//                }
//                if ( arg.getClass().equals(org.wsml.reasoner.datalog.Constant.class)) {
//                    symbName = symbTransfomer.convertToTool((org.wsml.reasoner.datalog.Constant) arg);
//                    t = lf.createConstantTerm(symbName, Object.class);
//                }
//                if ( arg.getClass().equals(org.wsml.reasoner.datalog.DataTypeValue.class)) {
//                    symbName = symbTransfomer.convertToTool((DataTypeValue) arg);
//                    Class cClass;
//                    
//                    if (((DataTypeValue) arg).getType() == DataTypeValue.DataType.INTEGER){
//                        cClass = Integer.class;
//                    } else if (((DataTypeValue) arg).getType() == DataTypeValue.DataType.STRING){
//                        cClass = String.class;
//                    } else if (((DataTypeValue) arg).getType() == DataTypeValue.DataType.DECIMAL){
//                        cClass = Float.class;
//                    } else {
//                        cClass = Object.class;
//                    }
//                    
//                    t = lf.createConstantTerm(symbName, cClass);
//                }
//                
//                predArgs[i] = t;
//               
//                i++;
//            }
//              
//            // We ignore the negation type of the literal since we only care about positive datalog here.
//            return lf.createFact(p, predArgs);
//            
//            
//        } catch(UnsupportedFeatureException ufe){
//            throw new ExternalToolException("Can not convert literal to tool: " + l.toString(), ufe, query);
//        }
//    }
//
//    /* (non-Javadoc)
//     * @see org.wsml.reasoner.wsmlcore.wrapper.DatalogReasonerFacade#useSymbolFactory(org.wsml.reasoner.wsmlcore.wrapper.SymbolFactory)
//     */
//    public void useSymbolFactory(SymbolFactory sf) {
//        symbTransfomer = new SymbolMap(sf);
//        
//    }
//
//    public void register(String ontologyURI, Program kb) throws ExternalToolException {
//
//        // Some initialization
//        //this.kb = new org.mandarax.reference.KnowledgeBase();
//        this.kb = new org.mandarax.reference.AdvancedKnowledgeBase();
//        
//        // First translate the logic program the query refers to.
//        translateKnowledgebase(kb);
//
//        registeredKbs.put(ontologyURI, this.kb);
//        this.kb=null;
//    }
//}
