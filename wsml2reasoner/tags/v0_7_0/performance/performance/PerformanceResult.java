package performance;


import java.util.HashMap;
import java.util.Map;

import org.wsml.reasoner.api.WSMLReasoner;
import org.wsml.reasoner.impl.BenchmarkHelper;

public class PerformanceResult {
    
    private long registerOntology = -1;
    private long deregisterOntology = -1;
    private Map <Integer, Long> executeQuery = new HashMap <Integer, Long> ();
    private BenchmarkHelper helper = null;

    public PerformanceResult() {  	
    }
    
    public PerformanceResult(WSMLReasoner reasoner) {
    	this.helper = new BenchmarkHelper(reasoner);
    }
    
    public Map <Integer, Long> getExecuteQuery() {
        return executeQuery;
    }

    public void addExecuteQuery(int theIteration, long theExecutionTime) {
        this.executeQuery.put(theIteration, theExecutionTime);
    }
    
    public long getAvgExecuteQuers(){
        long total=0;
        for(long time: executeQuery.values()){
            total+=time;
        }
        if (executeQuery.size()!=0){
            return total/executeQuery.size();
        }
        return -1;
    }

    public long getRegisterOntology() {
        return registerOntology;
    }

    public void setRegisterOntology(long theRegisterOntology) {
        this.registerOntology = theRegisterOntology;
    }
    
    public long getDeRegisterOntology() {
        return deregisterOntology;
    }

    public void setDeRegisterOntology(long theDeRegisterOntology) {
        this.deregisterOntology = theDeRegisterOntology;
    }

	public long getConvertTime() {
		return helper.getConvertTime();
	}

	public long getNormalizeTime() {
		return helper.getNormalizationTime();
	}

	public long getOntologyConsistencyCheckTime() {
		return helper.getConsistencyCheckTime();
	}

}
