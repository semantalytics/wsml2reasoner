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
import org.omwg.ontology.Parameter;
import org.omwg.ontology.Relation;
import org.omwg.ontology.RelationInstance;
import org.omwg.ontology.SimpleDataType;
import org.omwg.ontology.Type;
import org.omwg.ontology.Value;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.transformation.le.LEUtil;
import org.wsmo.common.BuiltIn;
import org.wsmo.common.Entity;
import org.wsmo.common.IRI;
import org.wsmo.common.Identifier;
import org.wsmo.common.UnnumberedAnonymousID;
import org.wsmo.factory.FactoryContainer;
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

    private Map<LogicalExpression, String> axiomIDs;

    public AxiomatizationNormalizer(FactoryContainer factory) {
        this.leFactory = factory.getLogicalExpressionFactory();
        this.wsmoFactory = factory.getWsmoFactory();
        this.axiomIDs = new HashMap<LogicalExpression, String>();
    }

    public Set<Axiom> normalizeAxioms(Collection<Axiom> theAxioms) {
        throw new UnsupportedOperationException();
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

    public Set<Entity> normalizeEntities(Collection<Entity> theEntities) {
        Set<Entity> result = new HashSet<Entity>();

        for (Entity e : theEntities) {
            if (e instanceof Axiom) {
                Axiom axiom = (Axiom) e;
                Identifier newAxiomId;
                if (axiom.getIdentifier() instanceof UnnumberedAnonymousID) {
                    newAxiomId = wsmoFactory.createAnonymousID();
                }
                else {
                    // create an axiom such that original one is not touched.
                    newAxiomId = wsmoFactory.createIRI(axiom.getIdentifier() + AnonymousIdUtils.NAMED_AXIOM_SUFFIX + System.currentTimeMillis());
                }

                Axiom newAxiom = wsmoFactory.createAxiom(newAxiomId);
                for (LogicalExpression definition : axiom.listDefinitions()) {
                    newAxiom.addDefinition(definition);
                }
                result.add(newAxiom);
            }
            else if (e instanceof Concept) {
                result.addAll(getAxioms(normalizeConcept((Concept) e)));
            }
            else if (e instanceof Relation) {
                result.addAll(getAxioms(normalizeRelation((Relation) e)));
            }
            else if (e instanceof RelationInstance) {
                result.addAll(getAxioms(normalizeRelationInstance((RelationInstance) e)));
            }
            else if (e instanceof Instance) {
                result.addAll(getAxioms(normalizeInstance((Instance) e)));
            }
        }
        return result;
    }

    protected Set<Axiom> getAxioms(Collection<LogicalExpression> expressions) {
        Set<Axiom> axioms = new HashSet<Axiom>();
        if (!expressions.isEmpty()) {
            for (LogicalExpression expression : expressions) {
                // corelate IDs to type of axioms
                Identifier id;
                if (axiomIDs.containsKey(expression)) {
                    id = wsmoFactory.createIRI(axiomIDs.get(expression));
                }
                else {
                    id = wsmoFactory.createAnonymousID();
                }
                Axiom axiom = wsmoFactory.createAxiom(id);
                axiom.addDefinition(expression);
                axioms.add(axiom);
            }
        }
        return axioms;
    }

    protected Set<LogicalExpression> normalizeConcept(Concept concept) {
        Identifier conceptID = concept.getIdentifier();
        Set<LogicalExpression> resultExpressions = new HashSet<LogicalExpression>();

        // process superconcepts:
        for (Concept superconcept : concept.listSuperConcepts()) {
            Identifier superconceptID = superconcept.getIdentifier();
            resultExpressions.add(leFactory.createSubConceptMolecule(conceptID, superconceptID));
        }

        // process attributes:
        for (Attribute attribute : concept.listAttributes()) {
            resultExpressions.addAll(normalizeConceptAttribute(conceptID, attribute));
        }

        return resultExpressions;
    }

    protected Set<LogicalExpression> normalizeConceptAttribute(Identifier conceptID, Attribute attribute) {
        Identifier attributeID = attribute.getIdentifier();

        Set<LogicalExpression> resultExpressions = new HashSet<LogicalExpression>();

		// process range types:
		if (attribute.isConstraining()) {
			for (Type type : attribute.listConstrainingTypes()) {
				// determine Id of range type:
				Identifier typeID = type.getIdentifier();
				
				// create an appropriate molecule per range type:
				LogicalExpression ofTypeConstraint = leFactory.createAttributeConstraint(conceptID, attributeID, typeID);
				resultExpressions.add(ofTypeConstraint);
				proclaimAxiomID(ofTypeConstraint, AnonymousIdUtils.getNewOfTypeIri());
			}
		}

		if (attribute.isInferring()) {
			for (Type type : attribute.listInferringTypes()) {
				// determine Id of range type:
				Identifier typeID = type.getIdentifier();

				// create an appropriate molecule per range type:
				resultExpressions.add(leFactory.createAttributeInference(conceptID, attributeID, typeID));
			}
		}

        // process attribute properties:
        if (attribute.isReflexive()) {
            resultExpressions.add(createReflexivityConstraint(conceptID, attributeID));
        }
        if (attribute.isSymmetric()) {
            resultExpressions.add(createSymmetryConstraint(conceptID, attributeID));
        }
        if (attribute.isTransitive()) {
            resultExpressions.add(createTransitivityConstraint(conceptID, attributeID));
        }
        if (attribute.getInverseOf() != null) {
            resultExpressions.addAll(createInverseConstraints(conceptID, attributeID, attribute.getInverseOf()));
        }

        // process cardinality constraints:
        if (attribute.getMinCardinality() > 0) {
            resultExpressions.addAll(createMinCardinalityConstraints(conceptID, attributeID, attribute.getMinCardinality()));
        }
        if (attribute.getMaxCardinality() < Integer.MAX_VALUE) {
            resultExpressions.add(createMaxCardinalityConstraint(conceptID, attributeID, attribute.getMaxCardinality()));
        }
        
        // process attribute hierarchy:
        Identifier superAttributeID = attribute.getSubAttributeOf();
        if (superAttributeID != null) {
        	resultExpressions.add(createSubAttributeConstraints(conceptID, attributeID, superAttributeID));
        }

        return resultExpressions;
    }

    private void proclaimAxiomID(LogicalExpression expression, String id) {
        axiomIDs.put(expression, id);
    }

    protected LogicalExpression createTransitivityConstraint(Identifier conceptID, Identifier attributeID) {
        Variable xVariable = leFactory.createVariable("x");
        Variable yVariable = leFactory.createVariable("y");
        Variable zVariable = leFactory.createVariable("z");
        LogicalExpression moX = leFactory.createMemberShipMolecule(xVariable, conceptID);
        LogicalExpression moY = leFactory.createMemberShipMolecule(yVariable, conceptID);
        LogicalExpression valXY = leFactory.createAttributeValue(xVariable, attributeID, yVariable);
        LogicalExpression valYZ = leFactory.createAttributeValue(yVariable, attributeID, zVariable);
        LogicalExpression valXZ = leFactory.createAttributeValue(xVariable, attributeID, zVariable);

        Set<LogicalExpression> conjuncts = new HashSet<LogicalExpression>();
        conjuncts.add(moX);
        conjuncts.add(moY);
        conjuncts.add(valXY);
        conjuncts.add(valYZ);
        return leFactory.createImplication(LEUtil.buildNaryConjunction(leFactory, conjuncts), valXZ);
    }

    protected LogicalExpression createSymmetryConstraint(Identifier conceptID, Identifier attributeID) {
        Variable xVariable = leFactory.createVariable("x");
        Variable yVariable = leFactory.createVariable("y");
        LogicalExpression moX = leFactory.createMemberShipMolecule(xVariable, conceptID);
        LogicalExpression moY = leFactory.createMemberShipMolecule(yVariable, conceptID);
        LogicalExpression valXY = leFactory.createAttributeValue(xVariable, attributeID, yVariable);
        LogicalExpression valYX = leFactory.createAttributeValue(yVariable, attributeID, xVariable);

        Set<LogicalExpression> conjuncts = new HashSet<LogicalExpression>();
        conjuncts.add(moX);
        conjuncts.add(moY);
        conjuncts.add(valXY);
        return leFactory.createImplication(LEUtil.buildNaryConjunction(leFactory, conjuncts), valYX);
    }

    protected LogicalExpression createReflexivityConstraint(Identifier conceptID, Identifier attributeID) {
        Variable xVariable = leFactory.createVariable("x");
        LogicalExpression moX = leFactory.createMemberShipMolecule(xVariable, conceptID);
        LogicalExpression valXX = leFactory.createAttributeValue(xVariable, attributeID, xVariable);
        return leFactory.createImplication(moX, valXX);
    }

    protected Collection<LogicalExpression> createInverseConstraints(Identifier conceptID, Identifier attributeID, Identifier inverseAttributeID) {
        Collection<LogicalExpression> inverseConstraints = new ArrayList<LogicalExpression>(2);

        // build required LE elements:
        Variable xVariable = leFactory.createVariable("x");
        Variable yVariable = leFactory.createVariable("y");
        LogicalExpression moX = leFactory.createMemberShipMolecule(xVariable, conceptID);
        LogicalExpression moY = leFactory.createMemberShipMolecule(yVariable, conceptID);
        LogicalExpression valXY = leFactory.createAttributeValue(xVariable, attributeID, yVariable);
        LogicalExpression valInvXY = leFactory.createAttributeValue(xVariable, inverseAttributeID, yVariable);
        LogicalExpression valYX = leFactory.createAttributeValue(yVariable, attributeID, xVariable);
        LogicalExpression valInvYX = leFactory.createAttributeValue(yVariable, inverseAttributeID, xVariable);

        // build implication : "..."
        LogicalExpression conjunction = leFactory.createConjunction(moX, valXY);
        inverseConstraints.add(leFactory.createImplication(conjunction, valInvYX));

        // build implication : "..."
        conjunction = leFactory.createConjunction(moY, valInvXY);
        inverseConstraints.add(leFactory.createImplication(conjunction, valYX));

        return inverseConstraints;
    }

    protected LogicalExpression createSubAttributeConstraints(Identifier conceptID, Identifier subAttributeID, Identifier superAttributeID) {
        // build required LE elements:
        Variable xVariable = leFactory.createVariable("x");
        Variable yVariable = leFactory.createVariable("y");
        LogicalExpression moX = leFactory.createMemberShipMolecule(xVariable, conceptID);
        LogicalExpression valSubXY = leFactory.createAttributeValue(xVariable, subAttributeID, yVariable);
        LogicalExpression valSuperXY = leFactory.createAttributeValue(xVariable, superAttributeID, yVariable);

        // build implication : "..."
        LogicalExpression subConjunction = leFactory.createConjunction(moX, valSubXY);

        return leFactory.createImplication(subConjunction, valSuperXY);
    }
    	
    protected Collection<LogicalExpression> createMinCardinalityConstraints(Identifier conceptID, Identifier attributeID, int cardinality) {
        Collection<LogicalExpression> minCardConstraints = new ArrayList<LogicalExpression>(2);

        // build required LE elements:
        Variable xVariable = leFactory.createVariable("x");
        Variable[] yVariable = new Variable[cardinality];
        for (int i = 0; i < yVariable.length; i++) {
            yVariable[i] = leFactory.createVariable("y" + Integer.toString(i));
        }
        LogicalExpression moX = leFactory.createMemberShipMolecule(xVariable, conceptID);
        LogicalExpression[] valXY = new LogicalExpression[cardinality];
        for (int i = 0; i < valXY.length; i++) {
            valXY[i] = leFactory.createAttributeValue(xVariable, attributeID, yVariable[i]);
        }
        Collection<LogicalExpression> inEqualities = new ArrayList<LogicalExpression>((cardinality * cardinality + cardinality) / 2 + 1);
        for (int i = 0; i < cardinality; i++) {
            for (int j = i + 1; j < cardinality; j++) {
                List<Term> args = new ArrayList<Term>(2);
                args.add(yVariable[i]);
                args.add(yVariable[j]);
                inEqualities.add(leFactory.createAtom(wsmoFactory.createIRI(BuiltIn.INEQUAL.getFullName()), args));
            }
        }

        // build LP-rule: "Pnew(?x,<attrID>) :- ?x memberOf <concept> and
        // ?x[<attribute>
        // hasValue ?y1, ... <attribute> hasValue yn] and ?y1!=?y2 and ... and
        // yn-1!=yn."
        Set<LogicalExpression> conjuncts = new HashSet<LogicalExpression>();
        conjuncts.add(moX);
        conjuncts.addAll(Arrays.asList(valXY));
        conjuncts.addAll(inEqualities);
        LogicalExpression conjunction = LEUtil.buildNaryConjunction(leFactory, conjuncts);
        IRI newPIRI = wsmoFactory.createIRI(AnonymousIdUtils.getNewAnonymousIri());
        Atom newPX = leFactory.createAtom(newPIRI, Arrays.asList(new Term[] { xVariable, attributeID }));
        minCardConstraints.add(leFactory.createLogicProgrammingRule(newPX, conjunction));

        // build constraint: "!- ?x memberOf <concept> and naf
        // Pnew(?x,<attrID>)."
        NegationAsFailure naf = leFactory.createNegationAsFailure(newPX);
        conjunction = leFactory.createConjunction(moX, naf);
        LogicalExpression minCardConstraint = leFactory.createConstraint(conjunction);
        proclaimAxiomID(minCardConstraint, AnonymousIdUtils.getNewMinCardIri());
        minCardConstraints.add(minCardConstraint);

        return minCardConstraints;
    }

    protected LogicalExpression createMaxCardinalityConstraint(Identifier conceptID, Identifier attributeID, int cardinality) {
        cardinality++;

        // build required LE elements:
        Variable xVariable = leFactory.createVariable("x");
        Variable[] yVariable = new Variable[cardinality];
        for (int i = 0; i < yVariable.length; i++) {
            yVariable[i] = leFactory.createVariable("y" + Integer.toString(i));
        }
        LogicalExpression moX = leFactory.createMemberShipMolecule(xVariable, conceptID);
        Molecule[] valXY = new Molecule[cardinality];
        for (int i = 0; i < valXY.length; i++) {
            valXY[i] = leFactory.createAttributeValue(xVariable, attributeID, yVariable[i]);
        }
        Collection<LogicalExpression> inEqualities = new ArrayList<LogicalExpression>((cardinality * cardinality + cardinality) / 2 + 1);
        for (int i = 0; i < cardinality; i++) {
            for (int j = i + 1; j < cardinality; j++) {
                List<Term> args = new ArrayList<Term>(2);
                args.add(yVariable[i]);
                args.add(yVariable[j]);
                inEqualities.add(leFactory.createAtom(wsmoFactory.createIRI(BuiltIn.INEQUAL.getFullName()), args));
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
        LogicalExpression conjunction = LEUtil.buildNaryConjunction(leFactory, conjuncts);
        LogicalExpression maxCardConstraint = leFactory.createConstraint(conjunction);
        proclaimAxiomID(maxCardConstraint, AnonymousIdUtils.getNewMaxCardIri());
        return maxCardConstraint;
    }

    protected Set<LogicalExpression> normalizeRelation(Relation relation) {
        Set<LogicalExpression> resultExpressions = new HashSet<LogicalExpression>();
        Identifier relationID = relation.getIdentifier();


        // process super relations:
        int arity = relation.listParameters().size();
        List<Term> terms = new ArrayList<Term>(arity);
        for (int i = 0; i < arity; i++) {
            terms.add(leFactory.createVariable("x" + Integer.toString(i)));
        }
        Atom relP = leFactory.createAtom(relationID, terms);
        for (Relation superRelation : relation.listSuperRelations()) {
            Atom superRelP = leFactory.createAtom(superRelation.getIdentifier(), terms);
            resultExpressions.add(leFactory.createImplication(relP, superRelP));
        }

        // process relation parameters:
        List<Parameter> parameters = relation.listParameters();
        Collection<LogicalExpression> parameterAxioms = new LinkedList<LogicalExpression>();
        int paramIndex = 0;
        for (Parameter parameter : parameters) {
            List<Molecule> typeMemberships = new ArrayList<Molecule>(parameter.listTypes().size());
            for (Type type : parameter.listTypes()) {
                Identifier typeID;
                if (type instanceof Concept) {
                    typeID = ((Concept) type).getIdentifier();
                }
                else if (type instanceof SimpleDataType) {
                    typeID = ((SimpleDataType) type).getIdentifier();
                }
                else {
                    typeID = ((ComplexDataType) type).getIdentifier();
                }
                typeMemberships.add(leFactory.createMemberShipMolecule(terms.get(paramIndex), typeID));
            }
            if (!typeMemberships.isEmpty()) {
                if (parameter.isConstraining()) {
                    // One rule for each param and each constraining type
                	for( Molecule type : typeMemberships ) {
	                    NegationAsFailure naf = leFactory.createNegationAsFailure(type);
	                    Conjunction conjunction = leFactory.createConjunction(relP, naf);
	                    parameterAxioms.add(leFactory.createConstraint(conjunction));
	                }
                }
                else {
                    LogicalExpression molecule = buildMolecule(typeMemberships);
                    parameterAxioms.add(leFactory.createImplication(relP, molecule));
                }
            }
            ++paramIndex;
        }
        resultExpressions.addAll(parameterAxioms);

        return resultExpressions;
    }

    protected Set<LogicalExpression> normalizeRelationInstance(RelationInstance relationInstance) {
        Set<LogicalExpression> resultExpressions = new HashSet<LogicalExpression>();
    	
        Relation relation = relationInstance.getRelation();
        if( relation != null ) {
	        Identifier relationID = relation.getIdentifier();
	        
	        if( relationID != null ) {
		        List<Term> args = new LinkedList<Term>();
		        for (Value value : relationInstance.listParameterValues()) {
		            if (value instanceof Instance) {
		                args.add(((Instance) value).getIdentifier());
		            }
		            else if (value instanceof DataValue) {
		                args.add((DataValue) value);
		            }
		        }
		        resultExpressions.add(leFactory.createAtom(relationID, args));
	        }
        }
        return resultExpressions;
    }

    protected LogicalExpression buildMolecule(List<Molecule> molecules) {
        if (molecules.size() == 1) {
            return molecules.get(0);
        }
        else {
            return leFactory.createCompoundMolecule(molecules);
        }
    }

    protected Set<LogicalExpression> normalizeInstance(Instance instance) {
        Identifier instanceID = instance.getIdentifier();

        Set<LogicalExpression> resultExpressions = new HashSet<LogicalExpression>();

        // process concepts:
        for (Concept concept : instance.listConcepts()) {
            resultExpressions.add(leFactory.createMemberShipMolecule(instanceID, concept.getIdentifier()));
        }

        // process attribute values:
        Map<Identifier, Set<Value>> attributeValues = instance.listAttributeValues();
        for (Identifier attribute : attributeValues.keySet()) {
            Set<Value> values = attributeValues.get(attribute);
            List<Molecule> molecules = new ArrayList<Molecule>(values.size());
            for (Value value : values) {
                Term valueTerm = null;
                if (value instanceof Instance) {
                    Instance i = ((Instance) value);
                    valueTerm = i.getIdentifier();
                }
                else if (value instanceof DataValue) {
                    valueTerm = ((DataValue) value);
                }
                molecules.add(leFactory.createAttributeValue(instanceID, attribute, valueTerm));
            }
            if (molecules.size() > 1) {
                resultExpressions.add(leFactory.createCompoundMolecule(molecules));
            }
            else if (molecules.size() == 1) {
                resultExpressions.add(molecules.get(0));
            }
        }
        return resultExpressions;
    }
}