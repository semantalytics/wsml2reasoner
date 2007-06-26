package performance;

import java.io.*;
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
import org.wsmo.common.exception.InvalidModelException;
import org.wsmo.wsml.ParserException;

import performance.chart.Chart;


public class PerformanceResults {

    //   Reasoner    Ontoloy   query
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
    
    FileFilter nonWsml = new FileFilter(){

		public boolean accept(File pathname) {
			return !pathname.getName().endsWith("wsml");
		}
    	
    };
    
    private void clearDirectory(File directory){
        if (!directory.exists()){
        	directory.mkdir();
        }
        if (directory.listFiles(nonWsml) != null){
            for (File f : directory.listFiles(nonWsml)){
                f.delete();
            }
        }
    }
    
    public List<IRI> getAllOntologiesInResultSorted(){
        Set <IRI> allOntologiesInTest = new HashSet <IRI> ();
        for (String reasoner : performanceresults.keySet()){
            for (IRI id : performanceresults.get(reasoner).keySet()){
                allOntologiesInTest.add(id);
            }
        }
        
        List <IRI> sortedAllOntologiesInTest = new ArrayList <IRI>(allOntologiesInTest);
        Collections.sort(sortedAllOntologiesInTest, new Comparator <IRI> () {
            public int compare(IRI o1, IRI o2) {
                return o1.toString().compareTo(o2.toString());
            }
        });
        return sortedAllOntologiesInTest;
    }
    
    public List<String> getAllQueriesInResultSorted(){
        Set<String> allQueriesInTest = new HashSet<String>();
        for (String reasoner : performanceresults.keySet()){
            for (IRI id : performanceresults.get(reasoner).keySet()){
                for (String query:performanceresults.get(reasoner).get(id).keySet()){
                	allQueriesInTest.add(query);
                }
            }
        }
        List <String> sortedAllQueriesInTest = new ArrayList <String>(allQueriesInTest);
        Collections.sort(sortedAllQueriesInTest);
        return sortedAllQueriesInTest;
    }
    
    public void writeCSVLoadTime(File directory) throws IOException{
        List<IRI> sortedAllOntologiesInTest = getAllOntologiesInResultSorted();
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
                    int size = values.size();
                    for (PerformanceResult performanceResult : values){
                    	long registerTime = performanceResult.getRegisterOntology();
                    	if (registerTime>0) {
                    		total +=registerTime;
                    		size++;
                    	}
                    }
                    if ((total/size) == 0) {
                    	bw.write(",-1");
                    }
                    else {
                    	bw.write("," + (total/size));
                    }
                }
                else{
                    bw.write(",-1");
                }
            }
            bw.write("\n");
        }
        bw.flush();
        bw.close();
        System.out.println("Written to: " + loadTimeFile.getAbsolutePath());
    }

    public void writeCSVQueryTimes(File directory) throws IOException{
//    	 Write ontology query time data
        List <String> sortedAllQueriesInTest = getAllQueriesInResultSorted();
        List<IRI> sortedAllOntologiesInTest = getAllOntologiesInResultSorted();
        Collections.sort(sortedAllQueriesInTest);
        
        for (String queryid : sortedAllQueriesInTest){
        	File queryTimeFile = new File(directory, "average-ontology-"+queryid+"-times.csv");
        	BufferedWriter bw = new BufferedWriter(new FileWriter(queryTimeFile));
        	bw.write("Reasoner," + toCommaDelimited(sortedAllOntologiesInTest) + "\n");
        	for (String reasoner : performanceresults.keySet()) {
				bw.write(reasoner);
				for (IRI id : sortedAllOntologiesInTest) {
					if (performanceresults.get(reasoner).containsKey(id)) {
						PerformanceResult result = performanceresults.get(
								reasoner).get(id).get(queryid);
						if (result != null) {
							bw.write("," + result.getAvgExecuteQuers());
						} else {
							bw.write(",-1");
						}
					} else {
						bw.write(",-1");
					}
				}
				bw.write("\n");
        	}
            bw.flush();
            bw.close();
            System.out.println("Written to: " + queryTimeFile.getAbsolutePath());
        }
    }
    public void writeAll(String theDirectory) throws IOException, ParserException, InvalidModelException{
        File directory = new File(theDirectory);
        clearDirectory(directory);
        writeCSVLoadTime(directory);
        writeCSVQueryTimes(directory);
        
        new Chart().doChartsFromCSV(directory);
    }
    
    private String toCommaDelimited(List theList) {
        String result = "";
        for (Object o : theList){
            if (!result.equals("")){
                result += ",";
            }
            if(o instanceof IRI){
                result += ((IRI)o).getLocalName(); 
            }else{
                result += o.toString();
            }
        }
        return result;
    }
}
