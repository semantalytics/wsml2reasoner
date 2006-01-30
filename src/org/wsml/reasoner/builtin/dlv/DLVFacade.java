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
//package org.wsml.reasoner.builtin.dlv;
//
//import java.util.*;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//
//import org.omwg.logicalexpression.terms.*;
//import org.omwg.ontology.*;
//import org.wsml.reasoner.api.queryanswering.VariableBinding;
//import org.wsml.reasoner.builtin.*;
//import org.wsml.reasoner.builtin.ConjunctiveQuery;
//import org.wsml.reasoner.builtin.Rule;
//import org.wsml.reasoner.builtin.wrapper.*;
//import org.wsml.reasoner.datalog.*;
//import org.wsml.reasoner.impl.VariableBindingImpl;
//
//import DLV.DLVException;
//import DLV.DLVExceptionUncheked;
//import DLV.DLVInvocationException;
//import DLV.DlvHandler;
//import DLV.Model;
//
//
///**
// * Integrates the DLV system into the WSML Core Reasoner framework for
// * query answering.
// * DLV can compute models for disjunctive logic programs and thus can 
// * deal with query answering with (non-disjunctive) datalog as well.
// * 
// * At present not all features of WSML Core are covered by DLV, namely
// * for built-in datatypes only integers are supported at present.
// * 
// * @author Uwe Keller, DERI Innsbruck
// */
//public class DLVFacade implements DatalogReasonerFacade {
//
//    private Logger logger = Logger.getLogger("org.wsml.reasoner.wsmlcore.wrapper.dlv");
//    {
//        logger.setLevel(Level.OFF);
//    }
// 
//    /**
//     * to do: read from property file!
//     */
//    private static final String DLV_BIN = "lib\\ext\\DLV\\dlv.cygwin.exe"; // relative to where the JVM is started
//   
//    private DLV.Program dlvProg;
//    static final String RESULT_PREDICATE_NAME = "dlv_result";
//    private DlvHandler dlv;
//
//    /** Sequence of variable names as used in the query from left to right */
//    private String[] queryVarNamesSequence;
//    
//    private SymbolMap symbTransfomer = new SymbolMap(new DLVSymbolFactory());
//    
//    private Query query;
//    
//    StringBuffer dlvProgText;
//    
//    /**
//     * Evaluates a Query on a Datalog knowledgebase
//     */
//    public QueryResult evaluate(ConjunctiveQuery q) throws ExternalToolException {
//        
//        try {
//            
//            QueryResult result = new QueryResult(q);
//            query = q;
//            
//            // Some initialization
//           
//            dlvProgText = new StringBuffer();
//            
//            
//            // First translate the logic program the query refers to.
////           translateKnowledgebase(q.getKnowledgebase());
//           // Translate the query itself
//           translateQuery(q); 
//           
//           dlvProg = new DLV.Program(dlvProgText);
//       
//           // Invoke DLV and get the results (can take some time)
//           
//           dlv = new DlvHandler(DLV_BIN);    
//           dlv.setProgram(dlvProg);
//                    
//           dlv.setNumberOfModels(1); // Since we only deal with (non-disjunctive) datalog there is only one minimal model
//           dlv.setFilter(new String[]{RESULT_PREDICATE_NAME}); // basically, we only need the ground instances of the result predicate.
//           
//           // Start model computation with DLV and read the model
//           
//           Model m;
//           DLV.Predicate resultPredicate;
//           
//           try
//           {
//                dlv.run(DlvHandler.MODEL_SYNCHRONOUS); // run a DLV process and set the output handling method.
//
//                while(dlv.hasMoreModels())   // waits while DLV outputs a new model
//                {
//                    m = dlv.nextModel(); // gets a Model object
//                    if(!m.isNoModel())
//                    {
//                        resultPredicate = m.getPredicate(RESULT_PREDICATE_NAME); // get result predicate with all its ground instances
//                        
//                        int i = 0;
//                        
//                        // Now we construct the query result
//                         while(result != null && resultPredicate != null && resultPredicate.hasMoreLiterals())            // iterate Literals contained in the model for result predicate
//                            {
//                             DLV.Predicate.Literal l=resultPredicate.nextLiteral();           // gets a Literal
//                             logger.fine("Result contains literal ["+ ++i +"] = " +l.toString());
//                             
//                             VariableBinding newVarBinding = new VariableBindingImpl();
//                             
//                             for(int k = 0; k < l.arity(); k++){
//                                 String varName = this.queryVarNamesSequence[k];
//                                 String varValue = symbTransfomer.convertToWSML(l.getTermAt(k));
//                                 newVarBinding.put(varName, varValue);
//                             }
//                             
//                             result.getVariableBindings().add(newVarBinding);
//                             logger.fine("Added respective variable binding to result for literal " +l.toString());
//                             
//                            }
//                            logger.fine("Found " + i + " results to the query");
//                        
//                    } else {
//                        logger.info("Query with the given knowledgebase is unsatisfiable, i.e. has no model!\n\n"  + q.toString());
//                    }
//                   
//                }
//                
//           }
//           catch(DLVException d) { throw new ExternalToolException("DLV can not handle given query", d, q); }
//           catch(DLVExceptionUncheked d) {throw new ExternalToolException("Some unchecked problem occured in DLV during query evaluation", d, q); }
//           catch(UnsupportedFeatureException d) { throw new ExternalToolException("DLV can not handle given query", d, q); }
//           finally
//           {
//               
//               try {
//                    dlv.reset();
//                } catch (DLVInvocationException e) {
//                    e.printStackTrace();
//                }
//               
//             m = null;
//             dlv = null;
//             resultPredicate = null;
//           }
//                      
//           return result;
//            
//        } catch (ExternalToolException ete){
//            logger.severe("External tool exception occured when trying to evaluate query: " + ete.getFailedQuery());
//            throw ete;
//        }
//    }
//    
//    /**
//     * Translate a knowledgebase
//     * @param p - the datalog program that constitutes the knowledgebase
//     */
//    private void translateKnowledgebase(org.wsml.reasoner.datalog.Program p) throws ExternalToolException {
//        logger.info("Translate knowledgebase :" + p);
//        
//        
//        if (p == null) {logger.info("KB is not referenced. Assume empty KB."); return;} 
//        
//        for (Rule r : p) {
//            translateRule(r);
//        }
//    }
//
//    /**
//     * Translate the query
//     * @param p - the datalog program that constitutes the knowledgebase
//     */
//    private void translateQuery(ConjunctiveQuery q) throws ExternalToolException {
//        logger.info("Translate query :" + q);
//        
//        // Derive and store the sequence of variables that defines the output tuples from the query 
//        List<Variable> bodyVars = q.getVariables();
//        queryVarNamesSequence = new String[bodyVars.size()]; 
//        Term[] predArgs = new Term[bodyVars.size()];
//        int i = 0;
//        for ( Variable v : bodyVars){
//            queryVarNamesSequence[i] = v.getSymbol();
//            predArgs[i] = v;
//            i++;
//        }
//        
//        logger.log(Level.FINE, "Sequence of variables in query is: ", queryVarNamesSequence);
//        
//        // Translate the query:
//        //   Given:         ?- query-expr(x1,...,xn)
//        //   Transform to:  result(x1,...,xk) :- query-expr(x1,...,xn)
//        //          where x1,...,xk is the subsequence of x1,...,xn where multiple occurrences of the
//        //          same variable have been removed.
//        
//        try {
//            
//            Literal head = new Literal(new org.wsml.reasoner.datalog.Predicate(RESULT_PREDICATE_NAME,queryVarNamesSequence.length), predArgs);
//            
//            Rule resultDef = new Rule(head, q.getLiterals());
//            logger.info("Converted query to rule:" + resultDef);
//            translateRule(resultDef);
//            
//        } catch(DatalogException d){
//            // Never happens
//            d.printStackTrace();
//        }
//      
//        
//    }
//    
//    /**
//     * Translate a datalog rule
//     */
//    private void translateRule(Rule r) throws ExternalToolException {
//        
//        String stringRepresentation = "";
//        
//        if (r.isFact()) {
//            stringRepresentation = translateLiteral(r.getHead());
//        } else if (r.isConstraint()) {
//            stringRepresentation = ":- ";
//        } else {
//            // normal rule with head and body: care about head
//            stringRepresentation += translateLiteral(r.getHead());
//            stringRepresentation += " :- ";
//        }
//        
//        // Care about body
//        int i = 1;
//        List<Literal> body = r.getBody();
//        for( Literal bl : body){
//            stringRepresentation += translateLiteral(bl);
//            if (i < body.size()){
//                stringRepresentation += ", ";
//            }
//            i++;
//        }
//        
//        stringRepresentation += ".";
//        stringRepresentation += "\n";
//        
//        
//        // Add the string representation to the DLV program
//        dlvProgText.append(stringRepresentation);
//        
//        logger.info("Transformed rule:\n" + r + "\n to \n" + stringRepresentation);
//        
//    }
//    
//    String translateLiteral(Literal l) throws ExternalToolException {
//        try {
//            String result = "";
//            
//            result += symbTransfomer.convertToTool(l.getSymbol());
//            result += "(";
//            
//            int i = 1;
//            Term[] args = l.getArguments();
//            for (Term arg : args){
//                if ( arg.getClass().equals(org.wsml.reasoner.datalog.Variable.class)) {
//                    result += symbTransfomer.convertToTool((org.wsml.reasoner.datalog.Variable) arg);
//                }
//                if ( arg.getClass().equals(org.wsml.reasoner.datalog.Constant.class)) {
//                    result += symbTransfomer.convertToTool((org.wsml.reasoner.datalog.Constant) arg);
//                }
//                if ( arg.getClass().equals(org.wsml.reasoner.datalog.DataTypeValue.class)) {
//                    result += symbTransfomer.convertToTool((DataTypeValue) arg);
//                }
//                
//                if (i < args.length){
//                    result += ", ";
//                }
//                i++;
//            }
//            
//            result += ")";
//            
//            // We ignore the negation type of the literal since we only care about positive datalog here.
//            return result;
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
//        // TODO
//       
////        // Translate (resp. Transfer) the knowledge base to DLV 
////        try {
////            String dlvKBText = translateKnowledgebase(kb);
////            registeredKbs.put(ontologyURI, dlvKBText);
////        } catch (UnsupportedFeatureException e) {
////            e.printStackTrace();            
////            throw new ExternalToolException("Unsupported feature for MINS in knowledgebase.");
////        }
////        
//      
//  
//    }
//
//    public QueryResult evaluate(ConjunctiveQuery q, String ontologyURI) throws ExternalToolException {
//        // TODO Auto-generated method stub
//        return null;
//    }
//
//    public void deregister(String ontologyURI) throws org.wsml.reasoner.builtin.ExternalToolException {
//        // TODO Auto-generated method stub
//        
//    }
//
//    public Set<Map<Variable, Term>> evaluate(ConjunctiveQuery q, String ontologyURI) throws org.wsml.reasoner.builtin.ExternalToolException {
//        // TODO Auto-generated method stub
//        return null;
//    }
//
//    public void register(String ontologyURI, Set<Rule> kb) throws org.wsml.reasoner.builtin.ExternalToolException {
//        // TODO Auto-generated method stub
//        
//    }
//    
//    
//}
