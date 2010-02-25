package org.wsml.reasoner.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Attribute;
import org.omwg.ontology.Axiom;
import org.omwg.ontology.Concept;
import org.omwg.ontology.DataType;
import org.omwg.ontology.DataValue;
import org.omwg.ontology.Instance;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Relation;
import org.omwg.ontology.Type;
import org.sti2.elly.api.basics.IAtom;
import org.sti2.elly.api.basics.IAtomicConcept;
import org.sti2.elly.api.basics.IAtomicRole;
import org.sti2.elly.api.basics.IConceptDescription;
import org.sti2.elly.api.basics.IRule;
import org.sti2.elly.api.factory.IBasicFactory;
import org.sti2.elly.api.terms.IIndividual;
import org.sti2.elly.api.terms.ITerm;
import org.sti2.elly.basics.BasicFactory;
import org.sti2.elly.util.Rules;
import org.sti2.wsmo4j.factory.WsmlFactoryContainer;
import org.sti2.wsmo4j.merger.Merger;
import org.wsml.reasoner.ELPReasonerFacade;
import org.wsml.reasoner.ExternalToolException;
import org.wsml.reasoner.api.DLReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory.BuiltInReasoner;
import org.wsml.reasoner.api.exception.InternalReasonerException;
import org.wsml.reasoner.api.inconsistency.InconsistencyException;
import org.wsml.reasoner.builtin.elly.ELLYFacade;
import org.wsml.reasoner.builtin.elly.Elly2WsmlOntologyEntityTranslator;
import org.wsml.reasoner.builtin.elly.Wsml2EllyOntologyEntityTranslator;
import org.wsml.reasoner.builtin.elly.Wsml2EllyTranslator;
import org.wsml.reasoner.transformation.AxiomatizationNormalizer;
import org.wsml.reasoner.transformation.ConstructReductionNormalizer;
import org.wsml.reasoner.transformation.InverseImplicationNormalizer;
import org.wsml.reasoner.transformation.LloydToporNormalizer;
import org.wsml.reasoner.transformation.OntologyNormalizer;
import org.wsmo.common.Entity;
import org.wsmo.common.IRI;
import org.wsmo.common.Identifier;
import org.wsmo.common.exception.InvalidModelException;
import org.wsmo.factory.FactoryContainer;

public class ELPBasedWSMLReasoner implements DLReasoner {

	private static IBasicFactory BASIC = BasicFactory.getInstance();

	private final Elly2WsmlOntologyEntityTranslator elly2wsml;

	private FactoryContainer factories;
	private ELPReasonerFacade builtInFacade;
	private boolean disableConsitencyCheck;
	private boolean changed;
	
	private Ontology registeredOntology = null;
	private final Merger merger;
	
	/* ********************************************
	 * Caches
	 * *******************************************/
	private final Set<IRI> inferringAttributes;
	private final Set<IRI> constrainingAttributes;
	private final Set<IRI> allAttributes;
	private final Set<Concept> allConcepts;
	private final Set<Instance> allInstances;


	public ELPBasedWSMLReasoner(BuiltInReasoner builtInType) throws InternalReasonerException {
		this(builtInType, new WsmlFactoryContainer());
	}

	public ELPBasedWSMLReasoner(BuiltInReasoner builtInType, FactoryContainer factories)
			throws InternalReasonerException {
		if (factories == null)
			throw new IllegalArgumentException("factory must not be null!");

		this.builtInFacade = createFacade(builtInType);

		this.merger = new Merger();
		this.factories = factories;
		this.elly2wsml = new Elly2WsmlOntologyEntityTranslator(factories);
		
		this.allConcepts = new HashSet<Concept>();
		this.allInstances = new HashSet<Instance>();
		this.allAttributes = new HashSet<IRI>();
		this.inferringAttributes = new HashSet<IRI>();
		this.constrainingAttributes = new HashSet<IRI>();
		
		this.disableConsitencyCheck = false;
		this.changed = true;
	}

	private ELPReasonerFacade createFacade(BuiltInReasoner builtInType) {
		switch (builtInType) {
		case ELLY:
			return new ELLYFacade();
		default:
			throw new InternalReasonerException("Reasoning with " + builtInType.toString() + " is not supported!");
		}
	}

	public void setDisableConsitencyCheck(boolean check) {
		if (this.disableConsitencyCheck != check)
			setChanged();
		
		this.disableConsitencyCheck = check;
	}

	public void registerOntology(Ontology ontology) throws InconsistencyException {
		setChanged();

		Set<Ontology> ontologies = new HashSet<Ontology>();
		ontologies.add(ontology);
		registerOntologies(ontologies);
	}

	public void registerOntologyNoVerification(Ontology ontology) {
		setChanged();

		// safe consistency check setting
		boolean consistencyCheck = this.disableConsitencyCheck;
		
		// disable consistency check
		setDisableConsitencyCheck(true);
		
		// register ontology
		try {
			registerOntology(ontology);
		} catch (InconsistencyException e) {
			new RuntimeException("Must not occur since consistency check disabled");
		}

		// set to old value
		setDisableConsitencyCheck(consistencyCheck);
	}

	public void registerOntologies(Set<Ontology> ontologies) throws InconsistencyException {
		setChanged();

		if (registeredOntology != null)
			ontologies.add(registeredOntology);
		
		try {
			registeredOntology = merger.merge(factories.getWsmoFactory().createIRI("http://org.wsml.reasoner.impl.ELPBasedWSMLReasoner/registeredOntology"), ontologies);
		} catch (InvalidModelException e) {
			throw new RuntimeException("Error merging Ontolgies", e);
		}
		

		Set<Entity> entities = new HashSet<Entity>();

		entities.addAll(registeredOntology.listConcepts());
		entities.addAll(registeredOntology.listInstances());
		entities.addAll(registeredOntology.listRelations());
		entities.addAll(registeredOntology.listRelationInstances());
		entities.addAll(registeredOntology.listAxioms());

		registerEntities(entities);
	}

	private void registerEntities(Set<Entity> theEntities) throws InconsistencyException {
		setChanged();

		registerEntitiesNoVerification(theEntities);
        if (!disableConsitencyCheck && !isSatisfiable()) {
            deRegister();
            throw new InconsistencyException("Given ontology is not satisfiable");
        }
	}

	public void deRegister() {
		setChanged();
		
		try {
			builtInFacade.deRegister();
			registeredOntology = null;
		} catch (org.wsml.reasoner.ExternalToolException e) {
			throw new InternalReasonerException(e);
		}
	}

	private void registerEntitiesNoVerification(Set<Entity> theEntities) {
		setChanged();

		List<IRule> ruleBase = new ArrayList<IRule>();
		ruleBase.addAll(convertEntities(theEntities));

		// Register the program at the built-in reasoner:
		try {
			builtInFacade.register(BASIC.createRuleBase(ruleBase));
		} catch (ExternalToolException e) {
			throw new IllegalArgumentException(
					"This set of entities could not be registered with the built-in reasoner", e);
		}
	}

	@Override
	public Set<IRI> getAllAttributes() {
		checkRegistered();
		
		synchronize();
		
		return allAttributes;
	}

	@Override
	public Set<Concept> getAllConcepts() {
		checkRegistered();
		
		synchronize();
		
		return allConcepts;
	}

	@Override
	public Set<IRI> getAllConstraintAttributes() {
		checkRegistered();
		
		synchronize();
		
		return constrainingAttributes;
	}

	@Override
	public Set<IRI> getAllInferenceAttributes() {
		checkRegistered();
		
		synchronize();
		
		return inferringAttributes;
	}

	@Override
	public Set<Instance> getAllInstances() {
		checkRegistered();
		
		synchronize();
		
		return allInstances;
	}

	@Override
	public Set<Concept> getConcepts(Instance instance) {
		checkRegistered();
		
		try {
			IIndividual individual = Wsml2EllyOntologyEntityTranslator.createIndividual(instance);
			Set<IAtomicConcept> concepts = builtInFacade.typesOf(individual);

			Set<Concept> wsmlConcepts = new HashSet<Concept>();
			for (IAtomicConcept concept : concepts) {
				wsmlConcepts.add(elly2wsml.createConcept(concept));
			}

			return wsmlConcepts;
		} catch (ExternalToolException e) {
			throw new InternalReasonerException(e);
		}
	}

	@Override
	public Set<Concept> getConceptsOf(Identifier attributeId) {
		checkRegistered();
		
		Set<Concept> directConcepts = new HashSet<Concept>();
		Set<Concept> allConcepts = new HashSet<Concept>();
		
		// filter direct concepts
		for (Concept concept : registeredOntology.listConcepts()) {
			for (Attribute attribute : concept.listAttributes()) {
				if (attribute.getIdentifier().equals(attributeId)) {
					directConcepts.add(attribute.getConcept());
				}
			}
		}
		
		// add their sub-concepts
		for (Concept concept : directConcepts) {
			allConcepts.addAll(getSubConcepts(concept));
		}
		
		assert allConcepts.containsAll(directConcepts);
		
		return allConcepts;
	}

	@Override
	public Map<Instance, Set<DataValue>> getConstraintAttributeInstances(Identifier attributeId) {
		checkRegistered();
		
		Map<Instance, Set<DataValue>> instanceMap = new HashMap<Instance, Set<DataValue>>();
		
		// Confirm that it is inferring
		if (!isConstrainingAttribute(attributeId)) {
			throw new IllegalArgumentException("Attribute " + attributeId + " is not constraining");
		}
		
		// Now retrieve all Values
		try {
			Map<IIndividual, Set<ITerm>> roleValues = builtInFacade.getRoleValues(Wsml2EllyOntologyEntityTranslator.createRole(attributeId));
			
			for (IIndividual individual : roleValues.keySet()) {
				Instance instance = elly2wsml.createInstance(individual);
				Set<DataValue> datavalueSet = new HashSet<DataValue>();
				
				for (ITerm term : roleValues.get(individual)) {
					// DL just allows for datavalues when constraining
					datavalueSet.add(elly2wsml.createDataValue(term));
				}
				
				instanceMap.put(instance, datavalueSet);
			}
		} catch (ExternalToolException e) {
			throw new IllegalArgumentException(e);
		}

		return instanceMap;
	}

	@Override
	public Map<IRI, Set<DataValue>> getConstraintAttributeValues(Instance instance) {
		checkRegistered();
		
		Map<IRI, Set<DataValue>> attributeMap = new HashMap<IRI, Set<DataValue>>();
		
		try {
			Map<IAtomicRole, Set<ITerm>> roleValues = builtInFacade
					.getObjectRoleValues(Wsml2EllyOntologyEntityTranslator.createIndividual(instance));

			for (IAtomicRole role : roleValues.keySet()) {
				IRI roleIRI = elly2wsml.createIRI(role);

				if (isConstrainingAttribute(roleIRI)) {
					Set<DataValue> datavalueSet = new HashSet<DataValue>();

					for (ITerm term : roleValues.get(role)) {
						datavalueSet.add(elly2wsml.createDataValue(term));
					}

					attributeMap.put(roleIRI, datavalueSet);
				}
			}
		} catch (ExternalToolException e) {
			throw new IllegalArgumentException(e);
		}
		

		return attributeMap;
	}

	@Override
	public Set<DataValue> getConstraintAttributeValues(Instance subject, Identifier attributeId) {
		checkRegistered();
		
		Set<DataValue> valueSet = new HashSet<DataValue>();
		
		if (!isConstrainingAttribute(attributeId)) {
			throw new IllegalArgumentException("Attribute " + attributeId + " is not constraining");
		}
		
		try {
			IIndividual individual = Wsml2EllyOntologyEntityTranslator.createIndividual(subject);
			IAtomicRole role = Wsml2EllyOntologyEntityTranslator.createRole(attributeId);
			Set<ITerm> roleValues = builtInFacade.getObjectRoleValues(individual, role);
			
			for (ITerm term : roleValues) {
				valueSet.add(elly2wsml.createDataValue(term));
			}
		} catch (ExternalToolException e) {
			throw new IllegalArgumentException(e);
		}

		return valueSet;
	}

	@Override
	public Set<Concept> getDirectConcepts(Instance instance) {
		checkRegistered();
		
		Set<Concept> concepts = new HashSet<Concept>();
		
		for (Concept concept : registeredOntology.listConcepts()) {
			if (concept.listInstances().contains(instance)) {
				concepts.add(concept);
			}
		}
		
		return concepts;
	}

	@Override
	public Set<Concept> getDirectSubConcepts(Concept concept) {
		checkRegistered();
		
		Set<Concept> concepts = new HashSet<Concept>();
		
		for (Concept ontologyConcept : registeredOntology.listConcepts()) {
			if (ontologyConcept.equals(concept)) {
				concepts.addAll(ontologyConcept.listSubConcepts());
			}
		}
		
		return concepts;
	}

	@Override
	public Set<IRI> getDirectSubRelations(Identifier attributeId) {
		checkRegistered();
		
		Set<IRI> subRelationIRIs = new HashSet<IRI>();
		Set<Relation> subRelations = new HashSet<Relation>();

		for (Relation relation : registeredOntology.listRelations()) {
			if (relation.getIdentifier().equals(attributeId)) {
				subRelations.addAll(relation.listSubRelations());
			}
		}

		for (Relation relation : subRelations) {
			Identifier identifier = relation.getIdentifier();
			if (identifier instanceof IRI) {
				subRelationIRIs.add((IRI) identifier);
			}
		}

		return subRelationIRIs;
	}

	@Override
	public Set<Concept> getDirectSuperConcepts(Concept concept) {
		checkRegistered();
		
		Set<Concept> concepts = new HashSet<Concept>();
		
		for (Concept ontologyConcept : registeredOntology.listConcepts()) {
			if (ontologyConcept.equals(concept)) {
				concepts.addAll(ontologyConcept.listSuperConcepts());
			}
		}
		
		return concepts;
	}

	@Override
	public Set<IRI> getDirectSuperRelations(Identifier attributeId) {
		checkRegistered();
		
		Set<IRI> superRelationIRIs = new HashSet<IRI>();
		Set<Relation> superRelations = new HashSet<Relation>();

		for (Relation relation : registeredOntology.listRelations()) {
			if (relation.getIdentifier().equals(attributeId)) {
				superRelations.addAll(relation.listSuperRelations());
			}
		}

		for (Relation relation : superRelations) {
			Identifier identifier = relation.getIdentifier();
			if (identifier instanceof IRI) {
				superRelationIRIs.add((IRI) identifier);
			}
		}

		return superRelationIRIs;
	}

	@Override
	public Set<Concept> getEquivalentConcepts(Concept concept) {
		checkRegistered();
		
		try {
			IAtomicConcept ellyConcept = Wsml2EllyOntologyEntityTranslator.createConcept(concept);
			Set<IAtomicConcept> ellyEqualConcepts = builtInFacade.equivalentConceptsOf(ellyConcept);

			Set<Concept> wsmlConcepts = new HashSet<Concept>();
			for (IAtomicConcept ellyEqualConcept : ellyEqualConcepts) {
				wsmlConcepts.add(elly2wsml.createConcept(ellyEqualConcept));
			}

			return wsmlConcepts;
		} catch (ExternalToolException e) {
			throw new InternalReasonerException(e);
		}
	}

	@Override
	public Set<IRI> getEquivalentRelations(Identifier attributeId) {
		checkRegistered();
		
		try {
			IAtomicRole ellyRole = Wsml2EllyOntologyEntityTranslator.createRole(attributeId);
			Set<IAtomicRole> ellyEqualRoles = builtInFacade.equivalentRolesOf(ellyRole);

			Set<IRI> wsmlRoles = new HashSet<IRI>();
			for (IAtomicRole ellyEqualRole : ellyEqualRoles) {
				wsmlRoles.add(elly2wsml.createIRI(ellyEqualRole));
			}

			return wsmlRoles;
		} catch (ExternalToolException e) {
			throw new InternalReasonerException(e);
		}
	}

	@Override
	public Map<Instance, Set<Term>> getInferingAttributeInstances(Identifier attributeId) {
		checkRegistered();
		
		Map<Instance, Set<Term>> instanceMap = new HashMap<Instance, Set<Term>>();
		
		// Confirm that it is inferring
		if (!isInferringAttribute(attributeId)) {
			throw new IllegalArgumentException("Attribute " + attributeId + " is not inferring");
		}
		
		// Now retrieve all Values
		try {
			Map<IIndividual, Set<ITerm>> roleValues = builtInFacade.getRoleValues(Wsml2EllyOntologyEntityTranslator.createRole(attributeId));
			
			for (IIndividual individual : roleValues.keySet()) {
				Instance instance = elly2wsml.createInstance(individual);
				Set<Term> termSet = new HashSet<Term>();
				
				for (ITerm term : roleValues.get(individual)) {
					termSet.add(elly2wsml.createTerm(term));
				}
				
				instanceMap.put(instance, termSet);
			}
		} catch (ExternalToolException e) {
			throw new IllegalArgumentException(e);
		}

		return instanceMap;
	}

	@Override
	public Map<IRI, Set<Term>> getInferingAttributeValues(Instance instance) {
		checkRegistered();
		
		Map<IRI, Set<Term>> attributeMap = new HashMap<IRI, Set<Term>>();
		
		try {
			Map<IAtomicRole, Set<ITerm>> roleValues = builtInFacade
					.getObjectRoleValues(Wsml2EllyOntologyEntityTranslator.createIndividual(instance));

			for (IAtomicRole role : roleValues.keySet()) {
				IRI roleIRI = elly2wsml.createIRI(role);

				if (isInferringAttribute(roleIRI)) {
					Set<Term> termSet = new HashSet<Term>();

					for (ITerm term : roleValues.get(role)) {
						termSet.add(elly2wsml.createTerm(term));
					}

					attributeMap.put(roleIRI, termSet);
				}
			}
		} catch (ExternalToolException e) {
			throw new IllegalArgumentException(e);
		}
		
		return attributeMap;
	}

	@Override
	public Set<Term> getInferingAttributeValues(Instance subject, Identifier attributeId) {
		checkRegistered();
		
		Set<Term> termSet = new HashSet<Term>();

		if (!isInferringAttribute(attributeId)) {
			throw new IllegalArgumentException("Attribute " + attributeId + " is not inferring");
		}
		
		try {
			IIndividual individual = Wsml2EllyOntologyEntityTranslator.createIndividual(subject);
			IAtomicRole role = Wsml2EllyOntologyEntityTranslator.createRole(attributeId);
			Set<ITerm> roleValues = builtInFacade.getObjectRoleValues(individual, role);
			
			for (ITerm term : roleValues) {
				termSet.add(elly2wsml.createTerm(term));
			}
		} catch (ExternalToolException e) {
			throw new IllegalArgumentException(e);
		}
		
		return termSet;
	}

	@Override
	public Set<Instance> getInstances(Concept concept) {
		checkRegistered();
		
		try {
			IAtomicConcept ellyConcept = Wsml2EllyOntologyEntityTranslator.createConcept(concept);
			Set<IIndividual> ellyIndividuals = builtInFacade.allInstancesOf(ellyConcept);

			Set<Instance> wsmlInstances = new HashSet<Instance>();
			for (IIndividual ellyIndividual : ellyIndividuals) {
				wsmlInstances.add(elly2wsml.createInstance(ellyIndividual));
			}

			return wsmlInstances;
		} catch (ExternalToolException e) {
			throw new InternalReasonerException(e);
		}
	}

	@Override
	public Set<IRI> getInverseRelations(Identifier attributeId) {
		checkRegistered();
		
		Set<IRI> inverseAttributeIRIs = new HashSet<IRI>();
		
		for (Concept concept : registeredOntology.listConcepts()) {
			for (Attribute attribute : concept.listAttributes()) {
				if (attribute.isConstraining() && attribute.getIdentifier().equals(attributeId)) { // TODO why constrainging?
					Identifier inverseId = attribute.getInverseOf();
					if (inverseId instanceof IRI) {
						inverseAttributeIRIs.add((IRI) inverseId);
					}
				}
			}
		}
		
		return inverseAttributeIRIs;
	}

	@Override
	public Set<DataType> getRangesOfConstraintAttribute(Identifier attributeId) {
		checkRegistered();
		
		Set<DataType> ranges = new HashSet<DataType>();
		
		if (!isConstrainingAttribute(attributeId)) {
			throw new IllegalArgumentException("Attribute " + attributeId + " is not constraining!");
		}
		
		// get super attributes
		Set<IRI> allAttributes = getSuperRelations(attributeId);

		// get ranges for all attributes
		for (Concept concept : registeredOntology.listConcepts()) {
			for (Attribute attribute : concept.listAttributes()) {
				if (allAttributes.contains(attribute.getIdentifier())) {
					for (Type type : attribute.listConstrainingTypes()) {
						// since this is a DL reasoner only DataTypes are allowed as types
						ranges.add((DataType) type);
					}
				}
			}
		}
		
		return ranges;
	}

	@Override
	public Set<Type> getRangesOfInferingAttribute(Identifier attributeId) {
		checkRegistered();
		
		Set<Type> ranges = new HashSet<Type>();
		
		if (!isInferringAttribute(attributeId)) {
			throw new IllegalArgumentException("Attribute " + attributeId + " is not inferring!");
		}
		
		// get super attributes
		Set<IRI> allAttributes = getSuperRelations(attributeId);

		// get ranges for all attributes
		for (Concept concept : registeredOntology.listConcepts()) {
			for (Attribute attribute : concept.listAttributes()) {
				if (allAttributes.contains(attribute.getIdentifier())) {
					ranges.addAll(attribute.listInferringTypes());
				}
			}
		}
		
		Set<Type> allRanges = new HashSet<Type>(ranges);
		for (Type directRange : ranges) {
			if (directRange instanceof Concept) {
				Concept rangeConcept = (Concept) directRange;
				allRanges.addAll(getSuperConcepts(rangeConcept));
			}
		}
		
		return allRanges;
	}

	@Override
	public Set<Concept> getSubConcepts(Concept concept) {
		checkRegistered();
		
		try {
			IAtomicConcept ellyConcept = Wsml2EllyOntologyEntityTranslator.createConcept(concept);
			Set<IAtomicConcept> ellySubConcepts = builtInFacade.subConceptsOf(ellyConcept);

			Set<Concept> wsmlConcepts = new HashSet<Concept>();
			for (IAtomicConcept ellySubConcept : ellySubConcepts) {
				wsmlConcepts.add(elly2wsml.createConcept(ellySubConcept));
			}

			return wsmlConcepts;
		} catch (ExternalToolException e) {
			throw new InternalReasonerException(e);
		}
	}

	@Override
	public Set<IRI> getSubRelations(Identifier attributeId) {
		checkRegistered();
		
		try {
			IAtomicRole ellyRole = Wsml2EllyOntologyEntityTranslator.createRole(attributeId);
			Set<IAtomicRole> ellySubRoles = builtInFacade.subRolesOf(ellyRole);

			Set<IRI> wsmlRoles = new HashSet<IRI>();
			for (IAtomicRole ellySubRole : ellySubRoles) {
				wsmlRoles.add(elly2wsml.createIRI(ellySubRole));
			}

			return wsmlRoles;
		} catch (ExternalToolException e) {
			throw new InternalReasonerException(e);
		}
	}

	@Override
	public Set<Concept> getSuperConcepts(Concept concept) {
		checkRegistered();
		
		try {
			IAtomicConcept ellyConcept = Wsml2EllyOntologyEntityTranslator.createConcept(concept);
			Set<IAtomicConcept> ellySuperConcepts = builtInFacade.superConceptsOf(ellyConcept);

			Set<Concept> wsmlConcepts = new HashSet<Concept>();
			for (IAtomicConcept ellySuperConcept : ellySuperConcepts) {
				wsmlConcepts.add(elly2wsml.createConcept(ellySuperConcept));
			}

			return wsmlConcepts;
		} catch (ExternalToolException e) {
			throw new InternalReasonerException(e);
		}
	}

	@Override
	public Set<IRI> getSuperRelations(Identifier attributeId) {
		checkRegistered();
		
		try {
			IAtomicRole ellyRole = Wsml2EllyOntologyEntityTranslator.createRole(attributeId);
			Set<IAtomicRole> ellySuperRoles = builtInFacade.superRolesOf(ellyRole);

			Set<IRI> wsmlRoles = new HashSet<IRI>();
			for (IAtomicRole ellySuperRole : ellySuperRoles) {
				wsmlRoles.add(elly2wsml.createIRI(ellySuperRole));
			}

			return wsmlRoles;
		} catch (ExternalToolException e) {
			throw new InternalReasonerException(e);
		}
	}


	@Override
	public boolean isEntailed(LogicalExpression expression) {
		checkRegistered();
		
		if (expression == null)
			throw new IllegalArgumentException("LogicalExpression must not be null");
		
		List<IRule> rules = new ArrayList<IRule>();
		expression.accept(new Wsml2EllyTranslator(rules));
		
		if (rules.size() != 1)
			throw new IllegalArgumentException("LogicalExpression must not be a rule or contain implications");
		
		IRule fact = rules.get(0);
		
		if (!Rules.isFact(fact))
			throw new IllegalArgumentException("LogicalExpression must not be a rule or contain implications");
		
		
		try {
			return builtInFacade.isEntailed(fact);
		} catch (ExternalToolException e) {
			throw new InternalReasonerException(e);
		}
	}


	@Override
	public boolean isConceptSatisfiable(LogicalExpression expression) {
		checkRegistered();
		
		List<IRule> rules = new ArrayList<IRule>();
		expression.accept(new Wsml2EllyTranslator(rules));
		
		assert rules.size() == 1;
		
		IRule rule = rules.get(0);
		assert rule.getBody().size() == 0;
		assert rule.getHead().size() == 1;
		
		IAtom conceptAtom = rule.getHead().get(0);
		assert conceptAtom.getDescription() instanceof IConceptDescription;
		
		IConceptDescription concept = (IConceptDescription) conceptAtom.getDescription();
		
		try {
			return builtInFacade.isConsistent(concept);
		} catch (ExternalToolException e) {
			throw new InternalReasonerException(e);
		}
	}

	@Override
	public boolean isEquivalentConcept(Concept concept1, Concept concept2) {
		checkRegistered();
		
		try {
			IAtomicConcept ellyConcept1 = Wsml2EllyOntologyEntityTranslator.createConcept(concept1);
			IAtomicConcept ellyConcept2 = Wsml2EllyOntologyEntityTranslator.createConcept(concept2);
			
			return builtInFacade.isEquivalentConcept(ellyConcept1, ellyConcept2);
		} catch (ExternalToolException e) {
			throw new InternalReasonerException(e);
		}
	}

	@Override
	public boolean isMemberOf(Instance instance, Concept concept) {
		checkRegistered();
		
		try {
			IAtomicConcept ellyConcept = Wsml2EllyOntologyEntityTranslator.createConcept(concept);
			IIndividual ellyIndividual= Wsml2EllyOntologyEntityTranslator.createIndividual(instance);
			
			return builtInFacade.isInstanceOf(ellyIndividual, ellyConcept);
		} catch (ExternalToolException e) {
			throw new InternalReasonerException(e);
		}
	}

	@Override
	public boolean isSatisfiable() {
		checkRegistered();
		
		try {
			return builtInFacade.isConsistent();
		} catch (ExternalToolException e) {
			throw new InternalReasonerException(e);
		}
	}

	@Override
	public boolean isSubConceptOf(Concept subConcept, Concept superConcept) {
		checkRegistered();
		
		try {
			IAtomicConcept ellySubConcept = Wsml2EllyOntologyEntityTranslator.createConcept(subConcept);
			IAtomicConcept ellySuperConcept = Wsml2EllyOntologyEntityTranslator.createConcept(superConcept);
			
			return builtInFacade.isSubConceptOf(ellySubConcept, ellySuperConcept);
		} catch (ExternalToolException e) {
			throw new InternalReasonerException(e);
		}
	}

	/* ********************************************
	 * Change - Helpers
	 * *******************************************/

	private boolean hasChanged() {
		return changed;
	}
	
	private void setChanged() {
		changed = true;
	}

	private void clearChanged() {
		changed = false;
	}
	
	private boolean synchronize() {
		checkRegistered();
		
		if (!hasChanged())
			return false;
		
		try {
			// update concepts
			allConcepts.clear();
			{
				Set<IAtomicConcept> ellyConcepts = builtInFacade.allConcepts();
				
				for (IAtomicConcept ellyConcept : ellyConcepts) {
					allConcepts.add(elly2wsml.createConcept(ellyConcept));
				}
			}

			// update instances
			allInstances.clear();
			{
				Set<IIndividual> ellyIndividuals = builtInFacade.allIndividuals();
				
				for (IIndividual ellyIndividual : ellyIndividuals) {
					allInstances.add(elly2wsml.createInstance(ellyIndividual));
				}
			}
			
			// update all roles
			allAttributes.clear();
			{
				Set<IAtomicRole> ellyRoles = builtInFacade.allRoles();

				for (IAtomicRole ellyRole : ellyRoles) {
					allAttributes.add(elly2wsml.createIRI(ellyRole));
				}
			}

			// update inferring attributes
			inferringAttributes.clear();
			{
				Set<Attribute> inferringAttributeObjects = new HashSet<Attribute>();
				
				for (Concept concept : registeredOntology.listConcepts()) {
					for (Attribute attribute : concept.listAttributes()) {
						if (attribute.isInferring()) { 
							inferringAttributeObjects.add(attribute);
						}
					}
				}
				
				for (Attribute attribute : inferringAttributeObjects) {
					Identifier id = attribute.getIdentifier();
					if (id instanceof IRI) {
						inferringAttributes.add((IRI) id);
//						inferringAttributes.addAll(getSubRelations(id)); // FIXME dw: this is very slow! but need to be added later again
					}
				}
			}
			
			// update constraining attributes
			constrainingAttributes.clear();
			{
				Set<Attribute> constrainingAttributeObjects = new HashSet<Attribute>();
				
				for (Concept concept : registeredOntology.listConcepts()) {
					for (Attribute attribute : concept.listAttributes()) {
						if (attribute.isConstraining()) {
							constrainingAttributeObjects.add(attribute);
						}
					}
				}
				
				for (Attribute attribute : constrainingAttributeObjects) {
					Identifier id = attribute.getIdentifier();
					if (id instanceof IRI) {
						constrainingAttributes.add((IRI) id);
//						constrainingAttributes.addAll(getSubRelations(id)); // TODO this is very slow! but need to be added later again
					}
				}
			}

			clearChanged();
			
			return true;
		} catch (ExternalToolException e) {
			throw new InternalReasonerException(e);
		}
	}
	
	/* ********************************************
	 * Helpers
	 * *******************************************/

	private void checkRegistered() {
		if (registeredOntology == null)
			throw new InternalReasonerException("No Ontology registered!");
	}

	private boolean isInferringAttribute(Identifier attributeId) {
		return getAllInferenceAttributes().contains(attributeId);
	}

	private boolean isConstrainingAttribute(Identifier attributeId) {
		return getAllConstraintAttributes().contains(attributeId);
	}

	protected List<IRule> convertEntities(Set<Entity> entities) {
		List<IRule> p = new ArrayList<IRule>();

		// Convert conceptual syntax to logical expressions
		OntologyNormalizer normalizer = new AxiomatizationNormalizer(factories);
		entities = normalizer.normalizeEntities(entities);

		Set<Axiom> axioms = new HashSet<Axiom>();
		for (Entity e : entities) {
			if (e instanceof Axiom) {
				axioms.add((Axiom) e);
			}
		}

		//        // Convert constraints to support debugging
		//        normalizer = new ConstraintReplacementNormalizer(factory);
		//        axioms = normalizer.normalizeAxioms(axioms);

		// Simplify axioms
		normalizer = new ConstructReductionNormalizer(factories);
		axioms = normalizer.normalizeAxioms(axioms);

		// Apply InverseImplicationTransformation (wsml-rule)
		normalizer = new InverseImplicationNormalizer(factories);
		axioms = normalizer.normalizeAxioms(axioms);

		// Apply Lloyd-Topor rules to get Datalog-compatible LEs
		normalizer = new LloydToporNormalizer(factories);
		axioms = normalizer.normalizeAxioms(axioms);

		Set<LogicalExpression> logicalExpressions = new LinkedHashSet<LogicalExpression>();
		for (Axiom axiom : axioms) {
			logicalExpressions.addAll(axiom.listDefinitions());
		}

		Wsml2EllyTranslator wsml2elp = new Wsml2EllyTranslator(p);
		for (LogicalExpression logicalExpression : logicalExpressions) {
			logicalExpression.accept(wsml2elp);
		}

		return p;
	}
}
