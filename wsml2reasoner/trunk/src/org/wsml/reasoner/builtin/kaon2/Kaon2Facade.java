/**
 * WSML Reasoner based on KAON2
 *
 * Copyright (c) 2005, FZI, Germany
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

package org.wsml.reasoner.builtin.kaon2;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.omwg.logicalexpression.Constants;
import org.omwg.ontology.DataValue;
import org.omwg.ontology.SimpleDataValue;
import org.omwg.ontology.WsmlDataType;
import org.semanticweb.kaon2.api.DefaultOntologyResolver;
import org.semanticweb.kaon2.api.KAON2Connection;
import org.semanticweb.kaon2.api.KAON2Exception;
import org.semanticweb.kaon2.api.KAON2Factory;
import org.semanticweb.kaon2.api.KAON2Manager;
import org.semanticweb.kaon2.api.Ontology;
import org.semanticweb.kaon2.api.OntologyChangeEvent;
import org.semanticweb.kaon2.api.owl.elements.Individual;
import org.semanticweb.kaon2.api.reasoner.Query;
import org.semanticweb.kaon2.api.reasoner.Reasoner;
import org.semanticweb.kaon2.api.rules.Literal;
import org.semanticweb.kaon2.api.rules.NonOWLPredicate;
import org.semanticweb.kaon2.api.rules.Rule;
import org.semanticweb.kaon2.api.rules.Term;
import org.semanticweb.kaon2.api.rules.Variable;
import org.semanticweb.kaon2.extensionapi.datatype.DatatypeManager;
import org.wsml.reasoner.builtin.ConjunctiveQuery;
import org.wsml.reasoner.builtin.DatalogReasonerFacade;
import org.wsml.reasoner.builtin.ExternalToolException;
import org.wsml.reasoner.builtin.UnsupportedFeatureException;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsmo.common.IRI;
import org.wsmo.factory.DataFactory;
import org.wsmo.factory.WsmoFactory;

/**
 * Integrates the Kaon2 system into the WSML Core/Flight Reasoner framework for
 * query answering. Kaon2 can compute models for disjunctive logic programs and
 * thus can deal with query answering with (non-disjunctive) datalog as well. In
 * addition, Kaon2 supports the basic WSML datatypes: string, integer and
 * decimal (float)
 * 
 * @author Gabor Nagypal, FZI, Germany
 */
public class Kaon2Facade implements DatalogReasonerFacade {

    public static void initialize() {
        DatatypeManager
                .registerDatatypeHandler(new WsmlBooleanlDatatypeHandler());
    }

    private final Map<String, Object> EMPTY_MAP = new HashMap<String, Object>();

    private org.wsml.reasoner.builtin.ConjunctiveQuery query;

    private KAON2Factory f = KAON2Manager.factory();

    private KAON2Connection conn = null;

    private DataFactory df = WSMO4JManager.getDataFactory();

    private WsmoFactory wf = WSMO4JManager.getWSMOFactory();

    private Set<String> registeredOntologies = new HashSet<String>();

    /**
     * Needed for testing purposes
     */
    public KAON2Connection getKaon2Connection() {
        return conn;
    }

    /**
     * Evaluates a Query on a Datalog knowledgebase
     */
    public Set<Map<org.omwg.ontology.Variable, org.omwg.logicalexpression.terms.Term>> evaluate(
            ConjunctiveQuery q, String ontologyUri)
            throws ExternalToolException {

        Set<Map<org.omwg.ontology.Variable, org.omwg.logicalexpression.terms.Term>> result = new HashSet<Map<org.omwg.ontology.Variable, org.omwg.logicalexpression.terms.Term>>();
        this.query = q;

        // Derive and store the sequence of variables that defines the
        // output
        // tuples from the query
        List<org.omwg.ontology.Variable> bodyVars = q.getVariables();
        List<String> varNames = new ArrayList<String>(bodyVars.size());
        for (org.omwg.ontology.Variable v : bodyVars) {
            varNames.add(v.getName());
        }

        try {
            if (this.conn == null) {
                throw new ExternalToolException("The ontology " + ontologyUri
                        + " is not registered");
            }
            if (!this.registeredOntologies.contains(ontologyUri)) {
                throw new ExternalToolException("The ontology " + ontologyUri
                        + " is not registered");
            }
            Ontology ontology = this.conn.openOntology(ontologyUri, EMPTY_MAP);
            Reasoner reasoner = ontology.createReasoner();
            Query query = translateQuery(q, reasoner, varNames);
            // for (Literal l : query.getQueryLiterals()) {
            // System.out.println("Query literal: " + l);
            // }
            query.open();
            while (!query.afterLast()) {
                org.omwg.logicalexpression.terms.Term[] tuple = convertQueryTuple(query
                        .tupleBuffer());
                Map<org.omwg.ontology.Variable, org.omwg.logicalexpression.terms.Term> newVarBinding = new HashMap<org.omwg.ontology.Variable, org.omwg.logicalexpression.terms.Term>();

                for (int j = 0; j < varNames.size(); j++) {
                    newVarBinding.put(wf.createVariable(varNames.get(j)),
                            tuple[j]);
                }

                result.add(newVarBinding);
                query.next();
            }
            query.close();
            query.dispose();
            reasoner.dispose();
        } catch (KAON2Exception e) {
            throw new ExternalToolException(query, e);
        } catch (InterruptedException e) {
            throw new ExternalToolException(
                    "Kaon2 query was interrupted during execution");
        }

        return result;
    }

    /**
     * Translate a knowledgebase
     * 
     * @param p -
     *            the datalog program that constitutes the knowledgebase
     */
    private Set<Rule> translateKnowledgebase(
            Set<org.wsml.reasoner.builtin.Rule> p) throws ExternalToolException {

        if (p == null) {
            return null;
        }

        Set<Rule> result = new HashSet<Rule>();

        for (org.wsml.reasoner.builtin.Rule r : p) {
            result.add(translateRule(r));
        }
        return result;
    }

    /**
     * Translate the query
     * 
     * @param p -
     *            the datalog program that constitutes the knowledgebase
     */
    private Query translateQuery(ConjunctiveQuery q, Reasoner reasoner,
            List<String> varNames) throws ExternalToolException {

        List<Variable> distinguishedVars = new ArrayList<Variable>();

        for (String varName : varNames) {
            distinguishedVars.add(f.variable(varName));
        }

        // Translate the query:
        // Given: ?- query-expr(x1,...,xn)
        // Transform to: result(x1,...,xk) :- query-expr(x1,...,xn)
        // where x1,...,xk is the subsequence of x1,...,xn where multiple
        // occurrences of the
        // same variable have been removed.
        List<org.wsml.reasoner.builtin.Literal> body = q.getLiterals();
        List<Literal> queryLiterals = new ArrayList<Literal>();

        for (org.wsml.reasoner.builtin.Literal l : body) {
            queryLiterals.add(translateLiteral(l));
        }

        Query result = null;
        try {
            result = reasoner.createQuery(queryLiterals, distinguishedVars);
        } catch (KAON2Exception e) {
            throw new ExternalToolException(query, e);
        }
        return result;

    }

    /**
     * Translate a datalog rule
     */
    private Rule translateRule(org.wsml.reasoner.builtin.Rule r)
            throws ExternalToolException {

        Literal head = null;

        if (!r.isConstraint()) {
            head = translateLiteral(r.getHead());
        }

        List<Literal> body = new ArrayList<Literal>();

        // Care about body
        for (org.wsml.reasoner.builtin.Literal bl : r.getBody()) {
            body.add(translateLiteral(bl));
        }

        Rule rule;

        // Handle constraints
        if (head == null)
            rule = f.rule(new Literal[] {}, body.toArray(new Literal[body
                    .size()]));
        else
            rule = f.rule(head, body);

        return rule;

    }

    private Literal translateLiteral(org.wsml.reasoner.builtin.Literal l)
            throws ExternalToolException {
        if (l == null)
            return null;
        boolean isPositive = l.isPositive();
        try {
            String p = l.getPredicateUri();
            NonOWLPredicate pred;
            if (p.equals(Constants.EQUAL)) {
                pred = f.equal();
            } else if (p.equals(Constants.INEQUAL)) {
                pred = f.equal();
                isPositive = false;
            } else {

                pred = f.nonOWLPredicate(p, l.getTerms().length);
            }
            List<Term> terms = new ArrayList<Term>();

            org.omwg.logicalexpression.terms.Term[] args = l.getTerms();
            for (org.omwg.logicalexpression.terms.Term arg : args) {
                translateTerm(arg, terms);

            }

            return f.literal(isPositive, pred, terms);

        } catch (UnsupportedFeatureException ufe) {
            throw new ExternalToolException(query, ufe);
        }
    }

    private void translateTerm(org.omwg.logicalexpression.terms.Term term,
            List<Term> terms) throws UnsupportedFeatureException,
            ExternalToolException {
        if (term instanceof org.omwg.ontology.Variable) {
            terms
                    .add(f.variable(((org.omwg.ontology.Variable) term)
                            .getName()));
        } else if (term instanceof IRI) {
            terms.add(f.individual(((IRI) term).toString()));
        } else if (term instanceof DataValue) {
            DataValue dv = (DataValue) term;

            if (dv instanceof SimpleDataValue) {
                // It is BigInteger, BigDecimal or String which is
                // supported by KAON2
                terms.add(f.constant(dv.getValue()));
            } else if (WsmlDataType.WSML_BOOLEAN.equals(dv.getType().getIRI()
                    .toString())) {
                terms.add(f.constant(new WsmlBoolean((Boolean) dv.getValue())));
            } else {
                // No other datatypes are supported at present.
                throw new UnsupportedFeatureException(
                        "Unsupported Datatype: Datavalue '" + dv.getValue()
                                + "' of datatype " + dv.getType());
            }
        } else {
            throw new ExternalToolException(
                    "Could not interpret WSML term, it is neither an IRI nor a data value: "
                            + term);
        }
    }

    private void appendRules(Ontology ontology, Set<Rule> rules)
            throws KAON2Exception {
        List<OntologyChangeEvent> changes = new ArrayList<OntologyChangeEvent>();
        for (Rule rule : rules) {
            changes.add(new OntologyChangeEvent(rule,
                    OntologyChangeEvent.ChangeType.ADD));
        }
        ontology.applyChanges(changes);
    }

    /**
     * Converts a query tuple to a WSML Term array. For Kaon2 individuals
     * extract the IRI, for other objects return the proper WSML Term
     * 
     * @param tupleBuffer
     * @return an array of WSML Terms
     */
    private org.omwg.logicalexpression.terms.Term[] convertQueryTuple(
            Object[] tupleBuffer) throws ExternalToolException {
        org.omwg.logicalexpression.terms.Term[] result = new org.omwg.logicalexpression.terms.Term[tupleBuffer.length];
        for (int i = 0; i < tupleBuffer.length; i++) {
            Object obj = tupleBuffer[i];
            if (obj instanceof Individual) {
                Individual inst = (Individual) obj;
                result[i] = wf.createIRI(inst.getURI());
            } else if (obj instanceof String) {
                result[i] = df.createWsmlString((String) obj);
            } else if (obj instanceof BigInteger) {
                result[i] = df.createWsmlInteger((BigInteger) obj);
            } else if (obj instanceof BigDecimal) {
                result[i] = df.createWsmlDecimal((BigDecimal) obj);
            } else if (obj instanceof WsmlBoolean) {
                result[i] = df
                        .createWsmlBoolean(((WsmlBoolean) obj).getValue());
            } else {
                throw new ExternalToolException(
                        "Unknown object in the KAON2 ontology: "
                                + obj.toString());
            }

        }
        return result;
    }

    public void register(String ontologyURI,
            Set<org.wsml.reasoner.builtin.Rule> kb)
            throws ExternalToolException {
        if (conn == null) {
            conn = KAON2Manager.newConnection();
            DefaultOntologyResolver resolver = new DefaultOntologyResolver();
            conn.setOntologyResolver(resolver);
        }
        // Deregister, if already registered
        if (this.registeredOntologies.contains(ontologyURI)) {
            deregister(ontologyURI);
        }
        // TODO Handle ontology imports
        DefaultOntologyResolver resolver = (DefaultOntologyResolver) conn
                .getOntologyResovler();
        resolver.registerReplacement(ontologyURI, "file:/C:/tmp/wsml.xml");
        try {
            Ontology o = conn.createOntology(ontologyURI, EMPTY_MAP);
            Set<Rule> rules = translateKnowledgebase(kb);
            appendRules(o, rules);
            // try {
            // o.saveOntology(OntologyFileFormat.OWL_XML, o.getPhysicalURI(),
            // "ISO-8859-15");
            // } catch (IOException e) {
            // e.printStackTrace();
            // } catch (InterruptedException e) {
            // e.printStackTrace();
            // }
        } catch (KAON2Exception e) {
            throw new ExternalToolException(
                    "Cannot register ontology in KAON2", e);
        }
        this.registeredOntologies.add(ontologyURI);

    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        this.conn.close();
    }

    public void deregister(String ontologyUri) throws ExternalToolException {
        if (!this.registeredOntologies.contains(ontologyUri)) {
            throw new ExternalToolException("The ontology " + ontologyUri
                    + " is not registered");
        }
        try {
            Ontology ontology = this.conn.openOntology(ontologyUri, EMPTY_MAP);
            this.conn.closeOntologies(Collections.singleton(ontology));
            this.registeredOntologies.remove(ontologyUri);
        } catch (KAON2Exception e) {
            throw new ExternalToolException("Internal Kaon2 exception", e);
        } catch (InterruptedException e) {
            throw new ExternalToolException(
                    "Kaon2 query was interrupted during execution");
        }
    }

}
