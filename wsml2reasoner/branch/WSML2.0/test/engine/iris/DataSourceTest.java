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
package engine.iris;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.deri.wsmo4j.io.parser.wsml.LogicalExpressionParserImpl;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.LogicalExpressionParser;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Variable;
import org.sti2.wsmo4j.factory.FactoryImpl;
import org.wsml.reasoner.api.LPReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory.BuiltInReasoner;
import org.wsml.reasoner.api.data.ExternalDataSource;
import org.wsml.reasoner.builtin.iris.IrisStratifiedFacade;
import org.wsmo.common.IRI;
import org.wsmo.factory.WsmoFactory;

import base.BaseReasonerTest;

/**
 * This is a modified version of the dogs world test, where the ontology file
 * doesn't have any instances. The instances are loaded from a data source.
 * 
 * @author Richard Pöttler (richard dot poettler at sti2 dot at)
 */
public class DataSourceTest extends BaseReasonerTest {
	private static final String NS = "urn:dogsworld#";
	private static final String ONTOLOGY_FILE = "files/dogsworld_no_instances.wsml";

	BuiltInReasoner previous;

	protected void setUp() throws Exception {
		super.setUp();
		
		// add the data source to the configuration
		final Map<String, Object> config = new HashMap<String, Object>();
		config.put(IrisStratifiedFacade.EXTERNAL_DATA_SOURCE, 
				Collections.singleton(new DogSource()));
		
		setupScenario(ONTOLOGY_FILE, config);
		previous = BaseReasonerTest.reasoner;
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		resetReasoner(previous);
	}

	public void testSubconceptsOfMammal() throws Exception {
		String query = "?x subConceptOf Mammal";
		Set<Map<Variable, Term>> expected = new HashSet<Map<Variable, Term>>();
		Map<Variable, Term> binding = new HashMap<Variable, Term>();
		binding.put(leFactory.createVariable("x"), wsmoFactory.createIRI(NS
				+ "Human"));
		expected.add(binding);
		binding = new HashMap<Variable, Term>();
		binding.put(leFactory.createVariable("x"), wsmoFactory.createIRI(NS
				+ "DogOwner"));
		expected.add(binding);
		binding = new HashMap<Variable, Term>();
		binding.put(leFactory.createVariable("x"), wsmoFactory.createIRI(NS
				+ "Mammal"));
		expected.add(binding);
		performQuery(query, expected);
	}

	public void testConsistencyChecker() throws Exception {
		assertTrue(((LPReasoner) wsmlReasoner).checkConsistency().size() == 0);
	}

	public void testInstanceRetrieval() throws Exception {
		String query = "Anne memberOf CatOwner";
		LogicalExpressionParser leParser = new LogicalExpressionParserImpl();
		LogicalExpression qExpression = leParser.parse(
				query);
		assertTrue(((LPReasoner) wsmlReasoner).ask(qExpression));
	}

	/**
	 * Data source to get the hasValue and memberOf facts for the dogs world ontology.
	 * 
	 * @author Richard Pöttler (richard dot poettler at sti2 dot at)
	 */
	private static class DogSource implements ExternalDataSource {
		
		/** Factory to create the wsmo objects. */
		private final WsmoFactory WSMO_FACTORY;

		/** Facts for the has-value relation. */
		private final Set<HasValue> hasValue = new HashSet<HasValue>();

		/** Facts for the member-of relation. */
		private final Set<MemberOf> memberOf = new HashSet<MemberOf>();

		public DogSource() {
			WSMO_FACTORY = (new FactoryImpl()).getWsmoFactory();
			
			// creating the has-value set
			// wsml-has-value(urn:dogsworld#Arthur, urn:dogsworld#barksAt, urn:dogsworld#Anne).
			hasValue.add(new HasValue(WSMO_FACTORY.createIRI(NS + "Arthur"), 
					WSMO_FACTORY.createIRI(NS + "barksAt"), 
					WSMO_FACTORY.createIRI(NS + "Anne")));
			// wsml-has-value(urn:dogsworld#Clare, urn:dogsworld#owns, urn:dogsworld#Arthur).
			hasValue.add(new HasValue(WSMO_FACTORY.createIRI(NS + "Clare"), 
					WSMO_FACTORY.createIRI(NS + "owns"), 
					WSMO_FACTORY.createIRI(NS + "Arthur")));
			// wsml-has-value(urn:dogsworld#Anne, urn:dogsworld#owns, urn:dogsworld#Paul).
			hasValue.add(new HasValue(WSMO_FACTORY.createIRI(NS + "Anne"), 
					WSMO_FACTORY.createIRI(NS + "owns"), 
					WSMO_FACTORY.createIRI(NS + "Paul")));
			
			// creating the member-of set
			// wsml-member-of(urn:dogsworld#Paul, urn:dogsworld#BigDog).
			memberOf.add(new MemberOf(WSMO_FACTORY.createIRI(NS + "Paul"), 
					WSMO_FACTORY.createIRI(NS + "BigDog")));
			// wsml-member-of(urn:dogsworld#Anne, urn:dogsworld#DogOwner).
			memberOf.add(new MemberOf(WSMO_FACTORY.createIRI(NS + "Anne"), 
					WSMO_FACTORY.createIRI(NS + "DogOwner")));
			// wsml-member-of(urn:dogsworld#Arthur, urn:dogsworld#SmallDog).
			memberOf.add(new MemberOf(WSMO_FACTORY.createIRI(NS + "Arthur"), 
					WSMO_FACTORY.createIRI(NS + "SmallDog")));
			// wsml-member-of(urn:dogsworld#Clare, urn:dogsworld#DogOwner).
			memberOf.add(new MemberOf(WSMO_FACTORY.createIRI(NS + "Clare"), 
					WSMO_FACTORY.createIRI(NS + "DogOwner")));
		}

		public Set<HasValue> hasValue(IRI id, IRI name, Term value) {
			return Collections.unmodifiableSet(hasValue);
		}

		public Set<MemberOf> memberOf(IRI id, IRI concept) {
			return Collections.unmodifiableSet(memberOf);
		}
	}
}
