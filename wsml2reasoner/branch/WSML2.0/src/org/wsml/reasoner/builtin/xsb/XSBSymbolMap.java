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
package org.wsml.reasoner.builtin.xsb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.omwg.logicalexpression.terms.ConstructedTerm;
import org.omwg.logicalexpression.terms.Term;
import org.omwg.ontology.DataValue;
import org.omwg.ontology.Variable;
import org.wsmo.common.IRI;
import org.wsmo.factory.Factory;

import com.declarativa.interprolog.TermModel;

/**
 * Package: package org.wsml.reasoner.datalog.wrapper.mins;
 * 
 * Author: Holger Lausen, DERI Innsbruck Date: $Date: 2006-12-20 14:06:01 $
 */

public class XSBSymbolMap {

    protected Factory wsmoManager;

    public XSBSymbolMap(Factory wsmoManager) {
        this.wsmoManager = wsmoManager;
    }

    private static Map<String, String> uri2name = new HashMap<String, String>();

    private static Map<String, String> name2uri = new HashMap<String, String>();

    public String uri2String(String URI) {
        if (uri2name.get(URI) != null)
            return uri2name.get(URI);
        else {
            int i = URI.length();
            while (Character.isLetter(URI.charAt(i - 1)))
                i--;
            String name = Character.toLowerCase(URI.charAt(i)) + URI.substring(i + 1);
            // System.out.println("#"+name+"\t\t"+URI);
            uri2name.put(URI, name);
            name2uri.put(name, URI);
            return name;
        }
    }

    public String term2String(Term t) {
        if (t instanceof ConstructedTerm) {
            ConstructedTerm ct = (ConstructedTerm) t;
            StringBuffer buf = new StringBuffer();
            buf.append(uri2String(ct.getFunctionSymbol().toString()) + "(");
            for (Term parm : (List<Term>) ct.listParameters()) {
                buf.append(term2String(parm) + ",");
            }
            buf.deleteCharAt(buf.length() - 1);
            buf.append(")");
            return buf.toString();
        }
        else if (t instanceof DataValue) {
            return t.toString();
        }
        else if (t instanceof IRI) {
            return uri2String(t.toString());
        }
        else if (t instanceof Variable) {
            Variable v = (Variable) t;
            // return "V"+v.getName();
            return Character.toUpperCase(v.getName().charAt(0)) + v.getName().substring(1);
        }
        else {
            return null;
        }
    }

    public Term convertToWSML(TermModel xsbTerm) {
        // conrtucted term
        if (!xsbTerm.isLeaf()) {
            System.err.println("TRANSLATE FSYM:" + xsbTerm);
        }
        else if (xsbTerm.isInteger()) {
            return wsmoManager.getWsmlDataFactory().createInteger(xsbTerm.toString());
        }
        else if (xsbTerm.isAtom()) {
            String uri = name2uri.get(xsbTerm.toString());
            return wsmoManager.getWsmoFactory().createIRI(uri);
        }
        else {
            System.err.println("unkown interprolog term:" + xsbTerm);
        }
        return null;
    }
}
