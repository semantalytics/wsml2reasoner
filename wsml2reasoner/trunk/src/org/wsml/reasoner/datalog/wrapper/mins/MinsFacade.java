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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.deri.mins.*;
import org.deri.mins.Rule;
import org.deri.mins.api.DBInterface;
import org.deri.mins.builtins.BuiltinConfig;
import org.deri.mins.terms.*;
import org.deri.mins.terms.Term;
import org.deri.mins.terms.Variable;
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

    private final static Predicate PRED_CONSTRAINT = new Predicate(
            "mins-constraint", 1);
    
    private Map<String, ConjunctiveQuery> constraints2Queries = new HashMap<String, ConjunctiveQuery>();


    /**
     * Here we store a MINS Engine which contains the compiled KB for each
     * registered ontology
     */
    private Map<String, RuleSet> registeredKbs = new HashMap<String, RuleSet>();

    private MinsSymbolMap symbTransfomer = new MinsSymbolMap(new MinsSymbolFactory());

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
        
        /* 0: Naive Evaluation (only stratifight prorgams)<BR> 
         * 1: Dynamic Filtering Evaluation (only stratifight prorgams)<BR> 
         * 2: Wellfounded Evaluation with alternating fixed point (juergen says works)<BR> 
         * 3: Wellfounded Evaluation (juergen says probably buggy!) */
        int evaluationMethod = 2;
        
        try {
            // retrieve KB ment for IRI:
            RuleSet minsEngine = registeredKbs.get(ontologyIRI);
            Rule query = translateQuery(q, minsEngine); 
            logger.info("Starting Constraint Check");
            checkConstraint(minsEngine);

            logger.info("Starting MINS evaluation");
            
            minsEngine.setEvaluationMethod(evaluationMethod);
            minsEngine.evaluate();
            Substitution s = minsEngine.getSubstitution(query);
            
            QueryResult result = new QueryResult(q);
            GroundAtom a = (GroundAtom) s.First();
            while (a!=null) {
                varBinding = new VariableBindingImpl();
                for (int i = 0; i < a.terms.length; i++) {
                    //terms has length of numbers of variables!
                    //not each index has necessarily a subsitution one that does not have is a variable
                    if (!(a.terms[i] instanceof Variable)){
                        String wsmlVariable = symbTransfomer.convertToWSML(i,q);
                        String wsmlTerm = symbTransfomer.convertToWSML(a.terms[i]);
                        varBinding.put(wsmlVariable, wsmlTerm);
                    }
                }
                result.getVariableBindings().add(varBinding);
                logger.info("Added new variable binding to result set: " + varBinding);
                a=s.Next();
            }
            return result;
        } catch (UnsupportedFeatureException e) {
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
    private Rule translateQuery(ConjunctiveQuery query, RuleSet minsEngine) throws ExternalToolException, UnsupportedFeatureException {
        Atom atom;
        Body body;
        Body[] bodies;
        Literal l;
        int no;
        
        // Care about body
        List<Literal> qBody = query.getLiterals();
        bodies = new Body[qBody.size()];
        for(int i=0; i<qBody.size(); i++){
            l = qBody.get(i);
            no = symbTransfomer.convertToTool(l.getSymbol());
            atom = translateLiteral2Atom(l, query); 
            body = new Body(no, !l.isPositive(), atom.terms);
            bodies[i] = body;
        }
        Rule rule = new Rule(null, bodies);
        minsEngine.addRule(rule);
        return rule;
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
            Constant cBody = new Constant(r.getBody().toString());
            r = new org.wsml.reasoner.datalog.Rule(new Literal(PRED_CONSTRAINT, new org.wsml.reasoner.datalog.Term[]{cBody}),
                    r.getBody());
            constraints2Queries.put(r.getBody().toString(),
                    new ConjunctiveQuery(r.getBody()));
        } 
        
        // Translate head
        l = r.getHead();
        //avoid high number of variables!
        no = symbTransfomer.convertToTool(l.getSymbol());
        atom = translateLiteral2Atom(l, r);
        head = new Head(no, atom.terms);
        
        // Care about body
        List<Literal> rBody = r.getBody();

        if (rBody.size() == 0){
            // rule is actually a fact
            groundAtom = new GroundAtom(atom.terms);
            minsEngine.addFact(no, groundAtom);
        } else {
            bodies = new Body[rBody.size()];
            for(int i=0; i<rBody.size(); i++){
                l = rBody.get(i);
                no = symbTransfomer.convertToTool(l.getSymbol());
                atom = translateLiteral2Atom(l, r); 
                body = new Body(no, !l.isPositive(), atom.terms);
                bodies[i] = body;
            }
            rule = new Rule(new Head[] {head}, bodies);
            minsEngine.addRule(rule);
        }
    }
    
    /**
     * 
     * @param literal
     * @param queryOrRuleContext a datalog rule or query to keep context for Variable mapping
     * @return
     * @throws ExternalToolException
     */
    private Atom translateLiteral2Atom(Literal literal, 
            Object queryOrRuleContext) throws ExternalToolException{
        
        org.wsml.reasoner.datalog.Term[] args = literal.getArguments();
        Term[] terms = new Term[args.length];
        Atom atom;
        
        long predName = symbTransfomer.convertToTool(literal.getSymbol());
       
        for (int i=0; i<args.length; i++){  
            if (args[i] instanceof org.wsml.reasoner.datalog.Variable) {
                terms[i] = new Variable(symbTransfomer.convertToTool((
                        org.wsml.reasoner.datalog.Variable) args[i], queryOrRuleContext));
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
        BuiltinConfig builtInConfig = new BuiltinConfig();
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
    
    private void checkConstraint(RuleSet minsEngine) throws 
            DatalogException, ExternalToolException, 
            UnsupportedFeatureException, ConstraintViolationError{
        //check contraint query
        List<Literal> cqlits = new LinkedList<Literal>();
        cqlits.add(new Literal(PRED_CONSTRAINT,
                new org.wsml.reasoner.datalog.Term[]{
                    new org.wsml.reasoner.datalog.Variable("query")
                }));
        Rule constraintQuery = translateQuery( new ConjunctiveQuery(cqlits),minsEngine);
        logger.info("Starting MINS evaluation of constraints");
        minsEngine.evaluate();
        
        Substitution cqsubs = minsEngine.getSubstitution(constraintQuery);
        ConjunctiveQuery violatedQuery = null;
        GroundAtom cqAnswer = cqsubs.First();
        if (cqAnswer!=null) {
            //predicate only has one variable:
            String value = symbTransfomer.convertToWSML(cqAnswer.terms[0]);
//            System.out.println(value);
            violatedQuery = constraints2Queries.get(value);
        }
        minsEngine.deleteRule(constraintQuery);

        if (violatedQuery==null) {
            return;
        }
        
        //We have a constraint violation, give client info about instances:
        Rule violatedMinsQuery = translateQuery(violatedQuery,minsEngine);
        minsEngine.evaluate();
        Substitution vmqsubs = minsEngine.getSubstitution(violatedMinsQuery );
        GroundAtom vmqAnswer = vmqsubs.First();
        String bindings="";
        for (int i=0; i<vmqAnswer.terms.length; i++){
            //terms has length of numbers of variables!
            //not each index has necessarily a subsitution one that does not have is a variable
            if (!(vmqAnswer.terms[i] instanceof Variable)){
                String wsmlVariable = symbTransfomer.convertToWSML(i,violatedQuery);
                String wsmlTerm = symbTransfomer.convertToWSML(vmqAnswer.terms[i]);
                bindings += wsmlVariable +" -> "+ wsmlTerm +"\n";
            }
        }
        throw new ConstraintViolationError(
                violatedQuery.toString() + "\n" + bindings);

    }
}