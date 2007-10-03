/*
 * Copyright (C) 2006 Digital Enterprise Research Insitute (DERI) Innsbruck
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
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

import org.deri.wsmo4j.io.serializer.wsml.VisitorSerializeWSMLTerms;
import org.deri.wsmo4j.logicalexpression.LogicalExpressionImpl;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.api.WSMLReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.api.inconsistency.InconsistencyException;
import org.wsml.reasoner.impl.DefaultWSMLReasonerFactory;
import org.wsmo.common.IRI;
import org.wsmo.common.TopEntity;
import org.wsmo.common.exception.InvalidModelException;
import org.wsmo.factory.Factory;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.wsml.Parser;
import org.wsmo.wsml.ParserException;

import com.ontotext.wsmo4j.parser.wsml.ParserImpl;


public class LoadReferenceOntology {

    public static void main(String[] args) throws FileNotFoundException, IOException, ParserException, InvalidModelException, InconsistencyException {        
        test(new File("test/files/CompensationOntology.wsml"), 
        		new File("test/files/DrivingLicenseOntology.wsml"), 
        		new File("test/files/GeographyOntology.wsml"), 
        		new File("test/files/TimeOntology.wsml"), 
        		new File("test/files/CompetenceOntology.wsml"), 
        		new File("test/files/LabourRegulatoryOntology.wsml"), 
        		new File("test/files/OccupationOntologyC.wsml"), 
        		new File("test/files/LanguageOntology.wsml"), 
        		new File("test/files/JobOfferOntology.wsml"), 
        		new File("test/files/JobSeekerOntology.wsml"));
        
        // DO WE NEED THESE???
//        test(new File("ontologies/RO/SkillOntology.wsml")); // 2 secs
//        test(new File("ontologies/RO/EconomicActivityOntology.wsml")); // 30 secs
//        test(new File("ontologies/RO/EducationOntology.wsml")); // 4 secs
        
//        File directory = new File("ontologies/RO/");
//        for (File f : directory.listFiles()){
//            test(f);
//        }
    }
    
    public static void test(File... files) throws FileNotFoundException, IOException, ParserException, InvalidModelException, InconsistencyException{
        
        HashMap<String, Object> parserProps = new HashMap<String, Object>();
        Parser wsmlParser;
        try {
            wsmlParser = new ParserImpl(parserProps);
        }
        catch (RuntimeException re) {
            re.printStackTrace();
            wsmlParser = Factory.createParser(parserProps);
        }
        
        Set <Ontology> ontologies = new HashSet <Ontology> ();
        for (File f : files){
            long parse_start = System.currentTimeMillis();
            TopEntity[] tes = wsmlParser.parse(new BufferedReader(new FileReader(f)));
            long parse_end = System.currentTimeMillis();
            long parse = parse_end - parse_start;
            System.out.println("Read " + f.getName() + " in " + parse + " ms");
            
            for (TopEntity te : tes){
                if (te instanceof Ontology){
                    ontologies.add((Ontology) te);
                }
            }
        }
        
        long register_start = System.currentTimeMillis();
        WSMLReasoner reasoner = getReasoner(WSMLReasonerFactory.BuiltInReasoner.MINS);
        reasoner.registerOntologies(ontologies);
        long register_end = System.currentTimeMillis();
        long register = register_end - register_start;
        System.out.println("Ontologies registered in " + register + " ms");
        long query_start = System.currentTimeMillis();
        
        LogicalExpression le = Factory.createLogicalExpressionFactory(new HashMap <String, Object> ()).createLogicalExpression("?x memberOf ?y");
        
        Ontology ontology = new ArrayList <Ontology>(ontologies).get(0);
        Set <Map<Variable, Term>> result = reasoner.executeQuery((IRI) ontology.getIdentifier(), le);
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
    
    private static WSMLReasoner getReasoner(WSMLReasonerFactory.BuiltInReasoner theReasoner) throws InconsistencyException{
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(WSMLReasonerFactory.PARAM_BUILT_IN_REASONER, theReasoner);
//        params.put(WSMLReasonerFactory.PARAM_EVAL_METHOD, new Integer(2));
        WSMLReasoner reasoner = DefaultWSMLReasonerFactory.getFactory().createWSMLFlightReasoner(params);
        return reasoner;
    }
    
    private static String termToString(Term t, Ontology o) {
        VisitorSerializeWSMLTerms v = new VisitorSerializeWSMLTerms(o);
        t.accept(v);
        return v.getSerializedObject();
    }
}
