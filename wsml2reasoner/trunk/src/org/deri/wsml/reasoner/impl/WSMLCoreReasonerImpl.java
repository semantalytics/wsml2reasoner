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

import org.deri.wsml.reasoner.api.OntologyRegistrationRequest;
import org.deri.wsml.reasoner.api.Request;
import org.deri.wsml.reasoner.api.Result;
import org.deri.wsml.reasoner.api.WSMLReasoner;
import org.deri.wsml.reasoner.api.WSMLReasonerFactory;
import org.deri.wsml.reasoner.api.queryanswering.QueryAnsweringRequest;
import org.deri.wsml.reasoner.normalization.AxiomatizationNormalizer;
import org.deri.wsml.reasoner.normalization.ConstructReductionNormalizer;
import org.deri.wsml.reasoner.normalization.LloydToporNormalizer;
import org.deri.wsml.reasoner.normalization.OntologyNormalizer;
import org.deri.wsml.reasoner.normalization.WSML2DatalogTransformer;
import org.deri.wsml.reasoner.wsmlcore.QueryAnsweringReasoner;
import org.deri.wsml.reasoner.wsmlcore.WSML2Datalog;
import org.deri.wsml.reasoner.wsmlcore.datalog.Program;
import org.deri.wsml.reasoner.wsmlcore.wrapper.DatalogReasonerFacade;
import org.deri.wsml.reasoner.wsmlcore.wrapper.ExternalToolException;
import org.deri.wsml.reasoner.wsmlcore.wrapper.dlv.DLVFacade;
import org.deri.wsml.reasoner.wsmlcore.wrapper.kaon2.Kaon2Facade;
import org.deri.wsml.reasoner.wsmlcore.wrapper.mandrax.MandraxFacade;
import org.omwg.ontology.Axiom;
import org.omwg.ontology.Ontology;

/**
 * A prototypical implementation of a reasoner for WSML Core and WSML Flight.
 * 
 * At present the implementation only supports the following reasoning tasks: -
 * Query answering
 * Ontology registration
 * 
 * @author Uwe Keller, DERI Innsbruck
 * @author Gabor Nagypal, FZI
 */
public class WSMLCoreReasonerImpl implements WSMLReasoner {

    private DatalogReasonerFacade builtInFacade = null;

    public WSMLCoreReasonerImpl(WSMLReasonerFactory.BuiltInReasoner builtInType) {
        switch (builtInType) {
        case DLV:
            builtInFacade = new DLVFacade();
            break;
        case MANDARAX:
            builtInFacade = new MandraxFacade(true);
            break;
//            throw new UnsupportedOperationException(
//                    "Reasoning with Mandarax is not supported yet!");
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
        
        Ontology normalizedOntology;
        
        //TODO Missing tranformation: convert anonymous IDs to IRIs 
        
        //Convert conceptual syntax to logical expressions
        OntologyNormalizer normalizer = new AxiomatizationNormalizer();
        normalizedOntology = normalizer.normalize(o);
        
        //Simplify axioms
        normalizer = new ConstructReductionNormalizer();
        normalizedOntology = normalizer.normalize(normalizedOntology);
        
        //Apply Lloyd-Topor rules to get Datalog-compatible LEs
        normalizer = new LloydToporNormalizer();
        normalizedOntology = normalizer.normalize(normalizedOntology);
        
        Program p = new Program();
        WSML2DatalogTransformer wsml2datalog = new WSML2DatalogTransformer();
        Set<org.omwg.logexpression.LogicalExpression> lExprs = new LinkedHashSet<org.omwg.logexpression.LogicalExpression>();
        for (Object a : normalizedOntology.listAxioms()) {
            lExprs.addAll(((Axiom) a).listDefinitions());
        }
        p = wsml2datalog.transform(lExprs);

        return p;
    }

}
