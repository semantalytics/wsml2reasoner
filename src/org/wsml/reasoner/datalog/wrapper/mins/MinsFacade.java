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

import java.util.*;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.deri.mins.builtins.*;
import org.deri.mins.inference.*;
import org.deri.mins.inference.Rule;
import org.deri.mins.inference.terms.*;
import org.deri.mins.inference.terms.Term;
import org.deri.mins.inference.terms.Variable;
import org.wsml.reasoner.api.queryanswering.VariableBinding;
import org.wsml.reasoner.datalog.*;
import org.wsml.reasoner.datalog.wrapper.*;
import org.wsml.reasoner.impl.VariableBindingImpl;


/**
 * Package: package org.wsml.reasoner.datalog.wrapper.mins;

 * Author: Darko Anicic, DERI Innsbruck, 
 *         Holger Lausen, DERI Innsbruck
 * Date: 15.09.2005  17:24:24
 */
public class MinsFacade implements DatalogReasonerFacade {
	private Logger logger = Logger
			.getLogger("org.wsml.reasoner.wsmlcore.wrapper.mins");

	/**
	 * Here we store a MINS Engine which contains the compiled KB for each
	 * registered ontology
	 */
	private Map<String, RuleSet> registeredKbs = new HashMap<String, RuleSet>();

	private SymbolMap symbTransfomer = new SymbolMap(new MinsSymbolFactory());

	/**
	 * Creates a facade object that allows to invoke the MINS rule system for
	 * performing query evaluation tasks.
	 */
	public MinsFacade() {
		super();
		logger.setLevel(Level.OFF);
	}

	/**
	 * Evaluates a Query on a Datalog knowledgebase. The actual evaluation is
	 * done by the MINS system.
	 * 
	 * @throws ExternalToolException
	 */
	public QueryResult evaluate(ConjunctiveQuery q, String ontologyIRI) throws ExternalToolException {
    	VariableBinding varBinding;
    	
    	// to be changed    	
    	int evaluationMethod = 2;
    	
    	try {
            QueryResult result = new QueryResult(q);
            // retrieve KB ment for IRI:
            RuleSet minsEngine = registeredKbs.get(ontologyIRI);
            minsEngine.debuglevel=0;
            translateQuery(q, minsEngine); 
            logger.info("Starting MINS evaluation");
            
            /* 0: Naive Evaluation (only stratifight prorgams)<BR> 
             * 1: Dynamic Filtering Evaluation (only stratifight prorgams)<BR> 
             * 2: Wellfounded Evaluation with alternating fixed point (juergen says works)<BR> 
             * 3: Wellfounded Evaluation (juergen says probably buggy!) */
            minsEngine.EvaluationMethod(evaluationMethod);
            if (evaluationMethod == 0 || evaluationMethod ==1) {
            	minsEngine.Stratify();
            }
            minsEngine.EvalQueries();
            logger.info("Computing substitutions");
            for (Rule query = minsEngine.NextQuery(null); query != null; query = minsEngine.NextQuery(query)) {
            	Substitution s = minsEngine.Substitution(query);
                Enumeration enm = s.elements();
                symbTransfomer.setQueryForVariableMapping(q);
                
                while (enm.hasMoreElements()) {
                    GroundAtom a = (GroundAtom) enm.nextElement();
                    varBinding = new VariableBindingImpl();
                    for (int i = 0; i < a.terms.length; i++) {
                        //terms has length of numbers of variables!
                        //not each index has necessarily a subsitution one that does not have is a variable
                        if (!(a.terms[i] instanceof Variable)){
                            String wsmlVariable = symbTransfomer.convertToWSML(i);
                            String wsmlTerm = symbTransfomer.convertToWSML(a.terms[i]);
                        	varBinding.put(wsmlVariable, wsmlTerm);
                        }
                    }
                    result.getVariableBindings().add(varBinding);
                    logger.info("Added new variable binding to result set: " + varBinding);
                }
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
	 * 
	 * @param p -
	 *            the datalog program that constitutes the knowledgebase
	 * @throws UnsupportedFeatureException
	 */
	private void translateKnowledgebase(Program p, RuleSet minsEngine)
			throws ExternalToolException, UnsupportedFeatureException {
		logger.info("Translate knowledgebase :" + p);
		if (p == null) {
			logger.info("KB is not referenced. Assume empty KB.");
			return;
		}
		for (org.wsml.reasoner.datalog.Rule r : p) {
			translateRule(r, minsEngine);
		}
	}

	/**
	 * Translate the query
	 * 
	 * @param p -
	 *            the datalog program that constitutes the knowledgebase
	 * @return an object that represents the same query in the Mandrax system
	 * @throws UnsupportedFeatureException
	 */
	private void translateQuery(ConjunctiveQuery q, RuleSet minsEngine) throws ExternalToolException, UnsupportedFeatureException {
        Atom atom;
        Body body;
        Rule query;
        Body[] bodies;
        Literal l;
        int no;
        
        symbTransfomer.setQueryForVariableMapping(q);
        // Care about body
        List<Literal> qBody = q.getLiterals();
        bodies = new Body[qBody.size()];
        for(int i=0; i<qBody.size(); i++){
        	l = qBody.get(i);
        	no = symbTransfomer.convertToTool(l.getSymbol());
        	atom = translateLiteral2Atom(l); 
        	body = new Body(no, false, atom.terms);
        	bodies[i] = body;
        }
        query = new Rule(null, bodies);
        //System.out.println("query: " + query.toString());
        minsEngine.AddRule(query);
    }
	
	/**
	 * Translate a datalog rule
	 * 
	 * @throws UnsupportedFeatureException
	 */
	private void translateRule(org.wsml.reasoner.datalog.Rule r, RuleSet minsEngine) throws ExternalToolException, UnsupportedFeatureException {
        Rule rule;
        GroundAtom groundAtom;
    	Body body;
    	Body[] bodies;
        Head head;
        Predicate p;
        int no;
        Atom atom;
        Literal l;
        
        if (r.isConstraint()) {
        	// Simply ignore constraints 
            return;
            // TODO handle constraints properly
        } 
        
        // Translate head
        l = r.getHead();
        //avoid high number of variables!
        symbTransfomer.resetVariables();
        no = symbTransfomer.convertToTool(l.getSymbol());
        atom = translateLiteral2Atom(l);
        head = new Head(no, atom.terms);
        
        // Care about body
        List<Literal> rBody = r.getBody();

        if (rBody.size() == 0){
        	// rule is actually a fact
        	groundAtom = new GroundAtom(atom.terms);
        	minsEngine.AddFact(no, groundAtom);
        } else {
        	bodies = new Body[rBody.size()];
            for(int i=0; i<rBody.size(); i++){
            	l = rBody.get(i);
            	no = symbTransfomer.convertToTool(l.getSymbol());
            	atom = translateLiteral2Atom(l); 
            	body = new Body(no, false, atom.terms);
            	bodies[i] = body;
            }
            rule = new Rule(new Head[] {head}, bodies);
            minsEngine.AddRule(rule);
        }
    }
	
	private Atom translateLiteral2Atom(Literal l) throws ExternalToolException, UnsupportedFeatureException {
    	org.wsml.reasoner.datalog.Term[] args = l.getArguments();
        Term[] terms = new Term[args.length];
        Atom atom;
        
        long predName = symbTransfomer.convertToTool(l.getSymbol());
       
        for (int i=0; i<args.length; i++){  
        	if (args[i] instanceof org.wsml.reasoner.datalog.Variable) {
        		terms[i] = new Variable(symbTransfomer.convertToTool((org.wsml.reasoner.datalog.Variable) args[i]));
            }
            else if (args[i] instanceof Constant) {
            	terms[i] = new ConstTerm(symbTransfomer.convertToTool((Constant) args[i]));
            }
            else if (args[i] instanceof DataTypeValue) {
                //terms[i] = symbTransfomer.convertToTool((DataTypeValue) arg);
            	//TODO: DataTypes unclear!
            }else {
                throw new RuntimeException("Uwe says we will not arrive here.");
            }
        }
        atom = new Atom(terms);
        return atom;
    }
	
	 /** (non-Javadoc)
	 * 
	 * @see org.wsml.reasoner.wsmlcore.wrapper.mins.DatalogReasonerFacade#useSymbolFactory(org.wsml.reasoner.wsmlcore.wrapper.mins.SymbolFactory)
	 */
	public void useSymbolFactory(
			org.wsml.reasoner.datalog.wrapper.SymbolFactory sf) {
		// symbTransfomer = new SymbolMap(sf);
	}

	public void register(String ontologyIRI, Program kb)
			throws ExternalToolException {
		// Set up an instance of a MINS engine
		Builtin[] buildInList = new Builtin[] {
				new Builtin("Add", 3, new Add()),
				new Builtin("evaluable_", 2, new Evaluable()),
				new Builtin("unify", 2, new Equal()),
				new Builtin("isNum", 1, new IsNum()),
				new Builtin("isString", 1, new IsString()),
				new Builtin("isConst", 1, new IsConst()),
				new Builtin("less", 2, new Less()),
				new Builtin("lessorequal", 2, new Lessorequal()),
				new Builtin("greater", 2, new Greater()),
				new Builtin("greaterorequal", 2, new Greaterorequal()),
				new Builtin("between", 3, new Between()),
				new Builtin("starts", 2, new Starts()),
				new Builtin("ends", 2, new Ends()),
				new Builtin("lives", 2, new Lives()) };

		BuiltinConfig builtInConfig = new BuiltinConfig(buildInList);
		DBInterface db = new DB(); // facts stored in RAM
		RuleSet minsEngine = new RuleSet(builtInConfig, db);
		minsEngine.debuglevel = 0;

		// Translate (resp. Transfer) the knowledge base to MINS
		try {
			translateKnowledgebase(kb, minsEngine);
		} catch (UnsupportedFeatureException e) {
			e.printStackTrace();
			throw new ExternalToolException(
					"Unsupported feature for MINS in knowledgebase.");
		}
		registeredKbs.put(ontologyIRI, minsEngine);
	}
}