/**
 * WSML Reasoner Implementation.
 *
 * Copyright (c) 2005, FZI, Austria.
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
package org.wsml.reasoner;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;


import org.omwg.logicalexpression.*;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.*;
import org.semanticweb.owl.model.*;
import org.semanticweb.owl.model.change.*;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsmo.common.Identifier;

/**
 * A class for transforming WSML-DL to OWL DL.
 *
 * <pre>
 *  Created on July 3rd, 2006
 *  Committed by $Author: nathalie $
 *  $Source: /home/richi/temp/w2r/wsml2reasoner/src/org/wsml/reasoner/WSMLDL2OWLTransformer.java,v $,
 * </pre>
 *
 * @author Nathalie Steinmetz, DERI Innsbruck
 * @version $Revision: 1.4 $ $Date: 2006-07-21 16:25:21 $
 */
public class WSMLDL2OWLTransformer implements Visitor{
	
	private OWLOntology owlOntology = null;
	
	private OWLDataFactory owlDataFactory = null;
	
	private ChangeVisitor changeVisitor = null;
	
	private OntologyChange ontologyChange = null;
	
	private String SUBATTRIBUTE = "subAttribute";
	
	private String INVERSE = "inverse";
	
	private String SYMMETRIC = "symmetric";
	
	private String DOMAIN = "domain";
	
	private String RANGE = "range";
	
	private OWLGenerator owlGenerator = new OWLGenerator();
	
	private TransformationHelper helper = new TransformationHelper();
	
    public WSMLDL2OWLTransformer(OWLOntology owlOntology, OWLDataFactory 
    		owlDataFactory, ChangeVisitor changeVisitor) {   
    	this.owlOntology = owlOntology;
    	this.owlDataFactory = owlDataFactory;
    	this.changeVisitor = changeVisitor;
    }
    
    /**
     * Method to transform a WSML-DL ontology to an OWL ontology.
     * 
     * @param ontology WSML-DL ontology to be transformed
     * @return OWL ontology
     * @throws URISyntaxException
     * @throws OWLException
     */
    @SuppressWarnings("unchecked")
	public OWLOntology transform(Ontology ontology) 
			throws OWLException {

    	// Collect all logical expressions from all axioms into one Set
        Set<LogicalExpression> logExprs = new LinkedHashSet
        		<LogicalExpression>();
        for (Object axiom : ontology.listAxioms()) {
            logExprs.addAll(((Axiom) axiom).listDefinitions());
        }
        
        // Process all logical expressions
    	Iterator<LogicalExpression> it = logExprs.iterator();
    	while (it.hasNext()) {
    		LogicalExpression logExpr = it.next();
//System.out.println(logExpr.getClass().toString() + "\n" + logExpr.toString());
    		logExpr.accept(this);
    	}
    	return owlOntology;
    }
    
    public OWLDescription transform(LogicalExpression logExpr) 
    		throws OWLException {
    	OWLDescription owlDescription = null;
    	// create a description from the logical expression
		if (logExpr instanceof MembershipMolecule) {
			owlDescription = helper.transformMemberShipMolecule(
					(MembershipMolecule) logExpr);
		}
		else if (logExpr instanceof Conjunction) {
			owlDescription = helper.transformConjunction((Conjunction) logExpr);
		}
		else if (logExpr instanceof Disjunction) {
			owlDescription = helper.transformDisjunction((Disjunction) logExpr);
		}
		else if (logExpr instanceof Negation) {
			owlDescription = helper.transformNegation((Negation) logExpr);
		}
		else if (logExpr instanceof ExistentialQuantification) {
			owlDescription = helper.transformExistentialQuantification(
					(ExistentialQuantification) logExpr);
		}
		else if (logExpr instanceof UniversalQuantification) {
			owlDescription = helper.transformUniversalQuantification(
					(UniversalQuantification) logExpr);
		}
    	return owlDescription;
    }

	public void visitAtom(Atom expr) {
		Molecule molecule = helper.atomToMolecule(expr);
		molecule.accept(this);
	}

	@SuppressWarnings("unchecked")
	public void visitCompoundMolecule(CompoundMolecule expr) {
		Iterator<Molecule> it = expr.listOperands().iterator();
        while(it.hasNext()){
            it.next().accept(this);
        }
	}

	public void visitSubConceptMolecule(SubConceptMolecule expr) {
		Identifier subConceptId = (Identifier) expr.getLeftParameter();
		Identifier superConceptId = (Identifier) expr.getRightParameter();
		OWLClass subClass = owlGenerator.createClass(subConceptId.
				toString());
		OWLClass superClass = owlGenerator.createClass(superConceptId.
				toString());
		owlGenerator.createSubClass(subClass, (OWLDescription) superClass);
	}

	public void visitMemberShipMolecule(MembershipMolecule expr) {
		OWLIndividual individual = null;
		OWLClass clazz = null;
		if (expr.getLeftParameter() instanceof Identifier) {
			Identifier instanceId = (Identifier) expr.getLeftParameter();
			individual = owlGenerator.createIndividual(instanceId.
					toString());
		}
		if (expr.getRightParameter() instanceof Identifier) {
			Identifier conceptId = (Identifier) expr.getRightParameter();
			clazz = owlGenerator.createClass(conceptId.toString());
		}
		if (individual != null && clazz != null) {
			owlGenerator.createMemberOf(individual, clazz);
		}
	}

	public void visitAttributeValueMolecule(AttributeValueMolecule expr) {
		OWLIndividual individual = null;
		if (expr.getLeftParameter() instanceof Identifier) {
			Identifier instanceId = (Identifier) expr.getLeftParameter();
			individual = owlGenerator.createIndividual(instanceId.toString());
		}
		Term value = expr.getRightParameter();
		Identifier attrId = (Identifier) expr.getAttribute();
		if (value instanceof DataValue) {
			OWLDataValue dataValue = owlGenerator.createDataValue(
					(DataValue) value);
			OWLDataProperty dataProperty = owlGenerator.createDataProperty(
					attrId.toString());
			if (individual != null) {
				owlGenerator.createIndividualValue(dataProperty, individual, 
						dataValue);
			}
		}
		else {
			OWLIndividual individual2 = owlGenerator.createIndividual(
					value.toString());
			OWLObjectProperty objectProperty = owlGenerator.createObjectProperty(
					attrId.toString());
			if (individual != null) {
				owlGenerator.createIndividualValue(objectProperty, individual, 
						individual2);
			}
		}
	}

	public void visitAttributeContraintMolecule(AttributeConstraintMolecule 
			expr) {
		Identifier conceptId = (Identifier) expr.getLeftParameter();
		Identifier attrId = (Identifier) expr.getAttribute();
		Term data = expr.getRightParameter();
		OWLClass domain = owlGenerator.createClass(conceptId.toString());
		OWLDataType dataType = owlGenerator.createDataType(data.toString());
		OWLDataProperty dataProperty = owlGenerator.createDataProperty(
				attrId.toString());
		owlGenerator.createDataPropertyRange(dataType, dataProperty);
		owlGenerator.createPropertyDomain(domain, dataProperty);
	}

	public void visitAttributeInferenceMolecule(AttributeInferenceMolecule expr) {
		Identifier domainId = (Identifier) expr.getLeftParameter();
		Identifier attrId = (Identifier) expr.getAttribute();
		Identifier rangeId = (Identifier) expr.getRightParameter();
		OWLClass domain = owlGenerator.createClass(domainId.toString());
		OWLClass range = owlGenerator.createClass(rangeId.toString());
		OWLObjectProperty objectProperty = owlGenerator.createObjectProperty(
				attrId.toString());
		owlGenerator.createObjectPropertyRange(range, objectProperty);
		owlGenerator.createPropertyDomain(domain, objectProperty);
	}

	public void visitNegation(Negation expr) {
		throw new RuntimeException("Negations are not allowed in " +
				"WSML-DL logical expressions.");
	}

	public void visitNegationAsFailure(NegationAsFailure expr) {
		throw new RuntimeException("NegationAsFailures are not allowed in " +
				"WSML-DL logical expressions.");
	}

	public void visitConstraint(Constraint expr) {
		throw new RuntimeException("Constraints are not allowed in " +
				"WSML-DL logical expressions.");
	}

	public void visitConjunction(Conjunction expr) {
//System.out.println(expr.toString());
		expr.getLeftOperand().accept(this);
		expr.getRightOperand().accept(this);	
	}

	public void visitDisjunction(Disjunction expr) {
		throw new RuntimeException("Disjunctions are not allowed in " +
				"WSML-DL logical expressions.");
	}

	public void visitInverseImplication(InverseImplication expr) {
//System.out.println(expr.toString());
		// check on transitive attribute structure
		String relation = helper.isTransitiveInvImpl(expr);
		if (relation != null) {
			owlGenerator.createTransitiveProperty(relation);
			return;
		}
		
        // check on symmetric, sub-attribute and inverse attribute structure
		List<String> list = helper.isOneOfInvImpl(expr);
		if (list != null) {
			String property = list.get(0);
			String leftRelation = list.get(1);
			String rightRelation = list.get(2);
	        if(property.equals(SUBATTRIBUTE)) {
	        	owlGenerator.createSubProperty(leftRelation, rightRelation);
	        	return;
	        }
	        if(property.equals(SYMMETRIC)) {
	        	owlGenerator.createSymmetricProperty(leftRelation);
	        	return;
	        }
	        if(property.equals(INVERSE)) {
	        	owlGenerator.createInverseProperty(leftRelation, rightRelation);
	        	return;
	        }
		}
		
		// check for subConcept attribute structure
		list = helper.isSubConceptInvImpl(expr);
		if (list != null) {
			String subConceptId = list.get(0);
			String superConceptId = list.get(1);
			OWLClass subClass = owlGenerator.createClass(subConceptId.
					toString());
			OWLClass superClass = owlGenerator.createClass(superConceptId.
					toString());
			owlGenerator.createSubClass(subClass, (OWLDescription) superClass);
			return;
		}
		
		// check for property domain or range definition
		list = helper.isPropertyInvImpl(expr);
		if (list != null) {
			String property = list.get(0);
			String attributeId = list.get(1);
			String conceptId = list.get(2);
			OWLObjectProperty objectProperty = owlGenerator.createObjectProperty(
					attributeId);
			OWLClass clazz = owlGenerator.createClass(conceptId);
			if (property.equals(DOMAIN)) {
				owlGenerator.createPropertyDomain(clazz, objectProperty);
				return;
			}
			if (property.equals(RANGE)) {
				owlGenerator.createObjectPropertyRange(clazz, objectProperty);
				return;
			}
		}
		
		// if the left expression is a conjunction, the expression can be splitted in 2
		if (expr.getLeftOperand() instanceof Conjunction) {
			InverseImplication inv = new WSMO4JManager().getLogicalExpressionFactory().
					createInverseImplication(((Conjunction) expr.getLeftOperand()).
					getLeftOperand(), expr.getRightOperand());
			inv.accept(this);
			inv = new WSMO4JManager().getLogicalExpressionFactory().
					createInverseImplication(((Conjunction) expr.getLeftOperand()).
							getRightOperand(), expr.getRightOperand());
			inv.accept(this);
			return;
		}
		
		// if the right expression is a disunction, the expression can be splitted in 2
		if (expr.getRightOperand() instanceof Disjunction) {
			InverseImplication inv = new WSMO4JManager().getLogicalExpressionFactory().
					createInverseImplication(expr.getLeftOperand(), ((Disjunction) 
							expr.getRightOperand()).getLeftOperand());
			inv.accept(this);
			inv = new WSMO4JManager().getLogicalExpressionFactory().
					createInverseImplication(expr.getLeftOperand(), ((Disjunction) 
							expr.getRightOperand()).getRightOperand());
			inv.accept(this);
			return;
		}
		
		// check for classes resulting from nested expressions and build subClass relation 
		// of them
		List<OWLDescription> classList = helper.buildInverseImplications(expr);
		OWLDescription subClass = classList.get(0);
		OWLDescription superClass = classList.get(1);
		if (subClass != null && superClass != null) {
			owlGenerator.createExplicitSubClass(subClass, superClass);
		}
	}

	public void visitImplication(Implication expr) {
		throw new RuntimeException("Implications should be eliminated " +
				"during the WSML-DL normalization process.");
	}

	public void visitEquivalence(Equivalence expr) {
		throw new RuntimeException("Equivalences should be eliminated " +
				"during the WSML-DL normalization process.");	
	}

	public void visitLogicProgrammingRule(LogicProgrammingRule expr) {
		throw new RuntimeException("LogicProgrammingRules are not allowed in " +
				"WSML-DL logical expressions.");
	}

	public void visitUniversalQuantification(UniversalQuantification expr) {
		throw new RuntimeException("UniversalQuantifications are not allowed in " +
				"WSML-DL logical expressions.");
	}

	public void visitExistentialQuantification(ExistentialQuantification expr) {
		throw new RuntimeException("ExistentialQuantifications are not allowed in " +
				"WSML-DL logical expressions.");
	}
	
    private class TransformationHelper {
    	
    	private List<OWLDescription> buildInverseImplications(InverseImplication expr) {
//System.out.println("Build inv impl: " + expr + "\n");
    		List<OWLDescription> list = new ArrayList<OWLDescription>(2);
    		LogicalExpression left = expr.getLeftOperand();
    		LogicalExpression right = expr.getRightOperand();
    		OWLDescription subClass = null;
    		OWLDescription superClass = null;
    		
    		// create a class from the left side of the inverse implication
    		if (left instanceof MembershipMolecule) {
    			superClass = transformMemberShipMolecule(
    					(MembershipMolecule) left);
    		}
//    		else if (left instanceof AttributeValueMolecule) {
//    			superClass = transformAttributeValueMolecule(
//    					(AttributeValueMolecule) left);
//    		}
    		else if (left instanceof Conjunction) {
    			superClass = transformConjunction((Conjunction) left);
    		}
    		else if (left instanceof Disjunction) {
    			superClass = transformDisjunction((Disjunction) left);
    		}
    		else if (left instanceof Negation) {
    			superClass = transformNegation((Negation) left);
    		}
    		else if (left instanceof ExistentialQuantification) {
    			superClass = transformExistentialQuantification(
    					(ExistentialQuantification) left);
    		}
    		else if (left instanceof UniversalQuantification) {
    			superClass = transformUniversalQuantification(
    					(UniversalQuantification) left);
    		}
    		
    		// create a class from the right side of the inverse implication
    		if (right instanceof MembershipMolecule) {
    			subClass = transformMemberShipMolecule(
    					(MembershipMolecule) right);
    		}
//    		else if (right instanceof AttributeValueMolecule) {
//    			subClass = transformAttributeValueMolecule(
//    					(AttributeValueMolecule) right);
//    		}
    		else if (right instanceof Conjunction) {
    			subClass = transformConjunction((Conjunction) right);
    		}
    		else if (right instanceof Disjunction) {
    			subClass = transformDisjunction((Disjunction) right);
    		}
    		else if (right instanceof Negation) {
    			subClass = transformNegation((Negation) right);
    		}
    		else if (right instanceof ExistentialQuantification) {
    			subClass = transformExistentialQuantification(
    					(ExistentialQuantification) right);
    		}
    		else if (right instanceof UniversalQuantification) {
    			subClass = transformUniversalQuantification(
    					(UniversalQuantification) right);
    		}
    		
    		// fill list with resulting classes
    		list.add(0, subClass);
    		list.add(1, superClass);
    		return list;
    	}
        
    	private OWLClass transformMemberShipMolecule(MembershipMolecule expr) {
//    		System.out.println("MembershipMolecule: " + expr.toString() + "\n");
    		OWLIndividual individual = null;
    		OWLClass clazz = null;
    		if (expr.getLeftParameter() instanceof Identifier) {
    			Identifier instanceId = (Identifier) expr.getLeftParameter();
    			individual = owlGenerator.createIndividual(instanceId.toString());
    		}
    		if (expr.getRightParameter() instanceof Identifier) {
    			Identifier conceptId = (Identifier) expr.getRightParameter();
    			clazz = owlGenerator.createClass(conceptId.toString());
    		}
    		if (individual != null && clazz != null) {
    			owlGenerator.createMemberOf(individual, clazz);
    		}
    		return clazz;
    	}
    	
//    	private OWLProperty transformAttributeValueMolecule(
//    			AttributeValueMolecule expr) {
////    		System.out.println("AttributeValueMolecule: " + expr.toString() + "\n");
//    		OWLIndividual individual = null;
//    		OWLProperty property = null;
//    		if (expr.getLeftParameter() instanceof Identifier) {
//    			Identifier instanceId = (Identifier) expr.getLeftParameter();
//    			individual = owlGenerator.createIndividual(instanceId.toString());
//    		}
//    		Term value = expr.getRightParameter();
//    		Identifier attrId = (Identifier) expr.getAttribute();
// 		
//    		if (value instanceof DataValue) {
//    			OWLDataValue dataValue = owlGenerator.createDataValue(
//    					(DataValue) value);
//    			property = owlGenerator.createDataProperty(attrId.toString());
//    			if (individual != null) {
//	    			owlGenerator.createIndividualValue((OWLDataProperty) property, 
//	    					individual, dataValue);
//    			}
//    		}
//    		else {
//    			OWLIndividual individual2 = null;
//    			if (value instanceof Identifier) {
//	    			individual2 = owlGenerator.createIndividual(
//	    					value.toString());
//    			}
//    			property = owlGenerator.createObjectProperty(attrId.toString());
//    			if (individual != null && individual2 != null) {
//	    			owlGenerator.createIndividualValue((OWLObjectProperty) property, 
//	    					individual, individual2);
//    			}
//    		}
//    		return property;
//    	}
    	
    	private OWLDescription transformConjunction(Conjunction expr) {
//System.out.println("Conjunction: " + expr.toString() + "\n");
    		LogicalExpression left = expr.getLeftOperand();
    		LogicalExpression right = expr.getRightOperand();
    		OWLDescription clazz1 = null;
    		OWLDescription clazz2 = null;
//    		OWLProperty property1 = null;
//    		OWLProperty property2 = null;
    		
    		// create a class from the left side of the conjunction
    		if (left instanceof MembershipMolecule) {
    			clazz1 = transformMemberShipMolecule(
    					(MembershipMolecule) left);
    		}
//    		else if (left instanceof AttributeValueMolecule) {
//    			property1 = transformAttributeValueMolecule(
//    					(AttributeValueMolecule) left);
//    		}
    		else if (left instanceof Conjunction) {
    			clazz1 = transformConjunction((Conjunction) left);
    		}
    		else if (left instanceof Disjunction) {
    			clazz1 = transformDisjunction((Disjunction) left);
    		}
    		else if (left instanceof Negation) {
    			clazz1 = transformNegation((Negation) left);
    		}
    		else if (left instanceof ExistentialQuantification) {
    			clazz1 = transformExistentialQuantification(
    					(ExistentialQuantification) left);
    		}
    		else if (left instanceof UniversalQuantification) {
    			clazz1 = transformUniversalQuantification(
    					(UniversalQuantification) left);
    		}
    		
    		// create a class from the right side of the conjunction
    		if (right instanceof MembershipMolecule) {
    			clazz2 = transformMemberShipMolecule(
    					(MembershipMolecule) right);
    		}
//    		else if (right instanceof AttributeValueMolecule) {
//    			property2 = transformAttributeValueMolecule(
//    					(AttributeValueMolecule) right);
//    		}
    		else if (right instanceof Conjunction) {
    			clazz2 = transformConjunction((Conjunction) right);
    		}
    		else if (right instanceof Disjunction) {
    			clazz2 = transformDisjunction((Disjunction) right);
    		}
    		else if (right instanceof Negation) {
    			clazz2 = transformNegation((Negation) right);
    		}
    		else if (right instanceof ExistentialQuantification) {
    			clazz2 = transformExistentialQuantification(
    					(ExistentialQuantification) right);
    		}
    		else if (right instanceof UniversalQuantification) {
    			clazz2 = transformUniversalQuantification(
    					(UniversalQuantification) right);
    		}
//    		if (property1 != null && clazz2 != null) {
//    			return owlGenerator.createSomeValuesFromClass((OWLObjectProperty) 
//    					property1, clazz2);
//    		}
//    		if (property2 != null && clazz1 != null) {
//    			return owlGenerator.createSomeValuesFromClass((OWLObjectProperty) 
//    					property2, clazz1);
//    		}
    		HashSet<OWLDescription> set = new HashSet<OWLDescription>();
    		set.add(clazz1);
    		set.add(clazz2);
    		return owlGenerator.createIntersectionClass(set);
    	}
    	
    	private OWLDescription transformDisjunction(Disjunction expr) {
//    		System.out.println("Disjunction: " + expr.toString() + "\n");
    		LogicalExpression left = expr.getLeftOperand();
    		LogicalExpression right = expr.getRightOperand();
    		OWLDescription clazz1 = null;
    		OWLDescription clazz2 = null;
//    		OWLProperty property1 = null;
//    		OWLProperty property2 = null;
    		
    		// create a class from the left side of the conjunction
    		if (left instanceof MembershipMolecule) {
    			clazz1 = transformMemberShipMolecule(
    					(MembershipMolecule) left);
    		}
//    		else if (left instanceof AttributeValueMolecule) {
//    			property1 = transformAttributeValueMolecule(
//    					(AttributeValueMolecule) left);
//    		}
    		else if (left instanceof Conjunction) {
    			clazz1 = transformConjunction((Conjunction) left);
    		}
    		else if (left instanceof Disjunction) {
    			clazz1 = transformDisjunction((Disjunction) left);
    		}
    		else if (left instanceof Negation) {
    			clazz1 = transformNegation((Negation) left);
    		}
    		else if (left instanceof ExistentialQuantification) {
    			clazz1 = transformExistentialQuantification(
    					(ExistentialQuantification) left);
    		}
    		else if (left instanceof UniversalQuantification) {
    			clazz1 = transformUniversalQuantification(
    					(UniversalQuantification) left);
    		}
    		
    		// create a class from the right side of the conjunction
    		if (right instanceof MembershipMolecule) {
    			clazz2 = transformMemberShipMolecule(
    					(MembershipMolecule) right);
    		}
//    		else if (right instanceof AttributeValueMolecule) {
//    			property2 = transformAttributeValueMolecule(
//    					(AttributeValueMolecule) right);
//    		}
    		else if (right instanceof Conjunction) {
    			clazz2 = transformConjunction((Conjunction) right);
    		}
    		else if (right instanceof Disjunction) {
    			clazz2 = transformDisjunction((Disjunction) right);
    		}
    		else if (right instanceof Negation) {
    			clazz2 = transformNegation((Negation) right);
    		}
    		else if (right instanceof ExistentialQuantification) {
    			clazz2 = transformExistentialQuantification(
    					(ExistentialQuantification) right);
    		}
    		else if (right instanceof UniversalQuantification) {
    			clazz2 = transformUniversalQuantification(
    					(UniversalQuantification) right);
    		}
//    		if (property1 != null && clazz2 != null) {
//    			return owlGenerator.createSomeValuesFromClass((OWLObjectProperty) 
//    					property1, clazz2);
//    		}
//    		if (property2 != null && clazz1 != null) {
//    			return owlGenerator.createSomeValuesFromClass((OWLObjectProperty) 
//    					property2, clazz1);
//    		}
    		HashSet<OWLDescription> set = new HashSet<OWLDescription>();
    		set.add(clazz1);
    		set.add(clazz2);
    		return owlGenerator.createUnionClass(set);
    	}
    	
    	private OWLDescription transformNegation(Negation expr) {
//    		System.out.println("Negation: " + expr.toString() + "\n");
    		OWLDescription clazz = null;
//    		OWLProperty property = null;
    		
    		// create a class from negation's operand
    		if (expr.getOperand() instanceof MembershipMolecule) {
    			clazz = transformMemberShipMolecule(
    					(MembershipMolecule) expr.getOperand());
    		}
//    		else if (expr.getOperand() instanceof AttributeValueMolecule) {
//    			property = transformAttributeValueMolecule(
//    					(AttributeValueMolecule) expr.getOperand());
//    		}
    		else if (expr.getOperand() instanceof Conjunction) {
    			clazz = transformConjunction((Conjunction) expr.getOperand());
    		}
    		else if (expr.getOperand() instanceof Disjunction) {
    			clazz = transformDisjunction((Disjunction) expr.getOperand());
    		}
    		else if (expr.getOperand() instanceof Negation) {
    			clazz = transformNegation((Negation) expr.getOperand());
    		}
    		else if (expr.getOperand() instanceof ExistentialQuantification) {
    			clazz = transformExistentialQuantification(
    					(ExistentialQuantification) expr.getOperand());
    		}
    		else if (expr.getOperand() instanceof UniversalQuantification) {
    			clazz = transformUniversalQuantification(
    					(UniversalQuantification) expr.getOperand());
    		}
    		return owlGenerator.createComplementClass(clazz);
    	}

    	private OWLDescription transformExistentialQuantification(
    			ExistentialQuantification expr) {
//System.out.println("ExistentialQuantification: " + expr.toString() + "\n");
    		OWLDescription filler = null;
//    		OWLProperty fillerProperty = null;
    		OWLObjectProperty property = null;

    		Conjunction conjunction = (Conjunction) expr.getOperand();
    		// get left part of conjunction
    		AttributeValueMolecule attribute = (AttributeValueMolecule) 
    				conjunction.getLeftOperand();
    		property = owlGenerator.createObjectProperty(attribute.getAttribute()
    				.toString());
    			
    		// get left part of inverse implication
    		if (conjunction.getRightOperand() instanceof MembershipMolecule) {
    			filler = transformMemberShipMolecule(
    					(MembershipMolecule) conjunction.getRightOperand());
    		}
//    		else if (invImpl.getLeftOperand() instanceof AttributeValueMolecule) {
//    			fillerProperty = transformAttributeValueMolecule(
//    					(AttributeValueMolecule) expr.getOperand());
//    		}
    		else if (conjunction.getRightOperand() instanceof Conjunction) {
    			filler = transformConjunction((Conjunction) conjunction.
    					getRightOperand());
    		}
    		else if (conjunction.getRightOperand() instanceof Disjunction) {
    			filler = transformDisjunction((Disjunction) conjunction.
    					getRightOperand());
    		}
    		else if (conjunction.getRightOperand() instanceof Negation) {
    			filler = transformNegation((Negation) conjunction.
    					getRightOperand());
    		}
    		else if (conjunction.getRightOperand() instanceof 
    				ExistentialQuantification) {
    			filler = transformExistentialQuantification(
    					(ExistentialQuantification) conjunction.getRightOperand());
    		}
    		else if (conjunction.getRightOperand() instanceof 
    				UniversalQuantification) {
    			filler = transformUniversalQuantification(
    					(UniversalQuantification) conjunction.getRightOperand());
    		}
			return owlGenerator.createSomeValuesFromClass(property, 
    				filler);
    	}
    	
    	private OWLDescription transformUniversalQuantification(
    			UniversalQuantification expr) {
//System.out.println("Universal: " + expr.toString() + "\n");
    		OWLDescription filler = null;
//    		OWLProperty fillerProperty = null;
    		OWLObjectProperty property = null;

    		InverseImplication invImpl = (InverseImplication) expr.getOperand();
    			
    		// get right part of inverse implication
    		AttributeValueMolecule attribute = (AttributeValueMolecule) 
    				invImpl.getRightOperand();
    		property = owlGenerator.createObjectProperty(attribute.getAttribute()
    				.toString());
    			
    		// get left part of inverse implication
    		if (invImpl.getLeftOperand() instanceof MembershipMolecule) {
    			filler = transformMemberShipMolecule(
    					(MembershipMolecule) invImpl.getLeftOperand());
    		}
//    		else if (invImpl.getLeftOperand() instanceof AttributeValueMolecule) {
//    			fillerProperty = transformAttributeValueMolecule(
//    					(AttributeValueMolecule) expr.getOperand());
//    		}
    		else if (invImpl.getLeftOperand() instanceof Conjunction) {
    			filler = transformConjunction((Conjunction) invImpl.
    					getLeftOperand());
    		}
    		else if (invImpl.getLeftOperand() instanceof Disjunction) {
    			filler = transformDisjunction((Disjunction) invImpl.
    					getLeftOperand());
    		}
    		else if (invImpl.getLeftOperand() instanceof Negation) {
    			filler = transformNegation((Negation) invImpl.
    					getLeftOperand());
    		}
    		else if (invImpl.getLeftOperand() instanceof 
    				ExistentialQuantification) {
    			filler = transformExistentialQuantification(
    					(ExistentialQuantification) invImpl.getLeftOperand());
    		}
    		else if (invImpl.getLeftOperand() instanceof 
    				UniversalQuantification) {
    			filler = transformUniversalQuantification(
    					(UniversalQuantification) invImpl.getLeftOperand());
    		}
			return owlGenerator.createAllValuesFromClass(property, 
    				filler);
    	}
    	
    	/*
         * Checks if a specified InverseImplication, containing a molecule and a 
         * conjunction as operands, is a valid transitive construction
         */
    	private String isTransitiveInvImpl(InverseImplication expr) {
        	LogicalExpression leftLE = expr.getLeftOperand();
    		LogicalExpression rightLE = expr.getRightOperand();
        	AttributeValueMolecule left, conjunctionLeft, 
        			conjunctionRight = null;
            if (((leftLE instanceof Atom) || (leftLE instanceof 
            		AttributeValueMolecule)) && (rightLE instanceof Conjunction)) {
                LogicalExpression leftConjunction = ((Conjunction) rightLE).
                		getLeftOperand();
                LogicalExpression rightConjunction = ((Conjunction) rightLE).
                		getRightOperand();
                if (((leftConjunction instanceof Atom) 
                        || (leftConjunction instanceof AttributeValueMolecule)) 
                        && ((rightConjunction instanceof Atom) 
                        || (rightConjunction instanceof AttributeValueMolecule))) {
                	// Atoms can be used interchangeably with attribute value molecules
                    if (expr.getLeftOperand() instanceof Atom) {
                        left = (AttributeValueMolecule) atomToMolecule((Atom) 
                        		expr.getLeftOperand());
                    }
                    else {
                        left = (AttributeValueMolecule) expr.getLeftOperand();
                    }
                    if (((Conjunction) expr.getRightOperand()).getLeftOperand() 
                    		instanceof Atom) {
                        conjunctionLeft = (AttributeValueMolecule) atomToMolecule((Atom) 
                                ((Conjunction) expr.getRightOperand()).getLeftOperand());
                    }
                    else {
                        conjunctionLeft = (AttributeValueMolecule) (
                                (Conjunction) expr.getRightOperand()).getLeftOperand();
                    }
                    if (((Conjunction) expr.getRightOperand()).getRightOperand() 
                    		instanceof Atom) {
                        conjunctionRight = (AttributeValueMolecule) atomToMolecule(
                        		(Atom) ((Conjunction) expr.getRightOperand()).
                        		getRightOperand());
                    }
                    else {
                        conjunctionRight = (AttributeValueMolecule) ((Conjunction) 
                        		expr.getRightOperand()).getRightOperand();
                    } 
                          
                    Variable leftVariable = (Variable) left.getLeftParameter();
                    Variable conjunctionLeftVariable = (Variable) conjunctionLeft.
                    	getLeftParameter();
                    Variable conjunctionRightVariable = (Variable) conjunctionRight.
                    	getLeftParameter();
                    Term leftRelation = left.getAttribute();
                    Term conjunctionLeftRelation = conjunctionLeft.getAttribute();
                    Term conjunctionRightRelation = conjunctionRight.getAttribute();     
                    Variable leftArgument = (Variable) left.getRightParameter();
                    Variable conjunctionLeftArgument = (Variable) conjunctionLeft.
                    	getRightParameter();
                    Variable conjunctionRightArgument = (Variable) conjunctionRight.
                    	getRightParameter();
                    
                    // check on globally transitive attribute/relation
                    if (leftVariable.equals(conjunctionLeftVariable) 
                            && leftVariable.equals(conjunctionRightVariable)) {
                        return null;
                    }
                    else if (!(leftRelation.equals(conjunctionLeftRelation)) 
                            || !(leftRelation.equals(conjunctionRightRelation))) {
                    	 return null;
                    }
                    else if (leftVariable.equals(conjunctionLeftVariable)) {
                        if (!(leftArgument.equals(conjunctionRightArgument)) 
                                || !(conjunctionLeftArgument.equals(
                                		conjunctionRightVariable))) {
                        	 return null;
                        }
                    }
                    else if (leftVariable.equals(conjunctionRightVariable)) {
                        if (!(leftArgument.equals(conjunctionLeftArgument)) 
                                || !(conjunctionRightArgument.equals(
                                		conjunctionLeftVariable))) {
                        	 return null;
                        }
                    }
                    return leftRelation.toString();
                }
            }
            return null;
        }
        
        /*
         * Checks if an Inverse Implication, containing two molecules as Operands, 
         * is a symmetric, sub-attribute or inverse attribute structure.
         * The method returns a set, containing either a String that defines which 
         * sort of inverse implication the expression is, and the attribute 
         * identifiers, or null.
         */
    	private List<String> isOneOfInvImpl(InverseImplication expr) {
        	List<String> list = new ArrayList<String>(3);
        	LogicalExpression leftLE = expr.getLeftOperand();
    		LogicalExpression rightLE = expr.getRightOperand();
            AttributeValueMolecule left, right = null;       
            if (((leftLE instanceof Atom) || (leftLE instanceof 
            		AttributeValueMolecule)) && ((rightLE instanceof Atom) || 
            				(rightLE instanceof AttributeValueMolecule))) {
            	// Atoms can be used interchangeably with attribute value molecules
                if (expr.getLeftOperand() instanceof Atom) {
                    left = (AttributeValueMolecule) atomToMolecule((Atom) 
                    		expr.getLeftOperand());
                }
                else {
                    left = (AttributeValueMolecule) expr.getLeftOperand();
                }
                if (expr.getRightOperand() instanceof Atom) {
                    right = (AttributeValueMolecule) atomToMolecule((Atom) 
                    		expr.getRightOperand());
                }
                else {
                    right = (AttributeValueMolecule) expr.getRightOperand();
                }
                if (left == null || right == null) {
                    return null;
                }
                else {
                    Variable leftVariable = (Variable) left.getLeftParameter();
                    Variable rightVariable = (Variable) right.getLeftParameter();
                    Term leftRelation = left.getAttribute();
                    Term rightRelation = right.getAttribute();       
                    Variable leftArgument = (Variable) left.getRightParameter();
                    Variable rightArgument = (Variable) right.getRightParameter();
                    
                    // check on symmetric attribute/relation
                    if (leftVariable.equals(rightArgument) && rightVariable.equals(
                    		leftArgument) && leftRelation.equals(rightRelation)) {
                    	list.add(0, SYMMETRIC);
                    	list.add(1, leftRelation.toString());
                    	list.add(2, rightRelation.toString());
                    	return list;
                    }
                    
                    // check on inverse attribute/relation
                    if (leftVariable.equals(rightArgument) && rightVariable.equals(
                    		leftArgument) && !(leftRelation.equals(rightRelation))) {
                    	list.add(0, INVERSE);
                    	list.add(1, leftRelation.toString());
                    	list.add(2, rightRelation.toString());
                    	return list;
                    }
                    
                    // check on subAttribute relation
                    if (leftVariable.equals(rightVariable) && leftArgument.equals(
                    		rightArgument) && !(leftRelation.equals(rightRelation))) {
                    	list.add(0, SUBATTRIBUTE);
                    	list.add(1, leftRelation.toString());
                    	list.add(2, rightRelation.toString());
                    	return list;
                    }
                }      
            }
            return null;
        }
    	
    	private List<String> isSubConceptInvImpl(InverseImplication expr) {
    		LogicalExpression leftLE = expr.getLeftOperand();
    		LogicalExpression rightLE = expr.getRightOperand();
    		List<String> list = new ArrayList<String>(2);
    		if (leftLE instanceof MembershipMolecule && rightLE instanceof 
    				MembershipMolecule) {
    			Variable leftVariable = (Variable) ((MembershipMolecule) 
    					leftLE).getLeftParameter();
    			Variable rightVariable = (Variable) ((MembershipMolecule) 
    					rightLE).getLeftParameter();      
                Identifier leftArgument = (Identifier) ((MembershipMolecule) 
    					leftLE).getRightParameter();
                Identifier rightArgument = (Identifier) ((MembershipMolecule) 
    					rightLE).getRightParameter();
    			if (leftVariable.equals(rightVariable) && !leftArgument.equals(
    					rightArgument)) {
    				list.add(0, rightArgument.toString());
    				list.add(1, leftArgument.toString());
    				return list;
    			}
    		}
    		return null;
    	}
    	
    	private List<String> isPropertyInvImpl(InverseImplication expr) {
    		LogicalExpression leftLE = expr.getLeftOperand();
    		LogicalExpression rightLE = expr.getRightOperand();
    		List<String> list = new ArrayList<String>(2);
    		if (leftLE instanceof MembershipMolecule && rightLE instanceof 
    				AttributeValueMolecule) {
    			Variable memberVariable = (Variable) ((MembershipMolecule) 
    					leftLE).getLeftParameter();
    			Identifier conceptId = (Identifier) ((MembershipMolecule)
    					leftLE).getRightParameter();
    			Variable leftVariable = (Variable) ((AttributeValueMolecule) 
    					rightLE).getLeftParameter();
    			Variable rightVariable = (Variable) ((AttributeValueMolecule) 
    					rightLE).getRightParameter();
    			Identifier attributeId = (Identifier) ((AttributeValueMolecule) 
    					rightLE).getAttribute();
    			if (memberVariable.equals(leftVariable) && !memberVariable.equals(
    					rightVariable)) {
    				list.add(0, DOMAIN);
    				list.add(1, attributeId.toString());
    				list.add(2, conceptId.toString());
    				return list;
    			}
    			else if (memberVariable.equals(rightVariable) && !memberVariable.
    					equals(leftVariable)) {
    				list.add(0, RANGE);
    				list.add(1, attributeId.toString());
    				list.add(2, conceptId.toString());
    				return list;
    			}
    		}
    		return null;
    	}
    	
    	/*
         * Gets an atom and transforms it into a value definition molecule.
         */
        private Molecule atomToMolecule(Atom expr) {
            Molecule molecule = new WSMO4JManager().getLogicalExpressionFactory().
            		createAttributeValue(expr.getParameter(0), expr.getIdentifier(), 
            				expr.getParameter(1));
                
            return molecule;
        }	
       
    }
    
    private class OWLGenerator {
        
    	private OWLClass createClass(String conceptId) {
    		URI uri = null;
        	OWLClass clazz = null;
    		try {
    			uri = new URI(conceptId);
    		} catch (URISyntaxException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		try {
    			clazz = owlDataFactory.getOWLClass(uri);
    			// Get a change object representing the addition of an OWL entity 
    			// to the ontology
    	    	ontologyChange = new AddEntity(owlOntology, clazz, null);
    	    	// Add the element to the ontology
    	    	ontologyChange.accept(changeVisitor);
    		} catch (OWLException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		return clazz;
    	}
    	
    	private void createSubClass(OWLClass subClass, OWLDescription superClass) {
    		try {
    			// Get a change object representing the addition of an OWL class 
    			// as superclass to another class
    	    	ontologyChange = new AddSuperClass(owlOntology, subClass, 
    	    			(OWLDescription) superClass, null);
    	    	// Add the element to the ontology
    	    	ontologyChange.accept(changeVisitor);
    		} catch (OWLException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}
    	
    	@SuppressWarnings("unchecked")
		private void createExplicitSubClass(OWLDescription subClass, 
				OWLDescription superClass) {
    		boolean equivalent = false;
    		try {
    			Iterator<OWLClassAxiom> it =owlOntology.getClassAxioms().iterator();
    			while (it.hasNext()) {
    				OWLClassAxiom axiom = it.next();
    				if (axiom instanceof OWLSubClassAxiom && ((OWLSubClassAxiom) axiom).
    						getSubClass().equals(superClass) && ((OWLSubClassAxiom) 
    					    axiom).getSuperClass().equals(subClass)) {
    					equivalent = true;
    					HashSet<OWLDescription> set = new HashSet<OWLDescription>();
    					set.add(subClass);
    					set.add(superClass);
    					OWLEquivalentClassesAxiom equivalentClassAxiom = 
    						owlGenerator.createEquivalentClasses(set);
    					// Get a change object representing the addition of an OWL class 
    					// as superclass to another class
    					ontologyChange = new AddClassAxiom(owlOntology, 
    							equivalentClassAxiom, null);
    					// Add the element to the ontology
    					ontologyChange.accept(changeVisitor);
    					// Get a change object representing the removal of the redundant 
    					// subClassAxiom
    					ontologyChange = new RemoveClassAxiom(owlOntology, 
    							(OWLSubClassAxiom) axiom, null);
    					// Add the element to the ontology
    					ontologyChange.accept(changeVisitor);
    				}
    			}
    			if (!equivalent) {
	    			OWLSubClassAxiom subClassAxiom = owlDataFactory.
	    					getOWLSubClassAxiom(subClass, superClass);
	    			// Get a change object representing the addition of an OWL class 
	    			// as superclass to another class
	    	    	ontologyChange = new AddClassAxiom(owlOntology, subClassAxiom, null);
    			}
    	    	// Add the element to the ontology
    	    	ontologyChange.accept(changeVisitor);
    		} catch (OWLException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}
        
    	private OWLNot createComplementClass(OWLDescription clazz) {
    		OWLNot not = null;
    		try {
				not = owlDataFactory.getOWLNot(clazz);
			} catch (OWLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return not;
    	}
    	
    	private OWLAnd createIntersectionClass(HashSet<OWLDescription> set) {
    		OWLAnd and = null;
    		try {
    			and = owlDataFactory.getOWLAnd(set);
			} catch (OWLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return and;
    	}
    	
    	private OWLOr createUnionClass(HashSet<OWLDescription> set) {
    		OWLOr or = null;
    		try {
    			or = owlDataFactory.getOWLOr(set);
			} catch (OWLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return or;
    	}
    	
    	private OWLObjectAllRestriction createAllValuesFromClass(OWLObjectProperty property, 
    			OWLDescription clazz) {
    		OWLObjectAllRestriction restriction = null;
    		try {
    			restriction = owlDataFactory.getOWLObjectAllRestriction(
    					property, clazz);
			} catch (OWLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return restriction;
    	}
    	
    	private OWLObjectSomeRestriction createSomeValuesFromClass(OWLObjectProperty property, 
    			OWLDescription clazz) {
    		OWLObjectSomeRestriction restriction = null;
    		try {
    			restriction = owlDataFactory.getOWLObjectSomeRestriction(
    					property, clazz);
			} catch (OWLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return restriction;
    	}
    	
    	private OWLEquivalentClassesAxiom createEquivalentClasses(HashSet<OWLDescription> set) {
    		OWLEquivalentClassesAxiom axiom = null;
    		try {
				axiom = owlDataFactory.getOWLEquivalentClassesAxiom(set);
				// Get a change object representing the addition of equivalent OWL 
				// classes to the ontology
    	    	ontologyChange = new AddClassAxiom(owlOntology, axiom, null);
    	    	// Add the element to the ontology
    	    	ontologyChange.accept(changeVisitor);
			} catch (OWLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return axiom;
    	}
    	
    	private OWLIndividual createIndividual(String instanceId) {
    		URI uri = null;
        	OWLIndividual individual = null;
    		try {
    			uri = new URI(instanceId);
    		} catch (URISyntaxException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		try {
    			individual = owlDataFactory.getOWLIndividual(uri);
    			// Get a change object representing the addition of an OWL individual 
    			// to the ontology
    	    	ontologyChange = new AddEntity(owlOntology, individual, null);
    	    	// Add the element to the ontology
    	    	ontologyChange.accept(changeVisitor);
    		} catch (OWLException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		return individual;
    	}
    	
    	private void createMemberOf(OWLIndividual individual, OWLClass clazz) {
    		try {
    			// Get a change object representing the addition of an OWL individual 
    			// to an OWL class
    	    	ontologyChange = new AddIndividualClass(owlOntology, individual, 
    	    			(OWLDescription) clazz, null);
    	    	// Add the element to the ontology
    	    	ontologyChange.accept(changeVisitor);
    		} catch (OWLException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}
    	
    	private OWLDataProperty createDataProperty(String id) {
    		OWLDataProperty dataProperty = null;
    		URI uri = null;
    		try {
    			uri = new URI(id);
    		} catch (URISyntaxException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		try {
    			dataProperty = owlDataFactory.getOWLDataProperty(uri);
    			// Get a change object representing the addition of an OWL object 
    			// property to the ontology
    			ontologyChange = new AddEntity(owlOntology, dataProperty, null);
    			// Add the element to the ontology
    	    	ontologyChange.accept(changeVisitor);
    		} catch (OWLException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		return dataProperty;
    	}
    	
    	private void createDataPropertyRange(OWLDataType dataType, OWLDataProperty 
    			dataProperty) {
    		try {
    			// Get a change object representing the addition of an OWL data  
    			// property range to the ontology
    	    	ontologyChange = new AddDataPropertyRange(owlOntology, dataProperty, 
    	    			dataType, null);
    	    	// Add the element to the ontology
    	    	ontologyChange.accept(changeVisitor);
    		} catch (OWLException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}
    	
    	private void createPropertyDomain(OWLClass clazz, OWLProperty property) {
    		try {
    			// Get a change object representing the addition of an OWL property 
    			// domain to the ontology
    	    	ontologyChange = new AddDomain(owlOntology, property, clazz, null);
    	    	// Add the element to the ontology
    	    	ontologyChange.accept(changeVisitor);
    		} catch (OWLException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}
    	
    	private OWLObjectProperty createObjectProperty(String id) {
    		OWLObjectProperty objectProperty = null;
    		URI uri = null;
    		try {
    			uri = new URI(id);
    		} catch (URISyntaxException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		try {
    			objectProperty = owlDataFactory.getOWLObjectProperty(uri);
    			// Get a change object representing the addition of an OWL object 
    			// property to the ontology
    			ontologyChange = new AddEntity(owlOntology, objectProperty, null);
    			// Add the element to the ontology
    			ontologyChange.accept(changeVisitor);   			
    		} catch (OWLException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		return objectProperty;
    	}
    	
    	private void createObjectPropertyRange(OWLClass clazz, OWLObjectProperty 
    			objectProperty) {
    		try {
    			// Get a change object representing the addition of an OWL object  
    			// property range to the ontology
    	    	ontologyChange = new AddObjectPropertyRange(owlOntology, 
    	    			objectProperty, clazz, null);
    	    	// Add the element to the ontology
    	    	ontologyChange.accept(changeVisitor);
    		} catch (OWLException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}
    	
    	private void createSubProperty(String leftRelation, String rightRelation) {
    		OWLObjectProperty subProperty = createObjectProperty(rightRelation);
    		OWLObjectProperty superProperty = createObjectProperty(leftRelation);
    		try {
    			OWLSubPropertyAxiom subPropertyAxiom = owlDataFactory.
    					getOWLSubPropertyAxiom(subProperty, superProperty);
    			// Get a change object representing the addition of an OWL subProperty  
    			// to the ontology
//    			ontologyChange = new AddSuperProperty(owlOntology, subProperty, 
//    						superProperty, null);
    	    	ontologyChange = new AddPropertyAxiom(owlOntology, subPropertyAxiom, 
    	    			null);
    	    	// Add the element to the ontology
    	    	ontologyChange.accept(changeVisitor);
    		} catch (OWLException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}
    	
    	private OWLObjectProperty createTransitiveProperty(String relation) {
    		OWLObjectProperty property = createObjectProperty(relation);
    		try {
    			// Get a change object representing the addition of an OWL inverse property  
    			// to the ontology
    	    	ontologyChange = new SetTransitive(owlOntology, property, true, null);
    	    	// Add the element to the ontology
    	    	ontologyChange.accept(changeVisitor);
    		} catch (OWLException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		return property;
    	}
    	
    	private OWLObjectProperty createSymmetricProperty(String relation) {
    		OWLObjectProperty property = createObjectProperty(relation);
    		try {		
    			// Get a change object representing the addition of an OWL symmetric 
    			// property to the ontology
    	    	ontologyChange = new SetSymmetric(owlOntology, 
    	    			property, true, null);
    	    	// Add the element to the ontology
    	    	ontologyChange.accept(changeVisitor);
    		} catch (OWLException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		return property;
    	}
    	
    	private HashSet<OWLObjectProperty> createInverseProperty(String leftRelation, 
    			String rightRelation) {
    		OWLObjectProperty leftProperty = createObjectProperty(leftRelation);
    		OWLObjectProperty rightProperty = createObjectProperty(rightRelation);
    		try {
    			// Get a change object representing the addition of an OWL inverse 
    			// property to the ontology
    	    	ontologyChange = new AddInverse(owlOntology, leftProperty, rightProperty, 
    	    			null);
    	    	// Add the element to the ontology
    	    	ontologyChange.accept(changeVisitor);
    		} catch (OWLException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		HashSet<OWLObjectProperty> set = new HashSet<OWLObjectProperty>();
    		set.add(leftProperty);
    		set.add(rightProperty);
    		return set;
    	}
    	
    	private void createIndividualValue(OWLDataProperty dataProperty, 
    			OWLIndividual individual, OWLDataValue dataValue) {
    		try {
    			// Get a change object representing the addition of an OWL individual 
    			// value to the ontology
    	    	ontologyChange = new AddDataPropertyInstance(owlOntology, individual, 
    	    			dataProperty, dataValue, null);
    	    	// Add the element to the ontology
    	    	ontologyChange.accept(changeVisitor);
    		} catch (OWLException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}
    	
    	private void createIndividualValue(OWLObjectProperty objectProperty, 
    			OWLIndividual individual1, OWLIndividual individual2) {
    		try {
    			// Get a change object representing the addition of an OWL individual 
    			// value to the ontology
    	    	ontologyChange = new AddObjectPropertyInstance(owlOntology, individual1, 
    	    			objectProperty, individual2, null);
    	    	// Add the element to the ontology
    	    	ontologyChange.accept(changeVisitor);
    		} catch (OWLException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}
    	
    	private OWLDataType createDataType(String value) {
    		OWLDataType dataType = null;
    		String xsd = "http://www.w3.org/2001/XMLSchema#";
//    		String xsd = "xsd:";
    		URI uri = null;
    		try {
    			if (value.equals(WsmlDataType.WSML_STRING))
    				uri = new URI(xsd + "string");
    			if (value.equals(WsmlDataType.WSML_INTEGER))
    				uri = new URI(xsd + "integer");
    			if (value.equals(WsmlDataType.WSML_DECIMAL))
    				uri = new URI(xsd + "decimal");
    			if (value.equals(WsmlDataType.WSML_FLOAT))
    				uri = new URI(xsd + "float");
    			if (value.equals(WsmlDataType.WSML_DOUBLE))
    				uri = new URI(xsd + "double");
    			if (value.equals(WsmlDataType.WSML_IRI))
    				uri = new URI(xsd + "anyURI");
    			if (value.equals(WsmlDataType.WSML_SQNAME))
    				uri = new URI(xsd + "QName");
    			if (value.equals(WsmlDataType.WSML_BOOLEAN))
    				uri = new URI(xsd + "boolean");
    			if (value.equals(WsmlDataType.WSML_DURATION))
    				uri = new URI(xsd + "duration");
    			if (value.equals(WsmlDataType.WSML_DATETIME))
    				uri = new URI(xsd + "dateTime");
    			if (value.equals(WsmlDataType.WSML_TIME))
    				uri = new URI(xsd + "time");
    			if (value.equals(WsmlDataType.WSML_DATE))
    				uri = new URI(xsd + "date");
    			if (value.equals(WsmlDataType.WSML_GYEARMONTH))
    				uri = new URI(xsd + "gYearMonth");
    			if (value.equals(WsmlDataType.WSML_GYEAR))
    				uri = new URI(xsd + "gYear");
    			if (value.equals(WsmlDataType.WSML_GMONTHDAY))
    				uri = new URI(xsd + "gMonthDay");
    			if (value.equals(WsmlDataType.WSML_GDAY))
    				uri = new URI(xsd + "gDay");
    			if (value.equals(WsmlDataType.WSML_GMONTH))
    				uri = new URI(xsd + "gMonth");
    			if (value.equals(WsmlDataType.WSML_HEXBINARY))
    				uri = new URI(xsd + "hexBinary");
    			if (value.equals(WsmlDataType.WSML_BASE64BINARY))
    				uri = new URI(xsd + "base64Binary");
    				
    		} catch (URISyntaxException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		try {
    			dataType = owlDataFactory.getOWLConcreteDataType(uri);
    		} catch (OWLException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		return dataType;
    	}
    	
    	private OWLDataValue createDataValue(DataValue value) {
    		OWLDataValue dataValue = null;
    		String xsd = "http://www.w3.org/2001/XMLSchema#";
//    		String xsd = "xsd:";
    		URI uri = null;
    		try {
    			if (value.getType().toString().equals(WsmlDataType.WSML_STRING))
    				uri = new URI(xsd + "string");
    			if (value.getType().toString().equals(WsmlDataType.WSML_INTEGER))
    				uri = new URI(xsd + "integer");
    			if (value.getType().toString().equals(WsmlDataType.WSML_DECIMAL))
    				uri = new URI(xsd + "decimal");
    			if (value.getType().toString().equals(WsmlDataType.WSML_FLOAT))
    				uri = new URI(xsd + "float");
    			if (value.getType().toString().equals(WsmlDataType.WSML_DOUBLE))
    				uri = new URI(xsd + "double");
    			if (value.getType().toString().equals(WsmlDataType.WSML_IRI))
    				uri = new URI(xsd + "anyURI");
    			if (value.getType().toString().equals(WsmlDataType.WSML_SQNAME))
    				uri = new URI(xsd + "QName");
    			if (value.getType().toString().equals(WsmlDataType.WSML_BOOLEAN))
    				uri = new URI(xsd + "boolean");
    			if (value.getType().toString().equals(WsmlDataType.WSML_DURATION))
    				uri = new URI(xsd + "duration");
    			if (value.getType().toString().equals(WsmlDataType.WSML_DATETIME))
    				uri = new URI(xsd + "dateTime");
    			if (value.getType().toString().equals(WsmlDataType.WSML_TIME))
    				uri = new URI(xsd + "time");
    			if (value.getType().toString().equals(WsmlDataType.WSML_DATE))
    				uri = new URI(xsd + "date");
    			if (value.getType().toString().equals(WsmlDataType.WSML_GYEARMONTH))
    				uri = new URI(xsd + "gYearMonth");
    			if (value.getType().toString().equals(WsmlDataType.WSML_GYEAR))
    				uri = new URI(xsd + "gYear");
    			if (value.getType().toString().equals(WsmlDataType.WSML_GMONTHDAY))
    				uri = new URI(xsd + "gMonthDay");
    			if (value.getType().toString().equals(WsmlDataType.WSML_GDAY))
    				uri = new URI(xsd + "gDay");
    			if (value.getType().toString().equals(WsmlDataType.WSML_GMONTH))
    				uri = new URI(xsd + "gMonth");
    			if (value.getType().toString().equals(WsmlDataType.WSML_HEXBINARY))
    				uri = new URI(xsd + "hexBinary");
    			if (value.getType().toString().equals(WsmlDataType.WSML_BASE64BINARY))
    				uri = new URI(xsd + "base64Binary");
    				
    		} catch (URISyntaxException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		Object val = value.getValue();
    		if (value.getType().toString().equals(WsmlDataType.WSML_DATE)) {
    			int day = ((Calendar) value.getValue()).get(Calendar.DAY_OF_MONTH);
    			int month = ((Calendar) value.getValue()).get(Calendar.MONTH) + 1;
    			int year = ((Calendar) value.getValue()).get(Calendar.YEAR);
    			if (month < 10) {
    				val = year + "-0" + month + "-" + day; 
    			}
    			else {
    				val = year + "-" + month + "-" + day;
    			}
    		}
    		try {
    			dataValue = owlDataFactory.getOWLConcreteData(uri, null, val);
    		} catch (OWLException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		return dataValue;
    	}
    }
}
/*
 * $Log: not supported by cvs2svn $
 * Revision 1.3  2006/07/20 17:48:34  nathalie
 * fixed a problem with xsd datatypes
 *
 * Revision 1.2  2006/07/18 10:52:28  nathalie
 * added transformation from universal quantification
 *
 * Revision 1.1  2006/07/18 08:21:01  nathalie
 * adding wsml dl reasoner interface,
 * transformation from wsml dl to owl-dl
 *
 *
 */