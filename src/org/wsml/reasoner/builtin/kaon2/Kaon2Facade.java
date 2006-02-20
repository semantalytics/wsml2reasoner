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

import java.io.IOException;
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
import org.semanticweb.kaon2.api.*;
import org.semanticweb.kaon2.api.owl.elements.Individual;
import org.semanticweb.kaon2.api.reasoner.Query;
import org.semanticweb.kaon2.api.reasoner.Reasoner;
import org.semanticweb.kaon2.api.rules.Literal;
import org.semanticweb.kaon2.api.rules.NonOWLPredicate;
import org.semanticweb.kaon2.api.rules.Rule;
import org.semanticweb.kaon2.api.rules.Term;
import org.semanticweb.kaon2.api.rules.Variable;
import org.semanticweb.kaon2.extensionapi.datatype.DatatypeManager;
import org.wsml.reasoner.*;
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

    private final Map<String, Object> EMPTY_MAP = new HashMap<String, Object>();

    private org.wsml.reasoner.ConjunctiveQuery query;

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
            for (Literal l : query.getQueryLiterals()) {
                //System.out.println("Query literal: " + l);
            }
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
            Set<org.wsml.reasoner.Rule> p) throws ExternalToolException {

        if (p == null) {
            return null;
        }

        Set<Rule> result = new HashSet<Rule>();

        for (org.wsml.reasoner.Rule r : p) {
            result.add(translateRule(r));
        }
        // Add some static rules to infer membership statements for datatype
        // predicates
        NonOWLPredicate mO = f.nonOWLPredicate(
                WSML2DatalogTransformer.PRED_MEMBER_OF, 2);
        NonOWLPredicate hasValue = f.nonOWLPredicate(
                WSML2DatalogTransformer.PRED_HAS_VALUE, 3);
        Literal hV = f.literal(true, hasValue, f.variable("x"),
                f.variable("a"), f.variable("v"));
        // wsml-member-of(?x,wsml#string) :- isstring(?x)
        Literal head = f.literal(true, mO, f.variable("v"), f
                .individual(WsmlDataType.WSML_STRING));
        Literal typeCheck = f.literal(true, f.ifTrue(2), f
                .constant("isstring($1)"), f.variable("v"));
        result.add(f.rule(head, new Literal[] { hV, typeCheck }));
        // wsml-member-of(?x,wsml#integer) :- isbiginteger(?x)
        head = f.literal(true, mO, f.variable("v"), f
                .individual(WsmlDataType.WSML_INTEGER));
        typeCheck = f.literal(true, f.ifTrue(2),
                f.constant("isbiginteger($1)"), f.variable("v"));
        result.add(f.rule(head, new Literal[] { hV, typeCheck }));
        // wsml-member-of(?x,wsml#decimal) :- isbigdecimal(?x)
        head = f.literal(true, mO, f.variable("v"), f
                .individual(WsmlDataType.WSML_DECIMAL));
        typeCheck = f.literal(true, f.ifTrue(2),
                f.constant("isbigdecimal($1)"), f.variable("v"));
        result.add(f.rule(head, new Literal[] { hV, typeCheck }));
        // wsml-member-of(?x,wsml#boolean) :- isboolean(?x)
        head = f.literal(true, mO, f.variable("v"), f
                .individual(WsmlDataType.WSML_BOOLEAN));
        typeCheck = f.literal(true, f.ifTrue(2), f.constant("isboolean($1)"), f
                .variable("v"));
        result.add(f.rule(head, new Literal[] { hV, typeCheck }));
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
        List<org.wsml.reasoner.Literal> body = q.getLiterals();
        List<Literal> queryLiterals = new ArrayList<Literal>();

        for (org.wsml.reasoner.Literal l : body) {
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
    private Rule translateRule(org.wsml.reasoner.Rule r)
            throws ExternalToolException {

        Literal head = null;

        if (!r.isConstraint()) {
            head = translateLiteral(r.getHead());
        }

        List<Literal> body = new ArrayList<Literal>();

        // Care about body
        for (org.wsml.reasoner.Literal bl : r.getBody()) {
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

    private Literal translateLiteral(org.wsml.reasoner.Literal l)
            throws ExternalToolException {
        if (l == null)
            return null;
        boolean isPositive = l.isPositive();
        try {
            String p = l.getPredicateUri();
            NonOWLPredicate pred = null;
            List<Term> terms = new ArrayList<Term>();
            if (p.equals(Constants.EQUAL)) {
                pred = f.equal();
                translateTerms(l, terms);
            } else if (p.equals(Constants.INEQUAL)) {
                pred = f.equal();
                isPositive = false;
                translateTerms(l, terms);
            } else if (p.equals(Constants.LESS_THAN)) {
                if (l.getTerms().length == 2) {
                    pred = f.ifTrue(3);
                    terms.add(f.constant("$1 < $2"));
                    translateTerms(l, terms);
                } else {
                    throw new ExternalToolException(
                            "wsml#lessThan should have exactly two arguments!");
                }
            } else if (p.equals(Constants.LESS_EQUAL)) {
                if (l.getTerms().length == 2) {
                    pred = f.ifTrue(3);
                    terms.add(f.constant("$1 <= $2"));
                    translateTerms(l, terms);
                } else {
                    throw new ExternalToolException(
                            "wsml#lessEqual should have exactly two arguments!");
                }
            } else if (p.equals(Constants.GREATER_THAN)) {
                if (l.getTerms().length == 2) {
                    pred = f.ifTrue(3);
                    terms.add(f.constant("$1 > $2"));
                    translateTerms(l, terms);
                } else {
                    throw new ExternalToolException(
                            "wsml#greaterThan should have exactly two arguments!");
                }
            } else if (p.equals(Constants.GREATER_EQUAL)) {
                if (l.getTerms().length == 2) {
                    pred = f.ifTrue(3);
                    terms.add(f.constant("$1 >= $2"));
                    translateTerms(l, terms);
                } else {
                    throw new ExternalToolException(
                            "wsml#greaterEqual should have exactly two arguments!");
                }
            } else if (p.equals(Constants.NUMERIC_EQUAL)) {
                if (l.getTerms().length == 2) {
                    pred = f.ifTrue(3);
                    terms.add(f.constant("$1 == $2"));
                    translateTerms(l, terms);
                } else {
                    throw new ExternalToolException(
                            "wsml#numericEqual should have exactly two arguments!");
                }
            } else if (p.equals(Constants.NUMERIC_INEQUAL)) {
                if (l.getTerms().length == 2) {
                    pred = f.ifTrue(3);
                    terms.add(f.constant("$1 != $2"));
                    translateTerms(l, terms);
                } else {
                    throw new ExternalToolException(
                            "wsml#numericInEqual should have exactly two arguments!");
                }
            } else if (p.equals(Constants.STRING_EQUAL)) {
                if (l.getTerms().length == 2) {
                    pred = f.ifTrue(3);
                    terms.add(f.constant("$1 == $2"));
                    translateTerms(l, terms);
                } else {
                    throw new ExternalToolException(
                            "wsml#stringEqual should have exactly two arguments!");
                }
            } else if (p.equals(Constants.STRING_INEQUAL)) {
                if (l.getTerms().length == 2) {
                    pred = f.ifTrue(3);
                    terms.add(f.constant("$1 != $2"));
                    translateTerms(l, terms);
                } else {
                    throw new ExternalToolException(
                            "wsml#lessThan should have exactly two arguments!");
                }
            } else if (p.equals(Constants.NUMERIC_ADD)) {
                if (l.getTerms().length == 3) {
                    pred = f.evaluate(4);
                    terms.add(f.constant("$1 + $2"));
                    org.omwg.logicalexpression.terms.Term[] literalTerms = l
                            .getTerms();
                    translateTerm(literalTerms[1], terms);
                    translateTerm(literalTerms[2], terms);
                    translateTerm(literalTerms[0], terms);
                } else {
                    throw new ExternalToolException(
                            "wsml#numericAdd should have exactly three arguments!");
                }
            } else if (p.equals(Constants.NUMERIC_SUB)) {
                if (l.getTerms().length == 3) {
                    pred = f.evaluate(4);
                    terms.add(f.constant("$1 - $2"));
                    org.omwg.logicalexpression.terms.Term[] literalTerms = l
                            .getTerms();
                    translateTerm(literalTerms[1], terms);
                    translateTerm(literalTerms[2], terms);
                    translateTerm(literalTerms[0], terms);
                } else {
                    throw new ExternalToolException(
                            "wsml#numericSubtract should have exactly three arguments!");
                }
            } else if (p.equals(Constants.NUMERIC_MUL)) {
                if (l.getTerms().length == 3) {
                    pred = f.evaluate(4);
                    terms.add(f.constant("$1 * $2"));
                    org.omwg.logicalexpression.terms.Term[] literalTerms = l
                            .getTerms();
                    translateTerm(literalTerms[1], terms);
                    translateTerm(literalTerms[2], terms);
                    translateTerm(literalTerms[0], terms);
                } else {
                    throw new ExternalToolException(
                            "wsml#numericAdd should have exactly three arguments!");
                }
            } else if (p.equals(Constants.NUMERIC_DIV)) {
                if (l.getTerms().length == 3) {
                    pred = f.evaluate(4);
                    terms.add(f.constant("$1 / $2"));
                    org.omwg.logicalexpression.terms.Term[] literalTerms = l
                            .getTerms();
                    translateTerm(literalTerms[1], terms);
                    translateTerm(literalTerms[2], terms);
                    translateTerm(literalTerms[0], terms);
                } else {
                    throw new ExternalToolException(
                            "wsml#numericAdd should have exactly three arguments!");
                }
            } else if (p.equals(WSML2DatalogTransformer.PRED_IMPLIES_TYPE)) {
                // check whether the last argument is a datatype
                if (l.getTerms().length == 3) {
                    org.omwg.logicalexpression.terms.Term t = l.getTerms()[2];
                    if (t instanceof IRI) {
                        IRI rangeIri = (IRI) t;
                        if (WsmlDataType.WSML_STRING.equals(rangeIri)
                                || WsmlDataType.WSML_INTEGER.equals(rangeIri
                                        .toString())
                                || WsmlDataType.WSML_DECIMAL.equals(rangeIri
                                        .toString())
                                || WsmlDataType.WSML_BOOLEAN.equals(rangeIri
                                        .toString())) {
                            pred = f.nonOWLPredicate(
                                    WSML2DatalogTransformer.PRED_OF_TYPE, l
                                            .getTerms().length);
                            translateTerms(l, terms);
                            //System.out.println("Translated implies_type for oftype for " + rangeIri);
                        } else {
                            pred = f.nonOWLPredicate(p, l.getTerms().length);
                            translateTerms(l, terms);
                        }
                    } else {
                        pred = f.nonOWLPredicate(p, l.getTerms().length);
                        translateTerms(l, terms);
                    }
                } else {
                    throw new ExternalToolException(
                            "wsml-implies-type should have exactly three arguments!");
                }
            } else {
                pred = f.nonOWLPredicate(p, l.getTerms().length);
                translateTerms(l, terms);
            }

            return f.literal(isPositive, pred, terms);

        } catch (UnsupportedFeatureException ufe) {
            throw new ExternalToolException(query, ufe);
        }
    }

    private void translateTerms(org.wsml.reasoner.Literal literal,
            List<Term> terms) throws UnsupportedFeatureException,
            ExternalToolException {
        org.omwg.logicalexpression.terms.Term[] args = literal.getTerms();
        for (org.omwg.logicalexpression.terms.Term arg : args) {
            translateTerm(arg, terms);

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
                Object value = dv.getValue();
                terms.add(f.constant(value));
            } else if (WsmlDataType.WSML_BOOLEAN.equals(dv.getType().getIRI()
                    .toString())) {
                terms.add(f.constant(dv.getValue()));
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
            } else if (obj instanceof Boolean) {
                result[i] = df.createWsmlBoolean((Boolean) obj);
            } else {
                throw new ExternalToolException(
                        "Unknown object in the KAON2 ontology: "
                                + obj.toString());
            }

        }
        return result;
    }

    public void register(String ontologyURI,
            Set<org.wsml.reasoner.Rule> kb)
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
//            try {
//                o.saveOntology(OntologyFileFormat.OWL_XML, o.getPhysicalURI(),
//                        "ISO-8859-15");
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
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
