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

import org.deri.wsmo4j.io.parser.wsml.LogExprParserImpl;
import org.deri.wsmo4j.logexpression.LogicalExpressionFactoryImpl;
import org.omwg.logexpression.LogicalExpression;
import org.omwg.logexpression.LogicalExpressionFactory;
import org.omwg.ontology.Ontology;
import org.wsml.reasoner.api.*;
import org.wsml.reasoner.api.queryanswering.*;
import org.wsml.reasoner.impl.*;
import org.wsmo.common.exception.InvalidModelException;
import org.wsmo.factory.Factory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.Parser;
import org.wsmo.wsml.ParserException;

/**
 * Web front-end for the WSML Ontobroker reasoner. Loads the given ontology into
 *   the reasoner and performs the given query. The result is then displayed on the
 *   Web Page.
 * 
 *  
 * @see org.deri.wsml.reasoner.ontobroker.Reasoner
 * @author Jos de Bruijn
 * $Author: hlausen $
 * $Date: 2005-09-01 09:31:37 $
 */
public class ReasonerServlet extends HttpServlet {
    private boolean debug=false;
	private PrintWriter out;
	//PushBackBuffer default for Parser 
	private final static int bufferSize=1123123;
	
    private String text="";
    public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

    	// If it is a get request forward to doPost()
    	doPost(request, response);
	 }
	
    
	/**
	 *	invoked by button click in validator.html 
	 */
    public void doPost(	HttpServletRequest request,	HttpServletResponse response )
    		throws ServletException, IOException {
		
		out = response.getWriter();

        String wsmlOntology = "";
		String wsmlQuery = "";
		
		try {
		
			// wsml file input from url
			if(  request.getParameter("url")!=null && request.getParameter("url").indexOf("[url]")==-1 )
			{ 
				URL url = new URL(request.getParameter("url"));
				InputStream in = url.openStream();
				int k;
				byte buff[] = new byte[bufferSize];
				OutputStream xOutputStream = new ByteArrayOutputStream(bufferSize);
				while ( (k=in.read(buff) ) != -1){
					xOutputStream.write(buff,0,k);
				}	
				wsmlOntology = xOutputStream.toString();
                
			}
			// wsml file input from textarea
			else 
				if( request.getParameter("wsmlOntology") != null)
					wsmlOntology=request.getParameter("wsmlOntology");
				
			if( request.getParameter("wsmlQuery") != null)
					wsmlQuery=request.getParameter("wsmlQuery");
					
			out.println( "<!DOCTYPE html PUBLIC '-W3CDTD HTML 4.01 TransitionalEN'>" );
			out.println("<html>");
			out.println("<head>");
			out.println("<title>DERI WSML Reasoning result</title>");
			out.println("  <link rel='stylesheet' type='text/css' href='validator.css'>");
			out.println("</head>");
			out.println("<body>");
				
			if( wsmlOntology.length() == 0 ) {
                error("No Ontology found, enter ontology ");
			} else if (wsmlQuery.length() ==0 ){
			    error("No Query found, enter Query ");
            } else {
                doReasoning(wsmlQuery, wsmlOntology);
			}
		}
		catch(MalformedURLException e) {
			error("Input URL malformed: " + request.getParameter("url"));
		}
		catch(Exception e) {
            e.printStackTrace();
			error(e.getMessage());
		}
		out.println("</body>");
		out.println("</html>");					
	}

    private void error (String text){
        out.println("<font color='#FF0000'>" +
                text + " and try <a href=\"Javascript:History.back()\">again</a>." +
                "</font>");

    }
    
    private void doReasoning (String wsmlQuery, String wsmlOntology){
        //setup factories
        if (debug) out.println("doReasoning");

        WsmoFactory _factory = Factory.createWsmoFactory(null);
        LogicalExpressionFactory _leFactory = new LogicalExpressionFactoryImpl(null);
        Map properties = new HashMap();
        properties.put(Parser.PARSER_LE_FACTORY, _leFactory);
        properties.put(Parser.PARSER_WSMO_FACTORY, _factory);
        Parser _parser = Factory.createParser(properties);
        
        Ontology ontology = null;
        LogicalExpression query = null;
        try {
            ontology = (Ontology)_parser.parse(new StringBuffer(wsmlOntology))[0];
        } catch (ParserException e) {
            error("Could not parse Ontology:<br>"+e.getMessage());
        } catch (InvalidModelException e) {
            error("Could not parse Ontology:<br>"+e.getMessage());
        } catch (ClassCastException e){
            error("First Element of input was no Ontology :<br>"+e.getMessage());
        } 
        out.println("<h2>Your Query</h2>");
        out.println("<p>"+wsmlQuery+"</p>");

        org.omwg.logexpression.io.Parser _leParser = LogExprParserImpl.getInstance(ontology); 
        try {
            query = _leParser.parse(wsmlQuery);
        } catch (IOException e) {
            error("Error parsing Query:" + e.getStackTrace().toString());
        } catch (ParserException e) {
            error("Error parsing Query:" + e.getStackTrace().toString());
        } catch (InvalidModelException e) {
            error("Error parsing Query:" + e.getStackTrace().toString());
        }

        if (query != null && ontology != null){
            out.println("<h2>Query answer:</h2>");

            QueryAnsweringRequest qaRequest = 
                    new QueryAnsweringRequestImpl(ontology.getIdentifier().toString(), query);
            
            //get A reasoner
            Map<String, Object> params = new HashMap<String, Object>();
            params.put(WSMLReasonerFactory.PARAM_WSML_VARIANT,
                    WSMLReasonerFactory.WSMLVariant.WSML_CORE);
            params.put(WSMLReasonerFactory.PARAM_BUILT_IN_REASONER,
                    WSMLReasonerFactory.BuiltInReasoner.MINS);
            WSMLReasoner reasoner = DefaultWSMLReasonerFactory.getFactory().getWSMLReasoner(
                    params);
            
            // Register ontology
            Set<Ontology> ontos = new HashSet<Ontology>();
            ontos.add(ontology);
            OntologyRegistrationRequest regReq = new OntologyRegistrationRequestImpl(
                    ontos);
            reasoner.execute(regReq);
            
            QueryAnsweringResult result = (QueryAnsweringResult) reasoner
                    .execute(qaRequest);
    
            // print out the results:
            out.println("<pre>");
            for (VariableBinding vBinding : result) {
                for (String var : vBinding.keySet()) {
                    out.print("  ?" + var + ": " + vBinding.get(var));
                }
                out.println("<br/>");
            }
            out.println("</pre>");
        }
    }
    

}
