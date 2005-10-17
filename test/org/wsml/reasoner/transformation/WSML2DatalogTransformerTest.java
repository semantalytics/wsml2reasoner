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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.deri.wsmo4j.io.parser.wsml.LogExprParserImpl;
import org.deri.wsmo4j.io.serializer.wsml.LogExprSerializerWSML;
import org.omwg.logicalexpression.Constants;
import org.omwg.logicalexpression.LogicalExpression;
import org.wsmo.factory.LogicalExpressionFactory;
import org.omwg.ontology.Ontology;
import org.wsml.reasoner.datalog.Constant;
import org.wsml.reasoner.datalog.DatalogException;
import org.wsml.reasoner.datalog.Literal;
import org.wsml.reasoner.datalog.Predicate;
import org.wsml.reasoner.datalog.Program;
import org.wsml.reasoner.datalog.Rule;
import org.wsml.reasoner.datalog.Variable;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsml.reasoner.transformation.WSML2DatalogTransformer;
import org.wsmo.common.IRI;
import org.wsmo.common.Namespace;
import org.wsmo.factory.Factory;
import org.wsmo.factory.WsmoFactory;

public class WSML2DatalogTransformerTest extends TestCase {

    private LogExprSerializerWSML logExprSerializer = null;
    
    private Ontology o = null;

    private LogicalExpressionFactory leFactory = null;

    private WSML2DatalogTransformer cut = null;

    public static void main(String[] args) {
        junit.textui.TestRunner.run(WSML2DatalogTransformerTest.class);
    }

    public void testFact() throws Exception {
        LogicalExpression qExpression = leFactory.createLogicalExpression("arc(a,b)", o);
        System.out.println("Parsed fact:");
        System.out.println(logExprSerializer.serialize(qExpression));
        Program p = cut.transform(qExpression);
        System.out.println("Program:");
        System.out.println(p);
        assertEquals(p.size(), 1);
        Rule expected = new Rule(
                new Literal(new Predicate("urn:test#arc", 2),
                        Literal.NegationType.NONNEGATED,
                        new org.wsml.reasoner.datalog.Term[] {
                                new Constant("urn:test#a"),
                                new Constant("urn:test#b") }));
        assertEquals(expected, p.get(0));
        System.out.println("-------");
    }

    public void testRule() throws Exception {
        LogicalExpression qExpression = leFactory.createLogicalExpression("path(?x,?y) :- arc(?x,?z) and path(?z,?y).", o);
        System.out.println("Parsed fact:");
        System.out.println(logExprSerializer.serialize(qExpression));
        Program p = cut.transform(qExpression);
        System.out.println("Program:");
        System.out.println(p);
        assertEquals(p.size(), 1);
        Literal head = new Literal(new Predicate("urn:test#path", 2),
                Literal.NegationType.NONNEGATED,
                new org.wsml.reasoner.datalog.Term[] {
                        new Variable("x"), new Variable("y") });
        List<Literal> body = new ArrayList<Literal>();
        body.add(new Literal(new Predicate("urn:test#arc", 2),
                Literal.NegationType.NONNEGATED,
                new org.wsml.reasoner.datalog.Term[] {
                        new Variable("x"), new Variable("z") }));
        body.add(new Literal(new Predicate("urn:test#path", 2),
                Literal.NegationType.NONNEGATED,
                new org.wsml.reasoner.datalog.Term[] {
                        new Variable("z"), new Variable("y") }));
        Rule expected = new Rule(head, body);
        assertEquals(expected, p.get(0));
        System.out.println("-------");
    }

    public void testImpliedBy() throws Exception {
        LogicalExpression qExpression = leFactory.createLogicalExpression("path(?x,?y) impliedBy arc(?x,?z) and path(?z,?y).", o);
        System.out.println("Parsed fact:");
        System.out.println(logExprSerializer.serialize(qExpression));
        Program p = cut.transform(qExpression);
        System.out.println("Program:");
        System.out.println(p);
        assertEquals(p.size(), 1);
        Literal head = new Literal(new Predicate("urn:test#path", 2),
                Literal.NegationType.NONNEGATED,
                new org.wsml.reasoner.datalog.Term[] {
                        new Variable("x"), new Variable("y") });
        List<Literal> body = new ArrayList<Literal>();
        body.add(new Literal(new Predicate("urn:test#arc", 2),
                Literal.NegationType.NONNEGATED,
                new org.wsml.reasoner.datalog.Term[] {
                        new Variable("x"), new Variable("z") }));
        body.add(new Literal(new Predicate("urn:test#path", 2),
                Literal.NegationType.NONNEGATED,
                new org.wsml.reasoner.datalog.Term[] {
                        new Variable("z"), new Variable("y") }));
        Rule expected = new Rule(head, body);
        assertEquals(expected, p.get(0));
        System.out.println("-------");
    }

    public void testMoleculeRule() throws Exception {
        LogicalExpression qExpression = leFactory.createLogicalExpression("?x[arc hasValue ?y] :- ?x[arc hasValue ?z] and ?z[path hasValue ?y].", o);
        System.out.println("Parsed fact:");
        System.out.println(logExprSerializer.serialize(qExpression));
        Program p = cut.transform(qExpression);
        System.out.println("Program:");
        System.out.println(p);
        assertEquals(p.size(), 1);
        Literal head = new Literal(new Predicate("wsml-has-value", 3),
                Literal.NegationType.NONNEGATED,
                new org.wsml.reasoner.datalog.Term[] {
                        new Variable("x"), new Constant("urn:test#arc"),
                        new Variable("y") });
        List<Literal> body = new ArrayList<Literal>();
        body.add(new Literal(new Predicate("wsml-has-value", 3),
                Literal.NegationType.NONNEGATED,
                new org.wsml.reasoner.datalog.Term[] {
                        new Variable("x"), new Constant("urn:test#arc"),
                        new Variable("z") }));
        body.add(new Literal(new Predicate("wsml-has-value", 3),
                Literal.NegationType.NONNEGATED,
                new org.wsml.reasoner.datalog.Term[] {
                        new Variable("z"), new Constant("urn:test#path"),
                        new Variable("y") }));
        Rule expected = new Rule(head, body);
        assertEquals(expected, p.get(0));
        System.out.println("-------");
    }

    public void testConstraint() throws Exception {
        LogicalExpression qExpression = leFactory.createLogicalExpression("!- ?x[arc hasValue ?z] and ?z[path hasValue ?y].", o);
        System.out.println("Parsed fact:");
        System.out.println(logExprSerializer.serialize(qExpression));
        Program p = cut.transform(qExpression);
        System.out.println("Program:");
        System.out.println(p);
        assertEquals(p.size(), 1);
        Literal head = null;
        List<Literal> body = new ArrayList<Literal>();
        body.add(new Literal(new Predicate("wsml-has-value", 3),
                Literal.NegationType.NONNEGATED,
                new org.wsml.reasoner.datalog.Term[] {
                        new Variable("x"), new Constant("urn:test#arc"),
                        new Variable("z") }));
        body.add(new Literal(new Predicate("wsml-has-value", 3),
                Literal.NegationType.NONNEGATED,
                new org.wsml.reasoner.datalog.Term[] {
                        new Variable("z"), new Constant("urn:test#path"),
                        new Variable("y") }));
        Rule expected = new Rule(head, body);
        assertEquals(expected, p.get(0));
        System.out.println("-------");
    }

    public void testMultipleImpliedBy() throws Exception {
        LogicalExpression qExpression = leFactory.createLogicalExpression("p(?x,?y) impliedBy q(?x,?y) impliedBy r(?x,?y)", o);
        System.out.println("Parsed fact:");
        System.out.println(logExprSerializer.serialize(qExpression));
        try {
            cut.transform(qExpression);
            fail();
        } catch (DatalogException expected) {
        }
        System.out.println("-------");
    }

    public void testSubconceptOfMolecule() throws Exception {
        LogicalExpression qExpression = leFactory.createLogicalExpression("?x subConceptOf ?y", o);
        System.out.println("Parsed fact:");
        System.out.println(logExprSerializer.serialize(qExpression));
        Program p = cut.transform(qExpression);
        System.out.println("Program:");
        System.out.println(p);
        assertEquals(p.size(), 1);
        Rule expected = new Rule(new Literal(new Predicate(
                "wsml-subconcept-of", 2), Literal.NegationType.NONNEGATED,
                new org.wsml.reasoner.datalog.Term[] {
                        new Variable("x"), new Variable("y") }));
        assertEquals(expected, p.get(0));
        System.out.println("-------");
    }

    public void testMemberOfMolecule() throws Exception {
        LogicalExpression qExpression = leFactory.createLogicalExpression("?x memberOf ?y", o);
        System.out.println("Parsed fact:");
        System.out.println(logExprSerializer.serialize(qExpression));
        Program p = cut.transform(qExpression);
        System.out.println("Program:");
        System.out.println(p);
        assertEquals(p.size(), 1);
        Rule expected = new Rule(new Literal(
                new Predicate("wsml-member-of", 2),
                Literal.NegationType.NONNEGATED,
                new org.wsml.reasoner.datalog.Term[] {
                        new Variable("x"), new Variable("y") }));
        assertEquals(expected, p.get(0));
        System.out.println("-------");
    }

    public void testUnificationFact() throws Exception {
        LogicalExpression qExpression = leFactory.createLogicalExpression("x=y", o); // Atom with
        // Constants.EQUAL
        System.out.println("Parsed fact:");
        System.out.println(logExprSerializer.serialize(qExpression));
        Program p = cut.transform(qExpression);
        System.out.println("Program:");
        System.out.println(p);
        assertEquals(p.size(), 1);
        Rule expected = new Rule(
                new Literal(new Predicate(Constants.EQUAL, 2),
                        Literal.NegationType.NONNEGATED,
                        new org.wsml.reasoner.datalog.Term[] {
                                new Constant("urn:test#x"),
                                new Constant("urn:test#y") }));
        assertEquals(expected, p.get(0));
        System.out.println("-------");
    }

    public void testInequalityFact() throws Exception {
        LogicalExpression qExpression = leFactory.createLogicalExpression("x!=y", o); // Atom with
        // Constants.INEQUAL
        System.out.println("Parsed fact:");
        System.out.println(logExprSerializer.serialize(qExpression));
        Program p = cut.transform(qExpression);
        System.out.println("Program:");
        System.out.println(p);
        assertEquals(p.size(), 1);
        Rule expected = new Rule(
                new Literal(new Predicate(Constants.INEQUAL, 2),
                        Literal.NegationType.NONNEGATED,
                        new org.wsml.reasoner.datalog.Term[] {
                                new Constant("urn:test#x"),
                                new Constant("urn:test#y") }));
        assertEquals(expected, p.get(0));
        System.out.println("-------");
    }

    public void testNafRule() throws Exception {
        LogicalExpression qExpression = leFactory.createLogicalExpression("path(?x,?y) :- naf arc(?x,?z) and arc(?x,?z) and naf ?z[path hasValue ?y] and ?z[path hasValue ?y].", o);
        System.out.println("Parsed fact:");
        System.out.println(logExprSerializer.serialize(qExpression));
        Program p = cut.transform(qExpression);
        System.out.println("Program:");
        System.out.println(p);
        assertEquals(p.size(), 1);
        Literal head = new Literal(new Predicate("urn:test#path", 2),
                Literal.NegationType.NONNEGATED,
                new org.wsml.reasoner.datalog.Term[] {
                        new Variable("x"), new Variable("y") });
        List<Literal> body = new ArrayList<Literal>();
        body.add(new Literal(new Predicate("urn:test#arc", 2),
                Literal.NegationType.NEGATIONASFAILURE,
                new org.wsml.reasoner.datalog.Term[] {
                        new Variable("x"), new Variable("z") }));
        body.add(new Literal(new Predicate("urn:test#arc", 2),
                Literal.NegationType.NONNEGATED,
                new org.wsml.reasoner.datalog.Term[] {
                        new Variable("x"), new Variable("z") }));
        body.add(new Literal(new Predicate("wsml-has-value", 3),
                Literal.NegationType.NEGATIONASFAILURE,
                new org.wsml.reasoner.datalog.Term[] {
                        new Variable("z"), new Constant("urn:test#path"),
                        new Variable("y") }));
        body.add(new Literal(new Predicate("wsml-has-value", 3),
                Literal.NegationType.NONNEGATED,
                new org.wsml.reasoner.datalog.Term[] {
                        new Variable("z"), new Constant("urn:test#path"),
                        new Variable("y") }));
        Rule expected = new Rule(head, body);
        assertEquals(expected, p.get(0));
        System.out.println("-------");
    }

    public void testImpliesTypeMolecule() throws Exception {
        LogicalExpression qExpression = leFactory.createLogicalExpression("?x[a impliesType ?y]", o);
        System.out.println("Parsed fact:");
        System.out.println(logExprSerializer.serialize(qExpression));
        Program p = cut.transform(qExpression);
        System.out.println("Program:");
        System.out.println(p);
        assertEquals(p.size(), 1);
        Rule expected = new Rule(new Literal(new Predicate("wsml-implies-type",
                3), Literal.NegationType.NONNEGATED,
                new org.wsml.reasoner.datalog.Term[] {
                        new Variable("x"), new Constant("urn:test#a"),
                        new Variable("y") }));
        assertEquals(expected, p.get(0));
        System.out.println("-------");
    }

    public void testOfTypeMolecule() throws Exception {
        LogicalExpression qExpression = leFactory.createLogicalExpression("?x[a ofType ?y]", o);
        System.out.println("Parsed fact:");
        System.out.println(logExprSerializer.serialize(qExpression));
        Program p = cut.transform(qExpression);
        System.out.println("Program:");
        System.out.println(p);
        assertEquals(p.size(), 1);
        Rule expected = new Rule(new Literal(new Predicate("wsml-of-type", 3),
                Literal.NegationType.NONNEGATED,
                new org.wsml.reasoner.datalog.Term[] {
                        new Variable("x"), new Constant("urn:test#a"),
                        new Variable("y") }));
        assertEquals(expected, p.get(0));
        System.out.println("-------");
    }

    public void testAttrValueMolecule() throws Exception {
        LogicalExpression qExpression = leFactory.createLogicalExpression("?x[a hasValue ?y]", o);
        System.out.println("Parsed fact:");
        System.out.println(logExprSerializer.serialize(qExpression));
        Program p = cut.transform(qExpression);
        System.out.println("Program:");
        System.out.println(p);
        assertEquals(p.size(), 1);
        Rule expected = new Rule(new Literal(
                new Predicate("wsml-has-value", 3),
                Literal.NegationType.NONNEGATED,
                new org.wsml.reasoner.datalog.Term[] {
                        new Variable("x"), new Constant("urn:test#a"),
                        new Variable("y") }));
        assertEquals(expected, p.get(0));
        System.out.println("-------");
    }

    public void testGenerateAuxiliaryRules() throws Exception {
        Program p = cut.generateAuxilliaryRules();
        System.out.println("Auxiliary rules:");
        System.out.println(p);
        assertEquals(p.size(), 4);
    }

    @Override
    protected void setUp() throws Exception {
        WsmoFactory wf = WSMO4JManager.getWSMOFactory();
        IRI ontoIri = wf.createIRI("urn:test");
        Namespace ns = wf.createNamespace("ns", ontoIri);
        o = wf.createOntology(ontoIri);
        o.setDefaultNamespace(ns);
        logExprSerializer = new LogExprSerializerWSML(o);
        leFactory = WSMO4JManager.getLogicalExpressionFactory();
        cut = new WSML2DatalogTransformer();
    }

}
