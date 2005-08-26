package org.deri.wsml.reasoner.normalization.le;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.StringTokenizer;

import org.omwg.logexpression.LogicalExpressionFactory;
import org.wsmo.factory.Factory;

/**
 * The abstract class FixedModificationRules represents a fixed list of
 * LEModificationRule objects, bundled together for a certain purpose. Subclasses
 * hereof usually contain implementations of LEModificationRule as inner
 * classes. Any write-operation on this list will be denied.
 * 
 * @author grimm
 */
public abstract class FixedModificationRules implements List
{
    protected List rules;
    protected static LogicalExpressionFactory leFactory;

    FixedModificationRules()
    {
        rules = new ArrayList();
        if(leFactory == null)
        {
            Map createParams = new HashMap();
            createParams.put(Factory.PROVIDER_CLASS, "org.deri.wsmo4j.logexpression.LogicalExpressionFactoryImpl");
            leFactory = (LogicalExpressionFactory)Factory.createLogicalExpressionFactory(createParams);
        }
    }

    public void add(int index, Object element)
    {
        throw new UnsupportedOperationException();
    }

    public boolean add(Object o)
    {
        throw new UnsupportedOperationException();
    }

    public boolean addAll(Collection c)
    {
        throw new UnsupportedOperationException();
    }

    public boolean addAll(int index, Collection c)
    {
        throw new UnsupportedOperationException();
    }

    public void clear()
    {
        throw new UnsupportedOperationException();
    }

    public boolean contains(Object o)
    {
        return rules.contains(o);
    }

    public boolean containsAll(Collection c)
    {
        return rules.containsAll(c);
    }

    public Object get(int index)
    {
        return rules.get(index);
    }

    public int indexOf(Object o)
    {
        return rules.indexOf(o);
    }

    public boolean isEmpty()
    {
        return false;
    }

    public Iterator iterator()
    {
        return rules.iterator();
    }

    public int lastIndexOf(Object o)
    {
        return rules.lastIndexOf(o);
    }

    public ListIterator listIterator()
    {
        return rules.listIterator();
    }

    public ListIterator listIterator(int index)
    {
        return rules.listIterator(index);
    }

    public Object remove(int index)
    {
        throw new UnsupportedOperationException();
    }

    public boolean remove(Object o)
    {
        throw new UnsupportedOperationException();
    }

    public boolean removeAll(Collection c)
    {
        throw new UnsupportedOperationException();
    }

    public boolean retainAll(Collection c)
    {
        throw new UnsupportedOperationException();
    }

    public Object set(int index, Object element)
    {
        throw new UnsupportedOperationException();
    }

    public int size()
    {
        return rules.size();
    }

    public List subList(int fromIndex, int toIndex)
    {
        return rules.subList(fromIndex, toIndex);
    }

    public Object[] toArray()
    {
        return rules.toArray();
    }

    public Object[] toArray(Object[] a)
    {
        return rules.toArray(a);
    }

    public String toString()
    {
        String resultString = new String();
        for(Object object : rules)
        {
            StringTokenizer ruleNameTokenizer = new StringTokenizer(object.getClass().getName().toString(), "$");
            ruleNameTokenizer.nextToken();
            resultString += ruleNameTokenizer.nextToken() + "\n";
            resultString += object.toString() + "\n";
        }
        return resultString;
    }
}
