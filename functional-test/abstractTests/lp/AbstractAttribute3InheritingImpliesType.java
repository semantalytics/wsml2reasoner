/**
 * WSML2Reasoner
 * An extensible framework for reasoning with WSML ontologies.
 * 
 * Copyright (C) 2008 Semantic Technology Institute (STI) Innsbruck, 
 * University of Innsbruck, Technikerstrasse 21a, 6020 Innsbruck, Austria.
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
package abstractTests.lp;

import helper.LPHelper;
import helper.OntologyHelper;
import helper.Results;
import junit.framework.TestCase;
import abstractTests.LP;

public abstract class AbstractAttribute3InheritingImpliesType extends TestCase implements LP {

	private static final String ONTOLOGY_FILE = "files/attribute3_inheriting_implies_type.wsml";
	private static final String NS = "http://example.com/attribute3#";
	private static final String WSML_STRING = "http://www.wsmo.org/wsml/wsml-syntax#string";
	
	public void testAttributeInheritance() throws Exception {

		Results r = new Results("concept", "attribute", "type");
		
		for( int i = 1; i <= 10; ++i )
		{
			for( int a = i; a >= 1; --a )
			{
				r.addBinding( Results.iri( NS + "c" + i ), Results.iri( NS + "a" + a ), Results.iri( WSML_STRING ) );
			}
		}

//		System.out.println( OntologyHelper.toString( LPHelper.executeQuery(OntologyHelper.loadOntology(ONTOLOGY_FILE), "?i[?attr impliesType ?type]", getLPReasoner()) ) );
		LPHelper.executeQueryAndCheckResults(OntologyHelper.loadOntology(ONTOLOGY_FILE), "?concept[?attribute impliesType ?type]", r.get(), getLPReasoner());
		
	}
}
