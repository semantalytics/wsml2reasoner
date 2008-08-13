/**
 * WSML Reasoner Implementation.
 *
 * Copyright (c) 2006, University of Innsbruck Austria
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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import junit.framework.TestCase;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.api.inconsistency.InconsistencyException;
import abstractTests.LPTest;

public abstract class AbstractConsistencyViolation extends TestCase implements LPTest {

    private static final String ONTOLOGY_FILE = "files/InconsistentOntology.wsml";
    
    public void testInconsistency() throws Exception {
        String query = "?x memberOf ?y";
        Set<Map<Variable, Term>> expected = new HashSet<Map<Variable, Term>>();
        try{
            LPHelper.executeQueryAndCheckResults( OntologyHelper.loadOntology( ONTOLOGY_FILE ), query, expected, getLPReasoner() );
            fail("Should have thrown InconsistencyException");
        }
        catch (InconsistencyException e){
        }
    }
}
