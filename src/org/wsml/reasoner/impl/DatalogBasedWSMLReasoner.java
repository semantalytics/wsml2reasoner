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

package org.wsml.reasoner.impl;

import java.util.LinkedHashSet;
import java.util.Set;

import org.omwg.logexpression.LogicalExpression;
import org.omwg.ontology.Axiom;
import org.omwg.ontology.Concept;
import org.omwg.ontology.Instance;
import org.omwg.ontology.Ontology;
import org.wsml.reasoner.api.OntologyRegistrationRequest;
import org.wsml.reasoner.api.Request;
import org.wsml.reasoner.api.Result;
import org.wsml.reasoner.api.WSMLCoreReasoner;
import org.wsml.reasoner.api.WSMLFlightReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.api.queryanswering.QueryAnsweringRequest;
import org.wsml.reasoner.api.queryanswering.VariableBinding;
import org.wsml.reasoner.datalog.Program;
import org.wsml.reasoner.datalog.wrapper.DatalogReasonerFacade;
import org.wsml.reasoner.datalog.wrapper.ExternalToolException;
import org.wsml.reasoner.datalog.wrapper.dlv.DLVFacade;
import org.wsml.reasoner.datalog.wrapper.kaon2.Kaon2Facade;
import org.wsml.reasoner.datalog.wrapper.mandrax.MandraxFacade;
import org.wsml.reasoner.datalog.wrapper.mins.MinsFacade;
import org.wsml.reasoner.transformation.AxiomatizationNormalizer;
import org.wsml.reasoner.transformation.ConstructReductionNormalizer;
import org.wsml.reasoner.transformation.LloydToporNormalizer;
import org.wsml.reasoner.transformation.OntologyNormalizer;
import org.wsml.reasoner.transformation.WSML2DatalogTransformer;
import org.wsmo.common.IRI;

/**
 * A prototypical implementation of a reasoner for WSML Core and WSML Flight.
 * 
 * At present the implementation only supports the following reasoning tasks: -
 * Query answering Ontology registration
 * 
 * @author Uwe Keller, DERI Innsbruck
 * @author Gabor Nagypal, FZI
 */
public class DatalogBasedWSMLReasoner implements WSMLCoreReasoner, WSMLFlightReasoner {

    private DatalogReasonerFacade builtInFacade = null;

    public DatalogBasedWSMLReasoner(
            WSMLReasonerFactory.BuiltInReasoner builtInType) {
        switch (builtInType) {
        case DLV:
            builtInFacade = new DLVFacade();
            break;
        case MANDARAX:
            builtInFacade = new MandraxFacade(true);
            break;
        case KAON2:
            builtInFacade = new Kaon2Facade();
            break;
        case MINS:
            builtInFacade = new MinsFacade();
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

        // TODO Missing tranformation: convert anonymous IDs to IRIs
        // TODO Check whether ontology import is currently handled

        // Convert conceptual syntax to logical expressions
        OntologyNormalizer normalizer = new AxiomatizationNormalizer();
        normalizedOntology = normalizer.normalize(o);

        // Simplify axioms
        normalizer = new ConstructReductionNormalizer();
        normalizedOntology = normalizer.normalize(normalizedOntology);
        // System.out.println("\n-------\n Ontology after simplification:" +
        // WSMLNormalizationTest.serializeOntology(normalizedOntology));

        // Apply Lloyd-Topor rules to get Datalog-compatible LEs
        normalizer = new LloydToporNormalizer();
        normalizedOntology = normalizer.normalize(normalizedOntology);

        // System.out.println("\n-------\n Ontology after Lloyd-Topor:" +
        // WSMLNormalizationTest.serializeOntology(normalizedOntology));
        Program p = new Program();
        WSML2DatalogTransformer wsml2datalog = new WSML2DatalogTransformer();
        Set<org.omwg.logexpression.LogicalExpression> lExprs = new LinkedHashSet<org.omwg.logexpression.LogicalExpression>();
        for (Object a : normalizedOntology.listAxioms()) {
            lExprs.addAll(((Axiom) a).listDefinitions());
        }
        p = wsml2datalog.transform(lExprs);
        p.addAll(wsml2datalog.generateAuxilliaryRules());
        // System.out.println("datalog program:");
        // System.out.println(p);
        // System.out.println("-*");
        return p;
    }

    public boolean isSatisfiable(IRI ontologyID)
    {
        // TODO Auto-generated method stub
        return false;
    }

    public void deRegisterOntology(IRI ontologyID)
    {
        // TODO Auto-generated method stub
        
    }

    public void deRegisterOntology(Set<IRI> ontologyIDs)
    {
        // TODO Auto-generated method stub
        
    }

    public boolean entails(IRI ontologyID, LogicalExpression expression)
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean entails(IRI ontologyID, Set<LogicalExpression> expressions)
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean executeGroundQuery(IRI ontologyID, LogicalExpression query)
    {
        // TODO Auto-generated method stub
        return false;
    }

    public Set<VariableBinding> executeQuery(IRI ontologyID, LogicalExpression query)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Set<Concept> getConcepts(IRI ontologyID, Instance instance)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Set<Instance> getInstances(IRI ontologyID, Concept concept)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Set<Concept> getSubConcepts(IRI ontologyID, Concept concept)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Set<Concept> getSuperConcepts(IRI ontologyID, Concept concept)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isMemberOf(IRI ontologyID, Instance instance, Concept concept)
    {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isSubConceptOf(IRI ontologyID, Concept subConcept, Concept superConcept)
    {
        // TODO Auto-generated method stub
        return false;
    }

    public void registerOntology(Ontology ontology)
    {
        // TODO Auto-generated method stub
        
    }

    public void registerOntology(Set<Ontology> ontologies)
    {
        // TODO Auto-generated method stub
        
    }

    public boolean entails(IRI baseOntologyID, IRI consequenceOntologyID) {
        // TODO Auto-generated method stub
        return false;
    }

}
