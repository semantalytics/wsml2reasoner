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

import java.util.*;

import junit.framework.TestCase;

import org.deri.wsmo4j.io.parser.wsml.LogExprParserImpl;
import org.deri.wsmo4j.io.serializer.wsml.LogExprSerializerWSML;
import org.omwg.logexpression.AttrSpecification;
import org.omwg.logexpression.LogicalExpression;
import org.omwg.logexpression.LogicalExpressionFactory;
import org.omwg.logexpression.Molecule;
import org.omwg.logexpression.terms.Term;
import org.omwg.ontology.Ontology;
import org.wsmo.common.IRI;
import org.wsmo.common.Namespace;
import org.wsmo.factory.Factory;
import org.wsmo.factory.WsmoFactory;

public class LogExpTest extends TestCase {

    private org.omwg.logexpression.io.Parser leParser = null;

    private org.omwg.logexpression.io.Serializer logExprSerializer = null;

    private LogicalExpressionFactory leFactory = null;

    public static void main(String[] args) {
        junit.textui.TestRunner.run(LogExpTest.class);
    }

    public void testFact() throws Exception {
        LogicalExpression qExpression = leParser.parse("arc(a,b)");
        System.out.println("Parsed fact:");
        System.out.println(logExprSerializer.serialize(qExpression));
        System.out.println("-------");
    }

    public void testRule() throws Exception {
        LogicalExpression qExpression = leParser
                .parse("path(?x,?y) :- arc(?x,?z) and path(?z,?y).");
        System.out.println("Parsed fact:");
        System.out.println(logExprSerializer.serialize(qExpression));
        System.out.println("-------");
    }

    public void testImpliedBy() throws Exception {
        LogicalExpression qExpression = leParser
                .parse("path(?x,?y) impliedBy arc(?x,?z) and path(?z,?y).");
        System.out.println("Parsed fact:");
        System.out.println(logExprSerializer.serialize(qExpression));
        System.out.println("-------");
    }

    public void testMoleculeRule() throws Exception {
        LogicalExpression qExpression = leParser
                .parse("?x[arc hasValue ?y] :- ?x[arc hasValue ?z] and ?z[path hasValue ?y].");
        System.out.println("Parsed fact:");
        System.out.println(logExprSerializer.serialize(qExpression));
        System.out.println("-------");
    }

    public void testContraint() throws Exception {
        LogicalExpression qExpression = leParser
                .parse("!- ?x[arc hasValue ?z] and ?z[path hasValue ?y].");
        System.out.println("Parsed fact:");
        System.out.println(logExprSerializer.serialize(qExpression));
        System.out.println("-------");
    }

    public void testComplexRuleOne() throws Exception {
        LogicalExpression qExpression = leParser
                .parse("?y memberOf ?z impliedBy ?z memberOf ?x :- naf ?y[a hasValue ?x, start hasValue _date(2005,6,6,0,0), nr hasValue 10, name hasValue \"myName\"] and p(?x,?y)");
        System.out.println("Parsed fact:");
        System.out.println(logExprSerializer.serialize(qExpression));
        System.out.println("-------");
    }

    public void testComplexRuleTwo() throws Exception {
        LogicalExpression qExpression = leParser
                .parse("?y memberOf ?x impliedBy ?y memberOf ?z impliedBy ?z memberOf ?x :- naf ?y[a hasValue ?x, start hasValue _date(2005,6,6,0,0), nr hasValue 10, name hasValue \"myName\"] and p(?x,?y)");
        System.out.println("Parsed fact:");
        System.out.println(logExprSerializer.serialize(qExpression));
        System.out.println("-------");
    }

    public void testComplexRuleThree() throws Exception {
        LogicalExpression qExpression = leParser
                .parse("?y memberOf ?x impliedBy (?y memberOf ?z impliedBy ?z memberOf ?x) :- naf ?y[a hasValue ?x, start hasValue _date(2005,6,6,0,0), nr hasValue 10, name hasValue \"myName\"] and p(?x,?y)");
        System.out.println("Parsed fact:");
        System.out.println(logExprSerializer.serialize(qExpression));
        System.out.println("-------");
    }

    public void testComplexRule4() throws Exception {
        LogicalExpression qExpression = leParser
                .parse("?y memberOf ?x impliedBy (?y memberOf ?z impliedBy ?z memberOf ?x) :- naf ?y[a hasValue ?x, start hasValue _date(2005,6,6,0,0), nr hasValue 10, name hasValue \"myName\"] and (p(?x,?y) or q(?x,?y))");
        System.out.println("Parsed fact:");
        System.out.println(logExprSerializer.serialize(qExpression));
        System.out.println("-------");
    }

    public void testMultipleImpliedBy() throws Exception {
        LogicalExpression qExpression = leParser
                .parse("p(?x,?y) impliedBy q(?x,?y) impliedBy r(?x,?y)");
        System.out.println("Parsed fact:");
        System.out.println(logExprSerializer.serialize(qExpression));
        System.out.println("-------");
    }

    public void testSubconceptOfMolecule() throws Exception {
        LogicalExpression qExpression = leParser.parse("?x memberOf ?y");
        System.out.println("Parsed fact:");
        System.out.println(logExprSerializer.serialize(qExpression));
        System.out.println("-------");
    }

    public void testUnificationFact() throws Exception {
        LogicalExpression qExpression = leParser.parse("x=y"); // Atom with
        // Constants.EQUAL
        System.out.println("Parsed fact:");
        System.out.println(logExprSerializer.serialize(qExpression));
        System.out.println("-------");
    }

    public void testInequalityFact() throws Exception {
        LogicalExpression qExpression = leParser.parse("x!=y"); // Atom with
        // Constants.INEQUAL
        System.out.println("Parsed fact:");
        System.out.println(logExprSerializer.serialize(qExpression));
        System.out.println("-------");
    }

    public void testManySubconceptOfMolecule() throws Exception {
        Term sub = leFactory.createIRI("sub1");
        Term super1 = leFactory.createIRI("super1");
        Term super2 = leFactory.createIRI("super2");
        Set<Term> superConcepts = new HashSet<Term>();
        superConcepts.add(super1);
        superConcepts.add(super2);
        Molecule qExpression = leFactory.createMolecule(sub, superConcepts,
                null, null);
        System.out.println("Created fact:");
        System.out.println(logExprSerializer.serialize(qExpression));
        System.out.println("-------");
    }

    public void testMixedfMolecule1() throws Exception {
        Term sub = leFactory.createIRI("sub1");
        Term super1 = leFactory.createIRI("super1");
        Term super2 = leFactory.createIRI("super2");
        Term a1 = leFactory.createIRI("a1");
        Term v1 = leFactory.createIRI("v1");
        Term a2 = leFactory.createIRI("a2");
        Term v2 = leFactory.createIRI("v2");
        Set<Term> superConcepts = new HashSet<Term>();
        superConcepts.add(super1);
        superConcepts.add(super2);
        AttrSpecification spec1 = leFactory.createAttrSpecification(
                AttrSpecification.ATTR_VALUE, a1, Collections.singleton(v1));
        AttrSpecification spec2 = leFactory.createAttrSpecification(
                AttrSpecification.ATTR_VALUE, a2, Collections.singleton(v2));
        Set<AttrSpecification> attrSpecs = new HashSet<AttrSpecification>();
        attrSpecs.add(spec1);
        attrSpecs.add(spec2);
        Molecule qExpression = leFactory.createMolecule(sub, superConcepts,
                null, attrSpecs);
        System.out.println("Created fact:");
        System.out.println(logExprSerializer.serialize(qExpression));
        System.out.println("-------");
    }

    public void testMixedfMolecule2() throws Exception {
        Term sub = leFactory.createIRI("sub1");
        Term meta1 = leFactory.createIRI("meta1");
        Term meta2 = leFactory.createIRI("meta2");
        Term a1 = leFactory.createIRI("a1");
        Term v1 = leFactory.createIRI("v1");
        Term a2 = leFactory.createIRI("a2");
        Term v2 = leFactory.createIRI("v2");
        Set<Term> metaConcepts = new HashSet<Term>();
        metaConcepts.add(meta1);
        metaConcepts.add(meta2);
        AttrSpecification spec1 = leFactory.createAttrSpecification(
                AttrSpecification.ATTR_VALUE, a1, Collections.singleton(v1));
        AttrSpecification spec2 = leFactory.createAttrSpecification(
                AttrSpecification.ATTR_VALUE, a2, Collections.singleton(v2));
        Set<AttrSpecification> attrSpecs = new HashSet<AttrSpecification>();
        attrSpecs.add(spec1);
        attrSpecs.add(spec2);
        Molecule qExpression = leFactory.createMolecule(sub, null,
                metaConcepts, attrSpecs);
        System.out.println("Created fact:");
        System.out.println(logExprSerializer.serialize(qExpression));
        System.out.println("-------");
    }

    public void testMixedfMolecule3() throws Exception {
        Term sub = leFactory.createIRI("sub1");
        Term super1 = leFactory.createIRI("super1");
        Term super2 = leFactory.createIRI("super2");
        Term a1 = leFactory.createIRI("a1");
        Term v1 = leFactory.createIRI("v1");
        Term a2 = leFactory.createIRI("a2");
        Term v2 = leFactory.createIRI("v2");
        Term implType = leFactory.createIRI("implied");
        Term typeConstr = leFactory.createIRI("constrained");
        Set<Term> superConcepts = new HashSet<Term>();
        superConcepts.add(super1);
        superConcepts.add(super2);
        AttrSpecification spec1 = leFactory.createAttrSpecification(
                AttrSpecification.ATTR_VALUE, a1, Collections.singleton(v1));
        AttrSpecification spec2 = leFactory.createAttrSpecification(
                AttrSpecification.ATTR_VALUE, a2, Collections.singleton(v2));
        AttrSpecification spec3 = leFactory.createAttrSpecification(
                AttrSpecification.ATTR_INFERENCE, a2, Collections
                        .singleton(implType));
        AttrSpecification spec4 = leFactory.createAttrSpecification(
                AttrSpecification.ATTR_CONSTRAINT, a2, Collections
                        .singleton(typeConstr));
        Set<AttrSpecification> attrSpecs = new HashSet<AttrSpecification>();
        attrSpecs.add(spec1);
        attrSpecs.add(spec2);
        attrSpecs.add(spec3);
        attrSpecs.add(spec4);
        Molecule qExpression = leFactory.createMolecule(sub, superConcepts,
                null, attrSpecs);
        System.out.println("Created fact:");
        System.out.println(logExprSerializer.serialize(qExpression));
        System.out.println("-------");
    }

    public void testAnonymousIds() throws Exception{
        LogicalExpression qExpression = leParser.parse("_# memberOf _#1 and _#1 subConceptOf _#");
        System.out.println("Parsed fact:");
        System.out.println(logExprSerializer.serialize(qExpression));
        System.out.println("-------");
    }

    @Override
    protected void setUp() throws Exception {
        WsmoFactory wf = Factory.createWsmoFactory(null);
        IRI ontoIri = wf.createIRI("urn:test");
        Namespace ns = wf.createNamespace("ns", ontoIri);
        Ontology o = wf.createOntology(ontoIri);
        o.setDefaultNamespace(ns);
        leParser = LogExprParserImpl.getInstance(o);
        logExprSerializer = new LogExprSerializerWSML(o);
        Map createParams = new HashMap();
        createParams.put(Factory.PROVIDER_CLASS,
                "org.deri.wsmo4j.logexpression.LogicalExpressionFactoryImpl");
        leFactory = (LogicalExpressionFactory) Factory
                .createLogicalExpressionFactory(createParams);
    }

}
