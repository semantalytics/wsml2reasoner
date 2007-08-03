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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.omwg.logicalexpression.Atom;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.terms.ConstructedTerm;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Attribute;
import org.omwg.ontology.Axiom;
import org.omwg.ontology.Concept;
import org.omwg.ontology.Instance;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Type;
import org.omwg.ontology.Variable;
import org.omwg.ontology.WsmlDataType;
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
import org.wsml.reasoner.api.inconsistency.UnNamedUserConstraintViolation;
import org.wsml.reasoner.api.inconsistency.UserConstraintViolation;
import org.wsml.reasoner.builtin.iris.IrisFacade;
import org.wsml.reasoner.builtin.kaon2.Kaon2Facade;
import org.wsml.reasoner.builtin.mins.MinsFacade;
import org.wsml.reasoner.builtin.xsb.XSBFacade;
import org.wsml.reasoner.transformation.AnonymousIdUtils;
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
import org.wsmo.common.Identifier;
import org.wsmo.common.exception.InvalidModelException;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.ParserException;

/**
 * A prototypical implementation of a reasoner for WSML Core and WSML Flight.
 * 
 * At present the implementation only supports the following reasoning tasks: -
 * Query answering Ontology registration
 * 
 * @author Uwe Keller, DERI Innsbruck
 * @author Gabor Nagypal, FZI
 * @author Nathalie Steinmetz, DERI Innsbruck
 */
public class DatalogBasedWSMLReasoner implements WSMLFlightReasoner,
        WSMLCoreReasoner {
    
	protected final static String WSML_RESULT_PREDICATE = "http://www.wsmo.org/reasoner/"
            + "wsml_query_result";

    protected org.wsml.reasoner.DatalogReasonerFacade builtInFacade = null;

    protected WsmoFactory wsmoFactory;

    protected LogicalExpressionFactory leFactory;

    protected WSMO4JManager wsmoManager;
    
    public static int allowImports = 0;
    
    private boolean disableConsitencyCheck = false;

    private long normTime = -1;
    
    private long convTime = -1;
    
    private long consTime = -1;
    
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
        case XSB:
            builtInFacade = new XSBFacade(wsmoManager);
            break;
        case IRIS:
        	builtInFacade = new IrisFacade(wsmoManager);
        	break;
        default:
            throw new UnsupportedOperationException("Reasoning with "
                    + builtInType.toString() + " is not supported yet!");
        }
        wsmoFactory = this.wsmoManager.getWSMOFactory();
        leFactory = this.wsmoManager.getLogicalExpressionFactory();
    }
    
    public void setDisableConsitencyCheck(boolean check){
        this.disableConsitencyCheck = check;
    }
    
    public void setEvalMethod(int method){
        if (builtInFacade instanceof MinsFacade){
            ((MinsFacade)builtInFacade).evaluationMethod=method;
        }
      }
    
	public void setAllowImports(int allowOntoImports){
    		allowImports = allowOntoImports;
    	}

	protected long getNormalizationTime() {
    	return normTime;
    }
    
	protected long getConvertionTime() {
    	return convTime;
    }
    
	protected long getConsistencyCheckTime() {
    	return consTime;
    }
    
    protected Set<org.wsml.reasoner.Rule> convertOntology(Ontology o) {

        Ontology normalizedOntology;

        // TODO Check whether ontology import is currently handled

        long normTime_start = System.currentTimeMillis();
        
        //in order to keep track of cyclic imports
        Set<Ontology> importedOntologies = new HashSet<Ontology>();
        
        // Convert conceptual syntax to logical expressions
        OntologyNormalizer normalizer = new AxiomatizationNormalizer(wsmoManager,importedOntologies);
        normalizedOntology = normalizer.normalize(o);
//      System.out.println("\n-------\n Ontology after Normalization:\n" +
//      WSMLNormalizationTest.serializeOntology(normalizedOntology));


        // Convert constraints to support debugging
        normalizer = new ConstraintReplacementNormalizer(wsmoManager);
        normalizedOntology = normalizer.normalize(normalizedOntology);
//        System.out.println("\n-------\n Ontology after constraints:\n" +
//        WSMLNormalizationTest.serializeOntology(normalizedOntology));

        // Simplify axioms
        normalizer = new ConstructReductionNormalizer(wsmoManager);
        normalizedOntology = normalizer.normalize(normalizedOntology);
//        System.out.println("\n-------\n Ontology after simplification:\n" +
//        WSMLNormalizationTest.serializeOntology(normalizedOntology));

        // Apply Lloyd-Topor rules to get Datalog-compatible LEs
        normalizer = new LloydToporNormalizer(wsmoManager);
        normalizedOntology = normalizer.normalize(normalizedOntology);
//        System.out.println("\n-------\n Ontology after Lloyd-Topor:\n" +
//        WSMLNormalizationTest.serializeOntology(normalizedOntology));
        
        Set<org.wsml.reasoner.Rule> p;
        org.wsml.reasoner.WSML2DatalogTransformer wsml2datalog = new org.wsml.reasoner.WSML2DatalogTransformer(
                wsmoManager);
        Set<org.omwg.logicalexpression.LogicalExpression> lExprs = new LinkedHashSet<org.omwg.logicalexpression.LogicalExpression>();
        for (Object a : normalizedOntology.listAxioms()) {
            lExprs.addAll(((Axiom) a).listDefinitions());
        }
//        System.out.println(lExprs);
        
        p = wsml2datalog.transform(lExprs);
        p.addAll(wsml2datalog.generateAuxilliaryRules());

        long normTime_end = System.currentTimeMillis();
        normTime = normTime_end - normTime_start;
        
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
        // build membership query:
        Term instanceID = wsmoFactory.createIRI(instance.getIdentifier()
                .toString());
        Term conceptVariable = leFactory.createVariable("x");
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
            IRI conceptID = (IRI) binding.get(leFactory.createVariable("x"));
            concepts.add(wsmoFactory.getConcept(conceptID));
        }
        return concepts;
    }

    public Set<Instance> getInstances(IRI ontologyID, Concept concept) {
        // build query:
        Term conceptID = wsmoFactory.createIRI(concept.getIdentifier()
                .toString());
        Term instanceVariable = leFactory.createVariable("x");
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
            IRI instanceID = (IRI) binding.get(leFactory.createVariable("x"));
            instances.add(wsmoFactory.getInstance(instanceID));
        }

        return instances;
    }

    public Set<Concept> getSubConcepts(IRI ontologyID, Concept concept) {
        // build query:
        Term conceptID = wsmoFactory.createIRI(concept.getIdentifier()
                .toString());
        Term conceptVariable = leFactory.createVariable("x");
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
            IRI subConceptID = (IRI) binding.get(leFactory
                    .createVariable("x"));
            concepts.add(wsmoFactory.getConcept(subConceptID));
        }
        if (concepts.contains(concept)) {
        	concepts.remove(concept);
        }
        return concepts;
    }

    public Set<Concept> getDirectSubConcepts(IRI ontologyID, Concept concept) {	
    	// build query:
        Term conceptID = concept.getIdentifier();
        LogicalExpression query = null;
        Set<Map<Variable, Term>> bindings;
		try {
			query = leFactory.createLogicalExpression("_\"" + 
					org.wsml.reasoner.WSML2DatalogTransformer.PRED_DIRECT_SUBCONCEPT + 
					"\"(?x, _\"" + conceptID.toString() + "\")");
			// submit query to reasoner:
            bindings = internalExecuteQuery(ontologyID, query);
        } catch (DatalogException e) {
            throw new InternalReasonerException();
        } catch (ExternalToolException e) {
            throw new InternalReasonerException();
        } catch (ParserException e) {
        	throw new InternalReasonerException();
		}

        // extract concepts from result:
        Set<Concept> concepts = new HashSet<Concept>();
        for (Map<Variable, Term> binding : bindings) {
            IRI subConceptID = (IRI) binding.get(leFactory
                    .createVariable("x"));
            concepts.add(wsmoFactory.getConcept(subConceptID));
        }
    	if (concepts.contains(concept)) {
    		concepts.remove(concept);
    	}
        return concepts;
	}
    
    public Set<Concept> getSuperConcepts(IRI ontologyID, Concept concept) {
        // build query:
        Term conceptID = wsmoFactory.createIRI(concept.getIdentifier()
                .toString());
        Term conceptVariable = leFactory.createVariable("x");
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
            IRI superConceptID = (IRI) binding.get(leFactory
                    .createVariable("x"));
            concepts.add(wsmoFactory.getConcept(superConceptID));
        }
        if (concepts.contains(concept)) {
        	concepts.remove(concept);
        }
        return concepts;
    }

    public Set<Concept> getDirectSuperConcepts(IRI ontologyID, Concept concept) {
    	// build query:
        Term conceptID = concept.getIdentifier();
        LogicalExpression query = null;
        Set<Map<Variable, Term>> bindings;
		try {
			query = leFactory.createLogicalExpression("_\"" + 
					org.wsml.reasoner.WSML2DatalogTransformer.PRED_DIRECT_SUBCONCEPT + 
					"\"(_\"" + conceptID.toString() + "\", ?x)");
			
			// submit query to reasoner:
            bindings = internalExecuteQuery(ontologyID, query);
        } catch (DatalogException e) {
            throw new InternalReasonerException();
        } catch (ExternalToolException e) {
            throw new InternalReasonerException();
        } catch (ParserException e) {
        	throw new InternalReasonerException();
		}

        // extract concepts from result:
        Set<Concept> concepts = new HashSet<Concept>();
        for (Map<Variable, Term> binding : bindings) {
            IRI subConceptID = (IRI) binding.get(leFactory
                    .createVariable("x"));
            concepts.add(wsmoFactory.getConcept(subConceptID));
        }
    	if (concepts.contains(concept)) {
    		concepts.remove(concept);
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
        if (!disableConsitencyCheck){
        	long consTime_start = System.currentTimeMillis();
	        Set<ConsistencyViolation> errors = new HashSet<ConsistencyViolation>();
	      
	        for (Ontology o : ontologies) {
	            IRI ontologyId = (IRI) o.getIdentifier();
	            errors.addAll(checkConsistency(ontologyId));
	        }
	        if (errors.size() > 0) {
	            Set<IRI> ids = new HashSet<IRI>();
	            for (Ontology o : ontologies) {
	                ids.add((IRI) o.getIdentifier());
	            }
	            deRegisterOntology(ids);
	            throw new InconsistencyException(errors);
	        }
	        long consTime_end = System.currentTimeMillis();
	        consTime = consTime_end - consTime_start;
        }
    }
    

    private void addAttributeOfTypeViolations(Set<ConsistencyViolation> errors,
            IRI ontologyId) throws InvalidModelException {
        // ATTR_OFTYPE(instance,value,concept, attribute,violated_type)

        Variable i = leFactory.createVariable("i");
        Variable v = leFactory.createVariable("v");
        Variable c = leFactory.createVariable("c");
        Variable a = leFactory.createVariable("a");
        Variable t = leFactory.createVariable("t");

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
            
            Term rawValue = violation.get(v);
            //Value value;
            //if (rawValue instanceof DataValue)
            //    value = (DataValue) rawValue;
            //else
            //    value = wsmoFactory.createInstance((IRI) rawValue);
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

            if (violation.get(i) instanceof Identifier){
            	Instance instance = wsmoFactory.getInstance((IRI) violation.get(i));            
            	errors.add(new AttributeTypeViolation(ontologyId, instance, rawValue,
                    attribute, type));
            }
            if (violation.get(i) instanceof ConstructedTerm){
            	errors.add(new AttributeTypeViolation(wsmoFactory.getOntology(ontologyId), (ConstructedTerm)violation.get(i), rawValue, attribute, type));
            }
        }

    }

    private void addMinCardinalityViolations(Set<ConsistencyViolation> errors,
            IRI ontologyId) throws InvalidModelException {
        // MIN_CARD(instance, concept, attribute)

        Variable i = leFactory.createVariable("i");
        Variable c = leFactory.createVariable("c");
        Variable a = leFactory.createVariable("a");

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
            //Instance instance = wsmoFactory.getInstance((Identifier) violation.get(i));
            Concept concept = wsmoFactory.getConcept((Identifier) violation.get(c));
            Attribute attribute = (Attribute) concept.findAttributes(
                    (Identifier) violation.get(a)).iterator().next();
            errors.add(new MinCardinalityViolation(ontologyId, violation.get(i),
                    attribute, wsmoFactory.getOntology(ontologyId)));
        }
    }

    private void addMaxCardinalityViolations(Set<ConsistencyViolation> errors,
            IRI ontologyId) throws InvalidModelException {
        // MAX_CARD(instance, concept, attribute)

        Variable i = leFactory.createVariable("i");
        Variable c = leFactory.createVariable("c");
        Variable a = leFactory.createVariable("a");

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
            //Instance instance = wsmoFactory.getInstance((IRI) violation.get(i));
            Concept concept = wsmoFactory.getConcept((IRI) violation.get(c));
            Attribute attribute = (Attribute) concept.findAttributes(
                    (IRI) violation.get(a)).iterator().next();
            errors.add(new MaxCardinalityViolation(ontologyId, violation.get(i),
                    attribute, wsmoFactory.getOntology(ontologyId)));
        }
    }

    private void addNamedUserViolations(Set<ConsistencyViolation> errors,
            IRI ontologyId) throws InvalidModelException {
        // NAMED_USER(axiom)

        Variable i = leFactory.createVariable("i");

        IRI atomId = wsmoFactory
                .createIRI(ConstraintReplacementNormalizer.NAMED_USER_IRI);

        List<Term> params = new ArrayList<Term>(1);
        params.add(i);
        Atom atom = leFactory.createAtom(atomId, params);

        Set<Map<Variable, Term>> violations = executeQuery(ontologyId, atom);
        for (Map<Variable, Term> violation : violations) {
            Axiom axiom = null;
            String id = violation.get(i).toString();
            if (AnonymousIdUtils.isAnonymousIri(id)){
                errors.add(new UnNamedUserConstraintViolation(ontologyId));
                return;
            }
            id = id.substring(0,id.indexOf(AnonymousIdUtils.NAMED_AXIOM_SUFFIX));
            IRI iri = wsmoFactory.createIRI(id);
            axiom =wsmoFactory.getAxiom(iri);
            errors.add(new NamedUserConstraintViolation(ontologyId, axiom));
        }
    }

    private void addUnNamedUserViolations(Set<ConsistencyViolation> errors,
            IRI ontologyId) throws InvalidModelException {
        // UNNAMED_USER(axiom)

        Variable i = leFactory.createVariable("i");

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
//    	System.out.println("query: " + query);            
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

        List<Term> params = new LinkedList<Term>();
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

            long convTime_start = System.currentTimeMillis();
            
            // Register the program at the built-in reasoner:
            try {
                builtInFacade.register(ontologyUri, kb);
            } catch (org.wsml.reasoner.ExternalToolException e) {
                e.printStackTrace();
                throw new IllegalArgumentException(
                        "This set of ontologies could not have been registered at the built-in reasoner",
                        e);
            }
            
            long convTime_end = System.currentTimeMillis();
            convTime = convTime_end - convTime_start;
        }
    }

    public void registerOntologyNoVerification(Ontology ontology) {
        Set<Ontology> ontologySingletonSet = new HashSet<Ontology>();
        ontologySingletonSet.add(ontology);
        registerOntologiesNoVerification(ontologySingletonSet);
    }

	public Set<Concept> getAllConcepts(IRI ontologyID) {
		// build membership query:
        Term instanceVariable = leFactory.createVariable("z");
		Term conceptVariable1 = leFactory.createVariable("x");
		Term conceptVariable2 = leFactory.createVariable("y");
        LogicalExpression query = leFactory.createMemberShipMolecule(
        		instanceVariable, conceptVariable1);

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
        	IRI conceptID = (IRI) binding.get(leFactory.createVariable("x"));
        	if (!conceptID.getNamespace().toString().startsWith(
        			"http://www.wsmo.org/wsml/wsml-syntax")) {
        		concepts.add(wsmoFactory.getConcept(conceptID));
        	}
        }
        
        // build new subconcept query:
        query = leFactory.createSubConceptMolecule(
        		conceptVariable1, conceptVariable2);
        
        // submit query to reasoner:
        try {
            bindings = internalExecuteQuery(ontologyID, query);
        } catch (DatalogException e) {
            throw new InternalReasonerException();
        } catch (ExternalToolException e) {
            throw new InternalReasonerException();
        }
        
        // extract concepts from result:
        for (Map<Variable, Term> binding : bindings) {
            IRI conceptID1 = (IRI) binding.get(leFactory.createVariable("x"));
            if (!conceptID1.getNamespace().toString().startsWith(
            		"http://www.wsmo.org/wsml/wsml-syntax")) {
        		concepts.add(wsmoFactory.getConcept(conceptID1));
        	}
            IRI conceptID2 = (IRI) binding.get(leFactory.createVariable("y"));
            if (!conceptID2.getNamespace().toString().startsWith(
            		"http://www.wsmo.org/wsml/wsml-syntax")) {
        		concepts.add(wsmoFactory.getConcept(conceptID2));
        	}
        }
        
        // build new constraint attributes query:
        query = leFactory.createAttributeConstraint(
        		conceptVariable1, instanceVariable, conceptVariable2);
        
        // submit query to reasoner:
        try {
            bindings = internalExecuteQuery(ontologyID, query);
        } catch (DatalogException e) {
            throw new InternalReasonerException();
        } catch (ExternalToolException e) {
            throw new InternalReasonerException();
        }
        
        // extract concepts from result:
        for (Map<Variable, Term> binding : bindings) {
            IRI conceptID = (IRI) binding.get(leFactory.createVariable("x"));
            if (!conceptID.getNamespace().toString().startsWith(
            		"http://www.wsmo.org/wsml/wsml-syntax")) {
        		concepts.add(wsmoFactory.getConcept(conceptID));
        	}
            if (binding.get(leFactory.createVariable("y")) instanceof IRI) {
            	conceptID = (IRI) binding.get(leFactory.createVariable("y"));
            	if (!conceptID.getNamespace().toString().startsWith(
            			"http://www.wsmo.org/wsml/wsml-syntax")) {
            		concepts.add(wsmoFactory.getConcept(conceptID));
            	}
            }
        }

        // build new inference attributes query:
        query = leFactory.createAttributeInference(
        		conceptVariable1, instanceVariable, conceptVariable2);
        
        // submit query to reasoner:
        try {
            bindings = internalExecuteQuery(ontologyID, query);
        } catch (DatalogException e) {
            throw new InternalReasonerException();
        } catch (ExternalToolException e) {
            throw new InternalReasonerException();
        }
        
        // extract concepts from result:
        for (Map<Variable, Term> binding : bindings) {
            IRI conceptID = (IRI) binding.get(leFactory.createVariable("x"));
            if (!conceptID.getNamespace().toString().startsWith(
            		"http://www.wsmo.org/wsml/wsml-syntax")) {
        		concepts.add(wsmoFactory.getConcept(conceptID));
        	}
            if (binding.get(leFactory.createVariable("y")) instanceof IRI) {
            	conceptID = (IRI) binding.get(leFactory.createVariable("y"));
            	if (!conceptID.getNamespace().toString().startsWith(
            			"http://www.wsmo.org/wsml/wsml-syntax")) {
            		concepts.add(wsmoFactory.getConcept(conceptID));
            	}
            }
        }
        
        Set<Instance> tmpInst = getAllInstances(ontologyID);
        Set<IRI> tmpAttr = getAllAttributes(ontologyID);
        Set<Concept> tmpConc = new HashSet<Concept>(concepts.size());
        for (Concept concept : concepts) {
        	tmpConc.add(concept);
        }
        
        for (Concept concept : tmpConc) {
        	IRI conceptID = (IRI) concept.getIdentifier();
        	for (Instance instance  : tmpInst) {
        		if (conceptID.equals(instance.getIdentifier())) {
        			concepts.remove(concept);
        		}
        	}
        	for (IRI attribute : tmpAttr) {
        		if (conceptID.equals(attribute)) {
        			concepts.remove(wsmoFactory.getConcept(attribute));
        		}
        	}
        }
        
        return concepts;
	}

	public Set<Instance> getAllInstances(IRI ontologyID) {
		// build membership query:
		Term instanceVariable = leFactory.createVariable("x");
		Term variable = leFactory.createVariable("y");
		Term attributeVariable = leFactory.createVariable("z");
		LogicalExpression query = leFactory.createMemberShipMolecule(
				instanceVariable, variable);
		
		// submit query to reasoner:
		Set<Map<Variable, Term>> bindings;
		try {
			bindings = internalExecuteQuery(ontologyID, query);
		} catch (DatalogException e) {
			throw new InternalReasonerException();
		} catch (ExternalToolException e) {
			throw new InternalReasonerException();
		}
		
		// extract instances from result:
		Set<Instance> instances = new HashSet<Instance>();
		for (Map<Variable, Term> binding : bindings) {
			if (binding.get(leFactory.createVariable("x")) instanceof IRI) {
				IRI instanceID = (IRI) binding.get(leFactory.createVariable("x"));
				instances.add(wsmoFactory.getInstance(instanceID));
			}
		}
		
		// build new attribute value query:
        query = leFactory.createAttributeValue(
        		instanceVariable, attributeVariable, variable);
        
        // submit query to reasoner:
        try {
            bindings = internalExecuteQuery(ontologyID, query);
        } catch (DatalogException e) {
            throw new InternalReasonerException();
        } catch (ExternalToolException e) {
            throw new InternalReasonerException();
        }
        
        // extract instances from result:
        for (Map<Variable, Term> binding : bindings) {
            IRI instanceID = (IRI) binding.get(leFactory.createVariable("x"));
            if (!instanceID.getNamespace().toString().startsWith(
            		"http://www.wsmo.org/wsml/wsml-syntax")) {
            	instances.add(wsmoFactory.getInstance(instanceID));
        	}
            if (binding.get(leFactory.createVariable("y")) instanceof IRI) {
            	instanceID = (IRI) binding.get(leFactory.createVariable("y"));
            	if (!instanceID.getNamespace().toString().startsWith(
            			"http://www.wsmo.org/wsml/wsml-syntax")) {
            		instances.add(wsmoFactory.getInstance(instanceID));
            	}
            }
        }
		
		return instances;
	}

	public Set<IRI> getAllAttributes(IRI ontologyID) {
		// build query for extracting constraining attributes:
		Term instanceVariable = leFactory.createVariable("x");
		Term attributeVariable = leFactory.createVariable("y");
		Term conceptVariable = leFactory.createVariable("z");
		LogicalExpression query = leFactory.createAttributeConstraint(
				instanceVariable, attributeVariable, conceptVariable);
		
		// submit query to reasoner:
		Set<Map<Variable, Term>> bindings;
		try {
			bindings = internalExecuteQuery(ontologyID, query);
		} catch (DatalogException e) {
			throw new InternalReasonerException();
		} catch (ExternalToolException e) {
			throw new InternalReasonerException();
		}
		
		// extract instances from result:
		Set<IRI> attributes = new HashSet<IRI>();
		for (Map<Variable, Term> binding : bindings) {
			IRI attributeID = (IRI) binding.get(leFactory.createVariable("y"));
			attributes.add(attributeID);
		}
		
		// build new query for infering attributes:
		query = leFactory.createAttributeInference(
				instanceVariable, attributeVariable, conceptVariable);
		
		// submit query to reasoner:
		try {
			bindings = internalExecuteQuery(ontologyID, query);
		} catch (DatalogException e) {
			throw new InternalReasonerException();
		} catch (ExternalToolException e) {
			throw new InternalReasonerException();
		}
		
		// extract instances from result:
		for (Map<Variable, Term> binding : bindings) {
			IRI attributeID = (IRI) binding.get(leFactory.createVariable("y"));
			attributes.add(attributeID);
		}
		
		// build new query for attribute which have attribute values:
		query = leFactory.createAttributeValue(
				instanceVariable, attributeVariable, conceptVariable);
		
		// submit query to reasoner:
		try {
			bindings = internalExecuteQuery(ontologyID, query);
		} catch (DatalogException e) {
			throw new InternalReasonerException();
		} catch (ExternalToolException e) {
			throw new InternalReasonerException();
		}
		
		// extract instances from result:
		for (Map<Variable, Term> binding : bindings) {
			IRI attributeID = (IRI) binding.get(leFactory.createVariable("y"));
			attributes.add(attributeID);
		}
		return attributes;
	}

	public Set<IRI> getAllConstraintAttributes(IRI ontologyID) {
		// build query:
		Term instanceVariable = leFactory.createVariable("x");
		Term attributeVariable = leFactory.createVariable("y");
		Term conceptVariable = leFactory.createVariable("z");
		LogicalExpression query = leFactory.createAttributeConstraint(
				instanceVariable, attributeVariable, conceptVariable);
		
		// submit query to reasoner:
		Set<Map<Variable, Term>> bindings;
		try {
			bindings = internalExecuteQuery(ontologyID, query);
		} catch (DatalogException e) {
			throw new InternalReasonerException();
		} catch (ExternalToolException e) {
			throw new InternalReasonerException();
		}
		
		// extract instances from result:
		Set<IRI> attributes = new HashSet<IRI>();
		for (Map<Variable, Term> binding : bindings) {
			IRI attributeID = (IRI) binding.get(leFactory.createVariable("y"));
			attributes.add(attributeID);
		}
		return attributes;
	}

	public Set<IRI> getAllInferenceAttributes(IRI ontologyID) {
		// build query:
		Term instanceVariable = leFactory.createVariable("x");
		Term attributeVariable = leFactory.createVariable("y");
		Term conceptVariable = leFactory.createVariable("z");
		LogicalExpression query = leFactory.createAttributeInference(
				instanceVariable, attributeVariable, conceptVariable);
		
		// submit query to reasoner:
		Set<Map<Variable, Term>> bindings;
		try {
			bindings = internalExecuteQuery(ontologyID, query);
		} catch (DatalogException e) {
			throw new InternalReasonerException();
		} catch (ExternalToolException e) {
			throw new InternalReasonerException();
		}
		
		// extract instances from result:
		Set<IRI> attributes = new HashSet<IRI>();
		for (Map<Variable, Term> binding : bindings) {
			IRI attributeID = (IRI) binding.get(leFactory.createVariable("y"));
			attributes.add(attributeID);
		}
		return attributes;
	}

	public Set<Concept> getEquivalentConcepts(IRI ontologyID, Concept concept) {
		throw new UnsupportedOperationException(
				"Equivalence queries are not supported in Datalog");
	}

	public boolean isEquivalentConcept(IRI ontologyID, Concept concept1, 
			Concept concept2) {
		throw new UnsupportedOperationException(
				"Equivalence queries are not supported in Datalog");
	}

	public Set<Concept> getDirectConcepts(IRI ontologyID, Instance instance) {
        // build query:
        Term instanceID = instance.getIdentifier();
        LogicalExpression query = null;
        Set<Map<Variable, Term>> bindings;
		try {
			query = leFactory.createLogicalExpression("_\"" + 
					org.wsml.reasoner.WSML2DatalogTransformer.PRED_DIRECT_CONCEPT + 
					"\"(_\"" + instanceID.toString() + "\", ?x)");
			// submit query to reasoner:
            bindings = internalExecuteQuery(ontologyID, query);
        } catch (DatalogException e) {
            throw new InternalReasonerException();
        } catch (ExternalToolException e) {
            throw new InternalReasonerException();
        } catch (ParserException e) {
        	throw new InternalReasonerException();
		}

        // extract concepts from result:
        Set<Concept> concepts = new HashSet<Concept>();
        for (Map<Variable, Term> binding : bindings) {
            IRI conceptID = (IRI) binding.get(leFactory.createVariable("x"));
            concepts.add(wsmoFactory.getConcept(conceptID));
        }
        return concepts;
	}

	public Set<Concept> getConceptsOf(IRI ontologyID, Identifier attributeId) {
        // build query:
		Term instanceVariable = leFactory.createVariable("x");
		Term valueVariable = leFactory.createVariable("y");
        Term conceptVariable = leFactory.createVariable("z");
        LogicalExpression queryPart1 = leFactory.createAttributeValue(
        		instanceVariable, attributeId, valueVariable);
        LogicalExpression queryPart2 = leFactory.createMemberShipMolecule(
        		instanceVariable, conceptVariable);
        LogicalExpression query = leFactory.createConjunction(queryPart1, 
        		queryPart2);
        
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
            IRI conceptID = (IRI) binding.get(leFactory.createVariable("z"));
            concepts.add(wsmoFactory.getConcept(conceptID));
        }
        return concepts;
	}

	
	public Set<IRI> getSubRelations(IRI ontologyID, Identifier attributeId) {
		throw new UnsupportedOperationException(
		"This method is not implemented for Datalog");
	}
	
	public Set<IRI> getDirectSubRelations(IRI ontologyID, Identifier attributeId) {
		throw new UnsupportedOperationException(
				"This method is not implemented for Datalog");
	}

	
	public Set<IRI> getSuperRelations(IRI ontologyID, Identifier attributeId) {
		throw new UnsupportedOperationException(
				"This method is not implemented for Datalog");
	}
	
	public Set<IRI> getDirectSuperRelations(IRI ontologyID, Identifier attributeId) {
		throw new UnsupportedOperationException(
				"This method is not implemented for Datalog");
	}

	public Set<IRI> getEquivalentRelations(IRI ontologyID, Identifier attributeId) {
		throw new UnsupportedOperationException(
				"Equivalence queries are not supported in Datalog");
	}

	public Set<IRI> getInverseRelations(IRI ontologyID, Identifier attributeId) {
		throw new UnsupportedOperationException(
		"Queries for inverse relations are not supported in Datalog");
	}
	
	public Set<IRI> getRangesOfInferingAttribute(IRI ontologyID, Identifier attributeId) {
        // build query:
		Term instanceVariable = leFactory.createVariable("x");
		Term valueVariable = leFactory.createVariable("y");
        LogicalExpression query = leFactory.createAttributeInference(
        		instanceVariable, attributeId, valueVariable);

        // submit query to reasoner:
        Set<Map<Variable, Term>> bindings;
        try {
            bindings = internalExecuteQuery(ontologyID, query);
        } catch (DatalogException e) {
            throw new InternalReasonerException();
        } catch (ExternalToolException e) {
            throw new InternalReasonerException();
        }

        // extract iris from result:
        Set<IRI> iris = new HashSet<IRI>();
        for (Map<Variable, Term> binding : bindings) {
            IRI id = (IRI) binding.get(leFactory.createVariable("y"));
            iris.add(id);
        }
        return iris;
	}

	public Set<IRI> getRangesOfConstraintAttribute(IRI ontologyID, 
			Identifier attributeId) {
        // build query:
		Term instanceVariable = leFactory.createVariable("x");
		Term valueVariable = leFactory.createVariable("y");
        LogicalExpression query = leFactory.createAttributeConstraint(
        		instanceVariable, attributeId, valueVariable);

        // submit query to reasoner:
        Set<Map<Variable, Term>> bindings;
        try {
            bindings = internalExecuteQuery(ontologyID, query);
        } catch (DatalogException e) {
            throw new InternalReasonerException();
        } catch (ExternalToolException e) {
            throw new InternalReasonerException();
        }

        // extract iris from result:
        Set<IRI> iris = new HashSet<IRI>();
        for (Map<Variable, Term> binding : bindings) {
            IRI id = (IRI) binding.get(leFactory.createVariable("y"));
            iris.add(id);
        }
        return iris;
	}

	public Map<IRI, Set<Term>> getInferingAttributeValues(IRI ontologyID, 
			Instance instance) {
        // build query:
		Term attributeVariable = leFactory.createVariable("x");
		Term valueVariable = leFactory.createVariable("y");
		Term valueVariable2 = leFactory.createVariable("w");
		Term instanceVariable = leFactory.createVariable("z");
        LogicalExpression queryPart1 = leFactory.createAttributeValue(
        		instance.getIdentifier(), attributeVariable, valueVariable);
        LogicalExpression queryPart2 = leFactory.createAttributeInference(
        		instanceVariable, attributeVariable, valueVariable2);
        LogicalExpression query = leFactory.createConjunction(queryPart1, 
        		queryPart2);

        // submit query to reasoner:
        Set<Map<Variable, Term>> bindings;
        try {
            bindings = internalExecuteQuery(ontologyID, query);
        } catch (DatalogException e) {
            throw new InternalReasonerException();
        } catch (ExternalToolException e) {
            throw new InternalReasonerException();
        }

        // extract results:
        Map<IRI, Set<Term>> results = new HashMap<IRI, Set<Term>>();
        for (Map<Variable, Term> binding : bindings) {
        	IRI attributeId = (IRI) binding.get(leFactory.createVariable("x"));
        	if (results.containsKey(attributeId)) {
        		Set<Term> temp = new HashSet<Term>();
        		Term value = (Term) binding.get(leFactory.createVariable("y"));
        		temp = results.get(attributeId);
        		temp.add(value);
        		results.put(attributeId, temp);
        	}
        	else {
        		Set<Term> temp = new HashSet<Term>();
        		Term value = (Term) binding.get(leFactory.createVariable("y"));
        		temp.add(value);
	        	results.put(attributeId, temp);
        	}
        }         
        return results;
	}

	public Map<IRI, Set<Term>> getConstraintAttributeValues(IRI ontologyID, 
			Instance instance) {
        // build query:
		Term attributeVariable = leFactory.createVariable("x");
		Term valueVariable = leFactory.createVariable("y");
		Term valueVariable2 = leFactory.createVariable("w");
		Term instanceVariable = leFactory.createVariable("z");
        LogicalExpression queryPart1 = leFactory.createAttributeValue(
        		instance.getIdentifier(), attributeVariable, valueVariable);
        LogicalExpression queryPart2 = leFactory.createAttributeConstraint(
        		instanceVariable, attributeVariable, valueVariable2);
        LogicalExpression query = leFactory.createConjunction(queryPart1, 
        		queryPart2);
        
        // submit query to reasoner:
        Set<Map<Variable, Term>> bindings;
        try {
            bindings = internalExecuteQuery(ontologyID, query);
        } catch (DatalogException e) {
            throw new InternalReasonerException();
        } catch (ExternalToolException e) {
            throw new InternalReasonerException();
        }

        // extract results:
        Map<IRI, Set<Term>> results = new HashMap<IRI, Set<Term>>();
        for (Map<Variable, Term> binding : bindings) {
        	IRI attributeId = (IRI) binding.get(leFactory.createVariable("x"));
        	if (results.containsKey(attributeId)) {
        		Set<Term> temp = new HashSet<Term>();
        		Term value = (Term) binding.get(leFactory.createVariable("y"));
        		temp = results.get(attributeId);
        		temp.add(value);
        		results.put(attributeId, temp);
        	}
        	else {
        		Set<Term> temp = new HashSet<Term>();
        		Term value = (Term) binding.get(leFactory.createVariable("y"));
        		temp.add(value);
	        	results.put(attributeId, temp);
        	}
        }             
        return results;
	}

	public Map<Instance, Set<Term>> getInferingAttributeInstances(IRI ontologyID, 
			Identifier attributeId) {
        // build query:
		Term valueVariable = leFactory.createVariable("x");
		Term valueVariable2 = leFactory.createVariable("z");
		Term instanceVariable = leFactory.createVariable("y");
		Term instanceVariable2 = leFactory.createVariable("w");
        LogicalExpression queryPart1 = leFactory.createAttributeValue(
        		instanceVariable, attributeId, valueVariable);
        LogicalExpression queryPart2 = leFactory.createAttributeInference(
        		instanceVariable2, attributeId, valueVariable2);
        LogicalExpression query = leFactory.createConjunction(queryPart1, 
        		queryPart2);
        
        // submit query to reasoner:
        Set<Map<Variable, Term>> bindings;
        try {
            bindings = internalExecuteQuery(ontologyID, query);
        } catch (DatalogException e) {
            throw new InternalReasonerException();
        } catch (ExternalToolException e) {
            throw new InternalReasonerException();
        }

        // extract results:
        Map<Instance, Set<Term>> results = new HashMap<Instance, Set<Term>>();
        for (Map<Variable, Term> binding : bindings) {
        	Instance instance = wsmoFactory.getInstance((IRI) binding.get(
        			leFactory.createVariable("y")));
        	if (results.containsKey(instance)) {
        		Set<Term> temp = new HashSet<Term>();
        		Term value = (Term) binding.get(leFactory.createVariable("x"));
        		temp = results.get(instance);
        		temp.add(value);
        		results.put(instance, temp);
        	}
        	else {
        		Set<Term> temp = new HashSet<Term>();
        		Term value = (Term) binding.get(leFactory.createVariable("x"));
        		temp.add(value);
	        	results.put(instance, temp);
        	}
        }         
        
        return results;
	}

	public Map<Instance, Set<Term>> getConstraintAttributeInstances(
			IRI ontologyID, Identifier attributeId) {
        // build query:
		Term valueVariable = leFactory.createVariable("x");
		Term valueVariable2 = leFactory.createVariable("z");
		Term instanceVariable = leFactory.createVariable("y");
		Term instanceVariable2 = leFactory.createVariable("w");
        LogicalExpression queryPart1 = leFactory.createAttributeValue(
        		instanceVariable, attributeId, valueVariable);
        LogicalExpression queryPart2 = leFactory.createAttributeConstraint(
        		instanceVariable2, attributeId, valueVariable2);
        LogicalExpression query = leFactory.createConjunction(queryPart1, 
        		queryPart2);
        
        // submit query to reasoner:
        Set<Map<Variable, Term>> bindings;
        try {
            bindings = internalExecuteQuery(ontologyID, query);
        } catch (DatalogException e) {
            throw new InternalReasonerException();
        } catch (ExternalToolException e) {
            throw new InternalReasonerException();
        }

        // extract results:
        Map<Instance, Set<Term>> results = new HashMap<Instance, Set<Term>>();
        for (Map<Variable, Term> binding : bindings) {
        	Instance instance = wsmoFactory.getInstance((IRI) binding.get(
        			leFactory.createVariable("y")));
        	if (results.containsKey(instance)) {
        		Set<Term> temp = new HashSet<Term>();
        		Term value = (Term) binding.get(leFactory.createVariable("x"));
        		temp = results.get(instance);
        		temp.add(value);
        		results.put(instance, temp);
        	}
        	else {
        		Set<Term> temp = new HashSet<Term>();
        		Term value = (Term) binding.get(leFactory.createVariable("x"));
        		temp.add(value);
	        	results.put(instance, temp);
        	}
        }         
        
        return results;
	}

	public Set getInferingAttributeValues(IRI ontologyID, 
			Instance subject, Identifier attributeId) {
        // build query:
		Term instanceId = wsmoFactory.createIRI(subject.getIdentifier()
                .toString());
		Term valueVariable = leFactory.createVariable("x");
		Term valueVariable2 = leFactory.createVariable("y");
		Term instanceVariable = leFactory.createVariable("z");
        LogicalExpression queryPart1 = leFactory.createAttributeValue(
        		instanceId, attributeId, valueVariable);
        LogicalExpression queryPart2 = leFactory.createAttributeInference(
        		instanceVariable, attributeId, valueVariable2);
        LogicalExpression query = leFactory.createConjunction(queryPart1, 
        		queryPart2);

        // submit query to reasoner:
        Set<Map<Variable, Term>> bindings;
        try {
            bindings = internalExecuteQuery(ontologyID, query);
        } catch (DatalogException e) {
            throw new InternalReasonerException();
        } catch (ExternalToolException e) {
            throw new InternalReasonerException();
        }

        // extract instances from result:
        Set values = new HashSet();
        for (Map<Variable, Term> binding : bindings) {
        	if (binding.get(leFactory.createVariable("x")) instanceof IRI) {
	            IRI iri = (IRI) binding.get(leFactory.createVariable("x"));
	            values.add(iri);
        	}
        	else {
        		values.add(binding.get(leFactory.createVariable("x")).toString());
        	}
        }
        return values;
	}

	public Set<String> getConstraintAttributeValues(IRI ontologyID, 
			Instance subject, Identifier attributeId) {
        // build query:
		Term instanceId = wsmoFactory.createIRI(subject.getIdentifier()
                .toString());
		Term valueVariable = leFactory.createVariable("x");
		Term valueVariable2 = leFactory.createVariable("y");
		Term instanceVariable = leFactory.createVariable("z");
        LogicalExpression queryPart1 = leFactory.createAttributeValue(
        		instanceId, attributeId, valueVariable);
        LogicalExpression queryPart2 = leFactory.createAttributeConstraint(
        		instanceVariable, attributeId, valueVariable2);
        LogicalExpression query = leFactory.createConjunction(queryPart1, 
        		queryPart2);

        // submit query to reasoner:
        Set<Map<Variable, Term>> bindings;
        try {
            bindings = internalExecuteQuery(ontologyID, query);
        } catch (DatalogException e) {
            throw new InternalReasonerException();
        } catch (ExternalToolException e) {
            throw new InternalReasonerException();
        }

        // extract instances from result:
        Set values = new HashSet();
        for (Map<Variable, Term> binding : bindings) {
        	if (binding.get(leFactory.createVariable("x")) instanceof IRI) {
	            IRI iri = (IRI) binding.get(leFactory.createVariable("x"));
	            values.add(iri);
        	}
        	else {
        		values.add(binding.get(leFactory.createVariable("x")).toString());
        	}
        }
        return values;
	}

}
