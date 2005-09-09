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

package org.wsml.reasoner.datalog.wrapper.kaon2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.omwg.logexpression.Constants;
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
import org.wsml.reasoner.api.queryanswering.VariableBinding;
import org.wsml.reasoner.datalog.ConjunctiveQuery;
import org.wsml.reasoner.datalog.Constant;
import org.wsml.reasoner.datalog.DataTypeValue;
import org.wsml.reasoner.datalog.Predicate;
import org.wsml.reasoner.datalog.Program;
import org.wsml.reasoner.datalog.QueryResult;
import org.wsml.reasoner.datalog.DataTypeValue.DataType;
import org.wsml.reasoner.datalog.wrapper.DatalogReasonerFacade;
import org.wsml.reasoner.datalog.wrapper.ExternalToolException;
import org.wsml.reasoner.datalog.wrapper.SymbolFactory;
import org.wsml.reasoner.datalog.wrapper.UnsupportedFeatureException;
import org.wsml.reasoner.impl.VariableBindingImpl;

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

    private Logger logger = Logger
            .getLogger("org.wsml.reasoner.wsmlcore.wrapper.kaon2");
    {
        logger.setLevel(Level.OFF);
    }

    private org.wsml.reasoner.datalog.Query query;

    private KAON2Factory f = KAON2Manager.factory();

    private KAON2Connection conn = null;

    /**
     * Needed for testing purposes
     */
    public KAON2Connection getKaon2Connection() {
        return conn;
    }

    /**
     * Evaluates a Query on a Datalog knowledgebase
     */
    public QueryResult evaluate(ConjunctiveQuery q, String ontologyUri)
            throws ExternalToolException {

        try {

            QueryResult result = new QueryResult(q);
            this.query = q;

            // Derive and store the sequence of variables that defines the
            // output
            // tuples from the query
            List<org.wsml.reasoner.datalog.Variable> bodyVars = q
                    .getVariables();
            List<String> varNames = new ArrayList<String>(bodyVars.size());
            for (org.wsml.reasoner.datalog.Variable v : bodyVars) {
                varNames.add(v.getSymbol());
            }

            logger.log(Level.FINE, "Sequence of variables in query is: ",
                    varNames);

            try {
                Ontology ontology = this.conn.openOntology(ontologyUri,
                        EMPTY_MAP);
                if (ontology == null)
                    throw new ExternalToolException("The ontology "
                            + ontologyUri + " is not registered");
                Reasoner reasoner = ontology.createReasoner();
                Query query = translateQuery(q, reasoner, varNames);
                for (Literal l : query.getQueryLiterals()) {
                    System.out.println("Query literal: " + l);
                }
                query.open();
                while (!query.afterLast()) {
                    String[] tuple = convertQueryTuple(query.tupleBuffer());
                    VariableBinding newVarBinding = new VariableBindingImpl();

                    for (int j = 0; j < varNames.size(); j++) {
                        newVarBinding.put(varNames.get(j), tuple[j]);
                    }

                    result.getVariableBindings().add(newVarBinding);
                    query.next();
                }
                query.close();
                query.dispose();
                reasoner.dispose();
            } catch (KAON2Exception e) {
                throw new ExternalToolException(
                        "Can not convert query to tool:" + '\"'
                                + e.getMessage() + '\"', e, query);
            } catch (InterruptedException e) {
                throw new ExternalToolException(
                        "Kaon2 query was interrupted during execution");
            }

            return result;

        } catch (ExternalToolException ete) {
            logger
                    .severe("External tool exception occured when trying to evaluate query: "
                            + ete.getFailedQuery());
            throw ete;
        }
    }

    /**
     * Translate a knowledgebase
     * 
     * @param p -
     *            the datalog program that constitutes the knowledgebase
     */
    private Set<Rule> translateKnowledgebase(org.wsml.reasoner.datalog.Program p)
            throws ExternalToolException {
        logger.info("Translate knowledgebase :" + p);

        if (p == null) {
            logger.info("KB is not referenced. Assume empty KB.");
            return null;
        }

        Set<Rule> result = new HashSet<Rule>();

        for (org.wsml.reasoner.datalog.Rule r : p) {
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
        logger.info("Translate query :" + q);

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
        List<org.wsml.reasoner.datalog.Literal> body = q.getLiterals();
        List<Literal> queryLiterals = new ArrayList<Literal>();

        for (org.wsml.reasoner.datalog.Literal l : body) {
            queryLiterals.add(translateLiteral(l));
        }

        Query result = null;
        try {
            result = reasoner.createQuery(queryLiterals, distinguishedVars);
        } catch (KAON2Exception e) {
            throw new ExternalToolException("Can not convert query to tool", e,
                    query);
        }
        return result;

    }

    /**
     * Translate a datalog rule
     */
    private Rule translateRule(org.wsml.reasoner.datalog.Rule r)
            throws ExternalToolException {

        Literal head = null;

        if (!r.isConstraint()) {
            head = translateLiteral(r.getHead());
        }

        List<Literal> body = new ArrayList<Literal>();

        // Care about body
        for (org.wsml.reasoner.datalog.Literal bl : r.getBody()) {
            body.add(translateLiteral(bl));
        }

        Rule rule;

        // Handle constraints
        if (head == null)
            rule = f.rule(new Literal[] {}, body.toArray(new Literal[body
                    .size()]));
        else
            rule = f.rule(head, body);

        logger.info("Transformed rule:\n" + r + "\n to \n" + rule);

        return rule;

    }

    private Literal translateLiteral(org.wsml.reasoner.datalog.Literal l)
            throws ExternalToolException {
        if (l == null)
            return null;
        boolean isPositive = l.isPositive();
        try {
            Predicate p = l.getSymbol();
            NonOWLPredicate pred;
            if (p.getSymbolName().equals(Constants.EQUAL)) {
                pred = f.equal();
            } else if (p.getSymbolName().equals(Constants.INEQUAL)) {
                pred = f.equal();
                isPositive = false;
            } else {

                pred = f.nonOWLPredicate(p.getSymbolName(), p.getArity());
            }
            List<Term> terms = new ArrayList<Term>();

            org.wsml.reasoner.datalog.Term[] args = l.getArguments();
            for (org.wsml.reasoner.datalog.Term arg : args) {
                if (arg.getClass().equals(
                        org.wsml.reasoner.datalog.Variable.class)) {
                    terms
                            .add(f
                                    .variable(((org.wsml.reasoner.datalog.Variable) arg)
                                            .getSymbol()));
                }
                if (arg.getClass().equals(
                        org.wsml.reasoner.datalog.Constant.class)) {
                    terms.add(f.individual(((Constant) arg).getSymbol()));
                }
                if (arg.getClass().equals(
                        org.wsml.reasoner.datalog.DataTypeValue.class)) {
                    DataTypeValue dv = (DataTypeValue) arg;
                    DataType dType = dv.getType();
                    if (dType == DataType.INTEGER) {
                        terms.add(f.constant(new Integer(dv.getSymbol())));
                        logger
                                .info("Handle WSML data value in Kaon2 SymbolFactory: "
                                        + dv.getSymbol());

                    } else if (dType == DataType.DECIMAL) {
                        terms.add(f.constant(new Double(dv.getSymbol())));
                        logger
                                .info("Handle WSML data value in Kaon2 SymbolFactory: "
                                        + dv.getSymbol());

                    } else if (dType == DataType.STRING) {
                        terms.add(f.constant(dv.getSymbol()));
                        logger
                                .info("Handle WSML data value in Kaon2 SymbolFactory: "
                                        + dv.getSymbol());

                    } else {
                        // No other datatypes are supported by DLV at present.
                        throw new UnsupportedFeatureException(
                                "Unsupported Datatype: Datavalue '"
                                        + dv.getSymbol() + "' of datatype "
                                        + dType.toString());
                    }

                }

            }
            return f.literal(isPositive, pred, terms);

        } catch (UnsupportedFeatureException ufe) {
            throw new ExternalToolException("Can not convert query to tool: "
                    + l.toString(), ufe, query);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.wsml.reasoner.wsmlcore.wrapper.DatalogReasonerFacade#useSymbolFactory(org.wsml.reasoner.wsmlcore.wrapper.SymbolFactory)
     */
    public void useSymbolFactory(SymbolFactory sf) {
        // do nothing

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
     * Converts a query tuple to a string array. For Kaon2 individuals extract
     * the URI, for other objects just call toString
     * 
     * @param tupleBuffer
     * @return
     */
    private String[] convertQueryTuple(Object[] tupleBuffer) {
        String[] result = new String[tupleBuffer.length];
        for (int i = 0; i < tupleBuffer.length; i++) {
            Object obj = tupleBuffer[i];
            if (obj instanceof Individual) {
                Individual inst = (Individual) obj;
                result[i] = inst.getURI();
            } else
                result[i] = obj.toString();
        }
        return result;
    }

    public void register(String ontologyURI, Program kb)
            throws ExternalToolException {
        if (conn == null) {
            conn = KAON2Manager.newConnection();
            DefaultOntologyResolver resolver = new DefaultOntologyResolver();
            conn.setOntologyResolver(resolver);
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
                    "Cannot register ontology in KAON2", e, null);
        }

    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        this.conn.close();
    }

}
