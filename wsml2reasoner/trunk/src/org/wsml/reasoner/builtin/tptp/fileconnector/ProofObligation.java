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

import java.io.BufferedReader;
import java.io.IOException;

/**
 * @author Uwe Keller, DERI Innsbruck
 */
public abstract class ProofObligation {

    
    protected String id;
    
    public ProofObligation(String poID){
        id = poID;
    }
    
    
    public String getIdentifier(){
        return id;
    }
    
    protected abstract String generateProofObligation();
    
    public String toString(){
        return generateProofObligation();
    }
    
    
    /**
     * Reads the data accessed by the given reader in a string.
     * 
     * Auxilliary function that should be located elsewhere :)
     * 
     * @param br
     * @return a string representation of the access data.
     * @throws IOException
     */
    protected static String readAll(BufferedReader br) throws IOException {
        String res = "";
		String s = null;
		    do {
		      		        
		        s = br.readLine();
		        if (s != null) {
		            res += s + "\n";
		        }
		    } while( s != null );
		    
		    br.close();
		    
		   return res;
    }
}
