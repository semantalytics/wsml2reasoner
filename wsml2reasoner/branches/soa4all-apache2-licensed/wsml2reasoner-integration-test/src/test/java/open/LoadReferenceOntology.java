/*
 * Copyright 2011, Semantic Technology Institute (STI) Innsbruck, 
 * University of Innsbruck, Technikerstrasse 21a, 6020 Innsbruck, Austria.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package open;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.deri.wsmo4j.io.parser.wsml.WsmlLogicalExpressionParser;
import org.deri.wsmo4j.io.serializer.wsml.SerializeWSMLTermsVisitor;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.api.LPReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.api.inconsistency.InconsistencyException;
import org.wsml.reasoner.impl.DefaultWSMLReasonerFactory;
import org.wsmo.common.TopEntity;
import org.wsmo.common.exception.InvalidModelException;
import org.wsmo.wsml.Parser;
import org.wsmo.wsml.ParserException;

import com.ontotext.wsmo4j.parser.wsml.WsmlParser;

public class LoadReferenceOntology {

    public static void main(String[] args) throws FileNotFoundException, IOException, ParserException, InvalidModelException, InconsistencyException {
        test(new File("test/files/CompensationOntology.wsml"), new File("test/files/DrivingLicenseOntology.wsml"), new File("test/files/GeographyOntology.wsml"), new File("test/files/TimeOntology.wsml"), new File("test/files/CompetenceOntology.wsml"), new File("test/files/LabourRegulatoryOntology.wsml"), new File("test/files/OccupationOntologyC.wsml"), new File("test/files/LanguageOntology.wsml"), new File("test/files/JobOfferOntology.wsml"), new File("test/files/JobSeekerOntology.wsml"));

        // DO WE NEED THESE???
        // test(new File("ontologies/RO/SkillOntology.wsml")); // 2 secs
        // test(new File("ontologies/RO/EconomicActivityOntology.wsml")); // 30
        // secs
        // test(new File("ontologies/RO/EducationOntology.wsml")); // 4 secs

        // File directory = new File("ontologies/RO/");
        // for (File f : directory.listFiles()){
        // test(f);
        // }
    }

    public static void test(File... files) throws FileNotFoundException, IOException, ParserException, InvalidModelException, InconsistencyException {

//      HashMap<String, Object> parserProps = new HashMap<String, Object>();
//		WsmoFactory wsmoFactory = new WsmlFactoryContainer().getWsmoFactory();
		Parser wsmlParser = new WsmlParser();

        Set<Ontology> ontologies = new HashSet<Ontology>();
        for (File f : files) {
            long parse_start = System.currentTimeMillis();
            TopEntity[] tes = wsmlParser.parse(new BufferedReader(new FileReader(f)));
            long parse_end = System.currentTimeMillis();
            long parse = parse_end - parse_start;
            System.out.println("Read " + f.getName() + " in " + parse + " ms");

            for (TopEntity te : tes) {
                if (te instanceof Ontology) {
                    ontologies.add((Ontology) te);
                }
            }
        }

        long register_start = System.currentTimeMillis();
        LPReasoner reasoner = getReasoner(WSMLReasonerFactory.BuiltInReasoner.IRIS_STRATIFIED);
        reasoner.registerOntologies(ontologies);
        long register_end = System.currentTimeMillis();
        long register = register_end - register_start;
        System.out.println("Ontologies registered in " + register + " ms");
        long query_start = System.currentTimeMillis();

		LogicalExpression le = new WsmlLogicalExpressionParser().parse("?x memberOf ?y");

        Ontology ontology = new ArrayList<Ontology>(ontologies).get(0);
        Set<Map<Variable, Term>> result = reasoner.executeQuery(le);
        long query_end = System.currentTimeMillis();
        long query = query_end - query_start;
        System.out.println("Query executed in " + query + " ms");
        System.out.println("-----------");
        System.out.println("The result:");
        for (Map<Variable, Term> vBinding : result) {
            for (Variable var : vBinding.keySet()) {
                System.out.print(var + ": " + termToString(vBinding.get(var), ontology) + "\t ");
            }
            System.out.println();
        }
        System.out.println("-----------");
    }

    private static LPReasoner getReasoner(WSMLReasonerFactory.BuiltInReasoner theReasoner) throws InconsistencyException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(WSMLReasonerFactory.PARAM_BUILT_IN_REASONER, theReasoner);
        // params.put(WSMLReasonerFactory.PARAM_EVAL_METHOD, new Integer(2));
        LPReasoner reasoner = DefaultWSMLReasonerFactory.getFactory().createFlightReasoner(params);
        return reasoner;
    }

    private static String termToString(Term t, Ontology o) {
        SerializeWSMLTermsVisitor v = new SerializeWSMLTermsVisitor(o);
        t.accept(v);
        return v.getSerializedObject();
    }
}