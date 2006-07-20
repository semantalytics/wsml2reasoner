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

import java.util.Set;

import org.mindswap.pellet.query.QueryResults;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLIndividual;
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
 *  Committed by $Author: nathalie $
 *  $Source: /home/richi/temp/w2r/wsml2reasoner/src/org/wsml/reasoner/DLReasonerFacade.java,v $,
 * </pre>
 *
 * @author Nathalie Steinmetz, DERI Innsbruck
 * @version $Revision: 1.2 $ $Date: 2006-07-20 17:50:23 $
 */
public interface DLReasonerFacade {

	/**
     * Registers the OWL-DL ontology at the external reasoner.
     * 
     * @param owlOntology the OWL-DL ontology
     * @throws OWLException
     */
    public void register(OWLOntology owlOntology) throws ExternalToolException;
    
    /**
     * Removes the ontology from the external reasoner.
     * 
     * @param ontologyURI the original logical ontology URI
     * @throws ExternalToolException if exception happens during ontology removal
     */
    public void deRegister(OWLOntology owlOntology);

    /** 
	 * @return true if the given ontology is consistent, false otherwise.
	 */
    public boolean isConsistent();
    
    /**
     * @return true if the given OWL Description is satisfiable, false otherwise.
     */
    public boolean isConsistent(OWLDescription clazz) 
			throws OWLException;
    
    /**
     * @return a set containing all classes from the loaded ontology
     */
	public Set<OWLEntity> allClasses();
	
	/**
	 * @return a set containing all individuals from the loaded ontology
	 */
	public Set<OWLEntity> allIndividuals();
	
	/**
	 * @return a set containing all individuals from the loaded ontology
	 */
	public Set<OWLEntity> allProperties();
    
	/**
	 * @return a set containing all subclasses of a given class
	 */
	public Set<Set> descendantClassesOf(OWLDescription clazz) 
			throws OWLException;
	
	/**
	 * @return a set containing all superclasses of a given class
	 */
	public Set<Set> ancestorClassesOf(OWLDescription clazz) 
			throws OWLException;
	
	/**
	 * @return a set containing all classes equivalent to the given class
	 */
	public Set<OWLEntity> equivalentClassesOf(OWLDescription clazz) 
			throws OWLException;
	
	/**
	 * @return true if the two given OWL descriptions are equivalent
	 */
	public boolean isEquivalentClass(OWLDescription clazz1, OWLDescription 
			clazz2) throws OWLException;
	
    /**
     * @return true if the given clazz2 is a subClass of clazz1
     */
    public boolean isSubClassOf(OWLDescription clazz1, OWLDescription clazz2) 
    		throws OWLException;
    
    /**
     * @return true if the given individual is an instance of clazz
     */
    public boolean isInstanceOf(OWLIndividual individual, OWLDescription clazz) 
    		throws OWLException;
    
    /**
     * @return a set all instances of a given OWL class
     */
    public Set<OWLEntity> allInstancesOf(OWLClass clazz) throws OWLException;
    
    /**
     * @return a set with all (also indirect) concepts of a given OWL individual
     */
    public Set<Set> allTypesOf(OWLIndividual individual) throws OWLException;
    
    /**
     * @return a set containing all subproperties of a given OWL property
     */
    public Set<Set> descendantPropertiesOf(OWLProperty property) 
    		throws OWLException;
    
    /**
     * @return a set containing all superproperties of a given OWL property
     */
    public Set<Set> ancestorPropertiesOf(OWLProperty property) 
    		throws OWLException; 
    
    /**
     * @return a set containing all properties equivalent to the given property
     */
	public Set<OWLEntity> equivalentPropertiesOf(OWLProperty property) 
			throws OWLException;
    
    /**
	 * Prints a class tree from the registered ontology.
	 */
    public void printClassTree();
    
    /**
     * Returns information about the registered ontology. Among these information 
     * are the expressivity, the number of classes, properties, individuals and 
     * GCIs.
     * 
     * @return String containing information about the registered ontology
     */
    public String getInfo();
    
    /**
     * Evaluates a given query on a particular external tool.
     * 
     * @param query the query to be evaluated.
     * @return a set of Query Results
     */
    public QueryResults evaluate(String query);
	
}
/*
 * $Log: not supported by cvs2svn $
 *
 */