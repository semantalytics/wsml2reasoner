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
package logexpapi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.deri.wsmo4j.io.serializer.wsml.LogExprSerializerWSML;
import org.omwg.logicalexpression.CompoundMolecule;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.Molecule;
import org.omwg.logicalexpression.SubConceptMolecule;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Ontology;
import org.wsmo.common.IRI;
import org.wsmo.common.Namespace;
import org.wsmo.factory.Factory;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;

public class LogExpTest extends TestCase {

    private LogExprSerializerWSML logExprSerializer = null;

    private LogicalExpressionFactory leFactory = null;

    private WsmoFactory wsmoFactory = null;

    private Ontology o = null;

    public static void main(String[] args) {
        junit.textui.TestRunner.run(LogExpTest.class);
    }

    public void testFact() throws Exception {
        LogicalExpression qExpression = leFactory.createLogicalExpression(
                "arc(a,b)", o);
        System.out.println("Parsed fact:");
        System.out.println(logExprSerializer.serialize(qExpression));
        System.out.println("-------");
    }

    public void testRule() throws Exception {
        LogicalExpression qExpression = leFactory.createLogicalExpression(
                "path(?x,?y) :- arc(?x,?z) and path(?z,?y).", o);
        System.out.println("Parsed fact:");
        System.out.println(logExprSerializer.serialize(qExpression));
        System.out.println("-------");
    }

    public void testImpliedBy() throws Exception {
        LogicalExpression qExpression = leFactory.createLogicalExpression(
                "path(?x,?y) impliedBy arc(?x,?z) and path(?z,?y).", o);
        System.out.println("Parsed fact:");
        System.out.println(logExprSerializer.serialize(qExpression));
        System.out.println("-------");
    }

    public void testMoleculeRule() throws Exception {
        LogicalExpression qExpression = leFactory
                .createLogicalExpression(
                        "?x[arc hasValue ?y] :- ?x[arc hasValue ?z] and ?z[path hasValue ?y].",
                        o);
        System.out.println("Parsed fact:");
        System.out.println(logExprSerializer.serialize(qExpression));
        System.out.println("-------");
    }

    public void testContraint() throws Exception {
        LogicalExpression qExpression = leFactory.createLogicalExpression(
                "!- ?x[arc hasValue ?z] and ?z[path hasValue ?y].", o);
        System.out.println("Parsed fact:");
        System.out.println(logExprSerializer.serialize(qExpression));
        System.out.println("-------");
    }

    public void testComplexRuleOne() throws Exception {
        LogicalExpression qExpression = leFactory
                .createLogicalExpression(
                        "?y memberOf ?z impliedBy ?z memberOf ?x :- naf ?y[a hasValue ?x, start hasValue _date(2005,6,6,0,0), nr hasValue 10, name hasValue \"myName\"] and p(?x,?y)",
                        o);
        System.out.println("Parsed fact:");
        System.out.println(logExprSerializer.serialize(qExpression));
        System.out.println("-------");
    }

    public void testComplexRuleTwo() throws Exception {
        LogicalExpression qExpression = leFactory
                .createLogicalExpression(
                        "?y memberOf ?x impliedBy ?y memberOf ?z impliedBy ?z memberOf ?x :- naf ?y[a hasValue ?x, start hasValue _date(2005,6,6,0,0), nr hasValue 10, name hasValue \"myName\"] and p(?x,?y)",
                        o);
        System.out.println("Parsed fact:");
        System.out.println(logExprSerializer.serialize(qExpression));
        System.out.println("-------");
    }

    public void testComplexRuleThree() throws Exception {
        LogicalExpression qExpression = leFactory
                .createLogicalExpression(
                        "?y memberOf ?x impliedBy (?y memberOf ?z impliedBy ?z memberOf ?x) :- naf ?y[a hasValue ?x, start hasValue _date(2005,6,6,0,0), nr hasValue 10, name hasValue \"myName\"] and p(?x,?y)",
                        o);
        System.out.println("Parsed fact:");
        System.out.println(logExprSerializer.serialize(qExpression));
        System.out.println("-------");
    }

    public void testComplexRule4() throws Exception {
        LogicalExpression qExpression = leFactory
                .createLogicalExpression(
                        "?y memberOf ?x impliedBy (?y memberOf ?z impliedBy ?z memberOf ?x) :- naf ?y[a hasValue ?x, start hasValue _date(2005,6,6,0,0), nr hasValue 10, name hasValue \"myName\"] and (p(?x,?y) or q(?x,?y))",
                        o);
        System.out.println("Parsed fact:");
        System.out.println(logExprSerializer.serialize(qExpression));
        System.out.println("-------");
    }

    public void testMultipleImpliedBy() throws Exception {
        LogicalExpression qExpression = leFactory.createLogicalExpression(
                "p(?x,?y) impliedBy q(?x,?y) impliedBy r(?x,?y)", o);
        System.out.println("Parsed fact:");
        System.out.println(logExprSerializer.serialize(qExpression));
        System.out.println("-------");
    }

    public void testSubconceptOfMolecule() throws Exception {
        LogicalExpression qExpression = leFactory
                .createLogicalExpression("?x memberOf ?y");
        System.out.println("Parsed fact:");
        System.out.println(logExprSerializer.serialize(qExpression));
        System.out.println("-------");
    }

    public void testUnificationFact() throws Exception {
        LogicalExpression qExpression = leFactory.createLogicalExpression(
                "x=y", o); // Atom with
        // Constants.EQUAL
        System.out.println("Parsed fact:");
        System.out.println(logExprSerializer.serialize(qExpression));
        System.out.println("-------");
    }

    public void testInequalityFact() throws Exception {
        LogicalExpression qExpression = leFactory.createLogicalExpression(
                "x!=y", o); // Atom with
        // Constants.INEQUAL
        System.out.println("Parsed fact:");
        System.out.println(logExprSerializer.serialize(qExpression));
        System.out.println("-------");
    }

    public void testManySubconceptOfMolecule() throws Exception {
        Term sub = wsmoFactory.createIRI("sub1");
        Term super1 = wsmoFactory.createIRI("super1");
        Term super2 = wsmoFactory.createIRI("super2");
        List<SubConceptMolecule> molecules = new ArrayList<SubConceptMolecule>();
        molecules.add(leFactory.createSubConceptMolecule(sub, super1));
        molecules.add(leFactory.createSubConceptMolecule(sub, super2));
        CompoundMolecule qExpression = leFactory
                .createCompoundMolecule(molecules);
        System.out.println("Created fact:");
        System.out.println(logExprSerializer.serialize(qExpression));
        System.out.println("-------");
    }

    public void testMixedfMolecule1() throws Exception {
        Term sub = wsmoFactory.createIRI("sub1");
        Term super1 = wsmoFactory.createIRI("super1");
        Term super2 = wsmoFactory.createIRI("super2");
        Term a1 = wsmoFactory.createIRI("a1");
        Term v1 = wsmoFactory.createIRI("v1");
        Term a2 = wsmoFactory.createIRI("a2");
        Term v2 = wsmoFactory.createIRI("v2");
        List<Molecule> molecules = new ArrayList<Molecule>();
        molecules.add(leFactory.createSubConceptMolecule(sub, super1));
        molecules.add(leFactory.createSubConceptMolecule(sub, super2));
        molecules.add(leFactory.createAttributeValue(sub, a1, v1));
        molecules.add(leFactory.createAttributeValue(sub, a2, v2));
        CompoundMolecule qExpression = leFactory
                .createCompoundMolecule(molecules);
        System.out.println("Created fact:");
        System.out.println(logExprSerializer.serialize(qExpression));
        System.out.println("-------");
    }

    public void testMixedfMolecule2() throws Exception {
        Term sub = wsmoFactory.createIRI("sub1");
        Term meta1 = wsmoFactory.createIRI("meta1");
        Term meta2 = wsmoFactory.createIRI("meta2");
        Term a1 = wsmoFactory.createIRI("a1");
        Term v1 = wsmoFactory.createIRI("v1");
        Term a2 = wsmoFactory.createIRI("a2");
        Term v2 = wsmoFactory.createIRI("v2");
        List<Molecule> molecules = new ArrayList<Molecule>();
        molecules.add(leFactory.createMemberShipMolecule(sub, meta1));
        molecules.add(leFactory.createMemberShipMolecule(sub, meta2));
        molecules.add(leFactory.createAttributeValue(sub, a1, v1));
        molecules.add(leFactory.createAttributeValue(sub, a2, v2));
        CompoundMolecule qExpression = leFactory
                .createCompoundMolecule(molecules);
        System.out.println("Created fact:");
        System.out.println(logExprSerializer.serialize(qExpression));
        System.out.println("-------");
    }

    public void testMixedfMolecule3() throws Exception {
        Term sub = wsmoFactory.createIRI("sub1");
        Term super1 = wsmoFactory.createIRI("super1");
        Term super2 = wsmoFactory.createIRI("super2");
        Term a1 = wsmoFactory.createIRI("a1");
        Term v1 = wsmoFactory.createIRI("v1");
        Term a2 = wsmoFactory.createIRI("a2");
        Term v2 = wsmoFactory.createIRI("v2");
        Term implType = wsmoFactory.createIRI("implied");
        Term typeConstr = wsmoFactory.createIRI("constrained");
        List<Molecule> molecules = new ArrayList<Molecule>();
        molecules.add(leFactory.createSubConceptMolecule(sub, super1));
        molecules.add(leFactory.createSubConceptMolecule(sub, super2));
        molecules.add(leFactory.createAttributeValue(sub, a1, v1));
        molecules.add(leFactory.createAttributeValue(sub, a2, v2));
        molecules.add(leFactory.createAttributeInference(sub, a2, implType));
        molecules.add(leFactory.createAttributeConstraint(sub, a2, typeConstr));
        CompoundMolecule qExpression = leFactory
                .createCompoundMolecule(molecules);
        System.out.println("Created fact:");
        System.out.println(logExprSerializer.serialize(qExpression));
        System.out.println("-------");
    }

    public void testAnonymousIds() throws Exception {
        LogicalExpression qExpression = leFactory
                .createLogicalExpression("_# memberOf _#1 and _#1 subConceptOf _#");
        System.out.println("Parsed fact:");
        System.out.println(logExprSerializer.serialize(qExpression));
        System.out.println("-------");
    }

    @Override
    protected void setUp() throws Exception {
        WsmoFactory wf = Factory.createWsmoFactory(null);
        IRI ontoIri = wf.createIRI("urn:test");
        Namespace ns = wf.createNamespace("ns", ontoIri);
        o = wf.createOntology(ontoIri);
        o.setDefaultNamespace(ns);
        logExprSerializer = new LogExprSerializerWSML(o);
        Map createParams = new HashMap();
        createParams.put(Factory.PROVIDER_CLASS,
                "org.deri.wsmo4j.logexpression.LogicalExpressionFactoryImpl");
        leFactory = (LogicalExpressionFactory) Factory
                .createLogicalExpressionFactory(null);
        wsmoFactory = Factory.createWsmoFactory(null);
    }

}
