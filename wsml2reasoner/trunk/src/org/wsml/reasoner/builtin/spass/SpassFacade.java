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
package org.wsml.reasoner.builtin.spass;

import java.util.*;

import org.apache.log4j.Logger;
import org.omwg.logicalexpression.Atom;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.terms.ConstructedTerm;
import org.omwg.logicalexpression.terms.Term;
import org.wsml.reasoner.ExternalToolException;
import org.wsml.reasoner.FOLAbstractFacade;
import org.wsml.reasoner.PredicateAndFSymbolCollector;
import org.wsml.reasoner.api.WSMLFOLReasoner.EntailmentType;
import org.wsml.reasoner.builtin.tptp.TPTPSymbolMap;
import org.wsml.reasoner.impl.WSMO4JManager;

/**
 * <p>
 * The wsmo4j interface to and from TPTP
 * </p>
 * <p>
 * $Id: SpassFacade.java,v 1.1 2007-08-10 09:44:49 graham Exp $
 * </p>
 * 
 * @author Holger Lausen
 * @version $Revision: 1.1 $
 */
public class SpassFacade extends FOLAbstractFacade {

    Logger log = Logger.getLogger(SpassFacade.class);
    
    
    private PredicateAndFSymbolCollector collector = new PredicateAndFSymbolCollector();

    public SpassFacade(WSMO4JManager manager, String endpoint){
    	super(manager, endpoint);
    }

    @Override
    public List<EntailmentType> checkEntailment(String ontologyIRI,List<LogicalExpression> conjecture) {
        String ontology = convertedOntologies.get(ontologyIRI);
        if (ontology==null) throw new RuntimeException("ontology not registered");
        TPTPSymbolMap map = symbolMaps.get(ontologyIRI);
        if (map==null) throw new RuntimeException("Could not find a symbolmap for iri, error in conversion");
        
        List<EntailmentType> results = new ArrayList<EntailmentType>();
        for (LogicalExpression le : conjecture) {
            String conjectureString = getConjecture(le,map);
            collector.clear();
            le.accept(collector);
            ontology = ontology.replace("<REPLACE_ME_WITH_MORE_PREDICATES>", 
            		getPredicates(map));
            ontology = ontology.replace("<REPLACE_ME_WITH_MORE_FUNCTIONS>", 
            		getFunctions(map));
            log.debug("checking conjecture:" +conjectureString);
            log.debug("\n\n"+ontology + "\n" + conjectureString);
            results.add(invokeHttp(ontology + "\n" + conjectureString));
        }
        return results;
    }
    
    @Override
	public String getConjecture(LogicalExpression le, TPTPSymbolMap map) {
    	SpassSerializeVisitor les = new SpassSerializeVisitor();
    	les.setSymbolMap(map);
    	String conjecture = "list_of_formulae(conjectures).\n";
    	le.accept(les);
    	conjecture += "formula("+les.getSerializedObject()+").\n";
    	conjecture += "end_of_list.\n";
    	conjecture += "end_problem.\n";
    	return conjecture;
    }
    
    
    public void register(String ontologyURI, Set<LogicalExpression> expressions) throws ExternalToolException {
        String kb ="";
    	kb += "begin_problem(WSML_Problem).\n\n";
    	kb += getSpassMetaDisc();
    	kb += "list_of_symbols.\n";
    	
    	SpassSerializeVisitor les = new SpassSerializeVisitor();
        
    	String allAxioms ="";
        for (LogicalExpression le:expressions ){
            le.accept(les);
            String newExpression = les.getSerializedObject();
        	newExpression = "formula ("+newExpression +"). \n";
        	allAxioms+=newExpression;
        }
    	TPTPSymbolMap symmap = les.getSymbolMap();
    	
    	collector.clear();
    	for (LogicalExpression le:expressions){
    		le.accept(collector);
    	}

    	kb += "functions[(succ,1),(minus,2),(plus,2),(times,2),(0,0)";
    	kb += getFunctions(symmap);
    	kb += "<REPLACE_ME_WITH_MORE_FUNCTIONS>].\n";
    			
    	kb +="predicates[(greater,2),(greatereq,2),(less,2),(lesseq,2)";
    	kb += getPredicates(symmap);
    	kb += "<REPLACE_ME_WITH_MORE_PREDICATES>].\n";
    	kb += "end_of_list.\n" ;
    	kb += "list_of_formulae(axioms).\n";
    	kb += getSpassStandardFormulas()+"\n";
    	kb += allAxioms;
    	kb += "end_of_list.\n";
//    	log.debug("REGISTERED KB: "+ontologyURI+"\n"+kb);
        convertedOntologies.put(ontologyURI, kb);
        symbolMaps.put(ontologyURI,symmap);
    }

	/**
	 * @param kb
	 * @param symmap
	 * @return
	 */
	private String getPredicates(TPTPSymbolMap symmap) {
		String kb = "";
		for (Atom a: collector.getAtoms()){
    		kb += ",("+symmap.getTPTPTerm(a.getIdentifier().toString())+","+
    			a.getArity()+")";
    	}
		return kb;
	}

	/**
	 * @param kb
	 * @param symmap
	 * @return
	 */
	private String getFunctions(TPTPSymbolMap symmap) {
		String kb ="";
		for (ConstructedTerm c : collector.getFSyms()){
    		kb += ",("+c.getFunctionSymbol()+","+c.getArity()+")";
    	}
    	for (Term t: collector.getConstants()){
    		kb += ",("+symmap.getTPTPTerm(t.toString())+",0)";
    	}
		return kb;
	}
    
    public String getSpassStandardFormulas(){
    	return "formula(forall([X,Y],equal(plus(minus(X,Y),Y),X))).\n" +
    			"formula(forall([X],equal(plus(X,0),X))).\n" +
    			"formula(forall([X],equal(plus(0,X),X))).\n" +
    			"formula(forall([X],less(X,plus(X,1)))).\n" +
    			"formula(forall([X,Y],implies(less(X,Y),lesseq(X,Y)))).\n" +
    			"formula(forall([X],lesseq(X,X))).\n" +
    			"formula(forall([X,Y],equiv(less(X,Y),greater(Y,X)))).\n" +
    			"formula(forall([X,Y],equiv(lesseq(X,Y),greatereq(Y,X)))).\n" +
    			"formula(forall([X,Y],not(and(less(X,Y),greatereq(X,Y))))).\n" +
    			"formula(forall([X,Y],not(and(lesseq(X,Y),greater(X,Y))))).\n" +
    			"formula(forall([X,Y],implies(and(lesseq(X,Y),lesseq(Y,X)),equal(X,Y)))).\n" +
    			"formula(forall([X],equal(uminus(X),minus(0,X)))).\n" +
    			"formula(forall([X],equal(times(X,0),0))).\n" +
    			"formula(forall([X],equal(times(0,X),0))).\n" +
    			"formula(forall([X],equal(times(X,1),X))).\n" +
    			"formula(forall([X],equal(times(1,X),X))).\n" +
    			"formula(forall([X],equal(divide(X,1),X))).\n" +
    			"formula(forall([X],equal(remainder(X,1),0))).";
    }
    
    public String getSpassMetaDisc(){
    	return "list_of_descriptions.\n" +
				"name({*WSML_PROBLEM*}).\n" +
				"author({*[ Source   :]*}).\n" +
				"status(unknown).\n" +
				"description({*[ Refs     :]*}).\n" +
				"end_of_list.\n" +
				"" +
				"list_of_settings(SPASS).\"" +
				"{*\n" +
				"  set_flag(PDer,0).\n" +
				"  set_flag(PEmptyClause,1).\n" +
				"  set_flag(Splits,0).\n" +
				"  set_flag(Sorts,0).\n" +
				"  set_flag(RArith,1).\n" +
				"  set_flag(IThI,2).\n" +
				"  set_flag(IIOE,1).\n" +
				"  set_flag(DocSplit,0).\n" +
				"  set_flag(DocProof,0).\n" +
				"  set_flag(DpcProver,2).\n" +
				"  set_flag(DpcTheory,1).\n" +
				"  set_precedence(plus,succ,times,greater,greatereq,less,lesseq).\n" +
				"  set_TheorySym(greater,greatereq,less,lesseq,succ,minus,plus,times).\n#" +
				"*}\n" +
				"end_of_list.\n" +
				"\n";
    }
}
