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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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
import org.wsml.reasoner.DatalogReasonerFacade;
import org.wsml.reasoner.ExternalToolException;
import org.wsml.reasoner.Literal;
import org.wsml.reasoner.Rule;
import org.wsml.reasoner.api.InternalReasonerException;
import org.wsml.reasoner.api.WSMLCoreReasoner;
import org.wsml.reasoner.api.WSMLFlightReasoner;
import org.wsml.reasoner.api.WSMLRuleReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory.BuiltInReasoner;
import org.wsml.reasoner.api.inconsistency.AttributeTypeViolation;
import org.wsml.reasoner.api.inconsistency.ConsistencyViolation;
import org.wsml.reasoner.api.inconsistency.InconsistencyException;
import org.wsml.reasoner.api.inconsistency.MaxCardinalityViolation;
import org.wsml.reasoner.api.inconsistency.MinCardinalityViolation;
import org.wsml.reasoner.api.inconsistency.NamedUserConstraintViolation;
import org.wsml.reasoner.api.inconsistency.UnNamedUserConstraintViolation;
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
import org.wsmo.common.Entity;
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
        WSMLCoreReasoner, WSMLRuleReasoner {
    
	protected final static String WSML_RESULT_PREDICATE = "http://www.wsmo.org/reasoner/"
            + "wsml_query_result";

    protected org.wsml.reasoner.DatalogReasonerFacade builtInFacade = null;

    protected WsmoFactory wsmoFactory;

    protected LogicalExpressionFactory leFactory;

    protected WSMO4JManager wsmoManager;
    
    private int allowImports = 0;
    
    private boolean disableConsitencyCheck = false;

    private long normTime = -1;
    
    private long convTime = -1;
    
    private long consTime = -1;
    
    private Set<Map<Variable, Term>> queryContainmentResult = null;
    
    /**
     * Constructs a new Reasoner.
     * @param builtInType the underlying reasoner to use
     * @param wsmoManager the wsmo4j manager to use
     * @param config additional configuration for the facade
     * @throws IllegalArgumentException if the <code>builtInType</code> is <code>null</code>
     * @throws IllegalArgumentException if the wsml4j manager is <code>null</code>
     */
    public DatalogBasedWSMLReasoner(final BuiltInReasoner builtInType, 
    		final WSMO4JManager wsmoManager, 
    		final Map<String, Object> config) {
    	if (builtInType == null) {
    		throw new IllegalArgumentException("The facade type must not be null");
    	}
    	if (wsmoManager == null) {
    		throw new IllegalArgumentException("The WSMO4JManager must not be null");
    	}
    	
    	builtInFacade = createFacade(builtInType.getFacadeClass(), wsmoManager, config);
        this.wsmoManager = wsmoManager;
        wsmoFactory = this.wsmoManager.getWSMOFactory();
        leFactory = this.wsmoManager.getLogicalExpressionFactory();
    }

    /**
     * Instantiates a new facade using reflection. The facade must have a 
     * constructor taking a <code>WSMO4JManager<code>.
     * @param className the class of the facade
     * @param wsmoManager the manager to pass to the constructor
     * @param config the additional configuration for the facade
     * @return the newly instantiated facade
     * @throws InternalReasonerException if something went wrong while 
     * instantiating the reasoner
     */
    private DatalogReasonerFacade createFacade(final String className, 
    		final WSMO4JManager wsmoManager,
    		final Map<String, Object> config) 
    		throws InternalReasonerException {
		assert className != null: "The class name must not be null";
		assert wsmoManager != null: "The manager must not be null";
    	
    	final String illegal_constructor_msg = "Couldn't use the constructor " 
    		+ "for " + className + " taking a WSMO4JManager and a Map";
    	
		try {
			final Class<?> facade = Class.forName(className);
			final Constructor<?> constructor = facade.getConstructor(WSMO4JManager.class, Map.class);
			return (DatalogReasonerFacade) constructor.newInstance(wsmoManager, config);
		} catch (NullPointerException e) {
			throw new InternalReasonerException(illegal_constructor_msg, e);
		} catch (ClassNotFoundException e) {
			throw new InternalReasonerException(illegal_constructor_msg, e);
		} catch (SecurityException e) {
			throw new InternalReasonerException(illegal_constructor_msg, e);
		} catch (NoSuchMethodException e) {
			throw new InternalReasonerException(illegal_constructor_msg, e);
		} catch (IllegalArgumentException e) {
			throw new InternalReasonerException(illegal_constructor_msg, e);
		} catch (InstantiationException e) {
			throw new InternalReasonerException(illegal_constructor_msg, e);
		} catch (IllegalAccessException e) {
			throw new InternalReasonerException(illegal_constructor_msg, e);
		} catch (InvocationTargetException e) {
			throw new InternalReasonerException(illegal_constructor_msg, e);
		}
    }

    public void setDisableConsitencyCheck(boolean check){
        this.disableConsitencyCheck = check;
    }
    
	public void setAllowImports(int allowOntoImports){
		this.allowImports = allowOntoImports;
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
  
    protected Set<org.wsml.reasoner.Rule> convertEntities(Set <Entity> theEntities) {
    	Set<org.wsml.reasoner.Rule> p = new HashSet <org.wsml.reasoner.Rule> ();

        long normTime_start = System.currentTimeMillis();  
        
        Set <Entity> entities = handleAttributeInheritance(theEntities);
        
        // Convert conceptual syntax to logical expressions
        OntologyNormalizer normalizer = new AxiomatizationNormalizer(wsmoManager);
        entities = normalizer.normalizeEntities(entities);

        Set <Axiom> axioms = new HashSet <Axiom> ();
        for (Entity e : entities){
        	if (e instanceof Axiom){
        		axioms.add((Axiom) e);
        	}
        }
        
        // Convert constraints to support debugging
        normalizer = new ConstraintReplacementNormalizer(wsmoManager);
        axioms = normalizer.normalizeAxioms(axioms);

        // Simplify axioms
        normalizer = new ConstructReductionNormalizer(wsmoManager);
        axioms = normalizer.normalizeAxioms(axioms);

        // Apply Lloyd-Topor rules to get Datalog-compatible LEs
        normalizer = new LloydToporNormalizer(wsmoManager);
        axioms = normalizer.normalizeAxioms(axioms);
        
        org.wsml.reasoner.WSML2DatalogTransformer wsml2datalog = new org.wsml.reasoner.WSML2DatalogTransformer(
                wsmoManager);
        Set<org.omwg.logicalexpression.LogicalExpression> lExprs = new LinkedHashSet<org.omwg.logicalexpression.LogicalExpression>();
        for (Axiom a : axioms) {
            lExprs.addAll(a.listDefinitions());
        }
//        System.out.println(lExprs);
        
        p = wsml2datalog.transform(lExprs);
        p.addAll(wsml2datalog.generateAuxilliaryRules());

        long normTime_end = System.currentTimeMillis();
        normTime = normTime_end - normTime_start;
        
        // System.out.println("datalog program:");
        // System.out.println(p);
        // System.out.println("-*");
    	
//        for (Rule r : p){
//        	System.out.println(r);
//        }
        
        return p;
    }
    
    /*
     * Add all attributes of superconcepts to the subconcepts
     */
    private Set <Entity> handleAttributeInheritance(Set <Entity> theEntities) {
    	Set <Entity> result = new HashSet <Entity> ();
    	
    	for (Entity e : theEntities){
    		if (e instanceof Concept){
    			Concept concept = (Concept) e;
    			// process superconcepts:
                for (Concept superconcept : concept.listSuperConcepts()) {
                	// handle inherited attributes
    	            Set<Attribute> superAttr = superconcept.listAttributes();
    	            for (Attribute a : superAttr) {
    	            	Attribute attribute;
    					try {
    						attribute = concept.createAttribute(a.getIdentifier());
    						// process range types:
    						for (Type type : (Set<Type>) a.listTypes()) {
    							attribute.addType(type);
    							// create an appropriate molecule per range type:
    							if (a.isConstraining()) {
    								attribute.setConstraining(true);
    							}
    						}
    	                
    		                // process attribute properties:
    		                if (a.isReflexive()) {
    		                    attribute.setReflexive(true);
    		                }
    		                if (a.isSymmetric()) {
    		                    attribute.setSymmetric(true);
    		                }
    		                if (a.isTransitive()) {
    		                    attribute.setTransitive(true);
    		                }
    		                Identifier inverseAttribute = a.getInverseOf();
    		                if (inverseAttribute != null) {
    		                    attribute.setInverseOf(inverseAttribute);
    		                }
    	                
    		                // process cardinality constraints:
    		                if (a.getMinCardinality() > 0) {
    		                   attribute.setMinCardinality(a.getMinCardinality());
    		                }
    		                if (a.getMaxCardinality() < Integer.MAX_VALUE) {
    		                    attribute.setMaxCardinality(a.getMaxCardinality());
    		                }
    					} catch (InvalidModelException ex) {
    						ex.printStackTrace();
    					}
    	            }
                }
                result.add(concept);
    		}
    		else {
    			result.add(e);
    		}
    	}
    	return result;
    }
    
    public boolean isSatisfiable() {
        IRI violationIRI = wsmoFactory
                .createIRI(ConstraintReplacementNormalizer.VIOLATION_IRI);
        Atom violation = leFactory.createAtom(violationIRI,
                Collections.EMPTY_LIST);
        boolean result = executeGroundQuery(violation) ? false
                : true;
        return result;
    }

    public void deRegister() {
        try {
            builtInFacade.deregister();
        } catch (org.wsml.reasoner.ExternalToolException e) {
            e.printStackTrace();
        }
    }

    public boolean entails(LogicalExpression expression) {
        return executeGroundQuery(expression);
    }

    public boolean entails(Set<LogicalExpression> expressions) {
        for (LogicalExpression e : expressions) {
            if (!executeGroundQuery(e))
                return false;
        }
        return true;
    }

    public boolean executeGroundQuery(LogicalExpression query) {
        return executeQuery(query).size() != 0;
    }

    public Set<Map<Variable, Term>> executeQuery(LogicalExpression query) {
        return internalExecuteQuery(query);
    }

    public boolean checkQueryContainment(LogicalExpression query1, LogicalExpression query2) {
    	
    	// create a logical expression visitor that checks whether the queries 
    	// contain disjunctions, negations or built-ins
    	QueryContainmentHelper helper = new QueryContainmentHelper();
    	query1.accept(helper);
    	query2.accept(helper);
    	    	
    	// convert logical expressions to conjunctive queries
    	Set<org.wsml.reasoner.ConjunctiveQuery> datalogQueries1 = convertQuery(query1);
    	Set<org.wsml.reasoner.ConjunctiveQuery> datalogQueries2 = convertQuery(query2);
    	
    	boolean result = false;
    	
    	// check whether query1 is contained within query2
        for (ConjunctiveQuery datalogQuery1 : datalogQueries1) {
        	for (ConjunctiveQuery datalogQuery2 : datalogQueries2) {
        		result = builtInFacade.checkQueryContainment(datalogQuery1, datalogQuery2);
        	}
        }
        return result;
    }
    
    public Set<Map<Variable, Term>> getQueryContainment(LogicalExpression query1, LogicalExpression query2) {
    	
    	// create a logical expression visitor that checks whether the queries 
    	// contain disjunctions, negations or built-ins
    	QueryContainmentHelper helper = new QueryContainmentHelper();
    	query1.accept(helper);
    	query2.accept(helper);
    	    	
    	// convert logical expressions to conjunctive queries
    	Set<org.wsml.reasoner.ConjunctiveQuery> datalogQueries1 = convertQuery(query1);
    	Set<org.wsml.reasoner.ConjunctiveQuery> datalogQueries2 = convertQuery(query2);
    	
    	// build set for query containment variable mapping result
    	queryContainmentResult = new HashSet<Map<Variable, Term>>();
    	
    	// check whether query1 is contained within query2
        for (ConjunctiveQuery datalogQuery1 : datalogQueries1) {
        	for (ConjunctiveQuery datalogQuery2 : datalogQueries2) {
	            try {
					queryContainmentResult.addAll(builtInFacade.getQueryContainment(datalogQuery1, datalogQuery2));
				} catch (ExternalToolException e) {
					throw new InternalReasonerException(e);
				}
        	}
        }
        return queryContainmentResult;
    }
    
    public Set<Concept> getConcepts(Instance instance) {
        // build membership query:
        Term instanceID = wsmoFactory.createIRI(instance.getIdentifier()
                .toString());
        Term conceptVariable = leFactory.createVariable("x");
        LogicalExpression query = leFactory.createMemberShipMolecule(
                instanceID, conceptVariable);

        // submit query to reasoner:
        Set<Map<Variable, Term>> bindings = internalExecuteQuery(query);

        // extract concepts from result:
        Set<Concept> concepts = new HashSet<Concept>();
        for (Map<Variable, Term> binding : bindings) {
            IRI conceptID = (IRI) binding.get(leFactory.createVariable("x"));
            concepts.add(wsmoFactory.getConcept(conceptID));
        }
        return concepts;
    }

    public Set<Instance> getInstances(Concept concept) {
        // build query:
        Term conceptID = wsmoFactory.createIRI(concept.getIdentifier()
                .toString());
        Term instanceVariable = leFactory.createVariable("x");
        LogicalExpression query = leFactory.createMemberShipMolecule(
                instanceVariable, conceptID);

        // submit query to reasoner:
        Set<Map<Variable, Term>> bindings = internalExecuteQuery(query);

        // extract concepts from result:
        Set<Instance> instances = new HashSet<Instance>();
        for (Map<Variable, Term> binding : bindings) {
            IRI instanceID = (IRI) binding.get(leFactory.createVariable("x"));
            instances.add(wsmoFactory.getInstance(instanceID));
        }

        return instances;
    }

    public Set<Concept> getSubConcepts(Concept concept) {
        // build query:
        Term conceptID = wsmoFactory.createIRI(concept.getIdentifier()
                .toString());
        Term conceptVariable = leFactory.createVariable("x");
        LogicalExpression query = leFactory.createSubConceptMolecule(
                conceptVariable, conceptID);

        // submit query to reasoner:
        Set<Map<Variable, Term>> bindings = internalExecuteQuery(query);

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

    public Set<Concept> getDirectSubConcepts(Concept concept) {	
    	// build query:
        Term conceptID = concept.getIdentifier();
        LogicalExpression query = null;
        Set<Map<Variable, Term>> bindings;
		try {
			query = leFactory.createLogicalExpression("_\"" + 
					org.wsml.reasoner.WSML2DatalogTransformer.PRED_DIRECT_SUBCONCEPT + 
					"\"(?x, _\"" + conceptID.toString() + "\")");
			// submit query to reasoner:
            bindings = internalExecuteQuery(query);
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
    
    public Set<Concept> getSuperConcepts(Concept concept) {
        // build query:
        Term conceptID = wsmoFactory.createIRI(concept.getIdentifier()
                .toString());
        Term conceptVariable = leFactory.createVariable("x");
        LogicalExpression query = leFactory.createSubConceptMolecule(conceptID,
                conceptVariable);

        // submit query to reasoner:
        Set<Map<Variable, Term>> bindings = internalExecuteQuery(query);

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

    public Set<Concept> getDirectSuperConcepts(Concept concept) {
    	// build query:
        Term conceptID = concept.getIdentifier();
        LogicalExpression query = null;
        Set<Map<Variable, Term>> bindings;
		try {
			query = leFactory.createLogicalExpression("_\"" + 
					org.wsml.reasoner.WSML2DatalogTransformer.PRED_DIRECT_SUBCONCEPT + 
					"\"(_\"" + conceptID.toString() + "\", ?x)");
			
			// submit query to reasoner:
            bindings = internalExecuteQuery(query);
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
    
    public boolean isMemberOf(Instance instance, Concept concept) {
        // build query:
        Term conceptID = wsmoFactory.createIRI(concept.getIdentifier()
                .toString());
        Term instanceID = wsmoFactory.createIRI(instance.getIdentifier()
                .toString());
        LogicalExpression query = leFactory.createMemberShipMolecule(
                instanceID, conceptID);

        // submit query to reasoner:
        Set<Map<Variable, Term>> bindings = internalExecuteQuery(query);

        // check for non-empty result:
        return bindings.size() != 0;
    }

    public boolean isSubConceptOf(Concept subConcept, Concept superConcept) {
        // build query:
        Term superconceptID = wsmoFactory.createIRI(superConcept
                .getIdentifier().toString());
        Term subconceptID = wsmoFactory.createIRI(subConcept.getIdentifier()
                .toString());
        LogicalExpression query = leFactory.createSubConceptMolecule(
                subconceptID, superconceptID);

        // / submit query to reasoner:
        Set<Map<Variable, Term>> bindings = internalExecuteQuery(query);

        // check for non-empty result:
        return bindings.size() != 0;
    }

    public void registerOntology(Ontology ontology) throws InconsistencyException {
        Set<Ontology> ontologySingletonSet = new HashSet<Ontology>();
        ontologySingletonSet.add(ontology);
        registerOntologies(ontologySingletonSet);
    }

    public void registerOntologies(Set<Ontology> ontologies) throws InconsistencyException {
    	if(allowImports == 0){
    		ontologies = getAllOntologies(ontologies);
    	}
    	
    	
    	Set <Entity> entities = new HashSet <Entity>();
        for (Ontology o : ontologies) {
        	entities.addAll(o.listConcepts());
        	entities.addAll(o.listInstances());
        	entities.addAll(o.listRelations());
        	entities.addAll(o.listRelationInstances());
        	entities.addAll(o.listAxioms());
        }
        registerEntities(entities);
    }
    
    public void registerEntities(Set<Entity> theEntities) throws InconsistencyException{
    	registerEntitiesNoVerification(theEntities);
    	if (!disableConsitencyCheck){
        	long consTime_start = System.currentTimeMillis();
	        Set<ConsistencyViolation> errors = new HashSet<ConsistencyViolation>();
	        errors.addAll(checkConsistency());
	   
	        if (errors.size() > 0) {
	            deRegister();
	            throw new InconsistencyException(errors);
	        }
	        long consTime_end = System.currentTimeMillis();
	        consTime = consTime_end - consTime_start;
        }
    }
    

    private void addAttributeOfTypeViolations(Set<ConsistencyViolation> errors) throws InvalidModelException {
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

        Set<Map<Variable, Term>> violations = executeQuery(atom);
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
            	errors.add(new AttributeTypeViolation(instance, rawValue, attribute, type));
            }
            if (violation.get(i) instanceof ConstructedTerm){
            	errors.add(new AttributeTypeViolation((ConstructedTerm)violation.get(i), rawValue, attribute, type));
            }
        }

    }

    private void addMinCardinalityViolations(Set<ConsistencyViolation> errors) throws InvalidModelException {
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

        Set<Map<Variable, Term>> violations = executeQuery(atom);
        for (Map<Variable, Term> violation : violations) {
            // Construct error object
            //Instance instance = wsmoFactory.getInstance((Identifier) violation.get(i));
            Concept concept = wsmoFactory.getConcept((Identifier) violation.get(c));
            Attribute attribute = (Attribute) concept.findAttributes((Identifier) violation.get(a)).iterator().next();
            errors.add(new MinCardinalityViolation(violation.get(i), attribute, concept.getOntology()));
        }
    }

    private void addMaxCardinalityViolations(Set<ConsistencyViolation> errors) throws InvalidModelException {
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

        Set<Map<Variable, Term>> violations = executeQuery(atom);
        for (Map<Variable, Term> violation : violations) {
            // Construct error object
            //Instance instance = wsmoFactory.getInstance((IRI) violation.get(i));
            Concept concept = wsmoFactory.getConcept((IRI) violation.get(c));
            Attribute attribute = (Attribute) concept.findAttributes((IRI) violation.get(a)).iterator().next();
            errors.add(new MaxCardinalityViolation(violation.get(i), attribute, concept.getOntology()));
        }
    }

    private void addNamedUserViolations(Set<ConsistencyViolation> errors) throws InvalidModelException {
        // NAMED_USER(axiom)

        Variable i = leFactory.createVariable("i");

        IRI atomId = wsmoFactory
                .createIRI(ConstraintReplacementNormalizer.NAMED_USER_IRI);

        List<Term> params = new ArrayList<Term>(1);
        params.add(i);
        Atom atom = leFactory.createAtom(atomId, params);

        Set<Map<Variable, Term>> violations = executeQuery(atom);
        for (Map<Variable, Term> violation : violations) {
            Axiom axiom = null;
            String id = violation.get(i).toString();
            if (AnonymousIdUtils.isAnonymousIri(id)){
                errors.add(new UnNamedUserConstraintViolation());
            }
            else {
	            id = id.substring(0,id.indexOf(AnonymousIdUtils.NAMED_AXIOM_SUFFIX));
	            IRI iri = wsmoFactory.createIRI(id);
	            axiom =wsmoFactory.getAxiom(iri);
	            errors.add(new NamedUserConstraintViolation(axiom));
            }
        }
    }

    private void addUnNamedUserViolations(Set<ConsistencyViolation> errors) throws InvalidModelException {
        // UNNAMED_USER(axiom)

        Variable i = leFactory.createVariable("i");

        IRI atomId = wsmoFactory
                .createIRI(ConstraintReplacementNormalizer.UNNAMED_USER_IRI);

        List<Term> params = new ArrayList<Term>(1);
        params.add(i);
        Atom atom = leFactory.createAtom(atomId, params);

        Set<Map<Variable, Term>> violations = executeQuery(atom);
        for (int k = 0; k < violations.size(); k++) {
            // Construct error object
            errors.add(new UnNamedUserConstraintViolation());
        }
    }

    public boolean entails(IRI baseOntologyID, IRI consequenceOntologyID) {
        throw new UnsupportedOperationException("Method not implemented yet!");
    }

    protected Set<Map<Variable, Term>> internalExecuteQuery(LogicalExpression query) {
        Set<org.wsml.reasoner.ConjunctiveQuery> datalogQueries = convertQuery(query);
        Set<Map<Variable, Term>> result = new HashSet<Map<Variable, Term>>();
        for (ConjunctiveQuery datalogQuery : datalogQueries) {
        	try
        	{
        		result.addAll(builtInFacade.evaluate(datalogQuery));
	        } catch (DatalogException e) {
	            throw new InternalReasonerException( e );
	        } catch (ExternalToolException e) {
	            throw new InternalReasonerException( e );
	        }

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

        LogicalExpressionNormalizer moleculeNormalizer = new OnePassReplacementNormalizer(
                new MoleculeDecompositionRules(wsmoManager), wsmoManager);
        q = moleculeNormalizer.normalize(q);

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

//         System.out.println("Query as program:" + p);
        // if (p.size() != 1)
        // throw new IllegalArgumentException("Could not transform query " + q);

        Set<ConjunctiveQuery> result = new HashSet<ConjunctiveQuery>();

        for (Rule rule : p) {
            
        	if (rule.getHead().getPredicateUri().equals(WSML_RESULT_PREDICATE)){
            
	            List<Literal> body = new LinkedList<Literal>();
	
	            for (Literal l : rule.getBody()) {
	                body.add(l);
	            }
	            result.add(new org.wsml.reasoner.ConjunctiveQuery(body));
        	} else {
        		//TODO we need to add these rules to the datalog programm
        		// and remove all of them after the query evaluation is finished!
        		
        		// problem wrt impl. right now> would require to change the DATALOG PROGRAM
        		// for each query! which is bad if one precomputes the model for a program
        		// an materializes the result, since the materializtion needs to be done all
        		// the time again and again. 
        		// CurrentlY this does not fit to our reasoner implementation. 
        		
        		// This means the following QUERY does not retrieve the correct answer>
        		// ?- ?x subConceptOf SomeConceptThatIsNOTintheOntology
        		// as answers we would get the empty set, but should get one tuple
        		// (SomeConceptThatIsNOTintheOntology)
        		
        		
        		
        	}
        }
        return result;
    }

    public Set<ConsistencyViolation> checkConsistency() {
        Set<ConsistencyViolation> errors = new HashSet<ConsistencyViolation>();
        if (!isSatisfiable()) {
            try {
                addAttributeOfTypeViolations(errors);
                addMinCardinalityViolations(errors);
                addMaxCardinalityViolations(errors);
                addNamedUserViolations(errors);
                addUnNamedUserViolations(errors);
            } catch (InvalidModelException e) {
                throw new InternalReasonerException(e);
            }
        }
        return errors;
    }

    public void registerEntitiesNoVerification(Set<Entity> theEntities) {	
        Set<org.wsml.reasoner.Rule> kb = new HashSet<org.wsml.reasoner.Rule>();
        
		kb.addAll(convertEntities(theEntities));

        long convTime_start = System.currentTimeMillis();
        
        // Register the program at the built-in reasoner:
        try {
            builtInFacade.register(kb);
        } catch (org.wsml.reasoner.ExternalToolException e) {
            e.printStackTrace();
            throw new IllegalArgumentException(
                    "This set of ontologies could not have been registered at the built-in reasoner",
                    e);
        }
        
        long convTime_end = System.currentTimeMillis();
        convTime = convTime_end - convTime_start;
    }

    private Set<Ontology> getAllOntologies(Set <Ontology> ontologies){
    	Set <Ontology> result = new HashSet <Ontology> ();
    	for (Ontology o : ontologies){
    		result.add(o);
    		getAllOntologies(o, result);
    	}
    	return result;
    }

	private void getAllOntologies( Ontology o, Set <Ontology> ontologies){
	    for (Ontology imported : o.listOntologies()){
	    	if (!ontologies.contains( imported )){
	    		ontologies.add(imported);
	    		getAllOntologies(imported, ontologies);
	    	}
	    }
    }

	public void registerOntologyNoVerification(Ontology ontology) {
    	Set <Entity> entities = new HashSet <Entity> ();
		entities.addAll(ontology.listConcepts());
    	entities.addAll(ontology.listInstances());
    	entities.addAll(ontology.listRelations());
    	entities.addAll(ontology.listRelationInstances());
    	entities.addAll(ontology.listAxioms());
        registerEntitiesNoVerification(entities);
    }

	/**
	 * Extract concept IRIs from a query result set.
	 * @param bindings The results set.
	 * @param variable The variables identifying the term of interest.
	 * @param concepts The collection to hold the found concepts.
	 */
	private void extractConcepts( Set<Map<Variable, Term>> bindings, Term variable, Set<Concept> concepts )
	{
        for (Map<Variable, Term> binding : bindings) {
        	Term term = binding.get( variable );
        	if ( term instanceof IRI)
        	{
	        	IRI conceptID = (IRI) binding.get(variable);
	        	if (!conceptID.getNamespace().toString().startsWith(
	        			"http://www.wsmo.org/wsml/wsml-syntax")) {
	        		concepts.add(wsmoFactory.getConcept(conceptID));
	        	}
        	}
        }
	}

	public Set<Concept> getAllConcepts() {
		// Create some variables to use in all the queries.
        Term instance = leFactory.createVariable("instance");
		Term concept1 = leFactory.createVariable("concept1");
		Term concept2 = leFactory.createVariable("concept2");

		// Store all the discovered concepts here.
        Set<Concept> concepts = new HashSet<Concept>();

		// memberOf
        LogicalExpression query = leFactory.createMemberShipMolecule(instance, concept1);

        Set<Map<Variable, Term>> bindings = internalExecuteQuery(query);
        extractConcepts( bindings, concept1, concepts );
        
        // subConceptOf
        query = leFactory.createSubConceptMolecule(concept1, concept2);
        
        bindings = internalExecuteQuery(query);
        extractConcepts( bindings, concept1, concepts );
        extractConcepts( bindings, concept2, concepts );
        
        // ofType
        query = leFactory.createAttributeConstraint(concept1, instance, concept2);
        
        bindings = internalExecuteQuery(query);
        
        extractConcepts( bindings, concept1, concepts );
        extractConcepts( bindings, concept2, concepts );

        // impliesType
        query = leFactory.createAttributeInference(concept1, instance, concept2);
        
        bindings = internalExecuteQuery(query);
        
        extractConcepts( bindings, concept1, concepts );
        extractConcepts( bindings, concept2, concepts );
        
        return concepts;
        /*
        This has been commented out, because concepts can also be instances of concepts,
        e.g. A can be a concept and also a member-of B
        
        In other words, this filtering should not be done.
        
        Set<Instance> tmpInst = getAllInstances();
        Set<IRI> tmpAttr = getAllAttributes();
        Set<Concept> tmpConc = new HashSet<Concept>(concepts);
        
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
        */
	}

	public Set<Instance> getAllInstances() {
		// build membership query:
		Term instanceVariable = leFactory.createVariable("x");
		Term variable = leFactory.createVariable("y");
		Term attributeVariable = leFactory.createVariable("z");
		LogicalExpression query = leFactory.createMemberShipMolecule(
				instanceVariable, variable);
		
		// submit query to reasoner:
        Set<Map<Variable, Term>> bindings = internalExecuteQuery(query);
		
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
        bindings = internalExecuteQuery(query);
        
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

	public Set<IRI> getAllAttributes() {
		// build query for extracting constraining attributes:
		Term instanceVariable = leFactory.createVariable("x");
		Term attributeVariable = leFactory.createVariable("y");
		Term conceptVariable = leFactory.createVariable("z");
		LogicalExpression query = leFactory.createAttributeConstraint(
				instanceVariable, attributeVariable, conceptVariable);
		
		// submit query to reasoner:
        Set<Map<Variable, Term>> bindings = internalExecuteQuery(query);
		
		// extract instances from result:
		Set<IRI> attributes = new HashSet<IRI>();
		for (Map<Variable, Term> binding : bindings) {
			IRI attributeID = (IRI) binding.get(leFactory.createVariable("y"));
			attributes.add(attributeID);
		}
		
		// build new query for inferring attributes:
		query = leFactory.createAttributeInference(
				instanceVariable, attributeVariable, conceptVariable);
		
		// submit query to reasoner:
        bindings = internalExecuteQuery(query);
		
		// extract instances from result:
		for (Map<Variable, Term> binding : bindings) {
			IRI attributeID = (IRI) binding.get(leFactory.createVariable("y"));
			attributes.add(attributeID);
		}
		
		// build new query for attribute which have attribute values:
		query = leFactory.createAttributeValue(
				instanceVariable, attributeVariable, conceptVariable);
		
		// submit query to reasoner:
        bindings = internalExecuteQuery(query);
		
		// extract instances from result:
		for (Map<Variable, Term> binding : bindings) {
			IRI attributeID = (IRI) binding.get(leFactory.createVariable("y"));
			attributes.add(attributeID);
		}
		return attributes;
	}

	public Set<IRI> getAllConstraintAttributes() {
		// build query:
		Term instanceVariable = leFactory.createVariable("x");
		Term attributeVariable = leFactory.createVariable("y");
		Term conceptVariable = leFactory.createVariable("z");
		LogicalExpression query = leFactory.createAttributeConstraint(
				instanceVariable, attributeVariable, conceptVariable);
		
		// submit query to reasoner:
        Set<Map<Variable, Term>> bindings = internalExecuteQuery(query);
		
		// extract instances from result:
		Set<IRI> attributes = new HashSet<IRI>();
		for (Map<Variable, Term> binding : bindings) {
			IRI attributeID = (IRI) binding.get(leFactory.createVariable("y"));
			attributes.add(attributeID);
		}
		return attributes;
	}

	public Set<IRI> getAllInferenceAttributes() {
		// build query:
		Term instanceVariable = leFactory.createVariable("x");
		Term attributeVariable = leFactory.createVariable("y");
		Term conceptVariable = leFactory.createVariable("z");
		LogicalExpression query = leFactory.createAttributeInference(
				instanceVariable, attributeVariable, conceptVariable);
		
		// submit query to reasoner:
        Set<Map<Variable, Term>> bindings = internalExecuteQuery(query);
		
		// extract instances from result:
		Set<IRI> attributes = new HashSet<IRI>();
		for (Map<Variable, Term> binding : bindings) {
			IRI attributeID = (IRI) binding.get(leFactory.createVariable("y"));
			attributes.add(attributeID);
		}
		return attributes;
	}

	public Set<Concept> getEquivalentConcepts(Concept concept) {
		throw new UnsupportedOperationException(
				"Equivalence queries are not supported in Datalog");
	}

	public boolean isEquivalentConcept(Concept concept1, 
			Concept concept2) {
		throw new UnsupportedOperationException(
				"Equivalence queries are not supported in Datalog");
	}

	public Set<Concept> getDirectConcepts(Instance instance) {
        // build query:
        Term instanceID = instance.getIdentifier();
        LogicalExpression query = null;
        Set<Map<Variable, Term>> bindings;
		try {
			query = leFactory.createLogicalExpression("_\"" + 
					org.wsml.reasoner.WSML2DatalogTransformer.PRED_DIRECT_CONCEPT + 
					"\"(_\"" + instanceID.toString() + "\", ?x)");
			// submit query to reasoner:
            bindings = internalExecuteQuery(query);
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

	public Set<Concept> getConceptsOf(Identifier attributeId) {
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
        Set<Map<Variable, Term>> bindings = internalExecuteQuery(query);

        // extract concepts from result:
        Set<Concept> concepts = new HashSet<Concept>();
        for (Map<Variable, Term> binding : bindings) {
            IRI conceptID = (IRI) binding.get(leFactory.createVariable("z"));
            concepts.add(wsmoFactory.getConcept(conceptID));
        }
        return concepts;
	}

	
	public Set<IRI> getSubRelations(Identifier attributeId) {
		throw new UnsupportedOperationException(
		"This method is not implemented for Datalog");
	}
	
	public Set<IRI> getDirectSubRelations(Identifier attributeId) {
		throw new UnsupportedOperationException(
				"This method is not implemented for Datalog");
	}

	
	public Set<IRI> getSuperRelations(Identifier attributeId) {
		throw new UnsupportedOperationException(
				"This method is not implemented for Datalog");
	}
	
	public Set<IRI> getDirectSuperRelations(Identifier attributeId) {
		throw new UnsupportedOperationException(
				"This method is not implemented for Datalog");
	}

	public Set<IRI> getEquivalentRelations(Identifier attributeId) {
		throw new UnsupportedOperationException(
				"Equivalence queries are not supported in Datalog");
	}

	public Set<IRI> getInverseRelations(Identifier attributeId) {
		throw new UnsupportedOperationException(
		"Queries for inverse relations are not supported in Datalog");
	}
	
	public Set<IRI> getRangesOfInferingAttribute(Identifier attributeId) {
        // build query:
		Term instanceVariable = leFactory.createVariable("x");
		Term valueVariable = leFactory.createVariable("y");
        LogicalExpression query = leFactory.createAttributeInference(
        		instanceVariable, attributeId, valueVariable);

        // submit query to reasoner:
        Set<Map<Variable, Term>> bindings = internalExecuteQuery(query);

        // extract iris from result:
        Set<IRI> iris = new HashSet<IRI>();
        for (Map<Variable, Term> binding : bindings) {
            IRI id = (IRI) binding.get(leFactory.createVariable("y"));
            iris.add(id);
        }
        return iris;
	}

	public Set<IRI> getRangesOfConstraintAttribute(Identifier attributeId) {
        // build query:
		Term instanceVariable = leFactory.createVariable("x");
		Term valueVariable = leFactory.createVariable("y");
        LogicalExpression query = leFactory.createAttributeConstraint(
        		instanceVariable, attributeId, valueVariable);

        // submit query to reasoner:
        Set<Map<Variable, Term>> bindings = internalExecuteQuery(query);

        // extract iris from result:
        Set<IRI> iris = new HashSet<IRI>();
        for (Map<Variable, Term> binding : bindings) {
            IRI id = (IRI) binding.get(leFactory.createVariable("y"));
            iris.add(id);
        }
        return iris;
	}

	public Map<IRI, Set<Term>> getInferingAttributeValues(Instance instance) {
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
        Set<Map<Variable, Term>> bindings = internalExecuteQuery(query);

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

	public Map<IRI, Set<Term>> getConstraintAttributeValues(Instance instance) {
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
        Set<Map<Variable, Term>> bindings = internalExecuteQuery(query);

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

	public Map<Instance, Set<Term>> getInferingAttributeInstances(Identifier attributeId) {
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
        Set<Map<Variable, Term>> bindings = internalExecuteQuery(query);

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

	public Map<Instance, Set<Term>> getConstraintAttributeInstances(Identifier attributeId) {
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
        Set<Map<Variable, Term>> bindings = internalExecuteQuery(query);

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

	public Set getInferingAttributeValues(Instance subject, Identifier attributeId) {
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
        Set<Map<Variable, Term>> bindings = internalExecuteQuery(query);

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

	public Set<String> getConstraintAttributeValues(Instance subject, Identifier attributeId) {
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
        Set<Map<Variable, Term>> bindings = internalExecuteQuery(query);

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

	public Set<ConsistencyViolation> checkConsistency(IRI ontologyID) {
		return checkConsistency();
	}

	public boolean checkQueryContainment(LogicalExpression query1,
			LogicalExpression query2, IRI ontologyID) {
		return checkQueryContainment(query1, query2);
	}

	public void deRegisterOntology(IRI ontologyID) {
		deRegister();
	}

	public void deRegisterOntology(Set<IRI> ontologyIDs) {
		deRegister();
	}

	public boolean entails(IRI ontologyID, LogicalExpression expression) {
		return entails(expression);
	}

	public boolean entails(IRI ontologyID, Set<LogicalExpression> expressions) {
		return entails(expressions);
	}

	public boolean executeGroundQuery(IRI ontologyID, LogicalExpression query) {
		return executeGroundQuery(query);
	}

	public Set<Map<Variable, Term>> executeQuery(IRI ontologyID,
			LogicalExpression query) {
		return executeQuery(query);
	}

	public Set<IRI> getAllAttributes(IRI ontologyID) {
		return getAllAttributes();
	}

	public Set<Concept> getAllConcepts(IRI ontologyID) {
		return getAllConcepts();
	}

	public Set<IRI> getAllConstraintAttributes(IRI ontologyID) {
		return getAllConstraintAttributes();
	}

	public Set<IRI> getAllInferenceAttributes(IRI ontologyID) {
		return getAllInferenceAttributes();
	}

	public Set<Instance> getAllInstances(IRI ontologyID) {
		return getAllInstances();
	}

	public Set<Concept> getConcepts(IRI ontologyID, Instance instance) {
		return getConcepts(instance);
	}

	public Set<Concept> getConceptsOf(IRI ontologyID, Identifier attributeId) {
		return getConceptsOf(attributeId);
	}

	public Map<Instance, Set<Term>> getConstraintAttributeInstances(
			IRI ontologyID, Identifier attributeId) {
		return getConstraintAttributeInstances(attributeId);
	}

	public Set getConstraintAttributeValues(IRI ontologyID, Instance subject,
			Identifier attributeId) {
		return getConstraintAttributeValues(subject, attributeId);
	}

	public Map<IRI, Set<Term>> getConstraintAttributeValues(IRI ontologyID,
			Instance instance) {
		return getConstraintAttributeValues(instance);
	}

	public Set<Concept> getDirectConcepts(IRI ontologyID, Instance instance) {
		return getDirectConcepts(instance);
	}

	public Set<Concept> getDirectSubConcepts(IRI ontologyID, Concept concept) {
		return getDirectSubConcepts(concept);
	}

	public Set<IRI> getDirectSubRelations(IRI ontologyID, Identifier attributeId) {
		return getDirectSubRelations(attributeId);
	}

	public Set<Concept> getDirectSuperConcepts(IRI ontologyID, Concept concept) {
		return getDirectSuperConcepts(concept);
	}

	public Set<IRI> getDirectSuperRelations(IRI ontologyID,
			Identifier attributeId) {
		return getDirectSuperRelations(attributeId);
	}

	public Set<Concept> getEquivalentConcepts(IRI ontologyID, Concept concept) {
		return getEquivalentConcepts(concept);
	}

	public Set<IRI> getEquivalentRelations(IRI ontologyID,
			Identifier attributeId) {
		return getEquivalentRelations(attributeId);
	}

	public Map<Instance, Set<Term>> getInferingAttributeInstances(
			IRI ontologyID, Identifier attributeId) {
		return getInferingAttributeInstances(attributeId);
	}

	public Set getInferingAttributeValues(IRI ontologyID, Instance subject,
			Identifier attributeId) {
		return getInferingAttributeValues(subject, attributeId);
	}

	public Map<IRI, Set<Term>> getInferingAttributeValues(IRI ontologyID,
			Instance instance) {
		return getInferingAttributeValues(instance);
	}

	public Set<Instance> getInstances(IRI ontologyID, Concept concept) {
		return getInstances(concept);
	}

	public Set<IRI> getInverseRelations(IRI ontologyID, Identifier attributeId) {
		return getInverseRelations(attributeId);
	}

	public Set<Map<Variable, Term>> getQueryContainment(
			LogicalExpression query1, LogicalExpression query2, IRI ontologyID) {
		return getQueryContainment(query1, query2);
	}

	public Set<IRI> getRangesOfConstraintAttribute(IRI ontologyID,
			Identifier attributeId) {
		return getRangesOfConstraintAttribute(attributeId);
	}

	public Set<IRI> getRangesOfInferingAttribute(IRI ontologyID,
			Identifier attributeId) {
		return getRangesOfInferingAttribute(attributeId);
	}

	public Set<Concept> getSubConcepts(IRI ontologyID, Concept concept) {
		return getSubConcepts(concept);
	}

	public Set<IRI> getSubRelations(IRI ontologyID, Identifier attributeId) {
		return getSubRelations(attributeId);
	}

	public Set<Concept> getSuperConcepts(IRI ontologyID, Concept concept) {
		return getSubConcepts(concept);
	}

	public Set<IRI> getSuperRelations(IRI ontologyID, Identifier attributeId) {
		return getSuperRelations(attributeId);
	}

	public boolean isEquivalentConcept(IRI ontologyID, Concept concept1,
			Concept concept2) {
		return isEquivalentConcept(concept1, concept2);
	}

	public boolean isMemberOf(IRI ontologyID, Instance instance, Concept concept) {
		return isMemberOf(instance, concept);
	}

	public boolean isSatisfiable(IRI ontologyID) {
		return isSatisfiable();
	}

	public boolean isSubConceptOf(IRI ontologyID, Concept subConcept,
			Concept superConcept) {
		return isSubConceptOf(subConcept, superConcept);
	}
	
}