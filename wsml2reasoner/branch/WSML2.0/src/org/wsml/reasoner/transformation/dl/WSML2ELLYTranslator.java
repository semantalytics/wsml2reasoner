package org.wsml.reasoner.transformation.dl;

import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.deri.wsmo4j.io.serializer.wsml.WsmlObjectVisitor;
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
import org.omwg.logicalexpression.MembershipMolecule;
import org.omwg.logicalexpression.Negation;
import org.omwg.logicalexpression.NegationAsFailure;
import org.omwg.logicalexpression.SubConceptMolecule;
import org.omwg.logicalexpression.TruthValue;
import org.omwg.logicalexpression.UniversalQuantification;
import org.omwg.logicalexpression.terms.ConstructedTerm;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Attribute;
import org.omwg.ontology.Axiom;
import org.omwg.ontology.ComplexDataValue;
import org.omwg.ontology.Concept;
import org.omwg.ontology.Instance;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Relation;
import org.omwg.ontology.RelationInstance;
import org.omwg.ontology.SimpleDataValue;
import org.omwg.ontology.Variable;
import org.sti2.elly.api.basics.IAtomicConcept;
import org.sti2.elly.api.basics.IAtomicRole;
import org.sti2.elly.api.basics.IRule;
import org.sti2.elly.api.factory.IBasicFactory;
import org.sti2.elly.api.factory.ITermFactory;
import org.sti2.elly.api.terms.IIndividual;
import org.sti2.elly.api.terms.IVariable;
import org.sti2.elly.basics.BasicFactory;
import org.sti2.elly.terms.TermFactory;
import org.wsmo.common.Entity;
import org.wsmo.common.IRI;
import org.wsmo.common.IdentifiableEntity;
import org.wsmo.common.NFPEntry;
import org.wsmo.common.NumberedAnonymousID;
import org.wsmo.common.UnnumberedAnonymousID;
import org.wsmo.mediator.GGMediator;
import org.wsmo.mediator.OOMediator;
import org.wsmo.mediator.WGMediator;
import org.wsmo.mediator.WWMediator;
import org.wsmo.service.Capability;
import org.wsmo.service.Goal;
import org.wsmo.service.Interface;
import org.wsmo.service.WebService;
import org.wsmo.service.capabilities.Assumption;
import org.wsmo.service.capabilities.Effect;
import org.wsmo.service.capabilities.Postcondition;
import org.wsmo.service.capabilities.Precondition;
import org.wsmo.service.choreography.rule.Rule;
import org.wsmo.service.choreography.rule.RulesContainer;
import org.wsmo.service.choreography.signature.StateSignature;
import org.wsmo.service.interfaces.Choreography;
import org.wsmo.service.interfaces.Orchestration;

public class WSML2ELLYTranslator implements WsmlObjectVisitor {
	private static IBasicFactory BASIC = BasicFactory.getInstance();
	private static ITermFactory TERM = TermFactory.getInstance();
	
	private List<IRule> rules;
	private Stack<Object> tempStack;
	
	private Map<Object, Object> wsml2EllyCache;
	
	@Override
	public void visit(NFPEntry nfpEntry) {
		// skip nfps
	}

	@Override
	public void visit(Attribute attribute) {
		IAtomicRole eRole = BASIC.createAtomicRole(asString(attribute));
		
		attribute.listTypes();
		attribute.isConstraining();
		attribute.isReflexive();
		attribute.isSymmetric();
		attribute.isTransitive();
		attribute.getConcept();
		attribute.getInverseOf(); // supported?
		attribute.getMaxCardinality();
		attribute.getMinCardinality();
		attribute.getSubAttributeOf(); // can't go super-direction
		
	}

	@Override
	public void visit(Assumption assumption) {
		for (LogicalExpression logicalExpression: assumption.listDefinitions()) {
			logicalExpression.accept(this);
		}
	}

	@Override
	public void visit(Effect effect) {
		for (LogicalExpression logicalExpression: effect.listDefinitions()) {
			logicalExpression.accept(this);
		}
	}

	@Override
	public void visit(Postcondition postcondition) {
		for (LogicalExpression logicalExpression: postcondition.listDefinitions()) {
			logicalExpression.accept(this);
		}
	}

	@Override
	public void visit(Precondition precondition) {
		for (LogicalExpression logicalExpression: precondition.listDefinitions()) {
			logicalExpression.accept(this);
		}
	}

	@Override
	public void visit(RulesContainer rulesContainer) {
		for (Rule rule : rulesContainer.listRules()) {
//			rule.accept(this);
		}
	}

	@Override
	public void visit(StateSignature stateSignature) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Choreography choreography) {
		choreography.getRules().accept(this);
		choreography.getStateSignature().accept(this);
		
//		for (IRI mediator : choreography.getUsedMediators().listMediators()) {
//			
//		}
		
	}

	@Override
	public void visit(Orchestration orchestration) {
		// nothing to do ...
	}

	@Override
	public void visit(Axiom axiom) {
		for (LogicalExpression logicalExpression: axiom.listDefinitions()) {
			logicalExpression.accept(this);
		}
	}

	@Override
	public void visit(Concept concept) {
		IAtomicConcept eConcept = getOrCreateConcept(concept);
		
		for (Attribute attribute : concept.listAttributes()) {
			// create C(x) and r(x,y) and ...
		}
		
		for (Instance instance : concept.listInstances()) {
			// create C(a).
			instance.accept(this);
			IIndividual individual = (IIndividual) tempStack.pop();
			
			// TODO add rule
		}
		
		for (Concept superConcept : concept.listSuperConcepts()) {
			// create SC :- C.
			IAtomicConcept eSuperConcept = getOrCreateConcept(superConcept);
			
			// TODO add rule
		}
		
		// concept.listSubConcepts(); do not render since one direction is enough
		// wsml also has just superconcepts in syntax
		
	}

	@Override
	public void visit(Instance instance) {
		IIndividual eIndividual = (IIndividual) wsml2EllyCache.get(instance);
		if (eIndividual == null) {
			eIndividual = TERM.createIndividual(asString(instance));
			wsml2EllyCache.put(instance, eIndividual);
		}
		
		// push it on Temporary Stack
		tempStack.push(eIndividual);
	}

	@Override
	public void visit(Relation relation) {
		IAtomicRole eRole = BASIC.createAtomicRole(asString(relation));
		
		relation.listParameters();
		relation.listRelationInstances();
		relation.listSuperRelations();
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(RelationInstance relationInstance) {
		IAtomicRole eRole = BASIC.createAtomicRole(asString(relationInstance));
		
		relationInstance.listParameterValues();

		// r(a,b).
	}

	@Override
	public void visit(Capability capability) {
//		capability.getUsedMediators().listMediators();
//		capability.getImportedOntologies().listOntologies() 
		
		for (Assumption assumption : capability.listAssumptions()) {
			assumption.accept(this);
		}
		
		for (Effect effect : capability.listEffects()) {
			effect.accept(this);
		}
		
		for (Postcondition postcondition : capability.listPostconditions()) {
			postcondition.accept(this);
		}
		
		for (Precondition precondition : capability.listPreconditions()) {
			precondition.accept(this);
		}
		
		for (Variable sharedVariable : capability.listSharedVariables()) {
			sharedVariable.accept(this);
			// TODO what to do with them?
		}
		
	}

	@Override
	public void visit(Interface intrface) {
		intrface.getOrchestration().accept(this);
//		intrface.getUsedMediators().listMediators()
		
		intrface.isLocatedById();
	}

	@Override
	public void visit(Ontology ontology) {
//		ontology.getUsedMediators().listMediators()
		for (Axiom axiom : ontology.listAxioms()) {
			axiom.accept(this);
		}
		
		for (Concept concept : ontology.listConcepts()) {
			concept.accept(this);
		}
		
		for (Instance instance : ontology.listInstances()) {
			instance.accept(this);
		}
		
		for (Relation relation : ontology.listRelations()) {
			relation.accept(this);
		}
		
		for (RelationInstance relationInstance : ontology.listRelationInstances()) {
			relationInstance.accept(this);
		}
		
//		ontology.isLocatedById()
		
	}

	@Override
	public void visit(OOMediator ooMediator) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(WGMediator wgMediator) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(GGMediator ggMediator) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(WWMediator wwMediator) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Goal goal) {
		//		goal.isLocatedById()
		
		for (Interface intrface : goal.listInterfaces()) {
			intrface.accept(this);
		}
	}

	@Override
	public void visit(WebService webService) {
//		webService.isLocatedById()
		
		for (Interface intrface : webService.listInterfaces()) {
			intrface.accept(this);
		}
	}

	@Override
	public void visit(ConstructedTerm t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Variable t) {
		IVariable eVariable = getOrCreateVariable(t);
		tempStack.push(eVariable);
	}

	@Override
	public void visit(SimpleDataValue t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(ComplexDataValue t) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(UnnumberedAnonymousID t) {
		IIndividual eIndividual = getOrCreateIndividual(t);
		tempStack.push(eIndividual);
	}

	@Override
	public void visit(NumberedAnonymousID t) {
		IIndividual eIndividual = getOrCreateIndividual(t);
		tempStack.push(eIndividual);
	}

	@Override
	public void visit(IRI t) {
		IIndividual eIndividual = getOrCreateIndividual(t);
		tempStack.push(eIndividual);
	}

	@Override
	public void visitAtom(Atom expr) {
		if (expr.getArity() == 1) {
//			IAtomicConcept eConcept = getOrCreateConcept(expr);
		} else if (expr.getArity() == 2) {
			
		} else {
			throw new IllegalArgumentException("Atom " + expr + " has illegal arity " + expr.getArity());
		}
		
		// stack?
	}

	@Override
	public void visitAttributeConstraintMolecule(
			AttributeConstraintMolecule expr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitAttributeInferenceMolecule(AttributeInferenceMolecule expr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitAttributeValueMolecule(AttributeValueMolecule expr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitCompoundMolecule(CompoundMolecule expr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitConjunction(Conjunction expr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitConstraint(Constraint expr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitDisjunction(Disjunction expr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitEquivalence(Equivalence expr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitExistentialQuantification(ExistentialQuantification expr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitImplication(Implication expr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitInverseImplication(InverseImplication expr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitLogicProgrammingRule(LogicProgrammingRule expr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitMemberShipMolecule(MembershipMolecule expr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitNegation(Negation expr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitNegationAsFailure(NegationAsFailure expr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitSubConceptMolecule(SubConceptMolecule expr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitTruthValue(TruthValue expr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitUniversalQuantification(UniversalQuantification expr) {
		// TODO Auto-generated method stub
		
	}

	
	// Helpers
	private String asString(IdentifiableEntity entity) {
		return entity.getIdentifier().toString();
	}
	
	private String asString(Term term) {
		return term.toString();
	}

	private IAtomicConcept getOrCreateConcept(Concept concept) {
		IAtomicConcept eConcept = (IAtomicConcept) wsml2EllyCache.get(concept);
		if (eConcept == null) {
			eConcept = BASIC.createAtomicConcept(asString(concept));
			wsml2EllyCache.put(concept, eConcept);
		}
		
		return eConcept;
	}

	private IIndividual getOrCreateIndividual(Term term) {
		IIndividual eIndividual = (IIndividual) wsml2EllyCache.get(term);
		if (eIndividual == null) {
			eIndividual = TERM.createIndividual(asString(term));
			wsml2EllyCache.put(term, eIndividual);
		}
		
		return eIndividual;			
	}

	private IVariable getOrCreateVariable(Variable variable) {
		IVariable eVariable = (IVariable) wsml2EllyCache.get(variable);
		if (eVariable == null) {
			eVariable = TERM.createVariable(asString(variable), variable.isSafe());
			wsml2EllyCache.put(variable, eVariable);
		}
		
		return eVariable;			
	}

}
