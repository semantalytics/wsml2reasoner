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

package org.wsml.reasoner;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a rule of a datalog program.
 * 
 * A rule is of the form: HEAD <- BODY where HEAD is a single literal and BODY
 * is a list of literals.
 * 
 * The BODY literals are combined conjunctively. A rule with an empty body is
 * called fact. A rule with an empty head is called constraint.
 * 
 * @author Uwe Keller, DERI Innsbruck
 * @author Gabor Nagypal, FZI
 */

public class Rule {

    private static final List<Literal> EMPTY_BODY = new ArrayList<Literal>();

    private List<Literal> body;

    private Literal head;

    /**
     * @return Returns the body.
     */
    public List<Literal> getBody() {
        return body;
    }

    /**
     * @return Returns the head.
     */
    public Literal getHead() {
        return head;
    }

    public boolean isFact() {
        return (body == EMPTY_BODY);
    }

    public boolean isConstraint() {
        return (head == null);
    }

    /**
     * Creates a rule with the given head and body.
     * 
     * @param body
     * @param head
     */
    public Rule(Literal head, List<Literal> body) throws DatalogException {
        super();

        if (body == null || body.size() == 0) {
            body = EMPTY_BODY;
        }

        if (head != null && !head.isPositive()) {
            throw new DatalogException("Only a positive literal is allowed in a rule");
        }

        this.body = body;
        this.head = head;
    }

    /**
     * Creates a fact, i.e. a rule with an empty body.
     * 
     * @param head
     */
    public Rule(Literal head) throws DatalogException {
        this(head, EMPTY_BODY);
    }

    public String toString() {
        String result;

        if (this.isConstraint()) {
            result = "";
        }
        else {
            // fact or general rule
            result = this.getHead().toString();
        }

        if (!this.isFact()) {
            result += " :- ";

            int i = 1;
            List<Literal> body = this.getBody();
            for (Literal l : body) {
                result += l.toString();
                if (i < body.size()) {
                    result += ", ";
                }
                i++;
            }
        }

        result += ".";
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if ((obj == null) || (obj.getClass() != this.getClass()))
            return false;
        Rule r = (Rule) obj;
        return (head == r.head || (head != null && head.equals(r.head))) && (body == r.body || (body != null && body.equals(r.body)));
    }

    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (null == head ? 0 : head.hashCode());
        hash = 31 * hash + (null == body ? 0 : body.hashCode());
        return hash;
    }

}
