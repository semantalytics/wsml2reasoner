/**
 * WSML Reasoner Implementation.
 *
 * Copyright (c) 2009, STI Innsbruck, Austria.
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
package org.wsml.reasoner;

import java.util.Map;
import java.util.Set;

import org.sti2.elly.api.basics.IAtomicConcept;
import org.sti2.elly.api.basics.IAtomicRole;
import org.sti2.elly.api.basics.IConceptDescription;
import org.sti2.elly.api.basics.IDescription;
import org.sti2.elly.api.basics.IRoleDescription;
import org.sti2.elly.api.basics.IRule;
import org.sti2.elly.api.basics.IRuleBase;
import org.sti2.elly.api.terms.IIndividual;
import org.sti2.elly.api.terms.ITerm;

/**
 * This interface represents a facade to various WSML-DL v2.0 engines that allows to perform a reasoning request, e.g.
 * ELLY. For each such system a specific facade must be implemented to integrate the component into the system.
 * 
 * @author Daniel Winkler, STI Innsbruck
 */
public interface ELPReasonerFacade {

	/**
	 * Registers the rule base at the external reasoner.
	 * 
	 * @param ruleBase
	 *            the rule base that will be used for reasoning.
	 */
	public void register(IRuleBase ruleBase) throws ExternalToolException;

	/**
	 * Removes the rule base from the external reasoner.
	 * 
	 * @throws ExternalToolException
	 *             if exception happens during rule base removal
	 */
	public void deRegister() throws ExternalToolException;

	/**
	 * @return <code>true</code> if the given rule base is consistent, <code>false</code> otherwise.
	 */
	public boolean isConsistent() throws ExternalToolException;

    /**
     * Checks whether or not a Fact (i.e. a list of Atoms) is entailed by the rule base.
     * 
     * @param fact The Fact to be checked for entailment; Note that a Fact is a rule with empty body.
     * @return {@code true} if the the given Fact is entailed by the knowledge base; <code>false</code> otherwise.
     * @throws IllegalArgumentException if the given fact is not a valid Fact.
     */
	public boolean isEntailed(IRule fact) throws ExternalToolException;

	/**
	 * @return <code>true</code> if the given Description is satisfiable, <code>false</code> otherwise.
	 * @throws ExternalToolException
	 */
	public boolean isConsistent(IDescription description) throws ExternalToolException;

	/**
	 * @return a set containing all concepts from the loaded rule base
	 */
	public Set<IAtomicConcept> allConcepts() throws ExternalToolException;

	/**
	 * @return a set containing all individuals from the loaded rule base
	 */
	public Set<IIndividual> allIndividuals() throws ExternalToolException;

	/**
	 * @return a set containing all properties from the loaded rule base
	 */
	public Set<IAtomicRole> allRoles() throws ExternalToolException;

	/**
	 * @return a set containing all data properties from the loaded rule base
	 */
	public Set<IAtomicRole> allDataRoles() throws ExternalToolException;

	/**
	 * @return a set containing all object properties from the loaded rule base
	 */
	public Set<IAtomicRole> allObjectRoles() throws ExternalToolException;

	/**
	 * @return a set containing all subconcepts of a given class
	 */
	public Set<IAtomicConcept> subConceptsOf(IConceptDescription concept) throws ExternalToolException;

	/**
	 * @return a set containing all direct subconcepts of a given class
	 */
	public Set<IAtomicConcept> directSubConceptsOf(IConceptDescription concept) throws ExternalToolException;

	/**
	 * @return a set containing all superconcepts of a given class
	 */
	public Set<IAtomicConcept> superConceptsOf(IConceptDescription concept) throws ExternalToolException;

	/**
	 * @return a set containing all direct superconcepts of a given class
	 */
	public Set<IAtomicConcept> directSuperConceptsOf(IConceptDescription concept) throws ExternalToolException;

	/**
	 * @return a set containing all concepts equivalent to the given class
	 */
	public Set<IAtomicConcept> equivalentConceptsOf(IConceptDescription concept) throws ExternalToolException;

	/**
	 * @return true if the two given descriptions are equivalent
	 */
	public boolean isEquivalentConcept(IConceptDescription concept1, IConceptDescription concept2)
			throws ExternalToolException;

	/**
	 * @return true if the given concept2 is a subConcept of concept1
	 */
	public boolean isSubConceptOf(IConceptDescription concept1, IConceptDescription concept2)
			throws ExternalToolException;

	/**
	 * @return true if the given individual is an instance of concept
	 */
	public boolean isInstanceOf(IIndividual individual, IConceptDescription concept) throws ExternalToolException;

	/**
	 * @return a set all instances of a given class
	 */
	public Set<IIndividual> allInstancesOf(IConceptDescription concept) throws ExternalToolException;

	/**
	 * @return a set with all direct concepts of a given individual
	 */
	public Set<IAtomicConcept> directTypesOf(IIndividual individual) throws ExternalToolException;

	/**
	 * @return a set with all (also indirect) concepts of a given individual
	 */
	public Set<IAtomicConcept> typesOf(IIndividual individual) throws ExternalToolException;

	/**
	 * @return a set containing all subproperties of a given role
	 */
	public Set<IAtomicRole> subRolesOf(IRoleDescription role) throws ExternalToolException;

	/**
	 * @return a set containing all direct subproperties of a given role
	 */
	public Set<IAtomicRole> directSubRolesOf(IRoleDescription role) throws ExternalToolException;

	/**
	 * @return a set containing all superproperties of a given role
	 */
	public Set<IAtomicRole> superRolesOf(IRoleDescription role) throws ExternalToolException;

	/**
	 * @return a set containing all direct superproperties of a given role
	 */
	public Set<IAtomicRole> directSuperRolesOf(IRoleDescription role) throws ExternalToolException;

	/**
	 * @return a set containing all properties equivalent to the given role
	 */
	public Set<IAtomicRole> equivalentRolesOf(IRoleDescription role) throws ExternalToolException;

	/**
	 * @return a set containing all properties inverse to the given role
	 */
	public Set<IAtomicRole> inverseRolesOf(IRoleDescription role) throws ExternalToolException;

	/**
	 * @return a set containing the domains of the given role
	 */
	public Set<IConceptDescription> domainsOf(IRoleDescription role) throws ExternalToolException;

	/**
	 * @return a set containing the ranges of the given role
	 */
	public Set<IConceptDescription> rangesOf(IRoleDescription role) throws ExternalToolException;

	/**
	 * @return a map containing all data properties and for each a set containing all its values
	 */
	public Map<IAtomicRole, Set<ITerm>> getDataRoleValues(IIndividual subject) throws ExternalToolException;

	/**
	 * @return a map containing all object properties and for each a set containing all its values
	 */
	public Map<IAtomicRole, Set<ITerm>> getObjectRoleValues(IIndividual subject)
			throws ExternalToolException;

	/**
	 * @return a map containing all individuals who have values for a specified object role and for each a set
	 *         containing all its values
	 */
	public Map<IIndividual, Set<ITerm>> getRoleValues(IRoleDescription role) throws ExternalToolException;

	/**
	 * @return true if the given subject individual has the given object role with the given object individual as
	 *         value
	 * @throws ExternalToolException
	 */
	public boolean hasRoleValue(IIndividual subject, IRoleDescription role, IIndividual object)
			throws ExternalToolException;

	/**
	 * @return true if the given subject individual has the given data role with the given data value as value
	 * @throws ExternalToolException
	 */
	public boolean hasRoleValue(IIndividual subject, IRoleDescription role, ITerm object)
			throws ExternalToolException;

	/**
	 * @return individual value of the given individual and object role
	 * @throws ExternalToolException
	 */
	public IIndividual getObjectRoleValue(IIndividual subject, IRoleDescription role)
			throws ExternalToolException;

	/**
	 * @return data value of the given individual and data role
	 * @throws ExternalToolException
	 */
	public ITerm getDataRoleValue(IIndividual subject, IRoleDescription role) throws ExternalToolException;

	/**
	 * @return set containing individual values of the given individual and object role
	 * @throws ExternalToolException
	 */
	public Set<ITerm> getObjectRoleValues(IIndividual subject, IRoleDescription role)
			throws ExternalToolException;

	/**
	 * @return set containing data values of the given individual and data role
	 * @throws ExternalToolException
	 */
	public Set<ITerm> getDataRoleValues(IIndividual subject, IRoleDescription role)
			throws ExternalToolException;

}