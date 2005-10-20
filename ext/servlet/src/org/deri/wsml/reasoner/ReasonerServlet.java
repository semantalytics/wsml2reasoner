/*
 Copyright (c) 2004, DERI Innsbruck


 This library is free software; you can redistribute it and/or modify it under
 the terms of the GNU Lesser General Public License as published by the Free
 Software Foundation; either version 2.1 of the License, or (at your option)
 any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 details.
 You should have received a copy of the GNU Lesser General Public License along
 with this library; if not, write to the Free Software Foundation, Inc.,
 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.deri.wsml.reasoner;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.deri.wsmo4j.validator.ValidationError;
import org.deri.wsmo4j.validator.WsmlValidatorImpl;
import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.ontology.Ontology;
import org.wsml.reasoner.api.*;
import org.wsml.reasoner.api.queryanswering.*;
import org.wsml.reasoner.impl.*;
import org.wsmo.common.*;
import org.wsmo.factory.*;
import org.wsmo.validator.WsmlValidator;
import org.wsmo.wsml.Parser;
import org.wsmo.wsml.ParserException;

/**
 * Web front-end for the WSML Ontobroker reasoner. Loads the given ontology into
 * the reasoner and performs the given query. The result is then displayed on
 * the Web Page.
 * 
 * 
 * @see org.deri.wsml.reasoner.ontobroker.Reasoner
 * @author Jos de Bruijn $Author: hlausen $ $Date: 2005-10-20 08:52:19 $
 */
public class ReasonerServlet extends HttpServlet {
    private boolean debug = false;

    private PrintWriter out;

    // PushBackBuffer default for Parser
    private final static int bufferSize = 1123123;

    private String text = "";

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // If it is a get request forward to doPost()
        doPost(request, response);
    }

    /**
     * invoked by button click in validator.html
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        out = response.getWriter();

        String wsmlOntology = "";
        String wsmlQuery = "";
        
        //needed to get rid of old object in weak hashmap
        System.gc();

        try {

            // wsml file input from url
            if (request.getParameter("url") != null
                    && request.getParameter("url").indexOf("[url]") == -1) {
                URL url = new URL(request.getParameter("url"));
                InputStream in = url.openStream();
                int k;
                byte buff[] = new byte[bufferSize];
                OutputStream xOutputStream = new ByteArrayOutputStream(
                        bufferSize);
                while ((k = in.read(buff)) != -1) {
                    xOutputStream.write(buff, 0, k);
                }
                wsmlOntology = xOutputStream.toString();

            }
            // wsml file input from textarea
            else if (request.getParameter("wsmlOntology") != null)
                wsmlOntology = request.getParameter("wsmlOntology");

            if (request.getParameter("wsmlQuery") != null)
                wsmlQuery = request.getParameter("wsmlQuery");

            out.println("<!DOCTYPE html PUBLIC '-W3CDTD HTML 4.01 TransitionalEN'>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>DERI WSML Reasoning result</title>");
            out.println("  <link rel='shortcut icon' href='favicon.ico'/>");
            out.println("  <link rel='stylesheet' type='text/css' href='reasoner.css'/>");
            out.println("</head>");
            out.println("<body><div class=\"box\">");

            if (wsmlOntology.length() == 0) {
                error("No Ontology found, enter ontology ");
            } else if (wsmlQuery.length() == 0) {
                error("No Query found, enter Query ");
            } else {
                try{
                    doReasoning(wsmlQuery, wsmlOntology);
                }catch (Exception e){
                    error("Error:",e);
                }
            }
        } catch (MalformedURLException e) {
            error("Input URL malformed: " + request.getParameter("url"));
        } catch (Exception e) {
            e.printStackTrace();
            error(e.getMessage());
        }
        out.println("</div></body>");
        out.println("</html>");
    }

    private void error(String text) {
        out.println("<div class=\"error\">" + text + "</div><br/><br/><br/>");
    }

    private void error(String text, Throwable e) {
        error(text+ " "+e.getMessage());
        if (e instanceof ParserException){
            return;
        }
        out.println("<div class=\"trace\">");
        String indent="";
        while(e !=null){
            StackTraceElement[] t = e.getStackTrace();
            out.println(e);
            for (int i=0; i<t.length; i++){
                out.println(indent+" &nbsp; "+t[i]+"<br/>");
            }
            e = e.getCause();
            indent += " &nbsp; &nbsp; ";
        }
        out.println("</div>");
    }

    private void doReasoning(String wsmlQuery, String wsmlOntology) {
        // setup factories
        if (debug)
            out.println("doReasoning");

        WsmoFactory _factory = WSMO4JManager.getWSMOFactory();
        LogicalExpressionFactory _leFactory = WSMO4JManager.getLogicalExpressionFactory();
        Parser _parser = Factory.createParser(null);

        Ontology ontology = null;
        LogicalExpression query = null;
        try {
            TopEntity te = _parser.parse(new StringBuffer(wsmlOntology))[0];
            if (!(te instanceof Ontology)){
                error("This reasoner can only process ontologies at present (the first TopEntity in the input file was not an ontology)");
                return;
            }
            ontology = (Ontology)te;
        } catch (Exception e) {
            String errorline = "";
            if (e instanceof ParserException){
                String[] lines = wsmlOntology.split("\\n");
                int line = ((ParserException)e).getErrorLine()-1;
                if (line > 0 && line <lines.length){
                    errorline="<br/> Line "+(line+1) + ": "+ 
                            markErrorPos(lines[line],((ParserException)e).getErrorPos())+"<br/>";
                }
            }
            error("Could not parse Ontology:"+errorline,e);
            return;
        }
        WsmlValidator wv = new WsmlValidatorImpl();
        List errors = new LinkedList();
        if (!wv.isValid(ontology,WSML.WSML_FLIGHT,errors)){
            error("Given input ontology is not within WSML flight.");
            out.print("<ul>");
            for (Object error :errors){
                ValidationError er = (ValidationError)error;
                out.println("<li>"+er.toString()+"</li>");
                
            }
            out.print("</ul>");
            return;
        }
        
        out.println("<h1>Query Result</h1>");
        out.println("<h3>Your Query</h3>");
        out.println("<p>" + wsmlQuery + "</p>");
        try {
            query = _leFactory.createLogicalExpression(wsmlQuery, ontology);
        } catch (ParserException e) {
            String errorline="<br/> "+wsmlQuery+"<br/>";
            error("Error parsing Query:"+markErrorPos(errorline,e.getErrorPos()),e);
            return;
        }

        if (query != null && ontology != null) {
            out.println("<h3>Query answer:</h3>");

            QueryAnsweringRequest qaRequest = new QueryAnsweringRequestImpl(
                    ontology.getIdentifier().toString(), query);

            // get A reasoner
            Map<String, Object> params = new HashMap<String, Object>();
            params.put(WSMLReasonerFactory.PARAM_WSML_VARIANT,
                    WSMLReasonerFactory.WSMLVariant.WSML_CORE);
            params.put(WSMLReasonerFactory.PARAM_BUILT_IN_REASONER,
                    WSMLReasonerFactory.BuiltInReasoner.MINS);
            WSMLReasoner reasoner = DefaultWSMLReasonerFactory.getFactory()
                    .getWSMLReasoner(params);

            // Register ontology
            Set<Ontology> ontos = new HashSet<Ontology>();
            ontos.add(ontology);
            OntologyRegistrationRequest regReq = new OntologyRegistrationRequestImpl(
                    ontos);
            reasoner.execute(regReq);

            QueryAnsweringResult result = (QueryAnsweringResult) reasoner
                    .execute(qaRequest);

            if (result.size()==0){
                out.println("<pre>the query returned no variable bindings.</pre>");
            }
            else {
                // print out the results:
                out.print("<table class=\"result\"><thead><tr>");
                for (String var : result.iterator().next().keySet()) {
                    out.println("<th>?" + var + "</th>");
                }
                out.println("</tr></thead><tbody>");
                WsmoFactory f = Factory.createWsmoFactory(null);
                
                for (VariableBinding vBinding : result) {
                    out.println("<tr>");
                    for (String var : vBinding.keySet()) {
                        out.println("<td>" + resolve(vBinding.get(var), ontology) +"</td>");
                    }
                    out.println("</tr>");
                }
                out.println("</tbody></table>");
            }
        }
    }
    
    private String resolve(String iri, Ontology o){
        if (iri.startsWith(o.getDefaultNamespace().getIRI().toString())){
            return iri.substring(o.getDefaultNamespace().getIRI().toString().length());
        }
        for (Object nso : o.listNamespaces()){
            Namespace ns = (Namespace)nso;
            if (iri.startsWith(ns.getIRI().toString())){
                return ns.getPrefix()+iri.substring(ns.getIRI().toString().length());
            }
        }
        return iri;
    }
    
    private String markErrorPos(String error, int pos){
        if (pos<0)
            return error;
        StringBuffer ret = new StringBuffer("<span style='color:black'>"+
                error.substring(0,pos-1));
        ret.append("<b><i>");
        int i=pos-1;
        for (; i<error.length() && error.charAt(i)!=' '; i++){
            ret.append(error.charAt(i));
        }
        ret.append("</i></b>");
        ret.append(error.substring(i)+"</span>");
        return ret.toString();
    }

}
