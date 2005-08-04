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

package org.deri.wsml.reasoner.wsmlcore;

import org.deri.wsml.reasoner.api.*;
import org.deri.wsml.reasoner.api.queryanswering.QueryAnsweringRequest;
import org.deri.wsml.reasoner.api.queryanswering.QueryAnsweringResult;
import org.deri.wsml.reasoner.wsmlcore.datalog.*;
import org.deri.wsml.reasoner.wsmlcore.wrapper.DatalogQueryAnsweringFacade;
import org.deri.wsml.reasoner.wsmlcore.wrapper.ExternalToolException;
import org.deri.wsml.reasoner.wsmlcore.wrapper.dlv.DLVFacade;
import org.deri.wsml.reasoner.impl.*;
import org.omwg.logexpression.*;

import java.util.*;

import org.omwg.ontology.*;
import org.wsmo.factory.Factory;

/**
 * A prototypical implementation of a query answering engine for 
 * WSML Core.
 * 
 * The implementation is based on mapping to some external tool
 * like DLV, Kaon2, Mandrax or XSB Prolog.
 * 
 * At present, the implementation uses the DLV system.
 * Hence, datatype support can not be provided except for integers.
 * 
 * @author Uwe Keller, DERI Innsbruck
 */
public class QueryAnsweringReasoner {

    private final static String WSML_RESULT_PREDICATE = "wsml:query_result";
    
    public QueryAnsweringResult execute(QueryAnsweringRequest req) 
    throws DatalogException, ExternalToolException {
        QueryAnsweringResult result = new QueryAnsweringResultImpl(req);
        
        // First convert ontologies to Datalog programs
        Program kb = new Program();
        
        for(Ontology o : req.getOntologies()){
            // convert the next ontology
            kb.addAll(convertOntology(o));
        }
       
        // Convert the query ...
        
        ConjunctiveQuery query = convertQuery(req.getQuery(), kb);
        
        // Create a facade to the external tool
        
        DatalogQueryAnsweringFacade qaf = new DLVFacade();
        QueryResult qres = qaf.evaluate(query);
        
        // TODO: No incremental fetching of results is supported here at present
        result.addAll(qres.getVariableBindings());
        
        return result;
    }
    
    private Program convertOntology(Ontology o){
        Program p = new Program();
        
        // TODO: Insert normalization steps first
        // - Conceptual syntax to WSML Logical Expressions (Axioms)
        // - Resolve Anonymous Identifiers
        // - Llyod-Topor-Transformation
        // - Remove syntactical shortcuts 
        // - ...
        
        // At present we only support very simple ontologies
        // - Consist only of logical expressions
        // - Axioms are simple WSML (datalog) rules
        
        WSML2Datalog wsml2datalog = new WSML2Datalog();
        Set<org.omwg.logexpression.LogicalExpression> lExprs = new LinkedHashSet<org.omwg.logexpression.LogicalExpression>();
        for (Object a : o.listAxioms()){
            lExprs.addAll(((Axiom) a).listDefinitions());
        }
        p = wsml2datalog.transform(lExprs);
        
        
        return p;
    }
    
    private ConjunctiveQuery convertQuery(org.omwg.logexpression.LogicalExpression q, Program kb){
        
        WSML2Datalog wsml2datalog = new WSML2Datalog();
        
        // Convert query Q(x1,...xn) to rule "wsml-result(x1,...xn) impliedBy Q(x1,...xn)";
        // and use as query simply the atomic expression: wsml-result(x1,...xn)
        
        Map<String, String> leProperties = new HashMap<String, String>();
        leProperties.put(Factory.PROVIDER_CLASS,
         "org.deri.wsmo4j.logexpression.LogicalExpressionFactoryImpl");

        org.omwg.logexpression.LogicalExpressionFactory leFactory = 
            (org.omwg.logexpression.LogicalExpressionFactory) Factory.createLogicalExpressionFactory(leProperties);
        
        
        List<org.omwg.logexpression.terms.Variable> params = new LinkedList<org.omwg.logexpression.terms.Variable>();
        LogicalExpressionVariableVisitor varVisitor = new LogicalExpressionVariableVisitor();
        q.accept(varVisitor);
        params.addAll(varVisitor.getFreeVariables(q));
        Atom rHead = leFactory.createAtom(leFactory.createIRI(WSML_RESULT_PREDICATE), params);
                    
        org.omwg.logexpression.LogicalExpression resultDefRule = leFactory.createBinary(CompoundExpression.IMPLIEDBY, rHead, q);
        
        Program p = wsml2datalog.transform(resultDefRule);
        
        // Add the tranlation of the result definition rule to the knowledgebase.
        
        kb.addAll(p);
        
        // Create datalog query ...
        Term[] qLitArgs = new Term[params.size()];
        int i = 0;
        for (org.omwg.logexpression.terms.Variable fv : params){
            // Create a respective datalog variable
            org.deri.wsml.reasoner.wsmlcore.datalog.Variable dVar = new org.deri.wsml.reasoner.wsmlcore.datalog.Variable(fv.getName());
            qLitArgs[i] = dVar;
            i++;
           
        }
        Literal qLiteral = new Literal(new Predicate(WSML_RESULT_PREDICATE,params.size()), qLitArgs);
        List<Literal> body = new LinkedList<Literal>();
        body.add(qLiteral);
        
        ConjunctiveQuery result = new ConjunctiveQuery(body);
        result.setKnowledgebase(kb);
        return result;
    }

}
