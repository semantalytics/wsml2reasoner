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

package org.deri.wsml.reasoner.impl;

import java.util.LinkedHashSet;
import java.util.Set;

import org.deri.wsml.reasoner.api.*;
import org.deri.wsml.reasoner.api.queryanswering.QueryAnsweringRequest;
import org.deri.wsml.reasoner.wsmlcore.QueryAnsweringReasoner;
import org.deri.wsml.reasoner.wsmlcore.WSML2Datalog;
import org.deri.wsml.reasoner.wsmlcore.datalog.Program;
import org.deri.wsml.reasoner.wsmlcore.wrapper.DatalogReasonerFacade;
import org.deri.wsml.reasoner.wsmlcore.wrapper.ExternalToolException;
import org.deri.wsml.reasoner.wsmlcore.wrapper.dlv.DLVFacade;
import org.deri.wsml.reasoner.wsmlcore.wrapper.kaon2.Kaon2Facade;
import org.omwg.ontology.Axiom;
import org.omwg.ontology.Ontology;

/**
 * A prototypical implementation of a reasoner for WSML Core.
 * 
 * At present the implementation only supports the following reasoning tasks: -
 * Query answering
 * 
 * @author Uwe Keller, DERI Innsbruck
 */
public class WSMLCoreReasonerImpl implements WSMLReasoner {

    private DatalogReasonerFacade builtInFacade = null;

    public WSMLCoreReasonerImpl(WSMLReasonerFactory.BuiltInReasoner builtInType) {
        switch (builtInType) {
        case DLV:
            builtInFacade = new DLVFacade();
            break;
        case MANDARAX:
            throw new UnsupportedOperationException(
                    "Reasoning with Mandarax is not supported yet!");
        case KAON2:
            builtInFacade = new Kaon2Facade();
            break;
        default:
            throw new UnsupportedOperationException("Reasoning with "
                    + builtInType.toString() + " is not supported yet!");
        }
    }

    public Result execute(Request req) throws UnsupportedOperationException,
            IllegalArgumentException {

        if (req instanceof QueryAnsweringRequest) {

            QueryAnsweringReasoner qaReasoner = new QueryAnsweringReasoner(
                    builtInFacade);
            try {
                return qaReasoner.execute((QueryAnsweringRequest) req);
            } catch (ExternalToolException e) {
                e.printStackTrace();
                throw new IllegalArgumentException(
                        "Given query could not be deal with by the external tool!",
                        e);
            }

        } else if (req instanceof OntologyRegistrationRequest) {
            OntologyRegistrationRequest registrationReq = (OntologyRegistrationRequest) req;

            // TODO Do some extra checking to make sure that ontologies which
            // are imported are converted before ontologies which import them
            for (Ontology o : registrationReq.getOntologies()) {
                // convert the ontology to Datalog Program
                String ontologyUri = o.getIdentifier().toString();
                Program kb = new Program();
                kb.addAll(convertOntology(o));
                // Then register the program at the built-in reasoner
                try {
                    builtInFacade.register(ontologyUri, kb);
                } catch (ExternalToolException e) {
                    e.printStackTrace();
                    throw new IllegalArgumentException(
                            "This set of ontologies could not have been registered at the built-in reasoner",
                            e);
                }
            }
            return new VoidResult(registrationReq);

        } else {
            // Everything else is currently not supported!
            throw new UnsupportedOperationException("Requested reasoning ["
                    + req.getClass().toString()
                    + "] task is currently not supported by class "
                    + getClass().toString());
        }

    }

    private Program convertOntology(Ontology o) {
        Program p = new Program();

        // TODO: Insert normalization steps first
        // - Conceptual syntax to WSML Logical Expressions (Axioms)
        // - Resolve Anonymous Identifiers
        // - Llyod-Topor-Transformation
        // - Remove syntactical shortcuts
        // - ...

        // At present we only support very simple ontologies
        // - Consist only of logical expressions
        // - Axioms are simple WSML (datalog) rules

        WSML2Datalog wsml2datalog = new WSML2Datalog();
        Set<org.omwg.logexpression.LogicalExpression> lExprs = new LinkedHashSet<org.omwg.logexpression.LogicalExpression>();
        for (Object a : o.listAxioms()) {
            lExprs.addAll(((Axiom) a).listDefinitions());
        }
        p = wsml2datalog.transform(lExprs);

        return p;
    }

}
