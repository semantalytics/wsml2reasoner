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
 * $Id: TPTPFacade.java,v 1.2 2007-03-20 20:30:53 hlausen Exp $
 * </p>
 * 
 * @author Holger Lausen
 * @version $Revision: 1.2 $
 */
public class TPTPFacade implements FOLReasonerFacade {

    WSMO4JManager wsmo4jmanager;
    Logger log = Logger.getLogger(TPTPFacade.class);
    
    private String httpAddress="http://dev1.deri.at/dont-treat-this-service-to-hard";
    
    public TPTPFacade(WSMO4JManager manager){
        this.wsmo4jmanager=manager;
    }
    
    public List<EntailmentType> checkEntailment(String ontologyIRI,
            List<LogicalExpression> conjecture) {
        
        String ontology = convertedOntologies.get(ontologyIRI);
        if (ontology==null) throw new RuntimeException("ontology not registered");
        
        TPTPLESerializeVisitor les = new TPTPLESerializeVisitor();
        List<EntailmentType> results = new ArrayList<EntailmentType>();
        for (LogicalExpression le : conjecture) {
            le.accept(les);
            String conjectureString = les.getSerializedObject();
            log.debug("checking conjecture"+conjectureString);
            conjectureString = "fof(id" + id++ + ",conjecture, ("
                    + conjectureString + ")). \n";
            // log.debug(newExpression);
            results.add(invokeHttp(ontology + "\n" + conjectureString));
        }
        return results;
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


    Map<String,String> convertedOntologies = new HashMap<String, String>();
    
    
    public static int id=0;
    /**
     * I guess the cleverest would be to make a file based storage
     * for the converted Ontologies, however for now h HasMap should
     * do it..
     */
    public void register(String ontologyURI, Set<LogicalExpression> expressions) throws ExternalToolException {
        TPTPLESerializeVisitor les = new TPTPLESerializeVisitor();
        String kb ="";
        for (LogicalExpression le:expressions ){
            le.accept(les);
            String newExpression = les.getSerializedObject();
            newExpression = "fof(id"+id++ +",axiom, ("+newExpression +")). \n";
            //log.debug(newExpression);
//            System.out.print(newExpression);
            kb+=newExpression;
        }
        convertedOntologies.put(ontologyURI, kb);
    }
}
