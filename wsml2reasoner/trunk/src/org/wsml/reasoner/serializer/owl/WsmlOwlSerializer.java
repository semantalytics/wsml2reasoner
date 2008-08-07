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
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.deri.wsmo4j.validator.WsmlValidatorImpl;
import org.omwg.ontology.Ontology;
import org.semanticweb.owl.io.RendererException;
import org.semanticweb.owl.model.OWLOntology;
import org.wsml.reasoner.api.WSMLReasonerFactory.BuiltInReasoner;
import org.wsml.reasoner.impl.DLBasedWSMLReasoner;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsmo.common.TopEntity;
import org.wsmo.validator.ValidationError;
import org.wsmo.validator.ValidationWarning;
import org.wsmo.validator.WsmlValidator;
import org.wsmo.wsml.Serializer;

/**
 * A serializer for OWL ontologies
 * 
 * @author Nathalie Steinmetz, DERI Innsbruck
 */
public class WsmlOwlSerializer implements Serializer {

    OWLSerializer owlSerializer = new OWLSerializerImpl();

    /**
     * This method allows to serialize a WSML-DL ontology to OWL-RDF syntax. The
     * resulting OWL ontology must not necessarily be valid OWL DL. The
     * serialized ontology could be validated at an online validator as e.g.
     * "http://phoebus.cs.man.ac.uk:9999/OWL/Validator"
     * 
     * @param arg0
     *            TopEntity array containing a WSML DL ontology to be
     *            transformed to owl
     * @param arg1
     *            Writer to be written to
     */
    public void serialize(TopEntity[] arg0, Writer arg1) throws IOException {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(OWLSerializer.OWL_SERIALIZER, OWLSerializer.OWL_RDF);
        serialize(arg0, arg1, map);
    }

    /**
     * This method allows to serialize a WSML-DL ontology to either OWL-RDF
     * syntax or OWL abstract syntax. The resulting OWL ontology must not
     * necessarily be valid OWL DL. The serialized ontology could be validated
     * at an online validator as e.g.
     * "http://phoebus.cs.man.ac.uk:9999/OWL/Validator".
     * 
     * @param arg0
     *            TopEntity array containing a WSML DL ontology to be
     *            transformed to owl
     * @param arg1
     *            Writer to be written to
     * @param arg2
     *            a map that can contain a preference: - serializing to the
     *            OWL-RDF syntax or - serializing to the OWL abstract syntax If
     *            no map, or an empty map, is added, OWL-RDF syntax is the
     *            default
     */
    public void serialize(TopEntity[] arg0, Writer arg1, Map arg2) throws IOException {
        OWLOntology owlOntology = transformOntology(arg0);
        try {
            owlSerializer.serialize(owlOntology, arg1, arg2);
        }
        catch (RendererException e) {
            throw new IOException(e.getMessage());
        }
    }

    /**
     * This method allows to serialize a WSML-DL ontology to OWL-RDF syntax. The
     * resulting OWL ontology must not necessarily be valid OWL DL. The
     * serialized ontology could be validated at an online validator as e.g.
     * "http://phoebus.cs.man.ac.uk:9999/OWL/Validator"
     * 
     * @param arg0
     *            TopEntity array containing a WSML DL ontology to be
     *            transformed to owl
     * @param arg1
     *            StringBuffer to be written to
     * @throws IOException
     */
    public void serialize(TopEntity[] arg0, StringBuffer arg1) {
        StringWriter writer = new StringWriter();
        try {
            serialize(arg0, writer);
        }
        catch (IOException e) {
            new RuntimeException("Error at writing!", e.getCause());
        }
        arg1.append(writer.getBuffer());
    }

    /**
     * This method allows to serialize a WSML-DL ontology to either OWL-RDF
     * syntax or OWL abstract syntax. The resulting OWL ontology must not
     * necessarily be valid OWL DL. The serialized ontology could be validated
     * at an online validator as e.g.
     * "http://phoebus.cs.man.ac.uk:9999/OWL/Validator".
     * 
     * @param arg0
     *            TopEntity array containing a WSML DL ontology to be
     *            transformed to owl
     * @param arg1
     *            StringBuffer to be written to
     * @param arg2
     *            a map that can contain a preference: - serializing to the
     *            OWL-RDF syntax or - serializing to the OWL abstract syntax If
     *            no map, or an empty map, is added, OWL-RDF syntax is the
     *            default
     */
    public void serialize(TopEntity[] arg0, StringBuffer arg1, Map arg2) {
        StringWriter writer = new StringWriter();
        try {
            serialize(arg0, writer, arg2);
        }
        catch (IOException e) {
            new RuntimeException("Error at writing!", e.getCause());
        }
        arg1.append(writer.getBuffer());
    }

    private OWLOntology transformOntology(TopEntity[] te) {
        Ontology ontology = (Ontology) te[0];

        DLBasedWSMLReasoner reasoner = new DLBasedWSMLReasoner(BuiltInReasoner.PELLET, new WSMO4JManager());

        // check if given WSML-DL ontology is valid
        WsmlValidator validator = new WsmlValidatorImpl();
        boolean valid = validator.isValid(ontology, "http://www.wsmo.org/wsml/wsml-syntax/wsml-dl", new ArrayList<ValidationError>(), new ArrayList<ValidationWarning>());
        if (!valid) {
            throw new RuntimeException("The given WSML-DL ontology is not " + "valid!");
        }
        // convert ontology
        return reasoner.createOWLOntology(ontology);
    }

}
