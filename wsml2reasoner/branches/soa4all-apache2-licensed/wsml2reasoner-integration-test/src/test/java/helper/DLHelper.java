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
package helper;

import org.deri.wsmo4j.io.parser.wsml.WsmlLogicalExpressionParser;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.LogicalExpressionParser;
import org.omwg.ontology.Ontology;
import org.wsml.reasoner.api.DLReasoner;
import org.wsml.reasoner.api.inconsistency.InconsistencyException;


/**
 * Helper utilities for DL based tests.
 */
public class DLHelper
{
	public static boolean isEntailed( Ontology ontology, String expression, DLReasoner reasoner ) throws InconsistencyException, org.wsmo.wsml.ParserException {
    	
    	reasoner.registerOntology( ontology );
    	LogicalExpressionParser leParser = new WsmlLogicalExpressionParser();
        LogicalExpression le = leParser.parse( expression );

    	return reasoner.isConceptSatisfiable( le );
	}

}
