/*
 * Copyright (C) 2006 Digital Enterprise Research Insitute (DERI) Innsbruck
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package org.wsml.reasoner.transformation.le;

import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;

public class AddOnlyArrayList<X extends LEModificationRule> extends ArrayList<X> {

    public void add(int index, X element) {
        throw new UnsupportedOperationException();
    }

    public void clear() {
        throw new UnsupportedOperationException();
    }

    public X remove(int index) {
        throw new UnsupportedOperationException();
    }

    public boolean remove(Object theObject) {
        throw new UnsupportedOperationException();
    }

    public boolean removeAll(Collection theObject) {
        throw new UnsupportedOperationException();
    }

    public boolean retainAll(Collection theObject) {
        throw new UnsupportedOperationException();
    }

    public X set(int index, X element) {
        throw new UnsupportedOperationException();
    }

    public String toString() {
        String resultString = new String();
        for (Object object : this) {
            StringTokenizer ruleNameTokenizer = new StringTokenizer(object.getClass().getName().toString(), "$");
            ruleNameTokenizer.nextToken();
            resultString += ruleNameTokenizer.nextToken() + "\n";
            resultString += object.toString() + "\n";
        }
        return resultString;
    }

}
