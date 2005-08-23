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

package org.deri.wsml.reasoner.normalization;

import org.omwg.ontology.Ontology;

/**
 * An interface for the normalization of ontologies written in WSML.
 * 
 * @author Uwe Keller, DERI Innsbruck
 */
public interface OntologyNormalizer
{

    /**
     * Applies a normalization step to an ontology.
     * 
     * Class that implement this interface represent some sort of
     * transformation of ontologies that are relevant for applications.
     *
     * NOTE: Implementations are not required to give back work on a 
     * copy of the original ontoloy. Thus, they could change the original
     * ontology destructively during normalization. 
     * 
     * @param o - the ontology to be transformed (according to a normalization process)
     * @return an ontology that represents the original ontology after application of
     *         the normalization step.
     */
    public Ontology normalize(Ontology ontology);
}
