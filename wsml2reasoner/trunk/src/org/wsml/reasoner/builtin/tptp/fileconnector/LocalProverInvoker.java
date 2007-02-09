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


import java.io.*;

/**
 * @author Uwe Keller, DERI Innsbruck
 */
public class LocalProverInvoker  {
    
    // private final String PATH_TO_PROVER = "bin/vampire";
    private final String PATH_TO_PROVER = "/home/swf/vampire";
    private final String PROVER_OPTIONS = "--mode casc-19 --proof off";
    private final int PROVER_TIMEOUT = 15; // in seconds. 
    
    private final String SUCCESSFUL_PATTERN = "proved";
   
    private String START_PROVER_COMMAND;
    
    private final String PO_DIR_PATH = "/home/swf/resources/proofobligations/";
    
  
    /**
     * Invokes the prover (process) with the given proof obligation 
     * in String format (TPTP)
     * @param po_id - identifier of the proof obligation
     * @param po_content - TPTP description of the proof obligation
     * 
     * @see org.deri.swf.prover.IProverConnector#check(org.deri.swf.prover.ProofObligation)
     */
    public boolean check(String po_content, String po_id) {
        
        START_PROVER_COMMAND = PATH_TO_PROVER + " " +  PROVER_OPTIONS + " " + "-t " + PROVER_TIMEOUT;
        
        boolean result = false;
        
        long startTime;
        long stopTime;
        
        // Start a new prover process for the given proof obligation ...
        
        File poFile = new File(PO_DIR_PATH + po_id);
        
        System.out.println("Generating PO-File: " + poFile.getName() + "["+poFile.getPath()+"]");
        
        // First write TPTP File.
        
        try {
            
            FileWriter fw = new FileWriter(poFile);
            fw.write(po_content);
            fw.close();
        
            System.out.println("... finished!");
            
        } catch (IOException io){
            io.printStackTrace();
            return false;
        }
        
        
        String proofObligationFile = poFile.getPath();
        Runtime runt = java.lang.Runtime.getRuntime();
        StringBuffer proverOutputBuffer = new StringBuffer();
        Process newProver; 
        
        try {
            System.err.println(" -- Checking PO: " + po_id);
           
	        startTime = System.currentTimeMillis();
	        newProver = runt.exec(START_PROVER_COMMAND + " " + proofObligationFile);
	        
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
                
            
                System.err.println("PROVER CALL OUTPUT: " + proverOutputBuffer.toString());
                result = (proverOutputBuffer.toString().toLowerCase().indexOf(this.SUCCESSFUL_PATTERN) != -1);
		        
            } catch(Exception e) {
                e.printStackTrace();
                result = false;
            }
             
            long duration = (stopTime - startTime); // in ms.
            
            System.err.println(" -- Done Checking PO: " + po_id + " [ "+(result? "match" : "no match")+" in ("+duration+" ms) ]");
        } catch (Exception e2){
            e2.printStackTrace();
        }
        
        return result;
      
    }

}
