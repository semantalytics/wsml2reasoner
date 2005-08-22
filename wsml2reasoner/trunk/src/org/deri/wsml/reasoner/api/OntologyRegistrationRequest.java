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

package org.deri.wsml.reasoner.api;

import java.util.Set;

import org.omwg.ontology.Ontology;

/**
 * Represents a request to register a set of ontologies at the reasoner The
 * possibility is given to register a set of ontologies to allow register a
 * group of ontologies that have circular import references.
 * 
 * @author Gabor Nagypal, FZI
 */
public interface OntologyRegistrationRequest extends OntologyBasedRequest {
    

    /**
     * @return a set of ontologies to which the request refers.
     */
    public Set<Ontology> getOntologies();

}
