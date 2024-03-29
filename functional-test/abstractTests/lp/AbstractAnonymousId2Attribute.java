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

import helper.LPHelper;
import helper.OntologyHelper;
import junit.framework.TestCase;
import abstractTests.LP;

public abstract class AbstractAnonymousId2Attribute extends TestCase implements LP {

	public void testAnonymousIdInAttributeValue() throws Exception {
		
		// This should not throw an exception
//		Set<Map<Variable, Term>> results = 
			LPHelper.executeQuery( OntologyHelper.loadOntology( "files/anonymous_id2_attribute.wsml" ),
	    					"?x[hasC hasValue ?c]", getLPReasoner() );
//		System.out.println( OntologyHelper.toString( results ) );
	}	
}
