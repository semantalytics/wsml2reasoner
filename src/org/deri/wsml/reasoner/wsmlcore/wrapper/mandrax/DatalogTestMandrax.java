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

package org.deri.wsml.reasoner.wsmlcore.wrapper.mandrax;

import java.util.*;
import java.util.logging.*;

import org.deri.wsml.reasoner.wsmlcore.datalog.*;
import org.deri.wsml.reasoner.wsmlcore.wrapper.*;
import org.deri.wsml.reasoner.api.queryanswering.*;

public class DatalogTestMandrax {

    public static void main(String[] args){
        Logger log = Logger.getLogger("org.deri.wsml.reasoner.wsmlcore.wrapper.test");
        {
            Logger wrapperLogger = Logger.getLogger("org.deri.wsml.reasoner.wsmlcore.wrapper.mandrax");
            wrapperLogger.setLevel(Level.OFF);
            
            log.setLevel(Level.INFO);
        }
        
        log.info("Testing Mandrax invocation ...");
        
        
        try {
            
            org.deri.wsml.reasoner.wsmlcore.datalog.Predicate p;
            org.deri.wsml.reasoner.wsmlcore.datalog.Variable v;
            org.deri.wsml.reasoner.wsmlcore.datalog.Literal l;
            org.deri.wsml.reasoner.wsmlcore.datalog.Rule r;
            
            // Construct a simple knowledge base
            
            log.info("(1) Constructing simple knowledgebase");
            
            Program kb = new Program();
           
            Literal h;
            
            // Some facts 
            
            h  = new Literal(new org.deri.wsml.reasoner.wsmlcore.datalog.Predicate("arc",2), new Constant[]{new Constant("a"),new Constant("b")});
            kb.add(new org.deri.wsml.reasoner.wsmlcore.datalog.Rule(h));
            h  = new Literal(new org.deri.wsml.reasoner.wsmlcore.datalog.Predicate("arc",2), new Constant[]{new Constant("b"),new Constant("c")});
            kb.add(new org.deri.wsml.reasoner.wsmlcore.datalog.Rule(h));
            h  = new Literal(new org.deri.wsml.reasoner.wsmlcore.datalog.Predicate("arc",2), new Constant[]{new Constant("c"),new Constant("d")});
            kb.add(new org.deri.wsml.reasoner.wsmlcore.datalog.Rule(h));
            h  = new Literal(new org.deri.wsml.reasoner.wsmlcore.datalog.Predicate("arc",2), new Constant[]{new Constant("c"),new Constant("e")});
            kb.add(new org.deri.wsml.reasoner.wsmlcore.datalog.Rule(h));
            h  = new Literal(new org.deri.wsml.reasoner.wsmlcore.datalog.Predicate("arc",2), new Constant[]{new Constant("c"),new Constant("a")});
            kb.add(new org.deri.wsml.reasoner.wsmlcore.datalog.Rule(h));
            h  = new Literal(new org.deri.wsml.reasoner.wsmlcore.datalog.Predicate("arc",2), new Constant[]{new Constant("c"),new Constant("f")});
            kb.add(new org.deri.wsml.reasoner.wsmlcore.datalog.Rule(h));
            h  = new Literal(new org.deri.wsml.reasoner.wsmlcore.datalog.Predicate("arc",2), new Constant[]{new Constant("f"),new Constant("g")});
            kb.add(new org.deri.wsml.reasoner.wsmlcore.datalog.Rule(h));
            h  = new Literal(new org.deri.wsml.reasoner.wsmlcore.datalog.Predicate("arc",2), new Constant[]{new Constant("g"),new Constant("f")});
            kb.add(new org.deri.wsml.reasoner.wsmlcore.datalog.Rule(h));
            h  = new Literal(new org.deri.wsml.reasoner.wsmlcore.datalog.Predicate("arc",2), new Constant[]{new Constant("h"),new Constant("g")});
            kb.add(new org.deri.wsml.reasoner.wsmlcore.datalog.Rule(h));
            h  = new Literal(new org.deri.wsml.reasoner.wsmlcore.datalog.Predicate("arc",2), new Constant[]{new Constant("g"),new Constant("h")});
            kb.add(new org.deri.wsml.reasoner.wsmlcore.datalog.Rule(h));
            h  = new Literal(new org.deri.wsml.reasoner.wsmlcore.datalog.Predicate("arc",2), new Constant[]{new Constant("h"),new Constant("i")});
            kb.add(new org.deri.wsml.reasoner.wsmlcore.datalog.Rule(h));
            
            
            // Some rules 
            List<Literal> body = new LinkedList<Literal>();
            
            h  = new Literal(new org.deri.wsml.reasoner.wsmlcore.datalog.Predicate("path",2), new Variable[]{new Variable("X"),new Variable("Y")});
            body.add(new Literal(new org.deri.wsml.reasoner.wsmlcore.datalog.Predicate("arc",2), new Variable[]{new Variable("X"),new Variable("Y")}));
                     
            kb.add(new org.deri.wsml.reasoner.wsmlcore.datalog.Rule(h, body));
            
            body = new LinkedList<Literal>();
            h  = new Literal(new org.deri.wsml.reasoner.wsmlcore.datalog.Predicate("path",2), new Variable[]{new Variable("X"),new Variable("Y")});
            // bad path def // body.add(new Literal(new org.deri.wsml.reasoner.wsmlcore.datalog.Predicate("path",2), new Variable[]{new Variable("X"),new Variable("Z")}));
            body.add(new Literal(new org.deri.wsml.reasoner.wsmlcore.datalog.Predicate("arc",2), new Variable[]{new Variable("X"),new Variable("Z")}));
            body.add(new Literal(new org.deri.wsml.reasoner.wsmlcore.datalog.Predicate("path",2), new Variable[]{new Variable("Z"),new Variable("Y")}));
            
            kb.add(new org.deri.wsml.reasoner.wsmlcore.datalog.Rule(h, body));
            
            body = new LinkedList<Literal>();
            h  = new Literal(new org.deri.wsml.reasoner.wsmlcore.datalog.Predicate("scElement",1), new Variable[]{new Variable("X")});
            body.add(new Literal(new org.deri.wsml.reasoner.wsmlcore.datalog.Predicate("path",2), new Variable[]{new Variable("X"),new Variable("Y")}));
            body.add(new Literal(new org.deri.wsml.reasoner.wsmlcore.datalog.Predicate("path",2), new Variable[]{new Variable("Y"),new Variable("X")}));
            
            kb.add(new org.deri.wsml.reasoner.wsmlcore.datalog.Rule(h, body));
            
            
            
            log.info("KB = \n" + kb);
            
            // Construct a simple conjunctive query
            log.info("(2) Constructing simple query");
            
            ConjunctiveQuery cq1;
            
            body = new LinkedList(); 
            body.add(new Literal(new org.deri.wsml.reasoner.wsmlcore.datalog.Predicate("path",2), new Variable[]{new Variable("H"),new Variable("J")}));
            
            cq1 = new ConjunctiveQuery(body);
            cq1.setKnowledgebase(kb);
            
            ConjunctiveQuery cq2; // computes all nodes in the strong component of node 'f'
            
            body = new LinkedList(); 
            body.add(new Literal(new org.deri.wsml.reasoner.wsmlcore.datalog.Predicate("path",2), new org.deri.wsml.reasoner.wsmlcore.datalog.Term[]{new Variable("H"),new Constant("f")}));
            body.add(new Literal(new org.deri.wsml.reasoner.wsmlcore.datalog.Predicate("path",2), new org.deri.wsml.reasoner.wsmlcore.datalog.Term[]{new Constant("f"),new Variable("H")}));
            body.add(new Literal(new org.deri.wsml.reasoner.wsmlcore.datalog.Predicate("scElement",1), new Variable[]{new Variable("H")}));
            
            cq2 = new ConjunctiveQuery(body);
            cq2.setKnowledgebase(kb);
           
            ConjunctiveQuery cq3; // computes all nodes in the strong component of node 'f'
            
            body = new LinkedList(); 
            body.add(new Literal(new org.deri.wsml.reasoner.wsmlcore.datalog.Predicate("path",2), new org.deri.wsml.reasoner.wsmlcore.datalog.Term[]{new Variable("H"),new Constant("f")}));
            body.add(new Literal(new org.deri.wsml.reasoner.wsmlcore.datalog.Predicate("path",2), new org.deri.wsml.reasoner.wsmlcore.datalog.Term[]{new Constant("f"),new Variable("H")}));
            
            
            cq3 = new ConjunctiveQuery(body);
            cq3.setKnowledgebase(kb);
            
            ConjunctiveQuery theQuery = cq3;
            log.info("Q = " + theQuery);
            
            // Invoke DLV
            
            log.info("(3) Evaluating query with MandraxFacade");
            
            DatalogQueryAnsweringFacade qaFacade = new MandraxFacade(true); // This line is the only tool specific thing!
            QueryResult qResult = qaFacade.evaluate(theQuery);

            // Output result
            log.info("(4) Output query answer:");
            
            Set<VariableBinding> varBindings = qResult.getVariableBindings();
            
            String s;
            
            int i;
            for (Map<String,String> varBinding : varBindings){
                s = "[ ";
                i = 1;
                for (String varName : varBinding.keySet()){
                    s += varName + " = " + varBinding.get(varName);
                    if (i < varBinding.size()){
                        s += ", ";
                    }
                    i++;
                }
                s += " ]\n";
                log.info(s);
            }
            
            log.info("Found "+varBindings.size()+" results to query: " + theQuery);
            
            
        } catch(DatalogException d) {
            d.printStackTrace();
        } catch (ExternalToolException e) {
            log.severe("An error occured when evaluating the query" + e);
            e.printStackTrace();
            
        }
        
        log.info("Finished!");
        
        
    }
}
