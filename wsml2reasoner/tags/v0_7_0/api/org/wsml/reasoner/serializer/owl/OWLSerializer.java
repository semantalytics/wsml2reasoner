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
package org.wsml.reasoner.serializer.owl;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import org.semanticweb.owl.io.RendererException;
import org.semanticweb.owl.model.OWLOntology;

/**
 * An interface serializing OWL ontologies.
 * 
 * @author Nathalie Steinmetz, DERI Innsbruck
 */
public interface OWLSerializer {

    public static final String OWL_SERIALIZER = "OWL Serializer";

    public static final String OWL_RDF = "OWL RDF Syntax";

    public static final String OWL_ABSTRACT = "OWL Abstract Syntax";

    /**
     * This method serializes to OWL-RDF syntax.
     * 
     * @param ontology
     *            OWL ontology to be serialized
     * @param arg1
     *            Writer to be written to
     * @throws IOException
     */
    public void serialize(OWLOntology ontology, Writer arg1) throws RendererException;

    /**
     * This method serializes to either OWL-RDF syntax or OWL abstract syntax.
     * 
     * @param ontology
     *            OWL ontology to be serialized
     * @param arg1
     *            Writer to be written to
     * @param arg2
     *            a map that can contain a preference: - serializing to the
     *            OWL-RDF syntax or - serializing to the OWL abstract syntax If
     *            no map, or an empty map, is added, OWL-RDF syntax is the
     *            default
     * @throws IOException
     */
    public void serialize(OWLOntology ontology, Writer arg1, Map<?, ?> arg2) throws RendererException;

    /**
     * This method serializes to OWL-RDF syntax.
     * 
     * @param ontology
     *            OWL ontology to be serialized
     * @param arg1
     *            StringBuffer to be written to
     * @throws IOException
     */
    public void serialize(OWLOntology ontology, StringBuffer arg1) throws RendererException;

    /**
     * This method serializes to either OWL-RDF syntax or OWL abstract syntax.
     * 
     * @param ontology
     *            OWL ontology to be serialized
     * @param arg1
     *            StringBuffer to be written to
     * @param arg2
     *            a map that can contain a preference: - serializing to the
     *            OWL-RDF syntax or - serializing to the OWL abstract syntax If
     *            no map, or an empty map, is added, OWL-RDF syntax is the
     *            default
     * @throws IOException
     */
    public void serialize(OWLOntology ontology, StringBuffer arg1, Map<?, ?> arg2) throws RendererException;

}
