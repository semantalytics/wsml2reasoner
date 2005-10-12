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
import org.omwg.logicalexpression.CompoundExpression;
import org.omwg.logicalexpression.Conjunction;
import org.omwg.logicalexpression.Constants;
import org.omwg.logicalexpression.LogicalExpression;
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
import org.wsml.reasoner.transformation.le.FixedModificationRules;
import org.wsmo.common.IRI;
import org.wsmo.common.Identifier;
import org.wsmo.common.Namespace;
import org.wsmo.common.UnnumberedAnonymousID;
import org.wsmo.common.exception.InvalidModelException;
import org.wsmo.common.exception.SynchronisationException;
import org.wsmo.factory.Factory;
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

    private org.wsmo.factory.LogicalExpressionFactory leFactory;

    public AxiomatizationNormalizer()
    {
        Map<String, String> leProperties = new HashMap<String, String>();
        leProperties.put(Factory.PROVIDER_CLASS, "org.deri.wsmo4j.logexpression.LogicalExpressionFactoryImpl");
        leFactory = (org.wsmo.factory.LogicalExpressionFactory)Factory.createLogicalExpressionFactory(leProperties);

        wsmoFactory = Factory.createWsmoFactory(null);
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
    public Ontology normalize(Ontology o)
    {

        // NOTE: the implementation at present assumes that every onotlogy
        // element
        // is identified by an IRI.
        // This should be ensured before proceeding by an additional
        // normalization step.

        // Set up factories for creating WSMO/WSML elements

        String resultIRI = (o.getIdentifier() != null ? o.getIdentifier().toString() + "-as-axioms" : "iri:normalized-ontology-" + o.hashCode());
        Ontology result = wsmoFactory.createOntology(wsmoFactory.createIRI(resultIRI));
        try
        {

            // Namespace defs.

            // Copy namespace defs.
            for(Object n : o.listNamespaces())
            {
                result.addNamespace((Namespace)n);
            }

            result.setDefaultNamespace(o.getDefaultNamespace());

            // Axioms
            for(Object a : o.listAxioms())
            {
                result.addAxiom((Axiom)a);
            }

            // Concepts
            for(Object c : o.listConcepts())
            {
                handleConcept((Concept)c, result);
            }

            // Relations
            for(Object r : o.listRelations())
            {
                handleRelation((Relation)r, result);
            }

            // Concept instances
            for(Object ci : o.listInstances())
            {
                handleConceptInstance((Instance)ci, result);
            }

            // Relation instances are handled inside relations already.

            // Nonfunctional properties

            Map nfps = o.listNFPValues();
            for(Object nextNFP : nfps.entrySet())
            {
                Map.Entry entry = (Map.Entry)nextNFP;
                result.addNFPValue((IRI)entry.getKey(), entry.getValue());
            }

        } catch(SynchronisationException e)
        {
            e.printStackTrace();
            result = null;

        } catch(InvalidModelException e)
        {
            e.printStackTrace();
            result = null;
        }

        return result;
    }

    /**
     * Inserts axioms that represent the concept in logical terms.
     * 
     * @param c -
     *            the concept to be translated to logical expressions.
     * @param o -
     *            the ontoloy to which to add the respective representation
     *            axioms
     * @throws InvalidModelException
     * @throws SynchronisationException
     */
    private void handleConcept(Concept c, Ontology o) throws SynchronisationException, InvalidModelException
    {
        Set<LogicalExpression> lExprs = new HashSet<LogicalExpression>();

        LogicalExpression expr;
        Set subConceptOfs = new HashSet();
        Set attrSpecs = new HashSet();

        Term cTerm = convertIRI(c.getIdentifier());

        Term t;
        for(Object sc : c.listSuperConcepts())
        {
            t = convertIRI(((Concept)sc).getIdentifier());
            subConceptOfs.add(t);
        }

        for(Object next : c.listAttributes())
        {
            Attribute a = (Attribute)next;
            lExprs.addAll(handleConceptAttribute(a, cTerm));
        }

        if(subConceptOfs.size() > 0)
        {
            expr = leFactory.createMolecule(cTerm, subConceptOfs, null, null);
            lExprs.add(expr);
        }

        int i = 1;
        String axPrefix = "Axiom-" + convertIRI(c.getIdentifier()).asString();
        for(LogicalExpression l : lExprs)
        {
            Axiom ax = wsmoFactory.createAxiom(wsmoFactory.createIRI(axPrefix + "-" + (i++)));
            ax.addDefinition(l);
            o.addAxiom(ax);
        }
    }
    
    protected Set<LogicalExpression> normalizeConcept(Concept concept)
    {
        Identifier conceptID = concept.getIdentifier();
        Set<LogicalExpression> resultExpressions = new HashSet<LogicalExpression>();

        //process superconcepts:
        for(Concept superconcept : (Set<Concept>)concept.listSuperConcepts())
        {
            Identifier superconceptID = superconcept.getIdentifier();
            resultExpressions.add(leFactory.createSubConceptMolecule(conceptID, superconceptID));
        }

        //process attributes:
        for(Attribute attribute : (Set<Attribute>)concept.listAttributes())
        {
            resultExpressions.addAll(normalizeConceptAttribute(conceptID, attribute));
        }
/*        
        LogicalExpression expr;
        Set subConceptOfs = new HashSet();
        Set attrSpecs = new HashSet();

        Term cTerm = convertIRI(c.getIdentifier());

        Term t;
        for(Object sc : c.listSuperConcepts())
        {
            t = convertIRI(((Concept)sc).getIdentifier());
            subConceptOfs.add(t);
        }

        for(Object next : c.listAttributes())
        {
            Attribute a = (Attribute)next;
            lExprs.addAll(handleConceptAttribute(a, cTerm));
        }

        if(subConceptOfs.size() > 0)
        {
            expr = leFactory.createMolecule(cTerm, subConceptOfs, null, null);
            lExprs.add(expr);
        }

        int i = 1;
        String axPrefix = "Axiom-" + convertIRI(c.getIdentifier()).asString();
        for(LogicalExpression l : lExprs)
        {
            Axiom ax = wsmoFactory.createAxiom(wsmoFactory.createIRI(axPrefix + "-" + (i++)));
            ax.addDefinition(l);
            o.addAxiom(ax);
        }
*/        
    }    

    /**
     * Inserts axioms that represent the attribute of a concept in logical
     * terms.
     * 
     * @param a -
     *            the attribute to be translated to logical expressions.
     * @param cTerm -
     *            the term that represents the respective class
     * @return the set of logical expressions to which to add the respective
     *         translation.
     * @throws InvalidModelException
     */
    private Set handleConceptAttribute(Attribute a, Term cTerm) throws InvalidModelException
    {
        Set<org.omwg.logicalexpression.LogicalExpression> result = new HashSet<org.omwg.logicalexpression.LogicalExpression>();
        Term attID = convertIRI(a.getIdentifier());

        Set<Type> rangeTypes = a.listTypes();
        Set<Term> moList = new HashSet<Term>();
        for(Type type : rangeTypes)
        {
            if(type instanceof Concept)
            {
                org.omwg.logicalexpression.terms.IRI tIRI = convertIRI(((Concept)type).getIdentifier());
                moList.add(tIRI);
            }
            else if(type instanceof SimpleDataType)
            {
                org.omwg.logicalexpression.terms.IRI tIRI = convertIRI(((SimpleDataType)type).getIRI());
                moList.add(tIRI);
            }
            else if(type instanceof ComplexDataType)
            {
                throw new IllegalArgumentException("Complex datatype are currently not supported in Normalization to LogicalExpressions");
                // TODO include complex data types as well.
            }
        }

        // Handle attribute type
        org.omwg.logicalexpression.AttrSpecification attSpec = null;
        if(a.isConstraining())
        { // ofType
            attSpec = leFactory.createAttrSpecification(AttrSpecification.ATTR_CONSTRAINT, attID, moList);
        }
        else
        { // impliesType
            attSpec = leFactory.createAttrSpecification(AttrSpecification.ATTR_INFERENCE, attID, moList);
        }

        org.omwg.logicalexpression.LogicalExpression typeExpr = leFactory.createMolecule(cTerm, null, null, toSet(attSpec));
        result.add(typeExpr);

        // Handle Attribute Feature
        Term x = leFactory.createVariable("x");
        Term y = leFactory.createVariable("y");
        Term z = leFactory.createVariable("z");

        org.omwg.logicalexpression.LogicalExpression m1, m2, m3, m4, m5, m6, m7;

        m1 = leFactory.createMolecule(x, null, toSet(cTerm), null);
        m2 = leFactory.createMolecule(y, null, toSet(cTerm), null);

        AttrSpecification attrSpec1 = leFactory.createAttrSpecification(AttrSpecification.ATTR_VALUE, attID, toSet(y));
        m3 = leFactory.createMolecule(x, null, null, toSet(attrSpec1));

        AttrSpecification attrSpec2 = leFactory.createAttrSpecification(AttrSpecification.ATTR_VALUE, attID, toSet(z));
        m4 = leFactory.createMolecule(y, null, null, toSet(attrSpec2));

        m5 = leFactory.createMolecule(x, null, null, toSet(attrSpec2));

        AttrSpecification attrSpec3 = leFactory.createAttrSpecification(AttrSpecification.ATTR_VALUE, attID, toSet(x));
        m6 = leFactory.createMolecule(y, null, null, toSet(attrSpec3));
        m7 = leFactory.createMolecule(x, null, null, toSet(attrSpec3));

        if(a.isTransitive())
        {

            org.omwg.logicalexpression.LogicalExpression h1, h2, h3;
            h1 = leFactory.createBinary(CompoundExpression.AND, m1, m2);
            h2 = leFactory.createBinary(CompoundExpression.AND, h1, m3);
            h3 = leFactory.createBinary(CompoundExpression.AND, h2, m4);
            org.omwg.logicalexpression.LogicalExpression transExpr = leFactory.createBinary(CompoundExpression.IMPLIES, h3, m5);

            result.add(transExpr);

        }

        if(a.isSymmetric())
        {

            org.omwg.logicalexpression.LogicalExpression h1, h2;
            h1 = leFactory.createBinary(CompoundExpression.AND, m1, m2);
            h2 = leFactory.createBinary(CompoundExpression.AND, h1, m3);

            org.omwg.logicalexpression.LogicalExpression symmExpr = leFactory.createBinary(CompoundExpression.IMPLIES, h2, m6);

            result.add(symmExpr);

        }

        if(a.isReflexive())
        {

            org.omwg.logicalexpression.LogicalExpression reflExpr = leFactory.createBinary(CompoundExpression.IMPLIES, m1, m7);

            result.add(reflExpr);

        }

        if(a.getInverseOf() != null)
        {

            org.omwg.logicalexpression.LogicalExpression h1, h2;
            Attribute inverseAtt = a.getInverseOf();
            IRI inversetAttIRI = convertIRI(a.getIdentifier());

            AttrSpecification attrSpec4 = leFactory.createAttrSpecification(AttrSpecification.ATTR_VALUE, inversetAttIRI, toSet(x));
            org.omwg.logicalexpression.LogicalExpression m8 = leFactory.createMolecule(y, null, null, toSet(attrSpec4));

            h1 = leFactory.createBinary(CompoundExpression.AND, m1, m3);

            org.omwg.logicalexpression.LogicalExpression invExpr1 = leFactory.createBinary(CompoundExpression.IMPLIES, h1, m8);

            h2 = leFactory.createBinary(CompoundExpression.AND, m1, m8);
            org.omwg.logicalexpression.LogicalExpression invExpr2 = leFactory.createBinary(CompoundExpression.IMPLIES, h2, m3);

            result.add(invExpr1);
            result.add(invExpr2);

        }

        // Handle Cardinality constraints

        if(a.getMinCardinality() > 0)
        {

            List headArgs = new LinkedList();
            headArgs.add(x);
            org.omwg.logicalexpression.LogicalExpression head = leFactory.createAtom(leFactory.createIRI("mincard_" + cTerm.toString() + "_" + attID), headArgs); // is
            // new
            // and
            // unique
            // within
            // the
            // onotlogy!

            Set xAttVals = new HashSet();
            Variable[] auxVars = new Variable[a.getMinCardinality()];
            for(int i = 0; i < a.getMinCardinality(); i++)
            {
                Variable nextVar = leFactory.createVariable("y" + (i + 1));
                auxVars[i] = nextVar;
                xAttVals.add(nextVar);
            }
            AttrSpecification as = leFactory.createAttrSpecification(AttrSpecification.ATTR_VALUE, attID, xAttVals);
            org.omwg.logicalexpression.LogicalExpression xValsMolecule = leFactory.createMolecule(x, null, null, toSet(as));

            org.omwg.logicalexpression.LogicalExpression body = leFactory.createBinary(CompoundExpression.AND, m1, xValsMolecule);

            // add all inequality statements on pairs of auxilliary predicates.
            for(int i = 0; i < a.getMinCardinality(); i++)
            {
                for(int j = i + 1; j < a.getMinCardinality(); j++)
                {
                    List iesArgs = new LinkedList();
                    iesArgs.add(auxVars[i]);
                    iesArgs.add(auxVars[j]);
                    org.omwg.logicalexpression.LogicalExpression nextInEqStatement = leFactory.createAtom(leFactory.createIRI(Constants.INEQUAL), iesArgs);

                    body = leFactory.createBinary(CompoundExpression.AND, body, nextInEqStatement);
                }
            }

            org.omwg.logicalexpression.LogicalExpression minCardExpr1 = leFactory.createBinary(CompoundExpression.LP_IMPL, head, body);

            org.omwg.logicalexpression.LogicalExpression nafExp = leFactory.createUnary(CompoundExpression.NAF, head);

            org.omwg.logicalexpression.LogicalExpression constraintbody = leFactory.createBinary(CompoundExpression.AND, m1, nafExp);

            org.omwg.logicalexpression.LogicalExpression minCardExpr2 = leFactory.createUnary(CompoundExpression.CONSTRAINT, constraintbody);

            result.add(minCardExpr1);
            result.add(minCardExpr2);

        }

        if(a.getMaxCardinality() < Integer.MAX_VALUE)
        {
            // we have a max cardinality ...

            Set xAttVals = new HashSet();
            Variable[] auxVars = new Variable[a.getMaxCardinality() + 1];
            for(int i = 0; i <= a.getMaxCardinality(); i++)
            {
                Variable nextVar = leFactory.createVariable("y" + (i + 1));
                auxVars[i] = nextVar;
                xAttVals.add(nextVar);
            }
            AttrSpecification as = leFactory.createAttrSpecification(AttrSpecification.ATTR_VALUE, attID, xAttVals);
            org.omwg.logicalexpression.LogicalExpression xValsMolecule = leFactory.createMolecule(x, null, null, toSet(as));

            org.omwg.logicalexpression.LogicalExpression body = leFactory.createBinary(CompoundExpression.AND, m1, xValsMolecule);

            // add all inequality statements on pairs of auxilliary predicates.
            for(int i = 0; i <= a.getMaxCardinality(); i++)
            {
                for(int j = i + 1; j <= a.getMaxCardinality(); j++)
                {
                    List iesArgs = new LinkedList();
                    iesArgs.add(auxVars[i]);
                    iesArgs.add(auxVars[j]);
                    org.omwg.logicalexpression.LogicalExpression nextInEqStatement = leFactory.createAtom(leFactory.createIRI(Constants.INEQUAL), iesArgs);

                    body = leFactory.createBinary(CompoundExpression.AND, body, nextInEqStatement);
                }
            }

            org.omwg.logicalexpression.LogicalExpression maxCardExpr = leFactory.createUnary(CompoundExpression.CONSTRAINT, body);

            result.add(maxCardExpr);

        }

        return result;
    }
    
    protected Set<LogicalExpression> normalizeConceptAttribute(Identifier conceptID, Attribute attribute)
    {
        Identifier attributeID = attribute.getIdentifier();
        Set<LogicalExpression> resultExpressions = new HashSet<LogicalExpression>();

        //process range types:
        Set<Identifier> rangeTypes = new HashSet<Identifier>();
        for(Type type : (Set<Type>)attribute.listTypes())
        {
            //determine Id of range type:
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

            //create an appropriate molecule per range type:
            if(attribute.isConstraining())
            {
                resultExpressions.add(leFactory.createAttributeConstraint(conceptID, attributeID, typeID));
            }
            else
            {
                resultExpressions.add(leFactory.createAttributeInference(conceptID, attributeID, typeID));
            }
        }
        
        //process attribute properties:
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
            
        //process cardinality constraints:
        if(attribute.getMinCardinality() > 0)
        {
            resultExpressions.add(createMinCardinalityConstraint(conceptID, attributeID));
        }
        if(attribute.getMaxCardinality() > 0)
        {
            resultExpressions.add(createMaxCardinalityConstraint(conceptID, attributeID));
        }


        return result;
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
        
        //build required LE elements:
        Variable xVariable = wsmoFactory.createVariable("x");
        Variable yVariable = wsmoFactory.createVariable("y");
        LogicalExpression moX = leFactory.createMemberShipMolecule(xVariable, conceptID);
        LogicalExpression moY = leFactory.createMemberShipMolecule(yVariable, conceptID);
        LogicalExpression valXY = leFactory.createAttributeValue(xVariable, attributeID, yVariable);
        LogicalExpression valInvXY = leFactory.createAttributeValue(xVariable, inverseAttributeID, yVariable);
        LogicalExpression valYX = leFactory.createAttributeValue(yVariable, attributeID, xVariable);
        LogicalExpression valInvYX = leFactory.createAttributeValue(yVariable, inverseAttributeID, xVariable);

        //build implication : "..."
        LogicalExpression conjunction = leFactory.createConjunction(moX, valXY);
        inverseConstraints.add(leFactory.createImplication(conjunction, valInvYX));

        //build implication : "..."
        conjunction = leFactory.createConjunction(moY, valInvXY);
        inverseConstraints.add(leFactory.createImplication(conjunction, valYX));
        
        return inverseConstraints;
    }
    
    protected Collection<LogicalExpression> createMinCardinalityConatraint(Identifier conceptID, Identifier attributeID, int cardinality)
    {
        Collection<LogicalExpression> minCardConstraints = new ArrayList<LogicalExpression>(2);
        
        //build required LE elements:
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
        
        //build LP-rule: "Pnew(?x) :- ?x memberOf <concept> and ?x[<attribute> hasValue ?y1, ... <attribute> hasValue yn] and ?y1!=?y2 and ... and yn-1!=yn."
        Set<LogicalExpression> conjuncts = new HashSet<LogicalExpression>();
        conjuncts.add(moX);
        conjuncts.addAll(Arrays.asList(valXY));
        conjuncts.addAll(inEqualities);
        LogicalExpression conjunction = FixedModificationRules.buildNaryConjunction(conjuncts);
        IRI newPIRI = wsmoFactory.createIRI("mincard_" + conceptID.toString() + "_" + attributeID);
        Atom newPX = leFactory.createAtom(newPIRI, Arrays.asList(new Variable[]{xVariable}));
        minCardConstraints.add(leFactory.createLogicProgrammingRule(newPX, conjunction));

        //build constraint: "!- ?x memberOf <concept> and naf Pnew(?x)."
        NegationAsFailure naf = leFactory.createNegationAsFailure(newPX);
        conjunction = leFactory.createConjunction(moX, naf);
        minCardConstraints.add(leFactory.createConstraint(conjunction));
        
        return minCardConstraints;
    }
    


    /**
     * Inserts axioms that represent the instance in logical terms.
     * 
     * @param i -
     *            the instance to be translated to logical expressions.
     * @param o -
     *            the ontoloy to which to add the respective representation
     *            axioms
     * @throws InvalidModelException
     * @throws SynchronisationException
     */
    private void handleConceptInstance(Instance i, Ontology o) throws SynchronisationException, InvalidModelException
    {
        Set<LogicalExpression> lExprs = new HashSet<LogicalExpression>();

        LogicalExpression expr;
        Set memberOfs = new HashSet();
        Set attrSpecs = new HashSet();

        Term iTerm = convertIRI(i.getIdentifier());

        Term t;
        for(Object mo : i.listConcepts())
        {
            t = convertIRI(((Concept)mo).getIdentifier());
            memberOfs.add(t);
        }
        Map attsAndValues = i.listAttributeValues();

        for(Object next : attsAndValues.keySet())
        {
            Attribute a = (Attribute)next;
            Set<Value> aVals = (Set<Value>)attsAndValues.get(next);
            lExprs.add(handleInstanceAttribute(a, aVals, iTerm));
        }

        if(memberOfs.size() > 0)
        {
            expr = leFactory.createMolecule(iTerm, null, memberOfs, null);
            lExprs.add(expr);
        }

        int j = 1;
        String axPrefix = "Axiom-" + convertIRI(i.getIdentifier()).asString();
        for(LogicalExpression l : lExprs)
        {
            Axiom ax = wsmoFactory.createAxiom(wsmoFactory.createIRI(axPrefix + "-" + (j++)));
            ax.addDefinition(l);
            o.addAxiom(ax);
        }

    }

    /**
     * Inserts axioms that represent the attribute of a instance of a concept in
     * logical terms.
     * 
     * @param a -
     *            the attribute to be translated to logical expressions.
     * @param aVals -
     *            the values that are assigned to the attribute for the given
     *            instance
     * @param iTerm -
     *            the term that represents the respective instance
     * @return a logical expression that represents the instance attribute value
     *         definition.
     * @throws InvalidModelException
     */
    private org.omwg.logicalexpression.LogicalExpression handleInstanceAttribute(Attribute a, Set aVals, Term iTerm) throws InvalidModelException
    {

        org.omwg.logicalexpression.LogicalExpression result;

        // This is like it has been described from Table 8.1 in D.16.1 v0.3

        Term attID = convertIRI(a.getIdentifier());

        Set<Term> valList = new HashSet<Term>();
        for(Object obj : aVals)
        {
            if(obj instanceof Instance)
            { // THIS SEEMS NOT TO BE USED RIGHT
                // NOW:
                Identifier val = ((Instance)obj).getIdentifier();
                valList.add(convertIRI(val));
            }
            else if(obj instanceof DataValue)
            {
                DataValue d = (DataValue)obj;
                Term dVal = convertDataValue(d);
                valList.add(dVal);
            }

        }

        org.omwg.logicalexpression.AttrSpecification attSpec = leFactory.createAttrSpecification(AttrSpecification.ATTR_VALUE, attID, valList);

        result = leFactory.createMolecule(iTerm, null, null, toSet(attSpec));

        return result;
    }

    /**
     * Inserts axioms that represent the relation in logical terms.
     * 
     * @param r -
     *            the relation to be translated to logical expressions.
     * @param o -
     *            the ontoloy to which to add the respective representation
     *            axioms
     * @throws InvalidModelException
     * @throws SynchronisationException
     */
    private void handleRelation(Relation r, Ontology o) throws SynchronisationException, InvalidModelException
    {

        Set<org.omwg.logicalexpression.LogicalExpression> lExprs = new HashSet<org.omwg.logicalexpression.LogicalExpression>();

        IRI rID = convertIRI(r.getIdentifier());
        int rArity = r.listParameters().size();
        Set<Relation> suprels = r.listSuperRelations();

        List<Term> predArgs = new LinkedList();
        for(int i = 0; i < rArity; i++)
        {
            org.omwg.ontology.Variable v = leFactory.createVariable("x" + (i + 1));
            predArgs.add(v);
        }

        org.omwg.logicalexpression.LogicalExpression a1 = leFactory.createAtom(rID, predArgs);

        for(Relation nextSuperRelation : suprels)
        {
            IRI srID = convertIRI(nextSuperRelation.getIdentifier());
            org.omwg.logicalexpression.LogicalExpression a2 = leFactory.createAtom(srID, predArgs);
            org.omwg.logicalexpression.LogicalExpression newExpr = leFactory.createBinary(CompoundExpression.IMPLIES, a1, a2);
            lExprs.add(newExpr);
        }

        // Handle parameter definitions of relation (currently missing in
        // Table 8.1 in D16.1

        List<Parameter> rParams = r.listParameters();
        int i = 1;
        for(Parameter p : rParams)
        {
            lExprs.add(handleRelationParameter(p, rID, i, rArity));
            i++;
        }

        // Handle relation instances

        Set<RelationInstance> rInstances = r.listRelationInstances();
        for(RelationInstance ri : rInstances)
        {
            lExprs.add(handleRelationInstance(ri, rID));
        }

        // Generate axioms in the ontology
        i = 1;
        String axPrefix = "Axiom-" + rID.asString();
        for(LogicalExpression l : lExprs)
        {
            Axiom ax = wsmoFactory.createAxiom(wsmoFactory.createIRI(axPrefix + "-" + (i++)));
            ax.addDefinition(l);
            o.addAxiom(ax);
        }

    }

    /**
     * Inserts axioms that represent a parameter of a relation in logical terms.
     * 
     * @param rp -
     *            the parameter of a relation in the ontology to be translated
     *            to logical expressions.
     * @param rID-
     *            the IRI of the respective relation to which the parameter
     *            belongs to.
     * @param pos -
     *            the index of the parameter in the parameter list of the
     *            respective relation (1-based)
     * @param arity -
     *            the arity of the respective relation
     * @param o -
     *            the ontoloy to which to add the respective representation
     *            axioms
     * @throws InvalidModelException
     */
    private org.omwg.logicalexpression.LogicalExpression handleRelationParameter(Parameter rp, IRI rID, int pos, int arity) throws InvalidModelException
    {
        org.omwg.logicalexpression.LogicalExpression result;

        Term paramVar = null;
        List<Variable> paramVars = new LinkedList();

        for(int j = 1; j <= arity; j++)
        {
            Variable nextVar = leFactory.createVariable("x" + j);
            paramVars.add(nextVar);
            if(j == pos)
            {
                paramVar = nextVar;
            }
        }

        org.omwg.logicalexpression.LogicalExpression body = leFactory.createAtom(rID, paramVars);

        Set<Type> rangeTypes = rp.listTypes();
        Set<Term> rangeTermList = new HashSet<Term>();
        for(Object type : rangeTypes)
        {
            if(type instanceof Concept)
            {
                org.omwg.logicalexpression.terms.IRI tIRI = convertIRI(((Concept)type).getIdentifier());
                rangeTermList.add(tIRI);
            }
            else if(type instanceof SimpleDataType)
            {
                org.omwg.logicalexpression.terms.IRI tIRI = convertIRI(((SimpleDataType)type).getIRI());
                rangeTermList.add(tIRI);
            }
            else if(type instanceof ComplexDataType)
            {
                throw new IllegalArgumentException("Complex datatype are currently not supported in Normalization to LogicalExpressions");
                // TODO include complex data types as well.
            }
        }

        org.omwg.logicalexpression.LogicalExpression head = leFactory.createMolecule(paramVar, null, rangeTermList, null);

        if(!rp.isConstraining())
        {
            // impliesType
            result = leFactory.createBinary(CompoundExpression.IMPLIES, body, head);
        }
        else
        {
            // ofType
            org.omwg.logicalexpression.LogicalExpression naf = leFactory.createUnary(CompoundExpression.NAF, head);
            org.omwg.logicalexpression.LogicalExpression cBody = leFactory.createBinary(CompoundExpression.AND, body, naf);
            result = leFactory.createUnary(CompoundExpression.CONSTRAINT, cBody);
        }

        return result;
    }

    /**
     * Inserts axioms that represent the instance of a relation in logical
     * terms.
     * 
     * @param ri -
     *            the instance of a relation in the ontology to be translated to
     *            logical expressions.
     * @param rID -
     *            the IRI of the relation to which this instance belongs to
     */
    private org.omwg.logicalexpression.LogicalExpression handleRelationInstance(RelationInstance ri, IRI rID)
    {

        List<Value> parVals = ri.listParameterValues();
        List<Term> args = new LinkedList();

        for(Value v : parVals)
        {
            if(v instanceof Instance)
            {
                Identifier val = ((Instance)v).getIdentifier();
                args.add(convertIRI(val));
            }
            else if(v instanceof DataValue)
            {
                DataValue d = (DataValue)v;
                Term dVal = convertDataValue(d);
                args.add(dVal);
            }
        }

        return leFactory.createAtom(rID, args);

    }

    // Some helper methods for convenience

    /**
     * Converts a given IRI from WSMO4j to a IRI that can be used in logical
     * expressions.
     * 
     * @param id
     * @return
     */
    private IRI convertIRI(Identifier id)
    {
        if(id instanceof UnnumberedAnonymousID)
            return leFactory.createIRI(AnonymousIdUtils.getNewIri());
        else
            return leFactory.createIRI(id.toString());
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
    private Term convertDataValue(DataValue d)
    {
        Term result = null;
        if(d.getType() instanceof SimpleDataType)
        {
            if(d.getType() instanceof WsmlInteger)
            {
                java.math.BigInteger bigint = new java.math.BigInteger(d.asString());
                result = leFactory.createWSMLInteger(bigint);
            }
            else if(d.getType() instanceof WsmlString)
            {
                result = leFactory.createWSMLString(d.asString());
            }
            else if(d.getType() instanceof WsmlDecimal)
            {
                java.math.BigDecimal bigdec = new java.math.BigDecimal(d.asString());
                result = leFactory.createWSMLDecimal(bigdec);
            }
        }
        else
        {
            // d instanceof ComplexDataType
            throw new IllegalArgumentException("Complex datatype values are at present not supported by the WSMO4j API!");
        }

        return result;
    }

}
