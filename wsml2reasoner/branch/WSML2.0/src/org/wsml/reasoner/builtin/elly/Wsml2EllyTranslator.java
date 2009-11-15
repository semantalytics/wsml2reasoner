package org.wsml.reasoner.builtin.elly;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

import org.omwg.logicalexpression.Atom;
import org.omwg.logicalexpression.AttributeConstraintMolecule;
import org.omwg.logicalexpression.AttributeInferenceMolecule;
import org.omwg.logicalexpression.AttributeValueMolecule;
import org.omwg.logicalexpression.BuiltInAtom;
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
import org.sti2.elly.DataType;
import org.sti2.elly.Vocabulary;
import org.sti2.elly.api.basics.IAtom;
import org.sti2.elly.api.basics.IAtomicConcept;
import org.sti2.elly.api.basics.IAtomicRole;
import org.sti2.elly.api.basics.IConceptDescription;
import org.sti2.elly.api.basics.IDescription;
import org.sti2.elly.api.basics.IRoleDescription;
import org.sti2.elly.api.basics.IRule;
import org.sti2.elly.api.basics.ITuple;
import org.sti2.elly.api.factory.IBasicFactory;
import org.sti2.elly.api.factory.IBuiltinsFactory;
import org.sti2.elly.api.factory.ITermFactory;
import org.sti2.elly.api.terms.IConcreteTerm;
import org.sti2.elly.api.terms.IIndividual;
import org.sti2.elly.api.terms.ITerm;
import org.sti2.elly.api.terms.IVariable;
import org.sti2.elly.basics.BasicFactory;
import org.sti2.elly.basics.BuiltinsFactory;
import org.sti2.elly.terms.TermFactory;
import org.sti2.elly.transformation.factory.AbstractFactory;
import org.wsmo.common.BuiltIn;
import org.wsmo.common.IRI;
import org.wsmo.common.NumberedAnonymousID;
import org.wsmo.common.UnnumberedAnonymousID;

public class Wsml2EllyTranslator implements LogicalExpressionVisitor, TermVisitor {

	/**
	 * This enum allows to specify the expected type. When visiting a term, the type must be set such that a concept,
	 * role, or term can be created.
	 */
	private enum Type {
		CONCEPT("Concept Description"), DATA_TYPE("Datatype"), ROLE("Role Description"), TERM("Term");

		private String name;

		Type(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}
	
	private static IBuiltinsFactory BUILTIN = BuiltinsFactory.getInstance();
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
	private final Stack<Queue<IAtom>> atomListStack;

	private final Map<Object, Object> wsml2EllyCache;

	private Type expectedType;

	public Wsml2EllyTranslator(List<IRule> rules) {
		if (rules == null)
			throw new IllegalArgumentException("rules must not be null");
		this.rules = rules;

		termStack = new Stack<ITerm>();
		descriptionStack = new Stack<IDescription>();
		atomListStack = new Stack<Queue<IAtom>>();

		wsml2EllyCache = new HashMap<Object, Object>();
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

		IConcreteTerm dataValue = Wsml2EllyDataValueTranslator.convertWsmo4jDataValueToEllyTerm(t);
		pushTerm(dataValue);
	}

	@Override
	public void visit(ComplexDataValue t) {
		if (expectedType != Type.TERM)
			throw new RuntimeException("Unable to create a " + expectedType + " from Term " + t);

		IConcreteTerm dataValue = Wsml2EllyDataValueTranslator.convertWsmo4jDataValueToEllyTerm(t);
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
			DataType dataType = DataType.asDataType(t.toString());
			if (dataType == null) {
				throw new RuntimeException("DataType " + t.toString() + " not supported");
			}
			IDescription dataTypeConcept = dataType.asConcept();
			pushDescription(dataTypeConcept);
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
		// must be an Built-in Atom
		if (expr instanceof BuiltInAtom) {
			IAtom builtin = translateBuiltIn((BuiltInAtom) expr);
			
			if (emptyStack()) {
				rules.add(BASIC.createFact(builtin));
			} else {
				addAtom(builtin);
			}
		} else {
			throw new UnsupportedOperationException("Atoms are not supported by ELP");
		}
	}

	/**
	 * AttributeConstraintMolecule represents a constraining attribute molecule (e.g. human[age ofType _integer)).
	 * Creates and adds a rule {@code DT(y) :- C(x) and r(x,y)} from {@code C[r ofType DT]}. </p> π(id1[id2 ofType dt])
	 * | dt(y) ← id1(x) ∧ id2(x,y)
	 * 
	 * @see org.omwg.logicalexpression.LogicalExpressionVisitor#visitAttributeConstraintMolecule(org.omwg.logicalexpression.AttributeConstraintMolecule)
	 */
	@Override
	public void visitAttributeConstraintMolecule(AttributeConstraintMolecule expr) {
		assert emptyStack();

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
	}

	/**
	 * AttributeInferenceMolecule Represents an inferring attribute molecule (e.g. human[ancestor impliesType human]).
	 * Creates and adds a rule {@code DT(y) :- C(x) and r(x,y)} from {@code C[r impliesType DT]}. </p> π(id1[id2
	 * impliesType id3]) | id3(y) ← id1(x) ∧ id2(x,y)
	 * 
	 * @see org.omwg.logicalexpression.LogicalExpressionVisitor#visitAttributeInferenceMolecule(org.omwg.logicalexpression.AttributeInferenceMolecule)
	 */
	@Override
	public void visitAttributeInferenceMolecule(AttributeInferenceMolecule expr) {
		assert emptyStack();

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
	}

	/**
	 * AttributeValueMolecule Represents a value attribute molecule (e.g. human[age hasValue 4]). </p> Creates and adds
	 * a fact {@code id2(X1,X2).} </p> π(X1[id2 hasValue X2]) | id2(X1,X2)
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
		if ((x1 instanceof IVariable) && (x1.equals(x2))) { // Self Restriction
			ITuple tuple = BASIC.createTuple(x1);
			atom = BASIC.createAtom(BASIC.createSelfRestriction(id2), tuple);
		} else {
			ITuple tuple = BASIC.createTuple(x1, x2);
			atom = BASIC.createAtom(id2, tuple);
		}
		
		if (emptyStack()) {
			rules.add(BASIC.createFact(atom));
		} else {
			addAtom(atom);
		}
	}

	/**
	 * CompoundMolecule Represents a compound molecule which is a container for simple molecules E.g.,
	 * "x subConceptOf {y,z}" or "a[b hasValue c] memberOf d". </p> Visits all {@link CompoundMolecule#listOperands()}
	 * Molecules, since all other {@code list} methods are just filters on all operands.
	 * 
	 * 
	 * @see org.omwg.logicalexpression.LogicalExpressionVisitor#visitCompoundMolecule(org.omwg.logicalexpression.CompoundMolecule)
	 */
	@Override
	public void visitCompoundMolecule(CompoundMolecule expr) {
		pushAtomList();

		for (LogicalExpression molecule : expr.listOperands()) {
			molecule.accept(this);
		}
		
		if (emptyStack()) {
			rules.add(BASIC.createFact(new ArrayList<IAtom>(popAtomList())));
		} else {
			peekAtomList().addAll(popAtomList());
		}
	}

	/**
	 * Conjunction represents specific kind of <code>Binary</code>. A conjunction whose operator is an and. </p> π(lexpr
	 * and rexpr) | π(lexpr) ∧ π(rexpr) </p> Both Operands are visited and the resulting Descriptions are left on the
	 * {@link #atomStack}.
	 * 
	 * @see org.omwg.logicalexpression.LogicalExpressionVisitor#visitConjunction(org.omwg.logicalexpression.Conjunction)
	 */
	@Override
	public void visitConjunction(Conjunction expr) {
		pushAtomList();
		expr.getLeftOperand().accept(this);
		pushAtomList();
		expr.getRightOperand().accept(this);
		// Check if this is a Concept or Role Intersection 
		Queue<IAtom> atomListR = popAtomList();
		Queue<IAtom> atomListL = popAtomList();
		Queue<IAtom> atomList = new LinkedList<IAtom>();
	
		
		// Try to create a concept/role intersection
		// this is possible if the right list is of size 1
		// and the last item of the left list matches against
		// the right one
		boolean createIntersection = true;
		
		createIntersection &= atomListR.size() == 1;
		
		// set atomL to the last atom of left list
		// since there are generally very little items
		// in the list iterating may be faster than
		// creating new list, asking for size and getting last element
		IAtom atomL = null;
		for (IAtom atom : atomListL) {
			atomL = atom;
		}
		IAtom atomR = atomListR.element();
		
		createIntersection &= atomL.getTuple().equals(atomR.getTuple());
		
		if (!createIntersection) {
			// flatten atom lists
			atomList.addAll(atomListL);
			atomList.addAll(atomListR);
		} else {
			// add all (but last) elements of left list
			while (atomListL.size() > 1) {
				atomList.add(atomListL.remove());
			}
			
			// safe since atomListL is not used any more
			assert atomListL.remove().equals(atomL);
			
			if (atomL.getDescription() instanceof IConceptDescription) { // since tuple arity matches they have to be of equal type
				// Create Concept Intersection
				IDescription conceptIntersection = BASIC.createIntersectionConcept((IConceptDescription)
						atomL.getDescription(), (IConceptDescription) atomR.getDescription());

				atomList.add(BASIC.createAtom(conceptIntersection, atomL.getTuple()));
			} else if (atomL.getDescription() instanceof IRoleDescription) { // since tuple arity matches they have to be of equal type
				// Create Concept Intersection
				IDescription roleIntersection = BASIC.createIntersectionRole((IRoleDescription)
						atomL.getDescription(), (IRoleDescription) atomR.getDescription());

				atomList.add(BASIC.createAtom(roleIntersection, atomL.getTuple()));
			}
		}
		
		if (emptyStack()) {
			rules.add(BASIC.createFact(new ArrayList<IAtom>(atomList)));
		} else {
			peekAtomList().addAll(atomList);
		}
	}

	@Override
	public void visitConstraint(Constraint expr) {
		assert emptyStack();

		pushAtomList();
		expr.getOperand().accept(this);
		Queue<IAtom> atomList = popAtomList();
		
		assert emptyStack();
		assert atomList.size() > 0;

		rules.add(BASIC.createIntegrityConstraint(new ArrayList<IAtom>(atomList)));
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
		pushAtomList();
		expr.getOperand().accept(this);
		Queue<IAtom> atomList = popAtomList();
		
		IAtom roleAtom = atomList.remove();
		IAtom conceptAtom = atomList.remove();
		
		assert atomList.isEmpty();

		if (!(conceptAtom.getDescription() instanceof IConceptDescription))
			throw new IllegalArgumentException(
					"ExistentialQuantification "
							+ expr.toString()
							+ " must be of syntax exists \\phi (G and F_1) with \\phi \\in V_V, G a b-molecule, F_1 an a-molecule");
		if (!(roleAtom.getDescription() instanceof IRoleDescription))
			throw new IllegalArgumentException(
					"ExistentialQuantification "
							+ expr.toString()
							+ " must be of syntax exists \\phi (G and F_1) with \\phi \\in V_V, G a b-molecule, F_1 an a-molecule");
		if (!(conceptAtom.getTuple().get(0).equals(roleAtom.getTuple().get(1))))
			throw new IllegalArgumentException(
					"ExistentialQuantification "
							+ expr.toString()
							+ " must be of syntax exists \\phi (G and F_1) with \\phi \\in V_V, G a b-molecule, F_1 an a-molecule");
		if (expr.listVariables().size() != 1)
			throw new IllegalArgumentException(
					"ExistentialQuantification "
							+ expr.toString()
							+ " must be of syntax exists \\phi (G and F_1) with \\phi \\in V_V, G a b-molecule, F_1 an a-molecule");

		IConceptDescription concept = (IConceptDescription) conceptAtom.getDescription();
		IRoleDescription role = (IRoleDescription) roleAtom.getDescription();
		IConceptDescription existential = BASIC.createExistentialConcept(role, concept);
		ITuple tuple = BASIC.createTuple(roleAtom.getTuple().get(0));

		IAtom atom = BASIC.createAtom(existential, tuple);
		if (emptyStack()) {
			rules.add(BASIC.createFact(atom));
		} else {
			addAtom(atom);
		}
		
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
		assert emptyStack();

		pushAtomList();
		headExpression.accept(this);
		pushAtomList();
		bodyExpression.accept(this);
		
		
		List<IAtom> body = new ArrayList<IAtom>(popAtomList());
		List<IAtom> head = new ArrayList<IAtom>(popAtomList());

		assert head.size() > 0;
		assert body.size() > 0;
		assert emptyStack();

		rules.add(BASIC.createRule(head, body));
	}

	/**
	 * MembershipMolecule Represents a molecule of the form "a memberOf b". </p> Creates and pushes an Atom
	 * <code>b(a)</code>. </p> π(X1 memberOf id2) | id2(X1)
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

		IAtom atom = BASIC.createAtom(id2, BASIC.createTuple(x1));
		
		if (emptyStack()) {
			rules.add(BASIC.createFact(atom));
		} else {
			addAtom(atom);
		}
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
	 * SubConceptMolecule Represents a Molecule of the Form "a subConceptOf b". </p> π(id1 subConceptOf id2) | id2(x) ←
	 * id1(x)
	 * 
	 * @see org.omwg.logicalexpression.LogicalExpressionVisitor#visitSubConceptMolecule(org.omwg.logicalexpression.SubConceptMolecule)
	 */
	@Override
	public void visitSubConceptMolecule(SubConceptMolecule expr) {
		assert emptyStack();
		
		expectedType = Type.CONCEPT;
		expr.getLeftParameter().accept(this);
		IDescription id1_subConcept = popDescription();

		expectedType = Type.CONCEPT;
		expr.getRightParameter().accept(this);
		IDescription id2_superConcept = popDescription();

		List<IAtom> head = Collections.singletonList(BASIC.createAtom(id2_superConcept, tupleX));
		List<IAtom> body = Collections.singletonList(BASIC.createAtom(id1_subConcept, tupleX));

		rules.add(BASIC.createRule(head, body));
	}

	@Override
	public void visitTruthValue(TruthValue expr) { 
		IAtom atom;
		if (expr.getValue())
			atom = BASIC.createAtom(Vocabulary.topConcept, tupleX);
		else
			atom = BASIC.createAtom(Vocabulary.bottomConcept, tupleX);
		
		if (emptyStack()) {
			rules.add(BASIC.createFact(atom));
		} else {
			addAtom(atom);
		}
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

	private boolean addAtom(IAtom atom) {
		return atomListStack.peek().add(atom);
	}

	private Queue<IAtom> pushAtomList() {
		return atomListStack.push(new LinkedList<IAtom>());
	}

	private Queue<IAtom> popAtomList() {
		return atomListStack.pop();
	}

	private Queue<IAtom> peekAtomList() {
		return atomListStack.peek();
	}

	private boolean emptyStack() {
		return atomListStack.isEmpty();
	}

	private static class NameFactory extends AbstractFactory {
		private static final String anonymousNamePrefix = ellyPrefix + "__anonymous__";

		private NameFactory() {
		}

		public static String getAnonymousName(byte anonymousNumber) {
			return anonymousNamePrefix + anonymousNumber;
		}

		public static String getAnonymousName() {
			return anonymousNamePrefix + getCounter();
		}
	}

	/* **************
	 * Built Ins
	 * **************/
	
	private IAtom translateBuiltIn(BuiltInAtom builtInAtom) {
		BuiltIn builtIn = BuiltIn.from(builtInAtom.getIdentifier().toString());
		expectedType = Type.TERM;
		
		List<ITerm> terms = new ArrayList<ITerm>();
		for (Term term : builtInAtom.listParameters()) {
			term.accept(this);
			terms.add(popTerm());
		}
		
		// handle individual equality
		// makes no difference if it is equality of concrete terms
		if (builtIn.equals(BuiltIn.EQUAL)) {
			return BASIC.createAtom(Vocabulary.equal, terms.get(0), terms.get(1));
		}
		
		// for some Built-ins the list must be sorted in a different order (first gets last)
		List<ITerm> sortedTerms = sortListForIRIS(terms);
		
		assert terms.size() >= 1;
		assert terms.size() <= 2;
    	
		switch (builtIn) {
		case EQUAL:
		case NUMERIC_EQUAL:
		case STRING_EQUAL:
		case DATE_EQUAL:
		case TIME_EQUAL:
		case DATETIME_EQUAL:
		case GYEAR_EQUAL:
		case GYEARMONTH_EQUAL:
		case GMONTHDAY_EQUAL:
		case GDAY_EQUAL:
		case GMONTH_EQUAL:
		case DURATION_EQUAL:
			return BUILTIN.createEqual(terms.get(0), terms.get(1));

			// check whether the predicate is a builtin
		case INEQUAL:
		case NUMERIC_INEQUAL:
		case STRING_INEQUAL:
		case DATE_INEQUAL:
		case TIME_INEQUAL:
		case DATETIME_INEQUAL:
			return BUILTIN.createUnequal(terms.get(0), terms.get(1));

		case LESS_THAN:
		case DATE_LESS_THAN:
		case TIME_LESS_THAN:
		case DATETIME_LESS_THAN:
		case DAYTIMEDURATION_LESS_THAN:
		case YEARMONTHDURATION_LESS_THAN:
			return BUILTIN.createLess(terms.get(0), terms.get(1));
		
		case LESS_EQUAL:
			return BUILTIN.createLessEqual(terms.get(0), terms.get(1));
		
		case GREATER_THAN:
		case DATE_GREATER_THAN:
		case TIME_GREATER_THAN:
		case DATETIME_GREATER_THAN:
		case DAYTIMEDURATION_GREATER_THAN:
		case YEARMONTHDURATION_GREATER_THAN:
			return BUILTIN.createGreater(terms.get(0), terms.get(1));
        case GREATER_EQUAL:
            return BUILTIN.createGreaterEqual(terms.get(0), terms.get(1));
        case IS_DATATYPE:
        	return BUILTIN.createIsDatatype(terms.get(0), terms.get(1));
        case IS_NOT_DATATYPE:
        	return BUILTIN.createIsNotDatatype(terms.get(0), terms.get(1));
//        case HAS_DATATYPE: // TODO currently not supported
//        	return BUILTIN.createHasDatatype(toArray(terms));
        case STRING_LENGTH:
        	return BUILTIN.createStringLength(sortedTerms.get(0), sortedTerms.get(1));
        case STRING_TO_UPPER:
        	return BUILTIN.createStringToUpper(sortedTerms.get(0), sortedTerms.get(1));
        case STRING_TO_LOWER:
        	return BUILTIN.createStringToLower(sortedTerms.get(0), sortedTerms.get(1));
        case STRING_URI_ENCODE:
        	return BUILTIN.createStringUriEncode(sortedTerms.get(0), sortedTerms.get(1));
        case STRING_IRI_TO_URI:
        	return BUILTIN.createStringIriToUri(sortedTerms.get(0), sortedTerms.get(1));
        case STRING_ESCAPE_HTML_URI:
        	return BUILTIN.createStringEscapeHtmlUri(sortedTerms.get(0), sortedTerms.get(1));
        case STRING_CONTAINS:
        	return BUILTIN.createStringContains(sortedTerms.get(0), sortedTerms.get(1));
        case STRING_STARTS_WITH:
        	return BUILTIN.createStringStartsWith(terms.get(0), terms.get(1));
        case STRING_ENDS_WITH:
        	return BUILTIN.createStringEndsWith(terms.get(0), terms.get(1));
        case STRING_MATCHES:
        	return BUILTIN.createStringMatches(terms.get(0), terms.get(1));
        case YEAR_PART:
        	return BUILTIN.createYearPart(sortedTerms.get(0), sortedTerms.get(1));
        case MONTH_PART:
        	return BUILTIN.createMonthPart(sortedTerms.get(0), sortedTerms.get(1));
        case DAY_PART:
        	return BUILTIN.createDayPart(sortedTerms.get(0), sortedTerms.get(1));
        case HOUR_PART:
        	return BUILTIN.createHourPart(sortedTerms.get(0), sortedTerms.get(1));
        case MINUTE_PART:
        	return BUILTIN.createMinutePart(sortedTerms.get(0), sortedTerms.get(1));
        case SECOND_PART:
        	return BUILTIN.createSecondPart(sortedTerms.get(0), sortedTerms.get(1));
        case TIMEZONE_PART:
        	return BUILTIN.createTimezonePart(sortedTerms.get(0), sortedTerms.get(1));
        case TEXT_FROM_STRING:
        	return BUILTIN.createTextFromString(sortedTerms.get(0), sortedTerms.get(1));
        case STRING_FROM_TEXT:
        	return BUILTIN.createStringFromText(sortedTerms.get(0), sortedTerms.get(1));
        case LANG_FROM_TEXT:
        	return BUILTIN.createLangFromText(sortedTerms.get(0), sortedTerms.get(1));
        case TO_BASE64:
        	return BUILTIN.createToBase64Binary(sortedTerms.get(0), sortedTerms.get(1));
        case TO_BOOLEAN:
        	return BUILTIN.createToBoolean(sortedTerms.get(0), sortedTerms.get(1));
        case TO_DATE:
        	return BUILTIN.createToDate(sortedTerms.get(0), sortedTerms.get(1));
        case TO_DATETIME:
        	return BUILTIN.createToDateTime(sortedTerms.get(0), sortedTerms.get(1));
        case TO_DAYTIMEDURATION:
        	return BUILTIN.createToDayTimeDuration(sortedTerms.get(0), sortedTerms.get(1));
        case TO_DECIMAL:
        	return BUILTIN.createToDecimal(sortedTerms.get(0), sortedTerms.get(1));
        case TO_DOUBLE:
        	return BUILTIN.createToDouble(sortedTerms.get(0), sortedTerms.get(1));
        case TO_DURATION:
        	return BUILTIN.createToDuration(sortedTerms.get(0), sortedTerms.get(1));
        case TO_FLOAT:
        	return BUILTIN.createToFloat(sortedTerms.get(0), sortedTerms.get(1));
        case TO_GDAY:
        	return BUILTIN.createToGDay(sortedTerms.get(0), sortedTerms.get(1));
        case TO_GMONTH:
        	return BUILTIN.createToGMonth(sortedTerms.get(0), sortedTerms.get(1));
        case TO_GMONTHDAY:
        	return BUILTIN.createToGMonthDay(sortedTerms.get(0), sortedTerms.get(1));
        case TO_GYEAR:
        	return BUILTIN.createToGYear(sortedTerms.get(0), sortedTerms.get(1));
        case TO_GYEARMONTH:
        	return BUILTIN.createToGYearMonth(sortedTerms.get(0), sortedTerms.get(1));
        case TO_HEXBINARY:
        	return BUILTIN.createToHexBinary(sortedTerms.get(0), sortedTerms.get(1));
        case TO_INTEGER:
        	return BUILTIN.createToInteger(sortedTerms.get(0), sortedTerms.get(1));
        case TO_IRI:
        	return BUILTIN.createToIRI(sortedTerms.get(0), sortedTerms.get(1));
        case TO_STRING:
        	return BUILTIN.createToString(sortedTerms.get(0), sortedTerms.get(1));
        case TO_TEXT:
        	return BUILTIN.createToText(sortedTerms.get(0), sortedTerms.get(1));
        case TO_TIME:
        	return BUILTIN.createToTime(sortedTerms.get(0), sortedTerms.get(1));
        case TO_XMLLITERAL:
        	return BUILTIN.createToXMLLiteral(sortedTerms.get(0), sortedTerms.get(1));
        case TO_YEARMONTHDURATION:
        	return BUILTIN.createToYearMonthDuration(sortedTerms.get(0), sortedTerms.get(1));
        	
        // TODO support for following? Add to WSMO4J?
//        // the is-datatype-things
//        case IS_BASE64BINARY:
//        	return BUILTIN.createIsBase64Binary(terms.get(0));
//        case IS_BOOLEAN:
//        	return BUILTIN.createIsBoolean(terms.get(0));
//        case IS_DATE:
//        	return BUILTIN.createIsDate(terms.get(0));
//        case IS_DATETIME:
//        	return BUILTIN.createIsDateTime(terms.get(0));
//        case IS_DAYTIME_DURATION:
//        	return BUILTIN.createIsDayTimeDuration(terms.get(0));
//        case IS_DECIMAL:
//        	return BUILTIN.createIsDecimal(terms.get(0));
//        case IS_DOUBLE:
//        	return BUILTIN.createIsDouble(terms.get(0));
//        case IS_DURATION:
//        	return BUILTIN.createIsDuration(terms.get(0));
//        case IS_FLOAT:
//        	return BUILTIN.createIsFloat(terms.get(0));
//        case IS_GDAY:
//        	return BUILTIN.createIsGDay(terms.get(0));
//        case IS_GMONTH:
//        	return BUILTIN.createIsGMonth(terms.get(0));
//        case IS_GMONTHDAY:
//        	return BUILTIN.createIsGMonthDay(terms.get(0));
//        case IS_GYEAR:
//        	return BUILTIN.createIsGYear(terms.get(0));
//        case IS_GYEARMONTH:
//        	return BUILTIN.createIsGYearMonth(terms.get(0));
//        case IS_HEXBINARY:
//        	return BUILTIN.createIsHexBinary(terms.get(0));
//        
//        case IS_INTEGER:
//        	return BUILTIN.createIsInteger(terms.get(0));
//        
//        case IS_IRI:
//        	return BUILTIN.createIsIRI(terms.get(0));
//        
//        case IS_STRING:
//        	return BUILTIN.createIsString(terms.get(0));
//        
//        case IS_TEXT:
//        	return BUILTIN.createIsText(terms.get(0));
//        
//        case IS_TIME:
//        	return BUILTIN.createIsTime(terms.get(0));
//        
//        case IS_XML_LITERAL:
//        	return BUILTIN.createIsXMLLiteral(terms.get(0));
//        
//        case IS_YEAR_MONTH_DURATION:
//        	return BUILTIN.createIsYearMonthDuration(terms.get(0));
//        
//        else if( ! headLiteral && sym.equals( WSML2DatalogTransformer.PRED_MEMBER_OF ) ) {
//        	// Special case! Look for wsml-member-of( ?x, wsml#<datatype> )
//        	// and change it to one of IRIS's IS_XXXXX() built-ins
//        	
//        	// We only do this for rule body predicates
//        	if( terms.size() == 2 ) {
//        		ITerm t0 = terms.get(0);
//        		ITerm t1 = terms.get(1);
//        		
////        		if( t0 instanceof IVariable && t1 instanceof IIri ) {
//           		if( t1 instanceof IIri ) {
//        			IIri iri = (IIri) t1;
//        			String type = iri.getValue();
//        			if( type.equals( WsmlDataType.WSML_STRING ) || type.equals( XmlSchemaDataType.XSD_STRING ) )
//        				return new IsStringBuiltin( t0 );
//        			else if( type.equals( WsmlDataType.WSML_DECIMAL ) || type.equals( XmlSchemaDataType.XSD_DECIMAL ) )
//        				return new IsDecimalBuiltin( t0 );
//        			else if( type.equals( WsmlDataType.WSML_DOUBLE ) || type.equals( XmlSchemaDataType.XSD_DOUBLE ) )
//        				return new IsDoubleBuiltin( t0 );
//        			else if( type.equals( WsmlDataType.WSML_BOOLEAN ) || type.equals( XmlSchemaDataType.XSD_BOOLEAN ) )
//        				return new IsBooleanBuiltin( t0 );
//        			else if( type.equals( WsmlDataType.WSML_INTEGER ) || type.equals( XmlSchemaDataType.XSD_INTEGER ) )
//        				return new IsIntegerBuiltin( t0 );
//        			else if( type.equals( WsmlDataType.WSML_BASE64BINARY ) || type.equals( XmlSchemaDataType.XSD_BASE64BINARY ) )
//        				return new IsBase64BinaryBuiltin( t0 );
//        			else if( type.equals( WsmlDataType.WSML_DATE ) || type.equals( XmlSchemaDataType.XSD_DATE ) )
//        				return new IsDateBuiltin( t0 );
//        			else if( type.equals( WsmlDataType.WSML_DATETIME ) || type.equals( XmlSchemaDataType.XSD_DATETIME ) )
//        				return new IsDateTimeBuiltin( t0 );
//        			else if( type.equals( WsmlDataType.WSML_DURATION ) || type.equals( XmlSchemaDataType.XSD_DURATION ) )
//        				return new IsDurationBuiltin( t0 );
//        			else if( type.equals( WsmlDataType.WSML_FLOAT ) || type.equals( XmlSchemaDataType.XSD_FLOAT ) )
//        				return new IsFloatBuiltin( t0 );
//        			else if( type.equals( WsmlDataType.WSML_GDAY ) || type.equals( XmlSchemaDataType.XSD_GDAY ) )
//        				return new IsGDayBuiltin( t0 );
//        			else if( type.equals( WsmlDataType.WSML_GMONTH ) || type.equals( XmlSchemaDataType.XSD_GMONTH ) )
//        				return new IsGMonthBuiltin( t0 );
//        			else if( type.equals( WsmlDataType.WSML_GMONTHDAY ) || type.equals( XmlSchemaDataType.XSD_GMONTHDAY ) )
//        				return new IsGMonthDayBuiltin( t0 );
//        			else if( type.equals( WsmlDataType.WSML_GYEAR ) || type.equals( XmlSchemaDataType.XSD_GYEAR ) )
//        				return new IsGYearBuiltin( t0 );
//        			else if( type.equals( WsmlDataType.WSML_GYEARMONTH ) || type.equals( XmlSchemaDataType.XSD_GYEARMONTH ) )
//        				return new IsGYearMonthBuiltin( t0 );
//        			else if( type.equals( WsmlDataType.WSML_HEXBINARY ) || type.equals( XmlSchemaDataType.XSD_HEXBINARY ) )
//        				return new IsHexBinaryBuiltin( t0 );
//        			else if( type.equals( WsmlDataType.WSML_TIME ) || type.equals( XmlSchemaDataType.XSD_TIME ) )
//        				return new IsTimeBuiltin( t0 );
//        			// new XSDs
//        			else if( type.equals( XmlSchemaDataType.XSD_YEARMONTHDURATION ) )  
//        				return new IsYearMonthDurationBuiltin( t0 );
//        			else if( type.equals( XmlSchemaDataType.XSD_DAYTIMEDURATION ) )  
//        				return new IsDayTimeDurationBuiltin( t0 );
//        			// RDF 
//        			else if( type.equals( RDFDataType.RDF_TEXT ) )  {
//        				return new IsTextBuiltin( t0 );
//        			}
//        			else if( type.equals( RDFDataType.RDF_XMLLITERAL ) )  
//        				return new IsXMLLiteralBuiltin( t0 );
//        			
//        		}
//        	}
//        	// If none of these then drop through to normal atom processing.
//        }
        
		default:
			// how is true handled?
//			String sym = builtInAtom.getIdentifier().toString();
//	        if (sym.equals(Constants.TRUE) || sym.equals(Constants.UNIV_TRUE) ){
//	        	return BUILTIN.createTrue();
//	        }
//	        else if (sym.equals(Constants.FALSE) || sym.equals(Constants.UNIV_FALSE) ){
//	        	return BUILTIN.createFalse();
//	        }

			
			throw new IllegalArgumentException("Unknown Built-In");
		}
	}

	/**
	 * Changes the order of the terms for IRIS. The first entry becomes the last one.
	 * 
	 * @param terms
	 *            a list of terms in normal order.
	 * @return a list of terms where the first entry is the last one.
	 */
	private static List<ITerm> sortListForIRIS(List<ITerm> terms) {
		assert terms != null;
		List<ITerm> newTerms = new ArrayList<ITerm>();
		ITerm first = terms.get(0);
		for (int i = 1; i < terms.size(); i++) {
			newTerms.add(terms.get(i));
		}
		newTerms.add(first);
		return newTerms;
	}

}
