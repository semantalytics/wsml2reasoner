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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.deri.mins.Atom;
import org.deri.mins.Body;
import org.deri.mins.DB;
import org.deri.mins.GroundAtom;
import org.deri.mins.Head;
import org.deri.mins.Rule;
import org.deri.mins.RuleSet;
import org.deri.mins.Substitution;
import org.deri.mins.api.DBInterface;
import org.deri.mins.builtins.BuiltinBody;
import org.deri.mins.builtins.BuiltinConfig;
import org.deri.mins.builtins.Equal;
import org.deri.mins.builtins.IsConst;
import org.deri.mins.builtins.IsInteger;
import org.deri.mins.builtins.IsNum;
import org.deri.mins.builtins.IsString;
import org.deri.mins.terms.ConstTerm;
import org.deri.mins.terms.NumTerm;
import org.deri.mins.terms.StringTerm;
import org.deri.mins.terms.concrete.DateTerm;
import org.deri.mins.terms.concrete.IntegerTerm;
import org.deri.wsmo4j.io.parser.wsml.TempVariable;
import org.omwg.logicalexpression.terms.ConstructedTerm;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.ComplexDataValue;
import org.omwg.ontology.SimpleDataValue;
import org.omwg.ontology.Variable;
import org.omwg.ontology.WsmlDataType;
import org.wsml.reasoner.ConjunctiveQuery;
import org.wsml.reasoner.DatalogReasonerFacade;
import org.wsml.reasoner.ExternalToolException;
import org.wsml.reasoner.Literal;
import org.wsml.reasoner.UnsupportedFeatureException;
import org.wsml.reasoner.WSML2DatalogTransformer;
import org.wsml.reasoner.api.InternalReasonerException;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsmo.common.IRI;
import org.wsmo.factory.WsmoFactory;

/**
 * Package: package org.wsml.reasoner.datalog.wrapper.mins;
 * 
 * Author: Darko Anicic, DERI Innsbruck, Holger Lausen, DERI Innsbruck, Uwe
 * Keller, DERI Innsbruck, Date $Date$
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

    private WSMO4JManager wsmoManager;

    private MinsSymbolMap symbTransfomer;
    
    /*
     * 0: Naive Evaluation (only stratifight prorgams)<BR> 
     * 1: Dynamic Filtering Evaluation (only stratifight prorgams)<BR> 
     * 2: Wellfounded  Evaluation with alternating fixed point (juergen says works)<BR> 
     * 3: Wellfounded Evaluation (juergen says probably buggy!)
     * 
     * 3 IS the only one that works! Probably the numbers are 
     * mixed up or Jrgen does not like us
     */
    public int evaluationMethod = 3;

    /**
     * Creates a facade object that allows to invoke the MINS rule system for
     * performing query evaluation tasks.
     */
    public MinsFacade(WSMO4JManager wsmoManager, final Map<String, Object> config) {
        super();
        this.wsmoManager = wsmoManager;
        this.symbTransfomer = new MinsSymbolMap(wsmoManager);
        logger.setLevel(Level.OFF);
        
        // setting the eval method
        final Object evalMethod = config.get(WSMLReasonerFactory.PARAM_EVAL_METHOD);
        if ((evalMethod != null) && (evalMethod instanceof Integer)) {
        	evaluationMethod = (Integer) evalMethod;
        }
    }

    /**
     * Evaluates a Query on a Datalog knowledgebase. The actual evaluation is
     * done by the MINS system.
     * 
     * @throws ExternalToolException
     */
    public Set<Map<Variable, Term>> evaluate(ConjunctiveQuery q,
            String ontologyURI) throws ExternalToolException {

        try {
            // retrieve KB ment for IRI:
            RuleSet minsEngine = registeredKbs.get(ontologyURI);
            if (minsEngine == null){
                throw new InternalReasonerException("No KB with given ID registered "+ontologyURI);
            }
            Rule query = translateQuery(q, minsEngine);

            logger.info("Starting MINS evaluation");
            minsEngine.setEvaluationMethod(evaluationMethod);
            minsEngine.evaluate();
            minsEngine.evalQueries();

            Substitution s = minsEngine.getSubstitution(query);

            Set<Map<Variable, Term>> result = new HashSet<Map<Variable, Term>>();
            GroundAtom a = (GroundAtom) s.First();
            while (a != null) {
                Map<Variable, Term> varBinding = new HashMap<Variable, Term>();
                for (int i = a.terms.length-1; i >=0 ; i--) {
                    // terms has length of numbers of variables!
                    // not each index has necessarily a subsitution one that
                    // does not have is a variable
		    	   if (!(a.terms[i] instanceof Variable)) {
		               Variable wsmlVariable = symbTransfomer.convertToWSML(i,
		                       q);
		               Term wsmlTerm = symbTransfomer
		                       .convertToWSML(a.terms[i]);
		               if (!(wsmlVariable instanceof TempVariable))
		                   varBinding.put(wsmlVariable, wsmlTerm);
		           }
                }
                
                // This if catches the {http://www.wsmo.org/wsml/wsml-syntax#boolean, _boolean("true/false")} 
		    	// binding which is always unnecessarily returned from MINS
                if (varBinding.keySet().size() == 2){
                	List <Term> vals = new ArrayList <Term> (varBinding.values());
                	
                	String key = "http://www.wsmo.org/wsml/wsml-syntax#boolean";
                	String btrue = "_boolean(\"true\")";
                	String bfalse = "_boolean(\"false\")";
                	
                	String v0 = vals.get(0).toString();
                	String v1 = vals.get(1).toString();
                	
                	if (!((v0.equals(key) && (v1.equals(btrue) || v1.equals(bfalse))) || 
                	      (v1.equals(key) && (v0.equals(btrue) || v0.equals(bfalse))))){
                		result.add(varBinding);
                	}
                }
                else{
                	result.add(varBinding);
                }
                
                logger.info("Added new variable binding to result set: "
                        + varBinding);
                a = s.Next();
            }
            return result;
        } catch (UnsupportedFeatureException e) {
            throw new ExternalToolException("MINS cannot handle given query",
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
    private void translateKnowledgebase(Set<org.wsml.reasoner.Rule> p,
            RuleSet minsEngine) throws ExternalToolException,
            UnsupportedFeatureException {
        logger.info("Translate knowledgebase :" + p);
        if (p == null) {
            logger.info("KB is not referenced. Assume empty KB.");
            return;
        }
        for (org.wsml.reasoner.Rule r : p) {
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
    private void translateRule(org.wsml.reasoner.Rule r, RuleSet minsEngine)
            throws ExternalToolException, UnsupportedFeatureException {
        Rule rule;
        GroundAtom groundAtom;
        Body body;
        Body[] bodies;
        Head head;
        int no;
        Atom atom;
        Literal l;
        
        if (r.isConstraint()) {
            System.err.append("Constraints should not appear in translated Datalog Program" +
                    " "+r);
        }

        // Translate head
        l = r.getHead();
        if (l != null) {
            // use predicatesymbol + arity to obtain mins pred number
            no = symbTransfomer.convertToTool(l);
            if (no == -1) {
                throw new RuntimeException(
                        "No BuiltIns Allowed in the Head of a Rule: " + r);
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
                // System.out.println(r+"\n"+rule);
                minsEngine.addRule(rule);
            }
        }
    }

    private Body createBody(int no, org.deri.mins.terms.Term[] terms,
            Literal lit) {
        String predSym = lit.getPredicateUri();
        boolean positive = !lit.isPositive();
        int arity = 0;
        if (lit.getTerms() != null)
            arity = lit.getTerms().length;

        if (no == -1) {
            if (predSym
                    .equals(org.omwg.logicalexpression.Constants.NUMERIC_ADD)
                    || predSym
                            .equals(org.omwg.logicalexpression.Constants.NUMERIC_MUL)) {
                terms = new org.deri.mins.terms.Term[] { terms[1], terms[2],
                        terms[0] };
            }
            if (predSym
                    .equals(org.omwg.logicalexpression.Constants.NUMERIC_SUB)
                    || predSym
                            .equals(org.omwg.logicalexpression.Constants.NUMERIC_DIV)) {
                terms = new org.deri.mins.terms.Term[] { terms[0], terms[2],
                        terms[1] };
            }
            if (predSym
                    .equals(org.omwg.logicalexpression.Constants.STRING_INEQUAL)
                    || predSym
                            .equals(org.omwg.logicalexpression.Constants.NUMERIC_INEQUAL)
                    || predSym
                            .equals(org.omwg.logicalexpression.Constants.INEQUAL)) {
                positive = !positive;
            }
            int num = symbTransfomer.minsBuiltIn2No.get(predSym);
            return new BuiltinBody(symbTransfomer.minsBuiltIn2No.get(predSym),
                    positive, terms, symbTransfomer.minsBuiltinFunc
                            .get(predSym));
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
            Object queryOrRuleContext) throws ExternalToolException,
            UnsupportedFeatureException {

        Term[] args = literal.getTerms();
        org.deri.mins.terms.Term[] minsTerms = new org.deri.mins.terms.Term[args.length];
        Atom atom;

        long predNo = symbTransfomer.convertToTool(literal);

        for (int i = 0; i < args.length; i++) {
            minsTerms[i] = createMinsTerm(args[i], queryOrRuleContext);
        }
        atom = new Atom(minsTerms);
        return atom;
    }

    private org.deri.mins.terms.Term createMinsTerm(Term wsmlTerm,
            Object queryOrRuleContext) {
        org.deri.mins.terms.Term minsTerm;
        if (wsmlTerm instanceof Variable) {
            minsTerm = new org.deri.mins.terms.Variable(symbTransfomer
                    .convertToTool((Variable) wsmlTerm, queryOrRuleContext));
        } else if (wsmlTerm instanceof IRI) {
            minsTerm = new ConstTerm(symbTransfomer
                    .convertToTool((IRI) wsmlTerm));
        } else if (wsmlTerm instanceof ConstructedTerm) {
            ConstructedTerm ct = (ConstructedTerm) wsmlTerm;
            org.deri.mins.terms.Term[] argTerms = new org.deri.mins.terms.Term[ct
                    .getArity()];
            for (int i = 0; i < ct.getArity(); i++) {
                argTerms[i] = createMinsTerm(ct.getParameter(i),
                        queryOrRuleContext);
            }
            minsTerm = new ConstTerm(symbTransfomer.convertToTool((IRI) ct
                    .getFunctionSymbol()), argTerms);
        } else if (wsmlTerm instanceof SimpleDataValue) {
            SimpleDataValue val = (SimpleDataValue) wsmlTerm;
            String type = val.getType().getIRI().toString();
            // System.out.println(type);
            if (type.equals(WsmlDataType.WSML_STRING)) {
                minsTerm = new StringTerm(val.toString());
            }else if (type.equals(WsmlDataType.WSML_INTEGER)) {
                minsTerm = new IntegerTerm(Integer.parseInt(val.toString()));
            } else { // decimal or int
                //TODO create decimal term in mins
                minsTerm = new NumTerm(Double.parseDouble(val.getValue()
                        .toString()));
            }
        } else {
            ComplexDataValue val = (ComplexDataValue) wsmlTerm;
            String type = val.getType().getIRI().toString();
            int arity = val.getArity();
            if (type.equals(WsmlDataType.WSML_BOOLEAN)) {
                minsTerm = new StringTerm(val.toString());
            } else if (type.equals(WsmlDataType.WSML_DATE)){
                //MINS ONLY support 3 ints, variables to be handled :(
                int year = ((BigInteger)val.getArgumentValue((byte)0).getValue()).intValue();
                int month = ((BigInteger)val.getArgumentValue((byte)1).getValue()).intValue();
                int day = ((BigInteger)val.getArgumentValue((byte)2).getValue()).intValue(); 
                minsTerm = new DateTerm(year, month, day);
                
            } else {
                throw new RuntimeException("No Complex data types yet:"
                        + wsmlTerm);
            }
        }
        return minsTerm;

    }

    /**
     * (non-Javadoc)
     * 
     * @see org.wsml.reasoner.wsmlcore.wrapper.mins.DatalogReasonerFacade#useSymbolFactory(org.wsml.reasoner.wsmlcore.wrapper.mins.SymbolFactory)
     */
    public void useSymbolFactory(
            org.wsml.reasoner.builtin.wrapper.SymbolFactory sf) {
        // symbTransfomer = new SymbolMap(sf);
    }

    /**
     * @see DatalogReasonerFacade#register(String, Set)
     */
    public void register(String ontologyURI, Set<org.wsml.reasoner.Rule> kb)
            throws ExternalToolException {
        // Set up an instance of a MINS engine
        BuiltinConfig builtInConfig = new BuiltinConfig();
        DBInterface db = new DB(); // facts stored in RAM
        RuleSet minsEngine = new RuleSet(builtInConfig, db);
        minsEngine.debuglevel = 0;

        // System.out.println(kb);

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

    private void addDataTypeMemberShipRules(RuleSet rs) {
        WsmoFactory f = wsmoManager.getWSMOFactory();
        int memberOfNo = symbTransfomer.convertToTool(new Literal(true,
                WSML2DatalogTransformer.PRED_MEMBER_OF, new Term[2]));
        int integerNo = symbTransfomer.convertToTool(f
                .createIRI(WsmlDataType.WSML_INTEGER));
        int iriNo = symbTransfomer.convertToTool(f
                .createIRI(WsmlDataType.WSML_IRI));
        int stringNo = symbTransfomer.convertToTool(f
                .createIRI(WsmlDataType.WSML_STRING));
        int decimalNo = symbTransfomer.convertToTool(f
                .createIRI(WsmlDataType.WSML_DECIMAL));
        int booleanNo = symbTransfomer.convertToTool(f
                .createIRI(WsmlDataType.WSML_BOOLEAN));

        // ?x memberOf _integer :- isInteger(?x)
        rs.addRule(new Rule(
                        new Head[] { new Head(memberOfNo,
                                new org.deri.mins.terms.Term[] {
                                        new org.deri.mins.terms.Variable(0),
                                        new ConstTerm(integerNo) }) },
                        new Body[] { new BuiltinBody(
                                13,
                                false,
                                new org.deri.mins.terms.Term[] { new org.deri.mins.terms.Variable(
                                        0) }, new IsInteger()) }));
        
        // ?x memberOf _String :- isString(?x)
        rs.addRule(new Rule(
                        new Head[] { new Head(memberOfNo,
                                new org.deri.mins.terms.Term[] {
                                        new org.deri.mins.terms.Variable(0),
                                        new ConstTerm(stringNo) }) },
                        new Body[] { new BuiltinBody(
                                14,
                                false,
                                new org.deri.mins.terms.Term[] { new org.deri.mins.terms.Variable(
                                        0) }, new IsString()) }));
        
        // ?x memberOf _decimal :- isNum(?x)
        rs.addRule(new Rule(
                        new Head[] { new Head(memberOfNo,
                                new org.deri.mins.terms.Term[] {
                                        new org.deri.mins.terms.Variable(0),
                                        new ConstTerm(decimalNo) }) },
                        new Body[] { new BuiltinBody(
                                12,
                                false,
                                new org.deri.mins.terms.Term[] { new org.deri.mins.terms.Variable(
                                        0) }, new IsNum()) }));

        // ?x memberOf wsml#boolean :- ?x = org.deri.mins.terms.concrete.BooleanTerm[value=true]
        // REPLACED with ?x memberOf wsml#boolean :- isString(?x) , ?x = "_boolean("true")
        rs.addRule(new Rule(
                        new Head[] { new Head(memberOfNo,
                                new org.deri.mins.terms.Term[] {
                                        new org.deri.mins.terms.Variable(0),
                                        new ConstTerm(booleanNo) }) },
                        new Body[] {
                                new BuiltinBody(
                                        6,
                                        false,
                                        new org.deri.mins.terms.Term[] {
                                                new org.deri.mins.terms.Variable(
                                                        0),
                                                new StringTerm("_boolean(\"true\")") },
                                        new Equal()) }));

        // ?x memberOf wsml#boolean :- ?x = org.deri.mins.terms.concrete.BooleanTerm[value=false]
        // REPLACED with ?x memberOf wsml#boolean :- isString(?x) , ?x = "_boolean("false")
        rs.addRule(new Rule(
                        new Head[] { new Head(memberOfNo,
                                new org.deri.mins.terms.Term[] {
                                        new org.deri.mins.terms.Variable(0),
                                        new ConstTerm(booleanNo) }) },
                        new Body[] {
                                new BuiltinBody(
                                        6,
                                        false,
                                        new org.deri.mins.terms.Term[] {
                                                new org.deri.mins.terms.Variable(
                                                        0),
                                                  new StringTerm("_boolean(\"false\")") },
                                        new Equal()) }));
        
        // ?x memberOf _integer :- isConst(?x)
        rs.addRule(new Rule(
                new Head[] { new Head(memberOfNo,
                    new org.deri.mins.terms.Term[] {
                            new org.deri.mins.terms.Variable(0),
                            new ConstTerm(iriNo) }) },
                new Body[] { new BuiltinBody(
                    15,
                    false,
                    new org.deri.mins.terms.Term[] { new org.deri.mins.terms.Variable(
                            0) }, new IsConst()) }));
    }

	public boolean checkQueryContainment(ConjunctiveQuery query1,
			ConjunctiveQuery query2, String ontologyURI){
		throw new UnsupportedOperationException("This method is not implemented");
	}
	
	public Set<Map<Variable, Term>> getQueryContainment(ConjunctiveQuery query1,
			ConjunctiveQuery query2, String ontologyURI){
		throw new UnsupportedOperationException("This method is not implemented");
	}

}