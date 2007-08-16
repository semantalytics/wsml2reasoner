/**
 * WSML Reasoner Implementation.
 *
 * Copyright (c) 2005, FZI, Germany.
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
package variant.flight;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.api.WSMLFlightReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.api.WSMLReasonerFactory.BuiltInReasoner;
import org.wsmo.common.IRI;

import base.BaseReasonerTest;

public class DogsworldTest extends BaseReasonerTest {
    private static final String NS = "urn:dogsworld#";
    private static final String ONTOLOGY_FILE = "files/dogsworld.wsml";

    BuiltInReasoner previous;
    
    @Override
    protected void setUp() throws Exception {
    	super.setUp();
    	setupScenario(ONTOLOGY_FILE);
    	previous = BaseReasonerTest.reasoner;
    }
    
    @Override
    protected void tearDown() throws Exception {
    	super.tearDown();
    	resetReasoner(previous);
    }
    
    public void subconceptsOfMammal() throws Exception {
        String query = "?x subConceptOf Mammal";
        Set<Map<Variable,Term>> expected = new HashSet<Map<Variable,Term>>();
        Map<Variable, Term> binding = new HashMap<Variable, Term>();
        binding.put(leFactory.createVariable("x"), wsmoFactory.createIRI(NS + "Human"));
        expected.add(binding);
        binding = new HashMap<Variable, Term>();
        binding.put(leFactory.createVariable("x"), wsmoFactory.createIRI(NS + "DogOwner"));
        expected.add(binding);
        binding = new HashMap<Variable, Term>();
        binding.put(leFactory.createVariable("x"), wsmoFactory.createIRI(NS + "Mammal"));
        expected.add(binding);
        performQuery(query, expected);
    }
    
    public void consistencyChecker() throws Exception {
        assertTrue(((WSMLFlightReasoner)wsmlReasoner).isSatisfiable((IRI)o.getIdentifier()));
    }

    public void instanceRetrieval() throws Exception {
        String query = "Anne memberOf CatOwner";
        LogicalExpression qExpression = leFactory.createLogicalExpression(query, o);
        assertTrue(wsmlReasoner.executeGroundQuery((IRI)o.getIdentifier(), qExpression));
    }
    
    public void testFlightReasoners() throws Exception{
    	resetReasoner(WSMLReasonerFactory.BuiltInReasoner.IRIS);
    	subconceptsOfMammal();
    	consistencyChecker();
    	instanceRetrieval();
    	
    	resetReasoner(WSMLReasonerFactory.BuiltInReasoner.MINS);
    	subconceptsOfMammal();
    	consistencyChecker();
    	instanceRetrieval();
    	
    	resetReasoner(WSMLReasonerFactory.BuiltInReasoner.KAON2);
    	subconceptsOfMammal();
    	consistencyChecker();
    	instanceRetrieval();
    }

}
