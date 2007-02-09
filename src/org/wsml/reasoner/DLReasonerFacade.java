/**
 * WSML Reasoner Implementation.
 *
 * Copyright (c) 2005, FZI, Austria.
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

import java.net.URISyntaxException;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owl.impl.model.OWLConcreteDataImpl;
import org.semanticweb.owl.impl.model.OWLConcreteDataTypeImpl;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataValue;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLProperty;

/**
 * This interface represents a facade to various DL engines that allows to
 * perform a reasoning request, e.g. PELLET, KAON2.
 * 
 * For each such system a specific facade must be implemented to integrate the
 * component into the system.
 *
 * <pre>
 *  Created on July 3rd, 2006
 *  Committed by $Author: hlausen $
 *  $Source: /home/richi/temp/w2r/wsml2reasoner/src/org/wsml/reasoner/DLReasonerFacade.java,v $,
 * </pre>
 *
 * @author Nathalie Steinmetz, DERI Innsbruck
 * @version $Revision: 1.10 $ $Date: 2007-02-09 08:40:54 $
 */
public interface DLReasonerFacade {

	/**
     * Registers the OWL-DL ontology at the external reasoner.
     * 
     * @param owlOntology the OWL-DL ontology
     * @throws OWLException
     */
    public void register(OWLOntology owlOntology) 
    		throws ExternalToolException;
    
    /**
     * Removes the ontology from the external reasoner.
     * 
     * @param ontologyURI the original logical ontology URI
     * @throws ExternalToolException if exception happens during ontology removal
     */
    public void deRegister(String ontologyURI) 
    		throws ExternalToolException;

    /** 
	 * @return true if the given ontology is consistent, false otherwise.
	 */
    public boolean isConsistent(String ontologyURI);
    
    /**
     * @return true if the given OWL Description is satisfiable, false otherwise.
     * @throws InterruptedException 
     * @throws KAON2Exception 
     */
    public boolean isConsistent(String ontologyURI, 
    		OWLDescription description) throws OWLException, InterruptedException;
    
    /**
     * @return a set containing all classes from the loaded ontology
     * @throws KAON2Exception 
     * @throws OWLException 
     * @throws URISyntaxException 
     */
	public Set<OWLEntity> allClasses(String ontologyURI) 
			throws  OWLException, URISyntaxException;
	
	/**
	 * @return a set containing all individuals from the loaded ontology
	 * @throws KAON2Exception 
	 * @throws URISyntaxException 
	 * @throws OWLException 
	 */
	public Set<OWLEntity> allIndividuals(String ontologyURI) 
			throws  OWLException, URISyntaxException;
	
	/**
	 * @return a set containing all properties from the loaded ontology
	 * @throws KAON2Exception 
	 * @throws URISyntaxException 
	 * @throws OWLException 
	 */
	public Set<OWLEntity> allProperties(String ontologyURI) 
			throws  OWLException, URISyntaxException;
    
	/**
	 * @return a set containing all data properties from the loaded ontology
	 * @throws KAON2Exception 
	 * @throws URISyntaxException 
	 * @throws OWLException 
	 */
	public Set<OWLEntity> allDataProperties(String ontologyURI) 
			throws  OWLException, URISyntaxException;
	
	/**
	 * @return a set containing all object properties from the loaded ontology
	 * @throws KAON2Exception 
	 * @throws URISyntaxException 
	 * @throws OWLException 
	 */
	public Set<OWLEntity> allObjectProperties(String ontologyURI) 
			throws  OWLException, URISyntaxException;
	
	/**
	 * @return a set containing all subclasses of a given class
	 * @throws KAON2Exception 
	 * @throws URISyntaxException 
	 */
	public Set<Set> descendantClassesOf(String ontologyURI, OWLDescription clazz) 
			throws OWLException,  URISyntaxException;
	
	/**
	 * @return a set containing all direct subclasses of a given class
	 * @throws URISyntaxException 
	 * @throws KAON2Exception 
	 */
	public Set<Set> subClassesOf(String ontologyURI, 
			OWLDescription clazz) throws OWLException,  URISyntaxException;
	
	/**
	 * @return a set containing all superclasses of a given class
	 * @throws KAON2Exception 
	 * @throws URISyntaxException 
	 */
	public Set<Set> ancestorClassesOf(String ontologyURI, OWLDescription clazz) 
			throws OWLException,  URISyntaxException;
	
	/**
	 * @return a set containing all direct superclasses of a given class
	 * @throws URISyntaxException 
	 * @throws KAON2Exception 
	 */
	public Set<Set> superClassesOf(String ontologyURI, 
			OWLDescription clazz) throws OWLException,  URISyntaxException;
	
	/**
	 * @return a set containing all classes equivalent to the given class
	 * @throws KAON2Exception 
	 * @throws URISyntaxException 
	 */
	public Set<OWLEntity> equivalentClassesOf(String ontologyURI, OWLDescription clazz) 
			throws OWLException,  URISyntaxException;
	
	/**
	 * @return true if the two given OWL descriptions are equivalent
	 * @throws URISyntaxException 
	 * @throws KAON2Exception 
	 */
	public boolean isEquivalentClass(String ontologyURI, 
			OWLDescription clazz1, OWLDescription clazz2) 
			throws OWLException,  URISyntaxException;
	
    /**
     * @return true if the given clazz2 is a subClass of clazz1
     * @throws KAON2Exception 
     * @throws URISyntaxException 
     */
    public boolean isSubClassOf(String ontologyURI, OWLDescription clazz1, 
    		OWLDescription clazz2) 
    		throws OWLException,  URISyntaxException;
    
    /**
     * @return true if the given individual is an instance of clazz
     * @throws KAON2Exception 
     * @throws URISyntaxException 
     */
    public boolean isInstanceOf(String ontologyURI, OWLIndividual individual, 
    		OWLDescription clazz) 
    		throws OWLException,  URISyntaxException;
    
    /**
     * @return a set all instances of a given OWL class
     * @throws KAON2Exception 
     * @throws URISyntaxException 
     */
    public Set<OWLEntity> allInstancesOf(String ontologyURI, OWLClass clazz) 
    		throws OWLException,  URISyntaxException;
	
	/**
     * @return a set with all direct concepts of a given OWL individual
	 * @throws KAON2Exception 
	 * @throws URISyntaxException 
     */
	public Set<Set> typesOf(String ontologyURI, OWLIndividual individual) 
			throws OWLException,  URISyntaxException;
    
    /**
     * @return a set with all (also indirect) concepts of a given OWL individual
     * @throws KAON2Exception 
     * @throws URISyntaxException 
     */
    public Set<Set> allTypesOf(String ontologyURI, OWLIndividual individual) 
    		throws OWLException,  URISyntaxException;
    
    /**
     * @return a set containing all subproperties of a given OWL property
     * @throws KAON2Exception 
     * @throws URISyntaxException 
     */
    public Set<Set> descendantPropertiesOf(String ontologyURI, 
    		OWLProperty property) 
    		throws OWLException,  URISyntaxException;
    
    /**
     * @return a set containing all direct subproperties of a given OWL 
     * 			property
     * @throws URISyntaxException 
     * @throws KAON2Exception 
     */
    public Set<Set> subPropertiesOf(String ontologyURI, 
    		OWLProperty property) throws OWLException,  URISyntaxException;
    
    /**
     * @return a set containing all superproperties of a given OWL property
     * @throws KAON2Exception 
     * @throws URISyntaxException 
     */
    public Set<Set> ancestorPropertiesOf(String ontologyURI, 
    		OWLProperty property) 
    		throws OWLException,  URISyntaxException; 
    
    /**
     * @return a set containing all direct superproperties of a given OWL 
     * 			property
     * @throws URISyntaxException 
     * @throws KAON2Exception 
     */
    public Set<Set> superPropertiesOf(String ontologyURI, 
    		OWLProperty property) throws OWLException,  URISyntaxException; 
    
    /**
     * @return a set containing all properties equivalent to the given property
     * @throws KAON2Exception 
     * @throws URISyntaxException 
     */
	public Set<OWLEntity> equivalentPropertiesOf(String ontologyURI, 
			OWLProperty property) 
			throws OWLException,  URISyntaxException;
    
	/**
	 * @return a set containing all properties inverse to the given property
	 * @throws KAON2Exception 
	 * @throws URISyntaxException 
	 */
	public Set<OWLEntity> inversePropertiesOf(String ontologyURI, 
			OWLObjectProperty property) 
			throws OWLException,  URISyntaxException;
	
	/**
	 * @return a set containing the domains of the given property
	 * @throws KAON2Exception 
	 */
	public Set<OWLEntity> domainsOf(String ontologyURI, 
			OWLProperty property) 
			throws OWLException,  URISyntaxException;
	
	/**
	 * @return a set containing the ranges of the given property
	 * @throws URISyntaxException 
	 * @throws KAON2Exception 
	 */
	public Set<OWLEntity> rangesOf(String ontologyURI, 
			OWLObjectProperty property) 
			throws OWLException,  URISyntaxException;
	
	/**
	 * @return a set containing the ranges of the given property
	 * @throws URISyntaxException 
	 * @throws KAON2Exception 
	 */
	public Set<OWLConcreteDataTypeImpl> rangesOf(String ontologyURI, 
			OWLDataProperty property) 
			throws OWLException,  URISyntaxException;
	
	/**
	 * @return a map containing all data properties and for each a set containing
	 * 			all its values
	 * @throws URISyntaxException 
	 * @throws KAON2Exception 
	 */
	public Map<OWLEntity, Set<OWLConcreteDataImpl>> getDataPropertyValues(
			String ontologyURI, OWLIndividual individual) 
			throws OWLException,  URISyntaxException;
	
	/**
	 * @return a map containing all object properties and for each a set containing
	 * 			all its values
	 * @throws KAON2Exception 
	 * @throws URISyntaxException 
	 */
	public Map<OWLEntity, Set<OWLEntity>> getObjectPropertyValues(
			String ontologyURI, OWLIndividual individual) 
			throws OWLException,  URISyntaxException;
	
	/**
	 * @return a map containing all individuals who have values for a specified 
	 * 			object property and for each a set containing all its values
	 * @throws URISyntaxException 
	 * @throws KAON2Exception 
	 */
	public Map<OWLEntity, Set<OWLEntity>> getPropertyValues(String ontologyURI, 
			OWLObjectProperty property) 
			throws OWLException,  URISyntaxException;
	
	/**
	 * @return a map containing all individuals who have values for a specified 
	 * 			data property and for each a set containing all its values
	 * @throws URISyntaxException 
	 * @throws KAON2Exception 
	 */
	public Map<OWLEntity, Set<OWLConcreteDataImpl>> getPropertyValues(
			String ontologyURI, OWLDataProperty property) 
			throws OWLException,  URISyntaxException;
	
	/**
	 * @return true if the given subject individual has the given object property 
	 * 			with the given object individual as value
	 * @throws KAON2Exception 
	 * @throws InterruptedException 
	 */
	public boolean hasPropertyValue(String ontologyURI, OWLIndividual subject,
			OWLObjectProperty property, OWLIndividual object) 
			throws OWLException,  InterruptedException;
	
	/**
	 * @return true if the given subject individual has the given data property 
	 * 			with the given data value as value
	 * @throws InterruptedException 
	 * @throws KAON2Exception 
	 */
	public boolean hasPropertyValue(String ontologyURI, OWLIndividual subject,
			OWLDataProperty property, OWLDataValue object) 
			throws OWLException,  InterruptedException;
	
	/**
	 * @return individual value of the given individual and object property
	 * @throws InterruptedException 
	 * @throws KAON2Exception 
	 * @throws URISyntaxException 
	 */
	public OWLIndividual getObjectPropertyValue(String ontologyURI, 
			OWLIndividual subject, OWLObjectProperty property) 
			throws OWLException,  InterruptedException, URISyntaxException;
	
	/**
	 * @return data value of the given individual and data property
	 * @throws URISyntaxException 
	 * @throws InterruptedException 
	 * @throws KAON2Exception 
	 */
	public OWLDataValue getDataPropertyValue(String ontologyURI, 
			OWLIndividual subject, OWLDataProperty property) 
			throws OWLException,  InterruptedException, URISyntaxException;
	
	/**
	 * @return set containing individual values of the given individual and object property
	 * @throws InterruptedException 
	 * @throws KAON2Exception 
	 * @throws URISyntaxException 
	 */
	public Set<OWLEntity> getObjectPropertyValues(String ontologyURI, 
			OWLIndividual subject, OWLObjectProperty property) 
			throws OWLException,  InterruptedException, URISyntaxException;
	
	/**
	 * @return set containing data values of the given individual and data property
	 * @throws InterruptedException 
	 * @throws KAON2Exception 
	 * @throws URISyntaxException 
	 */
	public Set<OWLDataValue> getDataPropertyValues(String ontologyURI, 
			OWLIndividual subject, OWLDataProperty property) 
			throws OWLException,  InterruptedException, URISyntaxException;
    
//    /**
//     * Evaluates a given query on a particular external tool.
//     * 
//     * @param query the query to be evaluated.
//     * @return a set of Query Results
//     */
//    public QueryResults evaluate(String ontologyURI, String query);
	
}
/*
 * $Log: not supported by cvs2svn $
 * Revision 1.9  2007/01/11 13:04:46  nathalie
 * removed unnecessary dependencies from pellet library
 *
 * Revision 1.8  2007/01/10 11:50:39  nathalie
 * completed kaon2DLFacade
 *
 * Revision 1.7  2006/11/30 16:54:57  nathalie
 * added methods to get direct super-/sub-concepts and direct super-/sub-relations
 *
 * Revision 1.6  2006/08/31 12:36:00  nathalie
 * removed methods from WSMLDLReasoner interface to the WSMLReasoner interface. Replaced some methods by entails() and groundQuery() methods.
 *
 * Revision 1.5  2006/08/10 08:31:00  nathalie
 * added request for getting direct concept/concepts of an instance
 *
 * Revision 1.4  2006/08/08 10:14:28  nathalie
 * implemented support for registering multiple ontolgies at wsml-dl reasoner
 *
 * Revision 1.3  2006/07/21 16:25:21  nathalie
 * completing the pellet reasoner integration
 *
 * Revision 1.2  2006/07/20 17:50:23  nathalie
 * integration of the pellet reasoner
 *
 *
 */