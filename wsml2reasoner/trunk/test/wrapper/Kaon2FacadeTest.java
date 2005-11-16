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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.omwg.logicalexpression.Constants;
import org.semanticweb.kaon2.api.KAON2Connection;
import org.semanticweb.kaon2.api.KAON2Factory;
import org.semanticweb.kaon2.api.KAON2Manager;
import org.semanticweb.kaon2.api.Ontology;
import org.semanticweb.kaon2.api.Request;
import org.wsml.reasoner.builtin.Literal;
import org.wsml.reasoner.builtin.Rule;
import org.wsml.reasoner.builtin.kaon2.Kaon2Facade;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsmo.factory.DataFactory;

public class Kaon2FacadeTest extends TestCase {

    private final String ONTO_URI = "urn:test";

    /**
     * The number of built-in rules which are automatically generated for every
     * ontology
     */
    private final int NUMBER_BUILT_IN = 4;

    private final Map<String, Object> EMPTY_MAP = new HashMap<String, Object>();

    private final org.semanticweb.kaon2.api.rules.Literal[] EL = new org.semanticweb.kaon2.api.rules.Literal[] {};

    private Kaon2Facade cut = null;

    private KAON2Factory f = KAON2Manager.factory();

    private DataFactory df = WSMO4JManager.getDataFactory();

    public static void main(String[] args) {
        junit.textui.TestRunner.run(Kaon2FacadeTest.class);
    }

    private org.semanticweb.kaon2.api.rules.Literal[] L(
            org.semanticweb.kaon2.api.rules.Literal... literals) {
        return literals;
    }

    public void testTranslateFact() throws Exception {
        // arc(a,b)

        Rule r = new Rule(new Literal(true, "urn:test#arc", df
                .createWsmlString("urn:test#a"), df
                .createWsmlString("urn:test#b")));
        cut.register(ONTO_URI, Collections.singleton(r));
        KAON2Connection conn = cut.getKaon2Connection();
        Ontology o = conn.openOntology(ONTO_URI, EMPTY_MAP);
        Request<org.semanticweb.kaon2.api.rules.Rule> ruleReq = o
                .createAxiomRequest(org.semanticweb.kaon2.api.rules.Rule.class);
        Set<org.semanticweb.kaon2.api.rules.Rule> rules = ruleReq.get();
        for (org.semanticweb.kaon2.api.rules.Rule rule : rules) {
            System.out.println("Rule:" + rule);
        }
        assertEquals(rules.size(), 1 + NUMBER_BUILT_IN);
        org.semanticweb.kaon2.api.rules.Rule expected = f.rule(f.literal(true,
                f.nonOWLPredicate("urn:test#arc", 2), f.constant("urn:test#a"),
                f.constant("urn:test#b")), EL);
        assertTrue(rules.contains(expected));
    }

    public void testTranslateRule() throws Exception {
        // arc(a,b) :- path(a,b), arc(b,c)
        Literal head = new Literal(true, "urn:test#arc", df
                .createWsmlString("urn:test#a"), df
                .createWsmlString("urn:test#b"));

        List<Literal> body = new ArrayList<Literal>();
        body.add(new Literal(true, "urn:test#path", df
                .createWsmlString("urn:test#a"), df
                .createWsmlString("urn:test#b")));
        body.add(new Literal(true, "urn:test#arc", df
                .createWsmlString("urn:test#b"), df
                .createWsmlString("urn:test#c")));
        Rule r = new Rule(head, body);
        cut.register(ONTO_URI, Collections.singleton(r));
        KAON2Connection conn = cut.getKaon2Connection();
        Ontology o = conn.openOntology(ONTO_URI, EMPTY_MAP);
        Request<org.semanticweb.kaon2.api.rules.Rule> ruleReq = o
                .createAxiomRequest(org.semanticweb.kaon2.api.rules.Rule.class);
        Set<org.semanticweb.kaon2.api.rules.Rule> rules = ruleReq.get();
        assertEquals(rules.size(), 1 + NUMBER_BUILT_IN);
        org.semanticweb.kaon2.api.rules.Rule expected = f.rule(f.literal(true,
                f.nonOWLPredicate("urn:test#arc", 2), f.constant("urn:test#a"),
                f.constant("urn:test#b")), L(f.literal(true, f.nonOWLPredicate(
                "urn:test#path", 2), f.constant("urn:test#a"), f
                .constant("urn:test#b")), f.literal(true, f.nonOWLPredicate(
                "urn:test#arc", 2), f.constant("urn:test#b"), f
                .constant("urn:test#c"))));
        assertTrue(rules.contains(expected));
    }

    public void testTranslateConstraint() throws Exception {
        // TODO Perhaps in the future create special KAON2 rules instead of real
        // constraints.
        // !- path(a,b), arc(b,c)
        Literal head = null;

        List<Literal> body = new ArrayList<Literal>();
        body.add(new Literal(true, "urn:test#path",

        df.createWsmlString("urn:test#a"), df.createWsmlString("urn:test#b")));
        body.add(new Literal(true, "urn:test#arc", df
                .createWsmlString("urn:test#b"), df
                .createWsmlString("urn:test#c")));
        Rule r = new Rule(head, body);
        cut.register(ONTO_URI, Collections.singleton(r));
        KAON2Connection conn = cut.getKaon2Connection();
        Ontology o = conn.openOntology(ONTO_URI, EMPTY_MAP);
        Request<org.semanticweb.kaon2.api.rules.Rule> ruleReq = o
                .createAxiomRequest(org.semanticweb.kaon2.api.rules.Rule.class);
        Set<org.semanticweb.kaon2.api.rules.Rule> rules = ruleReq.get();
        assertEquals(rules.size(), 1 + NUMBER_BUILT_IN);
        org.semanticweb.kaon2.api.rules.Rule expected = f.rule(EL, L(f.literal(
                true, f.nonOWLPredicate("urn:test#path", 2), f
                        .constant("urn:test#a"), f.constant("urn:test#b")), f
                .literal(true, f.nonOWLPredicate("urn:test#arc", 2), f
                        .constant("urn:test#b"), f.constant("urn:test#c"))));
        assertTrue(rules.contains(expected));
    }

    public void testTranslateRuleNegatedBody() throws Exception {

        // arc(a,b) :- naf path(a,b), arc(b,c)
        Literal head = new Literal(true, "urn:test#arc", df
                .createWsmlString("urn:test#a"), df
                .createWsmlString("urn:test#b"));

        List<Literal> body = new ArrayList<Literal>();
        body.add(new Literal(false, "urn:test#path",

        df.createWsmlString("urn:test#a"), df.createWsmlString("urn:test#b")));
        body.add(new Literal(true, "urn:test#arc",

        df.createWsmlString("urn:test#b"), df.createWsmlString("urn:test#c")));
        Rule r = new Rule(head, body);

        cut.register(ONTO_URI, Collections.singleton(r));
        KAON2Connection conn = cut.getKaon2Connection();
        Ontology o = conn.openOntology(ONTO_URI, EMPTY_MAP);
        Request<org.semanticweb.kaon2.api.rules.Rule> ruleReq = o
                .createAxiomRequest(org.semanticweb.kaon2.api.rules.Rule.class);
        Set<org.semanticweb.kaon2.api.rules.Rule> rules = ruleReq.get();
        assertEquals(rules.size(), 1 + NUMBER_BUILT_IN);
        org.semanticweb.kaon2.api.rules.Rule expected = f.rule(f.literal(true,
                f.nonOWLPredicate("urn:test#arc", 2), f.constant("urn:test#a"),
                f.constant("urn:test#b")), L(f.literal(false, f
                .nonOWLPredicate("urn:test#path", 2), f.constant("urn:test#a"),
                f.constant("urn:test#b")), f.literal(true, f.nonOWLPredicate(
                "urn:test#arc", 2), f.constant("urn:test#b"), f
                .constant("urn:test#c"))));
        assertTrue(rules.contains(expected));
    }

    public void testTranslateRuleWithEquality() throws Exception {
        // arc(a,b) :- path(a,b), a=b
        Literal head = new Literal(true, "urn:test#arc",

        df.createWsmlString("urn:test#a"), df.createWsmlString("urn:test#b"));

        List<Literal> body = new ArrayList<Literal>();
        body.add(new Literal(true, "urn:test#path",

        df.createWsmlString("urn:test#a"), df.createWsmlString("urn:test#b")));
        body.add(new Literal(true, Constants.EQUAL, df
                .createWsmlString("urn:test#a"), df
                .createWsmlString("urn:test#b")));
        Rule r = new Rule(head, body);
        cut.register(ONTO_URI, Collections.singleton(r));
        KAON2Connection conn = cut.getKaon2Connection();
        Ontology o = conn.openOntology(ONTO_URI, EMPTY_MAP);
        Request<org.semanticweb.kaon2.api.rules.Rule> ruleReq = o
                .createAxiomRequest(org.semanticweb.kaon2.api.rules.Rule.class);
        Set<org.semanticweb.kaon2.api.rules.Rule> rules = ruleReq.get();
        assertEquals(rules.size(), 1 + NUMBER_BUILT_IN);
        org.semanticweb.kaon2.api.rules.Rule expected = f.rule(f.literal(true,
                f.nonOWLPredicate("urn:test#arc", 2), f.constant("urn:test#a"),
                f.constant("urn:test#b")), L(f.literal(true, f.nonOWLPredicate(
                "urn:test#path", 2), f.constant("urn:test#a"), f
                .constant("urn:test#b")), f.literal(true, f.equal(), f
                .constant("urn:test#a"), f.constant("urn:test#b"))));
        assertTrue(rules.contains(expected));
    }

    public void testTranslateRuleWithInEquality() throws Exception {

        // arc(a,b) :- path(a,b), a!=b
        Literal head = new Literal(true, "urn:test#arc", df
                .createWsmlString("urn:test#a"), df
                .createWsmlString("urn:test#b"));

        List<Literal> body = new ArrayList<Literal>();
        body.add(new Literal(true, "urn:test#path", df
                .createWsmlString("urn:test#a"), df
                .createWsmlString("urn:test#b")));
        body.add(new Literal(true, Constants.INEQUAL, df
                .createWsmlString("urn:test#a"), df
                .createWsmlString("urn:test#b")));
        Rule r = new Rule(head, body);
        cut.register(ONTO_URI, Collections.singleton(r));
        KAON2Connection conn = cut.getKaon2Connection();
        Ontology o = conn.openOntology(ONTO_URI, EMPTY_MAP);
        Request<org.semanticweb.kaon2.api.rules.Rule> ruleReq = o
                .createAxiomRequest(org.semanticweb.kaon2.api.rules.Rule.class);
        Set<org.semanticweb.kaon2.api.rules.Rule> rules = ruleReq.get();
        assertEquals(rules.size(), 1 + NUMBER_BUILT_IN);
        org.semanticweb.kaon2.api.rules.Rule expected = f.rule(f.literal(true,
                f.nonOWLPredicate("urn:test#arc", 2), f.constant("urn:test#a"),
                f.constant("urn:test#b")), L(f.literal(true, f.nonOWLPredicate(
                "urn:test#path", 2), f.constant("urn:test#a"), f
                .constant("urn:test#b")), f.literal(false, f.equal(), f
                .constant("urn:test#a"), f.constant("urn:test#b"))));
        assertTrue(rules.contains(expected));
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        cut = new Kaon2Facade();
    }

}
