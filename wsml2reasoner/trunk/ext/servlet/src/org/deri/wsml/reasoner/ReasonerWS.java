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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.omwg.logicalexpression.LogicalExpression;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Ontology;
import org.omwg.ontology.Variable;
import org.wsml.reasoner.api.WSMLReasoner;
import org.wsml.reasoner.api.WSMLReasonerFactory;
import org.wsml.reasoner.impl.DefaultWSMLReasonerFactory;
import org.wsmo.common.IRI;
import org.wsmo.common.TopEntity;
import org.wsmo.common.exception.InvalidModelException;
import org.wsmo.factory.Factory;
import org.wsmo.factory.LogicalExpressionFactory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.validator.WsmlValidator;
import org.wsmo.wsml.Parser;
import org.wsmo.wsml.ParserException;

public class ReasonerWS{

    // PushBackBuffer default for Parser
    private final static int bufferSize = 1123123;
    
    public VariableBinding[][] getQueryAnswer(String wsmlQuery, String wsmlOntology) {
        WsmoFactory factory = Factory.createWsmoFactory(null);
        LogicalExpressionFactory leFactory = Factory.createLogicalExpressionFactory(null);
        Parser parser = Factory.createParser(null);
        
        TopEntity[] tes;
        try {
            tes = parser.parse(new StringBuffer(wsmlOntology));
        } catch (ParserException e) {
            throw new RuntimeException ("Error Parsing Ontology: "+e.getMessage());
        } catch (InvalidModelException e) {
            throw new RuntimeException ("Error Parsing Ontology: "+e.getMessage());
        }
            
        if (tes.length != 1) {
            throw new RuntimeException("Input must contain exactly ONE WSML ontology");
        }
        if (!(tes[0] instanceof Ontology)){
            throw new RuntimeException("Reasoner input must be an ontology, found: "+ tes[0].getClass());
        }
        
        Ontology ont = (Ontology) tes[0];
        
        LogicalExpression query;
        try {
            query = (LogicalExpression) leFactory.createLogicalExpression(wsmlQuery, ont);
        } catch (ParserException e) {
            throw new RuntimeException ("Error Parsing Query: "+e.getMessage());
        }
        
        WsmlValidator wv = Factory.createWsmlValidator(null);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put(WSMLReasonerFactory.PARAM_BUILT_IN_REASONER,
                WSMLReasonerFactory.BuiltInReasoner.MINS);
        WSMLReasoner reasoner = DefaultWSMLReasonerFactory.getFactory().
                createWSMLFlightReasoner(params);

        // Register ontology
        reasoner.registerOntology(ont);

        Set<Map<Variable,Term>> result = reasoner.executeQuery(
                (IRI)ont.getIdentifier(),query);
        if (result.size()==0){
            return null;
        }
        
        Iterator<Map<Variable,Term>> i = result.iterator();
        VariableBinding[][] vb = null;
        int key = 0;
        while(i.hasNext()){
            Map<Variable,Term> map = i.next();
            Iterator<Variable> keys = map.keySet().iterator();
            if (vb == null){
                vb = new VariableBinding[result.size()][map.size()];
            }
            int n=0;
            //vb[key] = new VariableBinding();
            while (keys.hasNext()){
                Variable v = keys.next();
                vb[key][n] = new VariableBinding();
                vb[key][n].key = v.toString();
                vb[key][n].value = map.get(v).toString();
                n++;
            }
            key++;
        }
        return vb;
    }
}
