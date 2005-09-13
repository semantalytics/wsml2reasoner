package org.wsml.reasoner.datalog.wrapper.mins;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.wsml.reasoner.api.queryanswering.VariableBinding;
import org.wsml.reasoner.datalog.ConjunctiveQuery;
import org.wsml.reasoner.datalog.Constant;
import org.wsml.reasoner.datalog.DataTypeValue;
import org.wsml.reasoner.datalog.Literal;
import org.wsml.reasoner.datalog.Predicate;
import org.wsml.reasoner.datalog.Program;
import org.wsml.reasoner.datalog.QueryResult;
import org.wsml.reasoner.datalog.wrapper.DatalogReasonerFacade;
import org.wsml.reasoner.datalog.wrapper.DefaultSymbolFactory;
import org.wsml.reasoner.datalog.wrapper.ExternalToolException;
import org.wsml.reasoner.datalog.wrapper.SymbolFactory;
import org.wsml.reasoner.datalog.wrapper.SymbolMap;
import org.wsml.reasoner.datalog.wrapper.UnsupportedFeatureException;
import org.wsml.reasoner.impl.VariableBindingImpl;

import com.ontoprise.builtins.Add;
import com.ontoprise.builtins.Builtin;
import com.ontoprise.builtins.BuiltinConfig;
import com.ontoprise.builtins.Equal;
import com.ontoprise.builtins.Evaluable;
import com.ontoprise.builtins.Greater;
import com.ontoprise.builtins.Greaterorequal;
import com.ontoprise.builtins.IsConst;
import com.ontoprise.builtins.IsNum;
import com.ontoprise.builtins.IsString;
import com.ontoprise.builtins.Less;
import com.ontoprise.builtins.Lessorequal;
import com.ontoprise.inference.DB;
import com.ontoprise.inference.DBInterface;
import com.ontoprise.inference.prolog.Atom;
import com.ontoprise.inference.prolog.Body;
import com.ontoprise.inference.prolog.GroundAtom;
import com.ontoprise.inference.prolog.Head;
import com.ontoprise.inference.prolog.Rule;
import com.ontoprise.inference.prolog.RuleSet;
import com.ontoprise.inference.prolog.Substitution;
import com.ontoprise.inference.prolog.terms.ConstTerm;
import com.ontoprise.inference.prolog.terms.Term;
import com.ontoprise.inference.prolog.terms.Variable;


public class NewMinsFacade implements DatalogReasonerFacade {
    private Logger logger = Logger.getLogger("org.wsml.reasoner.wsmlcore.wrapper.mins");
    /** Here we store a MINS Engine which contains the compiled KB for each registered ontology */
    private Map<String,RuleSet> registeredKbs = new HashMap<String,RuleSet>();
    private SymbolMap symbTransfomer = new SymbolMap(new DefaultSymbolFactory());
    //private org.wsml.reasoner.datalog.Query query;
      
    /** 
     * Creates a facade object that allows to invoke the MINS rule system
     * for performing query evaluation tasks.
     */
    public NewMinsFacade(){
       super();
       logger.setLevel(Level.OFF);        
    }
    
    /**
     * Evaluates a Query on a Datalog knowledgebase.
     * The actual evaluation is done by the MINS system.
     * 
     * @throws ExternalToolException 
     */
    public QueryResult evaluate(ConjunctiveQuery q, String ontologyIRI, int evaluationMethod) throws ExternalToolException {
    	VariableBinding varBinding;
    	String varName;
    	String varValue;
    	
    	try {
            QueryResult result = new QueryResult(q);
            // retrieve KB ment for IRI:
            RuleSet minsEngine = registeredKbs.get(ontologyIRI);
            // remove already contained queries in the KB
            minsEngine.deleteQueries();
            //minsEngine.ClearRuleSet();
            //query = q;
     
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
            
            /*// for all queries in KB
            Iterator<Vector> queries = v.iterator();
            if (v.size() > 1) {
                // Should not happen, since we only have a KB which contains a single query.
                throw new RuntimeException("We unexpectetly have a MINS engine which contains multiple queries at once!");
            }
            //we only have one query
*/            
            logger.info("Computing substitutions");
            for (Rule query = minsEngine.NextQuery(null); query != null; query = minsEngine.NextQuery(query)) {
            	System.out.println("Query: " + query.toString() + "-Evaluation;");
            	Substitution s = minsEngine.Substitution(query);
                Enumeration enm = s.elements();
                
                System.out.println("...enm...: " + enm);
                
                while (enm.hasMoreElements()) {
                    GroundAtom a = (GroundAtom) enm.nextElement();
                    System.out.println("...a.terms.length...: " + a.terms.length);
                    for (int i = 0; i < a.terms.length; i++) {
                        System.out.print("X" + i + " = " + a.terms[i]);
                        if (i < a.terms.length - 1) {
                            System.out.print(", ");
                        }
                        varBinding = new VariableBindingImpl();
                        varName = symbTransfomer.convertToWSML(i, 0);
                        //String varValue = symbTransfomer.convertToWSML(a.terms[i], 1);
                        varValue = a.terms[i].toString();
                        varBinding.put(varName, varValue);
                       
                        result.getVariableBindings().add(varBinding);
                        logger.info("Added new variable binding to result set: " + varBinding);
                    }
                    System.out.println();
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
     * @param p - the datalog program that constitutes the knowledgebase
     * @throws UnsupportedFeatureException 
     */
    private void translateKnowledgebase(Program p, RuleSet minsEngine) throws ExternalToolException, UnsupportedFeatureException {
        logger.info("Translate knowledgebase :" + p);

        if (p == null) {logger.info("KB is not referenced. Assume empty KB."); return;} 
        
        for (org.wsml.reasoner.datalog.Rule r : p) {
            translateRule(r, minsEngine);
        }
    }

    /**
     * Translate the query
     * @param p - the datalog program that constitutes the knowledgebase
     * @return an object that represents the same query in the Mandrax system
     * @throws UnsupportedFeatureException 
     */
    private void translateQuery(ConjunctiveQuery q, RuleSet minsEngine) throws ExternalToolException, UnsupportedFeatureException {
        Atom atom;
        String strNo;
        Body body;
        Rule query;
        Body[] bodies;
        
        // Care about body
        List<Literal> qBody = q.getLiterals();
        bodies = new Body[qBody.size()];
        for(int i=0; i<qBody.size(); i++){
        	atom = translateLiteral2Atom(qBody.get(i)); 
        	
        	strNo = Long.toString(atom.no);
        	int no = Integer.parseInt(strNo);
        	//System.out.println("no: " + no);
        	
        	body = new Body(no, false, atom.terms);
        	bodies[i] = body;
        }
        query = new Rule(null, bodies);
        
        //System.out.println("query: " + query.toString());
    
	    minsEngine.AddRule(query);
    }
    
    /**
     * Translate a datalog rule
     * @throws UnsupportedFeatureException 
     */
    private void translateRule(org.wsml.reasoner.datalog.Rule r, RuleSet minsEngine) throws ExternalToolException, UnsupportedFeatureException {
        Rule rule;
        GroundAtom groundAtom;
    	Body body;
    	Body[] bodies;
        Head head;
        Predicate p;
        Literal l;
        int predCnt;
        //Term[] terms;
        Atom atom;
        String strNo;
        
        System.out.println("r: " + r.toString());
        
        if (r.isConstraint()) {
            // Simply ignore constraints 
            return;
            
            // TODO handle constraints properly
        } 
        
        // Translate head
        p = r.getHead().getSymbol();
        predCnt = symbTransfomer.convertToTool(p);
        
        //terms = translateLiteral2Term(r.getHead());
        atom = translateLiteral2Atom(r.getHead());
        //head = new Head(predCnt, terms);
        head = new Head(predCnt, atom.terms);
        
        // Care about body
        List<Literal> rBody = r.getBody();

        if (rBody.size() == 0){
            // rule is actually a fact
        	groundAtom = new GroundAtom(atom.terms);
        	minsEngine.AddFact(predCnt, groundAtom);
        } else {
        	bodies = new Body[rBody.size()];
            for(int i=0; i<rBody.size(); i++){
            	atom = translateLiteral2Atom(rBody.get(i)); 
            	
            	strNo = Long.toString(atom.no);
            	int no = Integer.parseInt(strNo);
            	//System.out.println("no: " + no);
            	
            	body = new Body(no, false, atom.terms);
            	bodies[i] = body;
            }
            rule = new Rule(new Head[] {head}, bodies);
            
            System.out.println("rule: " + rule.toString());
            
            minsEngine.AddRule(rule);
        }
    }
    
    private Term[] translateLiteral2Term(Literal l) throws ExternalToolException, UnsupportedFeatureException {
        Term[] term;
        Variable variable;
        ConstTerm constant;
        ArrayList terms = new ArrayList();
        
        org.wsml.reasoner.datalog.Term[] args = l.getArguments();
        int symbName;
        String argString ="";
        for (org.wsml.reasoner.datalog.Term arg : args){
            if (arg instanceof org.wsml.reasoner.datalog.Variable) {
            	variable = new Variable(symbTransfomer.convertToTool((org.wsml.reasoner.datalog.Variable) arg));
            	terms.add(variable);
            }
            else if (arg instanceof Constant) {
            	constant = new ConstTerm(symbTransfomer.convertToTool((Constant) arg));
            	terms.add(constant);
            }
            else if (arg instanceof DataTypeValue) {
                //symbName = symbTransfomer.convertToTool((DataTypeValue) arg);
                //TODO: DataTypes unclear!
            }else {
                throw new RuntimeException("Uwe says we will not arrive here.");
            }
        }
        
        return (Term[]) (terms.toArray());
    }
    
    private Atom translateLiteral2Atom(Literal l) throws ExternalToolException, UnsupportedFeatureException {
    	org.wsml.reasoner.datalog.Term[] args = l.getArguments();
        Term[] terms = new Term[args.length];
        Atom atom;
        
        for (int i=0; i<args.length; i++){  
        	if (args[i] instanceof org.wsml.reasoner.datalog.Variable) {
        		terms[i] = new Variable(symbTransfomer.convertToTool((org.wsml.reasoner.datalog.Variable) args[i]));
        		/*System.out.println("Variable-args[i]: " + args[i]);
        		System.out.println("Variable-terms[i]: " + terms[i]);*/
            }
            else if (args[i] instanceof Constant) {
            	terms[i] = new ConstTerm(symbTransfomer.convertToTool((Constant) args[i]));
            	/*System.out.println("Constant-args[i]: " + args[i]);
        		System.out.println("Constant-terms[i]: " + terms[i]);*/
            }
            else if (args[i] instanceof DataTypeValue) {
                //terms[i] = symbTransfomer.convertToTool((DataTypeValue) arg);
            	//TODO: DataTypes unclear!
            }else {
                throw new RuntimeException("Uwe says we will not arrive here.");
            }
        }
        //System.out.println("Literal converted to Atom.");
        atom = new Atom(terms);
        return atom;
    }

    /* (non-Javadoc)
     * @see org.wsml.reasoner.wsmlcore.wrapper.DatalogReasonerFacade#useSymbolFactory(org.wsml.reasoner.wsmlcore.wrapper.SymbolFactory)
     */
    public void useSymbolFactory(SymbolFactory sf) {
        symbTransfomer = new SymbolMap(sf);
        
    }

    public void register(String ontologyIRI, Program kb) throws ExternalToolException {
        //Set up an instance of a MINS engine
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
                new Builtin("between", 3, new com.ontoprise.builtins.Between()),
                new Builtin("starts", 2, new com.ontoprise.builtins.Starts()),
                new Builtin("ends", 2, new com.ontoprise.builtins.Ends()),
                new Builtin("lives", 2, new com.ontoprise.builtins.Lives()) };

        BuiltinConfig builtInConfig = new BuiltinConfig(buildInList);
        DBInterface db = new DB(); // facts stored in RAM
        RuleSet minsEngine = new RuleSet(builtInConfig, db);
        minsEngine.debuglevel = 0;
        
//      Translate (resp. Transfer) the knowledge base to MINS 
        try {
            translateKnowledgebase(kb, minsEngine);
        } catch (UnsupportedFeatureException e) {
            e.printStackTrace();
            throw new ExternalToolException("Unsupported feature for MINS in knowledgebase.");
        }
        registeredKbs.put(ontologyIRI, minsEngine);
    }
}
