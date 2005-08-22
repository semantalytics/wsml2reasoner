/*
 * File: Query.java
 *
 */
package org.deri.wsml.reasoner.wsmlcore.datalog;

import java.util.List;

public interface Query {
    
    public List<Literal> getLiterals();
    
    public List<Variable> getVariables();

}