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
package reasoner.flight;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Concept;
import org.omwg.ontology.Instance;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.SimpleDataType;
import org.omwg.ontology.SimpleDataValue;
import org.omwg.ontology.Variable;
import org.omwg.ontology.WsmlDataType;
import org.wsml.reasoner.api.InternalReasonerException;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.api.inconsistency.AttributeTypeViolation;
import org.wsml.reasoner.api.inconsistency.ConsistencyViolation;
import org.wsml.reasoner.api.inconsistency.InconsistencyException;
import org.wsml.reasoner.api.inconsistency.MaxCardinalityViolation;
import org.wsml.reasoner.api.inconsistency.MinCardinalityViolation;
import org.wsml.reasoner.api.inconsistency.NamedUserConstraintViolation;
import org.wsml.reasoner.api.inconsistency.UnNamedUserConstraintViolation;
import org.wsml.reasoner.api.inconsistency.UserConstraintViolation;
import org.wsml.reasoner.impl.DefaultWSMLReasonerFactory;
import org.wsmo.common.IRI;
import org.wsmo.common.exception.InvalidModelException;
import org.wsmo.factory.Factory;
import org.wsmo.wsml.Parser;
import org.wsmo.wsml.ParserException;

import reasoner.BaseReasonerTest;

public class ViolationsTest extends BaseReasonerTest {
    private static final String NS = "urn:bad#";

    private static final String ONTOLOGY_FILE = "reasoner/flight/bad.wsml";

    public static void main(String[] args) throws Exception{
        //junit.textui.TestRunner.run(ViolationsTest.class);
        // Set up factories for creating WSML elements
        Parser wsmlparserimpl = Factory.createParser(null);
        // Read simple ontology from file
        final Ontology o = (Ontology)wsmlparserimpl.parse(new InputStreamReader(
                ViolationsTest.class.getClassLoader().getResourceAsStream(ONTOLOGY_FILE)))[0];

        // Create reasoner
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(WSMLReasonerFactory.PARAM_BUILT_IN_REASONER,
                WSMLReasonerFactory.BuiltInReasoner.MINS);
        wsmlReasoner = DefaultWSMLReasonerFactory.getFactory()
                .createWSMLFlightReasoner(params);

        // Register ontology
        System.out.println("Registering ontology");
        try{
        wsmlReasoner.registerOntology(o);
        }catch (Exception e){
            System.out.println();
            e.printStackTrace();
        }
    }

    public void testViolations() throws IOException, ParserException,
            InvalidModelException {
        try {
            setupScenario(ONTOLOGY_FILE);
            fail("Inconsistency exception expected!");
        } catch (InconsistencyException expected) {
            Set<ConsistencyViolation> errors = expected.getViolations();
            //assertEquals(6, errors.size());
            boolean iTypeChecked = false;
            boolean dTypeChecked = false;
            boolean minCardChecked = false;
            boolean maxCardChecked = false;
            boolean namedUserChecked = false;
            boolean unNamedUserChecked = false;
            for (ConsistencyViolation violation : errors) {
                if (violation instanceof AttributeTypeViolation) {
                    AttributeTypeViolation v = (AttributeTypeViolation) violation;
                    String attributeId = v.getAttribute().getIdentifier()
                            .toString();
                    String instanceId = v.getInstance().getIdentifier()
                            .toString();
                    if ("urn:bad#ai".equals(attributeId)) {
                        Concept t = (Concept) v.getExpectedType();
                        String typeId = t.getIdentifier().toString();
                        Instance val = (Instance) v.getViolatingValue();
                        String valueId = val.getIdentifier().toString();
                        if ("urn:bad#iC".equals(instanceId)
                                && "urn:bad#D".equals(typeId)
                                && "urn:bad#iE".equals(valueId))
                            iTypeChecked = true;
                    } else if ("urn:bad#ad".equals(attributeId)) {
                        SimpleDataType t = (SimpleDataType) v.getExpectedType();
                        String typeId = t.getIRI().toString();
                        SimpleDataValue val = (SimpleDataValue) v
                                .getViolatingValue();
                        String value = val.getValue().toString();
                        if ("urn:bad#iC".equals(instanceId)
                                && WsmlDataType.WSML_INTEGER.equals(typeId)
                                && "blah".equals(value))
                            dTypeChecked = true;
                    }

                } else if (violation instanceof MinCardinalityViolation) {
                    MinCardinalityViolation v = (MinCardinalityViolation) violation;
                    String attributeId = v.getAttribute().getIdentifier()
                            .toString();
                    String instanceId = v.getTerm()//.getIdentifier()
                            .toString();
                    if ("urn:bad#iC".equals(instanceId)
                            && "urn:bad#amin".equals(attributeId))
                        minCardChecked = true;
                } else if (violation instanceof MaxCardinalityViolation) {
                    MaxCardinalityViolation v = (MaxCardinalityViolation) violation;
                    String attributeId = v.getAttribute().getIdentifier()
                            .toString();
                    String instanceId = v.getTerm()//.getIdentifier()
                            .toString();
                    if ("urn:bad#iC".equals(instanceId)
                            && "urn:bad#amax".equals(attributeId))
                        maxCardChecked = true;

                } else if (violation instanceof UserConstraintViolation) {
                    if (violation instanceof NamedUserConstraintViolation) {
                        NamedUserConstraintViolation v = (NamedUserConstraintViolation) violation;
                        String axiomId = v.getAxiom().getIdentifier().toString();
                        if ("urn:bad#ax1".equals(axiomId)) namedUserChecked = true;
                    } 
                    if (violation instanceof UnNamedUserConstraintViolation){
                        unNamedUserChecked = true;
                    }
                }
            }
            assertTrue(iTypeChecked);
            assertTrue(dTypeChecked);
            assertTrue(minCardChecked);
            assertTrue(maxCardChecked);
            assertTrue(namedUserChecked);
            assertTrue(unNamedUserChecked);
        }
        // check that not registered
        try {
            executeQuery("_\"urn:bad#xxx\"()", o);
            fail();
        } catch (InternalReasonerException expected) {
            System.out.println("Catched expected exception: "
                    + expected.getMessage());

        }

    }

    private Set<Map<Variable, Term>> executeQuery(String query, Ontology o)
            throws ParserException {
        LogicalExpression qExpression = leFactory.createLogicalExpression(
                query, o);
        return wsmlReasoner.executeQuery((IRI) o.getIdentifier(), qExpression);
    }

}
