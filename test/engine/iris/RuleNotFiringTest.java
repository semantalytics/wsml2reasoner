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
package engine.iris;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.api.LPReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.api.WSMLReasonerFactory.BuiltInReasoner;
import org.wsmo.factory.Factory;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.Parser;

import base.BaseReasonerTest;

public class RuleNotFiringTest extends BaseReasonerTest {

	private WsmoFactory wsmoFactory;
	
	private LogicalExpressionFactory leFactory;
	
	private LPReasoner wsmlReasoner;
	
	private BuiltInReasoner previous;

	private Parser parser;
	   
	private Ontology ontology;
	   
	protected void setUp() throws Exception {
		super.setUp();
			
		// get a reasoner
		// currently set to IRIS since the other reasoning engines
		// cannot yet handle such built-ins
		previous = BaseReasonerTest.reasoner;
		BaseReasonerTest.reasoner = WSMLReasonerFactory.BuiltInReasoner.IRIS_STRATIFIED;
		wsmlReasoner =  (LPReasoner) BaseReasonerTest.getReasoner();
		wsmoFactory = Factory.createWsmoFactory(null);
		leFactory = Factory.createLogicalExpressionFactory(null);
		parser = Factory.createParser(null);
	}
	
	public void testRun() throws Exception {
		// read test file and parse it 
       InputStream is = this.getClass().getClassLoader().getResourceAsStream(
               "files/wsml-rules.wsml");
       assertNotNull(is);
       // assuming first topentity in file is an ontology  
       ontology = (Ontology)parser.parse(new InputStreamReader(is))[0]; 
       String nsp = ontology.getDefaultNamespace().getIRI().toString();
       
       // register ontology at the wsml reasoner
       wsmlReasoner.registerOntology(ontology);       
       
       // build query
       String query = "o2#mediated1(?X13,o2#Citizen)[o2#hasName hasValue " + 
       					"o2#mediated1(?X13,o2#Name)] memberOf o2#Citizen";
       
       LogicalExpression qExpression = leFactory.createLogicalExpression(
               query, ontology);
       
       // build set with expected results
       Set<Map<Variable, Term>> expected = new HashSet<Map<Variable, Term>>();
       Map<Variable, Term> binding = new HashMap<Variable, Term>();
       binding.put(leFactory.createVariable("X13"), wsmoFactory.createIRI(nsp + "me"));
       expected.add(binding);
       
       // perform query
       System.out.println("WSML Query LE:");
       System.out.println(qExpression.toString());
       System.out.println("\n\nExpecting " + expected.size() + " results...\n");
       Set<Map<Variable, Term>> result = wsmlReasoner.executeQuery(qExpression);
       System.out.println("Found < " + result.size() + " > results to the query:\n");
      
       // assert that expected result set size equals actual result set size
       assertEquals(expected.size(), result.size());

       Set<String> resultSet = new HashSet<String>(2);
       int i=0;
       for (Map<Variable, Term> resultBinding : result) {
    	   System.out.println("result binding (" + (i++) + "): " + resultBinding.toString());
    	   resultSet.add(resultBinding.entrySet().iterator().next().getValue().toString());
       }
       
       assertTrue(resultSet.contains(nsp + "me"));
		
       wsmlReasoner.deRegister();
	}
	
    @Override
    protected void tearDown() throws Exception {
    	// TODO Auto-generated method stub
    	super.tearDown();
    	resetReasoner(previous);
    }

	
}
