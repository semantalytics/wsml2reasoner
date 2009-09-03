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

import org.deri.iris.api.terms.ITerm;
import org.sti2.elly.api.basics.IAtomicConcept;
import org.sti2.elly.api.basics.IAtomicRole;
import org.sti2.elly.api.basics.IConceptDescription;
import org.sti2.elly.api.basics.IDescription;
import org.sti2.elly.api.basics.IRoleDescription;
import org.sti2.elly.api.basics.IRuleBase;
import org.sti2.elly.api.terms.IIndividual;

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
	 * @return <code>true</code> if the given Description is satisfiable, <code>false</code> otherwise.
	 * @throws ExternalToolException
	 */
	public boolean isConsistent(IDescription description) throws ExternalToolException;

	/**
	 * @return a set containing all classes from the loaded rule base
	 */
	public Set<IAtomicConcept> allClasses() throws ExternalToolException;

	/**
	 * @return a set containing all individuals from the loaded rule base
	 */
	public Set<IIndividual> allIndividuals() throws ExternalToolException;

	/**
	 * @return a set containing all properties from the loaded rule base
	 */
	public Set<IAtomicRole> allProperties() throws ExternalToolException;

	/**
	 * @return a set containing all data properties from the loaded rule base
	 */
	public Set<IAtomicRole> allDataProperties() throws ExternalToolException;

	/**
	 * @return a set containing all object properties from the loaded rule base
	 */
	public Set<IAtomicRole> allObjectProperties() throws ExternalToolException;

	/**
	 * @return a set containing all subclasses of a given class
	 */
	public Set<IAtomicConcept> descendantClassesOf(IConceptDescription clazz) throws ExternalToolException;

	/**
	 * @return a set containing all direct subclasses of a given class
	 */
	public Set<IAtomicConcept> subClassesOf(IConceptDescription clazz) throws ExternalToolException;

	/**
	 * @return a set containing all superclasses of a given class
	 */
	public Set<IAtomicConcept> ancestorClassesOf(IConceptDescription concept) throws ExternalToolException;

	/**
	 * @return a set containing all direct superclasses of a given class
	 */
	public Set<IAtomicConcept> superClassesOf(IConceptDescription concept) throws ExternalToolException;

	/**
	 * @return a set containing all classes equivalent to the given class
	 */
	public Set<IAtomicConcept> equivalentClassesOf(IConceptDescription concept) throws ExternalToolException;

	/**
	 * @return true if the two given OWL descriptions are equivalent
	 */
	public boolean isEquivalentClass(IConceptDescription concept1, IConceptDescription concept2)
			throws ExternalToolException;

	/**
	 * @return true if the given clazz2 is a subClass of clazz1
	 */
	public boolean isSubClassOf(IConceptDescription concept1, IConceptDescription concept2)
			throws ExternalToolException;

	/**
	 * @return true if the given individual is an instance of clazz
	 */
	public boolean isInstanceOf(IIndividual individual, IConceptDescription concept) throws ExternalToolException;

	/**
	 * @return a set all instances of a given OWL class
	 */
	public Set<IIndividual> allInstancesOf(IConceptDescription clazz) throws ExternalToolException;

	/**
	 * @return a set with all direct concepts of a given OWL individual
	 */
	public Set<IAtomicConcept> typesOf(IIndividual individual) throws ExternalToolException;

	/**
	 * @return a set with all (also indirect) concepts of a given OWL individual
	 */
	public Set<IAtomicConcept> allTypesOf(IIndividual individual) throws ExternalToolException;

	/**
	 * @return a set containing all subproperties of a given OWL property
	 */
	public Set<IAtomicConcept> descendantPropertiesOf(IRoleDescription property) throws ExternalToolException;

	/**
	 * @return a set containing all direct subproperties of a given OWL property
	 */
	public Set<IRoleDescription> subPropertiesOf(IRoleDescription property) throws ExternalToolException;

	/**
	 * @return a set containing all superproperties of a given OWL property
	 */
	public Set<IRoleDescription> ancestorPropertiesOf(IRoleDescription property) throws ExternalToolException;

	/**
	 * @return a set containing all direct superproperties of a given OWL property
	 */
	public Set<IRoleDescription> superPropertiesOf(IRoleDescription property) throws ExternalToolException;

	/**
	 * @return a set containing all properties equivalent to the given property
	 */
	public Set<IRoleDescription> equivalentPropertiesOf(IRoleDescription property) throws ExternalToolException;

	/**
	 * @return a set containing all properties inverse to the given property
	 */
	public Set<IRoleDescription> inversePropertiesOf(IRoleDescription property) throws ExternalToolException;

	/**
	 * @return a set containing the domains of the given property
	 */
	public Set<IConceptDescription> domainsOf(IRoleDescription property) throws ExternalToolException;

	/**
	 * @return a set containing the ranges of the given property
	 */
	public Set<IConceptDescription> rangesOf(IRoleDescription property) throws ExternalToolException;

	/**
	 * @return a map containing all data properties and for each a set containing all its values
	 */
	public Map<IRoleDescription, Set<ITerm>> getDataPropertyValues(IIndividual individual) throws ExternalToolException;

	/**
	 * @return a map containing all object properties and for each a set containing all its values
	 */
	public Map<IRoleDescription, Set<ITerm>> getObjectPropertyValues(IIndividual individual)
			throws ExternalToolException;

	/**
	 * @return a map containing all individuals who have values for a specified object property and for each a set
	 *         containing all its values
	 */
	public Map<IRoleDescription, Set<ITerm>> getPropertyValues(IRoleDescription property) throws ExternalToolException;

	/**
	 * @return true if the given subject individual has the given object property with the given object individual as
	 *         value
	 * @throws ExternalToolException
	 */
	public boolean hasPropertyValue(IIndividual subject, IRoleDescription property, IIndividual object)
			throws ExternalToolException;

	/**
	 * @return true if the given subject individual has the given data property with the given data value as value
	 * @throws ExternalToolException
	 */
	public boolean hasPropertyValue(IIndividual subject, IRoleDescription property, ITerm object)
			throws ExternalToolException;

	/**
	 * @return individual value of the given individual and object property
	 * @throws ExternalToolException
	 */
	public IIndividual getObjectPropertyValue(IIndividual subject, IRoleDescription property)
			throws ExternalToolException;

	/**
	 * @return data value of the given individual and data property
	 * @throws ExternalToolException
	 */
	public ITerm getDataPropertyValue(IIndividual subject, IRoleDescription property) throws ExternalToolException;

	/**
	 * @return set containing individual values of the given individual and object property
	 * @throws ExternalToolException
	 */
	public Set<ITerm> getObjectPropertyValues(IIndividual subject, IRoleDescription property)
			throws ExternalToolException;

	/**
	 * @return set containing data values of the given individual and data property
	 * @throws ExternalToolException
	 */
	public Set<ITerm> getDataPropertyValues(IIndividual subject, IRoleDescription property)
			throws ExternalToolException;

}