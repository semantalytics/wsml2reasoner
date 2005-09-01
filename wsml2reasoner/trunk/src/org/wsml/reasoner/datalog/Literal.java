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

package org.wsml.reasoner.datalog;

import java.util.ArrayList;
import java.util.List;

/**
 * An atom that can occur in a rule of a logic programm.
 * 
 * Atoms are of the form: p(t1,...,tn) where p is either a predicate symbol of
 * arity n>=0 and ti are terms. The predicate symbol can denote either
 * user-defined statements / properties or built-in predicates, like equality,
 * or datatype operations.
 * 
 * Terms in Datalog are either variables or constants (that denote either
 * objects or datavalues)
 * 
 * In a sense these classes in this package are very similar to WSML logical
 * expressions, nevertheless they provide functionality that is more specific to
 * an Logic Programming Framework than the classes that are currently part of
 * the WSMO4j Logical Exrpression API.
 * 
 * @author Uwe Keller, DERI Innsbruck
 */
public class Literal {

    public enum NegationType {
        NONNEGATED, NEGATIONASFAILURE
    };

    private Term[] args;

    private Predicate symb;

    private NegationType negationType;

    public int getArity() {
        return args.length;
    }

    public Predicate getSymbol() {
        return symb;
    }

    public boolean isPositive() {
        return (negationType == NegationType.NONNEGATED);
    }

    public List<Variable> getVariables() {
        List<Variable> result = new ArrayList<Variable>();
        for (Term t : args) {
            result.addAll(t.getVariables());
        }
        return result;

    }

    public Term[] getArguments() {
        return this.args;
    }

    /**
     * Creates a literal with the respective symbol and negation type.
     * 
     * @param symbol
     * @param negation
     */
    public Literal(Predicate symbol, NegationType negation, Term[] args)
            throws DatalogException {

        if (symbol == null) {
            throw new DatalogException(
                    "Predicate object must not be null when constructing a literal!");
        }

        if (symbol.getArity() != args.length) {
            throw new DatalogException(
                    "Predicate arity mismatche number of arguments when constructing literal with predicate: "
                            + symbol.toString());
        }

        this.symb = symbol;
        this.negationType = negation;
        this.args = args;
    }

    /**
     * Creates a positive (nonnegated) atom.
     * 
     * @param symbol
     */

    public Literal(Predicate symbol, Term[] args) throws DatalogException {
        this(symbol, NegationType.NONNEGATED, args);
    }

    public String toString() {
        String result = "";

        if (!this.isPositive()) {
            result += "~";
        }

        result += this.getSymbol().getSymbolName();
        result += "(";

        int i = 1;
        for (Term t : this.getArguments()) {
            result += t.toString();
            if (i < this.getArguments().length) {
                result += ", ";
            }
            i++;
        }

        result += ")";

        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if ((obj == null) || (obj.getClass() != this.getClass()))
            return false;
        Literal other = (Literal) obj;
        return (symb == other.symb || (symb != null && symb.equals(other.symb)))
                && (negationType == other.negationType || (negationType != null && negationType
                        .equals(other.negationType)))
                && (args == other.args || (args != null && other.args != null && arrayEquals(
                        args, other.args)));
    }

    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (null == symb ? 0 : symb.hashCode());
        hash = 31 * hash + (null == negationType ? 0 : negationType.hashCode());
        hash = 31 * hash + (null == args ? 0 : arrayHashCode(args));
        return hash;
    }

    private boolean arrayEquals(Object[] array1, Object[] array2) {
        if (array1.length != array2.length)
            return false;
        for (int i = 0; i < array1.length; i++) {
            Object o1 = array1[i];
            Object o2 = array2[i];
            boolean eq = (o1 == o2 || (o1 != null && o1.equals(o2)));
            if (!eq)
                return false;
        }
        return true;
    }

    private int arrayHashCode(Object[] array) {
        int hash = 7;
        for (Object object : array) {
            hash = 31 * hash + (null == object ? 0 : object.hashCode());
        }
        return hash;
    }
}
