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
package test.wsmx;

import java.io.Reader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.omwg.logicalexpression.AttrSpecification;
import org.omwg.logicalexpression.Binary;
import org.omwg.logicalexpression.LogicalExpression;
import org.wsmo.factory.LogicalExpressionFactory;
import org.omwg.logicalexpression.Molecule;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Variable;
import org.omwg.ontology.Concept;
import org.omwg.ontology.Instance;
import org.omwg.ontology.Ontology;
import org.wsml.reasoner.api.queryanswering.QueryAnsweringResult;
import org.wsml.reasoner.api.queryanswering.VariableBinding;
import org.wsml.reasoner.impl.VariableBindingImpl;
import org.wsml.reasoner.wsmx.WSMXReasoner;
import org.wsmo.common.Identifier;
import org.wsmo.common.Namespace;
import org.wsmo.execution.common.component.WSMLReasoner;
import org.wsmo.factory.Factory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.Parser;

import test.BaseTest;

public class WSMXReasonerTest extends TestCase
{
    protected WsmoFactory wsmoFactory;

    protected LogicalExpressionFactory leFactory;

    protected WSMLReasoner wsmxReasoner;

    protected Identifier ontologyID;

    protected Namespace ontologyNS;

    public WSMXReasonerTest()
    {
        wsmoFactory = Factory.createWsmoFactory(null);
        HashMap createParams = new HashMap();
        createParams.put(Factory.PROVIDER_CLASS, "org.deri.wsmo4j.logexpression.LogicalExpressionFactoryImpl");
        leFactory = (LogicalExpressionFactory)Factory.createLogicalExpressionFactory(createParams);
        wsmxReasoner = new WSMXReasoner();

        // initialize reasoner with ontology:
        try
        {
            Ontology ontology = parseOntology("examples/lordoftherings.wsml");
            ontologyID = ontology.getIdentifier();
            ontologyNS = ontology.getDefaultNamespace();
            wsmxReasoner.register(ontology);
        } catch(Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
        WSMXReasonerTest test = new WSMXReasonerTest();
        try
        {
            test.testInstanceCheck();
            System.out.println("test of instance checking successful!");
            test.testInstanceRetrieval();
            System.out.println("test of instance retrieval successful!");
            test.testRetrieval();
            System.out.println("test of general query answering successful!");
            test.testSubconceptRetrieval();
            System.out.println("test of subconcept reatrieval successful!");
            test.testSubsumptionCheck();
            System.out.println("test of subsumption check successful!");
            test.testSuperconceptRetrieval();
            System.out.println("test of superconcept retrieval successful!");
        } catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    protected Ontology parseOntology(String fileName) throws Exception
    {
        Map createParams = new HashMap();
        createParams.put(Parser.PARSER_WSMO_FACTORY, wsmoFactory);
        createParams.put(Parser.PARSER_LE_FACTORY, leFactory);
        Parser parser = Factory.createParser(createParams);
        Reader input = BaseTest.getReaderForFile(fileName);
        return (Ontology)parser.parse(input)[0];
    }

    public void testInstanceRetrieval() throws Exception
    {
        Set<Concept> concepts = wsmxReasoner.getAllInstances(wsmoFactory.createConcept(wsmoFactory.createIRI(ontologyNS, "Man")), ontologyID);
        for(Concept concept : concepts)
        {
            String string = concept.getIdentifier().toString();
            assertTrue(string.contains("Aragorn") || string.contains("Arathorn") || string.contains("Elendil"));
        }
    }

    public void testSubconceptRetrieval() throws Exception
    {
        Set<Concept> concepts = wsmxReasoner.getAllSubconcepts(wsmoFactory.createConcept(wsmoFactory.createIRI(ontologyNS, "Humanoid")), ontologyID);
        assertTrue(concepts.size() == 6);
    }

    public void testSuperconceptRetrieval() throws Exception
    {
        Set<Concept> concepts = wsmxReasoner.getAllSuperconcepts(wsmoFactory.createConcept(wsmoFactory.createIRI(ontologyNS, "Man")), ontologyID);
        assertTrue(concepts.size() == 4);
    }

    public void testSubsumptionCheck() throws Exception
    {
        Concept subConcept = wsmoFactory.createConcept(wsmoFactory.createIRI(ontologyNS, "Man"));
        Concept superConcept = wsmoFactory.createConcept(wsmoFactory.createIRI(ontologyNS, "Male"));
        assertTrue(wsmxReasoner.subsumes(superConcept, subConcept, ontologyID));
    }

    public void testInstanceCheck() throws Exception
    {
        Concept concept = wsmoFactory.createConcept(wsmoFactory.createIRI(ontologyNS, "Man"));
        Instance instance = wsmoFactory.createInstance(wsmoFactory.createIRI(ontologyNS, "Aragorn"));
        assertTrue(wsmxReasoner.isInstanceOf(instance, concept, ontologyID));
    }

    public void testRetrieval() throws Exception
    {
        Variable xVar = leFactory.createVariable("x");
        Variable yVar = leFactory.createVariable("y");
        Set<Term> args = new HashSet<Term>(1);
        args.add(yVar);
        Set<AttrSpecification> attrSpecSet = new HashSet<AttrSpecification>();
        attrSpecSet.add(leFactory.createAttrSpecification(AttrSpecification.ATTR_VALUE, leFactory.createIRI(wsmoFactory.createIRI(ontologyNS, "loves").toString()), args));
        Molecule lovesMolecule = leFactory.createMolecule(xVar, null, null, attrSpecSet);

        Set<Term> mofSet = new HashSet<Term>();
        mofSet.add(leFactory.createIRI(wsmoFactory.createIRI(ontologyNS, "Elf").toString()));
        Molecule isElfMolecule = leFactory.createMolecule(yVar, null, mofSet, null);
        LogicalExpression query = leFactory.createBinary(Binary.AND, lovesMolecule, isElfMolecule);
        QueryAnsweringResult result = ((WSMXReasoner)wsmxReasoner).retrieve(query, ontologyID);

        VariableBinding binding = new VariableBindingImpl();
        binding.put("x", wsmoFactory.createIRI(ontologyNS, "Aragorn").toString());
        assertTrue(result.contains(binding));
    }
}
