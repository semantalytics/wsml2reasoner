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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.omwg.logicalexpression.Atom;
import org.omwg.logicalexpression.AttributeValueMolecule;
import org.omwg.logicalexpression.Conjunction;
import org.omwg.logicalexpression.Constants;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.MembershipMolecule;
import org.omwg.logicalexpression.Molecule;
import org.omwg.logicalexpression.NegationAsFailure;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Attribute;
import org.omwg.ontology.Axiom;
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
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsml.reasoner.transformation.le.FixedModificationRules;
import org.wsmo.common.IRI;
import org.wsmo.common.Identifier;
import org.wsmo.common.Namespace;
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
 * NOTE: At present the transformation does not support COMPLEX DATATYPES and
 * their respective values that are present in WSML! SIMPLE DATATYPES and their
 * values are supported.
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
 */
public class AxiomatizationNormalizer implements OntologyNormalizer
{
    private WsmoFactory wsmoFactory;
    private LogicalExpressionFactory leFactory;

    public AxiomatizationNormalizer()
    {
        leFactory = WSMO4JManager.getLogicalExpressionFactory();
        wsmoFactory = WSMO4JManager.getWSMOFactory();
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
    public Ontology normalize(Ontology ontology)
    {
        String ontologyID = (ontology.getIdentifier() != null ? ontology.getIdentifier().toString() + "-as-axioms" : "iri:normalized-ontology-" + ontology.hashCode());
        Ontology resultOntology = wsmoFactory.createOntology(wsmoFactory.createIRI(ontologyID));
        Collection<LogicalExpression> axiomExpressions = new LinkedList<LogicalExpression>();

        // process namespace definitions:
        for(Namespace namespace : (Collection<Namespace>)ontology.listNamespaces())
        {
            resultOntology.addNamespace(namespace);
        }
        resultOntology.setDefaultNamespace(ontology.getDefaultNamespace());

        // process non-functional properties:
        for(Object nfp : ontology.listNFPValues().entrySet())
        {
            try
            {
                if(nfp instanceof Identifier)
                {
                    Map.Entry entry = (Map.Entry)nfp;
                    resultOntology.addNFPValue((IRI)entry.getKey(), (Identifier)entry.getValue());
                }
                else if(nfp instanceof Value)
                {
                    Map.Entry entry = (Map.Entry)nfp;
                    resultOntology.addNFPValue((IRI)entry.getKey(), (Value)entry.getValue());
                }
            } catch(InvalidModelException e)
            {
                // TODO: handle exception
            }
        }

        // process axioms:
        for(Axiom axiom : (Collection<Axiom>)ontology.listAxioms())
        {
            try
            {
                resultOntology.addAxiom(axiom);
            } catch(InvalidModelException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        // process concepts:
        for(Concept concept : (Collection<Concept>)ontology.listConcepts())
        {
            addAsAxioms(resultOntology, normalizeConcept(concept));
        }

        // process relations:
        for(Relation relation : (Collection<Relation>)ontology.listRelations())
        {
            addAsAxioms(resultOntology, normalizeRelation(relation));
        }

        // Concept instances
        for(Instance instance : (Collection<Instance>)ontology.listInstances())
        {
            addAsAxioms(resultOntology, normalizeInstance(instance));
        }

        return resultOntology;
    }

    protected void addAsAxioms(Ontology ontology, Collection<LogicalExpression> expressions)
    {
        int i = 1;
        for(LogicalExpression expression : expressions)
        {
            String axiomIDString = "reasoner:axiom_" + Integer.toString(i++) + "_" + ontology.getIdentifier();
            Identifier axiomID = wsmoFactory.createIRI(axiomIDString);
            Axiom axiom = wsmoFactory.createAxiom(axiomID);
            axiom.addDefinition(expression);
            try
            {
                ontology.addAxiom(axiom);
            } catch(InvalidModelException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    protected Set<LogicalExpression> normalizeConcept(Concept concept)
    {
        Identifier conceptID = concept.getIdentifier();
        Set<LogicalExpression> resultExpressions = new HashSet<LogicalExpression>();

        // process superconcepts:
        for(Concept superconcept : (Set<Concept>)concept.listSuperConcepts())
        {
            Identifier superconceptID = superconcept.getIdentifier();
            resultExpressions.add(leFactory.createSubConceptMolecule(conceptID, superconceptID));
        }

        // process attributes:
        for(Attribute attribute : (Set<Attribute>)concept.listAttributes())
        {
            resultExpressions.addAll(normalizeConceptAttribute(conceptID, attribute));
        }

        return resultExpressions;
    }

    protected Set<LogicalExpression> normalizeConceptAttribute(Identifier conceptID, Attribute attribute)
    {
        Identifier attributeID = attribute.getIdentifier();
        Set<LogicalExpression> resultExpressions = new HashSet<LogicalExpression>();

        // process range types:
        Set<Identifier> rangeTypes = new HashSet<Identifier>();
        for(Type type : (Set<Type>)attribute.listTypes())
        {
            // determine Id of range type:
            Identifier typeID;
            if(type instanceof Concept)
            {
                typeID = ((Concept)type).getIdentifier();
            }
            else if(type instanceof SimpleDataType)
            {
                typeID = ((SimpleDataType)type).getIRI();
            }
            else
            {
                System.err.println("Complex datatype are currently not supported in Normalization to LogicalExpressions");
                continue;
            }

            // create an appropriate molecule per range type:
            if(attribute.isConstraining())
            {
                resultExpressions.add(leFactory.createAttributeConstraint(conceptID, attributeID, typeID));
            }
            else
            {
                resultExpressions.add(leFactory.createAttributeInference(conceptID, attributeID, typeID));
            }
        }

        // process attribute properties:
        if(attribute.isReflexive())
        {
            resultExpressions.add(createReflexivityConstraint(conceptID, attributeID));
        }
        if(attribute.isSymmetric())
        {
            resultExpressions.add(createSymmetryConstraint(conceptID, attributeID));
        }
        if(attribute.isTransitive())
        {
            resultExpressions.add(createTransitivityConstraint(conceptID, attributeID));
        }
        Attribute inverseAttribute = attribute.getInverseOf();
        if(inverseAttribute != null)
        {
            resultExpressions.addAll(createInverseConstraints(conceptID, attributeID, inverseAttribute.getIdentifier()));
        }

        // process cardinality constraints:
        if(attribute.getMinCardinality() > 0)
        {
            resultExpressions.addAll(createMinCardinalityConstraints(conceptID, attributeID, attribute.getMinCardinality()));
        }
        if(attribute.getMaxCardinality() < Integer.MAX_VALUE)
        {
            resultExpressions.add(createMaxCardinalityConstraint(conceptID, attributeID, attribute.getMaxCardinality()));
        }

        return resultExpressions;
    }

    protected LogicalExpression createTransitivityConstraint(Identifier conceptID, Identifier attributeID)
    {
        Variable xVariable = wsmoFactory.createVariable("x");
        Variable yVariable = wsmoFactory.createVariable("y");
        Variable zVariable = wsmoFactory.createVariable("z");
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
        LogicalExpression conjunction = FixedModificationRules.buildNaryConjunction(conjuncts);
        return leFactory.createImplication(conjunction, valXZ);
    }

    protected LogicalExpression createSymmetryConstraint(Identifier conceptID, Identifier attributeID)
    {
        Variable xVariable = wsmoFactory.createVariable("x");
        Variable yVariable = wsmoFactory.createVariable("y");
        LogicalExpression moX = leFactory.createMemberShipMolecule(xVariable, conceptID);
        LogicalExpression moY = leFactory.createMemberShipMolecule(yVariable, conceptID);
        LogicalExpression valXY = leFactory.createAttributeValue(xVariable, attributeID, yVariable);
        LogicalExpression valYX = leFactory.createAttributeValue(yVariable, attributeID, xVariable);

        Set<LogicalExpression> conjuncts = new HashSet<LogicalExpression>();
        conjuncts.add(moX);
        conjuncts.add(moY);
        conjuncts.add(valXY);
        LogicalExpression conjunction = FixedModificationRules.buildNaryConjunction(conjuncts);
        return leFactory.createImplication(conjunction, valYX);
    }

    protected LogicalExpression createReflexivityConstraint(Identifier conceptID, Identifier attributeID)
    {
        Variable xVariable = wsmoFactory.createVariable("x");
        LogicalExpression moX = leFactory.createMemberShipMolecule(xVariable, conceptID);
        LogicalExpression valXX = leFactory.createAttributeValue(xVariable, attributeID, xVariable);

        return leFactory.createImplication(moX, valXX);
    }

    protected Collection<LogicalExpression> createInverseConstraints(Identifier conceptID, Identifier attributeID, Identifier inverseAttributeID)
    {
        Collection<LogicalExpression> inverseConstraints = new ArrayList<LogicalExpression>(2);

        // build required LE elements:
        Variable xVariable = wsmoFactory.createVariable("x");
        Variable yVariable = wsmoFactory.createVariable("y");
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

    protected Collection<LogicalExpression> createMinCardinalityConstraints(Identifier conceptID, Identifier attributeID, int cardinality)
    {
        Collection<LogicalExpression> minCardConstraints = new ArrayList<LogicalExpression>(2);

        // build required LE elements:
        Variable xVariable = wsmoFactory.createVariable("x");
        Variable[] yVariable = new Variable[cardinality];
        for(int i = 0; i < yVariable.length; i++)
        {
            yVariable[i] = wsmoFactory.createVariable("y" + Integer.toString(i));
        }
        LogicalExpression moX = leFactory.createMemberShipMolecule(xVariable, conceptID);
        LogicalExpression[] valXY = new LogicalExpression[cardinality];
        for(int i = 0; i < valXY.length; i++)
        {
            valXY[i] = leFactory.createAttributeValue(xVariable, attributeID, yVariable[i]);
        }
        Collection<LogicalExpression> inEqualities = new ArrayList<LogicalExpression>((cardinality * cardinality + cardinality) / 2 + 1);
        for(int i = 0; i < cardinality; i++)
        {
            for(int j = i + 1; j < cardinality; j++)
            {
                List args = new ArrayList(2);
                args.add(yVariable[i]);
                args.add(yVariable[j]);
                inEqualities.add(leFactory.createAtom(wsmoFactory.createIRI(Constants.INEQUAL), args));
            }
        }

        // build LP-rule: "Pnew(?x) :- ?x memberOf <concept> and ?x[<attribute>
        // hasValue ?y1, ... <attribute> hasValue yn] and ?y1!=?y2 and ... and
        // yn-1!=yn."
        Set<LogicalExpression> conjuncts = new HashSet<LogicalExpression>();
        conjuncts.add(moX);
        conjuncts.addAll(Arrays.asList(valXY));
        conjuncts.addAll(inEqualities);
        LogicalExpression conjunction = FixedModificationRules.buildNaryConjunction(conjuncts);
        IRI newPIRI = wsmoFactory.createIRI("mincard_" + conceptID.toString() + "_" + attributeID);
        Atom newPX = leFactory.createAtom(newPIRI, Arrays.asList(new Variable[] { xVariable }));
        minCardConstraints.add(leFactory.createLogicProgrammingRule(newPX, conjunction));

        // build constraint: "!- ?x memberOf <concept> and naf Pnew(?x)."
        NegationAsFailure naf = leFactory.createNegationAsFailure(newPX);
        conjunction = leFactory.createConjunction(moX, naf);
        minCardConstraints.add(leFactory.createConstraint(conjunction));

        return minCardConstraints;
    }

    protected LogicalExpression createMaxCardinalityConstraint(Identifier conceptID, Identifier attributeID, int cardinality)
    {
        cardinality++;

        // build required LE elements:
        Variable xVariable = wsmoFactory.createVariable("x");
        Variable[] yVariable = new Variable[cardinality];
        for(int i = 0; i < yVariable.length; i++)
        {
            yVariable[i] = wsmoFactory.createVariable("y" + Integer.toString(i));
        }
        LogicalExpression moX = leFactory.createMemberShipMolecule(xVariable, conceptID);
        LogicalExpression[] valXY = new LogicalExpression[cardinality];
        for(int i = 0; i < valXY.length; i++)
        {
            valXY[i] = leFactory.createAttributeValue(xVariable, attributeID, yVariable[i]);
        }
        Collection<LogicalExpression> inEqualities = new ArrayList<LogicalExpression>((cardinality * cardinality + cardinality) / 2 + 1);
        for(int i = 0; i < cardinality; i++)
        {
            for(int j = i + 1; j < cardinality; j++)
            {
                List args = new ArrayList(2);
                args.add(yVariable[i]);
                args.add(yVariable[j]);
                inEqualities.add(leFactory.createAtom(wsmoFactory.createIRI(Constants.INEQUAL), args));
            }
        }

        // build constraint: "!- ?x memberOf <concept> and ?x[<attribute>
        // hasValue ?y1, ... <attribute> hasValue yn+1] and ?y1!=?y2 and ... and
        // yn!=yn+1."
        Set<LogicalExpression> conjuncts = new HashSet<LogicalExpression>();
        conjuncts.add(moX);
        conjuncts.addAll(Arrays.asList(valXY));
        conjuncts.addAll(inEqualities);
        LogicalExpression conjunction = FixedModificationRules.buildNaryConjunction(conjuncts);
        return (leFactory.createConstraint(conjunction));
    }

    protected Set<LogicalExpression> normalizeRelation(Relation relation)
    {
        Identifier relationID = relation.getIdentifier();
        Set<LogicalExpression> resultExpressions = new HashSet<LogicalExpression>();

        // process super relations:
        int arity = relation.listParameters().size();
        List<Variable> variables = new ArrayList<Variable>(arity);
        for(int i = 0; i < arity; i++)
        {
            variables.add(wsmoFactory.createVariable("x" + Integer.toString(i)));
        }
        Atom relP = leFactory.createAtom(relationID, variables);
        for(Relation superRelation : (Collection<Relation>)relation.listSuperRelations())
        {
            Atom superRelP = leFactory.createAtom(superRelation.getIdentifier(), variables);
            resultExpressions.add(leFactory.createImplication(relP, superRelP));
        }

        // process relation parameters:
        List<Parameter> parameters = (List<Parameter>)relation.listParameters();
        Collection<LogicalExpression> parameterAxioms = new LinkedList<LogicalExpression>();
        int i = 0;
        for(Parameter parameter : parameters)
        {
            List<MembershipMolecule> typeMemberships = new ArrayList<MembershipMolecule>(parameter.listTypes().size());
            for(Type type : (Collection<Type>)parameter.listTypes())
            {
                Identifier typeID;
                if(type instanceof Concept)
                {
                    typeID = ((Concept)type).getIdentifier();
                }
                else if(type instanceof SimpleDataType)
                {
                    typeID = ((SimpleDataType)type).getIRI();
                }
                else
                {
                    continue;
                }
                typeMemberships.add(leFactory.createMemberShipMolecule(variables.get(i), typeID));
            }
            if(!typeMemberships.isEmpty())
            {
                if(parameter.isConstraining())
                {
                    LogicalExpression molecule = buildMolecule(typeMemberships);
                    NegationAsFailure naf = leFactory.createNegationAsFailure(molecule);
                    Conjunction conjunction = leFactory.createConjunction(relP, naf);
                    parameterAxioms.add(leFactory.createConstraint(conjunction));
                }
                else
                {
                    LogicalExpression molecule = buildMolecule(typeMemberships);
                    parameterAxioms.add(leFactory.createImplication(relP, molecule));
                }
            }
        }
        resultExpressions.addAll(parameterAxioms);

        // process relation instances:
        for(RelationInstance relInstance : (Collection<RelationInstance>)relation.listRelationInstances())
        {
            List<Term> args = new LinkedList<Term>();
            for(Value value : (List<Value>)relInstance.listParameterValues())
            {
                if(value instanceof Instance)
                {
                    Identifier val = ((Instance)value).getIdentifier();
                    args.add(val);
                }
                else if(value instanceof DataValue)
                {
                    DataValue dataValue = (DataValue)value;
                    // args.add(convertDataValue(dataValue));
                    args.add(dataValue);
                }
            }
            resultExpressions.add(leFactory.createAtom(relationID, args));
        }

        return resultExpressions;
    }

    protected LogicalExpression buildMolecule(List<? extends Molecule> molecules)
    {
        if(molecules.size() == 1)
        {
            return molecules.get(0);
        }
        else
        {
            return leFactory.createCompoundMolecule(molecules);
        }
    }

    protected Set<LogicalExpression> normalizeInstance(Instance instance)
    {
        Identifier instanceID = instance.getIdentifier();
        Set<LogicalExpression> resultExpressions = new HashSet<LogicalExpression>();

        // process concepts:
        for(Concept concept : (Collection<Concept>)instance.listConcepts())
        {
            resultExpressions.add(leFactory.createMemberShipMolecule(instanceID, concept.getIdentifier()));
        }

        // process attribute values:
        Map<Attribute, Set<Value>> attributeValues = (Map<Attribute, Set<Value>>)instance.listAttributeValues();
        for(Attribute attribute : attributeValues.keySet())
        {
            Identifier attributeID = attribute.getIdentifier();
            Set<Value> values = attributeValues.get(attribute);
            List<AttributeValueMolecule> molecules = new ArrayList<AttributeValueMolecule>(values.size());
            for(Value value : values)
            {
                Term valueTerm = null;
                if(value instanceof Instance)
                {
                    valueTerm = ((Instance)value).getIdentifier();
                }
                else if(value instanceof DataValue)
                {
                    // valueTerm = convertDataValue((DataValue)value);
                    valueTerm = ((DataValue)value);
                }
                molecules.add(leFactory.createAttributeValue(instanceID, attributeID, valueTerm));
            }
            if(molecules.size() > 1)
            {
                resultExpressions.add(leFactory.createCompoundMolecule(molecules));
            }
            else if(molecules.size() == 1)
            {
                resultExpressions.add(molecules.get(0));
            }
        }

        return resultExpressions;
    }

    private Set toSet(Object o)
    {
        Set s = new HashSet();
        s.add(o);
        return s;
    }

    /**
     * Converts a datavalue object that one finds in the conceptual syntax to a
     * value object that can be used to construct terms and formulae.
     * 
     * @param d -
     *            the data value in the conceptual syntax part that needs to be
     *            converted.
     * @return a org.omwg.logicalexpression.terms.Term object that represents
     *         the same datavalue.
     */
    // private Term convertDataValue(DataValue d)
    // {
    // Term result = null;
    // if(d.getType() instanceof SimpleDataType)
    // {
    // if(d.getType().getIRI().equals(WsmlDataType.WSML_INTEGER))
    // {
    // java.math.BigInteger bigint = (BigInteger) d.getValue();
    // result = leFactory.createWSMLInteger(bigint);
    // }
    // else if(d.getType() instanceof WsmlString)
    // {
    // result = leFactory.createWSMLString(d.asString());
    // }
    // else if(d.getType() instanceof WsmlDecimal)
    // {
    // java.math.BigDecimal bigdec = new java.math.BigDecimal(d.asString());
    // result = leFactory.createWSMLDecimal(bigdec);
    // }
    // }
    // else
    // {
    // // d instanceof ComplexDataType
    // throw new IllegalArgumentException("Complex datatype values are at
    // present not supported by the WSMO4j API!");
    // }
    //
    // return result;
    // }
}
