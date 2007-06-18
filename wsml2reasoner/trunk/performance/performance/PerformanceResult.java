package performance;


import java.util.HashMap;
import java.util.Map;

public class PerformanceResult {
    
    private long registerOntology = 0;
    private long deregisterOntology = 0;
    private Map <Integer, Long> executeQuery = new HashMap <Integer, Long> ();

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
}
