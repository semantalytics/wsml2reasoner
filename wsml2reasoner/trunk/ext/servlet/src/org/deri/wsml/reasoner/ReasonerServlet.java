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
import java.net.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.deri.wsmo4j.io.serializer.wsml.*;
import org.omwg.logicalexpression.*;
import org.omwg.logicalexpression.terms.*;
import org.omwg.ontology.*;
import org.wsml.reasoner.api.*;
import org.wsml.reasoner.api.queryanswering.*;
import org.wsml.reasoner.builtin.mins.*;
import org.wsml.reasoner.impl.*;
import org.wsmo.common.*;
import org.wsmo.factory.*;
import org.wsmo.validator.*;
import org.wsmo.wsml.*;

/**
 * Web front-end for the WSML Ontobroker reasoner. Loads the given ontology into
 * the reasoner and performs the given query. The result is then displayed on
 * the Web Page.
 * 
 * 
 * @see org.deri.wsml.reasoner.ontobroker.Reasoner
 * @author Jos de Bruijn $Author: hlausen $ $Date: 2005-12-02 12:51:03 $
 */
public class ReasonerServlet extends HttpServlet {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

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
        
        boolean inFrame = request.getParameter("inframe") != null;

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

            if (request.getParameter("wsmlQuery") != null){
                wsmlQuery = request.getParameter("wsmlQuery");
                //System.out.println(wsmlQuery);
            }

            out.println("<!DOCTYPE html PUBLIC '-W3CDTD HTML 4.01 TransitionalEN'>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>DERI WSML Reasoning result</title>");
            out.println("  <link rel='shortcut icon' href='favicon.ico'/>");
            out.println("  <link rel='stylesheet' type='text/css' href='wsml.css'/>");
            out.println("</head>");
            out.println("<body><div class=\"box\">");

            if (wsmlOntology.length() == 0) {
                error("No Ontology found, enter ontology ");
            } else if (wsmlQuery.length() == 0) {
                error("No Query found, enter Query ");
            } else {
                try{
                    //System.out.println("wsmlQuery0:" +wsmlQuery);
                    doReasoning(wsmlQuery, wsmlOntology, inFrame);
                }catch (ConstraintViolationError e){
                    error("<b>An Integrity Constaint has been violated</b>:\n<br/>",e);
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
        out.println("<div class=\"error\">" + text.replace("\n","<br/>") + "</div><br/><br/><br/>");
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

    private void doReasoning(String wsmlQuery, String wsmlOntology, boolean inFrame) {
        //System.out.println("wsmlQuery1:" +wsmlQuery);

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
        WsmlValidator wv = Factory.createWsmlValidator(null);
        List errors = new LinkedList();
        if (!wv.isValid(ontology,WSML.WSML_RULE,errors, null)){
            error("Given input ontology is not within WSML flight.");
            out.print("<ul>");
            for (Object error :errors){
                ValidationError er = (ValidationError)error;
                out.println("<li>"+er.toString()+"</li>");
                
            }
            out.print("</ul>");
            return;
        }
        
        out.println("<h1>Query Result Page</h1>");
        if (!inFrame){
            out.println("<h3>Your Query</h3>");
            out.println("<p>" + wsmlQuery + "</p>");
        }
        try {
            query = _leFactory.createLogicalExpression(wsmlQuery, ontology);
        } catch (ParserException e) {
            error("Error parsing Query:"+markErrorPos(wsmlQuery,e.getErrorPos()),e);
            return;
        }

        if (query != null && ontology != null) {
            if (!inFrame){
                out.println("<h3>Query answer:</h3>");
            }

            QueryAnsweringRequest qaRequest = new QueryAnsweringRequestImpl(
                    ontology.getIdentifier().toString(), query);

            // get A reasoner
            WSMLReasoner reasoner = DefaultWSMLReasonerFactory.getFactory().
                    getWSMLFlightReasoner(WSMLReasonerFactory.BuiltInReasoner.MINS);

            // Register ontology
            reasoner.registerOntology(ontology);

            Set<Map<Variable,Term>> result = reasoner.executeQuery(
                    (IRI)ontology.getIdentifier(),query);

            if (result.size()==0){
                out.println("<pre>the query returned no variable bindings.</pre>");
            }
            else {
                // print out the results:
                out.print("<table class=\"result\"><thead><tr>");
                for (Variable var : result.iterator().next().keySet()) {
                    out.println("<th>" + var + "</th>");
                }
                out.println("</tr></thead><tbody>");
                WsmoFactory f = Factory.createWsmoFactory(null);
                
                for (Map<Variable,Term> vBinding : result) {
                    out.println("<tr>");
                    for (Variable var : vBinding.keySet()) {
                        out.println("<td>" + resolve(vBinding.get(var), ontology) +"</td>");
                    }
                    out.println("</tr>");
                }
                out.println("</tbody></table>");
            }
        }
    }
    
    private String resolve(Term iri, Ontology o){
        VisitorSerializeWSMLTerms v = new VisitorSerializeWSMLTerms(o);
        iri.accept(v);
        return v.getSerializedObject().toString();
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