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

import junit.framework.TestCase;

import org.deri.wsmo4j.io.parser.wsml.LogExprParserImpl;
import org.omwg.logexpression.Molecule;
import org.omwg.logexpression.terms.Term;
import org.omwg.ontology.Ontology;
import org.wsml.reasoner.transformation.MoleculeUtils;
import org.wsmo.common.IRI;
import org.wsmo.common.Namespace;
import org.wsmo.factory.Factory;
import org.wsmo.factory.WsmoFactory;

public class MoleculeUtilsTest extends TestCase {

    private org.omwg.logexpression.io.Parser leParser = null;

    private Molecule m = null;

    public static void main(String[] args) {
        junit.textui.TestRunner.run(MoleculeUtilsTest.class);
    }

    @Override
    protected void setUp() throws Exception {
        WsmoFactory wf = Factory.createWsmoFactory(null);
        IRI ontoIri = wf.createIRI("urn:test");
        Namespace ns = wf.createNamespace("ns", ontoIri);
        Ontology o = wf.createOntology(ontoIri);
        o.setDefaultNamespace(ns);
        leParser = LogExprParserImpl.getInstance(o);
    }

    public void testIsSimpleSubconceptOf() throws Exception {
        m = (Molecule) leParser.parse("x subConceptOf y");
        assertTrue(MoleculeUtils.isSimpleSubconceptOf(m));
        m = (Molecule) leParser.parse("x subConceptOf {y, z}");
        assertFalse(MoleculeUtils.isSimpleSubconceptOf(m));
        m = (Molecule) leParser.parse("x memberOf y");
        assertFalse(MoleculeUtils.isSimpleSubconceptOf(m));
        m = (Molecule) leParser.parse("x memberOf {y, z}");
        assertFalse(MoleculeUtils.isSimpleSubconceptOf(m));
        m = (Molecule) leParser.parse("x[a hasValue y]");
        assertFalse(MoleculeUtils.isSimpleSubconceptOf(m));
        m = (Molecule) leParser.parse("x[a hasValue {y,z}]");
        assertFalse(MoleculeUtils.isSimpleSubconceptOf(m));
        m = (Molecule) leParser.parse("x[a hasValue {y,z}, b hasValue w]");
        assertFalse(MoleculeUtils.isSimpleSubconceptOf(m));
        m = (Molecule) leParser.parse("x[a ofType y]");
        assertFalse(MoleculeUtils.isSimpleSubconceptOf(m));
        m = (Molecule) leParser.parse("x[a impliesType y]");
        assertFalse(MoleculeUtils.isSimpleSubconceptOf(m));
    }

    public void testIsSimpleAttrValue() throws Exception {
        m = (Molecule) leParser.parse("x[a hasValue y]");
        assertTrue(MoleculeUtils.isSimpleAttrValue(m));
        m = (Molecule) leParser.parse("x subConceptOf y");
        assertFalse(MoleculeUtils.isSimpleAttrValue(m));
        m = (Molecule) leParser.parse("x subConceptOf {y, z}");
        assertFalse(MoleculeUtils.isSimpleAttrValue(m));
        m = (Molecule) leParser.parse("x memberOf y");
        assertFalse(MoleculeUtils.isSimpleAttrValue(m));
        m = (Molecule) leParser.parse("x memberOf {y, z}");
        assertFalse(MoleculeUtils.isSimpleAttrValue(m));
        m = (Molecule) leParser.parse("x[a hasValue {y,z}]");
        assertFalse(MoleculeUtils.isSimpleAttrValue(m));
        m = (Molecule) leParser.parse("x[a hasValue {y,z}, b hasValue w]");
        assertFalse(MoleculeUtils.isSimpleAttrValue(m));
        m = (Molecule) leParser.parse("x[a ofType y]");
        assertFalse(MoleculeUtils.isSimpleAttrValue(m));
        m = (Molecule) leParser.parse("x[a impliesType y]");
        assertFalse(MoleculeUtils.isSimpleAttrValue(m));
        m = (Molecule) leParser.parse("x[a hasValue y, a ofType y]");
        assertFalse(MoleculeUtils.isSimpleAttrValue(m));
    }

    public void testIsSimpleMemberOf() throws Exception {
        m = (Molecule) leParser.parse("x memberOf y");
        assertTrue(MoleculeUtils.isSimpleMemberOf(m));
        m = (Molecule) leParser.parse("x[a hasValue y]");
        assertFalse(MoleculeUtils.isSimpleMemberOf(m));
        m = (Molecule) leParser.parse("x subConceptOf y");
        assertFalse(MoleculeUtils.isSimpleMemberOf(m));
        m = (Molecule) leParser.parse("x subConceptOf {y, z}");
        assertFalse(MoleculeUtils.isSimpleMemberOf(m));
        m = (Molecule) leParser.parse("x memberOf {y, z}");
        assertFalse(MoleculeUtils.isSimpleMemberOf(m));
        m = (Molecule) leParser.parse("x[a hasValue {y,z}]");
        assertFalse(MoleculeUtils.isSimpleMemberOf(m));
        m = (Molecule) leParser.parse("x[a hasValue {y,z}, b hasValue w]");
        assertFalse(MoleculeUtils.isSimpleMemberOf(m));
        m = (Molecule) leParser.parse("x[a ofType y]");
        assertFalse(MoleculeUtils.isSimpleMemberOf(m));
        m = (Molecule) leParser.parse("x[a impliesType y]");
        assertFalse(MoleculeUtils.isSimpleMemberOf(m));
        m = (Molecule) leParser.parse("x[a hasValue y, a ofType y]");
        assertFalse(MoleculeUtils.isSimpleMemberOf(m));
    }

    public void testIsSimpleOfType() throws Exception {
        m = (Molecule) leParser.parse("x[a ofType y]");
        assertTrue(MoleculeUtils.isSimpleOfType(m));
        m = (Molecule) leParser.parse("x[a ofType y, b ofType z]");
        assertFalse(MoleculeUtils.isSimpleOfType(m));
        m = (Molecule) leParser.parse("x memberOf y");
        assertFalse(MoleculeUtils.isSimpleOfType(m));
        m = (Molecule) leParser.parse("x[a hasValue y]");
        assertFalse(MoleculeUtils.isSimpleOfType(m));
        m = (Molecule) leParser.parse("x subConceptOf y");
        assertFalse(MoleculeUtils.isSimpleOfType(m));
        m = (Molecule) leParser.parse("x subConceptOf {y, z}");
        assertFalse(MoleculeUtils.isSimpleOfType(m));
        m = (Molecule) leParser.parse("x memberOf {y, z}");
        assertFalse(MoleculeUtils.isSimpleOfType(m));
        m = (Molecule) leParser.parse("x[a hasValue {y,z}]");
        assertFalse(MoleculeUtils.isSimpleOfType(m));
        m = (Molecule) leParser.parse("x[a hasValue {y,z}, b hasValue w]");
        assertFalse(MoleculeUtils.isSimpleOfType(m));
        m = (Molecule) leParser.parse("x[a impliesType y]");
        assertFalse(MoleculeUtils.isSimpleOfType(m));
        m = (Molecule) leParser.parse("x[a hasValue y, a ofType y]");
        assertFalse(MoleculeUtils.isSimpleOfType(m));
    }

    public void testIsSimpleImpliesType() throws Exception {
        m = (Molecule) leParser.parse("x[a impliesType y]");
        assertTrue(MoleculeUtils.isSimpleImpliesType(m));
        m = (Molecule) leParser.parse("x[a impliesType y, b impliesType z]");
        assertFalse(MoleculeUtils.isSimpleImpliesType(m));
        m = (Molecule) leParser.parse("x[a ofType y]");
        assertFalse(MoleculeUtils.isSimpleImpliesType(m));
        m = (Molecule) leParser.parse("x[a ofType y, b ofType z]");
        assertFalse(MoleculeUtils.isSimpleImpliesType(m));
        m = (Molecule) leParser.parse("x memberOf y");
        assertFalse(MoleculeUtils.isSimpleImpliesType(m));
        m = (Molecule) leParser.parse("x[a hasValue y]");
        assertFalse(MoleculeUtils.isSimpleImpliesType(m));
        m = (Molecule) leParser.parse("x subConceptOf y");
        assertFalse(MoleculeUtils.isSimpleImpliesType(m));
        m = (Molecule) leParser.parse("x subConceptOf {y, z}");
        assertFalse(MoleculeUtils.isSimpleImpliesType(m));
        m = (Molecule) leParser.parse("x memberOf {y, z}");
        assertFalse(MoleculeUtils.isSimpleImpliesType(m));
        m = (Molecule) leParser.parse("x[a hasValue {y,z}]");
        assertFalse(MoleculeUtils.isSimpleImpliesType(m));
        m = (Molecule) leParser.parse("x[a hasValue {y,z}, b hasValue w]");
        assertFalse(MoleculeUtils.isSimpleImpliesType(m));
        m = (Molecule) leParser.parse("x[a hasValue y, a ofType y]");
        assertFalse(MoleculeUtils.isSimpleImpliesType(m));
    }

    public void testGetSuperConcept() throws Exception {
        m = (Molecule) leParser.parse("x subConceptOf y");
        Term sC = MoleculeUtils.getSuperConcept(m);
        assertEquals(sC.toString(), "urn:test#y");
    }

    public void testGetSuperConceptWrongType() throws Exception {
        m = (Molecule) leParser.parse("x memberOf y");
        try {
            MoleculeUtils.getSuperConcept(m);
            fail();
        } catch (Exception expected) {
        }
    }
    
    public void testGetParentConcept() throws Exception {
        m = (Molecule) leParser.parse("x memberOf y");
        Term sC = MoleculeUtils.getParentConcept(m);
        assertEquals(sC.toString(), "urn:test#y");
    }

    public void testGetParentConceptWrongType() throws Exception {
        m = (Molecule) leParser.parse("x subConceptOf y");
        try {
            MoleculeUtils.getParentConcept(m);
            fail();
        } catch (Exception expected) {
        }
    }
    
    public void testGetAttrValue() throws Exception {
        m = (Molecule) leParser.parse("x[a hasValue y]");
        Term sC = MoleculeUtils.getAttrValue(m);
        assertEquals(sC.toString(), "urn:test#y");
    }

    public void testGetAttrValueWrongType() throws Exception {
        m = (Molecule) leParser.parse("x subConceptOf y");
        try {
            MoleculeUtils.getAttrValue(m);
            fail();
        } catch (Exception expected) {
        }
    }
    
    public void testGetAttrName() throws Exception {
        m = (Molecule) leParser.parse("x[a hasValue y]");
        Term sC = MoleculeUtils.getAttrName(m);
        assertEquals(sC.toString(), "urn:test#a");
    }

    public void testGetAttrNameWrongType() throws Exception {
        m = (Molecule) leParser.parse("x subConceptOf y");
        try {
            MoleculeUtils.getAttrName(m);
            fail();
        } catch (Exception expected) {
        }
    }
    
    public void testGetTypeConstraint() throws Exception {
        m = (Molecule) leParser.parse("x[a ofType y]");
        Term sC = MoleculeUtils.getTypeConstraint(m);
        assertEquals(sC.toString(), "urn:test#y");
    }

    public void testGetTypeConstraintWrongType() throws Exception {
        m = (Molecule) leParser.parse("x subConceptOf y");
        try {
            MoleculeUtils.getTypeConstraint(m);
            fail();
        } catch (Exception expected) {
        }
    }
    
    public void testGetImpliedType() throws Exception {
        m = (Molecule) leParser.parse("x[a impliesType y]");
        Term sC = MoleculeUtils.getImpliedType(m);
        assertEquals(sC.toString(), "urn:test#y");
    }

    public void testGetImpliedTypeWrongType() throws Exception {
        m = (Molecule) leParser.parse("x subConceptOf y");
        try {
            MoleculeUtils.getImpliedType(m);
            fail();
        } catch (Exception expected) {
        }
    }

}
