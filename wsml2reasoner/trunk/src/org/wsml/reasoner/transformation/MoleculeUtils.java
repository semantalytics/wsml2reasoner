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
package org.wsml.reasoner.transformation;

import org.omwg.logicalexpression.AttrSpecification;
import org.omwg.logicalexpression.Molecule;
import org.omwg.logicalexpression.terms.Term;

/**
 * A utility class to handle atomic molecules until we do not have proper
 * subclasses for them
 * 
 * @author Gabor Nagypal (FZI)
 * 
 */
public abstract class MoleculeUtils {

    public static boolean isSimpleSubconceptOf(Molecule m) {
        return (m.listSubConceptOf() != null && m.listSubConceptOf().size() == 1);
    }

    public static boolean isSimpleMemberOf(Molecule m) {
        return (m.listMemberOf() != null && m.listMemberOf().size() == 1);
    }

    public static boolean isSimpleOfType(Molecule m) {
        if (m.listAttributeSpecifications() == null
                || m.listAttributeSpecifications().size() != 1)
            return false;
        AttrSpecification spec = (AttrSpecification) m
                .listAttributeSpecifications().iterator().next();
        if (spec.getOperator() != AttrSpecification.ATTR_CONSTRAINT
                || spec.listArguments().size() != 1)
            return false;
        return true;
    }

    public static boolean isSimpleImpliesType(Molecule m) {
        if (m.listAttributeSpecifications() == null
                || m.listAttributeSpecifications().size() != 1)
            return false;
        AttrSpecification spec = (AttrSpecification) m
                .listAttributeSpecifications().iterator().next();
        if (spec.getOperator() != AttrSpecification.ATTR_INFERENCE
                || spec.listArguments().size() != 1)
            return false;
        return true;
    }

    public static boolean isSimpleAttrValue(Molecule m) {
        if (m.listAttributeSpecifications() == null
                || m.listAttributeSpecifications().size() != 1)
            return false;
        AttrSpecification spec = (AttrSpecification) m
                .listAttributeSpecifications().iterator().next();
        if (spec.getOperator() != AttrSpecification.ATTR_VALUE
                || spec.listArguments().size() != 1)
            return false;
        return true;
    }
    
    public static boolean isSimple(Molecule m) {
        return isSimpleAttrValue(m) || isSimpleImpliesType(m) || isSimpleMemberOf(m) || isSimpleOfType(m) || isSimpleSubconceptOf(m);
    }

    public static Term getSuperConcept(Molecule m) {
        if (!isSimpleSubconceptOf(m))
            throw new IllegalArgumentException("Wrong molecule type!");
        Term result = (Term) m.listSubConceptOf().iterator().next();
        return result;
    }

    public static Term getParentConcept(Molecule m) {
        if (!isSimpleMemberOf(m))
            throw new IllegalArgumentException("Wrong molecule type!");
        Term result = (Term) m.listMemberOf().iterator().next();
        return result;
    }

    public static Term getTypeConstraint(Molecule m) {
        if (!isSimpleOfType(m))
            throw new IllegalArgumentException("Wrong molecule type!");
        AttrSpecification spec = (AttrSpecification) m.listAttributeSpecifications().iterator().next();
        Term result = (Term) spec.listArguments().iterator().next();
        return result;
    }

    public static Term getImpliedType(Molecule m) {
        if (!isSimpleImpliesType(m))
            throw new IllegalArgumentException("Wrong molecule type!");
        AttrSpecification spec = (AttrSpecification) m.listAttributeSpecifications().iterator().next();
        Term result = (Term) spec.listArguments().iterator().next();
        return result;
    }

    public static Term getAttrValue(Molecule m) {
        if (!isSimpleAttrValue(m))
            throw new IllegalArgumentException("Wrong molecule type!");
        AttrSpecification spec = (AttrSpecification) m.listAttributeSpecifications().iterator().next();
        Term result = (Term) spec.listArguments().iterator().next();
        return result;
    }
    
    public static Term getAttrName(Molecule m) {
        if (!(isSimpleAttrValue(m) || isSimpleImpliesType(m) || isSimpleOfType(m)))
            throw new IllegalArgumentException("Wrong molecule type!");
        AttrSpecification spec = (AttrSpecification) m.listAttributeSpecifications().iterator().next();
        Term result = (Term) spec.getName();
        return result;
    }

}
