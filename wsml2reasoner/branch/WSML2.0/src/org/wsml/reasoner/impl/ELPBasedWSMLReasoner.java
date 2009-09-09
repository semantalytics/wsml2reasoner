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
import org.omwg.ontology.Instance;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Relation;
import org.omwg.ontology.Type;
import org.omwg.ontology.Value;
import org.sti2.elly.api.basics.IAtom;
import org.sti2.elly.api.basics.IAtomicConcept;
import org.sti2.elly.api.basics.IAtomicRole;
import org.sti2.elly.api.basics.IConceptDescription;
import org.sti2.elly.api.basics.IRule;
import org.sti2.elly.api.factory.IBasicFactory;
import org.sti2.elly.api.terms.IIndividual;
import org.sti2.elly.basics.BasicFactory;
import org.sti2.wsmo4j.factory.WsmlFactoryContainer;
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
import org.wsmo.common.ImportedOntologiesBlock;
import org.wsmo.factory.FactoryContainer;

public class ELPBasedWSMLReasoner implements DLReasoner {

	private static IBasicFactory BASIC = BasicFactory.getInstance();

	private final Elly2WsmlOntologyEntityTranslator elly2wsml;

	private FactoryContainer factories;
	private ELPReasonerFacade builtInFacade;
	private boolean disableConsitencyCheck;
	private int allowImports;

	private final Set<Ontology> registeredOntologies;

	public ELPBasedWSMLReasoner(BuiltInReasoner builtInType) throws InternalReasonerException {
		this(builtInType, new WsmlFactoryContainer());
	}

	public ELPBasedWSMLReasoner(BuiltInReasoner builtInType, FactoryContainer factories)
			throws InternalReasonerException {
		if (factories == null)
			throw new IllegalArgumentException("factory must not be null!");

		this.registeredOntologies = new HashSet<Ontology>();
		this.factories = factories;
		this.elly2wsml = new Elly2WsmlOntologyEntityTranslator(factories);
		this.builtInFacade = createFacade(builtInType);
	}

	private ELPReasonerFacade createFacade(BuiltInReasoner builtInType) {
		switch (builtInType) {
		case ELLY:
			return new ELLYFacade();
		}
		throw new InternalReasonerException("Reasoning with " + builtInType.toString() + " is not supported!");
	}

	public void setDisableConsitencyCheck(boolean check) {
		this.disableConsitencyCheck = check;
	}

	public void setAllowImports(int allowOntoImports) {
		this.allowImports = allowOntoImports;
	}

	public void registerOntology(Ontology ontology) throws InconsistencyException {
		Set<Ontology> ontologySingletonSet = new HashSet<Ontology>();
		ontologySingletonSet.add(ontology);
		registerOntologies(ontologySingletonSet);
	}

	public void registerOntologies(Set<Ontology> ontologies) throws InconsistencyException {
		if (allowImports == 0) {
			ontologies = getAllOntologies(ontologies);
		}

		Set<Entity> entities = new HashSet<Entity>();
		for (Ontology o : ontologies) {
			entities.addAll(o.listConcepts());
			entities.addAll(o.listInstances());
			entities.addAll(o.listRelations());
			entities.addAll(o.listRelationInstances());
			entities.addAll(o.listAxioms());
		}
		registerEntities(entities);
		
		registeredOntologies.addAll(ontologies);
	}

	public void registerEntities(Set<Entity> theEntities) throws InconsistencyException {
		registerEntitiesNoVerification(theEntities);
        if (!disableConsitencyCheck && !isSatisfiable()) {
            deRegister();
            throw new InconsistencyException("Given ontology is not satisfiable");
        }
	}

	public void deRegister() {
		try {
			builtInFacade.deRegister();
			registeredOntologies.clear();
		} catch (org.wsml.reasoner.ExternalToolException e) {
			throw new InternalReasonerException(e);
		}
	}

	public void registerEntitiesNoVerification(Set<Entity> theEntities) {
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

	public void registerOntologyNoVerification(Ontology ontology) {
		Set<Entity> entities = new HashSet<Entity>();
		entities.addAll(ontology.listConcepts());
		entities.addAll(ontology.listInstances());
		entities.addAll(ontology.listRelations());
		entities.addAll(ontology.listRelationInstances());
		entities.addAll(ontology.listAxioms());
		registerEntitiesNoVerification(entities);
	}

	@Override
	public Set<IRI> getAllAttributes() {
		try{
			Set<IAtomicRole> ellyRoles = builtInFacade.allRoles();
			
			Set<IRI> wsmlAttributes = new HashSet<IRI>();
			for (IAtomicRole ellyRole : ellyRoles) {
				wsmlAttributes.add(elly2wsml.createIRI(ellyRole));
			}
			
			return wsmlAttributes;
		} catch (ExternalToolException e) {
			throw new InternalReasonerException(e);
		}
	}

	@Override
	public Set<Concept> getAllConcepts() {
		try{
			Set<IAtomicConcept> ellyConcepts = builtInFacade.allConcepts();
			
			Set<Concept> wsmlConcepts = new HashSet<Concept>();
			for (IAtomicConcept ellyConcept : ellyConcepts) {
				wsmlConcepts.add(elly2wsml.createConcept(ellyConcept));
			}
			
			return wsmlConcepts;
		} catch (ExternalToolException e) {
			throw new InternalReasonerException(e);
		}
	}

	@Override
	public Set<IRI> getAllConstraintAttributes() {
		Set<Attribute> constrainingAttributes = new HashSet<Attribute>();
		Set<IRI> constrainingAttributeIRIs = new HashSet<IRI>();
		
		for (Ontology ontology : registeredOntologies) {
			for (Concept concept : ontology.listConcepts()) {
				for (Attribute attribute : concept.listAttributes()) {
					if (attribute.isConstraining()) {
						constrainingAttributes.add(attribute);
					}
				}
			}
		}
		
		for (Attribute attribute : constrainingAttributes) {
			Identifier id = attribute.getIdentifier();
			if (id instanceof IRI) {
				constrainingAttributeIRIs.add((IRI) id);
			}
		}

		return constrainingAttributeIRIs;
	}

	@Override
	public Set<IRI> getAllInferenceAttributes() {
		Set<Attribute> inferringAttributes = new HashSet<Attribute>();
		Set<IRI> inferringAttributeIRIs = new HashSet<IRI>();
		
		for (Ontology ontology : registeredOntologies) {
			for (Concept concept : ontology.listConcepts()) {
				for (Attribute attribute : concept.listAttributes()) {
					if (!attribute.isConstraining()) { // if not constraining => is inferring
						inferringAttributes.add(attribute);
					}
				}
			}
		}
		
		for (Attribute attribute : inferringAttributes) {
			Identifier id = attribute.getIdentifier();
			if (id instanceof IRI) {
				inferringAttributeIRIs.add((IRI) id);
			}
		}

		return inferringAttributeIRIs;
	}

	@Override
	public Set<Instance> getAllInstances() {
		try{
			Set<IIndividual> ellyIndividuals = builtInFacade.allIndividuals();
			
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
	public Set<Concept> getConcepts(Instance instance) {
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
		Set<Concept> directConcepts = new HashSet<Concept>();
		Set<Concept> allConcepts = new HashSet<Concept>();
		
		// filter direct concepts
		for (Ontology ontology : registeredOntologies) {
			for (Concept concept : ontology.listConcepts()) {
				for (Attribute attribute : concept.listAttributes()) {
					if (attribute.getIdentifier().equals(attributeId)) {
						directConcepts.add(attribute.getConcept());
					}
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
	public Map<Instance, Set<Term>> getConstraintAttributeInstances(Identifier attributeId) {
		Map<Instance, Set<Term>> instanceMap = new HashMap<Instance, Set<Term>>();
		
		if (!isConstrainingAttribute(attributeId)) {
			throw new IllegalArgumentException("Attribute " + attributeId + " is not constraining!");
		}
		
		// filter direct concepts
		for (Ontology ontology : registeredOntologies) {
			for (Instance instance : ontology.listInstances()) {
				Map<Identifier, Set<Value>> attributeValues = instance.listAttributeValues();
				for (Identifier attributeIdentifier : attributeValues.keySet()) {
					if (attributeIdentifier.equals(attributeId)) {
						Set<Term> termSet = instanceMap.get(instance);
						if (termSet == null) {
							termSet = new HashSet<Term>();
							instanceMap.put(instance, termSet);
						}
						
						for (Value value : attributeValues.get(attributeIdentifier)) {
							if (value instanceof Term) {
								termSet.add((Term) value);
							}
						}
					}
				}
			}
		}

		return instanceMap;
	}

	@Override
	public Map<IRI, Set<Term>> getConstraintAttributeValues(Instance instance) {
		Map<IRI, Set<Term>> attributeMap = new HashMap<IRI, Set<Term>>();
		
		// filter direct concepts
		for (Ontology ontology : registeredOntologies) {
			for (Instance ontologyInstance : ontology.listInstances()) {
				if (ontologyInstance.equals(instance)) {
					Map<Identifier, Set<Value>> attributeValues = ontologyInstance.listAttributeValues();
					for (Identifier attributeIdentifier : attributeValues.keySet()) {
						if (attributeIdentifier instanceof IRI && isConstrainingAttribute(attributeIdentifier)) {
							IRI attributeIRI = (IRI) attributeIdentifier;
							
							Set<Term> termSet = attributeMap.get(attributeIRI);
							if (termSet == null) {
								termSet = new HashSet<Term>();
								attributeMap.put(attributeIRI, termSet);
							}
							
							for (Value value : attributeValues.get(attributeIRI)) {
								if (value instanceof Term) {
									termSet.add((Term) value);
								}
							}
						}
					}
				}
				
			}
		}
		
		return attributeMap;
	}

	@Override
	public Set<String> getConstraintAttributeValues(Instance subject, Identifier attributeId) {
		Set<String> valueStringSet = new HashSet<String>();
		Set<Value> valueSet = new HashSet<Value>();
		
		if (!isConstrainingAttribute(attributeId)) {
			throw new IllegalArgumentException("Attribute " + attributeId + " is not constraining!");
		}
		
		// filter direct concepts
		for (Ontology ontology : registeredOntologies) {
			for (Instance instance : ontology.listInstances()) {
				if (instance.equals(subject)) {
					valueSet.addAll(instance.listAttributeValues(attributeId));
				}
			}
		}
		
		for (Value value : valueSet) {
			valueStringSet.add(value.toString());
		}
		
		return valueStringSet;
	}

	@Override
	public Set<Concept> getDirectConcepts(Instance instance) {
		Set<Concept> concepts = new HashSet<Concept>();
		
		for (Ontology ontology : registeredOntologies) {
			for (Concept concept : ontology.listConcepts()) {
				if (concept.listInstances().contains(instance)) {
					concepts.add(concept);
				}
			}
		}
		
		return concepts;
	}

	@Override
	public Set<Concept> getDirectSubConcepts(Concept concept) {
		Set<Concept> concepts = new HashSet<Concept>();
		
		for (Ontology ontology : registeredOntologies) {
			for (Concept ontologyConcept : ontology.listConcepts()) {
				if (ontologyConcept.equals(concept)) {
					concepts.addAll(ontologyConcept.listSubConcepts());
				}
			}
		}
		
		return concepts;
	}

	@Override
	public Set<IRI> getDirectSubRelations(Identifier attributeId) {
		Set<IRI> subRelationIRIs = new HashSet<IRI>();
		Set<Relation> subRelations = new HashSet<Relation>();

		for (Ontology ontology : registeredOntologies) {
			for (Relation relation : ontology.listRelations()) {
				if (relation.getIdentifier().equals(attributeId)) {
					subRelations.addAll(relation.listSubRelations());
				}
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
		Set<Concept> concepts = new HashSet<Concept>();
		
		for (Ontology ontology : registeredOntologies) {
			for (Concept ontologyConcept : ontology.listConcepts()) {
				if (ontologyConcept.equals(concept)) {
					concepts.addAll(ontologyConcept.listSuperConcepts());
				}
			}
		}
		
		return concepts;
	}

	@Override
	public Set<IRI> getDirectSuperRelations(Identifier attributeId) {
		Set<IRI> superRelationIRIs = new HashSet<IRI>();
		Set<Relation> superRelations = new HashSet<Relation>();

		for (Ontology ontology : registeredOntologies) {
			for (Relation relation : ontology.listRelations()) {
				if (relation.getIdentifier().equals(attributeId)) {
					superRelations.addAll(relation.listSuperRelations());
				}
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
		Map<Instance, Set<Term>> instanceMap = new HashMap<Instance, Set<Term>>();
		
		if (!isInferringAttribute(attributeId)) {
			throw new IllegalArgumentException("Attribute " + attributeId + "is not inferring");
		}
		
		// filter direct concepts
		for (Ontology ontology : registeredOntologies) {
			for (Instance instance : ontology.listInstances()) {
				Map<Identifier, Set<Value>> attributeValues = instance.listAttributeValues();
				for (Identifier attributeIdentifier : attributeValues.keySet()) {
					if (attributeIdentifier.equals(attributeId)) {
						Set<Term> termSet = instanceMap.get(instance);
						if (termSet == null) {
							termSet = new HashSet<Term>();
							instanceMap.put(instance, termSet);
						}
						
						for (Value value : attributeValues.get(attributeIdentifier)) {
							if (value instanceof Term) {
								termSet.add((Term) value);
							}
						}
					}
				}
			}
		}

		return instanceMap;
	}

	@Override
	public Map<IRI, Set<Term>> getInferingAttributeValues(Instance instance) {
		Map<IRI, Set<Term>> attributeMap = new HashMap<IRI, Set<Term>>();
		
		// filter direct concepts
		for (Ontology ontology : registeredOntologies) {
			for (Instance ontologyInstance : ontology.listInstances()) {
				if (ontologyInstance.equals(instance)) {
					Map<Identifier, Set<Value>> attributeValues = ontologyInstance.listAttributeValues();
					for (Identifier attributeIdentifier : attributeValues.keySet()) {
						if (attributeIdentifier instanceof IRI && isInferringAttribute(attributeIdentifier)) {
							IRI attributeIRI = (IRI) attributeIdentifier;
							
							Set<Term> termSet = attributeMap.get(attributeIRI);
							if (termSet == null) {
								termSet = new HashSet<Term>();
								attributeMap.put(attributeIRI, termSet);
							}
							
							for (Value value : attributeValues.get(attributeIRI)) {
								if (value instanceof Term) {
									termSet.add((Term) value);
								}
							}
						}
					}
				}
				
			}
		}
		
		return attributeMap;
	}

	@Override
	public Set<IRI> getInferingAttributeValues(Instance subject, Identifier attributeId) {
		Set<IRI> instanceSet = new HashSet<IRI>();
		Set<Value> valueSet = new HashSet<Value>();

		if (!isInferringAttribute(attributeId)) {
			throw new IllegalArgumentException("Attribute " + attributeId + "is not inferring");
		}
		
		// filter direct concepts
		for (Ontology ontology : registeredOntologies) {
			for (Instance instance : ontology.listInstances()) {
				if (instance.equals(subject)) {
					valueSet.addAll(instance.listAttributeValues(attributeId));
				}
			}
		}

		for (Value value : valueSet) {
			if (value instanceof Instance) {
				Identifier instanceIdentifier = ((Instance) value).getIdentifier();
				if (instanceIdentifier instanceof IRI) {
					instanceSet.add((IRI) instanceIdentifier);
				}
			}
		}

		return instanceSet;
	}

	@Override
	public Set<Instance> getInstances(Concept concept) {
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
		Set<IRI> inverseAttributeIRIs = new HashSet<IRI>();
		
		for (Ontology ontology : registeredOntologies) {
			for (Concept concept : ontology.listConcepts()) {
				for (Attribute attribute : concept.listAttributes()) {
					if (attribute.isConstraining() && attribute.getIdentifier().equals(attributeId)) {
						Identifier inverseId = attribute.getInverseOf();
						if (inverseId instanceof IRI) {
							inverseAttributeIRIs.add((IRI) inverseId);
						}
					}
				}
			}
		}
		
		return inverseAttributeIRIs;
	}

	@Override
	public Set<IRI> getRangesOfConstraintAttribute(Identifier attributeId) {
		Set<IRI> rangeIRIs = new HashSet<IRI>();
		Set<Type> ranges = new HashSet<Type>();
		
		for (Ontology ontology : registeredOntologies) {
			for (Concept concept : ontology.listConcepts()) {
				for (Attribute attribute : concept.listAttributes()) {
					if (attribute.isConstraining() && attribute.getIdentifier().equals(attributeId)) {
						ranges.addAll(attribute.listTypes());
					}
				}
			}
		}
		
		for (Type type : ranges) {
			Identifier identifier = type.getIdentifier();
			if (identifier instanceof IRI) {
				rangeIRIs.add((IRI) identifier);
			}
		}
		
		return rangeIRIs;
	}

	@Override
	public Set<IRI> getRangesOfInferingAttribute(Identifier attributeId) {
		Set<IRI> rangeIRIs = new HashSet<IRI>();
		Set<Type> ranges = new HashSet<Type>();
		
		for (Ontology ontology : registeredOntologies) {
			for (Concept concept : ontology.listConcepts()) {
				for (Attribute attribute : concept.listAttributes()) {
					if ((!attribute.isConstraining()) && attribute.getIdentifier().equals(attributeId)) {
						ranges.addAll(attribute.listTypes());
					}
				}
			}
		}
		
		for (Type type : ranges) {
			Identifier identifier = type.getIdentifier();
			if (identifier instanceof IRI) {
				rangeIRIs.add((IRI) identifier);
			}
		}
		
		return rangeIRIs;
	}

	@Override
	public Set<Concept> getSubConcepts(Concept concept) {
		try {
			IAtomicConcept ellyConcept = Wsml2EllyOntologyEntityTranslator.createConcept(concept);
			Set<IAtomicConcept> ellySubConcepts = builtInFacade.directSubConceptsOf(ellyConcept);

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
		try {
			IAtomicRole ellyRole = Wsml2EllyOntologyEntityTranslator.createRole(attributeId);
			Set<IAtomicRole> ellySubRoles = builtInFacade.directSubRolesOf(ellyRole);

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
		try {
			IAtomicConcept ellyConcept = Wsml2EllyOntologyEntityTranslator.createConcept(concept);
			Set<IAtomicConcept> ellySuperConcepts = builtInFacade.directSuperConceptsOf(ellyConcept);

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
		try {
			IAtomicRole ellyRole = Wsml2EllyOntologyEntityTranslator.createRole(attributeId);
			Set<IAtomicRole> ellySuperRoles = builtInFacade.directSuperRolesOf(ellyRole);

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
	public boolean isConceptSatisfiable(LogicalExpression expression) {
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
		try {
			return builtInFacade.isConsistent();
		} catch (ExternalToolException e) {
			throw new InternalReasonerException(e);
		}
	}

	@Override
	public boolean isSubConceptOf(Concept subConcept, Concept superConcept) {
		try {
			IAtomicConcept ellySubConcept = Wsml2EllyOntologyEntityTranslator.createConcept(subConcept);
			IAtomicConcept ellySuperConcept = Wsml2EllyOntologyEntityTranslator.createConcept(superConcept);
			
			return builtInFacade.isSubConceptOf(ellySubConcept, ellySuperConcept);
		} catch (ExternalToolException e) {
			throw new InternalReasonerException(e);
		}
	}

	/* ********************************************
	 * Helpers
	 * *******************************************/

	private boolean isInferringAttribute(Identifier attributeId) {
		for (IRI attribute : getAllInferenceAttributes()) {
			if (attribute.equals(attributeId)) {
				return true;
			}
		}
		
		return false;
	}

	private boolean isConstrainingAttribute(Identifier attributeId) {
		for (IRI attribute : getAllConstraintAttributes()) {
			if (attribute.equals(attributeId)) {
				return true;
			}
		}
		
		return false;
	}

	private Set<Ontology> getAllOntologies(Set<Ontology> ontologies) {
		Set<Ontology> result = new HashSet<Ontology>();
		for (Ontology o : ontologies) {
			result.add(o);
			getAllOntologies(o, result);
		}
		return result;
	}

	private void getAllOntologies(Ontology o, Set<Ontology> ontologies) {
		ImportedOntologiesBlock importedOntologies = o.getImportedOntologies();
		if (importedOntologies != null)
			for (Ontology imported : importedOntologies.listOntologies()) {
				if (!ontologies.contains(imported)) {
					ontologies.add(imported);
					getAllOntologies(imported, ontologies);
				}
			}
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
