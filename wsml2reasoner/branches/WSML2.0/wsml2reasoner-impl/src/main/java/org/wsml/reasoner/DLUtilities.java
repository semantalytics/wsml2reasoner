package org.wsml.reasoner;

import static org.wsml.reasoner.TransformerPredicates.PRED_DIRECT_CONCEPT;
import static org.wsml.reasoner.TransformerPredicates.PRED_DIRECT_SUBCONCEPT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.deri.wsmo4j.io.parser.wsml.WsmlLogicalExpressionParser;
import org.omwg.logicalexpression.Atom;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.LogicalExpressionParser;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Concept;
import org.omwg.ontology.Instance;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.api.LPReasoner;
import org.wsml.reasoner.api.exception.InternalReasonerException;
import org.wsml.reasoner.transformation.ConstraintReplacementNormalizer;
import org.wsmo.common.IRI;
import org.wsmo.common.Identifier;
import org.wsmo.factory.FactoryContainer;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.ParserException;

public class DLUtilities {

	private final WsmoFactory wsmoFactory;

	private final LogicalExpressionFactory leFactory;

	private final LPReasoner mReasoner;

	public DLUtilities(LPReasoner reasoner, FactoryContainer factory) {

		this.wsmoFactory = factory.getWsmoFactory();
		this.leFactory = factory.getLogicalExpressionFactory();
		this.mReasoner = reasoner;
	}

	public boolean isSatisfiable() {
		IRI violationIRI = wsmoFactory
				.createIRI(ConstraintReplacementNormalizer.VIOLATION_IRI);
		Atom violation = leFactory.createAtom(violationIRI,
				new ArrayList<Term>());
		return !mReasoner.ask(violation);
	}

	public Set<Concept> getConcepts(Instance instance) {
		// build membership query:
		Term instanceID = wsmoFactory.createIRI(instance.getIdentifier()
				.toString());
		Term conceptVariable = leFactory.createVariable("x");
		LogicalExpression query = leFactory.createMemberShipMolecule(
				instanceID, conceptVariable);

		// submit query to reasoner:
		Set<Map<Variable, Term>> bindings = mReasoner.executeQuery(query);

		// extract concepts from result:
		Set<Concept> concepts = new HashSet<Concept>();
		for (Map<Variable, Term> binding : bindings) {
			IRI conceptID = (IRI) binding.get(leFactory.createVariable("x"));
			concepts.add(wsmoFactory.createConcept(conceptID));
		}
		return concepts;
	}

	public Set<Instance> getInstances(Concept concept) {
		// build query:
		Term conceptID = wsmoFactory.createIRI(concept.getIdentifier()
				.toString());
		Term instanceVariable = leFactory.createVariable("x");
		LogicalExpression query = leFactory.createMemberShipMolecule(
				instanceVariable, conceptID);

		// submit query to reasoner:
		Set<Map<Variable, Term>> bindings = mReasoner.executeQuery(query);

		// extract concepts from result:
		Set<Instance> instances = new HashSet<Instance>();
		for (Map<Variable, Term> binding : bindings) {
			IRI instanceID = (IRI) binding.get(leFactory.createVariable("x"));
			instances.add(wsmoFactory.createInstance(instanceID));
		}

		return instances;
	}

	public Set<Concept> getSubConcepts(Concept concept) {
		// build query:
		Term conceptID = wsmoFactory.createIRI(concept.getIdentifier()
				.toString());
		Term conceptVariable = leFactory.createVariable("x");
		LogicalExpression query = leFactory.createSubConceptMolecule(
				conceptVariable, conceptID);

		// submit query to reasoner:
		Set<Map<Variable, Term>> bindings = mReasoner.executeQuery(query);

		// extract concepts from result:
		Set<Concept> concepts = new HashSet<Concept>();
		for (Map<Variable, Term> binding : bindings) {
			IRI subConceptID = (IRI) binding.get(leFactory.createVariable("x"));
			concepts.add(wsmoFactory.createConcept(subConceptID));
		}
		if (concepts.contains(concept)) {
			concepts.remove(concept);
		}
		return concepts;
	}

	public Set<Concept> getDirectSubConcepts(Concept concept) {
		// build query:
		Term conceptID = concept.getIdentifier();
		LogicalExpression query = null;
		Set<Map<Variable, Term>> bindings;
		LogicalExpressionParser leParser = new WsmlLogicalExpressionParser();
		try {
			query = leParser.parse("_\"" + PRED_DIRECT_SUBCONCEPT
					+ "\"(?x, _\"" + conceptID.toString() + "\")");
			// submit query to reasoner:
			bindings = mReasoner.executeQuery(query);
		} catch (ParserException e) {
			throw new InternalReasonerException();
		}

		// extract concepts from result:
		Set<Concept> concepts = new HashSet<Concept>();
		for (Map<Variable, Term> binding : bindings) {
			IRI subConceptID = (IRI) binding.get(leFactory.createVariable("x"));
			concepts.add(wsmoFactory.createConcept(subConceptID));
		}
		if (concepts.contains(concept)) {
			concepts.remove(concept);
		}
		return concepts;
	}

	public Set<Concept> getSuperConcepts(Concept concept) {
		// build query:
		Term conceptID = wsmoFactory.createIRI(concept.getIdentifier()
				.toString());
		Term conceptVariable = leFactory.createVariable("x");
		LogicalExpression query = leFactory.createSubConceptMolecule(conceptID,
				conceptVariable);

		// submit query to reasoner:
		Set<Map<Variable, Term>> bindings = mReasoner.executeQuery(query);

		// extract concepts from result:
		Set<Concept> concepts = new HashSet<Concept>();
		for (Map<Variable, Term> binding : bindings) {
			IRI superConceptID = (IRI) binding.get(leFactory
					.createVariable("x"));
			concepts.add(wsmoFactory.createConcept(superConceptID));
		}
		if (concepts.contains(concept)) {
			concepts.remove(concept);
		}
		return concepts;
	}

	public Set<Concept> getDirectSuperConcepts(Concept concept) {
		// build query:
		Term conceptID = concept.getIdentifier();
		LogicalExpression query = null;
		Set<Map<Variable, Term>> bindings;
		LogicalExpressionParser leParser = new WsmlLogicalExpressionParser();
		try {
			query = leParser.parse("_\"" + PRED_DIRECT_SUBCONCEPT + "\"(_\""
					+ conceptID.toString() + "\", ?x)");

			// submit query to reasoner:
			bindings = mReasoner.executeQuery(query);
		} catch (ParserException e) {
			throw new InternalReasonerException();
		}

		// extract concepts from result:
		Set<Concept> concepts = new HashSet<Concept>();
		for (Map<Variable, Term> binding : bindings) {
			IRI subConceptID = (IRI) binding.get(leFactory.createVariable("x"));
			concepts.add(wsmoFactory.createConcept(subConceptID));
		}
		if (concepts.contains(concept)) {
			concepts.remove(concept);
		}
		return concepts;
	}

	public boolean isMemberOf(Instance instance, Concept concept) {
		// build query:
		Term conceptID = wsmoFactory.createIRI(concept.getIdentifier()
				.toString());
		Term instanceID = wsmoFactory.createIRI(instance.getIdentifier()
				.toString());
		LogicalExpression query = leFactory.createMemberShipMolecule(
				instanceID, conceptID);

		// submit query to reasoner:
		Set<Map<Variable, Term>> bindings = mReasoner.executeQuery(query);

		// check for non-empty result:
		return bindings.size() != 0;
	}

	public boolean isSubConceptOf(Concept subConcept, Concept superConcept) {
		// build query:
		Term superconceptID = wsmoFactory.createIRI(superConcept
				.getIdentifier().toString());
		Term subconceptID = wsmoFactory.createIRI(subConcept.getIdentifier()
				.toString());
		LogicalExpression query = leFactory.createSubConceptMolecule(
				subconceptID, superconceptID);

		// / submit query to reasoner:
		Set<Map<Variable, Term>> bindings = mReasoner.executeQuery(query);

		// check for non-empty result:
		return bindings.size() != 0;
	}

	public Set<Concept> getAllConcepts() {
		// Create some variables to use in all the queries.
		Term instance = leFactory.createVariable("instance");
		Term concept1 = leFactory.createVariable("concept1");
		Term concept2 = leFactory.createVariable("concept2");

		// Store all the discovered concepts here.
		Set<Concept> concepts = new HashSet<Concept>();

		// memberOf
		LogicalExpression query = leFactory.createMemberShipMolecule(instance,
				concept1);

		Set<Map<Variable, Term>> bindings = mReasoner.executeQuery(query);
		extractConcepts(bindings, concept1, concepts);

		// subConceptOf
		query = leFactory.createSubConceptMolecule(concept1, concept2);

		bindings = mReasoner.executeQuery(query);
		extractConcepts(bindings, concept1, concepts);
		extractConcepts(bindings, concept2, concepts);

		// ofType
		query = leFactory.createAttributeConstraint(concept1, instance,
				concept2);

		bindings = mReasoner.executeQuery(query);

		extractConcepts(bindings, concept1, concepts);
		extractConcepts(bindings, concept2, concepts);

		// impliesType
		query = leFactory
				.createAttributeInference(concept1, instance, concept2);

		bindings = mReasoner.executeQuery(query);

		extractConcepts(bindings, concept1, concepts);
		extractConcepts(bindings, concept2, concepts);

		return concepts;
		/*
		 * This has been commented out, because concepts can also be instances
		 * of concepts, e.g. A can be a concept and also a member-of B
		 * 
		 * In other words, this filtering should not be done.
		 * 
		 * Set<Instance> tmpInst = getAllInstances(); Set<IRI> tmpAttr =
		 * getAllAttributes(); Set<Concept> tmpConc = new
		 * HashSet<Concept>(concepts);
		 * 
		 * for (Concept concept : tmpConc) { IRI conceptID = (IRI)
		 * concept.getIdentifier(); for (Instance instance : tmpInst) { if
		 * (conceptID.equals(instance.getIdentifier())) {
		 * concepts.remove(concept); } } for (IRI attribute : tmpAttr) { if
		 * (conceptID.equals(attribute)) {
		 * concepts.remove(wsmoFactory.getConcept(attribute)); } } }
		 * 
		 * return concepts;
		 */
	}

	public Set<Instance> getAllInstances() {
		// build membership query:
		Term instanceVariable = leFactory.createVariable("x");
		Term variable = leFactory.createVariable("y");
		Term attributeVariable = leFactory.createVariable("z");
		LogicalExpression query = leFactory.createMemberShipMolecule(
				instanceVariable, variable);

		// submit query to reasoner:
		Set<Map<Variable, Term>> bindings = mReasoner.executeQuery(query);

		// extract instances from result:
		Set<Instance> instances = new HashSet<Instance>();
		for (Map<Variable, Term> binding : bindings) {
			if (binding.get(leFactory.createVariable("x")) instanceof IRI) {
				IRI instanceID = (IRI) binding.get(leFactory
						.createVariable("x"));
				instances.add(wsmoFactory.createInstance(instanceID));
			}
		}

		// build new attribute value query:
		query = leFactory.createAttributeValue(instanceVariable,
				attributeVariable, variable);

		// submit query to reasoner:
		bindings = mReasoner.executeQuery(query);

		// extract instancesbindings from result:
		for (Map<Variable, Term> binding : bindings) {
			IRI instanceID = (IRI) binding.get(leFactory.createVariable("x"));
			if (!instanceID.getNamespace().toString()
					.startsWith("http://www.wsmo.org/wsml/wsml-syntax")) {
				instances.add(wsmoFactory.createInstance(instanceID));
			}
			if (binding.get(leFactory.createVariable("y")) instanceof IRI) {
				instanceID = (IRI) binding.get(leFactory.createVariable("y"));
				if (!instanceID.getNamespace().toString()
						.startsWith("http://www.wsmo.org/wsml/wsml-syntax")) {
					instances.add(wsmoFactory.createInstance(instanceID));
				}
			}
		}

		return instances;
	}

	public Set<IRI> getAllAttributes() {
		// build query for extracting constraining attributes:
		Term instanceVariable = leFactory.createVariable("x");
		Term attributeVariable = leFactory.createVariable("y");
		Term conceptVariable = leFactory.createVariable("z");
		LogicalExpression query = leFactory.createAttributeConstraint(
				instanceVariable, attributeVariable, conceptVariable);

		// submit query to reasoner:
		Set<Map<Variable, Term>> bindings = mReasoner.executeQuery(query);

		// extract instances from result:
		Set<IRI> attributes = new HashSet<IRI>();
		for (Map<Variable, Term> binding : bindings) {
			IRI attributeID = (IRI) binding.get(leFactory.createVariable("y"));
			attributes.add(attributeID);
		}

		// build new query for inferring attributes:
		query = leFactory.createAttributeInference(instanceVariable,
				attributeVariable, conceptVariable);

		// submit query to reasoner:
		bindings = mReasoner.executeQuery(query);

		// extract instances from result:
		for (Map<Variable, Term> binding : bindings) {
			IRI attributeID = (IRI) binding.get(leFactory.createVariable("y"));
			attributes.add(attributeID);
		}

		// build new query for attribute which have attribute values:
		query = leFactory.createAttributeValue(instanceVariable,
				attributeVariable, conceptVariable);

		// submit query to reasoner:
		bindings = mReasoner.executeQuery(query);

		// extract instances from result:
		for (Map<Variable, Term> binding : bindings) {
			IRI attributeID = (IRI) binding.get(leFactory.createVariable("y"));
			attributes.add(attributeID);
		}
		return attributes;
	}

	public Set<IRI> getAllConstraintAttributes() {
		// build query:
		Term instanceVariable = leFactory.createVariable("x");
		Term attributeVariable = leFactory.createVariable("y");
		Term conceptVariable = leFactory.createVariable("z");
		LogicalExpression query = leFactory.createAttributeConstraint(
				instanceVariable, attributeVariable, conceptVariable);

		// submit query to reasoner:
		Set<Map<Variable, Term>> bindings = mReasoner.executeQuery(query);

		// extract instances from result:
		Set<IRI> attributes = new HashSet<IRI>();
		for (Map<Variable, Term> binding : bindings) {
			IRI attributeID = (IRI) binding.get(leFactory.createVariable("y"));
			attributes.add(attributeID);
		}
		return attributes;
	}

	public Set<IRI> getAllInferenceAttributes() {
		// build query:
		Term instanceVariable = leFactory.createVariable("x");
		Term attributeVariable = leFactory.createVariable("y");
		Term conceptVariable = leFactory.createVariable("z");
		LogicalExpression query = leFactory.createAttributeInference(
				instanceVariable, attributeVariable, conceptVariable);

		// submit query to reasoner:
		Set<Map<Variable, Term>> bindings = mReasoner.executeQuery(query);

		// extract instances from result:
		Set<IRI> attributes = new HashSet<IRI>();
		for (Map<Variable, Term> binding : bindings) {
			IRI attributeID = (IRI) binding.get(leFactory.createVariable("y"));
			attributes.add(attributeID);
		}
		return attributes;
	}

	public Set<Concept> getDirectConcepts(Instance instance) {
		// build query:
		Term instanceID = instance.getIdentifier();
		LogicalExpression query = null;
		Set<Map<Variable, Term>> bindings;
		LogicalExpressionParser leParser = new WsmlLogicalExpressionParser();
		try {
			query = leParser.parse("_\"" + PRED_DIRECT_CONCEPT + "\"(_\""
					+ instanceID.toString() + "\", ?x)");
			// submit query to reasoner:
			bindings = mReasoner.executeQuery(query);
		} catch (ParserException e) {
			throw new InternalReasonerException();
		}

		// extract concepts from result:
		Set<Concept> concepts = new HashSet<Concept>();
		for (Map<Variable, Term> binding : bindings) {
			IRI conceptID = (IRI) binding.get(leFactory.createVariable("x"));
			concepts.add(wsmoFactory.createConcept(conceptID));
		}
		return concepts;
	}

	public Set<Concept> getConceptsOf(Identifier attributeId) {
		// build query:
		Term instanceVariable = leFactory.createVariable("x");
		Term valueVariable = leFactory.createVariable("y");
		Term conceptVariable = leFactory.createVariable("z");
		LogicalExpression queryPart1 = leFactory.createAttributeValue(
				instanceVariable, attributeId, valueVariable);
		LogicalExpression queryPart2 = leFactory.createMemberShipMolecule(
				instanceVariable, conceptVariable);
		LogicalExpression query = leFactory.createConjunction(queryPart1,
				queryPart2);

		// submit query to reasoner:
		Set<Map<Variable, Term>> bindings = mReasoner.executeQuery(query);

		// extract concepts from result:
		Set<Concept> concepts = new HashSet<Concept>();
		for (Map<Variable, Term> binding : bindings) {
			IRI conceptID = (IRI) binding.get(leFactory.createVariable("z"));
			concepts.add(wsmoFactory.createConcept(conceptID));
		}
		return concepts;
	}

	public Set<IRI> getRangesOfInferingAttribute(Identifier attributeId) {
		// build query:
		Term instanceVariable = leFactory.createVariable("x");
		Term valueVariable = leFactory.createVariable("y");
		LogicalExpression query = leFactory.createAttributeInference(
				instanceVariable, attributeId, valueVariable);

		// submit query to reasoner:
		Set<Map<Variable, Term>> bindings = mReasoner.executeQuery(query);

		// extract iris from result:
		Set<IRI> iris = new HashSet<IRI>();
		for (Map<Variable, Term> binding : bindings) {
			IRI id = (IRI) binding.get(leFactory.createVariable("y"));
			iris.add(id);
		}
		return iris;
	}

	public Set<IRI> getRangesOfConstraintAttribute(Identifier attributeId) {
		// build query:
		Term instanceVariable = leFactory.createVariable("x");
		Term valueVariable = leFactory.createVariable("y");
		LogicalExpression query = leFactory.createAttributeConstraint(
				instanceVariable, attributeId, valueVariable);

		// submit query to reasoner:
		Set<Map<Variable, Term>> bindings = mReasoner.executeQuery(query);

		// extract iris from result:
		Set<IRI> iris = new HashSet<IRI>();
		for (Map<Variable, Term> binding : bindings) {
			IRI id = (IRI) binding.get(leFactory.createVariable("y"));
			iris.add(id);
		}
		return iris;
	}

	public Map<IRI, Set<Term>> getInferingAttributeValues(Instance instance) {
		// build query:
		Term attributeVariable = leFactory.createVariable("x");
		Term valueVariable = leFactory.createVariable("y");
		Term valueVariable2 = leFactory.createVariable("w");
		Term instanceVariable = leFactory.createVariable("z");
		LogicalExpression queryPart1 = leFactory.createAttributeValue(
				instance.getIdentifier(), attributeVariable, valueVariable);
		LogicalExpression queryPart2 = leFactory.createAttributeInference(
				instanceVariable, attributeVariable, valueVariable2);
		LogicalExpression query = leFactory.createConjunction(queryPart1,
				queryPart2);

		// submit query to reasoner:
		Set<Map<Variable, Term>> bindings = mReasoner.executeQuery(query);

		// extract results:
		Map<IRI, Set<Term>> results = new HashMap<IRI, Set<Term>>();
		for (Map<Variable, Term> binding : bindings) {
			IRI attributeId = (IRI) binding.get(leFactory.createVariable("x"));
			if (results.containsKey(attributeId)) {
				Set<Term> temp = new HashSet<Term>();
				Term value = binding.get(leFactory.createVariable("y"));
				temp = results.get(attributeId);
				temp.add(value);
				results.put(attributeId, temp);
			} else {
				Set<Term> temp = new HashSet<Term>();
				Term value = binding.get(leFactory.createVariable("y"));
				temp.add(value);
				results.put(attributeId, temp);
			}
		}
		return results;
	}

	public Map<IRI, Set<Term>> getConstraintAttributeValues(Instance instance) {
		// build query:
		Term attributeVariable = leFactory.createVariable("x");
		Term valueVariable = leFactory.createVariable("y");
		Term valueVariable2 = leFactory.createVariable("w");
		Term instanceVariable = leFactory.createVariable("z");
		LogicalExpression queryPart1 = leFactory.createAttributeValue(
				instance.getIdentifier(), attributeVariable, valueVariable);
		LogicalExpression queryPart2 = leFactory.createAttributeConstraint(
				instanceVariable, attributeVariable, valueVariable2);
		LogicalExpression query = leFactory.createConjunction(queryPart1,
				queryPart2);

		// submit query to reasoner:
		Set<Map<Variable, Term>> bindings = mReasoner.executeQuery(query);

		// extract results:
		Map<IRI, Set<Term>> results = new HashMap<IRI, Set<Term>>();
		for (Map<Variable, Term> binding : bindings) {
			IRI attributeId = (IRI) binding.get(leFactory.createVariable("x"));
			if (results.containsKey(attributeId)) {
				Set<Term> temp = new HashSet<Term>();
				Term value = binding.get(leFactory.createVariable("y"));
				temp = results.get(attributeId);
				temp.add(value);
				results.put(attributeId, temp);
			} else {
				Set<Term> temp = new HashSet<Term>();
				Term value = binding.get(leFactory.createVariable("y"));
				temp.add(value);
				results.put(attributeId, temp);
			}
		}
		return results;
	}

	public Map<Instance, Set<Term>> getInferingAttributeInstances(
			Identifier attributeId) {
		// build query:
		Term valueVariable = leFactory.createVariable("x");
		Term valueVariable2 = leFactory.createVariable("z");
		Term instanceVariable = leFactory.createVariable("y");
		Term instanceVariable2 = leFactory.createVariable("w");
		LogicalExpression queryPart1 = leFactory.createAttributeValue(
				instanceVariable, attributeId, valueVariable);
		LogicalExpression queryPart2 = leFactory.createAttributeInference(
				instanceVariable2, attributeId, valueVariable2);
		LogicalExpression query = leFactory.createConjunction(queryPart1,
				queryPart2);

		// submit query to reasoner:
		Set<Map<Variable, Term>> bindings = mReasoner.executeQuery(query);

		// extract results:
		Map<Instance, Set<Term>> results = new HashMap<Instance, Set<Term>>();
		for (Map<Variable, Term> binding : bindings) {
			Instance instance = wsmoFactory.createInstance((IRI) binding
					.get(leFactory.createVariable("y")));
			if (results.containsKey(instance)) {
				Set<Term> temp = new HashSet<Term>();
				Term value = binding.get(leFactory.createVariable("x"));
				temp = results.get(instance);
				temp.add(value);
				results.put(instance, temp);
			} else {
				Set<Term> temp = new HashSet<Term>();
				Term value = binding.get(leFactory.createVariable("x"));
				temp.add(value);
				results.put(instance, temp);
			}
		}

		return results;
	}

	public Map<Instance, Set<Term>> getConstraintAttributeInstances(
			Identifier attributeId) {
		// build query:
		Term valueVariable = leFactory.createVariable("x");
		Term valueVariable2 = leFactory.createVariable("z");
		Term instanceVariable = leFactory.createVariable("y");
		Term instanceVariable2 = leFactory.createVariable("w");
		LogicalExpression queryPart1 = leFactory.createAttributeValue(
				instanceVariable, attributeId, valueVariable);
		LogicalExpression queryPart2 = leFactory.createAttributeConstraint(
				instanceVariable2, attributeId, valueVariable2);
		LogicalExpression query = leFactory.createConjunction(queryPart1,
				queryPart2);

		// submit query to reasoner:
		Set<Map<Variable, Term>> bindings = mReasoner.executeQuery(query);

		// extract results:
		Map<Instance, Set<Term>> results = new HashMap<Instance, Set<Term>>();
		for (Map<Variable, Term> binding : bindings) {
			Instance instance = wsmoFactory.createInstance((IRI) binding
					.get(leFactory.createVariable("y")));
			if (results.containsKey(instance)) {
				Set<Term> temp = new HashSet<Term>();
				Term value = binding.get(leFactory.createVariable("x"));
				temp = results.get(instance);
				temp.add(value);
				results.put(instance, temp);
			} else {
				Set<Term> temp = new HashSet<Term>();
				Term value = binding.get(leFactory.createVariable("x"));
				temp.add(value);
				results.put(instance, temp);
			}
		}

		return results;
	}

	public Set<Object> getInferingAttributeValues(Instance subject,
			Identifier attributeId) {
		// build query:
		Term instanceId = wsmoFactory.createIRI(subject.getIdentifier()
				.toString());
		Term valueVariable = leFactory.createVariable("x");
		Term valueVariable2 = leFactory.createVariable("y");
		Term instanceVariable = leFactory.createVariable("z");
		LogicalExpression queryPart1 = leFactory.createAttributeValue(
				instanceId, attributeId, valueVariable);
		LogicalExpression queryPart2 = leFactory.createAttributeInference(
				instanceVariable, attributeId, valueVariable2);
		LogicalExpression query = leFactory.createConjunction(queryPart1,
				queryPart2);

		// submit query to reasoner:
		Set<Map<Variable, Term>> bindings = mReasoner.executeQuery(query);

		// extract instances from result:
		Set<Object> values = new HashSet<Object>();
		for (Map<Variable, Term> binding : bindings) {
			if (binding.get(leFactory.createVariable("x")) instanceof IRI) {
				IRI iri = (IRI) binding.get(leFactory.createVariable("x"));
				values.add(iri);
			} else {
				values.add(binding.get(leFactory.createVariable("x"))
						.toString());
			}
		}
		return values;
	}

	public Set<Object> getConstraintAttributeValues(Instance subject,
			Identifier attributeId) {
		// build query:
		Term instanceId = wsmoFactory.createIRI(subject.getIdentifier()
				.toString());
		Term valueVariable = leFactory.createVariable("x");
		Term valueVariable2 = leFactory.createVariable("y");
		Term instanceVariable = leFactory.createVariable("z");
		LogicalExpression queryPart1 = leFactory.createAttributeValue(
				instanceId, attributeId, valueVariable);
		LogicalExpression queryPart2 = leFactory.createAttributeConstraint(
				instanceVariable, attributeId, valueVariable2);
		LogicalExpression query = leFactory.createConjunction(queryPart1,
				queryPart2);

		// submit query to reasoner:
		Set<Map<Variable, Term>> bindings = mReasoner.executeQuery(query);

		// extract instances from result:
		Set<Object> values = new HashSet<Object>();
		for (Map<Variable, Term> binding : bindings) {
			if (binding.get(leFactory.createVariable("x")) instanceof IRI) {
				IRI iri = (IRI) binding.get(leFactory.createVariable("x"));
				values.add(iri);
			} else {
				values.add(binding.get(leFactory.createVariable("x"))
						.toString());
			}
		}
		return values;
	}

	/**
	 * Extract concept IRIs from a query result set.
	 * 
	 * @param bindings
	 *            The results set.
	 * @param variable
	 *            The variables identifying the term of interest.
	 * @param concepts
	 *            The collection to hold the found concepts.
	 */
	private void extractConcepts(Set<Map<Variable, Term>> bindings,
			Term variable, Set<Concept> concepts) {
		for (Map<Variable, Term> binding : bindings) {
			Term term = binding.get(variable);
			if (term instanceof IRI) {
				IRI conceptID = (IRI) binding.get(variable);
				if (!conceptID.getNamespace().toString()
						.startsWith("http://www.wsmo.org/wsml/wsml-syntax")) {
					concepts.add(wsmoFactory.createConcept(conceptID));
				}
			}
		}
	}
}
