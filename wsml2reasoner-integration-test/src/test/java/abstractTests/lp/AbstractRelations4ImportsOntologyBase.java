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
package abstractTests.lp;

import helper.LPHelper;
import helper.Results;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.omwg.ontology.Ontology;

import abstractTests.LP;

public abstract class AbstractRelations4ImportsOntologyBase extends TestCase implements LP {

	protected abstract void loadOntologies( Set<Ontology> ontologies ) throws Exception;
	
	public void testRelationPopulated() throws Exception
	{
		Set<Ontology> ontologies = new HashSet<Ontology>();

		loadOntologies( ontologies );
		
//		for( Ontology ontology : ontologies )
//			System.out.println( OntologyHelper.toString( ontology ) );
			
		String NS = "http://example.com/relations4b#";

		String query = "_\"http://example.com/relations4a#binary\"(?x,?y)";
    	Results r = new Results( "x", "y" );
    	r.addBinding( Results.iri( NS + "i1" ), Results.iri( NS + "i2" ) );
		
//    	System.out.println( OntologyHelper.toString( LPHelper.executeQuery( ontologies, query, getLPReasoner() ) ) );
		LPHelper.executeQueryAndCheckResults( ontologies, query, r.get(), getLPReasoner() );
	}
}
