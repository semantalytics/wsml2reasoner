/**
 * WSML Reasoner Implementation.
 *
 * Copyright (c) 2007, DERI, Innsbruck.
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
package performance;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;

import org.deri.wsmo4j.logicalexpression.util.OntologyUtil;
import org.omwg.logicalexpression.*;
import org.omwg.logicalexpression.terms.ConstructedTerm;
import org.omwg.logicalexpression.terms.Term;
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
	private static String path = "performance/performance/results/";
	
	// for each type of ontology (like e.g. subconcept ontology) ontologies 
	// with the following amounts of entities shall be created
	private static int[] amount;
	static{ 
		amount = new int[15];
		for (int i=0;i <amount.length;i++){
			int t = i*250;
			if (t==0) t=10;
			amount[i]=t;
		}
	}
	
	/*
	 * Non functional properties used for description of the ontologies and queries
	 */
	public static String DC_TITLE = "http://purl.org/dc/elements/1.1#title";
	
	public static String DC_DESCRIPTION = "http://purl.org/dc/elements/1.1#description";
	
	public static String BM_RESULT = "http://wsml2reasoner/benchmarks#result";
	
	public static String BM_CONCEPTS = "http://wsml2reasoner/benchmarks#conceptsAmount";
	
	public static String BM_INSTANCES = "http://wsml2reasoner/benchmarks#instancesAmount";
		
	public static String BM_ATTRIBUTES = "http://wsml2reasoner/benchmarks#attributesAmount";
	
	public static String BM_TOTAL = "http://wsml2reasoner/benchmarks#totalTermAmount";
	
	public static String BM_REPLICATED = "http://wsml2reasoner/benchmarks#replicated";
	
	/*
	 * Types of ontologies created
	 */
	private static String SUBCONCEPT = "subconcept";
	
	private static String DEEP_SUBCONCEPT = "deepSubconcept";
	
	private static String INSTANCE = "instance";
	
	private static String INSTANCE_SUBCONCEPT = "instanceANDsubconcept";
	
	private static String INSTANCE_DEEP_SUBCONCEPT = "instanceANDdeepSubconcept";
	
	private static String OFTYPE = "ofType";
	
	private static String OFTYPE_SUBCONCEPT = "ofTypeANDsubconcept";
	
	private static String CARDINALITY_01 = "cardinality_0_1";
	
	private static String CARDINALITY_010 = "cardinality_0_10";
	
	private static String CARDINALITY_MIN = "cardinality_1_max";
	
	private static String INVERSE = "inverseAttribute";
	
	private static String TRANSITIVE = "transitiveAttribute";
	
	private static String SYMMETRIC = "symmetricAttribute";
	
	private static String REFLEXIVE = "reflexiveAttribute";
	
	private static String LOC_STRAT_NEGATION = "locallyStratifiedNegation";
	
	private static String GLOB_STRAT_NEGATION = "globallyStratifiedNegation";
	
	private static String BUILTIN = "built_in";
	
	/*
	 * Types of queries
	 */
	public static String MEMBEROF_1 = "MemberOf query 1";
	
	public static String MEMBEROF_2 = "MemberOf query 2";
	
	public static String ATTR_VALUE_1 = "Attribute value query 1";
	
	public static String ATTR_VALUE_2 = "Attribute value query 2";
	
	
	public static void main(String[] args) throws Exception{
		WSMO4JManager wsmo4jManager = new WSMO4JManager();
		wsmoFactory = wsmo4jManager.getWSMOFactory();
		leFactory = wsmo4jManager.getLogicalExpressionFactory();
		dataFactory = wsmo4jManager.getDataFactory();
		serializer = Factory.createSerializer(null);
		
		BenchmarkOntologyGenerator generator = new BenchmarkOntologyGenerator();
			generator.genSubconceptOntologies();
			generator.genDeepSubconceptOntologies();
			generator.genInstanceOntologies();
			generator.genInstanceANDsubconceptOntologies();
			generator.genInstanceANDdeepSubconceptOntologies();
			generator.genOfTypeOntologies();
			generator.genOfTypeANDsubconceptOntologies();
			generator.genCardinality01Ontologies();
			generator.genCardinality010Ontologies();
			generator.genMinCardinalityOntologies();
			generator.genInverseAttributeOntologies();
			generator.genTransitiveAttributeOntologies();
			generator.genSymmetricAttributeOntologies();
			generator.genReflexiveAttributeOntologies();
			generator.genLocallyStratifiedNegationOntologies();
			generator.genGloballyStratifiedNegationOntologies();
			generator.genBuiltInAttributeOntologies();
	}
	
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
			
			/*
			 * add queries to ontology in form of relationinstances
			 */ 
			// create query 1
			Relation r1 = wsmoFactory.createRelation(wsmoFactory.createIRI(ns, "query1"));
			r1.createParameter((byte) 0);
			RelationInstance query1 = wsmoFactory.createRelationInstance(r1);
			query1.setParameterValue((byte) 0, dataFactory.createWsmlString("?x memberOf c1"));
			// add NFPs to query 1
			String description = "This query will return two result sets containing " +
					"i1 and i2.";
			String result1 = "?x=i2";
			addNFPs(query1, MEMBEROF_1, description, result1);
			ontology.addRelationInstance(query1);
			// create query 2
			Relation r2 = wsmoFactory.createRelation(wsmoFactory.createIRI(ns, "query2"));
			r2.createParameter((byte) 0);
			RelationInstance query2 = wsmoFactory.createRelationInstance(r2);
			query2.setParameterValue((byte) 0, dataFactory.createWsmlString("?x memberOf ?y"));
			// add NFPs to query 2
			description = "This query will return three result sets containing i1 and i2 (i2 is " +
					"member of two concepts).";
			String result2 = "?x=i2,?y=" + ((IRI) subConcept.getIdentifier()).getLocalName();
			addNFPs(query2, MEMBEROF_2, description, result2);
			ontology.addRelationInstance(query2);
			
			/*
			 * add non functional properties to the ontology: title, description
			 */
			String title = "Subconcept expressions";
			description = "\n\t\t\t This ontology is containing simple hierarchy expressions.\n\n\t\t\t " +
					"The x-axis value of the graph indicates the number of subconcept expressions." +
					"\n\n\t\t\t Ontology example: \n\t\t\t concept c1 \n\t\t\t concept c2 subConceptOf c1" +
					"\n\t\t\t instance i1 memberOf c1\n\t\t\t instance i2 memberOf c2\n\n\t\t\t " +
					"The following two queries are applied to it:\n\t\t\t " +
					"Query 1: ?x memberOf c1\n\t\t\t Query 2: ?x memberOf ?y";
			ontology = addNFPs(ontology, title, description, amount[i]);
			
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
			
			/*
			 * add queries to ontology in form of relationinstances
			 */ 
			// create query 1
			Relation r1 = wsmoFactory.createRelation(wsmoFactory.createIRI(ns, "query1"));
			r1.createParameter((byte) 0);
			RelationInstance query1 = wsmoFactory.createRelationInstance(r1);
			query1.setParameterValue((byte) 0, dataFactory.createWsmlString("?x memberOf c1"));
			// add NFPs to query 1
			String description = "This query will return two result sets containing " +
					"i1 and i2.";
			String result1 = "?x=i2";
			addNFPs(query1, MEMBEROF_1, description, result1);
			ontology.addRelationInstance(query1);
			// create query 2
			Relation r2 = wsmoFactory.createRelation(wsmoFactory.createIRI(ns, "query2"));
			r2.createParameter((byte) 0);
			RelationInstance query2 = wsmoFactory.createRelationInstance(r2);
			query2.setParameterValue((byte) 0, dataFactory.createWsmlString("?x memberOf ?y"));
			// add NFPs to query 2
			description = "This query will return a number of result sets rising along with " +
					"the amount of subconcept expressions in the ontology.";
			String result2 = "?x=i2,?y=c11";
			addNFPs(query2, MEMBEROF_2, description, result2);
			ontology.addRelationInstance(query2);

			/*
			 * add non functional properties to the ontology: title, description
			 */
			String title = "Deep subconcept expressions";
			description = "\n\t\t\t This ontology is containing deep hierarchy expressions.\n\n\t\t\t " +
					"The x-axis value of the graph indicates the number of subconcept expressions." +
					"\n\n\t\t\t Ontology example: \n\t\t\t concept c1 \n\t\t\t concept c2 subConceptOf c1" +
					"\n\t\t\t concept c3 subConceptOf c2" +
					"\n\t\t\t instance i1 memberOf c1\n\t\t\t instance i2 memberOf c3\n\n\t\t\t " +
					"The following two queries are applied to it:\n\t\t\t " +
					"Query 1: ?x memberOf c1\n\t\t\t Query 2: ?x memberOf ?y";
			ontology = addNFPs(ontology, title, description, amount[i]);
			
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
			
			/*
			 * add queries to ontology in form of relationinstances
			 */ 
			// create query 1
			Relation r1 = wsmoFactory.createRelation(wsmoFactory.createIRI(ns, "query1"));
			r1.createParameter((byte) 0);
			RelationInstance query1 = wsmoFactory.createRelationInstance(r1);
			query1.setParameterValue((byte) 0, dataFactory.createWsmlString("?x memberOf c1"));
			// add NFPs to query 1
			String description = "This query will return a number of result sets equivalent " +
					"to the number of memberof expressions in the ontology.";
			String result1 = "?x=i" + amount[i];
			addNFPs(query1, MEMBEROF_1, description, result1);
			ontology.addRelationInstance(query1);

			/*
			 * add non functional properties to the ontology: title, description
			 */
			String title = "Memberof expressions";
			description = "\n\t\t\t This ontology is containing memberof expressions.\n\n\t\t\t " +
					"The x-axis value of the graph indicates the number of memberof expressions." +
					"\n\n\t\t\t Ontology example: \n\t\t\t concept c1 " +
					"\n\t\t\t instance i1 memberOf c1\n\t\t\t instance i2 memberOf c1\n\n\t\t\t " +
					"The following query is applied to it:\n\t\t\t " +
					"Query: ?x memberOf c1";
			ontology = addNFPs(ontology, title, description, amount[i]);
			
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
			
			/*
			 * add queries to ontology in form of relationinstances
			 */ 
			// create query 1
			Relation r1 = wsmoFactory.createRelation(wsmoFactory.createIRI(ns, "query1"));
			r1.createParameter((byte) 0);
			RelationInstance query1 = wsmoFactory.createRelationInstance(r1);
			query1.setParameterValue((byte) 0, dataFactory.createWsmlString("?x memberOf c1"));
			// add NFPs to query 1
			String description = "This query will return a number of result sets equivalent " +
					"to the number of memberof expressions in the ontology.";
			String result1 = "?x=i" + amount[i];
			addNFPs(query1, MEMBEROF_1, description, result1);
			ontology.addRelationInstance(query1);
			// create query 2
			Relation r2 = wsmoFactory.createRelation(wsmoFactory.createIRI(ns, "query2"));
			r2.createParameter((byte) 0);
			RelationInstance query2 = wsmoFactory.createRelationInstance(r2);
			query2.setParameterValue((byte) 0, dataFactory.createWsmlString("?x memberOf ?y"));
			// add NFPs to query 2
			description = "This query will return a number of result sets equivalent " +
					"to 2 x the number of memberof expressions in the ontology.";
			String result2 = "?x=i" + amount[i] +",?y=c2";
			addNFPs(query2, MEMBEROF_2, description, result2);
			ontology.addRelationInstance(query2);
			
			/*
			 * add non functional properties to the ontology: title, description
			 */
			String title = "Memberof and subconcept expressions";
			description = "\n\t\t\t This ontology is containing both memberof and simple hierarchy " +
					"expressions.\n\n\t\t\t " +
					"The x-axis value of the graph indicates the number of memberof expressions." +
					"\n\n\t\t\t Ontology example: \n\t\t\t concept c1 \n\t\t\t concept c2 subConceptOf c1" +
					"\n\t\t\t instance i1 memberOf c2\n\t\t\t instance i2 memberOf c2\n\n\t\t\t " +
					"The following two queries are applied to it:\n\t\t\t " +
					"Query 1: ?x memberOf c1\n\t\t\t Query 2: ?x memberOf ?y";
			ontology = addNFPs(ontology, title, description, amount[i]);
			
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
			
			/*
			 * add queries to ontology in form of relationinstances
			 */ 
			// create query 1
			Relation r1 = wsmoFactory.createRelation(wsmoFactory.createIRI(ns, "query1"));
			r1.createParameter((byte) 0);
			RelationInstance query1 = wsmoFactory.createRelationInstance(r1);
			query1.setParameterValue((byte) 0, dataFactory.createWsmlString("?x memberOf c1"));
			// add NFPs to query 1
			String description = "This query will return a number of result sets equivalent " +
					"to the number of memberof expressions in the ontology.";
			String result1 = "?x=i" + amount[i];
			addNFPs(query1, MEMBEROF_1, description, result1);
			ontology.addRelationInstance(query1);
			// create query 2
			Relation r2 = wsmoFactory.createRelation(wsmoFactory.createIRI(ns, "query2"));
			r2.createParameter((byte) 0);
			RelationInstance query2 = wsmoFactory.createRelationInstance(r2);
			query2.setParameterValue((byte) 0, dataFactory.createWsmlString("?x memberOf ?y"));
			// add NFPs to query 2
			description = "The number of result sets returned by this query is growing exponentially " +
					"in relation to the amount of subconcept expressions in the ontology.";
			String result2 = "?x=i" + amount[i] + ",?y=c" + amount[i];
			addNFPs(query2, MEMBEROF_2, description, result2);
			ontology.addRelationInstance(query2);

			/*
			 * add non functional properties to the ontology: title, description
			 */
			String title = "Memberof and deep subconcept expressions";
			description = "\n\t\t\t This ontology is containing both memberof and deep hierarchy " +
					"expressions.\n\n\t\t\t " +
					"The x-axis value of the graph indicates the number of memberOf and of subconcept " +
					"expressions." +
					"\n\n\t\t\t Ontology example: \n\t\t\t concept c1 \n\t\t\t concept c2 subConceptOf c1" +
					"\n\t\t\t concept c3 subConceptOf c2" +
					"\n\t\t\t instance i1 memberOf c2\n\t\t\t instance i2 memberOf c3\n\n\t\t\t " +
					"The following two queries are applied to it:\n\t\t\t " +
					"Query 1: ?x memberOf c1\n\t\t\t Query 2: ?x memberOf ?y";
			ontology = addNFPs(ontology, title, description, amount[i]);
			
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

			/*
			 * add queries to ontology in form of relationinstances
			 */ 
			// create query 1
			Relation r1 = wsmoFactory.createRelation(wsmoFactory.createIRI(ns, "query1"));
			r1.createParameter((byte) 0);
			RelationInstance query1 = wsmoFactory.createRelationInstance(r1);
			query1.setParameterValue((byte) 0, dataFactory.createWsmlString("i1[a1 hasValue ?x]"));
			// add NFPs to query 1
			String description = "This query will return exactly one result set.";
			String result1 = "?x=i2";
			addNFPs(query1, ATTR_VALUE_1, description, result1);
			ontology.addRelationInstance(query1);
			// create query 2
			Relation r2 = wsmoFactory.createRelation(wsmoFactory.createIRI(ns, "query2"));
			r2.createParameter((byte) 0);
			RelationInstance query2 = wsmoFactory.createRelationInstance(r2);
			query2.setParameterValue((byte) 0, dataFactory.createWsmlString("?x[?y hasValue ?z]"));
			// add NFPs to query 2
			description = "";
			String result2 = "This query returns a number of result sets equivalent to the number " +
					"of attribute value expressions in the ontology.";
			addNFPs(query2, ATTR_VALUE_2, description, result2);
			ontology.addRelationInstance(query2);

			/*
			 * add non functional properties to the ontology: title, description
			 */
			String title = "Constraining attribute expressions";
			description = "\n\t\t\t This ontology is containing constraining attribute " +
					"expressions.\n\n\t\t\t " +
					"The x-axis value of the graph indicates the number of constraining " +
					"attribute and attribute value expressions." +
					"\n\n\t\t\t Ontology example: \n\t\t\t concept c1 \n\t\t\t concept c2" +
					"\n\t\t\t \t a1 ofType c1" +
					"\n\t\t\t instance i1 memberOf c2\n\t\t\t \t a1 hasValue i2" +
					"\n\t\t\t instance i2 memberOf c1\n\n\t\t\t " +
					"The following two queries are applied to it:\n\t\t\t " +
					"Query 1: i1[a1 hasValue ?y] \n\t\t\t Query 2: ?x[?y hasValue ?z]";
			ontology = addNFPs(ontology, title, description, amount[i]);
			
			// write ontology file
			writeFile(OFTYPE, getFileName(OFTYPE, amount[i]), ontology);
		}
	}	
	
	/**
	 * Generate ontologies containing simple hierarchy and constraining 
	 * attribute value expressions like e.g.:
	 * concept Human
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
			concept2.addSuperConcept(concept1);
			ontology.addConcept(concept2);
			ontology.addConcept(concept1);
			
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

			/*
			 * add queries to ontology in form of relationinstances
			 */ 
			// create query 1
			Relation r1 = wsmoFactory.createRelation(wsmoFactory.createIRI(ns, "query1"));
			r1.createParameter((byte) 0);
			RelationInstance query1 = wsmoFactory.createRelationInstance(r1);
			query1.setParameterValue((byte) 0, dataFactory.createWsmlString(
					"i1[a1 hasValue ?x]"));
			// add NFPs to query 1
			String description = "This query will return exactly one result set, as it is querying " +
					"for the value of one specific attribute.";
			String result1 = "?x=i2";
			addNFPs(query1, ATTR_VALUE_1, description, result1);
			ontology.addRelationInstance(query1);
			// create query 2
			Relation r2 = wsmoFactory.createRelation(wsmoFactory.createIRI(ns, "query2"));
			r2.createParameter((byte) 0);
			RelationInstance query2 = wsmoFactory.createRelationInstance(r2);
			query2.setParameterValue((byte) 0, dataFactory.createWsmlString(
					"?x[?y hasValue ?z]"));
			// add NFPs to query 2
			description = "This query will return a number of result sets equivalent to the " +
					"amount of attribute value expressions in the ontology.";
			String result2 = "?x=i1,?y=a" + amount[i] + ",?z=i2";
			addNFPs(query2, ATTR_VALUE_2, description, result2);
			ontology.addRelationInstance(query2);

			/*
			 * add non functional properties to the ontology: title, description
			 */
			String title = "Constraining attributes with subconcept expressions";
			description = "\n\t\t\t This ontology is containing constraining attribute and simple " +
					"hierarchy expressions.\n\n\t\t\t " +
					"The x-axis value of the graph indicates the number of constraining attribute " +
					"and attribute value expressions." +
					"\n\n\t\t\t Ontology example: \n\t\t\t concept c1 \n\t\t\t \t a1 ofType c2" +
					"\n\t\t\t concept c2 subConceptOf c1" +
					"\n\t\t\t instance i1 memberOf c1 \n\t\t\t \t a1 hasValue i2" +
					"\n\t\t\t instance i2 memberOf c2\n\n\t\t\t " +
					"The following two queries are applied to it:\n\t\t\t " +
					"Query 1: i1[a1 hasValue ?y]" +
					"\n\t\t\t Query 2: ?x[?y hasValue ?z]";
			ontology = addNFPs(ontology, title, description, amount[i]);
			
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
				attribute.setMinCardinality(0);
				attribute.setMaxCardinality(1);
				instance1.addAttributeValue(attribute.getIdentifier(), instance2);
			}

			/*
			 * add queries to ontology in form of relationinstances
			 */ 
			// create query 1
			Relation r1 = wsmoFactory.createRelation(wsmoFactory.createIRI(ns, "query1"));
			r1.createParameter((byte) 0);
			RelationInstance query1 = wsmoFactory.createRelationInstance(r1);
			query1.setParameterValue((byte) 0, dataFactory.createWsmlString("i1[a1 hasValue ?x]"));
			// add NFPs to query 1
			String description = "This query will return exactly one result set, as it is querying " +
					"for the value of one specific attribute.";
			String result1 = "?x=i2";
			addNFPs(query1, ATTR_VALUE_1, description, result1);
			ontology.addRelationInstance(query1);
			// create query 2
			Relation r2 = wsmoFactory.createRelation(wsmoFactory.createIRI(ns, "query2"));
			r2.createParameter((byte) 0);
			RelationInstance query2 = wsmoFactory.createRelationInstance(r2);
			query2.setParameterValue((byte) 0, dataFactory.createWsmlString("?x[?y hasValue ?z]"));
			// add NFPs to query 2
			description = "This query will return a number of result sets equivalent to the " +
					"amount of attribute value expressions in the ontology.";
			String result2 = "?x=i1,?y=a" + amount[i] + ",?z=i2";
			addNFPs(query2, ATTR_VALUE_2, description, result2);
			ontology.addRelationInstance(query2);

			/*
			 * add non functional properties to the ontology: title, description
			 */
			String title = "Maximal cardinality contraint (0 1) expressions";
			description = "\n\t\t\t This ontology is containing cardinality constraint expressions " +
					"with a maximal cardinality of 1.\n\n\t\t\t " +
					"The x-axis value of the graph indicates the number of cardinality constraint expressions." +
					"\n\n\t\t\t Ontology example: \n\t\t\t concept c1 \n\t\t\t \t a1 ofType (0 1) c1" +
					"\n\t\t\t instance i1 memberOf c1 \n\t\t\t \t a1 hasValue i2" +
					"\n\t\t\t instance i2 memberOf c1\n\n\t\t\t " +
					"The following two queries are applied to it:\n\t\t\t " +
					"Query 1: i1[a1 hasValue ?y]\n\t\t\t Query 2: ?x[?y hasValue ?z]";
			ontology = addNFPs(ontology, title, description, amount[i]);
			
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
	 * instance Bart memberOf Human
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
			ontology.addInstance(instance1);
			ontology.addInstance(instance2);
			
			// add amount[i] number of entities with cardinality contraints
			Attribute attribute = null;
			for (int j = 0; j < amount[i]; j++) {
				attribute = concept.createAttribute(wsmoFactory.createIRI(ns, "a" + (j + 1)));
				attribute.addType(concept);
				attribute.setMinCardinality(0);
				attribute.setMaxCardinality(10);
				instance1.addAttributeValue(attribute.getIdentifier(), instance2);
			}
			
			/*
			 * add queries to ontology in form of relationinstances
			 */ 
			// create query 1
			Relation r1 = wsmoFactory.createRelation(wsmoFactory.createIRI(ns, "query1"));
			r1.createParameter((byte) 0);
			RelationInstance query1 = wsmoFactory.createRelationInstance(r1);
			query1.setParameterValue((byte) 0, dataFactory.createWsmlString("i1[a1 hasValue ?y]"));
			// add NFPs to query 1
			String description = "This query will return exactly one result set, as it is querying " +
					"for the value of one specific attribute.";
			String result1 = "?x=i2";
			addNFPs(query1, ATTR_VALUE_1, description, result1);
			ontology.addRelationInstance(query1);
			// create query 2
			Relation r2 = wsmoFactory.createRelation(wsmoFactory.createIRI(ns, "query2"));
			r2.createParameter((byte) 0);
			RelationInstance query2 = wsmoFactory.createRelationInstance(r2);
			query2.setParameterValue((byte) 0, dataFactory.createWsmlString("?x[?y hasValue ?z]"));
			// add NFPs to query 2
			description = "This query will return a number of result sets equivalent to the " +
					"amount of attribute value expressions in the ontology.";
			String result2 = "?x=i1,?y=a" + amount[i] + ",?z=i2";
			addNFPs(query2, ATTR_VALUE_2, description, result2);
			ontology.addRelationInstance(query2);

			/*
			 * add non functional properties to the ontology: title, description
			 */
			String title = "Maximal cardinality contraint (0 10) expressions";
			description = "\n\t\t\t This ontology is containing cardinality constraint expressions " +
					"with a maximal cardinality of 10.\n\n\t\t\t " +
					"The x-axis value of the graph indicates the number of cardinality constraint expressions." +
					"\n\n\t\t\t Ontology example: \n\t\t\t concept c1 \n\t\t\t \t a1 ofType (0 10) c1" +
					"\n\t\t\t instance i1 memberOf c1 \n\t\t\t \t a1 hasValue i2" +
					"\n\t\t\t instance i2 memberOf c1\n\n\t\t\t " +
					"The following two queries are applied to it:\n\t\t\t " +
					"Query 1: i1[a1 hasValue ?y]\n\t\t\t Query 2: ?x[?y hasValue ?z]";
			ontology = addNFPs(ontology, title, description, amount[i]);
			
			// write ontology file
			writeFile(CARDINALITY_010, getFileName(CARDINALITY_010, amount[i]), ontology);
		}
	}	

	/**
	 * Generate ontologies containing cardinality constraints like e.g.:
	 * concept Human
	 *   hasRelative ofType (1 *) Human
	 * instance Homer memberOf Human
	 *   hasRelative hasValue Bart
	 * instance Bart memberOf Human
	 * 
	 * Queries: 
	 * - Homer[hasRelative hasValue ?y]
	 * - ?x[?y hasValue ?z]
	 * 
	 * @throws IOException
	 * @throws SynchronisationException
	 * @throws InvalidModelException
	 */
	public void genMinCardinalityOntologies() 
			throws IOException, SynchronisationException, InvalidModelException {
		Ontology ontology = null;
		for (int i = 0; i < amount.length; i++) {	
			// create default namespace
			Namespace ns = wsmoFactory.createNamespace("", 
					wsmoFactory.createIRI(getNamespace(CARDINALITY_MIN, amount[i])));
			
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
			ontology.addInstance(instance1);
			ontology.addInstance(instance2);
			
			// add amount[i] number of entities with cardinality contraints
			Attribute attribute = null;
			for (int j = 0; j < amount[i]; j++) {
				attribute = concept.createAttribute(wsmoFactory.createIRI(ns, "a" + (j + 1)));
				attribute.addType(wsmoFactory.createConcept(wsmoFactory.createIRI(ns, "c2")));
				attribute.setMinCardinality(1);
				attribute.setMaxCardinality(Integer.MAX_VALUE);
				instance1.addAttributeValue(attribute.getIdentifier(), instance2);
			}
			
			/*
			 * add queries to ontology in form of relationinstances
			 */ 
			// create query 1
			Relation r1 = wsmoFactory.createRelation(wsmoFactory.createIRI(ns, "query1"));
			r1.createParameter((byte) 0);
			RelationInstance query1 = wsmoFactory.createRelationInstance(r1);
			query1.setParameterValue((byte) 0, dataFactory.createWsmlString("i1[a1 hasValue ?x]"));
			// add NFPs to query 1
			String description = "This query will return exactly one result set, as it is querying " +
					"for the value of one specific attribute.";
			String result1 = "?x=i2";
			addNFPs(query1, ATTR_VALUE_1, description, result1);
			ontology.addRelationInstance(query1);
			// create query 2
			Relation r2 = wsmoFactory.createRelation(wsmoFactory.createIRI(ns, "query2"));
			r2.createParameter((byte) 0);
			RelationInstance query2 = wsmoFactory.createRelationInstance(r2);
			query2.setParameterValue((byte) 0, dataFactory.createWsmlString("?x[?y hasValue ?z]"));
			// add NFPs to query 2
			description = "This query will return a number of result sets equivalent to the " +
					"amount of attribute value expressions in the ontology.";
			String result2 = "?x=i1,?y=a" + amount[i] + ",?z=i2";
			addNFPs(query2, ATTR_VALUE_2, description, result2);
			ontology.addRelationInstance(query2);

			/*
			 * add non functional properties to the ontology: title, description
			 */
			String title = "Minimal cardinality contraint (1 *) expressions";
			description = "\n\t\t\t This ontology is containing cardinality constraint expressions " +
					"with a minimal cardinality of 1.\n\n\t\t\t " +
					"The x-axis value of the graph indicates the number of cardinality constraint expressions." +
					"\n\t\t\t Ontology example: \n\t\t\t concept c1 \n\t\t\t \t a1 ofType (1 *) c1" +
					"\n\t\t\t instance i1 memberOf c1 \n\t\t\t \t a1 hasValue i2" +
					"\n\t\t\t instance i2 memberOf c1\n\n\t\t\t " +
					"The following two queries are applied to it:\n\t\t\t " +
					"Query 1: i1[a1 hasValue ?y]\n\t\t\t Query 2: ?x[?y hasValue ?z]";
			ontology = addNFPs(ontology, title, description, amount[i]);
			
			// write ontology file
			writeFile(CARDINALITY_MIN, getFileName(CARDINALITY_MIN, amount[i]), ontology);
		}
	}	
	
	/**
	 * Generate ontologies containing inverse attribute features like 
	 * e.g.:
	 * concept Woman
	 *   hasHusband inverseOf(hasWife) impliesType Man
	 * concept Man
	 *   hasWife impliesType Woman
	 * instance Marge memberOf Woman
	 * instance Homer memberOf Man
	 *   hasWife hasValue Marge
	 * 
	 * Queries: 
	 * - Marge[hasHusband hasValue ?x]
	 * - Marge[?x hasValue ?y]
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
				attribute.setInverseOf(attribute1.getIdentifier());
				instance2.addAttributeValue(attribute1.getIdentifier(), instance1);
			}
			
			/*
			 * add queries to ontology in form of relationinstances
			 */ 
			// create query 1
			Relation r1 = wsmoFactory.createRelation(wsmoFactory.createIRI(ns, "query1"));
			r1.createParameter((byte) 0);
			RelationInstance query1 = wsmoFactory.createRelationInstance(r1);
			query1.setParameterValue((byte) 0, dataFactory.createWsmlString("i1[a1 hasValue ?x]"));
			// add NFPs to query 1
			String description = "This query will return exactly one result set, as it is querying " +
					"for the value of one specific attribute.";
			String result1 = "?x=i2";
			addNFPs(query1, ATTR_VALUE_1, description, result1);
			ontology.addRelationInstance(query1);
			// create query 2
			Relation r2 = wsmoFactory.createRelation(wsmoFactory.createIRI(ns, "query2"));
			r2.createParameter((byte) 0);
			RelationInstance query2 = wsmoFactory.createRelationInstance(r2);
			query2.setParameterValue((byte) 0, dataFactory.createWsmlString("i1[?x hasValue ?y]"));
			// add NFPs to query 2
			description = "This query will return a number of result sets equivalent to the " +
					"amount of inverse attribute expressions in the ontology.";
			String result2 = "?x=a" + amount[i] + ",?y=i2";
			addNFPs(query2, ATTR_VALUE_2, description, result2);
			ontology.addRelationInstance(query2);

			/*
			 * add non functional properties to the ontology: title, description
			 */
			String title = "Inverse attribute feature";
			description = "\n\t\t\t This ontology is containing inverse attribute expressions.\n\n\t\t\t " +
					"The x-axis value of the graph indicates the number of inverse attribute and " +
					"attribute value expressions." +
					"\n\n\t\t\t Ontology example: \n\t\t\t concept c1 \n\t\t\t \t a1 inverseOf(a2) impliesType c2" +
					"\n\t\t\t concept c2 \n\t\t\t \t a2 impliesType c1" +
					"\n\t\t\t instance i1 memberOf c1" +
					"\n\t\t\t instance i2 memberOf c2 \n\t\t\t \t a2 hasValue i1 \n\n\t\t\t " +
					"The following two queries are applied to it:\n\t\t\t " +
					"Query 1: i1[a1 hasValue ?x]\n\t\t\t Query 2: i1[?x hasValue ?y]";
			ontology = addNFPs(ontology, title, description, amount[i]);
			
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
	 * - ?x[?y hasValue ?z]
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
			
			/*
			 * add queries to ontology in form of relationinstances
			 */ 
			// create query 1
			Relation r1 = wsmoFactory.createRelation(wsmoFactory.createIRI(ns, "query1"));
			r1.createParameter((byte) 0);
			RelationInstance query1 = wsmoFactory.createRelationInstance(r1);
			query1.setParameterValue((byte) 0, dataFactory.createWsmlString("i1[a0 hasValue ?x]"));
			// add NFPs to query 1
			String description = "This query will return two result sets; it is querying " +
					"for the value of one specific transitive attribute.";
			String result1 = "?x=i3";
			addNFPs(query1, ATTR_VALUE_1, description, result1);
			ontology.addRelationInstance(query1);
			// create query 2
			Relation r2 = wsmoFactory.createRelation(wsmoFactory.createIRI(ns, "query2"));
			r2.createParameter((byte) 0);
			RelationInstance query2 = wsmoFactory.createRelationInstance(r2);
			query2.setParameterValue((byte) 0, dataFactory.createWsmlString(
					"?x[" + ((IRI) attribute.getIdentifier()).getLocalName() + " hasValue ?y]"));
			// add NFPs to query 2
			description = "This query will return a number of result set that is growing " +
					"in a linear way with the amount of attribute value expressions in the " +
					"ontology.";
			String result2 = "";
			addNFPs(query2, ATTR_VALUE_2, description, result2);
			ontology.addRelationInstance(query2);

			/*
			 * add non functional properties to the ontology: title, description
			 */
			String title = "Transitive attribute feature";
			description = "\n\t\t\t This ontology is containing transitive attribute expressions.\n\n\t\t\t " +
					"The x-axis value of the graph indicates the number of transitive attribute and " +
			"attribute value expressions." +
			"\n\n\t\t\t Ontology example: \n\t\t\t concept c1 \n\t\t\t \t a1 transitive impliesType c1" +
			"\n\t\t\t instance i1 \n\t\t\t \t a1 hasValue i2" +
			"\n\t\t\t instance i2 \n\t\t\t \t a2 hasValue i3" +
			"\n\t\t\t instance i3 \n\n\t\t\t " +
			"The following two queries are applied to it:\n\t\t\t " +
			"Query 1: i1[a1 hasValue ?x]\n\t\t\t Query 2: ?x[?y hasValue ?z]";
			ontology = addNFPs(ontology, title, description, amount[i]);
			
			// write ontology file
			writeFile(TRANSITIVE, getFileName(TRANSITIVE, amount[i]), ontology);
		}
	}	

	/**
	 * Generate ontologies containing symmetric attribute features like 
	 * e.g.:
	 * concept Human
	 *   hasRelative symmetric impliesType Human
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
				attribute.setSymmetric(true);
				instance1.addAttributeValue(attribute.getIdentifier(), instance2);
			}

			/*
			 * add queries to ontology in form of relationinstances
			 */ 
			// create query 1
			Relation r1 = wsmoFactory.createRelation(wsmoFactory.createIRI(ns, "query1"));
			r1.createParameter((byte) 0);
			RelationInstance query1 = wsmoFactory.createRelationInstance(r1);
			query1.setParameterValue((byte) 0, dataFactory.createWsmlString("i2[a0 hasValue ?x]"));
			// add NFPs to query 1
			String description = "This query will return exactly one result set as it is querying " +
					"for the value of one specific symmetric attribute.";
			String result1 = "?x=i1";
			addNFPs(query1, ATTR_VALUE_1, description, result1);
			ontology.addRelationInstance(query1);
			// create query 2
			Relation r2 = wsmoFactory.createRelation(wsmoFactory.createIRI(ns, "query2"));
			r2.createParameter((byte) 0);
			RelationInstance query2 = wsmoFactory.createRelationInstance(r2);
			query2.setParameterValue((byte) 0, dataFactory.createWsmlString(
					"?x[" + ((IRI) attribute.getIdentifier()).getLocalName() + " hasValue ?y]"));
			ontology.addRelationInstance(query2);
			// add NFPs to query 2
			description = "This query will return two result set as it is querying " +
					"for the value of one specific symmetric attribute.";
			String result2 = "?x=i2,?y=i1";
			addNFPs(query2, ATTR_VALUE_2, description, result2);
			ontology.addRelationInstance(query2);

			/*
			 * add non functional properties to the ontology: title, description
			 */
			String title = "Symmetric attribute feature";
			description = "\n\t\t\t This ontology is containing symmetric attribute expressions.\n\n\t\t\t " +
					"The x-axis value of the graph indicates the number of symmetric attribute and " +
					"attribute value expressions." +
					"\n\n\t\t\t Ontology example: \n\t\t\t concept c1 \n\t\t\t \t a1 symmetric impliesType c1" +
					"\n\t\t\t instance i1 \n\t\t\t \t a1 hasValue i2" +
					"\n\t\t\t instance i2 \n\n\t\t\t " +
					"The following two queries are applied to it:\n\t\t\t " +
					"Query 1: i1[a1 hasValue ?x]\n\t\t\t Query 2: ?x[a1 hasValue ?y]";
			ontology = addNFPs(ontology, title, description, amount[i]);
			
			// write ontology file
			writeFile(SYMMETRIC, getFileName(SYMMETRIC, amount[i]), ontology);
		}
	}	
	
	/**
	 * Generate ontologies containing reflexive attribute features like 
	 * e.g.:
	 * concept Human
	 *   loves reflexive impliesType Human
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
			
			// add instances
			Instance instance1 = wsmoFactory.createInstance(
					wsmoFactory.createIRI(ns, "i1"));
			ontology.addInstance(instance1);

			// add amount[i] number of reflexive attribute entities
			Attribute attribute = null;
			for (int j = 0; j < amount[i]; j++) {
				attribute = concept.createAttribute(wsmoFactory.createIRI(ns, "a" + (j + 1)));
				attribute.addType(concept);
				attribute.setReflexive(true);
			}

			/*
			 * add queries to ontology in form of relationinstances
			 */ 
			// create query 1
			Relation r1 = wsmoFactory.createRelation(wsmoFactory.createIRI(ns, "query1"));
			r1.createParameter((byte) 0);
			RelationInstance query1 = wsmoFactory.createRelationInstance(r1);
			query1.setParameterValue((byte) 0, dataFactory.createWsmlString("i1[a1 hasValue ?x]"));
			// add NFPs to query 1
			String description = "This query will return exactly one result set as it is querying " +
					"for the value of one specific reflexive attribute.";
			String result1 = "?x=i1";
			addNFPs(query1, ATTR_VALUE_1, description, result1);
			ontology.addRelationInstance(query1);
			// create query 2
			Relation r2 = wsmoFactory.createRelation(wsmoFactory.createIRI(ns, "query2"));
			r2.createParameter((byte) 0);
			RelationInstance query2 = wsmoFactory.createRelationInstance(r2);
			query2.setParameterValue((byte) 0, dataFactory.createWsmlString("i1[?x hasValue ?y]"));
			// add NFPs to query 2
			description = "This query will return a number of result sets equivalent to the " +
					"amount of reflexive attribute expressions in the ontology.";
			String result2 = "?x=a" + amount[i] + ",?y=i1";
			addNFPs(query2, ATTR_VALUE_2, description, result2);
			ontology.addRelationInstance(query2);

			/*
			 * add non functional properties to the ontology: title, description
			 */
			String title = "Reflexive attribute feature";
			description = "\n\t\t\t This ontology is containing reflexive attribute expressions.\n\n\t\t\t " +
					"The x-axis value of the graph indicates the number of reflexive attribute expressions." +
					"\n\n\t\t\t Ontology example: \n\t\t\t concept c1 \n\t\t\t \t a1 reflexive impliesType c1" +
					"\n\t\t\t instance i1 \n\n\t\t\t " +
					"The following two queries are applied to it:\n\t\t\t " +
					"Query 1: i1[a1 hasValue ?x]\n\t\t\t Query 2: i1[?x hasValue ?y]";
			ontology = addNFPs(ontology, title, description, amount[i]);
			
			// write ontology file
			writeFile(REFLEXIVE, getFileName(REFLEXIVE, amount[i]), ontology);
		}
	}	

	/**
	 * Generate ontologies containing logical expressions with locally 
	 * stratified negation 
	 * e.g.:
	 * concept Human
	 *   knows impliesType Human
	 *   distrust impliesType Human	
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
			ontology.addInstance(instance1);

			// add amount[i] number of logical expressions containing negation
			Attribute attribute;
			AttributeValueMolecule molecule1, molecule2;
			MembershipMolecule memberMol1, memberMol2;
			NegationAsFailure naf;
			Conjunction con1, con2;
			LogicProgrammingRule lpRule;
			/* create the following logical expression programmaticaly:
			 * "?x[a" + j + " hasValue ?y] :- naf ?x[a" + (j+1) + " hasValue ?y] " +
			 * "and ?x memberOf c1 and ?y memberOf c1" */
			for (int j = 0; j < amount[i]; j++) {
				attribute = concept.createAttribute(wsmoFactory.createIRI(ns, "a" + j));
				attribute.addType(concept);
				attribute = concept.createAttribute(wsmoFactory.createIRI(ns, "a" + (j+1)));
				attribute.addType(concept);
				molecule1 = leFactory.createAttributeValue(leFactory.createVariable("?x"), 
						wsmoFactory.createIRI(ns, "a" + j), leFactory.createVariable("?y"));
				molecule2 = leFactory.createAttributeValue(leFactory.createVariable("?x"), 
						wsmoFactory.createIRI(ns, "a" + (j+1)), leFactory.createVariable("?y"));
				memberMol1 = leFactory.createMemberShipMolecule(leFactory.createVariable("?x"), 
						wsmoFactory.createIRI(ns, "c1"));
				memberMol2 = leFactory.createMemberShipMolecule(leFactory.createVariable("?y"), 
						wsmoFactory.createIRI(ns, "c1"));
				naf = leFactory.createNegationAsFailure(molecule2);
				con1 = leFactory.createConjunction(naf, memberMol1);
				con2 = leFactory.createConjunction(con1, memberMol2);
				lpRule = leFactory.createLogicProgrammingRule(molecule1, con2); 
				axiom.addDefinition(lpRule);
			}

			/*
			 * add queries to ontology in form of relationinstances
			 */ 
			// create query 1
			Relation r1 = wsmoFactory.createRelation(wsmoFactory.createIRI(ns, "query1"));
			r1.createParameter((byte) 0);
			RelationInstance query1 = wsmoFactory.createRelationInstance(r1);
			query1.setParameterValue((byte) 0, dataFactory.createWsmlString("i1[a0 hasValue ?x]"));
			// add NFPs to query 1
			String description = "This query will return exactly one result set; it is querying " +
					"for the value of one specific attribute that gets a value as result of " +
					"evaluation of the logical expression.";
			String result1 = "?x=i1";
			addNFPs(query1, ATTR_VALUE_1, description, result1);
			ontology.addRelationInstance(query1);
			// create query 2
			Relation r2 = wsmoFactory.createRelation(wsmoFactory.createIRI(ns, "query2"));
			r2.createParameter((byte) 0);
			RelationInstance query2 = wsmoFactory.createRelationInstance(r2);
			query2.setParameterValue((byte) 0, dataFactory.createWsmlString(
					"?x[a" + (amount[i]-1) + " hasValue ?y]"));
			// add NFPs to query 2
			description = "This query will return a number of result sets that is equivalent to the " +
					"amount of attribute definitions; it is querying " +
					"for the value of one specific attribute that gets a value as result of " +
					"evaluation of the logical expression.";
			String result2 = "?x=i1,?y=i1";
			addNFPs(query2, ATTR_VALUE_2, description, result2);
			ontology.addRelationInstance(query2);

			/*
			 * add non functional properties to the ontology: title, description
			 */
			String title = "Logical expressions with locally stratified negation";
			description = "\n\t\t\t This ontology is containing logical expressions with " +
					"locally stratified negation.\n\n\t\t\t " +
					"The x-axis value of the graph indicates the number of logical expressions and " +
					"of attribute expressions." +
					"\n\n\t\t\t Ontology example: \n\t\t\t concept c1 \n\t\t\t \t a1 impliesType c1" +
					"\n\t\t\t \t a2 impliesType c1 \n\t\t\t instance i1 " +
					"\n\t\t\t axiom definedBy \n\t\t\t \t ?x[a2 hasValue ?y] :- naf ?x[a1 hasValue ?y] " +
					"and ?x memberOf c1 and ?y memberOf c1.\n\n\t\t\t" +
					"The following two queries are applied to it:\n\t\t\t " +
					"Query 1: i1[a2 hasValue ?x]\n\t\t\t Query 2: ?x[a2 hasValue ?y]";
			ontology = addNFPs(ontology, title, description, amount[i]);
			
			// write ontology file
			writeFile(LOC_STRAT_NEGATION, getFileName(LOC_STRAT_NEGATION, amount[i]), ontology);
		}
	}	
	
	/**
	 * Generate ontologies containing logical expressions with globally 
	 * stratified negation 
	 * e.g.:
	 * concept Human
	 *   knows impliesType Human
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

			// add amount[i] number of logical expressions containing negation
			Attribute attribute;
			Instance instance;
			MembershipMolecule memberMol1, memberMol2, memberMol3;
			AttributeValueMolecule molecule;
			NegationAsFailure naf;
			Conjunction con1, con2;
			LogicProgrammingRule lpRule;
			for (int j = 0; j < amount[i]; j++) {
				attribute = concept1.createAttribute(wsmoFactory.createIRI(ns, "a" + (j+1)));
				attribute.addType(concept1);
				instance = wsmoFactory.createInstance(wsmoFactory.createIRI(ns, "i" + (j+1)));
				ontology.addInstance(instance);
				/* create the following logical expression programmaticaly:
				 * "?y memberOf c2 :- naf ?x[a" + j + " hasValue ?y] " +
				 * "and ?x memberOf c2 and ?y memberOf c1" */ 
				memberMol1 = leFactory.createMemberShipMolecule(leFactory.createVariable("?y"),
						wsmoFactory.createIRI(ns, "c2"));
				molecule = leFactory.createAttributeValue(leFactory.createVariable("?x"),
						wsmoFactory.createIRI(ns, "a" + (j+1)), leFactory.createVariable("?y"));
				memberMol2 = leFactory.createMemberShipMolecule(leFactory.createVariable("?x"), 
						wsmoFactory.createIRI(ns, "c2"));
				memberMol3 = leFactory.createMemberShipMolecule(leFactory.createVariable("?y"), 
						wsmoFactory.createIRI(ns, "c1"));
				naf = leFactory.createNegationAsFailure(molecule);
				con1 = leFactory.createConjunction(naf, memberMol2);
				con2 = leFactory.createConjunction(con1, memberMol3);
				lpRule = leFactory.createLogicProgrammingRule(memberMol1, con2);
				axiom.addDefinition(lpRule);
			}

			/*
			 * add queries to ontology in form of relationinstances
			 */ 
			// create query 1
			Relation r1 = wsmoFactory.createRelation(wsmoFactory.createIRI(ns, "query1"));
			r1.createParameter((byte) 0);
			RelationInstance query1 = wsmoFactory.createRelationInstance(r1);
			query1.setParameterValue((byte) 0, dataFactory.createWsmlString("?x memberOf c2"));
			// add NFPs to query 1
			String description = "This query will return a number of result sets equivalent " +
					"to the number of instances in the ontology, i.e. exactly one result set " +
					"for this ontology.";
			String result1 = "?x=i1";
			addNFPs(query1, MEMBEROF_1, description, result1);
			ontology.addRelationInstance(query1);

			/*
			 * add non functional properties to the ontology: title, description
			 */
			String title = "Logical expressions with globally stratified negation";
			description = "\n\t\t\t This ontology is containing logical expressions with " +
					"globally stratified negation.\n\n\t\t\t " +
					"The x-axis value of the graph indicates the number of logical expressions and " +
					"of attribute and instance expressions." +
					"\n\n\t\t\t Ontology example: \n\t\t\t concept c1 \n\t\t\t \t a1 impliesType c1" +
					"\n\t\t\t concept c2 \n\t\t\t instance i1 " +
					"\n\t\t\t axiom definedBy \n\t\t\t \t ?x memberOf c2 :- naf ?x[a1 hasValue ?y] " +
					"and ?x memberOf c1 and ?y memberOf c1.\n\n\t\t\t" +
					"The following two queries are applied to it:\n\t\t\t " +
					"Query 1: ?x memberOf c2";
			ontology = addNFPs(ontology, title, description, amount[i]);
			
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
	 * 	 ?x[hasAge hasValue ?y] and (((?y*2)/4)+1) < 16 implies ?x memberOf Child .
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
			Axiom axiom = wsmoFactory.createAxiom(wsmoFactory.createIRI(ns, "ax1"));
			ontology.addAxiom(axiom);

			// add instance
			Instance instance1 = wsmoFactory.createInstance(wsmoFactory.createIRI(ns, "i0"));
			instance1.addAttributeValue(wsmoFactory.createIRI(ns, "a0"), 
					dataFactory.createWsmlInteger("10"));
			
			// add amount[i] number of logical expressions containing built-ins
			Instance instance;
			AttributeValueMolecule molecule;
			MembershipMolecule memberMol;
			Atom builtIn1;
			ConstructedTerm builtIn2, builtIn3, builtIn4;
			List<Term> terms1 = new LinkedList<Term>();
			List<Term> terms2 = new LinkedList<Term>();
			List<Term> terms3 = new LinkedList<Term>();
			List<Term> terms4 = new LinkedList<Term>();
			Conjunction con;
			Implication impl;
			Random random = new Random();
			for (int j = 0; j < amount[i]; j++) {
				instance = wsmoFactory.createInstance(wsmoFactory.createIRI(ns, "i" + (j+1)));
				instance.addAttributeValue(wsmoFactory.createIRI(ns, "a" + (j+1)), 
						dataFactory.createWsmlInteger("" + (Math.abs(random.nextInt()) % 80)));
				/* create the following logical expression programmaticaly:
				 * "?x[a" + j + " hasValue ?y] and (((?y*2)/4)+1) < 16 implies ?x memberOf c2" */
				molecule = leFactory.createAttributeValue(leFactory.createVariable("?x"), 
						wsmoFactory.createIRI(ns, "a" + j), leFactory.createVariable("?y"));
				terms2.clear();
				terms2.add(leFactory.createVariable("?y"));
				terms2.add(dataFactory.createWsmlInteger("2"));
				builtIn2 = leFactory.createConstructedTerm(
						wsmoFactory.createIRI(Constants.NUMERIC_MUL), terms2);
				terms3.clear();
				terms3.add(builtIn2);
				terms3.add(dataFactory.createWsmlInteger("4"));
				builtIn3 = leFactory.createConstructedTerm(
						wsmoFactory.createIRI(Constants.NUMERIC_DIV), terms3);
				terms4.clear();
				terms4.add(builtIn3);
				terms4.add(dataFactory.createWsmlInteger("1"));
				builtIn4 = leFactory.createConstructedTerm(
						wsmoFactory.createIRI(Constants.NUMERIC_ADD), terms4);
				terms1.clear();
				terms1.add(builtIn4);
				terms1.add(dataFactory.createWsmlInteger("16"));
				builtIn1 = leFactory.createAtom(wsmoFactory.createIRI(Constants.LESS_THAN), 
						terms1);
				memberMol = leFactory.createMemberShipMolecule(leFactory.createVariable("?x"),
						wsmoFactory.createIRI(ns, "c2"));
				con = leFactory.createConjunction(molecule, builtIn1);
				impl = leFactory.createImplication(con, memberMol);
				axiom.addDefinition(impl);
				ontology.addInstance(instance);
			}

			/*
			 * add queries to ontology in form of relationinstances
			 */ 
			// create query 1
			Relation r1 = wsmoFactory.createRelation(wsmoFactory.createIRI(ns, "query1"));
			r1.createParameter((byte) 0);
			RelationInstance query1 = wsmoFactory.createRelationInstance(r1);
			query1.setParameterValue((byte) 0, dataFactory.createWsmlString("?x memberOf c2"));
			// add NFPs to query 1
			String description = "This query will return a number of result sets which is " +
					"depending on the random factor at creating the logical expressions for " +
					"the ontology. The maximal number of result sets is the number of " +
					"instances in the ontology.";
			String result1 = "?x=i0";
			addNFPs(query1, MEMBEROF_1, description, result1);
			ontology.addRelationInstance(query1);

			/*
			 * add non functional properties to the ontology: title, description
			 */
			String title = "Logical expressions with built-ins";
			description = "\n\t\t\t This ontology is containing logical expressions with " +
					"built-ins.\n\n\t\t\t " +
					"The x-axis value of the graph indicates the number of logical expressions and " +
					"of instances (with one attribute value per instance)." +
					"\n\n\t\t\t Ontology example: \n\t\t\t concept c1 \n\t\t\t \t a1 ofType _integer" +
					"\n\t\t\t instance i1 memberOf c1 \n\t\t\t \t a1 hasValue 17" +
					"\n\t\t\t instance i2 memberOf c1 \n\t\t\t \t a1 hasValue 14" +
					"\n\t\t\t axiom definedBy \n\t\t\t \t ?x[a1 hasValue ?y] and ?y < 16 implies " +
					"?x memberOf c2.\n\n\t\t\t " +
					"The following two queries are applied to it:\n\t\t\t " +
					"Query 1: ?x memberOf c2 \n\t\t\t Query 2: ?x memberOf ?y";
			ontology = addNFPs(ontology, title, description, amount[i]);
			
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
	
	/*
	 * Create an ontology and add namespaces to it
	 */
	private Ontology createOntology(Namespace ns, int no){
		DecimalFormat dformat = new DecimalFormat("00000");
		Ontology ontology = wsmoFactory.createOntology(
				wsmoFactory.createIRI(ns,"o_"+dformat.format(no)));
		Namespace dcNs = wsmoFactory.createNamespace("dc", 
				wsmoFactory.createIRI("http://purl.org/dc/elements/1.1#"));
		ontology.addNamespace(dcNs);
		Namespace bmNs = wsmoFactory.createNamespace("bm", 
				wsmoFactory.createIRI("http://wsml2reasoner/benchmarks#"));
		ontology.addNamespace(bmNs);
		return ontology;
	}
	
	/*
	 * Add non functional properties to the given ontology:
	 * - dc:title (title given as parameter)
	 * - dc:description
	 */
	private Ontology addNFPs(Ontology ontology, String title, String description, 
			int replications) 
			throws SynchronisationException, InvalidModelException {
		
		ontology.addNFPValue(wsmoFactory.createIRI(DC_TITLE), 
				dataFactory.createWsmlString(title));
		ontology.addNFPValue(wsmoFactory.createIRI(DC_DESCRIPTION), 
				dataFactory.createWsmlString(description));
		
		// collect amount of ontology terms
		List<Term> concepts = OntologyUtil.getConcepts(ontology);
		int attributes = 0;
		for (Term c : concepts) {
			attributes += OntologyUtil.getAttributes(c, ontology).size();
		}
		int instances = OntologyUtil.getInstances(ontology).size();
		
		ontology.addNFPValue(wsmoFactory.createIRI(BM_CONCEPTS), 
				dataFactory.createWsmlInteger("" + concepts.size()));
		ontology.addNFPValue(wsmoFactory.createIRI(BM_INSTANCES), 
				dataFactory.createWsmlInteger("" + instances));
		ontology.addNFPValue(wsmoFactory.createIRI(BM_ATTRIBUTES), 
				dataFactory.createWsmlInteger("" + attributes));
		ontology.addNFPValue(wsmoFactory.createIRI(BM_TOTAL), 
				dataFactory.createWsmlInteger("" + 
						(concepts.size() + instances + attributes)));
		ontology.addNFPValue(wsmoFactory.createIRI(BM_REPLICATED), 
				dataFactory.createWsmlInteger("" + replications));
		
		return ontology;
	}
	
	/*
	 * Add non functional properties to the given relation instance
	 */
	private RelationInstance addNFPs(RelationInstance relInst, String title, 
			String description, String result) 
			throws SynchronisationException, InvalidModelException {
		relInst.addNFPValue(wsmoFactory.createIRI(DC_TITLE), 
				dataFactory.createWsmlString(title));
		relInst.addNFPValue(wsmoFactory.createIRI(DC_DESCRIPTION), 
				dataFactory.createWsmlString(description));
		relInst.addNFPValue(wsmoFactory.createIRI(BM_RESULT), 
				dataFactory.createWsmlString(result));
		
		return relInst;
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


	

}
/*
 * $Log: not supported by cvs2svn $
 * Revision 1.16  2007-06-21 11:25:05  nathalie
 * added query numbering to query names and fixed inconsistencies in minimal cardinality ontologies
 *
 * Revision 1.15  2007-06-21 10:57:28  hlausen
 * *** empty log message ***
 *
 * Revision 1.14  2007-06-21 10:34:41  nathalie
 * more nfps and slight changes in the ontology building
 *
 * Revision 1.13  2007-06-21 07:52:12  hlausen
 * more steps no iteration per step!
 *
 * Revision 1.12  2007-06-21 07:48:19  hlausen
 * *** empty log message ***
 *
 * Revision 1.11  2007-06-21 07:28:50  nathalie
 * fixed bug in attribute counting
 *
 * Revision 1.10  2007-06-21 06:42:30  hlausen
 * sorry for the delay
 *
 * Revision 1.9  2007-06-20 16:44:32  nathalie
 * *** empty log message ***
 *
 * Revision 1.8  2007-06-20 15:51:28  nathalie
 * changed amount of element features per ontology
 *
 * Revision 1.7  2007-06-20 15:47:36  nathalie
 * added nfps
 *
 * Revision 1.6  2007-06-20 14:11:39  nathalie
 * added nfps
 *
 * Revision 1.5  2007-06-20 09:43:30  nathalie
 * added minCardinality ontologies and tests; added minor changes in the ontology building
 *
 * Revision 1.4  2007-06-18 16:46:56  hlausen
 * more improvements for better charts (one per query)
 *
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
