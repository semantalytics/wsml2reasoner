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
import helper.OntologyHelper;
import helper.Results;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.omwg.ontology.Ontology;

import abstractTests.LP;

public abstract class AbstractRelations5Example extends TestCase implements LP {

	public void testQuery() throws Exception
	{
		Set<Ontology> ontologies = new HashSet<Ontology>();

		// This works when loading o2 before o1, but not the other way round. It should not matter. 
		Ontology o1 = OntologyHelper.loadOntology( "relations5_instance.wsml" );
		Ontology o2 = OntologyHelper.loadOntology( "relations5_sub.wsml" );
		Ontology o3 = OntologyHelper.loadOntology( "relations5_super.wsml" );
		ontologies.add( o1 );
		ontologies.add( o2 );
		ontologies.add( o3 );
		
		String NS_SUB = "http://example.com/relations5_sub#";
		String NS_INSTANCE = "http://example.com/relations5_instance#";

		String queryString = "_\"" + NS_SUB + "loves\"(?x,?y)";
		
    	Results r = new Results( "x", "y" );
    	
    	r.addBinding( Results.iri( NS_INSTANCE + "Peter" ), Results.iri( NS_INSTANCE + "Mary" ) );

//    	System.out.println( OntologyHelper.toString( LPHelper.executeQuery( ontologies, queryString, getLPReasoner() ) ) );
//    	if(true)return;
		LPHelper.executeQueryAndCheckResults( ontologies, queryString, r.get(), getLPReasoner() );
	}
}