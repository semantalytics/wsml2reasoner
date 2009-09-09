package org.wsml.reasoner.builtin.elly;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.deri.iris.api.basics.ITuple;
import org.deri.iris.api.terms.ITerm;
import org.omwg.logicalexpression.Atom;
import org.omwg.logicalexpression.AttributeConstraintMolecule;
import org.omwg.logicalexpression.AttributeInferenceMolecule;
import org.omwg.logicalexpression.AttributeValueMolecule;
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
import org.omwg.logicalexpression.terms.ConstructedTerm;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.logicalexpression.terms.TermVisitor;
import org.omwg.ontology.ComplexDataValue;
import org.omwg.ontology.SimpleDataValue;
import org.omwg.ontology.Variable;
import org.sti2.elly.api.Vocabulary;
import org.sti2.elly.api.basics.IAtom;
import org.sti2.elly.api.basics.IAtomicConcept;
import org.sti2.elly.api.basics.IAtomicRole;
import org.sti2.elly.api.basics.IConceptDescription;
import org.sti2.elly.api.basics.IDescription;
import org.sti2.elly.api.basics.IRoleDescription;
import org.sti2.elly.api.basics.IRule;
import org.sti2.elly.api.factory.IBasicFactory;
import org.sti2.elly.api.factory.ITermFactory;
import org.sti2.elly.api.terms.IIndividual;
import org.sti2.elly.api.terms.IVariable;
import org.sti2.elly.basics.BasicFactory;
import org.sti2.elly.terms.TermFactory;
import org.sti2.elly.transformation.factory.AbstractFactory;
import org.wsmo.common.IRI;
import org.wsmo.common.NumberedAnonymousID;
import org.wsmo.common.UnnumberedAnonymousID;

public class Wsml2EllyTranslator implements LogicalExpressionVisitor, TermVisitor {
	
	/**
	 * This enum allows to specify the expected type.
	 * When visiting a term, the type must be set such that a concept, role, or term can be created.
	 */
	private enum Type {
		CONCEPT("Concept Description"),
		DATA_TYPE("Datatype"),
		ROLE("Role Description"),
		TERM("Term");
		
		private String name;
		
		Type(String name) {
			this.name = name;
		}
		
		@Override
		public String toString() {
			return name;
		}
	}
	
	private static IBasicFactory BASIC = BasicFactory.getInstance();
	private static ITermFactory TERM = TermFactory.getInstance();
	
	static final IVariable varX = TERM.createVariable("x", false);
	static final IVariable varY = TERM.createVariable("y", false);
	static final IVariable varZ = TERM.createVariable("z", false);
	static final ITuple tupleX = BASIC.createTuple(varX);
	static final ITuple tupleY = BASIC.createTuple(varY);
	//	static final ITuple tupleZ = BASIC.createTuple(varZ);
	static final ITuple tupleXY = BASIC.createTuple(varX, varY);
	static final ITuple tupleXZ = BASIC.createTuple(varX, varZ);
	static final ITuple tupleYX = BASIC.createTuple(varY, varX);
	static final ITuple tupleYZ = BASIC.createTuple(varY, varZ);
	static final ITuple tupleXX = BASIC.createTuple(varX, varX);

	private final List<IRule> rules;
	private final Stack<ITerm> termStack;
	private final Stack<IDescription> descriptionStack;
	private final Stack<IAtom> atomStack;
	
	private final Map<Object, Object> wsml2EllyCache;
	
	private Type expectedType;
	
	private boolean inRule;
	
	public Wsml2EllyTranslator(List<IRule> rules) {
		if (rules == null)
			throw new IllegalArgumentException("rules must not be null");
		this.rules = rules;
		
		termStack = new Stack<ITerm>();
		descriptionStack = new Stack<IDescription>();
		atomStack = new Stack<IAtom>();
		
		wsml2EllyCache = new HashMap<Object, Object>();
		
		inRule = false;
	}
	
	/* *******************************
	 * Terms
	 * *******************************/

	@Override
	public void visit(ConstructedTerm t) {
		throw new UnsupportedOperationException("ConstructedTerms are not supported by ELP");
	}

	@Override
	public void visit(Variable t) {
		if (expectedType != Type.TERM)
			throw new RuntimeException("Unable to create a " + expectedType + " from Term " + t);
		
		IVariable eVariable = getOrCreateVariable(t);
		pushTerm(eVariable);
	}

	@Override
	public void visit(SimpleDataValue t) {
		if (expectedType != Type.TERM)
			throw new RuntimeException("Unable to create a " + expectedType + " from Term " + t);
		
		ITerm dataValue = Wsml2EllyDataValueTranslator.convertWsmo4jDataValueToIrisTerm(t);
		pushTerm(dataValue);
	}

	@Override
	public void visit(ComplexDataValue t) {
		if (expectedType != Type.TERM)
			throw new RuntimeException("Unable to create a " + expectedType + " from Term " + t);
		
		ITerm dataValue = Wsml2EllyDataValueTranslator.convertWsmo4jDataValueToIrisTerm(t);
		pushTerm(dataValue);
	}

	@Override
	public void visit(UnnumberedAnonymousID t) {
		String anonymousName = NameFactory.getAnonymousName();
		
		switch (expectedType) {
		case CONCEPT:
			pushDescription(BASIC.createAtomicConcept(anonymousName));
			break;

		case ROLE:
			pushDescription(BASIC.createAtomicRole(anonymousName));
			break;

		case TERM:
			pushTerm(TERM.createIndividual(anonymousName));
			break;

		case DATA_TYPE:
			throw new RuntimeException("Unable to create a " + expectedType + " from Term " + t);

		default:
			throw new RuntimeException("Unable to handle Type " + expectedType);
		}
	}

	@Override
	public void visit(NumberedAnonymousID t) {
		String anonymousName = NameFactory.getAnonymousName(t.getNumber());
		
		switch (expectedType) {
		case CONCEPT:
			pushDescription(BASIC.createAtomicConcept(anonymousName));
			break;

		case ROLE:
			pushDescription(BASIC.createAtomicRole(anonymousName));
			break;

		case TERM:
			pushTerm(TERM.createIndividual(anonymousName));
			break;

		case DATA_TYPE:
			throw new RuntimeException("Unable to create a " + expectedType + " from Term " + t);

		default:
			throw new RuntimeException("Unable to handle Type " + expectedType);
		}
	}

	@Override
	public void visit(IRI t) {
		switch (expectedType) {
		case CONCEPT:
			pushDescription(getOrCreateConcept(t));
			break;

		case ROLE:
			pushDescription(getOrCreateRole(t));
			break;

		case TERM:
			pushTerm(getOrCreateIndividual(t));
			break;

		case DATA_TYPE:
			pushDescription(getOrCreateConcept(t));
			// FIXME dw: change that later, handle wsml datatypes 
//			IDescription dataType = DataType.asDataType(t.toString()).asConcept();
//			pushDescription(dataType);
			break;

		default:
			throw new RuntimeException("Unable to handle Type " + expectedType);
		}

	}
	
	/* *******************************
	 * Logical Expressions
	 * *******************************/

	@Override
	public void visitAtom(Atom expr) {
		// TODO built-ins?
		throw new UnsupportedOperationException("Atoms are not supported by ELP");
	}

	/**
	 * AttributeConstraintMolecule represents a constraining attribute molecule (e.g. human[age ofType _integer)).
	 * Creates and adds a rule {@code DT(y) :- C(x) and r(x,y)} from {@code C[r ofType DT]}.
	 * </p>
	 * π(id1[id2 ofType dt]) | dt(y) ← id1(x) ∧ id2(x,y)
	 * 
	 * @see org.omwg.logicalexpression.LogicalExpressionVisitor#visitAttributeConstraintMolecule(org.omwg.logicalexpression.AttributeConstraintMolecule)
	 */
	@Override
	public void visitAttributeConstraintMolecule(AttributeConstraintMolecule expr) {
		setInRule();
		
		expectedType = Type.ROLE;
		expr.getAttribute().accept(this);
		IDescription id2 = popDescription();
		
		expectedType = Type.CONCEPT;
		expr.getLeftParameter().accept(this);
		IDescription id1 = popDescription();

		expectedType = Type.DATA_TYPE;
		expr.getRightParameter().accept(this);
		IDescription dt = popDescription();
		
		List<IAtom> head = new ArrayList<IAtom>();
		List<IAtom> body = new ArrayList<IAtom>();
		body.add(BASIC.createAtom(id1, tupleX));
		body.add(BASIC.createAtom(id2, tupleXY));
		head.add(BASIC.createAtom(dt, tupleY));
		
		rules.add(BASIC.createRule(head, body));
		
		clearInRule();
	}

	/**
	 * AttributeInferenceMolecule Represents an inferring attribute molecule (e.g. human[ancestor impliesType human]).
	 * Creates and adds a rule {@code DT(y) :- C(x) and r(x,y)} from {@code C[r impliesType DT]}.
	 * </p>
	 * π(id1[id2 impliesType id3]) | id3(y) ← id1(x) ∧ id2(x,y)
	 * 
	 * @see org.omwg.logicalexpression.LogicalExpressionVisitor#visitAttributeInferenceMolecule(org.omwg.logicalexpression.AttributeInferenceMolecule)
	 */
	@Override
	public void visitAttributeInferenceMolecule(AttributeInferenceMolecule expr) {
		setInRule();
		
		expectedType = Type.ROLE;
		expr.getAttribute().accept(this);
		IDescription id2 = popDescription();
		
		expectedType = Type.CONCEPT;
		expr.getLeftParameter().accept(this);
		IDescription id1 = popDescription();

		expectedType = Type.CONCEPT;
		expr.getRightParameter().accept(this);
		IDescription id3 = popDescription();

		List<IAtom> head = new ArrayList<IAtom>();
		List<IAtom> body = new ArrayList<IAtom>();
		body.add(BASIC.createAtom(id1, tupleX));
		body.add(BASIC.createAtom(id2, tupleXY));
		head.add(BASIC.createAtom(id3, tupleY));
		
		rules.add(BASIC.createRule(head, body));
		
		clearInRule();
	}

	/**
	 * AttributeValueMolecule Represents a value attribute molecule (e.g. human[age hasValue 4]).
	 * </p>
	 * Creates and adds a fact {@code id2(X1,X2).}
	 * </p>
	 * π(X1[id2 hasValue X2]) | id2(X1,X2)
	 * 
	 * @see org.omwg.logicalexpression.LogicalExpressionVisitor#visitAttributeValueMolecule(org.omwg.logicalexpression.AttributeValueMolecule)
	 */
	@Override
	public void visitAttributeValueMolecule(AttributeValueMolecule expr) {
		expectedType = Type.ROLE;
		expr.getAttribute().accept(this);
		IRoleDescription id2 = (IRoleDescription) popDescription();
		
		expectedType = Type.TERM;
		expr.getLeftParameter().accept(this);
		ITerm x1 = popTerm();

		expectedType = Type.TERM;
		expr.getRightParameter().accept(this);
		ITerm x2 = popTerm();

		
		IAtom atom = null;
		if (x1.equals(x2)) { // Self Restriction
			ITuple tuple = BASIC.createTuple(x1);
			atom = BASIC.createAtom(BASIC.createSelfRestriction(id2), tuple);
		} else {
			ITuple tuple = BASIC.createTuple(x1, x2);
			atom = BASIC.createAtom(id2, tuple);
		}
		
		handleAtom(atom);
	}

	/**
	 * CompoundMolecule Represents a compound molecule which is a container for simple molecules
	 * E.g., "x subConceptOf {y,z}" or "a[b hasValue c] memberOf d".
	 * </p>
	 * Visits all {@link CompoundMolecule#listOperands()} Molecules, since all other {@code list} methods are just filters on all operands.
	 * 
	 * 
	 * @see org.omwg.logicalexpression.LogicalExpressionVisitor#visitCompoundMolecule(org.omwg.logicalexpression.CompoundMolecule)
	 */
	@Override
	public void visitCompoundMolecule(CompoundMolecule expr) {
		for (LogicalExpression molecule : expr.listOperands()) {
			molecule.accept(this);
		}
	}

	/**
	 * Conjunction represents specific kind of <code>Binary</code>. A conjunction whose operator is an and.
	 * </p>
	 * π(lexpr and rexpr) | π(lexpr) ∧ π(rexpr)
	 * </p>
	 * Both Operands are visited and the resulting Descriptions are left on the {@link #atomStack}.
	 * 
	 * @see org.omwg.logicalexpression.LogicalExpressionVisitor#visitConjunction(org.omwg.logicalexpression.Conjunction)
	 */
	@Override
	public void visitConjunction(Conjunction expr) {
		expr.getLeftOperand().accept(this);
		expr.getRightOperand().accept(this);

		// Check if this is a Concept or Role Intersection 
		IAtom atom1 = popAtom();
		IAtom atom2 = popAtom();

		// If they are not equal it is not, therefore push again
		if (!(atom1.getTuple().equals(atom2.getTuple()))) {
			pushAtom(atom1);
			pushAtom(atom2);
		} else {
			if (atom1.getDescription() instanceof IConceptDescription) {
				if (atom2.getDescription() instanceof IConceptDescription) {
					// Create Concept Intersection
					IDescription conceptIntersection = BASIC.createIntersectionConcept(
							(IConceptDescription) atom1.getDescription(),
							(IConceptDescription) atom2.getDescription()
							);
					
					pushAtom(BASIC.createAtom(conceptIntersection, atom1.getTuple()));
				} else {
					throw new RuntimeException("Descriptions of conjunction " + expr + " must be equal!");
				}
			} else if (atom1.getDescription() instanceof IRoleDescription) {
				if (atom2.getDescription() instanceof IRoleDescription) {
					// Create Concept Intersection
					IDescription roleIntersection = BASIC.createIntersectionRole(
							(IRoleDescription) atom1.getDescription(),
							(IRoleDescription) atom2.getDescription()
							);
					
					pushAtom(BASIC.createAtom(roleIntersection, atom1.getTuple()));
				} else {
					throw new RuntimeException("Descriptions of conjunction " + expr + " must be equal!");
				}
			}
		}
	}

	@Override
	public void visitConstraint(Constraint expr) {
		setInRule();
		
		assert atomStack.isEmpty();
		
		expr.getOperand().accept(this);
		
		assert atomStack.size() > 0;
		
		rules.add(BASIC.createIntegrityConstraint(new ArrayList<IAtom>(atomStack)));
		atomStack.clear();
		
		clearInRule();
	}

	@Override
	public void visitDisjunction(Disjunction expr) {
		throw new UnsupportedOperationException("Disjunction is not supported by ELP");
	}

	@Override
	public void visitEquivalence(Equivalence expr) {
		visitImplication(expr.getLeftOperand(), expr.getRightOperand());
		visitImplication(expr.getRightOperand(), expr.getLeftOperand());
	}

	/**
	 * π(exists Y1,..,Yn expr) | ∃ Y1,..,Yn π(expr)
	 * 
	 * @see org.omwg.logicalexpression.LogicalExpressionVisitor#visitExistentialQuantification(org.omwg.logicalexpression.ExistentialQuantification)
	 */
	@Override
	public void visitExistentialQuantification(ExistentialQuantification expr) {
		expr.getOperand().accept(this);
		
		IAtom conceptAtom = popAtom();
		IAtom roleAtom = popAtom();
		
		if (!(conceptAtom.getDescription() instanceof IConceptDescription))
			throw new IllegalArgumentException("ExistentialQuantification " + expr.toString() + " must be of syntax exists \\phi (G and F_1) with \\phi \\in V_V, G a b-molecule, F_1 an a-molecule");
		if (!(roleAtom.getDescription() instanceof IRoleDescription))
			throw new IllegalArgumentException("ExistentialQuantification " + expr.toString() + " must be of syntax exists \\phi (G and F_1) with \\phi \\in V_V, G a b-molecule, F_1 an a-molecule");
		if (!(conceptAtom.getTuple().get(0).equals(roleAtom.getTuple().get(1))))
			throw new IllegalArgumentException("ExistentialQuantification " + expr.toString() + " must be of syntax exists \\phi (G and F_1) with \\phi \\in V_V, G a b-molecule, F_1 an a-molecule");
		if (expr.listVariables().size() != 1)
			throw new IllegalArgumentException("ExistentialQuantification " + expr.toString() + " must be of syntax exists \\phi (G and F_1) with \\phi \\in V_V, G a b-molecule, F_1 an a-molecule");
		
		IConceptDescription concept = (IConceptDescription) conceptAtom.getDescription();
		IRoleDescription role = (IRoleDescription) roleAtom.getDescription();
		IConceptDescription existential = BASIC.createExistentialConcept(role, concept);
		ITuple tuple = BASIC.createTuple(roleAtom.getTuple().get(0));
		
		handleAtom(BASIC.createAtom(existential, tuple));
	}

	@Override
	public void visitImplication(Implication expr) {
		visitImplication(expr.getRightOperand(), expr.getLeftOperand());
	}

	@Override
	public void visitInverseImplication(InverseImplication expr) {
		visitImplication(expr.getLeftOperand(), expr.getRightOperand());
	}
	
	@Override
	public void visitLogicProgrammingRule(LogicProgrammingRule expr) {
		visitImplication(expr.getLeftOperand(), expr.getRightOperand());
	}

	private void visitImplication(LogicalExpression headExpression, LogicalExpression bodyExpression) {
		setInRule();
		
		assert atomStack.isEmpty();
		
		headExpression.accept(this);
		List<IAtom> head = new ArrayList<IAtom>(atomStack);
		
		atomStack.clear();
	
		bodyExpression.accept(this);
		List<IAtom> body = new ArrayList<IAtom>(atomStack);
		
		atomStack.clear();
		
		assert head.size() > 0;
		assert body.size() > 0;
		
		rules.add(BASIC.createRule(head, body));
		
		clearInRule();
	}

	/**
	 * MembershipMolecule Represents a molecule of the form "a memberOf b".
	 * </p>
	 * Creates and pushes an Atom <code>b(a)</code>.
	 * </p>
	 * π(X1 memberOf id2) | id2(X1)
	 * 
	 * @see org.omwg.logicalexpression.LogicalExpressionVisitor#visitMemberShipMolecule(org.omwg.logicalexpression.MembershipMolecule)
	 */
	@Override
	public void visitMemberShipMolecule(MembershipMolecule expr) {
		expectedType = Type.TERM;
		expr.getLeftParameter().accept(this);
		ITerm x1 = popTerm();
		
		expectedType = Type.CONCEPT;
		expr.getRightParameter().accept(this);
		IDescription id2 = popDescription();
		
		handleAtom(BASIC.createAtom(id2, BASIC.createTuple(x1)));
	}

	@Override
	public void visitNegation(Negation expr) {
		throw new UnsupportedOperationException("Negation is not supported by ELP");
	}

	@Override
	public void visitNegationAsFailure(NegationAsFailure expr) {
		throw new UnsupportedOperationException("NegationAsFailure is not supported by ELP");
	}

	/**
	 * SubConceptMolecule Represents a Molecule of the Form "a subConceptOf b".
	 * </p>
	 * π(id1 subConceptOf id2) | id2(x) ← id1(x)
	 * 
	 * @see org.omwg.logicalexpression.LogicalExpressionVisitor#visitSubConceptMolecule(org.omwg.logicalexpression.SubConceptMolecule)
	 */
	@Override
	public void visitSubConceptMolecule(SubConceptMolecule expr) {
		setInRule();
		
		expectedType = Type.CONCEPT;
		expr.getLeftParameter().accept(this);
		IDescription id1_subConcept = popDescription();
		
		expectedType = Type.CONCEPT;
		expr.getRightParameter().accept(this);
		IDescription id2_superConcept = popDescription();
		
		List<IAtom> head = Collections.singletonList(BASIC.createAtom(id2_superConcept, tupleX));
		List<IAtom> body = Collections.singletonList(BASIC.createAtom(id1_subConcept, tupleX));
		
		rules.add(BASIC.createRule(head, body));
		
		clearInRule();
	}

	@Override
	public void visitTruthValue(TruthValue expr) { // FIXME is x as variable ok? 
		if (expr.getValue())
			handleAtom(BASIC.createAtom(Vocabulary.topConcept, tupleX));
		else
			handleAtom(BASIC.createAtom(Vocabulary.bottomConcept, tupleX));
	}

	@Override
	public void visitUniversalQuantification(UniversalQuantification expr) {
		throw new UnsupportedOperationException("UniversalQuantification is not supported by ELP");
	}

	/* *******************************************
	 * Helpers 
	 * *******************************************/
	
	private String asString(Term term) {
		return term.toString();
	}

	private IAtomicConcept getOrCreateConcept(Term concept) {
		IAtomicConcept ellyConcept = (IAtomicConcept) wsml2EllyCache.get(concept);
		if (ellyConcept == null) {
			ellyConcept = BASIC.createAtomicConcept(asString(concept));
			wsml2EllyCache.put(concept, ellyConcept);
		}
		
		return ellyConcept;
	}

	
	private IAtomicRole getOrCreateRole(Term role) {
		IAtomicRole ellyRole = (IAtomicRole) wsml2EllyCache.get(role);
		if (ellyRole == null) {
			ellyRole = BASIC.createAtomicRole(asString(role));
			wsml2EllyCache.put(role, ellyRole);
		}
		
		return ellyRole;
	}

	private IIndividual getOrCreateIndividual(Term term) {
		IIndividual ellyIndividual = (IIndividual) wsml2EllyCache.get(term);
		if (ellyIndividual == null) {
			ellyIndividual = TERM.createIndividual(asString(term));
			wsml2EllyCache.put(term, ellyIndividual);
		}
		
		return ellyIndividual;			
	}

	private IVariable getOrCreateVariable(Variable variable) {
		IVariable ellyVariable = (IVariable) wsml2EllyCache.get(variable);
		if (ellyVariable == null) {
			ellyVariable = TERM.createVariable(asString(variable), variable.isSafe());
			wsml2EllyCache.put(variable, ellyVariable);
		}
		
		return ellyVariable;			
	}
	
	private ITerm pushTerm(ITerm term) {
		return termStack.push(term);
	}

	private ITerm popTerm() {
		return termStack.pop();
	}

	private IDescription pushDescription(IDescription description) {
		return descriptionStack.push(description);
	}

	private IDescription popDescription() {
		return descriptionStack.pop();
	}

	private IAtom pushAtom(IAtom atom) {
		return atomStack.push(atom);
	}

	private IAtom popAtom() {
		return atomStack.pop();
	}

	
	private void setInRule() {
		inRule = true;
	}

	private void clearInRule() {
		inRule = false;
	}
	
	private boolean isInRule() {
		return inRule;
	}

	private void handleAtom(IAtom atom) {
		assert atom != null;
		
		if (isInRule()) {
			pushAtom(atom);
		} else {
			rules.add(BASIC.createFact(atom));
		}
	}

	private static class NameFactory extends AbstractFactory {
		private static final String anonymousNamePrefix = ellyPrefix + "__anonymous__";

		private NameFactory(){}

		public static String getAnonymousName(byte anonymousNumber) {
			return anonymousNamePrefix + anonymousNumber;
		}

		public static String getAnonymousName() {
			return anonymousNamePrefix + getCounter();
		}
	}
}
