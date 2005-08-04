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

package org.deri.wsml.reasoner.wsmlcore.datalog;

import java.util.List;

/**
 * Represents a conjunctive query against a knowledgebase which is a datalog program.
 * 
 * @author Uwe Keller, DERI Innsbruck
 */
public class ConjunctiveQuery extends Rule implements Query {

    /**
     * The knowledgebase to which the query refers to.
     */
    private Program knowledgebase; 
    
    public ConjunctiveQuery(List<Literal> body) throws DatalogException {
        super(null, body);
        knowledgebase = null;
    }

    /**
     * @return Returns the knowledgebase.
     */
    public Program getKnowledgebase() {
        return knowledgebase;
    }

    /**
     * @param knowledgebase The knowledgebase to set.
     */
    public void setKnowledgebase(Program knowledgebase) {
        this.knowledgebase = knowledgebase;
    }
    
    public String toString(){
        String result = " ?- ";
            
        int i = 1;
        List<Literal> body = this.getBody();
        for (Literal l : body ){
            result += l.toString();
            if (i < body.size()){
                result += ", ";
            }
            i++;
        }
        
        
        result += ".";
        return result;
    }
    

}
