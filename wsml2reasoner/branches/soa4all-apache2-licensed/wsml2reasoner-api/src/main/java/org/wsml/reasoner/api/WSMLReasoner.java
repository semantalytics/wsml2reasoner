/*
 * Copyright 2011, Semantic Technology Institute (STI) Innsbruck, 
 * University of Innsbruck, Technikerstrasse 21a, 6020 Innsbruck, Austria.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
     * @throws InconsistencyException
     */
    public void registerOntology(Ontology ontology) throws InconsistencyException;

    /**
     * Register all given ontologies with the reasoner.
     * All currently registered contents of the reasoner are removed and replaced with 
     * the elements of the new ontologies.
     * 
     * @param ontologies
     *            The ontologies to register in the same reasoning space.
     * @throws InconsistencyException
     */
    public void registerOntologies(Set<Ontology> ontologies) throws InconsistencyException;

    /**
     * Registers the ontology. All currently registered
     * contents of the reasoner are removed and replaced with the elements of
     * the new ontology. No verification is performed.
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
     * TODO danwin has no parameters how does this work
     * 
     * @param ontologyID
     */
    public void deRegister();
}