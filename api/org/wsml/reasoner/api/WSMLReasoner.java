/**
 * WSML Reasoner Implementation.
 *
 * Copyright (c) 2005, University of Innsbruck, Austria.
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

package org.wsml.reasoner.api;

import java.util.Set;

import org.omwg.ontology.Ontology;
import org.wsml.reasoner.api.inconsistency.InconsistencyException;

/**
 * An interface for invoking a WSML reasoner with a particular reasoning task.
 * 
 * @author Uwe Keller, DERI Innsbruck
 */
public interface WSMLReasoner {

    /**
     * Registers the ontology with the reasoner. All currently registered
     * contents of the reasoner are removed and replaced with the elements of
     * the new ontology.
     * 
     * @param ontology
     *            The ontology to register
     */
    public void registerOntology(Ontology ontology) throws InconsistencyException;

    /**
     * Register all given ontologies with the reasoner.
     * 
     * @param ontologies
     *            The ontologies to register in the same reasoning space.
     * @throws InconsistencyException
     */
    public void registerOntologies(Set<Ontology> ontologies) throws InconsistencyException;

    /**
     * Registers the ontology. If the ontology is already registered, updates
     * the ontology content.
     * 
     * @param ontology
     */
    public void registerOntologyNoVerification(Ontology ontology);

    /*
     * Register some entities and do a consistency check.
     * 
     * @param theEntities
     *            The entities to register.
     * @throws InconsistencyException
     *             If a consistency violation is detected.
     */
    // not supported any more, use registerOntology*() 
	//    public void registerEntities(Set<Entity> theEntities) throws InconsistencyException;
	//
	//    public void registerEntitiesNoVerification(Set<Entity> ontologies);

    /**
     * Deregisters the ontology. Any further request using this ontologyID will
     * result in an exception.
     * 
     * @param ontologyID
     */
    public void deRegister();
}
