package org.wsml.reasoner.wsmx;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.omwg.logicalexpression.LogicalExpression;
import org.wsmo.factory.LogicalExpressionFactory;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Variable;
import org.omwg.ontology.Concept;
import org.omwg.ontology.Instance;
import org.omwg.ontology.Ontology;
import org.wsml.reasoner.api.OntologyRegistrationRequest;
import org.wsml.reasoner.api.WSMLReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.api.queryanswering.QueryAnsweringRequest;
import org.wsml.reasoner.api.queryanswering.QueryAnsweringResult;
import org.wsml.reasoner.api.queryanswering.VariableBinding;
import org.wsml.reasoner.impl.DefaultWSMLReasonerFactory;
import org.wsml.reasoner.impl.OntologyRegistrationRequestImpl;
import org.wsml.reasoner.impl.QueryAnsweringRequestImpl;
import org.wsmo.common.Identifier;
import org.wsmo.execution.common.exception.ComponentException;
import org.wsmo.factory.Factory;
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
public class WSMXReasoner implements
        org.wsmo.execution.common.component.WSMLReasoner {
    protected WSMLReasoner reasoner;

    protected WsmoFactory wsmoFactory;

    protected LogicalExpressionFactory leFactory;

    public WSMXReasoner() {
        // create reasoner:
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(WSMLReasonerFactory.PARAM_WSML_VARIANT,
                WSMLReasonerFactory.WSMLVariant.WSML_CORE);
        params.put(WSMLReasonerFactory.PARAM_BUILT_IN_REASONER,
                WSMLReasonerFactory.BuiltInReasoner.KAON2);
        reasoner = DefaultWSMLReasonerFactory.getFactory().getWSMLReasoner(
                params);

        // create factories for wsmo objects and logical expressions:
        wsmoFactory = Factory.createWsmoFactory(null);
        params.clear();
        params.put(Factory.PROVIDER_CLASS,
                "org.deri.wsmo4j.logexpression.LogicalExpressionFactoryImpl");
        leFactory = (LogicalExpressionFactory) Factory
                .createLogicalExpressionFactory(params);
    }

    /**
     * This method returns all instances i of a concept C in a given ontology,
     * such that "i memberOf C" holds in WSML terms. The ontology must have been
     * registered with the reasoner beforehand. (Since currently the return
     * value is set to Set&lt;Concept&gt; instead of Set&lt;Instance&gt;, new
     * concepts are created having the appropriate instance's identifiers
     * augmented by "_as_concept".)
     */
    public Set<Concept> getAllInstances(Concept concept, Identifier ontology)
            throws ComponentException, UnsupportedOperationException {
        System.err
                .println("note that there is currently a misconceptualization in the method org.wsmo.execution.common.component.WSMLReasoner::getAllInstances() (wrong generic return type)!");

        // build query:
        Variable xVariable = wsmoFactory.createVariable("x");
        LogicalExpression query = leFactory.createMemberShipMolecule(xVariable,
                wsmoFactory.createIRI(concept.getIdentifier().toString()));

        // submit query to reasoner:
        QueryAnsweringRequest qaRequest = new QueryAnsweringRequestImpl(
                ontology.toString(), query);
        QueryAnsweringResult result = (QueryAnsweringResult) reasoner
                .execute(qaRequest);

        // extract instances out of result:
        Set<Instance> instances = new HashSet<Instance>();
        for (VariableBinding vBinding : result) {
            String instanceID = vBinding.get("x");
            Instance instance = wsmoFactory.getInstance(wsmoFactory
                    .createIRI(instanceID));
            instances.add(instance);
        }

        // copy set of instances to set of concepts (API-bug)
        Set<Concept> dummyConcepts = new HashSet<Concept>();
        for (Instance instance : instances) {
            Identifier dummyID = wsmoFactory.createIRI(instance.getIdentifier()
                    .toString()
                    + "_as_concept");
            dummyConcepts.add(wsmoFactory.createConcept(dummyID));
        }

        return dummyConcepts;
    }

    /**
     * This method returns all the subconcepts Csub of a given concept Csup in a
     * given ontology, such that "Csub subConceptOf Csup" holds in WSML terms.
     * The ontology must have been registered with the reasoner beforehand.
     */
    public Set<Concept> getAllSubconcepts(Concept concept, Identifier ontology)
            throws ComponentException, UnsupportedOperationException {
        // build query:
        Variable xVariable = wsmoFactory.createVariable("x");
        LogicalExpression query = leFactory.createSubConceptMolecule(xVariable,
                wsmoFactory.createIRI(concept.getIdentifier().toString()));

        // submit query to reasoner:
        QueryAnsweringRequest qaRequest = new QueryAnsweringRequestImpl(
                ontology.toString(), query);
        QueryAnsweringResult result = (QueryAnsweringResult) reasoner
                .execute(qaRequest);

        // extract concepts out of result:
        Set<Concept> subconcepts = new HashSet<Concept>();
        for (VariableBinding vBinding : result) {
            String subconceptID = vBinding.get("x");
            Concept subconcept = wsmoFactory.getConcept(wsmoFactory
                    .createIRI(subconceptID));
            subconcepts.add(subconcept);
        }

        return subconcepts;
    }

    /**
     * This method returns all the superconcepts Csup of a given concept Csub in
     * a given ontology, such that "Csub subConceptOf Csup" holds in WSML terms.
     * The ontology must have been registered with the reasoner beforehand.
     */
    public Set<Concept> getAllSuperconcepts(Concept concept, Identifier ontology)
            throws ComponentException, UnsupportedOperationException {
        // build query:
        Variable xVariable = wsmoFactory.createVariable("x");
        Set<Term> varSet = new HashSet<Term>();
        varSet.add(xVariable);
        Term conceptID = wsmoFactory.createIRI(concept.getIdentifier()
                .toString());

        LogicalExpression query = leFactory.createSubConceptMolecule(conceptID,
                xVariable);

        // submit query to reasoner:
        QueryAnsweringRequest qaRequest = new QueryAnsweringRequestImpl(
                ontology.toString(), query);
        QueryAnsweringResult result = (QueryAnsweringResult) reasoner
                .execute(qaRequest);

        // extract concepts out of result:
        Set<Concept> superconcepts = new HashSet<Concept>();
        for (VariableBinding vBinding : result) {
            String superconceptID = vBinding.get("x");
            Concept superconcept = wsmoFactory.getConcept(wsmoFactory
                    .createIRI(superconceptID));
            superconcepts.add(superconcept);
        }

        return superconcepts;
    }

    /**
     * This method checks whether a given instance i is an instance of a given
     * concept C in a given ontology, i.e. "i memberOf C" holds in WSMl terms.
     * The ontology must have been registered with the reasoner beforehand.
     */
    public boolean isInstanceOf(Instance instance, Concept concept,
            Identifier ontology) throws ComponentException,
            UnsupportedOperationException {
        // build query:
        Term conceptID = wsmoFactory.createIRI(concept.getIdentifier()
                .toString());
        Term instanceID = wsmoFactory.createIRI(instance.getIdentifier()
                .toString());
        LogicalExpression query = leFactory.createMemberShipMolecule(
                instanceID, conceptID);

        // submit query to reasoner:
        QueryAnsweringRequest qaRequest = new QueryAnsweringRequestImpl(
                ontology.toString(), query);
        QueryAnsweringResult result = (QueryAnsweringResult) reasoner
                .execute(qaRequest);

        // check for non-empty result:
        return result.size() != 0;
    }

    /**
     * This method registers a WSMO4J ontology with the resoner. To perform any
     * reasoning on an ontology it has to be registered first. After
     * registration, an ontology is referred to by its identifier.
     */
    public void register(Ontology ontology) throws ComponentException,
            UnsupportedOperationException {
        Set<Ontology> ontos = new HashSet<Ontology>();
        ontos.add(ontology);
        OntologyRegistrationRequest regReq = new OntologyRegistrationRequestImpl(
                ontos);
        reasoner.execute(regReq);
    }

    /**
     * This method checks whether a given concept Csup is a superconcept of a
     * given concept Csub in a given ontology, i.e. "Csub subConceptOf Csup"
     * holds in WSML terms. The ontology must have been registered with the
     * reasoner beforehand.
     */
    public boolean subsumes(Concept superConcept, Concept subConcept,
            Identifier ontology) throws ComponentException,
            UnsupportedOperationException {
        // build query:
        Term superconceptID = wsmoFactory.createIRI(superConcept
                .getIdentifier().toString());
        Term subconceptID = wsmoFactory.createIRI(subConcept.getIdentifier()
                .toString());
        LogicalExpression query = leFactory.createSubConceptMolecule(
                subconceptID, superconceptID);

        // submit query to reasoner:
        QueryAnsweringRequest qaRequest = new QueryAnsweringRequestImpl(
                ontology.toString(), query);
        QueryAnsweringResult result = (QueryAnsweringResult) reasoner
                .execute(qaRequest);

        // check for non-empty result:
        return result.size() != 0;
    }

    /**
     * This method allows to pose a conjunctive query to an ontology registered
     * with the reasoner. As a result it returns a set of bindings for the free
     * variables occurring in the query.
     */
    public QueryAnsweringResult retrieve(LogicalExpression query,
            Identifier ontology) {
        QueryAnsweringRequest request = new QueryAnsweringRequestImpl(ontology
                .toString(), query);
        return (QueryAnsweringResult) reasoner.execute(request);
    }

}
