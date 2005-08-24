package org.deri.wsml.reasoner.normalization.le;

import java.util.*;

import org.omwg.logexpression.*;
import org.omwg.logexpression.terms.Term;

public class MoleculeDecompositionRules extends FixedNormalizationRules
{
    protected static MoleculeDecompositionRules instance;

    private MoleculeDecompositionRules()
    {
        rules.add(new MoleculeDecompositionRule());
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
            Term term = molecule.getTerm();

            // extract subClassOf-statements:
            if(molecule.listSubConceptOf() != null)
            {
                for(Term superclassTerm : (Set<Term>)molecule.listSubConceptOf())
                {
                    simpleMolecules.add(createSubClassOfMolecule(term, superclassTerm));
                }
            }

            // extract instanceOf-statements:
            if(molecule.listMemberOf() != null)
            {
                for(Term classTerm : (Set<Term>)molecule.listMemberOf())
                {
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
    }
}
