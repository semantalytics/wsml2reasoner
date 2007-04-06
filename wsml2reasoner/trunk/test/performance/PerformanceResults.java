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

package performance;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.omwg.ontology.Ontology;
import org.wsmo.common.IRI;


public class PerformanceResults {

    
    Map <String, Map<IRI, Map <String, PerformanceResult>>> performanceresults = new HashMap <String, Map<IRI, Map <String, PerformanceResult>>> ();
    
    public void addReasonerPerformaceResult(String theReasonerName, Ontology theOntology, String theQuery, PerformanceResult thePerformanceResult){
        IRI id = (IRI) theOntology.getIdentifier();
        
        if (performanceresults.get(theReasonerName) == null){
            performanceresults.put(theReasonerName, new HashMap <IRI, Map <String, PerformanceResult>> ());
        }
        if (performanceresults.get(theReasonerName).get(id) == null){
            performanceresults.get(theReasonerName).put(id, new HashMap <String, PerformanceResult> ());
        }
        
        performanceresults.get(theReasonerName).get(id).put(theQuery, thePerformanceResult);
    }
    
    public void write(String theDirectory) throws IOException{
        File directory = new File(theDirectory);
        System.out.println(directory.exists());
        if (directory.listFiles() != null){
            for (File f : directory.listFiles()){
                f.delete();
            }
        }
        
        Set <IRI> allOntologiesInTest = new HashSet <IRI> ();
        Set <String> allQueriesInTest = new HashSet <String> ();
        for (String reasoner : performanceresults.keySet()){
            for (IRI id : performanceresults.get(reasoner).keySet()){
                allOntologiesInTest.add(id);
                allQueriesInTest.addAll(performanceresults.get(reasoner).get(id).keySet());
            }
        }
        
        List <IRI> sortedAllOntologiesInTest = new ArrayList <IRI>(allOntologiesInTest);
        Collections.sort(sortedAllOntologiesInTest, new Comparator <IRI> () {
            public int compare(IRI o1, IRI o2) {
                return o1.toString().compareTo(o2.toString());
            }
        });
        List <String> sortedAllQueriesInTest = new ArrayList <String>(allQueriesInTest);
        Collections.sort(sortedAllQueriesInTest);
        
        //Write ontology load time data
        File loadTimeFile = new File(directory, "ALL-average-ontology-registration-times.csv");
        BufferedWriter bw = new BufferedWriter(new FileWriter(loadTimeFile));
        bw.write("Reasoner," + toCommaDelimited(sortedAllOntologiesInTest) + "\n");
        for (String reasoner : performanceresults.keySet()){
            bw.write(reasoner);
            for (IRI id : sortedAllOntologiesInTest){
                if (performanceresults.get(reasoner).containsKey(id)){
                    Collection <PerformanceResult> values = performanceresults.get(reasoner).get(id).values();
                    int total = 0;
                    for (PerformanceResult performanceResult : values){
                        total += performanceResult.getRegisterOntology();
                    }
                    bw.write("," + (total/values.size()));
                }
                else{
                    bw.write(",-1");
                }
            }
            bw.write("\n");
        }
        bw.flush();
        bw.close();
    }

    private String toCommaDelimited(List <IRI> theList) {
        String result = "";
        for (IRI o : theList){
            if (!result.equals("")){
                result += ",";
            }
            result += o.getLocalName();
        }
        return result;
    }
}
