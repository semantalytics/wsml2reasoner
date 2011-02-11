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

import helper.OntologyHelper;

import java.util.Set;

import org.omwg.ontology.Ontology;

public abstract class AbstractRelations4ImportsOntologyReversed extends AbstractRelations4ImportsOntologyBase
{
	protected void loadOntologies( Set<Ontology> ontologies ) throws Exception
	{
		// The order should not matter, but it does at the moment.
		// Loading/parsing 4a and then 4b works.
		// Loading/parsing 4b and then 4a does not work.
		Ontology o1 = OntologyHelper.loadOntology( "relations4_relation_instances.wsml" );
		Ontology o2 = OntologyHelper.loadOntology( "relations4_relation_definitions.wsml" );
		ontologies.add( o1 );
		ontologies.add( o2 );
	}	
}
