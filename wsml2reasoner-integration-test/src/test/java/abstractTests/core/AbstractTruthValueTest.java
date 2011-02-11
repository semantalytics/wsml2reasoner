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
package abstractTests.core;

import helper.LPHelper;
import helper.OntologyHelper;

import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Variable;
import org.sti2.wsmo4j.factory.WsmlFactoryContainer;
import org.wsml.reasoner.api.LPReasoner;
import org.wsml.reasoner.api.inconsistency.InconsistencyException;
import org.wsmo.factory.FactoryContainer;
import org.wsmo.wsml.ParserException;

import abstractTests.LP;

public abstract class AbstractTruthValueTest extends TestCase implements LP {

	private static final String PATH = "truth_value.wsml";
	
	private LPReasoner reasoner; 
	
	private Ontology ontology;
	
	private FactoryContainer container;
	
	public AbstractTruthValueTest() {
		super();
	}

	public AbstractTruthValueTest(String name) {
		super(name);
	}

	public void setUp() throws Exception {
		container = new WsmlFactoryContainer();
		
		ontology = OntologyHelper.loadOntology(PATH);
		
		reasoner = getLPReasoner();
		reasoner.registerOntology(ontology);
	}
	
	public void testWsmlTrue() throws ParserException, InconsistencyException {
		String query = "hasCrowbar(?x)";
		checkResult(query);
	}
	
	public void testUniversalTrue() throws ParserException, InconsistencyException {
		String query = "isAwesome(?x)";
		checkResult(query);
	}
	
	public void testWsmlFalse() throws ParserException, InconsistencyException {
		String query = "hasRailgun(?x)";
		checkEmptyResult(query);
	}
	
	public void testUniversalFalse() throws ParserException, InconsistencyException {
		String query = "isSeriousSam(?x)";
		checkEmptyResult(query);
	}
	
	private void checkEmptyResult(String query) throws ParserException, InconsistencyException {
		Set<Map<Variable, Term>> result = LPHelper.executeQuery(ontology, query, reasoner);
		
		assertEquals(query + " result is not empty", 0, result.size());
	}
	
	private void checkResult(String query) throws ParserException, InconsistencyException {
		Set<Map<Variable, Term>> result = LPHelper.executeQuery(ontology, query, reasoner);
		
		assertEquals(query + " result of incorrect size", 1, result.size());
		
		Map<Variable, Term> binding = result.iterator().next();
		Variable x = container.getLogicalExpressionFactory().createVariable("x");
		
		Term term = binding.get(x);
		
		assertNotNull(term);
		assertEquals(query + " did not return correct instance", "http://example.com/truthValue#Gordon", term.toString());
	}
	
}
