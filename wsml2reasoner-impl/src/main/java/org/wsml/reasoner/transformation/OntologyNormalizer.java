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

package org.wsml.reasoner.transformation;

import java.util.Collection;
import java.util.Set;

import org.omwg.ontology.Axiom;
import org.wsmo.common.Entity;

/**
 * An interface for the normalization of ontologies written in WSML.
 * 
 * @author Uwe Keller, DERI Innsbruck
 */
public interface OntologyNormalizer {

    /**
     * Applies a normalization step to an ontology.
     * 
     * Class that implement this interface represent some sort of transformation
     * of ontologies that are relevant for applications.
     * 
     * NOTE: Implementations are not required to give back work on a copy of the
     * original ontoloy. Thus, they could change the original ontology
     * destructively during normalization.
     * 
     * @param ontology -
     *            the ontology to be transformed (according to a normalization
     *            process)
     * @return an ontology that represents the original ontology after
     *         application of the normalization step.
     */
    public Set<Entity> normalizeEntities(Collection<Entity> theEntities);

    public Set<Axiom> normalizeAxioms(Collection<Axiom> theAxioms);
}
