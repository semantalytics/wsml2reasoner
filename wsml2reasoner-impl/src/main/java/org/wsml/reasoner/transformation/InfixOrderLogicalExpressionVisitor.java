package org.wsml.reasoner.transformation;

import java.util.Iterator;

import org.omwg.logicalexpression.Atom;
import org.omwg.logicalexpression.AttributeConstraintMolecule;
import org.omwg.logicalexpression.AttributeInferenceMolecule;
import org.omwg.logicalexpression.AttributeValueMolecule;
import org.omwg.logicalexpression.CompoundExpression;
import org.omwg.logicalexpression.CompoundMolecule;
import org.omwg.logicalexpression.Conjunction;
import org.omwg.logicalexpression.Constraint;
import org.omwg.logicalexpression.Disjunction;
import org.omwg.logicalexpression.Equivalence;
import org.omwg.logicalexpression.ExistentialQuantification;
import org.omwg.logicalexpression.Implication;
import org.omwg.logicalexpression.InverseImplication;
import org.omwg.logicalexpression.LogicProgrammingRule;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.LogicalExpressionVisitor;
import org.omwg.logicalexpression.MembershipMolecule;
import org.omwg.logicalexpression.Negation;
import org.omwg.logicalexpression.NegationAsFailure;
import org.omwg.logicalexpression.SubConceptMolecule;
import org.omwg.logicalexpression.TruthValue;
import org.omwg.logicalexpression.UniversalQuantification;

/**
 * Implements a left-first, depth-first traversal over logical expressions. The
 * point in time when a node has to be handled according to the traversal order
 * is defined by the infix order.
 * 
 * Subclasses should be visitors of logical expressions that don't need to care
 * about how to traverse nodes but overwrite the new kinds of methods whose
 * default implementation is empty:
 * 
 * (1) 'handleXXX' methods, that are called by the abstract traversal
 * implementation when the respective node is actually to be handeled (according
 * to the traversal order)
 * 
 * (2) 'enterXXX' methods, that are called by the abstract traversal
 * implementation when the respective subtree rooted in the given node is
 * entered (according to the traversal order)
 * 
 * (3) 'leaveXXX' methods, that are called by the abstract traversal
 * implementation when the respective subtree rooted in the given node is left
 * (according to the traversal order)
 * 
 * @author Uwe Keller, DERI Innsbruck
 * @author Gabor Nagypal, FZI
 */
public abstract class InfixOrderLogicalExpressionVisitor implements
		LogicalExpressionVisitor {

	@Override
	public void visitAtom(Atom arg0) {
		enterAtom(arg0);
		handleAtom(arg0);
		leaveAtom(arg0);
	}

	@Override
	public void visitCompoundMolecule(CompoundMolecule arg0) {
		enterCompoundMolecule(arg0);
		visitOperands(arg0);
		handleCompoundMolecule(arg0);
		leaveCompoundMolecule(arg0);
	}

	@Override
	public void visitSubConceptMolecule(SubConceptMolecule arg0) {
		enterSubConceptMolecule(arg0);
		handleSubConceptMolecule(arg0);
		leaveSubConceptMolecule(arg0);

	}

	@Override
	public void visitMemberShipMolecule(MembershipMolecule arg0) {
		enterMemberShipMolecule(arg0);
		handleMemberShipMolecule(arg0);
		leaveMemberShipMolecule(arg0);

	}

	@Override
	public void visitAttributeValueMolecule(AttributeValueMolecule arg0) {
		enterAttributeValueMolecule(arg0);
		handleAttributeValueMolecule(arg0);
		leaveAttributeValueMolecule(arg0);

	}

	@Override
	public void visitAttributeConstraintMolecule(
			AttributeConstraintMolecule arg0) {
		enterAttributeConstraintMolecule(arg0);
		handleAttributeConstraintMolecule(arg0);
		leaveAttributeConstraintMolecule(arg0);

	}

	@Override
	public void visitAttributeInferenceMolecule(AttributeInferenceMolecule arg0) {
		enterAttributeInferenceMolecule(arg0);
		handleAttributeInferenceMolecule(arg0);
		leaveAttributeInferenceMolecule(arg0);

	}

	@Override
	public void visitTruthValue(TruthValue arg0) {
		enterTruthValue(arg0);
		handleTruthValue(arg0);
		leaveTruthValue(arg0);
	}

	@Override
	public void visitNegation(Negation arg0) {
		enterNegation(arg0);
		arg0.getOperand().accept(this);
		handleNegation(arg0);
		leaveNegation(arg0);

	}

	@Override
	public void visitNegationAsFailure(NegationAsFailure arg0) {
		enterNegationAsFailure(arg0);
		arg0.getOperand().accept(this);
		handleNegationAsFailure(arg0);
		leaveNegationAsFailure(arg0);

	}

	@Override
	public void visitConstraint(Constraint arg0) {
		enterConstraint(arg0);
		arg0.getOperand().accept(this);
		handleConstraint(arg0);
		leaveConstraint(arg0);

	}

	@Override
	public void visitConjunction(Conjunction arg0) {
		enterConjunction(arg0);
		arg0.getLeftOperand().accept(this);
		handleConjunction(arg0);
		arg0.getRightOperand().accept(this);
		leaveConjunction(arg0);
	}

	@Override
	public void visitDisjunction(Disjunction arg0) {
		enterDisjunction(arg0);
		arg0.getLeftOperand().accept(this);
		handleDisjunction(arg0);
		arg0.getRightOperand().accept(this);
		leaveDisjunction(arg0);

	}

	@Override
	public void visitInverseImplication(InverseImplication arg0) {
		enterInverseImplication(arg0);
		arg0.getLeftOperand().accept(this);
		handleInverseImplication(arg0);
		arg0.getRightOperand().accept(this);
		leaveInverseImplication(arg0);

	}

	@Override
	public void visitImplication(Implication arg0) {
		enterImplication(arg0);
		arg0.getLeftOperand().accept(this);
		handleImplication(arg0);
		arg0.getRightOperand().accept(this);
		leaveImplication(arg0);

	}

	@Override
	public void visitEquivalence(Equivalence arg0) {
		enterEquivalence(arg0);
		arg0.getLeftOperand().accept(this);
		handleEquivalence(arg0);
		arg0.getRightOperand().accept(this);
		leaveEquivalence(arg0);

	}

	@Override
	public void visitLogicProgrammingRule(LogicProgrammingRule arg0) {
		enterLogicProgrammingRule(arg0);
		arg0.getLeftOperand().accept(this);
		handleLogicProgrammingRule(arg0);
		arg0.getRightOperand().accept(this);
		leaveLogicProgrammingRule(arg0);

	}

	@Override
	public void visitUniversalQuantification(UniversalQuantification arg0) {
		enterUniversalQuantification(arg0);
		arg0.getOperand().accept(this);
		handleUniversalQuantification(arg0);
		leaveUniversalQuantification(arg0);

	}

	@Override
	public void visitExistentialQuantification(ExistentialQuantification arg0) {
		enterExistentialQuantification(arg0);
		arg0.getOperand().accept(this);
		handleExistentialQuantification(arg0);
		leaveExistentialQuantification(arg0);

	}

	private void visitOperands(CompoundExpression arg0) {
		for (Iterator<LogicalExpression> i = arg0.listOperands().iterator(); i
				.hasNext();) {
			LogicalExpression operand = (LogicalExpression) i.next();
			operand.accept(this);
		}
	}

	abstract public Object getSerializedObject();

	public void enterAtom(Atom arg0) {
	}

	public void handleAtom(Atom arg0) {
	}

	public void leaveAtom(Atom arg0) {
	}

	public void leaveTruthValue(TruthValue arg0) {
	}

	public void handleTruthValue(TruthValue arg0) {
	}

	public void enterTruthValue(TruthValue arg0) {
	}

	public void enterSubConceptMolecule(SubConceptMolecule arg0) {
	}

	public void handleSubConceptMolecule(SubConceptMolecule arg0) {
	}

	public void leaveSubConceptMolecule(SubConceptMolecule arg0) {
	}

	public void enterMemberShipMolecule(MembershipMolecule arg0) {
	}

	public void handleMemberShipMolecule(MembershipMolecule arg0) {
	}

	public void leaveMemberShipMolecule(MembershipMolecule arg0) {
	}

	public void enterAttributeValueMolecule(AttributeValueMolecule arg0) {
	}

	public void handleAttributeValueMolecule(AttributeValueMolecule arg0) {
	}

	public void leaveAttributeValueMolecule(AttributeValueMolecule arg0) {
	}

	public void enterAttributeConstraintMolecule(
			AttributeConstraintMolecule arg0) {
	}

	public void handleAttributeConstraintMolecule(
			AttributeConstraintMolecule arg0) {
	}

	public void leaveAttributeConstraintMolecule(
			AttributeConstraintMolecule arg0) {
	}

	public void enterAttributeInferenceMolecule(AttributeInferenceMolecule arg0) {
	}

	public void handleAttributeInferenceMolecule(AttributeInferenceMolecule arg0) {
	}

	public void leaveAttributeInferenceMolecule(AttributeInferenceMolecule arg0) {
	}

	public void enterConjunction(Conjunction arg0) {
	}

	public void handleConjunction(Conjunction arg0) {
	}

	public void leaveConjunction(Conjunction arg0) {
	}

	public void enterDisjunction(Disjunction arg0) {
	}

	public void handleDisjunction(Disjunction arg0) {
	}

	public void leaveDisjunction(Disjunction arg0) {
	}

	public void enterCompoundMolecule(CompoundMolecule arg0) {
	}

	public void handleCompoundMolecule(CompoundMolecule arg0) {
	}

	public void leaveCompoundMolecule(CompoundMolecule arg0) {
	}

	public void enterNegation(Negation arg0) {
	}

	public void handleNegation(Negation arg0) {
	}

	public void leaveNegation(Negation arg0) {
	}

	public void enterNegationAsFailure(NegationAsFailure arg0) {
	}

	public void handleNegationAsFailure(NegationAsFailure arg0) {
	}

	public void leaveNegationAsFailure(NegationAsFailure arg0) {
	}

	public void enterConstraint(Constraint arg0) {
	}

	public void handleConstraint(Constraint arg0) {
	}

	public void leaveConstraint(Constraint arg0) {
	}

	public void enterInverseImplication(InverseImplication arg0) {
	}

	public void handleInverseImplication(InverseImplication arg0) {
	}

	public void leaveInverseImplication(InverseImplication arg0) {
	}

	public void enterImplication(Implication arg0) {
	}

	public void handleImplication(Implication arg0) {
	}

	public void leaveImplication(Implication arg0) {
	}

	public void enterEquivalence(Equivalence arg0) {
	}

	public void handleEquivalence(Equivalence arg0) {
	}

	public void leaveEquivalence(Equivalence arg0) {
	}

	public void enterLogicProgrammingRule(LogicProgrammingRule arg0) {
	}

	public void handleLogicProgrammingRule(LogicProgrammingRule arg0) {
	}

	public void leaveLogicProgrammingRule(LogicProgrammingRule arg0) {
	}

	public void enterUniversalQuantification(UniversalQuantification arg0) {
	}

	public void handleUniversalQuantification(UniversalQuantification arg0) {
	}

	public void leaveUniversalQuantification(UniversalQuantification arg0) {
	}

	public void enterExistentialQuantification(ExistentialQuantification arg0) {
	}

	public void handleExistentialQuantification(ExistentialQuantification arg0) {
	}

	public void leaveExistentialQuantification(ExistentialQuantification arg0) {
	}
}
