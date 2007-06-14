/**
 * WSML Reasoner Implementation.
 *
 * Copyright (c) 2005, University of Innsbruck, Austria.
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
 * 
 */
package org.wsml.reasoner.builtin.tptp;

import java.io.*;
import java.net.*;
import java.util.*;

import org.apache.log4j.Logger;
import org.omwg.logicalexpression.LogicalExpression;
import org.wsml.reasoner.ExternalToolException;
import org.wsml.reasoner.FOLReasonerFacade;
import org.wsml.reasoner.api.WSMLFOLReasoner.EntailmentType;
import org.wsml.reasoner.impl.WSMO4JManager;

/**
 * <p>
 * The wsmo4j interface to and from TPTP
 * </p>
 * <p>
 * $Id: FOLAbstractFacade.java,v 1.1 2007-06-14 16:38:59 hlausen Exp $
 * </p>
 * 
 * @author Holger Lausen
 * @version $Revision: 1.1 $
 */ 
public abstract class FOLAbstractFacade implements FOLReasonerFacade {

    private Logger log = Logger.getLogger(FOLAbstractFacade.class);
    WSMO4JManager wsmo4jmanager;
    String httpAddress;
    Map<String,String> convertedOntologies = new HashMap<String, String>();
    
    static public String DERI_TPTP_REASONER="http://dev1.deri.at/dont-treat-this-service-to-hard";
    static public String DERI_SPASS_PLUS_T_REASONER="http://dev1.deri.at/spass-plus-t";
    
    public FOLAbstractFacade(WSMO4JManager manager, String endpoint){
        this.wsmo4jmanager=manager;
        this.httpAddress=endpoint;
    }
    
    public List<EntailmentType> checkEntailment(String ontologyIRI,List<LogicalExpression> conjecture) {
        String ontology = convertedOntologies.get(ontologyIRI);
        if (ontology==null) throw new RuntimeException("ontology not registered");

        List<EntailmentType> results = new ArrayList<EntailmentType>();
        for (LogicalExpression le : conjecture) {
            String conjectureString = getConjecture(le);
            log.debug("checking conjecture:" +conjectureString);
            log.debug("\n\n"+ontology + "\n" + conjectureString);
            results.add(invokeHttp(ontology + "\n" + conjectureString));
        }
        return results;
    }
    
    String getConjecture(LogicalExpression le){
    	throw new RuntimeException("must be overwritten!");
    }
    
    private String encode(String str){
        try {
            return URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return str;
        }
    }
    
    private EntailmentType invokeHttp(String stuff){
            String data = encode("theory") + "="+ encode(stuff);
            URL url;
            EntailmentType result =EntailmentType.unkown;
            try {
                url = new URL(httpAddress);
            } catch (MalformedURLException e) {
                throw new RuntimeException("FOL Reasoner not correclty configured: '"+httpAddress+"' is not an URL");
            }
            log.debug("sending theory to endpoint: "+url);
            try{
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(data);
                wr.flush();
                BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = rd.readLine()) != null) {
                    log.debug(line);
                    if (line.contains("Proof found")){
                        result=EntailmentType.entailed;
                    }
                    if (line.contains("Ran out of time")){
                        result=EntailmentType.unkown;
                    }
                    if (line.contains("Completion found")){
                        result=EntailmentType.notEntailed;
                    }
                }
                wr.close();
                rd.close();
            }catch (IOException io){
                throw new RuntimeException("the remote reasoner did not respond:"+io,io);
            }
            return result;
    }

    public void deregister(String ontologyURI) throws ExternalToolException {
        convertedOntologies.remove(ontologyURI);
    }
}
