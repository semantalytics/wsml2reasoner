package org.wsml.reasoner.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Axiom;
import org.omwg.ontology.Concept;
import org.omwg.ontology.Instance;
import org.omwg.ontology.Ontology;
import org.sti2.elly.api.basics.IRule;
import org.sti2.elly.api.factory.IBasicFactory;
import org.sti2.elly.basics.BasicFactory;
import org.wsml.reasoner.ELPReasonerFacade;
import org.wsml.reasoner.ExternalToolException;
import org.wsml.reasoner.api.DLReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory.BuiltInReasoner;
import org.wsml.reasoner.api.exception.InternalReasonerException;
import org.wsml.reasoner.api.inconsistency.InconsistencyException;
import org.wsml.reasoner.builtin.elly.ELLYFacade;
import org.wsml.reasoner.builtin.elly.Wsml2EllyTranslator;
import org.wsml.reasoner.transformation.AxiomatizationNormalizer;
import org.wsml.reasoner.transformation.ConstraintReplacementNormalizer;
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
	
	private FactoryContainer factory;
	private ELPReasonerFacade builtInFacade;
	private boolean disableConsitencyCheck;
	private int allowImports;

	public ELPBasedWSMLReasoner(BuiltInReasoner builtInType, FactoryContainer factory) throws InternalReasonerException {
		this.factory = factory;

		this.builtInFacade = createFacade(builtInType);
	}

	private ELPReasonerFacade createFacade(BuiltInReasoner builtInType) {
		switch (builtInType) {
		case ELLY:
			return new ELLYFacade();
		}
		throw new InternalReasonerException("Reasoning with " + builtInType.toString() + " is not supported yet!");
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
    }

    public void registerEntities(Set<Entity> theEntities) throws InconsistencyException {
        registerEntitiesNoVerification(theEntities);
        
        // TODO add this check later
		//        if (!disableConsitencyCheck) {
		//            Set<ConsistencyViolation> errors = new HashSet<ConsistencyViolation>();
		//            errors.addAll(checkConsistency());
		//
		//            if (errors.size() > 0) {
		//                deRegister();
		//                throw new InconsistencyException(errors);
		//            }
		//        }
    }

    public void deRegister() {
        try {
            builtInFacade.deRegister();
        }
        catch (org.wsml.reasoner.ExternalToolException e) {
            e.printStackTrace();
        }
    }

    public void registerEntitiesNoVerification(Set<Entity> theEntities) {
    	List<IRule> ruleBase = new ArrayList<IRule>();
        ruleBase.addAll(convertEntities(theEntities));

        // Register the program at the built-in reasoner:
        try {
            builtInFacade.register(BASIC.createRuleBase(ruleBase));
        }
        catch (ExternalToolException e) {
            throw new IllegalArgumentException("This set of entities could not be registered with the built-in reasoner", e);
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Concept> getAllConcepts() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<IRI> getAllConstraintAttributes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<IRI> getAllInferenceAttributes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Instance> getAllInstances() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Concept> getConcepts(Instance instance) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Concept> getConceptsOf(Identifier attributeId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Instance, Set<Term>> getConstraintAttributeInstances(Identifier attributeId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<IRI, Set<Term>> getConstraintAttributeValues(Instance instance) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getConstraintAttributeValues(Instance subject, Identifier attributeId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Concept> getDirectConcepts(Instance instance) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Concept> getDirectSubConcepts(Concept concept) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<IRI> getDirectSubRelations(Identifier attributeId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Concept> getDirectSuperConcepts(Concept concept) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<IRI> getDirectSuperRelations(Identifier attributeId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Concept> getEquivalentConcepts(Concept concept) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<IRI> getEquivalentRelations(Identifier attributeId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Instance, Set<Term>> getInferingAttributeInstances(Identifier attributeId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<IRI, Set<Term>> getInferingAttributeValues(Instance instance) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<IRI> getInferingAttributeValues(Instance subject, Identifier attributeId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Instance> getInstances(Concept concept) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<IRI> getInverseRelations(Identifier attributeId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<IRI> getRangesOfConstraintAttribute(Identifier attributeId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<IRI> getRangesOfInferingAttribute(Identifier attributeId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Concept> getSubConcepts(Concept concept) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<IRI> getSubRelations(Identifier attributeId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Concept> getSuperConcepts(Concept concept) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<IRI> getSuperRelations(Identifier attributeId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConceptSatisfiable(LogicalExpression expression) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEquivalentConcept(Concept concept1, Concept concept2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isMemberOf(Instance instance, Concept concept) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSatisfiable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSubConceptOf(Concept subConcept, Concept superConcept) {
		// TODO Auto-generated method stub
		return false;
	}

	
	/* ********************************************
	 * Helpers
	 * *******************************************/

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
        OntologyNormalizer normalizer = new AxiomatizationNormalizer(factory);
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
        normalizer = new ConstructReductionNormalizer(factory);
        axioms = normalizer.normalizeAxioms(axioms);
        
        // Apply InverseImplicationTransformation (wsml-rule)
        normalizer = new InverseImplicationNormalizer(factory);
        axioms = normalizer.normalizeAxioms(axioms);
        
        // Apply Lloyd-Topor rules to get Datalog-compatible LEs
        normalizer = new LloydToporNormalizer(factory);
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
