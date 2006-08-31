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

import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import org.semanticweb.owl.io.RendererException;
import org.semanticweb.owl.io.abstract_syntax.Renderer;
import org.semanticweb.owl.model.OWLOntology;

/**
 * An implementation of an OWL serializer.
 * 
 * @author Nathalie Steinmetz, DERI Innsbruck
 */
public class OWLSerializerImpl implements OWLSerializer {

	public void serialize(OWLOntology owlOntology, Writer arg1) 
			throws RendererException {
		org.semanticweb.owl.io.owl_rdf.Renderer renderer = 
			new org.semanticweb.owl.io.owl_rdf.Renderer();
		renderer.renderOntology(owlOntology, arg1);
	}

	public void serialize(OWLOntology owlOntology, Writer arg1, Map arg2) 
			throws RendererException {
		boolean rdf_syntax = true;
		if (arg2 != null && arg2.containsKey(OWLSerializer.OWL_SERIALIZER)) {
			if (arg2.get(OWLSerializer.OWL_SERIALIZER).equals(OWLSerializer.OWL_ABSTRACT)) {
				rdf_syntax = false;
				Renderer renderer = new Renderer();
				renderer.renderOntology(owlOntology, arg1);
			}
		}
		if (rdf_syntax) {
			serialize(owlOntology, arg1);
		}
	}

	public void serialize(OWLOntology owlOntology, StringBuffer arg1) 
			throws RendererException {
		StringWriter writer = new StringWriter();
		serialize(owlOntology, writer);
		arg1.append(writer.getBuffer());		
	}

	public void serialize(OWLOntology owlOntology, StringBuffer arg1, Map arg2) 
			throws RendererException {
		StringWriter writer = new StringWriter();
		serialize(owlOntology, writer, arg2);
		arg1.append(writer.getBuffer());
	}
	
}
