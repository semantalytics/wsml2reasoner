package org.wsml.reasoner.impl;

import java.util.HashSet;
import java.util.Set;

import org.omwg.ontology.Variable;
import org.wsml.reasoner.transformation.ConstructedTermVisitor;

/**
 * Collects all variables in the given term.
 * 
 * @author Uwe Keller, DERI Innsbruck
 */
public class TermVariableVisitor extends ConstructedTermVisitor {

	private Set<Variable> freeVars;

	public TermVariableVisitor() {
		super();
		reset();
	}

	/**
	 * Resets the internal state of the visitor such that the object can be
	 * reused across several calls in the same thread.
	 * 
	 */
	public void reset() {
		freeVars = new HashSet<Variable>();
	}

	/**
	 * Not used!
	 * 
	 * @return null.
	 */
	public Object getSerializedObject() {
		return null;
	}

	/**
	 * Returns the set of all variables in the visited term.
	 * 
	 * @return FreeVar(t)
	 */
	public Set<Variable> getVariables() {
		return freeVars;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.wsml.reasoner.normalization.PostfixOrderTermVisitor#handleVariable
	 * (org.omwg.ontology.Variable)
	 */
	@Override
	public void visit(Variable arg0) {
		freeVars.add(arg0);
	}

}
