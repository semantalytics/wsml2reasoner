/**
 * Copyright (C) 2007 Digital Enterprise Research Institute (DERI), 
 * Leopold-Franzens-Universitaet Innsbruck, Technikerstrasse 21a, 
 * A-6020 Innsbruck. Austria.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 */
package org.wsml.reasoner.ext.sql;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.api.LPReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.api.inconsistency.InconsistencyException;
import org.wsml.reasoner.impl.DefaultWSMLReasonerFactory;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsmo.common.TopEntity;
import org.wsmo.common.exception.InvalidModelException;
import org.wsmo.factory.Factory;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.wsml.Parser;
import org.wsmo.wsml.ParserException;

/**
 * This class encapsulates the reasoner and performs necessary pre-processing.
 */
public class WSMLReasonerFacade {

    /**
     * Constructs a new WSMLReasoenrFacade by setting up WSML4J resources, the
     * parser, the reasoner.
     */
    public WSMLReasonerFacade() {
        manager = new WSMO4JManager();
        parser = Factory.createParser(null);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(WSMLReasonerFactory.PARAM_BUILT_IN_REASONER, WSMLReasonerFactory.BuiltInReasoner.IRIS);

        reasoner = DefaultWSMLReasonerFactory.getFactory().createFlightReasoner(params);
    }

    /**
     * Executes a WSML query against a certain ontology.
     * 
     * @param query
     *            the query to be answered.
     * @param ontologyIRI
     *            an IRI pointing to the ontology to be used
     * @return the query results
     * @throws ParserException
     * @throws InconsistencyException
     * @throws InvalidModelException
     * @throws IOException
     */
    public ReasonerResult executeWsmlQuery(String query, String ontologyIRI) throws ParserException, InconsistencyException, IOException, InvalidModelException {
        if (ontologyIRI == null || query == null) {
            throw new IllegalArgumentException();
        }

        onto = loadOntology(ontologyIRI);
        if (onto == null) {
            return null; // loading failed
        }

        reasoner.registerOntology(onto);
        LogicalExpressionFactory leFactory = manager.getLogicalExpressionFactory();
        LogicalExpression lequery = leFactory.createLogicalExpression(query, onto);

        // a set of variable bindings (map with variables as keys, and the
        // bindings: IRIs or DataValues as values
        Set<Map<Variable, Term>> queryResult = reasoner.executeQuery(lequery);
        ReasonerResult reasonerResult = new ReasonerResult(queryResult);

        return reasonerResult;
    }

    /**
     * Returns the loaded ontology.
     * 
     * @return
     */
    public Ontology getOntology() {
        return onto;
    }

    /**
     * Loads an ontology given an IRI.
     * 
     * @param ontologyIri
     *            an IRR pointed to the ontology location. E.g. on the local
     *            file system, on the web, ...
     * @return the loaded ontology
     * @throws InvalidModelException
     * @throws ParserException
     * @throws IOException
     */
    protected Ontology loadOntology(String ontologyIri) throws IOException, ParserException, InvalidModelException {
        assert ontologyIri != null;

        URL url = null;
        Ontology ontology = null;
        url = new URL(ontologyIri);
        InputStream is = url.openStream();
        TopEntity[] identifiable = parser.parse(new InputStreamReader(is));
        if (identifiable.length > 0 && identifiable[0] instanceof Ontology) {
            ontology = ((Ontology) identifiable[0]);
        }

        return ontology;
    }

    private WSMO4JManager manager;

    private Parser parser;

    private LPReasoner reasoner;

    private Ontology onto;
}
