/**
 * WSML Reasoner Implementation.
 *
 * Copyright (c) 2008, University of Innsbruck, Austria.
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
package abstractTests.lp;

import java.util.Map;
import java.util.Set;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Variable;
import helper.LPHelper;
import helper.OntologyHelper;
import junit.framework.TestCase;
import abstractTests.LP;

public abstract class AbstractAnonymousId1Relation extends TestCase implements LP {

    public void testAnonymousIdInRelation() throws Exception {

		Set<Map<Variable, Term>> results =
			LPHelper.executeQuery( OntologyHelper.loadOntology( "files/anonymous_id1_relation.wsml" ),
						"rel(?c, ?d)", getLPReasoner() );
//    	System.out.println( OntologyHelper.toString( results ) );
    	
    	assertEquals( 2, results.size() );
    }
	
}
