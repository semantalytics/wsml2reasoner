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
package org.wsml.reasoner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.omwg.logicalexpression.LogicalExpression;
import org.wsml.reasoner.api.FOLReasoner.EntailmentType;
import org.wsml.reasoner.builtin.tptp.TPTPSymbolMap;
import org.wsml.reasoner.impl.WSMO4JManager;

/**
 * <p>
 * The wsmo4j interface to and from TPTP
 * </p>
 * <p>
 * $Id: FOLAbstractFacade.java,v 1.1 2007-08-10 09:44:49 graham Exp $
 * </p>
 * 
 * @author Holger Lausen
 * @version $Revision: 1.1 $
 */
public abstract class FOLAbstractFacade implements FOLReasonerFacade {

    private Logger log = Logger.getLogger(FOLAbstractFacade.class);

    WSMO4JManager wsmo4jmanager;

    String httpAddress;

    public String convertedOntology;

    protected TPTPSymbolMap symbolMap;

    static public String DERI_TPTP_REASONER = "http://dev1.deri.at/dont-treat-this-service-to-hard";

    static public String DERI_SPASS_REASONER = "http://dev1.deri.at/spass-plus-t";

    public FOLAbstractFacade(WSMO4JManager manager, String endpoint) {
        this.wsmo4jmanager = manager;
        this.httpAddress = endpoint;
    }

    public List<EntailmentType> checkEntailment(List<LogicalExpression> conjecture) {
        if (convertedOntology == null)
            throw new RuntimeException("ontology not registered");
        TPTPSymbolMap map = symbolMap;
        if (map == null)
            throw new RuntimeException("Could not find a symbolmap for iri, error in conversion");

        List<EntailmentType> results = new ArrayList<EntailmentType>();
        for (LogicalExpression le : conjecture) {
            String conjectureString = getConjecture(le, map);
            log.debug("checking conjecture:" + conjectureString);
            // log.debug("\n\n"+ontology + "\n" + conjectureString);
            results.add(invokeHttp(convertedOntology + "\n" + conjectureString));
        }
        return results;
    }

    public abstract String getConjecture(LogicalExpression le, TPTPSymbolMap map);

    private String encode(String str) {
        try {
            return URLEncoder.encode(str, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return str;
        }
    }

    protected EntailmentType invokeHttp(String stuff) {
        String data = encode("theory") + "=" + encode(stuff);
        URL url;
        EntailmentType result = EntailmentType.unkown;
        try {
            url = new URL(httpAddress);
        }
        catch (MalformedURLException e) {
            throw new RuntimeException("FOL Reasoner not correclty configured: '" + httpAddress + "' is not an URL");
        }
        log.debug("sending theory to endpoint: " + url);
        try {
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                log.debug("resultline: " + line);
                if (line.contains("Proof found")) {
                    result = EntailmentType.entailed;
                }
                if (line.contains("Ran out of time")) {
                    result = EntailmentType.unkown;
                }
                if (line.contains("Completion found")) {
                    result = EntailmentType.notEntailed;
                }
            }
            wr.close();
            rd.close();
        }
        catch (IOException io) {
            throw new RuntimeException("the remote reasoner did not respond:" + io, io);
        }
        return result;
    }

    public void deregister() throws ExternalToolException {
        convertedOntology = null;
    }
}
