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
package org.wsml.reasoner.builtin.tptp;

import java.util.Set;

import org.apache.log4j.Logger;
import org.omwg.logicalexpression.Atom;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.terms.ConstructedTerm;
import org.wsml.reasoner.ExternalToolException;
import org.wsml.reasoner.impl.WSMO4JManager;

/**
 * <p>
 * The wsmo4j interface to and from TPTP
 * </p>
 * <p>
 * $Id: SpassPlusTFacade.java,v 1.1 2007-06-14 16:38:59 hlausen Exp $
 * </p>
 * 
 * @author Holger Lausen
 * @version $Revision: 1.1 $
 */
public class SpassPlusTFacade extends FOLAbstractFacade {

    Logger log = Logger.getLogger(SpassPlusTFacade.class);
    SpassPlusTSerializeVisitor les = new SpassPlusTSerializeVisitor();
    
    public SpassPlusTFacade(WSMO4JManager manager, String endpoint){
    	super(manager, endpoint);
    }

    @Override
    String getConjecture(LogicalExpression le) {
    	String conjecture = "list_of_formulae(conjectures).\n";
    	le.accept(les);
    	conjecture += "formula("+les.getSerializedObject()+").\n";
    	conjecture += "end_of_list.\n";
    	conjecture += "end_problem.\n";
    	return conjecture;
    }
    
    private PredicateAndFSymbolCollector collector = new PredicateAndFSymbolCollector();

    public void register(String ontologyURI, Set<LogicalExpression> expressions) throws ExternalToolException {
        String kb ="";
    	kb += "begin_problem(WSML_Problem).\n\n";
    	kb += getSpassMetaDisc();

    	kb += "list_of_symbols.\n";
    	
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
    	for (ConstructedTerm c : collector.getFSyms()){
    		kb += ",("+c.getFunctionSymbol()+","+c.getArity()+")";
    	}
    	kb += "].\n";
    			
    	kb+="predicates[(greater,2),(greatereq,2),(less,2),(lesseq,2)";
    	for (Atom a: collector.getAtoms()){
    		kb += ",("+
    			symmap.getTPTPTerm(a.getIdentifier().toString())+","+
    			a.getArity()+")";
    	}
    	kb += "].\n";
    	
    	kb += "end_of_list.\n" ;
    	kb += "list_of_formulae(axioms).\n";
    	kb += getSpassStandardFormulas()+"\n";
        
    	kb += allAxioms;
    	
    	kb += "end_of_list.\n";
    	log.debug("REGISTERED KB: "+ontologyURI+"\n"+kb);
        convertedOntologies.put(ontologyURI, kb);
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
				"end_of_list.\n";
    }
}
