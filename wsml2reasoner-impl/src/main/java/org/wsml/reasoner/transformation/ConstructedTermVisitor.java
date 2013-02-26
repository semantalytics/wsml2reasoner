package org.wsml.reasoner.transformation;

import org.omwg.logicalexpression.terms.ConstructedTerm;
import org.omwg.logicalexpression.terms.TermVisitor;
import org.omwg.ontology.ComplexDataValue;
import org.omwg.ontology.SimpleDataValue;
import org.omwg.ontology.Variable;
import org.wsmo.common.IRI;
import org.wsmo.common.NumberedAnonymousID;
import org.wsmo.common.UnnumberedAnonymousID;

/**
 * A simple stub implementation of the term Visitor interface. Real
 * functionality should be added in subclasses.
 * 
 * @author Uwe Keller, DERI Innsbruck
 * @author Gabor Nagypal, FZI
 */
public class ConstructedTermVisitor implements TermVisitor {

	public void visit(ConstructedTerm arg0) {
		for (int i = 0; i < arg0.getArity(); i++) {
			arg0.getParameter(i).accept(this);
		}
	}

	public void visit(Variable arg0) {
	}

	public void visit(SimpleDataValue arg0) {
	}

	public void visit(ComplexDataValue arg0) {
	}

	public void visit(UnnumberedAnonymousID arg0) {
	}

	public void visit(NumberedAnonymousID arg0) {
	}

	public void visit(IRI arg0) {
	}
}
