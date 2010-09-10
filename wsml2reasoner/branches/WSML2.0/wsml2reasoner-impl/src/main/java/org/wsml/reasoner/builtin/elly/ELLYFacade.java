package org.wsml.reasoner.builtin.elly;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.sti2.elly.api.basics.IAtomicConcept;
import org.sti2.elly.api.basics.IAtomicRole;
import org.sti2.elly.api.basics.IConceptDescription;
import org.sti2.elly.api.basics.IDescription;
import org.sti2.elly.api.basics.IRoleDescription;
import org.sti2.elly.api.basics.IRule;
import org.sti2.elly.api.basics.IRuleBase;
import org.sti2.elly.api.basics.ITuple;
import org.sti2.elly.api.factory.IBasicFactory;
import org.sti2.elly.api.reasoning.IReasoner;
import org.sti2.elly.api.reasoning.ReasoningException;
import org.sti2.elly.api.terms.IIndividual;
import org.sti2.elly.api.terms.ITerm;
import org.sti2.elly.basics.BasicFactory;
import org.sti2.elly.reasoning.iris.IrisReasoner;
import org.wsml.reasoner.ELPReasonerFacade;
import org.wsml.reasoner.ExternalToolException;

public class ELLYFacade implements ELPReasonerFacade {

	private static IBasicFactory BASIC = BasicFactory.getInstance();

	private final IReasoner reasoner;

	public ELLYFacade() {
		reasoner = new IrisReasoner();
	}

	@Override
	public Set<IAtomicConcept> allConcepts() throws ExternalToolException {
		return reasoner.allConcepts();
	}

	@Override
	public Set<IAtomicRole> allDataRoles() throws ExternalToolException {
		return reasoner.allRoles();
	}

	@Override
	public Set<IIndividual> allIndividuals() throws ExternalToolException {
		return reasoner.allIndividuals();
	}

	@Override
	public Set<IIndividual> allInstancesOf(IConceptDescription concept) throws ExternalToolException {
		Set<IIndividual> individuals = new HashSet<IIndividual>();

		try {
			for (ITuple tuple : reasoner.allInstancesOf(concept)) {
				assert tuple.size() == 1;
				assert tuple.get(0) instanceof IIndividual;

				individuals.add((IIndividual) tuple.get(0));
			}
		} catch (ReasoningException e) {
			throw new ExternalToolException("allInstancesOf(" + concept.toString() + ")", e);
		}

		return individuals;
	}

	@Override
	public Set<IAtomicRole> allObjectRoles() throws ExternalToolException {
		return reasoner.allRoles();
	}

	@Override
	public Set<IAtomicRole> allRoles() throws ExternalToolException {
		return reasoner.allRoles();
	}

	@Override
	public Set<IAtomicConcept> typesOf(IIndividual individual) throws ExternalToolException {
		ITuple individualTuple = BASIC.createTuple(individual);
		try {
			return reasoner.conceptsOf(individualTuple);
		} catch (ReasoningException e) {
			throw new ExternalToolException("allTypesOf(" + individual.toString() + ")", e);
		}
	}

	@Override
	public Set<IAtomicConcept> superConceptsOf(IConceptDescription concept) throws ExternalToolException {
		try {
			return reasoner.superConceptOf(concept);
		} catch (ReasoningException e) {
			throw new ExternalToolException("superConceptsOf(" + concept.toString() + ")", e);
		}
	}

	@Override
	public Set<IAtomicRole> superRolesOf(IRoleDescription role) throws ExternalToolException {
		try {
			return reasoner.superRoleOf(role);
		} catch (ReasoningException e) {
			throw new ExternalToolException("superRolesOf(" + role.toString() + ")", e);
		}
	}

	@Override
	public void deRegister() throws ExternalToolException {
		reasoner.deRegister();
	}

	@Override
	public Set<IAtomicConcept> subConceptsOf(IConceptDescription concept) throws ExternalToolException {
		try {
			return reasoner.subConceptOf(concept);
		} catch (ReasoningException e) {
			throw new ExternalToolException("subConceptsOf(" + concept.toString() + ")", e);
		}
	}

	@Override
	public Set<IAtomicRole> subRolesOf(IRoleDescription role) throws ExternalToolException {
		try {
			return reasoner.subRoleOf(role);
		} catch (ReasoningException e) {
			throw new ExternalToolException("subRolesOf(" + role.toString() + ")", e);
		}
	}

	@Override
	public Set<IConceptDescription> domainsOf(IRoleDescription role) throws ExternalToolException {
		throw new ExternalToolException("domainsOf(" + role.toString() + ") not supported!");
	}

	@Override
	public Set<IAtomicConcept> equivalentConceptsOf(IConceptDescription concept) throws ExternalToolException {
		try {
			return reasoner.equivalentConceptOf(concept);
		} catch (ReasoningException e) {
			throw new ExternalToolException("equivalentConceptsOf(" + concept.toString() + ")", e);
		}
	}

	@Override
	public Set<IAtomicRole> equivalentRolesOf(IRoleDescription role) throws ExternalToolException {
		try {
			return reasoner.equivalentRoleOf(role);
		} catch (ReasoningException e) {
			throw new ExternalToolException("equivalentRolesOf(" + role.toString() + ")", e);
		}
	}

	@Override
	public ITerm getDataRoleValue(IIndividual subject, IRoleDescription role) throws ExternalToolException {
		try {
			for (ITuple tuple : reasoner.allInstancesOf(role)) {
				if (tuple.get(0).equals(subject))
					return tuple.get(1);
			}
			return null;
		} catch (ReasoningException e) {
			throw new ExternalToolException("getDataRoleValue(" + subject.toString() + "," + role.toString() + ")", e);
		}
	}

	@Override
	public Map<IAtomicRole, Set<ITerm>> getDataRoleValues(IIndividual subject) throws ExternalToolException {
		try {
			Map<IAtomicRole, Set<ITerm>> roleTermSetMap = new HashMap<IAtomicRole, Set<ITerm>>();

			for (IAtomicRole role : reasoner.allRoles()) {
				Set<ITerm> termSet = new HashSet<ITerm>();

				for (ITuple tuple : reasoner.allInstancesOf(role)) {
					if (tuple.get(0).equals(subject))
						termSet.add(tuple.get(1));
				}

				if (termSet.size() > 0)
					roleTermSetMap.put(role, termSet);
			}

			return roleTermSetMap;
		} catch (ReasoningException e) {
			throw new ExternalToolException("getDataRoleValues(" + subject.toString() + ")", e);
		}
	}

	@Override
	public Set<ITerm> getDataRoleValues(IIndividual subject, IRoleDescription role) throws ExternalToolException {
		try {
			Set<ITerm> termSet = new HashSet<ITerm>();

			for (ITuple tuple : reasoner.allInstancesOf(role)) {
				if (tuple.get(0).equals(subject))
					termSet.add(tuple.get(1));
			}

			return termSet;
		} catch (ReasoningException e) {
			throw new ExternalToolException("getDataRoleValues(" + subject.toString() + "," + role.toString() + ")", e);
		}
	}

	@Override
	public IIndividual getObjectRoleValue(IIndividual subject, IRoleDescription role) throws ExternalToolException {
		try {
			for (ITuple tuple : reasoner.allInstancesOf(role)) {
				if (tuple.get(0).equals(subject))
					return (IIndividual) tuple.get(1);
			}
			return null;
		} catch (ReasoningException e) {
			throw new ExternalToolException("getObjectRoleValue(" + subject.toString() + "," + role.toString() + ")", e);
		}
	}

	@Override
	public Map<IAtomicRole, Set<ITerm>> getObjectRoleValues(IIndividual subject) throws ExternalToolException {
		try {
			Map<IAtomicRole, Set<ITerm>> roleTermSetMap = new HashMap<IAtomicRole, Set<ITerm>>();

			for (IAtomicRole role : reasoner.allRoles()) {
				Set<ITerm> termSet = new HashSet<ITerm>();

				for (ITuple tuple : reasoner.allInstancesOf(role)) {
					if (tuple.get(0).equals(subject))
						termSet.add(tuple.get(1));
				}

				if (termSet.size() > 0)
					roleTermSetMap.put(role, termSet);
			}

			return roleTermSetMap;
		} catch (ReasoningException e) {
			throw new ExternalToolException("getObjectRoleValues(" + subject.toString() + ")", e);
		}
	}

	@Override
	public Set<ITerm> getObjectRoleValues(IIndividual subject, IRoleDescription role) throws ExternalToolException {
		try {
			Set<ITerm> termSet = new HashSet<ITerm>();

			for (ITuple tuple : reasoner.allInstancesOf(role)) {
				if (tuple.get(0).equals(subject))
					termSet.add(tuple.get(1));
			}

			return termSet;
		} catch (ReasoningException e) {
			throw new ExternalToolException("getObjectRoleValues(" + subject.toString() + "," + role.toString() + ")", e);
		}
	}

	@Override
	public Map<IIndividual, Set<ITerm>> getRoleValues(IRoleDescription role) throws ExternalToolException {
		try {
			Map<IIndividual, Set<ITerm>> individualTermSetMap = new HashMap<IIndividual, Set<ITerm>>();

			for (ITuple tuple : reasoner.allInstancesOf(role)) {
				ITerm subject = tuple.get(0);
				ITerm object = tuple.get(1);

				Set<ITerm> termSet = individualTermSetMap.get(subject);
				if (termSet == null) {
					termSet = new HashSet<ITerm>();
					individualTermSetMap.put((IIndividual) subject, termSet);
				}

				termSet.add(object);
			}

			return individualTermSetMap;
		} catch (ReasoningException e) {
			throw new ExternalToolException("getRoleValues(" + role.toString() + ")", e);
		}
	}

	@Override
	public boolean hasRoleValue(IIndividual subject, IRoleDescription role, IIndividual object)
			throws ExternalToolException {
		try {
			for (ITuple tuple : reasoner.allInstancesOf(role)) {
				if (tuple.get(0).equals(subject) && tuple.get(1).equals(object))
					return true;
			}
			return false;
		} catch (ReasoningException e) {
			throw new ExternalToolException("hasRoleValue(" + subject.toString() + "," + role.toString() + "," + object.toString() + ")", e);
		}
	}

	@Override
	public boolean hasRoleValue(IIndividual subject, IRoleDescription role, ITerm object) throws ExternalToolException {
		try {
			for (ITuple tuple : reasoner.allInstancesOf(role)) {
				if (tuple.get(0).equals(subject) && tuple.get(1).equals(object))
					return true;
			}
			return false;
		} catch (ReasoningException e) {
			throw new ExternalToolException("hasRoleValue(" + subject.toString() + "," + role.toString() + "," + object.toString() + ")", e);
		}
	}

	@Override
	public Set<IAtomicRole> inverseRolesOf(IRoleDescription role) throws ExternalToolException {
		throw new ExternalToolException("inverseRolesOf(" + role.toString() + ") not supported!");
	}

	@Override
	public boolean isEntailed(IRule fact) throws ExternalToolException {
		try {
			return reasoner.isEntailed(fact);
		} catch (ReasoningException e) {
			throw new ExternalToolException("isEntailed(" + fact + ")", e);
		}
	}
	
	@Override
	public boolean isConsistent() throws ExternalToolException {
		try {
			return reasoner.isSatisfiable();
		} catch (ReasoningException e) {
			throw new ExternalToolException("isConsistent()", e);
		}
	}

	@Override
	public boolean isConsistent(IDescription description) throws ExternalToolException {
		try {
			if (description instanceof IConceptDescription) {
				return reasoner.isSatisfiable((IConceptDescription) description);
			} else if (description instanceof IRoleDescription) {
				return reasoner.isSatisfiable((IRoleDescription) description);
			} else {
				throw new IllegalArgumentException("isConsistent(" + description + "); Type of description not supported!");
			}
		} catch (ReasoningException e) {
			throw new ExternalToolException("isConsistent()", e);
		}
	}

	@Override
	public boolean isEquivalentConcept(IConceptDescription concept1, IConceptDescription concept2)
			throws ExternalToolException {
		try {
			return reasoner.isEquivalentConcept(concept1, concept2);
		} catch (ReasoningException e) {
			throw new ExternalToolException("isEquivalentConcept(" + concept1.toString() + ", " + concept2.toString() + ")", e);
		}
	}

	@Override
	public boolean isInstanceOf(IIndividual individual, IConceptDescription concept) throws ExternalToolException {
		try {
			ITuple tuple = BASIC.createTuple(individual);
			return reasoner.isInstanceOf(tuple, concept);
		} catch (ReasoningException e) {
			throw new ExternalToolException("isInstanceOf(" + individual.toString() + ", " + concept.toString() + ")", e);
		}
	}

	@Override
	public boolean isSubConceptOf(IConceptDescription concept1, IConceptDescription concept2)
			throws ExternalToolException {
		try {
			return reasoner.isSubConceptOf(concept1, concept2);
		} catch (ReasoningException e) {
			throw new ExternalToolException("isSubConceptOf(" + concept1.toString() + ", " + concept2.toString() + ")", e);
		}
	}

	@Override
	public Set<IConceptDescription> rangesOf(IRoleDescription role) throws ExternalToolException {
		throw new ExternalToolException("rangesOf(" + role.toString() + ") not supported!");
	}

	@Override
	public void register(IRuleBase ruleBase) throws ExternalToolException {
//		System.out.println("Registering rule base:\n" + ruleBase);

		try {
			reasoner.register(ruleBase);
		} catch (ReasoningException e) {
			throw new ExternalToolException("Could not register rule base", e);
		}
	}

	@Override
	public Set<IAtomicConcept> directSubConceptsOf(IConceptDescription concept) throws ExternalToolException {
		throw new UnsupportedOperationException("directSubConceptsOf not supported");
	}

	@Override
	public Set<IAtomicRole> directSubRolesOf(IRoleDescription role) throws ExternalToolException {
		throw new UnsupportedOperationException("directSubRolesOf not supported");
	}

	@Override
	public Set<IAtomicConcept> directSuperConceptsOf(IConceptDescription concept) throws ExternalToolException {
		throw new UnsupportedOperationException("directSuperConceptsOf not supported");
	}

	@Override
	public Set<IAtomicRole> directSuperRolesOf(IRoleDescription role) throws ExternalToolException {
		throw new UnsupportedOperationException("directSuperRolesOf not supported");
	}

	@Override
	public Set<IAtomicConcept> directTypesOf(IIndividual individual) throws ExternalToolException {
		throw new UnsupportedOperationException("directTypesOf not supported");
	}
}
