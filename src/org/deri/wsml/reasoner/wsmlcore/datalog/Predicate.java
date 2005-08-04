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

import java.util.*;

import org.omwg.logexpression.Constants;

/**
 * Represents a predicate symbol in rules.
 * @author Uwe Keller, DERI Innsbruck
 */

public class Predicate {

   protected static Set<Predicate> BUILT_IN_PREDICATES;
   
   public final static Predicate EQUALS = new Predicate(Constants.EQUAL,2);  
   
   private int arity; 
   private String symbolName;

   
   static {
       Predicate.BUILT_IN_PREDICATES = new HashSet<Predicate>();
       BUILT_IN_PREDICATES.add(EQUALS);
       
       // more to be added finally.
       
   }

   /**
    * Creates a predicate symbol with the given name and arity.
    * @param arity
    * @param name
    */
   public Predicate(String name, int arity) {
       super();
       this.arity = arity;
       symbolName = name;
   }
   
   /**
    * @return Returns the arity.
    */
   public int getArity() {
       return arity;
   }
   
   /**
    * @return Returns the symbolName.
    */
   public String getSymbolName() {
       return symbolName;
   }
   
   /**
    * @return true if the predicate is recognized as a built-in WSML predicate.
    */
   public boolean isBuiltIn(){
       return Predicate.BUILT_IN_PREDICATES.contains(this);
   }
   
   /**
    * Two predicates are equal in case they have the same name and arity.
    */
   public boolean equals(Object o){
       boolean result = false;
       if (o != null && (o instanceof Predicate)) {
           Predicate p = (Predicate) o;
           result = (this.getArity() == p.getArity()) && this.getSymbolName().equals(p.getSymbolName());
       }
       return result;
           
   }
   
   
   
   
   
   
}
