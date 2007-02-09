/*
 * Copyright (c) 2006, University of Innsbruck, Austria.
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
 */
package org.wsml.reasoner.builtin.tptp.fileconnector;


import java.util.*;
import java.io.*;

/**
 * A class for invoking a prover for checking proof obligations.
 *
 * The prover will run locally and the number of concurrently working
 * prover process will be strictly limited.
 * 
 * The prover that is used at the moment is VAMPIRE (University of Manchester)
 *
 * The implementation of this class is thread-safe.
 *  
 * @author Uwe Keller, DERI Innsbruck
 */
public class LocalProverConnector implements IProverConnector {
    
    private static final IProverConnector SINGLETON = new LocalProverConnector();
    
    /** A list of the currently acitve provers that are running on the local machine */
    private Vector runningProvers;
    
    // TO DO: Move this to an environment class instead of using a local
    // static setting. The enironment should be initialized by a property file
    // describing the current configuration!
    
    private final int MAX_NUMBER_OF_ACTIVE_PROVERS = 5;
    
    // VAMPIRE FOF system for CASC-19
    
    private final String PATH_TO_PROVER = "bin/vampire";
    private final String PROVER_OPTIONS = "--mode casc-19 --proof off";
    private final int PROVER_TIMEOUT = 5; // in seconds. 
    
    private final String SUCCESSFUL_PATTERN = "proved";
   
    /*
    // Settings for VAMPIRE 7 (Kernel version only operating on CNF) for CASC-J2
    private final String PATH_TO_PROVER = "bin/vampire7kernel";
    private final String PROVER_OPTIONS = "--proof off";
    private final int PROVER_TIMEOUT = 15; // in seconds. 
    
    private final String SUCCESSFUL_PATTERN = "Proof found.";
    
    */
   
    /*
    // Otter FOF
    
    private final String PATH_TO_PROVER = "bin/otter";
    private final String PROVER_OPTIONS = " < ";
    private final int PROVER_TIMEOUT = 5; // in seconds. 
    
    private final String SUCCESSFUL_PATTERN = "- PROOF -";
    
    */
    private String START_PROVER_COMMAND;
    
    private final String PO_DIR_PATH = "resources/proofobligations/";
    
    
    
    /** An object to wait for starting a prover */
    private Object waitForProverObject = new Object();
    
    /**
     * Creates an instance of a prover connector and initializes it.
     * 
     * For creating instances the factory method getInstance() should be used
     * instead.
     */
    private LocalProverConnector(){
        init();
    }
    
    /**
     * Get an instance of a prover connector that can be used
     * to check proof obligations with a prover.
     * 
     * @return a singleton instance of prover connector.
     */
    public static IProverConnector getInstance(){
        return SINGLETON;
    }
    
    protected void init(){
        runningProvers = new Vector();
        START_PROVER_COMMAND = PATH_TO_PROVER + " " +  PROVER_OPTIONS + " ";
    }
    
    /**
     * Connects to a prover and checks whether the given proof obligation
     * holds.
     * 
     * Implementation is thread-safe.
     * 
     * @param po - the proof obligation to be checked.
     * @return true iff the proof obligation has successfully been proven.
     * @throws ProverConnectorException
     */
    public boolean check(ProofObligation po) throws ProverConnectorException {
        boolean result = false;
        
        long startTime;
        long stopTime;
        
        int n;
        synchronized(runningProvers){
            n = runningProvers.size();
        }
        
        while (n >= MAX_NUMBER_OF_ACTIVE_PROVERS){
            try {
                synchronized(waitForProverObject){
                    waitForProverObject.wait();
                }
            } catch(InterruptedException ie){
                ie.printStackTrace();
            }
            
            synchronized(runningProvers){
                n = runningProvers.size();
            }
      
        }
        
        // Start a new prover process for the given proof obligation ...
        
        String proofObligationFile = PO_DIR_PATH + po.getIdentifier();
        Runtime runt = java.lang.Runtime.getRuntime();
        StringBuffer proverOutputBuffer = new StringBuffer();
        Process newProver; 
        
        try {
            System.err.println(" -- Checking PO: " + po.getIdentifier());
            
	        synchronized(runningProvers){
	            startTime = System.currentTimeMillis();
	            newProver = runt.exec(START_PROVER_COMMAND + " " + proofObligationFile);
	            runningProvers.add(newProver);
	        }
	        
	        stopTime = startTime;
                        
            try {
                
                InputStream is = newProver.getInputStream();
                InputStreamReader sr = new java.io.InputStreamReader(is);
                
                boolean stop = false;
                
                while (!stop) {
                    int c = sr.read();
                    if (c != -1) {
                        proverOutputBuffer.append((char)c);
                    } else {
                        stop = true;
                    }
                }
                
                stopTime = System.currentTimeMillis();
                
                // delete the prover from the active list
                
                synchronized(runningProvers){
    	            runningProvers.remove(newProver);
    	        }
                
		 System.err.println("PROVER CALL OUTPUT: " + proverOutputBuffer.toString());
                result = (proverOutputBuffer.toString().toLowerCase().indexOf(this.SUCCESSFUL_PATTERN) != -1);
		        //  result = (newProver.exitValue() == 103); // according to the OTTER Manual
		 
            } catch(Exception e) {
                e.printStackTrace();
                result = false;
            }
             
            long duration = (stopTime - startTime); // in ms.
            
            System.err.println(" -- Done Checking PO: " + po.getIdentifier() + " [ "+(result? "match" : "no match")+" in ("+duration+" ms) ]");
        } catch (Exception e2){
            e2.printStackTrace();
        }
        
        return result;
        
        
    }

}
