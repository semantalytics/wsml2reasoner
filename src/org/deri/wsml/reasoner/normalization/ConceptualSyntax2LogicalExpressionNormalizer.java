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

package org.deri.wsml.reasoner.normalization;

import java.math.*;
import java.util.*;
import org.omwg.ontology.*;
import org.wsmo.common.*;
import org.wsmo.common.exception.InvalidModelException;
import org.wsmo.common.exception.SynchronisationException;
import org.wsmo.factory.Factory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.Parser;
import org.omwg.logexpression.AttrSpecification;
import org.omwg.logexpression.CompoundExpression;
import org.omwg.logexpression.Constants;
import org.omwg.logexpression.terms.IRI;
import org.omwg.logexpression.terms.Term;
import org.omwg.logexpression.terms.Value;
import org.omwg.logexpression.terms.Variable;


/**
 * A normalization step of an ontology that transforms the conceptual
 * syntax part to logical expressions. Hence, it transforms a WSML 
 * ontology to a set of logical expressions or to be technically more 
 * precise a set of axioms. 
 * 
 * ASSUMPTIONS: the transformation assumes the following:
 * All ontology elements are identified and the only identifier
 * we have to deal with in ontology descriptions are IRIs.
 * 
 * In order to guarantee the assumptions, one possibly must run a separate
 * normalization step before running this one.
 * 
 * NOTE: At present the transformation does not support COMPLEX DATATYPES
 * and their respective values that are present in WSML! SIMPLE DATATYPES 
 * and their values are supported.
 * 
 *  The result is presented as an ontology which consists of axioms
 *  only.
 *  
 *  Axioms of the original ontology are inserted themselves in the new onotology 
 *  that mean no copies of axioms are created.
 *  
 *  Namespace def. and non-functional properties 
 *  are taken over as well into the new ontology.
 *  
 *  Technically, the transformation implements Table 8.1 of Deliverable D16.1 v0.3
 *  of the WSML Working Group
 *  
 * @author Uwe Keller, DERI Innsbruck
 */
public class ConceptualSyntax2LogicalExpressionNormalizer implements
        WSMLOntologyNormalizer {

    private WsmoFactory factory; 
    private org.omwg.logexpression.LogicalExpressionFactory leFactory;
       
    
    public ConceptualSyntax2LogicalExpressionNormalizer() {
        Map<String, String> leProperties = new HashMap<String, String>();
        leProperties.put(Factory.PROVIDER_CLASS,
                "org.deri.wsmo4j.logexpression.LogicalExpressionFactoryImpl");

        leFactory = (org.omwg.logexpression.LogicalExpressionFactory) Factory
                .createLogicalExpressionFactory(leProperties);

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(Factory.PROVIDER_CLASS,
                "com.ontotext.wsmo4j.factory.WsmoFactoryImpl");
        properties.put(Parser.PARSER_LE_FACTORY, leFactory);
        factory = Factory.createWsmoFactory(properties);
    }
    
    /**
     * Performs the transformation that is described above.
     * @param o - the ontology for which we need to resolve the
     * conceptual syntax part.
     * 
     * @return an ontology represent o semantically but only consists of axioms
     */
    public Ontology normalize(Ontology o) {

        // NOTE: the implementation at present assumes that every onotlogy element
        // is identified by an IRI. 
        // This should be ensured before proceeding by an additional 
        // normalization step.
           
        // Set up factories for creating WSMO/WSML elements 

        String resultIRI = (o.getIdentifier() != null ? o.getIdentifier()
                .asString() : "iri:normalized-ontology-" + o.hashCode());
        Ontology result = factory.createOntology(factory.createIRI(resultIRI));
        try {

            // Namespace defs.

            // Copy namespace defs.
            for (Object n : o.listNamespaces()) {
                result.addNamespace((Namespace) n);
            }

            result.setDefaultNamespace(o.getDefaultNamespace());

            // Axioms
            for (Object a : o.listAxioms()) {
                result.addAxiom((Axiom) a);
            }

            // Concepts
            for (Object c : o.listConcepts()) {
                handleConcept((Concept)c, result);
            }
         
            
            // Relations
            for (Object r : o.listRelations()) {
                handleRelation((Relation)r, result);
            }

            // Concept instances
            for (Object ci : o.listInstances()) {
                handleConceptInstance((Instance)ci, result);
            }

            // Relation instances are handled inside relations already.
   
            // Nonfunctional properties

            Map nfps = o.listNFPValues();
            for (Object nextNFP : nfps.entrySet()) {
                Map.Entry entry = (Map.Entry) nextNFP;
                result.addNFPValue((org.wsmo.common.IRI) entry.getKey(), entry.getValue());
            }

        } catch (SynchronisationException e) {
            e.printStackTrace();
            result = null;
        } catch (InvalidModelException e) {
            e.printStackTrace();
            result = null;
        }
        
        return result;
    }

    /**
     * Inserts axioms that represent the concept in logical terms.
     * @param c - the concept to be translated to logical expressions.
     * @param o - the ontoloy to which to add the respective representation axioms
     * @throws InvalidModelException 
     * @throws SynchronisationException 
     */
    private void handleConcept(Concept c, Ontology o) throws SynchronisationException, InvalidModelException{
        
        Set<LogicalExpression> lExprs = new HashSet<LogicalExpression>();
        
        LogicalExpression expr;
        Set subConceptOfs = new HashSet();
        Set attrSpecs = new HashSet();
      
        Term cTerm = convertIRI((org.wsmo.common.IRI) c.getIdentifier());
        
        Term t;
        for( Object sc : c.listSuperConcepts() ){
            t = convertIRI((org.wsmo.common.IRI)((Concept) sc).getIdentifier());
            subConceptOfs.add(t);
        }
        
        for( Object next : c.listAttributes() ){
            Attribute a = (Attribute) next;
            lExprs.addAll(handleConceptAttribute(a, cTerm));
        }
        
        expr = leFactory.createMolecule(cTerm, subConceptOfs, null, null);
        
        lExprs.add(expr);
        
        int i = 1;
        String axPrefix =  "Axiom-" + c.getIdentifier().asString(); 
        for (LogicalExpression l : lExprs) {
            Axiom ax = factory.createAxiom(factory.createIRI( axPrefix + (i++) ));
            ax.addDefinition(l);
            o.addAxiom(ax);
        }
    }
    
    /**
     * Inserts axioms that represent the attribute of a concept in logical terms.
     * @param a - the attribute to be translated to logical expressions.
     * @param cTerm - the term that represents the respective class
     * @return the set of logical expressions to which to add the respective
     *         translation.
     * @throws InvalidModelException 
     */
    private Set handleConceptAttribute(Attribute a, Term cTerm) throws InvalidModelException{
        Set<org.omwg.logexpression.LogicalExpression> result = new HashSet<org.omwg.logexpression.LogicalExpression>();
        
        // This is like it has been described from Table 8.1 in D.16.1 v0.3
       
        org.omwg.logexpression.LogicalExpression moExpr, hvExpr, rangeMoExpr;
        
        Term v = leFactory.createVariable("?x");
        moExpr = leFactory.createMolecule(v,toSet(v), null, null);
        
        Term attID = convertIRI((org.wsmo.common.IRI) a.getIdentifier());
        Term v2 = leFactory.createVariable("?y");
        org.omwg.logexpression.AttrSpecification attSpec = 
            leFactory.createAttrSpecification(AttrSpecification.ATTR_VALUE, attID, toSet(v2));
            
        hvExpr = leFactory.createMolecule(v, null, null, toSet(attSpec));
        
        Set<Type> rangeTypes = a.listTypes();
        Set<Term> moList = new HashSet<Term>(); 
        for (Type type : rangeTypes) {
            if (type instanceof Concept){
                org.omwg.logexpression.terms.IRI  tIRI = 
                    convertIRI((org.wsmo.common.IRI)((Concept) type).getIdentifier()); 
                moList.add(tIRI);
            } else if (type instanceof SimpleDataType){
                org.omwg.logexpression.terms.IRI  tIRI = 
                    convertIRI(((SimpleDataType) type).getIRI()); 
                moList.add(tIRI);
            } else if (type instanceof ComplexDataType){
                throw new IllegalArgumentException("Complex datatype are currently not supported in Normalization to LogicalExpressions");
                // TODO include complex data types as well.
            }
        }
        
        rangeMoExpr = leFactory.createMolecule(v, null, moList, null);
        
        // Build the complete logical expression and add it 
        org.omwg.logexpression.LogicalExpression le;
        org.omwg.logexpression.LogicalExpression e1 = leFactory.createBinary(CompoundExpression.AND, moExpr, hvExpr);
        if (!a.isConstraining()){
            // impliesType
            le = leFactory.createBinary(CompoundExpression.IMPLIES, e1, rangeMoExpr);
        } else {
            // ofType
            org.omwg.logexpression.LogicalExpression nafExpr = leFactory.createUnary(CompoundExpression.NAF, rangeMoExpr);
            org.omwg.logexpression.LogicalExpression e2 = leFactory.createBinary(CompoundExpression.AND, e1, nafExpr);
            le = leFactory.createUnary(CompoundExpression.CONSTRAINT, e2);
        }
        
        result.add(le);
                
        // Handle Attribute Feature
        Term x = leFactory.createVariable("?x");
        Term y = leFactory.createVariable("?y");
        Term z = leFactory.createVariable("?z");
        
        org.omwg.logexpression.LogicalExpression m1, m2, m3, m4, m5, m6, m7;
        
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
        
        if (a.isTransitive()){
            
            org.omwg.logexpression.LogicalExpression h1,h2,h3;
            h1 = leFactory.createBinary(CompoundExpression.AND, m1, m2);
            h2 = leFactory.createBinary(CompoundExpression.AND, h1, m3);
            h3 = leFactory.createBinary(CompoundExpression.AND, h2, m4);
            org.omwg.logexpression.LogicalExpression transExpr = 
                leFactory.createBinary(CompoundExpression.IMPLIES, h3, m5);
            
            result.add(transExpr);
           
        }
        
        if (a.isSymmetric()){
            
            org.omwg.logexpression.LogicalExpression h1,h2;
            h1 = leFactory.createBinary(CompoundExpression.AND, m1, m2);
            h2 = leFactory.createBinary(CompoundExpression.AND, h1, m3);

            org.omwg.logexpression.LogicalExpression symmExpr = 
                leFactory.createBinary(CompoundExpression.IMPLIES, h2, m6);
            
            result.add(symmExpr);
           
        }
        
        if (a.isReflexive()){
           
            org.omwg.logexpression.LogicalExpression reflExpr = 
                leFactory.createBinary(CompoundExpression.IMPLIES, m1, m7);
            
            result.add(reflExpr);
           
        }
        
        if (a.getInverseOf() != null) {
            
            org.omwg.logexpression.LogicalExpression h1,h2;
            Attribute inverseAtt = a.getInverseOf();
            IRI inversetAttIRI = convertIRI((IRI) a.getIdentifier());
            
            AttrSpecification attrSpec4 = 
                leFactory.createAttrSpecification(AttrSpecification.ATTR_VALUE, inversetAttIRI , toSet(x));
            org.omwg.logexpression.LogicalExpression m8 = 
                leFactory.createMolecule(y, null, null, toSet(attrSpec4));
            
            h1 = leFactory.createBinary(CompoundExpression.AND, m1, m3);
            
            org.omwg.logexpression.LogicalExpression invExpr1 = 
                leFactory.createBinary(CompoundExpression.IMPLIES, h1, m8);
            
            h2 = leFactory.createBinary(CompoundExpression.AND, m1, m8);
            org.omwg.logexpression.LogicalExpression invExpr2 = 
                leFactory.createBinary(CompoundExpression.IMPLIES, h2, m3);
            
            result.add(invExpr1);
            result.add(invExpr2);
            
        }
        
        // Handle Cardinality constraints
        
        
         if (a.getMinCardinality() > 0) {
             
            List headArgs = new LinkedList();
            headArgs.add(x);
            org.omwg.logexpression.LogicalExpression head = 
                leFactory.createAtom(leFactory.createIRI("mincard_"+ cTerm.toString() + "_" + attID),headArgs); // is new and unique within the onotlogy!
            
            Set xAttVals = new HashSet();
            Variable[] auxVars = new Variable[a.getMinCardinality()];
            for (int i = 0; i < a.getMinCardinality(); i++ ) {
                Variable nextVar = leFactory.createVariable("?y" + (i+1) );
                auxVars[i] = nextVar;
                xAttVals.add(nextVar);
            }
            AttrSpecification as = leFactory.createAttrSpecification(AttrSpecification.ATTR_VALUE, attID, xAttVals);
            org.omwg.logexpression.LogicalExpression xValsMolecule = leFactory.createMolecule(x, null, null, toSet(as));
                
            org.omwg.logexpression.LogicalExpression body = leFactory.createBinary(CompoundExpression.AND, m1, xValsMolecule);
            
            // add all inequality statements on pairs of auxilliary predicates.
            for (int i = 0; i < a.getMinCardinality(); i++ ) {
                for (int j = i + 1; j < a.getMinCardinality(); j++ ) {
                    List iesArgs = new LinkedList();
                    iesArgs.add(auxVars[i]);
                    iesArgs.add(auxVars[j]);
                    org.omwg.logexpression.LogicalExpression nextInEqStatement = 
                        leFactory.createAtom(leFactory.createIRI(Constants.INEQUAL),iesArgs);
                    
                    body = leFactory.createBinary(CompoundExpression.AND, body, nextInEqStatement);
                }
            }
            
            org.omwg.logexpression.LogicalExpression minCardExpr1 = 
                leFactory.createBinary(CompoundExpression.LP_IMPL, head, body);
            
            org.omwg.logexpression.LogicalExpression nafExp = 
                leFactory.createUnary(CompoundExpression.NAF, head);

            org.omwg.logexpression.LogicalExpression constraintbody = 
                leFactory.createBinary(CompoundExpression.AND, m1 , nafExp);
            
            org.omwg.logexpression.LogicalExpression minCardExpr2 = 
                leFactory.createUnary(CompoundExpression.CONSTRAINT, constraintbody);
            
            result.add(minCardExpr1);
            result.add(minCardExpr2);
            
         }
         
         if (a.getMaxCardinality() < Integer.MAX_VALUE) {
             // we have a max cardinality ...
       
             Set xAttVals = new HashSet();
             Variable[] auxVars = new Variable[a.getMinCardinality() + 1];
             for (int i = 0; i <= a.getMinCardinality(); i++ ) {
                 Variable nextVar = leFactory.createVariable("?y" + (i+1) );
                 auxVars[i] = nextVar;
                 xAttVals.add(nextVar);
             }
             AttrSpecification as = leFactory.createAttrSpecification(AttrSpecification.ATTR_VALUE, attID, xAttVals);
             org.omwg.logexpression.LogicalExpression xValsMolecule = leFactory.createMolecule(x, null, null, toSet(as));
                 
             org.omwg.logexpression.LogicalExpression body = leFactory.createBinary(CompoundExpression.AND, m1, xValsMolecule);
             
             // add all inequality statements on pairs of auxilliary predicates.
             for (int i = 0; i <= a.getMinCardinality(); i++ ) {
                 for (int j = i + 1; j <= a.getMinCardinality(); j++ ) {
                     List iesArgs = new LinkedList();
                     iesArgs.add(auxVars[i]);
                     iesArgs.add(auxVars[j]);
                     org.omwg.logexpression.LogicalExpression nextInEqStatement = 
                         leFactory.createAtom(leFactory.createIRI(Constants.INEQUAL),iesArgs);
                     
                     body = leFactory.createBinary(CompoundExpression.AND, body, nextInEqStatement);
                 }
             }
             
       
             org.omwg.logexpression.LogicalExpression maxCardExpr = 
                 leFactory.createUnary(CompoundExpression.CONSTRAINT, body);
             
             result.add(maxCardExpr);
             
          }
        
        return result;
    }
    
    /**
     * Inserts axioms that represent the instance in logical terms.
     * @param i - the instance to be translated to logical expressions.
     * @param o - the ontoloy to which to add the respective representation axioms
     * @throws InvalidModelException 
     * @throws SynchronisationException 
     */
    private void handleConceptInstance(Instance i, Ontology o) throws SynchronisationException, InvalidModelException{
      Set<LogicalExpression> lExprs = new HashSet<LogicalExpression>();
        
        LogicalExpression expr;
        Set memberOfs = new HashSet();
        Set attrSpecs = new HashSet();
      
        Term iTerm = (org.omwg.logexpression.terms.IRI) i.getIdentifier();
        
        Term t;
        for( Object mo : i.listConcepts() ){
            t = convertIRI((org.wsmo.common.IRI) ((Concept) mo).getIdentifier());
            memberOfs.add(t);
        }
        Map attsAndValues = i.listAttributeValues();
        
        for( Object next : attsAndValues.keySet() ){
            Attribute a = (Attribute) next;
            Set<Value> aVals = (Set<Value>) attsAndValues.get(next);
            lExprs.add(handleInstanceAttribute(a, aVals, iTerm));
        }
        
        expr = leFactory.createMolecule(iTerm, null, memberOfs, null);
        
        lExprs.add(expr);
        
        int j = 1;
        String axPrefix =  "Axiom-" + i.getIdentifier().asString(); 
        for (LogicalExpression l : lExprs) {
            Axiom ax = factory.createAxiom(factory.createIRI( axPrefix + (j++) ));
            ax.addDefinition(l);
            o.addAxiom(ax);
        }
  
    }
    
    /**
     * Inserts axioms that represent the attribute of a instance of a concept in logical terms.
     * @param a - the attribute to be translated to logical expressions.
     * @param aVals - the values that are assigned to the attribute for the given instance
     * @param iTerm - the term that represents the respective instance
     * @return a logical expression that represents the instance attribute value definition.
     * @throws InvalidModelException 
     */
    private org.omwg.logexpression.LogicalExpression handleInstanceAttribute(Attribute a, Set aVals, Term iTerm) throws InvalidModelException{
        
        org.omwg.logexpression.LogicalExpression result;
        
        // This is like it has been described from Table 8.1 in D.16.1 v0.3
        
        Term attID = convertIRI((org.wsmo.common.IRI) a.getIdentifier());
        
        Set<Term> valList = new HashSet<Term>(); 
        for (Object obj : aVals) {
            if (obj instanceof  Instance) {
                Identifier val = ((Instance) obj).getIdentifier(); 
                valList.add(convertIRI((org.wsmo.common.IRI) val));
            } else if (obj instanceof  DataValue) {
                DataValue d = (DataValue) obj;
                Value dVal = convertDataValue(d);
                valList.add(dVal);
            }
           
        }
        
        org.omwg.logexpression.AttrSpecification attSpec = 
            leFactory.createAttrSpecification(AttrSpecification.ATTR_VALUE, attID, valList);
            
        result = leFactory.createMolecule(iTerm, null, null, toSet(attSpec));
              
        return result;
    }
    
    /**
     * Inserts axioms that represent the relation in logical terms.
     * @param r - the relation to be translated to logical expressions.
     * @param o - the ontoloy to which to add the respective representation axioms
     * @throws InvalidModelException 
     * @throws SynchronisationException 
     */
    private void handleRelation(Relation r, Ontology o) throws SynchronisationException, InvalidModelException{
        
          Set<org.omwg.logexpression.LogicalExpression> lExprs = 
              new HashSet<org.omwg.logexpression.LogicalExpression>();
        
          IRI rID = convertIRI((org.wsmo.common.IRI) r.getIdentifier());
          int rArity = r.listParameters().size();
          Set<Relation> suprels = r.listSuperRelations();
          
          List<Term> predArgs = new LinkedList();
          for (int i = 0; i < rArity; i++){
              org.omwg.logexpression.terms.Variable v = 
                  leFactory.createVariable("?x" + (i+1));
              predArgs.add(v);
          }
              
          org.omwg.logexpression.LogicalExpression a1 = 
              leFactory.createAtom(rID, predArgs);
          
          for(Relation nextSuperRelation : suprels){
              IRI srID = convertIRI((org.wsmo.common.IRI) nextSuperRelation.getIdentifier());
              org.omwg.logexpression.LogicalExpression a2 = 
                  leFactory.createAtom(srID, predArgs);
              org.omwg.logexpression.LogicalExpression newExpr = 
                  leFactory.createBinary(CompoundExpression.IMPLIES, a1, a2);
              lExprs.add(newExpr);
          }
          
          // Handle parameter definitions of relation (currently missing in
          // Table 8.1 in D16.1
          
          List<Parameter> rParams = r.listParameters();
          int i = 1;
          for (Parameter p : rParams){
              lExprs.add(handleRelationParameter(p, rID, i, rArity));
              i++;
          }
          
          // Handle relation instances
          
          Set<RelationInstance> rInstances = r.listRelationInstances();
          for (RelationInstance ri : rInstances){
              lExprs.add(handleRelationInstance(ri, rID));
          }
          
          // Generate axioms in the ontology 
          i = 1;
          String axPrefix =  "Axiom-" + rID.asString(); 
          for (LogicalExpression l : lExprs) {
              Axiom ax = factory.createAxiom(factory.createIRI( axPrefix + (i++) ));
              ax.addDefinition(l);
              o.addAxiom(ax);
          }
          
    }
    
    /**
     * Inserts axioms that represent a parameter of a relation in logical terms.
     * @param rp - the parameter of a relation in the ontology 
     *             to be translated to logical expressions.
     * @param rID- the IRI of the respective relation to which the parameter belongs to.     
     * @param pos - the index of the parameter in the parameter list of the respective relation (1-based)
     * @param arity - the arity of the respective relation
     * @param o - the ontoloy to which to add the respective representation axioms
     * @throws InvalidModelException 
     */
    private org.omwg.logexpression.LogicalExpression handleRelationParameter(Parameter rp, IRI rID, int pos, int arity) throws InvalidModelException{
        org.omwg.logexpression.LogicalExpression result;
         
        Term paramVar = null;
        List<Variable> paramVars = new LinkedList();
        
        for(int j = 1 ; j <= arity; j++){
            Variable nextVar = leFactory.createVariable("?x"+j);
            paramVars.add(nextVar);
            if (j == pos){ paramVar = nextVar; }
        }
        
        org.omwg.logexpression.LogicalExpression body = 
            leFactory.createAtom(rID, paramVars);
        
        
        Set<Type> rangeTypes = rp.listTypes();
        Set<Term> rangeTermList = new HashSet<Term>(); 
        for (Object type : rangeTypes) {
            if (type instanceof Concept){
                org.omwg.logexpression.terms.IRI  tIRI = 
                    convertIRI((org.wsmo.common.IRI)((Concept) type).getIdentifier()); 
                rangeTermList.add(tIRI);
            } else if (type instanceof SimpleDataType){
                org.omwg.logexpression.terms.IRI  tIRI = 
                    convertIRI(((SimpleDataType) type).getIRI()); 
                rangeTermList.add(tIRI);
            } else if (type instanceof ComplexDataType){
                throw new IllegalArgumentException("Complex datatype are currently not supported in Normalization to LogicalExpressions");
                // TODO include complex data types as well.
            }
        }
        
        org.omwg.logexpression.LogicalExpression head = 
            leFactory.createMolecule(paramVar, null, rangeTermList, null);
        
        if (!rp.isConstraining()){
            // impliesType
            result = leFactory.createBinary(CompoundExpression.IMPLIES, body, head);
        } else {
            // ofType
            org.omwg.logexpression.LogicalExpression naf = 
                leFactory.createUnary(CompoundExpression.NAF, head);
            org.omwg.logexpression.LogicalExpression cBody = 
                leFactory.createBinary(CompoundExpression.AND, body, naf);
            result = leFactory.createUnary(CompoundExpression.CONSTRAINT, cBody);
        }
        
        return result;
    }
    
    
    /**
     * Inserts axioms that represent the instance of a relation in logical terms.
     * @param ri - the instance of a relation in the ontology 
     *             to be translated to logical expressions.
     * @param rID - the IRI of the relation to which this instance belongs to
     */
    private org.omwg.logexpression.LogicalExpression handleRelationInstance(RelationInstance ri, IRI rID){
          
        List<Value> parVals = ri.listParameterValues();
        List<Term> args = new LinkedList();
        
        for(Value v : parVals){
            if (v instanceof  Instance) {
                Identifier val = ((Instance) v).getIdentifier(); 
                args.add(convertIRI((org.wsmo.common.IRI) val));
            } else if (v instanceof  DataValue) {
                DataValue d = (DataValue) v;
                Value dVal = convertDataValue(d);
                args.add(dVal);
            }
        }
        
        return leFactory.createAtom(rID, args);
        
    }
    
    
    // Some helper methods for convenience 
    
    /**
     * Converts a given IRI from WSMO4j to a IRI that can be used
     * in logical expressions.
     * 
     * @param iri
     * @return
     */
    private IRI convertIRI(org.wsmo.common.IRI iri){
        return leFactory.createIRI(iri.asString());
    }
    
    private Set toSet(Object o){
        Set s = new HashSet();
        s.add(o);
        return s;
    }
    
    /**
     * Converts a datavalue object that one finds in the conceptual syntax
     * to a value object that can be used to construct terms and formulae.
     * 
     * @param d - the data value in the conceptual syntax part that needs to be converted.
     * @return a org.omwg.logexpression.terms.Value object that represents the same datavalue.
     */
    private Value convertDataValue(DataValue d){
        Value result = null;
        if (d.getType() instanceof SimpleDataType) {
            if (d.getType() instanceof WsmlInteger){
                java.math.BigInteger bigint = new java.math.BigInteger(d.asString()); 
                result = leFactory.createWSMLInteger(bigint);
            } else if (d.getType() instanceof WsmlString){
                result = leFactory.createWSMLString(d.asString());
            } else if (d.getType() instanceof WsmlDecimal){
                java.math.BigDecimal bigdec = new java.math.BigDecimal(d.asString());
                result = leFactory.createWSMLDecimal(bigdec);
            }
        } else {
            // d instanceof ComplexDataType
            throw new IllegalArgumentException("Complex datatype values are at present not supported by the WSMO4j API!");
        } 
        
        return result;
    }
   
    
    
    
}
