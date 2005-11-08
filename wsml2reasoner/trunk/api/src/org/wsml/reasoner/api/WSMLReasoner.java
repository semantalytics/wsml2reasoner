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

import java.util.Map;
import java.util.Set;

import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Concept;
import org.omwg.ontology.Instance;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Variable;
import org.wsmo.common.IRI;

/**
 * An interface for invoking a WSML reasoner with a particular reasoning task.
 * 
 * @author Uwe Keller, DERI Innsbruck
 */
public interface WSMLReasoner {

//    /**
//     * Sends a request about some reasoning task to a reasoner. A reasoner might
//     * support different reasoning tasks such as for instance query answering or
//     * checking logical entailment of a statement wrt. to some ontology or
//     * knowledgebase.
//     * 
//     * @param req -
//     *            a description of the reasoning task to be performed.
//     * @return the result to the given request.
//     * @throws UnsupportedOperationException -
//     *             in case that the given request referes to a reasoning task
//     *             that is not supported by the respective reasoner.
//     * @deprecated
//     */
//    public Result execute(Request req) throws UnsupportedOperationException;

    public void registerOntology(Set<Ontology> ontologies);

    /**
     * Registers the ontology. If the ontology is already registered, updates
     * the ontology content.
     * 
     * @param ontology
     */
    public void registerOntology(Ontology ontology);

    /**
     * Deregisters the ontology. Any further request using this ontologyID will
     * result in an exception.
     * 
     * @param ontologyID
     */
    public void deRegisterOntology(IRI ontologyID);

    public void deRegisterOntology(Set<IRI> ontologyIDs);

    public Set<Map<Variable, Term>> executeQuery(IRI ontologyID,
            LogicalExpression query);

    public boolean executeGroundQuery(IRI ontologyID, LogicalExpression query);

    public boolean entails(IRI ontologyID, LogicalExpression expression);

    public boolean entails(IRI ontologyID, Set<LogicalExpression> expressions);

    public boolean isSubConceptOf(IRI ontologyID, Concept subConcept,
            Concept superConcept);

    public boolean isMemberOf(IRI ontologyID, Instance instance, Concept concept);

    public Set<Concept> getSubConcepts(IRI ontologyID, Concept concept);

    public Set<Concept> getSuperConcepts(IRI ontologyID, Concept concept);

    public Set<Instance> getInstances(IRI ontologyID, Concept concept);

    public Set<Concept> getConcepts(IRI ontologyID, Instance instance);

}
