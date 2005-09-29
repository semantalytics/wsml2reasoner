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

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.omwg.logexpression.Atom;
import org.omwg.logexpression.CompoundExpression;
import org.omwg.logexpression.LogicalExpression;
import org.omwg.logexpression.LogicalExpressionFactory;
import org.omwg.logexpression.terms.Term;
import org.omwg.logexpression.terms.Variable;
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
import org.wsml.reasoner.datalog.ConjunctiveQuery;
import org.wsml.reasoner.datalog.DatalogException;
import org.wsml.reasoner.datalog.Literal;
import org.wsml.reasoner.datalog.Program;
import org.wsml.reasoner.datalog.QueryResult;
import org.wsml.reasoner.datalog.Rule;
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
import org.wsml.reasoner.transformation.le.LogicalExpressionNormalizer;
import org.wsml.reasoner.transformation.le.MoleculeDecompositionRules;
import org.wsml.reasoner.transformation.le.OnePassReplacementNormalizer;
import org.wsmo.common.IRI;
import org.wsmo.factory.Factory;
import org.wsmo.factory.WsmoFactory;

/**
 * A prototypical implementation of a reasoner for WSML Core and WSML Flight.
 * 
 * At present the implementation only supports the following reasoning tasks: -
 * Query answering Ontology registration
 * 
 * @author Uwe Keller, DERI Innsbruck
 * @author Gabor Nagypal, FZI
 */
public class DatalogBasedWSMLReasoner implements WSMLFlightReasoner, WSMLCoreReasoner
{
    protected final static String WSML_RESULT_PREDICATE = "wsml:query_result";
    
    protected DatalogReasonerFacade builtInFacade = null;
    protected WsmoFactory wsmoFactory;
    protected LogicalExpressionFactory leFactory;

    public DatalogBasedWSMLReasoner(WSMLReasonerFactory.BuiltInReasoner builtInType)
    {
        switch(builtInType)
        {
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
            throw new UnsupportedOperationException("Reasoning with " + builtInType.toString() + " is not supported yet!");
        }
        wsmoFactory = Factory.createWsmoFactory(null);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(Factory.PROVIDER_CLASS, "org.deri.wsmo4j.logexpression.LogicalExpressionFactoryImpl");
        leFactory = (LogicalExpressionFactory)Factory.createLogicalExpressionFactory(params);
    }

    public Result execute(Request req) throws UnsupportedOperationException, IllegalArgumentException
    {

        if(req instanceof QueryAnsweringRequest)
        {

            QueryAnsweringReasoner qaReasoner = new QueryAnsweringReasoner(builtInFacade);
            try
            {
                return qaReasoner.execute((QueryAnsweringRequest)req);
            } catch(ExternalToolException e)
            {
                e.printStackTrace();
                throw new IllegalArgumentException("Given query could not be deal with by the external tool!", e);
            }

        }
        else if(req instanceof OntologyRegistrationRequest)
        {
            OntologyRegistrationRequest registrationReq = (OntologyRegistrationRequest)req;

            // TODO Do some extra checking to make sure that ontologies which
            // are imported are converted before ontologies which import them
            for(Ontology o : registrationReq.getOntologies())
            {
                // convert the ontology to Datalog Program
                String ontologyUri = o.getIdentifier().toString();
                Program kb = new Program();
                kb.addAll(convertOntology(o));
                // Then register the program at the built-in reasoner
                try
                {
                    builtInFacade.register(ontologyUri, kb);
                } catch(ExternalToolException e)
                {
                    e.printStackTrace();
                    throw new IllegalArgumentException("This set of ontologies could not have been registered at the built-in reasoner", e);
                }
            }
            return new VoidResult(registrationReq);

        }
        else
        {
            // Everything else is currently not supported!
            throw new UnsupportedOperationException("Requested reasoning [" + req.getClass().toString() + "] task is currently not supported by class " + getClass().toString());
        }

    }

    protected Program convertOntology(Ontology o)
    {

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
        for(Object a : normalizedOntology.listAxioms())
        {
            lExprs.addAll(((Axiom)a).listDefinitions());
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
        Set<IRI> ontologySingletonSet = new HashSet<IRI>();
        ontologySingletonSet.add(ontologyID);
        deRegisterOntology(ontologySingletonSet);
    }

    public void deRegisterOntology(Set<IRI> ontologyIDs)
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
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
        return executeQuery(ontologyID, query).size() != 0;
    }

    public Set<Map<Variable, Object>> executeQuery(IRI ontologyID, LogicalExpression query)
    {
        //execute query:
        Set<VariableBinding> bindings;
        try
        {
            bindings = executeQueryI(ontologyID, query);
        } catch(DatalogException e)
        {
            throw new WSMLReasonerException();
        } catch(ExternalToolException e)
        {
            throw new WSMLReasonerException();
        }
        
        //wrap result:
        Set<Map<Variable, Object>> result = new HashSet<Map<Variable, Object>>();
        for(VariableBinding binding : bindings)
        {
            Map<Variable, Object> newBinding = new HashMap<Variable, Object>(); 
            for(String varString : binding.keySet())
            {
                if (varString.charAt(0)=='?')
                    varString = varString.substring(1);
                Variable variable = leFactory.createVariable(varString);
                Object value = binding.get(varString);
                newBinding.put(variable, value);
            }
            result.add(newBinding);
        }

        return result;
    }

    public Set<Concept> getConcepts(IRI ontologyID, Instance instance)
    {
        // build query:
        Term instanceID = leFactory.createIRI(instance.getIdentifier().toString());
        Term conceptVariable = leFactory.createVariable("x");
        Set<Term> conceptSet = new HashSet<Term>();
        conceptSet.add(conceptVariable);
        LogicalExpression query = leFactory.createMolecule(instanceID, null, conceptSet, null);

        // submit query to reasoner:
        Set<VariableBinding> bindings;
        try
        {
            bindings = executeQueryI(ontologyID, query);
        } catch(DatalogException e)
        {
            throw new WSMLReasonerException();
        } catch(ExternalToolException e)
        {
            throw new WSMLReasonerException();
        }

        // extract concepts from result:
        Set<Concept> concepts = new HashSet<Concept>();
        for(VariableBinding binding : bindings)
        {
            String conceptIDString = binding.get("x");
            concepts.add(wsmoFactory.getConcept(wsmoFactory.createIRI(conceptIDString)));
        }
        
        return concepts;        
    }

    public Set<Instance> getInstances(IRI ontologyID, Concept concept)
    {
        // build query:
        Term conceptID = leFactory.createIRI(concept.getIdentifier().toString());
        Term instanceVariable = leFactory.createVariable("x");
        Set<Term> conceptSet = new HashSet<Term>();
        conceptSet.add(conceptID);
        LogicalExpression query = leFactory.createMolecule(instanceVariable, null, conceptSet, null);

        // submit query to reasoner:
        Set<VariableBinding> bindings;
        try
        {
            bindings = executeQueryI(ontologyID, query);
        } catch(DatalogException e)
        {
            throw new WSMLReasonerException();
        } catch(ExternalToolException e)
        {
            throw new WSMLReasonerException();
        }

        // extract concepts from result:
        Set<Instance> instances = new HashSet<Instance>();
        for(VariableBinding binding : bindings)
        {
            String instanceIDString = binding.get("x");
            instances.add(wsmoFactory.getInstance(wsmoFactory.createIRI(instanceIDString)));
        }
        
        return instances;        
    }

    public Set<Concept> getSubConcepts(IRI ontologyID, Concept concept)
    {
        // build query:
        Term conceptID = leFactory.createIRI(concept.getIdentifier().toString());
        Term conceptVariable = leFactory.createVariable("x");
        Set<Term> conceptSet = new HashSet<Term>();
        conceptSet.add(conceptID);
        LogicalExpression query = leFactory.createMolecule(conceptVariable, conceptSet, null, null);

        // submit query to reasoner:
        Set<VariableBinding> bindings;
        try
        {
            bindings = executeQueryI(ontologyID, query);
        } catch(DatalogException e)
        {
            throw new WSMLReasonerException();
        } catch(ExternalToolException e)
        {
            throw new WSMLReasonerException();
        }

        // extract concepts from result:
        Set<Concept> concepts = new HashSet<Concept>();
        for(VariableBinding binding : bindings)
        {
            String conceptIDString = binding.get("x");
            concepts.add(wsmoFactory.getConcept(wsmoFactory.createIRI(conceptIDString)));
        }
        
        return concepts;        
    }

    public Set<Concept> getSuperConcepts(IRI ontologyID, Concept concept)
    {
        // build query:
        Term conceptID = leFactory.createIRI(concept.getIdentifier().toString());
        Term conceptVariable = leFactory.createVariable("x");
        Set<Term> conceptSet = new HashSet<Term>();
        conceptSet.add(conceptVariable);
        LogicalExpression query = leFactory.createMolecule(conceptID, conceptSet, null, null);

        // submit query to reasoner:
        Set<VariableBinding> bindings;
        try
        {
            bindings = executeQueryI(ontologyID, query);
        } catch(DatalogException e)
        {
            throw new WSMLReasonerException();
        } catch(ExternalToolException e)
        {
            throw new WSMLReasonerException();
        }

        // extract concepts from result:
        Set<Concept> concepts = new HashSet<Concept>();
        for(VariableBinding binding : bindings)
        {
            String conceptIDString = binding.get("x");
            concepts.add(wsmoFactory.getConcept(wsmoFactory.createIRI(conceptIDString)));
        }
        
        return concepts;        
    }

    public boolean isMemberOf(IRI ontologyID, Instance instance, Concept concept)
    {
        // build query:
        Term conceptID = leFactory.createIRI(concept.getIdentifier().toString());
        Term instanceID = leFactory.createIRI(instance.getIdentifier().toString());
        Set<Term> conceptSet = new HashSet<Term>();
        conceptSet.add(conceptID);
        LogicalExpression query = leFactory.createMolecule(instanceID, null, conceptSet, null);

        // submit query to reasoner:
        Set<VariableBinding> bindings;
        try
        {
            bindings = executeQueryI(ontologyID, query);
        } catch(DatalogException e)
        {
            throw new WSMLReasonerException();
        } catch(ExternalToolException e)
        {
            throw new WSMLReasonerException();
        }

        // check for non-empty result:
        return bindings.size() != 0;
    }

    public boolean isSubConceptOf(IRI ontologyID, Concept subConcept, Concept superConcept)
    {
        // build query:
        Term superconceptID = leFactory.createIRI(superConcept.getIdentifier().toString());
        Term subconceptID = leFactory.createIRI(subConcept.getIdentifier().toString());
        Set<Term> superConceptSet = new HashSet<Term>();
        superConceptSet.add(superconceptID);
        LogicalExpression query = leFactory.createMolecule(subconceptID, superConceptSet, null, null);

        // submit query to reasoner:
        Set<VariableBinding> bindings;
        try
        {
            bindings = executeQueryI(ontologyID, query);
        } catch(DatalogException e)
        {
            throw new WSMLReasonerException();
        } catch(ExternalToolException e)
        {
            throw new WSMLReasonerException();
        }

        // check for non-empty result:
        return bindings.size() != 0;
    }

    public void registerOntology(Ontology ontology)
    {
        Set<Ontology> ontologySingletonSet = new HashSet<Ontology>();
        ontologySingletonSet.add(ontology);
        registerOntology(ontologySingletonSet);
    }

    public void registerOntology(Set<Ontology> ontologies)
    {
        // TODO Do some extra checking to make sure that ontologies which
        // are imported are converted before ontologies which import them
        for(Ontology o : ontologies)
        {
            // convert the ontology to Datalog Program:
            String ontologyUri = o.getIdentifier().toString();
            Program kb = new Program();
            kb.addAll(convertOntology(o));
            
            // Register the program at the built-in reasoner:
            try
            {
                builtInFacade.register(ontologyUri, kb);
            } catch(ExternalToolException e)
            {
                e.printStackTrace();
                throw new IllegalArgumentException("This set of ontologies could not have been registered at the built-in reasoner", e);
            }
        }
    }
    
    public boolean entails(IRI baseOntologyID, IRI consequenceOntologyID)
    {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException();
    }

    protected Set<VariableBinding> executeQueryI(IRI ontologyID, LogicalExpression query) throws DatalogException, ExternalToolException
    {
        ConjunctiveQuery datalogQuery = convertQuery(query);
        QueryResult datalogResult = builtInFacade.evaluate(datalogQuery, ontologyID.toString());

        // TODO: No incremental fetching of results is supported here at present
        return datalogResult.getVariableBindings();
    }

    protected ConjunctiveQuery convertQuery(org.omwg.logexpression.LogicalExpression q)
    {
        WSML2DatalogTransformer wsml2datalog = new WSML2DatalogTransformer();

        Map<String, String> leProperties = new HashMap<String, String>();
        leProperties.put(Factory.PROVIDER_CLASS, "org.deri.wsmo4j.logexpression.LogicalExpressionFactoryImpl");

        org.omwg.logexpression.LogicalExpressionFactory leFactory = (org.omwg.logexpression.LogicalExpressionFactory)Factory.createLogicalExpressionFactory(leProperties);

        List<org.omwg.logexpression.terms.Variable> params = new LinkedList<org.omwg.logexpression.terms.Variable>();
        LogicalExpressionVariableVisitor varVisitor = new LogicalExpressionVariableVisitor();
        q.accept(varVisitor);
        params.addAll(varVisitor.getFreeVariables(q));
        Atom rHead = leFactory.createAtom(leFactory.createIRI(WSML_RESULT_PREDICATE), params);

        LogicalExpressionNormalizer moleculeNormalizer = new OnePassReplacementNormalizer(MoleculeDecompositionRules.instantiate());
        // System.out.println("Q before molecule normalization: " + q);
        q = moleculeNormalizer.normalize(q);
        // System.out.println("Q after molecule normalization: " + q);
        org.omwg.logexpression.LogicalExpression resultDefRule = leFactory.createBinary(CompoundExpression.IMPLIEDBY, rHead, q);

        Program p = wsml2datalog.transform(resultDefRule);
        // System.out.println("Query as program:" + p);
        if(p.size() != 1)
            throw new IllegalArgumentException("Could not transform query " + q);
        Rule rule = p.get(0);
        if(!rule.getHead().getSymbol().getSymbolName().equals(WSML_RESULT_PREDICATE))
            throw new IllegalArgumentException("Could not transform query " + q);

        List<Literal> body = new LinkedList<Literal>();

        for(Literal l : rule.getBody())
        {
            body.add(l);
        }

        return new ConjunctiveQuery(body);
    }
}
