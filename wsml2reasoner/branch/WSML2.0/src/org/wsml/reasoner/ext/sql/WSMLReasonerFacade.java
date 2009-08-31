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
import java.util.Map;
import java.util.Set;

import org.deri.wsmo4j.io.parser.wsml.LogExprParserTypedImpl;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Variable;
import org.sti2.wsmo4j.factory.FactoryImpl;
import org.wsml.reasoner.api.LPReasoner;
import org.wsml.reasoner.api.inconsistency.InconsistencyException;
import org.wsml.reasoner.impl.DefaultWSMLReasonerFactory;
import org.wsmo.common.TopEntity;
import org.wsmo.common.exception.InvalidModelException;
import org.wsmo.factory.Factory;
import org.wsmo.wsml.Parser;
import org.wsmo.wsml.ParserException;

import com.ontotext.wsmo4j.parser.wsml.ParserImplTyped;

/**
 * This class encapsulates the reasoner and performs necessary pre-processing.
 */
public class WSMLReasonerFacade {

	private Factory factory;
	
    private LPReasoner reasoner;

    private Ontology onto;

	private Parser wsmlParser;

	/**
     * Constructs a new WSMLReasonerFacade by setting up WSMO4J resources, the
     * parser, the reasoner.
     */
    public WSMLReasonerFacade() {
    	factory = new FactoryImpl();
    	
    	wsmlParser = new ParserImplTyped();
    	
        reasoner = DefaultWSMLReasonerFactory.getFactory().createFlightReasoner(null);
    }
    
    /**
     *  Constructs a new WSMLReasonerFacade by setting up WSMO4j resources, the parser 
     *  and used a predefined reasoner.
     * @param reasoner The reasoner to use.
     */
    public WSMLReasonerFacade(LPReasoner reasoner) {
    	this();
       
        this.reasoner = reasoner;
    }

    public ReasonerResult executeWsmlQuery(String query) throws InconsistencyException, IOException, InvalidModelException, ParserException {
    	if(query == null) {
    		throw new IllegalArgumentException();
    	}
    	
        LogicalExpression lequery = new LogExprParserTypedImpl(factory).parse(query);
      
        return doQuery(lequery);
    }
    
    /**
     * Sets the reasoner object that should be used internally.
     * 
     * @param reasoner The reasoner
     */
    public void setReasoner(LPReasoner reasoner) {
    	this.reasoner = reasoner;
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
        LogicalExpression lequery = new LogExprParserTypedImpl(onto, factory).parse(query);

        return doQuery(lequery);
    }

    /**
     * Returns the loaded ontology.
     * 
     * @return
     */
    public Ontology getOntology() {
        return onto;
    }
    
    
    protected ReasonerResult doQuery(LogicalExpression lequery) {
    	// a set of variable bindings (map with variables as keys, and the
        // bindings: IRIs or DataValues as values
        Set<Map<Variable, Term>> queryResult = reasoner.executeQuery(lequery);
        ReasonerResult reasonerResult = new ReasonerResult(queryResult);

        return reasonerResult;
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
        TopEntity[] identifiable = wsmlParser.parse(new InputStreamReader(is));
        if (identifiable.length > 0 && identifiable[0] instanceof Ontology) {
            ontology = ((Ontology) identifiable[0]);
        }

        return ontology;
    }
}
