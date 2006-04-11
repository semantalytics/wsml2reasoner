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

import java.util.*;

import org.omwg.logicalexpression.Atom;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.*;
import org.wsml.reasoner.ConjunctiveQuery;
import org.wsml.reasoner.DatalogException;
import org.wsml.reasoner.ExternalToolException;
import org.wsml.reasoner.Literal;
import org.wsml.reasoner.Rule;
import org.wsml.reasoner.api.InternalReasonerException;
import org.wsml.reasoner.api.WSMLCoreReasoner;
import org.wsml.reasoner.api.WSMLFlightReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.api.inconsistency.AttributeTypeViolation;
import org.wsml.reasoner.api.inconsistency.ConsistencyViolation;
import org.wsml.reasoner.api.inconsistency.InconsistencyException;
import org.wsml.reasoner.api.inconsistency.MaxCardinalityViolation;
import org.wsml.reasoner.api.inconsistency.MinCardinalityViolation;
import org.wsml.reasoner.api.inconsistency.NamedUserConstraintViolation;
import org.wsml.reasoner.api.inconsistency.UserConstraintViolation;
import org.wsml.reasoner.builtin.kaon2.Kaon2Facade;
import org.wsml.reasoner.builtin.mins.MinsFacade;
import org.wsml.reasoner.transformation.AxiomatizationNormalizer;
import org.wsml.reasoner.transformation.ConstraintReplacementNormalizer;
import org.wsml.reasoner.transformation.ConstructReductionNormalizer;
import org.wsml.reasoner.transformation.LloydToporNormalizer;
import org.wsml.reasoner.transformation.OntologyNormalizer;
import org.wsml.reasoner.transformation.le.LloydToporRules;
import org.wsml.reasoner.transformation.le.LogicalExpressionNormalizer;
import org.wsml.reasoner.transformation.le.LogicalExpressionTransformer;
import org.wsml.reasoner.transformation.le.MoleculeDecompositionRules;
import org.wsml.reasoner.transformation.le.OnePassReplacementNormalizer;
import org.wsml.reasoner.transformation.le.TopDownLESplitter;
import org.wsml.reasoner.transformation.le.TransformationRule;
import org.wsmo.common.IRI;
import org.wsmo.common.exception.InvalidModelException;
import org.wsmo.factory.LogicalExpressionFactory;
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
public class DatalogBasedWSMLReasoner implements WSMLFlightReasoner,
        WSMLCoreReasoner {
    protected final static String WSML_RESULT_PREDICATE = "http://www.wsmo.org/reasoner/"
            + "wsml_query_result";

    protected org.wsml.reasoner.DatalogReasonerFacade builtInFacade = null;

    protected WsmoFactory wsmoFactory;

    protected LogicalExpressionFactory leFactory;

    protected WSMO4JManager wsmoManager;
    
//    private boolean disableConsitencyCheck=false;

    public DatalogBasedWSMLReasoner(
            WSMLReasonerFactory.BuiltInReasoner builtInType,
            WSMO4JManager wsmoManager) {
        this.wsmoManager = wsmoManager;
        switch (builtInType) {
        case KAON2:
            builtInFacade = new Kaon2Facade(wsmoManager);
            break;
        case MINS:
            builtInFacade = new MinsFacade(wsmoManager);
            break;
        default:
            throw new UnsupportedOperationException("Reasoning with "
                    + builtInType.toString() + " is not supported yet!");
        }
        wsmoFactory = this.wsmoManager.getWSMOFactory();
        leFactory = this.wsmoManager.getLogicalExpressionFactory();
    }
    
//    public void setDisableConsitencyCheck(boolean check){
//        this.disableConsitencyCheck = check;
//    }

    @SuppressWarnings("unchecked")
    protected Set<org.wsml.reasoner.Rule> convertOntology(Ontology o) {

        Ontology normalizedOntology;

        // TODO Check whether ontology import is currently handled

        // Convert conceptual syntax to logical expressions
        OntologyNormalizer normalizer = new AxiomatizationNormalizer(
                wsmoManager);
        normalizedOntology = normalizer.normalize(o);

        // Convert constraints to support debugging
        normalizer = new ConstraintReplacementNormalizer(wsmoManager);
        normalizedOntology = normalizer.normalize(normalizedOntology);

        // Simplify axioms
        normalizer = new ConstructReductionNormalizer(wsmoManager);
        normalizedOntology = normalizer.normalize(normalizedOntology);
        // System.out.println("\n-------\n Ontology after simplification:" +
        // WSMLNormalizationTest.serializeOntology(normalizedOntology));

        // Apply Lloyd-Topor rules to get Datalog-compatible LEs
        normalizer = new LloydToporNormalizer(wsmoManager);
        normalizedOntology = normalizer.normalize(normalizedOntology);

        // System.out.println("\n-------\n Ontology after Lloyd-Topor:" +
        // WSMLNormalizationTest.serializeOntology(normalizedOntology));
        Set<org.wsml.reasoner.Rule> p;
        org.wsml.reasoner.WSML2DatalogTransformer wsml2datalog = new org.wsml.reasoner.WSML2DatalogTransformer(
                wsmoManager);
        Set<org.omwg.logicalexpression.LogicalExpression> lExprs = new LinkedHashSet<org.omwg.logicalexpression.LogicalExpression>();
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

    public boolean isSatisfiable(IRI ontologyID) {
        IRI violationIRI = wsmoFactory
                .createIRI(ConstraintReplacementNormalizer.VIOLATION_IRI);
        Atom violation = leFactory.createAtom(violationIRI,
                Collections.EMPTY_LIST);
        boolean result = executeGroundQuery(ontologyID, violation) ? false
                : true;
        return result;
    }

    public void deRegisterOntology(IRI ontologyID) {
        Set<IRI> ontologySingletonSet = new HashSet<IRI>();
        ontologySingletonSet.add(ontologyID);
        deRegisterOntology(ontologySingletonSet);
    }

    public void deRegisterOntology(Set<IRI> ontologyIDs) {
        // TODO Later we need a method which accepts a set of ontology IDs on
        // the DatalogFacade
        for (IRI id : ontologyIDs) {
            try {
                builtInFacade.deregister(id.toString());
            } catch (org.wsml.reasoner.ExternalToolException e) {
                e.printStackTrace();
                throw new IllegalArgumentException(
                        "This set of ontologies could not have been deregistered at the built-in reasoner",
                        e);
            }

        }
    }

    public boolean entails(IRI ontologyID, LogicalExpression expression) {
        return executeGroundQuery(ontologyID, expression);
    }

    public boolean entails(IRI ontologyID, Set<LogicalExpression> expressions) {
        for (LogicalExpression e : expressions) {
            if (!executeGroundQuery(ontologyID, e))
                return false;
        }
        return true;
    }

    public boolean executeGroundQuery(IRI ontologyID, LogicalExpression query) {
        return executeQuery(ontologyID, query).size() != 0;
    }

    public Set<Map<Variable, Term>> executeQuery(IRI ontologyID,
            LogicalExpression query) {
        // execute query:
        Set<Map<Variable, Term>> bindings = null;
        try {
            bindings = internalExecuteQuery(ontologyID, query);
        } catch (DatalogException e) {
            throw new InternalReasonerException(e);
        } catch (ExternalToolException e) {
            throw new InternalReasonerException(e);
        }

        return bindings;
    }

    public Set<Concept> getConcepts(IRI ontologyID, Instance instance) {
        // build query:
        Term instanceID = wsmoFactory.createIRI(instance.getIdentifier()
                .toString());
        Term conceptVariable = wsmoFactory.createVariable("x");
        LogicalExpression query = leFactory.createMemberShipMolecule(
                instanceID, conceptVariable);

        // submit query to reasoner:
        Set<Map<Variable, Term>> bindings;
        try {
            bindings = internalExecuteQuery(ontologyID, query);
        } catch (DatalogException e) {
            throw new InternalReasonerException();
        } catch (ExternalToolException e) {
            throw new InternalReasonerException();
        }

        // extract concepts from result:
        Set<Concept> concepts = new HashSet<Concept>();
        for (Map<Variable, Term> binding : bindings) {
            IRI conceptID = (IRI) binding.get(wsmoFactory.createVariable("x"));
            concepts.add(wsmoFactory.getConcept(conceptID));
        }
        return concepts;
    }

    public Set<Instance> getInstances(IRI ontologyID, Concept concept) {
        // build query:
        Term conceptID = wsmoFactory.createIRI(concept.getIdentifier()
                .toString());
        Term instanceVariable = wsmoFactory.createVariable("x");
        LogicalExpression query = leFactory.createMemberShipMolecule(
                instanceVariable, conceptID);

        // submit query to reasoner:
        Set<Map<Variable, Term>> bindings;
        try {
            bindings = internalExecuteQuery(ontologyID, query);
        } catch (DatalogException e) {
            throw new InternalReasonerException();
        } catch (ExternalToolException e) {
            throw new InternalReasonerException();
        }

        // extract concepts from result:
        Set<Instance> instances = new HashSet<Instance>();
        for (Map<Variable, Term> binding : bindings) {
            IRI instanceID = (IRI) binding.get(wsmoFactory.createVariable("x"));
            instances.add(wsmoFactory.getInstance(instanceID));
        }

        return instances;
    }

    public Set<Concept> getSubConcepts(IRI ontologyID, Concept concept) {
        // build query:
        Term conceptID = wsmoFactory.createIRI(concept.getIdentifier()
                .toString());
        Term conceptVariable = wsmoFactory.createVariable("x");
        LogicalExpression query = leFactory.createSubConceptMolecule(
                conceptVariable, conceptID);

        // submit query to reasoner:
        Set<Map<Variable, Term>> bindings;
        try {
            bindings = internalExecuteQuery(ontologyID, query);
        } catch (DatalogException e) {
            throw new InternalReasonerException();
        } catch (ExternalToolException e) {
            throw new InternalReasonerException();
        }

        // extract concepts from result:
        Set<Concept> concepts = new HashSet<Concept>();
        for (Map<Variable, Term> binding : bindings) {
            IRI subConceptID = (IRI) binding.get(wsmoFactory
                    .createVariable("x"));
            concepts.add(wsmoFactory.getConcept(subConceptID));
        }
        return concepts;
    }

    public Set<Concept> getSuperConcepts(IRI ontologyID, Concept concept) {
        // build query:
        Term conceptID = wsmoFactory.createIRI(concept.getIdentifier()
                .toString());
        Term conceptVariable = wsmoFactory.createVariable("x");

        LogicalExpression query = leFactory.createSubConceptMolecule(conceptID,
                conceptVariable);

        // submit query to reasoner:
        Set<Map<Variable, Term>> bindings;
        try {
            bindings = internalExecuteQuery(ontologyID, query);
        } catch (DatalogException e) {
            throw new InternalReasonerException();
        } catch (ExternalToolException e) {
            throw new InternalReasonerException();
        }

        // extract concepts from result:
        Set<Concept> concepts = new HashSet<Concept>();
        for (Map<Variable, Term> binding : bindings) {
            IRI superConceptID = (IRI) binding.get(wsmoFactory
                    .createVariable("x"));
            concepts.add(wsmoFactory.getConcept(superConceptID));
        }
        return concepts;
    }

    public boolean isMemberOf(IRI ontologyID, Instance instance, Concept concept) {
        // build query:
        Term conceptID = wsmoFactory.createIRI(concept.getIdentifier()
                .toString());
        Term instanceID = wsmoFactory.createIRI(instance.getIdentifier()
                .toString());
        LogicalExpression query = leFactory.createMemberShipMolecule(
                instanceID, conceptID);

        // submit query to reasoner:
        Set<Map<Variable, Term>> bindings;
        try {
            bindings = internalExecuteQuery(ontologyID, query);
        } catch (DatalogException e) {
            throw new InternalReasonerException();
        } catch (ExternalToolException e) {
            throw new InternalReasonerException();
        }

        // check for non-empty result:
        return bindings.size() != 0;
    }

    public boolean isSubConceptOf(IRI ontologyID, Concept subConcept,
            Concept superConcept) {
        // build query:
        Term superconceptID = wsmoFactory.createIRI(superConcept
                .getIdentifier().toString());
        Term subconceptID = wsmoFactory.createIRI(subConcept.getIdentifier()
                .toString());
        LogicalExpression query = leFactory.createSubConceptMolecule(
                subconceptID, superconceptID);

        // / submit query to reasoner:
        Set<Map<Variable, Term>> bindings;
        try {
            bindings = internalExecuteQuery(ontologyID, query);
        } catch (DatalogException e) {
            throw new InternalReasonerException();
        } catch (ExternalToolException e) {
            throw new InternalReasonerException();
        }

        // check for non-empty result:
        return bindings.size() != 0;
    }

    public void registerOntology(Ontology ontology) throws InconsistencyException {
        Set<Ontology> ontologySingletonSet = new HashSet<Ontology>();
        ontologySingletonSet.add(ontology);
        registerOntologies(ontologySingletonSet);
    }

    public void registerOntologies(Set<Ontology> ontologies) throws InconsistencyException {
        registerOntologiesNoVerification(ontologies);
        // check satisfiability
        Set<ConsistencyViolation> errors = new HashSet<ConsistencyViolation>();
        for (Ontology o : ontologies) {
            IRI ontologyId = (IRI) o.getIdentifier();
//            if (!disableConsitencyCheck){
//                errors.addAll(checkConsistency(ontologyId));
//            }
        }
        if (errors.size() > 0) {
            Set<IRI> ids = new HashSet<IRI>();
            for (Ontology o : ontologies) {
                ids.add((IRI) o.getIdentifier());
            }
            deRegisterOntology(ids);
            throw new InconsistencyException(errors);
        }
    }

    private void addAttributeOfTypeViolations(Set<ConsistencyViolation> errors,
            IRI ontologyId) throws InvalidModelException {
        // ATTR_OFTYPE(instance,value,concept, attribute,violated_type)

        Variable i = wsmoFactory.createVariable("i");
        Variable v = wsmoFactory.createVariable("v");
        Variable c = wsmoFactory.createVariable("c");
        Variable a = wsmoFactory.createVariable("a");
        Variable t = wsmoFactory.createVariable("t");

        IRI atomId = wsmoFactory
                .createIRI(ConstraintReplacementNormalizer.ATTR_OFTYPE_IRI);

        List<Term> params = new ArrayList<Term>(5);
        params.add(i);
        params.add(v);
        params.add(c);
        params.add(a);
        params.add(t);
        Atom atom = leFactory.createAtom(atomId, params);

        Set<Map<Variable, Term>> violations = executeQuery(ontologyId, atom);
        for (Map<Variable, Term> violation : violations) {
            // Construct error object
            Instance instance = wsmoFactory.getInstance((IRI) violation.get(i));
            Term rawValue = violation.get(v);
            Value value;
            if (rawValue instanceof DataValue)
                value = (DataValue) rawValue;
            else
                value = wsmoFactory.createInstance((IRI) rawValue);
            Concept concept = wsmoFactory.getConcept((IRI) violation.get(c));
            Attribute attribute = (Attribute) concept.findAttributes(
                    (IRI) violation.get(a)).iterator().next();
            Type type;
            IRI typeId = (IRI) violation.get(t);
            if (WsmlDataType.WSML_STRING.equals(typeId.toString())
                    || WsmlDataType.WSML_INTEGER.equals(typeId.toString())
                    || WsmlDataType.WSML_DECIMAL.equals(typeId.toString())
                    || WsmlDataType.WSML_BOOLEAN.equals(typeId.toString()))
                type = wsmoManager.getDataFactory().createWsmlDataType(typeId);
            else
                type = wsmoFactory.getConcept(typeId);

            errors.add(new AttributeTypeViolation(ontologyId, instance, value,
                    attribute, type));
        }

    }

    private void addMinCardinalityViolations(Set<ConsistencyViolation> errors,
            IRI ontologyId) throws InvalidModelException {
        // MIN_CARD(instance, concept, attribute)

        Variable i = wsmoFactory.createVariable("i");
        Variable c = wsmoFactory.createVariable("c");
        Variable a = wsmoFactory.createVariable("a");

        IRI atomId = wsmoFactory
                .createIRI(ConstraintReplacementNormalizer.MIN_CARD_IRI);

        List<Term> params = new ArrayList<Term>(3);
        params.add(i);
        params.add(c);
        params.add(a);
        Atom atom = leFactory.createAtom(atomId, params);

        Set<Map<Variable, Term>> violations = executeQuery(ontologyId, atom);
        for (Map<Variable, Term> violation : violations) {
            // Construct error object
            Instance instance = wsmoFactory.getInstance((IRI) violation.get(i));
            Concept concept = wsmoFactory.getConcept((IRI) violation.get(c));
            Attribute attribute = (Attribute) concept.findAttributes(
                    (IRI) violation.get(a)).iterator().next();
            errors.add(new MinCardinalityViolation(ontologyId, instance,
                    attribute));
        }
    }

    private void addMaxCardinalityViolations(Set<ConsistencyViolation> errors,
            IRI ontologyId) throws InvalidModelException {
        // MAX_CARD(instance, concept, attribute)

        Variable i = wsmoFactory.createVariable("i");
        Variable c = wsmoFactory.createVariable("c");
        Variable a = wsmoFactory.createVariable("a");

        IRI atomId = wsmoFactory
                .createIRI(ConstraintReplacementNormalizer.MAX_CARD_IRI);

        List<Term> params = new ArrayList<Term>(3);
        params.add(i);
        params.add(c);
        params.add(a);
        Atom atom = leFactory.createAtom(atomId, params);

        Set<Map<Variable, Term>> violations = executeQuery(ontologyId, atom);
        for (Map<Variable, Term> violation : violations) {
            // Construct error object
            Instance instance = wsmoFactory.getInstance((IRI) violation.get(i));
            Concept concept = wsmoFactory.getConcept((IRI) violation.get(c));
            Attribute attribute = (Attribute) concept.findAttributes(
                    (IRI) violation.get(a)).iterator().next();
            errors.add(new MaxCardinalityViolation(ontologyId, instance,
                    attribute));
        }
    }

    private void addNamedUserViolations(Set<ConsistencyViolation> errors,
            IRI ontologyId) throws InvalidModelException {
        // NAMED_USER(axiom)

        Variable i = wsmoFactory.createVariable("i");

        IRI atomId = wsmoFactory
                .createIRI(ConstraintReplacementNormalizer.NAMED_USER_IRI);

        List<Term> params = new ArrayList<Term>(1);
        params.add(i);
        Atom atom = leFactory.createAtom(atomId, params);

        Set<Map<Variable, Term>> violations = executeQuery(ontologyId, atom);
        for (Map<Variable, Term> violation : violations) {
            Axiom axiom = null;
            //dirty but should work:
            String id = violation.get(i).toString();
            int idx_ = id.lastIndexOf('_');
            if (idx_ > 0){
                boolean isNumberedID = true;
                for (int n=idx_; n < id.length(); n++){
                    if (Character.isDigit(id.charAt(n))){
                        isNumberedID=false;
                    }
                }
                IRI iri = wsmoFactory.createIRI(id.substring(0,idx_));
                Axiom tmp =wsmoFactory.getAxiom(iri);
                int no = Integer.parseInt(id.substring(idx_+1));
                Iterator iter = tmp.listDefinitions().iterator();
                LogicalExpression le = null;
                for (int n=0;n<no;n++){
                    le = (LogicalExpression)iter.next();
                }
                axiom = wsmoFactory.createAxiom((IRI)violation.get(i));
                axiom.addDefinition(le);
            }
            // Construct error object
            if (axiom==null){
                axiom = wsmoFactory.getAxiom((IRI) violation.get(i));
            }
            errors.add(new NamedUserConstraintViolation(ontologyId, axiom));
        }
    }

    private void addUnNamedUserViolations(Set<ConsistencyViolation> errors,
            IRI ontologyId) throws InvalidModelException {
        // UNNAMED_USER(axiom)

        Variable i = wsmoFactory.createVariable("i");

        IRI atomId = wsmoFactory
                .createIRI(ConstraintReplacementNormalizer.UNNAMED_USER_IRI);

        List<Term> params = new ArrayList<Term>(1);
        params.add(i);
        Atom atom = leFactory.createAtom(atomId, params);

        Set<Map<Variable, Term>> violations = executeQuery(ontologyId, atom);
        for (int k = 0; k < violations.size(); k++) {
            // Construct error object
            errors.add(new UserConstraintViolation(ontologyId));
        }
    }

    public boolean entails(IRI baseOntologyID, IRI consequenceOntologyID) {
        throw new UnsupportedOperationException("Method not implemented yet!");
    }

    protected Set<Map<Variable, Term>> internalExecuteQuery(IRI ontologyID,
            LogicalExpression query) throws DatalogException,
            org.wsml.reasoner.ExternalToolException {
        Set<org.wsml.reasoner.ConjunctiveQuery> datalogQueries = convertQuery(query);
        Set<Map<Variable, Term>> result = new HashSet<Map<Variable, Term>>();
        for (ConjunctiveQuery datalogQuery : datalogQueries) {
            result.addAll(builtInFacade.evaluate(datalogQuery, ontologyID
                    .toString()));
        }
        return result;
    }

    protected Set<org.wsml.reasoner.ConjunctiveQuery> convertQuery(
            org.omwg.logicalexpression.LogicalExpression q) {
        org.wsml.reasoner.WSML2DatalogTransformer wsml2datalog = new org.wsml.reasoner.WSML2DatalogTransformer(
                wsmoManager);

        List<Variable> params = new LinkedList<Variable>();
        LogicalExpressionVariableVisitor varVisitor = new LogicalExpressionVariableVisitor();
        q.accept(varVisitor);
        params.addAll(varVisitor.getFreeVariables(q));
        Atom rHead = leFactory.createAtom(wsmoFactory
                .createIRI(WSML_RESULT_PREDICATE), params);
        // System.out.println("Query head:" + rHead);

        LogicalExpressionNormalizer moleculeNormalizer = new OnePassReplacementNormalizer(
                new MoleculeDecompositionRules(wsmoManager), wsmoManager);
        // System.out.println("Q before molecule normalization: " + q);
        q = moleculeNormalizer.normalize(q);
        // System.out.println("Q after molecule normalization: " + q);

        org.omwg.logicalexpression.LogicalExpression resultDefRule = leFactory
                .createInverseImplication(rHead, q);

        List<TransformationRule> lloydToporRules = (List<TransformationRule>) new LloydToporRules(
                wsmoManager);
        LogicalExpressionTransformer lloydToporNormalizer = new TopDownLESplitter(
                lloydToporRules);
        Set<LogicalExpression> conjunctiveQueries = lloydToporNormalizer
                .transform(resultDefRule);

        Set<Rule> p = new HashSet<Rule>();

        for (LogicalExpression query : conjunctiveQueries) {
            p.addAll(wsml2datalog.transform(query));
        }

        // System.out.println("Query as program:" + p);
        // if (p.size() != 1)
        // throw new IllegalArgumentException("Could not transform query " + q);

        Set<ConjunctiveQuery> result = new HashSet<ConjunctiveQuery>();

        for (Rule rule : p) {
            if (!rule.getHead().getPredicateUri().equals(WSML_RESULT_PREDICATE))
                throw new IllegalArgumentException("Could not transform query "
                        + q);

            List<Literal> body = new LinkedList<Literal>();

            for (Literal l : rule.getBody()) {
                body.add(l);
            }
            result.add(new org.wsml.reasoner.ConjunctiveQuery(body));
        }
        return result;
    }

    public Set<ConsistencyViolation> checkConsistency(IRI ontologyId) {
        Set<ConsistencyViolation> errors = new HashSet<ConsistencyViolation>();
        if (!isSatisfiable(ontologyId)) {
            try {
                addAttributeOfTypeViolations(errors, ontologyId);
                addMinCardinalityViolations(errors, ontologyId);
                addMaxCardinalityViolations(errors, ontologyId);
                addNamedUserViolations(errors, ontologyId);
                addUnNamedUserViolations(errors, ontologyId);
            } catch (InvalidModelException e) {
                throw new InternalReasonerException(e);
            }
        }
        return errors;
    }

    public void registerOntologiesNoVerification(Set<Ontology> ontologies) {
        // TODO Do some extra checking to make sure that ontologies which
        // are imported are converted before ontologies which import them
        for (Ontology o : ontologies) {
            // convert the ontology to Datalog Program:
            String ontologyUri = o.getIdentifier().toString();
            Set<org.wsml.reasoner.Rule> kb = new HashSet<org.wsml.reasoner.Rule>();
            kb.addAll(convertOntology(o));

            // Register the program at the built-in reasoner:
            try {
                builtInFacade.register(ontologyUri, kb);
            } catch (org.wsml.reasoner.ExternalToolException e) {
                e.printStackTrace();
                throw new IllegalArgumentException(
                        "This set of ontologies could not have been registered at the built-in reasoner",
                        e);
            }
        }
    }

    public void registerOntologyNoVerification(Ontology ontology) {
        Set<Ontology> ontologySingletonSet = new HashSet<Ontology>();
        ontologySingletonSet.add(ontology);
        registerOntologiesNoVerification(ontologySingletonSet);
    }
}
