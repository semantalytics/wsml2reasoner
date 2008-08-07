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
import org.wsml.reasoner.api.WSMLReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.api.WSMLReasonerFactory.BuiltInReasoner;
import org.wsml.reasoner.impl.DefaultWSMLReasonerFactory;
import org.wsmo.factory.Factory;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.Parser;

import base.BaseReasonerTest;

/**
 * Tests if the ontologies can be registered and a simple query executed, because
 * unexpected 'program not stratified' exceptions have been thrown from the reasoner.
 */
public class NotStratifiedTest extends BaseReasonerTest {

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
		BaseReasonerTest.reasoner = WSMLReasonerFactory.BuiltInReasoner.IRIS;
//		wsmlReasoner = BaseReasonerTest.getReasoner();
		wsmlReasoner = (LPReasoner) getIrisReasoner();
		wsmoFactory = Factory.createWsmoFactory(null);
		leFactory = Factory.createLogicalExpressionFactory(null);
		parser = Factory.createParser(null);
	}
	
    @Override
    protected void tearDown() throws Exception {
    	super.tearDown();
    	resetReasoner(previous);
    }
    
    private WSMLReasoner getIrisReasoner()
    {
    	Map<String, Object> params = new HashMap<String, Object>();
    	params.put( WSMLReasonerFactory.PARAM_BUILT_IN_REASONER, BuiltInReasoner.IRIS );
    	return DefaultWSMLReasonerFactory.getFactory().createRuleReasoner( params );
    }

    public void testOntology1() throws Exception
	{
		helperTestOntology( "files/StratifiedTest1.wsml" );
	}
	
	public void testOntology2() throws Exception
	{
		helperTestOntology( "files/StratifiedTest2.wsml" );
	}
	
	private void helperTestOntology( String strOntology ) throws Exception
	{
		// read test file and parse it 
       InputStream is = this.getClass().getClassLoader().getResourceAsStream(
    				   //"files/not_stratified.wsml");
    				   strOntology );
       assertNotNull(is);
       // assuming first topentity in file is an ontology  
       ontology = (Ontology)parser.parse(new InputStreamReader(is))[0]; 
       String nsp = ontology.getDefaultNamespace().getIRI().toString();
       
       // register ontology at the wsml reasoner
       wsmlReasoner.registerOntology(ontology);       
       
       // build query
       String query = "?x memberOf ?y";
       
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
      
       Set<String> resultSet = new HashSet<String>(2);
       int i=0;
       for (Map<Variable, Term> resultBinding : result) {
    	   System.out.println("result binding (" + (i++) + "): " + resultBinding.toString());
    	   resultSet.add(resultBinding.entrySet().iterator().next().getValue().toString());
       }
       
       wsmlReasoner.deRegister();
	}
}
