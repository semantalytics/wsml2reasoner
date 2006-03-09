package org.wsml.reasoner.wsmx;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Concept;
import org.omwg.ontology.Instance;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.api.WSMLFlightReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.impl.DefaultWSMLReasonerFactory;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsmo.common.IRI;
import org.wsmo.execution.common.exception.ComponentException;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;

/**
 * The class WSMXReasoner provides a WSML reasoner to be plugged into the WSMX
 * environment. It is a wrapper class for reasoners of the type
 * org.wsml.reasoner.WSMLReasoner to which it delegates its method calls. To
 * conform to the WSMX environment, it implements the interface
 * org.wsmo.execution.common.component.WSMLReasoner. The reasoner supports the
 * WSML-Flight variant of the WSML language and allows for several kinds of
 * reasoning services on WSML ontologies, such as instance retrieval or
 * subsumption checking. To reason with ontoloy objects, they have to be
 * registered before any of the reasoning service methods is called.
 * 
 * @author Stephan Grimm, FZI Karlsruhe
 */
public class WSMXReasoner implements WSMXReasonerInterface// implements
                                                            // org.wsmo.execution.common.component.WSMLReasoner
{
    protected WSMLFlightReasoner reasoner;

    public WSMXReasoner() {
        // create reasoner:
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(WSMLReasonerFactory.PARAM_BUILT_IN_REASONER,
                WSMLReasonerFactory.BuiltInReasoner.KAON2);
        reasoner = DefaultWSMLReasonerFactory.getFactory()
                .getWSMLFlightReasoner(params);
    }

    /**
     * This method returns all instances i of a concept C in a given ontology,
     * such that "i memberOf C" holds in WSML terms. The ontology must have been
     * registered with the reasoner beforehand. (Since currently the return
     * value is set to Set&lt;Concept&gt; instead of Set&lt;Instance&gt;, new
     * concepts are created having the appropriate instance's identifiers
     * augmented by "_as_concept".)
     */
    public Set<Instance> getAllInstances(Concept concept, IRI ontology)
            throws ComponentException, UnsupportedOperationException {
        System.err
                .println("note that there is currently a misconceptualization in the method org.wsmo.execution.common.component.WSMLReasoner::getAllInstances() (wrong generic return type)!");

        return reasoner.getInstances((IRI) ontology, concept);
    }

    /**
     * This method returns all the subconcepts Csub of a given concept Csup in a
     * given ontology, such that "Csub subConceptOf Csup" holds in WSML terms.
     * The ontology must have been registered with the reasoner beforehand.
     */
    public Set<Concept> getAllSubconcepts(Concept concept, IRI ontology)
            throws ComponentException, UnsupportedOperationException {
        return reasoner.getSubConcepts((IRI) ontology, concept);
        /*
         * // build query: Variable xVariable = wsmoFactory.createVariable("x");
         * LogicalExpression query =
         * leFactory.createSubConceptMolecule(xVariable,
         * wsmoFactory.createIRI(concept.getIdentifier().toString()));
         *  // submit query to reasoner: QueryAnsweringRequest qaRequest = new
         * QueryAnsweringRequestImpl(ontology.toString(), query);
         * QueryAnsweringResult result =
         * (QueryAnsweringResult)reasoner.execute(qaRequest);
         *  // extract concepts out of result: Set<Concept> subconcepts = new
         * HashSet<Concept>(); for(VariableBinding vBinding : result) { String
         * subconceptID = vBinding.get("x"); Concept subconcept =
         * wsmoFactory.getConcept(wsmoFactory.createIRI(subconceptID));
         * subconcepts.add(subconcept); }
         * 
         * return subconcepts;
         */
    }

    /**
     * This method returns all the superconcepts Csup of a given concept Csub in
     * a given ontology, such that "Csub subConceptOf Csup" holds in WSML terms.
     * The ontology must have been registered with the reasoner beforehand.
     */
    public Set<Concept> getAllSuperconcepts(Concept concept, IRI ontology)
            throws ComponentException, UnsupportedOperationException {
        return reasoner.getSuperConcepts((IRI) ontology, concept);
        /*
         * // build query: Variable xVariable = wsmoFactory.createVariable("x");
         * Set<Term> varSet = new HashSet<Term>(); varSet.add(xVariable); Term
         * conceptID =
         * wsmoFactory.createIRI(concept.getIdentifier().toString());
         * 
         * LogicalExpression query =
         * leFactory.createSubConceptMolecule(conceptID, xVariable);
         *  // submit query to reasoner: QueryAnsweringRequest qaRequest = new
         * QueryAnsweringRequestImpl(ontology.toString(), query);
         * QueryAnsweringResult result =
         * (QueryAnsweringResult)reasoner.execute(qaRequest);
         *  // extract concepts out of result: Set<Concept> superconcepts = new
         * HashSet<Concept>(); for(VariableBinding vBinding : result) { String
         * superconceptID = vBinding.get("x"); Concept superconcept =
         * wsmoFactory.getConcept(wsmoFactory.createIRI(superconceptID));
         * superconcepts.add(superconcept); }
         * 
         * return superconcepts;
         */
    }

    /**
     * This method checks whether a given instance i is an instance of a given
     * concept C in a given ontology, i.e. "i memberOf C" holds in WSMl terms.
     * The ontology must have been registered with the reasoner beforehand.
     */
    public boolean isInstanceOf(Instance instance, Concept concept, IRI ontology)
            throws ComponentException, UnsupportedOperationException {
        return reasoner.isMemberOf((IRI) ontology, instance, concept);
        /*
         * // build query: Term conceptID =
         * wsmoFactory.createIRI(concept.getIdentifier().toString()); Term
         * instanceID =
         * wsmoFactory.createIRI(instance.getIdentifier().toString());
         * LogicalExpression query =
         * leFactory.createMemberShipMolecule(instanceID, conceptID);
         *  // submit query to reasoner: QueryAnsweringRequest qaRequest = new
         * QueryAnsweringRequestImpl(ontology.toString(), query);
         * QueryAnsweringResult result =
         * (QueryAnsweringResult)reasoner.execute(qaRequest);
         *  // check for non-empty result: return result.size() != 0;
         */
    }

    /**
     * This method registers a WSMO4J ontology with the resoner. To perform any
     * reasoning on an ontology it has to be registered first. After
     * registration, an ontology is referred to by its identifier.
     */
    public void register(Ontology ontology) throws ComponentException,
            UnsupportedOperationException {
        reasoner.registerOntology(ontology);
        /*
         * Set<Ontology> ontos = new HashSet<Ontology>(); ontos.add(ontology);
         * OntologyRegistrationRequest regReq = new
         * OntologyRegistrationRequestImpl(ontos); reasoner.execute(regReq);
         */
    }

    /**
     * This method checks whether a given concept Csup is a superconcept of a
     * given concept Csub in a given ontology, i.e. "Csub subConceptOf Csup"
     * holds in WSML terms. The ontology must have been registered with the
     * reasoner beforehand.
     */
    public boolean subsumes(Concept superConcept, Concept subConcept,
            IRI ontology) throws ComponentException,
            UnsupportedOperationException {
        return reasoner
                .isSubConceptOf((IRI) ontology, subConcept, superConcept);
        /*
         * // build query: Term superconceptID =
         * wsmoFactory.createIRI(superConcept.getIdentifier().toString()); Term
         * subconceptID =
         * wsmoFactory.createIRI(subConcept.getIdentifier().toString());
         * LogicalExpression query =
         * leFactory.createSubConceptMolecule(subconceptID, superconceptID);
         *  // submit query to reasoner: QueryAnsweringRequest qaRequest = new
         * QueryAnsweringRequestImpl(ontology.toString(), query);
         * QueryAnsweringResult result =
         * (QueryAnsweringResult)reasoner.execute(qaRequest);
         *  // check for non-empty result: return result.size() != 0;
         */
    }

    public void deRegister(IRI ontology) throws ComponentException,
            UnsupportedOperationException {
        reasoner.deRegisterOntology(ontology);
    }

    public void deRegister(Set<IRI> ontologies) throws ComponentException,
            UnsupportedOperationException {
        reasoner.deRegisterOntology(ontologies);
    }

    public void register(Set<Ontology> ontologies) throws ComponentException,
            UnsupportedOperationException {
        reasoner.registerOntology(ontologies);
    }

    public boolean entails(IRI ontologyID, LogicalExpression expression) {
        return reasoner.entails(ontologyID, expression);
    }

    public boolean entails(IRI ontologyID, Set<LogicalExpression> expressions) {
        return reasoner.entails(ontologyID, expressions);
    }

    public Set<Map<Variable, Term>> executeQuery(LogicalExpression query,
            IRI ontology) {
        return reasoner.executeQuery(ontology, query);
    }

    public boolean executeGroundQuery(LogicalExpression query, IRI ontology) {
        return reasoner.executeGroundQuery(ontology, query);
    }

    public boolean isSatisfiable(IRI ontologyID) {
        return reasoner.isSatisfiable(ontologyID);
    }
}
