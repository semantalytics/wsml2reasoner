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
package wrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.omwg.logexpression.Constants;
import org.semanticweb.kaon2.api.KAON2Connection;
import org.semanticweb.kaon2.api.KAON2Factory;
import org.semanticweb.kaon2.api.KAON2Manager;
import org.semanticweb.kaon2.api.Ontology;
import org.semanticweb.kaon2.api.Request;
import org.wsml.reasoner.datalog.Constant;
import org.wsml.reasoner.datalog.Literal;
import org.wsml.reasoner.datalog.Predicate;
import org.wsml.reasoner.datalog.Program;
import org.wsml.reasoner.datalog.Rule;
import org.wsml.reasoner.datalog.wrapper.kaon2.Kaon2Facade;

public class Kaon2FacadeTest extends TestCase {

    private final String ONTO_URI = "urn:test";

    private final Map<String, Object> EMPTY_MAP = new HashMap<String, Object>();

    private final org.semanticweb.kaon2.api.rules.Literal[] EL = new org.semanticweb.kaon2.api.rules.Literal[] {};

    private Kaon2Facade cut = null;

    private KAON2Factory f = KAON2Manager.factory();

    public static void main(String[] args) {
        junit.textui.TestRunner.run(Kaon2FacadeTest.class);
    }

    private org.semanticweb.kaon2.api.rules.Literal[] L(
            org.semanticweb.kaon2.api.rules.Literal... literals) {
        return literals;
    }

    public void testTranslateFact() throws Exception {
        Program p = new Program();
        // arc(a,b)
        Rule r = new Rule(
                new Literal(new Predicate("urn:test#arc", 2),
                        Literal.NegationType.NONNEGATED,
                        new org.wsml.reasoner.datalog.Term[] {
                                new Constant("urn:test#a"),
                                new Constant("urn:test#b") }));
        p.add(r);
        cut.register(ONTO_URI, p);
        KAON2Connection conn = cut.getKaon2Connection();
        Ontology o = conn.openOntology(ONTO_URI, EMPTY_MAP);
        Request<org.semanticweb.kaon2.api.rules.Rule> ruleReq = o
                .createAxiomRequest(org.semanticweb.kaon2.api.rules.Rule.class);
        Set<org.semanticweb.kaon2.api.rules.Rule> rules = ruleReq.get();
        assertEquals(rules.size(), 1);
        org.semanticweb.kaon2.api.rules.Rule result = rules.iterator().next();
        org.semanticweb.kaon2.api.rules.Rule expected = f.rule(f.literal(true,
                f.nonOWLPredicate("urn:test#arc", 2), f
                        .individual("urn:test#a"), f.individual("urn:test#b")),
                EL);
        assertEquals(expected, result);
    }

    public void testTranslateRule() throws Exception {
        Program p = new Program();
        // arc(a,b) :- path(a,b), arc(b,c)
        Literal head = new Literal(
                new Predicate("urn:test#arc", 2),
                Literal.NegationType.NONNEGATED,
                new org.wsml.reasoner.datalog.Term[] {
                        new Constant("urn:test#a"), new Constant("urn:test#b") });

        List<Literal> body = new ArrayList<Literal>();
        body
                .add(new Literal(new Predicate("urn:test#path", 2),
                        Literal.NegationType.NONNEGATED,
                        new org.wsml.reasoner.datalog.Term[] {
                                new Constant("urn:test#a"),
                                new Constant("urn:test#b") }));
        body
                .add(new Literal(new Predicate("urn:test#arc", 2),
                        Literal.NegationType.NONNEGATED,
                        new org.wsml.reasoner.datalog.Term[] {
                                new Constant("urn:test#b"),
                                new Constant("urn:test#c") }));
        Rule r = new Rule(head, body);
        p.add(r);
        cut.register(ONTO_URI, p);
        KAON2Connection conn = cut.getKaon2Connection();
        Ontology o = conn.openOntology(ONTO_URI, EMPTY_MAP);
        Request<org.semanticweb.kaon2.api.rules.Rule> ruleReq = o
                .createAxiomRequest(org.semanticweb.kaon2.api.rules.Rule.class);
        Set<org.semanticweb.kaon2.api.rules.Rule> rules = ruleReq.get();
        assertEquals(rules.size(), 1);
        org.semanticweb.kaon2.api.rules.Rule result = rules.iterator().next();
        org.semanticweb.kaon2.api.rules.Rule expected = f.rule(f.literal(true,
                f.nonOWLPredicate("urn:test#arc", 2), f
                        .individual("urn:test#a"), f.individual("urn:test#b")),
                L(f.literal(true, f.nonOWLPredicate("urn:test#path", 2), f
                        .individual("urn:test#a"), f.individual("urn:test#b")),
                        f.literal(true, f.nonOWLPredicate("urn:test#arc", 2), f
                                .individual("urn:test#b"), f
                                .individual("urn:test#c"))));
        assertEquals(expected, result);
    }

    public void testTranslateConstraint() throws Exception {
        //TODO Perhaps in the future create special KAON2 rules instead of real constraints.
        Program p = new Program();
        // !- path(a,b), arc(b,c)
        Literal head = null;

        List<Literal> body = new ArrayList<Literal>();
        body
                .add(new Literal(new Predicate("urn:test#path", 2),
                        Literal.NegationType.NONNEGATED,
                        new org.wsml.reasoner.datalog.Term[] {
                                new Constant("urn:test#a"),
                                new Constant("urn:test#b") }));
        body
                .add(new Literal(new Predicate("urn:test#arc", 2),
                        Literal.NegationType.NONNEGATED,
                        new org.wsml.reasoner.datalog.Term[] {
                                new Constant("urn:test#b"),
                                new Constant("urn:test#c") }));
        Rule r = new Rule(head, body);
        p.add(r);
        cut.register(ONTO_URI, p);
        KAON2Connection conn = cut.getKaon2Connection();
        Ontology o = conn.openOntology(ONTO_URI, EMPTY_MAP);
        Request<org.semanticweb.kaon2.api.rules.Rule> ruleReq = o
                .createAxiomRequest(org.semanticweb.kaon2.api.rules.Rule.class);
        Set<org.semanticweb.kaon2.api.rules.Rule> rules = ruleReq.get();
        assertEquals(rules.size(), 1);
        org.semanticweb.kaon2.api.rules.Rule result = rules.iterator().next();
        org.semanticweb.kaon2.api.rules.Rule expected = f
                .rule(EL, L(f.literal(true, f.nonOWLPredicate("urn:test#path",
                        2), f.individual("urn:test#a"), f
                        .individual("urn:test#b")), f.literal(true, f
                        .nonOWLPredicate("urn:test#arc", 2), f
                        .individual("urn:test#b"), f.individual("urn:test#c"))));
        assertEquals(expected, result);
    }
    
    public void testTranslateRuleNegatedBody() throws Exception {
        Program p = new Program();
        // arc(a,b) :- naf path(a,b), arc(b,c)
        Literal head = new Literal(
                new Predicate("urn:test#arc", 2),
                Literal.NegationType.NONNEGATED,
                new org.wsml.reasoner.datalog.Term[] {
                        new Constant("urn:test#a"), new Constant("urn:test#b") });

        List<Literal> body = new ArrayList<Literal>();
        body
                .add(new Literal(new Predicate("urn:test#path", 2),
                        Literal.NegationType.NEGATIONASFAILURE,
                        new org.wsml.reasoner.datalog.Term[] {
                                new Constant("urn:test#a"),
                                new Constant("urn:test#b") }));
        body
                .add(new Literal(new Predicate("urn:test#arc", 2),
                        Literal.NegationType.NONNEGATED,
                        new org.wsml.reasoner.datalog.Term[] {
                                new Constant("urn:test#b"),
                                new Constant("urn:test#c") }));
        Rule r = new Rule(head, body);
        p.add(r);
        cut.register(ONTO_URI, p);
        KAON2Connection conn = cut.getKaon2Connection();
        Ontology o = conn.openOntology(ONTO_URI, EMPTY_MAP);
        Request<org.semanticweb.kaon2.api.rules.Rule> ruleReq = o
                .createAxiomRequest(org.semanticweb.kaon2.api.rules.Rule.class);
        Set<org.semanticweb.kaon2.api.rules.Rule> rules = ruleReq.get();
        assertEquals(rules.size(), 1);
        org.semanticweb.kaon2.api.rules.Rule result = rules.iterator().next();
        org.semanticweb.kaon2.api.rules.Rule expected = f.rule(f.literal(true,
                f.nonOWLPredicate("urn:test#arc", 2), f
                        .individual("urn:test#a"), f.individual("urn:test#b")),
                L(f.literal(false, f.nonOWLPredicate("urn:test#path", 2), f
                        .individual("urn:test#a"), f.individual("urn:test#b")),
                        f.literal(true, f.nonOWLPredicate("urn:test#arc", 2), f
                                .individual("urn:test#b"), f
                                .individual("urn:test#c"))));
        assertEquals(expected, result);
    }
    
    public void testTranslateRuleWithEquality() throws Exception {
        Program p = new Program();
        // arc(a,b) :- path(a,b), a=b
        Literal head = new Literal(
                new Predicate("urn:test#arc", 2),
                Literal.NegationType.NONNEGATED,
                new org.wsml.reasoner.datalog.Term[] {
                        new Constant("urn:test#a"), new Constant("urn:test#b") });

        List<Literal> body = new ArrayList<Literal>();
        body
                .add(new Literal(new Predicate("urn:test#path", 2),
                        Literal.NegationType.NONNEGATED,
                        new org.wsml.reasoner.datalog.Term[] {
                                new Constant("urn:test#a"),
                                new Constant("urn:test#b") }));
        body
                .add(new Literal(new Predicate(Constants.EQUAL, 2),
                        Literal.NegationType.NONNEGATED,
                        new org.wsml.reasoner.datalog.Term[] {
                                new Constant("urn:test#a"),
                                new Constant("urn:test#b") }));
        Rule r = new Rule(head, body);
        p.add(r);
        cut.register(ONTO_URI, p);
        KAON2Connection conn = cut.getKaon2Connection();
        Ontology o = conn.openOntology(ONTO_URI, EMPTY_MAP);
        Request<org.semanticweb.kaon2.api.rules.Rule> ruleReq = o
                .createAxiomRequest(org.semanticweb.kaon2.api.rules.Rule.class);
        Set<org.semanticweb.kaon2.api.rules.Rule> rules = ruleReq.get();
        assertEquals(rules.size(), 1);
        org.semanticweb.kaon2.api.rules.Rule result = rules.iterator().next();
        org.semanticweb.kaon2.api.rules.Rule expected = f.rule(f.literal(true,
                f.nonOWLPredicate("urn:test#arc", 2), f
                        .individual("urn:test#a"), f.individual("urn:test#b")),
                L(f.literal(true, f.nonOWLPredicate("urn:test#path", 2), f
                        .individual("urn:test#a"), f.individual("urn:test#b")),
                        f.literal(true, f.equal(), f
                                .individual("urn:test#a"), f
                                .individual("urn:test#b"))));
        assertEquals(expected, result);
    }
    
    public void testTranslateRuleWithInEquality() throws Exception {
        Program p = new Program();
        // arc(a,b) :- path(a,b), a!=b
        Literal head = new Literal(
                new Predicate("urn:test#arc", 2),
                Literal.NegationType.NONNEGATED,
                new org.wsml.reasoner.datalog.Term[] {
                        new Constant("urn:test#a"), new Constant("urn:test#b") });

        List<Literal> body = new ArrayList<Literal>();
        body
                .add(new Literal(new Predicate("urn:test#path", 2),
                        Literal.NegationType.NONNEGATED,
                        new org.wsml.reasoner.datalog.Term[] {
                                new Constant("urn:test#a"),
                                new Constant("urn:test#b") }));
        body
                .add(new Literal(new Predicate(Constants.INEQUAL, 2),
                        Literal.NegationType.NONNEGATED,
                        new org.wsml.reasoner.datalog.Term[] {
                                new Constant("urn:test#a"),
                                new Constant("urn:test#b") }));
        Rule r = new Rule(head, body);
        p.add(r);
        cut.register(ONTO_URI, p);
        KAON2Connection conn = cut.getKaon2Connection();
        Ontology o = conn.openOntology(ONTO_URI, EMPTY_MAP);
        Request<org.semanticweb.kaon2.api.rules.Rule> ruleReq = o
                .createAxiomRequest(org.semanticweb.kaon2.api.rules.Rule.class);
        Set<org.semanticweb.kaon2.api.rules.Rule> rules = ruleReq.get();
        assertEquals(rules.size(), 1);
        org.semanticweb.kaon2.api.rules.Rule result = rules.iterator().next();
        org.semanticweb.kaon2.api.rules.Rule expected = f.rule(f.literal(true,
                f.nonOWLPredicate("urn:test#arc", 2), f
                        .individual("urn:test#a"), f.individual("urn:test#b")),
                L(f.literal(true, f.nonOWLPredicate("urn:test#path", 2), f
                        .individual("urn:test#a"), f.individual("urn:test#b")),
                        f.literal(false, f.equal(), f
                                .individual("urn:test#a"), f
                                .individual("urn:test#b"))));
        assertEquals(expected, result);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        cut = new Kaon2Facade();
    }

}
