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
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.deri.wsmo4j.logicalexpression.ConstantTransformer;
import org.omwg.logicalexpression.Atom;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.terms.ConstructedTerm;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Attribute;
import org.omwg.ontology.Axiom;
import org.omwg.ontology.Concept;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Type;
import org.omwg.ontology.Variable;
import org.sti2.wsmo4j.merger.Merger;
import org.wsml.reasoner.ConjunctiveQuery;
import org.wsml.reasoner.DatalogException;
import org.wsml.reasoner.DatalogReasonerFacade;
import org.wsml.reasoner.ExternalToolException;
import org.wsml.reasoner.Literal;
import org.wsml.reasoner.Rule;
import org.wsml.reasoner.api.LPReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory.BuiltInReasoner;
import org.wsml.reasoner.api.exception.InternalReasonerException;
import org.wsml.reasoner.api.inconsistency.AttributeTypeViolation;
import org.wsml.reasoner.api.inconsistency.ConsistencyViolation;
import org.wsml.reasoner.api.inconsistency.InconsistencyException;
import org.wsml.reasoner.api.inconsistency.MaxCardinalityViolation;
import org.wsml.reasoner.api.inconsistency.MinCardinalityViolation;
import org.wsml.reasoner.api.inconsistency.NamedUserConstraintViolation;
import org.wsml.reasoner.api.inconsistency.UnNamedUserConstraintViolation;
import org.wsml.reasoner.builtin.iris.IrisSLDNFFacade;
import org.wsml.reasoner.builtin.iris.IrisStratifiedFacade;
import org.wsml.reasoner.builtin.iris.IrisWellFoundedFacade;
import org.wsml.reasoner.transformation.AnonymousIdUtils;
import org.wsml.reasoner.transformation.AxiomatizationNormalizer;
import org.wsml.reasoner.transformation.ConstraintReplacementNormalizer;
import org.wsml.reasoner.transformation.ConstructReductionNormalizer;
import org.wsml.reasoner.transformation.InverseImplicationNormalizer;
import org.wsml.reasoner.transformation.LloydToporNormalizer;
import org.wsml.reasoner.transformation.OntologyNormalizer;
import org.wsml.reasoner.transformation.le.LogicalExpressionNormalizer;
import org.wsml.reasoner.transformation.le.LogicalExpressionTransformer;
import org.wsml.reasoner.transformation.le.OnePassReplacementNormalizer;
import org.wsml.reasoner.transformation.le.TopDownLESplitter;
import org.wsml.reasoner.transformation.le.lloydtopor.LloydToporRules;
import org.wsml.reasoner.transformation.le.moleculedecomposition.MoleculeDecompositionRules;
import org.wsmo.common.Entity;
import org.wsmo.common.IRI;
import org.wsmo.common.Identifier;
import org.wsmo.common.ImportedOntologiesBlock;
import org.wsmo.common.exception.InvalidModelException;
import org.wsmo.factory.FactoryContainer;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;

import com.ontotext.wsmo4j.ontology.AttributeImpl;

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
public class DatalogBasedWSMLReasoner implements LPReasoner {

    protected final static String WSML_RESULT_PREDICATE = "http://www.wsmo.org/reasoner/" + "wsml_query_result";

    protected final org.wsml.reasoner.DatalogReasonerFacade builtInFacade;

    protected WsmoFactory wsmoFactory;
    protected LogicalExpressionFactory leFactory;
    protected FactoryContainer factory;

    private int allowImports = 0;
    private boolean disableConsitencyCheck = false;
    
    private long normTime = -1;
    private long convTime = -1;
    private long consTime = -1;

    private Set<Map<Variable, Term>> queryContainmentResult = null;

    /**
     * Constructs a new Reasoner.
     * 
     * @param builtInType
     *            the underlying reasoner to use
     * @param factory
     *            the wsmo4j factory to use
     * @param config
     *            additional configuration for the facade
     * @throws IllegalArgumentException
     *             if the <code>builtInType</code> is <code>null</code>
     * @throws IllegalArgumentException
     *             if the wsml4j manager is <code>null</code>
     */
    public DatalogBasedWSMLReasoner(final BuiltInReasoner builtInType, final FactoryContainer factory, final Map<String, Object> config) {
        if (builtInType == null) {
            throw new IllegalArgumentException("The facade type must not be null");
        }
        if (factory == null) {
            throw new IllegalArgumentException("The WSMO4JManager must not be null");
        }

        this.builtInFacade = createFacade(builtInType, factory, config);
        this.factory = factory;
        this.wsmoFactory = factory.getWsmoFactory();
        this.leFactory = factory.getLogicalExpressionFactory();
    }

    /**
     * Instantiates a new facade using reflection. The facade must have a
     * constructor taking a <code>WSMO4JManager<code>.
     * @param className the class of the facade
     * @param factory the wsmo4j factory to pass to the constructor
     * @param config the additional configuration for the facade
     * @return the newly instantiated facade
     * @throws InternalReasonerException if something went wrong while 
     * instantiating the reasoner
     */
    private DatalogReasonerFacade createFacade(BuiltInReasoner builtInType, FactoryContainer factory, Map<String, Object> config) throws InternalReasonerException {
        assert factory != null : "The manager must not be null";

        switch( builtInType )
        {
        case IRIS_STRATIFIED:
        	return new IrisStratifiedFacade( factory, config );
        case IRIS_WELL_FOUNDED:
        	return new IrisWellFoundedFacade( factory, config );
        case IRIS_SLDNF:
        	return new IrisSLDNFFacade( factory, config );
        }
        
        throw new InternalReasonerException( "An built-in reasoner could not be instantiated. " +
        				"Perhaps you have selected a built-in reasoner that does not support logic programming?" );
    }

    public void setDisableConsitencyCheck(boolean check) {
        this.disableConsitencyCheck = check;
    }

    public void setAllowImports(int allowOntoImports) {
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

    protected Set<org.wsml.reasoner.Rule> convertEntities(Set<Entity> theEntities) {
        Set<org.wsml.reasoner.Rule> p = new HashSet<org.wsml.reasoner.Rule>();

        long normTime_start = System.currentTimeMillis();

        // We don't do this any more.
//        Set<Entity> entities = handleAttributeInheritance(theEntities);
        Set<Entity> entities = theEntities;

        // Convert conceptual syntax to logical expressions
        OntologyNormalizer normalizer = new AxiomatizationNormalizer(factory);
        entities = normalizer.normalizeEntities(entities);

        Set<Axiom> axioms = new HashSet<Axiom>();
        for (Entity e : entities) {
            if (e instanceof Axiom) {
                axioms.add((Axiom) e);
            }
        }

        // Convert constraints to support debugging
        normalizer = new ConstraintReplacementNormalizer(factory);
        axioms = normalizer.normalizeAxioms(axioms);

        // Simplify axioms
        normalizer = new ConstructReductionNormalizer(factory);
        axioms = normalizer.normalizeAxioms(axioms);
        
        // Apply InverseImplicationTransformation (wsml-rule)
        normalizer = new InverseImplicationNormalizer(factory);
        axioms = normalizer.normalizeAxioms(axioms);
        
        // Apply Lloyd-Topor rules to get Datalog-compatible LEs
        normalizer = new LloydToporNormalizer(factory);
        axioms = normalizer.normalizeAxioms(axioms);
        
        org.wsml.reasoner.WSML2DatalogTransformer wsml2datalog = new org.wsml.reasoner.WSML2DatalogTransformer(factory);
        Set<org.omwg.logicalexpression.LogicalExpression> lExprs = new LinkedHashSet<org.omwg.logicalexpression.LogicalExpression>();
        for (Axiom a : axioms) {
            lExprs.addAll(a.listDefinitions());
        }

        p = wsml2datalog.transform(lExprs);
        p.addAll(wsml2datalog.generateAuxilliaryRules());

        long normTime_end = System.currentTimeMillis();
        normTime = normTime_end - normTime_start;
        return p;
    }
    
    /*
     * Helper method for handleAttributeInheritance()
     * See the note before that method.
     */
//    private void inheritAttributesToSubConcepts( Concept concept )
//    {
//        Set<Attribute> attributes = concept.listAttributes();
//        
//        for (Concept subConcept : concept.listSubConcepts()) {
//            for (Attribute a : attributes) {
//                try {
//                	Attribute attribute = subConcept.createAttribute(a.getIdentifier());
//                    // process range types:
//                    for (Type type : a.listTypes()) {
//                        attribute.addType(type);
//                        // create an appropriate molecule per range
//                        // type:
//                        if (a.isConstraining()) {
//                            attribute.setConstraining(true);
//                        }
//                    }
//
//                    // process attribute properties:
//                    if (a.isReflexive()) {
//                        attribute.setReflexive(true);
//                    }
//                    if (a.isSymmetric()) {
//                        attribute.setSymmetric(true);
//                    }
//                    if (a.isTransitive()) {
//                        attribute.setTransitive(true);
//                    }
//                    Identifier inverseAttribute = a.getInverseOf();
//                    if (inverseAttribute != null) {
//                        attribute.setInverseOf(inverseAttribute);
//                    }
//
//                    // process cardinality constraints:
//                    if (a.getMinCardinality() > 0) {
//                        attribute.setMinCardinality(a.getMinCardinality());
//                    }
//                    if (a.getMaxCardinality() < Integer.MAX_VALUE) {
//                        attribute.setMaxCardinality(a.getMaxCardinality());
//                    }
//                }
//                catch (InvalidModelException ex) {
//                    throw new RuntimeException( "An error occurred while propogating attributes to sub-concepts", ex );
//                }
//            }
//        }
//
//        // Now recurse all the way down to concepts that don't have any sub-concepts
//        for (Concept subConcept : concept.listSubConcepts()) {
//        	inheritAttributesToSubConcepts( subConcept );
//        }
//    }
    /*
     * Add all attributes of superconcepts to the subconcepts
     * This is a BAD way to do it! 
     * This is now accomplished with two new rules in WSML2DatalogTransformer,
     * but in case these new rules cause some problems this code will be left
     * for a little while.
     */
//    private Set<Entity> handleAttributeInheritance(Set<Entity> theEntities) {
//        Set<Entity> result = new HashSet<Entity>();
//
//        for (Entity e : theEntities) {
//            if (e instanceof Concept) {
//                Concept concept = (Concept) e;
//                // If this is a base concept, then push attributes
//                if( concept.listSuperConcepts().size() == 0 )
//                	inheritAttributesToSubConcepts( concept );
//            }
//            result.add(e);
//        }
//        return result;
//    }

    public void deRegister() {
        try {
            builtInFacade.deregister();
        }
        catch (org.wsml.reasoner.ExternalToolException e) {
            e.printStackTrace();
        }
    }

    public Set<Map<Variable, Term>> executeQuery(LogicalExpression query) {
        return internalExecuteQuery(query);
    }

    public boolean checkQueryContainment(LogicalExpression query1, LogicalExpression query2) {
        QueryContainmentHelper helper = new QueryContainmentHelper();
        query1.accept(helper);
        query2.accept(helper);

        Set<org.wsml.reasoner.ConjunctiveQuery> datalogQueries1 = convertQuery(query1);
        Set<org.wsml.reasoner.ConjunctiveQuery> datalogQueries2 = convertQuery(query2);

        boolean result = false;
        for (ConjunctiveQuery datalogQuery1 : datalogQueries1) {
            for (ConjunctiveQuery datalogQuery2 : datalogQueries2) {
                result = builtInFacade.checkQueryContainment(datalogQuery1, datalogQuery2);
            }
        }
        return result;
    }

    public Set<Map<Variable, Term>> getQueryContainment(LogicalExpression query1, LogicalExpression query2) {
        QueryContainmentHelper helper = new QueryContainmentHelper();
        query1.accept(helper);
        query2.accept(helper);

        Set<org.wsml.reasoner.ConjunctiveQuery> datalogQueries1 = convertQuery(query1);
        Set<org.wsml.reasoner.ConjunctiveQuery> datalogQueries2 = convertQuery(query2);

        queryContainmentResult = new HashSet<Map<Variable, Term>>();
        for (ConjunctiveQuery datalogQuery1 : datalogQueries1) {
            for (ConjunctiveQuery datalogQuery2 : datalogQueries2) {
                try {
                    queryContainmentResult.addAll(builtInFacade.getQueryContainment(datalogQuery1, datalogQuery2));
                }
                catch (ExternalToolException e) {
                    throw new InternalReasonerException(e);
                }
            }
        }
        return queryContainmentResult;
    }

    public void registerOntology(Ontology ontology) throws InconsistencyException {
        Set<Ontology> ontologySingletonSet = new HashSet<Ontology>();
        ontologySingletonSet.add(ontology);
        registerOntologies(ontologySingletonSet);
    }

    public void registerOntologies(Set<Ontology> ontologies) throws InconsistencyException {
        if (allowImports == 0) {
            ontologies = getAllOntologies(ontologies);
        }
        
        Set<Entity> entities = new HashSet<Entity>();
        
        /* FIXME am,dw: error-prone since entities may be lost 
        for (Ontology o : ontologies) {
            entities.addAll(o.listConcepts());
            entities.addAll(o.listInstances());
            entities.addAll(o.listRelations());
            entities.addAll(o.listRelationInstances());
            entities.addAll(o.listAxioms());
        }
        */
        
        IRI newIdentifier = wsmoFactory.createIRI("urn:org.wsml.reasoner.impl.DatalogBasedWSMLReasoner:registeredOntology");

        Ontology mergedOntology;
		try {
			mergedOntology = Merger.merge(newIdentifier , ontologies);
		} catch (IllegalArgumentException e) {
			throw new InconsistencyException(e.getLocalizedMessage());
		} catch (InvalidModelException e) {
			throw new InconsistencyException(e.getLocalizedMessage());
		}

		entities.addAll(mergedOntology.listConcepts());
        entities.addAll(mergedOntology.listInstances());
        entities.addAll(mergedOntology.listRelations());
        entities.addAll(mergedOntology.listRelationInstances());
        entities.addAll(mergedOntology.listAxioms());
        
        registerEntities(entities);
    }

    public void registerEntities(Set<Entity> theEntities) throws InconsistencyException {
        registerEntitiesNoVerification(theEntities);
        if (!disableConsitencyCheck) {
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
        // ATTR_OFTYPE(instance, value, concept, attribute, violated_type)
        Variable i = leFactory.createVariable("i");
        Variable v = leFactory.createVariable("v");
        Variable c = leFactory.createVariable("c");
        Variable a = leFactory.createVariable("a");
        Variable t = leFactory.createVariable("t");

        IRI atomId = wsmoFactory.createIRI(ConstraintReplacementNormalizer.ATTR_OFTYPE_IRI);

        List<Term> params = new ArrayList<Term>(5);
        params.add(i);
        params.add(v);
        params.add(c);
        params.add(a);
        params.add(t);
        Atom atom = leFactory.createAtom(atomId, params);

        Set<Map<Variable, Term>> violations = executeQuery(atom);
        for (Map<Variable, Term> violation : violations) {
            Term rawValue = violation.get(v);
            Concept concept = wsmoFactory.createConcept((IRI) violation.get(c));
            
            Attribute attribute = findAttributeForError((IRI) violation.get(a), concept);
            Type type;
            IRI typeId = (IRI) violation.get(t);
            ConstantTransformer constTransformer = ConstantTransformer.getInstance();
            if (constTransformer.isDataType(typeId.toString())) {
            	type = factory.getXmlDataFactory().createDataType(typeId);
            }
            else{
                type = wsmoFactory.createConcept(typeId);
            }    
            Term instanceTerm = violation.get(i);
			if (instanceTerm instanceof Identifier) {
                errors.add(new AttributeTypeViolation(wsmoFactory.createInstance((IRI) instanceTerm), rawValue, attribute, type));
            }
            if (instanceTerm instanceof ConstructedTerm) {
                errors.add(new AttributeTypeViolation((ConstructedTerm) instanceTerm, rawValue, attribute, type));
            }
        }

    }

    private Attribute findAttributeForError(Identifier id, Concept concept) {
        Attribute attribute = concept.findAttribute(id);
        if (attribute == null){
        	attribute = new AttributeImpl(id, concept);
        }
        return attribute;
    }

	private void addMinCardinalityViolations(Set<ConsistencyViolation> errors) throws InvalidModelException {
        // MIN_CARD(instance, concept, attribute)
        Variable i = leFactory.createVariable("i");
        Variable c = leFactory.createVariable("c");
        Variable a = leFactory.createVariable("a");

        IRI atomId = wsmoFactory.createIRI(ConstraintReplacementNormalizer.MIN_CARD_IRI);

        List<Term> params = new ArrayList<Term>(3);
        params.add(i);
        params.add(c);
        params.add(a);
        Atom atom = leFactory.createAtom(atomId, params);

        Set<Map<Variable, Term>> violations = executeQuery(atom);
        for (Map<Variable, Term> violation : violations) {
            Concept concept = wsmoFactory.createConcept((Identifier) violation.get(c));
            Attribute attribute = findAttributeForError((Identifier) violation.get(a), concept);
            errors.add(new MinCardinalityViolation(violation.get(i), attribute, concept.getOntology()));
        }
    }

    private void addMaxCardinalityViolations(Set<ConsistencyViolation> errors) throws InvalidModelException {
        // MAX_CARD(instance, concept, attribute)
        Variable i = leFactory.createVariable("i");
        Variable c = leFactory.createVariable("c");
        Variable a = leFactory.createVariable("a");

        IRI atomId = wsmoFactory.createIRI(ConstraintReplacementNormalizer.MAX_CARD_IRI);

        List<Term> params = new ArrayList<Term>(3);
        params.add(i);
        params.add(c);
        params.add(a);
        Atom atom = leFactory.createAtom(atomId, params);

        Set<Map<Variable, Term>> violations = executeQuery(atom);
        for (Map<Variable, Term> violation : violations) {
            Concept concept = wsmoFactory.createConcept((IRI) violation.get(c));
            Attribute attribute = findAttributeForError((Identifier) violation.get(a), concept);
            errors.add(new MaxCardinalityViolation(violation.get(i), attribute, concept.getOntology()));
        }
    }

    private void addNamedUserViolations(Set<ConsistencyViolation> errors) throws InvalidModelException {
        // NAMED_USER(axiom)
        Variable i = leFactory.createVariable("i");

        IRI atomId = wsmoFactory.createIRI(ConstraintReplacementNormalizer.NAMED_USER_IRI);

        List<Term> params = new ArrayList<Term>(1);
        params.add(i);
        Atom atom = leFactory.createAtom(atomId, params);

        Set<Map<Variable, Term>> violations = executeQuery(atom);
        for (Map<Variable, Term> violation : violations) {
            Axiom axiom = null;
            String id = violation.get(i).toString();
            if (AnonymousIdUtils.isAnonymousIri(id)) {
                errors.add(new UnNamedUserConstraintViolation());
            }
            else {
                id = id.substring(0, id.indexOf(AnonymousIdUtils.NAMED_AXIOM_SUFFIX));
                IRI iri = wsmoFactory.createIRI(id);
                axiom = wsmoFactory.createAxiom(iri);
                errors.add(new NamedUserConstraintViolation(axiom));
            }
        }
    }

    private void addUnNamedUserViolations(Set<ConsistencyViolation> errors) throws InvalidModelException {
        // UNNAMED_USER(axiom)
        Variable i = leFactory.createVariable("i");

        IRI atomId = wsmoFactory.createIRI(ConstraintReplacementNormalizer.UNNAMED_USER_IRI);

        List<Term> params = new ArrayList<Term>(1);
        params.add(i);
        Atom atom = leFactory.createAtom(atomId, params);

        Set<Map<Variable, Term>> violations = executeQuery(atom);
        for (int k = 0; k < violations.size(); k++) {
            errors.add(new UnNamedUserConstraintViolation());
        }
    }

    protected Set<Map<Variable, Term>> internalExecuteQuery(LogicalExpression query) {
        Set<org.wsml.reasoner.ConjunctiveQuery> datalogQueries = convertQuery(query);
        Set<Map<Variable, Term>> result = new HashSet<Map<Variable, Term>>();
        for (ConjunctiveQuery datalogQuery : datalogQueries) {
            try {
                result.addAll(builtInFacade.evaluate(datalogQuery));
            }
            catch (DatalogException e) {
                throw new InternalReasonerException(e);
            }
            catch (ExternalToolException e) {
                throw new InternalReasonerException(e);
            }

        }
        return result;
    }

    protected Set<org.wsml.reasoner.ConjunctiveQuery> convertQuery(org.omwg.logicalexpression.LogicalExpression q) {
        org.wsml.reasoner.WSML2DatalogTransformer wsml2datalog = new org.wsml.reasoner.WSML2DatalogTransformer(factory);

        List<Term> params = new LinkedList<Term>();
        LogicalExpressionVariableVisitor varVisitor = new LogicalExpressionVariableVisitor();
        q.accept(varVisitor);
        Set<Variable> freeVariables = varVisitor.getFreeVariables(q);
		if (freeVariables != null) {
			params.addAll(freeVariables);
		}
        Atom rHead = leFactory.createAtom(wsmoFactory.createIRI(WSML_RESULT_PREDICATE), params);

        LogicalExpressionNormalizer moleculeNormalizer = new OnePassReplacementNormalizer(new MoleculeDecompositionRules(factory).getRules(), factory);
        q = moleculeNormalizer.normalize(q);

        org.omwg.logicalexpression.LogicalExpression resultDefRule = leFactory.createInverseImplication(rHead, q);

        LloydToporRules lloydToporRules = new LloydToporRules(factory);
        LogicalExpressionTransformer lloydToporNormalizer = new TopDownLESplitter(lloydToporRules.getRules());
        Set<LogicalExpression> conjunctiveQueries = lloydToporNormalizer.transform(resultDefRule);

        Set<Rule> p = new HashSet<Rule>();

        for (LogicalExpression query : conjunctiveQueries) {
            p.addAll(wsml2datalog.transform(query));
        }
        
        Set<ConjunctiveQuery> result = new HashSet<ConjunctiveQuery>();
        for (Rule rule : p) {
            if (rule.getHead().getPredicateUri().equals(WSML_RESULT_PREDICATE)) {
                List<Literal> body = new LinkedList<Literal>();
                for (Literal l : rule.getBody()) {
                    body.add(l);
                }
                result.add(new org.wsml.reasoner.ConjunctiveQuery(body));
            }
            else {
                // TODO we need to add these rules to the datalog programm
                // and remove all of them after the query evaluation is
                // finished!

                // problem wrt impl. right now> would require to change the
                // DATALOG PROGRAM
                // for each query! which is bad if one precomputes the model for
                // a program
                // an materializes the result, since the materialization needs to
                // be done all
                // the time again and again.
                // CurrentlY this does not fit to our reasoner implementation.

                // This means the following QUERY does not retrieve the correct
                // answer>
                // ?- ?x subConceptOf SomeConceptThatIsNOTintheOntology
                // as answers we would get the empty set, but should get one
                // tuple
                // (SomeConceptThatIsNOTintheOntology)

            }
        }
        return result;
    }

    public Set<ConsistencyViolation> checkConsistency() {
        Set<ConsistencyViolation> errors = new HashSet<ConsistencyViolation>();
        IRI violationIRI = wsmoFactory.createIRI(ConstraintReplacementNormalizer.VIOLATION_IRI);
        Atom violation = leFactory.createAtom(violationIRI, new ArrayList<Term>());
        if (executeQuery(violation).size() > 0) {
            try {
                addAttributeOfTypeViolations(errors);
                addMinCardinalityViolations(errors);
                addMaxCardinalityViolations(errors);
                addNamedUserViolations(errors);
                addUnNamedUserViolations(errors);
            }
            catch (InvalidModelException e) {
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
        }
        catch (org.wsml.reasoner.ExternalToolException e) {
            throw new IllegalArgumentException("This set of entities could not be registered with the built-in reasoner", e);
        }

        long convTime_end = System.currentTimeMillis();
        convTime = convTime_end - convTime_start;
    }

    private Set<Ontology> getAllOntologies(Set<Ontology> ontologies) {
        Set<Ontology> result = new HashSet<Ontology>();
        for (Ontology o : ontologies) {
            result.add(o);
            getAllOntologies(o, result);
        }
        return result;
    }

    private void getAllOntologies(Ontology o, Set<Ontology> ontologies) {
        ImportedOntologiesBlock importedOntologies = o.getImportedOntologies();
        if (importedOntologies != null)
		for (Ontology imported : importedOntologies.listOntologies()) {
            if (!ontologies.contains(imported)) {
                ontologies.add(imported);
                getAllOntologies(imported, ontologies);
            }
        }
    }

    public void registerOntologyNoVerification(Ontology ontology) {
        Set<Entity> entities = new HashSet<Entity>();
        entities.addAll(ontology.listConcepts());
        entities.addAll(ontology.listInstances());
        entities.addAll(ontology.listRelations());
        entities.addAll(ontology.listRelationInstances());
        entities.addAll(ontology.listAxioms());
        registerEntitiesNoVerification(entities);
    }

    public boolean ask(LogicalExpression query) {
    	return executeQuery( query ).size() != 0;
    }
}