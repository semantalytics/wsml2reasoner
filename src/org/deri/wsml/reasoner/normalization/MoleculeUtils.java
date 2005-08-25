package org.deri.wsml.reasoner.normalization;

import org.omwg.logexpression.AttrSpecification;
import org.omwg.logexpression.Molecule;
import org.omwg.logexpression.terms.Term;

import com.sun.org.apache.xerces.internal.util.AttributesProxy;

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
        if (!isSimpleAttrValue(m) || isSimpleImpliesType(m) || isSimpleOfType(m))
            throw new IllegalArgumentException("Wrong molecule type!");
        AttrSpecification spec = (AttrSpecification) m.listAttributeSpecifications().iterator().next();
        Term result = (Term) spec.getName();
        return result;
    }

}
