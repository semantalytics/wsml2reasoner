package org.wsml.reasoner;

import static org.wsml.reasoner.TransformerPredicates.PRED_DIRECT_CONCEPT;
import static org.wsml.reasoner.TransformerPredicates.PRED_DIRECT_SUBCONCEPT;
import static org.wsml.reasoner.TransformerPredicates.PRED_HAS_VALUE;
import static org.wsml.reasoner.TransformerPredicates.PRED_IMPLIES_TYPE;
import static org.wsml.reasoner.TransformerPredicates.PRED_INDIRECT_CONCEPT;
import static org.wsml.reasoner.TransformerPredicates.PRED_INDIRECT_SUBCONCEPT;
import static org.wsml.reasoner.TransformerPredicates.PRED_KNOWN_CONCEPT;
import static org.wsml.reasoner.TransformerPredicates.PRED_MEMBER_OF;
import static org.wsml.reasoner.TransformerPredicates.PRED_OF_TYPE;
import static org.wsml.reasoner.TransformerPredicates.PRED_SUB_CONCEPT_OF;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.omwg.logicalexpression.Atom;
import org.omwg.logicalexpression.AtomicExpression;
import org.omwg.logicalexpression.AttributeConstraintMolecule;
import org.omwg.logicalexpression.AttributeInferenceMolecule;
import org.omwg.logicalexpression.AttributeValueMolecule;
import org.omwg.logicalexpression.Constants;
import org.omwg.logicalexpression.Constraint;
import org.omwg.logicalexpression.Disjunction;
import org.omwg.logicalexpression.Equivalence;
import org.omwg.logicalexpression.ExistentialQuantification;
import org.omwg.logicalexpression.Implication;
import org.omwg.logicalexpression.InverseImplication;
import org.omwg.logicalexpression.LogicProgrammingRule;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.MembershipMolecule;
import org.omwg.logicalexpression.Molecule;
import org.omwg.logicalexpression.Negation;
import org.omwg.logicalexpression.NegationAsFailure;
import org.omwg.logicalexpression.SubConceptMolecule;
import org.omwg.logicalexpression.TruthValue;
import org.omwg.logicalexpression.UniversalQuantification;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.transformation.ConstructedTermVisitor;
import org.wsml.reasoner.transformation.InfixOrderLogicalExpressionVisitor;
import org.wsmo.common.BuiltIn;
import org.wsmo.factory.FactoryContainer;
import org.wsmo.factory.LogicalExpressionFactory;

/**
 * <p>
 * Converts a set of WSML constructs to datalog. The following types of WSML
 * constructs are allowed:
 * </p>
 * <p>
 * <ul>
 * <li>A (fact)</li>
 * <li>A :- B1 and B2 and ... and Bn (rule)</li>
 * <li>!- B1 and B2 and ... and Bn (constraint)</li>
 * </ul>
 * Where Ai and Bi are either atoms or simple molecules (molecules which could
 * be logically transformed to atoms), or negation of them
 * </p>
 * <p>
 * Implementation is not thread-safe and thus may not be shared across different
 * threads.
 * </p>
 * 
 * @author Uwe Keller, DERI Innsbruck
 * @author Gabor Nagypal, FZI
 */
public class WSML2DatalogTransformer {

	FactoryContainer wsmoManager;

	/**
	 * Generates a WSML2Datalog converter.
	 */
	public WSML2DatalogTransformer(FactoryContainer wsmoManager) {
		this.wsmoManager = wsmoManager;
	}

	/**
	 * Transforms the given set of logical expressions to datalog. It is
	 * required that the logical expressions are rules of a specific syntactic
	 * kind (simple rules) which ensures that they can be converted to datalog.
	 * 
	 * The conversion in particular transformes the object-oriented modelling
	 * elements in rules, like slots or instance-of molecules.
	 * 
	 * If this is not the case an exception will be thrown.
	 * 
	 * @param rules
	 *            - the set of (simple) WSML rules
	 * @throws IllegalArgumentException
	 *             - if the given set of logical expressions does not satisfy
	 *             the syntactic requirements described above.
	 * @return a datalog program that represents the given set of WSML rules.
	 */
	public Set<Rule> transform(Set<? extends LogicalExpression> rules)
			throws IllegalArgumentException {
		Set<Rule> result = new HashSet<Rule>();

		DatalogVisitor datalogVisitor = new DatalogVisitor();

		for (LogicalExpression r : rules) {
			r.accept(datalogVisitor);
			Set<Rule> translation = datalogVisitor.getSerializedObject();
			if (translation.size() != 0) {
				for (Rule r1 : translation) {
					result.add(r1);
				}
			} else {
				throw new IllegalArgumentException(
						"WSML rule can not be translated to datalog: "
								+ r.toString());
			}
			datalogVisitor.reset();

			// Now add some additional facts stating that the found terms
			// represent in fact concepts of the ontology (needed for correct
			// implementation of reflexivity
			// of subConceptOf)

			Set<Term> conceptDenotingTerms = extractConstantsUsedAsConcepts(r);

			List<Literal> body = new LinkedList<Literal>(); // empty body
			Literal head;

			for (Term t : conceptDenotingTerms) {

				// Create a fact: knownConcept(t).
				head = new Literal(true, PRED_KNOWN_CONCEPT, t);
				result.add(new Rule(head, body));

			}

			// TODO: we need to construct in the same way the known_concept
			// facts for the query before handing the
			// datalog programm to the datalog engine!

		}
		return result;
	}

	// An inner class for detecting if a term contains any variables (and
	// therefore
	// is no ground term)

	class DetectVariablesTermVisitor extends ConstructedTermVisitor {
		boolean foundVariable = false;

		@Override
		public void visit(Variable arg0) {
			foundVariable = true;
		}

		public void reset() {
			foundVariable = false;
		}

		public boolean foundVariable() {
			return foundVariable;
		}

	}

	static class BodyMoleculeCollector extends DatalogVisitor {
		List<Molecule> bodyMolecules = new LinkedList<Molecule>();

		List<Molecule> getMolecules() {
			return bodyMolecules;
		}

		public void reset() {
			bodyMolecules = new LinkedList<Molecule>();
		}

		@Override
		public void handleAttributeConstraintMolecule(
				AttributeConstraintMolecule arg0) {
			if (inBodyOfRule) {
				bodyMolecules.add(arg0);
			}
		}

		@Override
		public void handleAttributeInferenceMolecule(
				AttributeInferenceMolecule arg0) {
			if (inBodyOfRule) {
				bodyMolecules.add(arg0);
			}
		}

		@Override
		public void handleMemberShipMolecule(MembershipMolecule arg0) {
			if (inBodyOfRule) {
				bodyMolecules.add(arg0);
			}
		}

		@Override
		public void handleSubConceptMolecule(SubConceptMolecule arg0) {
			if (inBodyOfRule) {
				bodyMolecules.add(arg0);
			}
		}

		@Override
		public void handleImplication(Implication arg0) {
			super.handleImplication(arg0);
		}

		@Override
		public void handleInverseImplication(InverseImplication arg0) {
			super.handleInverseImplication(arg0);
		}

		@Override
		public void handleAtom(Atom atom) {
			// do nothing.
		}

		@Override
		public void handleAttributeValueMolecule(AttributeValueMolecule arg0) {
			// do nothing.
		}

		@Override
		public void handleTruthValue(TruthValue truthValue) {
			// do nothing.
		}

	}

	public Set<Term> extractConstantsUsedAsConcepts(LogicalExpression rule) {

		Set<Term> result = new HashSet<Term>();
		DetectVariablesTermVisitor variableDetector = new DetectVariablesTermVisitor();

		BodyMoleculeCollector bmCollector = new BodyMoleculeCollector();
		rule.accept(bmCollector);
		List<Molecule> bodyMolecules = bmCollector.getMolecules();

		for (Molecule l : bodyMolecules) {
			org.omwg.logicalexpression.Molecule currentMolecule = l;
			if (currentMolecule instanceof org.omwg.logicalexpression.MembershipMolecule) {

				currentMolecule.getRightParameter().accept(variableDetector);
				if (!variableDetector.foundVariable()) {
					result.add(currentMolecule.getRightParameter());
				}
				variableDetector.reset();

			} else if (currentMolecule instanceof org.omwg.logicalexpression.SubConceptMolecule) {

				currentMolecule.getLeftParameter().accept(variableDetector);
				if (!variableDetector.foundVariable()) {
					result.add(currentMolecule.getLeftParameter());
				}
				variableDetector.reset();

				currentMolecule.getRightParameter().accept(variableDetector);
				if (!variableDetector.foundVariable()) {
					result.add(currentMolecule.getRightParameter());
				}
				variableDetector.reset();

			} else if (currentMolecule instanceof org.omwg.logicalexpression.AttributeMolecule) {
				currentMolecule.getLeftParameter().accept(variableDetector);
				if (!variableDetector.foundVariable()) {
					result.add(currentMolecule.getLeftParameter());
				}
				variableDetector.reset();

				currentMolecule.getRightParameter().accept(variableDetector);
				if (!variableDetector.foundVariable()) {
					result.add(currentMolecule.getRightParameter());
				}
				variableDetector.reset();
			}
		}

		bmCollector.reset();

		return result;
	}

	/**
	 * Transforms a single (simple) WSML (datalog) rule to datalog. In general,
	 * the result consists of several datalog rules i.e. a datalog program
	 * 
	 * @param rule
	 *            - the rule to be translated
	 * @return a datalog program that represents the given simple WSML rule.
	 * @throws IllegalArgumentException
	 *             - if the given rule can not be translated to datalog.
	 */
	public Set<Rule> transform(LogicalExpression rule)
			throws IllegalArgumentException {
		// System.out.println(rule.toString());
		Set<LogicalExpression> rules = new HashSet<LogicalExpression>();
		rules.add(rule);
		return transform(rules);
	}

	public Set<Rule> generateAuxilliaryRules() {
		Set<Rule> result = new HashSet<Rule>();

		LogicalExpressionFactory f = this.wsmoManager
				.getLogicalExpressionFactory();

		Variable vConcept = f.createVariable("concept");
		Variable vConcept2 = f.createVariable("concept2");
		Variable vConcept3 = f.createVariable("concept3");
		Variable vAttribute = f.createVariable("attribute");
		// Variable vRange = f.createVariable("range");
		Variable vInstance = f.createVariable("instance");
		Variable vInstance2 = f.createVariable("instance2");
		// Variable vAttributeValue = f.createVariable("attributevalue");
		Variable vType = f.createVariable("type");

		List<Literal> body;
		Literal head;

		// transitivity: sco(?c1,?c3) <- sco(?c1,?c2) and sco(?c2,?c3)
		// extension-subset: mo(?o,?c2) <- mo(?o,?c1) and sco(?c1,?c2)
		body = new LinkedList<Literal>();
		head = new Literal(true, PRED_SUB_CONCEPT_OF, vConcept, vConcept3);
		body.add(new Literal(true, PRED_SUB_CONCEPT_OF, vConcept, vConcept2));
		body.add(new Literal(true, PRED_SUB_CONCEPT_OF, vConcept2, vConcept3));
		result.add(new Rule(head, body));

		// memberOf(instance, concept2) <- memberOf(instance, concept),
		// subConceptOf(concept, concept2)
		body = new LinkedList<Literal>();
		head = new Literal(true, PRED_MEMBER_OF, vInstance, vConcept2);
		body.add(new Literal(true, PRED_MEMBER_OF, vInstance, vConcept));
		body.add(new Literal(true, PRED_SUB_CONCEPT_OF, vConcept, vConcept2));
		result.add(new Rule(head, body));

		// reflexivity: sco(?c,?c) :- ?c is a known concept IRI in the ontology
		// (explicit or inferred)
		body = new LinkedList<Literal>();
		head = new Literal(true, PRED_SUB_CONCEPT_OF, vConcept, vConcept);
		body.add(new Literal(true, PRED_KNOWN_CONCEPT, vConcept));
		result.add(new Rule(head, body));

		// knownConcept(?c) :- ?i memberOf ?c
		body = new LinkedList<Literal>();
		head = new Literal(true, PRED_KNOWN_CONCEPT, vConcept);
		body.add(new Literal(true, PRED_MEMBER_OF, vInstance, vConcept));
		result.add(new Rule(head, body));

		// knownConcept(?c1) :- ?c1 subConceptOf ?c2
		body = new LinkedList<Literal>();
		head = new Literal(true, PRED_KNOWN_CONCEPT, vConcept2);
		body.add(new Literal(true, PRED_SUB_CONCEPT_OF, vConcept2, vConcept3));
		result.add(new Rule(head, body));

		// knownConcept(?c2) :- ?c1 subConceptOf ?c2
		body = new LinkedList<Literal>();
		head = new Literal(true, PRED_KNOWN_CONCEPT, vConcept3);
		body.add(new Literal(true, PRED_SUB_CONCEPT_OF, vConcept2, vConcept3));
		result.add(new Rule(head, body));

		// knownConcept(?c) :- ?c[?a ofType ?t]
		body = new LinkedList<Literal>();
		head = new Literal(true, PRED_KNOWN_CONCEPT, vConcept);
		body.add(new Literal(true, PRED_OF_TYPE, vConcept, vAttribute,
				vConcept2));
		result.add(new Rule(head, body));

		// knownConcept(?c) :- ?c[?a impilesType ?t]
		body = new LinkedList<Literal>();
		head = new Literal(true, PRED_KNOWN_CONCEPT, vConcept);
		body.add(new Literal(true, PRED_IMPLIES_TYPE, vConcept, vAttribute,
				vConcept2));
		result.add(new Rule(head, body));

		// Inference of attr value types: mo(v,c2) <- itype(c1, att,
		// c2), mo(o,c1), hval(o,att, v)
		// mo(v1,v2) <- itype(v3, v4, v2), mo(v5,v3), hval(v5,v4, v1)
		body = new LinkedList<Literal>();
		head = new Literal(true, PRED_MEMBER_OF, vInstance2, vConcept2);
		body.add(new Literal(true, PRED_IMPLIES_TYPE, vConcept, vAttribute,
				vConcept2));
		body.add(new Literal(true, PRED_MEMBER_OF, vInstance, vConcept));
		body.add(new Literal(true, PRED_HAS_VALUE, vInstance, vAttribute,
				vInstance2));
		result.add(new Rule(head, body));

		/*
		 * Rules for: - getting all direct subConcepts of a specific concepts -
		 * getting all direct concepts a specific instance is member of
		 */
		// Indirect concepts: indirect(?x,?z) :- ?x subConceptOf ?y and
		// ?y subConceptOf ?z and ?x != ?y and ?y != ?z.
		body = new LinkedList<Literal>();
		head = new Literal(true, PRED_INDIRECT_SUBCONCEPT, vConcept, vConcept2);
		body.add(new Literal(true, PRED_SUB_CONCEPT_OF, vConcept, vConcept3));
		body.add(new Literal(true, PRED_SUB_CONCEPT_OF, vConcept3, vConcept2));
		body.add(new Literal(true, BuiltIn.INEQUAL.getFullName(), vConcept,
				vConcept3));
		body.add(new Literal(true, BuiltIn.INEQUAL.getFullName(), vConcept3,
				vConcept2));
		result.add(new Rule(head, body));
		// Direct concepts: direct(?x,?y) :- ?x subConceptOf ?y and
		// naf(indirect(?x,?y)).
		body = new LinkedList<Literal>();
		head = new Literal(true, PRED_DIRECT_SUBCONCEPT, vConcept, vConcept3);
		body.add(new Literal(true, PRED_SUB_CONCEPT_OF, vConcept, vConcept3));
		body.add(new Literal(false, PRED_INDIRECT_SUBCONCEPT, vConcept,
				vConcept3));
		result.add(new Rule(head, body));
		// Indirect concepts of: indirectOf(?x,?y) :- ?x memberOf ?y and
		// ?x memberOf ?z and ?z subConceptOf ?y and ?z != ?y.
		body = new LinkedList<Literal>();
		head = new Literal(true, PRED_INDIRECT_CONCEPT, vInstance, vConcept);
		body.add(new Literal(true, PRED_MEMBER_OF, vInstance, vConcept));
		body.add(new Literal(true, PRED_MEMBER_OF, vInstance, vConcept2));
		body.add(new Literal(true, PRED_SUB_CONCEPT_OF, vConcept2, vConcept));
		body.add(new Literal(true, BuiltIn.INEQUAL.getFullName(), vConcept2,
				vConcept));
		result.add(new Rule(head, body));
		// Direct concepts of: directOf(?x,?y) :- ?x memberOf ?y and
		// naf(indirectOf(?x,?y)).
		body = new LinkedList<Literal>();
		head = new Literal(true, PRED_DIRECT_CONCEPT, vInstance, vConcept);
		body.add(new Literal(true, PRED_MEMBER_OF, vInstance, vConcept));
		body.add(new Literal(false, PRED_INDIRECT_CONCEPT, vInstance, vConcept));
		result.add(new Rule(head, body));

		// TODO: subRelationOf not covered yet

		/*
		 * // Semantics of C1[att => C2] (oftype constraint) // !- oftype(c1,
		 * att, c2), mo(i,c1), hval(i, att, v), NAF mo(v,c2) // With variables:
		 * oftype(v1, v2, v3), mo(v4,v1), hval(v4, v2, v5), NAF // mo(v5,v3) //
		 * Commented out, because it is handled by
		 * 
		 * body = new LinkedList<Literal>(); body.add(new Literal(true,
		 * PRED_OF_TYPE, vConcept, vAttribute, vRange)); body.add(new
		 * Literal(true, PRED_MEMBER_OF, vInstance, vConcept)); body.add(new
		 * Literal(true, PRED_HAS_VALUE, vInstance, vAttribute,
		 * vAttributeValue)); body.add(new Literal(false, PRED_MEMBER_OF,
		 * vAttributeValue, vRange)); result.add(new Rule(null, body));
		 */

		/*
		 * The following two rules have been added so that the reasoner infers
		 * the attributes. The was previously done programmatically in:
		 * DatalogBasedWSMLReasoner.inheritAttributesToSubConcepts()
		 */

		// wsml-of-type(?sub, ?a, ?t) :- wsml-subconcept-of(?sub, ?super),
		// wsml-of-type(?super, ?a, ?t).
		head = new Literal(true, PRED_OF_TYPE, vConcept2, vAttribute, vType);
		body = new LinkedList<Literal>();
		body.add(new Literal(true, PRED_SUB_CONCEPT_OF, vConcept2, vConcept));
		body.add(new Literal(true, PRED_OF_TYPE, vConcept, vAttribute, vType));
		result.add(new Rule(head, body));

		// baz
		// wsml-implies-type(?sub, ?a, ?t) :- wsml-subconcept-of(?sub, ?super),
		// wsml-implies-type(?super, ?a, ?t).
		head = new Literal(true, PRED_IMPLIES_TYPE, vConcept2, vAttribute,
				vType);
		body = new LinkedList<Literal>();
		body.add(new Literal(true, PRED_SUB_CONCEPT_OF, vConcept2, vConcept));
		body.add(new Literal(true, PRED_IMPLIES_TYPE, vConcept, vAttribute,
				vType));
		result.add(new Rule(head, body));

		return result;
	}

	// -- Inner class for visiting rules (logical expressions) ...

	/**
	 * Given a WSML rule the visitor generates a new datalog rule that
	 * represents the original WSML rule but where all non-predicate molecules
	 * are replaced by respective atomic formulaes that use specific predicates
	 * 
	 * Implements a left-first, depth-first, infix traversal.
	 */
	private static class DatalogVisitor extends
			InfixOrderLogicalExpressionVisitor {

		private List<Literal> datalogBody;

		private List<Literal> datalogHead;

		protected boolean inHeadOfRule;

		protected boolean inBodyOfRule;

		private boolean positive;

		public DatalogVisitor() {
			super();
			reset();
		}

		/**
		 * Resets the state of the visitor to the intial state such that the
		 * visitor can be reused again.
		 * 
		 */
		public void reset() {
			datalogBody = new LinkedList<Literal>();
			datalogHead = new LinkedList<Literal>();
			inHeadOfRule = false;
			inBodyOfRule = false;
			positive = true;
		}

		/**
		 * Constructs a datalog representation of the given (visited) WSML rule.
		 * The representation is a datalog program.
		 * 
		 * The construction introduces new predicates for which a defining
		 * datalog rules will not be generated here (in order to avoid multiple
		 * generation of the same rules). Instead they will be generated only
		 * once in WSML2Datalog.transform().
		 * 
		 * @return the Datalog program that represents the WSML rule or null if
		 *         the visited rule does not conform to the syntax requirements
		 *         stated in class WSML2Datalog and thus can not be translated
		 *         to datalog.
		 * 
		 * @see org.wsml.reasoner.normalization.InfixOrderLogicalExpressionVisitor#getSerializedObject()
		 */

		@Override
		public Set<Rule> getSerializedObject() {
			Set<Rule> results = new HashSet<Rule>();
			if (datalogBody.size() != 0) {
				for (Literal l : datalogHead) {
					results.add(new Rule(l, datalogBody));
				}
			} else if (datalogHead.size() != 0) {
				for (Literal l : datalogHead) {
					results.add(new Rule(l));
				}
			}
			return results;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.wsml.reasoner.normalization.DepthFirstLogicalExpressionVisitor
		 * #handleAtom(org.omwg.logicalexpression.Atom)
		 */
		@Override
		public void handleAtom(Atom atom) {
			String predUri = atom.getIdentifier().toString();
			// System.out.println("Atom URI:" + predUri);
			// System.out.println("Atom arity:" + atom.getArity());
			// System.out.println("Atom parameters:" + atom.listParameters());
			// conditional expression is needed because WSMO4J throws a
			// nullpointerexception for atom.listParameters()
			Literal l = (atom.getArity() > 0) ? new Literal(positive, predUri,
					atom.listParameters()) : new Literal(positive, predUri,
					new ArrayList<Term>());
			storeLiteral(l);
		}

		@Override
		public void handleTruthValue(TruthValue truthValue) {
			boolean value = truthValue.getValue();
			String identifier = value ? Constants.WSML_TRUE
					: Constants.WSML_FALSE;

			// Create a new literal representing the truth value.
			Literal literal = new Literal(true, identifier,
					new ArrayList<Term>());
			storeLiteral(literal);
		}

		private void storeLiteral(Literal l) {
			if (inBodyOfRule) {
				datalogBody.add(l);
			} else if (inHeadOfRule) {
				// new Rule
				this.datalogHead.add(l);
			} else {
				// We do not have an implication but only a simple fact.
				// datalogHead = l;
				datalogHead.add(l);
			}
			positive = true;
		}

		@Override
		public void enterConstraint(Constraint arg0) {
			inHeadOfRule = false;
			inBodyOfRule = true;
		}

		@Override
		public void enterEquivalence(Equivalence arg0) {
			inHeadOfRule = true;
			inBodyOfRule = false;
		}

		@Override
		public void enterExistentialQuantification(
				ExistentialQuantification arg0) {
			throw new DatalogException(
					"Quantifier is not allowed in simple WSML datalog rules.");
		}

		@Override
		public void enterUniversalQuantification(UniversalQuantification arg0) {
			throw new DatalogException(
					"Quantifier is not allowed in simple WSML datalog rules.");
		}

		@Override
		public void handleSubConceptMolecule(SubConceptMolecule arg0) {
			Literal l = new Literal(positive, PRED_SUB_CONCEPT_OF,
					arg0.getLeftParameter(), arg0.getRightParameter());
			storeLiteral(l);
		}

		@Override
		public void handleMemberShipMolecule(MembershipMolecule arg0) {
			Literal l = new Literal(positive, PRED_MEMBER_OF,
					arg0.getLeftParameter(), arg0.getRightParameter());
			storeLiteral(l);
		}

		@Override
		public void handleAttributeInferenceMolecule(
				AttributeInferenceMolecule arg0) {
			Literal l = new Literal(positive, PRED_IMPLIES_TYPE,
					arg0.getLeftParameter(), arg0.getAttribute(),
					arg0.getRightParameter());
			storeLiteral(l);
		}

		@Override
		public void handleAttributeConstraintMolecule(
				AttributeConstraintMolecule arg0) {
			Literal l = new Literal(positive, PRED_OF_TYPE,
					arg0.getLeftParameter(), arg0.getAttribute(),
					arg0.getRightParameter());
			storeLiteral(l);
		}

		@Override
		public void handleAttributeValueMolecule(AttributeValueMolecule arg0) {
			Literal l = new Literal(positive, PRED_HAS_VALUE,
					arg0.getLeftParameter(), arg0.getAttribute(),
					arg0.getRightParameter());
			storeLiteral(l);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.wsml.reasoner.normalization.InfixOrderLogicalExpressionVisitor
		 * #handleNeg(org.omwg.logicalexpression.Unary)
		 */
		@Override
		public void enterNegation(Negation arg0) {
			throw new DatalogException(
					"Classical negation is not allowed in simple WSML datalog rules.");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.wsml.reasoner.normalization.InfixOrderLogicalExpressionVisitor
		 * #enterNaf(org.omwg.logicalexpression.Unary)
		 */
		@Override
		public void enterNegationAsFailure(NegationAsFailure naf) {
			LogicalExpression arg = naf.getOperand();
			if (!(arg instanceof AtomicExpression))
				throw new DatalogException(
						"Negation as failure is allowed only on atoms or molecules in simple WSML datalog rules.");
			positive = false;

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.wsml.reasoner.normalization.InfixOrderLogicalExpressionVisitor
		 * #handleOr(org.omwg.logicalexpression.Binary)
		 */
		@Override
		public void enterDisjunction(Disjunction arg0) {
			throw new DatalogException(
					"Disjunction is not allowed in simple WSML datalog rules.");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.wsml.reasoner.normalization.InfixOrderLogicalExpressionVisitor
		 * #enterImpliedBy(org.omwg.logicalexpression.Binary)
		 */
		@Override
		public void enterInverseImplication(InverseImplication arg0) {
			inHeadOfRule = true;
			inBodyOfRule = false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.wsml.reasoner.normalization.InfixOrderLogicalExpressionVisitor
		 * #enterImplies(org.omwg.logicalexpression.Binary)
		 */
		@Override
		public void enterImplication(Implication arg0) {
			inHeadOfRule = false;
			inBodyOfRule = true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.wsml.reasoner.normalization.InfixOrderLogicalExpressionVisitor
		 * #enterImpliesLP(org.omwg.logicalexpression.Binary)
		 */
		@Override
		public void enterLogicProgrammingRule(LogicProgrammingRule arg0) {
			inHeadOfRule = true;
			inBodyOfRule = false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.wsml.reasoner.normalization.InfixOrderLogicalExpressionVisitor
		 * #handleImpliedBy(org.omwg.logicalexpression.Binary)
		 */
		@Override
		public void handleInverseImplication(InverseImplication arg0) {
			inHeadOfRule = false;
			inBodyOfRule = true;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.wsml.reasoner.normalization.InfixOrderLogicalExpressionVisitor
		 * #handleImplies(org.omwg.logicalexpression.Binary)
		 */
		@Override
		public void handleImplication(Implication arg0) {
			inHeadOfRule = true;
			inBodyOfRule = false;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.wsml.reasoner.normalization.InfixOrderLogicalExpressionVisitor
		 * #handleImpliesLP(org.omwg.logicalexpression.Binary)
		 */
		@Override
		public void handleLogicProgrammingRule(LogicProgrammingRule arg0) {
			inHeadOfRule = false;
			inBodyOfRule = true;
		}

		@Override
		public void handleEquivalence(Equivalence arg0) {
			inHeadOfRule = false;
			inBodyOfRule = true;
		}
	}

}
