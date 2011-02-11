/*
 * Copyright 2011, Semantic Technology Institute (STI) Innsbruck, 
 * University of Innsbruck, Technikerstrasse 21a, 6020 Innsbruck, Austria.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wsml.reasoner.gui;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.deri.wsmo4j.io.parser.wsml.WsmlLogicalExpressionParser;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.api.LPReasoner;
import org.wsml.reasoner.api.inconsistency.InconsistencyException;
import org.wsmo.wsml.ParserException;

/**
 * Helper utilities for DL based tests.
 */
public class LPHelper
{
   
    public static Set<Map<Variable, Term>> executeQuery( Ontology ontology, String query, LPReasoner reasoner ) throws ParserException, InconsistencyException
    {
    	Set<Ontology> ontologies = new HashSet<Ontology>();
    	ontologies.add( ontology );
    	
    	return executeQuery( ontologies, query, reasoner );
    }
    
    public static Set<Map<Variable, Term>> executeQuery( Set<Ontology> ontologies, String query, LPReasoner reasoner ) throws ParserException, InconsistencyException
    {
        reasoner.registerOntologies(ontologies);

        LogicalExpression qExpression = new WsmlLogicalExpressionParser(ontologies.iterator().next()).parse( query );

//		System.out.println("Executing query string '" + query + "'");
//		System.out.println("Executing query LE: '" + OntologyHelper.toString( ontology, qExpression ) + "'");

		return reasoner.executeQuery(qExpression);
    }

}
