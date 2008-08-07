/**
 * WSML Reasoner Implementation.
 *
 * Copyright (c) 2005, FZI, Germany.
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
package org.wsml.reasoner.transformation.le;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.omwg.logicalexpression.Atom;
import org.omwg.logicalexpression.AttributeConstraintMolecule;
import org.omwg.logicalexpression.AttributeInferenceMolecule;
import org.omwg.logicalexpression.AttributeMolecule;
import org.omwg.logicalexpression.AttributeValueMolecule;
import org.omwg.logicalexpression.CompoundMolecule;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.MembershipMolecule;
import org.omwg.logicalexpression.Molecule;
import org.omwg.logicalexpression.SubConceptMolecule;
import org.omwg.logicalexpression.terms.Term;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsml.reasoner.transformation.AnonymousIdTranslator;
import org.wsmo.common.Identifier;
import org.wsmo.factory.WsmoFactory;

/**
 * This singleton class represents a set of normalization rules for replacing
 * complex molecules inside a logical expression by conjunctions of simple ones.
 * 
 * @author Stephan Grimm, FZI Karlsruhe
 */
public class FOLMoleculeDecompositionRules extends FixedModificationRules {
    protected AnonymousIdTranslator anonymousIDTranslator;

    protected WsmoFactory wsmoFactory;

    private AddOnlyArrayList<NormalizationRule> rules;

    public FOLMoleculeDecompositionRules(WSMO4JManager wsmoManager) {
        super(wsmoManager);
        wsmoFactory = wsmoManager.getWSMOFactory();
        anonymousIDTranslator = new AnonymousIdTranslator(wsmoManager.getWSMOFactory());
        rules = new AddOnlyArrayList<NormalizationRule>();
        rules.add(new MoleculeDecompositionRule());
        rules.add(new MoleculeAnonymousIDRule());
        rules.add(new AtomAnonymousIDRule());
    }

    protected class MoleculeDecompositionRule implements NormalizationRule {
        public LogicalExpression apply(LogicalExpression expression) {
            if (expression instanceof CompoundMolecule) {
                Set<LogicalExpression> simpleMolecules = decomposeMolecule((CompoundMolecule) expression);
                return buildNaryConjunction(simpleMolecules);
            }
            else {
                return moleculeToAtom((Molecule) expression);
            }
        }

        public boolean isApplicable(LogicalExpression expression) {
            return expression instanceof Molecule;
        }

        protected Set<LogicalExpression> decomposeMolecule(CompoundMolecule compoundMolecule) {
            Set<LogicalExpression> simpleMolecules = new HashSet<LogicalExpression>();
            Iterator moleculeIterator = compoundMolecule.listOperands().iterator();
            while (moleculeIterator.hasNext()) {
                Molecule molecule = (Molecule) moleculeIterator.next();
                simpleMolecules.add(moleculeToAtom(molecule));
            }
            return simpleMolecules;
        }

        public final static String sub = "urn://sub#";

        public final static String isa = "urn://isa#";

        public final static String hasValue = "urn://hv#";

        public final static String ofType = "urn://oftp#";

        public final static String impliesType = "urn://imtp#";

        private Atom moleculeToAtom(Molecule m) {
            Identifier id = null;
            List<Term> params = new ArrayList<Term>();
            params.add(m.getLeftParameter());
            if (m instanceof SubConceptMolecule) {
                id = wsmoFactory.createIRI(sub);
            }
            if (m instanceof MembershipMolecule) {
                id = wsmoFactory.createIRI(isa);
            }
            if (m instanceof AttributeConstraintMolecule) {
                id = wsmoFactory.createIRI(ofType);
                params.add(((AttributeConstraintMolecule) m).getAttribute());
            }
            if (m instanceof AttributeInferenceMolecule) {
                id = wsmoFactory.createIRI(impliesType);
                params.add(((AttributeInferenceMolecule) m).getAttribute());
            }
            if (m instanceof AttributeValueMolecule) {
                id = wsmoFactory.createIRI(hasValue);
                params.add(((AttributeValueMolecule) m).getAttribute());
            }
            params.add(m.getRightParameter());
            return leFactory.createAtom(id, params);
        }

        public String toString() {
            return "X[A1,...,An]\n\t=>\n X[A1] and ... and X[An]\n";
        }
    }

    protected class AtomAnonymousIDRule implements NormalizationRule {

        public LogicalExpression apply(LogicalExpression expression) {
            Atom atom = (Atom) expression;
            Identifier id = (Identifier) anonymousIDTranslator.translate(atom.getIdentifier());
            List<Term> args = new ArrayList<Term>();
            for (int i = 0; i < atom.getArity(); i++) {
                Term term = atom.getParameter(i);
                if (term instanceof Identifier) {
                    term = anonymousIDTranslator.translate(term);
                }
                args.add(term);
            }
            return leFactory.createAtom(id, args);
        }

        public boolean isApplicable(LogicalExpression expression) {
            return expression instanceof Atom;
        }
    }

    protected class MoleculeAnonymousIDRule implements NormalizationRule {

        public LogicalExpression apply(LogicalExpression expression) {
            Molecule molecule = (Molecule) expression;
            Term leftOperand = anonymousIDTranslator.translate(molecule.getLeftParameter());
            Term rightOperand = anonymousIDTranslator.translate(molecule.getRightParameter());
            Term attribute = null;
            if (molecule instanceof AttributeMolecule) {
                attribute = anonymousIDTranslator.translate(((AttributeMolecule) molecule).getAttribute());
            }

            // instantiate the appropriate molecule type:
            if (molecule instanceof MembershipMolecule) {
                return leFactory.createMemberShipMolecule(leftOperand, rightOperand);
            }
            else if (molecule instanceof SubConceptMolecule) {
                return leFactory.createSubConceptMolecule(leftOperand, rightOperand);
            }
            else if (molecule instanceof AttributeConstraintMolecule) {
                return leFactory.createAttributeConstraint(leftOperand, attribute, rightOperand);
            }
            else if (molecule instanceof AttributeInferenceMolecule) {
                return leFactory.createAttributeInference(leftOperand, attribute, rightOperand);
            }
            else if (molecule instanceof AttributeValueMolecule) {
                return leFactory.createAttributeValue(leftOperand, attribute, rightOperand);
            }
            else
                throw new RuntimeException("in MoleculeAnonymousIDRule::apply() : reached presumably unreachable code!");
        }

        public boolean isApplicable(LogicalExpression expression) {
            return expression instanceof Molecule;
        }
    }

    public AddOnlyArrayList<NormalizationRule> getRules() {
        return rules;
    }
}
