/**
 * WSML Reasoner Implementation.
 *
 * Copyright (c) 2005, FZI, Germany.
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
package org.wsml.reasoner.transformation.le;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.StringTokenizer;

import org.omwg.logicalexpression.Conjunction;
import org.omwg.logicalexpression.Disjunction;
import org.omwg.logicalexpression.LogicalExpression;
import org.wsml.reasoner.impl.WSMO4JManager;
import org.wsmo.factory.LogicalExpressionFactory;

/**
 * The abstract class FixedModificationRules represents a fixed list of
 * LEModificationRule objects, bundled together for a certain purpose.
 * Subclasses hereof usually contain implementations of LEModificationRule as
 * inner classes. Any write-operation on this list will be denied.
 * 
 * @author grimm
 */
public class FixedModificationRules implements List {
    protected static final byte CONJUNCTION = 0;

    protected static final byte DISJUNCTION = 1;

    protected List rules;

    protected LogicalExpressionFactory leFactory;

    public FixedModificationRules(WSMO4JManager wsmoManager) {
        rules = new ArrayList();
        leFactory = wsmoManager.getLogicalExpressionFactory();
    }

    public Conjunction buildNaryConjunction(
            Collection<? extends LogicalExpression> expressions) {
        return (Conjunction) buildNary(CONJUNCTION, expressions);
    }

    public Disjunction buildNaryDisjunction(
            Collection<? extends LogicalExpression> expressions) {
        return (Disjunction) buildNary(DISJUNCTION, expressions);
    }

    protected LogicalExpression buildNary(byte operationCode,
            Collection<? extends LogicalExpression> expressions) {
        LogicalExpression nary = null;
        Iterator<? extends LogicalExpression> leIterator = expressions
                .iterator();
        if (leIterator.hasNext()) {
            nary = leIterator.next();
        }
        while (leIterator.hasNext()) {
            switch (operationCode) {
            case CONJUNCTION:
                nary = leFactory.createConjunction(nary, leIterator.next());
                break;

            case DISJUNCTION:
                nary = leFactory.createDisjunction(nary, leIterator.next());
                break;
            }
        }
        return nary;
    }

    public void add(int index, Object element) {
        throw new UnsupportedOperationException();
    }

    public boolean add(Object o) {
        throw new UnsupportedOperationException();
    }

    public boolean addAll(Collection c) {
        throw new UnsupportedOperationException();
    }

    public boolean addAll(int index, Collection c) {
        throw new UnsupportedOperationException();
    }

    public void clear() {
        throw new UnsupportedOperationException();
    }

    public boolean contains(Object o) {
        return rules.contains(o);
    }

    public boolean containsAll(Collection c) {
        return rules.containsAll(c);
    }

    public Object get(int index) {
        return rules.get(index);
    }

    public int indexOf(Object o) {
        return rules.indexOf(o);
    }

    public boolean isEmpty() {
        return false;
    }

    public Iterator iterator() {
        return rules.iterator();
    }

    public int lastIndexOf(Object o) {
        return rules.lastIndexOf(o);
    }

    public ListIterator listIterator() {
        return rules.listIterator();
    }

    public ListIterator listIterator(int index) {
        return rules.listIterator(index);
    }

    public Object remove(int index) {
        throw new UnsupportedOperationException();
    }

    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    public boolean removeAll(Collection c) {
        throw new UnsupportedOperationException();
    }

    public boolean retainAll(Collection c) {
        throw new UnsupportedOperationException();
    }

    public Object set(int index, Object element) {
        throw new UnsupportedOperationException();
    }

    public int size() {
        return rules.size();
    }

    public List subList(int fromIndex, int toIndex) {
        return rules.subList(fromIndex, toIndex);
    }

    public Object[] toArray() {
        return rules.toArray();
    }

    public Object[] toArray(Object[] a) {
        return rules.toArray(a);
    }

    public String toString() {
        String resultString = new String();
        for (Object object : rules) {
            StringTokenizer ruleNameTokenizer = new StringTokenizer(object
                    .getClass().getName().toString(), "$");
            ruleNameTokenizer.nextToken();
            resultString += ruleNameTokenizer.nextToken() + "\n";
            resultString += object.toString() + "\n";
        }
        return resultString;
    }
}