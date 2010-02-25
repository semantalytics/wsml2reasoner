/**
 * WSML Reasoner Implementation.
 *
 * Copyright (c) 2006, University of Innsbruck, Austria.
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
package org.wsml.reasoner.builtin.xsb;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.ConjunctiveQuery;
import org.wsml.reasoner.DatalogReasonerFacade;
import org.wsml.reasoner.ExternalToolException;
import org.wsml.reasoner.Literal;
import org.wsml.reasoner.UnsupportedFeatureException;
import org.wsml.reasoner.api.exception.InternalReasonerException;
import org.wsmo.factory.FactoryContainer;

import com.declarativa.interprolog.TermModel;

/**
 * Package: package org.wsml.reasoner.datalog.wrapper.mins;
 * 
 * Author: Holger Lausen, DERI Innsbruck $Date: 2007-11-12 16:59:44 $
 */
public class XSBFacade implements DatalogReasonerFacade {
    private Logger logger = Logger.getLogger("org.wsml.reasoner.builtin.xsb.XSBFacade");

    /**
     * Here we store a MINS Engine which contains the compiled KB for each
     * registered ontology
     */
    private String program;

    private FactoryContainer wsmoManager;

    private XSBSymbolMap symbTransfomer;

    /**
     * Creates a facade object that allows to invoke the MINS rule system for
     * performing query evaluation tasks.
     */
    public XSBFacade(FactoryContainer factory, Map<String, Object> config) {
        super();
        this.wsmoManager = factory;
        this.symbTransfomer = new XSBSymbolMap(factory);
    }

    /**
     * Evaluates a Query on a Datalog knowledgebase. The actual evaluation is
     * done by the MINS system.
     * 
     * @throws ExternalToolException
     */
    public Set<Map<Variable, Term>> evaluate(ConjunctiveQuery q) throws ExternalToolException {

        try {
            // retrieve KB ment for IRI:
            if (program == null){
                throw new InternalReasonerException("XSB Reasoner not initialised");
            }
            String query = translateQuery(q);

            logger.info("Starting XSB evaluation");
            XSBQuery xsb = new XSBQuery();
            Set<Map<String,TermModel>> xsbresult = xsb.query(query, program);
            Set<Map<Variable, Term>> wsmlResult = new HashSet<Map<Variable, Term>>();
            
            for (Map<String,TermModel> subst:xsbresult) {
                Map<Variable, Term> varBinding = new HashMap<Variable, Term>();
                for (String xsbVar :subst.keySet()){
                    Variable wsmlVar = wsmoManager.getLogicalExpressionFactory().createVariable(xsbVar);
                    Term wsmlTerm = symbTransfomer.convertToWSML(subst.get(xsbVar));
                    varBinding.put(wsmlVar, wsmlTerm);
                }
                wsmlResult.add(varBinding);
                logger.info("Added new variable binding to result set: "
                        + varBinding);
            }
            return wsmlResult;
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
    private String translateKnowledgebase(Set<org.wsml.reasoner.Rule>p) {
        logger.info("Translate knowledgebase :" + p);
        if (p == null) {
            logger.info("KB is not referenced. Assume empty KB.");
            return null;
        }
        StringBuffer buf = new StringBuffer();
        buf.append("numericAdd(RESULT,X,Y) :- RESULT is X+Y .\n");
        buf.append("cARD(A,B,C).\n");
        for (org.wsml.reasoner.Rule r : p) {
            buf.append(translateRule(r));
        }
        return buf.toString();
    }

    private final static String xsbAND =",";
    /**
     * Translate the query
     * 
     * @param p -
     *            the datalog program that constitutes the knowledgebase
     * @return an object that represents the same query in the Mandrax system
     * @throws UnsupportedFeatureException
     */
    private String translateQuery(ConjunctiveQuery query)
            throws ExternalToolException, UnsupportedFeatureException {
        StringBuffer buf = new StringBuffer();
        for (Literal l:query.getLiterals()){
            buf.append(translateLiteral(l));
            buf.append(xsbAND);
        }
        buf.deleteCharAt(buf.length()-1);
        return buf.toString();
    }

    
    private String translateLiteral(Literal l){
        StringBuffer buf = new StringBuffer();
        if (l != null) {
            if (!l.isPositive()){
                buf.append("tnot(");
            }
            buf.append(symbTransfomer.uri2String(l.getPredicateUri()) + "(");
            for (Term t: l.getTerms()){
                buf.append(symbTransfomer.term2String(t)+",");
            }
            buf.deleteCharAt(buf.length()-1);
            if (l.getTerms().length!=0) buf.append(")");
            if (!l.isPositive()){
                buf.append(")");
            }
            
        }
        return buf.toString();
    }
    
    /**
     * Translate a datalog rule
     * 
     * @throws UnsupportedFeatureException
     */
    private String translateRule(org.wsml.reasoner.Rule r) {
        StringBuffer buf = new StringBuffer();
        // Translate head
        buf.append(translateLiteral(r.getHead()));

        if (r.getBody().size() == 0) {// rule is actually a fact
            buf.append(".\n");
            return buf.toString();
        } else { //translater body
            buf.append(" :- ");
            for (Literal bd:r.getBody()){
                buf.append(translateLiteral(bd));
                buf.append(",");
            }
            buf.deleteCharAt(buf.length()-1);
            return buf.toString()+".\n";
        }
    }

    /**
     * @see DatalogReasonerFacade#register(String, Set)
     */
    public void register(Set<org.wsml.reasoner.Rule> kb)
            throws ExternalToolException {
        program = translateKnowledgebase(kb);
    }

    /**
     * @see DatalogReasonerFacade#deregister(String)
     */
    public void deregister() throws ExternalToolException {
        program = null;
    }

	public boolean checkQueryContainment(ConjunctiveQuery query1, ConjunctiveQuery query2){
		throw new UnsupportedOperationException("This method is not implemented");
	}

	public Set<Map<Variable, Term>> getQueryContainment(ConjunctiveQuery query1, ConjunctiveQuery query2) {
		throw new UnsupportedOperationException("This method is not implemented");
	}
}