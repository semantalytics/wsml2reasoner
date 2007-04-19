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
