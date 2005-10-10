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
import org.omwg.logicalexpression.AttrSpecification;
import org.omwg.logicalexpression.Binary;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.Molecule;
import org.omwg.logicalexpression.terms.Identifier;
import org.omwg.logicalexpression.terms.NbAnonymousID;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.logicalexpression.terms.UnNbAnonymousID;
import org.wsml.reasoner.transformation.AnonymousIdUtils;
import org.wsml.reasoner.transformation.AnonymousIdUtils.AnonymousIdTranslator;

/**
 * This singleton class represents a set of normalization rules for replacing
 * complex molecules inside a logical expression by conjunctions of simple ones.
 * 
 * @author Stephan Grimm, FZI Karlsruhe
 */
public class MoleculeDecompositionRules extends FixedModificationRules
{
    protected static MoleculeDecompositionRules instance;

    private MoleculeDecompositionRules()
    {
        rules.add(new MoleculeDecompositionRule());
        rules.add(new AtomAnonymousIDRule());
    }

    public static MoleculeDecompositionRules instantiate()
    {
        if(instance == null)
        {
            instance = new MoleculeDecompositionRules();
        }
        return instance;
    }

    protected class MoleculeDecompositionRule implements NormalizationRule
    {
        protected AnonymousIdTranslator anonymousIDTranslator;

        public MoleculeDecompositionRule()
        {
            anonymousIDTranslator = AnonymousIdUtils.getAnonymousIdTranslator();
        }

        public LogicalExpression apply(LogicalExpression expression)
        {
            Set<LogicalExpression> simpleMolecules = decomposeMolecule((Molecule)expression);
            return buildConjunction(simpleMolecules);
        }

        public boolean isApplicable(LogicalExpression expression)
        {
            return expression instanceof Molecule;
        }

        protected Set<LogicalExpression> decomposeMolecule(Molecule molecule)
        {
            Set<LogicalExpression> simpleMolecules = new HashSet<LogicalExpression>();
            Term term = replaceAnonymous(molecule.getTerm());

            // extract subClassOf-statements:
            if(molecule.listSubConceptOf() != null)
            {
                for(Term superclassTerm : (Set<Term>)molecule.listSubConceptOf())
                {
                    superclassTerm = replaceAnonymous(superclassTerm);
                    simpleMolecules.add(createSubClassOfMolecule(term, superclassTerm));
                }
            }

            // extract instanceOf-statements:
            if(molecule.listMemberOf() != null)
            {
                for(Term classTerm : (Set<Term>)molecule.listMemberOf())
                {
                    classTerm = replaceAnonymous(classTerm);
                    simpleMolecules.add(createInstanceOfMolecule(term, classTerm));
                }
            }

            // extract attribute specification constructs:
            if(molecule.listAttributeSpecifications() != null)
            {
                for(AttrSpecification attrSpec : (Set<AttrSpecification>)molecule.listAttributeSpecifications())
                {
                    Set<Term> arguments = attrSpec.listArguments();
                    for(Term argTerm : arguments)
                    {
                        argTerm = replaceAnonymous(argTerm);
                        Set<Term> singletonTerm = new HashSet<Term>();
                        singletonTerm.add(argTerm);
                        AttrSpecification singleAttrSpec = leFactory.createAttrSpecification(attrSpec.getOperator(), attrSpec.getName(), singletonTerm);
                        simpleMolecules.add(createAttrSpecMolecule(term, singleAttrSpec));
                    }
                }
            }

            return simpleMolecules;
        }

        protected Molecule createSubClassOfMolecule(Term subclassTerm, Term superclassTerm)
        {
            Set<Term> termSingleton = new HashSet<Term>();
            termSingleton.add(superclassTerm);
            return leFactory.createMolecule(subclassTerm, termSingleton, null, null);
        }

        protected Molecule createInstanceOfMolecule(Term instanceTerm, Term classTerm)
        {
            Set<Term> termSingleton = new HashSet<Term>();
            termSingleton.add(classTerm);
            return leFactory.createMolecule(instanceTerm, null, termSingleton, null);
        }

        protected Molecule createAttrSpecMolecule(Term term, AttrSpecification attrSpec)
        {
            Set<AttrSpecification> attrSpecSingleton = new HashSet<AttrSpecification>();
            attrSpecSingleton.add(attrSpec);
            return leFactory.createMolecule(term, null, null, attrSpecSingleton);
        }

        protected LogicalExpression buildConjunction(Set<LogicalExpression> expressions)
        {
            LogicalExpression conjunction = null;
            Iterator<LogicalExpression> leIterator = expressions.iterator();
            if(leIterator.hasNext())
            {
                conjunction = leIterator.next();
            }
            while(leIterator.hasNext())
            {
                conjunction = leFactory.createBinary(Binary.AND, conjunction, leIterator.next());
            }
            return conjunction;
        }

        protected Term replaceAnonymous(Term term)
        {
            if(term instanceof UnNbAnonymousID || term instanceof NbAnonymousID)
            {
                return anonymousIDTranslator.translate((Identifier)term);
            }
            else
                return term;
        }

        public String toString()
        {
            return "X[A1,...,An]\n\t=>\n X[A1] and ... and X[An]\n";
        }
    }

    protected class AtomAnonymousIDRule implements NormalizationRule
    {
        protected AnonymousIdTranslator anonymousIDTranslator;

        public AtomAnonymousIDRule()
        {
            anonymousIDTranslator = AnonymousIdUtils.getAnonymousIdTranslator();
        }

        public LogicalExpression apply(LogicalExpression expression)
        {
            Atom atom = (Atom)expression;
            Identifier id = anonymousIDTranslator.translate(atom, atom.getIdentifier());
            List<Term> args = new ArrayList<Term>();
            for(int i = 0; i < atom.getArity(); i++)
            {
                Term term = atom.getParameter(i);
                if(term instanceof Identifier)
                {
                    term = anonymousIDTranslator.translate(atom, (Identifier)term);
                }
                args.add(term);
            }
            return leFactory.createAtom(id, args);
        }

        public boolean isApplicable(LogicalExpression expression)
        {
            return expression instanceof Atom;
        }
    }
}
