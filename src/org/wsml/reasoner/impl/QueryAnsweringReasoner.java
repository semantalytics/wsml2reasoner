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

package org.wsml.reasoner.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.omwg.logexpression.Atom;
import org.omwg.logexpression.CompoundExpression;
import org.wsml.reasoner.api.queryanswering.QueryAnsweringRequest;
import org.wsml.reasoner.api.queryanswering.QueryAnsweringResult;
import org.wsml.reasoner.datalog.ConjunctiveQuery;
import org.wsml.reasoner.datalog.DatalogException;
import org.wsml.reasoner.datalog.Literal;
import org.wsml.reasoner.datalog.Program;
import org.wsml.reasoner.datalog.QueryResult;
import org.wsml.reasoner.datalog.Rule;
import org.wsml.reasoner.datalog.wrapper.DatalogReasonerFacade;
import org.wsml.reasoner.datalog.wrapper.ExternalToolException;
import org.wsml.reasoner.transformation.WSML2DatalogTransformer;
import org.wsml.reasoner.transformation.le.LogicalExpressionNormalizer;
import org.wsml.reasoner.transformation.le.MoleculeDecompositionRules;
import org.wsml.reasoner.transformation.le.OnePassReplacementNormalizer;
import org.wsmo.factory.Factory;

/**
 * A prototypical implementation of a query answering engine for WSML Core.
 * 
 * The implementation is based on mapping to some external tool like DLV, Kaon2,
 * Mandrax or XSB Prolog.
 * 
 * At present, the implementation uses the DLV system. Hence, datatype support
 * can not be provided except for integers.
 * 
 * @author Uwe Keller, DERI Innsbruck
 */
public class QueryAnsweringReasoner {

    private DatalogReasonerFacade qaf = null;

    public QueryAnsweringReasoner(DatalogReasonerFacade builtInFacade) {
        this.qaf = builtInFacade;
    }

    private final static String WSML_RESULT_PREDICATE = "wsml:query_result";

    public QueryAnsweringResult execute(QueryAnsweringRequest req)
            throws DatalogException, ExternalToolException {
        QueryAnsweringResult result = new QueryAnsweringResultImpl(req);

        // Convert the query ...

        ConjunctiveQuery query = convertQuery(req.getQuery());

        QueryResult qres = qaf.evaluate(query, req.getOntologyUri());

        // TODO: No incremental fetching of results is supported here at present
        result.addAll(qres.getVariableBindings());

        return result;
    }

    private ConjunctiveQuery convertQuery(
            org.omwg.logexpression.LogicalExpression q) {
        WSML2DatalogTransformer wsml2datalog = new WSML2DatalogTransformer();

        Map<String, String> leProperties = new HashMap<String, String>();
        leProperties.put(Factory.PROVIDER_CLASS,
                "org.deri.wsmo4j.logexpression.LogicalExpressionFactoryImpl");

        org.omwg.logexpression.LogicalExpressionFactory leFactory = (org.omwg.logexpression.LogicalExpressionFactory) Factory
                .createLogicalExpressionFactory(leProperties);

        List<org.omwg.logexpression.terms.Variable> params = new LinkedList<org.omwg.logexpression.terms.Variable>();
        LogicalExpressionVariableVisitor varVisitor = new LogicalExpressionVariableVisitor();
        q.accept(varVisitor);
        params.addAll(varVisitor.getFreeVariables(q));
        Atom rHead = leFactory.createAtom(leFactory
                .createIRI(WSML_RESULT_PREDICATE), params);

        LogicalExpressionNormalizer moleculeNormalizer = new OnePassReplacementNormalizer(
                MoleculeDecompositionRules.instantiate());
        // System.out.println("Q before molecule normalization: " + q);
        q = moleculeNormalizer.normalize(q);
        // System.out.println("Q after molecule normalization: " + q);
        org.omwg.logexpression.LogicalExpression resultDefRule = leFactory
                .createBinary(CompoundExpression.IMPLIEDBY, rHead, q);

        Program p = wsml2datalog.transform(resultDefRule);
        // System.out.println("Query as program:" + p);
        if (p.size() != 1)
            throw new IllegalArgumentException("Could not transform query " + q);
        Rule rule = p.get(0);
        if (!rule.getHead().getSymbol().getSymbolName().equals(
                WSML_RESULT_PREDICATE))
            throw new IllegalArgumentException("Could not transform query " + q);

        List<Literal> body = new LinkedList<Literal>();

        for (Literal l : rule.getBody()) {
            body.add(l);
        }

        return new ConjunctiveQuery(body);
    }

}
