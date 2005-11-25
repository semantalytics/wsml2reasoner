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
import java.util.logging.*;

import org.deri.mins.*;
import org.deri.mins.Rule;
import org.deri.mins.api.*;
import org.deri.mins.builtins.*;
import org.deri.mins.terms.*;
import org.deri.wsmo4j.io.parser.wsml.*;
import org.omwg.logicalexpression.terms.*;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.*;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.builtin.*;
import org.wsml.reasoner.impl.*;
import org.wsmo.common.*;
import org.wsmo.factory.*;

import com.ontotext.wsmo4j.ontology.*;

/**
 * Package: package org.wsml.reasoner.datalog.wrapper.mins;
 * 
 * Author: Darko Anicic, DERI Innsbruck,
 *         Holger Lausen, DERI Innsbruck,
 *         Uwe Keller, DERI Innsbruck,
 * Date $Date$
 */
public class MinsFacade implements DatalogReasonerFacade {
    private Logger logger = Logger
            .getLogger("org.wsml.reasoner.wsmlcore.wrapper.mins");

    private final static String PRED_CONSTRAINT = "mins-constraint";

    private Map<String, ConjunctiveQuery> constraints2Queries = new HashMap<String, ConjunctiveQuery>();

    /**
     * Here we store a MINS Engine which contains the compiled KB for each
     * registered ontology
     */
    private Map<String, RuleSet> registeredKbs = new HashMap<String, RuleSet>();

    private MinsSymbolMap symbTransfomer = new MinsSymbolMap();

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
    public Set<Map<Variable, Term>> evaluate(ConjunctiveQuery q, String ontologyURI) throws ExternalToolException {
        /*
         * 0: Naive Evaluation (only stratifight prorgams)<BR> 
         * 1: Dynamic Filtering Evaluation (only stratifight prorgams)<BR>
         * 2: Wellfounded Evaluation with alternating fixed point (juergen says works)<BR> 
         * 3: Wellfounded Evaluation (juergen says probably buggy!)
         */
        int evaluationMethod = 2;

        try {
            // retrieve KB ment for IRI:
            RuleSet minsEngine = registeredKbs.get(ontologyURI);
            Rule query = translateQuery(q, minsEngine);
            logger.info("Starting Constraint Check");
            checkConstraint(minsEngine);

            logger.info("Starting MINS evaluation");

            minsEngine.setEvaluationMethod(evaluationMethod);
            minsEngine.evaluate();
            Substitution s = minsEngine.getSubstitution(query);

            Set<Map<Variable, Term>> result = new HashSet<Map<Variable, Term>>();
            GroundAtom a = (GroundAtom) s.First();
            while (a != null) {
                Map<Variable, Term> varBinding = new HashMap<Variable, Term>();
                for (int i = 0; i < a.terms.length; i++) {
                    // terms has length of numbers of variables!
                    // not each index has necessarily a subsitution one that
                    // does not have is a variable
                    if (!(a.terms[i] instanceof Variable)) {
                        Variable wsmlVariable = symbTransfomer
                                .convertToWSML(i, q);
                        Term wsmlTerm = symbTransfomer
                                .convertToWSML(a.terms[i]);
                        if (!(wsmlVariable instanceof TempVariable))
                            varBinding.put(wsmlVariable, wsmlTerm);
                    }
                }
                result.add(varBinding);
                logger.info("Added new variable binding to result set: "
                        + varBinding);
                a = s.Next();
            }
            return result;
        } catch (UnsupportedFeatureException e) {
            throw new ExternalToolException("MINS can not handle given query",
                    e);
        }
    }

    /**
     * Translate a knowledgebase
     * 
     * @param p -
     *            the datalog program that constitutes the knowledgebase
     * @throws UnsupportedFeatureException
     */
    private void translateKnowledgebase(Set<org.wsml.reasoner.builtin.Rule> p, RuleSet minsEngine)
            throws ExternalToolException, UnsupportedFeatureException {
        logger.info("Translate knowledgebase :" + p);
        if (p == null) {
            logger.info("KB is not referenced. Assume empty KB.");
            return;
        }
        for (org.wsml.reasoner.builtin.Rule r : p) {
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
    private Rule translateQuery(ConjunctiveQuery query, RuleSet minsEngine)
            throws ExternalToolException, UnsupportedFeatureException {
        Atom atom;
        Body body;
        Body[] bodies;
        Literal l;
        int no;

        // Care about body
        List<Literal> qBody = query.getLiterals();
        bodies = new Body[qBody.size()];
        for (int i = 0; i < qBody.size(); i++) {
            l = qBody.get(i);
            // use predicatesymbol + arity to obtain mins pred number
            no = symbTransfomer.convertToTool(l);
            atom = translateLiteral2Atom(l, query);
            body = createBody(no, atom.terms, l);
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
    private void translateRule(org.wsml.reasoner.builtin.Rule r,
            RuleSet minsEngine) throws ExternalToolException,
            UnsupportedFeatureException {
        Rule rule;
        GroundAtom groundAtom;
        Body body;
        Body[] bodies;
        Head head;
        int no;
        Atom atom;
        Literal l;

        if (r.isConstraint()) {
            r = new org.wsml.reasoner.builtin.Rule(
                    //head
                    new Literal(true, PRED_CONSTRAINT,
                            WSMO4JManager.getDataFactory().createWsmlString(r.getBody().toString())),
                    //body
                    r.getBody());
            constraints2Queries.put(r.getBody().toString(),
                    new ConjunctiveQuery(r.getBody()));
            //System.out.println(r);
        }

        // Translate head
        l = r.getHead();
        if(l!=null){
        // use predicatesymbol + arity to obtain mins pred number
        no = symbTransfomer.convertToTool(l);
        if (no==-1){
            throw new RuntimeException("No BuiltIns Allowed in the Head of a Rule: "+r);
        }
        atom = translateLiteral2Atom(l, r);
        head = new Head(no, atom.terms);
        

        // Care about body
        List<Literal> rBody = r.getBody();

        if (rBody.size() == 0) {
            // rule is actually a fact
            groundAtom = new GroundAtom(atom.terms);
            minsEngine.addFact(no, groundAtom);
        } else {
            bodies = new Body[rBody.size()];
            for (int i = 0; i < rBody.size(); i++) {
                l = rBody.get(i);
                // use predicatesymbol + arity to obtain mins pred number
                no = symbTransfomer.convertToTool(l);
                atom = translateLiteral2Atom(l, r);
                body = createBody(no, atom.terms, l);
                bodies[i] = body;
            }
            rule = new Rule(new Head[] { head }, bodies);
            //System.out.println(r+"\n"+rule);
            minsEngine.addRule(rule);
        }
        }
    }
    
    private Body createBody(int no, org.deri.mins.terms.Term[] terms, Literal lit){
        String predSym = lit.getPredicateUri();
        boolean positive = !lit.isPositive();
        int arity = 0;
        if (lit.getTerms()!=null) arity=lit.getTerms().length;
        
        if (no==-1){
            if (predSym.equals(org.omwg.logicalexpression.Constants.NUMERIC_ADD) ||
                    predSym.equals(org.omwg.logicalexpression.Constants.NUMERIC_MUL)){
                terms = new org.deri.mins.terms.Term[]{
                    terms[1],terms[2],terms[0]};
            }
            if (predSym.equals(org.omwg.logicalexpression.Constants.NUMERIC_SUB) ||
                    predSym.equals(org.omwg.logicalexpression.Constants.NUMERIC_DIV)){
                terms = new org.deri.mins.terms.Term[]{
                    terms[0],terms[2],terms[1]};
            }
            if (predSym.equals(org.omwg.logicalexpression.Constants.STRING_INEQUAL) ||
                    predSym.equals(org.omwg.logicalexpression.Constants.NUMERIC_INEQUAL) ||
                    predSym.equals(org.omwg.logicalexpression.Constants.INEQUAL)){
                positive = !positive;
            }
            int num = symbTransfomer.minsBuiltIn2No.get(predSym);
            return new BuiltinBody(symbTransfomer.minsBuiltIn2No.get(predSym),
                positive,terms, 
                symbTransfomer.minsBuiltinFunc.get(predSym));
        }
        return new Body(no, positive, terms);
    }

    /**
     * 
     * @param literal
     * @param queryOrRuleContext
     *            a datalog rule or query to keep context for Variable mapping
     * @return
     * @throws ExternalToolException
     */
    private Atom translateLiteral2Atom(Literal literal,
            Object queryOrRuleContext) throws ExternalToolException, UnsupportedFeatureException {

        Term[] args = literal.getTerms();
        org.deri.mins.terms.Term[] minsTerms = new org.deri.mins.terms.Term[args.length];
        Atom atom;

        long predNo = symbTransfomer.convertToTool(literal);

        for (int i = 0; i < args.length; i++) {
            minsTerms[i]=createMinsTerm(args[i],queryOrRuleContext);
        }
        atom = new Atom(minsTerms);
        return atom;
    }
    
    private org.deri.mins.terms.Term createMinsTerm(Term wsmlTerm, Object queryOrRuleContext){
        org.deri.mins.terms.Term minsTerm;
        if (wsmlTerm instanceof Variable) {
            minsTerm = new org.deri.mins.terms.Variable(
                    symbTransfomer.convertToTool(
                            (Variable) wsmlTerm, queryOrRuleContext));
        } else if (wsmlTerm instanceof IRI) {
            minsTerm = new ConstTerm(symbTransfomer
                    .convertToTool((IRI) wsmlTerm));
        } else if (wsmlTerm instanceof ConstructedTerm) {
            ConstructedTerm ct = (ConstructedTerm) wsmlTerm;
            org.deri.mins.terms.Term[] argTerms = new org.deri.mins.terms.Term[ct.getArity()];
            for (int i=0; i<ct.getArity(); i++){
                argTerms[i]=createMinsTerm(ct.getParameter(i),queryOrRuleContext);
            }
            minsTerm = new ConstTerm(
                    symbTransfomer.convertToTool((IRI) ct.getFunctionSymbol()),
                    argTerms);
        }  
        else if (wsmlTerm instanceof SimpleDataValue){
            SimpleDataValue val = (SimpleDataValue)wsmlTerm;
            String type = val.getType().getIRI().toString();
            //System.out.println(type);
            if (type.equals(WsmlDataType.WSML_STRING)){
                minsTerm = new StringTerm(val.toString());
            }else{ //decimal or int
                minsTerm = new NumTerm(Double.parseDouble(val.getValue().toString()));
            }
        } else{
            throw new RuntimeException("No Complex data types yet:" +wsmlTerm);
        }
        return minsTerm;

    }

    /**
     * (non-Javadoc)
     * 
     * @see org.wsml.reasoner.wsmlcore.wrapper.mins.DatalogReasonerFacade#useSymbolFactory(org.wsml.reasoner.wsmlcore.wrapper.mins.SymbolFactory)
     */
    public void useSymbolFactory(
            org.wsml.reasoner.datalog.wrapper.SymbolFactory sf) {
        // symbTransfomer = new SymbolMap(sf);
    }

    /**
     * @see DatalogReasonerFacade#register(String, Set)
     */
    public void register(String ontologyURI, Set<org.wsml.reasoner.builtin.Rule> kb) throws ExternalToolException {
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
        addDataTypeMemberShipRules(minsEngine);
        registeredKbs.put(ontologyURI, minsEngine);
    }
    
    /**
     * @see DatalogReasonerFacade#deregister(String)
     */
    public void deregister(String ontologyURI) throws ExternalToolException {
        registeredKbs.remove(ontologyURI);
        
    }

    private void checkConstraint(RuleSet minsEngine) throws DatalogException,
            ExternalToolException, UnsupportedFeatureException,
            ConstraintViolationError {
        // check contraint query
        List<Literal> cqlits = new LinkedList<Literal>();
        cqlits.add(new Literal(
                        true,
                        PRED_CONSTRAINT,
                        new Term[] { new VariableImpl("query") }));
        Rule constraintQuery = translateQuery(new ConjunctiveQuery(cqlits),
                minsEngine);
//        System.out.println("\n\n"+cqlits+"\n-"+constraintQuery);
        logger.info("Starting MINS evaluation of constraints");
        minsEngine.evaluate();

        Substitution cqsubs = minsEngine.getSubstitution(constraintQuery);
        ConjunctiveQuery violatedQuery = null;
        GroundAtom cqAnswer = cqsubs.First();
//        System.out.println(cqAnswer);
        if (cqAnswer != null) {
            // predicate only has one variable:
            String orgQuery = cqAnswer.terms[0].toString();
            orgQuery = orgQuery.substring(1,orgQuery.length()-1);
            System.out.println("violated"+orgQuery);
            violatedQuery = constraints2Queries.get(orgQuery);
        }
        minsEngine.deleteRule(constraintQuery);

        if (violatedQuery == null) {
            return;
        }

        // We have a constraint violation, give client info about instances:
        Rule violatedMinsQuery = translateQuery(violatedQuery, minsEngine);
        minsEngine.evaluate();
        Substitution vmqsubs = minsEngine.getSubstitution(violatedMinsQuery);
        GroundAtom vmqAnswer = vmqsubs.First();
        String bindings = "";
        for (int i = 0; i < vmqAnswer.terms.length; i++) {
            // terms has length of numbers of variables!
            // not each index has necessarily a subsitution one that does not
            // have is a variable
            if (!(vmqAnswer.terms[i] instanceof Variable)) {
                Variable wsmlVariable = symbTransfomer.convertToWSML(i,
                        violatedQuery);
                Term wsmlTerm = symbTransfomer
                        .convertToWSML(vmqAnswer.terms[i]);
                bindings += wsmlVariable + " -> " + wsmlTerm + "\n";
            }
        }
        throw new ConstraintViolationError(violatedQuery.toString() + "\n"
                + bindings);

    }

    private void addDataTypeMemberShipRules(RuleSet rs){
        WsmoFactory f = WSMO4JManager.getWSMOFactory();
        int memberOfNo = symbTransfomer.convertToTool(
                new Literal(true, WSML2DatalogTransformer.PRED_MEMBER_OF,new Term[2]));
        int integerNo = symbTransfomer.convertToTool(
                f.createIRI(WsmlDataType.WSML_INTEGER));
        int stringNo = symbTransfomer.convertToTool(
                f.createIRI(WsmlDataType.WSML_STRING));
        int decimalNo = symbTransfomer.convertToTool(
                f.createIRI(WsmlDataType.WSML_DECIMAL));
        
        //?x memberOf _integer :- isInteger(?x)
        rs.addRule(new Rule(
                new Head[]{ 
                        new Head(memberOfNo, new org.deri.mins.terms.Term[]{
                                new org.deri.mins.terms.Variable(0),
                                new ConstTerm(integerNo)}
                        )},
                new Body[] {
                        new BuiltinBody(13, false,
                                new org.deri.mins.terms.Term[]{
                                    new org.deri.mins.terms.Variable(0)},
                                new IsInteger()
                        )}));      
        //?x memberOf _String :- isString(?x)
        rs.addRule(new Rule(
                new Head[]{ 
                        new Head(memberOfNo, new org.deri.mins.terms.Term[]{
                                new org.deri.mins.terms.Variable(0),
                                new ConstTerm(stringNo)}
                        )},
                new Body[] {
                        new BuiltinBody(14, false,
                                new org.deri.mins.terms.Term[]{
                                    new org.deri.mins.terms.Variable(0)},
                                new IsString()
                        )}));      
        //?x memberOf _String :- isNum(?x)
        rs.addRule(new Rule(
                new Head[]{ 
                        new Head(memberOfNo, new org.deri.mins.terms.Term[]{
                                new org.deri.mins.terms.Variable(0),
                                new ConstTerm(stringNo)}
                        )},
                new Body[] {
                        new BuiltinBody(12, false,
                                new org.deri.mins.terms.Term[]{
                                    new org.deri.mins.terms.Variable(0)},
                                new IsNum()
                        )}));      
    }

}