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

package org.wsml.reasoner.transformation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.omwg.logicalexpression.Atom;
import org.omwg.logicalexpression.Conjunction;
import org.omwg.logicalexpression.Constants;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.Molecule;
import org.omwg.logicalexpression.NegationAsFailure;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Attribute;
import org.omwg.ontology.Axiom;
import org.omwg.ontology.ComplexDataType;
import org.omwg.ontology.Concept;
import org.omwg.ontology.DataValue;
import org.omwg.ontology.Instance;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Parameter;
import org.omwg.ontology.Relation;
import org.omwg.ontology.RelationInstance;
import org.omwg.ontology.SimpleDataType;
import org.omwg.ontology.Type;
import org.omwg.ontology.Value;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.impl.DatalogBasedWSMLReasoner;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsml.reasoner.transformation.le.FixedModificationRules;
import org.wsmo.common.IRI;
import org.wsmo.common.Identifier;
import org.wsmo.common.UnnumberedAnonymousID;
import org.wsmo.common.exception.InvalidModelException;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;

/**
 * A normalization step of an ontology that transforms the conceptual syntax
 * part to logical expressions. Hence, it transforms a WSML ontology to a set of
 * logical expressions or to be technically more precise a set of axioms.
 * 
 * ASSUMPTIONS: the transformation assumes the following: All ontology elements
 * are identified and the only identifier we have to deal with in ontology
 * descriptions are IRIs.
 * 
 * In order to guarantee the assumptions, one possibly must run a separate
 * normalization step before running this one.
 * 
 * The result is presented as an ontology which consists of axioms only.
 * 
 * Axioms of the original ontology are inserted themselves in the new onotology
 * that mean no copies of axioms are created.
 * 
 * Namespace def. and non-functional properties are taken over as well into the
 * new ontology.
 * 
 * Technically, the transformation implements Table 8.1 of Deliverable D16.1
 * v0.3 of the WSML Working Group.
 * 
 * @author Uwe Keller, DERI Innsbruck
 * @author Stephan Grimm, FZI Karlsruhe
 * @author Holger Lausen, DERI Innsbruck
 */
public class AxiomatizationNormalizer implements OntologyNormalizer {
    private WsmoFactory wsmoFactory;
    private LogicalExpressionFactory leFactory;
    private FixedModificationRules fixedRules;
    private Map<LogicalExpression,String> axiomIDs;
    
    private Set<Ontology> processedOntologies;
    
    private Set<Identifier> mentionedIRIs;
    
    
    public AxiomatizationNormalizer(WSMO4JManager wsmoManager,Set<Ontology> theProcessedOntologies) {
    	this(wsmoManager);
    	this.processedOntologies = theProcessedOntologies;
    }

    private AxiomatizationNormalizer(WSMO4JManager wsmoManager) {
        leFactory = wsmoManager.getLogicalExpressionFactory();
        wsmoFactory = wsmoManager.getWSMOFactory();
        fixedRules = new FixedModificationRules(wsmoManager);
        axiomIDs = new HashMap<LogicalExpression, String>();
        mentionedIRIs = new HashSet<Identifier>();
        
    }

    /**
     * Performs the transformation that is described above.
     * 
     * @param o -
     *            the ontology for which we need to resolve the conceptual
     *            syntax part.
     * 
     * @return an ontology represent o semantically but only consists of axioms
     */
    
    public Ontology normalize(Ontology ontology) {
        String ontologyID = ontology.getIdentifier() + "-as-axioms";
        Ontology resultOntology = wsmoFactory.createOntology(wsmoFactory.createIRI(ontologyID));
        for (Axiom a : (Set<Axiom>) resultOntology.listAxioms()) {
            try {
                a.setOntology(null);
            } catch (InvalidModelException e) {
                e.printStackTrace();
            }
        }
        
       
        //0 = allow imports
        //1 = do not allow imports
        if(DatalogBasedWSMLReasoner.allowImports == 0){
	        for(Ontology o: (Collection<Ontology>) ontology.listOntologies()){ 
	        	if (processedOntologies.contains(o)) continue;
	        	processedOntologies.add(o);
	        	
        		Ontology tempOntology = normalize(o);
        		for (Axiom a: (Collection<Axiom>) tempOntology.listAxioms()){
        			try {
						resultOntology.addAxiom(a);
					} catch (InvalidModelException e) {
						throw new RuntimeException(e);
					}
        		}
	        		
	        }
        }
        // process axioms:
        for (Axiom axiom : (Collection<Axiom>) ontology.listAxioms()) {
            Identifier newAxiomId;
            if (axiom.getIdentifier() instanceof UnnumberedAnonymousID){
                newAxiomId = wsmoFactory.createAnonymousID(); 
            }else {
                //create an axiom such that orginal one is not touched.
                newAxiomId = wsmoFactory.createIRI(axiom.getIdentifier()
                        +AnonymousIdUtils.NAMED_AXIOM_SUFFIX+System.currentTimeMillis());;
            }
            
            Axiom newAxiom = wsmoFactory.createAxiom(newAxiomId);
            for(LogicalExpression definition : (Set<LogicalExpression>)axiom.listDefinitions()){
                newAxiom.addDefinition(definition);
            }
            try{
                resultOntology.addAxiom(newAxiom);
            } catch(InvalidModelException e){
                e.printStackTrace();
            }
        }
        
        // process concepts:
        for (Concept concept : (Collection<Concept>) ontology.listConcepts()) {
            addAsAxioms(resultOntology, normalizeConcept(concept));
        }

        // process relations:
        for (Relation relation : (Collection<Relation>) ontology
                .listRelations()) {
            addAsAxioms(resultOntology, normalizeRelation(relation));
        }

        // Concept instances
        for (Instance instance : (Collection<Instance>) ontology
                .listInstances()) {
            addAsAxioms(resultOntology, normalizeInstance(instance));
        }
        
       // The following is needed to ensure certain meta-level inferences
        // such as reflexivity of subClassOf for all mentioned classes
        
       // Handle explicitly mentioned identifiers: 
       // insert a meta-level atom (in WSML) that specifies that the term
       // is an IRI that occurs in the program
       addAsAxioms(resultOntology, explicateIRIDeclaration(mentionedIRIs));
       
        

//        repealAxiomIDs();
        return resultOntology;
    }

    protected void addAsAxioms(Ontology ontology,
            Collection<LogicalExpression> expressions) {
        if (expressions.isEmpty()) {
            return;
        }
        for (LogicalExpression expression : expressions) {
            try {
                //corelate IDs to type of axioms
                Identifier id; 
                if (axiomIDs.containsKey(expression)){
                   id = wsmoFactory.createIRI(axiomIDs.get(expression)); 
                }else {
                   id = wsmoFactory.createAnonymousID();
                }
                Axiom axiom = wsmoFactory.createAxiom(id);
                ontology.addAxiom(axiom);
                axiom.addDefinition(expression);
            } catch (InvalidModelException e) {
                e.printStackTrace();
            }
        }
    }

    
    protected Set<LogicalExpression> normalizeConcept(Concept concept) {
        Identifier conceptID = concept.getIdentifier();
        Set<LogicalExpression> resultExpressions = new HashSet<LogicalExpression>();
        
        mentionedIRIs.add(conceptID);

        // process superconcepts:
        for (Concept superconcept : (Set<Concept>) concept.listSuperConcepts()) {
            Identifier superconceptID = superconcept.getIdentifier();
            resultExpressions.add(leFactory.createSubConceptMolecule(conceptID,
                    superconceptID));
            
            mentionedIRIs.add(superconceptID);
            
            // inheritance of attributes
            Set<Attribute> superAttr = superconcept.listAttributes();
            for (Attribute a : superAttr) {
            	resultExpressions.addAll(normalizeConceptAttribute(conceptID,
                        a));
            }
        }

        // process attributes:
        for (Attribute attribute : (Set<Attribute>) concept.listAttributes()) {
            resultExpressions.addAll(normalizeConceptAttribute(conceptID,
                    attribute));
        }

        return resultExpressions;
    }

    
    protected Set<LogicalExpression> normalizeConceptAttribute(
            Identifier conceptID, Attribute attribute) {
        Identifier attributeID = attribute.getIdentifier();
        
        mentionedIRIs.add(attributeID);
        
        Set<LogicalExpression> resultExpressions = new HashSet<LogicalExpression>();

        // process range types:
        for (Type type : (Set<Type>) attribute.listTypes()) {
            // determine Id of range type:
            Identifier typeID;
            if (type instanceof Concept) {
                typeID = ((Concept) type).getIdentifier();
                
                mentionedIRIs.add(typeID);
                
            } else if (type instanceof SimpleDataType) {
                typeID = ((SimpleDataType) type).getIRI();
            } else {
                typeID = ((ComplexDataType) type).getIRI();
            }

            // create an appropriate molecule per range type:
            if (attribute.isConstraining()) {
                LogicalExpression ofTypeConstraint = leFactory.createAttributeConstraint(conceptID, attributeID, typeID);
                resultExpressions.add(ofTypeConstraint);
                proclaimAxiomID(ofTypeConstraint, AnonymousIdUtils.getNewOfTypeIri());
            } else {
                resultExpressions.add(leFactory.createAttributeInference(
                        conceptID, attributeID, typeID));
            }
        }

        // process attribute properties:
        if (attribute.isReflexive()) {
            resultExpressions.add(createReflexivityConstraint(conceptID,
                    attributeID));
        }
        if (attribute.isSymmetric()) {
            resultExpressions.add(createSymmetryConstraint(conceptID,
                    attributeID));
        }
        if (attribute.isTransitive()) {
            resultExpressions.add(createTransitivityConstraint(conceptID,
                    attributeID));
        }
        Identifier inverseAttribute = attribute.getInverseOf();
        if (inverseAttribute != null) {
            resultExpressions.addAll(createInverseConstraints(conceptID,
                    attributeID, inverseAttribute));
        }

        // process cardinality constraints:
        if (attribute.getMinCardinality() > 0) {
            resultExpressions.addAll(createMinCardinalityConstraints(conceptID,
                    attributeID, attribute.getMinCardinality()));
        }
        if (attribute.getMaxCardinality() < Integer.MAX_VALUE) {
            resultExpressions.add(createMaxCardinalityConstraint(conceptID,
                    attributeID, attribute.getMaxCardinality()));
        }

        return resultExpressions;
    }
    
    private void proclaimAxiomID(LogicalExpression expression, String id)
    {
        axiomIDs.put(expression, id);
    }

    protected LogicalExpression createTransitivityConstraint(
            Identifier conceptID, Identifier attributeID) {
        Variable xVariable = leFactory.createVariable("x");
        Variable yVariable = leFactory.createVariable("y");
        Variable zVariable = leFactory.createVariable("z");
        LogicalExpression moX = leFactory.createMemberShipMolecule(xVariable,
                conceptID);
        LogicalExpression moY = leFactory.createMemberShipMolecule(yVariable,
                conceptID);
        LogicalExpression valXY = leFactory.createAttributeValue(xVariable,
                attributeID, yVariable);
        LogicalExpression valYZ = leFactory.createAttributeValue(yVariable,
                attributeID, zVariable);
        LogicalExpression valXZ = leFactory.createAttributeValue(xVariable,
                attributeID, zVariable);

        Set<LogicalExpression> conjuncts = new HashSet<LogicalExpression>();
        conjuncts.add(moX);
        conjuncts.add(moY);
        conjuncts.add(valXY);
        conjuncts.add(valYZ);
        LogicalExpression conjunction = fixedRules
                .buildNaryConjunction(conjuncts);
        return leFactory.createImplication(conjunction, valXZ);
    }

    protected LogicalExpression createSymmetryConstraint(Identifier conceptID,
            Identifier attributeID) {
        Variable xVariable = leFactory.createVariable("x");
        Variable yVariable = leFactory.createVariable("y");
        LogicalExpression moX = leFactory.createMemberShipMolecule(xVariable,
                conceptID);
        LogicalExpression moY = leFactory.createMemberShipMolecule(yVariable,
                conceptID);
        LogicalExpression valXY = leFactory.createAttributeValue(xVariable,
                attributeID, yVariable);
        LogicalExpression valYX = leFactory.createAttributeValue(yVariable,
                attributeID, xVariable);

        Set<LogicalExpression> conjuncts = new HashSet<LogicalExpression>();
        conjuncts.add(moX);
        conjuncts.add(moY);
        conjuncts.add(valXY);
        LogicalExpression conjunction = fixedRules
                .buildNaryConjunction(conjuncts);
        return leFactory.createImplication(conjunction, valYX);
    }

    protected LogicalExpression createReflexivityConstraint(
            Identifier conceptID, Identifier attributeID) {
        Variable xVariable = leFactory.createVariable("x");
        LogicalExpression moX = leFactory.createMemberShipMolecule(xVariable,
                conceptID);
        LogicalExpression valXX = leFactory.createAttributeValue(xVariable,
                attributeID, xVariable);

        return leFactory.createImplication(moX, valXX);
    }

    protected Collection<LogicalExpression> createInverseConstraints(
            Identifier conceptID, Identifier attributeID,
            Identifier inverseAttributeID) {
        Collection<LogicalExpression> inverseConstraints = new ArrayList<LogicalExpression>(
                2);

        // build required LE elements:
        Variable xVariable = leFactory.createVariable("x");
        Variable yVariable = leFactory.createVariable("y");
        LogicalExpression moX = leFactory.createMemberShipMolecule(xVariable,
                conceptID);
        LogicalExpression moY = leFactory.createMemberShipMolecule(yVariable,
                conceptID);
        LogicalExpression valXY = leFactory.createAttributeValue(xVariable,
                attributeID, yVariable);
        LogicalExpression valInvXY = leFactory.createAttributeValue(xVariable,
                inverseAttributeID, yVariable);
        LogicalExpression valYX = leFactory.createAttributeValue(yVariable,
                attributeID, xVariable);
        LogicalExpression valInvYX = leFactory.createAttributeValue(yVariable,
                inverseAttributeID, xVariable);

        // build implication : "..."
        LogicalExpression conjunction = leFactory.createConjunction(moX, valXY);
        inverseConstraints.add(leFactory.createImplication(conjunction,
                valInvYX));

        // build implication : "..."
        conjunction = leFactory.createConjunction(moY, valInvXY);
        inverseConstraints.add(leFactory.createImplication(conjunction, valYX));

        return inverseConstraints;
    }

    protected Collection<LogicalExpression> createMinCardinalityConstraints(
            Identifier conceptID, Identifier attributeID, int cardinality) {
        Collection<LogicalExpression> minCardConstraints = new ArrayList<LogicalExpression>(
                2);

        // build required LE elements:
        Variable xVariable = leFactory.createVariable("x");
        Variable[] yVariable = new Variable[cardinality];
        for (int i = 0; i < yVariable.length; i++) {
            yVariable[i] = leFactory
                    .createVariable("y" + Integer.toString(i));
        }
        LogicalExpression moX = leFactory.createMemberShipMolecule(xVariable,
                conceptID);
        LogicalExpression[] valXY = new LogicalExpression[cardinality];
        for (int i = 0; i < valXY.length; i++) {
            valXY[i] = leFactory.createAttributeValue(xVariable, attributeID,
                    yVariable[i]);
        }
        Collection<LogicalExpression> inEqualities = new ArrayList<LogicalExpression>(
                (cardinality * cardinality + cardinality) / 2 + 1);
        for (int i = 0; i < cardinality; i++) {
            for (int j = i + 1; j < cardinality; j++) {
                List<Term> args = new ArrayList<Term>(2);
                args.add(yVariable[i]);
                args.add(yVariable[j]);
                inEqualities.add(leFactory.createAtom(wsmoFactory
                        .createIRI(Constants.INEQUAL), args));
            }
        }

        // build LP-rule: "Pnew(?x,<attrID>) :- ?x memberOf <concept> and ?x[<attribute>
        // hasValue ?y1, ... <attribute> hasValue yn] and ?y1!=?y2 and ... and
        // yn-1!=yn."
        Set<LogicalExpression> conjuncts = new HashSet<LogicalExpression>();
        conjuncts.add(moX);
        conjuncts.addAll(Arrays.asList(valXY));
        conjuncts.addAll(inEqualities);
        LogicalExpression conjunction = fixedRules
                .buildNaryConjunction(conjuncts);
        IRI newPIRI = wsmoFactory.createIRI(AnonymousIdUtils.getNewAnonymousIri());
        Atom newPX = leFactory.createAtom(newPIRI, Arrays
                .asList(new Term[] { xVariable, attributeID }));
        minCardConstraints.add(leFactory.createLogicProgrammingRule(newPX,
                conjunction));

        // build constraint: "!- ?x memberOf <concept> and naf Pnew(?x,<attrID>)."
        NegationAsFailure naf = leFactory.createNegationAsFailure(newPX);
        conjunction = leFactory.createConjunction(moX, naf);
        LogicalExpression minCardConstraint = leFactory.createConstraint(conjunction);
        proclaimAxiomID(minCardConstraint, AnonymousIdUtils.getNewMinCardIri());
        minCardConstraints.add(minCardConstraint);

        return minCardConstraints;
    }

    protected LogicalExpression createMaxCardinalityConstraint(
            Identifier conceptID, Identifier attributeID, int cardinality) {
        cardinality++;
        
        // build required LE elements:
        Variable xVariable = leFactory.createVariable("x");
        Variable[] yVariable = new Variable[cardinality];
        for (int i = 0; i < yVariable.length; i++) {
            yVariable[i] = leFactory
                    .createVariable("y" + Integer.toString(i));
        }
        LogicalExpression moX = leFactory.createMemberShipMolecule(xVariable,
                conceptID);
        Molecule[] valXY = new Molecule[cardinality];
        for (int i = 0; i < valXY.length; i++) {
            valXY[i] = leFactory.createAttributeValue(xVariable, attributeID,
                    yVariable[i]);
        }
        Collection<LogicalExpression> inEqualities = new ArrayList<LogicalExpression>(
                (cardinality * cardinality + cardinality) / 2 + 1);
        for (int i = 0; i < cardinality; i++) {
            for (int j = i + 1; j < cardinality; j++) {
                List<Term> args = new ArrayList<Term>(2);
                args.add(yVariable[i]);
                args.add(yVariable[j]);
                inEqualities.add(leFactory.createAtom(wsmoFactory
                        .createIRI(Constants.INEQUAL), args));
            }
        }

        // build constraint: "!- ?x memberOf <concept> and ?x[<attribute>
        // hasValue ?y1, ... <attribute> hasValue yn+1] and ?y1!=?y2 and ... and
        // yn!=yn+1."
        Set<LogicalExpression> conjuncts = new HashSet<LogicalExpression>();
        conjuncts.add(moX);
        if (cardinality == 1) {
        	conjuncts.addAll(Arrays.asList(valXY));
        }
        else {
        	conjuncts.add(leFactory.createCompoundMolecule(Arrays.asList(valXY)));
        }
        conjuncts.addAll(inEqualities);
        LogicalExpression conjunction = fixedRules
                .buildNaryConjunction(conjuncts);
        LogicalExpression maxCardConstraint = leFactory.createConstraint(conjunction);
        proclaimAxiomID(maxCardConstraint, AnonymousIdUtils.getNewMaxCardIri());
//        System.out.println(maxCardConstraint.toString());
        return maxCardConstraint;
    }

    
    protected Set<LogicalExpression> normalizeRelation(Relation relation) {
        Identifier relationID = relation.getIdentifier();
        
        mentionedIRIs.add(relationID);
        
        Set<LogicalExpression> resultExpressions = new HashSet<LogicalExpression>();

        // process super relations:
        int arity = relation.listParameters().size();
        List<Term> terms = new ArrayList<Term>(arity);
        for (int i = 0; i < arity; i++) {
            terms
                    .add(leFactory.createVariable("x" + Integer.toString(i)));
        }
        Atom relP = leFactory.createAtom(relationID, terms);
        for (Relation superRelation : (Collection<Relation>) relation
                .listSuperRelations()) {
            Atom superRelP = leFactory.createAtom(
                    superRelation.getIdentifier(), terms);
            resultExpressions.add(leFactory.createImplication(relP, superRelP));
            
            mentionedIRIs.add(superRelation.getIdentifier());
            
        }

        // process relation parameters:
        List<Parameter> parameters = (List<Parameter>) relation
                .listParameters();
        Collection<LogicalExpression> parameterAxioms = new LinkedList<LogicalExpression>();
        int i = 0;
        for (Parameter parameter : parameters) {
            List<Molecule> typeMemberships = new ArrayList<Molecule>(
                    parameter.listTypes().size());
            for (Type type : (Collection<Type>) parameter.listTypes()) {
                Identifier typeID;
                if (type instanceof Concept) {
                    typeID = ((Concept) type).getIdentifier();
                } else if (type instanceof SimpleDataType) {
                    typeID = ((SimpleDataType) type).getIRI();
                } else {
                    typeID = ((ComplexDataType) type).getIRI();
                }
                typeMemberships.add(leFactory.createMemberShipMolecule(
                		terms.get(i++), typeID));
            }
            if (!typeMemberships.isEmpty()) {
                if (parameter.isConstraining()) {
                    LogicalExpression molecule = buildMolecule(typeMemberships);
                    NegationAsFailure naf = leFactory
                            .createNegationAsFailure(molecule);
                    Conjunction conjunction = leFactory.createConjunction(relP,
                            naf);
                    parameterAxioms
                            .add(leFactory.createConstraint(conjunction));
                } else {
                    LogicalExpression molecule = buildMolecule(typeMemberships);
                    parameterAxioms.add(leFactory.createImplication(relP,
                            molecule));
                }
            }
        }
        //System.out.println(parameterAxioms);
        resultExpressions.addAll(parameterAxioms);

        // process relation instances:
        for (RelationInstance relInstance : (Collection<RelationInstance>) relation
                .listRelationInstances()) {
            List<Term> args = new LinkedList<Term>();
            for (Value value : (List<Value>) relInstance.listParameterValues()) {
                if (value instanceof Instance) {
                    Identifier val = ((Instance) value).getIdentifier();
                    args.add(val);
                    
                    mentionedIRIs.add(val);
                    
                } else if (value instanceof DataValue) {
                    DataValue dataValue = (DataValue) value;
                    // args.add(convertDataValue(dataValue));
                    args.add(dataValue);
                }
            }
            resultExpressions.add(leFactory.createAtom(relationID, args));
        }

        return resultExpressions;
    }

    protected LogicalExpression buildMolecule(List<Molecule> molecules) {
        if (molecules.size() == 1) {
            return molecules.get(0);
        } else {
            return leFactory.createCompoundMolecule(molecules);
        }
    }

    
    protected Set<LogicalExpression> normalizeInstance(Instance instance) {
        Identifier instanceID = instance.getIdentifier();
        
        mentionedIRIs.add(instanceID);
        
        Set<LogicalExpression> resultExpressions = new HashSet<LogicalExpression>();

        // process concepts:
        for (Concept concept : (Collection<Concept>) instance.listConcepts()) {
            resultExpressions.add(leFactory.createMemberShipMolecule(
                    instanceID, concept.getIdentifier()));
            
            mentionedIRIs.add(concept.getIdentifier());
        }

        // process attribute values:
        Map<Identifier, Set<Value>> attributeValues = (Map<Identifier, Set<Value>>) instance
                .listAttributeValues();
        for (Identifier attribute : attributeValues.keySet()) {
            
            mentionedIRIs.add(attribute);
            
            Set<Value> values = attributeValues.get(attribute);
            List<Molecule> molecules = new ArrayList<Molecule>(
                    values.size());
            for (Value value : values) {
                Term valueTerm = null;
                if (value instanceof Instance) {
                    Instance i = ((Instance) value);
                    valueTerm = i.getIdentifier();
                                        
                    mentionedIRIs.add(i.getIdentifier());
                    
                } else if (value instanceof DataValue) {
                    // valueTerm = convertDataValue((DataValue)value);
                    valueTerm = ((DataValue) value);
                }
                molecules.add(leFactory.createAttributeValue(instanceID,
                        attribute, valueTerm));
            }
            if (molecules.size() > 1) {
                resultExpressions.add(leFactory
                        .createCompoundMolecule(molecules));
            } else if (molecules.size() == 1) {
                resultExpressions.add(molecules.get(0));
            }
        }

        return resultExpressions;
    }

    
	private Set<LogicalExpression> explicateIRIDeclaration(Collection<Identifier> mentionedIRIs){
        Set<LogicalExpression> resultExpressions = new HashSet<LogicalExpression>();
    
        Identifier pred_declared_iri_symbol = 
            wsmoFactory.createIRI(org.wsml.reasoner.WSML2DatalogTransformer.PRED_DECLARED_IRI);
             
        for (Identifier cid : mentionedIRIs){
            List params = new LinkedList();
            params.add(cid);
            resultExpressions.add(leFactory.createAtom(pred_declared_iri_symbol, params));
        }
        
        // System.err.println("Explicated IRI Declarations ( total count = "+mentionedIRIs.size()+" ): \n" + mentionedIRIs);
        
        return resultExpressions;
    }
    
}
