/*
 * Copyright 2011, Semantic Technology Institute (STI) Innsbruck, 
 * University of Innsbruck, Technikerstrasse 21a, 6020 Innsbruck, Austria.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wsml.reasoner;

import java.util.ArrayList;
import java.util.List;

import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.Variable;

/**
 * A small utility class to make parameter passing easier
 * 
 * @author Gabor Nagypal (FZI)
 * 
 */
public class Literal {
    private boolean positive = true;

    private Term[] terms;

    private String predicateUri = null;

    public Literal(boolean isPositive, String predicateUri, Term... terms) {
        this.positive = isPositive;
        this.terms = terms;
        this.predicateUri = predicateUri;
    }

    public Literal(boolean isPositive, String predicateUri, List<Term> terms) {
        this.positive = isPositive;
        this.terms = terms.toArray(new Term[terms.size()]);
        this.predicateUri = predicateUri;
    }

    public boolean isPositive() {
        return positive;
    }

    public void setPositive(boolean positive) {
        this.positive = positive;
    }

    public Term[] getTerms() {
        return terms;
    }

    public void setTerms(Term[] terms) {
        this.terms = terms;
    }

    public String getPredicateUri() {
        return predicateUri;
    }

    public void setPredicateUri(String predicateUri) {
        this.predicateUri = predicateUri;
    }

    public List<Variable> getVariables() {
        List<Variable> result = new ArrayList<Variable>();
        for (Term t : this.terms) {
            if (t instanceof Variable)
                if (!result.contains(t))
                    result.add((Variable) t);
        }
        return result;
    }

    public String toString() {
        String result = "";

        if (!this.positive) {
            result += "!";
        }

        result += this.predicateUri;
        result += "(";

        int i = 1;
        for (Term t : this.terms) {
            result += t.toString();
            if (i < this.terms.length) {
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
        return (predicateUri == other.predicateUri || (predicateUri != null && predicateUri
                .equals(other.predicateUri)))
                && (positive == other.positive)
                && (terms == other.terms || (terms != null
                        && other.terms != null && arrayEquals(terms,
                        other.terms)));
    }

    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (null == predicateUri ? 0 : predicateUri.hashCode());
        hash = 31 * hash + (positive ? 1 : 0);
        hash = 31 * hash + (null == terms ? 0 : arrayHashCode(terms));
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
