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
import junit.framework.TestCase;
import abstractTests.LP;

public abstract class AbstractAttribute3InheritingImpliesType extends TestCase implements LP {

	private static final String ONTOLOGY_FILE = "attribute3_inheriting_implies_type.wsml";
	private static final String NS = "http://example.com/attribute3#";
//	private static final String WSML_STRING = "http://www.wsmo.org/wsml/wsml-syntax#string";
	private static final String XSD_STRING = "http://www.w3.org/2001/XMLSchema#string";
	
	public void testAttributeInheritance() throws Exception {

		Results r = new Results("concept", "attribute", "type");
		
		for( int i = 1; i <= 10; ++i )
		{
			for( int a = i; a >= 1; --a )
			{
				r.addBinding( Results.iri( NS + "c" + i ), Results.iri( NS + "a" + a ), Results.iri( XSD_STRING ) );
			}
		}

//		System.out.println( OntologyHelper.toString( LPHelper.executeQuery(OntologyHelper.loadOntology(ONTOLOGY_FILE), "?concept[?attribute impliesType ?type]", getLPReasoner()) ) );
		LPHelper.executeQueryAndCheckResults(OntologyHelper.loadOntology(ONTOLOGY_FILE), "?concept[?attribute impliesType ?type]", r.get(), getLPReasoner());
		
	}
}