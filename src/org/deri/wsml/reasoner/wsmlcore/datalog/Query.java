/*
 * File: Query.java
 *
 */
package org.deri.wsml.reasoner.wsmlcore.datalog;

public interface Query {

    /**
     * @return Returns the knowledgebase.
     */
    public abstract Program getKnowledgebase();

    /**
     * @param knowledgebase The knowledgebase to set.
     */
    public abstract void setKnowledgebase(Program knowledgebase);

}