package org.wsml.reasoner.builtin.elly;

import java.util.Map;
import java.util.Set;

import org.deri.iris.api.terms.ITerm;
import org.sti2.elly.api.basics.IAtomicConcept;
import org.sti2.elly.api.basics.IAtomicRole;
import org.sti2.elly.api.basics.IConceptDescription;
import org.sti2.elly.api.basics.IDescription;
import org.sti2.elly.api.basics.IRoleDescription;
import org.sti2.elly.api.basics.IRuleBase;
import org.sti2.elly.api.reasoning.IReasoner;
import org.sti2.elly.api.reasoning.ReasoningException;
import org.sti2.elly.api.terms.IIndividual;
import org.sti2.elly.reasoning.iris.IrisReasoner;
import org.wsml.reasoner.ELPReasonerFacade;
import org.wsml.reasoner.ExternalToolException;

public class ELLYFacade implements ELPReasonerFacade {

	private final IReasoner reasoner;

	public ELLYFacade() {
		reasoner = new IrisReasoner();
	}
	
	@Override
	public Set<IAtomicConcept> allClasses() throws ExternalToolException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<IAtomicRole> allDataProperties() throws ExternalToolException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<IIndividual> allIndividuals() throws ExternalToolException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<IIndividual> allInstancesOf(IConceptDescription clazz) throws ExternalToolException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<IAtomicRole> allObjectProperties() throws ExternalToolException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<IAtomicRole> allProperties() throws ExternalToolException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<IAtomicConcept> allTypesOf(IIndividual individual) throws ExternalToolException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<IAtomicConcept> ancestorClassesOf(IConceptDescription concept) throws ExternalToolException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<IRoleDescription> ancestorPropertiesOf(IRoleDescription property) throws ExternalToolException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deRegister() throws ExternalToolException {
		// TODO Auto-generated method stub

	}

	@Override
	public Set<IAtomicConcept> descendantClassesOf(IConceptDescription clazz) throws ExternalToolException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<IAtomicConcept> descendantPropertiesOf(IRoleDescription property) throws ExternalToolException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<IConceptDescription> domainsOf(IRoleDescription property) throws ExternalToolException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<IAtomicConcept> equivalentClassesOf(IConceptDescription concept) throws ExternalToolException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<IRoleDescription> equivalentPropertiesOf(IRoleDescription property) throws ExternalToolException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ITerm getDataPropertyValue(IIndividual subject, IRoleDescription property) throws ExternalToolException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<IRoleDescription, Set<ITerm>> getDataPropertyValues(IIndividual individual) throws ExternalToolException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<ITerm> getDataPropertyValues(IIndividual subject, IRoleDescription property)
			throws ExternalToolException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IIndividual getObjectPropertyValue(IIndividual subject, IRoleDescription property)
			throws ExternalToolException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<IRoleDescription, Set<ITerm>> getObjectPropertyValues(IIndividual individual)
			throws ExternalToolException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<ITerm> getObjectPropertyValues(IIndividual subject, IRoleDescription property)
			throws ExternalToolException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<IRoleDescription, Set<ITerm>> getPropertyValues(IRoleDescription property) throws ExternalToolException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasPropertyValue(IIndividual subject, IRoleDescription property, IIndividual object)
			throws ExternalToolException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasPropertyValue(IIndividual subject, IRoleDescription property, ITerm object)
			throws ExternalToolException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<IRoleDescription> inversePropertiesOf(IRoleDescription property) throws ExternalToolException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isConsistent() throws ExternalToolException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isConsistent(IDescription description) throws ExternalToolException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEquivalentClass(IConceptDescription concept1, IConceptDescription concept2)
			throws ExternalToolException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isInstanceOf(IIndividual individual, IConceptDescription concept) throws ExternalToolException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSubClassOf(IConceptDescription concept1, IConceptDescription concept2)
			throws ExternalToolException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<IConceptDescription> rangesOf(IRoleDescription property) throws ExternalToolException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void register(IRuleBase ruleBase) throws ExternalToolException {
		System.out.println("Registering rule base:\n" + ruleBase);
		
		try {
			reasoner.register(ruleBase);
		} catch (ReasoningException e) {
			throw new ExternalToolException("Could not register rule base", e);
		}
	}

	@Override
	public Set<IAtomicConcept> subClassesOf(IConceptDescription clazz) throws ExternalToolException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<IRoleDescription> subPropertiesOf(IRoleDescription property) throws ExternalToolException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<IAtomicConcept> superClassesOf(IConceptDescription concept) throws ExternalToolException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<IRoleDescription> superPropertiesOf(IRoleDescription property) throws ExternalToolException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<IAtomicConcept> typesOf(IIndividual individual) throws ExternalToolException {
		// TODO Auto-generated method stub
		return null;
	}

}
