package performance;


import java.io.*;
import java.text.DecimalFormat;
import java.util.Random;

import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.ontology.*;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsmo.common.*;
import org.wsmo.common.exception.InvalidModelException;
import org.wsmo.common.exception.SynchronisationException;
import org.wsmo.factory.*;
import org.wsmo.wsml.ParserException;
import org.wsmo.wsml.Serializer;

/**
 * Class which generates automatically WSML-Flight ontologies 
 * with different features and different expressivity grades 
 * for use in benchmarking tests.
 * 
 * @author Nathalie Steinmetz, DERI Innsbruck
 * 
 */
public class BenchmarkOntologyGenerator {
	
	private static WsmoFactory wsmoFactory = null; 
	
	private static LogicalExpressionFactory leFactory = null;
	
	private static DataFactory dataFactory = null;
	
	private static Serializer serializer = null;
	
	// default path defining the directory in which to store the ontologies
	private static String path = "performance/performance/ontologies/";
	
	// for each type of ontology (like e.g. subconcept ontology) ontologies 
	// with the following amounts of entities shall be created
	private static int[] amount = {1,10,100,500,1000,5000,10000};
	
	private static String SUBCONCEPT = "subconcept";
	
	private static String DEEP_SUBCONCEPT = "deepSubconcept";
	
	private static String INSTANCE = "instance";
	
	private static String INSTANCE_SUBCONCEPT = "instanceANDsubconcept";
	
	private static String INSTANCE_DEEP_SUBCONCEPT = "instanceANDdeepSubconcept";
	
	private static String OFTYPE = "ofType";
	
	private static String OFTYPE_SUBCONCEPT = "ofTypeANDsubconcept";
	
	private static String CARDINALITY_01 = "cardinality_0_1";
	
	private static String CARDINALITY_010 = "cardinality_0_10";
	
	private static String INVERSE = "inverseAttribute";
	
	private static String TRANSITIVE = "transitiveAttribute";
	
	private static String SYMMETRIC = "symmetricAttribute";
	
	private static String REFLEXIVE = "reflexiveAttribute";
	
	private static String LOC_STRAT_NEGATION = "locallyStratifiedNegation";
	
	private static String GLOB_STRAT_NEGATION = "globallyStratifiedNegation";
	
	private static String BUILTIN = "built_in";
	
	/**
	 * Generate ontologies containing simple hierarchy expressions like 
	 * e.g. 
	 * concept Human
	 * concept Man subConceptOf Human
	 * instance Marge memberOf Human
	 * instance Homer memberOf Man
	 * 
	 * Queries: 
	 * - ?x memberOf Human
	 * - ?x memberOf ?y
	 * 
	 * @throws IOException
	 * @throws SynchronisationException
	 * @throws InvalidModelException
	 */
	public void genSubconceptOntologies() 
			throws IOException, SynchronisationException, InvalidModelException {
		Ontology ontology = null;
		for (int i = 0; i < amount.length; i++) {	
			// create default namespace
			Namespace ns = wsmoFactory.createNamespace("", 
					wsmoFactory.createIRI(getNamespace(SUBCONCEPT, amount[i])));
			
			// create ontology
			ontology = createOntology(ns,amount[i]);
			
			// set ontology default namespace and variant
			ontology.setDefaultNamespace(ns.getIRI());
			ontology.setWsmlVariant(WSML.WSML_FLIGHT);
			
			// add initial elements
			Concept concept = wsmoFactory.createConcept(
					wsmoFactory.createIRI(ns, "c1"));
			ontology.addConcept(concept);
			
			// add amount[i] number of subconcept entities
			Concept subConcept = null;
			Concept superConcept = null;
			for (int j = 0; j < amount[i]; j++) {
				subConcept = wsmoFactory.createConcept(
						wsmoFactory.createIRI(ns, "c" + (j + 2)));
				superConcept = wsmoFactory.createConcept(
						wsmoFactory.createIRI(ns, "c1"));
				subConcept.addSuperConcept(superConcept);
				ontology.addConcept(subConcept);
			}
			
			// add instances
			Instance instance1 = wsmoFactory.createInstance(
					wsmoFactory.createIRI(ns, "i1"));
			instance1.addConcept(concept);
			Instance instance2 = wsmoFactory.createInstance(
					wsmoFactory.createIRI(ns, "i2"));
			instance2.addConcept(subConcept);
			ontology.addInstance(instance1);
			ontology.addInstance(instance2);
			
			// add queries in form of relationinstances (see TestPerformanceWithUseOfFeatures)
			Relation r1 = wsmoFactory.createRelation(wsmoFactory.createIRI(ns, "query1"));
			r1.createParameter((byte) 0);
			RelationInstance query1 = wsmoFactory.createRelationInstance(r1);
			query1.setParameterValue((byte) 0, dataFactory.createWsmlString("?x memberOf c1"));
			ontology.addRelationInstance(query1);
			Relation r2 = wsmoFactory.createRelation(wsmoFactory.createIRI(ns, "query2"));
			r2.createParameter((byte) 0);
			RelationInstance query2 = wsmoFactory.createRelationInstance(r2);
			query2.setParameterValue((byte) 0, dataFactory.createWsmlString("?x memberOf ?y"));
			ontology.addRelationInstance(query2);
			
			// write ontology file
			writeFile(SUBCONCEPT, getFileName(SUBCONCEPT, amount[i]), ontology);
		}
	}	
	
	/**
	 * Generate ontologies containing deep hierarchy expressions like 
	 * e.g. 
	 * concept Human
	 * concept Man subConceptOf Human
	 * concept Boy subConceptOf Man
	 * instance Marge memberOf Human
	 * instance Bart memberOf Boy
	 * 
	 * Queries: 
	 * - ?x memberOf Human
	 * - ?x memberOf ?y
	 * 
	 * @throws IOException
	 * @throws SynchronisationException
	 * @throws InvalidModelException
	 */
	public void genDeepSubconceptOntologies() 
			throws IOException, SynchronisationException, InvalidModelException {
		Ontology ontology = null;
		// reduce amounts of entities that shall be created for this ontology type
		int[] amount = {1,10,100,500,1000};
		for (int i = 0; i < amount.length; i++) {	
			// create default namespace
			Namespace ns = wsmoFactory.createNamespace("", 
					wsmoFactory.createIRI(getNamespace(DEEP_SUBCONCEPT, amount[i])));
			
			// create ontology
			ontology = createOntology(ns,amount[i]);
			
			// set ontology default namespace and variant
			ontology.setDefaultNamespace(ns.getIRI());
			ontology.setWsmlVariant(WSML.WSML_FLIGHT);
			
			// add initial elements
			Concept concept = wsmoFactory.createConcept(
					wsmoFactory.createIRI(ns, "c1"));
			ontology.addConcept(concept);
			
			// add amount[i] number of subconcept entities
			Concept subConcept = null;
			Concept superConcept = null;
			for (int j = 0; j < amount[i]; j++) {
				subConcept = wsmoFactory.createConcept(
						wsmoFactory.createIRI(ns, "c" + (j + 2)));
				superConcept = wsmoFactory.createConcept(
						wsmoFactory.createIRI(ns, "c" + (j + 1)));
				subConcept.addSuperConcept(superConcept);
				ontology.addConcept(subConcept);
			}
			
			// add instances
			Instance instance1 = wsmoFactory.createInstance(
					wsmoFactory.createIRI(ns, "i1"));
			instance1.addConcept(concept);
			Instance instance2 = wsmoFactory.createInstance(
					wsmoFactory.createIRI(ns, "i2"));
			instance2.addConcept(subConcept);
			ontology.addInstance(instance1);
			ontology.addInstance(instance2);
			
			// add queries in form of relationinstances (see TestPerformanceWithUseOfFeatures)
			Relation r1 = wsmoFactory.createRelation(wsmoFactory.createIRI(ns, "query1"));
			r1.createParameter((byte) 0);
			RelationInstance query1 = wsmoFactory.createRelationInstance(r1);
			query1.setParameterValue((byte) 0, dataFactory.createWsmlString("?x memberOf c1"));
			ontology.addRelationInstance(query1);
			Relation r2 = wsmoFactory.createRelation(wsmoFactory.createIRI(ns, "query2"));
			r2.createParameter((byte) 0);
			RelationInstance query2 = wsmoFactory.createRelationInstance(r2);
			query2.setParameterValue((byte) 0, dataFactory.createWsmlString("?x memberOf ?y"));
			ontology.addRelationInstance(query2);
			
			// write ontology file
			writeFile(DEEP_SUBCONCEPT, getFileName(DEEP_SUBCONCEPT, amount[i]), ontology);
		}
	}	
	
	/**
	 * Generate ontologies containing memberOf expressions like 
	 * e.g. 
	 * concept Human
	 * instance Homer memberOf Human
	 * instance Marge memberOf Human
	 * 
	 * Queries: 
	 * - ?x memberOf Human
	 * - ?x memberOf ?y
	 * 
	 * @throws IOException
	 * @throws SynchronisationException
	 * @throws InvalidModelException
	 */
	public void genInstanceOntologies() 
			throws IOException, SynchronisationException, InvalidModelException {
		Ontology ontology = null;
		for (int i = 0; i < amount.length; i++) {	
			// create default namespace
			Namespace ns = wsmoFactory.createNamespace("", 
					wsmoFactory.createIRI(getNamespace(INSTANCE, amount[i])));
			
			// create ontology
			ontology = createOntology(ns,amount[i]);
			
			// set ontology default namespace and variant
			ontology.setDefaultNamespace(ns.getIRI());
			ontology.setWsmlVariant(WSML.WSML_FLIGHT);
			
			// add initial elements
			ontology.addConcept(wsmoFactory.createConcept(
					wsmoFactory.createIRI(ns, "c1")));
			
			// add amount[i] number of memberOf entities
			Concept concept;
			Instance instance;
			for (int j = 0; j < amount[i]; j++) {
				instance= wsmoFactory.createInstance(
						wsmoFactory.createIRI(ns, "i" + (j + 1)));
				concept = wsmoFactory.createConcept(
						wsmoFactory.createIRI(ns, "c1"));
				concept.addInstance(instance);
				ontology.addInstance(instance);
			}
			
			// add queries in form of relationinstances (see TestPerformanceWithUseOfFeatures)
			Relation r1 = wsmoFactory.createRelation(wsmoFactory.createIRI(ns, "query1"));
			r1.createParameter((byte) 0);
			RelationInstance query1 = wsmoFactory.createRelationInstance(r1);
			query1.setParameterValue((byte) 0, dataFactory.createWsmlString("?x memberOf c1"));
			ontology.addRelationInstance(query1);
			Relation r2 = wsmoFactory.createRelation(wsmoFactory.createIRI(ns, "query2"));
			r2.createParameter((byte) 0);
			RelationInstance query2 = wsmoFactory.createRelationInstance(r2);
			query2.setParameterValue((byte) 0, dataFactory.createWsmlString("?x memberOf ?y"));
			ontology.addRelationInstance(query2);
			
			// write ontology file
			writeFile(INSTANCE, getFileName(INSTANCE, amount[i]), ontology);
		}
	}	
	
	/**
	 * Generate ontologies containing memberOf and simple subConceptOf 
	 * expressions like e.g. 
	 * concept Human
	 * concept Man subConceptOf Human
	 * instance Homer memberOf Man
	 * instance Bart memberOf Man
	 * 
	 * Queries: 
	 * - ?x memberOf Human
	 * - ?x memberOf ?y
	 * 
	 * @throws IOException
	 * @throws SynchronisationException
	 * @throws InvalidModelException
	 */
	public void genInstanceANDsubconceptOntologies() 
			throws IOException, SynchronisationException, InvalidModelException {
		Ontology ontology = null;
		for (int i = 0; i < amount.length; i++) {	
			// create default namespace
			Namespace ns = wsmoFactory.createNamespace("", 
					wsmoFactory.createIRI(getNamespace(INSTANCE_SUBCONCEPT, amount[i])));
			
			// create ontology
			ontology = createOntology(ns,amount[i]);
			
			// set ontology default namespace and variant
			ontology.setDefaultNamespace(ns.getIRI());
			ontology.setWsmlVariant(WSML.WSML_FLIGHT);
			
			// add initial elements
			Concept subConcept = wsmoFactory.createConcept(
					wsmoFactory.createIRI(ns, "c2"));
			Concept superConcept = wsmoFactory.createConcept(
					wsmoFactory.createIRI(ns, "c1"));
			subConcept.addSuperConcept(superConcept);
			ontology.addConcept(subConcept);
			
			// add amount[i] number of memberOf entities
			Instance instance;
			for (int j = 0; j < amount[i]; j++) {
				instance= wsmoFactory.createInstance(
						wsmoFactory.createIRI(ns, "i" + (j + 1)));
				subConcept.addInstance(instance);
				ontology.addInstance(instance);
			}
			
			// add queries in form of relationinstances (see TestPerformanceWithUseOfFeatures)
			Relation r1 = wsmoFactory.createRelation(wsmoFactory.createIRI(ns, "query1"));
			r1.createParameter((byte) 0);
			RelationInstance query1 = wsmoFactory.createRelationInstance(r1);
			query1.setParameterValue((byte) 0, dataFactory.createWsmlString("?x memberOf c1"));
			ontology.addRelationInstance(query1);
			Relation r2 = wsmoFactory.createRelation(wsmoFactory.createIRI(ns, "query2"));
			r2.createParameter((byte) 0);
			RelationInstance query2 = wsmoFactory.createRelationInstance(r2);
			query2.setParameterValue((byte) 0, dataFactory.createWsmlString("?x memberOf ?y"));
			ontology.addRelationInstance(query2);
			
			// write ontology file
			writeFile(INSTANCE_SUBCONCEPT, getFileName(INSTANCE_SUBCONCEPT, amount[i]), ontology);
		}
	}	
	
	/**
	 * Generate ontologies containing memberOf and deep subConceptOf 
	 * expressions like e.g. 
	 * concept Human
	 * concept Man subConceptOf Human
	 * concept Boy subConceptOf Man
	 * instance Homer memberOf Man
	 * instance Bart memberOf Boy
	 * 
	 * Queries: 
	 * - ?x memberOf Human
	 * - ?x memberOf ?y
	 * 
	 * @throws IOException
	 * @throws SynchronisationException
	 * @throws InvalidModelException
	 */
	public void genInstanceANDdeepSubconceptOntologies() 
			throws IOException, SynchronisationException, InvalidModelException {
		Ontology ontology = null;
		int[] amount = {1,10,100,500,1000};
		for (int i = 0; i < amount.length; i++) {	
			// create default namespace
			Namespace ns = wsmoFactory.createNamespace("", 
					wsmoFactory.createIRI(getNamespace(INSTANCE_DEEP_SUBCONCEPT, amount[i])));
			
			// create ontology
			ontology = createOntology(ns,amount[i]);
			
			// set ontology default namespace and variant
			ontology.setDefaultNamespace(ns.getIRI());
			ontology.setWsmlVariant(WSML.WSML_FLIGHT);
			
			// add initial elements
			Concept concept = wsmoFactory.createConcept(
					wsmoFactory.createIRI(ns, "c1"));
			ontology.addConcept(concept);
			
			// add amount[i] number of subconcept and memberOf entities
			Concept subConcept = null;
			Concept superConcept = null;
			Instance instance = null;
			for (int j = 0; j < amount[i]; j++) {
				subConcept = wsmoFactory.createConcept(
						wsmoFactory.createIRI(ns, "c" + (j + 2)));
				superConcept = wsmoFactory.createConcept(
						wsmoFactory.createIRI(ns, "c" + (j + 1)));
				subConcept.addSuperConcept(superConcept);
				ontology.addConcept(subConcept);
				instance= wsmoFactory.createInstance(
						wsmoFactory.createIRI(ns, "i" + (j + 1)));
				subConcept.addInstance(instance);
				ontology.addInstance(instance);
			}
			
			// add queries in form of relationinstances (see TestPerformanceWithUseOfFeatures)
			Relation r1 = wsmoFactory.createRelation(wsmoFactory.createIRI(ns, "query1"));
			r1.createParameter((byte) 0);
			RelationInstance query1 = wsmoFactory.createRelationInstance(r1);
			query1.setParameterValue((byte) 0, dataFactory.createWsmlString("?x memberOf c1"));
			ontology.addRelationInstance(query1);
			Relation r2 = wsmoFactory.createRelation(wsmoFactory.createIRI(ns, "query2"));
			r2.createParameter((byte) 0);
			RelationInstance query2 = wsmoFactory.createRelationInstance(r2);
			query2.setParameterValue((byte) 0, dataFactory.createWsmlString("?x memberOf ?y"));
			ontology.addRelationInstance(query2);
			
			// write ontology file
			writeFile(INSTANCE_DEEP_SUBCONCEPT, 
					getFileName(INSTANCE_DEEP_SUBCONCEPT, amount[i]), ontology);
		}
	}	
	
	/**
	 * Generate ontologies containing constraining attribute value expressions 
	 * like e.g.:
	 * concept Thing
	 * concept Human
	 *   attr ofType Thing
	 * instance Homer memberOf Human
	 *   attr hasValue Bart
	 * instance Bart memberOf Thing
	 * 
	 * Queries: 
	 * - Homer[attr hasValue ?y]
	 * - ?x[?y hasValue ?z]
	 * 
	 * @throws IOException
	 * @throws SynchronisationException
	 * @throws InvalidModelException
	 */
	public void genOfTypeOntologies() 
			throws IOException, SynchronisationException, InvalidModelException {
		Ontology ontology = null;
		for (int i = 0; i < amount.length; i++) {	
			// create default namespace
			Namespace ns = wsmoFactory.createNamespace("", 
					wsmoFactory.createIRI(getNamespace(OFTYPE, amount[i])));
			
			// create ontology
			ontology = createOntology(ns,amount[i]);
			
			// set ontology default namespace and variant
			ontology.setDefaultNamespace(ns.getIRI());
			ontology.setWsmlVariant(WSML.WSML_FLIGHT);
			
			// add initial elements
			Concept concept1 = wsmoFactory.createConcept(
					wsmoFactory.createIRI(ns, "c1"));
			Concept concept2 = wsmoFactory.createConcept(
					wsmoFactory.createIRI(ns, "c2"));
			ontology.addConcept(concept1);
			ontology.addConcept(concept2);
			
			// add instances
			Instance instance1 = wsmoFactory.createInstance(
					wsmoFactory.createIRI(ns, "i1"));
			Instance instance2 = wsmoFactory.createInstance(
					wsmoFactory.createIRI(ns, "i2"));
			instance1.addConcept(concept1);
			instance2.addConcept(concept2);
			ontology.addInstance(instance1);
			ontology.addInstance(instance2);
			
			// add amount[i] number of constraining attribute entities
			Attribute attribute;
			for (int j = 0; j < amount[i]; j++) {
				attribute = concept1.createAttribute(wsmoFactory.createIRI(ns, "a" + (j + 1)));
				attribute.addType(concept2);
				attribute.setConstraining(true);
				instance1.addAttributeValue(attribute.getIdentifier(), instance2);
			}

			// add queries in form of relationinstances (see TestPerformanceWithUseOfFeatures)
			Relation r1 = wsmoFactory.createRelation(wsmoFactory.createIRI(ns, "query1"));
			r1.createParameter((byte) 0);
			RelationInstance query1 = wsmoFactory.createRelationInstance(r1);
			query1.setParameterValue((byte) 0, dataFactory.createWsmlString("i1[a1 hasValue ?x]"));
			ontology.addRelationInstance(query1);
			Relation r2 = wsmoFactory.createRelation(wsmoFactory.createIRI(ns, "query2"));
			r2.createParameter((byte) 0);
			RelationInstance query2 = wsmoFactory.createRelationInstance(r2);
			query2.setParameterValue((byte) 0, dataFactory.createWsmlString("?x[?y hasValue ?z]"));
			ontology.addRelationInstance(query2);
			
			// write ontology file
			writeFile(OFTYPE, getFileName(OFTYPE, amount[i]), ontology);
		}
	}	
	
	/**
	 * Generate ontologies containing simple hierarchy and constraining 
	 * attribute value expressions like e.g.:
	 * concept Animal
	 * concept Human subConceptOf Animal
	 *   hasChild ofType Child
	 * concept Child subConceptOf Human
	 * instance Homer memberOf Human
	 *   hasChild hasValue Bart
	 * instance Bart memberOf Child
	 * 
	 * Queries: 
	 * - Homer[hasChild hasValue ?y] and ?y memberOf ?z
	 * - ?x[?y hasValue ?z] and ?z memberOf ?w
	 * 
	 * @throws IOException
	 * @throws SynchronisationException
	 * @throws InvalidModelException
	 */
	public void genOfTypeANDsubconceptOntologies() 
			throws IOException, SynchronisationException, InvalidModelException {
		Ontology ontology = null;
		for (int i = 0; i < amount.length; i++) {	
			// create default namespace
			Namespace ns = wsmoFactory.createNamespace("", 
					wsmoFactory.createIRI(getNamespace(OFTYPE_SUBCONCEPT, amount[i])));
			
			// create ontology
			ontology = createOntology(ns,amount[i]);
			
			// set ontology default namespace and variant
			ontology.setDefaultNamespace(ns.getIRI());
			ontology.setWsmlVariant(WSML.WSML_FLIGHT);
			
			// add initial elements
			Concept concept1 = wsmoFactory.createConcept(
					wsmoFactory.createIRI(ns, "c1"));
			Concept concept2 = wsmoFactory.createConcept(
					wsmoFactory.createIRI(ns, "c2"));
			Concept concept3 = wsmoFactory.createConcept(
					wsmoFactory.createIRI(ns, "c3"));
			concept2.addSuperConcept(concept3);
			concept1.addSuperConcept(concept2);
			ontology.addConcept(concept2);
			ontology.addConcept(concept1);
			
			// add instances
			Instance instance1 = wsmoFactory.createInstance(
					wsmoFactory.createIRI(ns, "i1"));
			Instance instance2 = wsmoFactory.createInstance(
					wsmoFactory.createIRI(ns, "i2"));
			instance1.addConcept(concept2);
			instance2.addConcept(concept3);
			ontology.addInstance(instance1);
			ontology.addInstance(instance2);
			
			// add amount[i] number of constraining attribute entities
			Attribute attribute;
			for (int j = 0; j < amount[i]; j++) {
				attribute = concept2.createAttribute(wsmoFactory.createIRI(ns, "a" + (j + 1)));
				attribute.addType(concept3);
				attribute.setConstraining(true);
				instance1.addAttributeValue(attribute.getIdentifier(), instance2);
			}

			// add queries in form of relationinstances (see TestPerformanceWithUseOfFeatures)
			Relation r1 = wsmoFactory.createRelation(wsmoFactory.createIRI(ns, "query1"));
			r1.createParameter((byte) 0);
			RelationInstance query1 = wsmoFactory.createRelationInstance(r1);
			query1.setParameterValue((byte) 0, dataFactory.createWsmlString(
					"i1[a1 hasValue ?x] and ?x memberOf ?y"));
			ontology.addRelationInstance(query1);
			Relation r2 = wsmoFactory.createRelation(wsmoFactory.createIRI(ns, "query2"));
			r2.createParameter((byte) 0);
			RelationInstance query2 = wsmoFactory.createRelationInstance(r2);
			query2.setParameterValue((byte) 0, dataFactory.createWsmlString(
					"?x[?y hasValue ?z] and ?z memberOf ?w"));
			ontology.addRelationInstance(query2);
			
			// write ontology file
			writeFile(OFTYPE_SUBCONCEPT, getFileName(OFTYPE_SUBCONCEPT, amount[i]), ontology);
		}
	}	
	
	/**
	 * Generate ontologies containing cardinality constraints like e.g.:
	 * concept Human
	 *   isMarriedTo ofType (0 1) Human
	 * instance Homer memberOf Human
	 *   isMarriedTo hasValue Marge
	 * instance Marge memberOf Human
	 * 
	 * Queries: 
	 * - Homer[isMarriedTo hasValue ?y]
	 * - ?x[?y hasValue ?z]
	 * 
	 * @throws IOException
	 * @throws SynchronisationException
	 * @throws InvalidModelException
	 */
	public void genCardinality01Ontologies() 
			throws IOException, SynchronisationException, InvalidModelException {
		Ontology ontology = null;
		for (int i = 0; i < amount.length; i++) {	
			// create default namespace
			Namespace ns = wsmoFactory.createNamespace("", 
					wsmoFactory.createIRI(getNamespace(CARDINALITY_01, amount[i])));
			
			// create ontology
			ontology = createOntology(ns,amount[i]);
			
			// set ontology default namespace and variant
			ontology.setDefaultNamespace(ns.getIRI());
			ontology.setWsmlVariant(WSML.WSML_FLIGHT);
			
			// add initial elements
			Concept concept = wsmoFactory.createConcept(
					wsmoFactory.createIRI(ns, "c1"));
			ontology.addConcept(concept);
			
			// add instances
			Instance instance1 = wsmoFactory.createInstance(
					wsmoFactory.createIRI(ns, "i1"));
			Instance instance2 = wsmoFactory.createInstance(
					wsmoFactory.createIRI(ns, "i2"));
			instance1.addConcept(concept);
			instance2.addConcept(concept);
			ontology.addInstance(instance1);
			ontology.addInstance(instance2);
			
			// add amount[i] number of entities with cardinality contraints
			Attribute attribute = null;
			for (int j = 0; j < amount[i]; j++) {
				attribute = concept.createAttribute(wsmoFactory.createIRI(ns, "a" + (j + 1)));
				attribute.addType(concept);
				attribute.setConstraining(true);
				attribute.setMinCardinality(0);
				attribute.setMaxCardinality(1);
				instance1.addAttributeValue(attribute.getIdentifier(), instance2);
			}

			// add queries in form of relationinstances (see TestPerformanceWithUseOfFeatures)
			Relation r1 = wsmoFactory.createRelation(wsmoFactory.createIRI(ns, "query1"));
			r1.createParameter((byte) 0);
			RelationInstance query1 = wsmoFactory.createRelationInstance(r1);
			query1.setParameterValue((byte) 0, dataFactory.createWsmlString("i1[a1 hasValue ?x]"));
			ontology.addRelationInstance(query1);
			Relation r2 = wsmoFactory.createRelation(wsmoFactory.createIRI(ns, "query2"));
			r2.createParameter((byte) 0);
			RelationInstance query2 = wsmoFactory.createRelationInstance(r2);
			query2.setParameterValue((byte) 0, dataFactory.createWsmlString("?x[?y hasValue ?z]"));
			ontology.addRelationInstance(query2);
			
			// write ontology file
			writeFile(CARDINALITY_01, getFileName(CARDINALITY_01, amount[i]), ontology);
		}
	}	

	/**
	 * Generate ontologies containing cardinality constraints like  e.g.:
	 * concept Human
	 *   hasChild ofType (0 10) Human
	 * instance Homer memberOf Human
	 *   hasChild hasValue Bart
	 *   hasChild hasValue Lisa
	 * instance Bart memberOf Human
	 * instance Lisa memberOf Human
	 * 
	 * Queries: 
	 * - Homer[hasChild hasValue ?y]
	 * - ?x[?y hasValue ?z]
	 * 
	 * @throws IOException
	 * @throws SynchronisationException
	 * @throws InvalidModelException
	 */
	public void genCardinality010Ontologies() 
			throws IOException, SynchronisationException, InvalidModelException {
		Ontology ontology = null;
		for (int i = 0; i < amount.length; i++) {	
			// create default namespace
			Namespace ns = wsmoFactory.createNamespace("", 
					wsmoFactory.createIRI(getNamespace(CARDINALITY_010, amount[i])));
			
			// create ontology
			ontology = createOntology(ns,amount[i]);
			
			// set ontology default namespace and variant
			ontology.setDefaultNamespace(ns.getIRI());
			ontology.setWsmlVariant(WSML.WSML_FLIGHT);
			
			// add initial elements
			Concept concept = wsmoFactory.createConcept(
					wsmoFactory.createIRI(ns, "c1"));
			ontology.addConcept(concept);
			
			// add instances
			Instance instance1 = wsmoFactory.createInstance(
					wsmoFactory.createIRI(ns, "i1"));
			Instance instance2 = wsmoFactory.createInstance(
					wsmoFactory.createIRI(ns, "i2"));
			instance2.addConcept(concept);
			Instance instance3 = wsmoFactory.createInstance(
					wsmoFactory.createIRI(ns, "i3"));
			instance2.addConcept(concept);
			Instance instance4 = wsmoFactory.createInstance(
					wsmoFactory.createIRI(ns, "i4"));
			instance2.addConcept(concept);
			Instance instance5 = wsmoFactory.createInstance(
					wsmoFactory.createIRI(ns, "i5"));
			instance2.addConcept(concept);
			instance3.addConcept(concept);
			instance4.addConcept(concept);
			instance5.addConcept(concept);
			ontology.addInstance(instance1);
			ontology.addInstance(instance2);
			ontology.addInstance(instance3);
			ontology.addInstance(instance4);
			ontology.addInstance(instance5);
			
			// add amount[i] number of entities with cardinality contraints
			Attribute attribute = null;
			for (int j = 0; j < amount[i]; j++) {
				attribute = concept.createAttribute(wsmoFactory.createIRI(ns, "a" + (j + 1)));
				attribute.addType(concept);
				attribute.setConstraining(true);
				attribute.setMinCardinality(0);
				attribute.setMaxCardinality(10);
				instance1.addAttributeValue(attribute.getIdentifier(), instance2);
				instance1.addAttributeValue(attribute.getIdentifier(), instance3);
				instance1.addAttributeValue(attribute.getIdentifier(), instance4);
				instance1.addAttributeValue(attribute.getIdentifier(), instance5);
			}
			
			// add queries in form of relationinstances (see TestPerformanceWithUseOfFeatures)
			Relation r1 = wsmoFactory.createRelation(wsmoFactory.createIRI(ns, "query1"));
			r1.createParameter((byte) 0);
			RelationInstance query1 = wsmoFactory.createRelationInstance(r1);
			query1.setParameterValue((byte) 0, dataFactory.createWsmlString("i1[a1 hasValue ?y]"));
			ontology.addRelationInstance(query1);
			Relation r2 = wsmoFactory.createRelation(wsmoFactory.createIRI(ns, "query2"));
			r2.createParameter((byte) 0);
			RelationInstance query2 = wsmoFactory.createRelationInstance(r2);
			query2.setParameterValue((byte) 0, dataFactory.createWsmlString("?x[?y hasValue ?z]"));
			ontology.addRelationInstance(query2);
			
			// write ontology file
			writeFile(CARDINALITY_010, getFileName(CARDINALITY_010, amount[i]), ontology);
		}
	}	

	/**
	 * Generate ontologies containing inverse attribute features like 
	 * e.g.:
	 * concept Woman
	 *   hasHusband inverseOf(hasWife) ofType Man
	 * concept Man
	 *   hasWife ofType Woman
	 * instance Marge memberOf Woman
	 * instance Homer memberOf Man
	 *   hasWife hasValue Marge
	 * 
	 * Queries: 
	 * - Homer[hasWife hasValue ?x]
	 * - Homer[?x hasValue ?y]
	 * 
	 * @throws IOException
	 * @throws SynchronisationException
	 * @throws InvalidModelException
	 */
	public void genInverseAttributeOntologies() 
			throws IOException, SynchronisationException, InvalidModelException {
		Ontology ontology = null;
		for (int i = 0; i < amount.length; i++) {	
			// create default namespace
			Namespace ns = wsmoFactory.createNamespace("", 
					wsmoFactory.createIRI(getNamespace(INVERSE, amount[i])));
			
			// create ontology
			ontology = createOntology(ns,amount[i]);
			
			// set ontology default namespace and variant
			ontology.setDefaultNamespace(ns.getIRI());
			ontology.setWsmlVariant(WSML.WSML_FLIGHT);
			
			// add initial elements
			Concept concept1 = wsmoFactory.createConcept(wsmoFactory.createIRI(ns, "c1"));
			Concept concept2 = wsmoFactory.createConcept(wsmoFactory.createIRI(ns, "c2"));
			ontology.addConcept(concept1);
			ontology.addConcept(concept2);
			Attribute attribute1 = concept2.createAttribute(wsmoFactory.createIRI(ns, "a0"));
			attribute1.addType(concept1);
			attribute1.setConstraining(true);
			
			// add instances
			Instance instance1 = wsmoFactory.createInstance(
					wsmoFactory.createIRI(ns, "i1"));
			Instance instance2 = wsmoFactory.createInstance(
					wsmoFactory.createIRI(ns, "i2"));
			instance1.addConcept(concept1);
			instance2.addConcept(concept2);
			ontology.addInstance(instance1);
			ontology.addInstance(instance2);
			
			// add amount[i] number of inverse attribute entities
			Attribute attribute = null;
			for (int j = 0; j < amount[i]; j++) {
				attribute = concept1.createAttribute(wsmoFactory.createIRI(ns, "a" + (j + 1)));
				attribute.addType(concept2);
				attribute.setConstraining(true);
				attribute.setInverseOf(attribute1.getIdentifier());
				instance2.addAttributeValue(attribute.getIdentifier(), instance1);
			}
			
			// add queries in form of relationinstances (see TestPerformanceWithUseOfFeatures)
			Relation r1 = wsmoFactory.createRelation(wsmoFactory.createIRI(ns, "query1"));
			r1.createParameter((byte) 0);
			RelationInstance query1 = wsmoFactory.createRelationInstance(r1);
			query1.setParameterValue((byte) 0, dataFactory.createWsmlString("i1[a1 hasValue ?x]"));
			ontology.addRelationInstance(query1);
			Relation r2 = wsmoFactory.createRelation(wsmoFactory.createIRI(ns, "query2"));
			r2.createParameter((byte) 0);
			RelationInstance query2 = wsmoFactory.createRelationInstance(r2);
			query2.setParameterValue((byte) 0, dataFactory.createWsmlString("i1[?x hasValue ?y]"));
			ontology.addRelationInstance(query2);
			
			// write ontology file
			writeFile(INVERSE, getFileName(INVERSE, amount[i]), ontology);
		}
	}	

	/**
	 * Generate ontologies containing transitive attribute features like 
	 * e.g.:
	 * concept Human
	 *   hasAncestor transitive impliesType Human
	 * instance MargeChild
	 *   hasAncestor hasValue MargeMother
	 * instance MargeMother
	 *   hasAncestor hasValue MargeGrandMother
	 * instance MargeGrandMother
	 * 
	 * Queries: 
	 * - MargeChild[hasAncestor hasValue ?x]
	 * - ?x[hasAncestor hasValue ?y]
	 * 
	 * @throws IOException
	 * @throws SynchronisationException
	 * @throws InvalidModelException
	 */
	public void genTransitiveAttributeOntologies() 
			throws IOException, SynchronisationException, InvalidModelException {
		Ontology ontology = null;
		for (int i = 0; i < amount.length; i++) {	
			// create default namespace
			Namespace ns = wsmoFactory.createNamespace("", 
					wsmoFactory.createIRI(getNamespace(TRANSITIVE, amount[i])));
			
			// create ontology
			ontology = createOntology(ns,amount[i]);
			
			// set ontology default namespace and variant
			ontology.setDefaultNamespace(ns.getIRI());
			ontology.setWsmlVariant(WSML.WSML_FLIGHT);
			
			// add initial elements
			Concept concept = wsmoFactory.createConcept(wsmoFactory.createIRI(ns, "c1"));
			ontology.addConcept(concept);
			Attribute attribute1 = concept.createAttribute(wsmoFactory.createIRI(ns, "a0"));
			attribute1.addType(concept);
			attribute1.setTransitive(true);
			
			// add instances
			Instance instance1 = wsmoFactory.createInstance(
					wsmoFactory.createIRI(ns, "i1"));
			Instance instance2 = wsmoFactory.createInstance(
					wsmoFactory.createIRI(ns, "i2"));
			Instance instance3 = wsmoFactory.createInstance(
					wsmoFactory.createIRI(ns, "i3"));
			instance1.addAttributeValue(attribute1.getIdentifier(), instance2);
			instance2.addAttributeValue(attribute1.getIdentifier(), instance3);
			ontology.addInstance(instance1);
			ontology.addInstance(instance2);
			ontology.addInstance(instance3);

			// add amount[i] number of transitive attribute entities
			Attribute attribute = null;
			for (int j = 0; j < amount[i]; j++) {
				attribute = concept.createAttribute(wsmoFactory.createIRI(ns, "a" + (j + 1)));
				attribute.addType(concept);
				attribute.setTransitive(true);
				instance1.addAttributeValue(attribute.getIdentifier(), instance2);
				instance2.addAttributeValue(attribute.getIdentifier(), instance3);
			}
			
			// add queries in form of relationinstances (see TestPerformanceWithUseOfFeatures)
			Relation r1 = wsmoFactory.createRelation(wsmoFactory.createIRI(ns, "query1"));
			r1.createParameter((byte) 0);
			RelationInstance query1 = wsmoFactory.createRelationInstance(r1);
			query1.setParameterValue((byte) 0, dataFactory.createWsmlString("i1[a0 hasValue ?x]"));
			ontology.addRelationInstance(query1);
			Relation r2 = wsmoFactory.createRelation(wsmoFactory.createIRI(ns, "query2"));
			r2.createParameter((byte) 0);
			RelationInstance query2 = wsmoFactory.createRelationInstance(r2);
			query2.setParameterValue((byte) 0, dataFactory.createWsmlString("?x[a0 hasValue ?y]"));
			ontology.addRelationInstance(query2);
			Relation r3 = wsmoFactory.createRelation(wsmoFactory.createIRI(ns, "query3"));
			r3.createParameter((byte) 0);
			RelationInstance query3 = wsmoFactory.createRelationInstance(r3);
			query3.setParameterValue((byte) 0, dataFactory.createWsmlString(
					"?x[" + ((IRI) attribute.getIdentifier()).getLocalName() + " hasValue ?y]"));
			ontology.addRelationInstance(query3);
			
			// write ontology file
			writeFile(TRANSITIVE, getFileName(TRANSITIVE, amount[i]), ontology);
		}
	}	

	/**
	 * Generate ontologies containing symmetric attribute features like 
	 * e.g.:
	 * concept Human
	 *   hasRelative symmetric ofType Human
	 * instance Marge
	 *   hasRelative hasValue Homer
	 * instance Homer
	 * 
	 * Queries: 
	 * - Homer[hasRelative hasValue ?x]
	 * - ?x[hasRelative hasValue ?y]
	 * 
	 * @throws IOException
	 * @throws SynchronisationException
	 * @throws InvalidModelException
	 */
	public void genSymmetricAttributeOntologies() 
			throws IOException, SynchronisationException, InvalidModelException {
		Ontology ontology = null;
		for (int i = 0; i < amount.length; i++) {	
			// create default namespace
			Namespace ns = wsmoFactory.createNamespace("", 
					wsmoFactory.createIRI(getNamespace(SYMMETRIC, amount[i])));
			
			// create ontology
			ontology = createOntology(ns,amount[i]);
			
			// set ontology default namespace and variant
			ontology.setDefaultNamespace(ns.getIRI());
			ontology.setWsmlVariant(WSML.WSML_FLIGHT);
			
			// add initial elements
			Concept concept = wsmoFactory.createConcept(wsmoFactory.createIRI(ns, "c1"));
			ontology.addConcept(concept);
			Attribute attribute1 = concept.createAttribute(wsmoFactory.createIRI(ns, "a0"));
			attribute1.addType(concept);
			attribute1.setConstraining(true);
			attribute1.setSymmetric(true);
			
			// add instances
			Instance instance1 = wsmoFactory.createInstance(
					wsmoFactory.createIRI(ns, "i1"));
			Instance instance2 = wsmoFactory.createInstance(
					wsmoFactory.createIRI(ns, "i2"));
			instance1.addAttributeValue(attribute1.getIdentifier(), instance2);
			ontology.addInstance(instance1);
			ontology.addInstance(instance2);

			// add amount[i] number of symmetric attribute entities
			Attribute attribute = null;
			for (int j = 0; j < amount[i]; j++) {
				attribute = concept.createAttribute(wsmoFactory.createIRI(ns, "a" + (j + 1)));
				attribute.addType(concept);
				attribute.setConstraining(true);
				attribute.setSymmetric(true);
				instance1.addAttributeValue(attribute.getIdentifier(), instance2);
			}

			// add queries in form of relationinstances (see TestPerformanceWithUseOfFeatures)
			Relation r1 = wsmoFactory.createRelation(wsmoFactory.createIRI(ns, "query1"));
			r1.createParameter((byte) 0);
			RelationInstance query1 = wsmoFactory.createRelationInstance(r1);
			query1.setParameterValue((byte) 0, dataFactory.createWsmlString("i2[a0 hasValue ?x]"));
			ontology.addRelationInstance(query1);
			Relation r2 = wsmoFactory.createRelation(wsmoFactory.createIRI(ns, "query2"));
			r2.createParameter((byte) 0);
			RelationInstance query2 = wsmoFactory.createRelationInstance(r2);
			query2.setParameterValue((byte) 0, dataFactory.createWsmlString("?x[a0 hasValue ?y]"));
			ontology.addRelationInstance(query2);
			Relation r3 = wsmoFactory.createRelation(wsmoFactory.createIRI(ns, "query3"));
			r3.createParameter((byte) 0);
			RelationInstance query3 = wsmoFactory.createRelationInstance(r3);
			query3.setParameterValue((byte) 0, dataFactory.createWsmlString(
					"?x[" + ((IRI) attribute.getIdentifier()).getLocalName() + " hasValue ?y]"));
			ontology.addRelationInstance(query3);
			
			// write ontology file
			writeFile(SYMMETRIC, getFileName(SYMMETRIC, amount[i]), ontology);
		}
	}	
	
	/**
	 * Generate ontologies containing reflexive attribute features like 
	 * e.g.:
	 * concept Human
	 *   loves symmetric ofType Human
	 * instance Homer
	 * 
	 * Queries: 
	 * - Homer[loves hasValue ?x]
	 * - Homer[?x hasValue ?y]
	 * 
	 * @throws IOException
	 * @throws SynchronisationException
	 * @throws InvalidModelException
	 */
	public void genReflexiveAttributeOntologies() 
			throws IOException, SynchronisationException, InvalidModelException {
		Ontology ontology = null;
		for (int i = 0; i < amount.length; i++) {	
			// create default namespace
			Namespace ns = wsmoFactory.createNamespace("", 
					wsmoFactory.createIRI(getNamespace(REFLEXIVE, amount[i])));
			
			// create ontology
			ontology = createOntology(ns,amount[i]);
			
			// set ontology default namespace and variant
			ontology.setDefaultNamespace(ns.getIRI());
			ontology.setWsmlVariant(WSML.WSML_FLIGHT);
			
			// add initial elements
			Concept concept = wsmoFactory.createConcept(wsmoFactory.createIRI(ns, "c1"));
			ontology.addConcept(concept);
			Attribute attribute1 = concept.createAttribute(wsmoFactory.createIRI(ns, "a0"));
			attribute1.addType(concept);
			attribute1.setConstraining(true);
			attribute1.setReflexive(true);
			
			// add instances
			Instance instance1 = wsmoFactory.createInstance(
					wsmoFactory.createIRI(ns, "i1"));
			ontology.addInstance(instance1);

			// add amount[i] number of reflexive attribute entities
			Attribute attribute = null;
			for (int j = 0; j < amount[i]; j++) {
				attribute = concept.createAttribute(wsmoFactory.createIRI(ns, "a" + (j + 1)));
				attribute.addType(concept);
				attribute.setConstraining(true);
				attribute.setReflexive(true);
			}

			// add queries in form of relationinstances (see TestPerformanceWithUseOfFeatures)
			Relation r1 = wsmoFactory.createRelation(wsmoFactory.createIRI(ns, "query1"));
			r1.createParameter((byte) 0);
			RelationInstance query1 = wsmoFactory.createRelationInstance(r1);
			query1.setParameterValue((byte) 0, dataFactory.createWsmlString("i1[a0 hasValue ?x]"));
			ontology.addRelationInstance(query1);
			Relation r2 = wsmoFactory.createRelation(wsmoFactory.createIRI(ns, "query2"));
			r2.createParameter((byte) 0);
			RelationInstance query2 = wsmoFactory.createRelationInstance(r2);
			query2.setParameterValue((byte) 0, dataFactory.createWsmlString("i1[?x hasValue ?y]"));
			ontology.addRelationInstance(query2);
			
			// write ontology file
			writeFile(REFLEXIVE, getFileName(REFLEXIVE, amount[i]), ontology);
		}
	}	

	/**
	 * Generate ontologies containing logical expressions with locally 
	 * stratified negation 
	 * e.g.:
	 * concept Human
	 *   knows ofType Human
	 *   distrust ofType Human	
	 * instance Homer
	 * axiom definedBy
	 *   ?x[distrust hasValue ?y] :- naf ?x[knows hasValue ?y] 
	 *   and ?x memberOf Human and ?y memberOf Human.
	 * 
	 * Queries: 
	 * - Homer[distrust hasValue ?x]
	 * - ?x[distrust hasValue ?y]
	 * 
	 * @throws IOException
	 * @throws SynchronisationException
	 * @throws InvalidModelException
	 * @throws ParserException 
	 */
	public void genLocallyStratifiedNegationOntologies() 
			throws IOException, SynchronisationException, InvalidModelException, ParserException {
		Ontology ontology = null;
		for (int i = 0; i < amount.length; i++) {	
			// create default namespace
			Namespace ns = wsmoFactory.createNamespace("", 
					wsmoFactory.createIRI(getNamespace(LOC_STRAT_NEGATION, amount[i])));
			
			// create ontology
			ontology = createOntology(ns,amount[i]);
			
			// set ontology default namespace and variant
			ontology.setDefaultNamespace(ns.getIRI());
			ontology.setWsmlVariant(WSML.WSML_FLIGHT);
			
			// add initial elements
			Concept concept = wsmoFactory.createConcept(wsmoFactory.createIRI(ns, "c1"));
			ontology.addConcept(concept);
			Axiom axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns, "ax1"));
			ontology.addAxiom(axiom);
			
			// add instances
			Instance instance1 = wsmoFactory.createInstance(
					wsmoFactory.createIRI(ns, "i1"));
			Instance instance2 = wsmoFactory.createInstance(
					wsmoFactory.createIRI(ns, "i2"));
			Instance instance3 = wsmoFactory.createInstance(
					wsmoFactory.createIRI(ns, "i3"));
			ontology.addInstance(instance1);
			ontology.addInstance(instance2);
			ontology.addInstance(instance3);

			// add amount[i] number of logical expressions containing negation
			LogicalExpression logExpr = null;
			Attribute attribute = null;
			for (int j = 0; j < amount[i]; j++) {
				attribute = concept.createAttribute(wsmoFactory.createIRI(ns, "a" + j));
				attribute.addType(concept);
				attribute.setConstraining(true);
				attribute = concept.createAttribute(wsmoFactory.createIRI(ns, "a" + (j+1)));
				attribute.addType(concept);
				attribute.setConstraining(true);
				logExpr = leFactory.createLogicalExpression(
						"?x[a" + j + " hasValue ?y] :- naf ?x[a" + (j+1) + " hasValue ?y] " +
								"and ?x memberOf c1 and ?y memberOf c1", 
						ontology);
				axiom.addDefinition(logExpr);
			}

			// add queries in form of relationinstances (see TestPerformanceWithUseOfFeatures)
			Relation r1 = wsmoFactory.createRelation(wsmoFactory.createIRI(ns, "query1"));
			r1.createParameter((byte) 0);
			RelationInstance query1 = wsmoFactory.createRelationInstance(r1);
			query1.setParameterValue((byte) 0, dataFactory.createWsmlString("i1[a0 hasValue ?x]"));
			ontology.addRelationInstance(query1);
			Relation r2 = wsmoFactory.createRelation(wsmoFactory.createIRI(ns, "query2"));
			r2.createParameter((byte) 0);
			RelationInstance query2 = wsmoFactory.createRelationInstance(r2);
			query2.setParameterValue((byte) 0, dataFactory.createWsmlString("?x[a0 hasValue ?y]"));
			ontology.addRelationInstance(query2);
			Relation r3 = wsmoFactory.createRelation(wsmoFactory.createIRI(ns, "query3"));
			r3.createParameter((byte) 0);
			RelationInstance query3 = wsmoFactory.createRelationInstance(r3);
			query3.setParameterValue((byte) 0, dataFactory.createWsmlString(
					"?x[a" + (amount[i]-1) + " hasValue ?y]"));
			ontology.addRelationInstance(query3);
			
			// write ontology file
			writeFile(LOC_STRAT_NEGATION, getFileName(LOC_STRAT_NEGATION, amount[i]), ontology);
		}
	}	
	
	/**
	 * Generate ontologies containing logical expressions with globally 
	 * stratified negation 
	 * e.g.:
	 * concept Human
	 *   knows ofType Human
	 * concept DistrustedPerson  
	 * instance Homer
	 * axiom definedBy
	 *   ?y memberOf DistrustedPerson :- naf ?x[knows hasValue ?y] 
	 *   and ?x memberOf Human and ?y memberOf Human.
	 * 
	 * Queries: 
	 * - ?x memberOf DistrustedPerson
	 * - ?x memberOf ?y
	 * 
	 * @throws IOException
	 * @throws SynchronisationException
	 * @throws InvalidModelException
	 * @throws ParserException 
	 */
	public void genGloballyStratifiedNegationOntologies() 
			throws IOException, SynchronisationException, InvalidModelException, ParserException {
		Ontology ontology = null;
		for (int i = 0; i < amount.length; i++) {	
			// create default namespace
			Namespace ns = wsmoFactory.createNamespace("", 
					wsmoFactory.createIRI(getNamespace(GLOB_STRAT_NEGATION, amount[i])));
			
			// create ontology
			ontology = createOntology(ns,amount[i]);
			
			// set ontology default namespace and variant
			ontology.setDefaultNamespace(ns.getIRI());
			ontology.setWsmlVariant(WSML.WSML_FLIGHT);
			
			// add initial elements
			Concept concept1 = wsmoFactory.createConcept(wsmoFactory.createIRI(ns, "c1"));
			ontology.addConcept(concept1);
			Concept concept2 = wsmoFactory.createConcept(wsmoFactory.createIRI(ns, "c2"));
			ontology.addConcept(concept2);
			Axiom axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns, "ax1"));
			ontology.addAxiom(axiom);
			
			// add instances
			Instance instance1 = wsmoFactory.createInstance(
					wsmoFactory.createIRI(ns, "i1"));
			Instance instance2 = wsmoFactory.createInstance(
					wsmoFactory.createIRI(ns, "i2"));
			Instance instance3 = wsmoFactory.createInstance(
					wsmoFactory.createIRI(ns, "i3"));
			ontology.addInstance(instance1);
			ontology.addInstance(instance2);
			ontology.addInstance(instance3);

			// add amount[i] number of logical expressions containing negation
			LogicalExpression logExpr = null;
			Attribute attribute = null;
			for (int j = 0; j < amount[i]; j++) {
				attribute = concept1.createAttribute(wsmoFactory.createIRI(ns, "a" + j));
				attribute.addType(concept1);
				attribute.setConstraining(true);
				logExpr = leFactory.createLogicalExpression(
						"?y memberOf c2 :- naf ?x[a" + j + " hasValue ?y] " +
								"and ?x memberOf c2 and ?y memberOf c1", 
						ontology);
				axiom.addDefinition(logExpr);
			}

			// add queries in form of relationinstances (see TestPerformanceWithUseOfFeatures)
			Relation r1 = wsmoFactory.createRelation(wsmoFactory.createIRI(ns, "query1"));
			r1.createParameter((byte) 0);
			RelationInstance query1 = wsmoFactory.createRelationInstance(r1);
			query1.setParameterValue((byte) 0, dataFactory.createWsmlString("?x memberOf c2"));
			ontology.addRelationInstance(query1);
			Relation r2 = wsmoFactory.createRelation(wsmoFactory.createIRI(ns, "query2"));
			r2.createParameter((byte) 0);
			RelationInstance query2 = wsmoFactory.createRelationInstance(r2);
			query2.setParameterValue((byte) 0, dataFactory.createWsmlString("?x memberOf ?y"));
			ontology.addRelationInstance(query2);
			
			// write ontology file
			writeFile(GLOB_STRAT_NEGATION, getFileName(GLOB_STRAT_NEGATION, amount[i]), ontology);
		}
	}	
	
	/**
	 * Generate ontologies containing logical expressions with built-ins 
	 * e.g.:
	 * concept Human
	 *   hasAge ofType _integer
	 * instance Bart memberOf Human
	 *   hasAge hasValue 17
	 * instance Lisa memberOf Human
	 *   hasAge hasValue 14
	 * axiom definedBy
	 * 	 ?x[hasAge hasValue ?y] and ?y < 16 implies ?x memberOf Child .
	 * 
	 * Queries: 
	 * - ?x memberOf Child
	 * - ?x memberOf ?y
	 * 
	 * @throws IOException
	 * @throws SynchronisationException
	 * @throws InvalidModelException
	 * @throws ParserException 
	 */
	public void genBuiltInAttributeOntologies() 
			throws IOException, SynchronisationException, InvalidModelException, ParserException {
		Ontology ontology = null;
		for (int i = 0; i < amount.length; i++) {	
			// create default namespace
			Namespace ns = wsmoFactory.createNamespace("", 
					wsmoFactory.createIRI(getNamespace(BUILTIN, amount[i])));
			
			// create ontology
			ontology = createOntology(ns,amount[i]);
			
			// set ontology default namespace and variant
			ontology.setDefaultNamespace(ns.getIRI());
			ontology.setWsmlVariant(WSML.WSML_FLIGHT);
			
			// add initial elements
			Concept concept1 = wsmoFactory.createConcept(wsmoFactory.createIRI(ns, "c1"));
			Concept concept2 = wsmoFactory.createConcept(wsmoFactory.createIRI(ns, "c2"));
			ontology.addConcept(concept1);
			ontology.addConcept(concept2);
			Attribute attribute1 = concept1.createAttribute(wsmoFactory.createIRI(ns, "a0"));
			attribute1.addType(dataFactory.createWsmlDataType(WsmlDataType.WSML_INTEGER));
			attribute1.setConstraining(true);
			Axiom axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns, "ax1"));
			ontology.addAxiom(axiom);
			
			// add instances
			Instance instance1 = wsmoFactory.createInstance(
					wsmoFactory.createIRI(ns, "i1"));
			instance1.addAttributeValue(attribute1.getIdentifier(), dataFactory.createWsmlInteger("17"));
			Instance instance2 = wsmoFactory.createInstance(
					wsmoFactory.createIRI(ns, "i2"));
			instance2.addAttributeValue(attribute1.getIdentifier(), dataFactory.createWsmlInteger("14"));
			ontology.addInstance(instance1);
			ontology.addInstance(instance2);

			// add amount[i] number of logical expressions containing built-ins
			LogicalExpression logExpr = null;
			Attribute attribute = null;
			Instance instance = null;
			Random random = new Random();
			for (int j = 0; j < amount[i]+1; j++) {
				attribute = concept1.createAttribute(wsmoFactory.createIRI(ns, "a" + j));
				attribute.addType(dataFactory.createWsmlDataType(WsmlDataType.WSML_INTEGER));
				attribute.setConstraining(true);
				instance = wsmoFactory.createInstance(wsmoFactory.createIRI(ns, "i" + (j+3)));
				instance.addAttributeValue(attribute.getIdentifier(), 
						dataFactory.createWsmlInteger("" + (Math.abs(random.nextInt()) % 80)));
				logExpr = leFactory.createLogicalExpression(
						"?x[a" + j + " hasValue ?y] and ?y < 16 implies ?x memberOf c2", 
						ontology);
				axiom.addDefinition(logExpr);
				ontology.addInstance(instance);
			}

			// add queries in form of relationinstances (see TestPerformanceWithUseOfFeatures)
			Relation r1 = wsmoFactory.createRelation(wsmoFactory.createIRI(ns, "query1"));
			r1.createParameter((byte) 0);
			RelationInstance query1 = wsmoFactory.createRelationInstance(r1);
			query1.setParameterValue((byte) 0, dataFactory.createWsmlString("?x memberOf c2"));
			ontology.addRelationInstance(query1);
			Relation r2 = wsmoFactory.createRelation(wsmoFactory.createIRI(ns, "query2"));
			r2.createParameter((byte) 0);
			RelationInstance query2 = wsmoFactory.createRelationInstance(r2);
			query2.setParameterValue((byte) 0, dataFactory.createWsmlString("?x memberOf ?y"));
			ontology.addRelationInstance(query2);
			
			// write ontology file
			writeFile(BUILTIN, getFileName(BUILTIN, amount[i]), ontology);
		}
	}	
	
	/*
	 * Build file name based on specified number
	 */
	private String getNamespace(String ontType, int number) {
		return "http://www." + getFileName(ontType, number) + ".org";
	}
	
	private Ontology createOntology(Namespace ns, int no){
		DecimalFormat dformat = new DecimalFormat("00000");
		return wsmoFactory.createOntology(wsmoFactory.createIRI(ns,"o_"+dformat.format(no)));
	}
	private String getFileName(String ontType, int number) {
		DecimalFormat dformat = new DecimalFormat("00000");
		return ontType + "-" + dformat.format(number) + "-ontology";
	}
	
	private void writeFile(String ontType, String fileName, Ontology ontology) 
			throws IOException {
		File path1 = new File(BenchmarkOntologyGenerator.path);
		if (!path1.exists()){
			path1.mkdir();
		}
		File path = new File(BenchmarkOntologyGenerator.path + "/" + ontType);
		if (!path.exists()){
			System.out.println("creating directory: "+path);
			path.mkdir();
		}
		
		// Write ontology to file
	    File ontFile = new File(path, fileName + ".wsml");
	    FileWriter writer = new FileWriter(ontFile);
	    serializer.serialize(new TopEntity[] {ontology}, writer);
	    
	    writer.flush();
	    writer.close();
	    System.out.println("Wrote " + fileName + ".wsml");
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		WSMO4JManager wsmo4jManager = new WSMO4JManager();
		wsmoFactory = wsmo4jManager.getWSMOFactory();
		leFactory = wsmo4jManager.getLogicalExpressionFactory();
		dataFactory = wsmo4jManager.getDataFactory();
		serializer = Factory.createSerializer(null);
		
		BenchmarkOntologyGenerator generator = new BenchmarkOntologyGenerator();
		try {
			generator.genSubconceptOntologies();
			generator.genDeepSubconceptOntologies();
			generator.genInstanceOntologies();
			generator.genInstanceANDsubconceptOntologies();
			generator.genInstanceANDdeepSubconceptOntologies();
			generator.genOfTypeOntologies();
			generator.genOfTypeANDsubconceptOntologies();
			generator.genCardinality01Ontologies();
			generator.genCardinality010Ontologies();
			generator.genInverseAttributeOntologies();
			generator.genTransitiveAttributeOntologies();
			generator.genSymmetricAttributeOntologies();
			generator.genReflexiveAttributeOntologies();
			generator.genLocallyStratifiedNegationOntologies();
			generator.genGloballyStratifiedNegationOntologies();
			generator.genBuiltInAttributeOntologies();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SynchronisationException e) {
			e.printStackTrace();
		} catch (InvalidModelException e) {
			e.printStackTrace();
		} catch (ParserException e) {
			e.printStackTrace();
		}
	}

}
/*
 * $Log: not supported by cvs2svn $
 * Revision 1.3  2007-06-18 13:04:00  hlausen
 * adding numberformat to better sort files and creating directories on the fly
 *
 * Revision 1.2  2007-06-18 11:24:56  nathalie
 * changed/added methods to generate locally and globally stratified negation ontologies
 *
 * Revision 1.1  2007-06-18 07:20:11  nathalie
 * added 1) automatic ontology generator class and 2)
 * performance test ontologies and results
 *
 *
 */
