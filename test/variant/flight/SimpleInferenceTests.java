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
package variant.flight;

import org.wsml.reasoner.api.LPReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.api.WSMLReasonerFactory.BuiltInReasoner;

import base.BaseReasonerTest;

/*
 * IRIS does not pass
 * 
 */

public class SimpleInferenceTests extends BaseReasonerTest {

    private static final String ONTOLOGY_FILE = "files/simpsons.wsml";

    BuiltInReasoner previous;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setupScenario(ONTOLOGY_FILE);
        previous = BaseReasonerTest.reasoner;
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        resetReasoner(previous);
    }

    public void basicInferences() throws Exception {

        LPReasoner wsmlReasoner = (LPReasoner) BaseReasonerTest.wsmlReasoner;

        // register ontology at the wsml reasoner
        wsmlReasoner.registerOntology(o);

        // test ontology satisfiability
        assertTrue(wsmlReasoner.checkConsistency().size() == 0);

        // test logicalExpressionIsNotConsistent with a MembershipMolecule
        assertFalse(wsmlReasoner.entails(leFactory.createLogicalExpression("bart_simpson memberOf actor.", o)));

        // test logicalExpressionIsConsistent with a MembershipMolecule
        assertTrue(wsmlReasoner.entails(leFactory.createLogicalExpression("bart_simpson memberOf character.", o)));

        // test logicalExpressionIsConsistent with a Conjunction
        assertTrue(wsmlReasoner.entails(leFactory.createLogicalExpression("?x memberOf school and ?x memberOf place.", o)));
        assertTrue(wsmlReasoner.entails(leFactory.createLogicalExpression("marge_simpson memberOf character and " + "nancy_cartwright memberOf actor.", o)));

        // test logicalExpressionIsNotConsistent with a Conjunction
        assertFalse(wsmlReasoner.entails(leFactory.createLogicalExpression("?x memberOf character and ?x memberOf actor.", o)));

        // test isInstanceHavingInferingAttributeValue
        assertTrue(wsmlReasoner.executeGroundQuery(leFactory.createLogicalExpression("marge_simpson[hasChild " + "hasValue bart_simpson].", o)));

        // test isInstanceNotHavingInferingAttributeValue
        assertFalse(wsmlReasoner.executeGroundQuery(leFactory.createLogicalExpression("marge_simpson[hasChild " + "hasValue bobby_simpson].", o)));

        // test isInstanceHavingConstraintAttributeValue
        assertTrue(wsmlReasoner.executeGroundQuery(leFactory.createLogicalExpression("marge_simpson[hasName " + "hasValue \"Marge Simpson\"].", o)));

        // test isInstanceNotHavingConstraintAttributeValue
        assertFalse(wsmlReasoner.executeGroundQuery(leFactory.createLogicalExpression("marge_simpson[hasCatchPhrase " + "hasValue \"blabla\"].", o)));

        wsmlReasoner.deRegister();
    }

    public void testFlightReasoners() throws Exception {
        // resetReasoner(WSMLReasonerFactory.BuiltInReasoner.IRIS);
        // basicInferences();

        resetReasoner(WSMLReasonerFactory.BuiltInReasoner.MINS);
        basicInferences();

        if (exists("org.wsml.reasoner.builtin.kaon2.Kaon2Facade")) {
            resetReasoner(WSMLReasonerFactory.BuiltInReasoner.KAON2);
            basicInferences();
        }
    }
}
